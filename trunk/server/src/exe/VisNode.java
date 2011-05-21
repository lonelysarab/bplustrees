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

import java.util.regex.Pattern;

/**
 * This class maintains the information for a single node in the 
 * <code>VisualGraph</code> data structure. A <code>VisNode</code> object 
 * contains label, color, and position information, as well as other data
 * members that might be useful to a node in a graph.
 * <p>
 * In order to use any of these graph classes in a script-producing program,
 * the script-producing program must import the package <code>exe</code>.
 * </p>
 *
 * @author Jeff Lucas (original author)
 * @author Richard Teviotdale (GAIGS adaptations and additions)
 * @author Andrew Jungwirth (more GAIGS adaptations and Javadoc comments)
 */
public class VisNode implements Comparable{
    /* DATA: */

    /**
     * Stores this <code>VisNode</code>'s unique identifier and label.
     */
    protected char cindex;

    /**
     * Holds the hexadecimal color <code>String</code> for this 
     * <code>VisNode</code> of the form <code>#123456</code>.
     */
    protected String hexColor;

    /**
     * Contains the length of the current shortest path to this 
     * <code>VisNode</code>. This is used in graph-searching algorithms.
     */
    protected int cost;

    /**
     * Stores the heuristic value for this <code>VisNode</code>. This is used
     * in informed graph searches.
     */
    protected int heuristic;

    /**
     * Tracks if this <code>VisNode</code> is on the closed list in a 
     * graph-searching algorithm.
     */
    protected boolean closed;

    /**
     * Stores the name of the <code>VisNode</code> that is the predecessor of
     * this <code>VisNode</code> on the current shortest path to this
     * <code>VisNode</code>. This is used in graph-searching algorithms.
     */
    protected char pred;

    /**
     * Contains the Cartesian x-coordinate of the center of this 
     * <code>VisNode</code> in [0,1] space.
     */
    protected double x;

    /**
     * Contains the Cartesian y-coordinate of the center of this
     * <code>VisNode</code> in [0,1] space.
     */
    protected double y;

    /**
     * Maintains the status of this <code>VisNode</code> in a 
     * <code>VisualGraph</code>. A value of <code>true</code> means this
     * node is part of the graph and should be displayed, and 
     * <code>false</code> indicates it is not active and should not be 
     * displayed.
     */
    protected boolean activated;

    /* METHODS: */

    /**
     * Constructs a new <code>VisNode</code> with default information.
     */
    public VisNode(){
	cindex = 'A';
	hexColor = "#FFFFFF";
	cost = 10000;
	heuristic = 0;
	closed = false;
	pred = '~';
	x = 0.5;
	y = 0.5;
	activated = false;
    }

    /**
     * Constructs a new <code>VisNode</code> by specifying its unique 
     * name/label, hexadecimal color <code>String</code>, and Cartesian
     * coordinates.
     * <p>
     * All other data members are assigned default values and must be set with
     * the corresponding method calls. This newly constructed 
     * <code>VisNode</code> is inactive until the <code>activate</code> method
     * is called.
     * </p>
     *
     * @param label Indicates the unique name/label for this
     *              <code>VisNode</code>.
     * @param color Gives the hexadecimal <code>String</code> of the form
     *              <code>#123456</code> that defines the color for this
     *              <code>VisNode</code>.
     * @param x     Specifies the Cartesian x-coordinate in [0,1] space for the
     *              center of this <code>VisNode</code>.
     * @param y     Specifies the Cartesian y-coordinate in [0,1] space for the
     *              center of this <code>VisNode</code>.
     */
    public VisNode(char label, String color, double x, double y){
	cindex = label;
	hexColor = color;
	this.x = x;
	this.y = y;
	cost = 10000;
	heuristic = 0;
	closed = false;
	pred = '~';
	activated = false;
    }

    /**
     * Constructs a new <code>VisNode</code> that is a copy of 
     * <code>copy</code>.
     * 
     * @param copy Gives a <code>VisNode</code> containing information with
     *             which this <code>VisNode</code> should be initialized.
     */
    public VisNode(VisNode copy){
	cindex = copy.getChar();
	hexColor = copy.hexColor;
	cost = copy.getCost();
	heuristic = copy.getHeuristic();
	closed = copy.isClosed();
	pred = copy.getPred();
	x = copy.getX();
	y = copy.getY();
	activated = copy.isActivated();
    }


    /**
     * Resets this <code>VisNode</code>'s data members to their default values.
     */     
    public void clearNode(){
	cindex = 'A';
	hexColor = "#FFFFFF";
	cost = 10000;
	heuristic = 0;
	closed = false;
	pred = '~';
	x = 0.5;
	y = 0.5;
	activated = false;
    }

    /**
     * Returns the name/label for this <code>VisNode</code>.
     *
     * @return Gives the value stored in <code>cindex</code>, the unique
     *         label for this <code>VisNode</code>.
     */
    public char getChar(){ return cindex; }

    /**
     * Gives the hexadecimal <code>String</code> that defines the color for
     * this <code>VisNode</code>.
     *
     * @return Yields a <code>String</code> of the form <code>#123456</code>.
     */
    public String getHexColor(){ return hexColor; }

    /**
     * Returns the current shortest cost to this <code>VisNode</code>.
     *
     * @return Gives the value stored in <code>cost</code>.
     */
    public int getCost(){ return cost; }

    /**
     * Returns the heuristic value for this <code>VisNode</code>.
     *
     * @return Yields the value stored in <code>heuristic</code>.
     */
    public int getHeuristic(){ return heuristic; }

    /**
     * Gives the closed status of this <code>VisNode</code>.
     *
     * @return Yields <code>true</code> if this <code>VisNode</code> is on the
     *         closed list in a graph search or <code>false</code> if it is not
     *         on the closed list.
     */
    public boolean isClosed(){ return closed; }

    /**
     * Returns the name of the predecessor on the shortest path to this
     * <code>VisNode</code>.
     *
     * @return Gives the value stored in <code>pred</code>.
     */
    public char getPred(){ return pred; }

    /**
     * Returns the Cartesian x-coordinate in [0,1] space for the center of this
     * <code>VisNode</code>.
     *
     * @return Gives the value stored in <code>x</code>.
     */
    public double getX(){ return x; }

    /**
     * Returns the Cartesian y-coordinate in [0,1] space for the center of this
     * <code>VisNode</code>.
     *
     * @return Gives the value stored in <code>y</code>.
     */
    public double getY(){ return y; }

    /**
     * Gives the current activation status of this <code>VisNode</code> in a 
     * <code>VisualGraph</code>.
     *
     * @return Yields a value of <code>true</code> if this <code>VisNode</code>
     *         is a part of the <code>VisualGraph</code> and should be
     *         displayed or <code>false</code> if it should not be displayed.
     */
    public boolean isActivated(){ return activated; }

    /**
     * Assigns the name/label for this <code>VisNode</code>.
     *
     * @param my_c Specifies the name/label that is to appear within this 
     *             <code>VisNode</code> when it is displayed. This value is 
     *             assigned to <code>cindex</code>.
     */
    public void setChar(char my_c){ cindex = my_c; }

    /**
     * Sets the color that is displayed within this <code>VisNode</code>.
     *
     * @param c Indicates the color that appears within this 
     *          <code>VisNode</code> when it is displayed. The value must be a
     *          six-digit hexadecimal color <code>String</code> of the form
     *          <code>#123456</code>.
     */
    public void setHexColor(String c){
	if(Pattern.compile("#[0-9a-fA-F]{6}").matcher(c).matches()){
	    hexColor = c;
	}
    }
    
    /**
     * Sets the length of the current shortest path to this 
     * <code>VisNode</code>.
     *
     * @param newCost Specifies the current lowest cost to reach this 
     *                <code>VisNode</code> in a <code>VisualGraph</code>.
     */
    public void setCost(int newCost){ cost = newCost; }

    /**
     * Assigns the heuristic value for this <code>VisNode</code>.
     * 
     * @param newHeuristic Indicates the heuristic value for this
     *                     <code>VisNode</code> in an informed graph search.
     */
    public void setHeuristic(int newHeuristic){ heuristic = newHeuristic; }

    /**
     * Sets closed status of this <code>VisNode</code> in a graph-searching
     * algorithm.
     *
     * @param newClosed Gives a value that indicates if this 
     *                  <code>VisNode</code> is on the closed list; 
     *                  <code>true</code> means it appears on the closed list
     *                  while <code>false</code> indicates that it does not.
     */
    public void setClosed(boolean newClosed){ closed = newClosed; }

    /**
     * Assigns the name of the predecessor <code>VisNode</code> to this 
     * <code>VisNode</code> on the current lowest-cost path.
     *
     * @param pred Indicates the name/label of the <code>VisNode</code> that is
     *             the predecessor to this <code>VisNode</code> along its 
     *             current shortest path. This value is useful for retracing
     *             the path to this <code>VisNode</code> in a graph-searching
     *             algorithm.
     */
    public void setPred(char pred){ this.pred = pred; }

    /**
     * Changes the Cartesian x-coordinate in [0,1] space for the center of this
     * <code>VisNode</code>.
     *
     * @param my_x Specifies the new x-coordinate for this 
     *             <code>VisNode</code>.
     */
    public void setX(double my_x){ 
        x = my_x; 
    }

    /**
     * Changes the Cartesian y-coordinate in [0,1] space for the center of this
     * <code>VisNode</code>.
     *
     * @param my_y Specifies the new y-coordinate for this 
     *             <code>VisNode</code>.
     */
    public void setY(double my_y){ 
        y = my_y; 
    }

    /**
     * Changes the Cartesian x-coordinate in [0,1] space for the center of this
     * <code>VisNode</code>, ensuring that the new x-coordinate is in the range
     * [0,1].
     *
     * @param my_x Specifies the new x-coordinate for this 
     *             <code>VisNode</code>. If the value is less than 0, the
     *             x-coordinate is set as 0, and, if the value is greater than
     *             1, the x-coordinate is set as 1.
     */
    public void setLimitedX(double my_x){ 
	if(my_x < 0.0){
	    x = 0.0;
	}else if(my_x > 1.0){
	    x = 1.0;
	}else{
	    x = my_x;
	}
    }

    /**
     * Changes the Cartesian y-coordinate in [0,1] space for the center of this
     * <code>VisNode</code>, ensuring that the new y-coordinate is in the range
     * [0,1].
     *
     * @param my_y Specifies the new y-coordinate for this 
     *             <code>VisNode</code>. If the value is less than 0, the
     *             y-coordinate is set as 0, and, if the value is greater than
     *             1, the y-coordinate is set as 1.
     */
    public void setLimitedY(double my_y){ 
	if(my_y < 0.0){
	    y = 0.0;
	}else if(my_y > 1.0){
	    y = 1.0;
	}else{
	    y = my_y;
	}
    }

    /**
     * Sets this <code>VisNode</code> to active, meaning that it will be 
     * displayed when the <code>VisualGraph</code> to which it belongs is
     * displayed.
     */
    public void activate(){ activated = true; }

    /**
     * Sets this <code>VisNode</code> to inactive, meaning that it will not be
     * displayed when the <code>VisualGraph</code> to which it belongs is 
     * displayed. 
     */
    public void deactivate(){ activated = false; }

    /**
     * Compares two <code>VisNode</code> objects.
     * The two objects are compared using the sums of their <code>cost</code>
     * and <code>heuristic</code> values. If one of them has a lower sum of
     * these two values, it is considered less than the other. When these sums
     * are equal, if one of the objects has a <code>heuristic</code> value of 0
     * (and the other has a value not equal to 0), it is considered less than
     * the other. If the least of the two <code>VisNode</code>s still has not
     * been found at this point, they are compared using the alphabetical order
     * of their <code>cindex</code> values (their names/labels). If, at this 
     * point, their <code>cindex</code> values are identical, both objects are 
     * considered equal by this method.
     * <p>
     * This method is ideal as a comparison function for use when inserting
     * <code>VisNode</code> objects into an open priority queue in 
     * graph-searching algorithms. Note that this comparison method still works
     * for algorithms that do not use a heuristic because, in these cases, the
     * value of each object's <code>heuristic</code> data member will be the
     * default of 0 and will not affect the comparison.
     *
     * @param compare Indicates the <code>VisNode</code> to which this
     *                <code>VisNode</code> is to be compared.
     * @return        Gives a value of <code>-1</code> if the calling object is
     *                less than <code>compare</code>, <code>0</code> if the two
     *                objects are equal, and <code>1</code> if the calling
     *                object is greater than <code>compare</code>.
     */
    public int compareTo(Object compare){
	VisNode node1 = (VisNode) this;
	VisNode node2 = (VisNode) compare;

	int h1 = node1.getHeuristic();
	int h2 = node2.getHeuristic();
	int cost1 = node1.getCost() + h1;
	int cost2 = node2.getCost() + h2;

	if(cost1 < cost2){
	    return -1;
	}else if(cost1 == cost2){
	    if(node1.getHeuristic() == 0 && node2.getHeuristic() != 0){
		return -1;
	    }else if(node2.getHeuristic() == 0 && node1.getHeuristic() != 0){
		return 1;
	    }else if(node1.getChar() < node2.getChar()){
		return -1;
	    }else if(node2.getChar() < node1.getChar()){
		return 1;
	    }else{
		return 0;
	    }
	}else{
	    return 1;
	}
    }

    /**
     * Compares two <code>VisNode</code> objects.
     * The two objects are compared using only their <code>heuristic</code>
     * values. The node with the lower <code>heuristic</code> value is
     * considered less than the other. When the nodes have the same 
     * <code>heuristic</code> values, they are compared using the alphabetical
     * order of their <code>cindex</code> values (their names/labels). If, at
     * this point, their <code>cindex</code> values are identical, both objects
     * are considered equal by this method.
     * <p>
     *
     * @param compare Indicates the <code>VisNode</code> to which this
     *                <code>VisNode</code> is to be compared.
     * @return        Gives a value of <code>-1</code> if the calling object is
     *                less than <code>compare</code>, <code>0</code> if the two
     *                objects are equal, and <code>1</code> if the calling
     *                object is greater than <code>compare</code>.
     */
    public int compareToNoCost(Object compare){
	VisNode node1 = (VisNode) this;
	VisNode node2 = (VisNode) compare;

	int h1 = node1.getHeuristic();
	int h2 = node2.getHeuristic();

	if(h1 < h2){
	    return -1;
	}else if(h1 == h2){
	    if(node1.getChar() < node2.getChar()){
		return -1;
	    }else if(node2.getChar() < node1.getChar()){
		return 1;
	    }else{
		return 0;
	    }
	}else{
	    return 1;
	}
    }
}

