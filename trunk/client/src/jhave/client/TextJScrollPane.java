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
 * TextJScrollpane.java -- helper class for input generator specifications
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
import javax.swing.JButton;


// A simple helper class for the text_area specification in an input
// generator file, giving easy access to the text area in the
// JScrollPane.  The JScrollPane class itself did not seem to provide
// a good way to do this when the component in it was a text area.
// TLN 7/10/07
public class TextJScrollPane extends JScrollPane implements ActionListener {

    JTextArea my_text_area;
    JPanel panel = new JPanel();
    JButton loadFileButton;
    String info;

    public TextJScrollPane(JPanel p, JTextArea jt, JButton jb) {
	super ( p );
	panel = p;
	info = "";
 	JButton loadFileButton = jb;
 	loadFileButton.addActionListener(this);
 	my_text_area = jt;
    }

    JTextArea get_text_area () {
	return my_text_area;
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
		Scanner scan = new Scanner(file);
			
		while (scan.hasNext()){
		    info += scan.nextLine() + "\n";
		}
		my_text_area.setText(info);
	    }
		
    }
}
