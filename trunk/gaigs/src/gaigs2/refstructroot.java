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
import java.util.*;
import org.jdom.*;
import java.io.*;

public class refstructroot extends StructureType
{
    private static final double DELTA = (Math.PI/6.0);
    private static final double LENGTH = .035;
    private static final double INITIAL_X = 0.0;
    private static final double INITIAL_Y = 0.9;
    private Element struct;

    public refstructroot()
    {
	super();
    }

    // All loadStructure does is same the tree represenation of the
    // XML to a local variable.
    public void loadStructure(Element myRoot, LinkedList llist, draw d)
    {
	load_name_and_bounds(myRoot, llist, d);
	struct = myRoot;
    }

    public void drawStructure(LinkedList llist, draw d)
    {
	int remove = 0;
	List children = struct.getChildren();
	Iterator it = children.iterator();
     
	// This loop sees if name and bounds are children of the
	// refstructroot node so they can be removed from the iterator
	// below
	while(it.hasNext())
	{
	    Element temp = (Element)it.next();
	    if(temp.getName().equals("name")||temp.getName().equals("bounds")) remove++;
	    if(remove == 2) break;
	}
	it = children.iterator();
	for(int i = 0; i < remove; i++) it.next();
	auxDraw(it, INITIAL_X, INITIAL_Y, 0.0 ,llist, d);
    }

    private double auxDraw(Iterator it, double x, double y, double box, LinkedList llist, draw d)
    {
	double drawn = 0.0;	// The distance drawn on the current level
	double distDrawn = 0.0;	// The distance drawn on the level below the current level
	
	while(it.hasNext())
	{
	    Element ele = (Element)it.next();
	    // If it is a refstruct then there should be one or more
	    // refboxes that must be passed
	    if(ele.getName().equals("refstruct"))
	    {
		List children = ele.getChildren();
		Iterator passIt = children.iterator();
		drawn += auxDraw(passIt,x,y,0.0,llist,d);
		drawn += .1;
	    }
	    else if(ele.getName().equals("refbox"))
	    {
		int color = 0;	// Color of the box and any data or arrow
		String hasarrow = "false"; // If the box has an arrow
		String boxname = ""; // The name to be printed above the box
		String boxdata = ""; // The data to be printed within the box
		List attList = ele.getAttributes();
		// Here the attributes are pulled from the list
		for(int i = 0; i < attList.size(); i++)
		{
		    Attribute att = (Attribute)attList.get(i);
		    if(att.getName().equals("color")) color = colorStringToInt(att.getValue());
		    else if(att.getName().equals("boxname")) boxname = att.getValue();
		    else if(att.getName().equals("boxdata")) boxdata = att.getValue();
		    else if(att.getName().equals("hasarrow")) hasarrow = att.getValue();
		}
		LGKS.set_text_height(.04,llist,d);
		double nameLen = normalized_width(boxname); // Get length of box name
		LGKS.set_text_height(.06,llist,d);
		double dataLen = normalized_width(boxdata); // Get length of box data
		double boxSize = Math.max(.1,Math.max(nameLen,dataLen)); // Set the box size
		double[] xlist = {x+drawn,x+drawn,x+boxSize+drawn,x+boxSize+drawn,x+drawn};
		double[] ylist = {y,y-.1,y-.1,y,y};
		LGKS.set_textline_color(color,llist,d);
		LGKS.set_fill_int_style(bsSolid,color,llist,d);
		LGKS.polyline(5,xlist,ylist,llist,d); // Draw the box
		LGKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);
		double xnamedraw = xlist[0]+(boxSize/2);
		double ynamedraw = ylist[0]+.025;
		LGKS.set_text_height(.04,llist,d);
		LGKS.text(xnamedraw,ynamedraw,boxname,llist,d);	// Draw the box name
		if(hasarrow.equals("true"))
		{
		    // This first chunk of code draws the line that
		    // will make up the arrow
		    double xarrowstart = xlist[0]+.05;
		    double yarrowstart = ylist[0]-.05;
		    double xarrowend = x+distDrawn+.05;
		    double yarrowend = y-.160;
		    if(!(xarrowstart == xarrowend))
		    {
			xarrowend=x+distDrawn;
			yarrowend=y-.2;
		    }
		    double[] xarrowlist = {xarrowstart,xarrowend};
		    double[] yarrowlist = {yarrowstart,yarrowend};
		    LGKS.polyline(2,xarrowlist,yarrowlist,llist,d);
		    
		    // This part of the code draws the triangle that
		    // will make up the point of the arrow
		    double[] xtri = new double[3];
		    double[] ytri = new double[3];
		    xtri[0] = xarrowend;
		    ytri[0] = yarrowend;
		    double slope;
		    double alphaprime;
		    double alpha;
		    if(xarrowstart == xarrowend) // Undefined slope
		    {
			alpha = Math.PI/2;
			ytri[0] = ytri[0]-.01;
		    }
		    else
		    {
			slope = ((yarrowend-yarrowstart)/(xarrowend-xarrowstart));
			alphaprime = Math.atan(slope);
			alpha = Math.PI+alphaprime;
		    }
		    if((alpha+DELTA==(Math.PI/2)))
		    {
			xtri[1] = xtri[0];
			ytri[1] = ytri[0]+LENGTH;
		    }
		    else
		    {
			// Here the slope of the sides of the arrow is found
			double slope1 = Math.tan(alpha+DELTA);
			double slope2 = Math.tan(alpha-DELTA);
			double postemp1 = Math.sqrt((Math.pow(LENGTH,2))/(Math.pow(slope1,2)+1));
			double negtemp1 = postemp1*-1.0;
			postemp1 += xarrowend;
			negtemp1 += xarrowend;
			double postemp2 = Math.sqrt((Math.pow(LENGTH,2))/((Math.pow(1/slope2,2)+1)));
			double negtemp2 = postemp2*-1.0;
			postemp2 += yarrowend;
			negtemp2 += yarrowend;
			xtri[1] = Math.min(postemp1,negtemp1);
			ytri[1] = yarrowend+(slope1*(xtri[1]-xarrowend));
			ytri[2] = Math.max(postemp2,negtemp2);
			xtri[2] = xarrowend+((ytri[2]-yarrowend)/slope2);
		    }
		    LGKS.fill_area(3,xtri,ytri,llist,d);
		}					    
		else if (boxdata.equals("nil"))
		{
		    double xline_start = xlist[0]+.10;
		    double yline_start = ylist[0];
		    double xline_end = xlist[0];
		    double yline_end = ylist[0]-.10;
		    double[] xxlist = {xline_start,xline_end};
		    double[] yylist = {yline_start,yline_end};
		    LGKS.polyline(2,xxlist,yylist,llist,d);
		}
		else
		{
		    double xdatadraw = xlist[0]+(boxSize/2);
		    double ydatadraw = ylist[0]-.05;
		    LGKS.set_text_height(.06,llist,d);
		    // Draw the data in the box
		    LGKS.text(xdatadraw,ydatadraw,boxdata,llist,d); 
		}
		drawn += boxSize;
		List children = ele.getChildren();
		Iterator passIt = children.iterator();
		distDrawn += auxDraw(passIt,x+distDrawn,y-.2,boxSize,llist,d); 
	    }
	}
	return (drawn+distDrawn);
    }
}
