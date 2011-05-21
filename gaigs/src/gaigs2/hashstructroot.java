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

public class hashstructroot extends StructureType
{
    private static final double INITIAL_X = 0.0; // Initial x draw point
    private static final double INITIAL_Y = 0.9; // Initial y draw point
    private static final double LENGTH = .035; // Length of arrow head sides
    private static final double DELTA = (Math.PI/6.0); // Angle of arrow head sides
    private Element struct;

    public hashstructroot()
    {
	super();
    }

    public void loadStructure(Element myRoot, LinkedList llist, draw d)
    {
	load_name_and_bounds(myRoot,llist,d);
	struct = myRoot; // Save local copy of JDOM tree
    }

    public void drawStructure(LinkedList llist, draw d)
    {
	int remove = 0;
	List children = struct.getChildren();
	Iterator it = children.iterator();

	while(it.hasNext())
	{	    
	    Element temp = (Element)it.next();
	    if(temp.getName().equals("name")||temp.getName().equals("bounds")) remove++;
	    if(remove == 2) break;
	}
	it = children.iterator();
	for(int i = 0; i < remove; i++) it.next();
	Element rootBox = (Element)it.next();
	List rootAttList = rootBox.getAttributes();
	int rootColor = 0;
	String boxname = "";
	// Pull the box data from the XML
	for(int c = 0; c < rootAttList.size(); c++)
	{
	    Attribute att = (Attribute)rootAttList.get(c);
	    if(att.getName().equals("boxname")) boxname = att.getValue();
	    else if(att.getName().equals("color")) rootColor = colorStringToInt(att.getValue());
	}
	// Draw the root box of the hash tree
	double[] xlist1 = {INITIAL_X,INITIAL_X,INITIAL_X+.1,INITIAL_X+.1,INITIAL_X};
	double[] ylist1 = {INITIAL_Y,INITIAL_Y-.1,INITIAL_Y-.1,INITIAL_Y,INITIAL_Y};
	double xnamedraw = INITIAL_X+.05;
	double ynamedraw = INITIAL_Y+.025;
	LGKS.set_textline_color(rootColor,llist,d);
	LGKS.set_fill_int_style(bsSolid,rootColor,llist,d);
	LGKS.polyline(5,xlist1,ylist1,llist,d);
	LGKS.set_text_height(.04,llist,d);
	LGKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);
	LGKS.text(xnamedraw,ynamedraw,boxname,llist,d);
	List rootList = rootBox.getChildren();
	Element hash = null;
	if(rootList.size() > 0) hash = (Element)rootList.get(0);
	// Draw the hash table array structure
	if(hash != null)
	{
	    double[] xRootArrow = {INITIAL_X+.1,INITIAL_X+.2};
	    double[] yRootArrow = {INITIAL_Y-.1,INITIAL_Y-.15};
	    LGKS.polyline(2,xRootArrow,yRootArrow,llist,d);
	    drawArrowHead(xRootArrow[0],yRootArrow[0],xRootArrow[1],yRootArrow[1],llist,d);
	    List hashStructRoot = hash.getChildren();
	    for(int i = 0; i < hashStructRoot.size(); i++)
	    {
		Element hashStructBox = (Element)hashStructRoot.get(i);
		List hsbList = hashStructBox.getAttributes();
		int hsbColor = 0;
		for(int a = 0; a < hsbList.size(); a++)
		{
		    Attribute att = (Attribute)hsbList.get(a);
		    if(att.getName().equals("color")) hsbColor = colorStringToInt(att.getValue());
		}
		double xdraw = INITIAL_X+.2;
		double ydraw = INITIAL_Y-.15-(.1*i);
		double[] xhslist = {xdraw,xdraw,xdraw+.1,xdraw+.1,xdraw};
		double[] yhslist = {ydraw,ydraw-.1,ydraw-.1,ydraw,ydraw};
		LGKS.set_fill_int_style(bsSolid,hsbColor,llist,d);
		LGKS.set_textline_color(hsbColor,llist,d);
		LGKS.polyline(5,xhslist,yhslist,llist,d);
		Element hashStruct = (Element)hashStructRoot.get(i);
		List hashLists = hashStruct.getChildren();
		// Draw the null slash if no items are present
		if(hashLists.size() == 0)
		{
		    double[] xslash = {xdraw+.1,xdraw};
		    double[] yslash = {ydraw,ydraw-.1};
		    LGKS.polyline(2,xslash,yslash,llist,d);
		}
		// Draw the linked list coming out of the array
		else
		{ 
		    double[] xarrow1 = {xdraw+.05,xdraw+.2};
		    double[] yarrow1 = {ydraw-.05,ydraw-.05};
		    LGKS.polyline(2,xarrow1,yarrow1,llist,d);
		    drawArrowHead(xarrow1[0],yarrow1[0],xarrow1[1],yarrow1[1],llist,d);
		    Element hashList = (Element)hashLists.get(0);
		    List hashListNodes = hashList.getChildren();
		    // Here the individual nodes of the linked list
		    // are draw
		    for(int k = 0; k < hashListNodes.size(); k++)
		    {
			String boxdata = "";
			int nodeColor = 0;
			Element hashListNode = (Element)hashListNodes.get(k);
			List attList = hashListNode.getAttributes();
			for(int c = 0; c < attList.size(); c++)
			{
			    Attribute att = (Attribute)attList.get(c);
			    if(att.getName().equals("boxdata")) boxdata = att.getValue();
			    else if(att.getName().equals("color")) nodeColor = colorStringToInt(att.getValue());
			}
			xdraw += .2;
			LGKS.set_text_height(.06,llist,d);
			double boxsize = Math.max(.1,normalized_width(boxdata));
			double[] xnode1 = {xdraw,xdraw,xdraw+boxsize,xdraw+boxsize,xdraw};
			double[] xnode2 = {xdraw+boxsize,xdraw+boxsize,xdraw+.1+boxsize,xdraw+.1+boxsize,xdraw+boxsize};
			double[] ynode = {ydraw-.01,ydraw-.099,ydraw-.099,ydraw-.01,ydraw-.01};
			LGKS.set_textline_color(nodeColor,llist,d);
			LGKS.set_fill_int_style(bsSolid,nodeColor,llist,d);
			LGKS.polyline(5,xnode1,ynode,llist,d);
			LGKS.polyline(5,xnode2,ynode,llist,d);
			LGKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);
			double xdatadraw = xdraw+(boxsize/2.0);
			double ydatadraw = ydraw-.05;
			LGKS.text(xdatadraw,ydatadraw,boxdata,llist,d);
			// If the node is the final node then the null slash is drawn
			if(k == hashListNodes.size()-1)
			{
			    double[] xslash = {xdraw+.1+boxsize,xdraw+boxsize};
			    double[] yslash = {ydraw-.01,ydraw-.099};
			    LGKS.polyline(2,xslash,yslash,llist,d);
			}
			// If it is not then the arrow is draw to the next node
			else
			{
			    double[] xarrow2 = {xdraw+boxsize+.05,xdraw+boxsize+.2};
			    double[] yarrow2 = {ydraw-.05,ydraw-.05};
			    LGKS.polyline(2,xarrow2,yarrow2,llist,d);
			    drawArrowHead(xarrow2[0],yarrow2[0],xarrow2[1],yarrow2[1],llist,d);
			}
			xdraw += boxsize;
		    }
		}
	    }
	}
    }

    // Given the start and end point of the arrow this method will
    // draw the arrow head to the specified length and angle.
    private void drawArrowHead(double x1, double y1, double x2, double y2, LinkedList llist, draw d)
    {
	double[] xtri = new double[3];
	double[] ytri = new double[3];
	xtri[0] = x2;
	ytri[0] = y2;
	double slope;
	double alphaprime;
	double alpha;
	slope = ((y2-y1)/(x2-x1));
	alphaprime = Math.atan(slope);
	alpha = Math.PI+alphaprime;
	double slope1 = Math.tan(alpha+DELTA);
	double slope2 = Math.tan(alpha-DELTA);
	double postemp1 = Math.sqrt((Math.pow(LENGTH,2))/(Math.pow(slope1,2)+1));
	double negtemp1 = postemp1*-1.0;
	postemp1 += x2;
	negtemp1 += x2;
	double postemp2 = Math.sqrt((Math.pow(LENGTH,2))/((Math.pow(1/slope2,2)+1)));
	double negtemp2 = postemp2*-1.0;
	postemp2 += y2;
	negtemp2 += y2;
	xtri[1] = Math.min(postemp1,negtemp1);
	ytri[1] = y2+(slope1*(xtri[1]-x2));
	ytri[2] = Math.max(postemp2,negtemp2);
	xtri[2] = x2+((ytri[2]-y2)/slope2);
	LGKS.fill_area(3,xtri,ytri,llist,d);
    }
}


