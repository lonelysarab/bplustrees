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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;

import javax.swing.JComponent;

import xaal.adapters.jhave.objects.BaseObject;

public class Graphprim extends JComponent implements MouseListener, MouseMotionListener, ComponentListener {

    private DrawCanvas viewWin;

    public final static boolean debug = XaalAV.debug;

    public boolean readyToPaint = false;

    private XaalAV myParentVisualizer;

    public int workingWindow = 0; // current window that is being manipulated

    private boolean mouseDown = false; // Keeps track of whether the mouse
    // button is being held down so that the
    // mouse cursor can be properly set when
    // the mouse enters the viewing window.

    // allows for the change of the number of windows

    public int getNumViews() {
        return 1;
    }

    public Graphprim(XaalAV my_vis) {
        super();
        myParentVisualizer = my_vis;
        setLayout(null); // let me handle the layout
        addNotify();

        setPreferredSize(new Dimension(XaalAV.preferred_width,
                XaalAV.preferred_height));
        setBackground(new Color(1.0f, 1.0f, 1.0f)); // ensure white bg

        viewWin = new DrawCanvas(this); // intialize the base Draw
        add(viewWin); // add the base Draw to the graphics window
        viewWin.setFont(new Font("Serif", Font.BOLD, 300)); // set it to a
                                                                // scalable font
        viewWin.setBounds(getInsets().left,
                getInsets().top, // set up its size to fit the window
                getSize().width - getInsets().left - getInsets().right,
                getSize().height - getInsets().top - getInsets().bottom);

        // FOOBAR -- commented this out and instead set it at end of readScript
        readyToPaint = true;
    }

    public void createSnap(Iterator graphicals) {
        if (debug)
            System.out
                    .println("About to create graphprimlist using a snap element");
        viewWin.createDrawObjects(graphicals);
    }

    public XaalAV getParentVisualizer()
    {
	return myParentVisualizer;
    }

    /*
     * This next set of handlers satisfies the ComponentListener interface. The
     * only event we really want to react to is the resize event so we can reset
     * the GKS drawing coords.
     */
    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        if (getNumViews() == 1) {
            BaseObject.maxsize = getSize().width - getInsets().left
                    - getInsets().right;
        }
        if (debug)
            System.out.println("Resized to " + BaseObject.maxsize);
    }

    /*
     * null methods to satisfy MouseListener and MouseMotionListener interfaces.
     * We don't really need to do anything special with these events.
     */
    public void mouseMoved(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        if (mouseDown) {
            Cursor dragCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
            setCursor(dragCursor);
        } else {
            Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
            setCursor(moveCursor);
        }
    }

    public void mouseExited(MouseEvent e) {
        Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        setCursor(defaultCursor);
    }

    public void mouseReleased(MouseEvent e) {
    }

    /* Pass mouseDown events along to DrawCanvas */
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
        // Use crosshair for dragging because Java does not have a built-in
        // cursor for a hand that is "grabbed onto" the viewing window.
        Cursor dragCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        setCursor(dragCursor);
        if (debug)
            System.out.println("mousePressed");
        viewWin.mDown(e.getX(), e.getY());
    }

    /* Pass the mouseUp from the end of a drag operation to the DrawCanvas areas */
    public void mouseUp(MouseEvent e, boolean dragdone) {
        mouseDown = false;
        Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
        setCursor(moveCursor);
        if (dragdone)
            viewWin.mDrag(e.getX(), e.getY());
    }

    /* Check for double-clicks */
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (debug)
                System.out.println("double-clicked");
            viewWin.vertoff = 0;
            viewWin.horizoff = 0;
            if (viewWin != null)
                viewWin.execute(myParentVisualizer.snapAt, XaalAV.NO_ANIMATION);
        }
    }

    public void mouseDragged(MouseEvent e) {
        viewWin.mDrag(e.getX(), e.getY());
    }

    // this is a call to allow the XaalAV Visualizer to ask for a redraw or
    // for a new snapshot to be drawn.
    public void execute(int snapAt, int animation) {

        if (debug)
            System.out.println("Graphwin executing");
        viewWin.execute(snapAt, animation);

        // repaint(); // I think this last repaint is extra and unnecessary
    }

   public void clickedScaleBar(int barValue) {
        double amount = ((double) barValue) / ((double) 100);
        DrawCanvas.zoom = amount;
        if (viewWin != null)
            viewWin.execute(myParentVisualizer.snapAt,XaalAV.NO_ANIMATION);
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (readyToPaint) {
            if (viewWin != null) {
                viewWin.setBounds(getInsets().left, getInsets().top,
                        getSize().width - getInsets().left
                              - getInsets().right, getSize().height
                              - getInsets().top - getInsets().bottom);
                viewWin.execute(myParentVisualizer.snapAt,XaalAV.NO_ANIMATION);
            }
        }
    }
}
