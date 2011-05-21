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

package exe;
import java.util.*;

/**
 * <p><code>GAIGSlist</code> provides the ability to implement a linked list and also
 * create GAIGS visualizations of its state. Use the various constructors
 * to specify the general parameters for the list visualization, and use the
 * <code>toXML</code> method to actually generate the array XML for snapshots.</p>
 * 
 * <p>Many, but not all, of the methods specified by the <code>List</code> interface are
 * provided by this class.</p>
 * 
 * <p>Methods are also provided to set and get the presentation color of a list cell.
 * A default list cell color can be set by using the appropriate constructor. </p>
 * 
 * <p>Known inheriters: <code>GAIGSstack</code>, <code>GAIGSqueue</code>.
 * 
 * @author Joseph Naps (original toXML code)
 * @author Myles McNally (current version)
 * @version 6/20/06
 */

public class GAIGSlist extends GAIGSbase {

    
//---------------------- Instance Variables -------------------------------------


    
    /**
     * The actual list itself
     */
    LinkedList <Object> list;
    
    /**
     * The associated color list
     */
    LinkedList <String> colorList;
    
    
//---------------------- Constructors -------------------------------------------


    /**
     * Use all default values
     */
    public GAIGSlist() {
        this(DEFAULT_NAME, DEFAULT_COLOR, DEFAULT_X1, DEFAULT_Y1, DEFAULT_X2, DEFAULT_Y2, DEFAULT_FONT_SIZE);
    }
        
    /**
     * Set all instance variables
     * 
     * @param       name            Display name of this structure.
     * @param       color           Color for items unless locally overridden.
     * @param       x1              Left display bound.
     * @param       y1              Bottom display bound.
     * @param       x2              Top display bound.
     * @param       y2              Right display bound.
     * @param       fontSize        Font size for display.
     */
    public GAIGSlist(String name, String color, double x1, double y1, double x2, double y2, double fontSize) {
        this.name = name;
        this.color = color;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.fontSize = fontSize;
        list = new LinkedList <Object>();
        colorList = new LinkedList <String>();
    }  
    
    
//---------------------- Linked List Methods -----------------------------------


    /**
     * Appends the specified element to the end of this list.
     * Default color will be used for this item.
     * 
     * @param     v     The value to be stored. 
     */
    public boolean add (Object v) {
        colorList.add(color);
        return list.add(v);
    }

    /**
     * Appends the specified element to the end of this list
     * with the provided color.
     * 
     * @param     v     The value to be stored. 
     * @param     c     The color to be stored. 
     */
    public boolean add (Object v, String c) {
        colorList.add(c);
        return list.add(v);
    }
    
    /**
     * Inserts the specified element at the specified position
     * in this list. Default color will be used for this item.
     * 
     * @param     v     The value to be stored. 
     */
    public void add (int index, Object v) {
    	    colorList.add(index, color);
        list.add(index, v);
    }
    
    /**
     * Inserts the specified element at the specified position
     * in this list with the provided color.
     * 
     * @param     v     The value to be stored. 
     * @param     c     The color to be stored. 
     */
    public void add (int index, Object v, String c) {
	    colorList.add(index, c);
	    list.add(index, v);
    }
    
    /**
     * Inserts the given element at the beginning of this list.
     * Default color will be used for this item.
     * 
     * @param     v     The value to be stored. 
     */
    public void addFirst (Object v) {
	    colorList.addFirst(color);
	    list.addFirst(v);
    }
    
    /**
     * Inserts the given element at the beginning of this list
     * with the provided color.
     * 
     * @param     v     The value to be stored. 
     * @param     c     The color to be stored. 
     */
    public void addFirst (Object v, String c) {
	    colorList.addFirst(c);
	    list.addFirst(v);
    }
    
    /**
     * Inserts the given element at the end of this list.
     * Default color will be used for this item.
     * 
     * @param     v     The value to be stored. 
     */
    public void addLast (Object v) {
	    colorList.addLast(color);
	    list.addLast(v);
    }
    
    /**
     * Inserts the given element at the end of this list
     * with the provided color.
     * 
     * @param     v     The value to be stored. 
     * @param     c     The color to be stored. 
     */
    public void addLast (Object v, String c) {
	    colorList.addLast(c);
	    list.addLast(v);
    }

    /**
     * Removes all of the elements from this list. 
     */
    public void clear () {
	    colorList.clear();
	    list.clear();
    }
    
    /**
     * Returns true if this list contains the specified element.
     * 
     * @param     v     The value to be searched for. 
     */
    public boolean contains (Object v) {
        return indexOf(v) != -1;
    }
    
    /**
     * Returns the element at the specified position in this list.
     * 
     * @param     index     The location to be accessed. 
     */
    public Object get (int index) {
        return list.get(index);
    }
    
    /**
     * Returns the color of the element at the specified
     * position in this list.
     * 
     * @param     index     The location to be accessed. 
     */
    public String getColor (int index) {
        return (String)colorList.get(index);
    }
    
    /**
     * Returns the first element in this list.
     */
    public Object getFirst () {
        return list.getFirst();
    }
    
    /**
     * Returns the first element in this list.
     */
    public Object getLast () {
        return list.getLast();
    }
    
    /**
     * Returns the index in this list of the first
     * occurrence of the specified element, or -1 if
     * the List does not contain this element.
     * 
     * @param     v     The value to be searched for. 
     */
    public int indexOf (Object v) {
        return list.indexOf(v);
    }
    
    /**
     * Returns true if the list is empty.
     */
    public boolean isEmpty () {
        return list.isEmpty();
    }
    
    /**
     * Returns the index in this list of the last
     * occurrence of the specified element, or -1 if
     * the List does not contain this element.
     * 
     * @param     v     The value to be searched for. 
     */
    public int lastIndexOf (Object v) {
        return list.lastIndexOf(v);
    }     
    
    /**
     * Removes and return the element at the specified position
     * in this list.
     * 
     * @param     index     The location to be accessed. 
     */
    public Object remove (int index) {
    	    colorList.remove(index);
        return list.remove(index);
    }
    
    /**
     * Removes the first occurrence of the specified element in
     * this list. Return true if the item was found and removed.
     * 
     * @param     o     The object to be removed. 
     */
    public boolean remove (Object o) {
        int loc = indexOf(o);
        if (loc != -1) {
            remove(loc);
            return true;
        }
        return false;
    }

    /**
     * Remove and returns the first item in the list.
     */
    public Object removeFirst () {
	    colorList.removeFirst();    	
        return list.removeFirst();
    }   

    /**
     * Remove and returns the last item in the list.
     */
    public Object removeLast () {
	    colorList.removeLast();    	
        return list.removeLast();
    }   
    
    /**
     * Set the specified location with the element 
     * with the default color.
     * 
     * @param     index     The location to be accessed. 
     * @param     v         The value to be stored. 
     */
    public Object set (int index, Object v) {
        list.set(index, v);
        return v;
    }   
    
    /**
     * Set the specified location with the element 
     * with the provided color.
     * 
     * @param     index     The location to be accessed. 
     * @param     v         The value to be stored. 
     * @param     c         The color to be stored. 
     */
    public Object set (int index, Object v, String c) {
    	    colorList.set(index, c);
        list.set(index, v);
        return v;
    } 
    
    /**
     * Set the color of the element at the specified location.
     * 
     * @param     index     The location to be accessed. 
     * @param     c         The color to be stored. 
     */
    public void setColor (int index, String c) {
	    colorList.set(index, c);
    }   
    
    /**
     * Return the number of items in the list.
     */
    public int size () {
        return list.size();
    }      
    
//---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current state of the linked list
     * 
     * @return     A String containing GAIGS XML code for the linked list  
     */
    
    public String toXML() {
        return toXML("linkedlist");
    }
    
    /**
     * Creates and returns GAIGS XML code for the current state of the linear
     * structure named by the parameter string.
     * 
     * @param       s       The linear structure type name
     * @return      A String containing GAIGS XML code for the linked list  
     */
    public String toXML(String s) {
        String xmlString = "";
    
        xmlString += "<" + s + ">" + "\n";
        if (name != null)
            xmlString += "<name>" + name + "</name>" + "\n";
        xmlString += "<bounds "
                     + "x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2
                     + "\" fontsize=\"" + fontSize + "\"/>" + "\n";
                     
        for ( int i = 0; i < size(); i++ ) {
            Object item = get(i);
            String color = getColor(i);
            xmlString += "<list_item color=\"" + color + "\">" + "\n";
            xmlString += "<label>" + item + "</label>" + "\n";
            xmlString += "</list_item>" + "\n";
        }        
        xmlString += "</" + s + ">" + "\n";
        
        return xmlString;
    }
     
}
