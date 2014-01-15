/*
 * Created on 29.05.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Class describes the appearance of a tag; currently just the name and the color
 * @author klemens
 *
 */
public class TagDescriptor
{

    private boolean bEditable = false;

    private boolean bProtectContent = true;
    
    private Font font = null;
    
    private int fontStyle = SWT.NORMAL;

    private int rise = 0;
    
    private Color tagBackGroundColor = null;
    
    private Color tagForeGroundColor = null;
    
    private String tagName = null;

    /**
     * @param tagName
     * @param tagForeGroundColor
     * @param tagBackGroundColor
     */
    public TagDescriptor(String tagName, Color tagForeGroundColor, Color tagBackGroundColor)
    {
        super();
        this.tagName = tagName;
        this.tagForeGroundColor = tagForeGroundColor;
        this.tagBackGroundColor = tagBackGroundColor;
    }
    
    /**
     * @param tagName
     * @param tagForeGroundColor
     * @param tagBackGroundColor
     * @param editable
     */
    public TagDescriptor(String tagName, Color tagForeGroundColor, Color tagBackGroundColor, boolean editable)
    {
        super();
        this.tagName = tagName;
        this.tagForeGroundColor = tagForeGroundColor;
        this.tagBackGroundColor = tagBackGroundColor;
        bEditable = editable;
    }

    /**
     * @param tagName
     * @param tagForeGroundColor
     * @param tagBackGroundColor
     * @param editable
     * @param fontStyle
     * @param font
     * @param rise
     */
    public TagDescriptor(String tagName, Color tagForeGroundColor, Color tagBackGroundColor, boolean editable, int fontStyle, Font font)
    {
        super();
        this.tagName = tagName;
        this.tagForeGroundColor = tagForeGroundColor;
        this.tagBackGroundColor = tagBackGroundColor;
        bEditable = editable;
        this.fontStyle = fontStyle;
        this.font = font;
    }

    /**
     * @param tagName
     * @param tagForeGroundColor
     * @param tagBackGroundColor
     * @param editable
     * @param fontStyle
     * @param font
     * @param rise
     */
    public TagDescriptor(String tagName, Color tagForeGroundColor, Color tagBackGroundColor, boolean editable, int fontStyle, Font font, int rise)
    {
        super();
        this.tagName = tagName;
        this.tagForeGroundColor = tagForeGroundColor;
        this.tagBackGroundColor = tagBackGroundColor;
        bEditable = editable;
        this.fontStyle = fontStyle;
        this.font = font;
        this.rise = rise;
    }

    /**
     * @param tagName
     * @param tagForeGroundColor
     * @param tagBackGroundColor
     * @param editable
     * @param fontSytle
     * @param rise
     */
    public TagDescriptor(String tagName, Color tagForeGroundColor, Color tagBackGroundColor, boolean editable, int fontStyle, int rise)
    {
        super();
        this.tagName = tagName;
        this.tagForeGroundColor = tagForeGroundColor;
        this.tagBackGroundColor = tagBackGroundColor;
        bEditable = editable;
        this.fontStyle = fontStyle;
        this.rise = rise;
    }

    /**
     * @param tagName
     * @param tagForeGroundColor
     * @param tagBackGroundColor
     * @param font
     */
    public TagDescriptor(String tagName, Color tagForeGroundColor, Color tagBackGroundColor, Font font)
    {
        this.tagName = tagName;
        this.tagForeGroundColor = tagForeGroundColor;
        this.tagBackGroundColor = tagBackGroundColor;
        this.font = font;
        this.bProtectContent = true;
    }
    
    /**
     * @param tagName
     * @param tagForeGroundColor
     * @param tagBackGroundColor
     * @param font
     * @param bProtectContent
     */
    public TagDescriptor(String tagName, Color tagForeGroundColor, Color tagBackGroundColor, Font font, boolean bProtectContent)
    {
        this.tagName = tagName;
        this.tagForeGroundColor = tagForeGroundColor;
        this.tagBackGroundColor = tagBackGroundColor;
        this.font = font;
        this.bProtectContent = bProtectContent;
    }

    /**
     * @return the font
     */
    public Font getFont()
    {
        return font;
    }

    /**
     * @return the fontSytle
     */
    public int getFontStyle()
    {
        return fontStyle;
    }

    /**
     * @return the rise
     */
    public int getRise()
    {
        return rise;
    }

    /**
     * @return the tagBackGroundColor
     */
    public Color getTagBackGroundColor()
    {
        return tagBackGroundColor;
    }

    /**
     * @return the tagForeGroundColor
     */
    public Color getTagForeGroundColor()
    {
        return tagForeGroundColor;
    }

    /**
     * @return the tagName
     */
    public String getTagName()
    {
        return tagName;
    }

    /**
     * @return the bEditable
     */
    public boolean isBEditable()
    {
        return bEditable;
    }

    /**
     * @return the bProtectContent
     */
    public boolean isBProtectContent()
    {
        return bProtectContent;
    }

    /**
     * @param bEditable the bEditable to set
     */
    public void setBEditable(boolean bEditable)
    {
        this.bEditable = bEditable;
    }

    /**
     * @param bProtectContent the bProtectContent to set
     */
    public void setBProtectContent(boolean bProtectContent)
    {
        this.bProtectContent = bProtectContent;
    }

    /**
     * @param font the font to set
     */
    public void setFont(Font font)
    {
        this.font = font;
    }

    /**
     * @param fontSytle the fontSytle to set
     */
    public void setFontStyle(int fontStyle)
    {
        this.fontStyle = fontStyle;
    }

    /**
     * @param rise the rise to set
     */
    public void setRise(int rise)
    {
        this.rise = rise;
    }

    /**
     * @param tagBackGroundColor the tagBackGroundColor to set
     */
    public void setTagBackGroundColor(Color tagBackGroundColor)
    {
        this.tagBackGroundColor = tagBackGroundColor;
    }

    /**
     * @param tagForeGroundColor the tagForeGroundColor to set
     */
    public void setTagForeGroundColor(Color tagForeGroundColor)
    {
        this.tagForeGroundColor = tagForeGroundColor;
    }

    /**
     * @param tagName the tagName to set
     */
    public void setTagName(String tagName)
    {
        this.tagName = tagName;
    }
}
