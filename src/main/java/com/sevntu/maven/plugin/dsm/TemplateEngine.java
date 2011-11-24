package com.sevntu.maven.plugin.dsm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TemplateEngine {
	/**
	 * 
	 */
	public TemplateEngine() {
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return Sting of class attribute
	 */
	private String addAttribute(final String name, final String value) {
		return " " + name + "=\"" + value + "\"";
	}

	/**
	 * 
	 * @param tagName
	 *            Name of tag
	 * @param tagClassName
	 *            Name of some class from CSS file
	 * @return Sting of tag
	 */
	private String createTag(final String tagName,
			final HashMap<Attributes, String> attributes) {
		if (tagName == null) {
			throw new IllegalStateException();
		}
		String tagContent = tagName;
		if (attributes != null) {
			for (Iterator<Map.Entry<Attributes, String>> it = attributes
					.entrySet().iterator(); it.hasNext();) {
				Map.Entry<Attributes, String> attr = it.next();
				tagContent += addAttribute(attr.getKey().attributeName,
						attr.getValue());
			}
		}

		return "<" + tagContent + ">";
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String td(final String className) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, className);
		return createTag("td", attributes);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String td_() {
		return createTag("/td", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String tr(final String className) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, className);
		return createTag("tr", attributes);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String tr_() {
		return createTag("/tr", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String h1(final String className) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, className);
		return createTag("h1", attributes);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String h1_() {
		return createTag("/h1", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String ul(final String className) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, className);
		return createTag("ul", attributes);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String ul_() {
		return createTag("/ul", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String li(final String className) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, className);
		return createTag("li", attributes);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String li_() {
		return createTag("/li", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String body(final String className) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, className);
		return createTag("body", attributes);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String body_() {
		return createTag("/body", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String table(final String className) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, className);
		return createTag("table", attributes);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String table_() {
		return createTag("/table", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String b(final String className) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, className);
		return createTag("b", attributes);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String b_() {
		return createTag("/b", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String a(final String className, final String href,
			final String title, final String target) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, className);
		attributes.put(Attributes.HREF, href);
		attributes.put(Attributes.TITLE, title);
		attributes.put(Attributes.TARGET, target);
		return createTag("a", attributes);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String a_() {
		return createTag("/a", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String img(final String className, final String src, final String alt) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, className);
		attributes.put(Attributes.SRC, src);
		attributes.put(Attributes.ALT, alt);
		return createTag("img", attributes);
	}

	public enum Attributes {
		CLASS("class"), TITLE("title"), ALT("alt"), HREF("href"), SRC("src"), TARGET(
				"target");

		private String attributeName;

		private Attributes(String a) {
			attributeName = a;
		}
	}
}
