package com.taobao.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.io.Text;

public class UDAFGroupConcat extends UDAF {

	public static class UDAFGroupConcatEvaluator implements
			UDAFEvaluator {

		private StringBuilder sb = new StringBuilder();
		private static String __DELIMITER = ",";
		
		public UDAFGroupConcatEvaluator() {
			super();
			init();
		}

		/**
		 * Reset the state of the aggregation.
		 */
		public void init() {
			if (sb.length() > 0) {
				sb.delete(0, sb.length());
			}
			
		}

		/**
		 * Iterate through one row of original data.
		 * 
		 * This UDF accepts arbitrary number of String arguments, so we use
		 * String[]. If it only accepts a single String, then we should use a
		 * single String argument.
		 * 
		 * This function should always return true.
		 */
		public boolean iterate(Text o, String delimeter) {
			if (o != null) {
				if (sb.length() > 0) {
					sb.append(delimeter);
				}
				sb.append(o.toString());
			}
			return true;
		}
		
		public boolean iterate(Text o) {
			return iterate(o, __DELIMITER);
		}
		
		

		/**
		 * Terminate a partial aggregation and return the state.
		 */
		public Text terminatePartial() {
			return new Text(sb.toString());
		}

		/**
		 * Merge with a partial aggregation.
		 * 
		 * This function should always have a single argument which has the same
		 * type as the return value of terminatePartial().
		 * 
		 * This function should always return true.
		 */
		public boolean merge(Text o, String delimeter) {
			if (o != null) {
				if (sb.length() > 0) {
					sb.append(delimeter);
				}
				sb.append(o.toString());
			}
			return true;
		}
		
		public boolean merge(Text o) {
			return merge(o, __DELIMITER);
		}

		/**
		 * Terminates the aggregation and return the final result.
		 */
		public Text terminate() {
			return new Text(sb.toString().trim());
		}
	}
}
