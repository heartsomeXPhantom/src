/*
 * Created on 06.10.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import org.jdom.Element;

/**
 * @author  klemens To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TransUnitInformationData
{

    /**
     * true if the segment is approved
     */
    private boolean bApproved = false;

    /**
     * true if the segment can be edited
     */
    private boolean bEditable = false;

    /**
     * true if this is a style used for indicating indication
     */
    private boolean bInKnowStyle = false;

    /**
     * true if the cared offset is within the source text, otherwise false
     */
    private boolean bInSourceText = false;

    /**
     * true if the current caret position is a within a tag
     */
    private boolean bInTag = false;

    /**
     * the source segment text with the <s...> part; with final \n
     */
    private String fullSourceText;

    /**
     * the target segment text with the <t...> part; with final \n
     */
    private String fullTargetText;

    /**
     * 
     */
    private int iCurrentLineTextPosition;

    private int iPosition;

    /**
     * the segment number (starting from 0)
     */
    private int iSegmentNumber;

    /**
     * the staring position of the <s indicator
     */
    private int iSStartPosition;

    /**
     * the start position of the <t indicator
     */
    private int iTStartPosition;

    /**
     * 
     */
    private int segmentLengthInformation = -1;

    /**
     * 
     */
    private String sizeunit = "char";

    /**
     * the source segment text without <s...> part  the final \n removed
     */
    private String sourceText;

    /**
     * 
     */
    private String stateInformation = "";

    /**
     * the target segment text without the <t...> part; final \n removed
     */
    private String targetText;
    
    /**
     * the corresponding trans-unit element
     */
    private Element transUnit = null;
    
    /**
     * Segment is translate no / yes
     */
    private boolean bTranslate = true;
    
    /**
     * the id of the transunit
     */
    private String id = "";

    /**
     * 
     */
    public TransUnitInformationData()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    public TransUnitInformationData(int segmentNumber, int startSPosition, int startTPosition, int currentLineTextPosition, String sourceText, String targetText)
    {
        iSegmentNumber = segmentNumber;
        iSStartPosition = startSPosition;
        iTStartPosition = startTPosition;
        iCurrentLineTextPosition = currentLineTextPosition;
        this.sourceText = sourceText;
        this.targetText = targetText;
    }

    /**
     * @return the fullSourceText
     */
    public String getFullSourceText()
    {
        return fullSourceText;
    }

    /**
     * @return the fullTargetText
     */
    public String getFullTargetText()
    {
        return fullTargetText;
    }

    /**
     * @return the iCurrentLineTextPosition
     */
    public int getICurrentLineTextPosition()
    {
        return iCurrentLineTextPosition;
    }

    /**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

    /**
     * @return the iPosition
     */
    public int getIPosition()
    {
        return iPosition;
    }

    /**
     * @return the iSegmentNumber
     */
    public int getISegmentNumber()
    {
        return iSegmentNumber;
    }

    /**
     * @return the iSStartPosition
     */
    public int getISStartPosition()
    {
        return iSStartPosition;
    }

    /**
     * @return the iTStartPosition
     */
    public int getITStartPosition()
    {
        return iTStartPosition;
    }

    /**
     * @return the segmentLengthInformation
     */
    public int getSegmentLengthInformation()
    {
        return segmentLengthInformation;
    }

    /**
     * @return the sizeunit
     */
    public String getSizeunit()
    {
        return sizeunit;
    }

    /**
     * @return the sourceText
     */
    public String getSourceText()
    {
        return sourceText;
    }

    /**
     * @return the stateInformation
     */
    public String getStateInformation()
    {
        return stateInformation;
    }

    /**
     * @return the targetText
     */
    public String getTargetText()
    {
        return targetText;
    }

    /**
	 * @return the transUnit
	 */
	public Element getTransUnit()
	{
		return transUnit;
	}

    /**
     * @return the bApproved
     */
    public boolean isBApproved()
    {
        return bApproved;
    }

    /**
     * @return the bEditable
     */
    public boolean isBEditable()
    {
        return bEditable;
    }

    /**
     * @return the bInKnowStyle
     */
    public boolean isBInKnowStyle()
    {
        return bInKnowStyle;
    }

    /**
     * @return the bInSourceText
     */
    public boolean isBInSourceText()
    {
        return bInSourceText;
    }

    /**
     * @return the bInTag
     */
    public boolean isBInTag()
    {
        return bInTag;
    }

    /**
	 * @return the bTranslate
	 */
	public boolean isbTranslate()
	{
		return bTranslate;
	}

    /**
     * @param approved the bApproved to set
     */
    public void setBApproved(boolean approved)
    {
        bApproved = approved;
    }

    /**
     * @param editable the bEditable to set
     */
    public void setBEditable(boolean editable)
    {
        bEditable = editable;
    }

    /**
     * @param bInKnowStyle the bInKnowStyle to set
     */
    public void setBInKnowStyle(boolean bInKnowStyle)
    {
        this.bInKnowStyle = bInKnowStyle;
    }

    /**
     * @param inSourceText the bInSourceText to set
     */
    public void setBInSourceText(boolean inSourceText)
    {
        bInSourceText = inSourceText;
    }

    /**
     * @param bInTag the bInTag to set
     */
    public void setBInTag(boolean bInTag)
    {
        this.bInTag = bInTag;
    }

    /**
	 * @param bTranslate the bTranslate to set
	 */
	public void setbTranslate(boolean bTranslate)
	{
		this.bTranslate = bTranslate;
	}

    /**
     * @param fullSourceText the fullSourceText to set
     */
    public void setFullSourceText(String fullSourceText)
    {
        this.fullSourceText = fullSourceText;
    }

    /**
     * @param fullTargetText the fullTargetText to set
     */
    public void setFullTargetText(String fullTargetText)
    {
        this.fullTargetText = fullTargetText;
    }

    /**
     * @param currentLineTextPosition the iCurrentLineTextPosition to set
     */
    public void setICurrentLineTextPosition(int currentLineTextPosition)
    {
        iCurrentLineTextPosition = currentLineTextPosition;
    }

    /**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

    /**
     * @param position the iPosition to set
     */
    public void setIPosition(int position)
    {
        iPosition = position;
    }

    /**
     * @param segmentNumber the iSegmentNumber to set
     */
    public void setISegmentNumber(int segmentNumber)
    {
        iSegmentNumber = segmentNumber;
    }

    /**
     * 
     * @param startPosition the iSStartPosition to set
     */
    public void setISStartPosition(int startPosition)
    {
        iSStartPosition = startPosition;
    }

    /**
     * @param startPosition the iTStartPosition to set
     */
    public void setITStartPosition(int startPosition)
    {
        iTStartPosition = startPosition;
    }

    /**
     * @param segmentLengthInformation the segmentLengthInformation to set
     */
    public void setSegmentLengthInformation(int segmentLengthInformation)
    {
        this.segmentLengthInformation = segmentLengthInformation;
    }

	/**
     * @param sizeunit the sizeunit to set
     */
    public void setSizeunit(String sizeunit)
    {
        this.sizeunit = sizeunit;
    }

	/**
     * @param sourceText the sourceText to set
     */
    public void setSourceText(String sourceText)
    {
        this.sourceText = sourceText;
    }

	/**
     * @param stateInformation the stateInformation to set
     */
    public void setStateInformation(String stateInformation)
    {
        this.stateInformation = stateInformation;
    }

	/**
     * @param targetText the targetText to set
     */
    public void setTargetText(String targetText)
    {
        this.targetText = targetText;
    }

	/**
	 * @param transUnit the transUnit to set
	 */
	public void setTransUnit(Element transUnit)
	{
		this.transUnit = transUnit;
	}

	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return iPosition + ":" + iSegmentNumber + ":" + iCurrentLineTextPosition + ":" + this.bInSourceText + ":" + this.bApproved + ":" + this.bInKnowStyle + ":" + this.bEditable + "\n"
                + iSStartPosition + ": \"" + sourceText + "\"\n\"" + this.fullSourceText + "\"\n" + iTStartPosition + ": \"" + targetText + "\"\n" + this.fullTargetText + "\"";
    }
}