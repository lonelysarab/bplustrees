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
 * This class maintains the information for a single edge in the 
 * <code>VisualGraph</code> data structure. A <code>VisEdge</code> object 
 * contains color, weight, and position information, as well as keeping track
 * of whether it is active in a <code>VisualGraph</code> or should be hidden
 * when the <code>VisualGraph</code> is displayed.
 * <p>
 * In order to use any of these graph classes in a script-producing program,
 * the script-producing program must import the package <code>exe</code>.
 * </p>
 *
 * @author Jeff Lucas (original author)
 * @author Richard Teviotdale (GAIGS adaptations and additions)
 * @author Andrew Jungwirth (more GAIGS adaptations and Javadoc comments)
 */
public class VisEdge{
    /* DATA: */

    /**
     * Holds the hexadecimal color <code>String</code> for this 
     * <code>VisEdge</code> of the form <code>#123456</code>.
     */
    private String hexColor;

    /**
     * Stores the weight for this <code>VisEdge</code>.
     */
    private double weight;

    /**
     * Stores the Cartesian x-coordinate in [0,1] space of the starting point
     * for this <code>VisEdge</code>.
     */
    private double sx;

    /**
     * Stores the Cartesian y-coordinate in [0,1] space of the starting point
     * for this <code>VisEdge</code>.
     */
    private double sy;

    /**
     * Stores the Cartesian x-coordinate in [0,1] space of the ending point for
     * this <code>VisEdge</code>.
     */
    private double ex;

    /**
     * Stores the Cartesian y-coordinate in [0,1] space of the ending point for
     * this <code>VisNode</code>.
     */
    private double ey;

    /**
     * Maintains the status of this <code>VisEdge</code> in a 
     * <code>VisualGraph</code>. A value of <code>true</code> means this
     * edge is part of the graph and should be displayed, and 
     * <code>false</code> indicates it is not active and should not be 
     * displayed.
     */
    private boolean activated;

    /* METHODS: */

    /**
     * Constructs a new <code>VisEdge</code> with default information.
     */
    public VisEdge(){
        hexColor = "#999999";
        weight = 0.0;
        sx = 0.5;
	sy = 0.5;
        ex = 0.5;
	ey = 0.5;
        activated = false;
    }

    /**
     * Constructs a new <code>VisEdge</code> by specifying its hexadecimal 
     * color <code>String</code> and the Cartesian coordinates for its starting
     * and ending points.
     * <p>
     * All other data members are assigned default values and must be set with
     * the corresponding method calls. This newly constructed 
     * <code>VisNode</code> is inactive until the <code>activate</code> method
     * is called.
     * </p>
     *
     * @param color Gives the hexadecimal <code>String</code> of the form
     *              <code>#123456</code> that defines the color for this
     *              <code>VisEdge</code>.
     * @param sx    Indicates the Cartesian x-coordinate in [0,1] space for the
     *              starting point of this <code>VisEdge</code>.
     * @param sy    Indicates the Cartesian y-coordinate in [0,1] space for the
     *              starting point of this <code>VisEdge</code>.
     * @param ex    Specifies the Cartesian x-coordinate in [0,1] space for the
     *              ending point of this <code>VisEdge</code>.
     * @param ey    Specifies the Cartesian y-coordinate in [0,1] space for the
     *              ending point of this <code>VisEdge</code>.
     */
    public VisEdge(String color, double sx, double sy, double ex, double ey){
	hexColor = color;
	weight = 0.0;
	this.sx = sx;
	this.sy = sy;
	this.ex = ex;
	this.ey = ey;
	activated = false;
    }

    /**
     * Resets this <code>VisEdge</code>'s data members to their default values.
     */
    public void clearEdge(){
	hexColor = "#999999";
	weight = 0.0;
	sx = 0.5;
	sy = 0.5;
	ex = 0.5;
	ey = 0.5;
	activated = false;
    }

    /**
     * Gives the hexadecimal <code>String</code> that defines the color for
     * this <code>VisEdge</code>.
     *
     * @return Yields a <code>String</code> of the form <code>#123456</code>.
     */
    public String getHexColor(){ return hexColor; }

    /**
     * Returns the weight for this <code>VisEdge</code>.
     *
     * @return Gives the value stored in <code>weight</code>.
     */
    public double getWeight(){ return weight; }

    /**
     * Gives the Cartesian x-coordinate in [0,1] space of the starting point
     * for this <code>VisEdge</code>.
     *
     * @return Yields the x-coordinate for the starting point of this 
     *         <code>VisEdge</code>.
     */
    public double getSX(){ return sx; }

    /**
     * Gives the Cartesian y-coordinate in [0,1] space of the starting point
     * for this <code>VisEdge</code>.
     *
     * @return Yields the y-coordinate for the starting point of this 
     *         <code>VisEdge</code>.
     */
    public double getSY(){ return sy; }

    /**
     * Returns the Cartesian x-coordinate in [0,1] space for the ending point
     * of this <code>VisEdge</code>.
     *
     * @return Gives the x-coordinate for the ending point of this 
     *         <code>VisEdge</code>.
     */
    public double getEX(){ return ex; }

    /** 
     * Returns the Cartesian y-coordinate in [0,1] space for the ending point
     * of this <code>VisEdge</code>.
     *
     * @return Gives the y-coordinate for the ending point of this 
     *         <code>VisEdge</code>.
     */
    public double getEY(){ return ey; }

    /**
     * Gives the current activation status of this <code>VisEdge</code> in a 
     * <code>VisualGraph</code>.
     *
     * @return Yields a value of <code>true</code> if this <code>VisEdge</code>
     *         is a part of the <code>VisualGraph</code> and should be
     *         displayed or <code>false</code> if it should not be displayed.
     */
    public boolean isActivated(){ return activated; }

    /**
     * Sets the color of this <code>VisEdge</code>.
     *
     * @param c Indicates the color of this <code>VisEdge</code> when it is
     *          displayed. The value must be a six-digit hexadecimal color
     *          <code>String</code> of the form <code>#123456</code>.
     */
    public void setHexColor(String c){
	if(Pattern.compile("#[0-9a-fA-F]{6}").matcher(c).matches()){
	    hexColor = c;
	}
    }

    /**
     * Assigns a weight to this <code>VisEdge</code>.
     *
     * @param my_w Indicates the weight for this <code>VisEdge</code>.
     */
    public void setWeight(double my_w){ weight = my_w; } 
    
    /**
     * Assigns the Cartesian x-coordinate in [0,1] space for the starting point
     * of this <code>VisEdge</code>.
     *
     * @param m_sx Indicates the new x-coordinate for the starting point of
     *             this <code>VisEdge</code>.
     */
    public void setSX(double m_sx){ sx = m_sx; }

    /** 
     * Assigns the Cartesian y-coordinate in [0,1] space for the starting point
     * of this <code>VisEdge</code>.
     *
     * @param m_sy Indicates the new y-coordinate for the starting point of
     *             this <code>VisEdge</code>.
     */
    public void setSY(double m_sy){ sy = m_sy; }

    /**
     * Sets the Cartesian x-coordinate in [0,1] space for the ending point of
     * this <code>VisEdge</code>.
     *
     * @param m_ex Specifies the new x-coordinate for the ending point of this
     *             <code>VisEdge</code>.
     */
    public void setEX(double m_ex){ ex = m_ex; }

    /**
     * Sets the Cartesian y-coordinate in [0,1] space for the ending point of
     * this <code>VisEdge</code>.
     *
     * @param m_ey Specifies the new y-coordinate for the ending point of this
     *             <code>VisEdge</code>.
     */
    public void setEY(double m_ey){ ey = m_ey; }

    /**
     * Sets this <code>VisEdge</code> to active, meaning that it will be 
     * displayed when the <code>VisualGraph</code> to which it belongs is
     * displayed.
     */
    public void activate(){ activated = true; }

    /**
     * Sets this <code>VisEdge</code> to inactive, meaning that it will not be
     * displayed when the <code>VisualGraph</code> to which it belongs is 
     * displayed. 
     */
    public void deactivate(){ activated = false; }
}

