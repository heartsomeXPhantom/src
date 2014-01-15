/*
 * Created on 06.10.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.support;

import java.util.Hashtable;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import de.folt.util.ColorTable;

/**
 * This class implements a simple association of properties to a StyleRang.
 * Basically it uses (for the moment) range.background.toString() +
 * range.foreground.toString() to create a string key for the StyleRange.<br>
 * This will be improved in the future.
 * 
 * @author klemens
 * 
 */
public class OpenTMSStyleRangeProperties extends Hashtable<String, OpenTMSStyleRangeProperty>
{

	/**
     * 
     */
	private static final long serialVersionUID = -3291950131615877840L;

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		Display display = new Display();
		StyleRange range = new StyleRange(1, 1, ColorTable.getInstance(display, "blue"), ColorTable.getInstance(
				display, "grey"));
		OpenTMSStyleRangeProperties properties = new OpenTMSStyleRangeProperties();
		String key = properties.computeStyleRangeKey(range);
		System.out.println(key);
		OpenTMSStyleRangeProperty property = new OpenTMSStyleRangeProperty(true);
		properties.put(range, property);

		Color t1 = ColorTable.getInstance(display, "blue");
		Color t2 = ColorTable.getInstance(display, "grey");
		StyleRange testrange = new StyleRange(1, 1, t1, t2);

		Color t1a = ColorTable.getInstance(display, "blue");
		if (t1 == t1a)
		{
			System.out.println("Found: " + t1 + " " + t1a);
		}
		else
		{
			System.out.println("Not Found: " + t1 + " " + t1a);
		}

		boolean bContains = properties.containsKey(testrange);
		System.out.println(bContains);
		OpenTMSStyleRangeProperty bTest = properties.get(testrange);
		System.out.println(bTest.isBEditable());

		Color t3 = ColorTable.getInstance(display, "green");
		testrange = new StyleRange(1, 1, ColorTable.getInstance(display, "blue"), t3);

		bContains = properties.containsKey(testrange);
		System.out.println(bContains);
		if (bContains)
		{
			bTest = properties.get(testrange);
			System.out.println(bTest.isBEditable());
		}

		ColorTable.removeInstance();
	}

	private String computeStyleRangeKey(StyleRange range)
	{
		String key = "";
		if (range == null)
			return key;
		key = range.background.toString() + range.foreground.toString(); // +
																			// range.fontStyle
																			// +
																			// "";
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Hashtable#containsKey(java.lang.Object)
	 */
	public synchronized boolean containsKey(StyleRange range)
	{
		return super.containsKey(computeStyleRangeKey(range));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Hashtable#get(java.lang.Object)
	 */
	public synchronized OpenTMSStyleRangeProperty get(StyleRange range)
	{
		return super.get(computeStyleRangeKey(range));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Hashtable#get(java.lang.Object)
	 */
	public synchronized Object OpenTMSStyleRangeProperty(StyleRange range)
	{
		return super.get(computeStyleRangeKey(range));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
	 */
	public synchronized OpenTMSStyleRangeProperty put(StyleRange range, OpenTMSStyleRangeProperty property)
	{
		return super.put(computeStyleRangeKey(range), property);
	}
}
