/*
 * Created on 19.10.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;

import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.OpenTMSXMLStyledText;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.TagDescriptor;
import de.folt.util.ColorTable;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SimpleXliffEditorWindow extends OpenTMSXMLStyledText
{

    /**
     * @param parent
     * @param style
     */
    public SimpleXliffEditorWindow(Composite parent, int style)
    {
        super(parent, style);
        setTagDescriptors();
        addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                ;
            }
        });

        addVerifyKeyListener(new VerifyKeyListener()
        {
            public void verifyKey(VerifyEvent event)
            {

            }
        });

        addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {

            }

            public void keyReleased(KeyEvent e)
            {
                ((OpenTMSXMLStyledText) e.widget).notifyObservers();
            }
        });

    }

    public void setTagDescriptors()
    {
        // tmx
        Font font = this.getFont();
        Font font1 = new Font(font.getDevice(), font.getFontData()[0].getName(), font.getFontData()[0].getHeight() - 1, font.getFontData()[0].getStyle());

        TagDescriptor tagDescriptorPh = new TagDescriptor("ph", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "darkorange"), font1);
        TagDescriptor tagDescriptorUt = new TagDescriptor("ut", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "orange"), font1);
        TagDescriptor tagDescriptorIt = new TagDescriptor("it", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "orangered"), font1);
        TagDescriptor tagDescriptorHi = new TagDescriptor("hi", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "purple"), font1);
        TagDescriptor tagDescriptorEpt = new TagDescriptor("ebt", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "palevioletred"), font1);
        TagDescriptor tagDescriptorBpt = new TagDescriptor("bpt", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "indianred"), font1);
        TagDescriptor tagDescriptorSub = new TagDescriptor("sub", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "mediumvioletred"), font1);

        // xliff
        TagDescriptor tagDescriptorG = new TagDescriptor("g", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "cadetblue"), font1);
        TagDescriptor tagDescriptorX = new TagDescriptor("x", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "blue"), font1);
        TagDescriptor tagDescriptorBx = new TagDescriptor("bx", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "blueviolet"), font1);
        TagDescriptor tagDescriptorEx = new TagDescriptor("ex", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "cadetblue"), font1);
        TagDescriptor tagDescriptorMrk = new TagDescriptor("mrk", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "cornflowerblue"), font1);

        addTagDescriptor(tagDescriptorG);
        addTagDescriptor(tagDescriptorPh);
        addTagDescriptor(tagDescriptorUt);
        addTagDescriptor(tagDescriptorIt);
        addTagDescriptor(tagDescriptorHi);
        addTagDescriptor(tagDescriptorEpt);
        addTagDescriptor(tagDescriptorBpt);
        addTagDescriptor(tagDescriptorSub);
        addTagDescriptor(tagDescriptorX);
        addTagDescriptor(tagDescriptorBx);
        addTagDescriptor(tagDescriptorEx);
        addTagDescriptor(tagDescriptorMrk);
    }

}
