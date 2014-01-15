/*
 * Created on 19.10.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import org.eclipse.swt.custom.StyledText;

import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.OpenTMSXMLStyledText;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SynchronizeEditWindows
{

    @SuppressWarnings("unused")
    private static boolean synchronizeText(Object changedStyledTextWindow1, Object toChangeStyledTextWindow)
    {
        return synchronizeText(changedStyledTextWindow1, ((StyledText) changedStyledTextWindow1).getText(), toChangeStyledTextWindow, ((StyledText) toChangeStyledTextWindow).getText());
    }

    private static boolean synchronizeText(Object changedStyledTextWindow1, String text1, Object toChangeStyledTextWindow, String text2)
    {
        if (text1.equals(text2)) // no change
            return false;

        if (changedStyledTextWindow1.getClass().getName().equals(XliffEditorWindow.class.getName()))
        {
            ((XliffEditorWindow)toChangeStyledTextWindow).setText(text1);
            return true;
        }
        
        if (toChangeStyledTextWindow.getClass().getName().equals(OpenTMSXMLStyledText.class.getName()))
        {
            ((OpenTMSXMLStyledText)toChangeStyledTextWindow).setText(text1);
            return true;
        }
        
        ((StyledText)toChangeStyledTextWindow).setText(text1);
        
        return true;
    }
}
