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

public final class fillArcDraw extends obj{     //arcs are 14
    int x;
    int y;
    int width;
    int height;
    int sangle;
    int arcangle;

    double dx;
    double dy;
    double radx;
    double rady;
    Color C;

    public fillArcDraw(String values, Color CType){

        double radx;
        double rady;

        StringTokenizer st= new StringTokenizer(values);
	dx=Format.atof(st.nextToken());
	dy=Format.atof(st.nextToken());
	sangle=(int)Math.round((Format.atof(st.nextToken())/6.28)*360);
	arcangle=(int)Math.round(((Format.atof(st.nextToken())/6.28)*360)-sangle);
	radx=Format.atof(st.nextToken());
	rady=Format.atof(st.nextToken());
	C=CType;
    }

    public boolean execute(Graphics g, double zoom, int vertoff,  int horizoff){
        double maxs;

        maxs=maxsize*zoom;
	if (zoom!=1){
	    vertoff=vertoff+(int)(maxsize-maxsize*zoom)/2;
	    horizoff=horizoff+(int)(maxsize-maxsize*zoom)/2;
	}

        x=(int)Math.round((maxs*dx)-(maxs*radx))+horizoff;
        y=(int)Math.round((maxs-(maxs*dy))-(maxs*rady))+vertoff;
        width=(int)Math.round(maxs*radx);
        height=(int)Math.round(maxs*rady);
        g.setColor(C);
        g.fillArc(x,y,width,height,sangle,arcangle);
	return true;
    }

}
