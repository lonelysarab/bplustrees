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

import org.jdom.*;

/**
 * This class controls the loading and rendering of the legend structure used
 * to provide a color key in GAIGS visualizations. The functionality is
 * similar to the MD_Array class, but the structure is drawn to look more like
 * a typical color key.
 *
 * @author Andrew Jungwirth
 * @version 1.0 (29 June 2006)
 */

public class LegendofColors extends LinearList{
    // The number of columns of legend nodes.
    private int num_cols;

    // The maximum number of rows of legend nodes in any column.
    private int max_num_rows;

    // The width of the color key box next to the text.
    private double box_width;

    // The height of the color key box next to the text.
    private double box_height;

    // Value to draw the surrounding box
    private boolean draw_box;

    /**
     * Default constructor.
     */
    public LegendofColors(){
	super();

	num_cols = 0;
	max_num_rows = 0;
  draw_box = true;
    }

    /**
     * Indicates whether the legend structure is empty or contains data.
     * @return <code>True</code> if the legend structure is empty;
     *         <code>false</code> otherwise.
     */
    public boolean emptyStruct(){
	if(nodelist.size() == 0)
	    return true;
	else
	    return false;
    }

    /**
     * XML <code>loadStructure</code> method.
     * @param struct The <code>legend</code> element from the XML showfile.
     * @param llist  The <code>LinkedList</code> of snapshots for this
     *               showfile.
     * @param d      The <code>draw</code> object to which this show will
     *               eventually be drawn.
     * @throws VisualizerLoadException Indicates a problem loading the legend
     *                                 structure from the given XML element.
     */
    public void loadStructure(Element struct, LinkedList llist, draw d)
      throws VisualizerLoadException{
      load_name_and_bounds(struct, llist, d);

      Iterator iter = struct.getChildren().iterator();

      while(iter.hasNext()){
        Element child = (Element)iter.next();

        if(child.getName().equals("bounds")) {
          draw_box = Boolean.parseBoolean(child.getAttributeValue("drawbox"));
        }

        if(child.getName().equals("column")){
          num_cols++;
          loadColumn(child, llist, d);
        }
      }
    }

    // Helper method for the loadStructure method. Loads each column of the
    // Legend when given a <column> Element from the XML showfile.
    private void loadColumn(Element column, LinkedList llist, draw d)
	throws VisualizerLoadException{

	int num_rows = 0;
	LinkedList column_nodes = new LinkedList();
	LegendNode lnode = new LegendNode();
	Iterator iter = column.getChildren().iterator();
	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,
						 GaigsAV.preferred_height,
						 BufferedImage.TYPE_BYTE_GRAY);

	while(iter.hasNext()){
	    Element list_item = (Element)iter.next();
	    num_rows++;

	    int num_lines = 0;
	    String label_line;

	    lnode.color = list_item.getAttributeValue("color");

	    if(lnode.color.charAt(0) != '#'){
		lnode.color = "" + color_str_to_char(lnode.color);
	    }

	    Element label = list_item.getChild("label");
	    String label_text = label.getText();

	    StringTokenizer st = new StringTokenizer(label_text, "\f\r\n");
	    while(st.hasMoreTokens()){
		label_line = st.nextToken();
		lnode.text.append(label_line);
		num_lines++;

		int temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(label_line);
		double check =
		    ((double) temp / (double) GaigsAV.preferred_width);

		if (check > Maxstringlength)
		    Maxstringlength = check;

		label_line = "";
	    }

	    if(num_lines > linespernode){
		linespernode = num_lines;
	    }

	    if(num_rows > max_num_rows){
		max_num_rows = num_rows;
	    }

	    column_nodes.append(lnode);
	    lnode = new LegendNode();
	}

	nodelist.append(column_nodes);
    }

    /**
     * Method to calculate values used in drawing the legend structure.
     *
     * @param llist  The <code>LinkedList</code> of snapshots for this
     *               showfile.
     * @param d      The <code>draw</code> object to which this show will
     *               eventually be drawn.
     */
    public void calcDimsAndStartPts(LinkedList llist, draw d){
	super.calcDimsAndStartPts(llist,d);

	Lenx = 1.0 / (num_cols + (0.3 * num_cols));
	box_width = 0.1 * Lenx;
	TDx = 0.1 * Lenx;
	while((Textheight >= 0.04) &&
	      ((Lenx - box_width) < (Maxstringlength * Textheight))){
	    Textheight -= 0.001;
	}
	Leny = (1.0 - (Titleheight * title.size()) - (Titleheight / 2)) /
	    (max_num_rows + (0.2 * max_num_rows));
	box_height = Textheight + 0.02;
	TDy = 0.1 * Leny;
	while((Textheight >= 0.04) && (Leny < (linespernode * Textheight))){
	    Textheight -= 0.001;
	}

	snapheight = 1.0;
	snapwidth = 1.0;
	Startx = TDx;
	Starty = 1.0 - (Titleheight * title.size()) - (Titleheight / 2) - TDy;
	TitleStarty = 1.0 - Titleheight;
    }

    /**
     * Controls the drawing of the &lt;name&gt; Element for this legend.
     *
     * @param llist  The <code>LinkedList</code> of snapshots for this
     *               showfile.
     * @param d      The <code>draw</code> object to which this show will
     *               eventually be drawn.
     */
    public void drawTitle(LinkedList llist, draw d){
	double starty;

	starty = TitleStarty;
	LGKS.set_text_align(TA_CENTER, TA_BASELINE, llist, d);
	LGKS.set_text_height(Titleheight,llist,d);
	LGKS.set_textline_color(Black,llist,d);
	title.reset();
	while(title.hasMoreElements()){
	    String s = (String)title.nextElement();
	    if (s.length() > 2)  // potential color delimiter
		if (s.charAt(0) == Delim && inHighlightColors(s.charAt(1))) {
		    int colr = extractColor(s.charAt(1));
		    GKS.set_textline_color(colr,llist,d);
		    s = s.substring(2);
		}
	    LGKS.text(CenterScreen, starty, s, llist, d);
	    LGKS.set_textline_color(Black, llist, d);
	    starty = starty - Titleheight;
	}

	// Title is done; reset textheight for nodes in structure itself.
        LGKS.set_text_align(TA_LEFT, TA_BASELINE, llist, d);
	LGKS.set_text_height(Textheight, llist, d);
    }

    /**
     * Draws the legend.
     * @param llist The <code>LinkedList</code> of snapshots for this showfile.
     * @param d     The <code>draw</code> object to which this show will
     *              eventually be drawn.
     */
    public void drawStructure(LinkedList llist, draw d){
	double[] ptsx = new double[4];
	double[] ptsy = new double[4];
	double xPos = Startx;
	double yPos = Starty;

	LGKS.set_text_height(Textheight, llist, d);

	// Draw the box around the legend.
	ptsx[0] = 0.0;
	ptsy[0] = 0.0;
	ptsx[1] = 1.0;
	ptsy[1] = 0.0;
	ptsx[2] = 1.0;
	ptsy[2] = 1.0 - (Titleheight * title.size()) - (Titleheight / 2);
	ptsx[3] = 0.0;
	ptsy[3] = 1.0 - (Titleheight * title.size()) - (Titleheight / 2);

  if(draw_box){
    LGKS.polyline(4, ptsx, ptsy, llist, d);
  }

	nodelist.reset();

	// Draw each column of key elements.
	while(nodelist.hasMoreElements()){
	    LinkedList column = (LinkedList)nodelist.nextElement();

	    column.reset();

	    // Draw each item in the column.
	    while(column.hasMoreElements()){
		LegendNode lnode = (LegendNode)column.nextElement();

		if(((String)lnode.text.nextElement()).equals("nullandvoid") &&
		   lnode.color.equals("#FFFFFF")){
		    yPos -= (Leny + (2 * TDy));
		    continue;
		}

		ptsx[0] = xPos;
		ptsy[0] = yPos - ((Leny - box_height) / 2);
		ptsx[1] = xPos;
		ptsy[1] = ptsy[0] - box_height;
		ptsx[2] = xPos + box_width;
		ptsy[2] = ptsy[1];
		ptsx[3] = ptsx[2];
		ptsy[3] = ptsy[0];

		// Draw the box in the appropriate color.
		LGKS.set_fill_int_style(1, colorStringToInt(lnode.color),
					llist, d);
		LGKS.fill_area(4, ptsx, ptsy, llist, d);
		// Draw the outline around the box.
		LGKS.polyline(4, ptsx, ptsy, llist, d);

		lnode.text.reset();
		int lines = lnode.text.size();
		if(lines % 2 == 0){
		    double textPos =
			yPos - (Leny / 2) + (Textheight / 2) - TDy;
		    int steps = (lines / 2) - 1;
		    for(int i = 0; i < steps; i++){
			textPos += Textheight;
		    }

		    while(lnode.text.hasMoreElements()){
			LGKS.text(ptsx[2] + TDx, textPos,
				  (String)lnode.text.nextElement(), llist, d);
			textPos -= Textheight;
		    }
		}else{
		    double textPos = yPos - (Leny / 2) - TDy;
		    int steps = (lines / 2);
		    for(int i = 0; i < steps; i++){
			textPos += Textheight;
		    }

		    while(lnode.text.hasMoreElements()){
			LGKS.text(ptsx[2] + TDx, textPos,
				  (String)lnode.text.nextElement(), llist, d);
			textPos -= Textheight;
		    }
		}

		yPos -= (Leny + (2 * TDy));
	    }

	    xPos += ((3 * TDx) + Lenx);
	    yPos = Starty;
	}
    }
}

