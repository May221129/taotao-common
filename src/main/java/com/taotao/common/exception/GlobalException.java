package com.taotao.common.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * 	该类的作用：
 * 		利用springmvc做全局异常处理，这里对异常不进行细分，就用exception来做统一捕获，
 * 	一旦出错，springmvc就会找到这个类，按照类中自定义的逻辑代码执行。
 * 
 * 	做全局异常处理的步骤：
 * 		1.自定义一个类，用@ControllerAdvice注解；
 * 		2.类中自定义需要捕获的异常及其对应的异常处理机制，并将这些方法用@ExceptionHandler注解，且在注解中注明捕获哪种异常。
 * 		3.在springmvc.xml中配置SimpleMappingExceptionResolver。
 * 
 * 	提问：
 * 		用了全局异常处理后，日志怎么办？
 *  答：这里无法用到日志，因为日志需要传入具体的Controller.class，这里无法获取到。
 *  解放办法：/taotao-manage-web/src/main/java/com/taotao/manage/controller/BaseController.java
 */
//@ControllerAdvice//这个全局异常处理因为日志问题，所以不用了，这里就注掉了。
public class GlobalException {
	
	@ExceptionHandler(Exception.class)//全局异常处理
	public ModelAndView handleGrobalException(Exception exception){
		System.out.println("出异常啦======>" + exception);
		ModelAndView mv = new ModelAndView("error");
		//也可以把异常一起发送到error页面显示出来。
		//也可以不发，不必要让客户知道你到底出了什么异常，而是简单的告诉客户例如“系统维护中”这样的话。
		//mv.addObject("exception",exception);
		return mv;
	}
	
//	@ExceptionHandler(ArithmeticException.class)//更精细化的异常处理：算术异常处理
//	public ModelAndView handleArithmeticException(ArithmeticException arithmeticException){
//		//具体的处理逻辑...
//	}
	
//	@ExceptionHandler(ArrayIndexOutOfBoundsException.class)//更精细化的异常处理：数组下标越界异常处理
//	public ModelAndView handleArrayIndexOutOfBuondsException(ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
//		//具体的逻辑处理...
//	}
	
}
