package com.taobao.hive.udf;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;

//
public class UDAFGroupSpt extends UDAF {

	public static class UDAFGroupSptEvaluator implements UDAFEvaluator {

		public static class PartialResult {
			//double sum;
			//long count;
			
			//double[] in_x;
			//double[] in_y;
			int in_n;
			//double[] out_a;
			//double[] out_dt;			
			TreeMap map;
			
			String type;
			
			public static void SPT1(double[] x, double[] y, int n, double[] a,
					double[] dt)
			// double x[],y[],a[2],dt[6];
			{
				int i;
				double xx, yy, e, f, q, u, p, umax, umin, s;
				xx = 0.0;
				yy = 0.0;
				for (i = 0; i <= n - 1; i++) {
					xx = xx + x[i] / n;
					yy = yy + y[i] / n;
				}
				e = 0.0;
				f = 0.0;
				for (i = 0; i <= n - 1; i++) {
					q = x[i] - xx;
					e = e + q * q;
					f = f + q * (y[i] - yy);
				}
				a[1] = f / e;
				a[0] = yy - a[1] * xx;
				q = 0.0;
				u = 0.0;
				p = 0.0;
				umax = 0.0;
				umin = 1.0e+30;
				for (i = 0; i <= n - 1; i++) {
					s = a[1] * x[i] + a[0];
					q = q + (y[i] - s) * (y[i] - s);
					p = p + (s - yy) * (s - yy);
					e = Math.abs(y[i] - s);
					if (e > umax)
						umax = e;
					if (e < umin)
						umin = e;
					u = u + e / n;
				}
				dt[1] = Math.sqrt(q / n);
				dt[0] = q;
				dt[2] = p;
				dt[3] = umax;
				dt[4] = umin;
				dt[5] = u;
			}

		}

		private PartialResult partial;

		public void init() {
			partial = null;
		}

		public boolean iterate(Text order,Text value,Text type) {
			if (value == null || order == null) {
				return true;
			}
			if (partial == null) {
				partial = new PartialResult();
			}			
			
            partial.map.put(order, value);
            partial.in_n++;
			partial.type = type.toString();
            return true;
		}

		public PartialResult terminatePartial() {
			return partial;
		}

		public boolean merge(PartialResult other) {
			if (other == null) {
				return true;
			}
			if (partial == null) {
				partial = new PartialResult();
			}
			partial.map.putAll(other.map);
			
			partial.in_n += other.in_n;
									
			return true;
		}

		public DoubleWritable terminate() {
			if (partial == null) {
				return null;
			}
			double[] x = null;
			double[] y = null;
			int i;			
			Iterator titer=partial.map.entrySet().iterator();
			i=0;			
	        while(titer.hasNext()){
	            Map.Entry ent=(Map.Entry)titer.next();  
	            x[i]=Double.valueOf(ent.getKey().toString());  
	            y[i]=Double.valueOf(ent.getValue().toString());  	            
	        }
	        int n;
	        double[] a = null;	        
            double[] dt = null;
            n=partial.in_n;
            partial.SPT1(x, y, n, a, dt);            
			return new DoubleWritable(a[0]);
		}
	}	

}
