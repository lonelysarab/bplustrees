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
 * Main.java
 *
 * Created on August 7, 2004, 1:57 PM
 */

package org.gaffneyc.gaff;

import java.net.*;
import java.io.*;
import javax.swing.*;

import jhave.core.*;
import jhave.question.*;
import jhave.event.*;
import jhave.client.ControlPanel;


/**
 *
 * @author  Chris Gaffney
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        File script;
        
        // Step 1: Pick a method for finding the script
        // Find a file using a JFileChooser
        /*
        JFileChooser jfc = new JFileChooser("");
        if(jfc.showOpenDialog(null) == jfc.CANCEL_OPTION) {
            System.exit(1);
        }
        script = jfc.getSelectedFile();
         **/
        
        // Get a file that is somewhere within the classpath
        script = new File(ClassLoader.getSystemResource("org/gaffneyc/simple.gaff").getFile());
        
        // Get a file given a direct path
        // Windows
        //script = new File("c:\\path\\to\\file.ext
        // Linux
        //script = new File("/home/user/file.ext
        
        // Step 2: Instantiate you Visualizer
        Visualizer visualizer = new GaffVisualizer(new FileInputStream(script));
        
        // Step 3: The rest is taken care of
        visualizer.addQuestionListener(new QuestionHandler());
        visualizer.addDocumentationListener(new DocumentationHandler());
        ControlPanel p = new ControlPanel(visualizer);
        
        JFrame f = new JFrame("Visualizer test.");
        f.setContentPane(p);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    
    private static class QuestionHandler implements QuestionListener {
        public void handleQuestion(QuestionEvent event) {
            jhave.question.QuestionFactory.showQuestionDialog(event.getQuestion());
        }
    }
    private static class DocumentationHandler implements DocumentationListener {
        public void showDocument(DocumentEvent event) {
            if(event.getType() == event.TYPE_INFORMATION_PAGE) {
                System.out.print("Info page: " + event.getPage().toString());
            } else if(event.getType() == event.TYPE_PSEUDOCODE_PAGE) {
                System.out.print("Pseudocode page: " + event.getPage().toString());
            } else {
                System.out.print("Unknown type: " + event.getPage().toString());
            }
        }
    }
}
