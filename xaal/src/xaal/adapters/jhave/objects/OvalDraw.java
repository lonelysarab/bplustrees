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

import xaal.objects.graphical.Coordinate;
import xaal.objects.graphical.Ellipse;
import xaal.objects.graphical.Radius;
import xaal.objects.graphical.Style;

public class OvalDraw extends BaseObject {
    private int x;
    private int y;

    private int width;
    private int height;
    
    public OvalDraw(Ellipse e, Style s) {
        super(e);
        Coordinate center = e.getCenter();
        Radius rad = e.getRadius();
        x = center.getX() - rad.getX();
        y = center.getY() - rad.getY();
        width = rad.getX()*2;
        height = rad.getY()*2;
    }

    public boolean execute(Graphics g, double zoom, int vertoff, int horizoff) {
        if (width == 0 || height == 0)
            return false;
        zoom = zoom * getScale();
        if (zoom != 1) {
            vertoff = vertoff + (int) (maxsize - maxsize * zoom) / 2;
            horizoff = horizoff + (int) (maxsize - maxsize * zoom) / 2;
        }
        
        int scaledX = (int) Math.round(zoom * x) + horizoff;
        int scaledY = (int) Math.round(zoom * y) + vertoff;
        int scaledWidth = (int) Math.round(zoom * width);
        int scaledHeight = (int) Math.round(zoom * height);
        setStroke(g, zoom);
        if (fillColor != null) {
            g.setColor(fillColor);
            g.fillOval(scaledX, scaledY, scaledWidth, scaledHeight);
        } 
        if (fillColor == null || (color != null && !color.equals(fillColor))) {
            g.setColor(color);
            g.drawOval(scaledX, scaledY, scaledWidth, scaledHeight);
        }
        return true;
    }

}
