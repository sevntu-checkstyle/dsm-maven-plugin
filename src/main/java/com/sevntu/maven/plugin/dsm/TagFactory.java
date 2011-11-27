package com.sevntu.maven.plugin.dsm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TagFactory {

	/**
	 * 
	 * @param aAttributeName
	 *            Name of attribute
	 * @param aAttributeValue
	 *            Value fo attribute
	 * @return Sting of class attribute
	 */
	private String createTagAttribute(final String aAttributeName,
			final String aAttributeValue) {
		if (!textHasContent(aAttributeName)) {
			throw new IllegalArgumentException("Attribute name has no content.");
		}
		if (aAttributeValue == null) {
			throw new IllegalArgumentException(
					"Attribute value should not be null.");
		}
		return " " + aAttributeName + "=\"" + aAttributeValue + "\"";
	}

	/**
	 * 
	 * @param aTagName
	 *            Name of tag
	 * @param aAttributes
	 *            Some attributes for tag
	 * @return Sting of tag
	 */
	private String createTag(final String aTagName,
			final HashMap<Attributes, String> aAttributes) {
		if (!textHasContent(aTagName)) {
			throw new IllegalArgumentException("Tag name has no content.");
		}
		String tagContent = aTagName;
		if (aAttributes != null) {
			for (Iterator<Map.Entry<Attributes, String>> it = aAttributes
					.entrySet().iterator(); it.hasNext();) {
				Map.Entry<Attributes, String> attr = it.next();
				tagContent += createTagAttribute(attr.getKey().attributeName,
						attr.getValue());
			}
		}

		return "<" + tagContent + ">";
	}

	/**
	 * 
	 * @param aTagName
	 *            Name of tag
	 * @param aAttributeName
	 *            Name of tag attribute
	 * @param aAttributeValue
	 *            Value of tag attribute
	 * @return
	 */
	private String createTag(final String aTagName,
			final String aAttributeName, final String aAttributeValue) {
		if (!textHasContent(aTagName)) {
			throw new IllegalArgumentException("Tag name has no content.");
		}
		if (!textHasContent(aAttributeName)) {
			throw new IllegalArgumentException("Attribute name has no content.");
		}
		String tagContent = aTagName;
		if (textHasContent(aAttributeValue)) {
			tagContent += createTagAttribute(aAttributeName, aAttributeValue);
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
		return createTag("td", Attributes.CLASS.attributeName, className);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String tdEnd() {
		return createTag("/td", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String tr(final String className) {
		return createTag("tr", Attributes.CLASS.attributeName, className);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String trEnd() {
		return createTag("/tr", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String h1(final String className) {
		return createTag("h1", Attributes.CLASS.attributeName, className);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String h1End() {
		return createTag("/h1", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String ul(final String className) {
		return createTag("ul", Attributes.CLASS.attributeName, className);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String ulEnd() {
		return createTag("/ul", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String li(final String className) {
		return createTag("li", Attributes.CLASS.attributeName, className);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String liEnd() {
		return createTag("/li", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String body(final String className) {
		return createTag("body", Attributes.CLASS.attributeName, className);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String bodyEnd() {
		return createTag("/body", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String table(final String className) {
		return createTag("table", Attributes.CLASS.attributeName, className);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String tableEnd() {
		return createTag("/table", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public String b(final String className) {
		return createTag("b", Attributes.CLASS.attributeName, className);
	}

	/**
	 * 
	 * @return String of tag
	 */
	public String bEnd() {
		return createTag("/b", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @param href
	 *            Link
	 * @param title
	 *            Title of link
	 * @param target
	 *            Frame name where link will be open
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
	public String aEnd() {
		return createTag("/a", null);
	}

	/**
	 * 
	 * @param className
	 *            Name of some class from CSS file
	 * @param src
	 *            Link
	 * @param alt
	 *            Title of link
	 * @return String of tag
	 */
	public String img(final String className, final String src, final String alt) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, className);
		attributes.put(Attributes.SRC, src);
		attributes.put(Attributes.ALT, alt);
		return createTag("img", attributes);
	}

	public static boolean textHasContent(String aText) {
		String emptyString = "";
		return (aText != null) && (!aText.trim().equals(emptyString));
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
