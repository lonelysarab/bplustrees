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

/*
 * RenderingPane.java
 *
 * Created on August 7, 2004, 1:13 PM
 */

package org.gaffneyc.gaff;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.image.*;
/**
 *
 * @author  Chris Gaffney
 */
public class RenderingPane extends JComponent {
    /** */
    private static final int FORWARD = -1;
    /** */
    private static final int BACKWARD = 1;
    
    /** */
    private int index = 0;
    /** */
    private ArrayList snapshots;
    /** */
    private Image image = null;
    
    /** Creates a new instance of RenderingPane */
    public RenderingPane(ArrayList snapshots) {
        this.snapshots = snapshots;
        
        int width = 0;
        int height = 0;
        
        Iterator itr = snapshots.iterator();
        while(itr.hasNext()) {
            Snapshot s = (Snapshot)itr.next();
            
            int w = s.getImage().getWidth(null);
            int h = s.getImage().getHeight(null);
            if(w > width) {
                width = w;
            }
            if(h > height) {
                height = h;
            }
        }
        
        gotoFrame(0);
        setPreferredSize(new Dimension(width, height));
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        //Image image = ((Snapshot)snapshots.get(index)).getImage();
        //g.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), this);
        
        //Graphics2D g2d = (Graphics2D)g;
        
        //g2d.setColor(Color.BLACK);
        g.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), this);
        
        
        
        //g2d.fill(area);
    }
    
    public void stepForward() {
        animate(FORWARD);
    }
    
    public void stepBackward() {
        animate(BACKWARD);
    }
    
    public void gotoFrame(int index) {
        this.index = index;
        
        Image orig = ((Snapshot)snapshots.get(index)).getImage();
        BufferedImage buff = new BufferedImage(orig.getWidth(null), orig.getHeight(null), BufferedImage.TYPE_INT_RGB);
        
        Shape mask = createMask(buff.getWidth(), buff.getHeight());
        
        Graphics2D g2d = (Graphics2D)buff.getGraphics();
        
        g2d.drawImage(orig, 0, 0, buff.getWidth(), buff.getHeight(), this);
        g2d.setColor(Color.BLACK);
        g2d.fill(mask);
        
        image = buff;
        repaint();
    }
    
    public int getIndex() {
        return index;
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
    
    /**
     *
     */
    private void animate(int direction) {
        Image image1 = ((Snapshot)snapshots.get(index)).getImage();
        index += -1 * direction;
        Image image2 = ((Snapshot)snapshots.get(index)).getImage();
        
        int width = image1.getWidth(null);
        int height = image1.getHeight(null);
        double step = 2;
        
        Image buffer = getGraphicsConfiguration().createCompatibleVolatileImage(width, height);
        Graphics2D g2d = (Graphics2D)buffer.getGraphics();
        
        AffineTransform trans = AffineTransform.getTranslateInstance(step * direction, 0);
        AffineTransform orig = g2d.getTransform();
        
        Shape mask = createMask(width, height);
        
        for(double i = 0; i < width; i += step) {
            g2d.transform(trans);
            g2d.drawImage(image1, 0, 0, this);
            g2d.setColor(Color.BLACK);
            g2d.fill(mask);
            
            AffineTransform last = g2d.getTransform();
            g2d.transform(AffineTransform.getTranslateInstance(width * (-1 * direction), 0));
            g2d.drawImage(image2, 0, 0, this);
            g2d.fill(mask);
            
            g2d.setTransform(last);
            
            this.image = buffer;
            repaint();
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                
            }
        }
        Image b = getGraphicsConfiguration().createCompatibleImage(width, height);
        b.getGraphics().drawImage(buffer, 0, 0, null);
        this.image = b;
    }
}
