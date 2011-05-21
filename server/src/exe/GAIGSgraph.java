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
 * <p><code>GAIGSgraph</code> extends the <code>VisualGraph</code> class, providing
 * a uniform interface for that class in generating GAIGS XML code.
 * Use the various constructors to specify the general parameters for the graph
 * visualization, and use the <code>toXML</code> method to actually generate
 * the tree XML for snapshots.</p>
 * 
 * <p>All the graph methods themselves are contained within the <code>VisualGraph</code>
 * class. Consult the documentation of that class</p> 
 * 
 * @author Andrew Jungwirth (original toXML code)
 * @author Myles McNally (current version)
 * @version 5/28/06
 */

public class GAIGSgraph extends VisualGraph implements GAIGSdatastr {

    static final boolean DEFAULT_WEIGHTED = false;
    static final boolean DEFAULT_DIRECTED = false;
    static final boolean DEFAULT_HEURISTICS = false;
    
//---------------------- Instance Variables -------------------------------------


    String name;
    String color;
               
//---------------------- Constructors -------------------------------------------
                             
    /**
     * Use all default values
     */
     public GAIGSgraph() {
        this(DEFAULT_WEIGHTED, DEFAULT_DIRECTED, DEFAULT_HEURISTICS, DEFAULT_NAME, DEFAULT_COLOR, DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE);
    }                                  
    
    /**
     * Set weighted, directed and heuristics, otherwise use all default values
     * 
     * @param       weighted            Weighted edges or not.
     * @param       directed            Directed edges or not.
     * @param       heuristics          Use heuristics or not.
     */
    public GAIGSgraph(boolean weighted, boolean directed, boolean heuristics) {
        this(weighted, directed, heuristics, DEFAULT_NAME, DEFAULT_COLOR, DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE);
    }
    
    /**
     * Set all instance variables.
     * 
     * @param       weighted            Weighted edges or not.
     * @param       directed            Directed edges or not.
     * @param       heuristics          Use heuristics or not.
     * @param       name                Display name of this structure.
     * @param       color               Color for items unless locally overridden.
     * @param       x1                  Left display bound.
     * @param       y1                  Bottom display bound.
     * @param       x2                  Top display bound.
     * @param       y2                  Right display bound.
     * @param       fontSize            Font size for display.
     */
    public GAIGSgraph(boolean weighted, boolean directed, boolean heuristics, String name, String color, double x1, double y1, double x2, double y2, double fontSize) {
        super(weighted, directed, heuristics);
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
    
    
//---------------------- Graph Methods ------------------------------------------


    // all graph methods are inherited
    
    
//---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current state of the graph
     * 
     * @return     A String containing GAIGS XML code for the graph  
     */
    public String toXML(){
        
        String xmlString = "";
        
        xmlString += "<graph weighted = \"" + weighted + "\">" + "\n";
        if (name != null)
            xmlString += "<name>" + name + "</name>" + "\n";
        xmlString += "<bounds x1 = \"" + x1 + "\" y1 = \"" + y1 +
                     "\" x2 = \"" + x2 + "\" y2 = \"" + y2 + 
                     "\" fontsize = \"" + font_size + "\"/>" + "\n";
            
        for (int n = 0; n < MAX_NODES; n++) {
            if (my_nodeset[n].isActivated()){
                xmlString += "<vertex color = \"" + 
                             my_nodeset[n].getHexColor() + "\" id = \"" + n + "\">" + "\n";

                if (heuristics)
                    xmlString += "<label>" + my_nodeset[n].getChar() + "\n" + 
                                 my_nodeset[n].getHeuristic() + "</label>" + "\n";
                else
                    xmlString += "<label>" + my_nodeset[n].getChar() + "</label>" + "\n";

                    xmlString += "<position x = \"" + my_nodeset[n].getX() + 
                                 "\" y = \"" + my_nodeset[n].getY() + "\"/>" + "\n";

                for (int e = 0; e < MAX_NODES; e++) {
                    if (my_edgeset[n][e].isActivated()){
                        xmlString += "<edge target = \"" + e + 
                                     "\" directed = \"" + directed + 
                                     "\" color = \"" + 
                                     my_edgeset[n][e].getHexColor() + "\">" + "\n";
                        if (weighted)
                            xmlString += "<label>" +
                                         (int)my_edgeset[n][e].getWeight() + 
                                         "</label>" + "\n";
                        xmlString += "</edge>" + "\n";
                    }
                }
        
            xmlString += "</vertex>" + "\n";
            }
        }
        
        xmlString += "</graph>" + "\n";
        return xmlString;
    }
}
