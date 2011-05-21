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
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import java.io.*;
//import com.sun.java.swing.*;
import javax.swing.*;

import org.jdom.*;

import jhave.core.*;
import jhave.question.*;
import jhave.Algorithm;

// 
// import jhave.Question;
// import jhave.infoFrame;

// Some of the code here is now unnecessarily complicated.  Why?  In
// Version 1 of JHAVE, we the capability to draw anywhere from 1 to 4
// snapshots at once in the rendering pane.  When we went to the
// "filmstrip motif" in version 2, we decided that, with better
// stepping between snapshots, we would never present more than one
// snapshot at a time in the rendering pane.  Consequently some of the
// code employs an array of size four, for which we now only use index
// 0.  You say I should have removed this extraneous baggage?  Agreed!
// It will happen some day when I'm bored.

public class Graphprim extends JComponent implements /* WindowListener,*/ 
                                                MouseListener,
                                                MouseMotionListener,
                                                ComponentListener {

    String kind;
    draw viewWin[];
    public final static boolean debug = GaigsAV.debug;
    private LinkedList ll=new LinkedList();// The linked list of strings read from
                                           // the sequence of graphic primitives
    private int prevNumViews=1;	// the last numViews before numViews was changed.
    private int numViews=1;     // number of slides to show at a time (1-4) 
    private int jump=1;         // jump factor (by how many slides do we increment in each frame?)
    //    private Question questionPanel=null;
    //  private Question lastQuestion=null;
    //  public Question lastShownQuestion=null;

    //FOOBAR made this public so it could be accessed in GaigsAV at end of runScript
    public boolean readyToPaint = false;

    private GaigsAV my_parent_visualizer;
			
    public int workingWindow=0; // current window that is being manipulated
    int originalH;              //the original horizontal size of the whole window
    int originalW;              //the original vertal size of the whole window
    int lastHorSize;            //the last horizontal size of the window
    int lastVerSize;            //the last verticale size of the window

    private boolean mouseDown = false; // Keeps track of whether the mouse
                                       // button is being held down so that the
                                       // mouse cursor can be properly set when
                                       // the mouse enters the viewing window.


    //allows for the change of the number of windows
    public void setNumViews(int n){
        if (debug) System.out.println("Changing number of views from " + numViews + " to " + n);
	prevNumViews=numViews;
	numViews=n;
    }

    //returns the number of windows
    public int getNumViews(){
	return numViews;
    }

    //copies one window (aka. SHO structure) to another
    public void copyWindow(int origWin,int newWin){
	remove(viewWin[newWin]);
	viewWin[newWin]=new draw(viewWin[origWin]);
	//		getContentPane().add(viewWin[newWin]);
	viewWin[newWin].setFont(new Font("Serif",Font.BOLD,300));
    }


    //resets the applet for a clean slate (unused) and untrusted
    public void ReInitProg(){
        viewWin[workingWindow].ReInitProg();
    }

    // Constructors often tend to be very useful things to have.  Normally they go towards the
    // top of the class definition.  Who ever said we were normal?
    public Graphprim(GaigsAV my_vis) {
        super();
        my_parent_visualizer = my_vis;
        setLayout(null);			    //let me handle the layout //FIXME?
        addNotify();	
	//        addWindowListener(this);        //register self to receive window events


	// Why do these generate a null pointer error?
//         addMouseListener(this);         //register self to receive mouse events
//         addMouseMotionListener(this);   //register self to receive mouse motion events
        // addComponentListener(this);     //register self to receive window resize events

	setPreferredSize(new Dimension(GaigsAV.preferred_width,GaigsAV.preferred_height));
        setBackground(new Color(1.0f, 1.0f, 1.0f));    //ensure white bg
               
        // The sum of what is added to top above plus the height in the last argument
        // must match the constant maxsize, which is set to 480 below.  This seems to mean
        // that we're plotting in a 480 X 480 DC space
        viewWin=new draw[4];            //the SHO file structures with Linked Lists of Primatives
	viewWin[0]= new draw(this);     //intialize the base Draw
	add(viewWin[0]);	            //add the base Draw to the graphics window
        viewWin[0].setFont(new Font("Serif",Font.BOLD,300)); //set it to a scalable font
       	viewWin[0].setBounds(getInsets().left,getInsets().top,  //set up its size to fit the window
			     getSize().width-getInsets().left-getInsets().right,
			     getSize().height-getInsets().top-getInsets().bottom);
	originalH=getSize().height;		//intialize the heights and widths
	originalW=getSize().width;
	lastHorSize=getSize().height;
	lastVerSize=getSize().width;

	// FOOBAR -- commented this out and instead set it at end of readScript
	readyToPaint = true;
    }

    // creates snapshot(s) from a string containing GAIGS
    // data structure reps.  Adds it to the linked list of snapshots
    public int createSnap(String structs){
	if (debug) 
	    System.out.println("About to create eric list with " + structs.length() + "\n" + structs);
        return(viewWin[workingWindow].createericlist(structs));
    }

    public int createSnap(Element snap) {
	if (debug)
	    System.out.println("About to create graphprimlist using a snap element");
	return(viewWin[workingWindow].createericlist(snap));
    }

    public void createQuestions(Element questions) {
	if(debug)
	    System.out.println("About to create questions using a questions element");
	//return(viewWin[workingWindow].createericlist( questions.getText().trim() ) );
	viewWin[workingWindow].createQuestions( questions );
    }

    //this allows this class to recieve an Applet Context from the main applet
    public void giveContext(AppletContext viewerIs){
        viewWin[workingWindow].changeContext(viewerIs);
    }

    /* This next set of handlers satisfies the ComponentListener interface.  The only event
     * we really want to react to is the resize event so we can reset the GKS drawing coords.
     */

    public void componentHidden     (ComponentEvent e)  { }
    public void componentMoved      (ComponentEvent e)  { }
    public void componentShown      (ComponentEvent e)  { }

    public void componentResized(ComponentEvent e)
    {
        if (getNumViews() == 1)
	    {
		obj.maxsize = getSize().width - getInsets().left - getInsets().right;
		StructureType.maxsize = getSize().width - getInsets().left - getInsets().right;
	    }
// 	else if (getNumViews() == 3) {
// 	    obj.maxsize = (getSize().width - getInsets().left - getInsets().right) / 3;
// 	    StructureType.maxsize = (getSize().width - getInsets().left - getInsets().right) / 3;
// 	}
// 	else {
// 	    obj.maxsize = (getSize().width - getInsets().left - getInsets().right) / 2;
// 	    StructureType.maxsize = (getSize().width - getInsets().left - getInsets().right) / 2;
// 	}

        if (debug) System.out.println("Resized to " + obj.maxsize);
    }


    /* null methods to satisfy MouseListener and MouseMotionListener interfaces.
     * We don't really need to do anything special with these events.
     */
    public void mouseMoved      (MouseEvent e)  {  }
    public void mouseEntered    (MouseEvent e)  { 
	if(mouseDown){
	    Cursor dragCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	    setCursor(dragCursor);
	}else{
	    Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
	    setCursor(moveCursor);
	}
    }
    public void mouseExited     (MouseEvent e)  { 
	Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	setCursor(defaultCursor);
    }    
    public void mouseReleased   (MouseEvent e)  {  }
    
    /* Pass mouseDown events along to each Canvas */
    public void mousePressed    (MouseEvent e)  { 
	mouseDown = true;
	// Use crosshair for dragging because Java does not have a built-in
	// cursor for a hand that is "grabbed onto" the viewing window.
	Cursor dragCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	setCursor(dragCursor);
        if (debug) System.out.println("mousePressed");
        for (int count=0;count<numViews;count++) viewWin[count].mDown(e.getX(),e.getY()); 
    }
    
    /* Pass the mouseUp from the end of a drag operation to the draw areas */
    public void mouseUp(MouseEvent e, boolean dragdone)
    {
	mouseDown = false;
	Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
	setCursor(moveCursor);
        if (dragdone)
            for (int count = 0; count < numViews; count++)
                viewWin[count].mDrag(e.getX(), e.getY());
    }

    /* Check for double-clicks */
    public void mouseClicked(MouseEvent e)
    {   
        if(e.getClickCount() == 2)
	    {
		if (debug) System.out.println("double-clicked");
		for (int count=0;count<numViews;count++) 
		    {
			viewWin[count].vertoff=0;
			viewWin[count].horizoff=0;
			if(viewWin[count] != null)
			    viewWin[count].execute(my_parent_visualizer.snapAt[count]);
// 			    viewWin[count].repaint();
		    }
	    }
    }

    public void mouseDragged(MouseEvent e)
    {
        for (int count=0;count<numViews;count++) viewWin[count].mDrag(e.getX(),e.getY());
    }

    //this is a call to allow the GaigsAV Visualizer to ask for a redraw or
    //for a new snapshot to be drawn.
    public void execute(int snapAt[]){
	
	boolean done;
	
        if(debug) 
            System.out.println("Graphwin executing");
	//        questionPanel = null;
	//setVisible(true);


	// The following loop allows animation of individual
	// primitives in the view windows -- an aborted attempt that
	// may someday be revived

        // for (int foo = 0; foo < 50; foo++) {

	do {
	    
	    done = true; 
	    for (int count=0;count<numViews;count++){       
		if (debug) System.out.println("Snap At for"+count+"="+snapAt[count]);
		done = done && (viewWin[count].execute(snapAt[count]));
	    }

 	    if (!done) try { Thread.sleep(GaigsAV.animation_frame_interval); } catch (InterruptedException e) { e.printStackTrace(); }
 	} while (!done);

	//repaint();    // I think this last repaint is extra and unnecessary
    }

    //this is a call to allow the GaigsAV Visualizer to ask for an
    //animated step forward or backward in the show file -- forward
    //parameter is true if going forward, false if backward
    public void animate(int snapAt[], boolean forward){
	
	boolean done;
	
        if(debug) 
            System.out.println("Graphwin animating");
	//        questionPanel = null;
	//setVisible(true);


	// The following loop allows animation of individual
	// primitives in the view windows -- an aborted attempt that
	// may someday be revived

        // for (int foo = 0; foo < 50; foo++) {

	do {
	    
	    done = true; 
	    for (int count=0;count<numViews;count++){       
		if (debug) System.out.println("Snap At for"+count+"="+snapAt[count]);
		done = done && (viewWin[count].animate(snapAt[count],forward));
	    }

 	    if (!done) try { Thread.sleep(GaigsAV.animation_frame_interval); } catch (InterruptedException e) { e.printStackTrace(); }
 	} while (!done);

	//repaint();    // I think this last repaint is extra and unnecessary
    }

    // No longer a scale bar -- just responding to the Visualizer's request fora zoom event
    public void clickedScaleBar(int barValue) {
	double amount = ((double)barValue)/((double)100);
        for (int count=0;count<numViews;count++){
	    viewWin[count].zoom=amount;
	    if(viewWin[count] != null)
		viewWin[count].execute(my_parent_visualizer.snapAt[count]);
// 		viewWin[count].repaint();
	}
    }


    //this paints the current Graphics window with up to Multi Different windows
    //There has to be a better way to do this.  there has to.  Maybe at some point
    //someone less lazy than me will rewrite it.
    public void paint(Graphics g){
	super.paint(g);
	if (readyToPaint) {
	    //	 	    System.out.println("In GraphPrim's paint");
	    int county;
	    county=0;
	    if (numViews==1)
		viewWin[0].setBounds(getInsets().left,getInsets().top,
				     getSize().width-getInsets().left-getInsets().right,
				     getSize().height-getInsets().top-getInsets().bottom);
	    else if (numViews==2){
		viewWin[0].setBounds(getInsets().left,getInsets().top,
				     (int)((getSize().width-getInsets().left-getInsets().right)/2),
				     (int)((getSize().height-getInsets().top-getInsets().bottom)));
		viewWin[1].setBounds(getInsets().left+
				     (getSize().width-getInsets().left-getInsets().right)/2,
				     getInsets().top,
				     (int)((getSize().width-getInsets().left-getInsets().right)/2),
				     (int)((getSize().height-getInsets().top-getInsets().bottom)));
	    }
	    else if (numViews==3){
		viewWin[0].setBounds(getInsets().left,getInsets().top,
				     (int)((getSize().width-getInsets().left-getInsets().right)/3),
				     (int)((getSize().height-getInsets().top-getInsets().bottom)));
		viewWin[1].setBounds(getInsets().left+(getSize().width-getInsets().left-getInsets().right)/3,getInsets().top,
				     (int)((getSize().width-getInsets().left-getInsets().right)/3),
				     (int)((getSize().height-getInsets().top-getInsets().bottom)));
		viewWin[2].setBounds(getInsets().left+(getSize().width-getInsets().left-getInsets().right)*2/3,getInsets().top,
				     (int)((getSize().width-getInsets().left-getInsets().right)/3),
				     (int)((getSize().height-getInsets().top-getInsets().bottom)));
	    }
	    else if (numViews==4){
		viewWin[0].setBounds(getInsets().left,getInsets().top,
				     (int)((getSize().width-getInsets().left-getInsets().right)/2),
				     (int)((getSize().height-getInsets().top-getInsets().bottom)/2));
		for (int countx=1;countx<4;countx++){
		    viewWin[countx].setBounds(getInsets().left+countx%2*(getSize().width-getInsets().left-getInsets().right)/2,
					      getInsets().top+county%2*(getSize().height-getInsets().top-getInsets().bottom)/2,
					      (int)((getSize().width-getInsets().left-getInsets().right)/2),
					      (int)((getSize().height-getInsets().top-getInsets().bottom)/2));
		    county=county+countx%2;
		}
	    }
	    for (int x=0;x<numViews;x++)
		if(viewWin[x] != null)
		    viewWin[x].execute(my_parent_visualizer.snapAt[x]);
// 		    viewWin[x].repaint();
	}
    }
}

