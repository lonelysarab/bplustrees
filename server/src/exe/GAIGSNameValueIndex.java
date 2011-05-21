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
 * Build a GAIGSNameValueindex adhering to the following DTD
 *
 *      <!-- INDEXSTRUCT -->                                              
 *                                                                        
 * <!ELEMENT index (name?, bounds?, (indexstructroot)+,(indexitem)*)>     
 *                                                                        
 * <!ELEMENT namesortedroot (EMPTY)>                                      
 * <!ATTLIST namesortedroot boxname CDATA ""                              
 *                          color CDATA "black">                          
 *                                                                        
 * <!ELEMENT valsortedroot (EMPTY)>                                       
 * <!ATTLIST valsortedroot boxname CDATA ""                               
 *                         color CDATA "black">                           
 *                                                                        
 * <!ELEMENT indexitemlist ((indexitem)*,(EMPTY))>                        
 *                                                                        
 * <!ELEMENT indexitem (EMPTY)>                                           
 * <!ATTLIST indexitem name CDATA ""                                      
 *                     value CDATA ""                                     
 *                     color CDATA "black">                               
 *                                                                        
 *        <!-- END OF INDEXSTRUCT -->                                       
 */


public class GAIGSNameValueIndex extends GAIGSbase {

	private String name, color;
	private double x1,y1,x2,y2,fontSize;
	private ArrayList index;
	
    /**
     * Use all default rendering values
     */
    public GAIGSNameValueIndex() 
    {
    	setVariables("","black",0.0,0.0,1.0,1.0,0.5);
    }

    /**
     * 
     * @param       name            Display name of this structure.
     * @param       color           Color for items unless locally overridden.
     * @param       x1              Left display bound.
     * @param       y1              Bottom display bound.
     * @param       x2              Top display bound.
     * @param       y2              Right display bound.
     * @param       fontSize        Font size for display.
     */
    public GAIGSNameValueIndex(String name, String color, double x1, double y1, double x2, double y2, double fontSize) 
    {
    	setVariables(name,color,x1,y1,x2,y2,fontSize);
    }  
    
    private void setVariables(String name,String color,double x1,double y1,double x2,double y2,double fontSize)
    {
    	this.name = name;
    	this.color = color;
    	this.x1 = x1;
    	this.x2 = x2;
    	this.y1 = y1;
    	this.y2 = y2;
    	this.fontSize = fontSize;
    	index = new ArrayList();
    }
//---------------------- Name-Value Index Methods -----------------------------------


    /**
     * Add the specified name-value pair to this name-value index.  Default
     * color will be used for this item.
     * 
     * @param name The name to be stored.
     * @param v The value to be stored.
     */
    public boolean add (String name, Object v) 
    {
    	IndexItemData data = new IndexItemData(v,name,"black");
    	index.add(data);
    	return true;
    }

    /**
     * Add the specified name-value pair to this name-value index with the
     * provided color.
     * 
     * @param name The name to be stored.
     * @param v The value to be stored.
     * @param c The color to be stored.
     */
    public boolean add (String name, Object v, String c) 
    {
    	IndexItemData data = new IndexItemData(v,name,c);
    	index.add(data);
    	return true;
    }
    
    /**
     * Returns true if this name-value index contains the specified name.
     * 
     * @param     name     The name to be searched for. 
     */
    public boolean contains (String name) 
    {
    	for(int i = 0; i < index.size(); i++)
    	{
    		IndexItemData data = (IndexItemData)index.get(i);
    		String temp = data.getName();
    		if(temp.equals(name)) return false;
    	}
    	return false;
    }
    
    /**
     * Returns true if the name-value index is empty.
     */
    public boolean isEmpty () 
    {
    	return index.isEmpty();
    }
    
    
    /**
     * Removes the specified name (and corresponding value) in this
     * name-value index. Return true if the item was found and
     * removed.
     * 
     * @param     name     The name to be removed. 
     */
    public boolean remove (String name) 
    {	
    	for(int i = 0; i < index.size(); i++)
    	{
    		IndexItemData data = (IndexItemData)index.get(i);
    		String temp = data.getName();
    		if(temp.equals(name))
    		{
    			index.remove(i);
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * Return the number of items in the name-value index.
     */
    public int number_items () 
    {
    	return index.size();
    }      
    
//---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current state of the name-value index
     * 
     * @return     A String containing GAIGS XML code for the name-value index
     */
    
    public String toXML() 
    {
    	String snapShot = "";
    	
       	snapShot = snapShot+"<index>\n";
    	snapShot = snapShot+"<bounds x1=\""+x1+"\" y1=\""+y1+"\" x2=\""+x2+"\" y2=\""+y2+"\" fontsize=\""+fontSize+"\"/>\n";
    	snapShot = snapShot+"<namesortedroot boxname=\"nameSorted\"></namesortedroot>\n";
    	snapShot = snapShot+"<valsortedroot boxname=\"valSorted\"></valsortedroot>\n";
    	if(index.size() > 0) snapShot = snapShot+"<indexitemlist>\n";
    	
    	for(int i = 0; i < index.size(); i++)
    	{
    		IndexItemData data = (IndexItemData)index.get(i);
    		Object val = data.getValue();
    		String name = data.getName();
    		String color = data.getColor();
    		
    		snapShot = snapShot+"<indexitem name=\""+name+"\" value=\""+val+"\" color=\""+color+"\">\n";
    		snapShot = snapShot+"</indexitem>\n";
    	}
    	
    	if(index.size() > 0) snapShot = snapShot+"</indexitemlist>\n";
    	snapShot = snapShot+"</index>\n";
       	return snapShot;
    }

    // This local class is used to store the information for the individual index items
    class IndexItemData
    {
    	private Object value;
    	private String name, color;
    	
    	public IndexItemData(Object o, String n, String s)
    	{
    		name = n;
    		value = o;
    		color = s;
    	}
    	
    	public void setName(String n)
    	{
    		name = n;
    	}
    	
    	public void setValue(Object o)
    	{
    		value = o;
    	}
    	
    	public void setColor(String s)
    	{
    		color = s;
    	}
    	
    	public Object getValue()
    	{
    		return value;
    	}
    	
    	public String getName()
    	{
    		return name;
    	}
    	
    	public String getColor()
    	{
    		return color;
    	}
    }
}
    

