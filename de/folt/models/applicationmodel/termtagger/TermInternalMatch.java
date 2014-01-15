/**
 * 
 */
package de.folt.models.applicationmodel.termtagger;

import java.util.Vector;

import de.folt.models.applicationmodel.termtagger.TermTagObjectMatch.MATCHTYPE;

/**
 * @author Klemens
 * 
 */
public class TermInternalMatch
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

	private int				fuzzy		= 100;

	private MATCHTYPE		matchType	= MATCHTYPE.NONE;

	private String			termMatch	= null;

	private Vector<Integer>	iPosSegment; // 02.10.2013

	/**
	 * 
	 */
	public TermInternalMatch()
	{
		// TODO Auto-generated constructor stub
		iPosSegment = new Vector<Integer>(); // 02.10.2013
	}
	
	public TermInternalMatch(String termMatch, MATCHTYPE matchType, int fuzzy)
	{
		super();
		iPosSegment = new Vector<Integer>(); // 02.10.2013
		this.termMatch = termMatch;
		this.matchType = matchType;
		this.fuzzy = fuzzy;
	}

	public TermInternalMatch(String termMatch, MATCHTYPE matchType, int fuzzy, int iSegmentPos)
	{
		super();
		iPosSegment = new Vector<Integer>(); // 02.10.2013
		this.termMatch = termMatch;
		this.matchType = matchType;
		this.fuzzy = fuzzy;
		this.iPosSegment.add(iSegmentPos);
	}

	public int getFuzzy()
	{
		return fuzzy;
	}

	public MATCHTYPE getMatchType()
	{
		return matchType;
	}

	public String getTermMatch()
	{
		return termMatch;
	}

	public void setFuzzy(int fuzzy)
	{
		this.fuzzy = fuzzy;
	}

	public void setMatchType(MATCHTYPE matchType)
	{
		this.matchType = matchType;
	}

	public void setTermMatch(String termMatch)
	{
		this.termMatch = termMatch;
	}

	public Vector<Integer> getiPosSegment()
	{
		return iPosSegment;
	}

	public void setiPosSegment(Vector<Integer> iPosSegment)
	{
		this.iPosSegment = iPosSegment;
	}

}
