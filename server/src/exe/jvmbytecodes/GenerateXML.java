package exe.jvmbytecodes;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GenerateXML {
	/*
	 * generate the byte code XML file that is used to display bytecode in the
	 * visualizer.
	 */
	static void generateBytecodeXML() throws IOException {

		for (int i = 0; i < Driver.classes.length; i++) {
			for (int j = 0; j < Driver.classes[i].methods.size(); j++) {

				String bytecodeStr = "";
				bytecodeStr += "<?xml version=\"1.0\"?>\n";
				bytecodeStr += "<pseudocode>\n";
				bytecodeStr += "\t<stack>method: <replace var=\"method_call_stack\" /></stack>\n";
				bytecodeStr += "\t<code>\n";
				bytecodeStr += "\t<signature>Java Byte Code</signature>\n";

				// for (int k = j; k < Driver.classes[i].methods.size(); k++) {
				for (int l = 0; l < Driver.classes[i].methods.get(j).bytecodes.length; l++) {
					bytecodeStr += "\t<line num=\""
							+ Driver.classes[i].methods.get(j).bytecodes[l].lineNumber
							+ "\">"
							+ Driver.classes[i].methods.get(j).bytecodes[l].entireOpcode;
					bytecodeStr += "</line>\n";
				}
				// }

				bytecodeStr += "\t</code>\n";
				bytecodeStr += "\t<vars>\n";
				bytecodeStr += "\t<var>heap size: <replace var=\"heap_size\" /></var>\n";
				bytecodeStr += "\t<var>stack size: <replace var=\"stack_size\" /></var>\n";
				bytecodeStr += "\t</vars>\n";
				bytecodeStr += "</pseudocode>\n";

				//make the signature
				String signature="";
				for (int m=0; m<Driver.classes[i].methods.get(j).localVariableTable.length; m++)
					signature+=Driver.classes[i].methods.get(j).localVariableTable[m][2];
				signature = replaceSlashWithDot(signature);
				System.out.println("sinature is: "+signature);
				
				String XML = Driver.path + "/"
						+ Driver.classes[i].name
						+ Driver.classes[i].methods.get(j).name+signature
						+ ".xml";
				System.out.println("generating: " + XML);
				FileOutputStream out = new FileOutputStream(XML);
				out.write(bytecodeStr.getBytes());
				out.flush();
				out.close();
				System.out.println("finished generating XML");
			}
		}
	}

	/*
	 * Replaces and slash in the String with a '.'
	 */
	static String replaceSlashWithDot(String temp) {
		String returnStr = "";

		for (int i = 0; i < temp.length(); i++) {
			if (temp.charAt(i) == '/')
				returnStr += ".";
			else
				returnStr += temp.charAt(i);
		}
		return returnStr;
	}
	
	/*
	 * generate the Java XML file that is used to display java code in the
	 * visualizer.
	 */
	static void generateSourceCodeXML(String pathName, String file)
			throws IOException {
		
		for (int i = 0; i < Driver.classes.length; i++) {
			
			String bytecodeStr = "";
			bytecodeStr += "<?xml version=\"1.0\"?>\n";
			bytecodeStr += "<pseudocode>\n";
			bytecodeStr += "\t<stack>method: <replace var=\"method_call_stack\" /></stack>\n";
			bytecodeStr += "\t<code>\n";
			bytecodeStr += "\t<signature>Java Code</signature>\n";

			String path = pathName;
			String fileName = file;
			Process cat = null;
			
			System.out.println("generating XML for class: "+Driver.classes[i].name);

			Driver.file_contents = insertEscapeChar(Driver.file_contents);
			String fileContents[] = Driver.file_contents.split("[\n]");
			
			//Replace spaces at the beginning of the line with tabs.
			for (int k = 0; k < fileContents.length; k++) {
				int j=0;
				String javaTempStr = fileContents[k];
				while (fileContents[k].length() > j && (fileContents[k].charAt(j) == '\t' || fileContents[k].charAt(j) == ' '))
				{
					javaTempStr = javaTempStr.substring(1);
					j++;
				}
				
				if (k<10)
					bytecodeStr += "\t<line num=\"" + k + "\" indent=\""+j+"\" >" + " "+javaTempStr + "</line>\n";
				else
					bytecodeStr += "\t<line num=\"" + k + "\" indent=\""+j+"\" >" + javaTempStr + "</line>\n";
			}

			bytecodeStr += "\t</code>\n";
			bytecodeStr += "\t<vars>\n";
			bytecodeStr += "\t<var>heap size: <replace var=\"heap_size\" /></var>\n";
			bytecodeStr += "\t<var>stack size: <replace var=\"stack_size\" /></var>\n";
			bytecodeStr += "\t</vars>\n";
			bytecodeStr += "</pseudocode>\n";
			
			String XML = Driver.path + "/" + Driver.classes[i].name + ".xml";
			FileOutputStream out = new FileOutputStream(XML);
			out.write(bytecodeStr.getBytes());
			out.flush();
			out.close();

		}
	}

	/*
	 * When a character is found in the XML file that will generate an error,
	 * the character is replaced by the HTML code
	 */
	static String insertEscapeChar(String temp) {
		String returnStr = "";

		for (int i = 0; i < temp.length(); i++) {
			if (temp.charAt(i) == '<')
				returnStr += "&#60;";
			else if (temp.charAt(i) == '>')
				returnStr += "&#62;";
			else if (temp.charAt(i) == '&')
				returnStr += "&amp;amp;";
			else if (temp.charAt(i) == '|')
				returnStr += "&#124;";
			else if (temp.charAt(i) == '^')
				returnStr += "&#94;";
			else
				returnStr += temp.charAt(i);
		}
		return returnStr;
	}

}
