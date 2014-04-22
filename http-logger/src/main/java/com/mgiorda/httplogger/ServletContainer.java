package com.mgiorda.httplogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServletContainer extends HttpServlet {

	private static final long serialVersionUID = -1886366373810761633L;

	private static final Log logger = LogFactory.getLog(ServletContainer.class);

	private FilterConfig filterConfig;

	@Override
	public ServletContext getServletContext() {
		if (filterConfig != null)
			return filterConfig.getServletContext();

		return super.getServletContext();
	}

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info(String.format("Servicing request '%s: %s' from ip '%s'", request.getMethod(), request.getPathInfo(), request.getRemoteAddr()));

		StringBuilder builder = new StringBuilder();

		builder.append(String.format("HTTP Request : %s %s\n", request.getMethod(), request.getPathInfo()));

		if (request.getQueryString() != null) {
			builder.append("?" + request.getQueryString() + "\n");
		}

		builder.append("\n");

		@SuppressWarnings("unchecked")
		Enumeration<String> headerNames = (Enumeration<String>) request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			String value = request.getHeader(headerName);
			builder.append(headerName + " " + value + "\n");
		}

		String content = getContentAsString(request.getInputStream());
		builder.append("\n" + content);

		System.out.println(builder.toString());

		response.setStatus(200);
	}

	private String getContentAsString(InputStream inputStream) {

		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		String line = null;

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}

		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return stringBuilder.toString();
	}
}
