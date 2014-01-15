package de.folt.models.datamodel;

import java.util.Hashtable;
import java.util.Observer;

import org.jdom.Element;

import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSProperties;

/**
 * This interface implements the a filter to be applied to a trans-unit element
 * @author klemens
 * 
 */
public interface Filter extends Observer
{


	/**
	 * This method applies a filter and/or a sort to a xliff trans-unit element. It is called at the end of the execution from within the translate method and allows to modify a the trans-unit element created from the translate method.
	 * @param transUnit the trans-unit to filter
	 * @param file the corresponding file element
	 * @param xliffDocument the corresponding xliff element
	 * @param sourceLanguage the source language (of the search)
	 * @param targetLanguage the target language (of the search)
	 * @param matchSimilarity the match similarity used
	 * @param translationParameters the translation parameters
	 * @param dataSource the data source for the alt-trans matches
	 * @param instanceOpenTMSProperties the openTMS properties used
	 * @return the (modified) trans-unit element
	 */
	public Element run(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage,
			String targetLanguage, int matchSimilarity, Hashtable<String, Object> translationParameters,
			DataSource dataSource, OpenTMSProperties instanceOpenTMSProperties);

}
