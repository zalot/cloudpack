/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taobao.ad.data.search.udf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

@Description(name = "wm_concat", value = "_FUNC_(expr) - Returns the concat value of expr")
public class GenericUDAFWm_concat extends AbstractGenericUDAFResolver {

  static final Log LOG = LogFactory.getLog(GenericUDAFWm_concat.class.getName());

  @Override
  public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters)
    throws SemanticException {
    if (parameters.length != 1) {
      throw new UDFArgumentTypeException(parameters.length - 1,
          "Exactly one argument is expected.");
    }
    ObjectInspector oi = TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(parameters[0]);
    if (!ObjectInspectorUtils.compareSupported(oi)) {
      throw new UDFArgumentTypeException(parameters.length - 1,
          "Cannot support comparison of map<> type or complex type containing map<>.");
    }
    return new GenericUDAFWm_concatEvaluator();
  }

  public static class GenericUDAFWm_concatEvaluator extends GenericUDAFEvaluator {

    ObjectInspector inputOI;
    ObjectInspector outputOI;

    @Override
    public ObjectInspector init(Mode m, ObjectInspector[] parameters)
        throws HiveException {
      assert (parameters.length == 1);
      super.init(m, parameters);
      inputOI = parameters[0];
      // Copy to Java object because that saves object creation time.
      // Note that on average the number of copies is log(N) so that's not
      // very important.
      outputOI = ObjectInspectorUtils.getStandardObjectInspector(inputOI,
          ObjectInspectorCopyOption.JAVA);
      return outputOI;
    }

    /** class for storing the current max value */
    static class Wm_concatAgg implements AggregationBuffer {
      Object o;
      int    mcount = 0;
      int    size = 4000;
    }

    @Override
    public AggregationBuffer getNewAggregationBuffer() throws HiveException {
      Wm_concatAgg result = new Wm_concatAgg();
      return result;
    }

    @Override
    public void reset(AggregationBuffer agg) throws HiveException {
      Wm_concatAgg myagg = (Wm_concatAgg) agg;
      myagg.o = null;
      myagg.mcount = 0;
    }

    boolean warned = false;

    @Override
    public void iterate(AggregationBuffer agg, Object[] parameters)
        throws HiveException {
      assert (parameters.length == 1);
      merge(agg, parameters[0]);
    }

    @Override
    public Object terminatePartial(AggregationBuffer agg) throws HiveException {
      return terminate(agg);
    }

    @Override
    public void merge(AggregationBuffer agg, Object partial)
        throws HiveException {
        if (partial != null) {
            Wm_concatAgg myagg = (Wm_concatAgg) agg;

            if(myagg.mcount == 0 ){
               myagg.o = ObjectInspectorUtils.copyToStandardObject(partial, inputOI, ObjectInspectorCopyOption.JAVA);

            } else {
            	myagg.size = myagg.o.toString().length() >=4000 ? 4000 : myagg.o.toString().length();
                if(partial.toString().length()>0 && myagg.size == 0 ){    
                	myagg.o = ObjectInspectorUtils.copyToStandardObject(partial, inputOI, ObjectInspectorCopyOption.JAVA);
                }else if (myagg.size <4000 && partial.toString().length()>0 ){
                	myagg.o = myagg.o.toString() + ',' + ObjectInspectorUtils.copyToStandardObject(partial, inputOI, ObjectInspectorCopyOption.JAVA);
            	}
              }
            	myagg.mcount++;	
            }


      }

    @Override
    public Object terminate(AggregationBuffer agg) throws HiveException {
      Wm_concatAgg myagg = (Wm_concatAgg) agg;
      return  myagg.o.toString().length() >=4000 ? myagg.o.toString().substring(0, 4000)  : myagg.o;
    }

  }

}
