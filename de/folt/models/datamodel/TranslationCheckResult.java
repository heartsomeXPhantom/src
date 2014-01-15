/*
 * Created on 26.10.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.util.Vector;

/**
 * This class is a data structure which is mainly intended to contain the result of a source/ target segment combination search. A key element is the status.<br>
 * Currently four stati are supported:<br>
 * NEW = the source target combination is not contained in the data source<br>
 * SOURCEFOUND = the source is contained in the data source<br>
 * TARGETFOUND = the target is contained in the data source<br>
 * SOURCEANDTARGETFOUND = the source target combination is contained in the data source<br>
 * The Vector MultiLingualObject matches contains the matching entries from the data source
 * 
 * 
 * @author klemens
 *
 */
public class TranslationCheckResult
{

    /**
     * The status of a translation source target (translation) check.
     * @author klemens
     *
     */
    public enum TranslationCheckStatus {
        /**
         * the source target segment combination does not exist data source
         */
        NEW, 
        /**
         * the source and target segment exists in the data source
         */
        SOURCEANDTARGETFOUND,
        /**
         * the source segment exists in the data source
         */
        SOURCEFOUND,
        /**
         * the target segment exists in the data source
         */
        TARGETFOUND
    }

    Vector<MultiLingualObject> sourceSegmentMatches = new Vector<MultiLingualObject>();
    
    TranslationCheckStatus status = TranslationCheckStatus.NEW;

    Vector<MultiLingualObject> targetSegmentMatches = new Vector<MultiLingualObject>();
    
    Vector<MultiLingualObject> sourceAndTargetSegmentMatches = new Vector<MultiLingualObject>();


    /**
     * @return the sourceAndTargetSegmentMatches
     */
    public Vector<MultiLingualObject> getSourceAndTargetSegmentMatches()
    {
        return sourceAndTargetSegmentMatches;
    }


    /**
     * @param sourceAndTargetSegmentMatches the sourceAndTargetSegmentMatches to set
     */
    public void setSourceAndTargetSegmentMatches(Vector<MultiLingualObject> sourceAndTargetSegmentMatches)
    {
        this.sourceAndTargetSegmentMatches = sourceAndTargetSegmentMatches;
    }


    /**
     * @return the sourceSegmentMatches
     */
    public Vector<MultiLingualObject> getSourceSegmentMatches()
    {
        return sourceSegmentMatches;
    }


    /**
     * @return the status which is one of the following TranslationCheckResult.TranslationCheckStatus.NEW, SOURCEFOUND, TARGETFOUND, SOURCEANDTARGETFOUND
     */
    public TranslationCheckStatus getStatus()
    {
        return status;
    }


    /**
     * @return the targetSegmentMatches
     */
    public Vector<MultiLingualObject> getTargetSegmentMatches()
    {
        return targetSegmentMatches;
    }

    /**
     * @param sourceSegmentMatches the sourceSegmentMatches to set
     */
    public void setSourceSegmentMatches(Vector<MultiLingualObject> sourceSegmentMatches)
    {
        this.sourceSegmentMatches = sourceSegmentMatches;
    }


    /**
     * @param status the status to set which is one of the following .TranslationCheckResultTranslationCheckStatus.NEW, SOURCEFOUND, TARGETFOUND, SOURCEANDTARGETFOUND
     */
    public void setStatus(TranslationCheckStatus status)
    {
        this.status = status;
    }


    /**
     * @param targetSegmentMatches the targetSegmentMatches to set
     */
    public void setTargetSegmentMatches(Vector<MultiLingualObject> targetSegmentMatches)
    {
        this.targetSegmentMatches = targetSegmentMatches;
    }
}
