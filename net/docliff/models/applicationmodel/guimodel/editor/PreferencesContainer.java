/*
 * Created on 27.10.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import org.eclipse.swt.graphics.Font;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PreferencesContainer
{
	private Font defaultFont;

	private int defaultFontHeight;

	private String defaultFontName;

	private int defaultFontStyle;

	private String guiLanguage = "en";

	private String ptDataSources = "";

	private boolean queryAddTranslation;

	private boolean queryNewTranslation;

	private boolean querySourceMatch;

	private boolean queryTargetMatch;

	private String tmDataSources = "";

	private boolean segmentDictionaryOnTop = true;

	private boolean globalDictionaryOnTop = true;

	private boolean bSearchIfApproved = true;

	/**
	 * @return the defaultFont
	 */
	public Font getDefaultFont()
	{
		return defaultFont;
	}

	/**
	 * @return the defaultFontHeight
	 */
	public int getDefaultFontHeight()
	{
		return defaultFontHeight;
	}

	/**
	 * @return the defaultFontName
	 */
	public String getDefaultFontName()
	{
		return defaultFontName;
	}

	/**
	 * @return the defaultFontStyle
	 */
	public int getDefaultFontStyle()
	{
		return defaultFontStyle;
	}

	/**
	 * @return the guiLanguage
	 */
	public String getGuiLanguage()
	{
		return guiLanguage;
	}

	/**
	 * getPreferences
	 * 
	 * @param editorConfiguration
	 */
	public void getPreferences(de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration editorConfiguration)
	{
		queryNewTranslation = editorConfiguration.loadBooleanValueForKey("queryNewTranslation");
		queryAddTranslation = editorConfiguration.loadBooleanValueForKey("queryAddTranslation");
		querySourceMatch = editorConfiguration.loadBooleanValueForKey("querySourceMatch");
		queryTargetMatch = editorConfiguration.loadBooleanValueForKey("queryTargetMatch");

		bSearchIfApproved = editorConfiguration.loadBooleanValueForKey("searchIfApproved");

		globalDictionaryOnTop = editorConfiguration.loadBooleanValueForKey("globalDictionaryOnTop");
		segmentDictionaryOnTop = editorConfiguration.loadBooleanValueForKey("segmentDictionaryOnTop");

		guiLanguage = editorConfiguration.loadValueForKey("guiLanguage");

		tmDataSources = editorConfiguration.loadValueForKey("tmDataSources");
		ptDataSources = editorConfiguration.loadValueForKey("ptDataSources");

		setDefaultFontHeight(editorConfiguration.loadIntValueForKey("defaultFontHeight"));
		setDefaultFontStyle(editorConfiguration.loadIntValueForKey("defaultFontStyle"));
		setDefaultFontName(editorConfiguration.loadValueForKey("defaultFontName"));
	}

	/**
	 * @return the ptDataSources
	 */
	public String getPtDataSources()
	{
		return ptDataSources;
	}

	/**
	 * @return the tmDataSources
	 */
	public String getTmDataSources()
	{
		return tmDataSources;
	}

	/**
	 * @return the bSearchIfApproved
	 */
	public boolean isbSearchIfApproved()
	{
		return bSearchIfApproved;
	}

	/**
	 * @return the globalDictionaryOnTop
	 */
	public boolean isGlobalDictionaryOnTop()
	{
		return globalDictionaryOnTop;
	}

	/**
	 * @return the queryAddTranslation
	 */
	public boolean isQueryAddTranslation()
	{
		return queryAddTranslation;
	}

	/**
	 * @return the queryNewTranslation
	 */
	public boolean isQueryNewTranslation()
	{
		return queryNewTranslation;
	}

	/**
	 * @return the querySourceMatch
	 */
	public boolean isQuerySourceMatch()
	{
		return querySourceMatch;
	}

	/**
	 * @return the queryTargetMatch
	 */
	public boolean isQueryTargetMatch()
	{
		return queryTargetMatch;
	}

	/**
	 * @return the segmentDictionaryOnTop
	 */
	public boolean isSegmentDictionaryOnTop()
	{
		return segmentDictionaryOnTop;
	}

	/**
	 * savePreferences
	 * 
	 * @param editorConfiguration
	 */
	public void savePreferences(de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration editorConfiguration)
	{
		editorConfiguration.saveKeyValuePair("queryNewTranslation", queryNewTranslation);
		editorConfiguration.saveKeyValuePair("queryAddTranslation", queryAddTranslation);
		editorConfiguration.saveKeyValuePair("querySourceMatch", querySourceMatch);
		editorConfiguration.saveKeyValuePair("queryTargetMatch", queryTargetMatch);

		editorConfiguration.saveKeyValuePair("searchIfApproved", this.bSearchIfApproved);

		editorConfiguration.saveKeyValuePair("tmDataSources", tmDataSources);
		editorConfiguration.saveKeyValuePair("ptDataSources", ptDataSources);

		editorConfiguration.saveKeyValuePair("globalDictionaryOnTop", globalDictionaryOnTop);
		editorConfiguration.saveKeyValuePair("segmentDictionaryOnTop", segmentDictionaryOnTop);

		if (defaultFont != null)
		{
			int height = this.defaultFont.getFontData()[0].getHeight();
			int style = this.defaultFont.getFontData()[0].getStyle();
			String name = this.defaultFont.getFontData()[0].getName();

			editorConfiguration.saveKeyValuePair("defaultFontName", name);
			editorConfiguration.saveKeyValuePair("defaultFontHeight", height);
			editorConfiguration.saveKeyValuePair("defaultFontStyle", style);
		}

		editorConfiguration.saveKeyValuePair("guiLanguage", guiLanguage);
	}

	/**
	 * @param bSearchIfApproved
	 *            the bSearchIfApproved to set
	 */
	public void setbSearchIfApproved(boolean bSearchIfApproved)
	{
		this.bSearchIfApproved = bSearchIfApproved;
	}

	/**
	 * @param defaultFont
	 *            the defaultFont to set
	 */
	public void setDefaultFont(Font defaultFont)
	{
		this.defaultFont = defaultFont;
	}

	/**
	 * @param defaultFontHeight
	 *            the defaultFontHeight to set
	 */
	public void setDefaultFontHeight(int defaultFontHeight)
	{
		this.defaultFontHeight = defaultFontHeight;
	}

	/**
	 * @param defaultFontName
	 *            the defaultFontName to set
	 */
	public void setDefaultFontName(String defaultFontName)
	{
		this.defaultFontName = defaultFontName;
	}

	/**
	 * @param defaultFontStyle
	 *            the defaultFontStyle to set
	 */
	public void setDefaultFontStyle(int defaultFontStyle)
	{
		this.defaultFontStyle = defaultFontStyle;
	}

	/**
	 * setGlobalDictionaryOnTop
	 * 
	 * @param selection
	 */
	public void setGlobalDictionaryOnTop(boolean selection)
	{
		globalDictionaryOnTop = selection;
	}

	/**
	 * @param guiLanguage
	 *            the guiLanguage to set
	 */
	public void setGuiLanguage(String guiLanguage)
	{
		this.guiLanguage = guiLanguage;
	}

	/**
	 * @param ptDataSources
	 *            the ptDataSources to set
	 */
	public void setPtDataSources(String ptDataSources)
	{
		this.ptDataSources = ptDataSources;
	}

	/**
	 * @param queryAddTranslation
	 *            the queryAddTranslation to set
	 */
	public void setQueryAddTranslation(boolean queryAddTranslation)
	{
		this.queryAddTranslation = queryAddTranslation;
	}

	/**
	 * @param queryNewTranslation
	 *            the queryNewTranslation to set
	 */
	public void setQueryNewTranslation(boolean queryNewTranslation)
	{
		this.queryNewTranslation = queryNewTranslation;
	}

	/**
	 * @param querySourceMatch
	 *            the querySourceMatch to set
	 */
	public void setQuerySourceMatch(boolean querySourceMatch)
	{
		this.querySourceMatch = querySourceMatch;
	}

	/**
	 * @param queryTargetMatch
	 *            the queryTargetMatch to set
	 */
	public void setQueryTargetMatch(boolean queryTargetMatch)
	{
		this.queryTargetMatch = queryTargetMatch;
	}

	/**
	 * setSegmentDictionaryOnTop
	 * 
	 * @param selection
	 */
	public void setSegmentDictionaryOnTop(boolean selection)
	{
		segmentDictionaryOnTop = selection;
	}

	/**
	 * @param tmDataSources
	 *            the tmDataSources to set
	 */
	public void setTmDataSources(String tmDataSources)
	{
		this.tmDataSources = tmDataSources;
	}
}
