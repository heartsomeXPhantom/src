/*
 * Created on 27.07.2010
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.support;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class StyledTextWithDragAndDropSupport extends org.eclipse.swt.custom.StyledText
{

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

	private de.folt.models.applicationmodel.guimodel.support.StyledTextWithDragAndDropSupport styledTextWithDragAndDropSupport;

	/**
	 * @param parent
	 * @param style
	 */
	public StyledTextWithDragAndDropSupport(Composite parent, int style)
	{
		super(parent, style);
		styledTextWithDragAndDropSupport = this;
		AddDropSupport(styledTextWithDragAndDropSupport);
	}

	private void AddDropSupport(Control control)
	{
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		DropTarget dragdropsupportwindow = new DropTarget(control, operations);

		// Receive data in Text or File format
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { fileTransfer, textTransfer };
		dragdropsupportwindow.setTransfer(types);
		dragdropsupportwindow.addDropListener(new DropTargetListener()
		{
			public void dragEnter(DropTargetEvent event)
			{
				if (event.detail == DND.DROP_DEFAULT)
				{
					if ((event.operations & DND.DROP_COPY) != 0)
					{
						event.detail = DND.DROP_COPY;
					}
					else
					{
						event.detail = DND.DROP_NONE;
					}
				}
				// will accept text but prefer to have files dropped
				for (int i = 0; i < event.dataTypes.length; i++)
				{
					if (fileTransfer.isSupportedType(event.dataTypes[i]))
					{
						event.currentDataType = event.dataTypes[i];
						// files should only be copied
						if (event.detail != DND.DROP_COPY)
						{
							event.detail = DND.DROP_NONE;
						}
						break;
					}
				}
			}

			public void dragLeave(DropTargetEvent event)
			{
				;
			}

			public void dragOperationChanged(DropTargetEvent event)
			{
				if (event.detail == DND.DROP_DEFAULT)
				{
					if ((event.operations & DND.DROP_COPY) != 0)
					{
						event.detail = DND.DROP_COPY;
					}
					else
					{
						event.detail = DND.DROP_NONE;
					}
				}
				// allow text to be moved but files should only be copied
				if (fileTransfer.isSupportedType(event.currentDataType))
				{
					if (event.detail != DND.DROP_COPY)
					{
						event.detail = DND.DROP_NONE;
					}
				}
			}

			public void dragOver(DropTargetEvent event)
			{
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
				if (textTransfer.isSupportedType(event.currentDataType))
				{
					// NOTE: on unsupported platforms this will return null
					Object o = textTransfer.nativeToJava(event.currentDataType);
					@SuppressWarnings("unused")
					String t = (String) o;
					// if (t != null)
					// System.out.println(t);
				}
			}

			public void drop(DropTargetEvent event)
			{
				@SuppressWarnings("unused")
				Widget widget = event.widget;
				if (textTransfer.isSupportedType(event.currentDataType))
				{
					// do nothing in this case
					@SuppressWarnings("unused")
					String text = (String) event.data;
				}
				if (fileTransfer.isSupportedType(event.currentDataType))
				{
					String[] files = (String[]) event.data;
					try
					{
						if (files.length > 0)
						{
							String file1 = files[0];
							styledTextWithDragAndDropSupport.setText(file1);
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}

			public void dropAccept(DropTargetEvent event)
			{
				;
			}
		});
	}

}
