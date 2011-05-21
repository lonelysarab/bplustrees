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

/**

* Write a description of class VisEdge here.

*

* A class to store information about edges in a visual graph

*   Such as color, highlighting, weight, and positioning

*

* @author Jeff Lucas

* @version June 10th, 2002

*/

package gaigs2;
import gaigs2.*;

public class VisEdge
{


        // instance variables - replace the example below with your own

        private double r, g, b;          //Stores the color of the edge

        private boolean highlighted;     //Is the edge highlighted or not?

        private boolean bidirectional;   //(Add this back in, to tell where

                                         // the arrows are on the edge?)

        private boolean activated;       //Is the edge activated?

        private double weight;           //Stores the weight of this edge

        private double sx, sy;           //The start point for the edge

        private double ex, ey;           //The end point for the edge

                                         //(Note: points in Cartesian coordinates

                                         // on a normalized [0,1] grid)

                                         //(Note2:  If (sx == ex) and (sy == ey)

                                         // then the edge is a self-loop)




        /** Constructor for objects of class VisEdge */

        public VisEdge()
    {


                // Initialize all variables

                r = 0;                  //Default color is black

                g = 0;

                b = 0;

                sx = 0.5;               //Default edge is a self-loop at the center

                sy = 0.5;

                ex = 0.5;

                sy = 0.5;

                weight = 0;             //Default weight is zero

                highlighted = false;    //Highlighting is off by default

                bidirectional = true;   //Bidirectional by default

                activated = false;      //Not activated until you say so


        }




        /**  Constructor that is given beginning values  **/

        public VisEdge(double my_r, double my_g, double my_b, double my_weight,

                       double my_sx, double my_sy, double my_ex, double my_ey,

                       boolean my_bidir, boolean my_active, boolean my_hlight)

        {


                // Set all variables

                r = my_r;

                g = my_g;

                b = my_b;

                weight = my_weight;

                sx = my_sx;

                sy = my_sy;

                ex = my_ex;

                sy = my_sy;

                bidirectional = my_bidir;

                highlighted = my_hlight;

                activated = my_active;

        }




        // Function clearEdge

        // Pre:  Nothing

        // Post:  Sets Edge's values to the defaults

        public void clearEdge()
    {


                // Initialize all variables to the defaults

                r = 0;                  //Default color is black

                g = 0;

                b = 0;

                sx = 0.5;               //Default edge is a self-loop at the center

                sy = 0.5;

                ex = 0.5;

                sy = 0.5;

                weight = 0;             //Default weight is zero

                highlighted = false;    //Highlighting is off by default

                bidirectional = true;   //Bidirectional by default

                activated = false;      //Not activated until you say so


        }





        /********************************/

        /*      Query node values       */

        /********************************/



        public double getRed()   { return r; }         //Get the edge's red color

        public double getGreen() { return g; }         //Get the edge's green color

        public double getBlue()  { return b; }         //Get the edge's blue color

        public double getSX()    { return sx; }        //Get the edge's start-x

        public double getSY()    { return sy; }        //Get the edge's start-y

        public double getEX()    { return ex; }        //Get the edge's end-x

        public double getEY()    { return ey; }        //Get the edge's end-y

        public double getWeight(){ return weight; }    //Get the edge's weight

        public boolean isHighlighted() { return highlighted; }      //Highlighted?

        public boolean isBidirectonal() { return bidirectional; }   //Bidirectional?

        public boolean isActivated()   { return activated;   }      //Activated?





        /******************************/

        /*      Set edge values       */

        /******************************/



        public void setRed(double my_r)   { r = my_r; }   //Set the edge's red color

        public void setGreen(double my_g) { g = my_g; }   //Set the edge's green color

        public void setBlue(double my_b)  { b = my_b; }   //Set the edge's blue color

        public void setSX(double my_sx)   { sx = my_sx; } //Set the edge's start-x

        public void setSY(double my_sy)   { sy = my_sy; } //Set the edge's start-y

        public void setEX(double my_ex)   { ex = my_ex; } //Set the edge's end-x

        public void setEY(double my_ey)   { ey = my_ey; } //Set the edge's end-y

        public void setWeight(double my_w) 		  //Set the edge's weight
                { weight = my_w; }

        public void toggleHighlighted()                   // Toggle Highlighted

                { highlighted = !(highlighted); }

        public void toggleBidirectional()  		  // Toggle Bidirectional
                { bidirectional = !(bidirectional); }


        public void activate()   { activated = true;   }  // Activate

        public void deactivate() { activated = false;  }  // Deactivate





        /******************************/

        /*     Other edge functions   */

        /******************************/


        public void printEdge() {}                          // Print the edge in ANIMAL


}


