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

import exe.*;

/**
 * <p>
 * This class provides support for a color legend structure that can be used to
 * show the meanings of the various colors used in GAIGS visualizations. The
 * class is implemented similarly to the <code>GAIGSarray</code> class and uses
 * a two-dimensional array of <code>GAIGSItem</code> objects to store the color
 * and text information for each element in the color key. Each element in the
 * color key is displayed as a box containing the color for that element with
 * the text for that element appearing to the right of the box. Multiple lines
 * are allowed in the text that is displayed, but using too many lines for the
 * labels may force the text to shrink to fit into the available space. Note
 * also that, since the legend structure will likely be displayed in a small
 * portion of the screen, the <code>fontsize</code> will usually have to be
 * increased to keep the labels readable when the structure is localized to its
 * small subset of the screen's area. Each <code>GAIGSItem</code> element in
 * the key is initialized to <code>null</code> so any element that is not set
 * with a value will not be displayed when the key is shown on the screen.
 * </p>
 *
 * @author Andrew Jungwirth
 * @version 1.0 (3 July 2006)
 */

public class GAIGSlegend extends GAIGSbase{
    // The two-dimensional array of color-text pairs that stores the legend.
    private GAIGSItem[][] legend;

    // The number of rows in legend.
    private int rows;

    // The number of columns in the legend.
    private int columns;

    // The font size for the name for this legend.
    double name_fontsize;

    // The value to determine whether we draw the box for the legend
    boolean draw_box;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    /**
     * Constructor that sets only the number of elements in the key and uses
     * default values for all other fields.
     *
     * @param rows    The number of rows in the color key.
     * @param columns The number of columns in the color key.
     */
    public GAIGSlegend(int rows, int columns){
	setVariables(rows, columns, DEFAULT_NAME, DEFAULT_X1, DEFAULT_Y1,
		     DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE, true);
    }

    /**
     * Constructor that sets the number of elements in the key and the key's
     * name and uses default values for all other fields.
     *
     * @param rows    The number of rows in the color key.
     * @param columns The number of columns in the color key.
     * @param name    The name of the color key.
     */
    public GAIGSlegend(int rows, int columns, String name){
	setVariables(rows, columns, name, DEFAULT_X1, DEFAULT_Y1,
		     DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE, true);
    }

    /**
     * Constructor that sets the number of elements in the key and the key's
     * font size and uses default values for all other fields.
     *
     * @param rows     The number of rows in the color key.
     * @param columns  The number of columns in the color key.
     * @param fontsize The font size for the text in this legend.
     */
    public GAIGSlegend(int rows, int columns, double fontsize){
	setVariables(rows, columns, DEFAULT_NAME, DEFAULT_X1, DEFAULT_Y1,
		     DEFAULT_X2, DEFAULT_Y2, fontsize, true);
    }

    /**
     * Constructor that sets the number of elements in the key and the key's
     * name and font size and uses default values for the bounds.
     *
     * @param rows     The number of rows in the color key.
     * @param columns  The number of columns in the color key.
     * @param name     The name of the color key.
     * @param fontsize The font size for the text in this legend.
     */
    public GAIGSlegend(int rows, int columns, String name, double fontsize){
	setVariables(rows, columns, name, DEFAULT_X1, DEFAULT_Y1,
		     DEFAULT_X2, DEFAULT_Y2, fontsize, true);
    }

    /**
     * Constructor that sets the number of elements in the key and the bounds
     * for the key and uses default values for all other fields.
     *
     * @param rows    The number of rows in the color key.
     * @param columns The number of columns in the color key.
     * @param x1      The left boundary of the legend (usually in [0,1]).
     * @param y1      The lower boundary of the legend (usually in [0,1]).
     * @param x2      The right boundary of the legend (usually in [0,1]).
     * @param y2      The upper boundary of the legend (usually in [0,1]).
     */
    public GAIGSlegend(int rows, int columns, double x1, double y1,
		       double x2, double y2){
	setVariables(rows, columns, DEFAULT_NAME, x1, y1, x2, y2,
		     DEFAULT_FONT_SIZE, true);
    }

    /**
     * Constructor that explicitly sets all the values for this legend.
     *
     * @param rows     The number of rows in the color key.
     * @param columns  The number of columns in the color key.
     * @param name     The name of the color key.
     * @param x1       The left boundary of the legend (usually in [0,1]).
     * @param y1       The lower boundary of the legend (usually in [0,1]).
     * @param x2       The right boundary of the legend (usually in [0,1]).
     * @param y2       The upper boundary of the legend (usually in [0,1]).
     * @param fontsize The font size for the text in this legend.
     */
    public GAIGSlegend(int rows, int columns, String name,
		       double x1, double y1, double x2, double y2,
		       double fontsize){
	setVariables(rows, columns, name, x1, y1, x2, y2, fontsize, true);
    }

    // Helper method used by constructors to set variables used by this object.
    private void setVariables(int rows, int columns, String name,
			      double x1, double y1, double x2, double y2,
			      double fontsize, boolean draw){
      legend = new GAIGSItem[rows][columns];
      for(int r = 0; r < rows; r++){
        for(int c = 0; c < columns; c++){
          legend[r][c] = null;
        }
      }

      this.rows = rows;
      this.columns = columns;
      this.name = name;
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      name_fontsize = fontsize;
      fontSize = fontsize;
      draw_box = draw;
    }

    //////////////////////
    // ACCESSOR METHODS //
    //////////////////////

    /**
     * Returns the item from this legend at the index specified.
     *
     * @param row    The row index of the <code>GAIGSItem</code> to be
     *               returned.
     * @param column The column index of the <code>GAIGSItem</code> to be
     *               returned.
     * @return       The <code>GAIGSItem</code> at index
     *               <code>[row][column]</code> in the internal array that
     *               represents this <code>GAIGSlegend</code>. If this index
     *               is out of the bounds given to the array when this
     *               <code>GAIGSlegend</code> was created, then
     *               <code>null</code> is returned.
     */
    public GAIGSItem getItem(int row, int column){
	if(row < 0 || row >= rows || column < 0 || column >= columns){
	    return null;
	}else{
	    return legend[row][column];
	}
    }

    /**
     * Gives the number of rows in this legend structure.
     *
     * @return The number of rows allocated for this legend.
     */
    public int getRows(){
	return rows;
    }

    /**
     * Gives the number of columns in this legend structure.
     *
     * @return The number of columns allocated for this legend.
     */
    public int getColumns(){
	return columns;
    }

    /**
     * Returns the name of this legend structure.
     *
     * @return The name for this <code>GAIGSlegend</code> object.
     */
    public String getName(){
	return name;
    }

    /**
     * Returns the font size for this legend's name (can be sized independently
     * from the text size used for labels within the legend).
     *
     * @return The font size used for the name for this
     *         <code>GAIGSlegend</code> object.
     */
    public double getNameFontSize(){
	return name_fontsize;
    }

    /**
     * Returns the left x-bound of this legend.
     *
     * @return The left x-coordinate of the bounds for this
     *         <code>GAIGSlegend</code> object.
     */
    public double getX1(){
	return x1;
    }

    /**
     * Returns the lower y-bound of this legend.
     *
     * @return The lower y-coordinate of the bounds for this
     *         <code>GAIGSlegend</code> object.
     */
    public double getY1(){
	return y1;
    }

    /**
     * Returns the right x-bound of this legend.
     *
     * @return The right x-coordinate of the bounds for this
     *         <code>GAIGSlegend</code> object.
     */
    public double getX2(){
	return x2;
    }

    /**
     * Returns the upper y-bound of this legend.
     *
     * @return The upper y-coordinate of the bounds for this
     *         <code>GAIGSlegend</code> object.
     */
    public double getY2(){
	return y2;
    }

    /**
     * Gives the font size for the text labels in this legend.
     *
     * @return The font size for the labels next to the boxes in this
     *         <GAIGSlegend</code> object.
     */
    public double getFontSize(){
	return fontSize;
    }

    /////////////////////
    // MUTATOR METHODS //
    /////////////////////

    /**
     * Sets the item in this legend at the index specified.
     *
     * @param row    The row index of the <code>GAIGSItem</code> to be set.
     * @param column The column index of the <code>GAIGSItem</code> to be set.
     * @param item   The <code>GAIGSItem</code> to be stored at the index
     *               <code>[row][column]</code> in the internal array that
     *               represents this <code>GAIGSlegend</code>. If this index is
     *               out of the bounds that were given to this array when this
     *               <code>GAIGSlegend</code> was created, then this method
     *               returns without doing anything.
     */
    public void setItem(int row, int column, GAIGSItem item){
	if(row < 0 || row >= rows || column < 0 || column >= columns){
	    return;
	}else{
	    legend[row][column] = item;
	}
    }

    /**
     * Sets the name for this legend.
     *
     * @param name The new name <code>String</code> for this
     *             <code>GAIGSlegend</code> object.
     */
    public void setName(String name){
	this.name = name;
    }

    /**
     * Sets the font size used to display this legend's name (can be sized
     * independently from the text size used for labels within the legend).
     * The name's font size is initialized to the font size for the labels
     * within the legend by the constructor and can only be changed by calling
     * this method.
     *
     * @param name_fontsize The new font size for the name of this
     *                      <code>GAIGSlegend</code> object.
     */
    public void setNameFontSize(double name_fontsize){
	this.name_fontsize = name_fontsize;
    }

    /**
     * Sets the bounds for this <code>GAIGSlegend</code> object.
     *
     * @param x1 The new left x-coordinate for the bounds of this legend.
     * @param y1 The new lower y-coordinate for the bounds of this legend.
     * @param x2 The new right x-coordinate for the bounds of this legend.
     * @param y2 The new upper y-coordinate for the bounds of this legend.
     */
    public void setBounds(double x1, double y1, double x2, double y2){
	this.x1 = x1;
	this.y1 = y1;
	this.x2 = x2;
	this.y2 = y2;
    }

    /**
     * Sets the font size used for the text labels in this
     * <code>GAIGSlegend</code>.
     *
     * @param fontsize The new font size for labels in this legend.
     */
    public void setFontSize(double fontsize){
	fontSize = fontsize;
    }

    /**
     * Enables the drawing of the surrounding box for the legend
    */
    public void enableBox() {
      draw_box = true;
    }

     /**
      * Disables the drawing of the surroudning box for the legend
     */
     public void disableBox() {
       draw_box = false;
     }

    //////////////////////
    // GAIGS XML METHOD //
    //////////////////////

    /**
     * Returns the GAIGS XML representation of this <code>GAIGSlegend</code>
     * object as a <code>String</code>.
     *
     * @return A <code>String</code> containing the GAIGS XML representation of
     *         this <code>GAIGSlegend</code> object.
     */
    public String toXML(){
	String returnString = "<legend>\n";

	if(name != null){
	    returnString += "<name fontsize=\"" + name_fontsize + "\">" +
		name + "</name>\n";
	}

	returnString += "<bounds x1=\"" + x1 + "\" y1=\"" + y1 +"\" x2=\"" +
	    x2 + "\" y2=\"" + y2 + "\" fontsize=\"" + fontSize + "\"" + " drawbox=\"" + draw_box + "\"" + "/>\n";

	for(int c = 0; c < columns; c++){
	    returnString += "<column>\n";

	    for(int r = 0; r < rows; r++){
		if(legend[r][c] != null){
		    returnString += "<list_item color=\"" +
			legend[r][c].color + "\">\n<label>" +
			(String)legend[r][c].value +
			"</label>\n</list_item>\n";
		}else{
		    returnString += "<list_item color=\"#FFFFFF\">\n<label>nullandvoid</label>\n</list_item>\n";
		}
	    }

	    returnString += "</column>\n";
	}

	returnString += "</legend>\n";

	return returnString;
    }
}

