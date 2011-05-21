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
import java.lang.*;

/**
 * <p><code>GAIGSprimitiveCollection</code> provides the ability to draw 2D graphics for use in visualizations.
 * This class supports a variety of 2D graphics primitives, including lines, polygons, circles, ellipises, etc.
 * Creation of the primitives adheres to the other GAIGS classes and colors are specified with the standard color string.
 * </p>
 *
 * @author Shawn Recker
 * @version 6/22/2010
 */

public class GAIGSprimitiveCollection implements GAIGSdatastr {

  protected interface Primitive { }

  public final double TEXT_HEIGHT = .03;
  public final int LINE_WIDTH = 12;

  protected class Circle implements Primitive {
    public double x;
    public double y;
    public double r;
    public String fcolor;
    public String ocolor;
    public String lcolor;
    public String label;
    public double height;
    public int width;

    public Circle(double cx, double cy, double r, String fcolor, String ocolor,
                  String lcolor, String label, double height, int width)
    {
      this.x = cx;
      this.y = cy;
      this.r = r;
      this.fcolor = fcolor;
      this.ocolor = ocolor;
      this.lcolor = lcolor;
      this.label = label;
      this.height = height;
      this.width = width;
    }
  }

  protected class Polygon implements Primitive {
    public int nSides;
    public double [] ptsX;
    public double [] ptsY;
    public String fcolor;
    public String ocolor;
    public String lcolor;
    public String label;
    public double height;
    public int width;

    public Polygon (int nSides , double ptsX [], double ptsY [], String fcolor,
                    String ocolor, String lcolor, String label, double height, int width)
    {
      this.nSides = nSides;
      this.ptsX = new double[nSides];
      this.ptsY = new double[nSides];
      for(int i=0;i<nSides;++i)
      {
        this.ptsX[i]=ptsX[i];
        this.ptsY[i]=ptsY[i];
      }
      this.fcolor = fcolor;
      this.ocolor = ocolor;
      this.lcolor = lcolor;
      this.label = label;
      this.height = height;
      this.width = width;
    }
  }

  protected class StraightLine implements Primitive {
    public double x[] = new double[2];
    public double y[] = new double[2];
    public String color;
    public String lcolor;
    public String label;
    public double height;
    public int width;

    public StraightLine(double x[], double y[], String color, String lcolor,
                        String label, double height, int width)
    {
      for(int i=0;i<2;++i)
      {
        this.x[i]=x[i];
        this.y[i]=y[i];
      }
      this.color = color;
      this.lcolor = lcolor;
      this.label = label;
      this.height = height;
      this.width = width;
    }
  }

  protected class Ellipse implements Primitive {
    public double x;
    public double y;
    public double stAngle;
    public double endAngle;
    public double xR;
    public double yR;
    public String color;
    public String lcolor;
    public String label;
    public double height;
    public int width;

    public Ellipse(double x, double y, double stAngle, double endAngle, double xR,
      double yR, String color, String lcolor, String label, double height, int width)
    {
      this.x = x;
      this.y = y;
      this.stAngle = stAngle;
      this.endAngle = endAngle;
      this.xR = xR;
      this.yR = yR;
      this.color = color;
      this.lcolor = lcolor;
      this.label = label;
      this.height = height;
      this.width = width;
    }
  }

  /**
   * The Current collection of graphical primitives
  */
  protected ArrayList<Primitive> primitives;

  /**
   * The Name of the collection of graphical primitives
  */
  protected String name;

  /**
   * Creates an empty primitive collection with no name
  */
  public GAIGSprimitiveCollection() {
    primitives = new ArrayList<Primitive>();
	  name = "";
  }

  /**
   * Creates an empty primitive collection with the specified name
  */
  public GAIGSprimitiveCollection(String name) {
    primitives = new ArrayList<Primitive>();
    this.name = name;
  }

  /**
   * Sets the name of the primitive collection
   * @param name  The name of the collection
  */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the name of the primitive collection
   * @return The name of the primitive collection
  */
  public String getName() {
    return name;
  }

  /**
   * Creates and Returns the GAIGS XML code for the current state of the primitive collection
   * @return A String containing GAIGS XML code for the primitive collection
  */
  public String toXML() {
    String xml;
    String bounds = computeBounds();

    xml = "<primitivecollection>\n\t<name>" + name +
      "</name>\n\t"+ "<bounds x1=\"0.0\" y1=\"0.0\" x2=\"1.0\" y2=\"1.0\"/>" +"\n\t";

    for(int i = 0; i < primitives.size(); ++i) {
      Primitive p = primitives.get(i);

      if(p instanceof Circle) {
        Circle c = (Circle)p;
        xml += "\t<circle x=\"" + c.x + "\" y=\"" + c.y + "\" " +
          "r=\"" + c.r + "\" fcolor=\"" + c.fcolor + "\" " +
          "ocolor=\"" + c.ocolor + "\" text=\"" + c.label + "\" lcolor=\"" + c.lcolor + "\" height=\"" +
          c.height + "\" width=\""+ c.width +"\"/>\n";
      }

      if(p instanceof Polygon) {
        Polygon pl = (Polygon)p;
        xml += "<polygon nSides=\"" + pl.nSides;
        for(int j=0; j<pl.nSides ; ++j) {
          xml += "\" ptsX"+ j + "=\"" + pl.ptsX[j] + "\" ptsY"+ j + "=\"" + pl.ptsY[j];
        }
        xml += "\" fcolor=\"" + pl.fcolor + "\" " +
          "ocolor=\"" + pl.ocolor + "\" text=\"" + pl.label + "\" lcolor=\"" + pl.lcolor + "\" height=\"" +
          pl.height + "\" width=\"" + pl.width + "\"/>\n";
      }

      if(p instanceof StraightLine) {
        StraightLine l = (StraightLine)p;
        xml += "<polygon nSides=\"" + 2;
        for(int j=0; j<2 ; ++j) {
          xml += "\" ptsX"+ j + "=\"" + l.x[j] + "\" ptsY"+ j + "=\"" + l.y[j];
        }
        xml += "\" fcolor=\"" + l.color + "\" " +
          "ocolor=\"" + l.color + "\" text=\"" + l.label + "\" lcolor=\"" + l.lcolor +
          "\" height=\"" + l.height + "\" width=\"" + l.width + "\"/>\n";
      }

      if(p instanceof Ellipse) {
        Ellipse e = (Ellipse)p;
        xml += "\t<ellipse x=\"" + e.x + "\" y=\"" + e.y + "\" " +
          "sa=\"" + e.stAngle + "\" ea=\"" + e.endAngle + "\" rx=\"" + e.xR + "\" ry=\"" + e.yR + "\" color=\"" + e.color + "\" " +
          "text=\"" + e.label + "\" lcolor=\"" + e.lcolor + "\" height=\"" + e.height + "\" width=\"" + e.width + "\"/>\n";
      }
    }
    xml += "</primitivecollection>\n";
    return xml;
  }

  /**
   * Removes all primitives from the collection
  */
  public void clearPrimitives() {
    primitives.clear();
  }

  /**
   * Private method for computing bounds of the primitive collection for output to xml
   * @return The xml string representing the bounds of the primitive collection
  */
  private String computeBounds() {
    double x1 = Double.MAX_VALUE;
    double y1 = Double.MAX_VALUE;
    double x2 = Double.MIN_VALUE;
    double y2 = Double.MIN_VALUE;

    for(int i = 0; i < primitives.size(); ++i) {
      Primitive p = primitives.get(i);

      if(p instanceof Circle) {
        Circle c = (Circle)p;
        x1 = (x1 < (c.x - c.r) ? x1 : c.x - c.r);
        y1 = (y1 < (c.y - c.r) ? y1 : c.y - c.r);
        x2 = (x2 > (c.x + c.r) ? x2 : c.x + c.r);
        y2 = (y2 > (c.y + c.r) ? y2 : c.y + c.r);
      }

      if(p instanceof Polygon) {
        Polygon t = (Polygon)p;
        for(int j = 0; j < t.nSides; ++j) {
          x1 = (x1 < t.ptsX[j] ? x1 : t.ptsX[j]);
          y1 = (y1 < t.ptsY[j] ? y1 : t.ptsY[j]);
          x2 = (x2 > t.ptsX[j] ? x1 : t.ptsX[j]);
          y2 = (y2 > t.ptsY[j] ? y1 : t.ptsY[j]);
        }
      }

      if(p instanceof StraightLine) {
        StraightLine t = (StraightLine)p;
        for(int j = 0; j < 2; ++j) {
          x1 = (x1 < t.x[j] ? x1 : t.x[j]);
          y1 = (y1 < t.y[j] ? y1 : t.y[j]);
          x2 = (x2 > t.x[j] ? x1 : t.x[j]);
          y2 = (y2 > t.y[j] ? y1 : t.y[j]);
        }
      }

      if(p instanceof Ellipse) {
        Ellipse e = (Ellipse)p;
        x1 = (x1 < e.x ? x1 : e.x);
        x1 = (x1 < e.x + e.xR ? x1 : e.x + e.xR);
        x2 = (x2 > e.x ? x2 : e.x);
        x2 = (x2 > e.x + e.xR ? x2 : e.x + e.xR);
        y1 = (y1 < e.y ? y1 : e.y);
        y1 = (y1 < e.y + e.yR ? y1 : e.y + e.yR);
        y2 = (y2 > e.y ? y2 : e.y);
        y2 = (y2 > e.y + e.yR ? y2 : e.y + e.yR);
      }
    }

    return ("<bounds x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2 + "\"/>");
  }

  /**
   * Adds a circle to the primitive collection
   * @param cx The center x coordinate of the circle
   * @param cy The center y coordinate of the circle
   * @param r The radius of the circle
   * @param fillColor The internal color of the circle (use an empty string for no fill color)
   * @param outlineColor The color of the circle outline
   * @param labelColor The color of the text in the circle label
   * @param labelText The text to be drawn in the center of the circle
   * @param textHeight The Height of the text in the label
   * @param lineWidth The thickness of the outline of the circle
  */
  public void addCircle(double cx, double cy, double r, String fillColor, String outlineColor,
    String labelColor, String labelText, double textHeight, int lineWidth)
  {
    primitives.add(new Circle(cx, cy, r, fillColor, outlineColor, labelColor, labelText, textHeight, lineWidth));
  }

  /**
   * Adds a polygon to the primitive collection
   * @param nSides The number of sides to the polygon
   * @param ptsX Array containing the x coordinate values for the polygon
   * @param otsY Array containing the y coordinate values for the polygon
   * @param fillColor The internal color of the polygon (use an empty string for no fill color)
   * @param outlineColor The color of the circle polygon
   * @param labelColor The color of the text in the circle label
   * @param labelText The text to be drawn in the center of the circle
   * @param lineWidth The thickness of the outline of the polygon
  */
  public void addPolygon(int nSides, double ptsX[], double ptsY[], String fillColor, String outlineColor,
    String labelColor, String labelText, double textHeight, int lineWidth)
  {
    primitives.add(new Polygon(nSides, ptsX, ptsY, fillColor, outlineColor, labelColor, labelText, textHeight, lineWidth));
  }

  /**
   * Adds a line to the primitive collection
   * @param x Array of 2 containing the x coordinates for the start point and end point
   * @param y Array of 2 containing the y coordinates for the start point and end point
   * @param color The color of the line
   * @param lcolor The color of the text in the label
   * @param label The text to printed near the line
   * @param textHeight The Height of the text in the label
   * @param lineWidth The thickness of the line
  */
  public void addLine(double x[], double y[], String color, String lcolor, String label, double textHeight, int lineWidth)
  {
    primitives.add(new StraightLine(x, y, color,lcolor, label, textHeight, lineWidth));
  }

  /**
   * Adds a line to the primitive collection
   * @param x Array of 2 containing the x coordinates for the start point and end point
   * @param y Array of 2 containing the y coordinates for the start point and end point
   * @param color The color of the line
   * @param lcolor The color of the text in the label
   * @param label The text to printed near the line
   * @param dashSize The length of the dash in the line
   * @param textHeight The Height of the text in the label
   * @param lineWidth The thickness of the line dashes
  */
  public void addDashedLine(double x[], double y[], String color, String lcolor,
    String label, double dashSize, double textHeight, int lineWidth)
  {

    double dist = Math.sqrt((x[1] - x[0])*(x[1] - x[0]) + (y[1] - y[0])*(y[1] - y[0]));
    double size = dashSize;

    int numLines = ((int)Math.ceil(dist / size)) / 2;

    double [] xvals = new double[2];
    double [] yvals = new double[2];

    for(int i = 0; i < numLines; i+=2) {
      xvals[0] = x[0] + (i / (2*(double)numLines)) * x[1];
      yvals[0] = y[0] + (i / (2*(double)numLines)) * y[1];
      xvals[1] = x[0] + ((i+1) / (2*(double)numLines)) * x[1];
      yvals[1] = y[0] + ((i+1) / (2*(double)numLines)) * y[1];
      primitives.add(new StraightLine(xvals, yvals, color, lcolor, (i == numLines/2 ? label : ""), textHeight, lineWidth));
    }

  }

  /**
   * Adds a line to the primitive collection ending in an arrow
   * @param x Array of 2 containing the x coordinates for the start point and end point
   * @param y Array of 2 containing the y coordinates for the start point and end point
   * @param color The color of the line
   * @param lcolor The color of the text in the label
   * @param label The text to printed near the line
   * @param headSize The size of the arrow head
   * @param textHeight The Height of the text in the label
   * @param lineWidth The thickness of the line
  */
  public void addArrow(double x[], double y[], String color, String lcolor,
    String label, double headSize, double textHeight, int lineWidth)
  {
    primitives.add(new StraightLine(x, y, color, lcolor, "", textHeight, lineWidth));

    double size = headSize;

    double [] x1 = {x[1], 0};
    double [] y1 = {y[1], 0};
    double [] x2 = {x[1], 0};
    double [] y2 = {y[1], 0};

    double theta = Math.atan((y[1] - y[0])/(x[1] - x[0]));
    double end1 = theta + Math.toRadians(30);
    double end2 = theta - Math.toRadians(30);

    x1[1] = x[1] - size * Math.cos(end1);
    x2[1] = x[1] - size * Math.cos(end2);
    y1[1] = y[1] - size * Math.sin(end1);
    y2[1] = y[1] - size * Math.sin(end2);

    double [] xvals = {x[1], x1[1], x2[1]};
    double [] yvals = {y[1], y1[1], y2[1]};

    primitives.add(new Polygon(3, xvals, yvals, color, color, lcolor, label, textHeight, lineWidth));

  }

  /**
   * Adds an ellipse to the primitive collection.  Does not support a filled ellipse.
   * @param x The lower right hand x coordinate of the ellipse bounds
   * @param y The lower right hand y coordinate of the ellipse bounds
   * @param stAngle The starting angle in radians of the ellipse
   * @param endAngle The ending angle in radians of the ellipse
   * @param xR The radius value along the x axis
   * @param yR The radius value along the y axis
   * @param color The color of the outline of the ellipse
   * @param lcolor The color of the text in the label
   * @param label The text for the label to appear in the center of the ellipse
   * @param textHeight The Height of the text in the label
   * @param lineWidth The width of the outline of the circle
  */
  public void addEllipse(double x, double y, double stAngle, double endAngle, double xR,
    double yR, String color, String lcolor, String label, double textHeight, int lineWidth)
  {
    primitives.add(new Ellipse(x,y,stAngle,endAngle,xR,yR,color,lcolor,label,textHeight, lineWidth));
  }

  /**
      * Adds a circle to the primitive collection
      * @param cx The center x coordinate of the circle
      * @param cy The center y coordinate of the circle
      * @param r The radius of the circle
      * @param fillColor The internal color of the circle (use an empty string for no fill color)
      * @param outlineColor The color of the circle outline
      * @param labelColor The color of the text in the circle label
      * @param labelText The text to be drawn in the center of the circle

  */
      public void addCircle(double cx, double cy, double r, String fillColor, String outlineColor,
                            String labelColor, String labelText)
  {
    primitives.add(new Circle(cx, cy, r, fillColor, outlineColor, labelColor, labelText, TEXT_HEIGHT, LINE_WIDTH));
  }

  /**
      * Adds a polygon to the primitive collection
      * @param nSides The number of sides to the polygon
      * @param ptsX Array containing the x coordinate values for the polygon
      * @param otsY Array containing the y coordinate values for the polygon
      * @param fillColor The internal color of the polygon (use an empty string for no fill color)
      * @param outlineColor The color of the circle polygon
      * @param labelColor The color of the text in the circle label
      * @param labelText The text to be drawn in the center of the circle

  */
      public void addPolygon(int nSides, double ptsX[], double ptsY[], String fillColor, String outlineColor,
                             String labelColor, String labelText)
  {
    primitives.add(new Polygon(nSides, ptsX, ptsY, fillColor, outlineColor, labelColor, labelText, TEXT_HEIGHT, LINE_WIDTH));
  }

  /**
      * Adds a line to the primitive collection
      * @param x Array of 2 containing the x coordinates for the start point and end point
      * @param y Array of 2 containing the y coordinates for the start point and end point
      * @param color The color of the line
      * @param lcolor The color of the text in the label
      * @param label The text to printed near the line
  */
      public void addLine(double x[], double y[], String color, String lcolor, String label)
  {
    primitives.add(new StraightLine(x, y, color,lcolor, label, TEXT_HEIGHT,LINE_WIDTH));
  }

  /**
      * Adds a line to the primitive collection
      * @param x Array of 2 containing the x coordinates for the start point and end point
      * @param y Array of 2 containing the y coordinates for the start point and end point
      * @param color The color of the line
      * @param lcolor The color of the text in the label
      * @param label The text to printed near the line
      * @param dashSize The length of the dash in the line

  */
      public void addDashedLine(double x[], double y[], String color, String lcolor,
                                String label, double dashSize)
  {

    double dist = Math.sqrt((x[1] - x[0])*(x[1] - x[0]) + (y[1] - y[0])*(y[1] - y[0]));
    double size = dashSize;

    int numLines = ((int)Math.ceil(dist / size)) / 2;

    double [] xvals = new double[2];
    double [] yvals = new double[2];

    for(int i = 0; i < numLines; i+=2) {
      xvals[0] = x[0] + (i / (2*(double)numLines)) * x[1];
      yvals[0] = y[0] + (i / (2*(double)numLines)) * y[1];
      xvals[1] = x[0] + ((i+1) / (2*(double)numLines)) * x[1];
      yvals[1] = y[0] + ((i+1) / (2*(double)numLines)) * y[1];
      primitives.add(new StraightLine(xvals, yvals, color, lcolor, (i == numLines/2 ? label : ""), TEXT_HEIGHT, LINE_WIDTH));
    }

  }

  /**
      * Adds a line to the primitive collection ending in an arrow
      * @param x Array of 2 containing the x coordinates for the start point and end point
      * @param y Array of 2 containing the y coordinates for the start point and end point
      * @param color The color of the line
      * @param lcolor The color of the text in the label
      * @param label The text to printed near the line
      * @param headSize The size of the arrow head
  */
      public void addArrow(double x[], double y[], String color, String lcolor,
                           String label, double headSize)
  {
    primitives.add(new StraightLine(x, y, color, lcolor, "", TEXT_HEIGHT, LINE_WIDTH));

    double size = headSize;

    double [] x1 = {x[1], 0};
    double [] y1 = {y[1], 0};
    double [] x2 = {x[1], 0};
    double [] y2 = {y[1], 0};

    double theta = Math.atan((y[1] - y[0])/(x[1] - x[0]));
    double end1 = theta + Math.toRadians(30);
    double end2 = theta - Math.toRadians(30);

    x1[1] = x[1] - size * Math.cos(end1);
    x2[1] = x[1] - size * Math.cos(end2);
    y1[1] = y[1] - size * Math.sin(end1);
    y2[1] = y[1] - size * Math.sin(end2);

    double [] xvals = {x[1], x1[1], x2[1]};
    double [] yvals = {y[1], y1[1], y2[1]};

    primitives.add(new Polygon(3, xvals, yvals, color, color, lcolor, label, TEXT_HEIGHT,LINE_WIDTH));

  }

  /**
      * Adds an ellipse to the primitive collection.  Does not support a filled ellipse.
      * @param x The lower right hand x coordinate of the ellipse bounds
      * @param y The lower right hand y coordinate of the ellipse bounds
      * @param stAngle The starting angle in radians of the ellipse
      * @param endAngle The ending angle in radians of the ellipse
      * @param xR The radius value along the x axis
      * @param yR The radius value along the y axis
      * @param color The color of the outline of the ellipse
      * @param lcolor The color of the text in the label
      * @param label The text for the label to appear in the center of the ellipse
      * @param textHeight The Height of the text in the label
  */
      public void addEllipse(double x, double y, double stAngle, double endAngle, double xR,
                             double yR, String color, String lcolor, String label)
  {
    primitives.add(new Ellipse(x,y,stAngle,endAngle,xR,yR,color,lcolor,label,TEXT_HEIGHT,LINE_WIDTH));
  }
}

