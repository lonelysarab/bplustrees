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

/*
  Object numbers          expected input
  --------------          --------------
  1:rectangle             x1,y1   x2,y2  (top left, bottom right)
  2:Ovals                 x1,y1   x2,y2  (top left, bottom right) {Rectangle the oval is in}
  3:Lines                 x1,y1   x2,y2  (starting coordinnate, ending coordinate)
  4:Filled rectangles     x1,y1   x2,y2  (top left, bottom right)
  5:Filled ovals          x1,y1   x2,y2  (top left, bottom right) {Rectangle the oval is in}
  6:Text Output           x,y     String (The bottom-left of the text and then the text)
  7:Color                 color          (number 1-7, see colorSet)
*/


public abstract class obj extends Object{
    public static int maxsize=GaigsAV.preferred_width;  // This was 480 originally.  Playing with it fits more or less
    // of the data structure graphic in the window.  For instance, 400
    // fits more of the data structure in the window, but it also causes
    // it to be slightly off center and the font produces text slightly
    // to large to fit in nodes.
    //FIXME -- maxsize really oughtn't be static... it should be static across the same
    //graphwin, but not across all GaigsAV instances.  However, this would be a painful
    //experiment in the art of cut & paste to fix, and I don't feel like screwing
    //everything up right now.

    public obj() {}
    public void changeMax(int max){
	if (max>GaigsAV.preferred_width)
	    maxsize=max;
	else
	    maxsize=GaigsAV.preferred_width;
    }

    // function path_finder: Receives: number of points in path pathx
    // and pathy arrays of (x,y) coords number of frames Returns:
    // which-seg -- an array whose size is the number of frames.

    // After path_finder executes, which-seg[i] contains the index in
    // the path arrays to use during frame i, i = 0, 1,
    // ... num_frames.  That is, during frame i, delta_x and delta_y
    // should be pathx[i+1]-pathx[i] and pathy[i+1]-path[i]
    // respectively.  frames_in_seg[i] contains the number of frames
    // in segments i, that is the segment from pathxy[i] to
    // pathxy[i+1].
    protected void path_finder(int num_points, double pathx[], double pathy[], int num_frames, int which_seg[], int frames_in_seg[]) {

	double[] distrib;	// contains the accumulated percentage
				// of time (frames) to spend on each
				// segment of the path
	distrib = new double[num_points - 1];
	
	double total_length = 0;
	for (int i1 = 0; i1 < num_points - 1; i1++) {
	    total_length += Math.sqrt( (pathx[i1+1] - pathx[i1])*(pathx[i1+1] - pathx[i1]) +
				       (pathx[i1+1] - pathy[i1])*(pathx[i1+1] - pathy[i1]) );
	}
	for (int i2 = 0; i2 < num_points - 1; i2++) {
	    distrib[i2] = (i2 == 0 ? (Math.sqrt( (pathx[i2+1] - pathx[i2])*(pathx[i2+1] - pathx[i2]) +
						 (pathx[i2+1] - pathy[i2])*(pathx[i2+1] - pathy[i2]) ) / total_length)
			   : (distrib[i2 - 1] + Math.sqrt( (pathx[i2+1] - pathx[i2])*(pathx[i2+1] - pathx[i2]) +
							  (pathx[i2+1] - pathy[i2])*(pathx[i2+1] - pathy[i2]) ) / total_length ) );
	}

	int current_dist_index = 0;
	for (int fr = 0; fr <= num_frames; fr++) {
	    if ( ( (double) fr ) / ( (double) num_frames ) >= distrib[current_dist_index] ) {
		if (current_dist_index < num_points - 2) current_dist_index++; // Inner if is guard against rounding error
				// the last time throught he loop
	    }
	    which_seg[fr] = current_dist_index;
	}
	for (int j = 0; j < num_points - 1; j++) frames_in_seg[j] = 0;
	for (int k = 0; k <= num_frames; k++) frames_in_seg[which_seg[k]]++;
    }

    public abstract boolean execute(Graphics g, double zoom, int vertoff, int horizoff);
}
