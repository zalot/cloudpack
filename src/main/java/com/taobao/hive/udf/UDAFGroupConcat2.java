package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.io.Text;

//
public class UDAFGroupConcat2 extends UDAF {
	
	public static class State {
	    private Text concat;
	    private Text splitor;
	}
	
	public static class GroupConcatEvaluator implements UDAFEvaluator {
		private final State state;
		private StringBuilder builder;
		private Text result;

	    public GroupConcatEvaluator() {
	      state = new State();
	      result = new Text();
	      builder = new StringBuilder();
	    }

	    public void init() {
	      state.concat = new Text();
	      state.splitor = new Text();
	      builder.delete(0, builder.length());
	    }
	    
	    public boolean iterate(Text o, Text splitor) {
	    	if (state.splitor == null) {
	    		state.splitor = new Text();
	    	}
	        if (o != null && splitor != null) {
	        	String oStr = o.toString();
	        	String sStr = splitor.toString();
	        	builder.append(oStr);
	        	builder.append(sStr);
	        	state.splitor.set(sStr);
	        }
	        return true;
	    }
	    
	    public State terminatePartial() {
	    	if (state.concat == null) {
	    		state.concat = new Text();
	    	}
	    	String tmpResult = builder.toString();
	    	state.concat.set(tmpResult);
	        return state;
	    }
	    
	    public boolean merge(State other) {
	    	if (state.splitor == null) {
	    		state.splitor = new Text();
	    	}
	    	if (other.concat != null && other.splitor != null) {
	    		builder.append(other.concat.toString());
	    		state.splitor = other.splitor;
	    	}
	        return true;
	    }


	    public Text terminate() {
	        // No input data.
	    	if (builder.length()>0 && state.splitor != null) {
	    		String rStr = builder.toString();
	    		String sStr = state.splitor.toString();
	    		result .set(rStr.substring(0,rStr.length()-sStr.length()));
	    	} else {
	    		return null;
	    	}
	        return result;
	    }

	}
}
