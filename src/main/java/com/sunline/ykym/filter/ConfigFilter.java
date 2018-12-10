package com.sunline.ykym.filter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.sunline.ykym.util.HttpClientUtil;
import com.sunline.ykym.util.PropertUtils;
import com.sunline.ykym.util.StringUtils;
/**
* <p>Title: ConfigFilter</p>  
* <p>Description: VUE请求后端过滤转发 </p>  
* @author zly  
* @date 2018年12月6日
 */
public class ConfigFilter implements Filter{

	public static final String YKYM_APP_SELVET = "ykym.app.selvet";
	public static final String YKYM_WEB_OBJECT = "ykym.web.object";
	
	 private static Logger LOGGER=LoggerFactory.getLogger(ConfigFilter.class);
	
	public void destroy() {
		
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		//获取部分访问路径，若地址路径是http://localhost:8080/user/login，则url就是/user/login
		String url = request.getRequestURI();
		// 过滤掉静态文件请求
		if(url.contains(".css") || url.contains(".js") || url.contains(".png")|| url.contains(".jpg")|| url.contains(".xls")){
			// 不过滤 css,js,png,jpg,html 文件
			filterChain.doFilter(request, response);
			return;
		}/*else if (url.contains(".html")) {
			// filterChain.doFilter(request, response);
			response.sendRedirect(request.getContextPath()+"/index.html");
			return;
		}*/
		/*// 判断是否为ajax请求，默认不是  
        boolean isAjaxRequest = false;  
        if(!StringUtils.isBlank(request.getHeader("x-requested-with")) && request.getHeader("x-requested-with").equals("XMLHttpRequest")){  
        	isAjaxRequest =  true;
	    } */
		 // 获取请求方式
        String customHeader = request.getHeader("X-Custom-Header");

        //没有登陆
        if ( "Ajax".equals(customHeader)) { //ajax请求
		// 获取请求参数
		StringBuffer sb = new StringBuffer();
		InputStream is = request.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		String s = "";
		while ((s = br.readLine()) != null) {
			sb.append(s);
		}
        // 此处调用HttpCilent 请求后端服务
        // 截取URL
        String[] urlSplit = url.split(PropertUtils.getProperty(YKYM_WEB_OBJECT));
        String ipurl = PropertUtils.getProperty(YKYM_APP_SELVET)+urlSplit[1];
        // 将页面请求数据
        JSONObject jsonObject = JSONObject.parseObject(sb.toString());
        String sendJsonData = HttpClientUtil.sendJsonData(ipurl, jsonObject);
        // 返回前端数据
        response.setHeader("noAuthentication", "true");  
        response.setContentType("application/json;charset=UTF-8");  
        PrintWriter writer = response.getWriter();  
        writer.write(sendJsonData);  
        writer.close();  
        response.flushBuffer();  
		}else{
			filterChain.doFilter(request, response);
			return;
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		
	}


}
