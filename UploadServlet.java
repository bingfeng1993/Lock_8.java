package com.atguigu.upload.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 处理文件上传的Servlet
 */
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 获取姓名和文件
		// String username = request.getParameter("username");
		// String photo = request.getParameter("photo");
		// System.out.println(username);
		// System.out.println(photo);
		// 1.创建工厂类实例
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 2.创建解析器类实例
		ServletFileUpload fileUpload = new ServletFileUpload(factory);
		// 限制单个文件的大小不能超过20KB
		fileUpload.setFileSizeMax(20 * 1024);
		// 设置总文件的大小不能超过20KB
		fileUpload.setSizeMax(20 * 1024);
		try {
			// 3.解析请求
			List<FileItem> fileItems = fileUpload.parseRequest(request);
			// 4.遍历得到每一个FileItem对象
			for (FileItem fileItem : fileItems) {
				/*
				 * 7个核心方法：
				 * 
				 * isFormField()：判断是否是一个普通表单项 getString(String
				 * encoding)：获取普通表单项中输入的文本值，该方法中可以传入一个字符集 getFieldName()：获取表单项的name属性值
				 * 
				 * getName()：获取文件名 getContentType()：获取文件的类型 getSize()：获取文件的大小，单位是字节 write(File
				 * file)：将上传的文件写到某个位置
				 */
				boolean formField = fileItem.isFormField();
				if (formField) {
					// 是一个普通表单项
					// 获取用户输入的文本
					String userInput = fileItem.getString("UTF-8");
					// 获取文本框的name属性值
					String fieldName = fileItem.getFieldName();
					System.out.println("用户输入的姓名是：" + userInput);
					System.out.println("文本框的name属性值是：" + fieldName);
				} else {
					// 不是普通表单项
					// 获取文件名
					String name = fileItem.getName();
					// 对应IE浏览器，在获取文件名时会带盘符，需要截取
					name = name.substring(name.lastIndexOf("\\") + 1);
					// 获取文件的类型
					String contentType = fileItem.getContentType();
					// 获取文件的大小
					long size = fileItem.getSize();
					System.out.println("文件名是：" + name);
					System.out.println("文件名的类型是：" + contentType);
					System.out.println("文件名的大小是：" + size + "个字节");
					// 将文件写到G盘的根目录
					// fileItem.write(new File("G:/" + name));
					// 要求：将文件上传到服务器的upload目录中
					// 获取ServletContext对象
					ServletContext servletContext = this.getServletContext();
					String realPath = servletContext.getRealPath("/upload");

					// 判断服务器中是否有upload目录，如果没有让它自动创建
					File file = new File(realPath);
					if (!file.exists()) {
						// 创建该目录
						file.mkdirs();
					}
					// 使用UUID生产一个随机的字符串
					String prfix = UUID.randomUUID().toString().replace("-", "");
					// 将文件上传到upload目录中
					fileItem.write(new File(realPath + "/" + prfix + "_" + name));
				}
			}
			// 重定向到成功页面
			response.sendRedirect(request.getContextPath() + "/success.jsp");
		} catch (FileSizeLimitExceededException e) {
			// e.printStackTrace();
			// 设置一个提示信息，并放到request域中
			request.setAttribute("msg", "单个文件的大小不能超过20KB!");
			// 转发到首页
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}catch (SizeLimitExceededException e) {
			// e.printStackTrace();
			// 设置一个提示信息，并放到request域中
			request.setAttribute("msg", "总文件的大小不能超过20KB!");
			// 转发到首页
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}

