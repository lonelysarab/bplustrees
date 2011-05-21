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
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.*;

public class Alg_vis extends StructureType {

    private static final int SIZE = 4;		//size of arrays used
    private static final int NAMEPOS = 0;	//initional position of var name
    private static final int LOCPOS = 1;	//initional position of var loc
    private static final int ARRAYPOS = 4;	//inital position of var array (true or false)
    private static final int ARROWPOS = 3;	//inital position of var arrow (true or false)
    private static final int CHANGEPOS = 2;	//inital position of changed value (true or false)
    private static final int ARRAYIDX = 5;	//inital position of the index used
    private static final int JUMP = 6;		//what needs to be added to get to next name or loc
    //    private static final int GLOBAL = 3;	//color of global var = green
    private static final int GLOBAL = - 0x32CD32;	//color of global var = green
    private static final int MAIN = 4;	//color of main var = red
    private static final int ARG = 5;	//color of arg var = megenta
    //    private static final int FUNC = 6;	//color of func var = light blue
    private static final int FUNC = - 0x0000FF;	//color of func var = light blue
    private static final int CHANGE = 1; //color of boxed that change = black
    private static final double RIGHTBOUND = .90;    //the left most spot boxes can be drawn
    private static final double LEFTBOUND = .40;    //the right most spot boxes can be drawn
    private static final double YDOWN = .15; 	//the degree y should be decreased to drawn next row
    private static final double SPACE = .02;	//space between boxes of the same group
    private static double startx = .02;	//default start coor 
    private static double starty = .65;	//default start coor 
    private static final String STR = "88";	//Used to determine lengh_of_string
    private static int MAGIC_GET_STRING_IN_BOX_NUMBER = 150; // play with this to get strings in boxes
    private double length_of_str;	//length of the string STR (box size)
    private int totalNum;	//total num of variables drawn in snapshot
    private String phase;	//variable declaration or equations 
    private ArrayList varInfo;	//holds var name, location, and number of values it currently has
    private ArrayList value;	//holds all var values from every variable
    private ArrayList x, y;	//sets of x and y coor where boxes should be drawn
    private ArrayList arrowList; //holds the name and location of the variable an arrow points to

    
    /*----------------Constructor------------------*/
    public Alg_vis () { 
	super();
	value = new ArrayList();
	varInfo = new ArrayList();
	x = new ArrayList();
	y = new ArrayList();
	arrowList = new ArrayList();
	length_of_str = 0.0;
    }//constructor

    
    /*----------------calclates the dimentions and start points of graphics------------------*/
    public void calcDimsAndStartPts(LinkedList llist, draw d) {
	super.calcDimsAndStartPts(llist, d);

	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);

	int temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(STR);

	// This strategy works also, but the BufferedImage seems
	// guaranteed and the java docs regarding
	// FontRenderContext seem to indicate less reliable
	//	int temp = (int)(defaultFont.getStringBounds(STR, new FontRenderContext(new AffineTransform(),true,true))).getWidth();

// 	length_of_str = ((double) temp / (double) GaigsAV.preferred_width /*maxsize*/);
// 
	length_of_str = ((double) temp / (double) MAGIC_GET_STRING_IN_BOX_NUMBER /*maxsize*/);

// 	length_of_str = ((double)temp/ (double) d.getSize().width) + .05;
    }//calcDimsAndStartPts
    

    /*----------------determine all coordinates of all boxes------------------*/
    public void coor(){
    
        /*****Local variable declarations*****/
        double maxX = startx, minY = starty, curX = startx, curY = starty;
	int arrayPos = ARRAYPOS, locPos = LOCPOS;
	boolean firstg = true, firstm = true, firsta = true, firstf = true;
	
	
	/*****Set all coor. for all variables being drawn depending on location*****/
        for(int i = 0; i < totalNum; i++){
	    String loc = (String)varInfo.get(locPos);
	    String arr = (String)varInfo.get(arrayPos);
	    
	    //startx and starty are the default values of x and y, 
	    //maxX and minY keep track of the highest postion of x and the lowest of y
	    //this ensures that the var. group being drawn next and blow the current one 
	    //is drawn immediatly after the current is finished drawing
	    //curX and curY are the current positions of x and y
	    //the following if/else statemetens find the location of the variable
	    //if it is the first variable drawn in that location, reset default x and y values
	    //if the x value has reached its right side limit, or if curX is not 
	    //at the correct starting position, then drawn down one line and start back at the 
	    //leftmost side for x; check the maxX and minY to make sure they represent the largest
	    //and smallest values respectivly
	    if(loc.compareTo("g") == 0){
	        if(firstg)
		    firstg = false;
	        if(curX >= LEFTBOUND || 
			(arr.compareTo("true") == 0 && curX != startx && (curX + (length_of_str * SIZE) + .02 > maxX))){
		    curX = startx;
		    curY = curY - YDOWN;
		}//if
		
		if(arr.compareTo("true") == 0)
		    maxX = (length_of_str * (SIZE-1));
		if(curX > maxX)
		    maxX = curX;
		if(curY < minY)
		    minY = curY;
	    }//if
	    
	    else if(loc.compareTo("m") == 0){
	        if(firstm){
		    firstm = false;
		    if(!firstg)
		        maxX = maxX + (length_of_str * 2);
	            curY = starty;
		    curX = maxX;
		}//if			
	   	if(curX >= RIGHTBOUND || 
			(arr.compareTo("true") == 0 && curX != maxX  && (curX + (length_of_str * SIZE) + .02  > 1))){
		    curX = maxX;
		    curY = curY - YDOWN;
		}//if
		if(curY < minY)
		    minY = curY;
	    }//else if
	    
	    else if(loc.compareTo("a") == 0){
	   	if(firsta){
		    firsta = false;
		    curX = startx;
		    minY = minY - (length_of_str * 2);
		    curY = minY;
		    maxX = startx;
		}//if
	        if(curX >= LEFTBOUND){
		    curX = startx;
		    curY = curY - YDOWN;
		}//if
		if(curX > maxX)
		    maxX = curX;
	    }//else if
	    
	    else{
	   	if(firstf){
		    firstf = false;
		    maxX = maxX + (length_of_str * 2);
	            curY = minY;
		    curX = maxX;
		}//if			
	   	if(curX >= RIGHTBOUND || 
			(arr.compareTo("true") == 0 && (curX != maxX ) && (curX + (length_of_str * SIZE) + .02  > 1))){
		    curX = maxX;
		    curY = curY - YDOWN;
		}//if
            }//else
	    	    
	    //if an array is being drawn, compute positions for each value
	    //else compute positions for the one value
	    if(arr.compareTo("true") == 0 && loc.compareTo("a") != 0){
	    	for(int a = 0; a < SIZE; a++){
		    double tempX[] = {curX, curX + length_of_str, curX + length_of_str, curX, curX};
	    	    double tempY[] = {curY, curY, curY + length_of_str, curY + length_of_str, curY};

	    	    x.add(tempX);
	    	    y.add(tempY);
		    curX = curX + length_of_str;
		}//for
		curX = curX + SPACE  + SPACE;
	    }//if
	    
	    else{	    	
	    	double tempX[] = {curX, curX + length_of_str, curX + length_of_str, curX, curX};
	    	double tempY[] = {curY, curY, curY + length_of_str, curY + length_of_str, curY};

	    	x.add(tempX);
	    	y.add(tempY);
		curX = curX + length_of_str + SPACE;
	    }//else
		
	    locPos = locPos + JUMP;
	    arrayPos = arrayPos + JUMP;
	}//for    
    }//coor
    
    /*----------------Draw an arrow in the appropriate location when given the starting location------------------*/
    public void drawArrow(double tempX[], double tempY[], LinkedList llist, draw d){
    	String namef = (String)arrowList.get(0);	//get the name of the variable the arrow is going to
	String locf = (String)arrowList.get(1);		//get the location of the variable the arrow is going to
	int namePos = NAMEPOS, locPos = LOCPOS, arrIxPos = ARRAYIDX, arrPos = ARRAYPOS;
	int index = 0;
	
	GKS.set_textline_color(CHANGE, llist, d); //set color to black
	
	for(int i = 0; i < varInfo.size(); i++){
	    String namet = (String)varInfo.get(namePos);
	    String loct = (String)varInfo.get(locPos);
	    String arr = (String)varInfo.get(arrPos);
	    int arrIx = Integer.parseInt((String)varInfo.get(arrIxPos));

	    //compare strings until a match is found
	    if(namet.compareTo(namef) == 0 && loct.compareTo(locf) == 0){
	        double temp[], temp2[];
	    	if(arr.compareTo("true") == 0){
		  index = index + arrIx;
		  temp = (double [])x.get(index);
	   	  temp2 = (double [])y.get(index);
		}//if
		else{
		  temp = (double [])x.get(index);
	   	  temp2 = (double [])y.get(index);
		}//else 
		
 		double newX[] = {tempX[2] - .02, temp[0] + .04};
 		double newY[] = {tempY[2], temp2[0]- .03};
		
		double newTriX[] = {temp[0] + .04, temp[0] + .02, temp[0] + .06, temp[0] + .04};
		double newTriY[] = {temp2[0], temp2[0] - .03, temp2[0] - .03, temp2[0]};
				
		GKS.polyline(2, newX, newY, llist, d);	//draw the line
		GKS.fill_area(4, newTriX, newTriY, llist, d);	//draw the triangle
		i = varInfo.size();
	    }//if
	    
	    else{
	        //increse the index where the variable coordinates will be found
	        if(arr.compareTo("true") == 0)
		  index = index + SIZE;
		else
		  index++;
	
	        namePos = namePos + JUMP;
	    	locPos = locPos + JUMP;
		arrPos = arrPos + JUMP;
	    	arrIxPos = arrIxPos + JUMP;
	   }//else
	
	}//for    
    
    }//drawArrow
    
    
    /*----------------draw the boxes and arrows to the screen------------------*/
    void drawStructure (LinkedList llist, draw d) {

    	/*****Local variable declarations*****/
	double xline[], yline[];
	int namePos = NAMEPOS, arrayPos = ARRAYPOS, locPos = LOCPOS;
	int arrayIdxPos = ARRAYIDX, changePos = CHANGEPOS, arrowPos = ARROWPOS;
	int index = 0, valIndex = 0, color = 0;	
	
	/*****Draw line and tital at top of snapshot*****/
	super.drawStructure(llist,d);

	xline = new double [2];
	yline = new double [2];

        yline[0]=TitleEndy;
        yline[1]=yline[0];
        xline[0]= 0;
        xline[1]= 1;
        // The polyline is drawn immediately under the title/caption 
        GKS.polyline(2,xline,yline,llist,d);
	    
	coor();		//call to function to set all variable coordinates
		
	/*****draw all variable boxes*****/
	for(int i = 0; i < totalNum; i++){
	   String name = (String)varInfo.get(namePos);
	   String array = (String)varInfo.get(arrayPos);
	   String loc = (String)varInfo.get(locPos);
	   String chg = (String)varInfo.get(changePos);
	   String arrow = (String)varInfo.get(arrowPos);
	   int arrayIdx = Integer.parseInt((String)varInfo.get(arrayIdxPos));
	   int tempcol;
	   
	   //determine the color
	   if(loc.compareTo("g") == 0)
	   	color = GLOBAL;
	   else if(loc.compareTo("m") == 0)
	   	color = MAIN;
	   else if(loc.compareTo("a") == 0)
	   	color = ARG;
	   else
	   	color = FUNC;
		
	   tempcol = color;
	   
	   if(chg.compareTo("true") == 0)
	   	  color = CHANGE;
		  
	   GKS.set_textline_color(color, llist, d);  //set the color
			   
	   //if an array draw each box without any spaces between
	   //else draw individual boxes with spaces between
	   if(array.compareTo("true") == 0 && loc.compareTo("a") != 0){
	   	int tempIdx = index;	
		int col = color;
		
		double temp[] = (double [])x.get(tempIdx);
	   	double temp2[] = (double [])y.get(tempIdx++);
	   	GKS.text(temp[0] + 0.02, temp2[0] + 0.12, name, llist, d);	//draw name
		
		//loop through the values of the array and draw each one
		for(int a = 0; a < SIZE; a++){
		    		    
		    if(chg.compareTo("true") == 0 && arrayIdx != a && phase.compareTo("equation") == 0)
	   	       color = tempcol;

		    GKS.set_textline_color(color, llist, d);  //set the color
		    double tempX[] = (double [])x.get(index);
	   	    double tempY[] = (double [])y.get(index++);
		    
		    //if an arrow is needed call drawArrow
		    if(arrow.compareTo("true") == 0 && arrayIdx == a)
		        drawArrow(tempX, tempY, llist, d);
		    
		    String val = (String)value.get(valIndex++);
		    GKS.text(tempX[0] + 0.02, tempY[0] + 0.05, val, llist, d);	//draw value
	   	    GKS.polyline(5, tempX, tempY, llist, d);	//draw box
		    
		    color = col;
		    GKS.set_textline_color(color, llist, d);	//reset color
		}//for
	   }//if
	    
	   else{
	        
	   	String val;	    	
	    	double tempX[] = (double [])x.get(index);
	   	double tempY[] = (double [])y.get(index++);
		
		//if an arrow is needed call drawArrow
		if(arrow.compareTo("true") == 0)
		    drawArrow(tempX, tempY, llist, d);
		
		GKS.set_textline_color(color, llist, d);	//reset color
		GKS.text(tempX[0] + 0.02, tempY[0] + 0.12, name, llist, d);	//draw name
		
		if(array.compareTo("true") == 0){
		  int idx = Integer.parseInt((String)varInfo.get(arrayIdxPos));
		  valIndex = valIndex + idx;
		  val = (String)value.get(valIndex);
		  valIndex = valIndex + (4 - idx);
		}//if
		else
		  val = (String)value.get(valIndex++);
		GKS.text(tempX[0] + 0.02, tempY[0] + 0.05, val, llist, d);  //draw value
	   	GKS.polyline(5, tempX, tempY, llist, d);  //draw box
	   }//else
	   
	   namePos = namePos + JUMP;
	   locPos = locPos + JUMP;
	   arrayPos = arrayPos + JUMP;
	   changePos = changePos + JUMP;
	   arrowPos = arrowPos + JUMP;
	   arrayIdxPos = arrayIdxPos + JUMP;
	   
        }//for
    }//drawStructure

    
    /*----------------empties structure------------------*/
    boolean emptyStruct() {
	return(false);
    }//emptyStruct 

    
    /*----------------loads all the values in one snapshot------------------*/
    void loadStructure (StringTokenizer st, LinkedList llist, draw d)
	throws VisualizerLoadException  {

	String tline, temp;
	int num = 0;
	
	/*****Max num values in snapshot****/
	if (st.hasMoreTokens()) {
	    tline = st.nextToken();
	}//if
	else 
	    throw (new VisualizerLoadException ("End of data in Arg_vis when expecting num var in shapshot"));
	totalNum = Format.atoi(tline);
	
	/*****variable declaration or equations****/
	if (st.hasMoreTokens()) {
	    tline = st.nextToken();
	}//if
	else 
	    throw (new VisualizerLoadException ("End of data in Arg_vis when expecting variable declaration or equations"));
	phase = tline;	
	
	//loop through all values in snapshot to read them all
	for(int i = 0; i < totalNum; i++){ 
	    
	    /**********Variable Name*********/
	    if (st.hasMoreTokens()) {
	        tline = st.nextToken();
	    }//if
	    else 
	        throw (new VisualizerLoadException ("End of data in Arg_vis when expecting variable name"));
	    varInfo.add(tline);
	    
	    /**********Variable Location*********/
	    if (st.hasMoreTokens()) {
	        tline = st.nextToken();
	    }//if
	    else 
	        throw (new VisualizerLoadException ("End of data in Arg_vis when expecting variable location"));
	    varInfo.add(tline);
	
	    /**********Variable Change*********/    
	    if (st.hasMoreTokens()) {
	        tline = st.nextToken();
	    }//if
	    else 
	        throw (new VisualizerLoadException ("End of data in Arg_vis when expecting variable chnage"));
	    varInfo.add(tline);
	    
	    /**********Variable Arrow*********/  
	    if (st.hasMoreTokens()) {
	        tline = st.nextToken();
	    }//if
	    else 
	        throw (new VisualizerLoadException ("End of data in Arg_vis when expecting variable arrow"));
	    varInfo.add(tline);
	    
	    if(tline.compareTo("true") == 0){
	    	/**********Arrow pointing name*********/  
		if (st.hasMoreTokens()) {
		    tline = st.nextToken();
	        }//if
	    	else 
		    throw (new VisualizerLoadException ("End of data in Arg_vis when expecting variable arrow name"));
		arrowList.add(tline);
		
		/**********Arrow pointing location*********/  
		if (st.hasMoreTokens()) {
		    tline = st.nextToken();
	        }//if
	    	else 
		    throw (new VisualizerLoadException ("End of data in Arg_vis when expecting variable arrow location"));
		arrowList.add(tline);	
	    }//if
	    
	    /**********Variable array*********/     	    
	    if (st.hasMoreTokens()) {
	        tline = st.nextToken();
	    }//if
	    else 
	        throw (new VisualizerLoadException ("End of data in Arg_vis when expecting variable array"));
	    varInfo.add(tline);
	    
	    /**********Variable Array Index*********/  
	    if (st.hasMoreTokens()) {
	        temp = st.nextToken();
	    }//if
	    else 
	        throw (new VisualizerLoadException ("End of data in Arg_vis when expecting array idx"));
	    varInfo.add(temp);
	    
	    /**********Read all Variable Values (array or otherwise)*********/  
	    if(tline.compareTo("true") == 0){
	    	for(int a = 0; a < SIZE; a++){
		    if (st.hasMoreTokens()) {
			tline = st.nextToken();
	            }//if
	    	
	    	    else 
		 	throw (new VisualizerLoadException ("End of data in Arg_vis when expecting variable values"));
			
	    	    value.add(tline);
		}//for
	    }//if
	    else{	
	    	if (st.hasMoreTokens()) {
		    tline = st.nextToken();
	    	}//if
	    	
	    	else 
		    throw (new VisualizerLoadException ("End of data in Arg_vis when expecting variable values"));
	        value.add(tline);
	    }//else
	        
	}//for
	
	/**********Last Token*********/  
	if (st.hasMoreTokens()) {
	    tline = st.nextToken();
	}//if
	else 
	    throw (new VisualizerLoadException ("End of data in Arg_vis when expecting end of snapshot marker"));
	
    }//loadStructure

}//alg_vis
