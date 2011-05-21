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

// ***********************************
// File: Stack.java
// Associated File(s): StackTest.java
// Author: Joseph Naps
// Date: 07-16-2005
// ***********************************

// package exe.stacktest;		// Don't use the package name in standalone mode

import java.util.LinkedList;
import java.util.EmptyStackException;

public class Stack
{
    private LinkedList word;

    // The constructor simply initializes an emtpy stack
    public Stack()
    {
	word = new LinkedList();
    }
    
    // Returns true is the stack is empty
    public boolean empty()
    {
	return word.isEmpty();
    }

    // Returns the number of items in the stack
    public int size()
    {
	return word.size();
    }

    // Adds an item to the top of the stack
    public void push( Object ob )
    {
	word.add( 0, ob );
    }
    
    // *************************************************************
    // Removes and returns the item on top of the stack throwing an
    // exception if the stack is empty
    // *************************************************************
    public Object pop()
    {
	if( word.size() == 0 ) throw new EmptyStackException();
	return word.removeFirst();
    }

    // ****************************************************************
    // Only returns the item on top of the stack throwing an exception
    // if the stack is empty
    // ****************************************************************
    public Object peek()
    {
	if( word.size() == 0 ) throw new EmptyStackException();
	return word.getFirst();
    }

    // **************************************************************
    // Returns an XML represenation of the stack at the present time
    // in a form that can be understood by the Jhave program
    // **************************************************************
    public String toXML()
    {
	String xmlString = "";

	xmlString = xmlString + "<stack>"+"\n";
	for( int i = 0; i < word.size(); i++)
	{
	    String color = "";
	    if( (word.size() - i) % 2 == 0 ) color = "#FF0000";
	    else color = "#0000FF";

	    xmlString = xmlString + "<list_item color=\""+color+"\">"+"\n";
	    xmlString = xmlString + "<label>"+word.get(i)+"</label>"+"\n";
	    xmlString = xmlString + "</list_item>"+"\n";
	}
	xmlString = xmlString + "</stack>"+"\n";
	return xmlString;
    }
}
