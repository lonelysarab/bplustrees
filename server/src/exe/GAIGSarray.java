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
 * <p><code>GAIGSarray</code> provides the ability to implement a one or two dimensional
 * array and also create GAIGS visualizations of its state. Use the various constructors
 * to specify the general parameters for the array visualization, and use the
 * <code>toXML</code> method to actually generate the array XML for snapshots. The display
 * mode can be as an array or as a bar graph.  In the case of a bar graph the stored items must
 * be integers (or bad things will happen!).</p>
 * 
 * <p>Unlike standard Java, where all arrays are really one dimensional arrays, a
 * <code>GAIGSarray</code> is a two dimensional array, whether treated like one or not.
 * If a 1-d array is constructed, a 2-d array is allocated with just one column.  The getter
 * and setter methods take one or two parameters, allowing the array to be treated as 1-d
 * or 2-d.  Row and column labels are also supported.</p>
 * 
 * <p>Methods are also provided to set and get the presentation color of an array cell.
 * A default array cell color can be set by using the appropriate constructor. </p>
 * 
 * @author Myles McNally 
 * @version 6/20/06
 */


public class GAIGSarray extends GAIGSbase {
    

    /**
     * Default display mode is as an array.
     */
    static final boolean DEFAULT_ISBAR = false;
    
    /**
     * Color for null values.
     */
    static final String NULL_COLOR = "#CCCCCC";
    
    //---------------------- Instance Variables -------------------------------------

    /**
     * Display mode - bar graph or as an array.
     */
    boolean isBar;
    
    /**
     * Column labels.
     */
    String[] colLabels;

    /**
     * Row labels.
     */
    String[] rowLabels;
    
    /**
     * The array of items.
     */
    Object[][] theArray;
    
    /**
     * The array of items.
     */
    String[][] theColorArray;    
    
    //---------------------- Constructors -------------------------------------------

    // ---------- One dimensional array constructors -----------

    /**
     * Create a "1-d" array of length rows, using default parameters for all other instance variables.
     * 
     * @param       rows            The length of this array.
     */
    public GAIGSarray (int rows) {
        setInstanceVariables (rows, 1, DEFAULT_ISBAR, DEFAULT_NAME, DEFAULT_COLOR,
                  DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE);
    }
    
    /**
     * Create a "1-d" array of length rows explicitly setting the display mode,
     * using default parameters for all other instance variables.
     * 
     * @param       rows            The length of this array.
     * @param       isBar           Whether is display this array as a bar graph.
     */
    public GAIGSarray (int rows, boolean isBar) {
        setInstanceVariables (rows, 1, isBar, DEFAULT_NAME, DEFAULT_COLOR,
                  DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE);
    }
    
    /**
     * Create a "1-d" array of length rows using default parameters for all other instance variables..
     * 
     * @param       rows            The length of this array.
     * @param       isBar           Whether is display this array as a bar graph.
     * @param       name            Display name of this structure.
     * @param       color           Color for items unless locally overridden.
     * @param       x1              Left display bound.
     * @param       y1              Bottom display bound.
     * @param       x2              Top display bound.
     * @param       y2              Right display bound.
     * @param       fontSize        Font size for display.
     */
    public GAIGSarray (int rows, boolean isBar, String name, String color,
                       double x1, double y1, double x2, double y2, double fontSize) {
        setInstanceVariables (rows, 1, isBar, name, color, x1, y1, x2, y2, fontSize);
    }
    

    // ---------- Two dimensional array constructors -----------
     
    /**
     * Create a "2-d" array of dimensions rows x cols, using default parameters for all other instance variables.
     * 
     * @param       rows            The number of rows in this array.
     * @param       cols            The number of columns in this array.
     */
    public GAIGSarray (int rows, int cols) {
        setInstanceVariables (rows, cols, false, DEFAULT_NAME, DEFAULT_COLOR,
                  DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE);
    }
    
        
    /**
     * Create a "2-d" array of dimensions rows x cols, setting all the instance variables.
     * 
     * @param       rows            The length of this array.
     * @param       cols            The number of columns in this array.
     * @param       name            Display name of this structure.
     * @param       color           Color for items unless locally overridden.
     * @param       x1              Left display bound.
     * @param       y1              Bottom display bound.
     * @param       x2              Top display bound.
     * @param       y2              Right display bound.
     * @param       fontSize        Font size for display.
     */
    public GAIGSarray (int rows, int cols, String name, String color,
                       double x1, double y1, double x2, double y2, double fontSize) {
        setInstanceVariables (rows, cols, false, name, color, x1, y1, x2, y2, fontSize);
    }
    
    /**
     * Helper method for constructors which explicitly sets all the instance variables
     * 
     * @param       rows            The length of this array.
     * @param       cols            The number of columns in this array.
     * @param       isBar           Whether is display this array as a bar graph.
     * @param       name            Display name of this structure.
     * @param       color           Color for items unless locally overridden.
     * @param       x1              Left display bound.
     * @param       y1              Bottom display bound.
     * @param       x2              Top display bound.
     * @param       y2              Right display bound.
     * @param       fontSize        Font size for display.
     */
    private void setInstanceVariables(int rows, int cols, boolean isBar, String name, String color,
                      double x1, double y1, double x2, double y2, double fontSize) {
        this.isBar = isBar;
        this.name = name;
        this.color = color;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.fontSize = fontSize;
        
        colLabels = new String[cols];           // init the column labels
        for (int i = 0; i < cols; i++)
            colLabels[i] = "";
            
        rowLabels = new String[rows];           // init the row labels
        for (int i = 0; i < rows; i++)
            rowLabels[i] = "";
            
        theArray = new Object[rows][cols];   // init the array itself
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                theArray[r][c] = null;

        theColorArray = new String[rows][cols];   // init the array itself
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                theColorArray[r][c] = color;
        
    }   

    
    //---------------------- Array Methods -----------------------------------

    // ---------- Item Getter Methods -----------
    
    /**
     * Return the value at location r.
     * A "1-d" method.
     * 
     * @param       r           The location to be accessed.
     * @return      The object at that location      
     */
    public Object get (int r) {
        return theArray[r][0];
    }
    
    /**
     * Return the value at location r,c.
     * A "2-d" method.
     * 
     * @param       r           The row to be accessed.
     * @param       c           The col to be accessed.
     * @return      The object at that location      
     */
    public Object get (int r, int c) {
        return theArray[r][c];
    }
    
    
    // ---------- Item Setter Methods -----------
    
    /**
     * Set location r to value item. Default color will be used
     * for this item. A "1-d" method.
     * 
     * @param       item        The value to be stored. 
     * @param       r           The location to be set.
     */
    public void set (Object item, int r) {
        theArray[r][0] = item;
    }
    
    /**
     * Set location r to value item using color cl.
     * A "1-d" method.
     * 
     * @param       item        The value to be stored. 
     * @param       r           The location to be set.
     * @param       cl          The color for the value.
     */
    public void set (Object item, int r, String cl) {
        theArray[r][0] = item;
        theColorArray[r][0] = cl;
    }

    /**
     * Set location r,c to value item. Default color will be used
     * for this item. A "2-d" method.
     * 
     * @param       item        The value to be stored. 
     * @param       r           The row location to be set.
     * @param       c           The col location to be set.
     */
    public void set (Object item, int r, int c ) {
        theArray[r][c] = item;
    }
    
    /**
     * Set location r,c to value itemusing color cl.
     * A "2-d" method.
     * 
     * @param       item        The value to be stored. 
     * @param       r           The row location to be set.
     * @param       c           The col location to be set.
     * @param       cl          The color for the value.
     */
    public void set (Object item, int r, int c, String cl) {
        theArray[r][c] = item;
        theColorArray[r][c] = cl;
    }
    
    
    // ---------- Color Getter Methods -----------
    
    /**
     * Return the color at location r.
     * A "1-d" method.
     * 
     * @param       r           The location to be accessed.
     * @return      The color at the location      
     */
    public String getColor (int r) {
        return theColorArray[r][0];
    }
    
    /**
     * Return the color at location r,c.
     * A "2-d" method.
     * 
     * @param       r           The row to be accessed.
     * @param       c           The col to be accessed.
     * @return      The color at the location      
     */
    public String getColor (int r, int c) {
        return theColorArray[r][c];
    }
    
    
    // ---------- Color Setter Methods -----------
    
    /**
     * Set the color of location r. 
     * A "1-d" method.
     * 
     * @param       r           The location to be set. 
     * @param       co          The color to be stored.
     */
    public void setColor (int r, String co) {
        theColorArray[r][0]= co;
    }
    
    /**
     * Set the color of location r,c.
     * A "2-d" method.
     * 
     * @param       r           The row location to be set.
     * @param       c           The col location to be set.
     * @param       co          The color for the value.
     */
    public void setColor (int r, int c, String co) {
        theColorArray[r][c] = co;
    }
    
    
    // ---------- Label Getter Methods -----------
    
    /**
     * Get the label of row r. The row labels are also used as the labels for
     * the bars in the bargraph representation of a one-dimensional array.
     * 
     * @param       r         The row.
     * @return      The label of the row
     */
    public String getRowLabel (int r) {
        return rowLabels[r];
    }    

    /**
     * Get the label of column c. 
     * 
     * @param       c           The column. 
     * @return      The label of the column
     */
    public String getColLabel (String label, int c) {
        return colLabels[c];
    }  
    
    
    // ---------- Label Setter Methods -----------
    
    /**
     * Set the label of row r. This method is also used to set the labels
     * for each bar in the bargraph representation of a one-dimensional array.
     * 
     * @param       label       The label.
     * @param       r           The row. 
     */
    public void setRowLabel (String label, int r) {
        rowLabels[r] = label;
    }    

    /**
     * Set the label of column c. 
     * 
     * @param       label       The label.
     * @param       c           The column. 
     */
    public void setColLabel (String label, int c) {
        colLabels[c] = label;
    }  
    
    
    //---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current state of the array.
     * Whether this is the standard array display or the bar graph display
     * depends on the value of <code>isBar</code>.
     * 
     * @return     A String containing GAIGS XML code for the array  
     */
    
    public String toXML() {
        String xmlString = "";
        
        if (isBar) {            // produce a bar graph XML representation
            
            xmlString += "<bargraph>" + "\n";
            if (name != null)
                xmlString += "<name>" + name + "</name>" + "\n";
            xmlString += "<bounds "
		+ "x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2
		+ "\" fontsize=\"" + fontSize + "\"/>" + "\n";
	    
	    for ( int r = 0; r < theArray.length; r++ ) {
		Object item = theArray[r][0];
		String color = theColorArray[r][0];
		if (item != null)
		    xmlString += "<bar magnitude=\"" + item + "\" " + 
			"color=\"" + color + "\">\n<label>" +
			rowLabels[r] + "</label>\n</bar>\n";          
	    }
	    xmlString += "</bargraph>" + "\n";
	                
        } else {                // produce an array XML representation
    
            xmlString += "<array>" + "\n";
            if (name != null)
                xmlString += "<name>" + name + "</name>" + "\n";
            xmlString += "<bounds "
                + "x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2
                + "\" fontsize=\"" + fontSize + "\"/>" + "\n";
        
            for ( int i = 0; i < theArray.length; i++ ) {       // row labels
                xmlString += "<row_label>";
                if (rowLabels[i] != null)
                    xmlString += rowLabels[i];
                xmlString += "</row_label>" + "\n";
            }
        
            for ( int i = 0; i < theArray[0].length; i++ ) {    // column labels
                xmlString += "<column_label>";
                if (colLabels[i] != null)
                    xmlString += colLabels[i];
                    xmlString += "</column_label>" + "\n";
            }
                     
            for ( int c = 0; c < theArray[0].length; c++ ) {    // items
                xmlString += "<column>"+ "\n";            
                for ( int r = 0; r < theArray.length; r++ ) {
                    Object item = theArray[r][c];
                    String color = theColorArray[r][c];
                    xmlString += "<list_item color=\"" + color + "\">" + "\n";
                    if (item != null)
                        xmlString += "<label>" + item + "</label>" + "\n";
                    else
                        xmlString += "<label>" + "null" + "</label>" + "\n";                    
                    xmlString += "</list_item>" + "\n";
                 }
                 xmlString += "</column>"+ "\n";

             }     
             xmlString += "</array>" + "\n";
        }
        
        return xmlString;
    }
     
}

