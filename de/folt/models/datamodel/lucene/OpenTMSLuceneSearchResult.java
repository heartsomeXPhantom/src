/*
 * Created on 02.04.2010
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OpenTMSLuceneSearchResult
{
    Document document;
    ScoreDoc scoreDocument;
    /**
     * @param document
     * @param scoreDocument
     */
    public OpenTMSLuceneSearchResult(Document document, ScoreDoc scoreDocument)
    {
        super();
        this.document = document;
        this.scoreDocument = scoreDocument;
    }
    /**
     * @param scoreDocument
     */
    public OpenTMSLuceneSearchResult(ScoreDoc scoreDocument)
    {
        super();
        this.scoreDocument = scoreDocument;
    }
    /**
     * @return the document
     */
    public Document getDocument()
    {
        return document;
    }
    /**
     * @return the scoreDocument
     */
    public ScoreDoc getScoreDocument()
    {
        return scoreDocument;
    }
    /**
     * @param document the document to set
     */
    public void setDocument(Document document)
    {
        this.document = document;
    }
    
    /**
     * @param scoreDocument the scoreDocument to set
     */
    public void setScoreDocument(ScoreDoc scoreDocument)
    {
        this.scoreDocument = scoreDocument;
    }
}
