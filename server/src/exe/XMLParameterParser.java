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

package exe;

import java.util.*;
import java.lang.*;
import java.io.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

/**
 * This class contains static methods designed to parse the user's inputs from
 * an XML input generator file. Consult the individual method descriptions to
 * to see how each method stores the user's inputs.
 *
 * @author Andrew Jungwirth
 */
public class XMLParameterParser{
    /**
     * Parses the user's input into a an array of <code>String</code>s. The 
     * user's inputs are added to the array in the same order in which they
     * appear in the XML input generator file starting at the index specified
     * by <code>start</code>. The array indices before <code>start</code> are
     * filled with empty <code>String</code> values so that the calling program
     * can fill them with other parameters that it sends to the 
     * script-producing program.
     *
     * @param inputXML     A <code>String</code> indicating the file from which
     *                     the XML input generator should be read. Note that
     *                     this should be the same <code>String</code> that the
     *                     server places in argument 2 when running the program
     *                     to start generating a showfile.
     * @param start        The index in the array in which the first user input
     *                     should be placed. All array elements before this 
     *                     position are filled with empty <code>String</code>
     *                     values.
     * @return             Gives an array of <code>String</code> objects 
     *                     containing the user's inputs to the input generator
     *                     in the same order as the input fields appear in the
     *                     XML input generator file. The array elements less
     *                     than the value passed to <code>start</code> contain
     *                     empty <code>String</code>s to allow the calling
     *                     program to insert additional parameters.
     * @throws IOException Indicates a problem in processing the XML file. In
     *                     theory, this should never happen because the file
     *                     has already been parsed by the client before it 
     *                     reaches the server.
     */
    public static String[] parseToArray(String inputXML, 
					int start) throws IOException {
	// Get a Document representation of the temporary server file.
	Document doc = makeDoc(inputXML);

	Element elem = doc.getRootElement();
	// Each child corresponds to one of the input generator fields.
	Iterator input_fields = elem.getChildren().iterator();

	Vector array = new Vector();

	// Leave the specified number of null elements at the front of the 
	// array so the user program can insert the needed parameters.
	for(int i = 0; i < start; i++){
	    array.add("");
	}

	// Loop through the input generator fields to hash the user's inputs.
	while(input_fields.hasNext()){
	    elem = (Element)input_fields.next();
	    java.util.List children = elem.getChildren();
	    
	    // Add the user's input for this field to the Vector.
	    array.add(((Element)children.get(children.size() - 1)).getText());
	}
	
	// Return the Vector as an array of Strings.
	String[] return_array = new String[array.size()];
	return (String[])array.toArray((Object[])return_array);
    }

    /**
     * Parses the user's inputs into a <code>Hashtable</code>. The keys used to
     * hash the inputs are the <code>String</code>s from the corresponding
     * <code>&lt;label_line&gt;</code> elements. As an example, this method is
     * used in <code>beamsearch.beamsearch.java</code>.
     *
     * @param inputXML     A <code>String</code> indicating the file from which
     *                     the XML input generator should be read. Note that
     *                     this should be the same <code>String</code> that the
     *                     server places in argument 2 when running the program
     *                     to start generating a showfile.
     * @return             Gives a <code>Hashtable</code> containing the user's
     *                     inputs to the XML input generator. The hash keys are
     *                     the <code>String</code> values from the 
     *                     <code>&lt;label_line&gt;</code> elements that
     *                     correspond to each user input.
     * @throws IOException Indicates a problem in processing the XML file. In
     *                     theory, this should never happen because the file
     *                     has already been parsed by the client before it 
     *                     reaches the server.
     */
    public static Hashtable parseToHash(String inputXML) throws IOException {
	// Get a Document representation of the temporary server file.
	Document doc = makeDoc(inputXML);

	Element elem = doc.getRootElement();
	// Each child corresponds to one of the input generator fields.
	Iterator input_fields = elem.getChildren().iterator();
	
	Hashtable hash = new Hashtable();
	String key;
	String value;
	
	// Loop through the input generator fields to hash the user's inputs.
	while(input_fields.hasNext()){
	    elem = (Element)input_fields.next();
	    java.util.List children = elem.getChildren();
	    
	    // Each hash table entry has its key specified by label_line (the
	    // first child element) and its value given by 
	    // value_entered/option_entered (the last child element).
	    key = ((Element)children.get(0)).getText();
	    value = ((Element)children.get(children.size() - 1)).getText();

	    hash.put(key, value);
	}
	
	return hash;
    }

    /* Utility method to open the file from the server and read the file into a
     * Document object so it can be parsed by one of the above methods. */
    private static Document makeDoc(String inputXML) throws IOException {
	// Get the temporary file stored on the server.
	BufferedReader reader = 
	    new BufferedReader(new FileReader(inputXML));

	SAXBuilder builder = new SAXBuilder();
	Document doc;

	// Store the XML file in a Document object.
	try{
	    doc = builder.build(reader);
	}catch(JDOMException error){
	    throw new IOException("Problem generating document from String: " +
				  error.getMessage());
	}

	return doc;
    }
}

