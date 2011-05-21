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

package xaal.adapters.jhave.objects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.Font;
import xaal.objects.graphical.Coordinate;
import xaal.objects.graphical.Line;
import xaal.objects.graphical.Parabola;
import xaal.objects.graphical.Style;

public final class ParabolaDraw extends BaseObject implements Cloneable {     //polyDraw are 11
    private int a,b,c;
    private int xmin,ymin;
    private int xmax,ymax;
    private int x,y;
    private int width, height;

    private String family;
    private boolean italic;
    private boolean bold;
    private int fontSize;

    public ParabolaDraw(Parabola p, Style s){
	super(p);
	this.a = p.getA();
	this.b = p.getB();
	this.c = p.getC();
	this.xmin = p.getXmin();
	this.ymin = p.getYmin();
	this.xmax = p.getXmax();
	this.ymax = p.getYmax();
	this.x = p.getX();
	this.y = p.getY();
	this.width = p.getWidth();
	this.height = p.getHeight();

	family = s.getFont().getFamily();
        fontSize = s.getFont().getSize();
	italic = s.getFont().isItalic();
	bold = s.getFont().isBold();
    }


    public Object clone() throws CloneNotSupportedException
    {
	return super.clone();
    }

    public boolean execute(Graphics g, double zoom, int vertoff, int horizoff)
    {
	Graphics2D g2d = (Graphics2D) g;

	/*
	System.out.println( "("+x+","+y+")" + "\n" +
			    "("+width+","+height+")" +"\n" +
			    "("+xmin+","+xmax+")" +"\n" +
			    "("+ymin+","+ymax+")" +"\n\n"
			    );
	*/
	if (isVisible())
	{
	    zoom = zoom * getScale();
	    
	    if (zoom != 1) {
		vertoff = vertoff + (int) (maxsize - maxsize * zoom) / 2;
		horizoff = horizoff + (int) (maxsize - maxsize * zoom) / 2;
	    }
	    int scaledX = (int) Math.round(zoom * x) + horizoff;
	    int scaledY = (int) Math.round(zoom * y) + vertoff;
	    int scaledWidth = (int)(Math.round(zoom*width));
	    int scaledHeight = (int)(Math.round(zoom*height));
	    //int scaledSize = (int) Math.round(zoom * fontSize);

	    // horiz and vert units per pixel
	    double hupp = 1.0 * (xmax-xmin) / width / zoom;
	    double vupp = 1.0 * (ymax-ymin) / height / zoom;

	    setStroke(g2d, zoom);
	    //int style = Font.PLAIN;
	    //g2d.setFont( new Font( family,  style, scaledSize) );
	    //g2d.setColor(color);        	    


	    g2d.setColor(new Color(220,220,220));
	    g2d.fillRect(scaledX,scaledY-scaledHeight,scaledWidth, scaledHeight);

	    g2d.setColor( Color.BLACK );
	    // X-axis
	    int yv = (int)Math.round(scaledY+ymin/vupp); // *zoom
	    g2d.drawLine(1+(int)(1.05*scaledX), yv, 
			 scaledX + (int)(0.96*scaledWidth)-1, yv);

	    // Y-axis
	    g2d.drawLine((int)Math.round(scaledX-xmin/hupp), // *zoom
			 (int)(0.98*scaledY)-1, 
			 (int)Math.round(scaledX-xmin/hupp), // *zoom
			 scaledY - (int)(0.98*scaledHeight) + 1);

		
	    //g2d.setColor(Color.BLACK);
	    // move to origin
	    //g2d.translate( 
	    // + Math.round( -xmin/horiz_units_per_pixel ), 
	    //		  y + Math.round( ymin/vert_units_per_pixel ) );


	    //ArrayList<Integer> xp = new ArrayList<Integer>( xmax-xmin+1 );
	    //ArrayList<Integer> yp = new ArrayList<Integer>( xmax-xmin+1 );
	    int[] xp = new int[scaledWidth+1];
	    int[] yp = new int[scaledWidth+1];

	    //int xshift = horizoff + (int) Math.round( zoom*(x - xmin/hupp));
	    //int yshift = vertoff +  (int) Math.round( zoom*(y + ymin/vupp));

	    //g2d.scale( 1/width_per_pixel, 1/height_per_pixel );

	    int index = 0;
	    double actualx, actualy;
	    for(int xpixel=scaledX; xpixel<=scaledX+scaledWidth; xpixel++) 
	    {
		xp[index] = xpixel;
		actualx = xmin + (xpixel-scaledX)*hupp; // /zoom
		actualy = a * actualx * actualx + b * actualx + c;
		int tmp = scaledY + (int)Math.round((ymin-actualy)/vupp); //*zom
		
		if (tmp>=scaledY-scaledHeight && tmp<=scaledY)
		{
		    yp[index] = tmp;
		    index++;
		}
	    }

	    //g.drawString("parabola " + a + " " + b + " " + c + " " + xmin + " " + ymin + " " + xmax + " " + ymax + " " + x + " " + y + " " + width + " " + height + " "  , scaledX, scaledY);
	    g2d.setColor(Color.BLUE);
	    g2d.drawPolyline(xp, yp, index);

	    double vx;
	    double vy;

	    // y-intercept
	    vy = c;
	    int vxi = (int)Math.round(scaledX + (-xmin)/hupp); // *zoom
	    int vyi = (int)Math.round(scaledY + (ymin-c)/vupp); // *zoom
	    g2d.setColor(Color.GREEN);
	    g2d.fillOval( vxi-4, vyi-4, 9, 9);

	    // x-intercepts
	    int disc = b*b-4*a*c;
	    if (disc>0)
	    {		
		g2d.setColor(Color.RED);
		vx = (-b - Math.sqrt(1.0*disc))/2/a;
		vxi = (int)Math.round(scaledX + (vx-xmin)/hupp);// *zoom
		vyi = (int)Math.round(scaledY + ymin/vupp);// *zoom

		g2d.fillOval( vxi-4, vyi-4, 9, 9);

		vx = (-b + Math.sqrt(1.0*disc))/2/a;
		vxi = (int)Math.round(scaledX + (vx-xmin)/hupp);// *zoom
		vyi = (int)Math.round(scaledY + ymin/vupp);// *zoom
		g2d.setColor(Color.RED);
		g2d.fillOval( vxi-4, vyi-4, 9, 9);
	    }

	    // vertex
	    vx = -1.0 * b / (2 *a);
	    vy = a*vx*vx + b*vx + c;
	    vxi = (int)Math.round(scaledX + (vx-xmin)/hupp);// *zoom
	    vyi = (int)Math.round(scaledY + (ymin-vy)/vupp);// *zoom
	    g2d.setColor(Color.BLUE);
	    g2d.fillOval( vxi-4, vyi-4, 9, 9);
	}
	return true;
    }
}
