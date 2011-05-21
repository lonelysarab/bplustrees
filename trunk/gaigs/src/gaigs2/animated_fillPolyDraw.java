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

public final class animated_fillPolyDraw extends obj{     //animated_fillPolyDraw are 64
    int numpoints;
    int num_path_points;
    int num_frames;
    int current_frame;
    double accum_x;
    double accum_y;
    double[] txpoints;
    double[] typoints;
    double[] x_path_points;
    double[] y_path_points;
    int[] xpoints;
    int[] ypoints;
    int[] which_chunk;
    int[] frames_in_chunk;
    Color C;

    public animated_fillPolyDraw(String values, Color CType){
        String temp;
        int x;

	current_frame = 0;
	accum_x = 0;
	accum_y = 0;
        StringTokenizer st= new StringTokenizer(values);
	numpoints=Format.atoi(st.nextToken());
	x=0;
	txpoints=new double[numpoints];
	typoints=new double[numpoints];
	while (x < numpoints){
	    txpoints[x]=Format.atof(st.nextToken());
	    typoints[x]=Format.atof(st.nextToken());
	    x++;
	}

	num_path_points=Format.atoi(st.nextToken());

	x=0;
	x_path_points=new double[num_path_points];
	y_path_points=new double[num_path_points];
	while (x < num_path_points){
	    x_path_points[x]=Format.atof(st.nextToken());
	    y_path_points[x]=Format.atof(st.nextToken());
	    x++;
	}

	num_frames=Format.atoi(st.nextToken());

	which_chunk = new int[num_frames + 1];
	frames_in_chunk = new int[num_path_points - 1];
	path_finder (num_path_points, x_path_points, y_path_points, num_frames, which_chunk, frames_in_chunk);

	C=CType;
    }

    public boolean execute(Graphics g, double zoom, int vertoff, int horizoff){
        int x;
        double maxs;
	double delta_x;
	double delta_y;

        x=0;
        maxs=maxsize*zoom;
	if (zoom!=1){
	    vertoff=vertoff+(int)(maxsize-maxsize*zoom)/2;
	    horizoff=horizoff+(int)(maxsize-maxsize*zoom)/2;
	}

	delta_x = (x_path_points[which_chunk[current_frame]+1] - x_path_points[which_chunk[current_frame]])/((double)(frames_in_chunk[which_chunk[current_frame]]));
	delta_y = (y_path_points[which_chunk[current_frame]+1] - y_path_points[which_chunk[current_frame]])/((double)(frames_in_chunk[which_chunk[current_frame]]));
	accum_x += delta_x; /* * ((double)current_frame); */
	accum_y += delta_y; /* * ((double)current_frame); */

        xpoints=new int[numpoints];
        ypoints=new int[numpoints];

        while (x<numpoints){
            xpoints[x]=(int)Math.round(maxs*(txpoints[x]+accum_x))+horizoff;
            ypoints[x]=(int)Math.round(maxs-(maxs*(typoints[x]+accum_y)))+vertoff;
            x++;
        }
        g.setColor(C);
        g.fillPolygon(xpoints,ypoints,numpoints);
	if (current_frame < num_frames) {
	    current_frame++;
	    return false;
	}
	else {
	    current_frame = 0;
	    accum_x = 0.0;
	    accum_y = 0.0;
	    return true;
	}
    }

}
