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

import org.jdom.*;

public class BarScat extends StructureType {

    protected boolean itIsBar;
    double MaxBarHeight;
    final double SpaceConst =5.0;
    final double XMin =0.0;
    final double spc =0.005;
    final double MinHeight =0.005;
    protected LinkedList bar_labels = new LinkedList();

    public BarScat (String whichOne) {
	super();
	if (whichOne.compareTo("BAR") == 0)
	    itIsBar = true;
	else
	    itIsBar = false;
	MaxBarHeight = 0;
    }


    boolean emptyStruct() {
  
	if (nodelist.size() == 0) 
	    return(true);
	else
	    return(false);
    }

    void loadStructure( Element struct, LinkedList llist, draw d) 
	throws VisualizerLoadException{
	load_name_and_bounds(struct, llist, d);

	Iterator iter = struct.getChildren().iterator();
	while( iter.hasNext() ) {
	    Element child = (Element) iter.next();
	    if( child.getName().compareTo("bar") == 0 ) {
		String label;
		LinkedList text = new LinkedList();

		if( child.getAttributeValue("color").charAt(0) != '#' )
		    label = "\\" + 
			color_str_to_char( child.getAttributeValue("color") );
		else
		    label = "\\" + child.getAttributeValue("color");
		
		label = label + child.getAttributeValue("magnitude");
		double tempHeight = 
		    Format.atof(child.getAttributeValue("magnitude"));
		if(tempHeight > MaxBarHeight)
		    MaxBarHeight = tempHeight;

		text.append(label);
		nodelist.append(text);

		Element label_element;

		try{
		    label_element = 
			(Element)child.getChildren().iterator().next();
		}catch(NoSuchElementException empty){
		    throw new VisualizerLoadException("Element <bar> is missing the required element <label>: " + empty.toString());
		}

		LinkedList bar_label = new LinkedList();

		if(label_element.getName().equalsIgnoreCase("label")){
		    String bar_label_text = label_element.getText();
		    //		    bar_labels.append(bar_label);
		    StringTokenizer label_tokens = 
			new StringTokenizer(bar_label_text, "\n");
		    int number_of_lines = label_tokens.countTokens();

		    for(int i = 0; i < number_of_lines; i++){
			bar_label.append(label_tokens.nextToken());
		    }

		    bar_labels.append(bar_label);
		    
		    if(number_of_lines > linespernode){
			linespernode = number_of_lines;
		    }
		}else{
		    throw(new VisualizerLoadException("The <label> element was not found for this <bar> element."));
		}
	    }
	}
    } // loadStructure(element)

    void loadStructure (StringTokenizer st, LinkedList llist, draw d)
	throws VisualizerLoadException {

	boolean done = false; 
	LinkedList templl = new LinkedList();
	double tempHeight;

	Xcenter=CenterScreen; // These Snapshots are always centered *)
	Ycenter=CenterScreen; 
	if (linespernode != 1) 
	    throw (new VisualizerLoadException("Wrong number of lines per node for BAR or SCAT"));
	while (!done) {
	    try {
		templl = getTextNode(st, linespernode, llist,d);
		templl.reset();
		tempHeight = Format.atof (textwocolor( (String) (templl.currentElement()) ));
		if (tempHeight > MaxBarHeight) 
		    MaxBarHeight = tempHeight;
	    }
	    catch ( EndOfSnapException e ) {
		done = true;
	    }
	    if (!done) nodelist.append(templl);
	}

    } // loadStructure(st)
	
    void drawStructure (LinkedList llist, draw d)  {
 
	double BarWidth,BarSpace,tempHeight,HeightScale, Xstart, Ystart, ScreenTop, yPos;
	int TotalBars, nodeColor, textColor;
	double BarX [], BarY [];
	String tempString;
	LinkedList templl;
 
    	if (emptyStruct()) {
	    super.drawStructure(llist,d);  // to handle empty structure
	    return;
        }
	BarX = new double [5];
	BarY = new double [5];
        //Ystart=(Titleheight* (double) (title.size()))
	//		   +(0.5*Titleheight* (double) (title.size()));
	Ystart = 0.0;
        Xstart=XMin;
        //ScreenTop=0.9-Ystart;
        ScreenTop = TitleEndy;
        TotalBars=nodelist.size();
        BarSpace=spc;
        BarWidth=(1.0-(((double)TotalBars)*spc))/((double)TotalBars);
        if (BarWidth<MinHeight)
	    BarWidth=MinHeight;
        HeightScale=ScreenTop/MaxBarHeight;
	nodelist.reset();
	while (nodelist.hasMoreElements()) {
	    templl = (LinkedList) nodelist.nextElement();
	    templl.reset();
	    tempString = (String) templl.currentElement();
	    if ( tempString.charAt(0) == Delim && inHighlightColors(tempString.charAt(1))) {
		textColor = Black;
		nodeColor = new_extractColor(tempString.substring(1));
		LGKS.set_fill_int_style(bsSolid,nodeColor,llist,d);
		LGKS.set_textline_color(textColor,llist,d);
		tempString = ( (tempString.charAt(1) != '#') ? tempString.substring(2) : tempString.substring(8) ) ;
		//		tempString = tempString.substring(2);
	    }
	    else {
		nodeColor=Black;
		textColor=Black;
		LGKS.set_fill_int_style(bsSolid,nodeColor,llist,d);
		LGKS.set_textline_color(textColor,llist,d);
	    }
	    tempHeight = Format.atof(tempString);
	    if (itIsBar) {
		BarX[0]=Xstart;
		BarY[0]=Ystart;
		BarX[1]=Xstart+BarWidth;
		BarY[1]=Ystart;
		BarX[2]=BarX[1];
		BarY[2]=Ystart+(tempHeight*HeightScale);
		BarX[3]=Xstart;
		BarY[3]=BarY[2];
		BarX[4]=Xstart;
		BarY[4]=Ystart;

		yPos = Ystart - 0.05;
		LinkedList bar_label = (LinkedList)bar_labels.remove();

		LGKS.set_text_align(TA_CENTER, TA_BASELINE, llist, d);

		while(bar_label.hasMoreElements()){
		    LGKS.text(Xstart + (BarWidth / 2) - spc, yPos,
			      (String)bar_label.nextElement(), llist, d);
		    yPos -= Textheight;
		}
	    }
	    else {
		BarX[0]=Xstart;
		if ((BarWidth/2.0)>=MinHeight) 
		    BarY[0]=Ystart+(tempHeight*HeightScale)-(BarWidth/2.0);
		else
		    BarY[0]=Ystart+(tempHeight*HeightScale)-MinHeight;
		BarX[1]=Xstart+BarWidth;
		BarY[1]=BarY[0];
		BarX[2]=BarX[1];
		BarY[2]=Ystart+(tempHeight*HeightScale);
		BarX[3]=Xstart;
		BarY[3]=BarY[2];
		BarX[4]=Xstart;
		BarY[4]=BarY[0];
	    }
	    LGKS.fill_area(5,BarX,BarY,llist,d);
	    Xstart=Xstart+BarWidth+BarSpace;
	}
	textColor=Black;
	LGKS.set_textline_color(textColor,llist,d);
	//This code left in here to confuse the poor, helpless sap who has to work
	//with this code in the future.
	TitleStarty=(Titleheight* ((double)title.size()))+
	    (0.5*Titleheight *((double) (title.size())))-Titleheight;
	LGKS.set_fill_int_style(bsClear,White,llist,d);
    }

}


