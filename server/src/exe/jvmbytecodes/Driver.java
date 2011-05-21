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
import java.io.*;
import java.util.*;
import java.net.*;
import org.jdom.*;
import exe.*;
import exe.pseudocode.*;
/*
* <p><code>Driver</code> drives the execution of the JVM simulator
* Use the <code>main</code> method to execute all operations.
*
* @author William Clements
* @author David Furcy
* @author Caitlyn Pickens
* @author Cory Sheeley
* @version 7/11/2010
*/
public class Driver {
  //---------------------- Instance Variables -------------------------------------
  /**
    * The title
  */
    static final String TITLE = null; // no title
  /**
    * An array of Pseudo Code display tab information
  */
    static ArrayList<PseudoCodeDisplay> pseudoBytecodes[];//pseudo;]

  /**
    * An array of Info tab display information
  */
    static PseudoCodeDisplay pseudoSourceCode[];//realCode;

  /**
    * A boolean counter
  */
    static boolean success;

  /**
    * The show file where JHAVE snapshot are stored
  */
    static ShowFile show;

  /**
    * The visual runtime stack
  */
    static GAIGSnewStack runTimeStack;
  /**
    * The virtual runtime stack
  */
    static Stack _runTimeStack = new Stack();
  /**
    * The visual array
  */
    static GAIGSnewArray array;
  /**
    * The virtual heap
  */
    static char[] _heap = new char[26];
  /**
    * Current class counter
  */
    static int currentClass;
  /**
    * The number of lines in the input java file
  */
    static int numberOfLinesInJavaFile = 1;
  /**
    * The number of arrays in the java file
  */
    static int numberOfArrays = 0;
  /**
    * The type of array
  */
    static String arrayType = "";
  /**
    * The size of the heap
  */
    static int heapSize = 0;
  /**
    * Current method counter
  */
    static int currentMethod = 1;
  /**
    * An array of Class_ objects
  */
    static Class_[] classes;
  /**
    * The color used to highlight in a snapshot
  */
	static String CURRENT_HIGHLIGHT_COLOR = "#CCFFCC";
  /**
    * The color used for standard gray background
  */
	static String standardGray = "#EEEEEE";
  /**
    * The color used for light gray
  */
	static String lightGray = "#BBBBBB";
  /**
    * The color used for dark gray
  */
	static String darkGray = "#888888";
  /**
    * XMLstack used in XML classes
  */
	static GAIGSarray XMLstack;
  /**
    * XMLstack size used in XML classes
  */
	static int XMLstackSize = 0;
  /**
    * Array of colors for the visual runtime stack
  */
	static String[] runTimeStackColors = new String[3];
  /**
    * The input file contents
  */
	static String file_contents = "";
	static String path;

	/*
	 * Main driver for the client
	 * 
	 * args[0] is the full path with the number of the uid folder at the end
	 * args[1] is the name of the class in the source code file. example: Foo
	 * args[2] is the string containing the whole source code. It contains
	 * 		"contents are not in here" if the example is a hardedcoded file that resides on the server.
	 */
	public static void main(String args[]) throws IOException,
			InvalidClassFileException, InterruptedException, JDOMException {

		String fileName = null;
		File pathname = new File("", args[0]);
		show = new ShowFile(args[0] + ".sho", 5);
		path = args[0];

		try {
			pathname.mkdir();
		
			//hard coded file is loaded 
			if (args[2].equals("contents are not in here")) {

				fileName = args[1] + ".java";
				Process cp = null;
				cp= Runtime.getRuntime().exec("cp ../../src/exe/jvmbytecodes/Builtin_Programs/"
								+ args[1] + " " + args[0] + "/" + fileName);
				int cpStatus =0;
				cpStatus = cp.waitFor();
					System.out.println("Finished with status: "+cpStatus);

				// grab the file contents
				
				FileReader fr = new FileReader(args[0] + "/" + fileName);
				BufferedReader br = new BufferedReader(fr);
				String line;
				while ((line = br.readLine()) != null) 
		        	file_contents += line+"\n";

			} else { // file is a user file and its content is in args[2]
				file_contents = args[2];

				fileName = args[1];

				// try {
				FileWriter fw = new FileWriter(args[0] + "/" + fileName);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter outFile = new PrintWriter(bw);
				outFile.print(file_contents);
				outFile.close();
				// } catch (Exception e) {
				// System.err.println("Error: " + e.getMessage() );
				// }
			} // done writing .java file

			String[] tmp = { args[0], fileName };

			classes = GenerateBytecodes.getClasses(tmp);

			// create visual stack and heap using the predetermined sizes
			array = new GAIGSnewArray(0, "", "#999999", -.05, 0.6, 0.05, 0.9, .5);
			runTimeStack = new GAIGSnewStack("Runtime Stack", "#999999", -1, 0.1, .05, 0.5);

			// set current method and class
			currentClass = 0;
			currentMethod = 0;
			int index = 0;

			// make the XML files
			GenerateXML.generateBytecodeXML();
			GenerateXML.generateSourceCodeXML(args[0], fileName);

			pseudoBytecodes = new ArrayList[Driver.classes.length];
			for (int i = 0; i < Driver.classes.length; i++)
				pseudoBytecodes[i] = new ArrayList<PseudoCodeDisplay>();
			pseudoSourceCode = new PseudoCodeDisplay[Driver.classes.length];

			// make the URI need for the display
			// try {

			System.out.println("starting uri ");

			for (int i = 0; i < Driver.classes.length; i++) {
				currentMethod = 0;
				for (int j = 0; j < Driver.classes[i].methods.size(); j++) {
					String signature = "";
					for (int m = 0; m < Driver.classes[i].methods.get(j).localVariableTable.length; m++)
						signature += Driver.classes[i].methods.get(j).localVariableTable[m][2];
					signature = GenerateXML.replaceSlashWithDot(signature);
					System.out.println("sinature is: " + signature + " i: " + i
							+ " j: " + j + " " + args[0] + "/"
							+ Driver.classes[i].name
							+ Driver.classes[i].methods.get(j).name + signature
							+ ".xml");
					pseudoBytecodes[i].add(new PseudoCodeDisplay(
							args[0] + "/" + Driver.classes[i].name
									+ Driver.classes[i].methods.get(j).name
									+ signature + ".xml"));
					System.out.println("found file");
					currentMethod++;
				}
				currentClass++;
			}
			currentClass = 0;
			currentMethod = 0;
			System.out.println("completed uri ");

			for (int i = 0; i < Driver.classes.length; i++) {
				pseudoSourceCode[i] = (new PseudoCodeDisplay(
						args[0] + "/" + Driver.classes[i].name + ".xml"));
				currentClass++;
			}
			currentClass = 0;
			currentMethod = -1;
			// } catch (JDOMException e) {
			// e.printStackTrace();
			// }

			//find the main method as an entry point
			for (Method_ m : classes[0].methods) {
				if (m.name.equals("main")) {
					currentMethod = index;
					break;
				}
				index++;
			}

			//if there is no main method, throw an error
			if(currentMethod == -1)
				throw new InvalidClassFileException("The input class must have a main method with the following signature: \n"+"public static void main(String[] args)\n" + "Please check your java source code and make sure the main method exists.");

			//set the visual runtime stack colors
			//these colors will be cycled through when a new frame is added
			runTimeStackColors[0] = "#a7bbff";
			runTimeStackColors[1] = "#dbf1ff";
			runTimeStackColors[2] = "#ffffaa";

			Frame_ f = new Frame_(currentMethod);	//make a new frame
			_runTimeStack.push(f); //push the frame

			//write opening snapshots
			show.writeSnap(TITLE, MakeURI.doc_uri(-1, f), MakeURI.make_uri(-1,
					PseudoCodeDisplay.RED, f), runTimeStack);
			runTimeStack.push(classes[0].methods.get(currentMethod).name,
					f.CURRENT_FRAME_COLOR);
			show.writeSnap(TITLE, MakeURI.doc_uri(-1, f), MakeURI.make_uri(-1,
					PseudoCodeDisplay.RED, f), runTimeStack);

			// begin interpreter
			Interpreter.interpret();
		} catch (Exception e) {
			GAIGStext errorText = new GAIGStext(0.5, 0.5, "" + e);
			show.writeSnap(Driver.TITLE, errorText);
		}

		show.close(); //close the show file
		
		//remove the directory b/c you are done with it
			File dirObj = new File(args[0]);
			if (dirObj.isDirectory()) { // dir exists
				String[] files = dirObj.list();
				
				//remove the files in the dir
				if (files.length > 0) { 
					for (int i=0; i<files.length; i++) {
						File fileObj = new File(args[0]+"/"+files[i]);
						fileObj.delete();
					}
				}
				
				//finally delete the folder
				dirObj.delete();
			}
	}

	/*
	 * Generates a random string for a hex color in the format: "#000000"
	 */
	static String getRandomColor() {
		String returnStr = "#";
		int temp = 0;
		for (int i = 0; i < 6; i++) {
			Random rand = new Random();
			temp = rand.nextInt(7);
			if (temp == 10)
				returnStr += "a";
			else if (temp == 11)
				returnStr += "b";
			else if (temp == 12)
				returnStr += "c";
			else if (temp == 13)
				returnStr += "d";
			else if (temp == 14)
				returnStr += "e";
			else if (temp == 15)
				returnStr += "f";
			else
				returnStr += (Integer.toString(temp));
		}
		return (returnStr);
	}
}
