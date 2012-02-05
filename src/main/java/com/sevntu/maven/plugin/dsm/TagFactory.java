package com.sevntu.maven.plugin.dsm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 
 * @author yuriy
 * 
 */
public final class TagFactory {

	private TagFactory() {
		// no code
	}


	/**
	 * 
	 * @param aAttributeName
	 *            Name of attribute
	 * @param aAttributeValue
	 *            Value fo attribute
	 * @return Sting of class attribute
	 */
	private static String createTagAttribute(final String aAttributeName,
			final String aAttributeValue) {
		if (!isNotEmptyString(aAttributeName)) {
			throw new IllegalArgumentException("Attribute name has no content.");
		}
		if (aAttributeValue == null) {
			throw new IllegalArgumentException("Attribute value should not be null.");
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
	private static String createTag(final String aTagName,
			final HashMap<Attributes, String> aAttributes) {
		if (!isNotEmptyString(aTagName)) {
			throw new IllegalArgumentException("Tag name has no content.");
		}
		String tagContent = aTagName;
		if (aAttributes != null) {
			for (Iterator<Map.Entry<Attributes, String>> it = aAttributes.entrySet().iterator(); it
					.hasNext();) {
				Map.Entry<Attributes, String> attr = it.next();
				tagContent += createTagAttribute(attr.getKey().attributeName, attr.getValue());
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
	private static String createTag(final String aTagName, final String aAttributeName,
			final String aAttributeValue) {
		if (!isNotEmptyString(aTagName)) {
			throw new IllegalArgumentException("Tag name has no content.");
		}
		if (!isNotEmptyString(aAttributeName)) {
			throw new IllegalArgumentException("Attribute name has no content.");
		}
		String tagContent = aTagName;
		if (isNotEmptyString(aAttributeValue)) {
			tagContent += createTagAttribute(aAttributeName, aAttributeValue);
		}

		return "<" + tagContent + ">";
	}


	/**
	 * 
	 * @param aClassName
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public final static String td(final String aClassName) {
		return createTag("td", Attributes.CLASS.attributeName, aClassName);
	}


	/**
	 * 
	 * @return String of tag
	 */
	public final static String tdEnd() {
		return createTag("/td", null);
	}


	/**
	 * 
	 * @param aClassName
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public final static String tr(final String aClassName) {
		return createTag("tr", Attributes.CLASS.attributeName, aClassName);
	}


	/**
	 * 
	 * @return String of tag
	 */
	public final static String trEnd() {
		return createTag("/tr", null);
	}


	/**
	 * 
	 * @param aClassName
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public final static String h1(final String aClassName) {
		return createTag("h1", Attributes.CLASS.attributeName, aClassName);
	}


	/**
	 * 
	 * @return String of tag
	 */
	public final static String h1End() {
		return createTag("/h1", null);
	}


	/**
	 * 
	 * @param aClassName
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public final static String ul(final String aClassName) {
		return createTag("ul", Attributes.CLASS.attributeName, aClassName);
	}


	/**
	 * 
	 * @return String of tag
	 */
	public final static String ulEnd() {
		return createTag("/ul", null);
	}


	/**
	 * 
	 * @param aClassName
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public final static String li(final String aClassName) {
		return createTag("li", Attributes.CLASS.attributeName, aClassName);
	}


	/**
	 * 
	 * @return String of tag
	 */
	public final static String liEnd() {
		return createTag("/li", null);
	}


	/**
	 * 
	 * @param aClassName
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public final static String body(final String aClassName) {
		return createTag("body", Attributes.CLASS.attributeName, aClassName);
	}


	/**
	 * 
	 * @return String of tag
	 */
	public final static String bodyEnd() {
		return createTag("/body", null);
	}


	/**
	 * 
	 * @param aClassName
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public final static String table(final String aClassName) {
		return createTag("table", Attributes.CLASS.attributeName, aClassName);
	}


	/**
	 * 
	 * @return String of tag
	 */
	public final static String tableEnd() {
		return createTag("/table", null);
	}


	/**
	 * 
	 * @param aClassName
	 *            Name of some class from CSS file
	 * @return String of tag
	 */
	public final static String b(final String aClassName) {
		return createTag("b", Attributes.CLASS.attributeName, aClassName);
	}


	/**
	 * 
	 * @return String of tag
	 */
	public final static String bEnd() {
		return createTag("/b", null);
	}


	/**
	 * 
	 * @param aClassName
	 *            Name of some class from CSS file
	 * @param aHref
	 *            Link
	 * @param aTitle
	 *            Title of link
	 * @param aTarget
	 *            Frame name where link will be open
	 * @return String of tag
	 */
	public final static String a(final String aClassName, final String aHref, final String aTitle,
			final String aTarget) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, aClassName);
		attributes.put(Attributes.HREF, aHref);
		attributes.put(Attributes.TITLE, aTitle);
		attributes.put(Attributes.TARGET, aTarget);
		return createTag("a", attributes);
	}


	/**
	 * 
	 * @return String of tag
	 */
	public final static String aEnd() {
		return createTag("/a", null);
	}


	/**
	 * 
	 * @param aClassName
	 *            Name of some class from CSS file
	 * @param aSrc
	 *            Link
	 * @param aAlt
	 *            Title of link
	 * @return String of tag
	 */
	public final static String img(final String aClassName, final String aSrc, final String aAlt) {
		HashMap<Attributes, String> attributes = new HashMap<Attributes, String>();
		attributes.put(Attributes.CLASS, aClassName);
		attributes.put(Attributes.SRC, aSrc);
		attributes.put(Attributes.ALT, aAlt);
		return createTag("img", attributes);
	}


	public final static boolean isNotEmptyString(String aText) {
		return (aText != null) && (!aText.trim().isEmpty());
	}

	public static enum Attributes {
		CLASS("class"), TITLE("title"), ALT("alt"), HREF("href"), SRC("src"), TARGET("target");

		private String attributeName;


		private Attributes(String attributeName) {
			this.attributeName = attributeName;
		}
	}
}
