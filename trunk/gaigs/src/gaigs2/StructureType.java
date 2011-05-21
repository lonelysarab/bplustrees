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

// MG 15 June 2005
// changes from previous StructureType:
//  - given protected LGKS (constructed in default constructor, default bounds)
//  - all references to GKS now go to LGKS
//  - added loadStructure(Element,LinkedList,draw)
//  - added set_bounds(x1,y1,x2,y2)

abstract class StructureType {

    // access your GKS graphics routines thru this. send drawing
    // commands to this in normalized [0,1]x[0,1] coordinates,
    // describing your position within the bounds given to LGKS (whole
    // draw-space default).
    protected LocalizedGKS LGKS;

    // For derived objects who want to know their bounds
    protected double structure_fontsize, structure_left, 
                     structure_right, structure_bottom, structure_top;

    // Constant declarations
    final static int Black  =  1;
    final static int Blue   =  2;
    final static int Green  =  3;
    final static int Red    =  4;
    final static int Magenta = 5;
    final static int LightBlue=6;
    final static int Yellow=   7;
    final static int White=    8;
    final static int LightGray=9;

    final static char GAIGSLineDelim = ' ';
    final static String EndSnapShot = "***^***";
    final static String  EndTitle ="***\\***";
    final static char Delim ='\\';
    final static char UseArrow  = 'A';
    final static int EndNode =32767; 
    //final static int MaxGraphNetworkNodes=50;     
    final static int MaxDataStructs =50;   
    
    // A few needed graphical constants 
    final static double Topy =1.0;

    //  The following icon constants should not be needed, but they are since Chris
    //   uses then in calculating position of title relative to icons, etc.  They
    //   may have to be revised for the PC implementation.  

    final static double IconWidth =0.12;
    final static double IconHeight =0.03;
    final static double IconToTitleGap =0.05;
    final static double CenterScreen   =0.5;
  
    //  Highlighted nodes color constants  

    final static char Black_Color ='X';
    final static char Brt_Green_Color='G';
    final static char Red_Color      ='R';
    final static char Blue_Color     ='B';
    final static char White_Color    ='W';
    final static char Lt_Blue_Color  ='L';
    final static char Magenta_Color  ='M';
    final static char Yellow_Color   ='Y';
    final static char Hex_Color   ='#';

    final static double DummyMLW =0.0;			// Needed by tree building procedures as static 
    final static double MAxTextSize  = 10000.0;//0.08;		// Any title or text size read from a show file and greater
    //than this value is reset to this value 

    // NOTE:  Playing with Tmult below allows you to finetune the text sizes.  It's 1.85
    // in the PC implementation, but I like the other setting better for the 
    // Java visualizer class

    // mg: does something a little different now. controls defaultFont's size for more spacious nodes.
    public final static double TMult = 1.2;//2.1/*1.85*/;	// used for compatibility of text sizes from
    //                                                                  Gaigs030

    //    final static double Pi =3.1415927;			// Graphics Const
    final static double Pi =3.1415926535897932384626433832795028841972;//we can do better than that
    final static int BoxSize =5;
    final static int LineSize =2;
    final static double TitleToSSGap =0.05;
    final static int TriSize =4;
    final static double TreeSideBorder =0.05;
    final static int MaxLevels =300;
    final static double  ArrowPercentofLenx=0.5;
    final static double  MaxTextSize = 10000.0;//0.08;     // Any title or text size read from a show file and greater
    // than this value is reset to this value 


    static int maxsize = GaigsAV.preferred_width;;			// Should match the maxsize in the obj and 
    //GraphPrim classes

    final static int TA_CENTER    = 0;		    // Used for compatibility with Pascal GKS calls
    final static int TA_LEFT      = 1;
    final static int TA_RIGHT     = 2;

    final static int TA_BASELINE  = 0;
    final static int TA_BOTTOM    = 1;
    final static int TA_TOP       = 2;

    final static int bsSolid      = 0;			// Used for compatibility with Pascal GKS calls
    final static int bsClear      = 1;
    final static int bsHorizontal = 2;
    final static int bsVertical   = 3;
    final static int bsFDiagonal  = 4;
    final static int bsBDiagonal  = 5;
    final static int bsCross      = 6;
    final static int bsDiagCross  = 7;

 
    //    In most cases	(except graph and networks), the nodelist field of the
    //     objects derived from
    //    StructureType contains a list of a given type node.  For binary and general
    //    trees, nodelist contains only the root of the tree.  Title
    //    is a linked list of single text lines.}


    int linespernode ; // Number of lines per snapshot node 
    double Maxstringlength;     // length of the longest line of this snapshot, in WC }
    double Maxtitlelength;      // length of longest title line of this snapshot, in WC }
    double Xcenter;             // geometric x-coord center of the snapshot, def. 0.5 }
    double Ycenter;       // geometric y-coord center of the snapshot, def. 0.5 }
    double snapheight;    // height of entire snapshot, in WC }
    double snapwidth;     // width of entire snapshot, in WC }
    protected double Textheight;    // height of each character in WC }
    protected double Titleheight;   // height of title character in world coordinates }
    LinkedList title ;    // pointer to title of snapshot }
    LinkedList nodelist;	// list of text strings in structure nodes
    double TitleStarty, TitleEndy; // the y-coordinates where the title/caption starts and ends
    Font defaultFont;
    /* Arer these better declared in inherited classes?	 YES!!!
       double Lenx;
       double Leny;
       double Startx;
       double Starty;
       double TDx;
       double TDy;
    */


    // constructor
    public StructureType () {
	title = new LinkedList();
	nodelist = new LinkedList();
        Xcenter=0.5;
        Ycenter=0.5;
        snapheight=0;
        snapwidth=0;
	structure_left = 0.0;
	structure_bottom = 0.0;
	structure_right = 1.0;
	structure_top = 1.0;
	structure_fontsize = Textheight= 0.03;  /* 0.0175 in wbatgaigs */ //Was 0.015 in non-Delphi version }
	// Playing with this setting of the default Textheight finetunes
	// the spacing around strings in rectangular nodes (and circular nodes?)
	Titleheight= 0.035;//0.035; /* 0.02 in wbatgaigs */
        Maxtitlelength=0.0;
        Maxstringlength=0.0; 
	// The 300 used here should be consistent with the setting used in GraphPrim class
	defaultFont = new Font("Serif",Font.BOLD, (int)(stringDraw.TEXT_SIZE_CORRECTION * Textheight * TMult));

	LGKS = new LocalizedGKS();
    }

    // set_bounds(x1,y1,x2,y2)
    // this structure will draw itself in the region [x1,x2]x[y1,y2]
    public void set_bounds(double x1, double y1, double x2, double y2) {
	structure_left = x1; structure_bottom = y1; structure_right = x2; structure_top = y2;
	LGKS.set_bounds(x1,y1,x2,y2);
    }

    // Responsible from loading the optional Textheight and TitleTextheight	parameters
    // from the line containing the structure type.  This method expects a space/tab
    // delimited tokenizer containing (possibly) containing these two values
    public void loadTextHeights(StringTokenizer structLine, LinkedList llist, draw d)
	/* throws VisualizerLoadException */	 {

	if (structLine.hasMoreTokens()) {
	    String s = structLine.nextToken();
	    Textheight = Format.atof(s);
	    if (Textheight > MaxTextSize) Textheight = MaxTextSize;
	}

	if (structLine.hasMoreTokens()) {
	    String s = structLine.nextToken();
	    Titleheight = Format.atof(s);
	    if (Titleheight > MaxTextSize) Titleheight = MaxTextSize;
	}
        Textheight=Textheight; // unless we're running daemons.. what??
        Titleheight=Titleheight;

	//mg - dealing with the headache of textsizes:
	if(Textheight < 0.025)
	    Textheight = 0.025;
	if(Titleheight < 0.025)
	    Titleheight= 0.025;
		  
	// mg - this wasnt done:
	defaultFont = new Font("Serif",Font.BOLD, (int)(stringDraw.TEXT_SIZE_CORRECTION * Textheight * TMult));
    }
	
    // Responsible for loading the number of lines per node
    // Receives a line delimited tokenizer.  It will only process one line of 
    // that tokenizer.  It must read that line, create a space/tab delimited
    // tokenizer from it, grab the number of lines per node from that tokenizer.
    // Structures that can contain additional information following the number
    // of lines per node (such as trees) should then override this generic 
    // loadLinesPerNodeInfo with their own version of the method which will call
    // on the super version to get the actual lines per node and add additional 
    // code to process the other information
    public void loadLinesPerNodeInfo(StringTokenizer st, LinkedList llist, draw d)
	throws VisualizerLoadException	 {

	String tempString, tempString2;

	if (st.hasMoreTokens()) 
	    tempString = st.nextToken();
	else 
	    throw ( new VisualizerLoadException ("Expected lines per node - found end of string"));
	StringTokenizer t = new StringTokenizer (tempString, " \t");
	if (t.hasMoreTokens()) 
	    tempString2 = t.nextToken();
	else 
	    throw ( new VisualizerLoadException ("Expected lines per node - found " + tempString));
	linespernode = Format.atoi(tempString2);

    }

    // Responsible for loading title, that is, info after the data
    // data structure type and befor the ***\***, including calling 
    // loadExtra, which does nothing except for tree types
    public void loadTitle (StringTokenizer st, LinkedList llist, draw d) 
	throws VisualizerLoadException	{

	String tline, tlinenocolor;
	int temp;
	double check;
	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);
	
	if (st.hasMoreTokens()) {
	    tline = st.nextToken();
	}
	else 
	    throw (new VisualizerLoadException ("End of data when expecting title"));
	while (tline.compareTo(EndTitle) != 0) {
	    tline = tline.trim();
	    // remove delims if present and calclate widthusing getGraphics
	    tlinenocolor = textwocolor(tline);

	    temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(tlinenocolor);


	    // This strategy works also, but the BufferedImage seems
	    // guaranteed and the java docs regarding
	    // FontRenderContext seem to indicate less reliable

	    //temp = (int)(defaultFont.getStringBounds(tlinenocolor, new FontRenderContext(new AffineTransform(),true,true))).getWidth();

	    //System.out.println("String width in load title" + temp);

	    //	    check = ((double) temp / (double) buffer.getSize().width /*maxsize*/);
	    check = ((double) temp / (double) GaigsAV.preferred_width /*maxsize*/);

	    // System.out.println("Check load title" + check);

	    if (check > Maxtitlelength)
		Maxtitlelength = check;
	    title.append(tline);
	    if (st.hasMoreTokens()) {
		tline = st.nextToken();
	    }
	    else 
		throw (new VisualizerLoadException ("End of data when expecting title"));
	}

    }

    // load name and bounds info common to all localized derived structures
    public void load_name_and_bounds(Element my_root, LinkedList llist, draw d) {
	loadTitle( my_root.getChild("name"), llist, d );

	// load bounds
	Element bounds_node = my_root.getChild("bounds");
	if(bounds_node != null) {
	    set_bounds( Format.atof( bounds_node.getAttributeValue("x1") ),
			Format.atof( bounds_node.getAttributeValue("y1") ),
			Format.atof( bounds_node.getAttributeValue("x2") ),
			Format.atof( bounds_node.getAttributeValue("y2") ) );

	    structure_fontsize = Textheight = Titleheight = Format.atof( bounds_node.getAttributeValue("fontsize") );
	}
    }

    public void loadTitle(Element title_node, LinkedList llist, draw d) {
	if(title_node == null) {
	    Maxtitlelength = 0;
	    return;
	}

	//	System.out.println(title_node.getText());
	Titleheight = Format.atof( title_node.getAttributeValue("fontsize") );

	StringTokenizer st = new StringTokenizer( title_node.getText().trim(), "\r\f\n" );

	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,
						 BufferedImage.TYPE_BYTE_GRAY);

	while( st.hasMoreTokens() ) {
	    String tlinenocolor, tline = st.nextToken();
	    tline = tline.trim();
	    tlinenocolor = textwocolor(tline);
	    int linelength = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(tlinenocolor);
	    double check = ( (double) linelength / (double) GaigsAV.preferred_width );

	    if(check > Maxtitlelength)
		Maxtitlelength = check;
	    title.append(tline);
	}
    }

    // Given s, return its normalized width 
    protected double normalized_width(String s) { 

	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,
						 BufferedImage.TYPE_BYTE_GRAY);

	//	int text_width_pixels = ((int) (Textheight/0.03)) * buffer.getGraphics().getFontMetrics(defaultFont).stringWidth( s );
	int text_width_pixels = ((int) (LGKS.get_text_height()/0.03)) * buffer.getGraphics().getFontMetrics(defaultFont).stringWidth( s );
	//System.out.println(Textheight);
	double width = ((double) text_width_pixels / (double) GaigsAV.preferred_width);
	return width;
    }

    protected String textwocolor(String s) {
	
	if (s.length() >= 2 && s.charAt(0) == '\\')
	    if (s.charAt(1) != '#')	// Not a hex-specified color
		return (s.substring(2));
	    else
		return (s.substring(8));
	else
	    return (s);
    }

    protected boolean inHighlightColors (char c) {
	if( 'a' <= c && c <= 'z' )
	    c += ( 'A' - 'a' ); // mg - gotta check for lowercase as well!
	
	if ((c == Black_Color) || (c == Brt_Green_Color) ||(c == Red_Color) ||(c == Blue_Color) ||
	    (c == White_Color) ||(c == Lt_Blue_Color) ||(c == Magenta_Color) ||(c == Yellow_Color) || (c == Hex_Color) )
	    return (true);
	else return(false);
    }

    protected char color_str_to_char(String color) {
	color = color.toLowerCase();
	if(color.equals("black"))
	    return 'x';
	if(color.equals("green"))
	    return 'g';
	if(color.equals("red"))
	    return 'r';
	if(color.equals("blue"))
	    return 'b';
	if(color.equals("white"))
	    return 'w';
	if(color.equals("light blue"))
	    return 'l';
	if(color.equals("magenta"))
	    return 'm';
	if(color.equals("yellow"))
	    return 'y';
	return 'x'; // default: black
    }

    protected int extractColor(char c) {
	
	switch (c) {

	case 'X': case 'x':
	    return(Black);
		
	case 'R': case 'r':
	    return(Red);
		
	case 'G': case 'g':
	    return(Green);
		
	case 'B': case 'b':
	    return(Blue);
		
	case 'W': case 'w':
	    return(White);
		
	case 'L': case 'l':
	    return(LightBlue);
		
	case 'M': case 'm':
	    return(Magenta);
		
	case 'Y': case 'y':
	    return(Yellow);
	default:
	    System.out.println( " Bad color choice ");
	    break;
		
		
	}
	return(Black);  // for default
	
    }

    protected int extractTextColorForHighlightedNodes(char c) {
	
	switch (c) {

	case 'X': case 'x':
	    return(White);
		
	case 'R': case 'r':
	    return(Yellow);
		
	case 'G': case 'g':
	    return(Black);
		
	case 'B': case 'b':
	    return(Yellow);
		
	case 'W': case 'w':
	    return(Black);
		
	case 'L': case 'l':
	    return(Black);
		
	case 'M': case 'm':
	    return(Yellow);
		
	case 'Y': case 'y':
	    return(Black);
	default:
	    System.out.println( " Bad color for text in node ");
	    break;
		
		
	}
	return(Black);  // for default
	
    }


    protected int new_extractColor(String c) {
	
	switch (c.charAt(0)) {

	case 'X': case 'x':
	    return(Black);
		
	case 'R': case 'r':
	    return(Red);
		
	case 'G': case 'g':
	    return(Green);
		
	case 'B': case 'b':
	    return(Blue);
		
	case 'W': case 'w':
	    return(White);
		
	case 'L': case 'l':
	    return(LightBlue);
		
	case 'M': case 'm':
	    return(Magenta);
		
	case 'Y': case 'y':
	    return(Yellow);
	case '#': 		// For backward compatibility reasons with non-hex colors
	    // hex colors are stored as negative decoded numbers and then un-negated in the
	    // colorSet function in draw.java
	    return(- (Integer.decode(c.substring(0,7))).intValue());
	default:
	    System.out.println( " Bad color choice ");
	    break;
		
		
	}
	return(Black);  // for default
	
    }

    // Given color as a (usually hex) string, convert it to the right
    // Java color as int
    protected int colorStringToInt(String color) {
	color = color.toLowerCase();
	if(color.equals("black"))
	    return Black;
	if(color.equals("green"))
	    return Green;
	if(color.equals("red"))
	    return Red;
	if(color.equals("blue"))
	    return Blue;
	if(color.equals("white"))
	    return White;
	if(color.equals("light blue"))
	    return LightBlue;
	if(color.equals("magenta"))
	    return Magenta;
	if(color.equals("yellow"))
	    return Yellow;
	if(color.charAt(0) == '#')
	    return(- (Integer.decode(color.substring(0,7))).intValue());
	return Black; // default: black
    }

    // Given a fill-area color as a (usually hex) string, convert text
    // to appear in the fill area to the right Java color as int
    protected int colorStringToTextColorInt(String color) {
	color = color.toLowerCase();
	if(color.equals("black"))
	    return White;
	if(color.equals("green"))
	    return Black;
	if(color.equals("red"))
	    return Yellow;
	if(color.equals("blue"))
	    return Yellow;
	if(color.equals("white"))
	    return Black;
	if(color.equals("light blue"))
	    return Black;
	if(color.equals("magenta"))
	    return Yellow;
	if(color.equals("yellow"))
	    return Black;
	if(color.charAt(0) == '#') {
	    int colorint = Integer.decode(color.substring(0,7)).intValue();
	    int r = colorint / (256*256);
	    int g = (int) ( ( (colorint / 256) % 256 ) * 1.65 ); // green seems to be much brighter than red or blue
	    int b = colorint % 256;
	    //System.out.println("Color " + color + " as " + r + "," + g + "," + b + " = " + (r+g+b));
	    if( r+b+g > (256+256+256)/2 )
		return(Black);
	    else
		return(White);
	}
	return Black; // default: black
    }

    protected int new_extractTextColorForHighlightedNodes(String c) {
	
	switch (c.charAt(0)) {

	case 'X': case 'x':
	    return(White);
		
	case 'R': case 'r':
	    return(Yellow);
		
	case 'G': case 'g':
	    return(Black);
		
	case 'B': case 'b':
	    return(Yellow);
		
	case 'W': case 'w':
	    return(Black);
		
	case 'L': case 'l':
	    return(Black);
		
	case 'M': case 'm':
	    return(Yellow);
		
	case 'Y': case 'y':
	    return(Black);
	case '#': 
	    int color = Integer.decode(c.substring(0,7)).intValue();
	    int r = color / (256*256);
	    int g = (int) ( ( (color / 256) % 256 ) * 1.65 ); // green seems to be much brighter than red or blue
	    int b = color % 256;
	    //System.out.println("Color " + c + " as " + r + "," + g + "," + b + " = " + (r+g+b));
	    if( r+b+g > (256+256+256)/2 )
		return(Black);
	    else
		return(White);
	default:
	    System.out.println( " Bad color for text in node ");
	    break;
		
		
	}
	return(Black);  // for default
	
    }


    // return a linked list of strings corresponding to the lines per node
    // that are in one node of the structure.  Along the way, update the setting
    // for Maxstringlength = the length of the longest line in this snapshot.
    // In case we encounter the end of snapshot while trying to load this node,
    // throw the appropriate exception.
    protected LinkedList getTextNode(StringTokenizer st, int linesPerNode,
				     LinkedList llist, draw d) throws 
					 EndOfSnapException, VisualizerLoadException {

	String tline, tlinenocolor;
	int temp;
	double check; 

	LinkedList str_ll = new LinkedList(); 
	BufferedImage buffer = new BufferedImage(GaigsAV.preferred_width,GaigsAV.preferred_height,BufferedImage.TYPE_BYTE_GRAY);
	
	for (int i = 0; i < linesPerNode; ++i) {
	    if (st.hasMoreTokens()) {
		tline = st.nextToken();
		if (tline.compareTo(EndSnapShot) == 0) 
		    throw (new EndOfSnapException());
	    }
	    else 
		throw (new VisualizerLoadException ("End of data when expecting node information"));

	    tline = tline.trim();
	    tlinenocolor = textwocolor(tline);

	    temp = buffer.getGraphics().getFontMetrics(defaultFont).stringWidth(tlinenocolor);

	    // This strategy works also, but the BufferedImage seems
	    // guaranteed and the java docs regarding
	    // FontRenderContext seem to indicate less reliable
	    //	    temp = (int)(defaultFont.getStringBounds(tlinenocolor, new FontRenderContext(new AffineTransform(),true,true))).getWidth();

	    //	    System.out.println("String width in load title" + temp);

	    //	    check = ((double) temp / (double) buffer.getSize().width /*maxsize*/);
	    check = ((double) temp / (double) GaigsAV.preferred_width /*maxsize*/);

	    //	    System.out.println("Check load title" + check);

// 	    temp = d.getGraphics().getFontMetrics(defaultFont).stringWidth(tlinenocolor);
// 
// 	    System.out.println("String width in get text node" + temp);
// 
// 	    check = ((double) temp / (double) d.getSize().width /*maxsize*/);
// 
// 	    System.out.println("Check get text node" + check);
// 
	    if (check > Maxstringlength)
		Maxstringlength = check;
	    str_ll.append(tline);
	}
	return(str_ll);
    }



    // Establish the protected variables that determine starting point,
    // length, and height of a node.  Also the starting vertical coordinate of
    // the title (and its ending vertical coordinate).  
    // When node-oriented structures override this, they will
    // need to establish all of these variables.  When a non-node-oriented
    // structure overrides it, only the starting and ending points of the title need
    // to be determined.  HOWEVER, in the latter case,
    // the super class's calcDimsAndStartPts should
    // probably be called to insure reliable settings for all variables.
    public void calcDimsAndStartPts(LinkedList llist, draw d) {

	Xcenter=CenterScreen; // By default Snapshts are always centered *)
	Ycenter=CenterScreen; // Change this in LoadStructure if desired *)
	if ((Textheight*(double)linespernode)>Maxstringlength) 
	    Maxstringlength=(Textheight*(double)linespernode)+
		(Textheight*0.5*(double)(linespernode-1));

	// make sure the node text isn't taller than it is long}
	if ( Maxstringlength<(2*Textheight) )
	    Maxstringlength=(2*Textheight);
	TitleStarty = 1.0 - Titleheight;
	TitleEndy = 1.0-(1.5*((double)title.size())*Titleheight);
	//Now, create the Graphic Primitives List in SegInfo}
	// First we must establish the text attributes... *)
	LGKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
	LGKS.set_textline_color(Black,llist,d); // Default at first *)
	LGKS.set_text_height(Textheight,llist,d);
	
    }

    // Load the rest of the structure-specific information from the line-delimited
    // tokenizer st, appending graphic prims to llist as necessary
    //quasi-abstract 
    void loadStructure (StringTokenizer st, LinkedList llist, draw d)
	throws VisualizerLoadException {
	throw new VisualizerLoadException("This structure has not implemented " +
					  "loadStructure(StringTokenizer,LinkedList,draw");
    }

    // Load the structure from the root of its XML tree (JDOM style)
    //quasi-abstract
    void loadStructure(Element myRoot, LinkedList llist, draw d)
	throws VisualizerLoadException {
	throw new VisualizerLoadException("loadStructure(jdom.Element,LinkedList,draw) is not yet " +
					  "supported for this class.");
    }

    public void drawTitle(LinkedList llist, draw d) {

	double starty;

//     	// Begin by setting the font height for titles
// 	LGKS.set_text_height(Titleheight, llist, d);

	// Fill in here ...

	// The following keeps StartTitleY from being changed, as it is in the
	// PC impl of GAIGS where the title is drawn after the structure.  Because
	// the Java version draws the title first, we should not let the title drawing
	// change StartTitleY
	starty = TitleStarty;
	LGKS.set_text_align(TA_CENTER,TA_BASELINE/* was TA_BOTTOM, but BASELINE works better*/,llist,d);
	LGKS.set_text_height(Titleheight,llist,d);
	LGKS.set_textline_color(Black,llist,d);
	title.reset();
	while (title.hasMoreElements()) {
	    String s = (String) title.nextElement();
	    if (s.length() > 2)  // potential color delimiter
		if (s.charAt(0) == Delim && inHighlightColors(s.charAt(1))) {
		    int colr = extractColor(s.charAt(1));
		    GKS.set_textline_color(colr,llist,d);
		    s = s.substring(2);
		}
	    LGKS.text(CenterScreen,starty,s,llist,d);
	    LGKS.set_textline_color(Black, llist, d);
	    starty=starty-(1.5*(Titleheight));
	}


	// Title is done, so last thing to do reset textheight to that for nodes
	// in structure itself
        LGKS.set_text_align(TA_LEFT,TA_BOTTOM,llist,d);
	LGKS.set_text_height(Textheight, llist, d);

    }

    // All dervived StructureTypes should override this -- be sure to
    // call super on this method when your structure is empty
    void drawStructure (LinkedList llist, draw d){

	double NTitleLines;

	if (emptyStruct()) {
	    NTitleLines = (double) title.size();
            TitleStarty=CenterScreen+(0.5*(((NTitleLines+1.0)*Titleheight)+(NTitleLines*
									    (0.5*Titleheight))));
            Xcenter=CenterScreen;
            Ycenter=CenterScreen;
            LGKS.set_text_align(TA_CENTER, TA_BOTTOM, llist, d);
            LGKS.set_text_height(Titleheight,llist,d);
            String temp = new String (" ");
            LGKS.text(CenterScreen,TitleStarty-
		     (((NTitleLines+1.0)*(Titleheight))+(NTitleLines*Titleheight)),
		     temp,llist,d);
            LGKS.set_text_align(TA_LEFT,
			       TA_BOTTOM,llist,d);
	}

    }

    // OR as necessary
    boolean emptyStruct() {
	return false;
    }

}

/*class VisualizerLoadException extends IOException {

public VisualizerLoadException () { }
public VisualizerLoadException (String whatIsWrong) {
super (whatIsWrong);
}

}*/

