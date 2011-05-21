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
import java.util.*;

/**
 * <p><code>GAIGSnewQueue</code> extends the <code>GAIGSlist</code> class, providing
 * the ability to implement a standard queue data structure and also create GAIGS
 * visualizations of its state. Use the various constructors to specify the general
 * parameters for the queue visualization, and use the <code>toXML</code> method to
 * actually generate the queue XML for snapshots.</p>
 *
 * <p>A method is also provided to set the presentation color of a queue cell.
 * A default queue cell color can be set by using the appropriate constructor.
 * Methods to get a cell color are inherited from <code>GAIGSlist</code>.</p>
 *
 * @author Shawn Recker
 * @version 7/7/2010
 */

public class GAIGSnewQueue extends GAIGSlist {

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

    /**
        * ArrayList for keeping track of width and size requirements
    */
        protected LinkedList<BoxPair> deltaArray;

    /**
        * Current maximum width
    */
        protected double maxWidth;

    /**
        * Current maximum height
    */
        protected double maxHeight;

//---------------------- Constructors -------------------------------------------


    /**
     * Use all default values for instance variables
     */
    public GAIGSnewQueue() {
        super(DEFAULT_NAME, DEFAULT_COLOR, DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE);
        deltaArray = new LinkedList<BoxPair>();
        maxWidth = STD_WIDTH;
        maxHeight = 0;
    }

    /**
     * Explicitly set all  instance variables.
     *
     * @param       name            Display name of this structure.
     * @param       color           Color for items unless locally overridden.
     * @param       x1              Left display bound.
     * @param       y1              Bottom display bound.
     * @param       x2              Top display bound.
     * @param       y2              Right display bound.
     * @param       fontSize        Font size for display.
     */
    public GAIGSnewQueue(String name, String color, double x1, double y1, double x2, double y2) {
        super(name, color, x1, y1, x2, y2, DEFAULT_FONT_SIZE);
        deltaArray = new LinkedList<BoxPair>();
        maxWidth = STD_WIDTH;
        maxHeight = 0;
    }


//---------------------- Queue Methods -----------------------------------------


    /**
     * Adds an item to the queue. Default color will be used
     * for this item.
     *
     * @param     v     The value to be stored.
     */
    public void enqueue (Object v) {
      addLast(v);
      deltaArray.addLast(new BoxPair());
      resize();
    }

    /**
     * Adds an item with an associated color to the queue.
     *
     * @param     v     The value to be stored.
     * @param     c     The display color for this item.
     */
    public void enqueue (Object v, String c) {
      addLast(v, c);
      deltaArray.addLast(new BoxPair());
      resize();
    }

    /**
     * removes an item from the queue and returns it.
     *
     * @return     An <code>Object</code> containing popped value
     */
    public Object dequeue () {
      deltaArray.removeFirst();
      resize();
      return removeFirst();
    }

    /**
     * returns but does not remove an item from the queue.
     *
     * @return      An <code>Object</code> containing the next value to be dequeued.
     */
    public Object peek () {
        return getFirst();
    }

//---------------------- Corner Point Methods ---------------------------------
    /**
        * Get the corner points making up the box around the element
        *
        * @param       e the element
        * @return An array containing the corner points as x,y pairs.
        * First pair is the bottom left, then bottom right, then top right, then top left
    */
    public double [] getCornerPoints(int e) {
      double xout = x2 - deltaArray.get(0).width;
      double yout = STD_HEIGHT * (list.size() + 1) + y1;

      double dx = x2 - x1;
      double dxi = x1 - xout;
      double xratio = xout < x1 ? dx / (dx + dxi) : 1;

      double dy = y2 - y1;
      double dyi = yout - y2;
      double yratio = yout > y2 ? dy / (dyi + dy) : 1;

      double xstart = x2 - deltaArray.get(0).width;
      xstart = xout < x1 ? (xstart - xout) * xratio + x1 : x2 - STD_WIDTH;
      double ystart = y1;

      double [] ret = new double[8];

      //draw Stack
      for(int i = 0; i < list.size(); ++i) {
        double [] x = new double[]{xstart, deltaArray.get(i).width * xratio + xstart,
          deltaArray.get(i).width * xratio + xstart, xstart};
          double [] y = new double[]{ystart, ystart, deltaArray.get(i).height * yratio + ystart,
            deltaArray.get(i).height * yratio + ystart};
            if(e == i) {
              ret[0] = x[0]; ret[1] = y[0];
              ret[2] = x[1]; ret[3] = y[1];
              ret[4] = x[2]; ret[5] = y[2];
              ret[6] = x[3]; ret[7] = y[3];
            }
            ystart += deltaArray.get(i).height * yratio;
      }

      return ret;
    }

//---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current state of the queue
     *
     * @return     A <code>String</code> containing GAIGS XML code for the queue
     */
    public String toXML() {
      GAIGSprimitiveCollection pc = new GAIGSprimitiveCollection();

      if(list.size() == 0) {
        return pc.toXML();
      }

      //draw bounding box
      //double [] bx = new double[]{x1, x2, x2, x1};
      //double [] by = new double[]{y1, y1, y2, y2};
      //pc.addPolygon(4, bx, by, "", "#0000FF", "#000000", "", .03, 12);

      double xout = x2 - deltaArray.get(0).width;
      double yout = STD_HEIGHT * (list.size() + 2) + y1;

      double dx = x2 - x1;
      double dxi = x1 - xout;
      double xratio = xout < x1 ? dx / (dx + dxi) : 1;

      double dy = y2 - y1;
      double dyi = yout - y2;
      double yratio = yout > y2 ? dy / (dyi + dy) : 1;

      double xstart = x2 - STD_WIDTH;
      xstart = xout < x1 ? (xstart - xout) * xratio + x1 : x2 - STD_WIDTH;
      double ystart = y1;

      //draw first arrow
      double [] ax = new double[]{(x2 + (xstart)) / 2,
        (x2 + (xstart)) / 2};
      double [] ay = new double[]{ystart, ystart + deltaArray.get(0).height * yratio };
      pc.addArrow(ax, ay, DEFAULT_COLOR, DEFAULT_COLOR,
                  "", .02, FONT_SIZE, LINE_THICKNESS);
      ystart += deltaArray.get(0).height * yratio;

      //draw Queue
      for(int i = 0; i < list.size(); ++i) {
        double [] x = new double[]{xstart, deltaArray.get(i).width * xratio + xstart,
          deltaArray.get(i).width * xratio + xstart, xstart};
        double [] y = new double[]{ystart, ystart, deltaArray.get(i).height * yratio + ystart,
          deltaArray.get(i).height * yratio + ystart};
        String temp = list.get(i) == null ? "null" : list.get(i).toString();
        double newXFontSize =  FONT_SIZE * xratio;
        double newYFontSize = yratio * FONT_SIZE;
        double newFontSize = newXFontSize < newYFontSize ? newXFontSize : newYFontSize;
        pc.addPolygon(4, x, y, colorList.get(i), "#000000", "#000000",
          (list.get(i) == null ? "null" : list.get(i).toString()), newFontSize, LINE_THICKNESS);
        ystart += deltaArray.get(i).height * yratio;
      }

      //draw last arrow
      ay[0] = ystart;
      ay[1] = ystart + deltaArray.get(deltaArray.size() - 1).height * yratio;
      pc.addArrow(ax, ay, DEFAULT_COLOR, DEFAULT_COLOR,
                  "", .02, FONT_SIZE, 12);

      //draw Title
      String textStrings = "";
      GAIGStext title = new GAIGStext((x2 + xstart)/2, y1 - 2 * EPSILON, name);
      title.setValign(GAIGStext.VTOP);
      title.setHalign(GAIGStext.HCENTER);
      title.setFontsize(TITLE_FONT_SIZE);
      textStrings += title.toXML() + "\n";
      return textStrings + pc.toXML();

    }

//-------------------- Helper methods ------------------------------------------
    /**
        * Resizes the structure to fit the maximum length item
    */
    private void resize() {
      int max = Integer.MIN_VALUE;
      for(int i = 0; i < list.size(); ++i) {
        String ele = (list.get(i) == null ? "null" : list.get(i).toString());
        max = (max > ele.length() ? max : ele.length());
      }
      int inc = max / LINE_LENGTH;
      for(int i = 0; i < deltaArray.size(); ++i) {
        deltaArray.get(i).width = STD_WIDTH + inc * STD_WIDTH;
      }
    }
}
