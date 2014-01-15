package de.folt.similarity;

public class MatchSimilarity
{
	public MatchSimilarity(String longestString, int similarity)
	{
		super();
		this.longestString = longestString;
		this.similarity = similarity;
	}

	private String	longestString	= null;

	private int		similarity		= 0;

	public String getLongestString()
	{
		return longestString;
	}

	public int getSimilarity()
	{
		return similarity;
	}

	public void setLongestString(String longestString)
	{
		this.longestString = longestString;
	}

	public void setSimilarity(int similarity)
	{
		this.similarity = similarity;
	}
}
