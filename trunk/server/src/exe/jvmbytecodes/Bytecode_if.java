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
package exe.jvmbytecodes;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.net.*;
import org.jdom.*;
import exe.*;
import exe.pseudocode.*;

/*
* <p><code>Bytecode_if</code> provides a representation of an "if" bytecode in the JVM.
* Use the <code>Bytecode_if</code> constructor to parse a line of output from javap into a 
* <code>Bytecode_if</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate comparing two integers.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

// ifeq, ifne, iflt, ifgt, ifle, ifge, if_icmpeq, if_icmpne, if_icmplt, if_icmpgt, if_icmpge, if_icmple
public class Bytecode_if extends Bytecode_ {
  /**
    * The Integers to compare
  */
		Integer x, y;

	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_if object
	 */
	Bytecode_if(String str) {
		parse(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
	public int execute() throws IOException,JDOMException 
	{
		f = (Frame_) Driver._runTimeStack.peek(); //get current frame
		next = lineNumber + 1; //update line number
		next = next + 2; //update line number

		if(!underscore.contains("_")) //is it an if_icmp bytecode?
		{ //no
			if (opcode.contains("ifeq")) //ifeq
			{
				x = (Integer) f._stack.pop(); //int to compare
				if ( x == 0) //comparison
					next = Integer.parseInt(arguments.get(0));
				else
					;/* no jump */

				//update visual
				f.stack.setRowLabel("", (f.currentStackHeight));
				f.stack.set("", f.currentStackHeight++);
				if(f.currentStackHeight == f.stackSize)
					;
				else
					f.stack.setRowLabel("Top", (f.currentStackHeight));	
				makeGreenSingle();
			}
			else if (opcode.contains("ifne")) //ifne
			{
				x = (Integer) f._stack.pop(); //int to compare

				if ( x != 0) //comparison
					next = Integer.parseInt(arguments.get(0));
				else
					;/* no jump */	

				//update visual
				f.stack.setRowLabel("", (f.currentStackHeight));
				f.stack.set("", f.currentStackHeight++);
				if(f.currentStackHeight == f.stackSize)
					;
				else
					f.stack.setRowLabel("Top", (f.currentStackHeight));	
				makeGreenSingle();
			}
			else if (opcode.contains("iflt")) //iflt
			{
				x = (Integer) f._stack.pop(); //int to compare

				if ( x < 0) //comparison
					next = Integer.parseInt(arguments.get(0));
				else
					;/* no jump */	

				//update visual
				f.stack.setRowLabel("", (f.currentStackHeight));
				f.stack.set("", f.currentStackHeight++);
				if(f.currentStackHeight == f.stackSize)
					;
				else
					f.stack.setRowLabel("Top", (f.currentStackHeight));
				makeGreenSingle();
			}
			else if (opcode.contains("ifge")) //ifge
			{
				x = (Integer) f._stack.pop(); //int to compare

				if ( x >= 0) //comparison
					next = Integer.parseInt(arguments.get(0));
				else
					;/* no jump */		

				//update visual
				f.stack.setRowLabel("", (f.currentStackHeight));
				f.stack.set("", f.currentStackHeight++);
				if(f.currentStackHeight == f.stackSize)
					;
				else
					f.stack.setRowLabel("Top", (f.currentStackHeight));	
				makeGreenSingle();
			}
			else if (opcode.contains("ifgt")) //ifgt
			{
				x = (Integer) f._stack.pop(); //int to compare

				if ( x > 0) //comparison
					next = Integer.parseInt(arguments.get(0));
				else
					;/* no jump */		

				//update visual
				f.stack.setRowLabel("", (f.currentStackHeight));
				f.stack.set("", f.currentStackHeight++);
				if(f.currentStackHeight == f.stackSize)
					;
				else
					f.stack.setRowLabel("Top", (f.currentStackHeight));	
				makeGreenSingle();
			}
			else if (opcode.contains("ifle")) //ifle
			{
				x = (Integer) f._stack.pop(); //int to compare

				if ( x <= 0) //comparison
					next = Integer.parseInt(arguments.get(0));
				else
					;/* no jump */		

				//update visual
				f.stack.setRowLabel("", (f.currentStackHeight));
				f.stack.set("", f.currentStackHeight++);
				if(f.currentStackHeight == f.stackSize)
					;
				else
					f.stack.setRowLabel("Top", (f.currentStackHeight));	
				makeGreenSingle();
			}
			else
				System.out.println("Not a recognized bytecode");
		}

		else //it is an if_icmp
		{
			if (arguments.get(0).contains("icmpeq")) //if_icmpeq
			{
				x = (Integer) f._stack.pop(); //int to compare
				y = (Integer) f._stack.pop(); //int to compare

				if ( x == y) //comparison
					next = Integer.parseInt(arguments.get(1));
				else
					;/* no jump */

				//update visual
				f.stack.setRowLabel("", (f.currentStackHeight));
				f.stack.set("", f.currentStackHeight++);
				f.stack.set("", f.currentStackHeight++);
				if(f.currentStackHeight == f.stackSize)
					;
				else
					f.stack.setRowLabel("Top", (f.currentStackHeight));
				makeGreen();
			}
			else if (arguments.get(0).contains("icmpne")) //if_icmpne
			{
				x = (Integer) f._stack.pop(); //int to compare
				y = (Integer) f._stack.pop(); //int to compare

				if ( x != y) //comparison
					next = Integer.parseInt(arguments.get(1));
				else
					;/* no jump */

				//update visual
				f.stack.setRowLabel("", (f.currentStackHeight));
				f.stack.set("", f.currentStackHeight++);
				f.stack.set("", f.currentStackHeight++);
				if(f.currentStackHeight == f.stackSize)
					;
				else
					f.stack.setRowLabel("Top", (f.currentStackHeight));
				makeGreen();
			}
			else if (arguments.get(0).contains("icmplt")) //if_icmplt
			{
				x = (Integer) f._stack.pop(); //int to compare
				y = (Integer) f._stack.pop(); //int to compare

				if ( x > y) //comparison
					next = Integer.parseInt(arguments.get(1));
				else
					;/* no jump */

				//update visual
				f.stack.setRowLabel("", (f.currentStackHeight));
				f.stack.set("", f.currentStackHeight++);
				f.stack.set("", f.currentStackHeight++);
				if(f.currentStackHeight == f.stackSize)
					;
				else
					f.stack.setRowLabel("Top", (f.currentStackHeight));
				makeGreen();
			}
			else if (arguments.get(0).contains("icmpge")) //if_icmpge
			{
				x = (Integer) f._stack.pop(); //int to compare
				y = (Integer) f._stack.pop(); //int to compare

				if ( x <= y) //comparison
					next = Integer.parseInt(arguments.get(1));
				else
					;/* no jump */

				//update visual
				f.stack.setRowLabel("", (f.currentStackHeight));
				f.stack.set("", f.currentStackHeight++);
				f.stack.set("", f.currentStackHeight++);
				if(f.currentStackHeight == f.stackSize)
					;
				else
					f.stack.setRowLabel("Top", (f.currentStackHeight));
				makeGreen();
			}
			else if (arguments.get(0).contains("icmpgt")) //if_icmpgt
			{
				x = (Integer) f._stack.pop(); //int to compare
				y = (Integer) f._stack.pop(); //int to compare
				

				if (x < y) //comparison
					next = Integer.parseInt(arguments.get(1));
				else
					;/* no jump */

				//update visual
				f.stack.setRowLabel("", (f.currentStackHeight));
				f.stack.set("", f.currentStackHeight++);
				f.stack.set("", f.currentStackHeight++);
				if(f.currentStackHeight == f.stackSize)
					;
				else
					f.stack.setRowLabel("Top", (f.currentStackHeight));
				makeGreen();
			}
			else if (arguments.get(0).contains("icmple")) //if_icmple
			{
				x = (Integer) f._stack.pop(); //int to compare
				y = (Integer) f._stack.pop(); //int to compare


				if ( x >= y) //comparison
					next = Integer.parseInt(arguments.get(1));
				else
					;/* no jump */

				//update visual
				f.stack.setRowLabel("", (f.currentStackHeight));
				f.stack.set("", f.currentStackHeight++);
				f.stack.set("", f.currentStackHeight++);
				if(f.currentStackHeight == f.stackSize)
					;
				else
					f.stack.setRowLabel("Top", (f.currentStackHeight));
				makeGreen();
			}
			else
				System.out.println("Not a recognized bytecode.");
		}

		f.returnAddress = next; //update return address
		return next; //return next line number
	}

    /**
	 * Highlight green for if_icmp bytecodes
     *
     */
	void makeGreen() throws IOException,JDOMException
	{
		f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
		f.stack.setColor(f.currentStackHeight-2, Driver.lightGray);
		writeSnap();
		f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
		f.stack.setColor(f.currentStackHeight-2, Driver.lightGray);
	}

    /**
	 * Highlight green for if bytecodes
     *
     */
	void makeGreenSingle() throws IOException,JDOMException
	{
		f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
		writeSnap();
		f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
	}

/*
	void createQuestion1() throws IOException {
		tfQuestion = new XMLtfQuestion(Driver.show, Driver.questionID + "");
		tfQuestion.setQuestionText("The bytecode will jump to line number " + arguments.get(1) + ".");
		tfQuestion.setAnswer(x <= y);
	}


	void createQuestion2() throws IOException {
		mcQuestion = new XMLmcQuestion(Driver.show, Driver.questionID + "");
		mcQuestion.setQuestionText("What line number will the program jump to next?");
		mcQuestion.addChoice(next + "");
		mcQuestion.addChoice(arguments.get(1));
		mcQuestion.addChoice(lineNumber + "");
		if (x > y)
			mcQuestion.setAnswer(1);
		else
			mcQuestion.setAnswer(2);
	}


	void setQuestion() throws IOException {
		Random rand = new Random();
		int random = rand.nextInt(2);


		if(rand.equals(0)) {
			createQuestion1();
		} 
		else
			createQuestion2();
	}*/

}
