/*
 * Created on 08.07.2011
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.segmenter;

import java.util.List;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface Segment
{

	public abstract boolean isHardBoundary();

	public abstract int getHardBoundaryLevel();

	public abstract String getSegmentString();

	public abstract void setSegmentString(String s);

	public abstract String getFormattedSegmentString();

	public abstract void setFormattedSegmentString(String s);

	public abstract List getWords();

	public abstract List getNonWords();

	public abstract List getFormatting();

	public abstract List getNumbers();

	public abstract String getDocumentFormat();

	public abstract String getHardboundaryTagName();

	public abstract String getId();

	/**
	 * @param id
	 *            The id to set.
	 */
	public abstract void setId(String id);

	public static final int NOTBOUNDARY = 0;
	public static final int SOFTFLOW = 1;
	public static final int SOFT = 2;
	public static final int HARD = 3;
	public static final int HARDSUBSECTION = 4;
	public static final int HARDSECTION = 5;
}
