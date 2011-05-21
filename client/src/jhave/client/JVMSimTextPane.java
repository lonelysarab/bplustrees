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

/*
 * JVMSimTextPane.java -- helper class for input generator specifications
 *
 */
package jhave.client;

import java.util.Iterator;
import java.util.Vector;
import java.util.Scanner;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;


// A simple helper class for the jvm_text_area specification in an
// input generator file.  The difference between this and
// TextJScrollPane is that the latter does not prepend a line with the
// loaded file name to the beginning of the XML string returned to the
// server while this one does.  If the user does not use the load file
// button to populate the text area, then they must enter the file
// name manually to avoid the string "\\EMPTY" being sent as the first
// line.

public class JVMSimTextPane extends JScrollPane implements ActionListener {

    JTextArea my_text_area;
    JPanel panel = new JPanel();
    JButton loadFileButton;
    JTextField my_text_field;
    String info;
 
    public JVMSimTextPane(JPanel p, JTextField jf, JTextArea jt, JButton jb) {
	super ( p );
	panel = p;
	info = "";
 	JButton loadFileButton = jb;
 	loadFileButton.addActionListener(this);
 	my_text_area = jt;
	my_text_field = jf;
    }

    String getInputString () {
	return my_text_field.getText() + "\n" + my_text_area.getText();
    }

    public void actionPerformed(ActionEvent e){
	try{
	    loadFile();
	}catch(IOException t){
	} 
	
    }
    private void loadFile() throws IOException
    {

	JFileChooser chooser = new JFileChooser();
		
	int status = chooser.showOpenDialog (null);
		
	if (status != JFileChooser.APPROVE_OPTION)
	    {
		info = "No File Chosen";
		my_text_area.setText(info);
	    }
	else
	    {
		info = "";
		File file = chooser.getSelectedFile();
		my_text_field.setText(file.getName());
		Scanner scan = new Scanner(file);
			
		while (scan.hasNext()){
		    info += scan.nextLine() + "\n";
		}
		my_text_area.setText(info);
	    }
		
    }
}
