package net.docliff.segmenter;

import java.util.List;
import java.util.Vector;

/**
 * @author Administrator
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates. To enable and disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
class XMLSegmentNode
{
	public XMLSegmentNode parent; // the father node
	public List sons = new Vector(); // list of son nodes sequentially order by segment number
	public String tag; // the tag name - e.g. h2
	public int level; // the hard boundary level - e.g. 4
	public int startSegNumber; // the start segment number associated with the tag - e.g. 100
	public int endSegNumber; // the end segment number associated with the tag - e.g. 120

	public XMLSegmentNode(XMLSegmentNode parentNode, String tagname, int boundarylevel, int segNumber)
	{
		this.parent = parentNode;
		this.tag = tagname;
		this.level = boundarylevel;
		this.startSegNumber = segNumber;
	}

	protected void addSon(XMLSegmentNode son)
	{
		sons.add(son);
	}
}
