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

package gaigs2;
import java.awt.image.*;
import java.util.*;
import java.lang.*;
import org.jdom.*;

public class primitivecollection extends StructureType
{

protected interface Primitive {  }

  protected class Circle implements Primitive {
    public double x;
    public double y;
    public double r;
    public int fcolor;
    public int ocolor;
    public int lcolor;
    public String label;
    public double height;
    public int width;

    public Circle(double cx, double cy, double r, int fcolor, int ocolor, int lcolor, String label, double height, int width) {
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

  protected class Polygon implements Primitive
  {
    public int nSides;
    public double ptsX[];
    public double ptsY[];
    public int fcolor;
    public int ocolor;
    public int lcolor;
    public String label;
    public double height;
    public int width;

    public Polygon(int nSides, double ptsX[], double ptsY[], int fcolor, int ocolor,
                   int lcolor, String label, double height, int width)
    {
      this.nSides = nSides;
      this.ptsX = new double [nSides];
      this.ptsY = new double [nSides];

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

  protected class Ellipse implements Primitive {
    public double x;
    public double y;
    public double stAngle;
    public double endAngle;
    public double xR;
    public double yR;
    public int color;
    public int lcolor;
    public String label;
    public double height;
    public int width;

    public Ellipse(double x, double y, double stAngle, double endAngle, double xR,
                   double yR, int color, int lcolor, String label, double height, int width)
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

  protected ArrayList<Primitive> primitives;

  public primitivecollection()
  {
    super();
    primitives = new ArrayList<Primitive>();
  }

  public void loadStructure(Element rootEl, LinkedList thingsToRender, draw drawerObj)
  {

    load_name_and_bounds(rootEl, thingsToRender, drawerObj);
    //calcDimsAndStartPts(thingsToRender, drawerObj);

    List children = rootEl.getChildren();
    Iterator iter = children.iterator();

    while(iter.hasNext())
    {
      Element child = (Element)iter.next();

      if(child.getName().equals("circle"))
      {
        double x = Double.parseDouble(child.getAttributeValue("x"));
        double y = Double.parseDouble(child.getAttributeValue("y"));
        double r = Double.parseDouble(child.getAttributeValue("r"));

        String fcstring = child.getAttributeValue("fcolor");

        int fcolor = ((fcstring == "" || fcstring == null) ? Integer.MIN_VALUE :
          colorStringToInt(child.getAttributeValue("fcolor")));
        int ocolor = colorStringToInt(child.getAttributeValue("ocolor"));
        int lcolor = colorStringToInt(child.getAttributeValue("lcolor"));

        String text = child.getAttributeValue("text");
        double h = Double.parseDouble(child.getAttributeValue("height"));
        int w = Integer.parseInt(child.getAttributeValue("width"));
        primitives.add(new Circle(x, y, r, fcolor, ocolor, lcolor, text, h, w));
      }

      if(child.getName().equals("polygon"))
      {
        int nSides = Integer.parseInt(child.getAttributeValue("nSides"));
        double [] ptsX = new double[nSides];
        double [] ptsY = new double[nSides];
        for(int i=0; i<nSides ; ++i)
        {
          String pX = "ptsX" + i;
          String pY = "ptsY" + i;
          ptsX[i] = Double.parseDouble(child.getAttributeValue(pX));
          ptsY[i] = Double.parseDouble(child.getAttributeValue(pY));
        }

        String fcstring = child.getAttributeValue("fcolor");

        int fcolor = ((fcstring == "" || fcstring == null) ? Integer.MIN_VALUE :
          colorStringToInt(child.getAttributeValue("fcolor")));
        int ocolor = colorStringToInt(child.getAttributeValue("ocolor"));
        int lcolor = colorStringToInt(child.getAttributeValue("lcolor"));

        String text = child.getAttributeValue("text");
        double h = Double.parseDouble(child.getAttributeValue("height"));
        int w = Integer.parseInt(child.getAttributeValue("width"));
        primitives.add(new Polygon(nSides, ptsX, ptsY, fcolor, ocolor, lcolor, text, h, w));
      }

      if(child.getName().equals("ellipse"))
      {
        double x = Double.parseDouble(child.getAttributeValue("x"));
        double y = Double.parseDouble(child.getAttributeValue("y"));
        double sa = Double.parseDouble(child.getAttributeValue("sa"));
        double ea = Double.parseDouble(child.getAttributeValue("ea"));
        double rx = Double.parseDouble(child.getAttributeValue("rx"));
        double ry = Double.parseDouble(child.getAttributeValue("ry"));

        int color = colorStringToInt(child.getAttributeValue("color"));
        int lcolor = colorStringToInt(child.getAttributeValue("lcolor"));

        String text = child.getAttributeValue("text");
        double h = Double.parseDouble(child.getAttributeValue("height"));
        int w = Integer.parseInt(child.getAttributeValue("width"));
        primitives.add(new Ellipse(x, y, sa, ea, rx, ry, color, lcolor, text, h, w));
      }

    }
  }
  public void drawStructure(LinkedList thingsToRender, draw drawerObj) {

    for(int i = 0; i < primitives.size(); ++i) {
      Primitive p = primitives.get(i);

      if(p instanceof Circle) {
        Circle c = (Circle)p;
        LGKS.set_text_height(c.height, thingsToRender, drawerObj);
        //draw interior of circle
        if(c.fcolor != Integer.MIN_VALUE) {
          LGKS.set_fill_int_style(bsSolid, c.fcolor, thingsToRender, drawerObj);
          LGKS.circle_fill(c.x, c.y, c.r, thingsToRender, drawerObj);
        }

        //draw circle outline
        LGKS.set_textline_color(c.ocolor, thingsToRender, drawerObj);
        LGKS.set_line_width(c.width, thingsToRender, drawerObj);
        LGKS.circle(c.x, c.y, c.r, thingsToRender, drawerObj);

        //draw label
        LGKS.set_textline_color(c.lcolor, thingsToRender, drawerObj);
        LGKS.set_text_align(TA_CENTER, TA_BOTTOM, thingsToRender, drawerObj);
        LGKS.text(c.x, c.y, c.label, thingsToRender, drawerObj);
      }

      if(p instanceof Polygon) {
        Polygon pl = (Polygon)p;
        LGKS.set_text_height(pl.height, thingsToRender, drawerObj);
        if(pl.fcolor != Integer.MIN_VALUE) {
          //draw interior of Polygon
          LGKS.set_fill_int_style(bsSolid, pl.fcolor, thingsToRender, drawerObj);
          LGKS.fill_area(pl.nSides, pl.ptsX, pl.ptsY, thingsToRender, drawerObj);
        }

        //draw Polygon outline
        LGKS.set_textline_color(pl.ocolor, thingsToRender, drawerObj);
        LGKS.set_line_width(pl.width, thingsToRender, drawerObj);
        LGKS.polyline(pl.nSides, pl.ptsX, pl.ptsY, thingsToRender, drawerObj);

        //draw label
        LGKS.set_textline_color(pl.lcolor, thingsToRender, drawerObj);
        LGKS.set_text_align(TA_CENTER, TA_BOTTOM, thingsToRender, drawerObj);
        LGKS.text(computeCenter(pl.ptsX), computeCenter(pl.ptsY), pl.label, thingsToRender, drawerObj);
      }

      if(p instanceof Ellipse) {
        Ellipse e = (Ellipse)p;
        LGKS.set_text_height(e.height, thingsToRender, drawerObj);
        //draw ellipse outline
        LGKS.set_textline_color(e.color, thingsToRender, drawerObj);
        LGKS.set_line_width(e.width, thingsToRender, drawerObj);
        LGKS.ellipse(e.x, e.y,e.stAngle, e.endAngle, e.xR, e.yR, thingsToRender, drawerObj);

        //draw label
        LGKS.set_textline_color(e.lcolor, thingsToRender, drawerObj);
        LGKS.set_text_align(TA_CENTER, TA_BOTTOM, thingsToRender, drawerObj);
        LGKS.text(e.x-e.xR/2.0, e.y+e.yR/2.0, e.label, thingsToRender, drawerObj);
      }

    }
  }
  double computeCenter(double points[])
  {
    double avg=0;
    for(int i=0;i<points.length;++i)
    {
      avg+=points[i];
    }
    return avg/points.length;
  }
}


