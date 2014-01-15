package de.folt.models.documentmodel.xliff;

public class XliffMTQualityObject
{

	private boolean	bSource	= true;

	private boolean	bMT		= false;

	private String	text	= "";

	public XliffMTQualityObject(String text, boolean bTM, boolean bSource)
	{
		super();
		this.text = text;
		this.bMT = bTM;
		this.bSource = bSource;
	}

	public String getText()
	{
		return text;
	}

	public boolean isbSource()
	{
		return bSource;
	}

	public boolean isbMT()
	{
		return bMT;
	}

	public void setbSource(boolean bSource)
	{
		this.bSource = bSource;
	}

	public void setbMT(boolean bMT)
	{
		this.bMT = bMT;
	}

	public void setText(String text)
	{
		this.text = text;
	}
	
}
