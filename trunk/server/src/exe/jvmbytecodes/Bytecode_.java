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
import java.lang.InterruptedException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import exe.GAIGSlegend;
import exe.GAIGSItem;
import exe.GAIGStext;
import java.io.*;
import java.util.*;
import java.net.*;
import exe.pseudocode.*;
import org.jdom.JDOMException;
import java.io.*;
import java.util.*;
import org.jdom.*;
import exe.*;
import exe.pseudocode.*;

/*
* <p><code>Bytecode_</code> provides a representation of a bytecode within the Java Virtual
* Machine (JVM). Use the <code>parse</code> method to parse a line of output from javap into a 
* <code>Bytecode_</code> object. Use the <code>execute</code> method through polymorphism to 
* simulate the actions the opcode indicates. Lastly, use the various kinds of <code>push</
* code>, <code>pop</code>, <code>store</code>, and <code>load</code> methods to access 
* different data structures of the simulated JVM and write snapshots to the XML file.
*
* @author Caitlyn Pickens
* @author Cory Sheeley
* @author William Clements
* @version 7/11/2010
*/

abstract class Bytecode_ {
  //---------------------- Instance Variables -------------------------------------
  /**
    * The opcode
  */
	public String opcode;
  /**
    * The line number (refers to bytecodes)
  */
	public int lineNumber;
  /**
    * Arguments following the bytecode
  */
	public ArrayList<String> arguments = new ArrayList<String>();
  /**
    * The type of object referred to
  */
	public String objectType;
  /**
    * A string taken from the comment section of javap output
	* Refers to something in the constant pool
	* Could be a method call, an object, or a primitive type
  */
	public String path;
  /**
    * Parameters of a method
  */
	public String[] parameters;
  /**
    * Return type of a method
  */
	public String returnType;
  /**
    * Next line number (refers to bytecodes)
  */
	public int next;
  /**
    * Does the opcode contain an underscore?
  */
	public String underscore;
  /**
    * The entire javap input string
  */
	public String entireOpcode;
  /**
    * Text describing the current opcode
  */
	public String text;
  /**
    * The frame a Bytecode_ object exists within
  */
	public Frame_ f;

    //---------------------- Constructors -------------------------------------------
	// No constructors.

    //---------------------- Abstract methods -------------------------------------------
	public abstract int execute() throws IOException,JDOMException;
	
    //---------------------- Snapshot Methods ---------------------------------------

    /**
     * Write a generic snapshot of a bytecode
     */
	public void writeSnap() throws IOException, JDOMException {
		f = (Frame_) Driver._runTimeStack.peek(); //peek at the current frame

		//create a gaigslabel
	    GAIGSlabel hpLabel = new GAIGSlabel("Heap", -.1, .85, .1, .95, .5);

		//create a primitiveCollection object
	    exe.GAIGSprimitiveCollection pcHeap = new exe.GAIGSprimitiveCollection( "");
	    pcHeap.addPolygon(
			    4,
			    new double[] { -.1, -.1, .1, .1 },
			    new double[] { 0.55, 0.95, 0.95, 0.55 },
			    "#EAC1F7",  // fill color
			    "#EAC1F7",  // outline color
			    "#FFFFFF",  // label color
			    ""          // polygon label
			  );

		//create a primitiveCollection object
	    exe.GAIGSprimitiveCollection pc = new exe.GAIGSprimitiveCollection( f.methodName + " frame");
	    pc.addPolygon(
			    4,
			    new double[] { .20, .20, 1.15, 1.15 },
			    new double[] { 0.05, 0.95, 0.95, 0.05 },
			    f.CURRENT_FRAME_COLOR,  // fill color
			    f.CURRENT_FRAME_COLOR,  // outline color
			    "#FFFFFF",  // label color
			    ""          // polygon label
			  );


		//Draw connecting lines to frame
		double [] coords = Driver.runTimeStack.getCornerPoints(
          Driver.runTimeStack.size() - 1);
		double [] xline1 = new double[]{coords[2], .20};
		double [] yline1 = new double[]{coords[3], .05};
		pc.addLine(xline1, yline1, f.CURRENT_FRAME_COLOR, "#000000", "");
		double [] xline2 = new double[]{coords[4], .20};
		double [] yline2 = new double[]{coords[5], .95};
		pc.addLine(xline2, yline2, f.CURRENT_FRAME_COLOR, "#000000", "");

		//Get the colors to highlight the lines in the Pseudocode window
		int[] colorArray;
		if(next > lineNumber)
			colorArray = new int[] {PseudoCodeDisplay.GREEN, PseudoCodeDisplay.RED};
		else
			colorArray = new int[] {PseudoCodeDisplay.RED, PseudoCodeDisplay.GREEN};

	f.textBox.setText(text);
		//Write the snapshot
if(Driver.numberOfArrays > 0){
       if (f.stackSize > 0) {
	    Driver.show.writeSnap(Driver.TITLE, 
				  MakeURI.doc_uri(lineNumber, f), 
				  MakeURI.make_uri(new int[] {next, lineNumber}, colorArray, f), 
				  pc,
				  pcHeap,
				  Driver.runTimeStack, 
				  f.stack, 
				  Driver.array,
				  hpLabel,
				  f.localVariableArray, f.jvmLegend, f.textBox
				  );
		}
		else {
			Driver.show.writeSnap(Driver.TITLE, 
				  MakeURI.doc_uri(lineNumber, f), 
				  MakeURI.make_uri(new int[] {next, lineNumber}, colorArray, f), 
				  pc,
				  pcHeap,
				  Driver.runTimeStack,
				  Driver.array,
				  hpLabel,
				  f.localVariableArray, f.jvmLegend, f.emptyStackLabel, f.textBox
				  );
			}
}
else
       if (f.stackSize > 0) {
	    Driver.show.writeSnap(Driver.TITLE, 
				  MakeURI.doc_uri(lineNumber, f), 
				  MakeURI.make_uri(new int[] {next, lineNumber}, colorArray, f), 
				  pc,
				  pcHeap,
				  Driver.runTimeStack, 
				  f.stack, 
				  hpLabel,
				  f.localVariableArray, f.jvmLegend,
				  f.textBox);
		f.textBox.setText(" ");
		}
		else {
			Driver.show.writeSnap(Driver.TITLE, 
				  MakeURI.doc_uri(lineNumber, f), 
				  MakeURI.make_uri(new int[] {next, lineNumber}, colorArray, f), 
				  pc,
				  pcHeap,
				  Driver.runTimeStack,
				  hpLabel,
				  f.localVariableArray, f.jvmLegend, f.emptyStackLabel,f.textBox
				  );
			}

		}

    /**
     * Write a snapshot of a return bytecode
     */
	public void writeSnapReturn() throws IOException,JDOMException {
		f = (Frame_) Driver._runTimeStack.peek(); //peek at the current frame

		//create a gaigslabel
	    GAIGSlabel hpLabel = new GAIGSlabel("Heap", -.1, .85, .1, .95, .5);

		//create a primitiveCollection object
	    exe.GAIGSprimitiveCollection pcHeap = new exe.GAIGSprimitiveCollection( "");
	    pcHeap.addPolygon(
			    4,
			    new double[] { -.1, -.1, .1, .1 },
			    new double[] { 0.55, 0.95, 0.95, 0.55 },
			    "#EAC1F7",  // fill color
			    "#EAC1F7",  // outline color
			    "#FFFFFF",  // label color
			    ""          // polygon label
			  );

		//create a primitiveCollection object
	    exe.GAIGSprimitiveCollection pc = new exe.GAIGSprimitiveCollection( f.methodName + " frame");
	    pc.addPolygon(
			    4,
			    new double[] { .20, .20, 1.15, 1.15 },
			    new double[] { 0.05, 0.95, 0.95, 0.05 },
			    f.CURRENT_FRAME_COLOR,  // fill color
			    f.CURRENT_FRAME_COLOR,  // outline color
			    "#FFFFFF",  // label color
			    ""          // polygon label
			  );

		//Draw connecting lines to frame
		double [] coords = Driver.runTimeStack.getCornerPoints(
          Driver.runTimeStack.size() - 1);
		double [] xline1 = new double[]{coords[2], .20};
		double [] yline1 = new double[]{coords[3], .05};
		pc.addLine(xline1, yline1, f.CURRENT_FRAME_COLOR, "#000000", "");
		double [] xline2 = new double[]{coords[4], .20};
		double [] yline2 = new double[]{coords[5], .95};
		pc.addLine(xline2, yline2, f.CURRENT_FRAME_COLOR, "#000000", "");

		f.textBox.setText(text);
		//Write the snapshot
if(Driver.numberOfArrays > 0) {
		if (f.stackSize > 0)
			Driver.show.writeSnap(Driver.TITLE, 
				  MakeURI.doc_uri(lineNumber, f), 
				  MakeURI.make_uri(new int[] {next}, new int[] 
						   {PseudoCodeDisplay.RED}, f), 
				  pc,
				  pcHeap,
				  Driver.runTimeStack, 
				  f.stack, 
				  Driver.array,
				  hpLabel,
				  f.localVariableArray, f.jvmLegend, f.textBox
				  );
		else
			Driver.show.writeSnap(Driver.TITLE, 
				  MakeURI.doc_uri(lineNumber, f), 
				  MakeURI.make_uri(new int[] {next}, new int[] 
						   {PseudoCodeDisplay.RED}, f), 
				  pc,
				  pcHeap,
				  Driver.runTimeStack,
				  Driver.array,
				  hpLabel,
				  f.localVariableArray, f.jvmLegend, f.emptyStackLabel, f.textBox
				  );
}
else {
		if (f.stackSize > 0)
			Driver.show.writeSnap(Driver.TITLE, 
				  MakeURI.doc_uri(lineNumber, f), 
				  MakeURI.make_uri(new int[] {next}, new int[] 
						   {PseudoCodeDisplay.RED}, f), 
				  pc,
				  pcHeap,
				  Driver.runTimeStack, 
				  f.stack, 
				  hpLabel,
				  f.localVariableArray, f.jvmLegend,f.textBox
				  );
		else
			Driver.show.writeSnap(Driver.TITLE, 
				  MakeURI.doc_uri(lineNumber, f), 
				  MakeURI.make_uri(new int[] {next}, new int[] 
						   {PseudoCodeDisplay.RED}, f), 
				  pc,
				  pcHeap,
				  Driver.runTimeStack,
				  hpLabel,
				  f.localVariableArray, f.jvmLegend, f.emptyStackLabel,f.textBox
				  );
		}

		f.textBox.setText(" ");
	}

    /**
     * Write a snapshot of a bytecode that calls a method
     */
	public void writeMethodSnap() throws IOException,JDOMException {

		//create a gaigslabel
	    GAIGSlabel hpLabel = new GAIGSlabel("Heap", -.1, .85, .1, .95, .5);

		//create a primitiveCollection object
	    exe.GAIGSprimitiveCollection pcHeap = new exe.GAIGSprimitiveCollection( "");
	    pcHeap.addPolygon(
			    4,
			    new double[] { -.1, -.1, .1, .1 },
			    new double[] { 0.55, 0.95, 0.95, 0.55 },
			    "#EAC1F7",  // fill color
			    "#EAC1F7",  // outline color
			    "#FFFFFF",  // label color
			    ""          // polygon label
			  );

		f.textBox.setText(text);
		//Write the snapshot
if(Driver.numberOfArrays > 0) {
		Driver.show.writeSnap(Driver.TITLE, 
				  MakeURI.doc_uri(lineNumber, f), 
				  MakeURI.make_uri(lineNumber, 
						   PseudoCodeDisplay.GREEN, f), 
				  Driver.array,
				  hpLabel,
				  Driver.runTimeStack, f.jvmLegend, pcHeap, f.textBox);
}
else {
		Driver.show.writeSnap(Driver.TITLE, 
				  MakeURI.doc_uri(lineNumber, f), 
				  MakeURI.make_uri(lineNumber, 
						   PseudoCodeDisplay.GREEN, f), 
						   hpLabel,
				  Driver.runTimeStack, f.jvmLegend, pcHeap, f.textBox);
		}
		f.textBox.setText(" ");
	}

    /**
     * Write the last snapshot of the visualization
     */
	public void writeFinalSnap() throws IOException,JDOMException {
		f.textBox.setText(text);
		//Write the snapshot
		Driver.show.writeSnap(Driver.TITLE, 
				  MakeURI.doc_uri(lineNumber, f), 
				  MakeURI.make_uri(lineNumber, 
						   PseudoCodeDisplay.GREEN, f), 
				  Driver.runTimeStack, f.jvmLegend,f.textBox);
		f.textBox.setText(" ");
	}

    //---------------------- Stack Manipulation Methods ----------------------
    //---------------------- Double Methods ----------------------------------

    /**
	 * Push a Double on the operand stack
     *
     * @param       d           The double to be pushed
     */
	public void pushDouble(double d) throws IOException,JDOMException {
		//push a double on to the virtual stack
		f._stack.push("");
		f._stack.push(d);

		//set the double in the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));

		f.stack.set("", --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.set(d, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		if(opcode.contains("return"))
			writeSnapReturn();
		else
			writeSnap();

		//update the coloring of the visual stack
		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stack.setColor(f.currentStackHeight+1, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stack.setColor(f.currentStackHeight+1, Driver.lightGray);
			f.stackColor = true;
		}	
	}
    /**
	 * Pop a Double from the operand stack
     *
     * @return           The double popped off the operand stack
     */
	public Double popDouble() {

		//pop a double off of the virtual stack
		Double temp;
		temp = (Double) f._stack.pop();
		f._stack.pop();

		//set the visual stack to blank values
		f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set("", f.currentStackHeight++);			
		f.stack.set("", f.currentStackHeight++);
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("Top", (f.currentStackHeight));

		//update the coloring of the visual stack
		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
			f.stack.setColor(f.currentStackHeight-2, Driver.lightGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
			f.stack.setColor(f.currentStackHeight-2, Driver.lightGray);
			f.stackColor = true;
		}

		//return the double
		return temp;
	}

    /**
	 * Store a Double in the local variable array
     *
     * @param       x           The double to be stored
     */
	public void storeDouble(Double x) throws IOException,JDOMException {
		//get the index where the double will be stored
		int index = Integer.parseInt(arguments.get(0));

		//store the double in the virtual local variable array
		f._localVariableArray[index] = String.valueOf(x);
		f._localVariableArray[index+1] = "";

		//set the double in the visual local variable array
		f.localVariableArray.set(String.valueOf(x), index, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.localVariableArray.set("", index+1, Driver.CURRENT_HIGHLIGHT_COLOR);

		//write the snap
		writeSnap();

		//set the local variable array back to the correct shade of gray
		f.localVariableArray.setColor(index, f._colorLocalVariableArray[index]);
		f.localVariableArray.setColor(index+1, f._colorLocalVariableArray[index+1]);
	}
    /**
	 * Store a double in the array located in the heap
     *
     * @param       x           The Double to be stored
     */
	public void storeArrayDouble(Double x) throws IOException,JDOMException {
		String colorAtIndex = Driver.lightGray;
		//value
		double val = x;
		//get the index
		int index = popInteger();
		index = index * 2;
		//get the array reference
		String refArray = popString(); 
		if(refArray.equals("Arr 1")) {
			//store the value in the virtual array in heap
			Bytecode_newarray.double_array[index] = val;
			//get color at index
			colorAtIndex = Driver.array.getColor(index);
			//set the value in the visual array in heap
			Driver.array.set(String.valueOf(x), index, Driver.CURRENT_HIGHLIGHT_COLOR);
			Driver.array.set("", index+1, Driver.CURRENT_HIGHLIGHT_COLOR);
		}
		//write the snapshot
		writeSnap();

		//update the shades of gray
		Driver.array.setColor(index, colorAtIndex);
		Driver.array.setColor(index+1, colorAtIndex);
	}

    /**
	 * Load a Double to the operand stack
     *
     * @param       x           The double to be loaded
     */
	public void loadDouble(Double x) throws IOException,JDOMException {
		//push a double on to the virtual stack
		f._stack.push("");
		f._stack.push(x);

		//set the double in the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set("", --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.set(x, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		writeSnap();

		//update the coloring of the visual stack
		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stack.setColor(f.currentStackHeight+1, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stack.setColor(f.currentStackHeight+1, Driver.lightGray);
			f.stackColor = true;
		}
	}
    /**
	 * Load a Double from array onto the operand stack
     *
     * @param       x           The double to be loaded
     */
	public void loadArrayDouble(Double x) throws IOException,JDOMException {
		//push a double on to the virtual stack
		f._stack.push("");
		f._stack.push(x);

		//set the double in the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set("", --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.set(x, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		writeSnap();

		//update the coloring of the visual stack
		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stack.setColor(f.currentStackHeight+1, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stack.setColor(f.currentStackHeight+1, Driver.lightGray);
			f.stackColor = true;
		}
	}

    //---------------------- Long Methods ------------------------------------
    /**
	 * Push a long on the operand stack
     *
     * @param       l           The long to be pushed
     */
	public void pushLong(long l) throws IOException,JDOMException {
		//push a long on the virtual stack
		f._stack.push("");
		f._stack.push(l);

		//set a long on the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));

		f.stack.set("", --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.set(l, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		if(opcode.contains("return"))
			writeSnapReturn();
		else
			writeSnap();

		//update the coloring of the visual stack
		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stack.setColor(f.currentStackHeight+1, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stack.setColor(f.currentStackHeight+1, Driver.lightGray);
			f.stackColor = true;
		}		
	}

    /**
	 * Pop a long from the operand stack
     *
     * @return           The long popped off the operand stack
     */
	public Long popLong() {
		//pop a long off the virtual stack
		Long temp;
		temp = (Long) f._stack.pop();
		f._stack.pop();

		//set the visual stack to blank values
		f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set("", f.currentStackHeight++);			
		f.stack.set("", f.currentStackHeight++);

		//update the colors on the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("Top", (f.currentStackHeight));

		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
			f.stack.setColor(f.currentStackHeight-2, Driver.lightGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
			f.stack.setColor(f.currentStackHeight-2, Driver.lightGray);
			f.stackColor = true;
		}

		//return the long
		return temp;
	}

    /**
	 * Store a long in the local variable array
     *
     * @param       x           The long to be stored
     */
	public void storeLong(Long x) throws IOException,JDOMException {
		//get the index
		int index = Integer.parseInt(arguments.get(0));

		//store the long in the virtual local variable array
		f._localVariableArray[index] = String.valueOf(x);
		f._localVariableArray[index+1] = "";

		//set the long in the visual local variable array
		f.localVariableArray.set(String.valueOf(x), index, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.localVariableArray.set("", index+1, Driver.CURRENT_HIGHLIGHT_COLOR);

		//write the snapshot
		writeSnap();

		//update the shades of gray
		f.localVariableArray.setColor(index, f._colorLocalVariableArray[index]);
		f.localVariableArray.setColor(index+1, f._colorLocalVariableArray[index+1]);
	}
    /**
	 * Store a long in the array located in the heap
     *
     * @param       x           The Long to be stored
     */
	public void storeArrayLong(Long x) throws IOException,JDOMException {
		String colorAtIndex = Driver.lightGray;
		//value
		long val = x;
		//get the index
		int index = popInteger();
		index = index * 2;
		//get the array reference
		String refArray = popString(); 
		if(refArray.equals("Arr 1")) {
			//store the value in the virtual array in heap
			Bytecode_newarray.long_array[index] = val;
			//get color at index
			colorAtIndex = Driver.array.getColor(index);
			//set the value in the visual array in heap
			Driver.array.set(String.valueOf(x), index, Driver.CURRENT_HIGHLIGHT_COLOR);
			Driver.array.set("", index+1, Driver.CURRENT_HIGHLIGHT_COLOR);
		}
		//write the snapshot
		writeSnap();

		//update the shades of gray
		Driver.array.setColor(index, colorAtIndex);
		Driver.array.setColor(index+1, colorAtIndex);
	}

    /**
	 * Load a long to the operand stack
     *
     * @param       x           The long to be loaded
     */
	public void loadLong(Long x) throws IOException,JDOMException {
		//push the long on the virtual stack
		f._stack.push("");
		f._stack.push(x);

		//set the long in the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set("", --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.set(x, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		writeSnap();

		//update the colors of the visual stack
		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stack.setColor(f.currentStackHeight+1, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stack.setColor(f.currentStackHeight+1, Driver.lightGray);
			f.stackColor = true;
		}
	}
    /**
	 * Load a long from array onto the operand stack
     *
     * @param       x           The long to be loaded
     */
	public void loadArrayLong(Long x) throws IOException,JDOMException {
		//push the long on the virtual stack
		f._stack.push("");
		f._stack.push(x);

		//set the long in the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set("", --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.set(x, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		writeSnap();

		//update the colors of the visual stack
		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stack.setColor(f.currentStackHeight+1, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stack.setColor(f.currentStackHeight+1, Driver.lightGray);
			f.stackColor = true;
		}
	}

    //---------------------- Integer Methods ----------------------------------
    /**
	 * Push an int on the operand stack
     *
     * @param       i           The int to be pushed
     */
	public void pushInteger(int i) throws IOException,JDOMException {
		//push the int on the virtual stack
		f._stack.push(i);

		//set the int on the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));

		f.stack.set(i, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		if(opcode.contains("return"))
			writeSnapReturn();
		else
			writeSnap();

		//update the colors on the visual stack
		if(f.stackColor){
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stackColor = true;
		}
	}

    /**
	 * Pop an int from the operand stack
     *
     * @return           The Integer popped off the operand stack
     */
	public Integer popInteger() {
		//pop an int off of the virtual stack
		Integer temp = (Integer) f._stack.pop();

		//set the visual stack to blank values
		f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set("", f.currentStackHeight++);

		//set the colors of the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("Top", (f.currentStackHeight));

		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
			f.stackColor = true;
		}

		//return the int
		return temp;
	}

    /**
	 * Store an int in the local variable array
     *
     * @param       x           The Integer to be stored
     */
	public void storeInteger(Integer x) throws IOException,JDOMException {
		//get the index
		int index = Integer.parseInt(arguments.get(0));

		//store the value in the virtual local variable array
		f._localVariableArray[index] = String.valueOf(x);

		//set the value in the visual local variable array
		f.localVariableArray.set(String.valueOf(x), index, Driver.CURRENT_HIGHLIGHT_COLOR);

		//write the snapshot
		writeSnap();

		//update the shades of gray
		f.localVariableArray.setColor(index, f._colorLocalVariableArray[index]);
	}
    /**
	 * Store an int in the array located in the heap
     *
     * @param       x           The Integer to be stored
     */
	public void storeArrayInteger(Integer x) throws IOException,JDOMException {
		String colorAtIndex = Driver.lightGray;
		//value
		int val = x;
		//get the index
		int index = popInteger();
		//get the array reference
		String refArray = popString(); 
		if(refArray.equals("Arr 1")) {
			//store the value in the virtual array in heap
			Bytecode_newarray.int_array[index] = val;
			//get color at index
			colorAtIndex = Driver.array.getColor(index);
			//set the value in the visual array in heap
			Driver.array.set(String.valueOf(x), index, Driver.CURRENT_HIGHLIGHT_COLOR);
		}
		//write the snapshot
		writeSnap();

		//update the shades of gray
		Driver.array.setColor(index, colorAtIndex);
	}

    /**
	 * Load an int to the operand stack
     *
     * @param       x           The int to be loaded
     */
	public void loadInteger(Integer x) throws IOException,JDOMException {
		//push the int on the virtual stack
		f._stack.push(x);

		//set the int on the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set(x, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		writeSnap();

		//update the colors of the visual stack
		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stackColor = true;
		}
	}
    /**
	 * Load an int from array onto the operand stack
     *
     * @param       x           The int to be loaded
     */
	public void loadArrayInteger(Integer x) throws IOException,JDOMException {
		//push the int on the virtual stack
		f._stack.push(x);

		//set the int on the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set(x, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		writeSnap();

		//update the colors of the visual stack
		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stackColor = true;
		}
	}

    //---------------------- Float Methods -----------------------------------
    /**
	 * Push a float on the operand stack
     *
     * @param       f           The float to be pushed
     */
	public void pushFloat(float fl) throws IOException,JDOMException {
		//push the float on the virtual stack
		f._stack.push(fl);
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));

		//set the float on the visual stack
		f.stack.set(fl, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		if(opcode.contains("return"))
			writeSnapReturn();
		else
			writeSnap();

		//update the colors on the visual stack
		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stackColor = true;
		}	
	}

    /**
	 * Pop a float from the operand stack
     *
     * @return           The float popped off the operand stack
     */
	public Float popFloat() {
		//pop the float off of the virtual stack
		Float temp = (Float) f._stack.pop();

		//set the visual stack to blank values
		f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set("", f.currentStackHeight++);

		//update the colors of the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("Top", (f.currentStackHeight));

		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
			f.stackColor = true;
		}

		//return the float
		return temp;
	}

    /**
	 * Store a float in the local variable array
     *
     * @param       x           The float to be stored
     */
	public void storeFloat(Float x) throws IOException,JDOMException {
		//get the index
		int index = Integer.parseInt(arguments.get(0));

		//store the value in the virtual local variable array
		f._localVariableArray[index] = String.valueOf(x);

		//set the value in the visual local variable array
		f.localVariableArray.set(String.valueOf(x), index, Driver.CURRENT_HIGHLIGHT_COLOR);
		
		//write the snapshot
		writeSnap();

		//update the colors of the local variable array
		f.localVariableArray.setColor(index, f._colorLocalVariableArray[index]);
	}
    /**
	 * Store a float in the array located in the heap
     *
     * @param       x           The Float to be stored
     */
	public void storeArrayFloat(Float x) throws IOException,JDOMException {
		String colorAtIndex = Driver.lightGray;
		//value
		float val = x;
		//get the index
		int index = popInteger();
		//get the array reference
		String refArray = popString(); 
		if(refArray.equals("Arr 1")) {
			//store the value in the virtual array in heap
			Bytecode_newarray.float_array[index] = val;
			//get color at index
			colorAtIndex = Driver.array.getColor(index);
			//set the value in the visual array in heap
			Driver.array.set(String.valueOf(x), index, Driver.CURRENT_HIGHLIGHT_COLOR);
		}
		//write the snapshot
		writeSnap();

		//update the shades of gray
		Driver.array.setColor(index, colorAtIndex);
	}

    /**
	 * Load a float to the operand stack
     *
     * @param       x           The float to be loaded
     */
	public void loadFloat(Float x) throws IOException,JDOMException {
		//push the float on the virtual stack
		f._stack.push(x);

		//set the float on the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set(x, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		writeSnap();

		//update the coloring of the visual stack
		if(f.stackColor){
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stackColor = true;
		}
	}
    /**
	 * Load a float from array onto the operand stack
     *
     * @param       x           The float to be loaded
     */
	public void loadArrayFloat(Float x) throws IOException,JDOMException {
		//push the float on the virtual stack
		f._stack.push(x);

		//set the float on the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set(x, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		writeSnap();

		//update the coloring of the visual stack
		if(f.stackColor){
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stackColor = true;
		}
	}
    //---------------------- String Methods -----------------------------------
    /**
	 * Push a string on the operand stack
     *
     * @param       f           The string to be pushed
     */
	public void pushString(String str) throws IOException,JDOMException {
		//push the string on the virtual stack
		f._stack.push(str);
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));

		//set the string on the visual stack
		f.stack.set(str, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		if(opcode.contains("return"))
			writeSnapReturn();
		else
			writeSnap();

		//update the colors on the visual stack
		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stackColor = true;
		}	
	}

    /**
	 * Pop a float from the operand stack
     *
     * @return           The float popped off the operand stack
     */
	public String popString() {
		//pop the string off of the virtual stack
		String temp = (String) f._stack.pop();

		//set the visual stack to blank values
		f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set("", f.currentStackHeight++);

		//update the colors of the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("Top", (f.currentStackHeight));

		if(f.stackColor) {
			f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight-1, Driver.lightGray);
			f.stackColor = true;
		}

		//return the float
		return temp;
	}

    /**
	 * Store a string in the local variable array
     *
     * @param       x           The string to be stored
     */
	public void storeString(String x) throws IOException,JDOMException {
		//get the index
		int index = Integer.parseInt(arguments.get(0));

		//store the value in the virtual local variable array
		f._localVariableArray[index] = String.valueOf(x);

		//set the value in the visual local variable array
		f.localVariableArray.set(String.valueOf(x), index, Driver.CURRENT_HIGHLIGHT_COLOR);
		
		//write the snapshot
		writeSnap();

		//update the colors of the local variable array
		f.localVariableArray.setColor(index, f._colorLocalVariableArray[index]);
	}

    /**
	 * Load a string to the operand stack
     *
     * @param       x           The string to be loaded
     */
	public void loadString(String x) throws IOException,JDOMException {
		//push the string on the virtual stack
		f._stack.push(x);

		//set the string on the visual stack
		if(f.currentStackHeight == f.stackSize)
		;
		else
			f.stack.setRowLabel("", (f.currentStackHeight));
		f.stack.set(x, --f.currentStackHeight, Driver.CURRENT_HIGHLIGHT_COLOR);
		f.stack.setRowLabel("Top", (f.currentStackHeight));

		//write the snapshot
		writeSnap();

		//update the coloring of the visual stack
		if(f.stackColor){
			f.stack.setColor(f.currentStackHeight, Driver.darkGray);
			f.stackColor = false;
		}		
		else {
			f.stack.setColor(f.currentStackHeight, Driver.lightGray);
			f.stackColor = true;
		}
	}

    //---------------------- Parse Methods -----------------------------------
	/*
	 * Splices out the elements needed within a byte code string
	 *
	 * @param	s	The string to be parsed
	 */
	public void parse(String s) {
		//remove the line number for display purposes
		entireOpcode = s;
		String[] s2 = s.split(":");
		entireOpcode = s2[1];	

		//does the opcode contain "_"?
		if(s.contains("_"))
			underscore = "_";
		else
			underscore = " ";

		//split the input string according two whether or not it has a comment
		String[] split;
		if(s.contains(";")) { //it has a comment 

			//split out the "//" symbol
			//split[0] has the front half of the string
			//split[1] has the back half of the string, the comment
			split = s.split("//");

			//split the front half of the string into an array of components
			String[] front = split[0].split("( |\\t|:|,|_|;)+");

			//get the line number
			lineNumber = Integer.parseInt(front[0]);

			//get the opcode
			opcode = front[1];

			//add any arguments to the arguments array
			for(int i = 2; i < front.length; i++)
				arguments.add(front[i]);

			//split the back half of the string into an array of components
			String[] back = split[1].split("( |\\t|:|,|;|\\))+");

			//get the type of object referred to
			objectType = back[0];

			//get the "path"
			//this can be an object type, a method call, or even a primitive type
			//if it is a long, we have to take out the "l"
			path = back[1];
			if(objectType.equals("long"))
			{
				System.out.println("cait");
				path = path.substring(0,path.length()-1);
				System.out.println("path in if: " + path);
			}
			System.out.println("path: " + path);

			//get the parameters for a method call
			if(back.length > 2) {
				String combinedParams = back[2].substring(1, back[2].length());
				char[] charArray = combinedParams.toCharArray();
				parameters = new String[charArray.length];
				for(int i = 0; i < charArray.length; i++)
					parameters[i] = Character.toString(charArray[i]);
			}
			else
				;

			//get the return type for a method call
			if(back.length > 3)
				returnType = back[3];
			else
				;
		}
		else {  //it doesn't have a comment

			//split the front half of the string into an array of components
			split = s.split("( |\\t|:|,|_)+");

			//get the line number
			lineNumber = Integer.parseInt(split[0]);

			//get the opcode
			opcode = split[1];

			//add any arguments to the arguments array			
			for(int i = 2; i < split.length; i++)
				arguments.add(split[i]);
		}

		//get the next line number
		next = lineNumber + 1;

		//set entireOpcode to the value to be displayed in the Pseudocode window
		if(entireOpcode.contains("#")) {
			String[] x = entireOpcode.split("#");
			String[] backHalf = split[1].split(" ");
			entireOpcode = x[0];
			for(int i = 1; i < backHalf.length; i++)
				entireOpcode = entireOpcode + backHalf[i];
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * Current line number being executed within the byte code
	 */
	public int getLineNumber() {
		return this.lineNumber;
	}

        public int getLocalVariableTable(String parameter) {
                int index = 0;
                for(int i = 0; i < Driver.classes[0].methods.get(Driver.currentMethod).localVariableTable.length; i++) {
                        if(Driver.classes[0].methods.get(Driver.currentMethod).localVariableTable[i][0].compareTo(parameter) == 0) {
                                index = i;
                                break;
                        }                       
                }
                return index;
        }


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return opcode + " " + arguments + " " + lineNumber;
	}
}
