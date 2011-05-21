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
* <p><code>Bytecode_invokestatic</code> provides a representation of an "invokestatic" bytecode in the * JVM.Use the <code>Bytecode_invokestatic</code> constructor to parse a line of output from javap into * a <code>Bytecode_invokestatic</code> object. Use the <code>execute</code> method through
* polymorphism * to simulate add operator.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

//invokestatic implemented
public class Bytecode_invokestatic extends Bytecode_
{

	/**
	 * Constructor
	 * @param	str		The string to be parsed into a Bytecode_invokestatic object
	 */        
        Bytecode_invokestatic(String str) 
        {
                parse(str);
        }

	/*
	 * (non-Javadoc)
	 * @see exe.jvmbytecodes.Bytecode_#execute()
	 */
        public int execute() throws IOException,JDOMException 
        {
			next = lineNumber+3; //update line number
			f = (Frame_) Driver._runTimeStack.peek(); //get the current frame
			f.returnAddress = next; //update the return address

            int index = 0;
            for(Method_ m : Driver.classes[0].methods) //search for the invoked method
            {
                    if(m.name.equals(path))
                    {
                            break;
                    }
                    index++;
            }
        
			writeMethodSnap(); //write a snapshot of invoking the method
			Driver.currentMethod = index; //update the currentMethod counter
			int numParameters = parameters.length; //number of parameters passed in

			int counter = 0;
			int check2 = 0;
			for(int i = (numParameters-1); i >= 0; i--) //find the number of array elements needed to store parameters
			{
				if(i != 0)
					check2 = i-1;
				else
					check2 = 0;
				if((parameters[i].equals("D") || parameters[i].equals("J")) && !parameters[check2].equals("[")){

					System.out.println("here");
					counter+=2;
				}
				else if(!parameters[i].equals("["))
					counter++;
				//System.out.println(check2);
				System.out.println(parameters[i]);
				System.out.println(counter);
			}

        	Frame_ f2 = new Frame_(Driver.currentMethod); //create the new frame
			Driver.runTimeStack.push(path, f2.CURRENT_FRAME_COLOR); //push the new frame on the visual stack

			//keep track of colors for parameters (grays)
			int j = counter-1;
		    if(numParameters%2 == 0)
				;
			else
				f2.stackColor = true; 
			//put the parameters in the local variable array of f2
			int check;
			for(int i = (numParameters-1); i >= 0; i--)
			{
				//System.out.println(j);
				if(i != 0)
					check = i-1;
				else
					check = 0;
				if(parameters[i].equals("I") && !parameters[check].equals("[")) //is the parameter an int?
				{
					int var;
					var = popInteger();
					f2._localVariableArray[j] = Integer.toString(var);
					if(!f2.stackColor){
						f2.localVariableArray.set(Integer.toString(var), j, Driver.darkGray);
						f2.stackColor = true;
					}
					else{
						f2.localVariableArray.set(Integer.toString(var), j, Driver.lightGray);
						f2.stackColor = false;		
					}				
				}
				else if(parameters[i].equals("J") && !parameters[check].equals("[")) //in the parameter a long?
				{
					long var = popLong();
					f2._localVariableArray[j-1] = Long.toString(var);
					f2._localVariableArray[j] = "";
					if(!f2.stackColor){
						f2.localVariableArray.set(var, j-1, Driver.darkGray);
						f2.localVariableArray.set("", j, Driver.darkGray);
						f2.stackColor = true;
					}
					else{
						f2.localVariableArray.set(var, j-1, Driver.lightGray);
						f2.localVariableArray.set("", j, Driver.lightGray);
						f2.stackColor = false;
					}
					j--;
				}
				else if(parameters[i].equals("F") && !parameters[check].equals("[")) //is the parameter a float?
				{
					float var;
					var = popFloat();
					f2._localVariableArray[j] = Float.toString(var);
					if(!f2.stackColor){
						f2.localVariableArray.set(Float.toString(var), j, Driver.darkGray);
						f2.stackColor = true;
					}
					else{
						f2.localVariableArray.set(Float.toString(var), j, Driver.lightGray);
						f2.stackColor = false;
					}				
				}
				else if(parameters[i].equals("D") && !parameters[check].equals("[")) //is the parameter a double?
				{
					double var = popDouble();
				System.out.println("here");
					f2._localVariableArray[j-1] = Double.toString(var);
					f2._localVariableArray[j] = "";
					if(!f2.stackColor){
						f2.localVariableArray.set(var, j-1, Driver.darkGray);
						f2.localVariableArray.set("", j, Driver.darkGray);
						f2.stackColor = true;
					}
					else{
						f2.localVariableArray.set(var, j-1, Driver.lightGray);
						f2.localVariableArray.set("", j, Driver.lightGray);
						f2.stackColor = false;
					}
					j--;
				}
				else if((parameters[i].equals("I") && parameters[check].equals("[")) || (parameters[i].equals("J") && parameters[check].equals("[")) || (parameters[i].equals("F") && parameters[check].equals("[")) || (parameters[i].equals("D") && parameters[check].equals("["))) //is the parameter an int?
				{	
					String var;
					var = popString();
					f2._localVariableArray[j] = var;
					if(!f2.stackColor){
						f2.localVariableArray.set(var, j, Driver.darkGray);
						f2.stackColor = true;
					}
					else{
						f2.localVariableArray.set(var, j, Driver.lightGray);
						f2.stackColor = false;		
					}
					i--;	
				}
				else
					System.out.println("not working");

				j--;
			System.out.println("Now");
			}
			Driver._runTimeStack.push(f2); //push the frame on the virtual stack
			next = 0; //set the next line number to the start of the new method
			
			if (Driver.runTimeStack.size() > 50)
		  		throw new InvalidClassFileException("Cannot produce more thatn 50 stack frames.\n"
		  			+"Try less than 50 method calls.");
		  
        	return next;
        }
}

