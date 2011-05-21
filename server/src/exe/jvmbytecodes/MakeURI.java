package exe.jvmbytecodes;
import java.util.ArrayList;
import java.util.HashMap;
import exe.pseudocode.*;
import org.jdom.JDOMException;
import java.io.*;
import java.util.*;
import java.net.*;
import org.jdom.*;
import exe.*;
import exe.pseudocode.*;

public class MakeURI {

	static int numSnapshots =0;
	/*
	 * Generate the byte code for the visualization
	 */
	static String make_uri(int line, int color, Frame_ f) throws JDOMException, IOException{
		// System.out.println("highlighting line: "+line);
		return make_uri(new int[] { line }, new int[] { color }, f);
	}

	/*
	 * Generate the byte code for the visualization
	 */
	static String make_uri(int[] lines, int[] colors, Frame_ f) throws JDOMException, IOException {
		numSnapshots++;
	if (numSnapshots > 1000) {
		Driver.show.close();
		Driver.show = new ShowFile(Driver.path + ".sho", 5);
		throw new InvalidClassFileException("Too many snapshots made. \n" + "Your program may be too long or may have an infinite loop.\n" + "Check to see if the source code compiles before resubmitting.");
}
		               
		String sSize = f.stackSize == -1 ? "null" : String.valueOf(f.stackSize);
		String hSize = Driver.heapSize == -1 ? "null" : String.valueOf(Driver.heapSize);
				//print the call stack in the display
		String methodCallStack = printCallStack();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("stack_size", sSize);
		map.put("heap_size", hSize);
		map.put("method_call_stack", methodCallStack);
		String uri = null;

		uri = Driver.pseudoBytecodes[Driver.currentClass].get(Driver.currentMethod).pseudo_uri(
			map, lines, colors);

		return uri;
	}

	static String printCallStack() {
		String returnStr =" ";
		Frame_ tempFrame;

		if (!Driver._runTimeStack.empty())
			returnStr += ((Frame_) Driver._runTimeStack.get(0)).methodName;

		for (int i=1; i<Driver._runTimeStack.size(); i++)
			returnStr += " -> "+((Frame_) Driver._runTimeStack.get(i)).methodName;
			
		return returnStr;
	}

	/*
	 * This method displays the java class
	 */
	static String doc_uri(int line, Frame_ f) throws JDOMException{
		String sSize = f.stackSize == -1 ? "null" : String.valueOf(f.stackSize);
		String hSize = Driver.heapSize == -1 ? "null" : String.valueOf(Driver.heapSize);
		//print the call stack in the display
		String methodCallStack = printCallStack();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("stack_size", sSize);
		map.put("heap_size", hSize);
		map.put("method_call_stack", methodCallStack);
		String uri = null;

		// convert the byte code line number to the java line number
		// System.out.println("\t\t\t\t\t\tLineNumberTable: "
		// +Driver.classes[Driver.currentClass].methods.get(Driver.currentMethod).lineNumberTable.length);
		int[] colors = new int[] { 0 };

		int javaLine = 0;
		ArrayList<String> lineArr = new ArrayList<String>();
		Method_ currentMethod = Driver.classes[Driver.currentClass].methods
				.get(Driver.currentMethod);
		for (int i = 0; i < currentMethod.lineNumberTable.length; i++) {
			if (currentMethod.lineNumberTable[i][1] <= line) {
				javaLine = currentMethod.lineNumberTable[i][0] - 1;
				
			}
		}

		int[] lineToHighlight = new int[] { javaLine };

		//try {
			uri = Driver.pseudoSourceCode[Driver.currentClass].pseudo_uri(
					map, lineToHighlight, colors);
		//} catch (JDOMException e) {
		//	e.printStackTrace();
		//}

		return uri;

	}
}
