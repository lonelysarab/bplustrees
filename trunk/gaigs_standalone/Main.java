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
 * Driver to test gaigs standalone
 */

package gaigs_standalone;

import java.io.*;
import javax.swing.*;
import jhave.core.Visualizer;
import jhave.client.ControlPanel;

import jhave.event.*;

import gaigs2.*;
import jhave.Algorithm;


/**
 *
 * @author  T.N.
 */ 
public class Main {
    /**
     * @param args the command line arguments
     */

    public static String WEBROOT;
    public static Algorithm algo;

    public static void main(String[] args) throws Exception {

	if (args.length != 0) {
	    WEBROOT = args[0];
	    algo = new Algorithm();
	    algo.SetAlgoName(args[1]);
	}
	else {
// 	    WEBROOT = "http://localhost/jhave/html_root/";
	    WEBROOT = "file:///home/naps/";
	    algo = new Algorithm();
	    algo.SetAlgoName("rbtreealex");
	}

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
	//		        script = new File(ClassLoader.getSystemResource("gaigs_test_no_ques.sho").getFile());
	//			        script = new File(ClassLoader.getSystemResource("gaigs_test_with_ques.sho").getFile());

	//	script = new File(ClassLoader.getSystemResource("gaigs_test_with_ques_url.sho").getFile());
	//	script = new File(ClassLoader.getSystemResource("sofar.sho").getFile());
	script = new File(ClassLoader.getSystemResource("questions.sho").getFile());
	//script = new File(ClassLoader.getSystemResource("mgcircle_xml.sho").getFile());
	//		script = new File(ClassLoader.getSystemResource("random_unweighted_graph.sho").getFile());


	//        script = new File(ClassLoader.getSystemResource("demo_str.sho").getFile());
	//	        script = new File(ClassLoader.getSystemResource("animated_demo_str.sho").getFile());
	//        script = new File(ClassLoader.getSystemResource("just_a_stack.sho").getFile());
        
        // Get a file given a direct path
        // Windows
        //script = new File("c:\\path\\to\\file.ext
        // Linux
        //script = new File("/home/user/file.ext
        
        // Step 2: Instantiate you Visualizer
        Visualizer visualizer = new GaigsAV(new FileInputStream(script));
        
        // Step 3: The rest is taken care of
        visualizer.addQuestionListener(new QuestionHandler());
        visualizer.addDocumentationListener(new DocumentationHandler());
        ControlPanel p = new ControlPanel(visualizer);
        
        JFrame f = new JFrame("GAIGS Standalone test");
        f.setContentPane(p);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
	visualizer.gotoFrame(0); // Artificial but necessary to get questions and html docs for first snapshot
// 
// 	((GaigsAV)visualizer).readScript(new Algorithm(), new FileInputStream(script));
// 	((GaigsAV)visualizer).runScript();
	
    }
    
    private static class QuestionHandler implements QuestionListener {
        public void handleQuestion(QuestionEvent event) {
            jhave.question.QuestionFactory.showQuestionDialog(event.getQuestion());
        }
    }
    private static class DocumentationHandler implements DocumentationListener {
        public void showDocument(DocumentEvent event) {
//             if(event.getType() == event.TYPE_INFORMATION_PAGE) {
//                 System.out.println("Info page: " + event.getPage().toString());
//             } else if(event.getType() == event.TYPE_PSEUDOCODE_PAGE) {
//                 System.out.println("Pseudocode page: " + event.getPage().toString());
//             } else {
//                 System.out.println("Unknown type: " + event.getPage().toString());
//             }
        }
    }
}
