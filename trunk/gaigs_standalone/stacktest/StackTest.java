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

// ******************************
// File: StackTest.java
// Associated File(s): Stack.java
// Author: Joseph Naps
// Date: 07-16-2005
// ******************************

//package exe.stacktest;		// Don't use the package name in standalone mode

import java.io.*;
import exe.*;

public class StackTest
{
    // *****************************************************************
    // Takes in a series of stack push and pop commands at the command
    // line. The first command line arg is the path of the file you
    // wish to output the XML to.  To initiate a pop simply the string
    // "pop" is needed.  To initiate a push the string "pushx" is used
    // where x is what you wish to push on to the stack.  Keep in mind
    // that an exception will be thrown if a pop is called on an empty
    // stack.
    // *****************************************************************
    public static void main(String args[]) throws IOException
    {
	XMLfibQuestion fib;
	XMLtfQuestion tf;
	XMLmcQuestion mc;
	int qr = 0;
	PrintWriter pw = new PrintWriter(new FileWriter(new File(args[0])));
	XMLquestionCollection questions = new XMLquestionCollection(pw);
	pw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n");
	pw.write("<!DOCTYPE show PUBLIC \"-//JHAVE//DTD GAIGS SHO//EN\" \"gaigs_sho.dtd\">"+"\n"+"\n");
	pw.write("<show>"+"\n"+"\n");
	Stack stack = new Stack();
	for( int i = 1; i < args.length; i++)
	{
	    pw.write("<snap>"+"\n"); 
	    pw.write("<title>stack</title>"+"\n");

	    // The pseudocode_url won't work in standalone mode
	    if(args[i].length() == 3) 
	    {		
		//		pw.write("<pseudocode_url>index.php?line=1</pseudocode_url>"+"\n");
		stack.pop();
	    }
	    else
	    {
		//		pw.write("<pseudocode_url>index.php?line=2</pseudocode_url>"+"\n");
		String s = args[i].substring(4);
		stack.push(s);
	    }


	    pw.write(stack.toXML());
	    if( i % 3 == 0 && i + 1 < args.length )
	    {
		if( args[i+1].equals("pop"))
		{
		    fib = new XMLfibQuestion(pw, new Integer(qr++).toString());
		    fib.setQuestionText("What value will be popped from the stack?");
		    fib.insertQuestion();
		    questions.addQuestion(fib);
		    fib.setAnswer((String)stack.peek());
		}
		else
		{
		    mc = new XMLmcQuestion(pw, new Integer(qr++).toString());
		    mc.setQuestionText("What will the color of the next stack item be?");
		    mc.insertQuestion();
		    questions.addQuestion(mc);
		    mc.addChoice("red");
		    mc.addChoice("blue");
		    if( (stack.size() % 2) == 0) mc.setAnswer(2);
		    else mc.setAnswer(1);
		}
	    }
	    pw.write("</snap>"+"\n"+"\n");
	}
	questions.writeQuestionsAtEOSF();
	pw.write("</show>");
	pw.close();
    }
}

