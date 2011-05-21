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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import xaal.adapters.jhave.XaalAV;
import xaal.objects.graphical.GraphicalPrimitive;
import xaal.objects.graphical.Style;
import xaal.util.XaalConstants;

public abstract class BaseObject implements Cloneable {
    public static int maxsize = XaalAV.preferred_width;
    
    protected Color color;
    protected Color fillColor;
    protected Style style;
    private double scale;
    protected String id;
    protected boolean visible;

    public BaseObject(GraphicalPrimitive gp) {
        style = gp.getStyle();
        if (style != null) {
            color = style.getColor();
            fillColor = style.getFillColor();
        }
        if (fillColor == null && color == null)
            color = XaalConstants.getDefaultColor();
        scale = gp.getScale();
	id = gp.getId();
	visible = (gp.getOpacity() == 1);
    }

    public boolean isVisible()
    {
	return visible;
    }

    public void setVisible()
    {
	this.visible = true;
    }

    public void setInvisible()
    {
	this.visible = false;
    }

    public void setColor( Color color )
    {
	this.color = color;
    }


    public Color getColor()
    {
	return color;
    }

    public String getId() { return id; }

    public Object clone() throws CloneNotSupportedException
    {
	BaseObject copy = (BaseObject) super.clone();
	
	if (style != null)
	    copy.style = (Style) style.clone();
	return copy;
    }

    public void setStroke(Graphics g, double zoom) {
        Graphics2D g2 = (Graphics2D) g;
        if (style != null && style.getStrokeType().equalsIgnoreCase("dashed"))
            g2.setStroke(new BasicStroke(Math.round(style.getStrokeWidth()*zoom),
                    BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10,
                    new float[] { Math.round(6*zoom) }, 0));
        else if (style != null && style.getStrokeType().equalsIgnoreCase("dotted"))
            g2.setStroke(new BasicStroke(Math.round(style.getStrokeWidth()*zoom),
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10,
                    new float[] { Math.round(2*zoom), Math.round(3*zoom) }, 0));
        else if (style != null)
            g2.setStroke(new BasicStroke( Math.round(style.getStrokeWidth()*zoom)));
        else
            g2.setStroke(new BasicStroke(Math.round(XaalConstants.DEFAULT_PROPERTY_STROKEWIDTH*zoom)));
    }
    
    public double getScale() {
        return scale;
    }
    
    public abstract boolean execute(Graphics g, double zoom, int vertoff, int horizoff);
}
