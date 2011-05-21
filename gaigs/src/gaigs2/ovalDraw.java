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
import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import java.io.*;

final class ovalDraw extends obj{     //Ovals are 2
    int x1;
    int x2;
    int y1;
    int y2;
    double xc;
    double yc;
    double rad;
    Color C;

    public ovalDraw(String values,Color CType){
        String temp;

        StringTokenizer st= new StringTokenizer(values);
	xc=Format.atof(st.nextToken());
	yc=Format.atof(st.nextToken());
	rad=Format.atof(st.nextToken());
	C=CType;
    }

    public boolean execute(Graphics g, double zoom, int vertoff, int horizoff){
        double maxs;

        maxs=maxsize*zoom;
	if (zoom!=1){
	    vertoff=vertoff+(int)(maxsize-maxsize*zoom)/2;
	    horizoff=horizoff+(int)(maxsize-maxsize*zoom)/2;
	}
	x1=(int)Math.round((maxs*xc)-(maxs*rad))+horizoff;
        y1=(int)Math.round((maxs-(maxs*yc))-(maxs*rad))+vertoff;
        x2=(int)Math.round(maxs*rad*2);
        y2=(int)Math.round(maxs*rad*2);
        g.setColor(C);
        g.drawOval(x1,y1,x2,y2);
	return true;
    }

}
