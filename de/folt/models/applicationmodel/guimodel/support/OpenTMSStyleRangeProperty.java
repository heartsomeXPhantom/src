/*
 * Created on 06.10.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.support;

/**
 * This class implements simple properties for a StyleRange. Currently it just
 * supports the type bEditable, this determines if a given Style Range can be
 * edited or not. Default is false
 * 
 * @author klemens
 */
public class OpenTMSStyleRangeProperty
{

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	private boolean bEditable = false;

	/**
	 * @param editable
	 *            the bEditable to set - set to true if style range is editable
	 */
	public OpenTMSStyleRangeProperty(boolean editable)
	{
		super();
		bEditable = editable;
	}

	/**
	 * @return the bEditable - is this an editable StyleRange? True if yes
	 */
	public boolean isBEditable()
	{
		return bEditable;
	}

	/**
	 * @param editable
	 *            the bEditable to set - set to true if style range is editable
	 */
	public void setBEditable(boolean editable)
	{
		bEditable = editable;
	}

}
