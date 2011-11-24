package com.sevntu.maven.plugin.dsm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Read content from file in source folder of project
 * 
 * @author yuriy
 * 
 */
public class ReadContentFromFile {
	private String template;

	/**
	 * Constructor. Read content from template file.
	 * 
	 * @param path
	 *            Path to input file
	 */
	public ReadContentFromFile(String path) {
		InputStream inputStream = getClass().getResourceAsStream("/" + path);
		template = convertStreamToString(inputStream);
	}

	/**
	 * Read content from input stream.
	 * 
	 * @param is
	 *            input stream of source file
	 * @return content from template file
	 */
	public String convertStreamToString(InputStream is) {
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	/**
	 * Get content of template
	 * 
	 * @return Content of template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * Set template content
	 * 
	 * @param template
	 *            Add content
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
}
