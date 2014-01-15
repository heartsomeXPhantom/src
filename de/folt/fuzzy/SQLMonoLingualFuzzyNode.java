/*
 * Created on 24.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

import java.util.Vector;

import de.folt.models.datamodel.sql.SQLMonoLingualObject;

/**
 * This class implements a StringFuzzyNode and uses a SQLMonoLingualObject
 * {@see de.folt.models.datamodel.sql.SQLMonoLingualObject} as the reference
 * value. The fuzzy key is computed from the plain text segment of the
 * SQLMonoLingualObject.
 * 
 * @author klemens
 * 
 * 
 */
public class SQLMonoLingualFuzzyNode extends StringFuzzyNode<SQLMonoLingualObject>
{

	/**
     * 
     */
	private static final long serialVersionUID = -7554227310640186759L;

	/**
	 * Create a new MonoLingualFuzzyNode based on the MonoLingualObject mono.
	 * Call MonoLingualFuzzyNode(MonoLingualObject mono, int iKeyLen) with
	 * iKeyLen = FuzzyNodeKey.getFuzzyKeyLength(). See {@see
	 * de.folt.fuzzy.MonoLingualFuzzyNode#MonoLingualFuzzyNode(MonoLingualObject
	 * , int)}
	 * 
	 * @param mono
	 *            the MonolingualObject to use.
	 */
	public SQLMonoLingualFuzzyNode(SQLMonoLingualObject mono)
	{
		this(mono, FuzzyNodeKey.getDefaultFuzzyKeyLength());
	}

	/**
	 * This constructs a FuzzyNode for a MonoLingualObject. It uses the plain
	 * text of the MonoLingualobject (@see
	 * {@link de.folt.models.datamodel.MonoLingualObject#getPlainTextSegment()}
	 * for the key construction.
	 * 
	 * @param mono
	 *            the MonolingualObject to use.
	 * @param iKeyLen
	 *            the key length to use for generating the FuzzyNodKey
	 */
	public SQLMonoLingualFuzzyNode(SQLMonoLingualObject mono, int iKeyLen)
	{
		if (values == null)
			values = new Vector<SQLMonoLingualObject>();
		values.add(mono);
		if ((mono != null) && (mono.getPlainTextSegment() != null))
		{
			fuzzyNodeKey = new FuzzyNodeKey(mono.getPlainTextSegment(), nGram, iKeyLen);
		}
		leftSon = null;
		rightSon = null;
		status = FUZZYNODESTATUS.NEW;
		LEVEL = 0;
		maxID = 0;
		nodeID = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.fuzzy.StringFuzzyNode#remove(java.lang.Object)
	 */
	@Override
	public void remove(Object value)
	{
		this.removeValue(value);
		if (this.values.size() == 0)
		{
			this.status = FuzzyNode.FUZZYNODESTATUS.DELETED;
		}
		if (this.leftSon != null)
		{
			this.leftSon.remove(value);
		}
		if (this.rightSon != null)
		{
			this.rightSon.remove(value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.fuzzy.StringFuzzyNode#removeValue(java.lang.Object)
	 */
	@Override
	public boolean removeValue(Object value)
	{
		// must remove based on stUniqueId
		if (this.values == null)
			return false;
		SQLMonoLingualObject mono = (SQLMonoLingualObject) value;
		String stUniqueId = mono.getStUniqueID();
		boolean bRemoved = false;
		for (int i = 0; i < this.values.size(); i++)
		{
			String foundStUniqueID = this.values.get(i).getStUniqueID();
			if (stUniqueId.equals(foundStUniqueID))
			{
				this.values.remove(i);
				bRemoved = true;
				break;
			}

		}

		if (bRemoved)
		{
			this.setChanged();
			this.notifyObservers(this);
		}

		return bRemoved;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.fuzzy.FuzzyNode#search(de.folt.fuzzy.FuzzyNode, int)
	 */
	@Override
	public Vector<FuzzyNodeSearchResult<String, SQLMonoLingualObject>> search(FuzzyNode<String, SQLMonoLingualObject> fuzzyCompareKey, int similarity)
	{
		Vector<FuzzyNodeSearchResult<String, SQLMonoLingualObject>> resVec = super.search(fuzzyCompareKey, similarity);
		String monosearch = fuzzyCompareKey.getValues().get(0).getPlainTextSegment();
		for (int i = 0; i < resVec.size(); i++)
		{
			FuzzyNodeSearchResult<String, SQLMonoLingualObject> fuzzyRes = resVec.get(i);
			SQLMonoLingualFuzzyNode monoFuzzy = (SQLMonoLingualFuzzyNode) fuzzyRes.getFuzzyNode();
			Vector<SQLMonoLingualObject> monos = monoFuzzy.getValues();
			float[] levenDistance = new float[monos.size()];
			for (int j = 0; j < monos.size(); j++)
			{
				String plaintext = monos.get(j).getPlainTextSegment();
				levenDistance[j] = de.folt.similarity.LevenshteinSimilarity.levenshteinSimilarity(monosearch, plaintext, similarity);
			}
			fuzzyRes.setLevenDistance(levenDistance);
		}

		return resVec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.fuzzy.StringFuzzyNode#search(java.lang.Object)
	 */
	@Override
	public Vector<FuzzyNode<String, SQLMonoLingualObject>> search(Object value)
	{
		Vector<FuzzyNode<String, SQLMonoLingualObject>> matches = new Vector<FuzzyNode<String, SQLMonoLingualObject>>();
		Vector<SQLMonoLingualObject> values = this.values;
		for (int i = 0; i < values.size(); i++)
		{
			if (values.get(i).getStUniqueID().equals(((SQLMonoLingualObject) value).getStUniqueID()))
				matches.add(this);
		}
		if (this.leftSon != null)
		{
			matches.addAll(this.leftSon.search(value));
		}
		if (this.rightSon != null)
		{
			matches.addAll(this.rightSon.search(value));
		}

		return matches;
	}

	/**
	 * search search for a StringFuzzyNode
	 * 
	 * @param fuzzyCompareKey
	 *            the string fuzzy key to compare with
	 * @param similarity
	 *            the similarity in % (0..100)
	 * @return
	 */
	public Vector<FuzzyNodeSearchResult<String, SQLMonoLingualObject>> search(StringFuzzyNode<SQLMonoLingualObject> fuzzyCompareKey, int similarity)
	{
		Vector<FuzzyNodeSearchResult<String, SQLMonoLingualObject>> resVec = super.search(fuzzyCompareKey, similarity);
		String monosearch = fuzzyCompareKey.getValues().get(0).getPlainTextSegment();
		for (int i = 0; i < resVec.size(); i++)
		{
			FuzzyNodeSearchResult<String, SQLMonoLingualObject> fuzzyRes = resVec.get(i);
			SQLMonoLingualFuzzyNode monoFuzzy = (SQLMonoLingualFuzzyNode) fuzzyRes.getFuzzyNode();
			Vector<SQLMonoLingualObject> monos = monoFuzzy.getValues();
			float[] levenDistance = new float[monos.size()];
			for (int j = 0; j < monos.size(); j++)
			{
				String plaintext = monos.get(j).getPlainTextSegment();
				levenDistance[j] = de.folt.similarity.LevenshteinSimilarity.getLevenshteinDistance(monosearch, plaintext, similarity);
			}
			fuzzyRes.setLevenDistance(levenDistance);
		}

		return resVec;
	}
}
