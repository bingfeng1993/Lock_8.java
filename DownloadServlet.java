package com.gwz.upload.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import sun.misc.BASE64Encoder;

/**
 * 处理文件下载的Servlet
 */
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 获取要下载的资源的真实路径
		ServletContext servletContext = getServletContext();
		String realPath = servletContext.getRealPath("/WEB-INF/download/tsrj.mp3");

		// 给要下载的文件设置一个名字
		String filename = "千年等一回.mp3";
		// 获取请求头中的用户信息
		String header = request.getHeader("User-Agent");
		if (header.contains("Firefox")) {
			// 使用的是火狐浏览器
			// 对应火狐浏览器来说，对应中文名需要使用BASE64进行编码
			filename = "=?utf-8?b?" + new BASE64Encoder().encode(filename.getBytes()) + "?=";
		} else {
			// 对中文名字使用URLEncoder进行编码
			filename = URLEncoder.encode(filename, "UTF-8");
		}
		// 1.创建一个输入流
		InputStream is = new FileInputStream(realPath);
		// 2.设置两个响应头
		// ①设置要下载的文件的类型
		// 获取要下载的文件的mime值
		String mimeType = servletContext.getMimeType(realPath);
		response.setHeader("Content-Type", mimeType);
		// ②告诉浏览器如何处理该文件
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		// 3.将流输出到浏览器
		ServletOutputStream os = response.getOutputStream();
		IOUtils.copy(is, os);
		// 4.关闭流
		is.close();

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}

