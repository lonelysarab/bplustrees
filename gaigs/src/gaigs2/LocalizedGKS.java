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

// LocalizedGKS.java
//  an intermediary for GKS
//  Set the region of the screen for the LocalizedGKS object to draw in.
//  Then you can send draw messages to the LocalizedGKS object in normalized [0,1]x[0,1] coordinates,
//   and the LGKS object will automatically translate the input coords into the corresponding coords
//   within the bounds that you gave to the object, and send that to GKS.
//
// - Mike Gilmore
// - 15 June 2005

package gaigs2;
import java.text.DecimalFormat;


class LocalizedGKS {

    // PRIVATE:
    
    private double bound_x1, bound_y1, bound_x2, bound_y2; // draw bound coords, global frame of reference
    private double span_x, span_y;                         // calculated, how far between upper&lower bounds

    private double text_height;
    
    // PUBLIC:

    // transforms an x-coord from [0,1] to [bound_x1,bound_x2]
    public double transform_x_coord(double x_coord) {
	return x_coord * span_x + bound_x1;
    }
    // transforms a  y-coord from [0,1] to [bound_y1,bound_y2]
    public double transform_y_coord(double y_coord) {
	return y_coord * span_y + bound_y1;
    }
    // simple scaling for x,y directions
    public double scale_x_dist(double x) {
	return x * span_x;
    }
    public double scale_y_dist(double y) {
	return y * span_y;
    }

    public double minScale(double textheight) {
	return Math.min(textheight*span_x, textheight*span_y);
    }

    public double scaledVectorMagnitudeRatio(double x1, double y1, double x2, double y2) {
	double olx = x1-x2;
	double oly = y1-y2;
	double slx = (x1-x2)*span_x;
	double sly = (y1-y2)*span_y;
	return Math.sqrt( slx*slx + sly*sly ) / Math.sqrt( olx*olx + oly*oly );
    }
    

    // set_bounds(x1,y1,x2,y2)
    //  tell the LGKS object where in normalized global coordinates it should be drawing.
    // note: any bounds changing should be passed on to any children it concerns..
    public void set_bounds(double x1, double y1, double x2, double y2) {
	bound_x1 = x1;
	bound_y1 = y1;
	bound_x2 = x2;
	bound_y2 = y2;
	span_x = bound_x2 - bound_x1;
	span_y = bound_y2 - bound_y1;
    }

    
    // CONSTRUCTORS

    public LocalizedGKS() {
	set_bounds(0.0, 0.0, 1.0, 1.0);
	text_height = 0.02;
    }
    public LocalizedGKS(double x1, double y1, double x2, double y2) {
	set_bounds(x1, y1, x2, y2);
	text_height = Math.min(0.02*span_x, 0.02*span_y);
    }


    //  UNCHANGED FUNCTIONS, just pass it on to GKS. (lil inefficient)


    // Set the interior style for a fill area.  The int color is
    // typically obtained from the hex string by StructureType's
    // colorStringToInt method.  Sorry, only the constants bsClear and
    // bsSolid are presently supported for style.  And, of course, a
    // clear fill area is just ...
    public void set_fill_int_style(int style, int color, LinkedList seginfo, draw d) {
	GKS.set_fill_int_style(style, color, seginfo, d);
    }

    // Set the color (as a Java int) for drawing text.  Usually this
    // int is obtained from the hex string by your having called
    // colorStringToTextColorInt in StructureType.java
    public void set_textline_color(int color, LinkedList seginfo, draw d) {
	GKS.set_textline_color(color, seginfo, d);
    }

    public void set_line_width(int thickness, LinkedList seginfo, draw d) {
	GKS.set_line_width(thickness, seginfo, d);
    }
    public void set_url(String url, LinkedList seginfo, draw d) {
	GKS.set_url(url, seginfo, d);
    }
    public void windows(int numwin, LinkedList seginfo, draw d) {
	GKS.windows(numwin, seginfo, d);
    }
    public void jump(int jumpfactor, LinkedList seginfo, draw d) {
	GKS.jump(jumpfactor, seginfo, d);
    }


    // LOCALIZED INTERMEDIARY FUNCTIONS

    
    // Draw a fill area with the specified number of points and their
    // coordinates
    public void fill_area(int numpts, double ptsx[], double ptsy[], LinkedList seginfo, draw d) {
        double transformed_ptsx[] = new double[numpts];
	double transformed_ptsy[] = new double[numpts];
	for(int i = 0; i < numpts; i++) {
	    transformed_ptsx[i] = transform_x_coord(ptsx[i]);
	    transformed_ptsy[i] = transform_y_coord(ptsy[i]);
	}
	GKS.fill_area(numpts, transformed_ptsx, transformed_ptsy, seginfo, d);
    }


    public void animated_fill_area(int numpts, double ptsx[], double ptsy[],
					  int num_path_pts, double pathx[], double pathy[], int num_frames,
				 LinkedList seginfo, draw d) {
	int i;
        double transformed_ptsx[] = new double[numpts];
	double transformed_ptsy[] = new double[numpts];
	for( i = 0; i < numpts; i++ ) {
	    transformed_ptsx[i] = transform_x_coord(ptsx[i]);
	    transformed_ptsy[i] = transform_y_coord(ptsy[i]);
	}
	double transformed_pathx[] = new double[num_path_pts];
	double transformed_pathy[] = new double[num_path_pts];
	for( i = 0; i < num_path_pts; i++ ) {
	    transformed_pathx[i] = transform_x_coord(pathx[i]);
	    transformed_pathy[i] = transform_y_coord(pathy[i]);
	}
	GKS.animated_fill_area(numpts, transformed_ptsx, transformed_ptsy, num_path_pts,
			       transformed_pathx, transformed_pathy, num_frames, seginfo, d);
    }

    
    // Draw a polyline with the specified number of points and their
    // coordinates
    public void polyline(int numpts, double ptsx[], double ptsy[], LinkedList seginfo, draw d) {
	double transformed_ptsx[] = new double[numpts];
	double transformed_ptsy[] = new double[numpts];
	for(int i = 0; i < numpts; i++) {
	    transformed_ptsx[i] = transform_x_coord(ptsx[i]);
	    transformed_ptsy[i] = transform_y_coord(ptsy[i]);
	}
	GKS.polyline(numpts, transformed_ptsx, transformed_ptsy, seginfo, d);
    }
    
    // Set the text alignment.  Choices for horiz and vert are:
    //     final static int TA_CENTER    = 0;	
    //     final static int TA_LEFT      = 1;
    //     final static int TA_RIGHT     = 2;
    // 
    //     final static int TA_BASELINE  = 0;
    //     final static int TA_BOTTOM    = 1;
    //     final static int TA_TOP       = 2;
    // 
    public void set_text_align(int horiz, int vert, LinkedList seginfo, draw d) {
	GKS.set_text_align(horiz, vert, seginfo, d);
    }

    // Change the font size
    public void set_text_height(double height, LinkedList seginfo, draw d) {
	// this affects text length as well. choose whichever factor makes it smaller
// 	if(span_x < span_y)
// 	    height = height * span_x;
// 	else
// 	    height = height * span_y;
	//GKS.set_text_height(height, seginfo, d);
	text_height = height;//0.03;
	//System.out.println("Text height set: " + text_height);
    }
    
    public double get_text_height() {
	return text_height;
    }
    
    // Draw your text at the specified coordinate
    public void text(double x,double y, String str, LinkedList seginfo, draw d) {
        x = transform_x_coord(x);
	y = transform_y_coord(y);
	double height = Math.min(text_height*span_x, text_height*span_y);
	// System.out.println("Text height set to: " + height);
	GKS.set_text_height( height, seginfo,d);
	GKS.text(x, y, str, seginfo, d);
    }
   
    // Draw an ellipse from start angle thru end angle
    public void ellipse(double x, double y, double stangle, double endangle, double xradius, double yradius,
    LinkedList seginfo, draw d) {
        x = transform_x_coord(x);
	y = transform_y_coord(y);
	xradius = scale_x_dist(xradius);
	yradius = scale_y_dist(yradius);
	GKS.ellipse(x, y, stangle, endangle, xradius, yradius, seginfo, d);
    }
 
    // Draw a (outlined) circle
    public void circle(double x, double y, double radius, LinkedList seginfo, draw d) {
        x = transform_x_coord(x);
	y = transform_y_coord(y);
	if(span_x < span_y)
	    radius = radius * span_x;
	else
	    radius = radius * span_y;
	GKS.circle(x, y, radius, seginfo, d);
    }
    
    
    // Draw a filled circle
    public void circle_fill(double x, double y, double radius, LinkedList seginfo, draw d) {
        x = transform_x_coord(x);
	y = transform_y_coord(y);
	if(span_x < span_y)
	    radius = radius * span_x;
	else
	    radius = radius * span_y;
	GKS.circle_fill(x, y, radius, seginfo, d);
    }
    

    // It didnt work as it was.
    // plan: find coords as vectors from center, scale vectors, translate back.
    // useful for highlighting, perhaps. local affect.
    // both this and set_bounds should be appropriately passed on to children in a LocalizedStructure heirarchy.
    // Stays centered where is, and aspect ratio the same.

    // write another one for that, one to keep bot.right corner same? thou that's not really necessary.
    public void scale(double scalefactor) {
	//System.out.println("LGKS.scale(f) called.");
        return;
    }
}



