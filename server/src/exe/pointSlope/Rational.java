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

package exe.pointSlope;

import java.awt.FontMetrics;
import org.jdom.Element;

class Rational extends Visual
{
    private int num;
    private int den;
    private int x, y;
    private int numwidth, denwidth;

    Rational(int num)            
    { 
	this.num = num; 
	den = 1; 
    }

    Rational(int num, int den) 
    { 
	
	this.num = num; 
	this.den = den; 
	normalize();
	reduce();
    }


    Rational(String id, int num, int den, int x, int y, int ftsize,
	     FontMetrics fm, String color, boolean hidden) 
    { 
	this.id = id;
	this.num = num; 
	this.den = den; 
	this.x = x;
	this.y = y;
	this.left = x;
	numwidth = fm.stringWidth( num+"" );
	denwidth = fm.stringWidth( den+"" );
	this.width = Math.max( numwidth, denwidth );
	this.right = left + width;
	this.fontSize = ftsize;
	this.color = color;
	this.hidden = hidden;
	this.family = pointSlope.ftName;
	this.italic = false;
	this.bold = false;

	//normalize();
	//reduce();
    }

    boolean equalsZero()
    {
	return ( num == 0 );
    }

    boolean equalsOne()
    {
	return ( num == den );
    }

    boolean equalsMinusOne()
    {
	return ( num == -den );
    }

    // assumed reduced
    boolean isInteger()
    {
	return ( den == 1 );
    }


    // assumed normalized
    boolean isPositive()
    {
	return (num > 0);
    }

    // assumed normalized
    boolean isNegative()
    {
	return (num < 0);
    }

    public Element[] makeElements()
    {
	Element[] e = new Element[3];
	int center = left + width/2;

	Element coord, contents, style, font, c;

	/* numerator element */
	e[0] = new Element("text");
	e[0].setAttribute("id",id + "_num");
	if (hidden) e[0].setAttribute( "hidden", "true" );
	coord = new Element("coordinate");
	coord.setAttribute("x",(center-numwidth/2) + "");
	coord.setAttribute("y",(y-(int)(fontSize*0.1)) + "");
	e[0].addContent( coord );
	contents = new Element("contents");
	contents.addContent(num + "");
	e[0].addContent(contents);
	style = new Element( "style" );
	c = new Element( "color" );
	c.setAttribute("name",color);
	style.addContent( c );
	font = new Element( "font" ); // not used for div but OK
	font.setAttribute( "size", fontSize + "");
	if (family != null)
	    font.setAttribute( "family", family);
	if (italic)
	    font.setAttribute( "italic", "true");
	if (bold)
	    font.setAttribute( "bold", "true");
	style.addContent( font );
	e[0].addContent(style);

	/* dividing line element */
	e[1] = new Element("line");
	e[1].setAttribute("id",id + "_div");
	if (hidden) e[1].setAttribute( "hidden", "true" );
	coord = new Element("coordinate");
	coord.setAttribute("x",left + "");
	coord.setAttribute("y", y+ "");
	e[1].addContent( coord );
	coord = new Element("coordinate");
	coord.setAttribute("x",right + "");
	coord.setAttribute("y", y+ "");
	e[1].addContent( coord );
	style = new Element( "style" );
	c = new Element( "color" );
	c.setAttribute("name",color);
	style.addContent( c );
	e[1].addContent(style);
    
	/* denominator element */
	e[2] = new Element("text");
	e[2].setAttribute("id",id + "_den");
	if (hidden) e[2].setAttribute( "hidden", "true" );
	coord = new Element("coordinate");
	coord.setAttribute("x",(center-denwidth/2) + "");
	//coord.setAttribute("x",(right - denwidth) + "");
	coord.setAttribute("y",(y+(int)(0.95*fontSize)) + "");
	e[2].addContent( coord );
	contents = new Element("contents");
	contents.addContent(den + "");
	e[2].addContent(contents);
	style = new Element( "style" );
	c = new Element( "color" );
	c.setAttribute("name",color);
	style.addContent( c );
	font = new Element( "font" ); // not used for div but OK
	font.setAttribute( "size", fontSize + "");
	if (family != null)
	    font.setAttribute( "family", family);
	if (italic)
	    font.setAttribute( "italic", "true");
	if (bold)
	    font.setAttribute( "bold", "true");
	style.addContent( font );
	e[2].addContent(style);

	return e;
    }


    public void translate(int dx, int dy)
    {
	x += dx;
	left += dx;      
	right += dx;
	y += dy;
    }

    public int getNum()
    {
	return num;
    }

    public int getDen()
    {
	return den;
    }

    private void normalize()
    {
	if (den<0)
	{
	    num *= -1;
	    den *= -1;
	}
    }

    private static int gcd(int a, int b)  /* a>0 and b>0 */
    {
	//System.out.println("in GCD: " + a + " / " + b);
	int min = Math.min(a,b);
	int max = Math.max(a,b);
	int rem = max % min;
       
	if ( rem == 0 ) return min;
	else return gcd( min, rem );
    }

    private void reduce()
    {
	//System.out.println("calling GCD with " + Math.abs(num) + " / " +
	//	   Math.abs(den) );
	if (num==0)
	    den = 1;
	else
	{
	    int GCD = gcd( Math.abs(num), Math.abs(den) );
	    if (GCD>1)
		{
		    num /= GCD;
		    den /= GCD;
		}
	}
    }

    public Rational add(Rational r)
    {
	Rational sum = new Rational(num*r.den + r.num*den, den*r.den );
	sum.normalize();
	sum.reduce();
	return sum;
    }


    public Rational sub(Rational r)
    {
	Rational diff = new Rational(num*r.den - r.num*den, den*r.den );
	diff.normalize();
	diff.reduce();
	return diff;
    }

    public Rational mult(Rational r)
    {
	Rational prod = new Rational(num*r.num, den*r.den );
	prod.normalize();
	prod.reduce();
	return prod;
    }


    public Rational div(Rational r)
    {
	Rational d = new Rational(num*r.den, den*r.num );
	d.normalize();
	d.reduce();
	return d;
    }

}// Rational class
