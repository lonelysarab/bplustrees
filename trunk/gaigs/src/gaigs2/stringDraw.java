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
import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import java.io.*;

public final class stringDraw extends obj{     //stringDraw are 6
    String showtxt;
    int x;
    int y;
    Color C;
    double localFontMult;
    int h;
    int v;

    double dx;
    double dy;

    public static final double TEXT_SIZE_CORRECTION = 300.0;

    public stringDraw(String values, Color CType,int th, int tv,double fontMult){
        String temp;

        StringTokenizer st= new StringTokenizer(values);
	dx=Format.atof(st.nextToken());
	dy=Format.atof(st.nextToken());
	showtxt="";
	while (st.hasMoreTokens()){
	    showtxt=showtxt+" "+(st.nextToken());
	    C=CType;
	    localFontMult = fontMult;
            h=th;
            v=tv;
	}
    }

    // at zoom = 1.0
    public static int getFontSize(double size) {
	int ret = (int) Math.round(size * TEXT_SIZE_CORRECTION);
	if( ret < 7 )
	    ret = 7;
	return ret;
    }

    public static double getStringWidth(double size, String string, Graphics g) {
	g.setFont(new Font("SansSerif",Font.PLAIN,getFontSize(size)));
	return ((double) g.getFontMetrics().stringWidth(string)) / GaigsAV.preferred_width;
    }

    public static double getStringHeight(double size, Graphics g) {
	g.setFont(new Font("SansSerif",Font.PLAIN,getFontSize(size)));
	return ((double) g.getFontMetrics().getHeight()) / GaigsAV.preferred_height;
    }

    public boolean execute(Graphics g, double zoom, int vertoff, int horizoff){
        int fontH;
        int fontV;
        int tx=0;
        int ty=0;
        double maxs;
        int fontsize;

        maxs=maxsize*zoom;
	if (zoom!=1){
	    vertoff=vertoff+(int)(int)(maxsize-maxsize*zoom)/2;
	    horizoff=horizoff+(int)(int)(maxsize-maxsize*zoom)/2;
	}

        x=(int)Math.round(maxs*dx)+horizoff;
        y=(int)Math.round(maxs-(maxs*dy))+vertoff;

	// TLN 10/14/97 - the following lines embed the font size in each 
	// string.  Changed to accomodate condensed prm files
	// 	System.out.println("execute: " + g.getFont().getSize() + " :: " + TEXT_SIZE_CORRECTION + ", " + localFontMult + ", " + zoom + " = " + TEXT_SIZE_CORRECTION * localFontMult * zoom);
	//        fontsize=(int)Math.round((g.getFont().getSize()*localFontMult)*zoom * TEXT_SIZE_CORRECTION);
	fontsize = (int) Math.round(localFontMult * zoom * TEXT_SIZE_CORRECTION);
        //if (fontsize<Math.round(12*zoom))
        //    fontsize=(int)Math.round(12*zoom);
	// if (fontsize<Math.round(7*zoom))
        //    fontsize=(int)Math.round(7*zoom);
        g.setFont(new Font("SansSerif",Font.PLAIN,(int)(fontsize)));

	// Here follows the new code added to allow multiple colors per line of
	// text - A.J., 6-23-06.
	StringTokenizer st = new StringTokenizer(showtxt, "\\");

	if(st.countTokens() <= 1){
	    // There are no color delimiters in the String.
	    // Draw the text as usual.
	    fontH = g.getFontMetrics().stringWidth(showtxt);
	    fontV = g.getFontMetrics().getHeight();
	    if(h == 0){
		tx = x - (fontH / 2);
	    }else if(h == 1){
		tx = x;
	    }else if(h == 2){
		tx = x - fontH;
	    }
	    
	    if(v == 0){
		ty = y;
	    }else if(v == 1){
		ty = y + (fontV / 2);
	    }else if(v == 2){
		ty = y + fontV;
	    }

	    g.setColor(C);
	    g.drawString(showtxt, tx, ty);
	}else{
	    // Time to deal with potential color delimiters.
	    java.util.LinkedList indices = new java.util.LinkedList();
	    java.util.LinkedList colors = new java.util.LinkedList();

	    String finalString = st.nextToken();

	    while(st.hasMoreTokens()){
		String token = st.nextToken();
		if(isColorDelimiter(token.charAt(0))){
		    int textColor = extractColor(token);

		    indices.add(finalString.length());
		    colors.add(textColor);

		    // Append text (minus color delimiter) to finalString.
		    if(token.charAt(0) == '#'){
			finalString += token.substring(7);
		    }else{
			finalString += token.substring(1);
		    }
		}else{
		    // The '\\' was not a color delimiter - put it back in.
		    finalString += "\\" + token;
		}
	    }

	    // Calculate position of the text based on the size of the actual
	    // String to be printed without the color formatting characters.
	    fontH = g.getFontMetrics().stringWidth(finalString);
	    fontV = g.getFontMetrics().getHeight();
	    if(h == 0){
		tx = x - (fontH / 2);
	    }else if(h == 1){
		tx = x;
	    }else if(h == 2){
		tx = x - fontH;
	    }
	    
	    if(v == 0){
		ty = y;
	    }else if(v == 1){
		ty = y + (fontV / 2);
	    }else if(v == 2){
		ty = y + fontV;
	    }

	    if(indices.isEmpty()){
		// There were no color delimiters after all.
		// Draw the String as usual.
		g.setColor(C);
		g.drawString(finalString, tx, ty);
	    }else{
		// Draw each substring in the appropriate color.
		int start = 0;
		int end = ((Integer)indices.removeFirst()).intValue();
		int length = finalString.length();
		String temp = finalString.substring(start, end);

		// Draw the first part of the String - color is already in C.
		g.setColor(C);
		g.drawString(temp, tx, ty);

		// Adjust the starting x-coordinate for the next substring.
		tx += g.getFontMetrics().stringWidth(temp);

		while(end < length){
		    start = end;
		    if(!indices.isEmpty()){
			end = ((Integer)indices.removeFirst()).intValue();
		    }else{
			end = length;
		    }
		    Color newC = 
			colorSet(((Integer)colors.removeFirst()).intValue());
		    temp = finalString.substring(start, end);

		    // Draw the next part of the String.
		    g.setColor(newC);
		    g.drawString(temp, tx, ty);

		    // Adjust the starting x-coordinate for the next substring.
		    tx += g.getFontMetrics().stringWidth(temp);
		}
	    }
	}

	return true;
    }

    // Color methods from StructureType.java needed here because they are not
    // static methods in StructureType.java.
    public boolean isColorDelimiter(char c){
	if('a' <= c && c <= 'z')
	    c += ( 'A' - 'a' ); // mg - gotta check for lowercase as well!
	
	if((c == StructureType.Black_Color) || 
	   (c == StructureType.Brt_Green_Color) || 
	   (c == StructureType.Red_Color) ||
	   (c == StructureType.Blue_Color) || 
	   (c == StructureType.White_Color) || 
	   (c == StructureType.Lt_Blue_Color) ||
	   (c == StructureType.Magenta_Color) || 
	   (c == StructureType.Yellow_Color) || 
	   (c == StructureType.Hex_Color))
	    return (true);
	else 
	    return(false);
    }

    protected int extractColor(String c) {
	
	switch (c.charAt(0)) {

	case 'X': case 'x':
	    return(StructureType.Black);
		
	case 'R': case 'r':
	    return(StructureType.Red);
		
	case 'G': case 'g':
	    return(StructureType.Green);
		
	case 'B': case 'b':
	    return(StructureType.Blue);
		
	case 'W': case 'w':
	    return(StructureType.White);
		
	case 'L': case 'l':
	    return(StructureType.LightBlue);
		
	case 'M': case 'm':
	    return(StructureType.Magenta);
		
	case 'Y': case 'y':
	    return(StructureType.Yellow);
	case '#': 		// For backward compatibility reasons with non-hex colors
	    // hex colors are stored as negative decoded numbers and then un-negated in the
	    // colorSet function in draw.java
	    return(- (Integer.decode(c.substring(0,7))).intValue());
	default:
	    System.out.println( " Bad color choice ");
	    break;
		
		
	}
	return(StructureType.Black);  // for default
	
    }

    // Taken from draw.java but needed here for changing text colors within a
    // String.
    public Color colorSet(int x){
        Color c=Color.black;
        
        if (x==1)
            c=Color.black;
        else if (x==2)
            c=Color.blue;
        else if (x==6)
            c=Color.cyan;
        else if (x==13)
            c=Color.darkGray;
        else if (x==11)
            c=Color.gray;
        else if (x==3)
            c=Color.green;
        else if (x==9)
            c=Color.lightGray;
        else if (x==5)
            c=Color.magenta;
        else if (x==10)
            c=Color.orange;
        else if (x==12)
            c=Color.pink;
        else if (x==4)
            c=Color.red;
        else if (x==8)
            c=Color.white;
        else if (x==7)
            c=Color.yellow;
        else if (x < 0)
            c=  new Color(-x);
        return c;
    }
}

