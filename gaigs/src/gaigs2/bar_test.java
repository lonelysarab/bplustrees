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

//Ben Tidman
//Class used to illustrate a bar graph

package gaigs2;
import java.io.*;
import java.awt.*;
import java.util.*;

public class bar_test extends StructureType 
{
 
 private double [] bar;
 private double temp;
 private char [] color;
 private char tempColor;
 private int numBars, movements, comparsions;
 private String status;
 
 public bar_test () 
 {
	super();
	bar = new double [10];
	numBars = 0;
 }

 public void calcDimsAndStartPts(LinkedList llist, draw d) 
 {
	super.calcDimsAndStartPts(llist, d);
 }

 boolean emptyStruct() 
 {
	return(false);
 }

 //reads in information on each bar.  Height is stored in bar[].  Color is stored in color[]
 //The number of swaps and comparsions are the last two numbers read in 
 void loadStructure (StringTokenizer st, LinkedList llist, draw d) throws VisualizerLoadException  
 {
	String line;
	double scale, max = 0.0;

 	if (st.hasMoreTokens()) 
	{
		line = st.nextToken();
		numBars = Format.atoi(line);
	}
	else
		throw (new VisualizerLoadException ("End of data when expecting number of bars"));
	
	//declare arrays of length numBars
	bar = new double[numBars];
	color = new char[numBars];

	for(int x = 0; x < numBars; x++)
	{
		if (st.hasMoreTokens()) 
			line = st.nextToken();
		else
			throw (new VisualizerLoadException ("End of data when expecting hight number"));
		
		//check for color
		if(line.charAt(0) == '\\')
		{
			color[x] = line.charAt(1);
			bar[x] = (Format.atof(line.substring(2)));
		}
		//if no color set to black
		else
		{
			color[x] = 'X';
			bar[x] = (Format.atof(line));
		}

		if(bar[x] > max)
			max = bar[x];	
	}
	
	//calculate scale based on the highest bar so far
	scale = 10.0/max;	
	
	//get last three values from file
	for(int x = 0; x < 3; x++)
	{
		if (st.hasMoreTokens()) 
			line = st.nextToken();
	        else 
			throw (new VisualizerLoadException ("End of data when expecting data"));
		
		//if value is for temp
		if(x == 0)
		{
			//check for color
			if(line.charAt(0) == '\\')
			{
				tempColor = line.charAt(1);
				temp = (Format.atof(line.substring(2)));
			}
			else
			{
				tempColor = 'X';
				temp = (Format.atof(line));
			}
			
			//reset scale if temp is the largest
			if(temp > max)
				scale = 10.0/temp;
			temp = (temp * scale) * .06;
		}
		else if(x == 1)
			movements = Format.atoi(line);
		else
			comparsions = Format.atoi(line);
	}
	
	//apply scale to all the bars
	for(int x = 0; x < numBars; x++)
		bar[x] = (bar[x] * scale) * .06;
	
	if (st.hasMoreTokens()) 
		line = st.nextToken();
	else 
		throw (new VisualizerLoadException ("End of data when expecting data"));

	status = line;	

	if (st.hasMoreTokens()) 
		line = st.nextToken();
	else 
		throw (new VisualizerLoadException ("End of data when expecting data"));
	
 }

 //draws the bar graph
 //to turn off temp slot send a value less then 0.0
 void drawStructure (LinkedList llist, draw d) 
 {
	double xline[], yline[];
	double [] barx = new double[5];
	double [] bary = new double[5];
	int count = 0;
	double start = 0.0, size;
	String text = "";
	Integer a;

        super.drawStructure(llist,d);
        xline = new double [2];
        yline = new double [2];
        yline[0]=TitleEndy;
        yline[1]=yline[0];
        xline[0]= 0;
        xline[1]= 1;
        // The polyline is drawn immediately under the title/caption 
	GKS.polyline(2,xline,yline,llist,d);
	if(numBars > 7)
		size = (.6/numBars) + .01;
	else
		size = .11;
	
	start = findStart(size);

	GKS.set_text_align(0, 2, llist, d); 
	//this loop draws the bars
	while(count < numBars)
	{
		start += size;
		barx[0] = start;
		bary[0] = bar[count] + .22;
		barx[1] = start + (size - .01);
		bary[1] = bar[count] + .22;
		barx[2] = start + (size - .01);
		bary[2] = .22;
		barx[3] = start;
		bary[3] = .22;
		barx[4] = barx[0];
		bary[4] = bary[0];
		GKS.set_fill_int_style(bsSolid, (extractColor(color[count])), llist, d);
        	GKS.fill_area(5, barx, bary, llist, d);
		//an irratating way to put in index numbers on bars
		a = new Integer(count);
		text = a.toString();
		GKS.text((start+(size/2)-.01), .22, text, llist, d);
		count++;
	}
	
	//rest of text	
	text = "Movements: " + movements;
	GKS.text(.1, .17, text, llist, d);
	text = " Comparsions: " + comparsions;
	GKS.text(.1, .12, text, llist, d);
	text = "Status info";
	GKS.text(.8, .17, text, llist, d);	
	GKS.text(.8, .12, status, llist, d);

	//draw temp bar
	if(temp >= 0.0)
	{
		start = .9;
		barx[0] = start;
		bary[0] = temp + .22;
		barx[1] = start + (size - .01);
		bary[1] = temp + .22;
		barx[2] = start + (size - .01);
		bary[2] = .22;
		barx[3] = start;
		bary[3] = .22;
		barx[4] = barx[0];
		bary[4] = bary[0];
		GKS.set_fill_int_style(bsSolid, extractColor(tempColor), llist, d);
		GKS.fill_area(5, barx, bary, llist, d);
		GKS.text((start+(size/2) - .01), .22, "temp", llist, d); 
	}
 }

 //method that returns the x coordinate for the first bar
 double findStart(double size)
 {
	int half = (numBars/2);
	int oe = numBars%2;
	
	//if there is a temp spot shift everything over some
	if(temp >= 0.0)
	{
		if(oe == 0)
			return (.3 - (half * size));
		else
			return (.3 - ((half * size) + 0.05));
	}
	else
	{
		if(oe == 0)
			return (.4 - (half * size));
		else
			return (.4 - ((half * size) + 0.05));
	}
 }
}
