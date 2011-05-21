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
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import java.applet.*;
import java.net.*;
import javax.swing.*;
import java.lang.reflect.*;

import org.jdom.*;

import jhave.core.*;
import jhave.question.*;
import jhave.Algorithm;

//this is a JPanel that will make a linked list of snapshots from a linked list of Primitives
//it also stores them and draws them appropriately
public class draw extends JPanel implements MouseListener, MouseMotionListener {
    Color lineC=Color.black;    //line color
    Color fillC=Color.black;    //fill color
    Color textC=Color.black;    //text color
    int LineH=0;
    int LineV=0;
    double fontMult = 0.01; // this does..?
    
    public final static boolean debug = GaigsAV.debug;
    static public double zoom;        //zoom factor
    public int vertoff= 0;//-5;        //vertical offset for scrolling
    public int horizoff= 0;//-17;      //horizontal offset for scrolling
    public boolean multiTrigger=false;  //set to true if a primative triggers multiple windows
    
    private int Snaps=0;                                    //number of total snapshots
    private int SnapAt=1;                           //current snapshot
    
    private LinkedList list_of_snapshots =new LinkedList();          // The list of snapshots
    
    //     private LinkedList urlList=new LinkedList();    // And their corresponding URL's
    //     private String prevUrl="";                                  //the last URL called.
    
    private Graphprim graphWin;                     //mommy (our parent)
    
    private AppletContext contextOf;
    
    private int startX,startY;
    private boolean dragging = false;
    private Image my_image = null;
    private int my_width, my_height;
    
    private boolean animation_done; // set in paintComponent to flag if animation steps are done.
    // paintComponent is called indirectly via paintImmediately in execute, which in turns returns
    // this flag to the Graphprim from whence it was called

    // These two new boolean variables are used to center the visualization in
    // the window when it starts. Note that the visualization will only appear
    // centered in the window if the structures are properly drawn in the
    // center of their normalized [0,1] coordinates - A.J., 6-27-06.
    private boolean no_mouse_drag = true;  // True until the viewing window
                                           // has been dragged by the user;
                                           // false afterward. Used to properly
                                           // set the initial values of vertoff
                                           // and horizoff to center the
                                           // visualization when it starts.

    private boolean first_paint_call = true; // True until paintComponent is
                                             // called; false afterward. This
                                             // is necessary because the first
                                             // call to paintComponent receives
                                             // the width of the entire screen
                                             // before the 
                                             // pseudocode/documentation pane
                                             // is added so the first call must
                                             // calculate the horizontal center
                                             // position differently to prevent
                                             // the image from "jumping" when
                                             // paintComponent is called the
                                             // second time.

                                             // NOTE (7/27/07 - TN)
                                             // This may not be
                                             // necessary any longer
                                             // now that I've
                                             // converted everything
                                             // to drawing into a
                                             // Buffered image
    public draw(Graphprim gp) {
        if (zoom == 0) // then this is first construction since Java inits to zero
        {
            zoom = 1.0;
        }
        graphWin = gp;
        setBackground(new Color(1.0f, 1.0f, 1.0f));    //ensure white bg
	my_width = getSize().width;
	my_height = getSize().height;
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    //copy contructors can be fun, too
    public draw(draw s){
        zoom=s.zoom;
	no_mouse_drag = s.no_mouse_drag;
	first_paint_call = s.first_paint_call;
        fontMult = s.fontMult;
        list_of_snapshots=s.list_of_snapshots;
        //         urlList=s.urlList;
        Snaps=s.Snaps;
        vertoff=s.vertoff;
        horizoff=s.horizoff;
        SnapAt=s.SnapAt;
        contextOf=s.contextOf;
        //         prevUrl=s.prevUrl;
	my_width = s.my_width;
	my_height = s.my_height;
        graphWin=s.graphWin;
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    //copies and sets a new snapAt
    public draw(draw s,int sAtNew){
        list_of_snapshots=s.list_of_snapshots;
        fontMult = s.fontMult;
        zoom=s.zoom;
	no_mouse_drag = s.no_mouse_drag;
	first_paint_call = s.first_paint_call;
        //         urlList=s.urlList;
        Snaps=s.Snaps;
        vertoff=s.vertoff;
        horizoff=s.horizoff;
        SnapAt=sAtNew;
        contextOf=s.contextOf;
        //         prevUrl=s.prevUrl;
	my_width = s.my_width;
	my_height = s.my_height;
        graphWin=s.graphWin;
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    //resets the draw to the original state
    public /*synchronized*/ void ReInitProg(){
        list_of_snapshots=new LinkedList();
        //         urlList=new LinkedList();
        SnapAt=1;
        Snaps=0;
        zoom=1;
	no_mouse_drag = true;
        //         String prevUrl="";
    }
    
    
    //draws the current snapshot
    public void paintComponent(Graphics g){
        
        //System.out.println("In paint in draw");
        super.paintComponent(g);
	my_width = getSize().width;
	my_height = getSize().height;
	if (my_image != null)
	    g.drawImage(my_image, 0, 0, my_width, my_height, this);

        
        setBackground(new Color(1.0f, 1.0f, 1.0f));
//         g.setColor(Color.black);
//         g.drawRect(0,0,my_width-1,my_height-1);
        
    }
    
    //receives an applet context
    public /*synchronized*/ void changeContext(AppletContext viewerIs){
        contextOf=viewerIs;
    }
    
    /* Grab mouse events and pass them on to our parent.  This is a really ugly
     * kludge.  Fixme please.  The problem is that Canvases will grab the mouse events
     * so the parent Frame never receives them.  For some reason trying to
     * disableEvents(AWTEvent.MOUSE_EVENT_MASK|AWTEvent.MOUSE_MOTION_EVENT_MASK)
     * did not successfully keep the events from reaching the Canvas.
     *
     * Another way to do this is to pass in a Vector of all the Canvases to each Canvas.
     * Then the Canvas can inform the other Canvases accordingly instead of leaving it
     * up to the Frame to do so.  I'm not convinced that's a better way, though.
     * JRE 8/jul/99
     */
    public void mouseMoved      (MouseEvent e)  { graphWin.mouseMoved(e); }
    public void mouseEntered    (MouseEvent e)  { graphWin.mouseEntered(e); }
    public void mouseExited     (MouseEvent e)  { graphWin.mouseExited(e); }
    public void mousePressed    (MouseEvent e)  { graphWin.mousePressed(e); }
    public void mouseReleased   (MouseEvent e)  { graphWin.mouseUp(e, dragging); }
    public void mouseClicked    (MouseEvent e)  { 
	no_mouse_drag = true;
	graphWin.mouseClicked(e);
    }
    public void mouseDragged    (MouseEvent e)  { mDrag(e.getX(), e.getY()); }
    
    //for scrolling
    public /*synchronized*/ boolean mDown(int x,int y ){
        startX=x;
        startY=y;
        return true;
    }
    
    //for scrolling
    public /*synchronized*/ boolean mDrag(int x,int y ){
        dragging = true;
	no_mouse_drag = false;
        int vertTemp,horizTemp;
        
        vertTemp=vertoff;
        horizTemp=horizoff;
        vertTemp=vertTemp-(startY-y);
        horizTemp=horizTemp-(startX-x);
        if (vertTemp!=vertoff || horizTemp!=horizoff){
            vertoff=vertTemp;
            horizoff=horizTemp;
            startX=x;
            startY=y;
	    execute(SnapAt);
//             repaint();
        }
        return true;
    }
    
    /**
     *
     */
    private static final Shape createMask(int width, int height) {
        Shape outside = new Rectangle2D.Double(0, 0, width, height);
        Shape inside = new RoundRectangle2D.Double(10, 10, width - 20, height - 20, 50, 50);
        
        Area area = new Area(outside);
        area.subtract(new Area(inside));
        
        return area;
    }
    
    //draws the current snapshot with a new URL and SnapAt

    // Although it presently returns a boolean, that was only needed
    // during my aborted attempted at animated graphics primitives.
    // Until those become a reality the boolean value returned by this
    // routine is unnecessary
    public /*synchronized*/ boolean execute(int sAt){

	// The commented-out variables below are remnants from legacy
	// code -- they appear to be no longer needed.

//         String urlTemp="";
//         int z=0;
//         int idx;
//         int urlid;
//         boolean showURL=false;
        animation_done = true;	// May be re-set in paintComponent via indirect paintImmediately call at end

                                // Was used in abored attempted to
                                // introduce animated primitives.  Now
                                // it's probably excess baggage that
                                // remains because I still have hopes
                                // of eventually having animated
                                // primitives
        SnapAt=sAt;

	if (getSize().width != 0 && getSize().height != 0) {
	    my_width = getSize().width;            // set dimensions
	    my_height = getSize().height;
	}
	else {
	    my_width = GaigsAV.preferred_width;            // set dimensions
	    my_height = GaigsAV.preferred_height;
	}
        BufferedImage buff = new BufferedImage(my_width, my_height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) buff.getGraphics(); // need a separate object each time?
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	g2.setColor(Color.WHITE);
	g2.fillRect(0,0,my_width,my_height);
	// Set horizoff and vertoff to properly center the visualization in the
	// viewing window. This is not quite perfect because visualizations
	// that are not properly centered within their [0,1] localized
	// coordinates will not be perfectly centered, but it is much better 
	// than it was previously.

// 	if(no_mouse_drag){
// 	    if(first_paint_call){
// // 		horizoff = (my_width - (int)(0.25 * my_width) - 
// // 			    GaigsAV.preferred_width) / 2;
// 		horizoff = (my_width - GaigsAV.preferred_width) / 2;
// 		first_paint_call = false;
// 	    }else{
// 		horizoff = (my_width - GaigsAV.preferred_width) / 2;
// 		no_mouse_drag = false;
// 	    }
// 	}
        
	if (no_mouse_drag) {
	    horizoff = (my_width - GaigsAV.preferred_width) / 2;
	    vertoff = (my_height - GaigsAV.preferred_height) / 2;
	}

        int x;
        
        list_of_snapshots.reset();
        x=0;
        LinkedList lt=new LinkedList();
        while (x<SnapAt && list_of_snapshots.hasMoreElements()){
            lt=(LinkedList)list_of_snapshots.nextElement();
            x++;
        }
        lt.reset();
        animation_done = true;
	//        System.out.println("before loop " + horizoff);
        while (lt.hasMoreElements()){
            obj tempObj=(obj)lt.nextElement();
            animation_done = animation_done && (tempObj.execute(g2 /*offscreen*/,zoom, vertoff, horizoff));
            //  System.out.println("in loop");
        }

        Shape mask = createMask(buff.getWidth(), buff.getHeight());
        
        g2.setColor(Color.BLACK);
        g2.fill(mask);

	my_image = buff;
	repaint();

        return animation_done;
        
    }

    //this is a call to allow the GaigsAV Visualizer to ask for an
    //animated step forward or backward in the show file -- forward
    //parameter is true if going forward, false if backward

    // Although it presently returns a boolean, that was only needed
    // during my aborted attempted at animated graphics primitives.
    // Until those become a reality the boolean value returned by this
    // routine is unnecessary
    public boolean animate(int sAt, boolean forward){

	int x;
	LinkedList lt = null;
        animation_done = true;	// May be re-set in paintComponent via indirect paintImmediately call at end

                                // Was used in aborted attempted to
                                // introduce animated primitives.  Now
                                // it's probably excess baggage that
                                // remains because I still have hopes
                                // of eventually having animated
                                // primitives


	if (getSize().width != 0 && getSize().height != 0) {
	    my_width = getSize().width;            // set dimensions
	    my_height = getSize().height;
	}
	else {
	    my_width = GaigsAV.preferred_width;            // set dimensions
	    my_height = GaigsAV.preferred_height;
	}

	// First capture the new image in a buffer called image2
        SnapAt=sAt;
        BufferedImage image2 = new BufferedImage(my_width, my_height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) image2.getGraphics(); // need a separate object each time?
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	g2.setColor(Color.WHITE);
	g2.fillRect(0,0,my_width,my_height);
	// Set horizoff and vertoff to properly center the visualization in the
	// viewing window. This is not quite perfect because visualizations
	// that are not properly centered within their [0,1] localized
	// coordinates will not be perfectly centered, but it is much better 
	// than it was previously.

	if (no_mouse_drag) {
	    horizoff = (my_width - GaigsAV.preferred_width) / 2;
	    vertoff = (my_height - GaigsAV.preferred_height) / 2;
	}

        list_of_snapshots.reset();
        x=0;
        lt=new LinkedList();
        while (x<SnapAt && list_of_snapshots.hasMoreElements()){
            lt=(LinkedList)list_of_snapshots.nextElement();
            x++;
        }
        lt.reset();
        animation_done = true;
	//        System.out.println("before loop " + horizoff);
        while (lt.hasMoreElements()){
            obj tempObj=(obj)lt.nextElement();
            animation_done = animation_done && (tempObj.execute(g2 /*offscreen*/,zoom, vertoff, horizoff));
            //  System.out.println("in loop");
        }

	// Next capture the image we are coming from in a buffer called image1
        SnapAt = (forward ? sAt - 1 : sAt +1);
        BufferedImage image1 = new BufferedImage(my_width, my_height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g1 = (Graphics2D) image1.getGraphics(); // need a separate object each time?
	g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	g1.setColor(Color.WHITE);
	g1.fillRect(0,0,my_width,my_height);
	// Set horizoff and vertoff to properly center the visualization in the
	// viewing window. This is not quite perfect because visualizations
	// that are not properly centered within their [0,1] localized
	// coordinates will not be perfectly centered, but it is much better 
	// than it was previously.

	if (no_mouse_drag) {
	    horizoff = (my_width - GaigsAV.preferred_width) / 2;
	    vertoff = (my_height - GaigsAV.preferred_height) / 2;
	}

        list_of_snapshots.reset();
        x=0;
        lt =new LinkedList();
        while (x<SnapAt && list_of_snapshots.hasMoreElements()){
            lt=(LinkedList)list_of_snapshots.nextElement();
            x++;
        }
        lt.reset();
        animation_done = true;
	//        System.out.println("before loop " + horizoff);
        while (lt.hasMoreElements()){
            obj tempObj=(obj)lt.nextElement();
            animation_done = animation_done && (tempObj.execute(g1 /*offscreen*/,zoom, vertoff, horizoff));
            //  System.out.println("in loop");
        }

	// Now slide from image1 to image2

	// From the gaff Visualizer by Chris Gaffney
	//        double step = 4;	// Adjust this for more/less granularity between images
        double step = 40;	// Adjust this for more/less granularity between images
        
        Image buffer = getGraphicsConfiguration().createCompatibleVolatileImage(my_width, my_height);
        Graphics2D g2d = (Graphics2D)buffer.getGraphics();
        
	AffineTransform trans = AffineTransform.getTranslateInstance( step * (forward ? -1 : 1), 0);
	//        AffineTransform orig = g2d.getTransform();
        
        Shape mask = createMask(my_width, my_height);
        
        for(double i = 0; i < my_width; i += step) {
	    if (i + step > my_width) // last time through loop, so adjust transform
		trans = AffineTransform.getTranslateInstance( ((double)(my_width - i)) * (forward ? -1 : 1), 0);
            g2d.transform(trans);
            g2d.drawImage(image1, 0, 0, this);
            g2d.setColor(Color.BLACK);
            g2d.fill(mask);
            
            AffineTransform last = g2d.getTransform();
            g2d.transform(AffineTransform.getTranslateInstance(my_width * (-1 * (forward ? -1 : 1)), 0));
            g2d.drawImage(image2, 0, 0, this);
            g2d.setColor(Color.BLACK);
            g2d.fill(mask);
            
            g2d.setTransform(last);
            
            this.my_image = buffer;
            repaint();
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                
            }
        }
        Image b = getGraphicsConfiguration().createCompatibleImage(my_width, my_height);
        b.getGraphics().drawImage(buffer, 0, 0, null);
        this.my_image = b;
        
	return animation_done;
    }
    
    // createericlist with a String signature is used to process a String
    // containing a sequence of GAIGS structures
    // createericlist creates the list of graphics primitives for these snapshot(s).
    // After creating these lists, that are then appended to l, the list of snapshots,
    // Also must return the number of snapshots loaded from the string
    public /*synchronized*/ int createericlist(String structString){
        int numsnaps = 0;
        if (debug) System.out.println("In create eric " + structString);
        StringTokenizer st = new StringTokenizer(structString, "\r\n");
        while ( st.hasMoreTokens() ) {
            numsnaps++; // ??
            String tempString;
            LinkedList tempList = new LinkedList();
            StructureType strct;
            tempString = st.nextToken();
            try {
                boolean headers = true;
                while (headers) {
                    headers = false;
                    if (tempString.toUpperCase().startsWith("VIEW")) {
                        tempString = HandleViewParams(tempString, tempList, st);
                        headers = true;
                    }
                    if (tempString.toUpperCase().startsWith("FIBQUESTION ")
                    || tempString.toUpperCase().startsWith("MCQUESTION ")
                    || tempString.toUpperCase().startsWith("MSQUESTION ")
                    || tempString.toUpperCase().startsWith("TFQUESTION ")) {
                        tempString = add_a_question(tempString, st);
                        headers = true;
                    }
                }
                
                if (tempString.toUpperCase().equals("STARTQUESTIONS")) {
                    numsnaps--; //questions don't count as snapshots
                    readQuestions(st);
                    break;
                }
                // After returning from HandleViewParams, tempString should now contain
                // the line with the structure type and possible additional text height info
                StringTokenizer structLine = new StringTokenizer(tempString, " \t");
                String structType = structLine.nextToken();
                if (debug) System.out.println("About to assign structure" + structType);
                strct = assignStructureType(structType);
                strct.loadTextHeights(structLine, tempList, this);
                strct.loadLinesPerNodeInfo(st, tempList, this);
                
                strct.loadTitle(st, tempList, this);
                strct.loadStructure(st, tempList, this);
                strct.calcDimsAndStartPts(tempList, this);
                strct.drawTitle(tempList, this);
                strct.drawStructure(tempList, this);
            }
            catch (VisualizerLoadException e) {
                System.out.println(e.toString());
            }
            //             // You've just created a snapshot.  Need to insure that "**" is appended
            //             // to the URLList for this snapshot IF no VIEW ALGO line was parsed in the
            //             // string for the snapshot.  This could probably best be done in the
            //             // HandleViewParams method
            list_of_snapshots.append(tempList);
            Snaps++;
        }
        return(numsnaps);
    } // createericlist(string)

    public int createericlist(Element snap) {
	int numsnaps = 0;

	StructureCollection structs = new StructureCollection();
	LinkedList tempList = new LinkedList();

	try {
	    load_snap_from_xml(snap, structs, tempList);
	    
	    structs.calcDimsAndStartPts(tempList,this);
	    structs.drawTitle(tempList, this);
	    structs.drawStructure(tempList, this);

	} catch (VisualizerLoadException e) {
	    System.out.println(e.toString());
	}

	list_of_snapshots.append(tempList);
	Snaps++;

	return numsnaps; // this.. isnt used, and isnt what it says it is.
    } // createericlist(element)

    private void load_snap_from_xml(Element snap, StructureCollection structs, LinkedList tempList) 
	          throws VisualizerLoadException {
	Iterator iterator = snap.getChildren().iterator();
	
	if( !iterator.hasNext() )
	    throw new VisualizerLoadException("Ran out of elements");
	
	structs.loadTitle( (Element) (iterator.next()) , tempList, this );
	structs.calcDimsAndStartPts(tempList, this);
	
	if( !iterator.hasNext() )
	    return;
	Element child = (Element) iterator.next();
	
	if( child.getName().compareTo("doc_url") == 0 ) {
	    // load the doc_url
	    add_a_documentation_URL( child.getText().trim() );
	    if( !iterator.hasNext() )
		return;
	    child = (Element) iterator.next();
	}
	
	if( child.getName().compareTo("pseudocode_url") == 0 ) {
	    // load the psuedocode_url
	    add_a_pseudocode_URL( child.getText().trim() );
	    if( !iterator.hasNext() )
		return;
	    child = (Element) iterator.next();
	}
	
	if( child.getName().compareTo("audio_text") == 0 ) {
	    // load the psuedocode_url
	    add_audio_text( child.getText().trim() );
	    if( !iterator.hasNext() )
		return;
	    child = (Element) iterator.next();
	}
	
	// create & load the individual structures, hooking them into the StructureCollection.
	// linespernode : calculate it at end of loadStructure call
	while( child.getName().compareTo("question_ref") != 0 ) {
	    StructureType child_struct = assignStructureType(child);
	    child_struct.loadStructure(child, tempList, this);
	    structs.addChild(child_struct);
	    
	    if( !iterator.hasNext() )
		return;
	    child = (Element) iterator.next();
	}
	
	if( child.getName().compareTo("question_ref") == 0 ) {
	    // set up question ref
	    add_a_question_xml( child.getAttributeValue("ref") );
	}
	else
	    // should never happen
	    throw new VisualizerLoadException("Expected question_ref element, but found " + child.getName());
	
	if( iterator.hasNext() ) {
	    child = (Element) iterator.next();
	    throw new VisualizerLoadException("Found " + child.getName() + " when expecting end of snap.");
	}
    } // load_snap_from_xml
    
    // See comment in createericlist regarding HandleViewParams
    private /*synchronized*/ String HandleViewParams(String tempString, LinkedList tempList, StringTokenizer st)
    throws VisualizerLoadException {
        while  (tempString.toUpperCase().startsWith("VIEW")) {
            StringTokenizer t = new StringTokenizer(tempString, " \t");
            String s1 = t.nextToken().toUpperCase();
            String s2 = t.nextToken().toUpperCase();
            String s3 = t.nextToken();
            
            // HERE PROCESS URL's AS YOU DID QUESTIONS
            
            if (s2.compareTo("ALGO") == 0 ) {
                add_a_pseudocode_URL(s3);
                // System.out.println("Adding to urls: "+Snaps+":"+s3);
                //                 urlList.append(Snaps+1 +":"+s3);
            }
            else if (s2.compareTo("DOCS") == 0 ) {
                add_a_documentation_URL(s3);
                // System.out.println("Adding to urls: "+Snaps+":"+s3);
                //                 if (debug) System.out.println("Adding to urlList: "+Snaps+1+":"+s3);
                //                 urlList.append(Snaps+1 +":"+s3);
            }
            else if (s2.compareTo("SCALE") == 0) {
                GKS.scale(Format.atof(s3.toUpperCase()), tempList, this);
            }
            else if (s2.compareTo("WINDOWS") == 0)  {
                GKS.windows(Format.atoi(s3.toUpperCase()), tempList, this);
            }
            else if (s2.compareTo("JUMP") == 0) {
                GKS.jump(Format.atoi(s3.toUpperCase()), tempList, this);
            }
            else
                throw (new VisualizerLoadException(s2 + " is invalid VIEW parameter"));
            tempString = st.nextToken();
        }
        //         if (urlList.size() == 0)
        //             urlList.append("**");
        return(tempString);
    }
    
    private /*synchronized*/ String add_a_question(String tmpStr, StringTokenizer st) throws VisualizerLoadException {
        try{
            StringTokenizer tokzer = new StringTokenizer(tmpStr);
            String idTok = tokzer.nextToken();
            idTok = tokzer.nextToken();             // what we really want is the id
            //             FIBQuestion q = new FIBQuestion(idTok);
            //             qTable.put(idTok, q);                   //add map id -> q
            GaigsAV.qCtlTable.put(new Integer(Snaps), idTok); //add map snap -> q (assumes <= 1 q/snap)
            if (debug) System.out.println("Adding question for snap " + Snaps);
            return st.nextToken();
        }
        catch (Exception e){
            throw new VisualizerLoadException("Aieee... bad SHO file");
        }
    }

    private void add_a_question_xml(String qRef) {
	GaigsAV.qCtlTable.put(new Integer(Snaps), qRef);
	if (debug) System.out.println("Adding question \"" + qRef + "\" for snap " + Snaps);
    }
    
    private /*synchronized*/ void add_a_pseudocode_URL(String URLname) throws VisualizerLoadException {
        try {
            URI codeURI = new URI(URLname);
            if(codeURI.getScheme() == null) {
                // Now we recreate it with the appended rel scheme
                codeURI = new URI("Rel:" + URLname);
            } 
            // If it does have a scheme definition then it's already set to go.

            GaigsAV.pseudoCtlTable.put(new Integer(Snaps), codeURI); //add map snap -> URI
            //System.out.println("Adding pseudocode URL for snap " + Snaps + " " + my_url.toString());
        }
        catch (Exception e) {
            throw new VisualizerLoadException("Aieee... bad SHO file");
        }
    }
    
    private /*synchronized*/ void add_a_documentation_URL(String URLname) throws VisualizerLoadException {
        try {
            URI infoURI = new URI(URLname);
            if(infoURI.getScheme() == null) {
                // Now we recreate it with the appended rel scheme
                infoURI = new URI("Rel:" + URLname);
            } 
            // If it does have a scheme definition then it's already set to go.

            GaigsAV.docCtlTable.put(new Integer(Snaps), infoURI); //add map snap -> URI
            //System.out.println("Adding documentation URL for snap " + Snaps + " " + my_url.toString());
        } catch (Exception e){
            throw new VisualizerLoadException("Aieee... bad SHO file");
        }
    }
    
    private /*synchronized*/ void add_audio_text(String the_text) throws VisualizerLoadException {
        try {
// 	    //	    System.out.println(the_text);
// 	    if (the_text.contains(".au") || the_text.contains(".wav")) { // we then assume it's an audio file
// 		URI audioURI = new URI(the_text);
// 		if(audioURI.getScheme() == null) {
// 		    // Now we recreate it with the appended rel scheme
// 		    audioURI = new URI("Rel:" + the_text);
// 		} 
// 		// If it does have a scheme definition then it's already set to go.
		
// 		GaigsAV.audioCtlTable.put(new Integer(Snaps), audioURI); //add map snap -> URI
// 		//System.out.println("Adding documentation URL for snap " + Snaps + " " + my_url.toString());
// 	    }
// 	    else {
	    GaigsAV.audioCtlTable.put(new Integer(Snaps), the_text); //add map snap -> the text to speak
		//System.out.println("Adding documentation URL for snap " + Snaps + " " + my_url.toString());
// 	    }
        } catch (Exception e){
            throw new VisualizerLoadException("Aieee... bad SHO file");
        }
    }
    
    public void createQuestions(Element questions) {
	try {
	    GaigsAV.questionCollection = QuestionFactory.questionCollectionFromXML( questions );
	} catch(QuestionParseException e) {
	    System.out.println("ERROR: could not parse the questions.");
	}
    } // createQuestions(element)
    
    private /*synchronized*/ void readQuestions(StringTokenizer st) throws VisualizerLoadException {
        String tmpStr = "STARTQUESTIONS\n"; // When CG's QuestionFactory parses from a string, it looks for
        // a line with STARTQUESTIONS -- hence we add it artificially here
        try {			// Build the string for the QuestionFactory
            while (st.hasMoreTokens()) {
                tmpStr += st.nextToken() + "\n";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new VisualizerLoadException("Ooof!  bad question format");
        }
        
        try {
            // System.out.println(tmpStr);
            // Problem -- must make this be line oriented
            GaigsAV.questionCollection = QuestionFactory.parseScript(tmpStr);
        } catch (QuestionParseException e) {
            e.printStackTrace();
            System.out.println("Error parsing questions.");
            throw new VisualizerLoadException("Ooof!  bad question format");
            //throw new IOException();
        }
        
    } // readQuestions(st)
    
    private /*synchronized*/ String insure_text_case_correct_for_legacy_scripts(String structType)
    throws VisualizerLoadException {
        
        if (debug) System.out.println("In insure_case_for_legacy_scripts with " + structType);
        if ( structType.toUpperCase().compareTo("DEMO_STR") == 0)
            return (new String("gaigs2.Demo_str"));
        else if ( structType.toUpperCase().compareTo("ANIMATED_DEMO_STR") == 0)
            return (new String("gaigs2.animated_demo_str"));
        else if ( structType.toUpperCase().compareTo("ALG_VIS") == 0)
            return (new String("gaigs2.Alg_vis"));
        else if ( structType.toUpperCase().compareTo("BAR_TEST") == 0)
            return (new String("gaigs2.bar_test"));
        else if ( structType.toUpperCase().compareTo("HEAP") == 0)
            return (new String("gaigs2.Heap"));
        // HeapStuff is an abstract type, so it can't be instantiated
        //         else if ( structType.toUpperCase().compareTo("HEAPSTUFF") == 0)
        //             return (new HeapStuff());
        else if ( structType.toUpperCase().compareTo("STACK") == 0)
            return (new String("gaigs2.Stack"));
        else if ( structType.toUpperCase().compareTo("QUEUE") == 0)
            return (new String("gaigs2.Queue"));
        else if ( structType.toUpperCase().compareTo("LINKEDLIST") == 0)
            return (new String("gaigs2.VisLinkedList"));
	else if ( structType.toUpperCase().compareTo("NO3DLINKEDLIST") == 0)
            return (new String("gaigs2.No3dLinkedList"));
	else if ( structType.toUpperCase().compareTo("LINKEDLISTNONULL") == 0)
            return (new String("gaigs2.VisLinkedListNoNull"));
        //         else if ( (structType.toUpperCase().compareTo("BAR") == 0)
        //                   || (structType.toUpperCase().compareTo("SCAT") == 0))
        //             return (new BarScat(structType.toUpperCase()));
        else if ( structType.toUpperCase().compareTo("MD_ARRAY") == 0)
            return (new String("gaigs2.MD_Array"));
        else if ( structType.toUpperCase().compareTo("NO3DARRAY") == 0)
            return (new String("gaigs2.No3darray"));
	else if ( structType.toUpperCase().compareTo("BINARYTREE") == 0)
            return (new String("gaigs2.BinaryTree"));
        else if ( structType.toUpperCase().compareTo("GENERALTREE") == 0)
            return (new String("gaigs2.GeneralTree"));
        //         else if ( (structType.toUpperCase().compareTo("GRAPH") == 0)
        //                   || (structType.toUpperCase().compareTo("NETWORK") == 0))
        //             return (new Graph_Network(structType.toUpperCase()));
        else if ( structType.toUpperCase().compareTo("KMP_ARRAY") == 0)
            return (new String("gaigs2.KMP_Array"));
        else
            return ("gaigs2."+structType); // Not a legacy structure
    }

    
    private StructureType assignStructureType(Element structure) throws VisualizerLoadException {
	String name = structure.getName().toLowerCase();
	if( name.compareTo("graph") == 0 ) {
	    if( structure.getAttributeValue("weighted").compareTo("true") == 0 )
		return new Graph_Network("NETWORK");
	    else
		return new Graph_Network("GRAPH");
	}
	else if( name.equals("array"))
	    return new MD_Array();
	else if( name.equals("no3darray"))
	    return new No3darray();
	else if( name.equals("linkedlist"))
	   return new VisLinkedList();
	else if( name.equals("no3dlinkedlist"))
	   return new No3dLinkedList();
	else if( name.equals("linkedlistnonull"))
	   return new VisLinkedListNoNull();
	else if( name.equals("bargraph"))
	    return new BarScat("BAR");
	else if( name.equals("scattergraph"))
	    return new BarScat("SCAT"); // not implemented in xml dtd, and does not have a load-from-xml method defined
	else if( name.equals("stack"))
	    return new Stack();
	else if( name.equals("queue"))
	    return new Queue();
	else if( name.equals("tree")) {
	    if( structure.getChild("binary_node") != null )
		return new BinaryTree();
	    else
		return new GeneralTree();
	}
	else if( name.equals("text"))
	    return new TextStructure();
	// if the XML element name is different from your structure's name, you can do something like this:
// 	else if( name.equalsIgnoreCase("node"))
// 	    return new Node();
	else if( name.equals("legend"))
	    return new LegendofColors();
	else {
	    // try dynamic typing
	    try {
		return assignStructureType( name );
	    } catch(Exception e) {
		throw new VisualizerLoadException("Unable to instantiate class \"" + name + "\":\n" +
						  e.getMessage() );
	    }
	}
    }
    
    
    private /*synchronized*/ StructureType assignStructureType(String structType)
    throws VisualizerLoadException {
        
        if (debug) System.out.println("In assignStructureType with " + structType);
        
        // Handle objects whose constructors require args separately
        if ( (structType.toUpperCase().compareTo("BAR") == 0)
        || (structType.toUpperCase().compareTo("SCAT") == 0))
            return (new BarScat(structType.toUpperCase()));
        else if ( (structType.toUpperCase().compareTo("GRAPH") == 0)
        || (structType.toUpperCase().compareTo("NETWORK") == 0))
            return (new Graph_Network(structType.toUpperCase()));
        else {			// Constructor for object does not require args
            
            try {
                return ( (StructureType) ( (Class.forName(insure_text_case_correct_for_legacy_scripts(structType))).newInstance() ) );
            } catch (InstantiationException e) {
                System.out.println(e);
                throw (new VisualizerLoadException(structType + " is invalid structure type"));
            } catch (IllegalAccessException e) {
                System.out.println(e);
                throw (new VisualizerLoadException(structType + " is invalid structure type"));
            } catch (ClassNotFoundException e) {
                System.out.println(e);
                throw (new VisualizerLoadException(structType + " is invalid structure type"));
            }
        }
    }
    
    
    
    //creates the list of snapshots
    public /*synchronized*/ obj dothis(String inputString) {
        /* Snapshot codes
         
        29 - rectangle draw
        2 - oval draw
        5 - fill oval
        6 - string
        7 - line & text color
        8 - fill color
        9 - text color (possible to ignore)
        10 - text height
        11 - polydraw
        4 - fill poly
        14 - arc draw
        30 - fill arc
        12 - text style, centered horizontal/vertical
        ???? 45 - url.  Note, when bring up multiple algorithms, the URL's for the most recently
        run algorithm are posted in the upper browser frame
        THE CODE BELOW WOULD INDICATE THIS IS 54, NOT 45 ????
         
        20 - number of windows.  For static algorithms, 1, 2, 3, 4 have the obvious meaning.
        21 - scale factor
        22 - jump factor
         
        For 20, 21, 22, the last factor loaded is the one that will affect all snapshots in
        the show
         
         */
        
        obj temp=new rectDraw("0 0 0 0",lineC);
        Object urlTest;
        String arrg;
        
        
        
        int graphic_obj_code = Format.atoi(inputString.substring(0,3));
        StringTokenizer tmp = null;
        
        switch (graphic_obj_code) {
            
            case 29:
                temp= new rectDraw(inputString.substring(3,(inputString.length())),lineC);
                break;
            case 2:
                temp= new ovalDraw(inputString.substring(3,(inputString.length())),lineC);
                break;
            case 5:
                temp= new fillOvalDraw(inputString.substring(3,(inputString.length())),fillC);
                break;
            case 6:
                temp= new stringDraw(inputString.substring(3,(inputString.length())),
                textC,LineH,LineV,fontMult);
// 		System.out.println(" printing " + inputString);
                break;
            case 7:
                lineC=colorSet(inputString.substring(3,(inputString.length())));
                textC=lineC;
                break;
            case 8:
                tmp=new StringTokenizer(inputString.substring(2,
                (inputString.length())));
                fillC=colorSet(tmp.nextToken());
                break;
            case 9:
                textC=colorSet(inputString.substring(3,(inputString.length())));
                break;
            case 10:
                StringTokenizer st= new StringTokenizer(inputString.substring(3,
                (inputString.length())));
                fontMult=Format.atof(st.nextToken());
// 		System.out.println("setting fontMult= " + fontMult);
                break;
                // TLN changed on 10/14/97 to accomodate condensed prm files
                // temp=new textHeight(inputString.substring(3,(inputString.length())));
            case 11:
                temp=new polyDraw(inputString.substring(3,(inputString.length())),lineC);
                break;
            case 4:
                temp=new fillPolyDraw(inputString.substring(3,(inputString.length())),fillC);
                break;
            case 64:
                temp=new animated_fillPolyDraw(inputString.substring(3,(inputString.length())),fillC);
                break;
            case 14:
                temp=new arcDraw(inputString.substring(3,(inputString.length())),lineC);
                break;
            case 30:
                temp=new fillArcDraw(inputString.substring(3,(inputString.length())),fillC);
                break;
            case 12:
                tmp=new StringTokenizer(inputString.substring(3,
                (inputString.length())));
                LineH=Format.atoi(tmp.nextToken());
                LineV=Format.atoi(tmp.nextToken());
                break;
            case 20:
                tmp=new StringTokenizer(inputString.substring(3,
                (inputString.length())));
                //graphWin.setNumViews(Format.atoi(tmp.nextToken()));
                //multiTrigger=true;
                break;
            case 21:
                tmp=new StringTokenizer(inputString.substring(3,
                (inputString.length())));
                double tempFloat=Format.atof(tmp.nextToken());
                zoom=tempFloat;
                break;
            case 22:
                tmp=new StringTokenizer(inputString.substring(3,
                (inputString.length())));
                //            graphWin.setJump(Format.atoi(tmp.nextToken()));  // This is now a noop in gaigs2
                break;
            case 54:
                tmp=new StringTokenizer(inputString.substring(3,
                (inputString.length())));
                //             if (tmp.hasMoreElements()){
                //                 urlTest=tmp.nextToken();
                //                 urlList.append(urlTest);
                //             }
                //             else{
                //                 tmp=new StringTokenizer("**");
                //                 urlTest=tmp.nextToken();
                //                 urlList.append(urlTest);
                //             }
                break;
                
        } // end switch
        
        return (temp);
    }
    
    /*
     * Color Chart
     * ---------------
     *   0:black
     *   1:blue
     *   2:cyan
     *   3:dark gray
     *   4:gray
     *   5:green
     *   6:light gray
     *   7:magenta
     *   8:orange
     *   9:pink
     *   10:red
     *   11:white
     *   12:yellow
     *   negative -- it was hex specified color
     */
    
    public /*synchronized*/ Color colorSet(String values){
        String temp;
        int x;
        Color c=Color.black;
        
        StringTokenizer st= new StringTokenizer(values);
        x=Format.atoi(st.nextToken());
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



