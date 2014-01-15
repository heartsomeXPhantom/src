/*
 * Created on 07.06.2007
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.segmenter;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;

import de.folt.models.documentmodel.document.XmlDocument;

/**
 * @author klemens.waldhoer
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SegmentReader
{

	/**
	 * readSegments
	 * 
	 * @param xliff
	 * @return
	 */
	public static Vector readSegments(String xliff)
	{
		try
		{
			XmlDocument doc = new XmlDocument(xliff);
			Element root = doc.getRoot();
			Element file = root.getChild("file"); //$NON-NLS-1$
			Element body = file.getChild("body"); //$NON-NLS-1$
			List segments = body.getChildren("trans-unit"); //$NON-NLS-1$
			Iterator s = segments.iterator();
			int iNum = segments.size();
			Vector segmentVector = new Vector();
			;
			while (s.hasNext())
			{
				Element seg = (Element) s.next();
				segmentVector.add(seg);
			}

			return segmentVector;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
	}
}