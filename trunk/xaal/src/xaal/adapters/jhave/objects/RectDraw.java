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
import xaal.objects.graphical.Rectangle;
import xaal.objects.graphical.Style;

public class RectDraw extends BaseObject {
    private int x;
    private int y;
    private int width;
    private int height;
    private int rx;
    private int ry;

    
    public RectDraw(Rectangle rect, Style s) {
        super(rect);
        Coordinate start = rect.getStartCoordinate();
        Coordinate end = rect.getEndCoordinate();
        x = start.getX();
        y = start.getY();
        width = end.getX() - start.getX();
        height = end.getY() - start.getY();
        rx = rect.getRound()[0];
        ry = rect.getRound()[1];
    }

    public boolean execute(Graphics g, double zoom, int vertoff, int horizoff) {
        zoom = zoom * getScale();
        if (zoom != 1) {
            vertoff = vertoff + (int) (maxsize - maxsize * zoom) / 2;
            horizoff = horizoff + (int) (maxsize - maxsize * zoom) / 2;
        }
        int scaledX = (int) (Math.round(zoom*x) + horizoff);
        int scaledY = (int) (Math.round(zoom*y) + vertoff);
        int scaledWidth = (int) (Math.round(zoom*width));
        int scaledHeight = (int) (Math.round(zoom*height));
        setStroke(g, zoom);
        int scaledRx = (int) (Math.round(zoom*rx));
        int scaledRy = (int) (Math.round(zoom*ry));
        if (scaledRx == 0 && fillColor != null) {
            g.setColor(fillColor);
            g.fillRect(scaledX, scaledY, scaledWidth, scaledHeight);
        } else if (scaledRx != 0 && fillColor != null) {
            g.setColor(fillColor);
            g.fillRoundRect(scaledX, scaledY, scaledWidth, scaledHeight, scaledRx, scaledRy);            
        }
        if (scaledRx == 0 && (fillColor == null || (color != null && !color.equals(fillColor))) ){
            g.setColor(color);
            g.drawRect(scaledX, scaledY, scaledWidth, scaledHeight);
        } else if (scaledRx != 0 && (fillColor == null || (color != null && !color.equals(fillColor)))){
            g.setColor(color);
            g.drawRoundRect(scaledX, scaledY, scaledWidth, scaledHeight, scaledRx, scaledRy);                        
        }
        return true;
    }
}
