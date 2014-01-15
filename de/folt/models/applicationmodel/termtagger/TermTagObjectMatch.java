package de.folt.models.applicationmodel.termtagger;

import java.util.Vector;

import de.folt.util.WordHandling;

public class TermTagObjectMatch extends TermTagObjectAbstract implements Comparable<TermTagObjectMatch>
{

	public enum MATCHTYPE
	{
		EXACT, FUZZY, LOWERCASE, NONE, STEMMED
	}

	private static boolean				bCompareMethod		= true;

	private int							fuzzy				= 100;

	private int							iWeight				= 0;

	private MATCHTYPE					matchType			= MATCHTYPE.NONE;

	private int							numberOfMatches		= 0;

	private Vector<TermInternalMatch>	termInternalMatch	= new Vector<TermInternalMatch>();

	private String						termMatch			= null;

	public TermTagObjectMatch(String language, String term)
	{
		super(language, term);
		numberOfMatches = 0;
		// TODO Auto-generated constructor stub
	}

	public TermTagObjectMatch(String language, String term, String termID, String uniqueID)
	{
		super(language, term, termID, uniqueID);
		numberOfMatches = 0;
	}

	public TermTagObjectMatch(String term, String language, String termID, String uniqueID, boolean bSource)
	{
		super(term, language, termID, uniqueID, bSource);
		numberOfMatches = 0;
	}

	public TermTagObjectMatch(String term, String language, String termID, String uniqueID, String longRepresentation,
			boolean bSource, WordHandling wordHandling)
	{
		super(term, language, termID, uniqueID, longRepresentation, bSource, wordHandling);
		numberOfMatches = 0;
	}

	public TermTagObjectMatch(TermTagObject termTagObject)
	{
		super();
		this.attributes = termTagObject.attributes;
		this.bSource = termTagObject.bSource;
		this.language = termTagObject.language;
		this.longLCRepresentation = termTagObject.longLCRepresentation;
		this.longRepresentation = termTagObject.longRepresentation;
		this.longStemmedRepresentation = termTagObject.longStemmedRepresentation;
		this.term = termTagObject.term;
		this.termBaseForm = termTagObject.termBaseForm;
		this.termElementID = termTagObject.termElementID;
		this.termID = termTagObject.termID;
		this.termLowercase = termTagObject.termLowercase;
		this.translation = termTagObject.translation;
		this.uniqueID = termTagObject.uniqueID;
		this.numberOfMatches = 0;
		this.encodedXliffString = termTagObject.encodedXliffString;
		this.wordsInEncodedVector = termTagObject.wordsInEncodedVector;

	}

	/**
	 * @param termInternalMatch
	 */
	public void addSorted(TermInternalMatch termInternalMatch)
	{
		for (int i = 0; i < this.getTermInternalMatch().size(); i++)
		{
			if (this.getTermInternalMatch().get(i).getFuzzy() > termInternalMatch.getFuzzy())
			{
				continue; // current match has a higher fuzzy value, so check for the next one
			}
			else if ((this.getTermInternalMatch().get(i).getFuzzy() == termInternalMatch.getFuzzy()))
			{
				// EXACT is always first
				if (this.getTermInternalMatch().get(i).getMatchType() == MATCHTYPE.EXACT)
				{
					continue;
				}
				
				// EXACT before LOWERCASE
				if ((this.getTermInternalMatch().get(i).getMatchType() == MATCHTYPE.LOWERCASE) && (termInternalMatch.getMatchType() == MATCHTYPE.EXACT))
				{
					this.getTermInternalMatch().add(i, termInternalMatch); // add before the current match
					return;
				}
				
				// EXACT before STEMMED
				if ((this.getTermInternalMatch().get(i).getMatchType() == MATCHTYPE.STEMMED) && (termInternalMatch.getMatchType() == MATCHTYPE.EXACT))
				{
					this.getTermInternalMatch().add(i, termInternalMatch); // add before the current match
					return;
				}
				
				// EXACT before STEMMED
				if ((this.getTermInternalMatch().get(i).getMatchType() == MATCHTYPE.FUZZY) && (termInternalMatch.getMatchType() == MATCHTYPE.EXACT))
				{
					this.getTermInternalMatch().add(i, termInternalMatch); // add before the current match
					return;
				}
				
				// LOWERCASE before STEMMED
				if ((this.getTermInternalMatch().get(i).getMatchType() == MATCHTYPE.LOWERCASE) && (termInternalMatch.getMatchType() == MATCHTYPE.STEMMED))
				{
					this.getTermInternalMatch().add(i, termInternalMatch); // add before the current match
					return;
				}
				
				// LOWERCASE before FUZZY
				if ((this.getTermInternalMatch().get(i).getMatchType() == MATCHTYPE.LOWERCASE) && (termInternalMatch.getMatchType() == MATCHTYPE.FUZZY))
				{
					this.getTermInternalMatch().add(i, termInternalMatch); // add before the current match
					return;
				}
				
				// STEMMED before FUZZY
				if ((this.getTermInternalMatch().get(i).getMatchType() == MATCHTYPE.STEMMED) && (termInternalMatch.getMatchType() == MATCHTYPE.FUZZY))
				{
					this.getTermInternalMatch().add(i, termInternalMatch);
					return;
				}
				// add after the current match
				this.getTermInternalMatch().add(i + 1, termInternalMatch);
				return;
			}
			else
			{
				this.getTermInternalMatch().add(i, termInternalMatch);
				return;
			}
		}
		// nothing matched, add at end
		this.getTermInternalMatch().add(termInternalMatch);
	}

	@Override
	public int compareTo(TermTagObjectMatch o)
	{
		TermTagObjectMatch ot = (TermTagObjectMatch) o;
		if (TermTagObjectMatch.bCompareMethod)
		{
			// 1 if o smaller than this, 0 if equal, -1 if o greater than this
			// the longest matches should come first

			// System.out.println(this.term + ":" + this.matchType + " - " + ot.term
			// + ":" + ot.matchType);

			int is = countOccurrences(this.longRepresentation, '!');
			int it = countOccurrences(ot.longRepresentation, '!');
			if (is > it)
				return -1;
			
			if ((ot.matchType == MATCHTYPE.EXACT)
					&& ((this.matchType == MATCHTYPE.FUZZY) || (this.matchType == MATCHTYPE.LOWERCASE) || (this.matchType == MATCHTYPE.STEMMED)))
				return 1;

			else if ((ot.matchType == MATCHTYPE.LOWERCASE)
					&& ((this.matchType == MATCHTYPE.FUZZY) || (this.matchType == MATCHTYPE.STEMMED)))
				return 1;

			else if ((ot.matchType == MATCHTYPE.STEMMED) && (this.matchType == MATCHTYPE.FUZZY))
				return 1;

			if ((this.matchType == MATCHTYPE.FUZZY) && (ot.matchType == MATCHTYPE.FUZZY) && this.term.equals(ot.term))
			{
				if (this.fuzzy > ot.fuzzy)
					return -1;
			}


			if (is == it)
			{
				if (this.term.length() > o.term.length())
					return -1;
				if (this.term.length() < o.term.length())
					return 1;
				return 0;
			}
			
			if (ot == this)
				return 0;
			
			return 1;
		}
		else
		{
			int th = this.matchWeighting();
			int otth = ot.matchWeighting();
			return (th > otth ? -1 : (th == otth ? 0 : 1));
		}
	}

	public int getFuzzy()
	{
		return fuzzy;
	}

	public int getiWeight()
	{
		return iWeight;
	}

	public MATCHTYPE getMatchType()
	{
		return matchType;
	}

	public int getNumberOfMatches()
	{
		return numberOfMatches;
	}

	public Vector<TermInternalMatch> getTermInternalMatch()
	{
		return termInternalMatch;
	}

	public String getTermMatch()
	{
		return termMatch;
	}

	public void incrementNumberOfMatches()
	{
		this.numberOfMatches++;
	}

	public int matchWeighting()
	{
		iWeight = 0;
		int iMatchType = 0;
		if (this.matchType == MATCHTYPE.EXACT)
			iMatchType = 5;
		else if (this.matchType == MATCHTYPE.LOWERCASE)
			iMatchType = 3;
		else if (this.matchType == MATCHTYPE.STEMMED)
			iMatchType = 2;
		else if (this.matchType == MATCHTYPE.FUZZY)
			iMatchType = 1;

		// int ratiocharvswords = this.termMatch.length() / this.term.length();

		// weighting scheme Type 60 %, Word number 20%, string length 20%
		iWeight = iMatchType * 4 / 5 + /* ratiocharvswords / 5 + */(int) (((float) this.fuzzy) / (float) 100) * 5;
		return iWeight;
	}

	public void setFuzzy(int fuzzy)
	{
		this.fuzzy = fuzzy;
	}

	public void setiWeight(int iWeight)
	{
		this.iWeight = iWeight;
	}

	public void setMatchType(MATCHTYPE matchType)
	{
		this.matchType = matchType;
	}

	public void setNumberOfMatches(int numberOfMatches)
	{
		this.numberOfMatches = numberOfMatches;
	}

	public void setTermInternalMatch(Vector<TermInternalMatch> termInternalMatch)
	{
		this.termInternalMatch = termInternalMatch;
	}

	public void setTermMatch(String termMatch)
	{
		this.termMatch = termMatch;
	}

	@Override
	public String stringify()
	{
		// TODO Auto-generated method stub
		String matches = "\t\t";
		for (int i = 0; i < this.getTermInternalMatch().size(); i++)
		{
			matches = matches + "#" + i + ": " + format("termMatch", this.getTermInternalMatch().get(i).getTermMatch()) + format("matchType", this.getTermInternalMatch().get(i).getMatchType() + "")
					+ format("fuzzy", this.getTermInternalMatch().get(i).getFuzzy() + "") + format("Pos in Seg", this.getTermInternalMatch().get(i).getiPosSegment() + "");

			if (i != (this.getTermInternalMatch().size() - 1))
				matches = matches + "\n\t\t";
		}
		return super.stringify() + "\n\t" + format("termMatch", this.termMatch) + format("fuzzy", this.fuzzy + "")
				+ format("matchType", this.matchType + "") + format("matchWeight", this.iWeight + "") + format("numberOfMatches", this.getNumberOfMatches() + "") + "\n" + matches;
	}

	@Override
	public String stringifyTranslation()
	{
		// TODO Auto-generated method stub
		return super.stringifyTranslation();
	}

}
