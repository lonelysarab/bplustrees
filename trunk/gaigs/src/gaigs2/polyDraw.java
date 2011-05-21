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

public final class polyDraw extends obj{     //polyDraw are 11
    int numpoints;
    double[] txpoints;
    double[] typoints;
    int[] xpoints;
    int[] ypoints;
    Color C;

    public polyDraw(String values, Color CType){
        String temp;
        int x;

        StringTokenizer st= new StringTokenizer(values);
	//System.out.println(values);
	numpoints=Format.atoi(st.nextToken());
	x=0;
	txpoints=new double[numpoints];
	typoints=new double[numpoints];
	while (st.hasMoreTokens()){
	    txpoints[x]=Format.atof(st.nextToken());
	    typoints[x]=Format.atof(st.nextToken());
	    x++;
	}
	C=CType;

    }

    public boolean execute(Graphics g, double zoom, int vertoff, int horizoff){
        int x;
        double maxs;

        x=0;

        maxs=maxsize*zoom;
	if (zoom!=1){
	    vertoff=vertoff+(int)(maxsize-maxsize*zoom)/2;
	    horizoff=horizoff+(int)(maxsize-maxsize*zoom)/2;
	}

        xpoints=new int[numpoints];
        ypoints=new int[numpoints];

        while (x<numpoints){
            xpoints[x]=(int)Math.round(maxs*txpoints[x])+horizoff;
            ypoints[x]=(int)Math.round(maxs-(maxs*typoints[x]))+vertoff;
            x++;
        }
        g.setColor(C);
        g.drawPolygon(xpoints,ypoints,numpoints);
	return true;
    }
}
