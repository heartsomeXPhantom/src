/*
 * Created on 29.05.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import de.folt.models.applicationmodel.guimodel.support.OpenTMSStyleRangeProperties;
import de.folt.models.applicationmodel.guimodel.support.OpenTMSStyleRangeProperty;
import de.folt.util.ColorTable;

/**
 * This class implements a configurable xml Style text viewer. It assumes a simple xml format. Namely only a flat xml structure is supported, like in tmx or xliff with its ph ebt etc. tags.
 * @author klemens
 *
 */
public class OpenTMSXMLStyledText extends OpenTMSStyledText
{

    protected boolean bChangeBackGroundOnChange = true;

    protected boolean changed = false;

    protected Color defaultBackGroundColor; // white;

    protected Color defaultForeGroundColor; // green

    protected final String matchTagPattern = "\\<(\\w+)[ */]?(.*?)\\>(.*?\\</\\1\\>)?";

    protected String oldToolTipText;

    protected OpenTMSStyleRangeProperties openTMSStyleRangeProperties = new OpenTMSStyleRangeProperties();

    protected Hashtable<String, TagDescriptor> tags = new Hashtable<String, TagDescriptor>();

    protected Pattern xmlTagPattern;

    /**
     * @param parent
     * @param style
     */
    public OpenTMSXMLStyledText(Composite parent, int style)
    {
        super(parent, style);
        defaultForeGroundColor = ColorTable.getInstance(getDisplay(), "white"); // white;
        defaultBackGroundColor = ColorTable.getInstance(getDisplay(), "green");// green 

        xmlTagPattern = Pattern.compile(matchTagPattern, Pattern.DOTALL);

        addVerifyListener(new VerifyListener()
        {
            public void verifyText(VerifyEvent event)
            {
                // Only expand when text is inserted.
                if (!getText().equals(""))
                {
                    if (bChangeBackGroundOnChange)
                        setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_YELLOW));
                    changed = true;
                }
            }
        });

        addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                ;
            }

            public void keyReleased(KeyEvent e)
            {
                ;
            }
        });

        addVerifyKeyListener(new VerifyKeyListener()
        {
            public void verifyKey(VerifyEvent event)
            {
                try
                {
                    int keyCode = event.keyCode;

                    ((OpenTMSXMLStyledText) event.widget).notifyObservers();
                    if ((keyCode == 16777222) || (keyCode == 16777221) || (keyCode == SWT.ARROW_LEFT) || (keyCode == SWT.ARROW_RIGHT) || (keyCode == SWT.ARROW_UP) || (keyCode == SWT.ARROW_DOWN)
                            || (keyCode == SWT.END) || (keyCode == SWT.F1))
                    {
                        return;
                    }
                    int caretOffset = getCaretOffset();

                    // we have to check if we are immediately before the style range where input is allowed!
                    if (caretOffset != 0)
                    {
                        StyleRange style = (StyleRange) getStyleRangeAtOffset(caretOffset - 1);
                        // need to check if delete key
                        // System.out.println(keyCode + " / " + caretOffset  + " / " +  (int)SWT.DEL  + " / " +  (int)SWT.BS + " / " + style);
                        if ((style != null) && (keyCode == SWT.BS))
                        {
                            if ((style.background.toString().equals(defaultBackGroundColor.toString())) && (style.foreground.toString().equals(defaultForeGroundColor.toString())))
                                event.doit = true;
                            else
                                event.doit = false;
                            return;
                        }
                        if ((caretOffset + 1) < ((OpenTMSXMLStyledText) event.widget).getText().length())
                        {
                            style = (StyleRange) getStyleRangeAtOffset(caretOffset + 1);
                            if ((style != null) && (keyCode == SWT.DEL))
                            {
                                if ((style.background.toString().equals(defaultBackGroundColor.toString())) && (style.foreground.toString().equals(defaultForeGroundColor.toString())))
                                    event.doit = true;
                                else
                                    event.doit = false;
                                return;
                            }
                            if (style == null)
                            {
                                event.doit = true;
                                return;
                            }
                        }
                    }
                    StyleRange style = (StyleRange) getStyleRangeAtOffset(caretOffset);
                    if ((openTMSStyleRangeProperties != null) && openTMSStyleRangeProperties.containsKey(style))
                    {
                        if (!openTMSStyleRangeProperties.get(style).isBEditable())
                        {
                            event.doit = false;
                            return;
                        }
                    }
                    if ((caretOffset + 1) == ((OpenTMSXMLStyledText) event.widget).getText().length())
                    {
                        // style = (StyleRange) getStyleRangeAtOffset(caretOffset - 1);
                        // if (!openTMSStyleRangeProperties.get(style).isBEditable())
                        // {
                        //    event.doit = false;
                        //    return;
                        //}
                    }
                    event.doit = true;
                    return;
                }
                catch (Exception e)
                {
                    event.doit = false;
                }
                event.doit = true;
            }
        });

        addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                ;
            }
        });

        addMouseMoveListener(new MouseMoveListener()
        {
            public void mouseMove(MouseEvent e)
            {
                Point p = new Point(e.x, e.y);
                // ((OpenTMSXMLStyledText)e.widget).notifyObservers();
                try
                {
                    int offset = getOffsetAtLocation(p);
                    StyleRange style = (StyleRange) getStyleRangeAtOffset(offset);
                    if (style == null)
                    {
                        setToolTipText(oldToolTipText);
                        return;
                    }
                    return;
                }
                catch (Exception e1)
                {
                    setToolTipText(oldToolTipText);
                }
                setToolTipText(oldToolTipText);
            }
        });
    }

    /**
     * addTagDescriptor add a TagDescriptor
     * @param tagDescriptor the tagDescriptor to add
     */
    public void addTagDescriptor(TagDescriptor tagDescriptor)
    {
        tags.put(tagDescriptor.getTagName(), tagDescriptor);
        StyleRange style = new StyleRange(1, 1, tagDescriptor.getTagForeGroundColor(), tagDescriptor.getTagBackGroundColor());
        OpenTMSStyleRangeProperty property = new OpenTMSStyleRangeProperty(tagDescriptor.isBEditable());
        this.openTMSStyleRangeProperties.put(style, property);
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.custom.StyledText#cut()
     */
    @Override
    public void cut()
    {
        int caretOffset = getCaretOffset();
        StyleRange style = (StyleRange) getStyleRangeAtOffset(caretOffset);
        if ((openTMSStyleRangeProperties != null) && openTMSStyleRangeProperties.containsKey(style))
        {
            if (!openTMSStyleRangeProperties.get(style).isBEditable())
            {
                return;
            }
        }
        super.cut();
    }

    /**
     * @return the degaultBackGroundColor
     */
    public Color getDefaultBackGroundColor()
    {
        return defaultBackGroundColor;
    }

    /**
     * @return the defaultForeGroundColor
     */
    public Color getDefaultForeGroundColor()
    {
        return defaultForeGroundColor;
    }

    /**
     * @return the openTMSStyleRangeProperties
     */
    public OpenTMSStyleRangeProperties getOpenTMSStyleRangeProperties()
    {
        return openTMSStyleRangeProperties;
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.custom.StyledText#getText()
     */
    @Override
    public String getText()
    {
        return super.getText();
    }

    /**
     * @return the bChangeBackGroundOnChange
     */
    public boolean isBChangeBackGroundOnChange()
    {
        return bChangeBackGroundOnChange;
    }

    /**
     * @return the changed
     */
    public boolean isChanged()
    {
        return changed;
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.custom.StyledText#paste()
     */
    @Override
    public void paste()
    {
        int caretOffset = getCaretOffset();
        StyleRange style = (StyleRange) getStyleRangeAtOffset(caretOffset);
        if ((openTMSStyleRangeProperties != null) && openTMSStyleRangeProperties.containsKey(style))
        {
            if (!openTMSStyleRangeProperties.get(style).isBEditable())
            {
                return;
            }
        }
        super.paste();
    }

    /**
     * removeTagDescriptors remove all TagDescriptors from the component
     */
    public void removeTagDescriptors()
    {
        tags.clear();
    }

    /**
     * @param changeBackGroundOnChange the bChangeBackGroundOnChange to set
     */
    public void setBChangeBackGroundOnChange(boolean changeBackGroundOnChange)
    {
        bChangeBackGroundOnChange = changeBackGroundOnChange;
    }

    /**
     * @param changed the changed to set
     */
    public void setChanged(boolean changed)
    {
        this.changed = changed;
    }

    /**
     * @param setDefaultBackGroundColor the defaultBackGroundColor to set
     */
    public void setDefaultBackGroundColor(Color defaultBackGroundColor)
    {
        this.defaultBackGroundColor = defaultBackGroundColor;
    }

    /**
     * @param defaultForeGroundColor the defaultForeGroundColor to set
     */
    public void setDefaultForeGroundColor(Color defaultForeGroundColor)
    {
        this.defaultForeGroundColor = defaultForeGroundColor;
    }

    /**
     * @param openTMSStyleRangeProperties the openTMSStyleRangeProperties to set
     */
    public void setOpenTMSStyleRangeProperties(OpenTMSStyleRangeProperties openTMSStyleRangeProperties)
    {
        this.openTMSStyleRangeProperties = openTMSStyleRangeProperties;
    }

    /**
     * setStyleRange set the style range for elements
     * @param text the text to "style range"
     * @return
     */
    public void setStyleRange(String text)
    {
        Matcher matcher = xmlTagPattern.matcher(text);
        // Font data = this.getFont();
        // FontData fontdata = data.getFontData()[0];
        // Font font1 = new Font(getDisplay(), fontdata.getName(), fontdata.getHeight() * 2, fontdata.getStyle());
        
        while (matcher.find()) // <..>...</..>
        {
            int iStartPos = matcher.start();
            int iEndPos = matcher.end();
            int iLen = iEndPos - iStartPos;
            boolean bUseDefault = true;
            if (matcher.groupCount() > 0)
            {
                String matchString = matcher.group(1);
                if (tags.containsKey(matchString))
                {
                    StyleRange range = new StyleRange(iStartPos, iLen, tags.get(matchString).getTagForeGroundColor(), tags.get(matchString).getTagBackGroundColor());
                    setStyleRange(range);
                    bUseDefault = false;
                }
            }
            if (bUseDefault)
            {
                StyleRange range = new StyleRange(iStartPos, iLen, this.getDefaultForeGroundColor(), this.getDefaultBackGroundColor());
                setStyleRange(range);
            }
        }
    }

    /**
     * setStyleRange 
     * @param text
     * @param startPosition
     * @param endPostion
     */
    public void setStyleRange(String text, int startPosition, int endPostion)
    {
        if (startPosition >= endPostion)
            return;
        String myText = text.substring(startPosition, endPostion - 1);
        Matcher matcher = xmlTagPattern.matcher(myText);
        while (matcher.find()) // <..>...</..>
        {
            int iStartPos = matcher.start() + startPosition;
            int iEndPos = matcher.end() + startPosition;
            int iLen = iEndPos - iStartPos;
            boolean bUseDefault = true;
            if (matcher.groupCount() > 0)
            {
                String matchString = matcher.group(1);
                if (tags.containsKey(matchString))
                {
                    TagDescriptor tag = tags.get(matchString);
                    if (!tag.isBProtectContent())
                    {
                        
                    }
                    StyleRange range = new StyleRange(iStartPos, iLen, tags.get(matchString).getTagForeGroundColor(), tags.get(matchString).getTagBackGroundColor());
                    if (tag.getFontStyle() != SWT.NORMAL)
                        range.fontStyle = tag.getFontStyle();
                    if (tag.getFont() != null)
                        range.font = tag.getFont();
                    if (tag.getRise() != 0)
                        range.rise = tag.getRise();
                    setStyleRange(range);
                    bUseDefault = false;
                }
            }
            if (bUseDefault)
            {
                StyleRange range = new StyleRange(iStartPos, iLen, this.getDefaultForeGroundColor(), this.getDefaultBackGroundColor());
                setStyleRange(range);
            }
        }

    }

    /**
     * setTemporaryToolTipText set the tool tip for the window
     * @param string the string for the tool tip
     */
    protected void setTemporaryToolTipText(String string)
    {
        super.setToolTipText(string);
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.custom.StyledText#setText(java.lang.String)
     */
    @Override
    public void setText(String text)
    {
        if (text == null)
            return;
        this.setData("initialText", text);
        super.setText(text);
        // now format the text by retrieving all the tags <..>..</..> or <../>
        // setStyleRange(text);
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Control#setToolTipText(java.lang.String)
     */
    @Override
    public void setToolTipText(String string)
    {
        oldToolTipText = string;
        super.setToolTipText(string);
    }

}
