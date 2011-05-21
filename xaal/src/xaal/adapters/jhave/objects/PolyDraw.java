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
import java.awt.Graphics;
import java.util.Iterator;

import xaal.objects.graphical.Coordinate;
import xaal.objects.graphical.Line;
import xaal.objects.graphical.Polygon;
import xaal.objects.graphical.Polyline;

public final class PolyDraw extends BaseObject implements Cloneable {     //polyDraw are 11
    private int[] xpoints;
    private int[] ypoints;
    public boolean polyline = false;
    public boolean closed = false;

    public PolyDraw(Polygon p){
        this(p.getPoints(), p.getPointCount(), p);
        if (p instanceof Polyline)
	{
            polyline = true;
	    if (((Polyline)p).isClosed())
		closed = true;
	}
    }

    public PolyDraw(Line l) {
        this(l.getPoints(), l.getPointCount(), l);
    }

    public int[] getXpoints() { return xpoints; }

    public int[] getYpoints() { return ypoints; }

    public void setX(int index, int value) 
    { 
	xpoints[index] = value;
    }

    public void setY(int index, int value) 
    { 
	ypoints[index] = value;
    }

    public PolyDraw(Iterator points, int pointCount, Polygon p) {
        super(p);
        xpoints = new int[pointCount];
        ypoints = new int[pointCount];
        int i = 0;
        while(points.hasNext()) {
            Coordinate c = (Coordinate) points.next();
            xpoints[i] = c.getX();
            ypoints[i] = c.getY();
            i++;
        }
    }

    public Object clone() throws CloneNotSupportedException
    {
	PolyDraw copy = (PolyDraw) super.clone();

	copy.xpoints = xpoints.clone();
	copy.ypoints = ypoints.clone();
	return copy;       
    }

    public boolean execute(Graphics g, double zoom, int vertoff, int horizoff){

	if (isVisible())
	{
	    zoom = zoom * getScale();

	    if (zoom != 1) {
		vertoff = vertoff + (int) (maxsize - maxsize * zoom) / 2;
		horizoff = horizoff + (int) (maxsize - maxsize * zoom) / 2;
	    }
	    setStroke(g, zoom);
	    int pointCount = xpoints.length;
	    int[] scaledXpoints = new int[pointCount];
	    int[] scaledYpoints = new int[pointCount];
	    for (int i=0; i < pointCount; i++) {
		scaledXpoints[i] = 
                         (int) (Math.round(zoom*xpoints[i]) + horizoff);
		scaledYpoints[i] = 
                         (int) (Math.round(zoom*ypoints[i]) + vertoff);
	    }
	    if (pointCount == 2) 
            {
		g.setColor(color);        
		g.drawLine(scaledXpoints[0], scaledYpoints[0], 
			   scaledXpoints[1], scaledYpoints[1]);
	    } 
            else 
            {
		if (closed && (fillColor != null)) 
		{
		    g.setColor(fillColor);
		    g.fillPolygon(scaledXpoints, scaledYpoints, pointCount);
		}

		//		if (fillColor == null || 
		//  (color != null && !color.equals(fillColor))) 
		//{
		g.setColor(color);
		
		if (polyline)
		    g.drawPolyline(scaledXpoints,scaledYpoints,pointCount);
		else
		    g.drawPolygon(scaledXpoints,scaledYpoints,pointCount);
		//}
	    }
	    return true;
	}
	else return false;
    }

}
