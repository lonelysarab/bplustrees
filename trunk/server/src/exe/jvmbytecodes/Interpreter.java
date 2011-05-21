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
import org.jdom.JDOMException;

/*
 * Interprets the byte code that resides within the *.class file
* @author Caitlyn Pickens
* @author Cory Sheeley
 */
public class Interpreter {

	/*
	 * Cycles through bytecode executions
	 */
	static public void interpret() throws IOException,JDOMException {	
		Bytecode_ bc = Driver.classes[0].methods.get(Driver.currentMethod).bytecodes[0]; //get the very first bytecode
		bc.execute(); //execute the first one
		System.out.println(bc);
		while (bc.next != -1) { //loop through bytecodes
			Bytecode_[] b = Driver.classes[0].methods.get(Driver.currentMethod).bytecodes;
			for (Bytecode_ x : b) {
				if (x.getLineNumber() == bc.next) { //find the correct bytecode
					System.out.println(x);
					x.execute(); //execute that bytecode
					bc = x;
				} else
					;
			}
		}
	}
}
