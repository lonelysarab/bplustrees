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

package exe;

/**
 * <p><code>GAIGStree</code> extends the <code>Tree</code> class, providing
 * a uniform interface for that class in generating GAIGS XML code.
 * Use the various constructors to specify the general parameters for the tree
 * visualization, and use the <code>toXML</code> method to actually generate
 * the tree XML for snapshots.</p>
 * 
 * <p>All the tree methods themselves are contained within the <code>Tree</code>
 * class. Consult the documentation of that class</p> 
 * 
 * @author Andrew Jungwirth (original toXML code)
 * @author Myles McNally (current version)
 * @version 5/28/06
 */

public class GAIGStree  extends Tree implements GAIGSdatastr {
    
 
//---------------------- Instance Variables -------------------------------------

    /**
     * Display name.
     */
    String name;
    
    /**
     * Display color.
     */
    String color;

    
//---------------------- Constructors -------------------------------------------


    /**
     * Set the type of tree (binary or general), otherwise use default values.
     * 
     * @param       b               Whether this tree is binary (true) or general (false).
     */
    public GAIGStree(boolean b) {
        this(b, DEFAULT_NAME, DEFAULT_COLOR, DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE);
    }
    
    /**
     * Set all instance variables.
     * 
     * @param       b               Whether this tree is binary (true) or general (false).
     * @param       name            Display name of this structure.
     * @param       color           Color for items unless locally overridden.
     * @param       x1              Left display bound.
     * @param       y1              Bottom display bound.
     * @param       x2              Top display bound.
     * @param       y2              Right display bound.
     * @param       fontSize        Font size for display.
     */
    public GAIGStree(boolean b, String name, String color, double x1, double y1, double x2, double y2, double fontSize) {
        super(b);
        this.name = name;
        this.color = color;
        setBounds(x1,y1,x2,y2);
        setFontSize(fontSize);
    }
    
    
//---------------------- Display Setter/Getter Methods ---------------------------------


    /**
     * Set the value of the name to be displayed.
     * 
     * @param       name        The display name.
     */
    public void setName (String name) {
        this.name = name;
    }
    
    /**
     * Get the value of the name.
     * 
     * @return      The display name.
     */
    public String getName () {
        return name;
    }  
    
    
//---------------------- Tree Methods ------------------------------------------


    // all tree methods are inherited
    
    
//---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current state of the tree
     * 
     * @return     A String containing GAIGS XML code for the tree  
     */
    public String toXML(){
        String xmlString = "";
        
        if (root != null) {
            xmlString += "<tree x_spacing = \"" + x_spacing + 
                         "\" y_spacing = \"" + y_spacing + "\">" + "\n";
            if (name != null)
                xmlString += "<name>" + name + "</name>" + "\n";
            xmlString += "<bounds x1 = \"" + x1 + "\" y1 = \"" + y1 + 
                         "\" x2 = \"" + x2 + "\" y2 = \"" + y2 + 
                         "\" fontsize = \"" + font_size + "\"/>" + "\n";
            if (binary) {
                xmlString += "<binary_node color = \"" + 
                             root.getHexColor() + "\">" + "\n";
            } else {
                xmlString += "<tree_node color = \"" + root.getHexColor() + "\">" + "\n";
            }
            xmlString += "<label>" + root.getValue() + "</label>" + "\n";

            // Construct the tree recursively in a depth-first manner.
            if (root.getChild() != null)
                xmlString = writeXMLHelper(root.getChild(), xmlString);

            if (binary)
                xmlString += "</binary_node>" + "\n";
            else
                xmlString += "</tree_node>" + "\n";
               
            xmlString += "</tree>" + "\n";
        }

        return xmlString;
    }


    /**
     * Construct the tree XML for each node recursively in a depth-first manner
     * 
     * @return     A String containing GAIGS XML code for the items in a tree  
     */
    private String writeXMLHelper(TreeNode current, String xmlString){
        if (!current.isPlaceHolder()){
            
            if (binary){
                if (current.isLeftChild())
                    xmlString += "<left_node color = \"" + 
                                 current.getHexColor() + "\">" + "\n";
                else
                    xmlString += "<right_node color = \"" + 
                                 current.getHexColor() + "\">" + "\n";
            } else
                xmlString += "<tree_node color = \"" + 
                             current.getHexColor() + "\">" + "\n";

            xmlString += "<label>" + current.getValue() + "</label>" + "\n";

            if (current.getChild() != null)
                xmlString = writeXMLHelper(current.getChild(), xmlString);

            if (binary) {
                if (current.isLeftChild())
                    xmlString += "</left_node>" + "\n";
                else
                    xmlString += "</right_node>" + "\n";
            } else
                xmlString += "</tree_node>" + "\n";


            xmlString += "<tree_edge color = \"" + 
                         current.getLineToParent().getHexColor() + "\">" + "\n";
            xmlString += "</tree_edge>" + "\n";
        }

        if(current.getSibling() != null){
            xmlString = writeXMLHelper(current.getSibling(), xmlString);
        }
        
        return xmlString;
    }
}
