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
 * <p>
 * This is the new GAIGS support class for drawing text to the visualization
 * pane. Unlike <code>GAIGSlabel</code>, which previously provided support for
 * drawing text to the screen, a <code>GAIGStext</code> object can be used to
 * precisely position the text that is to be drawn. The appearance of this text
 * is controlled by the following fields:
 * </p>
 * <pre>
 *  x        - The x-coordinate (usually within the bounds [0,1]) at which the
 *             text is to be drawn. How the text is displayed relative to this
 *             x-coordinate is controlled by the halign field.
 *
 *  y        - The y-coordinate (usually within the bounds [0,1]) at which the
 *             text is to be drawn. How the text is displayed relative to this
 *             y-coordinate is controlled by the valign field.
 *
 *  halign   - Controls how the text is drawn relative to the x-coordinate
 *             stored in x. This field can have one of three possible values:
 *               HCENTER - Centers the text horizontally about x.
 *               HLEFT   - Left-justifies the text with x as the left margin.
 *               HRIGHT  - Right-justifies the text with x as the right margin.
 *
 *  valign   - Controls how the text is drawn relative to the y-coordinate
 *             stored in y. This field can have one of three possible values:
 *               VCENTER - Centers the text vertically about y.
 *               VBOTTOM - Draws the lowest line of text at y.
 *               VTOP    - Draws the highest line of text at y.
 *
 *  fontsize - The size of the text that is to be drawn. This corresponds to
 *             the fontsize field used in the other GAIGS structures.
 *
 *  color    - The default color of the text that is to be drawn. This is
 *             similar to the coloring used in other GAIGS structures; the
 *             value is a hexadecimal RGB string of the form #000000. Note that
 *             color escapes (e.g., \#000000) can be used to change the color
 *             of text within a line. However, each new line in the text String
 *             will revert back to the default color stored in color so a color
 *             escape is required to begin a line in a different color than the
 *             color stored in the color field.
 *
 *  text     - The actual text String that is to be drawn to the screen. This
 *             String can have multiple lines, and the newlines will be 
 *             preserved when it is displayed. Note that the vertical 
 *             positioning set using valign uses all the lines of text to
 *             compute where the text is displayed. For example, a five-line
 *             String that has been vertically centered will appear with its
 *             third line positioned vertically on y.
 * </pre>
 *
 * @author Andrew Jungwirth
 * @version 1.0 (28 June 2006)
 */

public class GAIGStext implements GAIGSdatastr{
    /**
     * Value that can be assigned to <code>halign</code> to center the text
     * horizontally about the x-coordinate in <code>x</code>.
     */
    public static final int HCENTER = 0;

    /**
     * Value that can be assigned to <code>halign</code> to left-justify the
     * text horizontally at the x-coordinate in <code>x</code>.
     */
    public static final int HLEFT = 1;

    /**
     * Value that can be assigned to <code>halign</code> to right-justify the
     * text horizontally at the x-coordinate in <code>x</code>.
     */
    public static final int HRIGHT = 2;

    /**
     * Value that can be assigned to <code>valign</code> to center the text
     * vertically about the y-coordinate in <code>y</code>.
     */
    public static final int VCENTER = 0;

    /**
     * Value that can be assigned to <code>valign</code> to position the lowest
     * line of text at the y-coordinate in <code>y</code>.
     */
    public static final int VBOTTOM = 1;

    /**
     * Value that can be assigned to <code>valign</code> to position the
     * highest line of text at the y-coordinate in <code>y</code>.
     */
    public static final int VTOP = 2;

    // The x-coordinate for the text.
    private double x;

    // The y-coordinate for the text.
    private double y;

    // The horizontal alignment of the text.
    private int halign;

    // The vertical alignment of the text.
    private int valign;

    // The fontsize for the text.
    private double fontsize;

    // The default coloring for the text.
    private String color;

    // The text that is to be displayed.
    private String text;

    /**
     * Default constructor that assigns default values to all fields.
     * The fields are set as follows:
     * <pre>
     *  x = 0.5
     *  y = 0.5
     *  halign = HCENTER
     *  valign = VCENTER
     *  fontsize = 0.04
     *  color = "#000000"
     *  text = ""
     * </pre>
     */
    public GAIGStext(){
	x = 0.5;
	y = 0.5;
	halign = HCENTER;
	valign = VCENTER;
	fontsize = 0.04;
	color = "#000000";
	text = "";
    }

    /**
     * Constuctor that only sets the location of the text.
     * The remaining fields are set as follows:
     * <pre>
     *  halign = HCENTER
     *  valign = VCENTER
     *  fontsize = 0.04
     *  color = "#000000"
     *  text = ""
     * </pre>
     *
     * @param x The x-coordinate for the text (usually within [0,1]).
     * @param y The y-coordinate for the text (usually within [0,1]).
     */
    public GAIGStext(double x, double y){
	this.x = x;
	this.y = y;
	halign = HCENTER;
	valign = VCENTER;
	fontsize = 0.04;
	color = "#000000";
	text = "";
    }

    /**
     * Constructor that sets the text and its location.
     * The remaining fields are set as follows:
     * <pre>
     *  halign = HCENTER
     *  valign = VCENTER
     *  fontsize = 0.04
     *  color = "#000000"
     * </pre>
     *
     * @param x    The x-coordinate for the text (usually within [0,1]).
     * @param y    The y-coordinate for the text (usually within [0,1]).
     * @param text The text that is to be displayed on the screen.
     */
    public GAIGStext(double x, double y, String text){
	this.x = x;
	this.y = y;
	halign = HCENTER;
	valign = VCENTER;
	fontsize = 0.04;
	color = "#000000";
	this.text = text;
    }

    /**
     * Constructor that sets all fields for this <code>GAIGStext</code> object.
     *
     * @param x        The x-coordinate for the text (usually within [0,1]).
     * @param y        The y-coordinate for the text (usually within [0,1]).
     * @param halign   One of the three possible constant values used to define
     *                 the horizontal alignment of the text relative to
     *                 <code>x</code>. An invalid input defaults to
     *                 <code>HCENTER</code>.
     * @param valign   One of the three possible constant values used to define
     *                 the vertical alignment of the text relative to
     *                 <code>y</code>. An invalid input defaults to
     *                 <code>VCENTER</code>.
     * @param fontsize The size of the text that is to be displayed. Functions
     *                 similarly to other GAIGS structures.
     * @param color    The default color in which the text is to be drawn.
     * @param text     The text that is to be displayed on the screen.
     */
    public GAIGStext(double x, double y, int halign, int valign,
		     double fontsize, String color, String text){
	this.x = x;
	this.y = y;

	if(halign == HCENTER || halign == HLEFT || halign == HRIGHT)
	    this.halign = halign;
	else
	    this.halign = HCENTER;

	if(valign == VCENTER || valign == VBOTTOM || valign == VTOP)
	    this.valign = valign;
	else
	    this.valign = VCENTER;

	this.fontsize = fontsize;
	this.color = color;
	this.text = text;
    }

    //////////////////////
    // ACCESSOR METHODS //
    //////////////////////

    /**
     * Gives the value stored to <code>x</code>.
     *
     * @return The x-coordinate for this <code>GAIGStext</code> object.
     */
    public double getX(){
	return x;
    }

    /**
     * Gives the value stored to <code>y</code>.
     *
     * @return The y-coordinate for this <code>GAIGStext</code> object.
     */
    public double getY(){
	return y;
    }

    /**
     * Gives the value stored to <code>halign</code>.
     *
     * @return The horizontal alignment constant for this
     *         <code>GAIGStext</code> object.
     */
    public int getHalign(){
	return halign;
    }

    /**
     * Gives the value stored to <code>valign</code>.
     *
     * @return The vertical alignment constant for this
     *         <code>GAIGStext</code> object.
     */
    public int getValign(){
	return valign;
    }

    /**
     * Gives the value stored to <code>fontsize</code>.
     *
     * @return The <code>fontsize</code> for this <code>GAIGStext</code>
     *         object.
     */
    public double getFontsize(){
	return fontsize;
    }

    /**
     * Gives the value stored to <code>color</code>.
     *
     * @return The color <code>String</code> for this <code>GAIGStext</code>
     *         object.
     */
    public String getColor(){
	return color;
    }

    /**
     * Gives the value stored to <code>text</code>.
     *
     * @return The text <code>String</code> for this <code>GAIGStext</code>
     *         object.
     */
    public String getText(){
	return text;
    }

    /**
     * Method required to implement the <code>GAIGSdatastr</code> interface.
     * Returns the value stored in <code>text</code>. This method is identical
     * to {@link #getText() getText}.
     *
     * @return The text <code>String</code> for this <code>GAIGStext</code>
     *         object.
     */
    public String getName(){
	return text;
    }

    /////////////////////
    // MUTATOR METHODS //
    /////////////////////

    /**
     * Sets the value of <code>x</code>. This should usually be in the range
     * [0,1].
     *
     * @param x The new x-coordinate for this <code>GAIGStext</code> object.
     */
    public void setX(double x){
	this.x = x;
    }

    /**
     * Sets the value of <code>y</code>. This should usually be in the range
     * [0,1].
     *
     * @param y The new y-coordinate for this <code>GAIGStext</code> object.
     */
    public void setY(double y){
	this.y = y;
    }

    /**
     * Sets the value of <code>halign</code>. The value of <code>halign</code>
     * remains unchanged if an invalid input is given.
     *
     * @param halign The new horizontal alignment for this
     * <code>GAIGStext</code> object.
     * @return       <code>True</code> if <code>halign</code> was successfully
     *               changed; <code>false</code> otherwise.
     */
    public boolean setHalign(int halign){
	if(halign == HCENTER || halign == HLEFT || halign == HRIGHT){
	    this.halign = halign;
	    return true;
	}

	return false;
    }

    /**
     * Sets the value of <code>valign</code>. The value of <code>valign</code>
     * remains unchanged if an invalid input is given.
     *
     * @param valign The new vertical alignment for this <code>GAIGStext</code>
     *               object.
     * @return       <code>True</code> if <code>valign</code> was successfully
     *               changed; <code>false</code> otherwise.
     */
    public boolean setValign(int valign){
	if(valign == VCENTER || valign == VBOTTOM || valign == VTOP){
	    this.valign = valign;
	    return true;
	}

	return false;
    }

    /**
     * Sets the value of <code>fontsize</code>.
     *
     * @param fontsize The new <code>fontsize</code> for this
     *                 <code>GAIGStext</code> object.
     */
    public void setFontsize(double fontsize){
	this.fontsize = fontsize;
    }

    /**
     * Sets the value of <code>color</code>.
     *
     * @param color The new default color for this <code>GAIGStext</code> 
     *              object.
     */
    public void setColor(String color){
	this.color = color;
    }

    /**
     * Sets the value of <code>text</code>.
     *
     * @param text The new text <code>String</code> for this 
     *             <code>GAIGStext</code> object.
     */
    public void setText(String text){
	this.text = text;
    }

    /**
     * Method required to implement the <code>GAIGSdatastr</code> interface.
     * Sets the value of <code>text</code>. This method is identical to 
     * {@link #setText(String) setText}.
     *
     * @param text The new text <code>String</code> for this 
     *             <code>GAIGStext</code> object.
     */
    public void setName(String text){
	this.text = text;
    }

    //////////////////////
    // GAIGS XML METHOD //
    //////////////////////

    /**
     * Returns the GAIGS XML representation of this <code>GAIGStext</code>
     * object as a <code>String</code>.
     *
     * @return A <code>String</code> containing the GAIGS XML representation of
     *         this <code>GAIGStext</code> object.
     */
    public String toXML(){
	return ("<text x=\"" + x + "\" y=\"" + y + "\" halign=\"" + halign +
		"\" valign=\"" + valign + "\" fontsize=\"" + fontsize + 
		"\" color=\"" + color + "\">" + text + "</text>\n");
    }
}

