package com.taotao.common.spring.exetend.converter.json;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 对于controller进行数据返回前，如果返回的数据是jsonp数据，就利用该类进行处理。
 * 效果如taotao-manage-web模块的
 * 	ResponseEntity<ItemCatResult> com.taotao.manage.controller.api.ApiItemCatController.queryItemCatList()所示。
 * 	1.因为可能很多数据都需要返回成jsonp类型，所以将这部分重复的代码抽取出来在这里进行统一实现。在数据进行返回前，自动进行消息转化。
 * 	2.第二步是在springmvc的xml中进行配置。
 * 	3.提问：为什么通过拼接前端传来的callback和result，就能完成跨域请求了？
 * 		答：其实这个callback是"对象.方法"，将callback+result序列化成json，再返回给前台系统的lib-v1.js中，
 * 		此时该js会将json数据进行解析，通过callback找到指定对象并执行指定方法，再将result数据进行处理。
 */
public class CallbackMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

	// 做jsonp的支持的标识，在请求参数中加该参数
	private String callbackName;

	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		// 从threadLocal中获取当前的Request对象
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String callbackParam = request.getParameter(callbackName);
		if(StringUtils.isEmpty(callbackParam)){
			// 没有找到callback参数，直接返回json数据
			super.writeInternal(object, outputMessage);
		}else{
			JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
			try {
				String result =callbackParam+"("+super.getObjectMapper().writeValueAsString(object)+");";
				IOUtils.write(result, outputMessage.getBody(),encoding.getJavaName());
			}
			catch (JsonProcessingException ex) {
				throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
			}
		}
		
	}

	public String getCallbackName() {
		return callbackName;
	}

	public void setCallbackName(String callbackName) {
		this.callbackName = callbackName;
	}

}
