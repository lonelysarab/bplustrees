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
import java.awt.geom.*;
import java.awt.font.*;
import java.util.*;
import java.awt.image.*;

import org.jdom.*;

/**
 * <p>
 * This class controls the drawing of text via the <code>text</code>
 * element in gaigs_sho.dtd. The attributes of the <code>text</code>
 * element correspond to the GKS parameters used to control the drawing of
 * text. The <code>x</code> and <code>y</code> values specify the coordinate in
 * the [0,1] space in which to draw the text. The possible values of 
 * <code>halign</code> are:
 * </p>
 * <pre>
 *  0 = CENTER - A value of 0 centers the text about the x-coordinate given by
 *               the value of x.
 *  1 = LEFT   - A value of 1 positions the left side of the text at the
 *               x-coordinate given by the value of x.
 *  2 = RIGHT  - A value of 2 positions the right side of the text at the
 *               x-coordinate given by the value of x.
 * </pre>
 * <p>
 * The possible values of <code>valign</code> are:
 * </p>
 * <pre>
 *  0 = BASELINE - A value of 0 centers the text about the y-coordinate given
 *                 by the value of y.
 *  1 = BOTTOM   - A value of 1 positions the bottom of the text at the
 *                 y-coordinate given by the value of y.
 *  2 = TOP      - A value of 2 positions the top of the text at the
 *                 y-coordinate given by the value of y.
 * </pre>
 * <p>
 * As in all GAIGS XML structures, the <code>fontsize</code> attribute is used
 * to control the size of the text that is drawn, and the <code>color</code>
 * attribute specifies the initial color in which the text is to be drawn. Note
 * that the text structure also supports the GAIGS color escapes within the
 * text String (e.g., \#000000 for black) to control the colors of individual
 * characters.
 * </p>
 *
 * @author Andrew Jungwirth
 * @version 1.0 (27 June 2006)
 */

public class TextStructure extends StructureType{
    // The x-coordinate at which the text should be drawn.
    private double x;

    // The y-coordinate at which the text should be drawn.
    private double y;

    // The horizontal alignment of the text relative to the x-coordinate.
    private int halign;

    // The vertical alignment of the text relative to the y-coordinate.
    private int valign;

    // The text size for the text to be drawn.
    private double fontsize;

    // The initial color of the text. The color can be changed within the 
    // String using color escapes.
    private String color;

    // Holds the text to be displayed in String format.
    private String text_string;

    // The actual text to be drawn. Each line of text must be stored in a 
    // separate element of the LinkedList so it can be drawn properly.
    private LinkedList text;

    /**
     * Default constructor. Simply calls the <code>StructureType</code> 
     * constructor.
     */
    public TextStructure(){
	super();

	text = new LinkedList();
    }

    /**
     * XML <code>loadStructure</code> method.
     * @param struct The <code>text</code> element from the XML showfile.
     * @param llist  The <code>LinkedList</code> of snapshots for this
     *               showfile.
     * @param d      The <code>draw</code> object to which this show will
     *               eventually be drawn.
     * @throws VisualizerLoadException Indicates a problem loading the text
     *                                 structure from the given XML element.
     */
    public void loadStructure(Element struct, LinkedList llist, draw d)
	throws VisualizerLoadException{
	try{
	    x = Double.parseDouble(struct.getAttributeValue("x"));
	    y = Double.parseDouble(struct.getAttributeValue("y"));
	    halign = Integer.parseInt(struct.getAttributeValue("halign"), 10);
	    valign = Integer.parseInt(struct.getAttributeValue("valign"), 10);
	    fontsize = 	
		Double.parseDouble(struct.getAttributeValue("fontsize"));
	    color = struct.getAttributeValue("color");
	}catch(Exception e){
	    throw new VisualizerLoadException("Problem loading text: " + e);
	}

	text_string = struct.getText();
	StringTokenizer text_lines = 
	    new StringTokenizer(text_string, "\f\r\n");

	while(text_lines.hasMoreTokens()){
	    text.append(text_lines.nextToken());
	}
    }

    /**
     * Indicates whether the text structure is empty or contains data.
     * @return <code>True</code> if the text structure is empty; 
     *         <code>false</code> otherwise.
     */
    public boolean emptyStruct(){
	if(text_string.equals("")){
	    return true;
	}else{
	    return false;
	}
    }

    /**
     * Draws the text.
     * @param llist The <code>LinkedList</code> of snapshots for this showfile.
     * @param d     The <code>draw</code> object to which this show will
     *              eventually be drawn.
     */
    public void drawStructure(LinkedList llist, draw d){
	double yStart = y;
	int lines;
	String line;

	if(emptyStruct()){
	    super.drawStructure(llist, d);
	    return;
	}

	LGKS.set_textline_color(new_extractColor(color), llist, d);
	LGKS.set_text_align(halign, valign, llist, d);
	LGKS.set_text_height(fontsize, llist, d);

	switch(valign){
	case 0:
	    // Center the lines of text vertically about y.
	    lines = text.size();

	    if((lines % 2) == 0){
		yStart += (fontsize / 2);

		int steps = (lines / 2) - 1;
		for(int i = 0; i < steps; i++){
		    yStart += fontsize;
		}

		while(text.hasMoreElements()){
		    line = (String)(text.remove());
		    LGKS.text(x, yStart, line, llist, d);
		    yStart -= fontsize;
		}
	    }else{
		int steps = (lines / 2);
		for(int i = 0; i < steps; i++){
		    yStart += fontsize;
		}

		while(text.hasMoreElements()){
		    line = (String)(text.remove());
		    LGKS.text(x, yStart, line, llist, d);
		    yStart -= fontsize;
		}
	    }

	    break;
	case 1:
	    // Place the bottom of the lowest line of text at y.
	    lines = text.size();

	    yStart -= (fontsize / 3);

	    for(int i = 0; i < lines; i++){
		yStart += fontsize;
	    }

	    while(text.hasMoreElements()){
		line = (String)(text.remove());
		LGKS.text(x, yStart, line, llist, d);
		yStart -= fontsize;
	    }

	    break;
	case 2:
	    // Place the top of the highest line of text at y.
	    yStart += (fontsize / 2);

	    while(text.hasMoreElements()){
		line = (String)(text.remove());
		LGKS.text(x, yStart, line, llist, d);
		yStart -= fontsize;
	    }

	    break;
	default:
	    // Should not happen - invalid value for valign.
	    System.err.println("The value " + valign + 
			       " is invalid for valign.");
	    break;
	}
    }
}

