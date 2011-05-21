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
 * SetupPanel.java
 *
 * Created on July 1, 2002, 2:03 AM
 */

package jhave.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import translator.TranslatableGUIElement;

import jhave.Algorithm;
import jhave.client.misc.AlgorithmComboBoxModel;
import jhave.client.misc.AlgorithmListCellRenderer;
import jhave.core.JHAVETranslator;
import jhave.event.NetworkEvent;
/**
 * The initial pages that is displayed when the JHave client is loaded.
 * @author Chris Gaffney
 */
public class SetupPanel extends JPanel {
    
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    
    /** Location and filename of the logo. */
    private static final String LOGO_FILENAME = "jhave/client/graphics/jhave_logo.png";
    /** Location of the page detailing how to start a visualization. */
    private static final String INFO_FILENAME = "jhave/client/docs/info_page";
    /** Location of the error page displayed if a page fails to load. */
    private static final String ERROR_FILENAME = "jhave/client/docs/error_loading.html";
    /** Key identifying the Category Panel in the card layout. */
    private static final String CATEGORY_KEY = "Category Panel";
    /** Key identifying the Combo box panel in the card layout. */
    private static final String COMBOBOX_KEY = "Combobox Panel";
    
    // End Constants
    /////////////////////////////////////////////////////////////////////////////////
    // Globals
    
    /** Panel that displays the login controls. */
    private LoginPanel loginPanel;
    /** Buffer to set the state of the input generator button back to its original state after clicking visualize button. */
    private boolean generatorStateBuffer = false;
    /** Buffer to set the state of the visualize button back to its original state after clicking visualize button. */
    private boolean visualizerStateBuffer = false;
    /** Combo box that contains the list of algorithms. */
    private static final JComboBox algorithmComboBox = new JComboBox();
    
    /** Button that loads the input generator for the selected algorithm. */
    //private static final JButton generatorButton = new JButton("Load Generator"); 
    // **************************************************
    // This button has been eliminated from the interface
    // to make the interaction with the client simpler.
    // **************************************************

    /** JRadioButton for using newly created data, for algorithms that require input generators. */
    protected static final JToggleButton newData = 
    	JHAVETranslator.getGUIBuilder().generateJToggleButton("newData",
    			null, null, true);	
//    	new JRadioButton("New Data", true);
    /** JRadioButton for using the last algorithms data, for algorithms that require input generators. */
    protected static final JToggleButton oldData = 
    	JHAVETranslator.getGUIBuilder().generateJToggleButton("oldData",
			null, null, true);	
//    	new JRadioButton("Old Data", false);
    /** ButtonGroup that makes sure new and old data type are not selected at the same time. */
    private static final ButtonGroup dataButtonGroup = new ButtonGroup();
    /** Button that starts the visualizer. */
    protected static final AbstractButton visualizerButton =
    	JHAVETranslator.getGUIBuilder().generateJButton("visualize");
//    	new JButton("Visualize");
    /** The JTextField where the user enters a category of algorithms. */
    private static final JTextField categoryJTextArea = new JTextField(10);
    /** The JTextField where the user enters a server to connect to. */
    private static final JTextField serverTextField = new JTextField(25);
    /** The JButton the user clicks to connect to the server. */
    private static final AbstractButton connectButton = 
    	JHAVETranslator.getGUIBuilder().generateJButton("connect");
//    	new JButton("Connect");
    /** The card layout used to switch the bottom panel. */
    private static final CardLayout switcherLayout = new CardLayout();
    /** The panel that houses the switcher layout. */
    private static final JPanel switcherPanel = new JPanel(switcherLayout);
    /** Client Network Conroller that has the connection with the server. */
    private ClientNetworkController controller;
    /** The JEditorPane that displays the */
    private JEditorPane infoPanel0 = new JEditorPane();
    private JEditorPane infoPanel1 = new JEditorPane();
    /** Last algorithm that needed an input generator. */
    private Algorithm lastInputGeneratorAlgorithm = null;
    /** Last set of parameters retrieved from an input generator. */
    private String parameters = null;
  
    // Used to determine if the visualization has a input generator.
    private static boolean hasInputGenerator = false;
    // Used to determine if Jhave has been started to auto load a
    // visualization.
    private static boolean autoLoad = false;
    
    private Client client;

    static {
        algorithmComboBox.setRenderer(new AlgorithmListCellRenderer(AlgorithmListCellRenderer.DISPLAY_DESCRIPTIVE_TEXT));
        
        dataButtonGroup.add(newData);
        dataButtonGroup.add(oldData);
        
        //generatorButton.setEnabled(false);
        visualizerButton.setEnabled(true);
    }
    
    // End Globals
    /////////////////////////////////////////////////////////////////////////////////
    // Constructors
    
    /**
     * Creates a new instance of SetupPanel.
     * @param controller <code>ClientNetworkController</code> used to login.
     *
     * ----------------------------------
     * | 0,0 L|    1,0 LoginPanel       |
     * |-----o--------------------------|
     * | 0,1 g|    1,1 ComboboxPanel    |
     * |     o|                         |
     * |      |                         |
     * |______|_________________________|
     */
    public SetupPanel(ClientNetworkController controller) {
        super(new BorderLayout());
        this.controller = controller;
        infoPanel0.setEditable(false);
	infoPanel1.setEditable(false);
        categoryJTextArea.setText(controller.getClientProperties().getCategory());
        serverTextField.setText(controller.getClientProperties().getServer());
        
        //add(BorderLayout.WEST, buildLogoPanel());
        //add(BorderLayout.CENTER, buildCenterPanel());
        addListeners();
    }

    public SetupPanel(ClientNetworkController controller, Client cli)
    {
	this(controller);
	client = cli;
    }
    
    // End Constructors
    /////////////////////////////////////////////////////////////////////////////////
    // Build GUI Methods
    
    /**
     * Builds a JPanel containing the logo. Loads the image file into an
     * <code>ImageIcon</code> and puts a lowered bevel boarder around the JPanel.
     * @return JPanel contains the logo.
     */
    public JPanel buildLogoPanel() {
        JPanel returnedPanel = new JPanel(new BorderLayout());
        returnedPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        returnedPanel.setBackground(Color.black);
        
        ClassLoader loader = getClass().getClassLoader();
        ImageIcon logo = new ImageIcon(loader.getResource(LOGO_FILENAME));
        returnedPanel.add(BorderLayout.CENTER, new JLabel(logo));
        
        return returnedPanel;
    }

    /**
     * Builds the Panel that contains the Login panel and the ComboBoxPanel.
     * @return JPanel contains the login panel and ComboBoxPanel.
     */
    public JPanel buildCenterPanel(int panel) {
        JEditorPane jep = null;
	if(panel==0) jep = infoPanel0;
	if(panel==1) jep = infoPanel1;
	
	JPanel returnedPanel = new JPanel(new BorderLayout());
        
        returnedPanel.add(BorderLayout.CENTER, new JScrollPane(jep, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        //returnedPanel.add(BorderLayout.SOUTH, buildCategoryPanel());
        
	String localeAppendix = "_" + JHAVETranslator.getLocale() +".html";
//         String localeAppendix = JHAVETranslator.getLocale().getLanguage()
//         +"_" + JHAVETranslator.getLocale() +".html";
        //
        try {
            ClassLoader loader = getClass().getClassLoader();
            jep.setPage(loader.getResource(INFO_FILENAME + localeAppendix));
        } catch (IOException e) {
            // Don't do anything
        }
        
        return returnedPanel;
    }
    
    /**
     * Build the panel that is initialy displayed and allows the user to select a category and connect
     * to the server.
     * @return JPanel the category panel.
     */
    public JPanel buildCategoryPanel() {
        JPanel returnedPanel = new JPanel(new GridLayout(4, 2));
        TranslatableGUIElement generator = JHAVETranslator.getGUIBuilder();
        returnedPanel.setBorder(generator.generateTitledBorder("catSelection"));
//        returnedPanel.setBorder(BorderFactory.createTitledBorder("Category Selection"));
        
        // The labels
        JLabel serverLabel = generator.generateJLabel("selServer");
        JLabel categoryLabel = generator.generateJLabel("selCat");
        JLabel connectLabel = generator.generateJLabel("connServer");
//        JLabel serverLabel = new JLabel("1. Select a server");
//        JLabel categoryLabel = new JLabel("2. Select a category");
//        JLabel connectLabel = new JLabel("3. Connect to server");
        JLabel fillerLabel = new JLabel("");
        
        // Add the labels and the components
        returnedPanel.add(serverLabel);
        returnedPanel.add(serverTextField);
        returnedPanel.add(categoryLabel);
        returnedPanel.add(categoryJTextArea);
        returnedPanel.add(connectLabel);
        returnedPanel.add(connectButton);
        returnedPanel.add(fillerLabel);
        returnedPanel.add(fillerLabel);
        
        return returnedPanel;
    }
    
    /**
     * Build the panel that contains the directions for use and the button to start the visualizers.
     * @param model the AlgorithmComboBoxModel that contains the list of algorithms available to the user.
     * @return JPanel contains the directions and buttons for starting the visualizers.
     *
     * --------------------<br>
     * | 1~~~~ |-------|  |<br>
     * | 2~~~~ |-------|  |<br>
     * | 3~~~~ |=======|  |<br>
     * | 4~~~~ |=======|  |<br>
     * |------------------|
     */
    public JPanel buildComboBoxPanel(AlgorithmComboBoxModel model) {
        JPanel returnedPanel = new JPanel(new GridLayout(4, 2));
        TranslatableGUIElement generator = JHAVETranslator.getGUIBuilder();
        returnedPanel.setBorder(generator.generateTitledBorder("algoSel"));
//        returnedPanel.setBorder(BorderFactory.createTitledBorder("Algorithm Selection"));
        
        JLabel selectAlgorithmLabel = generator.generateJLabel("algoSel1");
        JLabel selectData = generator.generateJLabel("algoSel2");
        //JLabel loadGeneratorLabel = generator.generateJLabel("algoSel3");
        JLabel loadVisualizerLabel = generator.generateJLabel("algoSel4");
//        JLabel selectAlgorithmLabel = new JLabel("1. Select Algorithm ");
//        JLabel selectData = new JLabel("2. Create new data or use last algorithm's data");
//        JLabel loadGeneratorLabel = new JLabel("3. Select Data (if needed) ");
//        JLabel loadVisualizerLabel = new JLabel("4. Load Visualizer ");
        
        // Create a panel to contain the two radio buttons
        JPanel radioButtonPanel = new JPanel();
	// Commenting these four lines out removed a 
	// "java.awt.AWTError: BoxLayout can't be shared"
	// error that emerged in Java 6
//        BoxLayout boxLayout = new BoxLayout(radioButtonPanel, BoxLayout.X_AXIS);
//         boxLayout.addLayoutComponent("New data", newData);
//         boxLayout.addLayoutComponent("Old data", oldData);
//         radioButtonPanel.setLayout(boxLayout);
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.X_AXIS));
        radioButtonPanel.add(Box.createHorizontalStrut(25));
        radioButtonPanel.add(newData);
        radioButtonPanel.add(Box.createHorizontalGlue());
        radioButtonPanel.add(oldData);
        radioButtonPanel.add(Box.createHorizontalStrut(25));

        /* // GR: This code should work just fine with JDK 6
        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalStrut(25));
        box.add(newData);
        box.add(Box.createHorizontalStrut(25));
        box.add(oldData);
        box.add(Box.createHorizontalStrut(25));
        */
        
        // Add each component to the panel
        returnedPanel.add(selectAlgorithmLabel);
        returnedPanel.add(algorithmComboBox);
        returnedPanel.add(selectData);
        returnedPanel.add(radioButtonPanel);
        //returnedPanel.add(loadGeneratorLabel);
        //returnedPanel.add(generatorButton);
        returnedPanel.add(loadVisualizerLabel);
        returnedPanel.add(visualizerButton);
        
        algorithmComboBox.setModel(model);

        setStateForAlgorithm((Algorithm)algorithmComboBox.getSelectedItem());
        return returnedPanel;
    }
    
    /**
     * Adds default listeners to various components.
     */
    private void addListeners() {
        class AlgorithmSelectionActionListener extends Thread implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                //generatorButton.setEnabled(false);
		hasInputGenerator = false;
                visualizerButton.setEnabled(true);
                
                new Thread(this).start();
            }
            public void run() {
		NetworkEvent event = new NetworkEvent(controller, 
				NetworkEvent.LOG_MESSAGE,
				JHAVETranslator.translateMessage("retrieveAlgoInf"));
				//"Retrieving Algorithm Information");
		controller.fireOutboundTransaction(event);

                setStateForAlgorithm((Algorithm)algorithmComboBox.getSelectedItem());
            }
        }
        algorithmComboBox.addActionListener(new AlgorithmSelectionActionListener());
        
        final JComboBox cb = algorithmComboBox;
        class AlgorithmKeyListener extends KeyAdapter {
            public void keyPressed(KeyEvent e) {
                if(!Character.isLetterOrDigit(e.getKeyChar())) {
                    return;
                }
                ComboBoxModel model = algorithmComboBox.getModel();
                int index = algorithmComboBox.getSelectedIndex();
                int total = algorithmComboBox.getItemCount();
                for(int i = (index + 1) % total; i != index; i = (i + 1) % total) {
                    Algorithm algo = (Algorithm)model.getElementAt(i);
                    if(algo.GetDescriptiveText().toLowerCase().startsWith(Character.toLowerCase(e.getKeyChar()) + "")) {
                        final int j = i;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                cb.setSelectedIndex(j);
                            }
                        });
                        break;
                    }
                }
            }
        }
        algorithmComboBox.addKeyListener(new AlgorithmKeyListener());
        
        class InputGeneratorActionListener extends Thread implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                //EventQueue.invokeLater(this);
                new Thread(this).start();
            }
            
            public void run() {
                InputGenerator ig = new InputGenerator(controller.getClientProperties());
                try {
                    int returnedInt = ig.showInputGenerator(getParent(), (Algorithm)algorithmComboBox.getSelectedItem());
		    if(returnedInt == ig.OK_OPTION){
                        parameters = ig.getParameters(controller.getQuizForGrade());
			System.out.println(parameters);
                        visualizerButton.setEnabled(true);
                    }
                } catch (java.io.IOException exception) {
                    JOptionPane.showMessageDialog(getParent(),
                    		JHAVETranslator.translateMessage("errorDownloadInGenScript"));
//                    		"An error occured while downloading the Input Generator Script");
                }
            }
        }
        //generatorButton.addActionListener(new InputGeneratorActionListener());
        
        class VisualizerButtonActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                InputGenerator ig = new InputGenerator(controller.getClientProperties());
		int returnedInt = ig.OK_OPTION;
		if(hasInputGenerator && controller.getQuizForGrade())
		{
		    try {
			parameters = ig.getParameters(controller.getQuizForGrade());
			System.out.println(parameters);
			visualizerButton.setEnabled(true);
			
		    } catch (Exception exception) {
			JOptionPane.showMessageDialog(getParent(), "An error occured while sending parameters");
		    }
		}
		else if(hasInputGenerator)
		{
		    try {
			returnedInt = ig.showInputGenerator(getParent(), (Algorithm)algorithmComboBox.getSelectedItem());
			if(returnedInt == ig.OK_OPTION){
			    parameters = ig.getParameters(controller.getQuizForGrade());
			    System.out.println(parameters);
			    visualizerButton.setEnabled(true);
			}
		    } catch (java.io.IOException exception) {
			JOptionPane.showMessageDialog(getParent(), "An error occured while downloading the Input Generator Script");
		    }
		}
		if(returnedInt == ig.OK_OPTION)
		{
		    try {
			if(!((Algorithm)algorithmComboBox.getSelectedItem()).GetDynamicStatus()) {
			    
			    // If we are in quiz mode we request a scored script.
			    if(controller.getQuizForGrade()) {
				controller.requestScoredStaticScript();
			    } else {
				controller.requestStaticScript();
			    }
			} else {
			    lastInputGeneratorAlgorithm = (Algorithm)algorithmComboBox.getSelectedItem();
			    
			    // If we are in quiz mode we request a scored script.
			    if(controller.getQuizForGrade()) {
				System.out.println("$$$ " + parameters);
				controller.requestScoredGeneratedScript(parameters);
			    } else {
				controller.requestGeneratedScript(parameters, newData.isSelected());
			    }
			}
		    } catch (java.io.IOException exception) {
			System.err.println("Error loading algorithm: " + ((Algorithm)algorithmComboBox.getSelectedItem()).GetAlgoName());
		    }
		}
	    }
        }
        visualizerButton.addActionListener(new VisualizerButtonActionListener());
        
        class DataRadioButtonActionListener implements ActionListener {
            public void actionPerformed(final ActionEvent e) {
            	Object source = e.getSource();
            	if (source == newData) {
//                if(e.getActionCommand().equalsIgnoreCase("new data")) {
                    //generatorButton.setEnabled(true);
                    hasInputGenerator = true;
                    visualizerButton.setEnabled(true);
            	} else if (source == oldData) {
//                } else if(e.getActionCommand().equalsIgnoreCase("old data")) {
                    //generatorButton.setEnabled(false);
                    hasInputGenerator = false;
                    visualizerButton.setEnabled(true);
                } 
            }
        }
        oldData.addActionListener(new DataRadioButtonActionListener());
        newData.addActionListener(new DataRadioButtonActionListener());
        
	// COME BACK HERE TO SEE WHAT NEEDS TO BE DONE HERE!!!
        class TutorialHyperlinkListener extends Thread implements HyperlinkListener {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        infoPanel1.setPage(e.getURL());
                    } catch (java.io.IOException exception) {
                        // Put a generic error message.
                    	infoPanel1.setText(
                    			JHAVETranslator.translateMessage("errorLoadingPageNoInfo"));
//                        infoPanel1.setText("Error loading page...");
                    }
                }
            }
        };
        infoPanel1.addHyperlinkListener(new TutorialHyperlinkListener());
        
        class CategoryFieldKeyListener extends KeyAdapter {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    connectButton.doClick();
                }
            }
        }
        categoryJTextArea.addKeyListener(new CategoryFieldKeyListener());
        serverTextField.addKeyListener(new CategoryFieldKeyListener());
        
        class ConnectButtonActionListener implements ActionListener, Runnable {
            ActionEvent event;
            public void actionPerformed(ActionEvent e) {
                event = e;
                //((AbstractButton)e.getSource()).removeActionListener(this);
                new Thread(this).start();
            }
            public void run() {
		if(controller.isConnected()) client.resetConnection();
                Container parent = getParent();
                while(parent != null && !(parent instanceof JFrame)) {
                    parent = parent.getParent();
                }
                /*InfiniteProgressPanel ipp = null;
                if(parent != null) {
//                    ipp = new InfiniteProgressPanel("Connecting...");
                	ipp = new InfiniteProgressPanel(JHAVETranslator.translateMessage(
                			"connectingWait"));
                    ((JFrame)parent).setGlassPane(ipp);
                    ipp.start();
		    }*/
                
                AlgorithmComboBoxModel algorithms = null;
                try {
                    controller.getClientProperties().setServer(serverTextField.getText().trim());
                } catch (java.net.UnknownHostException exception) {
                    ((AbstractButton)event.getSource()).addActionListener(this);
                    JOptionPane.showMessageDialog(getParent(),
                    		JHAVETranslator.translateMessage("serverNotFound"),
                    		JHAVETranslator.translateMessage("serverNotFoundHeader"),
//                    		"The server address you specified cannot be found.", 
//                    		"Server cannot be located", 
                    		JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
		if (autoLoad && Client.autoLoadInfo != null) {
		    try {
			algorithms = controller.getAlgorithmList(Client.autoLoadInfo);
		    } catch (Exception e) {
			((AbstractButton)event.getSource()).addActionListener(this);
			JOptionPane.showMessageDialog(getParent(),
						      "The autoload algo does not exist, choose another category instead.",
						      "Category error", 
						      JOptionPane.ERROR_MESSAGE);
			return;
		    }
		}
		else
		    {
			try {
			    // HERE IS WHERE AUTOLOAD COULD FAKE A CATEGORY (AND DOES IN THE ABOVE IF)
			    controller.getClientProperties().setCategory(categoryJTextArea.getText().trim());
			    URL catUrl = new URL(controller.getClientProperties().getCategoryList());
			    algorithms = controller.getAlgorithmList(catUrl);
			} catch (java.util.NoSuchElementException exception) {
			    // This is thrown when the category file is not written correctly and the parser runs out of tokens to parse unexpectantly
			    ((AbstractButton)event.getSource()).addActionListener(this);
			    JOptionPane.showMessageDialog(getParent(),
							  JHAVETranslator.translateMessage("catCorrupt"),
							  JHAVETranslator.translateMessage("catCorruptHeader"),
							  //                    		"The category file has been corrupted, choose another category.",
							  //                    		"Category error", 
							  JOptionPane.ERROR_MESSAGE);
			    return;
			} catch (IOException exception) {
			    ((AbstractButton)event.getSource()).addActionListener(this);
			    JOptionPane.showMessageDialog(getParent(),
							  JHAVETranslator.translateMessage("missingCat"),
							  JHAVETranslator.translateMessage("missingCatHeader"),
							  //                    		"You have selected a category that does not exist, please try again.",
							  //                            "Category does not exist", 
							  JOptionPane.ERROR_MESSAGE);
			    return;
			}
		    }

                try {
                    // This doesn't need to be here... it's all for the effect.
                    /*try {
                        Thread.sleep(2000);
			} catch (InterruptedException e) {}*/
                    
                    controller.connectToServer();
                } catch (IOException exception) {
                	((AbstractButton)event.getSource()).addActionListener(this);
                	JOptionPane.showMessageDialog(getParent(),
                			JHAVETranslator.translateMessage("errorConnParam", 
                					controller.getClientProperties().getServer()),
                					JHAVETranslator.translateMessage("errorConnHeader"),
//              					"Error connectiong to server: " 
//              					+ controller.getClientProperties().getServer() 
//              					+ "\nA server is not running at that address.", 
//              					"Server not found at address", 
                					JOptionPane.ERROR_MESSAGE);
                    return;
                } finally {
                    /*if(ipp != null) {
                        ipp.stop();
			}*/
                }
		client.buildSetupTab(algorithms);
                //switcherPanel.add(buildComboBoxPanel(algorithms), COMBOBOX_KEY);
                //switcherLayout.show(switcherPanel, COMBOBOX_KEY);
                algorithmComboBox.requestFocus();
		
		//		if(autoLoad) visualizerButton.doClick();
		if (autoLoad) {
		    Runnable doClickRunnable = new Runnable() {
			    public void run() { visualizerButton.doClick(); }
			};
		    SwingUtilities.invokeLater(doClickRunnable);
		    autoLoad = false; // In case the user later wants to re-connect
		}

		//connectButton.addActionListener(new ConnectButtonActionListener());
	    }
	}
        connectButton.addActionListener(new ConnectButtonActionListener());
    }
    
    // End Build GUI Methods
    /////////////////////////////////////////////////////////////////////////////////
    // Get and Set Methods
    
    /**
     * Sets the state of the buttons for input generator and visualization button for the selected algorithm.
     * @param algorithm the algorithm used to set the state of the buttons.
     */
    private void setStateForAlgorithm(Algorithm algorithm) {
        if(controller.hasInputGenerator((Algorithm)algorithmComboBox.getSelectedItem())) {
            newData.setEnabled(true);
            newData.setSelected(true);
            oldData.setEnabled(false);
            //generatorButton.setEnabled(true);
	    hasInputGenerator = true;
            visualizerButton.setEnabled(true);
            if((lastInputGeneratorAlgorithm != null && lastInputGeneratorAlgorithm.IsAlgoFriend((Algorithm)algorithmComboBox.getSelectedItem())) && parameters != null) {
                oldData.setEnabled(true);
            }
        } else {
            newData.setEnabled(true);
            newData.setSelected(true); // When would we ever not want
				       // newData enabled and true?
				       // The old code below seemed to
				       // think there was a reason,
				       // but ????  (TN)
//             newData.setEnabled(false);
            oldData.setEnabled(false);
	    hasInputGenerator = false;
            //generatorButton.setEnabled(false);
	    hasInputGenerator = false;
            visualizerButton.setEnabled(true);
        }

	//OLD CODE
//         if(controller.hasInputGenerator((Algorithm)algorithmComboBox.getSelectedItem())) {
//             newData.setEnabled(true);
//             newData.setSelected(true);
//             oldData.setEnabled(false);
//             //generatorButton.setEnabled(true);
// 	    hasInputGenerator = true;
//             visualizerButton.setEnabled(true);
//             if((lastInputGeneratorAlgorithm != null && lastInputGeneratorAlgorithm.IsAlgoFriend((Algorithm)algorithmComboBox.getSelectedItem())) && parameters != null) {
//                 oldData.setEnabled(true);
//             }
//         } else {
//             newData.setEnabled(false);
//             oldData.setEnabled(false);
// 	    hasInputGenerator = false;
//             //generatorButton.setEnabled(false);
// 	    hasInputGenerator = false;
//             visualizerButton.setEnabled(true);
//         }


    }
    
    /**
     * Sets the state of the buttons to enabled or disabled.
     * @param enabled if the panel is enabled or not.
     */
    public void setEnabled(boolean enabled) {
        if(!enabled) {
            newData.setEnabled(enabled);
            oldData.setEnabled(enabled);
            //generatorButton.setEnabled(enabled);
            visualizerButton.setEnabled(enabled);
            algorithmComboBox.setEnabled(enabled);
        } else {
            algorithmComboBox.setEnabled(enabled);
            setStateForAlgorithm(controller.getSelectedAlgorithm());
        }
    }

    // Clicks the connect button to autoload a visualization
    public static void clickConnect()
    {
	autoLoad = true;
	//connectButton.doClick();

	Runnable doClickRunnable = new Runnable() {
		public void run() { connectButton.doClick(); }
	    };
	SwingUtilities.invokeLater(doClickRunnable);
    }

    public void setController(ClientNetworkController cnc)
    {
	this.controller = cnc;
    }

    public ClientNetworkController getController()
    {
	return this.controller;
    }
    // End Get and Set Methods
    /////////////////////////////////////////////////////////////////////////////////
}

