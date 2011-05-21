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

public final class rectDraw extends obj{     //rectangles are 1
    // Note: Rectangle is never used.  If it ever is used, it won't work as it is below
    // since it is parsing integer coordinates instead of NDC coordinates
    int x1;
    int x2;
    int y1;
    int y2;
    Color C;

    public rectDraw(String values,Color CType){
        String temp;

        StringTokenizer st= new StringTokenizer(values);
	x1=Format.atoi(st.nextToken());
	y1=Format.atoi(st.nextToken());
	x2=Format.atoi(st.nextToken())-x1;
	y2=Format.atoi(st.nextToken())-y1;
	C=CType;
    }

    public boolean execute(Graphics g, double zoom, int vertoff, int horizoff){
        g.setColor(C);
        g.drawRect(x1,y1,x2,y2);
	return true;
    }

}
