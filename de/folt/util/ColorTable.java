/*
 * Created on 05.06.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;

/**
 * Class implements RGB color names and maps them to their resp. RGB value. RGB names and colors taken from http://www.w3.org/TR/css3-color/#x11-color
 * <br>The following color names are supported (if color name not found RGB for red is returned).
 * <pre>
"aliceblue", new RGB(240,248,255)
"antiquewhite", new RGB(250,235,215)
"aqua", new RGB(0,255,255)
"aquamarine", new RGB(127,255,212)
"azure", new RGB(240,255,255)
"beige", new RGB(245,245,220)
"bisque", new RGB(255,228,196)
"black", new RGB(0,0,0)
"blanchedalmond", new RGB(255,235,205)
"blue", new RGB(0,0,255)
"blueviolet", new RGB(138,43,226)
"brown", new RGB(165,42,42)
"burlywood", new RGB(222,184,135)
"cadetblue", new RGB(95,158,160)
"chartreuse", new RGB(127,255,0)
"chocolate", new RGB(210,105,30)
"coral", new RGB(255,127,80)
"cornflowerblue", new RGB(100,149,237)
"cornsilk", new RGB(255,248,220)
"crimson", new RGB(220,20,60)
"cyan", new RGB(0,255,255)
"darkblue", new RGB(0,0,139)
"darkcyan", new RGB(0,139,139)
"darkgoldenrod", new RGB(184,134,11)
"darkgray", new RGB(169,169,169)
"darkgreen", new RGB(0,100,0)
"darkgrey", new RGB(169,169,169)
"darkkhaki", new RGB(189,183,107)
"darkmagenta", new RGB(139,0,139)
"darkolivegreen", new RGB(85,107,47)
"darkorange", new RGB(255,140,0)
"darkorchid", new RGB(153,50,204)
"darkred", new RGB(139,0,0)
"darksalmon", new RGB(233,150,122)
"darkseagreen", new RGB(143,188,143)
"darkslateblue", new RGB(72,61,139)
"darkslategray", new RGB(47,79,79)
"darkslategrey", new RGB(47,79,79)
"darkturquoise", new RGB(0,206,209)
"darkviolet", new RGB(148,0,211)
"deeppink", new RGB(255,20,147)
"deepskyblue", new RGB(0,191,255)
"dimgray", new RGB(105,105,105)
"dimgrey", new RGB(105,105,105)
"dodgerblue", new RGB(30,144,255)
"firebrick", new RGB(178,34,34)
"floralwhite", new RGB(255,250,240)
"forestgreen", new RGB(34,139,34)
"fuchsia", new RGB(255,0,255)
"gainsboro", new RGB(220,220,220)
"ghostwhite", new RGB(248,248,255)
"gold", new RGB(255,215,0)
"goldenrod", new RGB(218,165,32)
"gray", new RGB(128,128,128)
"green", new RGB(0,128,0)
"greenyellow", new RGB(173,255,47)
"grey", new RGB(128,128,128)
"honeydew", new RGB(240,255,240)
"hotpink", new RGB(255,105,180)
"indianred", new RGB(205,92,92)
"indigo", new RGB(75,0,130)
"ivory", new RGB(255,255,240)
"khaki", new RGB(240,230,140)
"lavender", new RGB(230,230,250)
"lavenderblush", new RGB(255,240,245)
"lawngreen", new RGB(124,252,0)
"lemonchiffon", new RGB(255,250,205)
"lightblue", new RGB(173,216,230)
"lightcoral", new RGB(240,128,128)
"lightcyan", new RGB(224,255,255)
"lightgoldenrodyellow", new RGB(250,250,210)
"lightgray", new RGB(211,211,211)
"lightgreen", new RGB(144,238,144)
"lightgrey", new RGB(211,211,211)
"lightpink", new RGB(255,182,193)
"lightsalmon", new RGB(255,160,122)
"lightseagreen", new RGB(32,178,170)
"lightskyblue", new RGB(135,206,250)
"lightslategray", new RGB(119,136,153)
"lightslategrey", new RGB(119,136,153)
"lightsteelblue", new RGB(176,196,222)
"lightyellow", new RGB(255,255,224)
"lime", new RGB(0,255,0)
"limegreen", new RGB(50,205,50)
"linen", new RGB(250,240,230)
"magenta", new RGB(255,0,255)
"maroon", new RGB(128,0,0)
"mediumaquamarine", new RGB(102,205,170)
"mediumblue", new RGB(0,0,205)
"mediumorchid", new RGB(186,85,211)
"mediumpurple", new RGB(147,112,219)
"mediumseagreen", new RGB(60,179,113)
"mediumslateblue", new RGB(123,104,238)
"mediumspringgreen", new RGB(0,250,154)
"mediumturquoise", new RGB(72,209,204)
"mediumvioletred", new RGB(199,21,133)
"midnightblue", new RGB(25,25,112)
"mintcream", new RGB(245,255,250)
"mistyrose", new RGB(255,228,225)
"moccasin", new RGB(255,228,181)
"navajowhite", new RGB(255,222,173)
"navy", new RGB(0,0,128)
"oldlace", new RGB(253,245,230)
"olive", new RGB(128,128,0)
"olivedrab", new RGB(107,142,35)
"orange", new RGB(255,165,0)
"orangered", new RGB(255,69,0)
"orchid", new RGB(218,112,214)
"palegoldenrod", new RGB(238,232,170)
"palegreen", new RGB(152,251,152)
"paleturquoise", new RGB(175,238,238)
"palevioletred", new RGB(219,112,147)
"papayawhip", new RGB(255,239,213)
"peachpuff", new RGB(255,218,185)
"peru", new RGB(205,133,63)
"pink", new RGB(255,192,203)
"plum", new RGB(221,160,221)
"powderblue", new RGB(176,224,230)
"purple", new RGB(128,0,128)
"red", new RGB(255,0,0)
"rosybrown", new RGB(188,143,143)
"royalblue", new RGB(65,105,225)
"saddlebrown", new RGB(139,69,19)
"salmon", new RGB(250,128,114)
"sandybrown", new RGB(244,164,96)
"seagreen", new RGB(46,139,87)
"seashell", new RGB(255,245,238)
"sienna", new RGB(160,82,45)
"silver", new RGB(192,192,192)
"skyblue", new RGB(135,206,235)
"slateblue", new RGB(106,90,205)
"slategray", new RGB(112,128,144)
"slategrey", new RGB(112,128,144)
"snow", new RGB(255,250,250)
"springgreen", new RGB(0,255,127)
"steelblue", new RGB(70,130,180)
"tan", new RGB(210,180,140)
"teal", new RGB(0,128,128)
"thistle", new RGB(216,191,216)
"tomato", new RGB(255,99,71)
"turquoise", new RGB(64,224,208)
"violet", new RGB(238,130,238)
"wheat", new RGB(245,222,179)
"white", new RGB(255,255,255)
"whitesmoke", new RGB(245,245,245)
"yellow", new RGB(255,255,0)
"yellowgreen", new RGB(154,205,50)
 * </pre>
 * @author klemens
 *
 */
public class ColorTable
{

    private static Hashtable<String, Color> colorHashTable = null;
    
    public static Hashtable<String, RGB> colorNameRGB = new Hashtable<String, RGB>();
    
    public static String[] colorNames = null;

    private static Hashtable<RGB, Color> colorRGBHashTable;

    private static Hashtable<String, Color> colorRGBStringnameHashTable = null;;



    /**
     * fillColorTable 
     */
    private static void fillColorTable()
    {
        colorNameRGB.put("aliceblue", new RGB(240,248,255));
        colorNameRGB.put("antiquewhite", new RGB(250,235,215));
        colorNameRGB.put("aqua", new RGB(0,255,255));
        colorNameRGB.put("aquamarine", new RGB(127,255,212));
        colorNameRGB.put("azure", new RGB(240,255,255));
        colorNameRGB.put("beige", new RGB(245,245,220));
        colorNameRGB.put("bisque", new RGB(255,228,196));
        colorNameRGB.put("black", new RGB(0,0,0));
        colorNameRGB.put("blanchedalmond", new RGB(255,235,205));
        colorNameRGB.put("blue", new RGB(0,0,255));
        colorNameRGB.put("blueviolet", new RGB(138,43,226));
        colorNameRGB.put("brown", new RGB(165,42,42));
        colorNameRGB.put("burlywood", new RGB(222,184,135));
        colorNameRGB.put("cadetblue", new RGB(95,158,160));
        colorNameRGB.put("chartreuse", new RGB(127,255,0));
        colorNameRGB.put("chocolate", new RGB(210,105,30));
        colorNameRGB.put("coral", new RGB(255,127,80));
        colorNameRGB.put("cornflowerblue", new RGB(100,149,237));
        colorNameRGB.put("cornsilk", new RGB(255,248,220));
        colorNameRGB.put("crimson", new RGB(220,20,60));
        colorNameRGB.put("cyan", new RGB(0,255,255));
        colorNameRGB.put("darkblue", new RGB(0,0,139));
        colorNameRGB.put("darkcyan", new RGB(0,139,139));
        colorNameRGB.put("darkgoldenrod", new RGB(184,134,11));
        colorNameRGB.put("darkgray", new RGB(169,169,169));
        colorNameRGB.put("darkgreen", new RGB(0,100,0));
        colorNameRGB.put("darkgrey", new RGB(169,169,169));
        colorNameRGB.put("darkkhaki", new RGB(189,183,107));
        colorNameRGB.put("darkmagenta", new RGB(139,0,139));
        colorNameRGB.put("darkolivegreen", new RGB(85,107,47));
        colorNameRGB.put("darkorange", new RGB(255,140,0));
        colorNameRGB.put("darkorchid", new RGB(153,50,204));
        colorNameRGB.put("darkred", new RGB(139,0,0));
        colorNameRGB.put("darksalmon", new RGB(233,150,122));
        colorNameRGB.put("darkseagreen", new RGB(143,188,143));
        colorNameRGB.put("darkslateblue", new RGB(72,61,139));
        colorNameRGB.put("darkslategray", new RGB(47,79,79));
        colorNameRGB.put("darkslategrey", new RGB(47,79,79));
        colorNameRGB.put("darkturquoise", new RGB(0,206,209));
        colorNameRGB.put("darkviolet", new RGB(148,0,211));
        colorNameRGB.put("deeppink", new RGB(255,20,147));
        colorNameRGB.put("deepskyblue", new RGB(0,191,255));
        colorNameRGB.put("dimgray", new RGB(105,105,105));
        colorNameRGB.put("dimgrey", new RGB(105,105,105));
        colorNameRGB.put("dodgerblue", new RGB(30,144,255));
        colorNameRGB.put("firebrick", new RGB(178,34,34));
        colorNameRGB.put("floralwhite", new RGB(255,250,240));
        colorNameRGB.put("forestgreen", new RGB(34,139,34));
        colorNameRGB.put("fuchsia", new RGB(255,0,255));
        colorNameRGB.put("gainsboro", new RGB(220,220,220));
        colorNameRGB.put("ghostwhite", new RGB(248,248,255));
        colorNameRGB.put("gold", new RGB(255,215,0));
        colorNameRGB.put("goldenrod", new RGB(218,165,32));
        colorNameRGB.put("gray", new RGB(128,128,128));
        colorNameRGB.put("green", new RGB(0,128,0));
        colorNameRGB.put("greenyellow", new RGB(173,255,47));
        colorNameRGB.put("grey", new RGB(128,128,128));
        colorNameRGB.put("honeydew", new RGB(240,255,240));
        colorNameRGB.put("hotpink", new RGB(255,105,180));
        colorNameRGB.put("indianred", new RGB(205,92,92));
        colorNameRGB.put("indigo", new RGB(75,0,130));
        colorNameRGB.put("ivory", new RGB(255,255,240));
        colorNameRGB.put("khaki", new RGB(240,230,140));
        colorNameRGB.put("lavender", new RGB(230,230,250));
        colorNameRGB.put("lavenderblush", new RGB(255,240,245));
        colorNameRGB.put("lawngreen", new RGB(124,252,0));
        colorNameRGB.put("lemonchiffon", new RGB(255,250,205));
        colorNameRGB.put("lightblue", new RGB(173,216,230));
        colorNameRGB.put("lightcoral", new RGB(240,128,128));
        colorNameRGB.put("lightcyan", new RGB(224,255,255));
        colorNameRGB.put("lightgoldenrodyellow", new RGB(250,250,210));
        colorNameRGB.put("lightgray", new RGB(211,211,211));
        colorNameRGB.put("lightgreen", new RGB(144,238,144));
        colorNameRGB.put("lightgrey", new RGB(211,211,211));
        colorNameRGB.put("lightpink", new RGB(255,182,193));
        colorNameRGB.put("lightsalmon", new RGB(255,160,122));
        colorNameRGB.put("lightseagreen", new RGB(32,178,170));
        colorNameRGB.put("lightskyblue", new RGB(135,206,250));
        colorNameRGB.put("lightslategray", new RGB(119,136,153));
        colorNameRGB.put("lightslategrey", new RGB(119,136,153));
        colorNameRGB.put("lightsteelblue", new RGB(176,196,222));
        colorNameRGB.put("lightyellow", new RGB(255,255,224));
        colorNameRGB.put("lime", new RGB(0,255,0));
        colorNameRGB.put("limegreen", new RGB(50,205,50));
        colorNameRGB.put("linen", new RGB(250,240,230));
        colorNameRGB.put("magenta", new RGB(255,0,255));
        colorNameRGB.put("maroon", new RGB(128,0,0));
        colorNameRGB.put("mediumaquamarine", new RGB(102,205,170));
        colorNameRGB.put("mediumblue", new RGB(0,0,205));
        colorNameRGB.put("mediumorchid", new RGB(186,85,211));
        colorNameRGB.put("mediumpurple", new RGB(147,112,219));
        colorNameRGB.put("mediumseagreen", new RGB(60,179,113));
        colorNameRGB.put("mediumslateblue", new RGB(123,104,238));
        colorNameRGB.put("mediumspringgreen", new RGB(0,250,154));
        colorNameRGB.put("mediumturquoise", new RGB(72,209,204));
        colorNameRGB.put("mediumvioletred", new RGB(199,21,133));
        colorNameRGB.put("midnightblue", new RGB(25,25,112));
        colorNameRGB.put("mintcream", new RGB(245,255,250));
        colorNameRGB.put("mistyrose", new RGB(255,228,225));
        colorNameRGB.put("moccasin", new RGB(255,228,181));
        colorNameRGB.put("navajowhite", new RGB(255,222,173));
        colorNameRGB.put("navy", new RGB(0,0,128));
        colorNameRGB.put("oldlace", new RGB(253,245,230));
        colorNameRGB.put("olive", new RGB(128,128,0));
        colorNameRGB.put("olivedrab", new RGB(107,142,35));
        colorNameRGB.put("orange", new RGB(255,165,0));
        colorNameRGB.put("orangered", new RGB(255,69,0));
        colorNameRGB.put("orchid", new RGB(218,112,214));
        colorNameRGB.put("palegoldenrod", new RGB(238,232,170));
        colorNameRGB.put("palegreen", new RGB(152,251,152));
        colorNameRGB.put("paleturquoise", new RGB(175,238,238));
        colorNameRGB.put("palevioletred", new RGB(219,112,147));
        colorNameRGB.put("papayawhip", new RGB(255,239,213));
        colorNameRGB.put("peachpuff", new RGB(255,218,185));
        colorNameRGB.put("peru", new RGB(205,133,63));
        colorNameRGB.put("pink", new RGB(255,192,203));
        colorNameRGB.put("plum", new RGB(221,160,221));
        colorNameRGB.put("powderblue", new RGB(176,224,230));
        colorNameRGB.put("purple", new RGB(128,0,128));
        colorNameRGB.put("red", new RGB(255,0,0));
        colorNameRGB.put("rosybrown", new RGB(188,143,143));
        colorNameRGB.put("royalblue", new RGB(65,105,225));
        colorNameRGB.put("saddlebrown", new RGB(139,69,19));
        colorNameRGB.put("salmon", new RGB(250,128,114));
        colorNameRGB.put("sandybrown", new RGB(244,164,96));
        colorNameRGB.put("seagreen", new RGB(46,139,87));
        colorNameRGB.put("seashell", new RGB(255,245,238));
        colorNameRGB.put("sienna", new RGB(160,82,45));
        colorNameRGB.put("silver", new RGB(192,192,192));
        colorNameRGB.put("skyblue", new RGB(135,206,235));
        colorNameRGB.put("slateblue", new RGB(106,90,205));
        colorNameRGB.put("slategray", new RGB(112,128,144));
        colorNameRGB.put("slategrey", new RGB(112,128,144));
        colorNameRGB.put("snow", new RGB(255,250,250));
        colorNameRGB.put("springgreen", new RGB(0,255,127));
        colorNameRGB.put("steelblue", new RGB(70,130,180));
        colorNameRGB.put("tan", new RGB(210,180,140));
        colorNameRGB.put("teal", new RGB(0,128,128));
        colorNameRGB.put("thistle", new RGB(216,191,216));
        colorNameRGB.put("tomato", new RGB(255,99,71));
        colorNameRGB.put("turquoise", new RGB(64,224,208));
        colorNameRGB.put("violet", new RGB(238,130,238));
        colorNameRGB.put("wheat", new RGB(245,222,179));
        colorNameRGB.put("white", new RGB(255,255,255));
        colorNameRGB.put("whitesmoke", new RGB(245,245,245));
        colorNameRGB.put("yellow", new RGB(255,255,0));
        colorNameRGB.put("yellowgreen", new RGB(154,205,50));   
        
        colorNameRGB.put("lightblue0", new RGB(0x25, 0xA8, 0xCC));
        colorNameRGB.put("lightblue1", new RGB(0x4F, 0xB9, 0xD6));
        colorNameRGB.put("lightblue2", new RGB(0x72, 0xC7, 0xDE));
        colorNameRGB.put("lightblue3", new RGB(0x9B, 0xD7, 0xE7));
        colorNameRGB.put("lightblue4", new RGB(0xC2, 0xD7, 0xE7));
        
        colorNames = new String[colorNameRGB.size()];
        
        Enumeration<String> names = colorNameRGB.keys();
        int i = 0;
        while (names.hasMoreElements())
        {
            colorNames[i] = names.nextElement();
            i++;
        }
    }



    /**
     * @return the colorNameRGB (hash table with key color name and value is RGB
     */
    public static Hashtable<String, RGB> getColorNameRGB()
    {
        return colorNameRGB;
    }
    


    /**
     * Method returns a string array of supported color name
     * @return the colorNames
     */
    public static String[] getColorNames()
    {
        return colorNames;
    }
    
    /**
     * getInstance get an instance of color based on RGB Value
     * @param device the device for the color display
     * @param color RGB value
     * @return the matching Color
     */
    public static Color getInstance(Device device, RGB colorRGB)
    {
        if (colorHashTable == null)
            colorHashTable = new Hashtable<String, Color>();
        if (colorRGBStringnameHashTable == null)
            colorRGBStringnameHashTable = new Hashtable<String, Color>();
        if (colorRGBHashTable == null)
            colorRGBHashTable = new Hashtable<RGB, Color>();
        
        if (colorRGBHashTable.containsKey(colorRGB))
            return colorRGBHashTable.get(colorRGB);
        
        if (device == null)
            return null;
        
        Color color = new Color(device, colorRGB);
        colorHashTable.put(colorRGB.toString(), color); // ok tricky needed for giving resource free...
        colorRGBStringnameHashTable.put(colorRGB.toString(), color);
        colorRGBHashTable.put(colorRGB, color);
        
        return color;
    }
    
    /**
     * getInstance get an instance of color based on a name
     * @param device the device for the color display
     * @param colorName a string with the color names
     * @return the matching Color
     */
    public static Color getInstance(Device device, String colorName)
    {
        if (colorHashTable == null)
            colorHashTable = new Hashtable<String, Color>();
        if (colorRGBStringnameHashTable == null)
            colorRGBStringnameHashTable = new Hashtable<String, Color>();
        if (colorRGBHashTable == null)
            colorRGBHashTable = new Hashtable<RGB, Color>();
        
        if (colorHashTable.containsKey(colorName))
            return colorHashTable.get(colorName);
        if (colorRGBStringnameHashTable.containsKey(colorName))
            return colorRGBStringnameHashTable.get(colorName);
        
        if (device == null)
            return null;
        
        Color color = new Color(device, de.folt.util.ColorTable.getRGBByName(colorName));
        colorHashTable.put(colorName, color);
        colorRGBStringnameHashTable.put(color.getRGB().toString(), color);
        colorRGBHashTable.put(color.getRGB(), color);
        
        return color;
    }
    
    /**
     * getRGBByName returns the RGB value for a given color name; the color name is converted to lower case
     * @param colorName the color name e.g. green
     * @return the RGB value for the color; red is returned if the color does not exist
     */
    public static RGB getRGBByName(String colorName)
    {
        colorName = colorName.toLowerCase();
        if (colorNameRGB == null)
        {
            colorNameRGB = new Hashtable<String, RGB>(); 
        }
        if (colorNameRGB.size() < 1)
        {
            fillColorTable();
        }
        
        if (colorNameRGB.containsKey(colorName))
            return colorNameRGB.get(colorName);
        else
            return colorNameRGB.get("red");
    }
    
    /**
     * isInstantiatedColor check if a color has been instatiated
     * @param colorName the color name
     * @return true if instatiated otherwise false;
     */
    public static boolean isInstantiatedColor(String colorName)
    {
        if (colorHashTable == null)
            return false;
        return colorHashTable.containsKey(colorName);
    }
    
    /**
     * removeInstance remove and dispose all the allocated colors
     */
    public static void removeInstance()
    {
        if (colorHashTable == null)
            return;
        Enumeration<Color> enumar = colorHashTable.elements();
        while (enumar.hasMoreElements())
        {
            try
            {
                enumar.nextElement().dispose();
            }
            catch (Exception ex)
            {
                
            }
        }
        colorHashTable.clear();
        colorHashTable = null;
    }
    
}
