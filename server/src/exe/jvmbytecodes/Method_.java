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

/*
 * A representation of a Java method
 * @author David Furcy
 */
class Method_ {
	String name;
	String parameterTypes;
	String returnType;
	String modifiers;
	Bytecode_[] bytecodes;
	int stackSize, numLocals, numArgs;
	int[][] lineNumberTable;
	String[][] localVariableTable;

	String indent;

	/*
	 * Constructor 
	 */
	Method_(String modifiers, String returnType, String name, String parameterTypes, int stackSize, int numLocals,
			int numArgs, Bytecode_[] bytecodes, int[][] lineNumberTable, String[][] localVariableTable, String i) {

		this.modifiers = modifiers;
		this.returnType = returnType;
		this.name = name;
		this.parameterTypes = parameterTypes;
		this.stackSize = stackSize;
		this.numLocals = numLocals;
		this.numArgs = numArgs;
		this.bytecodes = bytecodes;
		this.lineNumberTable = lineNumberTable;
		this.localVariableTable = localVariableTable;
		this.indent = i;
	}

	/*
	 * Creates a formated representation of the Method object
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		if (modifiers != null) {
			s.append(modifiers);
			s.append(" ");
		}
		if (returnType != null) {
			s.append(returnType);
			s.append(" ");
		}
		s.append(name);
		s.append("(");
		s.append(parameterTypes);
		s.append(")\n");
		s.append(indent);
		s.append("stack size = ");
		s.append(stackSize);
		s.append("\n");
		s.append(indent);
		s.append("number of locals = ");
		s.append(numLocals);
		s.append("\n");
		s.append(indent);
		s.append("number of arguments = ");
		s.append(numArgs);
		s.append("\n");
		for (Bytecode_ bc : bytecodes) {
			s.append(indent);
			s.append(bc);
			s.append("\n");
		}
		s.append(indent);
		s.append("LineNumberTable:\n");
		for (int i = 0; i < lineNumberTable.length; i++) {
			s.append(indent);
			s.append(lineNumberTable[i][0]);
			s.append("\t");
			s.append(lineNumberTable[i][1]);
			s.append("\n");
		}
		s.append(indent);
		s.append("LocalVariableTable:\n");
		for (int i = 0; i < localVariableTable.length; i++) {
			s.append(indent);
			s.append(localVariableTable[i][0]);
			s.append("\t");
			s.append(localVariableTable[i][1]);
			s.append("\t");
			s.append(localVariableTable[i][2]);
			s.append("\n");
		}
		return s.toString();
	}

}// Method_ class

