/*
 * Created on 13.07.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.phrasetranslate;

/**
 * @author klemens To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PhraseTranslateResult implements Comparable<PhraseTranslateResult>
{
	private static char sepChar = '!';

	private static String sourceTargetSepString = "::";

	private int iStartPosition = 0;

	/**
     * 
     */
	private Long[] longPhrase;

	/**
     * 
     */
	private String sourcePhrase;

	/**
     * 
     */
	private String sourceTargetUniqueId;

	/**
     * 
     */
	private String targetPhrase;

	/**
     * 
     */
	public PhraseTranslateResult()
	{
	}

	/**
	 * @param startPosition
	 *            the start position of this match in the corresponding search
	 *            phrase
	 * @param longPhrase
	 *            the long Arraya version of the source pharse matching
	 * @param targetPhrase
	 *            the translation of the match
	 */
	public PhraseTranslateResult(int startPosition, Long[] longPhrase, String targetPhrase)
	{
		super();
		iStartPosition = startPosition;
		this.longPhrase = longPhrase;
		this.targetPhrase = targetPhrase;
	}

	/**
	 * @param iStartPositioni
	 * @param longsearch
	 * @param targetPhrase
	 */
	public PhraseTranslateResult(int iStartPositioni, String longsearch, String targetPhrase)
	{
		longsearch = longsearch.replaceFirst(sepChar + "", "");
		String[] longs = longsearch.split(sepChar + "");
		Long[] longpt = new Long[longs.length];
		for (int k = 0; k < longs.length; k++)
		{
			longpt[k] = Long.parseLong(longs[k]);
		}
		this.longPhrase = longpt;
		this.iStartPosition = iStartPositioni;
		this.targetPhrase = targetPhrase;
	}

	/**
	 * @param longPhrase
	 *            the long Array version of the source pharse matching
	 * @param targetPhrase
	 *            the translation of the match
	 */
	/**
	 * @param longPhrase
	 * @param targetPhrase
	 */
	public PhraseTranslateResult(Long[] longPhrase, String targetPhrase)
	{
		super();

		this.longPhrase = longPhrase;
		this.targetPhrase = targetPhrase;
	}

	/**
	 * @param longPhrase
	 *            the long Array version of the source pharse matching
	 * @param sourcePhrase
	 *            the source string of the match
	 * @param targetPhrase
	 *            the translation of the match
	 */
	public PhraseTranslateResult(Long[] longPhrase, String sourcePhrase, String targetPhrase)
	{
		super();
		this.longPhrase = longPhrase;
		this.sourcePhrase = sourcePhrase;
		this.targetPhrase = targetPhrase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PhraseTranslateResult o)
	{
		// 1 if o smaller than this, 0 if equal, -1 if o greater than this
		// the longest matches should come first
		if (this.longPhrase.length >= o.longPhrase.length)
			return -1;
		else if (this.longPhrase.length == o.longPhrase.length)
			return 0;
		return 1;
	}

	public int getiStartPosition()
	{
		return iStartPosition;
	}

	/**
	 * @return the iStartPosition
	 */
	public int getIStartPosition()
	{
		return iStartPosition;
	}

	/**
	 * @return the longPhrase
	 */
	public Long[] getLongPhrase()
	{
		return longPhrase;
	}

	/**
	 * @return the sepChar
	 */
	public char getSepChar()
	{
		return sepChar;
	}

	/**
	 * @return the sourcePhrase
	 */
	public String getSourcePhrase()
	{
		return sourcePhrase;
	}

	public String getSourceTargetSepString()
	{
		return sourceTargetSepString;
	}

	public String getSourceTargetUniqueId()
	{
		return sourceTargetUniqueId;
	}

	/**
	 * @return the targetPhrase
	 */
	public String getTargetPhrase()
	{
		return targetPhrase;
	}

	public void setSourceTargetUniqueId(String sourceUniqueId)
	{
		this.sourceTargetUniqueId = sourceUniqueId;
	}

	public void setiStartPosition(int iStartPosition)
	{
		this.iStartPosition = iStartPosition;
	}

	/**
	 * @param iStartPosition
	 *            the iStartPosition to set
	 */
	public void setIStartPosition(int iStartPosition)
	{
		this.iStartPosition = iStartPosition;
	}

	/**
	 * @param longPhrase
	 *            the longPhrase to set
	 */
	public void setLongPhrase(Long[] longPhrase)
	{
		this.longPhrase = longPhrase;
	}

	/**
	 * @param sepChar
	 *            the sepChar to set
	 */
	public void setSepChar(char sepChar)
	{
		PhraseTranslateResult.sepChar = sepChar;
	}

	/**
	 * @param sourcePhrase
	 *            the sourcePhrase to set
	 */
	public void setSourcePhrase(String sourcePhrase)
	{
		this.sourcePhrase = sourcePhrase;
	}

	public void setSourceTargetSepString(String sourceTargetSepString)
	{
		PhraseTranslateResult.sourceTargetSepString = sourceTargetSepString;
	}

	/**
	 * @param targetPhrase
	 *            the targetPhrase to set
	 */
	public void setTargetPhrase(String targetPhrase)
	{
		this.targetPhrase = targetPhrase;
	}

	/**
	 * @return
	 */
	public String stringify()
	{
		String ret = "\"" + this.sourcePhrase + "\": " + this.targetPhrase + "\"";
		if (this.getSourceTargetUniqueId() != null)
		{
			String[] st = this.getSourceTargetUniqueId().split("::");
			if (st.length > 0)
				ret = ret + " Source ID: " + st[0];
			if (st.length > 1)
				ret = ret + " Target ID: " + st[1];
		}
		return ret;
	}
}