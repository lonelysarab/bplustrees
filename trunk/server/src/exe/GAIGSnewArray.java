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
 * <p><code>GAIGSnewArray</code> provides the ability to implement a one or two dimensional
 * array and also create GAIGS visualizations of its state. Use the various constructors
 * to specify the general parameters for the array visualization, and use the
 * <code>toXML</code> method to actually generate the array XML for snapshots. The display
 * mode can be as an array or as a bar graph.  In the case of a bar graph the stored items must
 * be integers (or bad things will happen!).</p>
 *
 * <p>Unlike standard Java, where all arrays are really one dimensional arrays, a
 * <code>GAIGSnewArray</code> is a two dimensional array, whether treated like one or not.
 * If a 1-d array is constructed, a 2-d array is allocated with just one column.  The getter
 * and setter methods take one or two parameters, allowing the array to be treated as 1-d
 * or 2-d.  Row and column labels are also supported.</p>
 *
 * <p>Methods are also provided to set and get the presentation color of an array cell.
 * A default array cell color can be set by using the appropriate constructor. </p>
 *
 * @author Shawn Recker
 * @version 6/6/2010
 */


public class GAIGSnewArray extends GAIGSbase {

  /**
      * Standard box width
  */
      static final double STD_WIDTH = .12;
  /**
      * Standard box height
  */
      static final double STD_HEIGHT = .07;
  /**
      * Adjustment factor
  */
      static final double EPSILON = .005;
  /**
      * Default label font size
  */
      static final double LABEL_FONT_SIZE = .043;
  /**
      * Width of the character
  */
      static final double CHAR_SIZE = LABEL_FONT_SIZE / 2;

  /**
      * Default Title font size
  */
      static final double TITLE_FONT_SIZE = .045;

  protected class BoxPair {
    public double width;
    public double height;
    public double font;

    public BoxPair() {
      width = STD_WIDTH;
      height = STD_HEIGHT;
      font = FONT_SIZE;
    }
  }

    /**
        * Color for null values.
    */
        static final String NULL_COLOR = "#CCCCCC";

    /**
        * Default Font Size
    */
        static final double FONT_SIZE = 0.043;
    /**
        * Number of characters before resizing box
    */
        static final int LINE_LENGTH = (int)Math.ceil(STD_WIDTH / (FONT_SIZE / 2));
    /**
        * Default Line Width for Boxes
    */
        static final int LINE_THICKNESS = 12;

    //---------------------- Instance Variables -------------------------------------

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

    /**
     * The array of box widths and heights
    */
     BoxPair[][] deltaArray;

    /**
     * The row count for the array
    */
    int rows;

    /**
     * The column count for the array
    */
    int cols;

    //---------------------- Constructors -------------------------------------------

    // ---------- One dimensional array constructors -----------

    /**
     * Create a "1-d" array of length rows, using default parameters for all other instance variables.
     *
     * @param       rows            The length of this array.
     */
    public GAIGSnewArray (int rows) {
        setInstanceVariables (rows, 1, DEFAULT_NAME, NULL_COLOR,
                  DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE);
    }

    /**
     * Create a "1-d" array of length rows using default parameters for all other instance variables..
     *
     * @param       rows            The length of this array.
     * @param       name            Display name of this structure.
     * @param       color           Color for items unless locally overridden.
     * @param       x1              Left display bound.
     * @param       y1              Bottom display bound.
     * @param       x2              Top display bound.
     * @param       y2              Right display bound.
     * @param       fontSize        Font size for display.
     */
    public GAIGSnewArray (int rows, String name, String color,
                       double x1, double y1, double x2, double y2, double fontSize) {
        setInstanceVariables (rows, 1, name, color, x1, y1, x2, y2, fontSize);
    }


    // ---------- Two dimensional array constructors -----------

    /**
     * Create a "2-d" array of dimensions rows x cols, using default parameters for all other instance variables.
     *
     * @param       rows            The number of rows in this array.
     * @param       cols            The number of columns in this array.
     */
    public GAIGSnewArray (int rows, int cols) {
        setInstanceVariables (rows, cols, DEFAULT_NAME, NULL_COLOR,
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
    public GAIGSnewArray (int rows, int cols, String name, String color,
                       double x1, double y1, double x2, double y2) {
        setInstanceVariables (rows, cols, name, color, x1, y1, x2, y2, DEFAULT_FONT_SIZE);
    }

    /**
     * Helper method for constructors which explicitly sets all the instance variables
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
    private void setInstanceVariables(int rows, int cols, String name, String color,
                      double x1, double y1, double x2, double y2, double fontSize) {
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

        deltaArray = new BoxPair[rows][cols];
        for(int r = 0; r < rows; ++r) {
          for(int c = 0; c < cols; ++c) {
            deltaArray[r][c] = new BoxPair();
          }
        }

        this.rows = rows;
        this.cols = cols;

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
        resizeCol(0);
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
        resizeCol(0);
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
        resizeCol(0);
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
        resizeCol(0);
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
        resizeColForLabel(c);
    }

    //---------------------- Corner Point Methods ---------------------------------
    /**
        * Get the corner points making up the box around the element
        *
        * @param       re      The row element.
        * @param       ce      The column element.
        * @return An array containing the corner points as x,y pairs.
        * First pair is the bottom left, then bottom right, then top right, then top left
    */
    public double[] getCornerPoints(int re, int ce) {
      if(!outOfBounds()) {
        double xstart = x2 - STD_WIDTH; //right hand side for the moment
        double ystart = y1;
        double xupdate = 0;
        double [] ret = new double[8];

        for(int c = cols - 1; c >= 0; --c) {
          for(int r = rows - 1; r >= 0; --r) {
            xupdate = deltaArray[r][(c == 0 ? 0 : c - 1)].width;
            double [] x = new double[]{xstart, deltaArray[r][c].width + xstart, deltaArray[r][c].width + xstart, xstart};
            double [] y = new double[]{ystart, ystart, deltaArray[r][c].height + ystart, deltaArray[r][c].height + ystart};

            if(r == re && c == ce) {
              ret[0] = x[0]; ret[1] = y[0];
              ret[2] = x[1]; ret[3] = y[1];
              ret[4] = x[2]; ret[5] = y[2];
              ret[6] = x[3]; ret[7] = y[3];
            }

            ystart += deltaArray[r][c].height;
          }
          ystart = y1;
          xstart -= xupdate;
        }
        return ret;

      } else {
        double xout = computeOutX();
        double yout = computeOutY();

        double dx = x2 - x1;
        double dxi = x1 - xout;
        double xratio = xout < x1 ? dx / (dx + dxi) : 1;

        double dy = y2 - y1;
        double dyi = yout - y2;
        double yratio = yout > y2 ? dy / (dyi + dy) : 1;

        double xstart = x2 - deltaArray[rows-1][cols-1].width; //right hand side for the moment
        xstart = (xstart - xout) * xratio + x1;
        double ystart = y1;
        double xupdate = 0;
        double [] ret = new double[8];

        for(int c = cols - 1; c >= 0; --c) {
          for(int r = rows - 1; r >= 0; --r) {
            xupdate = deltaArray[r][(c == 0 ? 0 : c - 1)].width * xratio;
            double [] x = new double[]{xstart, deltaArray[r][c].width * xratio + xstart,
              deltaArray[r][c].width * xratio + xstart, xstart};
            double [] y = new double[]{ystart, ystart, deltaArray[r][c].height * yratio + ystart,
              deltaArray[r][c].height * yratio + ystart};

            if(r == re && c == ce) {
              ret[0] = x[0]; ret[1] = y[0];
              ret[2] = x[1]; ret[3] = y[1];
              ret[4] = x[2]; ret[5] = y[2];
              ret[6] = x[3]; ret[7] = y[3];
            }

            ystart += deltaArray[r][c].height * yratio;
          }
          ystart = y1;
          xstart -= xupdate;
        }

        return ret;
      }

    }


    //---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current state of the array.
     *
     * @return     A String containing GAIGS XML code for the array
     */

    public String toXML() {
      GAIGSprimitiveCollection pc = new GAIGSprimitiveCollection();

      //draw bounding box
      //double [] bx = new double[]{x1, x2, x2, x1};
      //double [] by = new double[]{y1, y1, y2, y2};
      //pc.addPolygon(4, bx, by, "", "#0000FF", "#000000", "", .03, 12);


      if(!outOfBounds()) {
        //draw the array
        double xstart = x2 - STD_WIDTH; //right hand side for the moment
        double ystart = y1;
        double xupdate = 0;

        for(int c = cols - 1; c >= 0; --c) {
          for(int r = rows - 1; r >= 0; --r) {
            xupdate = deltaArray[r][(c == 0 ? 0 : c - 1)].width;
            double [] x = new double[]{xstart, deltaArray[r][c].width + xstart, deltaArray[r][c].width + xstart, xstart};
            double [] y = new double[]{ystart, ystart, deltaArray[r][c].height + ystart, deltaArray[r][c].height + ystart};
            pc.addPolygon(4, x, y, theColorArray[r][c], "#000000", "#000000",
                          (theArray[r][c] == null ? "null" : theArray[r][c].toString()), FONT_SIZE, LINE_THICKNESS);
            ystart += deltaArray[r][c].height;
          }
          ystart = y1;
          xstart -= xupdate;
        }

        //draw the row labels
        xstart += xupdate - EPSILON;
        String textStrings = "";
        ystart = y1;
        double yinc = 0;
        for(int r = rows - 1; r >= 0; --r) {
          yinc = deltaArray[r][0].height / 2;
          ystart += yinc;
          String label = rowLabels[r];
          GAIGStext temp = new GAIGStext(xstart, ystart, rowLabels[r]);
          temp.setValign(GAIGStext.VTOP);
          temp.setHalign(GAIGStext.HRIGHT);
          temp.setFontsize(LABEL_FONT_SIZE);
          textStrings += temp.toXML() + "\n";
          ystart += yinc;
        }

        //draw the col labels
        ystart += EPSILON;
        double xinc = 0;
        xstart = x2;
        for(int c = cols - 1; c >= 0; --c) {
          xinc = deltaArray[0][c].width / 2;
          xstart -= xinc;
          String label = colLabels[c];
          GAIGStext temp = new GAIGStext(xstart, ystart, label);
          temp.setValign(GAIGStext.VBOTTOM);
          temp.setHalign(GAIGStext.HCENTER);
          temp.setFontsize(LABEL_FONT_SIZE);
          textStrings += temp.toXML() + "\n";
          xstart -= xinc;
        }

        //draw Title
        GAIGStext title = new GAIGStext((x2 + xstart)/2, y1 - 2 * EPSILON, name);
        title.setValign(GAIGStext.VTOP);
        title.setHalign(GAIGStext.HCENTER);
        title.setFontsize(TITLE_FONT_SIZE);
        textStrings += title.toXML() + "\n";

        return textStrings + pc.toXML();

      } else {
        double xout = computeOutX();
        double yout = computeOutY();

        double dx = x2 - x1;
        double dxi = x1 - xout;
        double xratio = xout < x1 ? dx / (dx + dxi) : 1;

        double dy = y2 - y1;
        double dyi = yout - y2;
        double yratio = yout > y2 ? dy / (dyi + dy) : 1;

        //draw the array
        double xstart = x2 - deltaArray[rows-1][cols-1].width; //right hand side for the moment
        xstart = (xstart - xout) * xratio + x1;
        double ystart = y1;
        double xupdate = 0;

        for(int c = cols - 1; c >= 0; --c) {
          for(int r = rows - 1; r >= 0; --r) {
            xupdate = deltaArray[r][(c == 0 ? 0 : c - 1)].width * xratio;
            double [] x = new double[]{xstart, deltaArray[r][c].width * xratio + xstart,
              deltaArray[r][c].width * xratio + xstart, xstart};
              double [] y = new double[]{ystart, ystart, deltaArray[r][c].height * yratio + ystart,
                deltaArray[r][c].height * yratio + ystart};
              String temp = theArray[r][c] == null ? "null" : theArray[r][c].toString();
              double newXFontSize =  FONT_SIZE * xratio;
              double newYFontSize = yratio * FONT_SIZE;
              double newFontSize = newXFontSize < newYFontSize ? newXFontSize : newYFontSize;
              pc.addPolygon(4, x, y, theColorArray[r][c], "#000000", "#000000",
                            (theArray[r][c] == null ? "null" : theArray[r][c].toString()), newFontSize, LINE_THICKNESS);
              ystart += deltaArray[r][c].height * yratio;
          }
          ystart = y1;
          xstart -= xupdate;
        }

          //draw the row labels
        xstart += xupdate - EPSILON;
        String textStrings = "";
        ystart = y1;
        double yinc = 0;
        for(int r = rows - 1; r >= 0; --r) {
          yinc = deltaArray[r][0].height * yratio / 2;
          ystart += yinc;
          String label = rowLabels[r];
          GAIGStext temp = new GAIGStext(xstart, ystart, rowLabels[r]);
          temp.setValign(GAIGStext.VTOP);
          temp.setHalign(GAIGStext.HRIGHT);
          double xlen = CHAR_SIZE * label.length();
          double xnewlen = xratio * xlen;
          double labelFontSize = LABEL_FONT_SIZE * (xnewlen / xlen) * yratio;
          temp.setFontsize(labelFontSize);
          textStrings += temp.toXML() + "\n";
          ystart += yinc;
        }

        //draw the col labels
        ystart += EPSILON;
        double xinc = 0;
        xstart = x2;
        for(int c = cols - 1; c >= 0; --c) {
          xinc = xratio * deltaArray[0][c].width / 2;
          xstart -= xinc;
          String label = colLabels[c];
          GAIGStext temp = new GAIGStext(xstart, ystart, label);
          temp.setValign(GAIGStext.VBOTTOM);
          temp.setHalign(GAIGStext.HCENTER);
          double len = CHAR_SIZE * label.length();
          double newlen = xratio * len;
          double labelFontSize = LABEL_FONT_SIZE * newlen / len;
          temp.setFontsize(labelFontSize);
          textStrings += temp.toXML() + "\n";
          xstart -= xinc;
        }

        //draw Title
        GAIGStext title = new GAIGStext((x2 + xstart)/2, y1 - 2 * EPSILON, name);
        title.setValign(GAIGStext.VTOP);
        title.setHalign(GAIGStext.HCENTER);
        title.setFontsize(TITLE_FONT_SIZE);
        textStrings += title.toXML() + "\n";
        return textStrings + pc.toXML();
      }
    }

    //---------------------- Helper Methods -----------------------------------
    /**
        * Resizes the column widths to fit the elements inside
        *
    */
    private void resizeCol(int c) {
      int max = 0;
      for(int r = 0; r < rows; ++r) {
        String temp = (theArray[r][c] == null ? "null" : theArray[r][c].toString());
        max = max < temp.length() ? temp.length() : max;
      }
      int inc = max / LINE_LENGTH;
      for(int r = 0; r < rows; ++r) {
        deltaArray[r][c].width = STD_WIDTH + inc * STD_WIDTH;
      }
    }

    /**
    * Resizes the column widths to fit the label of the column
    *
    */
    private void resizeColForLabel(int c) {
      double curSize = deltaArray[0][c].width;
      double newSize = colLabels[c].length() * CHAR_SIZE;
      if(newSize > curSize) {
        for(int r = 0; r < rows; ++r) {
          deltaArray[r][c].width = newSize;
        }
      }
    }

    /**
        * Checks to see if the default size goes beyond the bounds
        *@return Returns true if the default size is out of bounds
    */
    private boolean outOfBounds() {
      boolean outofbounds = false;

      //check the array structure
      double xstart = x2 - deltaArray[rows-1][cols-1].width; //right hand side for the moment
      double ystart = y1;
      double xupdate = 0;

      for(int c = cols - 1; c >= 0; --c) {
        for(int r = rows - 1; r >= 0; --r) {
          xupdate = deltaArray[r][(c == 0 ? 0 : c - 1)].width;
          ystart += deltaArray[r][c].height;
          outofbounds = outofbounds || ystart > y2;
        }
        ystart = y1;
        outofbounds = outofbounds || xstart < x1;
        xstart -= xupdate;
      }

      //check the row labels
      xstart += xupdate - EPSILON;
      outofbounds = xstart < x1;
      ystart = y1;
      double yinc = 0;
      for(int r = rows - 1; r >= 0; --r) {
        outofbounds = outofbounds || (xstart - (rowLabels[r].length() * CHAR_SIZE)) < x1;
      }

      return outofbounds;
    }

    /**
        * Checks to find out where the smallest x value went out of bounds
        * @return Returns smallest x value that went out of bounds
    */
    private double computeOutX() {
      //check the array structure
      double xstart = x2 - STD_WIDTH; //right hand side for the moment
      double ystart = y1;
      double xupdate = 0;
      double xsmall = Double.MAX_VALUE;

      for(int c = cols - 1; c >= 0; --c) {
        for(int r = rows - 1; r >= 0; --r) {
          xupdate = deltaArray[r][(c == 0 ? 0 : c - 1)].width;
        }

        xsmall = xsmall < xstart ? xsmall : xstart;
        xstart -= xupdate;
      }

      //check the row labels
      xstart += xupdate - EPSILON;

      xsmall = xsmall < xstart ? xsmall : xstart;
      ystart = y1;
      double yinc = 0;
      for(int r = rows - 1; r >= 0; --r) {
        xsmall = xsmall < (xstart - (rowLabels[r].length() * CHAR_SIZE)) ? xsmall :
            (xstart - (rowLabels[r].length() * CHAR_SIZE));
      }
      return xsmall;
    }

    /**
        * Checks to find out where the largest y value went out of bounds
        * @return Returns largest y value that went out of bounds
    */
    private double computeOutY() {
      double ylarge = STD_HEIGHT * (rows + 1) + y1;

      return ylarge;
    }

}

