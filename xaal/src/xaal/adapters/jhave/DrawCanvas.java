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

package xaal.adapters.jhave;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JPanel;

import xaal.adapters.jhave.objects.BaseObject;
import xaal.adapters.jhave.objects.OvalDraw;
import xaal.adapters.jhave.objects.PolyDraw;
import xaal.adapters.jhave.objects.RectDraw;
import xaal.adapters.jhave.objects.TextDraw;
import xaal.adapters.jhave.objects.ParabolaDraw;
import xaal.objects.graphical.Coordinate;
import xaal.objects.graphical.Ellipse;
import xaal.objects.graphical.GraphicalPrimitive;
import xaal.objects.graphical.Line;
import xaal.objects.graphical.Polyline;
import xaal.objects.graphical.Parabola;
import xaal.objects.graphical.Rectangle;
import xaal.objects.graphical.Parabola;
import xaal.objects.graphical.Style;
//import xaal.objects.graphical.Tex

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import xaal.adapters.jhave.objects.BaseObject;
import xaal.adapters.jhave.objects.OvalDraw;
import xaal.adapters.jhave.objects.PolyDraw;
import xaal.adapters.jhave.objects.RectDraw;
import xaal.adapters.jhave.objects.TextDraw;
import xaal.objects.graphical.Coordinate;
import xaal.objects.graphical.Ellipse;
import xaal.objects.graphical.GraphicalPrimitive;
import xaal.objects.graphical.Line;
import xaal.objects.graphical.Polygon;
import xaal.objects.graphical.Rectangle;
import xaal.objects.graphical.Style;
import xaal.objects.graphical.Text;

import xaal.objects.animation.ParOperation;
import xaal.objects.animation.GraphicalOperation;
import xaal.objects.animation.graphical.MoveOperation;
import xaal.objects.animation.graphical.ChangeStyleOperation;
import xaal.objects.animation.graphical.ShowOperation;
import xaal.objects.animation.graphical.HideOperation;
import xaal.objects.animation.timing.Timing;

import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;


//this is a JPanel that will make a linked list of snapshots from a linked list of Primitives
//it also stores them and draws them appropriately
public class DrawCanvas extends JPanel implements MouseListener, MouseMotionListener {
    //private static int statici = 0;
    public final static boolean debug = XaalAV.debug;
    public static double zoom = 1.0;        //zoom factor
    public int vertoff= 10;        //vertical offset for scrolling
    public int horizoff= 60;      //horizontal offset for scrolling

    //    private int snapAt=1;                           //current snapshot
    private int snapAt = 0;

    private LinkedList snapshotList =new LinkedList();          // The list of snapshots

    private Graphprim graphWin;                     //mommy (our parent)

    private int startX,startY;
    private boolean dragging = false;
    private Image my_image = null;
    private int my_width, my_height;


    // The LineBreakMeasurer used to line-break the paragraph.
    private LineBreakMeasurer lineMeasurer;

    // index of the first character in the paragraph.
    private int paragraphStart;

    // index of the first character after the end of the paragraph.
    private int paragraphEnd;

    private static final 
        Hashtable<TextAttribute, Object> map =
           new Hashtable<TextAttribute, Object>();

    static {
        map.put(TextAttribute.FAMILY, "Serif");
        map.put(TextAttribute.WEIGHT, new Float(2.0));
        map.put(TextAttribute.SIZE, new Float(24.0));
    }  

    



    // These two new boolean variables are used to center the visualization in
    // the window when it starts. Note that the visualization will only appear
    // centered in the window if the structures are properly drawn in the
    // center of their normalized [0,1] coordinates - A.J., 6-27-06.
   // private boolean no_mouse_drag = true;  // True until the viewing window
    // has been dragged by the user;
    // false afterward. Used to properly
    // set the initial values of vertoff
    // and horizoff to center the
    // visualization when it starts.

    public DrawCanvas(Graphprim gp) {
        graphWin = gp;
        setBackground(new Color(1.0f, 1.0f, 1.0f));    //ensure white bg
        my_width = getSize().width;
        my_height = getSize().height;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /** Resets the DrawCanvas to the original state */
    public void ReInitProg(){
        snapshotList=new LinkedList();
        //snapAt=1;
	snapAt = 0;
        zoom=1;
    }

    //draws the current snapshot
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        my_width = getSize().width;
        my_height = getSize().height;
        if (my_image != null)
	    {
		g.drawImage(my_image, 0, 0, my_width, my_height, this);
	    }
        setBackground(new Color(1.0f, 1.0f, 1.0f));
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
            execute(snapAt,XaalAV.NO_ANIMATION);
//          repaint();
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
    public /*synchronized*/ void execute(int sAt,int animation) {

        snapAt = sAt;

	//System.out.println( "in execute: snapAt = " + snapAt);

        if (getSize().width != 0 && getSize().height != 0) {
	    my_width = getSize().width;  
	    my_height = getSize().height; 
	}
        else { 
	    my_width = XaalAV.preferred_width; 
	    my_height = XaalAV.preferred_height;
        }
        BufferedImage buff = new BufferedImage(my_width, my_height, 
					       BufferedImage.TYPE_INT_RGB);
	// need a separate object each time?
        Graphics2D g2 = (Graphics2D) buff.getGraphics(); 
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			    RenderingHints.VALUE_ANTIALIAS_ON);

	g2.setColor(Color.WHITE);
	g2.fillRect(0,0,my_width,my_height);
	
	// List l = (List) snapshotList.get(snapAt-1);
	List baseObjects = (List) snapshotList.get(snapAt);
	Iterator iter;
	ArrayList parOps = null;

	switch (animation) 
	{
	    
        /*********************************************************************/
	case XaalAV.NO_ANIMATION:
        /*********************************************************************/

	    if (snapAt==0)

		displayNarrative(
		     "Solving a quadratic equation by \"completing the square\"",
		     g2 );

	    else
		displayNarrative(
		     graphWin.getParentVisualizer().snapshots[snapAt-1].narrative,
		     g2 );

	    //System.out.println( " NO_ANIMATION");
	    iter = baseObjects.iterator();
	    while (iter.hasNext()){
		BaseObject tempObj=(BaseObject)iter.next();
		tempObj.execute(g2, zoom, vertoff, horizoff);
	    }
	    my_image = buff;
	    repaint();
	    break;

        /*********************************************************************/
	case XaalAV.FORWARD:
        /*********************************************************************/

	    parOps = graphWin.getParentVisualizer().snapshots[snapAt-1].forward;

	    baseObjects = cloneBaseObjects( (List) snapshotList.get(snapAt-1) );

	    /* loop over the parallel ops for this snapshot */
	    for(int parOpIndex=0; parOpIndex<parOps.size(); parOpIndex++)
	    {
		ParOperation parOp = (ParOperation) parOps.get(parOpIndex);
		int ms;
		if ( (ms = checkForWaitOperation(parOp)) > 0)
		{  
		    if (snapAt==0)
			displayNarrative(
					 "Solving a quadratic equation by \"completing the square\"",
					 g2 );

		    else
			displayNarrative(
				     graphWin.getParentVisualizer().snapshots[snapAt-1].narrative,
				     g2 );
		    iter = baseObjects.iterator();
		    while (iter.hasNext()){
			BaseObject tempObj=(BaseObject)iter.next();
			tempObj.execute(g2, zoom, vertoff, horizoff);
		    }
		    my_image = buff;
		    repaint();

		    try { Thread.sleep(ms); } catch (Exception e) {};
		}
		else if (checkForInstantaneousOperation(parOp))
		{
		    executeInstantaneousOperations( parOp, g2, baseObjects );
		    my_image = buff;
		    repaint();		
		}
		else
		{
		    executeTranslationOperations( parOp, g2, baseObjects, buff );
		}
	    }// for loop on ParOperations

	    break;  // end of FORWARD animation


        /*********************************************************************/
	case XaalAV.BACKWARD:
        /*********************************************************************/

	    parOps = graphWin.getParentVisualizer().snapshots[snapAt].backward;

	    baseObjects = cloneBaseObjects( (List) snapshotList.get(snapAt+1) );

	    /* loop over the parallel ops for this snapshot */
	    for(int parOpIndex=0; parOpIndex<parOps.size(); parOpIndex++)
	    {
		ParOperation parOp = (ParOperation) parOps.get(parOpIndex);
		int ms;
		if ( (ms = checkForWaitOperation(parOp)) > 0)
		{  
		    try { Thread.sleep(ms); } catch (Exception e) {};
		}
		else if (checkForInstantaneousOperation(parOp))
		{
		    executeInstantaneousOperations( parOp, g2, baseObjects );
		    my_image = buff;
		    repaint();		
		}
		else
		{
		    executeTranslationOperations( parOp, g2, baseObjects, buff );
		}
	    }// for loop on ParOperations

	    break;  // end of FORWARD animation

	}// switch on type of animation

       /* example of rotation

        tempObj.execute(g2, zoom, 
	                 vertoff-(int)(Math.sqrt((6400-(80-step)*(80-step)))), 
			 horizoff+step*3);
	*/
    }
    
    /* a Wait Operation is defined as a parOp containing a single
       move operation of type "move" whose X value is the delay in ms 
      
       if op is a Wait Operation, then return the delay (at least 1 ms)
       else return -1
    */
    private int checkForWaitOperation( ParOperation op )
    {
	if (op.getNumOperations() == 1)
	{
	    GraphicalOperation gop =  
		(GraphicalOperation) op.getOperationsList().get(0);

	    if ( gop instanceof MoveOperation)
	    {
		MoveOperation mop = (MoveOperation) gop;
		if (mop.getType().equals("move")) 
		    return Math.max(1,mop.getNewCoordinate().getX());
	    }
	}
	return -1;
    }// checkForWaitOperation method


    /* returns true if the ParOperation has no duration, that is, if
       it is a combination of ChangeStyle, Show, or Hide operations

       the assumption is that, in a script, a ParOperation cannot
       contain both instantaneous and move operations; this is why
       only the first operation is checked

    */
    private boolean checkForInstantaneousOperation( ParOperation op )
    {
	if (op.getNumOperations() > 0)
	{
	    GraphicalOperation gop =  
		(GraphicalOperation) op.getOperationsList().get(0);

	    return ( (gop instanceof ChangeStyleOperation) ||
		     (gop instanceof HideOperation) ||
		     (gop instanceof ShowOperation) );
	}
	else return false;
    }// checkForInstantaneousOperation method

    public void executeInstantaneousOperations( ParOperation parOp,
						Graphics2D g2,
						List baseObjects)
    {
	Hashtable style_changed = new Hashtable(20);
	HashSet hidden = new HashSet(20);
	HashSet shown = new HashSet(20);
	Iterator ops = parOp.getOperations();
	while (ops.hasNext())
	{
	    GraphicalOperation op = (GraphicalOperation) ops.next();

	    if (op instanceof ChangeStyleOperation)
	    {
		ChangeStyleOperation o = (ChangeStyleOperation) op;
		ArrayList gps = (ArrayList) o.getGraphicalPrimitives();
		for(int i=0; i<gps.size(); i++)
		{
		    GraphicalPrimitive gp = (GraphicalPrimitive)gps.get(i);
		    style_changed.put(gp.getId(),o.getStyle().getColor());
		}	
	    }// ChangeStyleOperation
	    else if (op instanceof ShowOperation)
	    {
		// must come before HideOperation since 
		// ShowOperation inherits from HideOperation
		ShowOperation o = (ShowOperation) op;
		ArrayList gps = (ArrayList) o.getGraphicalPrimitives();
		for(int i=0; i<gps.size(); i++)
		{
		    GraphicalPrimitive gp = (GraphicalPrimitive)gps.get(i);
		    shown.add(gp.getId());
		}	
	    }// ShowOperation
	    else if (op instanceof HideOperation)
	    {
		HideOperation o = (HideOperation) op;
		ArrayList gps = (ArrayList) o.getGraphicalPrimitives();
		for(int i=0; i<gps.size(); i++)
		{
		    GraphicalPrimitive gp = (GraphicalPrimitive)gps.get(i);
		    hidden.add(gp.getId());
		}
	    }// HideOperation	
	}

	g2.setColor(Color.WHITE);
	g2.fillRect(0,0,my_width,my_height);

	if (snapAt==0)
	    displayNarrative(
   	        "Solving a quadratic equation by \"completing the square\"",
		g2 );
	else
	    displayNarrative(
		graphWin.getParentVisualizer().snapshots[snapAt-1].narrative,
		g2 );

	Iterator iter = baseObjects.iterator();
	while (iter.hasNext())
	{
	    BaseObject obj=(BaseObject)iter.next();

	    /* adjust color if necessary */
	    Color newColor = (Color) style_changed.get( obj.getId() );
	    if (newColor != null) obj.setColor( newColor );

	    /* adjust visibility if necessary */
	    if (hidden.contains( obj.getId() ))
		obj.setInvisible();
	    else if (shown.contains( obj.getId() ))
		obj.setVisible();

	    obj.execute(g2, zoom, vertoff, horizoff);
	}
    }// executeInstantaneousOperations method

    private void displayNarrative(String text, Graphics2D g )
    {
	/*
	g.setColor(Color.RED);
	    
        map.put(TextAttribute.SIZE, new Float(24.0*zoom));

	AttributedString title = new AttributedString( text, map);

	AttributedCharacterIterator paragraph = title.getIterator();
	paragraphStart = paragraph.getBeginIndex();
	paragraphEnd = paragraph.getEndIndex();
	FontRenderContext frc = g.getFontRenderContext();
	lineMeasurer = new LineBreakMeasurer(paragraph, frc);

        // Set break width to width of Component.
        float breakWidth = (float) (my_width - 20);
        float drawPosY = 20;
        // Set position to the index of the first character in the paragraph.
        lineMeasurer.setPosition(paragraphStart);

        // Get lines until the entire paragraph has been displayed.
        while (lineMeasurer.getPosition() < paragraphEnd) {

            // Retrieve next layout. A cleverer program would also cache
            // these layouts until the component is re-sized.
            TextLayout layout = lineMeasurer.nextLayout(breakWidth);

            // Compute pen x position. If the paragraph is right-to-left we
            // will align the TextLayouts to the right edge of the panel.
            // Note: this won't occur for the English text in this sample.
            // Note: drawPosX is always where the LEFT of the text is placed.
            float drawPosX = layout.isLeftToRight()
                ? 0 : breakWidth - layout.getAdvance();

            // Move y-coordinate by the ascent of the layout.
            drawPosY += layout.getAscent();

            // Draw the TextLayout at (drawPosX, drawPosY).
            layout.draw(g, 
			(my_width - layout.getAdvance())/2,
			drawPosY);

            // Move y-coordinate in preparation for next layout.
            drawPosY += layout.getDescent() + layout.getLeading();
        }

	*/
    }

    private void executeTranslationOperations( ParOperation parOp, 
					       Graphics2D g2, 
					       List baseObjects,
					       BufferedImage buff)
    {
	Iterator ops = parOp.getOperations();

	Hashtable amount = new Hashtable(20);
	Hashtable originalX = new Hashtable(20);
	Hashtable originalY = new Hashtable(20);

	while (ops.hasNext())
	{
	    GraphicalOperation op = (GraphicalOperation) ops.next();

	    if (op instanceof MoveOperation)
	    {
		int dx = ((MoveOperation)op).getNewCoordinate().getX();
		int dy = ((MoveOperation)op).getNewCoordinate().getY();
		ArrayList gps = (ArrayList) op.getGraphicalPrimitives();
		for(int i=0; i<gps.size(); i++)
		{
		    GraphicalPrimitive gp = (GraphicalPrimitive)gps.get(i);
		    amount.put(gp.getId(),new Coordinate(dx,dy));
		}
	    }// MoveOperation
	}

	int num_frames = 20;
	int delay_ms = 60;
	for(int frame=0; frame<=num_frames; frame++)
	{
	    g2.setColor(Color.WHITE);
	    g2.fillRect(0,0,my_width,my_height);

	    if (snapAt==0)
		displayNarrative(
		     "Solving a quadratic equation by \"completing the square\"",
		     g2 );
	    else
		displayNarrative(
			     graphWin.getParentVisualizer().snapshots[snapAt-1].narrative,
			     g2 );
	    Iterator iter = baseObjects.iterator();
	    while (iter.hasNext())
	    {
		BaseObject obj=(BaseObject)iter.next();


		if (obj.isVisible()) 
		{
		    if (frame==0) 
			rememberOriginalPosition(obj, originalX, originalY);

		    Coordinate shift = (Coordinate) amount.get( obj.getId() );

		    if ( shift != null ) {  // object is moving
			if (obj instanceof TextDraw) {
			    TextDraw t = (TextDraw)obj;

			    int x = ((Integer)originalX.get(obj.getId())).intValue();
			    int y = ((Integer)originalY.get(obj.getId())).intValue();

			    double fraction = ((double) frame) / num_frames;
			    t.setX( (int) Math.round( x + fraction*shift.getX()));
			    t.setY( (int) Math.round( y + fraction*shift.getY()));
			}// TextDraw case
			else if (obj instanceof PolyDraw) {
			    PolyDraw l = (PolyDraw)obj;
			    
			    if (l.polyline) 
			    {
				int num = l.getXpoints().length;
				for(int i=0; i<num; i++)
				{
				    int x = ((Integer)originalX.get(obj.getId()+"x"+i)).intValue();
				    int y = ((Integer)originalY.get(obj.getId()+"y"+i)).intValue();

				    l.setX(i, (int) Math.round( 
					x + (double)frame*shift.getX()/num_frames ));
				    l.setY(i, (int) Math.round( 
				        y + (double)frame*shift.getY()/num_frames ));
				}
			    }// polyline case
			    else
			    {
				int x1 = ((Integer)originalX.get(obj.getId()+"x1")).intValue();
				int y1 = ((Integer)originalY.get(obj.getId()+"y1")).intValue();
				int x2 = ((Integer)originalX.get(obj.getId()+"x2")).intValue();
				int y2 = ((Integer)originalY.get(obj.getId()+"y2")).intValue();

				l.setX(0, (int) Math.round( 
				   x1 + (double)frame*shift.getX()/num_frames ));
				l.setY(0, (int) Math.round( 
				   y1 + (double)frame*shift.getY()/num_frames ));
				
				l.setX(1, (int) Math.round( 
				   x2 + (double)frame*shift.getX()/num_frames ));
				l.setY(1, (int) Math.round( 
				   y2 + (double)frame*shift.getY()/num_frames ));
			    }// line case
			}// PolyDraw case
		    }// if object is moving

		    obj.execute(g2,zoom,vertoff,horizoff);

		}// if visible
	    }// while loop on base objects
	    
	    my_image = buff;
	    repaint();		    
	    try { Thread.sleep(delay_ms); } catch (Exception e) {};

	}// for loop on frames
    }// executeTranslationOperations method


    private void rememberOriginalPosition(BaseObject obj, 
					  Hashtable originalX, 
					  Hashtable originalY)
    {
	if (obj instanceof TextDraw) 
        {
	    originalX.put(obj.getId(), new Integer(((TextDraw)obj).getX()));
	    originalY.put(obj.getId(), new Integer(((TextDraw)obj).getY()));
	} 
	else if (obj instanceof PolyDraw)   // a line or polyline
	{
	    
	    if (((PolyDraw)obj).polyline) // a polyline
	    {
		int[] x = ((PolyDraw)obj).getXpoints();
		int[] y = ((PolyDraw)obj).getYpoints();
		for(int i=0; i<x.length; i++)
		{
		    originalX.put(obj.getId()+"x"+i, new Integer(x[i]));
		    originalY.put(obj.getId()+"y"+i, new Integer(y[i]));
		}
	    }
	    else  // a single line segment
	    {
		originalX.put(obj.getId()+"x1",
			      new Integer(((PolyDraw)obj).getXpoints()[0]));
		originalY.put(obj.getId()+"y1",
			      new Integer(((PolyDraw)obj).getYpoints()[0]));
		originalX.put(obj.getId()+"x2",
			      new Integer(((PolyDraw)obj).getXpoints()[1]));
		originalY.put(obj.getId()+"y2",
			      new Integer(((PolyDraw)obj).getYpoints()[1]));
	    }
	}
    }

    private List cloneBaseObjects(List l) 
    {
	ArrayList copy = new ArrayList( 30 );
	try
	{
	    for(int i=0; i<l.size(); i++)
	    {
		BaseObject obj = (BaseObject) l.get(i);
		if (obj instanceof TextDraw)
		    copy.add(((TextDraw)obj).clone());
		else if (obj instanceof PolyDraw)
		    copy.add(((PolyDraw)obj).clone());
		else if (obj instanceof ParabolaDraw)
		    copy.add(((ParabolaDraw)obj).clone());
		else
		{
		    System.err.println("   BaseObject not supported " +
				       "in cloneBaseObjects: " +
				       obj.getClass());
		}

	    }
	}
	catch (CloneNotSupportedException e) 
	{
	    System.err.println("Cloning problem in cloneBaseObjects()");
	}
	return copy;
    }


    public void createDrawObjects(Iterator i) {
        LinkedList tmp = new LinkedList();

        while (i.hasNext()) {
            GraphicalPrimitive gp = (GraphicalPrimitive) i.next();

            Style s = gp.getStyle();
            if (gp instanceof Parabola) {
		ParabolaDraw obj = new ParabolaDraw( (Parabola)gp, s);
		if (((Parabola)gp).isHidden()) obj.setInvisible();
                //if (p.getWidth() == 0 || p.getHeight() == 0)
                //    continue;
                tmp.addLast( obj );
            } else if (gp instanceof Rectangle) {
                Rectangle r = (Rectangle) gp;
                //if (r.getWidth() == 0 || r.getHeight() == 0)
                //    continue;
                tmp.addLast(new RectDraw(r, s));
            } else if (gp instanceof Ellipse) 
	    {
                Ellipse e = (Ellipse) gp;
                //if (e.getRadius().getX() == 0 || e.getRadius().getY() == 0)
                //    continue;
                tmp.addLast(new OvalDraw(e, s));
            } else if (gp instanceof Text) {
                Text t = (Text) gp;
		TextDraw obj = new TextDraw(t, s);
		//System.out.println("in client " + s.getFont().getFamily());
		if (t.isHidden()) obj.setInvisible();
                tmp.addLast( obj );
            } else if (gp instanceof Line) {
                Line l = (Line) gp;
                Coordinate start = l.getStartCoordinate();
                Coordinate end = l.getEndCoordinate();
		PolyDraw obj = new PolyDraw(l);
		if (gp.isHidden()) obj.setInvisible();
                tmp.addLast( obj );
            } else if (gp instanceof Polygon) {
		//System.out.println(gp.getId() + " " + ((Polyline)gp).isClosed());
		PolyDraw obj = new PolyDraw((Polygon)gp);
		if (gp.isHidden()) obj.setInvisible();
                tmp.addLast( obj );
            } else {
                System.out.println("unknown graphical primitive: " + gp);                
            }
        }
        snapshotList.addLast(tmp);
    }
}
