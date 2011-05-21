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

package exe.pointSlope;

import java.awt.FontMetrics;
import java.util.LinkedList;
import org.jdom.Element;

abstract class Visual
{
    protected String id;
    //protected int x, y;
    protected int left, right;
    protected int fontSize;             /* used for */
    protected String family;            /* Text     */
    protected boolean bold;             /* and      */
    protected boolean italic;           /* rational */

    protected int width;
    protected String color;
    protected boolean hidden;
    //public Visual prev, next;

    //public abstract int computeWidth(FontMetrics fm);
    //public abstract Element makeElement();
    public abstract void    translate(int dx, int dy);

    protected void setWidth(int w)  { width = w;      }
    protected int  getWidth()       { return width;   }
    protected int  getRight()       { return right;   }
    protected int  getLeft()        { return left;    }
    protected String  getId()       { return id;      }

    protected boolean isVisible()   { return !hidden; }
    protected void setVisible(boolean visible)   
    { hidden = !visible; }
}// Visual class

class Text extends Visual
{
    private String text;
    int x, y;


    Text( String id, String text, int x, int y, int fontSize, FontMetrics fm, 
	  String color, boolean hidden)
    {
	this.id = id;
	this.text = text;
	this.x = x;
	this.y = y;
	this.left = x;
	width = fm.stringWidth(text);
	this.right = x + width;
	this.fontSize = fontSize;
	this.color = color;
	this.hidden = hidden;

	this.family = pointSlope.ftName;
	this.bold = false;
	this.italic = false;

    } 


    Text( String id, String text, int x, int y, int fontSize, FontMetrics fm, 
	  String color, boolean hidden, String family, boolean italic,
	  boolean bold)
    {
	this(id,text,x,y,fontSize,fm,color,hidden);
	this.family = family;
	this.italic = italic;
	this.bold = bold;
    } 

    protected void setX(int x)      { this.x = x;     }
    protected void setY(int y)      { this.y = y;     }
    protected int  getX()           { return x;       }
    protected int  getY()           { return y;       }
    
    public void translate(int dx, int dy)
    {
	x += dx;
	left += dx;      
	right += dx;
	y += dy;
    }

    public Element makeElement()
    {
	Element t = new Element("text");
	t.setAttribute("id",id);
	if (hidden) t.setAttribute( "hidden", "true" );
	Element coord = new Element("coordinate");
	coord.setAttribute("x",x+"");
	coord.setAttribute("y",y+"");
	Element contents = new Element("contents");
	contents.addContent(text);
	Element style = new Element( "style" );
	Element c = new Element( "color" );
	c.setAttribute("name",color);
	style.addContent( c );
	Element font = new Element( "font" );
	font.setAttribute( "size", fontSize + "");
	if (family != null)
	    font.setAttribute( "family", family);
	if (italic)
	    font.setAttribute( "italic", "true");
	if (bold)
	    font.setAttribute( "bold", "true");
	style.addContent( font );

	t.addContent(coord);
	t.addContent(contents);
	t.addContent(style);

	return t;
    }
}// Text class


class Line extends Visual
{
    protected int x1, y1;
    protected int x2, y2;

    Line( String id, int x1, int y1, int x2, int y2,
	  String color, boolean hidden)
    {
	this.id = id;
	this.x1 = x1;
	this.y1 = y1;
	this.x2 = x2;
	this.y2 = y2;
	this.left = Math.min(x1,x2);
	this.right = Math.max(x1,x2);
	width = right - left;
	this.color = color;
	this.hidden = hidden;

    } 

    public void setX2(int x)     { this.x2 = x;     }
    public void setY2(int y)     { this.y2 = y;     }
    public int getX1()           { return x1;       }
    public int getY1()           { return y1;       }
    public int getX2()           { return x2;       }
    public int getY2()           { return y2;       }

    public void translate(int dx, int dy)
    {
	x1 += dx;
	x2 += dx;
	left += dx;      
	right += dx;
	y1 += dy;
	y2 += dy;
    }


    public Element makeElement()
    {
	Element line = new Element("line");
	line.setAttribute("id",id);
	if (hidden) line.setAttribute( "hidden", "true" );
	Element coord1 = new Element("coordinate");
	coord1.setAttribute("x",x1+"");
	coord1.setAttribute("y",y1+"");
	line.addContent(coord1);
	Element coord2 = new Element("coordinate");
	coord2.setAttribute("x",x2+"");
	coord2.setAttribute("y",y2+"");
	line.addContent(coord2);
	Element style = new Element( "style" );
	Element c = new Element( "color" );
	c.setAttribute("name",color);
	style.addContent( c );
	line.addContent(style);

	return line;
    }


}// Line class


class Polyline extends Visual
{
    protected int[] x;
    protected int[] y;

    Polyline( String id, int[] x, int[] y,
	  String color, boolean hidden)
    {
	this.id = id;
	this.x = x;
	this.y = y;
	int min = 99999;
	for(int i=0; i<x.length; i++)
	    if (x[i] < min) min = x[i];

	int max = -1;
	for(int i=0; i<x.length; i++)
	    if (x[i] > max) max = x[i];
	this.left = min;
	this.right = max;
	width = right - left;
	this.color = color;
	this.hidden = hidden;
    } 

    public int[] getX()           { return x;       }
    public int[] getY()           { return y;       }

    public void translate(int dx, int dy)
    {
	for(int i=0; i<x.length; i++)
	{
	    x[i] += dx;
	    y[i] += dy;
	}
	left += dx;      
	right += dx;
    }


    public Element makeElement()
    {
	Element line = new Element("polyline");
	line.setAttribute("id",id);
	if (hidden) line.setAttribute( "hidden", "true" );
	Element coord;
	for(int i=0; i<x.length; i++)
	{
	    coord = new Element("coordinate");
	    coord.setAttribute("x",x[i]+"");
	    coord.setAttribute("y",y[i]+"");
	    line.addContent(coord);
	}
	Element style = new Element( "style" );
	Element c = new Element( "color" );
	c.setAttribute("name",color);
	style.addContent( c );
	line.addContent(style);

	return line;
    }


}// Polyline class

/*
class Vlist
{
    private int leftMargin;
    private int baseLine;
    public Visual head;

    Vlist(int leftMargin, int baseLine)
    {
	this.leftMargin = leftMargin;
	this.baseLine = baseLine;
	head = null;
    }

    void addAfter(Visual v, Visual prec, int dx, int dy)
    {
	if (prec==null)
	{
	    v.setX( leftMargin + dx );
	    v.setY( baseLine + dy );
	    v.next = head;
	    head = v;
	}
	else
	{
	    v.setX( prec.getX() + prec.getWidth() + dx );	 
	    v.setY( baseLine + dy );
	    if (prec.next != null)
	    {
		prec.next.prev = v;
		v.next = prec.next;
	    }
	    prec.next = v;
	    v.prev = prec;

	}
    }
    
}
*/
