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


* Write a description of class VisNode here.


*


* A class to store information about nodes (vertices) in a visual graph


* Such as color, highlighting, position, and the character stored inside

*



* @author Jeff Lucas


* @version June 10th, 2002


**/

package gaigs2;
import gaigs2.*;

public class VisNode
{


//Data members, public for now... accessed by functions later?




private double r, g, b;      // Saves the color (inside) of this node


private boolean highlighted; // Is this node highlighted or not?


private char cindex;         // The character stored in this node


			      // (Is this necessary given our indexing?)




private double x, y;         // The cartesian coordinates of the node,


			      // normalized to be E[0, 1]




private boolean activated;   // Is this node used in the current structure?





	/**  Default Constructor for objects of class VisNode  **/


	public VisNode()
    {



 		// Initialize all variables

 		r = 1;          //Default color is white

 		g = 1;
 		b = 1;

  		cindex = 'A';   //Default character is A

 		x = 0.5;        //Default location is at the center

 		y = 0.5;

 		highlighted = false;    //Highlight is off by default

 		activated = false;      //The node is not activated until you activate it


 	}



	/**  Constructor that is given beginning values  **/

	public VisNode(double my_r, double my_g, double my_b, double my_x, double my_y,

             boolean my_active, boolean my_hlight, char my_c)
    {


		// Set all variables

		r = my_r;

		g = my_g;

		b = my_b;

		cindex = my_c;

		x = my_x;

		y = my_y;

		highlighted = my_hlight;

		activated = my_active;


	}



	// Function clearNode

	// Pre:  Nothing

	// Post:  Sets Node's values to the defaults

	public void clearNode()
    {


		// Initialize all variables

		r = 1;          //Default color is white

		g = 1;

		b = 1;

		cindex = 'A';   //Default character is A

		x = 0.5;        //Default location is at the center

		y = 0.5;

		highlighted = false;    //Highlight is off by default


	}




	/********************************/

	/*      Query node values       */

	/********************************/



	public double getRed()   { return r; }         //Get the node's red color

	public double getGreen() { return g; }         //Get the node's green color

	public double getBlue()  { return b; }         //Get the node's blue color

	public double getX()     { return x; }         //Get the node's x-coordinate

	public double getY()     { return y; }         //Get the node's y-coordinate

	public char   getChar()  { return cindex; }    //Get the node's character

	public boolean isHighlighted() { return highlighted; }  //Highlighted?

	public boolean isActivated()   { return activated;   }  //Activated?




	/******************************/

	/*      Set node values       */

	/******************************/



	public void setRed(double my_r)   { r = my_r; }   //Set the node's red color

	public void setGreen(double my_g) { g = my_g; }   //Set the node's green color

	public void setBlue(double my_b)  { b = my_b; }   //Set the node's blue color

	public void setX(double my_x)     { x = my_x; }   //Set the node's x-coordinate

	public void setY(double my_y)     { y = my_y; }   //Set the node's y-coordinate

	public void setChar(char my_c) { cindex = my_c; } //Set the node's character

	public void toggleHighlighted()                   // Toggle Highlighted

		{ highlighted = !(highlighted); }

	public void activate()   { activated = true;   }  //Activate

	public void deactivate() { activated = false;  }  //Deactivate




	/******************************/

	/*     Other node functions   */

	/******************************/



	public void printNode() {}	// Print the node in ANIMAL


}



