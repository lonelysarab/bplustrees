// TN NOTE -- down the road may want a version of constructor that
// takes an array/vector of trees

package exe;

//import exe.*;
import java.util.Vector;

/**
 * <p><code>GAIGSphylogenetic_forest.java</code> provides an
 * GAIGS-renderable data structure for a forest of GAIGSphylogenetic
 * trees.  Essentially the forest is a vector of such trees with the
 * layout of individual trees scattered semi-intelligently via setting
 * the bounds of the individual trees.
 * 
 * @author Tom Naps
 * @version 10/20/2007
 */

public class GAIGSphylogenetic_forest implements GAIGSdatastr {
    
 
//---------------------- Instance Variables -------------------------------------

    /**
     * Display name.
     */
    String name;
    
    /**
     * Display color.
     */
    String color;

    Vector<GAIGSphylogenetic_tree> the_forest;

    double my_x1, my_y1, my_x2, my_y2; // My bounds to be allocated to my trees appropriately
    
    /**
     * Holds the minimum font size for this forest when drawn in the
     * new GAIGS XML format. Used to specify a font size that will be
     * readable when this the forest is drawn in a smaller portion of
     * the screen.
     */
    protected double font_size = 0.03;

    int leafCount;

//---------------------- Constructors -------------------------------------------


    /**
     * Construct a forest using all the default values.
     * 
     */
    public GAIGSphylogenetic_forest() {
        this(DEFAULT_NAME, DEFAULT_COLOR, DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE);
    }
    
    /**
     * Set all instance variables.
     * 
     * @param       name            Display name of this structure.
     * @param       color           Color for items unless locally overridden.
     * @param       x1              Left display bound.
     * @param       y1              Bottom display bound.
     * @param       x2              Top display bound.
     * @param       y2              Right display bound.
     * @param       fontSize        Font size for display.
     */


    public GAIGSphylogenetic_forest(String name, String color, double x1, double y1, double x2, double y2, double fontSize) {
        this.name = name;
        this.color = color;
	my_x1 = x1;
	my_y1 = y1;
	my_x2 = x2;
	my_y2 = y2;
        setBounds(x1,y1,x2,y2);
        setFontSize(fontSize);
    font_size =fontSize;
	the_forest = new Vector<GAIGSphylogenetic_tree>();
    }
    
    
//---------------------- Display Setter/Getter Methods ---------------------------------
    public void setLeaves(int leafCount){this.leafCount = leafCount;}

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
    
    /**
     * Sets the bounds in which this forest is to be drawn.  These
     * bounds specify the area within the normalized [0,1] space
     * within which this forest should appear in the snapshot. Used to
     * draw this forest in a portion of the viewing window so that
     * multiple structures can appear in a snapshot in the new GAIGS
     * XML format.
     *
     * @param x1 Sets the left-most bound on the horizontal axis.
     * @param y1 Sets the lower bound on the vertical axis.
     * @param x2 Sets the right-most bound on the horizontal axis.
     * @param y2 Sets the upper bound on the vertical axis.
     */
    public void setBounds(double x1, double y1, double x2, double y2){
	my_x1 = x1;
	my_y1 = y1;
	my_x2 = x2;
	my_y2 = y2;
    }

    /**
     * Sets the minimum font size to keep the fonts in this
     * <code>GAIGSphylogenetic_forest</code> readable. Used to upsize
     * the font when drawing this
     * <code>GAIGSphylogenetic_forest</code> in a portion of the
     * viewing window in the new GAIGS XML format.
     *
     * @param size Indicates the minimum font size for the fonts in
     *             this <code>GAIGSphylogenetic_forest</code>.
     */
    public void setFontSize(double size){ font_size = size; }


//---------------------- Forest-specific manipulation Methods ---------------------------------


    /**
       @param t    a tree to be removed from the forest
       @return true if t could be removed, false otherwise
     */
    public boolean remove(GAIGSphylogenetic_tree t) {
	boolean b = the_forest.remove(t);
	if (b) {
	    for (int i = 0; i < the_forest.size(); i++) {
		double width_per_tree = (my_x2 - my_x1)/the_forest.size();
		((GAIGSphylogenetic_tree)the_forest.get(i)).setBounds(my_x1+(double)i*width_per_tree,my_y1,
							 my_x1+(double)(i+1)*width_per_tree,my_y2);
	    }
	}
	return b;
    }

    /**
       @param t a tree to be added to the forest.  Since the forest is
       a vector of trees, the addition of t to the forest will occur
       as the last element of the underlying vector.

       @return true if t could be added, false otherwise
     */
    public boolean addElement(GAIGSphylogenetic_tree t) {
	boolean b = the_forest.add(t);
	    for (int i = 0; i < the_forest.size(); i++) {
		double width_per_tree = (my_x2 - my_x1)/the_forest.size();
		((GAIGSphylogenetic_tree)the_forest.get(i)).setBounds(my_x1+(double)i*width_per_tree,my_y1,
							 my_x1+(double)(i+1)*width_per_tree,my_y2);
	    }
	return b;
    }
    
    /**
       @param index    an index in the underlying vector of phylogenetic trees
       @return the tree at that index
     */
    public GAIGSphylogenetic_tree elementAt(int index) {
	return (GAIGSphylogenetic_tree)the_forest.elementAt(index);
    }
    /**
     * x/y gaps not right.
     * x gap is gap between each node. -uses the total leaves.
     * y gap scales y values for trees to prevent vertical skewing.
     cl*/
    public void setTreeBounds(){
    	double xGap = (my_x2 - my_x1)/ (leafCount-1);
//    	double xGap = (my_x2 - my_x1)/ (the_forest.size()-1)/2;
//    	double yGap = (my_y2 - my_y1)/ (the_forest.size()-1)/2 -1;
    	double yGap = (my_y2 - my_y1)/ (leafCount-1);
    	double x2, x1 = my_x1;
    	double y1, y2 = my_y2;
//    	double fontsize = .2;
    	double fontsize = font_size;
    	double relativeFontSize;
    	for (int i = 0; i < the_forest.size(); i++)
    	{
    		int lt = (the_forest.get(i).size() +1)/2;		//leaves in the tree
    		int temp = lt;
    		if (lt >= 2)
    			lt--;
    		x2 = x1 + lt * xGap;
    		
//    		fontsize = fontsize/lt;
//    		fontsize = 1.0/ (2.0 *the_forest.get(i).size());
//    		fontsize = xGap;

    		y1 = y2 - lt * yGap;
    		relativeFontSize = fontsize / (x2-x1);
//    		System.out.println(fontsize);
    		the_forest.get(i).setBounds(x1, y1, x2, y2);
    		the_forest.get(i).setFontSize(relativeFontSize);
    		x1 = x2;
    		if (temp > 1)
    			x1 += xGap;
    	}
    }
    	//set bounds of all trees in vector within the bounds of 
    	//the forest.
    
    public void colorRoot(int treeIndex, String color){
    	the_forest.get(treeIndex).getRoot().setHexColor(color);
    }
   public void prepareForRefinedTrees(){the_forest.clear();}

//---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current state of the
     * forest
     * 
     * @return     A String containing GAIGS XML code for the tree  
     */
    public String toXML(){
    	
    	setTreeBounds();
    	String xml = new String("");
    	for (int i = 0; i < the_forest.size(); i++)
    	{
    		xml = xml + the_forest.get(i).toXML();
    	}
    	return xml;	
    }
//        String xmlString = "";
//
//	// Code below was an aborted attempted to give more space,
//	// based on bounds, to bigger trees.  But the results of
//	// setting bounds are too unpredictable to make this work
//	// well.
//
//	int freq [] = new int [the_forest.size()];
//	double rel_freq [] = new double [the_forest.size()];
//	int total = 0;
//
//	for (int i = 0; i < the_forest.size(); i++) {
//	    freq[i] = ((GAIGSphylogenetic_tree)the_forest.elementAt(i)).size();
//	    total += freq[i];
//	}
//
//	rel_freq[0] = ((double) freq[0])/((double)total);
//	for (int i = 1; i < the_forest.size(); i++) {
//	    rel_freq[i] = /*rel_freq[i-1] + */((double) freq[i])/((double)total);
//	}
//
//	for (int i = 0; i < the_forest.size(); i++) {
//	    double width_per_tree = (my_x2 - my_x1)/the_forest.size();
//	    ((GAIGSphylogenetic_tree)the_forest.get(i)).setBounds(my_x1+(double)i*width_per_tree,
//								  my_y1,
//								  my_x1+(double)(i+1)*width_per_tree,
//								  my_y2 * (rel_freq[i] < 0.2 ? 0.5 : 1.0));
//// 	    ((GAIGSphylogenetic_tree)the_forest.get(i)).setBounds(my_x1 + (i == 0 ? 0.0 : rel_freq[i-1]) * (my_x2 - my_x1),
//// 								  my_y1,
//// 								  my_x1 + rel_freq[i] * (my_x2 - my_x1),
//// 								  my_y2);
//	}
//
//
//	for (int i = 0; i < the_forest.size(); i++) {
//	    ((GAIGSphylogenetic_tree)the_forest.get(i)).setFontSize(DEFAULT_FONT_SIZE*(double)the_forest.size());
//	    ((GAIGSphylogenetic_tree)the_forest.get(i)).setSpacing(1.0,2.0/(double)the_forest.size());
////  	    ((GAIGSphylogenetic_tree)the_forest.get(i)).setFontSize(DEFAULT_FONT_SIZE/(rel_freq[i] - (i == 0 ? 0.0 : rel_freq[i-1])));
//	    xmlString = xmlString + ((GAIGSphylogenetic_tree)the_forest.get(i)).toXML();
//	}
//        
//        return xmlString;
//    }
}
