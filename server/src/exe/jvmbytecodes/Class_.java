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
 * A representation of a Java class
 * @author David Furcy
 */
class Class_ {
	private String modifiers;
	private String packageName;
	String name;
	private String superClassName;
	private String sourceFileName;
	private ArrayList<Field_> fields;
	ArrayList<Method_> methods;
	public final String indent = "   ";

	/*
	 * Constructor
	 */
	Class_(String modifiers, String packageName, String name, String superClassName) {
		this.modifiers = modifiers;
		this.packageName = packageName;
		this.name = name;
		this.superClassName = superClassName;
		fields = new ArrayList<Field_>();
		methods = new ArrayList<Method_>();
	}

	/*
	 * sets the source file
	 */
	public void setSourceFileName(String fileName) {
		sourceFileName = fileName;
	}

	/*
	 * Adds a field.
	 */
	public void addField(Field_ f) {
		fields.add(f);
	}

	/*	
	 * Adds a method.
	 */
	public void addMethod(Method_ m) {
		methods.add(m);
	}

	/*
	 * Formated representation.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer s = new StringBuffer("// Source file: " + sourceFileName + "\n");
		s.append("class ");
		if (packageName != null)
			s.append(packageName + ".");
		s.append(name);
		s.append(" extends ");
		s.append(superClassName);
		s.append(" {\n");

		for (Field_ f : fields) {
			s.append(indent);
			s.append(f);
			s.append("\n");
		}
		s.append("\n");

		for (Method_ m : methods) {
			s.append(m.toString());
			s.append("\n");
		}
		s.append("}\n");

		return s.toString();
	}

}// Class_ class
