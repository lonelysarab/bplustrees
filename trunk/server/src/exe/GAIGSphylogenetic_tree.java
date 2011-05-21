package exe;

//import exe.*;

/**
 * <p><code>GAIGSphylogenetic_tree</code> extends the
 * <code>GAIGStree</code> class, providing a uniform interface for that
 * class in generating GAIGS XML code.  Use the various constructors
 * to specify the general parameters for the tree visualization, and
 * use the <code>toXML</code> method to actually generate the tree XML
 * for snapshots.</p>
 * 
 * <p>Many of the more standard tree methods themselves are contained
 * within the <code>GAIGStree</code> and <code>Tree</code>
 * classes. Consult the documentation of those classes</p>.  The
 * unique aspect of phylogenetic trees is there layout in which only
 * leaf nodes contain data and edgeweights are proportional to
 * distances between gene sequences.
 * 
 * @author Tom Naps
 * @version 10/20/2007
 */

public class GAIGSphylogenetic_tree  extends GAIGStree implements GAIGSdatastr {
    

    
//---------------------- Constructors -------------------------------------------


    /**
     * Set the type of phylogenetic tree (binary or general),
     * otherwise use default values.  Most phylogenetic trees should
     * be binary and the layout algorithm is designed for such.
     * 
     * @param       b  Whether this tree is binary (true) or general (false).
     */
    public GAIGSphylogenetic_tree(boolean b) {
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
    public GAIGSphylogenetic_tree(boolean b, String name, String color, double x1, double y1, double x2, double y2, double fontSize) {
        super(b, name, color, x1, y1, x2, y2, fontSize);
//         setName(name);  These four not needed because they are done in the super call
//         setColor(color);
//         setBounds(x1,y1,x2,y2);
//         setFontSize(fontSize);
    }

    /**
       @return the number of nodes in the tree
     */

    public int size() {
	return size_helper(getRoot());
    }
    
    /**
       Recursive helper function

       @return the number of nodes in the tree
     */

    protected int size_helper (TreeNode the_root) {

	if (the_root == null)
	    return 0;
	else {
	    return 1 + size_helper(the_root.getLeftChild()) + size_helper(the_root.getRightChild());
	}
    }

//---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current state of the tree
     * 
     * @return     A String containing GAIGS XML code for the tree  
     */
    public String toXML(){
        String xmlString = "";
        
        if (root != null) {
            xmlString += "<phylogenetic_tree x_spacing = \"" + x_spacing + 
                         "\" y_spacing = \"" + y_spacing + "\">" + "\n";
            if (getName() != null)
                xmlString += "<name>" + getName() + "</name>" + "\n";
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
               
            xmlString += "</phylogenetic_tree>" + "\n";
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
	    xmlString += "<label>" + current.getLineToParent().getWeight() + "</label>";
            xmlString += "</tree_edge>" + "\n";
        }

        if(current.getSibling() != null){
            xmlString = writeXMLHelper(current.getSibling(), xmlString);
        }
        
        return xmlString;
    }
    
}
