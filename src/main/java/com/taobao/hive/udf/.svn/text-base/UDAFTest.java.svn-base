package com.taobao.hive.udf;

//需要import org.apache.hadoop.hive.ql.exec.UDAF
//以及org.apache.hadoop.hive.ql.exec.UDAFEvaluator,
//这两个包都是必须的
import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.io.Text;

//CREATE TEMPORARY FUNCTION count_test  AS 'com.taobao.hive.udf.UDAFTest';
//函数类需要继承UDAF类
 public class UDAFTest extends UDAF {
	//内部类Evaluator实现UDAFEvaluator接口
	public static class Evaluator implements UDAFEvaluator {

	  private int mCount; 
	  
      public Evaluator() {
          super();
          init();
      }
	  //Evaluator需要实现 init、iterate、terminatePartial、merge、terminate这几个函数
	  //init函数类似于构造函数，用于UDAF的初始化.对属性进行初始化
	  //iterate接收传入的参数，并进行内部的轮转。其返回类型为boolean
	  //terminatePartial无参数，其为iterate函数轮转结束后，返回轮转数据
	  //merge接收terminatePartial的返回结果，进行数据merge操作，其返回类型为boolean
	  //terminate返回最终的聚集函数结果
	  public void init() {
		  mCount = 0;
	  } 

	  public boolean iterate(Text o) {
			if (o!=null)
			  mCount++; 
	
			return true;
	  } 

	  public Text terminatePartial() {
		    return new Text(mCount+"");
	  } 

	  public boolean merge(Text o) {
		    int a = Integer.parseInt(o.toString());
			mCount += a;
			return true;
	  } 

	  public Text terminate() {
		    return new Text(mCount+"");
	  }
   }
 }