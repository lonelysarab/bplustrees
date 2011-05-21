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

package xaal.adapters.jhave.objects;

import java.awt.Font;
import java.awt.Graphics;

import xaal.objects.graphical.Style;
import xaal.objects.graphical.Text;

public final class TextDraw extends BaseObject implements Cloneable { // TextDraw are 6

    private String family;
    private boolean italic;
    private boolean bold;
    private int fontSize;

    private int y;

    private int x;

    private String textString;
    
    public TextDraw(Text text, Style s) {
        super(text);
        family = s.getFont().getFamily();
        fontSize = s.getFont().getSize();
	italic = s.getFont().isItalic();
	bold = s.getFont().isBold();
        textString = text.getContents();
        x = text.getCoordinate().getX();
        y = text.getCoordinate().getY();
	//System.out.println( textString + " at " + x + " , " + y);
    }

    public Object clone() throws CloneNotSupportedException
    {
	return super.clone();
    }

    public int getX()
    {
	return  x;
    }

    public int getY()
    {
	return  y;
    }

    public void incX(int dx)
    {
	this.x += dx;
    }

    public void incY(int dy)
    {
	this.y += dy;
    }

    public void setX(int x)
    {
	this.x = x;
    }

    public void setY(int y)
    {
	this.y = y;
    }

    public boolean execute(Graphics g, double zoom, int vertoff, int horizoff) {
	if (isVisible())
	{
	    zoom = zoom * getScale();
	    if (zoom != 1) {
		vertoff = vertoff + (int) (maxsize - maxsize * zoom) / 2;
		horizoff = horizoff + (int) (maxsize - maxsize * zoom) / 2;
	    }
	    int scaledX = (int) Math.round(zoom * x) + horizoff;
	    int scaledY = (int) Math.round(zoom * y) + vertoff;
	    int scaledSize = (int) Math.round(zoom * fontSize);
	    g.setColor(color);
	
	    int style = Font.PLAIN;
	    if (italic) style |= Font.ITALIC;
	    if (bold)   style |= Font.BOLD;
	    // should avoid creating a new Font object each time
	    g.setFont( new Font( family,  style, scaledSize) );
	    g.drawString(textString, scaledX, scaledY);
	    return true;
	}
	else return false;
    }

    public String getString() { return textString; }
}
