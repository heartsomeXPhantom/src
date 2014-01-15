/*
 * Created on 23.06.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OpenTMSStyledTextWithTitle extends Shell
{

    Composite parent;

    int style;
    
    Shell shell;
    
    OpenTMSStyledText openTMSStyledText;

    /**
     * @param parent
     * @param style
     */
    public OpenTMSStyledTextWithTitle(int style)
    {
        super();
        this.style = style;
        
        openTMSStyledText = new OpenTMSStyledText(this, style);
    }
}
