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
import java.text.DecimalFormat;

/** Snapshot codes
 *
 *  29 - rectangle draw
 *  2 - oval draw
 *  5 - fill oval
 *  6 - string
 *  7 - line & text color
 *  8 - fill color
 *  9 - text color (possible to ignore)
 *  10 - text height
 *  11 - polydraw
 *  4 - fill poly
 *  64 - animated fill poly
 *  14 - arc draw
 *  30 - fill arc
 *  12 - text style, centered horizontal/vertical
 *  54 - url.  Note, when bring up multiple algorithms, the URL's for the most recently
 *             run algorithm are posted in the upper browser frame
 *  20 - number of windows.  For static algorithms, 1, 2, 3, 4 have the obvious meaning.
 *                  For dynamic algos, 5 means 1 algo & 2 windows with the capability to load
 *                  2 window algorithm.  6 means 1 algo with 3 windows -- no second algorithm
 *                  should be loaded.  7 means 1 algo with 4 windows again no second algorithm
 *                  should be loaded.
 *  21 - scale factor
 *  22 - jump factor
 *
 *  For 20, 21, 22, the last factor loaded is the one that will affect all snapshots in
 *  the show
 */
class GKS {
    public static void set_fill_int_style(int style, int color, LinkedList seginfo, draw d) {
        String input = "8  " + color + " " + style;
        
        seginfo.append(d.dothis(input));
        
        //System.out.println("set_fill_int_style: " + input);
    }
    
    public static void fill_area(int numpts, double ptsx[], double ptsy[], LinkedList seginfo, draw d) {
        DecimalFormat format = new DecimalFormat("#########0.000000");
        StringBuffer buffer = new StringBuffer(256);
        
        buffer.append("4  ");
        buffer.append(numpts);
        for (int i = 0; i < numpts; ++i) {
            buffer.append(" ");
            buffer.append(format.format(ptsx[i]));
            buffer.append(" ");
            buffer.append(format.format(ptsy[i]));
        }
        seginfo.append(d.dothis(buffer.toString()));
        
        //System.out.println("fill_area: " + buffer.toString());
    }


    public static void animated_fill_area(int numpts, double ptsx[], double ptsy[],
					  int num_path_pts, double pathx[], double pathy[], int num_frames,
				 LinkedList seginfo, draw d) {

        DecimalFormat format = new DecimalFormat("#########0.000000");
        StringBuffer buffer = new StringBuffer(512);
        
        buffer.append("64  ");
        buffer.append(numpts);
        for (int i = 0; i < numpts; ++i) {
            buffer.append(" ");
            buffer.append(format.format(ptsx[i]));
            buffer.append(" ");
            buffer.append(format.format(ptsy[i]));
        }
        buffer.append(" ");
        buffer.append(num_path_pts);
        for (int j = 0; j < num_path_pts; ++j) {
            buffer.append(" ");
            buffer.append(format.format(pathx[j]));
            buffer.append(" ");
            buffer.append(format.format(pathy[j]));
        }
        buffer.append(" ");
        buffer.append(num_frames);
        seginfo.append(d.dothis(buffer.toString()));
        
        // //System.out.println("animated_fill_area: " + buffer.toString());
    }

    
    public static void polyline(int numpts, double ptsx[], double ptsy[], LinkedList seginfo, draw d) {
        DecimalFormat format = new DecimalFormat("#########0.000000");
        StringBuffer buffer = new StringBuffer(256);
        
        buffer.append("11 ");
        buffer.append(numpts);
        for (int i = 0; i < numpts; ++i) {
            buffer.append(" ");
            buffer.append(format.format(ptsx[i]));
            buffer.append(" ");
            buffer.append(format.format(ptsy[i]));
        }
        seginfo.append(d.dothis(buffer.toString()));
        
        //System.out.println("polyline: " + buffer.toString());
    }
    
    public static void set_textline_color(int color, LinkedList seginfo, draw d) {
        String input = "7  " + color;
        seginfo.append(d.dothis(input));
        
        //System.out.println("set_textline_color: " + input);
    }
    
    public static void set_text_align(int horiz, int vert, LinkedList seginfo, draw d) {
        String input = "12 " + horiz + " " + vert;
        seginfo.append(d.dothis(input));
        
        //System.out.println("set_text_align: " + input);
    }
    
    public static void set_text_height(double height, LinkedList seginfo, draw d) {
        DecimalFormat format = new DecimalFormat("#########0.000000");
        String input = "10 " + format.format(height);
        seginfo.append(d.dothis(input));
        
        //System.out.println("set_text_height: " + input);
    }
    
    public static void text(double x,double y, String str, LinkedList seginfo, draw d) {
        DecimalFormat format = new DecimalFormat("#########0.000000");
        String input = "6  " + format.format(x) + " " + format.format(y) + " " + str;
        seginfo.append(d.dothis(input));
        
        //System.out.println("text: " + input);
    }
    
    // Never used
    public static void set_line_width(int thickness, LinkedList seginfo, draw d) {
        String input = "13 " + thickness;
        seginfo.append(d.dothis(input));
        
        //System.out.println("set_line_width: " + input);
    }
    
    public static void ellipse(double x, double y, double stangle, double endangle, double xradius, double yradius,
    LinkedList seginfo, draw d) {
        DecimalFormat format = new DecimalFormat("#########0.000000");
        StringBuffer buffer = new StringBuffer(256);
        buffer.append("14 ");
        buffer.append(format.format(x));
        buffer.append(" ");
        buffer.append(format.format(y));
        buffer.append(" ");
        buffer.append(format.format(stangle));
        buffer.append(" ");
        buffer.append(endangle);
        buffer.append(" ");
        buffer.append(xradius);
        buffer.append(" ");
        buffer.append(yradius);
        
        seginfo.append(d.dothis(buffer.toString()));
        
        //System.out.println("ellipse: " + buffer.toString());
    }
    
    public static void circle(double x, double y, double radius, LinkedList seginfo, draw d) {
        DecimalFormat format = new DecimalFormat("#########0.000000");
        
        String input = "2  " + format.format(x) + " " + format.format(y) + " " + format.format(radius);
        seginfo.append(d.dothis(input));
        
        //System.out.println("circle: " + input);
    }
    
    
    public static void circle_fill(double x, double y, double radius, LinkedList seginfo, draw d) {
        DecimalFormat format = new DecimalFormat("#########0.000000");
        
        String input = "5  " + format.format(x) + " " + format.format(y) + " " + format.format(radius);
        seginfo.append(d.dothis(input));
        
        //System.out.println("circle_fill: " + input);
    }
    
    // Never used
    public static void set_url(String url, LinkedList seginfo, draw d) {
        String input = "54 " + url;
        seginfo.append(d.dothis(input));
        
        //System.out.println("set_url: " + input);
    }
    
    public static void windows(int numwin, LinkedList seginfo, draw d) {
        String input = "20 " + numwin;
        seginfo.append(d.dothis(input));
        
        //System.out.println("windows: " + input);
    }
    
    public static void jump(int jumpfactor, LinkedList seginfo, draw d) {
        String input = "22 " + jumpfactor;
        seginfo.append(d.dothis(input));
        
        //System.out.println("jump: " + input);
    }
    
    public static void scale(double scalefactor, LinkedList seginfo, draw d) {
        DecimalFormat format = new DecimalFormat("#########0.000000");
        String input = "21 " + format.format(scalefactor);
        seginfo.append(d.dothis(input));
        
        //System.out.println("scale: " + input);
    }
}

/* GColor:Array[1..9] of Tcolor=(clBlack,clBlue,clLime,
                               clRed,clPurple,clTeal,
                               clYellow,clWhite,clGray);
 
 { General information pertaining to more than one graphic primitive:
 
   1.  Al GAIGS graphics output is done on a normalized (0.0, 0.0) -
       (1.0, 1.0) screen.  Points on this screen are called normalized
       device coordinates.
 
   2. An NDCArray is an array of up to five reals representing the x
      or y coordinates of point on the normalized screen.
 
   3. The type TBrushStyle is defined by:
 
       TBrushStyle = (bsSolid, bsClear, bsHorizontal, bsVertical, bsFDiagonal,
                              bsBDiagonal, bsCross, bsDiagCross);
 
   4. The colors supported by the graphic primitives are:
        The valules of the Colr parameter are:
 
     Black  =  1;
     Blue   =  2;
     Green  =  3;
     Red    =  4;
     Magenta	= 5;
     LightBlue=6;
     Yellow=   7;
     White=    8;
     LightGray=9;
 
   4.  All graphic primitives are added to the "seginfo" list that is part of
       the snapshot being rendered.  Actual output of the primitves does
       not accur until the snapshot is slected for drawing by the GAIGS user.
 
   5. It is also useful to note that the total amount of vertical NDC space
      consumed by the snapshot's title/caption can be computed by:
 
       (1.5* snapsht.Title.Size * snapsht.TitleHeight )
 
   }
 
 */


