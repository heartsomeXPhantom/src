/*
 * Created on 26.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.documentmodel.tmx;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import de.folt.models.datamodel.LinguisticProperties;
import de.folt.models.datamodel.LinguisticProperty;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.datamodel.GeneralLinguisticObject.LinguisticTypes;
import de.folt.models.documentmodel.document.XmlDocument;
import de.folt.models.documentmodel.tmx.TmxProp.PropType;
import de.folt.util.Timer;

/**
 * Interface defines the methods for accessing, reading and writing an TMX Document.<br>
 * For details of TMX - Translation Memory eXchange (TMX): {@see <a href="http://www.lisa.org/Translation-Memory-e.34.0.html">TMX</a>}
 * 
 * @author klemens
 * 
 */
public class TmxDocument extends XmlDocument
{

    /**
     * 
     */
    private static final long serialVersionUID = 3546531477195131822L;

    /**
     * main
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            String tmxfile = args[0];
            Timer timer = new Timer();
            timer.startTimer();
            File f = new File(tmxfile);
            TmxDocument doc = new TmxDocument();
            // load the xml file
            doc.loadXmlFile(f);
            timer.stopTimer();
            System.out.println(timer.timerString("TMX file read " + tmxfile + ": Version " + doc.getTmxVersion()));
            timer.startTimer();
            // save it
            doc.saveToXmlFile(tmxfile + ".copy.tmx");
            timer.stopTimer();

            Element header = doc.getTmxHeader();
            System.out.println("TMX Header:\n" + doc.createLinguisticProperties(header).format());

            System.out.println(timer.timerString("TMX file save " + tmxfile + ": Version " + doc.getTmxVersion()));
            System.out.println("TMX file #tuvs = " + doc.getTuList().size());
            for (int i = 0; i < doc.getTuList().size(); i++)
            {
                Element tu = doc.getTuList().get(i);
                String tustring = doc.elementToString(tu);
                System.out.println("TUV string (" + i + ") = \"" + tustring + "\"");
                List<Element> tuvs = doc.getTuvList(tu);
                for (int j = 0; j < tuvs.size(); j++)
                {
                    Element tuv = tuvs.get(j);
                    Element seg = doc.getSeg(tuv);
                    System.out.println("\tTU string a (" + j + ") = \"" + doc.elementToString(seg) + "\"");
                    System.out.println("\tTU string b (" + j + ") = \"" + doc.elementContentToString(seg) + "\"");
                }

                MultiLingualObject multi = doc.tuToMultiLingualObject(tu);
                System.out.println(multi.format());

            }
            doc = null;
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    Integer idVal = 1;

    private String srclang = null;

    protected Element TmxBody = null;

    protected Element TmxHeader = null;

    protected String TmxVersion = "";

	/**
     * 
     */
    public TmxDocument()
    {
        super();
    }

    /**
     * create LinguisticProperties Method returns the attributes of the element as LinguisticProperties
     * 
     * @param element
     *            the element to retrieve the attributes from
     * @return the LinguisticProperties for the element
     */
    @SuppressWarnings("unchecked")
    public LinguisticProperties createLinguisticProperties(Element element)
    {
        LinguisticProperties linguisticProperties = new LinguisticProperties();

        List<Attribute> attributes = element.getAttributes();
        for (int i = 0; i < attributes.size(); i++)
        {
            Attribute att = attributes.get(i);
            if (att.getName().equals("lang")) // ignore language
                continue;
            TmxProp tmxProp = new TmxProp(att.getValue(), "", "", TmxProp.PropType.CORE, att.getName(), idVal++);
            LinguisticProperty lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
            lingProp.setData(att);
            lingProp.setPropStatus(LinguisticProperty.PropStatus.NEW);
            linguisticProperties.add(lingProp);
        }

        List<Element> props = element.getChildren("prop");
        for (int i = 0; i < props.size(); i++)
        {
            TmxProp tmxProp = new TmxProp(props.get(i), idVal++);
            LinguisticProperty lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
            lingProp.setData(props.get(i));
            lingProp.setPropStatus(LinguisticProperty.PropStatus.NEW);
            linguisticProperties.add(lingProp);
        }

        List<Element> notes = element.getChildren("note");
        for (int i = 0; i < notes.size(); i++)
        {
            TmxProp tmxProp = new TmxProp(notes.get(i), idVal++);
            LinguisticProperty lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
            lingProp.setData(notes.get(i));
            lingProp.setPropStatus(LinguisticProperty.PropStatus.NEW);
            linguisticProperties.add(lingProp);
        }

        return linguisticProperties;
    }

    /**
     * getCreationid
     * 
     * @param element
     * @return
     */
    public String getCreationid(Element element)
    {
        String creationid = element.getAttributeValue("creationid");
        return creationid;
    }

    /**
     * @return the document
     */
    public Document getDocument()
    {
        return document;
    }

    /**
     * getSegList returns the seg (segment) of a TUV Element
     * 
     * @param tuv
     *            the tuv
     * @return the seg element of the tuv
     */
    public Element getSeg(Element tuv)
    {
        @SuppressWarnings("rawtypes")
		List segs = tuv.getChildren("seg");
        if (segs != null)
            return (Element) segs.get(0);
        return null;
    }

    /**
     * getSegAsString
     * 
     * @param tuv
     * @return null if el == null; otherwise the string representation of the seg element ({@see de.folt.models.documentmodel.document.XmmDocument#elementToString})
     */
    public String getSegAsString(Element tuv)

    {
        Element el = getSeg(tuv);
        if (el != null)
            return this.elementContentToString(tuv);
        return null;
    }

    public String getSrclang()
	{
		return srclang;
	}

    public Element getTmxBody()
    {
        if (TmxBody == null)
            TmxBody = getDocument().getRootElement().getChild("body");
        return TmxBody;
    }

    /**
     * getTmxHeader
     * 
     * @return the header of the TMX document
     */
    public Element getTmxHeader()
    {
        if (TmxHeader == null)
            TmxHeader = getDocument().getRootElement().getChild("header");
        return TmxHeader;
    }

    @SuppressWarnings("unchecked")
    public List<Element> getTmxHeaderNoteList()
    {
        Element header = this.getTmxHeader();
        if (header != null)
            return (List<Element>) header.getChildren("note");
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    public List<Element> getTmxHeaderPropList()
    {
        Element header = this.getTmxHeader();
        if (header != null)
            return (List<Element>) header.getChildren("prop");
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    public List<Element> getTmxHeaderUdeList()
    {
        Element header = this.getTmxHeader();
        if (header != null)
            return (List<Element>) header.getChildren("ude");
        else
            return null;
    }

    /**
     * getTmxversion get the TMX version of the tmx document
     * 
     * @return the TMX version of the document
     */
    public String getTmxVersion()
    {
        String version = "unknown";
        if (getDocument() == null)
            return version;
        Element root = getDocument().getRootElement();
        if (root == null)
            return version;
        version = root.getAttributeValue("version");
        if (version == null)
            version = "unknown";
        return version;
    }

    @SuppressWarnings("unchecked")
    public List<Element> getTuList()
    {
        if (TmxBody == null)
            TmxBody = this.getTmxBody();

        return TmxBody.getChildren("tu");
    }

    /**
     * getTUVLang returns the language of an element
     * 
     * @param element
     *            the element
     * @return the language of an element (xml:lang or lang)
     */
    public String getTuvLang(Element element)
    {
        String language = element.getAttributeValue("lang", Namespace.XML_NAMESPACE);
        return language;
    }

    @SuppressWarnings("unchecked")
    public List<Element> getTuvList(Element tuv)
    {
        return tuv.getChildren("tuv");
    }

    /**
     * getTuvList
     * 
     * @param tu
     *            the tu element
     * @param language
     *            the language tuvs to retrieve
     * @return a List with all tuvs of the given language
     */
    public List<Element> getTuvList(Element tu, String language)
    {
        List<Element> tuvs = getTuvList(tu);
        Iterator<Element> en = tuvs.iterator();
        while (en.hasNext())
        {
            Element current = en.next();
            String lang = current.getAttributeValue("xml:lang");
            if (!lang.equals(language))
                tuvs.remove(current);
        }
        return tuvs;
    }

    private void handleTmxProp(TmxProp tmxProp, LinguisticProperty ling, Element tuv, Object data)
    {
        if (tmxProp != null)
        {
            if (ling.getPropStatus().equals(LinguisticProperty.PropStatus.DELETED))
            {
                // now handle associations with Elements and Attributes
                if (data != null)
                {
                    if (data.getClass().getCanonicalName().equals("org.jdom.Element"))
                    {
                        Element element = (Element) data;
                        if (element != null)
                        {
                            Element parent = element.getParentElement();
                            if (parent != null)
                                parent.removeContent(element);
                        }
                    }
                    else if (data.getClass().getCanonicalName().equals("org.jdom.Attribute"))
                    {
                        Attribute attribute = (Attribute) data;
                        if (attribute != null)
                        {
                            Element parent = attribute.getParent();
                            if (parent != null)
                                parent.removeAttribute(attribute);
                        }
                    }
                }
                return;
            }
            @SuppressWarnings("unused")
            String id = tmxProp.getId() + "";
            String content = tmxProp.getContent();
            String language = tmxProp.getLang();
            String o_encoding = tmxProp.getO_encoding();
            PropType propType = tmxProp.getPropType();
            String type = tmxProp.getType(); // this is actually the property name, e.g. creation-id
            // now we must check if contained in the tuv
            // we must check if CORE or PROP OR NOTE attribute
            if (propType.equals(PropType.CORE))
            {
                Attribute attr = tuv.getAttribute(type);
                if (!attr.getValue().equals(content))
                    attr.setValue(content);
            }
            else if (propType.equals(PropType.PROP))
            {
                Element prop = tuv.getChild(type); // we identify based on the child
                if (prop == null) // a new one
                {
                    prop = new Element("prop");
                    prop.setText(content);
                    if (type != null)
                        prop.setAttribute("type", type);
                    if (language != null)
                    {
                        prop.setAttribute("lang", language, Namespace.XML_NAMESPACE);
                    }
                    if (o_encoding != null)
                        prop.setAttribute("o-encoding", o_encoding);
                    tuv.addContent(prop);
                }
                else
                {
                    // we only allow the content to change!
                    if (!prop.getText().equals(content))
                        prop.setText(content);
                }

            }
            else if (propType.equals(PropType.NOTE))
            {
                Element prop = tuv.getChild(type); // we identify based on the child
                if (prop == null) // a new one
                {
                    prop = new Element("note");
                    prop.setText(content);
                    if (type != null)
                        prop.setAttribute("type", type);
                    if (language != null)
                    {
                        prop.setAttribute("lang", language, Namespace.XML_NAMESPACE);
                    }
                    if (o_encoding != null)
                        prop.setAttribute("o-encoding", o_encoding);
                    tuv.addContent(prop);
                }
                else
                {
                    // we only allow the content to change!
                    if (!prop.getText().equals(content))
                        prop.setText(content);
                }
            }
            ling.setPropStatus(LinguisticProperty.PropStatus.OLD);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.documentmodel.document.XmlDocument#loadXmlFile(java.io.File)
     */
    public Document loadXmlFile(File newFile)
    {
        try
        {
            super.loadXmlFile(newFile);
            if (document != null)
            {
                getTmxBody();
                getTmxHeader();
                
                // srclang="en-us"
                if (this.TmxHeader != null)
                {
                	this.setSrclang(this.TmxHeader.getAttributeValue("srclang"));
                }
                
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            document = null;
        }
        return document;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.documentmodel.document.XmlDocument#loadXmlFile(java.lang.String)
     */
    public Document loadXmlFile(String filename)
    {
        try
        {
            super.loadXmlFile(filename);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            document = null;
        }
        if (document != null)
        {
            getTmxBody();
            getTmxHeader();
        }
        return document;
    }

    /**
     * saveModifiedLinguisticProperties save the properties back to the tuv of the MonoLingualObject. It is assumed that the tuv Element is stored as a LinguisticProperty (monoLingualObject.getObjectLinguisticProperty("tuv")).
     * @param monoLingualObject the MonoLingualObject which contains the changed or new Properties
     * @return the possible changed tuv Element
     */
    @SuppressWarnings("unchecked")
    public Element saveModifiedLinguisticProperties(MonoLingualObject monoLingualObject)
    {
        LinguisticProperty lingtuv = monoLingualObject.getObjectLinguisticProperty("tuv");
        Element tuv = (Element) lingtuv.getValue();
        if (tuv == null)
            return tuv;

        LinguisticProperties linguisticProperties = monoLingualObject.getLinguisticProperties();
        if (linguisticProperties == null)
            return tuv;
        Set<String> enumprop = linguisticProperties.keySet();
        Iterator<String> it = enumprop.iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            Object value = (Object) linguisticProperties.get(key);
            Object data = null;
            TmxProp tmxProp = null;
            LinguisticProperty ling = null;
            if (value.getClass().getName().equals("java.util.Vector"))
            {
                for (int i = 0; i < ((Vector<LinguisticProperty>) value).size(); i++)
                {
                    ling = ((Vector<LinguisticProperty>) value).get(i);
                    if (ling.getPropStatus().equals(LinguisticProperty.PropStatus.NEW))
                        continue;
                    Object lingValue = ling.getValue();
                    String classname = lingValue.getClass().getName();
                    if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
                    {
                        tmxProp = (TmxProp) lingValue;
                        data = ling.getData();
                        handleTmxProp(tmxProp, ling, tuv, data);
                    }
                }
            }
            else
            {
                ling = (LinguisticProperty) linguisticProperties.get(key);
                Object lingValue = ling.getValue();
                String classname = lingValue.getClass().getName();
                if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
                {
                    if (ling.getPropStatus().equals(LinguisticProperty.PropStatus.OLD))
                        continue;
                    tmxProp = (TmxProp) lingValue;
                    data = ling.getData();
                    handleTmxProp(tmxProp, ling, tuv, data);
                }
            }
        }

        return tuv;
    }

    /**
     * saveModifiedLinguisticProperties 
     * @param multiLingualObject
     */
    @SuppressWarnings("unchecked")
    public Element saveModifiedLinguisticProperties(MultiLingualObject multiLingualObject)
    {
        LinguisticProperty lingtu = multiLingualObject.getObjectLinguisticProperty("tu");
        Element tu = (Element) lingtu.getValue();
        if (tu == null)
            return tu;

        LinguisticProperties linguisticProperties = multiLingualObject.getLinguisticProperties();
        if (linguisticProperties == null)
            return tu;
        Set<String> enumprop = linguisticProperties.keySet();
        Iterator<String> it = enumprop.iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            Object value = (Object) linguisticProperties.get(key);
            Object data = null;
            TmxProp tmxProp = null;
            LinguisticProperty ling = null;
            if (value.getClass().getName().equals("java.util.Vector"))
            {
                for (int i = 0; i < ((Vector<LinguisticProperty>) value).size(); i++)
                {
                    ling = ((Vector<LinguisticProperty>) value).get(i);
                    if (ling.getPropStatus().equals(LinguisticProperty.PropStatus.NEW))
                        continue;
                    Object lingValue = ling.getValue();
                    String classname = lingValue.getClass().getName();
                    if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
                    {
                        tmxProp = (TmxProp) lingValue;
                        data = ling.getData();
                        handleTmxProp(tmxProp, ling, tu, data);
                    }
                }
            }
            else
            {
                ling = (LinguisticProperty) linguisticProperties.get(key);
                Object lingValue = ling.getValue();
                String classname = lingValue.getClass().getName();
                if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
                {
                    if (ling.getPropStatus().equals(LinguisticProperty.PropStatus.OLD))
                        continue;
                    tmxProp = (TmxProp) lingValue;
                    data = ling.getData();
                    handleTmxProp(tmxProp, ling, tu, data);
                }
            }
        }

        return tu;
    }

    /**
     * segToString returns the string representation of an element
     * 
     * @param seg
     *            the seg to convert
     * @return a string representation of a seg
     */
    public String segToString(Element seg)
    {
        String elst = seg.toString();
        return elst.replaceAll(seg.getName(), "");
    }

    /**
     * @param document
     *            the document to set
     */
    public void setDocument(Document document)
    {
        this.document = document;
    }

    public void setSrclang(String srclang)
	{
		this.srclang = srclang;
	}

    /**
     * toString converts an (TMX) element to a string)<br>
     * 
     * <pre>
     * &lt;seg&gt;hallo&lt;ph&gt;xxx&lt;/ph&gt; ist...&lt;/seg&gt;
     * </pre>
     * 
     * @param element
     *            to create astring from
     * @return a string representing the element
     */
    public String toString(Element element)
    {
        return element.toString();
    }

	/**
     * tuToMultiLingualObject converts a given tu element into a MultiLingualObject
     * 
     * @param tu
     *            the tu Element
     * @return the MultiLingualObject for the given tu
     * 
     */
    public MultiLingualObject tuToMultiLingualObject(Element tu)
    {
        LinguisticProperties linguisticProperties = createLinguisticProperties(tu);
        
        MultiLingualObject multi = new MultiLingualObject(linguisticProperties, LinguisticTypes.TMX);
        LinguisticProperty tuProp = new LinguisticProperty("tu", tu);
        multi.addLinguisticProperty(tuProp);
        
        String uniquidvalue = (String) linguisticProperties.search("unique-id");
        if ((uniquidvalue != null) && !uniquidvalue.equals(""))
        {
        	multi.setStUniqueID(uniquidvalue);
        }
        
        String stlastAccessTime = (String) linguisticProperties.search("lastAccessTime");
        if ((stlastAccessTime != null) && !stlastAccessTime.equals(""))
        {
        	try
			{
				long lastAccessTime = Long.parseLong(stlastAccessTime);
				multi.setLastAccessTime(lastAccessTime);
			}
			catch (NumberFormatException e)
			{
			}
        }

        List<Element> tuvs = this.getTuvList(tu);
        for (int i = 0; i < tuvs.size(); i++)
        {
            MonoLingualObject mono = tuvToMonoLingualObject(tuvs.get(i));
            if (mono != null)
                multi.addMonoLingualObject(mono);
        }
        return multi;
    }

	/**
     * tuvToMonoLingualObject converts a given tuv element into a MoonLingualObject
     * 
     * @param tuv
     *            the tuv Element
     * @return the MonoLingualObject for the given tuv
     */
    public MonoLingualObject tuvToMonoLingualObject(Element tuv)
    {
        // get the attributes, props and notes
        LinguisticProperties linguisticProperties = createLinguisticProperties(tuv);
        LinguisticProperty tuProp = new LinguisticProperty("tuv", tuv);
        String segment = this.elementContentToString(this.getSeg(tuv));
        String plainTextSegment = MonoLingualObject.simpleComputePlainText(segment);
        MonoLingualObject mono = new MonoLingualObject(linguisticProperties, LinguisticTypes.TMX, segment, plainTextSegment, this.getTuvLang(tuv));
        
        String uniquidvalue = (String) linguisticProperties.search("unique-id");
        if ((uniquidvalue != null) && !uniquidvalue.equals(""))
        {
        	mono.setStUniqueID(uniquidvalue);
        }
        
        String stlastAccessTime = (String) linguisticProperties.search("lastAccessTime");
        if ((stlastAccessTime != null) && !stlastAccessTime.equals(""))
        {
        	try
			{
				long lastAccessTime = Long.parseLong(stlastAccessTime);
				mono.setLastAccessTime(lastAccessTime);
			}
			catch (NumberFormatException e)
			{
			}
        }
        
        mono.addLinguisticProperty(tuProp);
        return mono;
    }

}
