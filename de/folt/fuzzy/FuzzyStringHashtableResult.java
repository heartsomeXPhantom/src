/*
 * Created on 08.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

/**
 * A search results where the search is based on strings and returns a object T which represents the associated value of the search operation.
 * @author klemens
 *
 */
public class FuzzyStringHashtableResult<T> extends SearchResult<String, T>
{
    FuzzyDataStructureElement<String, T> value = null;

    /**
     * @param value
     */
    public FuzzyStringHashtableResult(FuzzyDataStructureElement<String, T> value)
    {
        super();
        this.value = value;
    }
}
