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
import java.io.*;
import java.awt.*;
import java.util.*;

public class animated_demo_str extends StructureType {

    private int str_color;

    private double x[], y[];

    public animated_demo_str () {
	super();
	x = new double[5];
	y = new double[5];
    }

    public void calcDimsAndStartPts(LinkedList llist, draw d) {
	super.calcDimsAndStartPts(llist, d);
    }

    void drawStructure (LinkedList llist, draw d) {

	double xline[], yline[];
	double xpath[], ypath[];

	super.drawStructure(llist,d);
	xline = new double [2];
	yline = new double [2];
        yline[0]=TitleEndy;
        yline[1]=yline[0];
        xline[0]= 0;
        xline[1]= 1;

	xpath = new double [4];
	ypath = new double [4];
        ypath[0]=0.0;
        ypath[1]=0.5;
	ypath[2]=0.5;
	ypath[3]=0.0;
        xpath[0]=0.0;
        xpath[1]=0.5;
	xpath[2]=0.0;
	xpath[3]=0.0;



        // The polyline is drawn immediately under the title/caption
	GKS.scale(0.75,llist,d);
        GKS.polyline(2,xline,yline,llist,d);
        GKS.set_fill_int_style(bsSolid,str_color,llist,d);
        GKS.animated_fill_area(5,x,y,4,xpath,ypath,60,llist,d);
        GKS.set_fill_int_style(bsClear,White,llist,d);
        //The final Set_fill_int_style is necessary to insure that later
        // titles are displayed on a white background. 
    }

    boolean emptyStruct() {
	return(false);
    }

    void loadStructure (StringTokenizer st, LinkedList llist, draw d)
	throws VisualizerLoadException  {

	String tline;

	if (st.hasMoreTokens()) {
	    tline = st.nextToken();
	}
	else 
	    throw (new VisualizerLoadException ("End of data in Demo_str when expecting color number"));
	str_color = Format.atoi(tline);
	for (int i = 0; i < 4; i++)  {
	    if (st.hasMoreTokens()) {
		tline = st.nextToken();
	    }
	    else 
		throw (new VisualizerLoadException ("End of data in Demo_str when expecting x coord"));
	    x[i] = Format.atof(tline);
	    if (st.hasMoreTokens()) {
		tline = st.nextToken();
	    }
	    else 
		throw (new VisualizerLoadException ("End of data in Demo_str when expecting y coord"));
	    y[i] = Format.atof(tline);
	}
	x[4] = x[0];
	y[4] = y[0];
	if (st.hasMoreTokens()) {
	    tline = st.nextToken();
	}
	else 
	    throw (new VisualizerLoadException ("End of data in Demo_str when expecting end of snapshot marker"));

    }

}
