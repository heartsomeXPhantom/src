/*
 * Created on 13.06.2010
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.jsp;

import java.util.Vector;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OpenTMSTEI extends TagExtraInfo
{

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.jsp.tagext.TagExtraInfo#getVariableInfo(javax.servlet.jsp
     * .tagext.TagData)
     */
    @Override
    public VariableInfo[] getVariableInfo(TagData tagData)
    {

	Vector<VariableInfo> vec = new Vector<VariableInfo>();

	VariableInfo info = new VariableInfo("logfile", String.class.getName(), true, VariableInfo.AT_BEGIN);
	vec.add(info);
	info = new VariableInfo("propfile", String.class.getName(), true, VariableInfo.AT_BEGIN);
	vec.add(info);
	info = new VariableInfo("propertiesString", String.class.getName(), true, VariableInfo.AT_BEGIN);
	vec.add(info);
	info = new VariableInfo("dataSourceConfigurationsFile", String.class.getName(), true, VariableInfo.AT_BEGIN);
	vec.add(info);
	info = new VariableInfo("transUnit", String.class.getName(), true, VariableInfo.AT_BEGIN);
	vec.add(info);
	
	VariableInfo[] variableInfo = null;
	if (vec.size() > 0)
	{
	    variableInfo = new VariableInfo[vec.size()];
	    if (variableInfo != null)
	    {
		for (int i = 0; i < vec.size(); i++)
		{
		    variableInfo[i] = vec.get(i);
		}
	    }
	}

	return variableInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.jsp.tagext.TagExtraInfo#isValid(javax.servlet.jsp.tagext
     * .TagData)
     */
    @Override
    public boolean isValid(TagData data)
    {
	// TODO Auto-generated method stub
	return super.isValid(data);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.jsp.tagext.TagExtraInfo#validate(javax.servlet.jsp.tagext
     * .TagData)
     */
    @Override
    public ValidationMessage[] validate(TagData data)
    {
	// TODO Auto-generated method stub
	return super.validate(data);
    }

}
