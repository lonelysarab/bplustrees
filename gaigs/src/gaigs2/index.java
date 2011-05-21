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
import java.awt.geom.*;

public class index extends StructureType
{
    private static final double DELTA = (Math.PI/12.0); // Angle of arrow head sides
    private static final double LENGTH = .04;	       // Length of arrow head sides
    private static final double INITIAL_X = 0.0;       // Initial x draw point
    private static final double INITIAL_Y = 0.9;       // Initial y draw point
    private Element struct;
    private ArrayList sortedVals = new ArrayList();
    private ArrayList sortedNames =  new ArrayList();
    private ArrayList nameArrowStartPoints = new ArrayList();
    private ArrayList valArrowStartPoints = new ArrayList();

    public index()
    {
	super();
    }

    public void loadStructure(Element myRoot, LinkedList llist, draw d)
    {
	load_name_and_bounds(myRoot,llist,d);
	struct = myRoot;	// Save local copy of JDOM tree
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
	Element nameRoot = null;
	Element valRoot = null;
	Element items = null;
	while(it.hasNext())
	{
	    Element temp = (Element)it.next();
	    if(temp.getName().equals("namesortedroot")) nameRoot = temp;
	    else if(temp.getName().equals("valsortedroot")) valRoot = temp;
	    else if(temp.getName().equals("indexitemlist")) items = temp;
	}
	int nameColor = 0;
	int valColor = 0;
	String nameName = "";
	String valName = "";
	List nameAttList = nameRoot.getAttributes();
	List valAttList = valRoot.getAttributes();
	for(int i = 0; i < nameAttList.size(); i++)
	{
	    Attribute temp = (Attribute)nameAttList.get(i);
	    if(temp.getName().equals("boxname")) nameName = temp.getValue();
	    else if(temp.getName().equals("color")) nameColor = colorStringToInt(temp.getValue());
	 }
	for(int i = 0; i < valAttList.size(); i++)
	{
	    Attribute temp = (Attribute)valAttList.get(i);
	    System.out.println(temp.getValue());
	    if(temp.getName().equals("boxname")) valName = temp.getValue();
	    else if(temp.getName().equals("color")) valColor = colorStringToInt(temp.getValue());
	}
	if(items == null)
	{
	    double[] nameBoxDrawX = {INITIAL_X,INITIAL_X,INITIAL_X+.1,INITIAL_X+.1,INITIAL_X};
	    double[] nameBoxDrawY = {INITIAL_Y,INITIAL_Y-.1,INITIAL_Y-.1,INITIAL_Y,INITIAL_Y};
	    double[] valBoxDrawX = {INITIAL_X,INITIAL_X,INITIAL_X+.1,INITIAL_X+.1,INITIAL_X};
	    double valDrawY = INITIAL_Y/2.0;
	    double[] valBoxDrawY = {valDrawY,valDrawY-.1,valDrawY-.1,valDrawY,valDrawY};
	    double nameNameDrawX = INITIAL_X+.05;
	    double nameNameDrawY = INITIAL_Y+.025;
	    double valNameDrawX = INITIAL_X+.05;
	    double valNameDrawY = valDrawY+.025;
	    LGKS.set_fill_int_style(bsSolid,nameColor,llist,d);
	    LGKS.set_textline_color(nameColor,llist,d);
	    LGKS.set_text_height(.04,llist,d);
	    LGKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);
	    LGKS.polyline(5,nameBoxDrawX,nameBoxDrawY,llist,d);
	    LGKS.text(nameNameDrawX,nameNameDrawY,nameName,llist,d);
	    LGKS.set_fill_int_style(bsSolid,valColor,llist,d);
	    LGKS.set_textline_color(valColor,llist,d);
	    LGKS.polyline(5,valBoxDrawX,valBoxDrawY,llist,d);
	    LGKS.text(valNameDrawX,valNameDrawY,valName,llist,d);
	}
	else
	{
	    List itemList = items.getChildren();
	    double offset = ((itemList.size()*.1)+.1);
	    double[] nameBoxDrawX = {INITIAL_X,INITIAL_X,INITIAL_X+.1,INITIAL_X+.1,INITIAL_X};
	    double[] nameBoxDrawY = {INITIAL_Y,INITIAL_Y-.1,INITIAL_Y-.1,INITIAL_Y,INITIAL_Y};
	    double[] valBoxDrawX = {INITIAL_X,INITIAL_X,INITIAL_X+.1,INITIAL_X+.1,INITIAL_X};
	    double valDrawY = INITIAL_Y-offset;
	    double[] valBoxDrawY = {valDrawY,valDrawY-.1,valDrawY-.1,valDrawY,valDrawY};
	    double nameNameDrawX = INITIAL_X+.05;
	    double nameNameDrawY = INITIAL_Y+.025;
	    double valNameDrawX = INITIAL_X+.05;
	    double valNameDrawY = valDrawY+.025;
	    LGKS.set_fill_int_style(bsSolid,nameColor,llist,d);
	    LGKS.set_textline_color(nameColor,llist,d);
	    LGKS.set_text_height(.04,llist,d);
	    LGKS.set_text_align(TA_CENTER,TA_BOTTOM,llist,d);
	    LGKS.polyline(5,nameBoxDrawX,nameBoxDrawY,llist,d);
	    LGKS.text(nameNameDrawX,nameNameDrawY,nameName,llist,d);
	    LGKS.set_fill_int_style(bsSolid,valColor,llist,d);
	    LGKS.set_textline_color(valColor,llist,d);
	    LGKS.polyline(5,valBoxDrawX,valBoxDrawY,llist,d);
	    LGKS.text(valNameDrawX,valNameDrawY,valName,llist,d);
	    for(int i = 0; i < itemList.size(); i++)
	    {
		double draw = .1*i;
		double[] xnameDraw = {INITIAL_X+.2,INITIAL_X+.2,INITIAL_X+.3,INITIAL_X+.3,INITIAL_X+.2};
		double[] ynameDraw = {INITIAL_Y-draw,INITIAL_Y-draw-.1,INITIAL_Y-draw-.1,INITIAL_Y-draw,INITIAL_Y-draw};
		double[] xvalDraw = {INITIAL_X+.2,INITIAL_X+.2,INITIAL_X+.3,INITIAL_X+.3,INITIAL_X+.2};
		double[] yvalDraw = {valDrawY-draw,valDrawY-draw-.1,valDrawY-draw-.1,valDrawY-draw,valDrawY-draw};
		LGKS.set_textline_color(1,llist,d);
		LGKS.set_fill_int_style(bsSolid,1,llist,d);
		LGKS.polyline(5,xnameDraw,ynameDraw,llist,d);
		LGKS.polyline(5,xvalDraw,yvalDraw,llist,d);
		if(i == 0)
		{
		    double[] xnamearrow = {INITIAL_X+.1,INITIAL_X+.2};
		    double[] ynamearrow = {INITIAL_Y-.05,INITIAL_Y-draw-.05};
		    double[] xvalarrow = {INITIAL_X+.1,INITIAL_X+.2};
		    double[] yvalarrow = {valDrawY-.05,valDrawY-draw-.05};
		    LGKS.set_fill_int_style(bsSolid,nameColor,llist,d);
		    LGKS.set_textline_color(nameColor,llist,d);
		    LGKS.polyline(2,xnamearrow,ynamearrow,llist,d);
		    drawArrowHead(xnamearrow[0],ynamearrow[0],xnamearrow[1],ynamearrow[1],llist,d);
		    LGKS.set_fill_int_style(bsSolid,valColor,llist,d);
		    LGKS.set_textline_color(valColor,llist,d);
		    LGKS.polyline(2,xvalarrow,yvalarrow,llist,d);
		    drawArrowHead(xvalarrow[0],yvalarrow[0],xvalarrow[1],yvalarrow[1],llist,d);
		    LGKS.set_textline_color(1,llist,d);
		    LGKS.set_fill_int_style(bsSolid,1,llist,d);
		}
		nameArrowStartPoints.add(new Point2D.Double(INITIAL_X+.25,INITIAL_Y-draw-.05));
		valArrowStartPoints.add(new Point2D.Double(INITIAL_X+.25,valDrawY-draw-.05));
	    }
	    if(itemList.size()%2==0)
	    {
		for(int i = 0; i < itemList.size(); i+=2)
		{
		    Element ele1 = (Element)itemList.get(i);
		    Element ele2 = (Element)itemList.get(i+1);
		    String nameData1 = "";
		    String nameData2 = "";
		    String valData1 = "";
		    String valData2 = "";
		    int color1 = 0;
		    int color2 = 0;
		    List attList1 = ele1.getAttributes();
		    List attList2 = ele2.getAttributes();
		    for(int k = 0; k < attList1.size(); k++)
		    {
			Attribute att = (Attribute)attList1.get(k);
			if(att.getName().equals("name")) 
			{
			    nameData1 = att.getValue();
			    sortedNames.add(nameData1);
			}
			else if(att.getName().equals("value")) 
			{
			    valData1 = att.getValue();
			    sortedVals.add(valData1);
			}
			else if(att.getName().equals("color")) color1 = colorStringToInt(att.getValue());
		    }
		    for(int k = 0; k < attList2.size(); k++)
		    {
			Attribute att = (Attribute)attList2.get(k);
			if(att.getName().equals("name")) 
			{
			    nameData2 = att.getValue();
			    sortedNames.add(nameData2);
			}
			else if(att.getName().equals("value")) 
			{
			    valData2 = att.getValue();
			    sortedVals.add(valData2);
			}
			else if(att.getName().equals("color")) color2 = colorStringToInt(att.getValue());
		    }
		    Collections.sort(sortedNames);
		    Collections.sort(sortedVals);
		    double boxsize1a = Math.max(.1,normalized_width(nameData1));
		    double boxsize2a = Math.max(.1,normalized_width(nameData2));
		    double boxsize1b = Math.max(.1,normalized_width(valData1));
		    double boxsize2b = Math.max(.1,normalized_width(valData2));
		    double itemOffset = valDrawY+.1;
		    double ydraw1 = itemOffset+(.1+(i*.1));
		    double ydraw2 = itemOffset-(.1-(i*.1));
		    double[] xItem1a = {INITIAL_X+.6,INITIAL_X+.6,INITIAL_X+.6+boxsize1a,INITIAL_X+.6+boxsize1a,INITIAL_X+.6};
		    double[] yItem1a = {ydraw1,ydraw1-.1,ydraw1-.1,ydraw1,ydraw1};
		    System.out.println(boxsize1a);
		    System.out.println(boxsize2a);
		    double[] xItem2a = {INITIAL_X+.6,INITIAL_X+.6,INITIAL_X+.6+boxsize2a,INITIAL_X+.6+boxsize2a,INITIAL_X+.6};
		    double[] yItem2a = {ydraw2,ydraw2-.1,ydraw2-.1,ydraw2,ydraw2};
		    double xData1a = INITIAL_X+.6+(boxsize1a/2.0);
		    double yData1a = ydraw1-.05;
		    double xData2a = INITIAL_X+.6+(boxsize2a/2.0);
		    double yData2a = ydraw2-.05;
		    LGKS.set_fill_int_style(bsSolid,color1,llist,d);
		    LGKS.set_textline_color(color1,llist,d);
		    LGKS.text(xData1a,yData1a,nameData1,llist,d);
		    LGKS.polyline(5,xItem1a,yItem1a,llist,d);
		    LGKS.set_fill_int_style(bsSolid,color2,llist,d);
		    LGKS.set_textline_color(color2,llist,d);
		    LGKS.text(xData2a,yData2a,nameData2,llist,d);
		    LGKS.polyline(5,xItem2a,yItem2a,llist,d);
		    double[] xItem1b = {INITIAL_X+.6+boxsize1a,INITIAL_X+.6+boxsize1a,INITIAL_X+.6+boxsize1a+boxsize1b,INITIAL_X+.6+boxsize1a+boxsize1b,INITIAL_X+.6+boxsize1a};
		    double[] yItem1b = {ydraw1,ydraw1-.1,ydraw1-.1,ydraw1,ydraw1};
		    double[] xItem2b = {INITIAL_X+.6+boxsize2a,INITIAL_X+.6+boxsize2a,INITIAL_X+.6+boxsize2a+boxsize2b,INITIAL_X+.6+boxsize2a+boxsize2b,INITIAL_X+.6+boxsize2a};
		    double[] yItem2b = {ydraw2,ydraw2-.1,ydraw2-.1,ydraw2,ydraw2};
		    double xData1b = INITIAL_X+.6+boxsize1a+(boxsize1b/2.0);
		    double yData1b = ydraw1-.05;
		    double xData2b = INITIAL_X+.6+boxsize2a+(boxsize2b/2.0);
		    double yData2b = ydraw2-.05;
		    LGKS.set_fill_int_style(bsSolid,color1,llist,d);
		    LGKS.set_textline_color(color1,llist,d);
		    LGKS.text(xData1b,yData1b,valData1,llist,d);
		    LGKS.polyline(5,xItem1b,yItem1b,llist,d);
		    LGKS.set_fill_int_style(bsSolid,color2,llist,d);
		    LGKS.set_textline_color(color2,llist,d);
		    LGKS.text(xData2b,yData2b,valData2,llist,d);
		    LGKS.polyline(5,xItem2b,yItem2b,llist,d);
		    if(sortedNames.contains(nameData1)) sortedNames.set(sortedNames.indexOf(nameData1),new Point2D.Double(INITIAL_X+.6,ydraw1-.05));
		    if(sortedNames.contains(nameData2)) sortedNames.set(sortedNames.indexOf(nameData2),new Point2D.Double(INITIAL_X+.6,ydraw2-.05));
		    if(sortedVals.contains(valData1)) sortedVals.set(sortedVals.indexOf(valData1),new Point2D.Double(INITIAL_X+.6,ydraw1-.05));
		    if(sortedVals.contains(valData2)) sortedVals.set(sortedVals.indexOf(valData2),new Point2D.Double(INITIAL_X+.6,ydraw2-.05));
		}
	    }
	    else
	    {
		String nameoddData = "";
		String valoddData = "";
		int oddcolor = 0;
		Element ele = (Element)itemList.get(0);
		List attList = ele.getAttributes();
		for(int k = 0; k < attList.size(); k++)
		{
		    Attribute att = (Attribute)attList.get(k);
		    if(att.getName().equals("name")) 
		    {
			nameoddData = att.getValue();
			sortedNames.add(nameoddData);
		    }
		    else if(att.getName().equals("value")) 
		    {
			valoddData = att.getValue();
			sortedVals.add(valoddData);    
		    }
		    else if(att.getName().equals("color")) oddcolor = colorStringToInt(att.getValue());
		}
		double oddboxsize = Math.max(.1,normalized_width(nameoddData));
		double itemOffset = valDrawY+.1;
		double[] xodddraw1 = {INITIAL_X+.6,INITIAL_X+.6,INITIAL_X+.6+oddboxsize,INITIAL_X+.6+oddboxsize,INITIAL_X+.6};
		double[] yodddraw1 = {itemOffset,itemOffset-.1,itemOffset-.1,itemOffset,itemOffset};
		double xoddnamedraw = INITIAL_X+.6+(oddboxsize/2.0);
		double yoddnamedraw = itemOffset-.05;
		LGKS.set_textline_color(oddcolor,llist,d);
		LGKS.set_fill_int_style(bsSolid,oddcolor,llist,d);
		LGKS.text(xoddnamedraw,yoddnamedraw,nameoddData,llist,d);
		LGKS.polyline(5,xodddraw1,yodddraw1,llist,d);
		double[] xodddraw2 = {INITIAL_X+.7,INITIAL_X+.7,INITIAL_X+.8,INITIAL_X+.8,INITIAL_X+.7};
		double[] yodddraw2 = {itemOffset,itemOffset-.1,itemOffset-.1,itemOffset,itemOffset};
		double xoddvaldraw = INITIAL_X+.75;
		double yoddvaldraw = itemOffset-.05;
		LGKS.text(xoddvaldraw,yoddvaldraw,valoddData,llist,d);
		LGKS.polyline(5,xodddraw2,yodddraw2,llist,d);
		if(itemList.size() == 1)
		 {
		     if(sortedNames.contains(nameoddData)) sortedNames.set(sortedNames.indexOf(nameoddData),new Point2D.Double(INITIAL_X+.6,itemOffset-.05));
		     if(sortedVals.contains(valoddData)) sortedVals.set(sortedVals.indexOf(valoddData),new Point2D.Double(INITIAL_X+.6,itemOffset-.05));
		 }
		for(int i = 1; i < itemList.size(); i+=2)
		{
		    Element ele1 = (Element)itemList.get(i);
		    Element ele2 = (Element)itemList.get(i+1);
		    String nameData1 = "";
		    String nameData2 = "";
		    String valData1 = "";
		    String valData2 = "";
		    int color1 = 0;
		    int color2 = 0;
		    List attList1 = ele1.getAttributes();
		    List attList2 = ele2.getAttributes();
		    for(int k = 0; k < attList1.size(); k++)
		    {
			Attribute att = (Attribute)attList1.get(k);
			if(att.getName().equals("name")) 
			{
			    nameData1 = att.getValue();
			    sortedNames.add(nameData1);
			}
			else if(att.getName().equals("value")) 
			{
			    valData1 = att.getValue();
			    sortedVals.add(valData1);
			}
			else if(att.getName().equals("color")) color1 = colorStringToInt(att.getValue());
		    }
		    for(int k = 0; k < attList2.size(); k++)
		    {
			Attribute att = (Attribute)attList2.get(k);
			if(att.getName().equals("name")) 
			{
			    nameData2 = att.getValue();
			    sortedNames.add(nameData2);
			}
			else if(att.getName().equals("value")) 
			{
			    valData2 = att.getValue();
			    sortedVals.add(valData2);
			}
			else if(att.getName().equals("color")) color2 = colorStringToInt(att.getValue());
		    }
		    Collections.sort(sortedNames);
		    Collections.sort(sortedVals);
		    double boxsize1a = Math.max(.1,normalized_width(nameData1));
		    double boxsize2a = Math.max(.1,normalized_width(nameData2));
		    double boxsize1b = Math.max(.1,normalized_width(valData1));
		    double boxsize2b = Math.max(.1,normalized_width(valData2));
		    double ydraw1 = itemOffset+(i*.2);
		    double ydraw2 = itemOffset-(i*.2);
		    double[] xItem1a = {INITIAL_X+.6,INITIAL_X+.6,INITIAL_X+.6+boxsize1a,INITIAL_X+.6+boxsize1a,INITIAL_X+.6};
		    double[] yItem1a = {ydraw1,ydraw1-.1,ydraw1-.1,ydraw1,ydraw1};
		    double[] xItem2a = {INITIAL_X+.6,INITIAL_X+.6,INITIAL_X+.6+boxsize2a,INITIAL_X+.6+boxsize2a,INITIAL_X+.6};
		    double[] yItem2a = {ydraw2,ydraw2-.1,ydraw2-.1,ydraw2,ydraw2};
		    double xData1a = INITIAL_X+.6+(boxsize1a/2.0);
		    double yData1a = ydraw1-.05;
		    double xData2a = INITIAL_X+.6+(boxsize2a/2.0);
		    double yData2a = ydraw2-.05;
		    LGKS.set_fill_int_style(bsSolid,color1,llist,d);
		    LGKS.set_textline_color(color1,llist,d);
		    LGKS.text(xData1a,yData1a,nameData1,llist,d);
		    LGKS.polyline(5,xItem1a,yItem1a,llist,d);
		    LGKS.set_fill_int_style(bsSolid,color2,llist,d);
		    LGKS.set_textline_color(color2,llist,d);
		    LGKS.text(xData2a,yData2a,nameData2,llist,d);
		    LGKS.polyline(5,xItem2a,yItem2a,llist,d);
		    double[] xItem1b = {INITIAL_X+.6+boxsize1a,INITIAL_X+.6+boxsize1a,INITIAL_X+.6+boxsize1a+boxsize1b,INITIAL_X+.6+boxsize1a+boxsize1b,INITIAL_X+.6+boxsize1a};
		    double[] yItem1b = {ydraw1,ydraw1-.1,ydraw1-.1,ydraw1,ydraw1};
		    double[] xItem2b = {INITIAL_X+.6+boxsize2a,INITIAL_X+.6+boxsize2a,INITIAL_X+.6+boxsize2a+boxsize2b,INITIAL_X+.6+boxsize2a+boxsize2b,INITIAL_X+.6+boxsize2a};
		    double[] yItem2b = {ydraw2,ydraw2-.1,ydraw2-.1,ydraw2,ydraw2};
		    double xData1b = INITIAL_X+.6+boxsize1a+(boxsize1b/2.0);
		    double yData1b = ydraw1-.05;
		    double xData2b = INITIAL_X+.6+boxsize2a+(boxsize2b/2.0);
		    double yData2b = ydraw2-.05;
		    LGKS.set_fill_int_style(bsSolid,color1,llist,d);
		    LGKS.set_textline_color(color1,llist,d);
		    LGKS.text(xData1b,yData1b,valData1,llist,d);
		    LGKS.polyline(5,xItem1b,yItem1b,llist,d);
		    LGKS.set_fill_int_style(bsSolid,color2,llist,d);
		    LGKS.set_textline_color(color2,llist,d);
		    LGKS.text(xData2b,yData2b,valData2,llist,d);
		    LGKS.polyline(5,xItem2b,yItem2b,llist,d);
		    if(sortedNames.contains(nameoddData)) sortedNames.set(sortedNames.indexOf(nameoddData),new Point2D.Double(INITIAL_X+.6,itemOffset-.05));
		    if(sortedNames.contains(nameData1)) sortedNames.set(sortedNames.indexOf(nameData1),new Point2D.Double(INITIAL_X+.6,ydraw1-.05));
		    if(sortedNames.contains(nameData2)) sortedNames.set(sortedNames.indexOf(nameData2),new Point2D.Double(INITIAL_X+.6,ydraw2-.05));
		    if(sortedVals.contains(valoddData)) sortedVals.set(sortedVals.indexOf(valoddData),new Point2D.Double(INITIAL_X+.6,itemOffset-.05));
		    if(sortedVals.contains(valData1)) sortedVals.set(sortedVals.indexOf(valData1),new Point2D.Double(INITIAL_X+.6,ydraw1-.05));
		    if(sortedVals.contains(valData2)) sortedVals.set(sortedVals.indexOf(valData2),new Point2D.Double(INITIAL_X+.6,ydraw2-.05));
		}
	    }
	    for(int j = 0; j < sortedVals.size(); j++)
	    {
		LGKS.set_textline_color(1,llist,d);
		LGKS.set_fill_int_style(bsSolid,1,llist,d);
		Point2D.Double nameEndPoint = (Point2D.Double)sortedNames.get(j);
		Point2D.Double valEndPoint = (Point2D.Double)sortedVals.get(j);
		Point2D.Double nameStartPoint = (Point2D.Double)nameArrowStartPoints.get(j);
		Point2D.Double valStartPoint = (Point2D.Double)valArrowStartPoints.get(j);
		double[] xtemp1 = {nameStartPoint.getX(),nameEndPoint.getX()};
		double[] ytemp1 = {nameStartPoint.getY(),nameEndPoint.getY()};
		double[] xtemp2 = {valStartPoint.getX(),valEndPoint.getX()};
		double[] ytemp2 = {valStartPoint.getY(),valEndPoint.getY()};
		LGKS.polyline(2,xtemp1,ytemp1,llist,d);
		LGKS.polyline(2,xtemp2,ytemp2,llist,d);
		drawArrowHead(nameStartPoint.getX(),nameStartPoint.getY(),nameEndPoint.getX(),nameEndPoint.getY(),llist,d);
		drawArrowHead(valStartPoint.getX(),valStartPoint.getY(),valEndPoint.getX(),valEndPoint.getY(),llist,d);
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
	if(y1>=y2) ytri[2] = Math.max(postemp2,negtemp2);
	else ytri[2] = Math.min(postemp2,negtemp2);
	xtri[2] = x2+((ytri[2]-y2)/slope2);
	LGKS.fill_area(3,xtri,ytri,llist,d);
    }
}
