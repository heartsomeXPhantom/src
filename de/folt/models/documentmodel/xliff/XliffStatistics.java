package de.folt.models.documentmodel.xliff;

public class XliffStatistics
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	private String[] boundaries = { "0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100" };

	private int[] percentFields = null;

	private long[] statFields = null;

	private long[] wordFieldsHuman = null;

	private long[] wordFieldsMT = null;

	private long wordSumHuman = 0;
	private long wordSumMT = 0;

	public XliffStatistics(String[] boundaries)
	{
		super();
		if (boundaries != null)
		{
			this.setBoundaries(boundaries);
		}

		statFields = new long[this.boundaries.length];
		for (int i = 0; i < statFields.length; i++)
		{
			statFields[i] = 0;
		}

		wordFieldsHuman = new long[this.boundaries.length];
		for (int i = 0; i < wordFieldsHuman.length; i++)
		{
			wordFieldsHuman[i] = 0;
		}

		wordFieldsMT = new long[this.boundaries.length];
		for (int i = 0; i < wordFieldsMT.length; i++)
		{
			wordFieldsMT[i] = 0;
		}

		percentFields = new int[statFields.length];
		for (int i = 0; i < percentFields.length; i++)
		{
			try
			{
				percentFields[i] = Integer.parseInt(this.boundaries[i]);
			}
			catch (NumberFormatException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		java.util.Arrays.sort(percentFields);
	}

	/**
	 * @param percentage
	 */
	public void addPercentage(int percentage)
	{
		for (int i = statFields.length - 1; i >= 0; i--)
		{
			if (percentage >= percentFields[i])
			{
				statFields[i] = statFields[i] + 1;
				break;
			}
		}
	}

	/**
	 * @param percentage
	 */
	public void addPercentage(int percentage, int mtWords, int humanWords)
	{
		for (int i = statFields.length - 1; i >= 0; i--)
		{
			if (percentage >= percentFields[i])
			{
				statFields[i] = statFields[i] + 1;
				wordFieldsHuman[i] = wordFieldsHuman[i] + humanWords;
				wordFieldsMT[i] = wordFieldsMT[i] + mtWords;
				this.wordSumHuman = this.wordSumHuman + humanWords;
				this.wordSumMT = this.wordSumMT + mtWords;
				break;
			}
		}
	}

	public String[] getBoundaries()
	{
		return boundaries;
	}

	public int[] getPercentFields()
	{
		return percentFields;
	}

	public long[] getStatFields()
	{
		return statFields;
	}

	public long[] getWordFieldsHuman()
	{
		return wordFieldsHuman;
	}

	public long[] getWordFieldsMT()
	{
		return wordFieldsMT;
	}

	public long getWordSumHuman()
	{
		return wordSumHuman;
	}

	public long getWordSumMT()
	{
		return wordSumMT;
	}

	public void setBoundaries(String[] boundaries2)
	{
		this.boundaries = boundaries2;
	}

	public void setPercentFields(int[] percentFields)
	{
		this.percentFields = percentFields;
	}

	public void setStatFields(long[] statFields)
	{
		this.statFields = statFields;
	}

	public void setWordFieldsHuman(long[] wordFieldsHuman)
	{
		this.wordFieldsHuman = wordFieldsHuman;
	}

	public void setWordFieldsMT(long[] wordFieldsMT)
	{
		this.wordFieldsMT = wordFieldsMT;
	}

	public void setWordSumHuman(long wordSumHuman)
	{
		this.wordSumHuman = wordSumHuman;
	}

	public void setWordSumMT(long wordSumMT)
	{
		this.wordSumMT = wordSumMT;
	}

}
