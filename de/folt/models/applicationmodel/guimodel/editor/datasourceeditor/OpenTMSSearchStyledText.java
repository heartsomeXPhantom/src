/*
 * Created on 28.05.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OpenTMSSearchStyledText extends OpenTMSStyledText
{

    /**
     * @param parent
     * @param style
     */
    public OpenTMSSearchStyledText(Composite parent, int style)
    {
        super(parent, style);
    }

    /* (non-Javadoc)
     * @see de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.OpenTMSStyledText#handleRightMouseClick(org.eclipse.swt.widgets.Widget)
     */
    @Override
    protected Menu handleRightMouseClick(Widget w, MouseEvent eMouse)
    {
        Menu popupmenu = super.handleRightMouseClick(w, eMouse);
        new MenuItem(popupmenu, SWT.SEPARATOR);
        MenuItem searchItem = new MenuItem(popupmenu, SWT.PUSH);
        searchItem.setText(message.getString("SearchSegment"));
        searchItem.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                search();
            }
        });
        return popupmenu;
    }
    
    private void search()
    {
        DataSourceForm dataSourceForm = (DataSourceForm) this.getData("DataSourceForm");
        if (dataSourceForm != null)    
            dataSourceForm.search();
    }

}
