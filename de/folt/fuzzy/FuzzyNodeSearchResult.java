/*
 * Created on 23.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

/**
 * This class represents the result of a fuzzy node search. It contains the reference to the fuzzy node which fulfilled the search criteria. 
 * distance is difference between the node found and the node searched. 
 * kleyDistance is the value computed based on the search similarity in % supplied; it represents the absolute key value of the similarity. If a string was search levenDistance contains the Levenshtein distance. This is actually an array of float numbers for each of the values of the fuzzy node. In addition it contains
 * some statistical information: NODESMATCHED is the number of matching nodes, NOEDESSEACHED the number of nodes searched in the tree so far and NODESPUSHED the number of fuzzy nodes pushed onto the
 * stack so far. SearchSimilarity is the similarity value which was used for the fuzzy search algorithm.
 * 
 * @author klemens
 * 
 */
public class FuzzyNodeSearchResult<K, T> extends SearchResult<K, T>
{
    private float distance = 0;

    private FuzzyNode<K, T> fuzzyNode;

    int fuzzyNodedifference = -1; // currentFuzzyNode.computeKeyDistance(fuzzyCompareKey);

    float keyDistance = -1; // dist = (int) ((minkeysum * percentDiff) / 100) + 2; percentDiff = 100 - similarity

    private float[] levenDistance = null; // this is a special distances value for the individual objects associated with the fuzzy node ... (Levenshtein...)

    int NODESMATCHED = 0;

    int NODESPUSHED = 0;

    int NODESSEARCHED = 0;

    int searchSimilarity = -1; // the original similarity search for

    /**
     * Create a FuzzyNodeSearchResult for a given distance (fuzzy node key distance) and the matching fuzzy node
     * 
     * @param distance
     *            the distance for the fuzzy node keys
     * @param fuzzyNode
     *            the fuzzy node matched
     */
    public FuzzyNodeSearchResult(float distance, FuzzyNode<K, T> fuzzyNode)
    {
        super();
        this.distance = distance;
        this.fuzzyNode = fuzzyNode;
    }

    /**
     * Create a FuzzyNodeSearchResult for a given distance (fuzzy node key distance) and the matching fuzzy node
     * 
     * @param distance
     *            the distance of the match
     * @param fuzzyNode
     *            the matching fuzzy node
     * @param nodessearched
     *            statistics: nodes searched
     * @param nodesmatched
     *            statistics: nodes matched
     * @param nodespushed
     *            statistics: nodes pushed
     */
    public FuzzyNodeSearchResult(float distance, FuzzyNode<K, T> fuzzyNode, int nodessearched, int nodesmatched, int nodespushed)
    {
        super();
        this.distance = distance;
        this.fuzzyNode = fuzzyNode;
        NODESSEARCHED = nodessearched;
        NODESMATCHED = nodesmatched;
        NODESPUSHED = nodespushed;
    }

    /**
     * Create a FuzzyNodeSearchResult for a given distance (fuzzy node key distance) and the matching fuzzy node
     * 
     * @param distance
     *            the distance for the fuzzy node keys
     * @param similarity
     *            the similarity for the fuzzy node keys
     * @param dist
     *            the distance used for the keys searched
     * @param difference
     *            the fuzzy node difference
     * @param fuzzyNode
     *            the fuzzy node matched
     * @param nodessearched
     *            the number of nodes searched
     * @param nodesmatched
     *            the number of nodes matches
     * @param nodespushed
     *            the number of nodes pushed
     */
    public FuzzyNodeSearchResult(float distance, int similarity, float dist, int difference, FuzzyNode<K, T> fuzzyNode, int nodessearched, int nodesmatched, int nodespushed)
    {
        super();
        this.distance = distance;
        this.fuzzyNode = fuzzyNode;
        NODESSEARCHED = nodessearched;
        NODESMATCHED = nodesmatched;
        NODESPUSHED = nodespushed;

        searchSimilarity = similarity;
        keyDistance = dist;
        fuzzyNodedifference = difference;

    }

    /**
     * format formats a FuzzyNodeResult
     * 
     * @return the formatted FuzzyNodeResult
     */
    public String format()
    {
        String str = "";
        String levendist = "[";
        if (levenDistance != null)
        {
            for (int i = 0; i < levenDistance.length; i++)
            {
                levendist = levendist + levenDistance[i];
                if (i != (levenDistance.length - 1))
                    levendist = levendist + ",";
            }
        }
        levendist += "]";

        str = "FUZZYNODE: " + fuzzyNode + " / distance=" + distance + " searchSimilarity=" + searchSimilarity + " keyDistance=" + keyDistance + " fuzzyNodedifference=" + fuzzyNodedifference
                + " levenDistance=" + levendist + " NODESSEARCHED=" + NODESSEARCHED + " NODESMATCHED=" + NODESMATCHED + " NODESPUSHED=" + NODESPUSHED;
        return str;
    }

    /**
     * @return the distance
     */
    public float getDistance()
    {
        return distance;
    }

    /**
     * @return the fuzzyNode
     */
    public FuzzyNode<K, T> getFuzzyNode()
    {
        return fuzzyNode;
    }

    /**
     * @return the fuzzyNodedifference
     */
    public int getFuzzyNodedifference()
    {
        return fuzzyNodedifference;
    }

    /**
     * @return the keyDistance
     */
    public float getKeyDistance()
    {
        return keyDistance;
    }

    /**
     * return the Levenshtein distances computed. In most cases this will a % value measuring the similarity with a given search object (100% = perfect match)
     * 
     * @return the levenDistance
     */
    public float[] getLevenDistance()
    {
        return levenDistance;
    }

    /**
     * @return the nODESMATCHED
     */
    public int getNODESMATCHED()
    {
        return NODESMATCHED;
    }

    /**
     * @return the nODESPUSHED
     */
    public int getNODESPUSHED()
    {
        return NODESPUSHED;
    }

    /**
     * @return the nODESSEARCHED
     */
    public int getNODESSEARCHED()
    {
        return NODESSEARCHED;
    }

    /**
     * @return the searchSimilarity
     */
    public int getSearchSimilarity()
    {
        return searchSimilarity;
    }

    /**
     * @param distance
     *            the distance to set
     */
    public void setDistance(float distance)
    {
        this.distance = distance;
    }

    /**
     * @param fuzzyNode
     *            the fuzzyNode to set
     */
    public void setFuzzyNode(FuzzyNode<K, T> fuzzyNode)
    {
        this.fuzzyNode = fuzzyNode;
    }

    /**
     * @param fuzzyNodedifference
     *            the fuzzyNodedifference to set
     */
    public void setFuzzyNodedifference(int fuzzyNodedifference)
    {
        this.fuzzyNodedifference = fuzzyNodedifference;
    }

    /**
     * @param keyDistance
     *            the keyDistance to set
     */
    public void setKeyDistance(float keyDistance)
    {
        this.keyDistance = keyDistance;
    }

    /**
     * Sets the levenDistance of the result for each of the values of the result node.
     * 
     * @param levenDistance
     *            the levenDistance to set
     */
    public void setLevenDistance(float[] levenDistance)
    {
        this.levenDistance = levenDistance;
    }

    /**
     * @param nodesmatched
     *            the nODESMATCHED to set
     */
    public void setNODESMATCHED(int nodesmatched)
    {
        NODESMATCHED = nodesmatched;
    }

    /**
     * @param nodespushed
     *            the nODESPUSHED to set
     */
    public void setNODESPUSHED(int nodespushed)
    {
        NODESPUSHED = nodespushed;
    }

    /**
     * @param nodessearched
     *            the nODESSEARCHED to set
     */
    public void setNODESSEARCHED(int nodessearched)
    {
        NODESSEARCHED = nodessearched;
    }

    /**
     * @param searchSimilarity
     *            the searchSimilarity to set
     */
    public void setSearchSimilarity(int searchSimilarity)
    {
        this.searchSimilarity = searchSimilarity;
    }

}
