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
 * Build a GAIGShashStruct adhering to the following DTD
 *
 *    <!-- HASHSTRUCT -->
 *    
 * <!ELEMENT hashstructroot (name?, bounds?, hashbox)>
 *
 * <!ELEMENT hashbox ((hashstruct)?,(hashlist)?,(EMPTY))>
 * <!ATTLIST hashbox boxname CDATA ""
 *                 color CDATA "black">
 *
 * <!ELEMENT hashstruct ((hashbox)+)>
 *
 * <!ELEMENT hashlist ((hashlistnode)*)>
 *
 * <!ELEMENT hashlistnode (EMPTY)>
 * <!ATTLIST hashlistnode boxdata CDATA ""
 *                       color CDATA "black">
 *
 *     <!-- END OF HASHSTRUCT -->
 */
public class GAIGShashStruct extends GAIGSbase 
{
	private ArrayList hashTable;
	private String name,hash_struct_root_color;
	private double x1,y1,x2,y2,fontSize;
	private int size;
	private String[] hash_struct_list_colors;
	
    /**
     * Use all default rendering values
     * @param       size            Size of the hash table.
     */
    public GAIGShashStruct(int size) 
    {
    	String[] temp = new String[size];
    	for(int i = 0; i < size; i++)
    	{
    		temp[i] = "black";
    	}
    	setVariables("","black",0.0,0.0,1.0,1.0,0.5,size,temp);
    }

    /**
     * 
     * @param       name            Name of the hashtable root.
     * @param       color           Color for items unless locally overridden.
     * @param       x1              Left display bound.
     * @param       y1              Bottom display bound.
     * @param       x2              Top display bound.
     * @param       y2              Right display bound.
     * @param       fontSize        Font size for display.
     * @param       size            Size of the hash table.
     */
    public GAIGShashStruct(String name, String color, double x1, double y1, double x2, double y2, double fontSize, int size) 
    {
    	String[] temp = new String[size];
    	for(int i = 0; i < size; i++)
    	{
    		temp[i] = "black";
    	}
    	setVariables(name,color,x1,y1,x2,y2,fontSize,size,temp);
    }
    
    /**
     * 
     * @param name Display name of this structure.
     * @param hash_struct_root_color Color for items unless locally overridden.
     * @param x1 Left display bound.
     * @param y1 Bottom display bound.
     * @param x2 Top display bound.
     * @param y2 Right display bound.
     * @param fontSize Font size for display.
     * @param size Size of the hash table.
     * @param hash_struct_list_colors array of colors for the (initially empty) hashboxes
     */
    public GAIGShashStruct(String name, String hash_struct_root_color, double x1, double y1, double x2, double y2, double fontSize,
			   int size, String[] hash_struct_list_colors) 
    {
    	setVariables(name,hash_struct_root_color,x1,y1,x2,y2,fontSize,size,hash_struct_list_colors);
    }
    
    private void setVariables(String name, String hash_struct_root_color, double x1, double y1, double x2, double y2, double fontSize, int size, String[] hash_struct_list_colors)
    {
    	this.name = name;
    	this.hash_struct_root_color = hash_struct_root_color;
    	this.x1 = x1;
    	this.y1 = y1;
    	this.x2 = x2;
    	this.y2 = y2;
    	this.fontSize = fontSize;
    	this.size = size;
    	this.hash_struct_list_colors = hash_struct_list_colors;
    	hashTable = new ArrayList();
    	for(int i = 0; i < size; i++) hashTable.add(new LinkedList());
    }
    
//---------------------- Hash Table Methods -----------------------------------


    /**
     * Add the specified object to this hash table using Java's
     * built-in hash code mod the size of the table.  Default color
     * will be used for this item.
     * 
     * @param v The value to be stored.
     */
    public boolean add (Object v) 
    {
    	int index = v.hashCode()%size;
    	LinkedList llist = (LinkedList)hashTable.get(index);
    	llist.add(new HashTableDataItem(v,"black"));
    	return true;
    }

    /**
     * Add the specified object to this hash table using Java's
     * built-in hash code mod the size of the table with the provided
     * color.
     * 
     * @param v The value to be stored.
     * @param c The color to be stored.
     */
    public boolean add (Object v, String c) 
    {
    	int index = v.hashCode()%size;
    	LinkedList llist = (LinkedList)hashTable.get(index);
    	llist.add(new HashTableDataItem(v,c));
    	return true;
    }
    
    /**
     * Inserts the specified element at the specified position
     * in this hash table. 
     * Default color will be used for this item.
     * 
     * @param index The table position
     * @param v The value to be stored.
     */
    public boolean add (int index, Object v) 
    {
    	LinkedList llist = (LinkedList)hashTable.get(index);
    	llist.add(new HashTableDataItem(v,"black"));
    	return true;
    }
    
    /**
     * Inserts the specified element at the specified position in this
     * hash table with the provided color.
     * 
     * @param index The table position
     * @param v The value to be stored.
     * @param c The color to be stored.
     */
    public boolean add (int index, Object v, String c) 
    {
    	LinkedList llist = (LinkedList)hashTable.get(index);
    	llist.add(new HashTableDataItem(v,c));
    	return true;
    }
    
    /**
     * Returns true if this hash table contains the specified element.
     * 
     * @param     v     The value to be searched for. 
     */
    public boolean contains (Object v) 
    {
    	for(int i = 0; i < size; i++)
    	{
    		LinkedList llist = (LinkedList)hashTable.get(i);
    		for(int k = 0; k < llist.size(); k++)
    		{
    			HashTableDataItem data = (HashTableDataItem)llist.get(k);
    			Object obj = data.getData();
    			if(v.equals(obj)) return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Returns true if the hash table is empty.
     */
    public boolean isEmpty () 
    {
    	for(int i = 0; i < size; i++)
    	{
    		LinkedList llist = (LinkedList)hashTable.get(i);
    		if(!llist.isEmpty()) return false;
    	}
    	return true;
    }
    
    
    /**
     * Removes the specified element in this hash table. Return true
     * if the item was found and removed.
     * 
     * @param     o     The object to be removed. 
     */
    public boolean remove (Object o) 
    {
    	for(int i = 0; i < size; i++)
    	{
    		LinkedList llist = (LinkedList)hashTable.get(i);
    		for(int k = 0; k < llist.size(); k++)
    		{
    			HashTableDataItem data = (HashTableDataItem)llist.get(k);
    			Object obj = data.getData();
    			if(o.equals(obj))
    			{
    				llist.remove(k);
    				return true;
    			}
    		}
    	}
    	return false;
    }

    /**
     * Return the number of items in the hash table.
     */
    public int number_items() 
    {
    	int numItems = 0;
    	for(int i = 0; i < size; i++)
    	{
    		LinkedList llist = (LinkedList)hashTable.get(i);
    		numItems += llist.size();
    	}
    	return numItems;
    }      
    
//---------------------- XML Methods -------------------------------------------


    /**
     * Creates and returns GAIGS XML code for the current state of the hash table
     * 
     * @return     A String containing GAIGS XML code for the hash table
     */
    
    public String toXML() 
    {
    	String snapShot = "";
    	snapShot = snapShot+"<hashstructroot>\n";
    	snapShot = snapShot+"<bounds x1=\""+x1+"\" y1=\""+y1+"\" x2=\""+x2+"\" y2=\""+y2+"\" fontsize=\""+fontSize+"\"/>\n";
    	snapShot = snapShot+"<hashbox boxname=\""+name+"\" color=\""+hash_struct_root_color+"\">\n";
    	if(size > 0) snapShot = snapShot+"<hashstruct>\n";
    	
    	for(int i = 0; i < size; i++)
    	{
    		snapShot = snapShot+"<hashbox color=\""+hash_struct_list_colors[i]+"\">\n";
    		LinkedList llist = (LinkedList)hashTable.get(i);
    		if(!llist.isEmpty()) snapShot = snapShot+"<hashlist>\n";
    		for(int k = 0; k < llist.size(); k++)
    		{
    			 HashTableDataItem data = (HashTableDataItem)llist.get(k);
    			 Object obj = data.getData();
    			 String boxColor = data.getColor();
    			 snapShot = snapShot+"<hashlistnode boxdata=\""+obj+"\" color=\""+boxColor+"\">\n";
    			 snapShot = snapShot+"</hashlistnode>\n";
    		}
    		if(!llist.isEmpty()) snapShot = snapShot+"</hashlist>\n";
    		snapShot = snapShot+"</hashbox>\n";
    	}
    	
    	if(size > 0) snapShot = snapShot+"</hashstruct>\n";
    	snapShot = snapShot+"</hashbox>\n";
    	snapShot = snapShot+"</hashstructroot>\n";
    	return snapShot;
    }
    
    // This local class is used to store the information in a nice container within the hashTable ArrayList
    class HashTableDataItem
    {
    	private Object data;
    	private String color;
    	
    	public HashTableDataItem(Object v, String c)
    	{
    		data = v;
    		color = c;
    	}
    	
    	public void setData(Object v)
    	{
    		data = v;
    	}
    	
    	public void setColor(String c)
    	{
    		color = c;
    	}
    	
    	public Object getData()
    	{
    		return data;
    	}
    	
    	public String getColor()
    	{
    		return color;
    	}
    }
}