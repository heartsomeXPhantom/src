/*
 * Created on 02.06.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.util.Hashtable;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

/**
 * This class extends StyleRange and add some features. Esp. it is possible to associate a tool tip with a StyledText and to add additional data (key/value) pair with the OpenTMSStyleRange.
 * @author klemens
 *
 */
public class OpenTMSStyleRange extends StyleRange
{

    private boolean bEditable = true;

    private Hashtable<String, Object> data = new Hashtable<String, Object>();
    
    private String tooltipText = "";

    /**
     * @param startPos
     * @param len
     * @param tagForeGroundColor
     * @param tagBackGroundColor
     */
    public OpenTMSStyleRange(int startPos, int len, Color tagForeGroundColor, Color tagBackGroundColor)
    {
        super(startPos, len, tagForeGroundColor, tagBackGroundColor);
    }

    /**
     * @return the data
     */
    public Hashtable<String, Object> getData()
    {
        return data;
    }

    /**
     * getData get a data object associated with the OpenTMSStyleRange
     * @param string the key of the data
     * @return
     */
    public Object getData(String string)
    {
        return data.get(string);
    }

    /**
     * getToolTip get a tool tip for the OpenTMSStyleRange
     * @return
     */
    public String getToolTip()
    {
        return this.tooltipText;
    }

    /**
     * @return the bEditable
     */
    public boolean isBEditable()
    {
        return bEditable;
    }

    /**
     * removeData remove a data object
     * @param string the key for the data to remove
     * @return
     */
    public Object removeData(String string)
    {
        return data.remove(string);
    }

    /**
     * @param editable the bEditable to set; if true allow editing in the style range
     */
    public void setBEditable(boolean editable)
    {
        bEditable = editable;
    }

    /**
     * @param data the data to set
     */
    public void setData(Hashtable<String, Object> data)
    {
        this.data = data;
    }

    /**
     * setData associate a string as a data for the OpenTMSStyleRange (key = string / value = string)
     * @param string the string to set
     */
    public void setData(String string)
    {
        data.put(string, string);
    }

    /**
     * setData associate a data object with an OpenTMSStyleRange
     * @param string the key for the data
     * @param object the data object
     */
    public void setData(String string, Object object)
    {
        data.put(string, object);
    }

    /**
     * setToolTip set a tool tip for the OpenTMSStyleRange
     * @param toolTipText the tool tip text to display
     */
    public void setToolTipText(String toolTipText)
    {
        this.tooltipText = toolTipText;
    }

}
