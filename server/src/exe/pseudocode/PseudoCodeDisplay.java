/* 
This file is part of JHAVE -- Java Hosted Algorithm Visualization
Environment, developed by Tom Naps, David Furcy (both of the
University of Wisconsin - Oshkosh), Myles McNally (Alma College), and
numerous other contributors who are listed at the http://jhave.org
site

JHAVE is free software: you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free
Software Foundation, either version 3 of the License, or (at your
option) any later version.

JHAVE is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with the JHAVE. If not, see:
<http://www.gnu.org/licenses/>.
*/

package exe.pseudocode;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.transform.*;
import org.jdom.xpath.*;

/**
 * PsuedoCodeDisplay.java - This program is used to generate a URI String from a
 *     pseudocode XML file (defined by pseudo.dtd).  For more information, see
 *     the documentation for the pseudo_uri method.
 * 
 * @author Justin Henry, William Gates
 */
public class PseudoCodeDisplay {
	// First we define some default highlight colors
	private final String[][] HIGHLIGHTS = {
		{"#FF0000","#FFDDDD"}, // Red
		{"#FF6600","#FFEECC"}, // Orange
		{"#999900","#FFFFBB"}, // Yellow
		{"#009900","#DDFFDD"}, // Green
		{"#0000FF","#DDEEFF"}, // Blue
		{"#880088","#FFDDFF"}, // Purple
		{"#666666","#EEEEEE"}, // Gray
	};

	public static final int RED = 0;
	public static final int ORANGE = 1;
	public static final int YELLOW = 2;
	public static final int GREEN = 3;
	public static final int BLUE = 4;
	public static final int PURPLE = 5;
	public static final int GRAY = 6;

	private final int DEFAULT_COLOR = RED;

	// Where our files live
	private final String XML = "../../src/exe/pseudocode/pseudo.xml";
	private final String XSL = "../../src/exe/pseudocode/pseudo.xsl";

	SAXBuilder builder;
	private Document doc;
	private String uriStr;

	private HashMap<String, String> vars;
	private int[] selected;
	private int[] colors;
	private int currSel;

	/**
	 * Constructor - creates a new instance of the PseudoCodeDisplay class.  The
	 *     constructor also initializes the original document tree which is
	 *     built from the file which the user passes in.
	 * 
	 * @param fileName the name of the XML file (defined by pseudo.dtd)
	 * @throws JDOMException
	 * @throws IOException
	 */
    public PseudoCodeDisplay(String fileName) throws JDOMException,
    												 IOException {
    	builder = new SAXBuilder();
    	doc = builder.build(fileName);
    }

    /**
     * This is the main method of the overloaded pseudo_uri method.  All other
     *     versions call this one with some modification of their parameters.
     * 
     * @param vars a HashMap of the variables to be replaced in the XML template
     * @param selected which line numbers should be highlighted
     * @param lineColors what color each line should be highlighted
     * @return a valid URI in String format with its special characters escaped
     * @throws JDOMException
     */
    public String pseudo_uri(HashMap<String, String> vars, int[] selected,
    									int[] lineColors) throws JDOMException {
    	this.vars = vars;
    	this.selected = selected;
    	this.currSel = 0;

    	// Make sure the two arrays are the same length and pad if they are not
    	this.colors = new int[selected.length];
    	for(int i = 0; i < lineColors.length; i++) {
    		this.colors[i] = DEFAULT_COLOR;
    	}

    	for(int i = 0; i < selected.length; i++) {
    		this.colors[i] = lineColors[i];
    	}

    	// Use XPath to traverse the pseudocode XML document
    	uriStr = "";
    	XPath x = XPath.newInstance("/pseudocode");
    	List<?> list = x.selectNodes(doc);
    	
    	uriStr += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    	listElements(list, "");

    	try {
    		writeXML();
    		convertToHTML();
    	} catch(JDOMException jdome) {
    		// Big error here!
    		jdome.printStackTrace();
    	} catch(IOException ioe) {
			// Big error here!
    		ioe.printStackTrace();
		}

    	// We're done; delete the intermediate XML file
    	new File(XML).delete();

    	try {
    		// Make the URI, escape special characters and clean it up 
    		String answer = new URI("str", uriStr, "").toString();
			return answer.replace("&", "%26").replace("%3C?xml%20version=%221" +
					".0%22%20encoding=%22UTF-8%22?%3E%0D%0A", "");
		} catch(URISyntaxException urise) {
			// Big error here!
			return null;
		}
    }
    
    public String pseudo_uri(HashMap<String, String> vars, int selected)
    													throws JDOMException {
    	return pseudo_uri(vars, new int[]{selected}, new int[]{DEFAULT_COLOR});
    }

    public String pseudo_uri(HashMap<String, String> vars, int selected,
    										int color) throws JDOMException {
    	return pseudo_uri(vars, new int[]{selected}, new int[]{color});
    }

    public String pseudo_uri(HashMap<String, String> vars, int[] selected)
    													throws JDOMException {
    	int[] colors = new int[selected.length];
    	for(int i = 0; i < colors.length; i++) {
    		colors[i] = DEFAULT_COLOR;
    	}

    	return pseudo_uri(vars, selected, colors);
    }

    /**
     * Recursive function which takes a List of elements and adds itself and all
     *     its children to the URI String.
     *
     * @param list the list of elements to be added to the URI String
     * @param indent the amount of space to indent these elements
     */
    private void listElements(List<?> list, String indent) {
    	Iterator<?> iter = list.iterator();
    	while(iter.hasNext()) {
    		Element e = (Element)iter.next();
    		listElement(e, indent);
    	}
    }

    /**
     * Non-recursive function which adds a specific element to the URI String,
     *     replacing it if it is a "replace" element, then adding all its
     *     attributes, and finally printing any children elements (using the
     *     recursive method above).
     * 
     * @param e the element to add to the URI String
     * @param indent the amount of space to indent this element
     */
    private void listElement(Element e, String indent) {
    	// If it's a "replace" element, replace it with the corresponding string
    	// from our HashMap.
    	if(e.getName().equals("replace")) {
    		Attribute att = (Attribute)e.getAttributes().get(0);
    		uriStr += vars.get(att.getValue());
    	} else {
    		// Add the element and its attributes
    		uriStr += indent + "<" + e.getName();
        	listAttributes(e);
        	uriStr += ">";

        	// If its text is not blank, escape the special characters and add
        	// the text
        	if(!e.getTextTrim().equals("")) {
        		uriStr += e.getText().replace("<", "&lt;").replace(">", "&gt;");
        	}

        	// Some formatting to make the XML output file look "pretty"
        	if(e.getChildren("replace").isEmpty() && 
        			e.getTextTrim().equals("")) {
        		uriStr += "\n";

        		List<?> children = e.getChildren();
            	listElements(children, indent + "  ");

            	uriStr += indent + "</" + e.getName() + ">\n";
        	} else {
        		List<?> children = e.getChildren();
            	listElements(children, indent + "  ");

            	uriStr += "</" + e.getName() + ">\n";
        	}
    	}
    }

    /**
     * Non-recursive function which takes an element and adds all its
     *     attributes to the URI String, adding extra attributes as necessary if
     *     the element is a "line."
     * 
     * @param e the element whose attributes should be added
     */
    private void listAttributes(Element e) {
    	// Add each existing attribute and its corresponding value
    	for(Iterator<?> i = e.getAttributes().iterator(); i.hasNext();) {
    		Attribute a = (Attribute)i.next();
    		uriStr += " " + a.getName() + "=\"" + a.getValue() + "\"";
    	}

    	// If this is a line, check to see if it is supposed to be highlighted
    	// and add the corresponding attributes
    	if(e.getName().equals("line")) {
    		boolean isSel = false;
    		int lineNum = Integer.parseInt(e.getAttribute("num").getValue());
    		for(int i = 0; i < selected.length; i++) {
    			if(selected[i] == lineNum) {
    	    		uriStr += " text=\"" + HIGHLIGHTS[colors[currSel]][0] +
    	    			"\" back=\"" + HIGHLIGHTS[colors[currSel++]][1] + "\"";
    				isSel = true;
    				break;
    			}
    		}

    		uriStr += " sel=\"" + isSel + "\"";
    	}
    }

    /**
     * Writes the current data stored in the URI String to a temporary XML file
     *     so that it can be transformed into an HTML URI using our XSL
     *     Stylesheet (pseudo.xsl).
     * 
     * @throws IOException
     */
    private void writeXML() throws IOException {
		FileOutputStream out = new FileOutputStream(XML);
		out.write(uriStr.getBytes());
		out.flush();
		out.close();
    }

    /**
     * Takes the newly written XML file and transforms it using our XSL
     *     Stylesheet (pseudo.xsl), storing the HTML output in the URI String.
     * 
     * @throws IOException 
     * @throws JDOMException 
     */
    private void convertToHTML() throws JDOMException, IOException {
   		// Autobots, transform and roll out!
    	XSLTransformer autobots = new XSLTransformer(XSL);
		Document doc = autobots.transform(builder.build(XML));
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());

		uriStr = out.outputString(doc);
    }
}