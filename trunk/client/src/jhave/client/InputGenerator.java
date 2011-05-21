
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
 * InputGenerator.java
 *
 * Created on July 3, 2002, 5:18 AM
 */
package jhave.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import java.util.Scanner;
import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import jhave.Algorithm;
import jhave.core.JHAVETranslator;

import org.apache.xml.resolver.tools.CatalogResolver;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * User interface to obtain input for algorithms which require it.
 * Original version developed by:
 * @author JRE and LLN
 */

/**
 * Displays a dialog that is used to gather information from the user.
 * New version develop by:
 * @author Chris Gaffney
 */

/**
 * Modifications by Andrew Jungwirth and TLN to allow XML-defined
 * input generators based on input_panel.dtd
 * @author Andrew Jungwirth, TLN
 */

public class InputGenerator extends JComponent implements ActionListener {

    /**
     * @param script The InputGenerator script must be a String
     * containing one or more repetitions of the following widget descriptors:
     *
     * <pre>
     * TEXTFIELD
     * <i>label-for-textfield</i>
     * <i>default text</i>
     * ENDTEXTFIELD
     *
     * CHOICE
     * <i>label-for-choicebox</i>
     * <i>choice-1-text</i>
     * <i>choice-2-text</i>
     * ...
     * <i>choice-n-text</i>
     * ENDCHOICE
     * </pre>
     *
     * Line terminators are significant; there may be empty lines between widget
     * descriptors, but not within a descriptor.  Choice widgets must have at least
     * one item to choose from (though a choice of only one item isn't really much of
     * a choice at all).
     */

    /////////////////////////////////////////////////////////////////////////////
    // Constants

    /** Return value if send is choosen. */
    public static final int OK_OPTION = 0;
    /** Return value if cancel is choosen. */
    public static final int CANCEL_OPTION = 1;
    /** Constant for unknown option. */
    public static final int UNKNOWN_OPTION = -1;

    /** Label for the ok button */
    private static final String OK_BUTTON_LABEL =
	JHAVETranslator.translateMessage("okButton.label");
    //      "OK";
    /** Label for the cancel button */

    private static final String CANCEL_BUTTON_LABEL = 
	JHAVETranslator.translateMessage("cancelButton.label");
    //      "Cancel";

    // End Constants
    /////////////////////////////////////////////////////////////////////////////
    // Globals

    /** Dialog that is shown. */
    private JDialog dialog = null;
    /** Value returned when a button is pressed. */
    private int returnedInt = UNKNOWN_OPTION;
    /** Parameters taken from the user. */
    private String parameters = "";
    /** Connection properties for the current session. */
    private ClientProperties clientProperties;
    /** Script URL. */
    private URL scriptFileURL = null;
    /** The set of components used in the input generator. */
    private Vector components = null;
    /** Tracks whether input generator specification is in XML or the old
     *style. */
    private boolean isXML;
    /** Stores the XML Document for XML input generator specifications. */
    private Document doc;
    //Alejandro
    String texts ="";

    // End Globals
    /////////////////////////////////////////////////////////////////////////////
    // Constructors

    /**
     * Creates a new instance of InputGenerator.
     * @param clientProperties properties file used to figure out the webroot.
     */
    public InputGenerator(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    // End Constructors
    /////////////////////////////////////////////////////////////////////////////
    // GUI Building

    /**
     * Displays a dialog used ot get information from the user.
     * @param parent component that the dialog is modal to.
     * @param algorithm algorithm that needs input.
     * @return int option that was choosen by the user (cancel or ok).
     * @throws IOException an error occured downloadint the input generator script.
     */
    public int showInputGenerator(Component parent, Algorithm algorithm) throws IOException {
        returnedInt = UNKNOWN_OPTION;
        parameters = "";
        scriptFileURL = getInputGenerator(algorithm, clientProperties);
        components = null;
        
	//        dialog = createDialog(parent, "Select Algorithm Input", true);
        dialog = createDialog(parent, 
			      JHAVETranslator.translateMessage("selectAlgoInput"), true);
        dialog.setVisible(true);
        dialog.dispose();
        dialog = null;
        return returnedInt;
    }

    /**
     * Creates the dialog that is displayed.
     * @param parent parent of the dialog.
     * @param title displayed in the title bar.
     * @param modal if the dialog is modal.
     * @return JDialog the dialog that contains the components for gathering input generator information.
     * @throws IOException an error occured loading the input generator script.
     */
    protected JDialog createDialog(Component parent, String title, boolean modal) throws IOException {
        Frame frame = (parent instanceof Frame) ? (Frame)parent : (Frame)SwingUtilities.getAncestorOfClass(java.awt.Frame.class, parent);
        JDialog returnedDialog = new JDialog(frame, title, modal);
        returnedDialog.getContentPane().setLayout(new BorderLayout());
        returnedDialog.getContentPane().add(BorderLayout.CENTER, buildInputPanel(scriptFileURL));
        returnedDialog.getContentPane().add(BorderLayout.SOUTH, buildButtonPanel());

        returnedDialog.pack();
        returnedDialog.setResizable(false);
	//Alejandro
        if(texts.equals("Choose Mode:"))
	    returnedDialog.setSize(720,700);

        returnedDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        returnedDialog.setLocationRelativeTo(frame);
        return returnedDialog;
    }

    /**
     * Builds the JPanel containing the buttons.
     * @return JPanel contains ok and cancel buttons.
     */
    protected JPanel buildButtonPanel() {
        JPanel returnedPanel = new JPanel(new GridLayout(1, 2));
	//        JButton okButton = new JButton(OK_BUTTON_LABEL);
        AbstractButton okButton = 
	    JHAVETranslator.getGUIBuilder().generateJButton("okButton", 
							    null, false, this);
	//        JButton cancelButton = new JButton(CANCEL_BUTTON_LABEL);
        AbstractButton cancelButton = 
	    JHAVETranslator.getGUIBuilder().generateJButton("cancelButton",
							    null, false, this);
        // alread covered in JHAVETranslator...
	//        okButton.addActionListener(this);
	//        cancelButton.addActionListener(this);
        
        returnedPanel.add(okButton);
        returnedPanel.add(cancelButton);
        return returnedPanel;
    }

    /**
     * Builds the panel containing input areas for the user. Given
     * the URL of a Input Generator Script, it downloads, parses,
     * and builds a JPanel with areas to modify the data.
     * @param scriptURL location of the Input Generator Script file.
     * @return JPanel contains input areas for the user.
     * @throws IOException error occured downloading script.
     */
    private JPanel buildInputPanel(URL scriptURL) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(scriptURL.openStream()));

	reader.mark(100);
	String line = reader.readLine();
	reader.reset();

	if( line.trim().charAt(0) == '<' ){
	    isXML = true;

	    try {
		return buildXMLInputPanel(reader);
	    } catch(JDOMException jdomEx) {
		throw new IOException( "JDOM Exception: " + jdomEx.getMessage() );
	    }
	}else{
	    isXML = false;

	    return buildOldInputPanel(reader);
	}
    } // buildInputPanel(URL)

    /* Builds the input panel for non-XML input generator files. */
    private JPanel buildOldInputPanel(BufferedReader reader) throws IOException {
	JPanel returnedPanel = new JPanel();
        JPanel comboBoxPanel = null;
        GridLayout grid = new GridLayout(1, 2);
        BoxLayout box = new BoxLayout(returnedPanel, BoxLayout.Y_AXIS);
        returnedPanel.setLayout(box);

	String readString;
        components = new Vector();
        while((readString = reader.readLine()) != null) {
            if (readString.equalsIgnoreCase("TEXTFIELD")) {
                JLabel label = new JLabel(reader.readLine());
                JTextField textField = new JTextField(reader.readLine());

                if (!(readString = reader.readLine()).equalsIgnoreCase("ENDTEXTFIELD")) {
                    // Ignore for now
                }
                components.add(textField);
                returnedPanel.add(label);
                returnedPanel.add(textField);
            } else if (readString.equalsIgnoreCase("CHOICE")) {
                if(comboBoxPanel == null) {
                    comboBoxPanel = new JPanel(grid);
                } else {
                    grid.setRows(grid.getRows() + 1);
                }

                comboBoxPanel.add(new JLabel(readString = reader.readLine()));
                JComboBox choice = new JComboBox();
                choice.setBackground(Color.white);

                while(!(readString = reader.readLine()).equalsIgnoreCase("ENDCHOICE")) {
                    choice.addItem(readString);
                }
                components.add(choice);
                comboBoxPanel.add(choice);
            }
        }

        if(comboBoxPanel != null) {
            returnedPanel.add(comboBoxPanel);
        }
        return returnedPanel;
    } // buildOldInputPanel(BufferedReader)

    /* Builds the input panel for XML input generator files. */
    private JPanel buildXMLInputPanel(BufferedReader reader) throws IOException, JDOMException {
        JPanel returnedPanel = new JPanel();
        JPanel comboBoxPanel = null;
        GridLayout grid = new GridLayout(1, 2);
        BoxLayout box = new BoxLayout(returnedPanel, BoxLayout.Y_AXIS);
        returnedPanel.setLayout(box);
	components = new Vector();

	SAXBuilder builder = new SAXBuilder();
	builder.setEntityResolver( new CatalogResolver() );
	doc = builder.build(reader);

	Element root = doc.getRootElement();

	if( ! root.getName().equalsIgnoreCase("input_panel") )
	    throw new JDOMException("Expected a \"input_panel\" as the root element of the document.");

	Iterator iter = root.getChildren().iterator();

	while( iter.hasNext() ) {
	    Element child = (Element) iter.next();

	    if( child.getName().equalsIgnoreCase("textfield") ) {
		Iterator txt_iter = child.getChildren().iterator();
		while( txt_iter.hasNext() ) {
		    Element txt_child = (Element) txt_iter.next();

		    if( txt_child.getName().equalsIgnoreCase("label_line") ) {
			returnedPanel.add( new JLabel( txt_child.getText() ) );
		    }
		    else if( txt_child.getName().equalsIgnoreCase("default_field") ) {
			JTextField textField = new JTextField( txt_child.getText() );
			components.add(textField);
			returnedPanel.add(textField);
		    }
		}
	    } // if "textfield"

	    else if( child.getName().equalsIgnoreCase("textarea") ) {
		Iterator txt_iter = child.getChildren().iterator();
		while( txt_iter.hasNext() ) {
		    Element txt_child = (Element) txt_iter.next();

		    if( txt_child.getName().equalsIgnoreCase("label_line") ) {
			returnedPanel.add( new JLabel( txt_child.getText() ) );
		    }
		    else if( txt_child.getName().equalsIgnoreCase("default_field") ) {
			JTextArea textArea = new JTextArea( null,25,80 );
			JPanel panel = new JPanel();
			panel.add(textArea);
			JButton loadFile = new JButton("Load File");
			panel.add(loadFile);
			TextJScrollPane scrollPane = 
			    new TextJScrollPane(panel, textArea, loadFile); // A helper class defined below
			//			    new TextJScrollPane(textArea); // A helper class defined below
			components.add(scrollPane);
			returnedPanel.add(scrollPane);
		    }
		}
	    } // if "textarea"

	    //Alejandro
	    else if( child.getName().equalsIgnoreCase("clickingarea") ) {
		Iterator txt_iter = child.getChildren().iterator();
		while( txt_iter.hasNext() ) {
		    Element txt_child = (Element) txt_iter.next();

		    if( txt_child.getName().equalsIgnoreCase("label_line") ) {
			returnedPanel.add( new JLabel( txt_child.getText() ) );
			System.out.println("This is label: " + txt_child.getText());
			//Here is where I get text, and based on its content I resize.
			texts=txt_child.getText();
		    }
		    else if( txt_child.getName().equalsIgnoreCase("default_field") ) {
			//JTextArea textArea = new JTextArea( null,25,80 );

			JPanel panel = new JPanel((new GridLayout(7,0)));

			ClickArea clickArea =
			    new ClickArea(panel); // A helper class defined below

			components.add(clickArea);
			returnedPanel.add(clickArea);

		    }
		}
	    }

	    else if( child.getName().equalsIgnoreCase("jvm_text_area") ) {
		Iterator txt_iter = child.getChildren().iterator();
		while( txt_iter.hasNext() ) {
		    Element txt_child = (Element) txt_iter.next();

		    if( txt_child.getName().equalsIgnoreCase("label_line") ) {
			returnedPanel.add( new JLabel( txt_child.getText() ) );
		    }
		    else if( txt_child.getName().equalsIgnoreCase("default_field") ) {
			JTextArea textArea = new JTextArea( txt_child.getText(),25,80 );
			JTextField textField = new JTextField( "EMPTY", 80 );
			JButton loadFile = new JButton("Load File");
			JPanel panel = new JPanel(new BorderLayout(10,10));
			panel.add(textField, BorderLayout.PAGE_START);
			panel.add(loadFile, BorderLayout.PAGE_END);
			panel.add(textArea, BorderLayout.CENTER);
			JVMSimTextPane scrollPane =
			    new JVMSimTextPane(panel, textField, textArea, loadFile); // A helper class
			components.add(scrollPane);
			returnedPanel.add(scrollPane);
		    }
		}
	    } // if "jvm_text_area"

	    else if( child.getName().equalsIgnoreCase("combobox") ) {
		Iterator cmb_iter = child.getChildren().iterator();
		JComboBox combobox = new JComboBox();
		combobox.setBackground(Color.white);

		while( cmb_iter.hasNext() ) {
		    Element cmb_child = (Element) cmb_iter.next();

		    if( cmb_child.getName().equalsIgnoreCase("label_line") ) {
			if(comboBoxPanel == null) {
			    comboBoxPanel = new JPanel(grid);
			} else {
			    grid.setRows(grid.getRows() + 1);
			}
			comboBoxPanel.add(new JLabel( cmb_child.getText() ) );
		    }
		    else if( cmb_child.getName().equalsIgnoreCase("option") ) {
			if(comboBoxPanel == null) {
			    comboBoxPanel = new JPanel(grid);
			} else {
			    //grid.setRows(grid.getRows() + 1);
			}
			combobox.addItem( cmb_child.getText() );
		    }
		} // for all children of "combobox"
		components.add(combobox);
		JPanel panel = new JPanel();
		comboBoxPanel.add(combobox);
	    } // else if "combobox"
	} // for all elements

	if(comboBoxPanel != null) {
	    returnedPanel.add(comboBoxPanel);
	}

	return returnedPanel;
    } // buildXMLInputPanel(Element)

    // End GUI Building
    /////////////////////////////////////////////////////////////////////////////
    // Get And Set Methods

    /**
     * Returns the information entered by the user.
     * @return String information entered by the user.
     */
    public String getParameters(boolean quizMode) {
        if(quizMode) return "QUIZMODE";
	else return parameters;
    }

    /**
     * Sets the information entered by the user.
     * @param parameters information entered by the user.
     */
    private void setParameters(String parameters) {
        this.parameters = parameters;
    }

    // End Get And Set Methods
    /////////////////////////////////////////////////////////////////////////////
    // Listener Methods

    /**
     * Action associated with the click of the displayed buttons.
     * @param e event that is fired.
     */
    public void actionPerformed(ActionEvent e) {
	//    if(e.getActionCommand().equals(OK_BUTTON_LABEL)) {
        if(e.getActionCommand().equals(
				       JHAVETranslator.translateMessage("okButton.label"))) {
            returnedInt = OK_OPTION;

	    if(isXML){
		xmlOKaction();
	    }else{
		oldOKaction();
	    }
        } else if(e.getActionCommand().equals(
					      JHAVETranslator.translateMessage("cancelButton.label"))) {
            returnedInt = CANCEL_OPTION;
            if(getParameters(false) != "") {
                setParameters("");
            }
        } else {
            return;
        }
        if(dialog != null) {
            dialog.setVisible(false);
        }
    }

    /* Processes the clicking of the OK button for non-XML input generators. */
    private void oldOKaction(){
	String parameterString = "";
	for(int index = 0; index < components.size(); index++){
	    if(components.elementAt(index) instanceof JTextField) {
		parameterString = parameterString + ((JTextField)components.elementAt(index)).getText() + " \001 ";
	    } else {
		parameterString = parameterString + ((String)((JComboBox)components.elementAt(index)).getSelectedItem()) + " \001 ";
	    }
	}
	setParameters(parameterString);
    }

    /* Processes the clicking of the OK button for XML input generators. */
    private void xmlOKaction(){
	Element elem = doc.getRootElement();
	// Each child corresponds to one of the user input fields.
	Iterator iter = elem.getChildren().iterator();

	// Go through each user input, and place that input in the proper
	// element in the XML input generator document.
	for(int index = 0; index < components.size(); index++){
	    elem = (Element)iter.next();
	    // Get the children elements that correspond to this input field
	    // element. The important element here is the last one in the list,
	    // which should be modified to include the user's input.
	    java.util.List children = elem.getChildren();

	    // Insert the user's input into the value_entered/option_entered
	    // element.
	    if(components.elementAt(index) instanceof JTextField){
		((Element)children.get(children.size() - 1)).setText(((JTextField)components.elementAt(index)).getText());
	    }
	    else if(components.elementAt(index) instanceof TextJScrollPane){
		JTextArea ta = (JTextArea)(((TextJScrollPane)components.elementAt(index)).get_text_area());
		((Element)children.get(children.size() - 1)).setText(ta.getText());
	    }
	    else if(components.elementAt(index) instanceof JVMSimTextPane){
		((Element)children.get(children.size() - 1)).setText(((JVMSimTextPane)components.elementAt(index)).getInputString());
		//Alejandro
	    }
	    else if(components.elementAt(index) instanceof ClickArea){
		ClickArea ca = (ClickArea)components.elementAt(index);
		((Element)children.get(children.size() - 1)).setText(ca.getText());
	    }
	    else{
		((Element)children.get(children.size() - 1)).setText(((String)((JComboBox)components.elementAt(index)).getSelectedItem()));
	    }
	}

	XMLOutputter out = new XMLOutputter();

	// Send a String representation of the XML document to the server.
	setParameters(out.outputString(doc).trim());
    }


    // End Listener Methods
    /////////////////////////////////////////////////////////////////////////////
    // Other Methods

    /**
     * Given an algorithm it returns the URL of the algorithms InputGenerator script.
     * If the algorithm does not use an input generator then null is returned.
     * @param algorithm algorithm to find to find input generator for.
     * @param clientProperties connection settings - used to get webroot.
     * @return URL location of algorithms input generator script.
     */
    public static final URL getInputGenerator(Algorithm algorithm, ClientProperties clientProperties) {
        URL returnedURL = null;
        if(algorithm.GetAlwaysNeedsInputGenerator()) {
            try {
                returnedURL = new URL(clientProperties.getWebroot() + "ingen/" + algorithm.GetAlgoName() + ".igs");
            } catch (MalformedURLException e) {
                // Shouldn't happen
            }
        }
        return returnedURL;
    }

    // End Other Methods
    /////////////////////////////////////////////////////////////////////////////
}


