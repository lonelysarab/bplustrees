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
 * ClientAppPrototype.java
 *
 * Created on May 17, 2002, 12:40 AM
 */
package jhave.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import jhave.Algorithm;
import jhave.client.misc.AlgorithmComboBoxModel;
import jhave.client.misc.IllegalPropertyException;
import jhave.client.misc.PrintStreamTextArea;
import jhave.core.JHAVETranslator;
import jhave.core.TransactionCodes;
import jhave.core.Visualizer;
import jhave.event.NetworkEvent;
import jhave.event.NetworkListener;
import jhave.event.QuestionEvent;
import jhave.event.QuestionListener;
import jhave.question.QuestionFactory;
import translator.TranslatableGUIElement;
/**
 * The main client GUI for the JHAVE Project.
 * @author Chris Gaffney
 */
public class Client extends JFrame implements NetworkListener, TransactionCodes {
    
    ///////////////////////////////////////////////////////////////////////////
    // Constants
    
    /** Version number as a string, should use major.minor convention. */
    public static final String VERSION_STRING = "2.0";
    /** Version number as a double, used for comparisons, should use major.minor convention.*/
    public static final double VERSION_NUMBER = 2.0;

    /** Index of the Connection tab in the Tabbed pane */
    private static final int CONNECTION_TAB_POSITION = 0;
    /** Index of the Setup tab in the Tabbed pane */
    private static final int SETUP_TAB_POSITION = 1;
    /** Index of the visualizer tab in the Tabbed pane.*/
    private static final int VISUALIZER_TAB_POSITION = 2;
    /** Index of the Debug tab in the Tabbed pane */
    private static final int DEBUG_TAB_POSITION = 3;
    /** The location of the script directory. */
    private static final String SCRIPT_DIRECTORY = "scripts" + File.pathSeparatorChar;
    /** File name for the splash screen displayed */
    private static final String JHAVE_SPLASH_LOGO_FILENAME = "jhave/client/graphics/jhave_splash_logo.png";
    /** File name for the frame icon. */
    private static final String JHAVE_FRAME_ICON = "jhave/client/graphics/frame_icon.png";
    
    // End Constants
    ///////////////////////////////////////////////////////////////////////////
    // Globals
    
    /** The Main GUI that holds all the Tabs */
    private JTabbedPane mainGUI;
    /** The panel that shows the connection history. */
    private ConnectionPanel connectionPanel;
    /** The ClientProperties object that contains what connection properties we are using. */
    private ClientProperties clientProperties;
    /** The ClientNetworkController that manages the connection with the server. */
    private ClientNetworkController networkControl;
    /** If the student is in the process of taking a quiz. */
    private boolean isTakingQuiz = false;
    /** If the user is in show answer mode */
    private boolean showAnswers = false;
    /** Checkbox for showing or not showing questions. */
    private boolean showQuestions = true;
    /** Checkbox for hearing or not not hearing audio. */
    private static boolean audioActive = true;
    /** The QuizInfo object that contains quiz properties */
    private QuizInfo quizInfo;
    
    /** */
    private String WEBROOT = "";
    /** */
    private String default_dir_for_local_mode = ".";
    /** */
    private Map visualizerClasses = null;
    /** */
    private Visualizer currentVisualizer = null;
    /** */
    private ControlPanel visController = null;
    
    /** Debug setting. */
    private static boolean debug = false;

    private static String username = null;

    // Should Jhave auto load a visualization
    private static boolean autoLoad = false;
    public static String autoLoadInfo = null;

    private static boolean startUpQuizMode = false;

    private Component parent = getParent();

    private SetupPanel setupPanel;
   
    // End Globals
    ///////////////////////////////////////////////////////////////////////////
    // Menu Items
    
    /** Menu item for the login command. */
    private JMenuItem loginMenuItem = null;
    //      new JMenuItem("Login", KeyEvent.VK_L);
    /** Menu item for the quiz mode check box. */
    //    private JCheckBoxMenuItem quizModeMenuItem =
    //      new JCheckBoxMenuItem("Quiz Mode", false);
    //    private JMenuItem quizModeMenuItem = null;
    private JCheckBoxMenuItem quizModeMenuItem = new JCheckBoxMenuItem("Quiz Mode", false);;
    /** Menu item for the show answers mode check box. */
    private JCheckBoxMenuItem showAnswersModeMenuItem = new JCheckBoxMenuItem("Show Answers", false);
    
    // End Menu Items
    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    
    /**
     * Instantiates a new Client without added options.
     */
    public Client() {
        this(null);
    }
    
    /**
     * Instantiates a new Client with given command line arguments.
     * @param args command line arguments.
     */
    public Client(String[] args) {
        super("JHAV\u00C9 " + VERSION_STRING);
        // Show the user a splash screen while loading.
        ClassLoader cl = getClass().getClassLoader();
        Image splashImage = Toolkit.getDefaultToolkit().createImage(cl.getResource(JHAVE_SPLASH_LOGO_FILENAME));
        SplashScreen splash = new SplashScreen(splashImage);
        splash.setVisible(true);
        
        String commandLineCategory = null;
        String commandLineServer = null;
        String commandLineWEBROOT = null;

	// Needed for the freetts voices to work
	System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        
        // Check the command line arguments.
        if(args != null) {
            for(int index = 0; index < args.length; index++) {
                if(args[index].equalsIgnoreCase("-debug")) {
                    setDebug(true);
                } if(args[index].equalsIgnoreCase("-q")) {
                    startUpQuizMode = true;
                } else if(args[index].equals("-c")) {
                    // The user has specified a certain category at startup. The next token should contain the category
                    index++;
                    if(!args[index].startsWith("-")) {
                        commandLineCategory = args[index];
                    } else {
                        // Print the help message and exit.
                        System.out.println(printHelp());
                        System.exit(0);
                    }
                } else if(args[index].equals("-u")) {
                    // The user has specified a username at startup,
                    // hence enabling potential connectiont to DB. The
                    // next token should contain the category
                    index++;
                    if(!args[index].startsWith("-")) {
                        username = args[index];
                    } else {
                        // Print the help message and exit.
                        System.out.println(printHelp());
                        System.exit(0);
                    }
                } else if(args[index].equals("-s")) {
                    // The user has specified a certain server at startup. The next token should contain the server
                    index++;
                    if(!args[index].startsWith("-")) {
                        commandLineServer = args[index];
                    } else {
                        // Print the help message and exit.
                        System.out.println(printHelp());
                        System.exit(0);
                    }
                    
                } else if(args[index].equals("-w")) {
                    // The user has specified a certain WEBROOT at startup. The next token should contain the WEBROOT
                    index++;
                    if(!args[index].startsWith("-")) {
                        commandLineWEBROOT = args[index];
                    } else {
                        // Print the help message and exit.
                        System.out.println(printHelp());
                        System.exit(0);
                    }
                } else if (args[index].equalsIgnoreCase("-l") 
			   || args[index].equalsIgnoreCase("-locale")) {
		    // The user has specified a certain LOCALE to be used.
		    // The next token should describe the target Locale
		    index++;
		    if(!args[index].startsWith("-")) {
			String localeText = args[index];
                      
			if (localeText.equalsIgnoreCase("de_DE"))
			    JHAVETranslator.setTargetLocale(Locale.GERMANY);
			else
			    JHAVETranslator.setTargetLocale(Locale.US);
			//                    System.err.println("read '" +localeText +"', set Locale to "
			//                        +JHAVETranslator.getLocale().toString());
		    } else {
			// Print the help message and exit.
			System.out.println(printHelp());
			System.exit(0);
		    }
                } else if(args[index].equals("-d")) {
                    // The user has specified a certain default dir for local scripts at startup. The next token should contain the directory
                    index++;
                    if(!args[index].startsWith("-")) {
                        default_dir_for_local_mode = args[index];
                    } else {
                        // Print the help message and exit.
                        System.out.println(printHelp());
                        System.exit(0);
                    }
                } else if(args[index].equalsIgnoreCase("--help") || args[index].equals("-h")) {
                    // Print the help message and exit.
                    System.out.println(printHelp());
                    System.exit(0);
                } else if(args[index].equals("-r"))
		    {
			autoLoad = true;
			if (index + 1 < args.length && !args[index+1].startsWith("-"))
			    {
				autoLoadInfo = args[index+1];
				index++;
				//				System.out.println(autoLoadInfo);
			    }
			else {
			    // Print the help message and exit.
			    System.out.println(printHelp());
			    System.exit(0);
			}
		    }
		///////////////////TRAKLA START PARAMS///////////////////////////////////
		else if(args[index].equals("-tu"))
		    {
			if (index + 1 < args.length && !args[index+1].startsWith("-"))
			    {
				System.setProperty("jhave.client.TRAKLAusername",args[index+1]);
				index++;
			    }
			else {
			    // Print the help message and exit.
			    System.out.println(printHelp());
			    System.exit(0);
			}
		    }
		else if(args[index].equals("-ts"))
		    {
			if (index + 1 < args.length && !args[index+1].startsWith("-"))
			    {
				System.setProperty("jhave.client.TRAKLAseed",args[index+1]);
				index++;
			    }
			else {
			    // Print the help message and exit.
			    System.out.println(printHelp());
			    System.exit(0);
			}
		    }
		else if(args[index].equals("-tc"))
		    {
			if (index + 1 < args.length && !args[index+1].startsWith("-"))
			    {
				System.setProperty("jhave.client.TRAKLAcourse",args[index+1]);
				index++;
			    }
			else {
			    // Print the help message and exit.
			    System.out.println(printHelp());
			    System.exit(0);
			}
		    }
		else if(args[index].equals("-tq"))
		    {
			if (index + 1 < args.length && !args[index+1].startsWith("-"))
			    {
				System.setProperty("jhave.client.TRAKLAquizid",args[index+1]);
				index++;
			    }
			else {
			    // Print the help message and exit.
			    System.out.println(printHelp());
			    System.exit(0);
			}
		    }
		else if(args[index].equals("-tn"))
		    {
			if (index + 1 < args.length && !args[index+1].startsWith("-"))
			    {
				System.setProperty("jhave.client.TRAKLAnormalizedquizvalue",args[index+1]);
				index++;
			    }
			else {
			    // Print the help message and exit.
			    System.out.println(printHelp());
			    System.exit(0);
			}
		    }
		/////////////////////END TRAKLA START PARAMS//////////////////////////////////////////////////////
            }
        }
        
        try {
            // Find the file that contains the connetion properties. If the file doesn't
            // exist then use default properties.
            String propertiesFilename = null;
            if(getClass().getClassLoader().getResource(ClientProperties.CLIENT_PROPERTIES_FILENAME) != null) {
                propertiesFilename = getClass().getClassLoader().getResource(ClientProperties.CLIENT_PROPERTIES_FILENAME).getFile();
                
                // Now since the URL replaces spaces with %20 we do the opposite
                StringTokenizer propertiesTokenizer = new StringTokenizer(propertiesFilename, "%20");
                StringBuffer bufferedProperties = new StringBuffer();
                while(propertiesTokenizer.hasMoreTokens()) {
                    bufferedProperties.append(propertiesTokenizer.nextToken());
                    bufferedProperties.append(" ");
                }
                propertiesFilename = bufferedProperties.toString();
            }
            
            if(propertiesFilename != null) {
                clientProperties = new ClientProperties(new File(propertiesFilename));
            } else {
                clientProperties = new ClientProperties();
            }
        } catch (IllegalPropertyException e) {
            // There was a problem with one of the values.
            JOptionPane.showMessageDialog(this, e.getMessage());
            System.exit(-1);
        } catch (UnknownHostException e) {
            // Display message showing no connection to server was made and exit.
	    JOptionPane.showMessageDialog(this, 
					  JHAVETranslator.translateMessage("serverError"));
	    //            JOptionPane.showMessageDialog(this, "An error occured with the server. Check your internet connection or your JHAVE server address.");
            System.exit(-1);
        } catch (IOException e) {
            // Display message saying there was a problem loading default values.
	    JOptionPane.showMessageDialog(this, 
					  JHAVETranslator.translateMessage("defaultPropsError"));
	    //            JOptionPane.showMessageDialog(this, "An error occured while loading the default properties.");
            System.exit(-1);
        }
        
        // If the user specified a command line with a username we set it in the client properties.  Otherwise that property is set to null
	clientProperties.setUsername(username);
        
        // If the user specified a command line with a category we set it in the client properties.
        if(commandLineCategory != null) {
            clientProperties.setCategory(commandLineCategory);
        }
        
        // If the user specified a command line with a server we set it in the client properties.
        if(commandLineServer != null) {
            try {
                clientProperties.setServer(commandLineServer);
            } catch (UnknownHostException e) {
                // There was a problem with the server, so throw a message and close.
                JOptionPane.showMessageDialog(this, e.getMessage());
                System.exit(-1);
            }
        }
        
        // If the user specified a command line with a WEBROOT we set it in the client properties.
        if(commandLineWEBROOT != null) {
            try {
                clientProperties.setWebroot(commandLineWEBROOT);
            } catch (IllegalPropertyException e) {
                // There was a problem with the WEBROOT, so throw a message and close.
                JOptionPane.showMessageDialog(this, e.getMessage());
                System.exit(-1);
            }
        }
        
        try {
            networkControl = new ClientNetworkController(clientProperties);
	    setupPanel = new SetupPanel(networkControl, this);
	} catch (IOException e) {
            // Display message showing that the category list could not be downloaded and exit.
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
					  JHAVETranslator.translateMessage("errorLoadCategoryFile",
									   clientProperties.getCategoryList()));
	    //                "Error downloading category file. Please check: " + clientProperties.getCategoryList().toString());
            System.exit(-1);
        }
        networkControl.addNetworkListener(this);
        connectionPanel = new ConnectionPanel(networkControl);
        WEBROOT = clientProperties.getWebroot().toString();
        
        // Add panels to the content pane.
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(BorderLayout.CENTER, mainGUI = buildTabbedPane());
        getContentPane().add(BorderLayout.SOUTH, connectionPanel.getStatusPanel());
        setJMenuBar(buildMenuBar());
        setNonSetupTabsEnabled(false);
        
	JPanel tempPanel = new JPanel(new BorderLayout());
	tempPanel.add(BorderLayout.WEST, setupPanel.buildLogoPanel());
	tempPanel.add(BorderLayout.CENTER, setupPanel.buildCenterPanel(0));
	tempPanel.add(BorderLayout.SOUTH, setupPanel.buildCategoryPanel());
	mainGUI.setComponentAt(CONNECTION_TAB_POSITION, tempPanel);

        //addWindowListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(true);
        setLocationRelativeTo(null);
        
        // Get rid of the splash screen.
        splash.dispose();
        
        // Add the graphic
        try {
            Image icon = ImageIO.read(cl.getResource(JHAVE_FRAME_ICON));
            setIconImage(icon);
        } catch (Exception e) {
            // Just don't add it.
        }
 
	// If needed automatically click the connect button
	if(autoLoad) SetupPanel.clickConnect();
	//SetupPanel.connectButton.doClick();
	//SetupPanel.visualizerButton.doClick();
    }
    
    // End Constructors
    ///////////////////////////////////////////////////////////////////////////
    // GUI Build Methods
    
    /**
     * Builds the setup panel.
     * @return JPanel the setup panel.
     */
    private JPanel buildSetupPanel() {
        return new SetupPanel(networkControl);
    }
    
    /**
     * Builds a JTabbedPane which contains two tabs, one for System.out redirection
     * and one for System.err redirection.
     * @return JTabbedPane contains debug output area.
     */
    private JTabbedPane buildDebugPanel() {
        JTabbedPane returnedPane = new JTabbedPane(JTabbedPane.TOP);
	//        JHAVETranslator.getGUIBuilder().insertTranslatableTab("connectionTab",
	//            connectionPanel, returnedPane);
        returnedPane.addTab(JHAVETranslator.translateMessage("connectionTab"), 
			    connectionPanel);
        try {
            PrintStreamTextArea systemOut = new PrintStreamTextArea();
            PrintStreamTextArea systemErr = new PrintStreamTextArea();
            
            System.setOut(new PrintStream(systemOut.getOutputStream()));
            System.setErr(new PrintStream(systemErr.getOutputStream()));
            
            returnedPane.addTab("System.out", new JScrollPane(systemOut));
            returnedPane.addTab("System.err", new JScrollPane(systemErr));
        } catch (IOException e) {
            // Lets not do anything. The Tabs just won't be added.
        }
        
        return returnedPane;
    }
    
    /**
     * Builds the tabbed pane that contains the GUI
     * @return JTabbedPane the main GUI.
     */
    private JTabbedPane buildTabbedPane() {
        JTabbedPane returnedPane = new JTabbedPane(JTabbedPane.TOP);
        
        /* Code to setup tabs. Only here while layout is still in flux. */
        for(int index = 0; index == returnedPane.getTabCount(); index++) {
            switch(index) {
	    case CONNECTION_TAB_POSITION:
		returnedPane.addTab(JHAVETranslator.translateMessage("connectionTab"), 
				    new JPanel());
		//		    returnedPane.addTab("Connection", new JPanel());
		break;
	    case SETUP_TAB_POSITION:
		returnedPane.addTab(
				    JHAVETranslator.translateMessage("SetupTab"),
				    new JPanel());
		break;
	    case VISUALIZER_TAB_POSITION:
		returnedPane.addTab(JHAVETranslator.translateMessage("VisTab"),
				    new JPanel());
		//                   returnedPane.addTab("Visualizer", new JPanel());
		break;
	    case DEBUG_TAB_POSITION:
		if(debug) {
		    returnedPane.addTab(
					JHAVETranslator.translateMessage("DebugTab"),
					buildDebugPanel());
		}
		break;
            }
        }
        return returnedPane;
    }
    
    private JMenuItem aboutMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem pingMenuItem;
    private JMenuItem loadScriptMenuItem;
    private JMenuItem connectionConfigMenuItem;
    private JMenuItem showQuestionsMenuItem;
    private JMenuItem testValidLogin;
    private JMenuItem testOutsideTimeRange;
    private JMenuItem testInvalidUserID;
    private JMenuItem testInvalidPassword;
    private JMenuItem testValidXML;
    private JMenuItem testInvalidXML;
    private JMenuItem audioActiveMenuItem;
    
    /**
     * Builds the menu bar that is displayed along the top of the screen.
     * @return JMenuBar menu bar built by this function.
     */
    private JMenuBar buildMenuBar() {
        JMenuBar returnedMenuBar = new JMenuBar();
	// TODO Guido
        TranslatableGUIElement generator = JHAVETranslator.getGUIBuilder();
        aboutMenuItem = generator.generateJMenuItem("about");
        exitMenuItem = generator.generateJMenuItem("exit");
        pingMenuItem = generator.generateJMenuItem("checkLatency");
        loadScriptMenuItem = generator.generateJMenuItem("loadScript");
        connectionConfigMenuItem = 
	    generator.generateJMenuItem("connectionConfig");
        showQuestionsMenuItem = 
	    generator.generateToggleableJMenuItem("showQuestions", null, true);
        audioActiveMenuItem =
	    generator.generateToggleableJMenuItem("audioActive", null, 
						  audioActive);

        testValidLogin = generator.generateJMenuItem("testValidLogin");
        testOutsideTimeRange = 
	    generator.generateJMenuItem("outsideTimeRange");
        testInvalidUserID = generator.generateJMenuItem("testInvalidID");
        testInvalidPassword = generator.generateJMenuItem("testInvalidPassword");
        testValidXML = generator.generateJMenuItem("testValidXML");
        testInvalidXML = generator.generateJMenuItem("testInvalidXML");
	//        JMenuItem aboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
	//        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
	//        JMenuItem pingMenuItem = new JMenuItem("Check Latency", KeyEvent.VK_C);
	//        JMenuItem loadScriptMenuItem = new JMenuItem("Load Script", KeyEvent.VK_S);
	//        JMenuItem connectionConfigMenuItem = new JMenuItem("Connection Config", KeyEvent.VK_C);
	//        JMenuItem showQuestionsMenuItem = new JCheckBoxMenuItem("Show Questions", showQuestions);
	//        //FAKE CLIENT JN
	//        JMenuItem testValidLogin = new JMenuItem("Test Valid Login");
	//        JMenuItem testOutsideTimeRange = new JMenuItem("Test Outside Time Range");
	//        JMenuItem testInvalidUserID = new JMenuItem("Test Invalid User ID");
	//        JMenuItem testInvalidPassword = new JMenuItem("Test Invalid Password");
	//        JMenuItem testValidXML = new JMenuItem("Test Valid XML");
	//        JMenuItem testInvalidXML = new JMenuItem("Test Invalid XML");
        //FAKE CLIENT JN
	//        showQuestionsMenuItem.setMnemonic(KeyEvent.VK_S);
        loginMenuItem = 
	    JHAVETranslator.getGUIBuilder().generateJMenuItem("loginMenuItem");

	// The following code from Guido did not let me cast the
	// button to a JCheckBoxMenuItem, which I need to set its
	// state to true

	//         quizModeMenuItem = 
	//           JHAVETranslator.getGUIBuilder().generateToggleableJMenuItem("quizModeItem",
	//             null, true);

	// If the userName has been specified, then this user should be able to take a quiz

	// DEBUG OUTPUT
	System.out.println("In client with username " + (username != null ? username : "null")
			   + " quiz start up is " + (startUpQuizMode ? "true" : "false"));

	quizModeMenuItem.setEnabled(username != null);
	if (username != null && startUpQuizMode) {
	    showAnswersModeMenuItem.setSelected(false);
	    quizModeMenuItem.setState(true);
	    setQuizForGrade(true);
	}

        // Create Main Menus
        JMenu helpMenu = generator.generateJMenu("helpMenu");
        JMenu fileMenu = generator.generateJMenu("fileMenu");
        JMenu optionsMenu = generator.generateJMenu("optionsMenu");
	//        JMenu helpMenu = new JMenu("Help");
	//        JMenu fileMenu = new JMenu("File");
	//        JMenu optionsMenu = new JMenu("Options");
        //FAKE CLIENT JN
        JMenu testsMenu = generator.generateJMenu("testsMenu");
	//        JMenu testsMenu = new JMenu("Tests");
        //FAKE CLIENT JN
        
        // Set Mnemonics to main menus and sub menus
	//        helpMenu.setMnemonic(KeyEvent.VK_H);
	//        fileMenu.setMnemonic(KeyEvent.VK_F);
	//        optionsMenu.setMnemonic(KeyEvent.VK_O);
        //FAKE CLIENT JN
	//        testsMenu.setMnemonic(KeyEvent.VK_T);
        //FAKE CLIENT JN
        
        // Set the accelerator keys for certain menu items
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK, true));
        loginMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK, true));
        
        final JFrame parent = this;
        class MenuListener implements ActionListener {
            /**
             * Called when a user selects a menu item.
             * @param e event fired when a menu item is clicked.
             */
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getActionCommand());
                Object o = e.getSource();
                if (o == exitMenuItem) {
		    //                if(e.getActionCommand().equalsIgnoreCase("exit")) {
                    windowClosing(null);
                } else if (o == aboutMenuItem) {
		    //if(e.getActionCommand().equalsIgnoreCase("about")) {
                    // Hard coded about webroot, because other servers probably wont have the about text. (Guessing)
                    new AboutDialog().displayDialog(getParent());
                } else if (o == connectionConfigMenuItem) { 
		    //                  if(e.getActionCommand().equalsIgnoreCase("connection config")) {
                    clientProperties.showPropertiesDialog(getParent());
                } else if (o == pingMenuItem) {
		    //                  if(e.getActionCommand().equalsIgnoreCase("check latency")) {
                    try {
                        networkControl.requestPing();
                    } catch (IOException exception) {
                        // Do nothing here
                    }
                } else if (o == loginMenuItem) { 
		    //                  if(e.getActionCommand().equalsIgnoreCase("login")) {
                    networkControl.setNameAndPassword();
                } else if (o == quizModeMenuItem) { 
		    //                  if(e.getActionCommand().equalsIgnoreCase("quiz mode")) {
                    if(getCurrentVisualizer() == null) {
			showAnswersModeMenuItem.setSelected(false);
			setQuizForGrade(((JCheckBoxMenuItem)e.getSource()).getState());
                    } else {
                        quizModeMenuItem.setSelected(false);
                        //JOptionPane.showMessageDialog(getParent(), "You are currently viewing an algorithm and cannot enter quiz mode.\nClose the visualizer and try again.");
                        int response = JOptionPane.showConfirmDialog(
								     getParent(), 
								     JHAVETranslator.translateMessage("noQuizDuringViewingQuery"),
								     JHAVETranslator.translateMessage("noQDVHeader"),
								     JOptionPane.YES_NO_OPTION);
			//			int response = JOptionPane.showConfirmDialog(getParent(), "You are currently viewing an algorithm.  In order to enter quiz mode the visualizer must be closed, would you like to continue?", "Must close visualizer", JOptionPane.YES_NO_OPTION);
			if(response == 0) {
			    mainGUI.setEnabledAt(VISUALIZER_TAB_POSITION, false);
			    currentVisualizer = null;
			    quizModeMenuItem.setSelected(true);
			    setQuizForGrade(true);
			    mainGUI.setSelectedIndex(SETUP_TAB_POSITION);
			}
		    }
                } else if(e.getActionCommand().equalsIgnoreCase("show answers"))
		    {
			quizModeMenuItem.setSelected(false);
			setShowAnswers(((JCheckBoxMenuItem)o).getState());
		    } else if (o == loadScriptMenuItem) {
		    //		    } else if(e.getActionCommand().equalsIgnoreCase("load script")) {
		    //                    JFileChooser fileChooser = new JFileChooser(System.getProperty("lax.dir"));
		    //            default_dir_for_local_mode = System.getProperty("user.dir");
		    if (default_dir_for_local_mode == null)
			{
			    default_dir_for_local_mode = System.getProperty("user.dir");
			}
                    JFileChooser fileChooser = new JFileChooser(default_dir_for_local_mode);
		    //                    if (fileChooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
		    if (fileChooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
			default_dir_for_local_mode = fileChooser.getSelectedFile().getParent();
			// Build a dummy algorithm to pass to the visualizer
			Algorithm dummyAlgorithm = new Algorithm();
			dummyAlgorithm.SetAlgoName(fileChooser.getSelectedFile().getName());
			dummyAlgorithm.SetDescriptiveText(fileChooser.getSelectedFile().getName());
			dummyAlgorithm.SetDynamicStatus("static");
                        
                        // determine the extension of the chosen file
			String chosenFile = fileChooser.getSelectedFile().getName();
                        String extension = chosenFile.substring(chosenFile.lastIndexOf('.')+1);
                        default_dir_for_local_mode = fileChooser.getCurrentDirectory().getAbsolutePath();
			//			System.out.println(default_dir_for_local_mode);
                        // Figure out which visualizer to use based on the extension
                        if ("sam".equalsIgnoreCase(extension)) {
                            dummyAlgorithm.SetVisualizerType("samba");
                        } else if ("sho".equalsIgnoreCase(extension)) {
                            dummyAlgorithm.SetVisualizerType("gaigs");
                        } else if ("asu".equalsIgnoreCase(extension)) {
                            dummyAlgorithm.SetVisualizerType("animalscript");
                        } else if ("aml".equalsIgnoreCase(extension)) {
                            dummyAlgorithm.SetVisualizerType("animal");
                        } else if ("gaff".equalsIgnoreCase(extension)) {
                            dummyAlgorithm.SetVisualizerType("gaff");
                        } else if ("xaal".equalsIgnoreCase(extension)) {
                            dummyAlgorithm.SetVisualizerType("xaal");
                        } else if ("matrix".equalsIgnoreCase(extension)) {
			    dummyAlgorithm.SetVisualizerType("matrix");
                        }
                        
                        if (dummyAlgorithm.GetVisualizerType() == null
			    || dummyAlgorithm.GetVisualizerType().length() == 0)
			    JOptionPane.showMessageDialog(mainGUI,
							  JHAVETranslator.translateMessage("noVizFoundFor", extension));
                        else {
			    try {
				setCurrentVisualizer(dummyAlgorithm, fileChooser.getSelectedFile().toURL());
			    } catch (MalformedURLException exception) {
				JOptionPane.showMessageDialog(getParent(),
							      JHAVETranslator.translateMessage("errorLoadingScript"),
							      JHAVETranslator.translateMessage("errorLoadingScriptHeader"),
							      //                                "An error occured loading the designated script", 
							      //                                "Error loading script", 
							      JOptionPane.ERROR_MESSAGE);
			    } catch (IOException exception) {
				JOptionPane.showMessageDialog(getParent(), 
							      JHAVETranslator.translateMessage("errorLoadingScript"),
							      JHAVETranslator.translateMessage("errorLoadingScriptHeader"),
							      //                                "An error occured loading the designated script", 
							      //                                "Error loading script", 
							      JOptionPane.ERROR_MESSAGE);
			    }
			}
                    }
                } else if (o == showQuestionsMenuItem) { 
		    //                  if(e.getActionCommand().equalsIgnoreCase("show questions")) {
                    showQuestions = ((JCheckBoxMenuItem)o).isSelected();
                }
                else if(e.getActionCommand().equalsIgnoreCase("audio")) {
                    audioActive = ((JCheckBoxMenuItem)o).isSelected();
                }
                
                //FAKE CLIENT JN
                //These are the operations for the "fake" server and client.
                try {
		    if (o == testValidLogin) {
			//                    if (e.getActionCommand().equalsIgnoreCase("test valid login")) {
			//(e.getSource() == testValidLogin) {//
			System.out.println(JHAVETranslator.translateMessage("sendValidLogin"));
			//                        System.out.println("Sending valid login to server");
                        networkControl.sendLogin(FC_REQUEST_QUIZ_LOGIN, "hanoi login password");
                    } else if (o == testOutsideTimeRange) { 
			//                      if(e.getActionCommand().equalsIgnoreCase("test outside time range")) {
			System.out.println(JHAVETranslator.translateMessage("sendOutsideTimeRange"));
			//                        System.out.println("Sending quiz outside of valid time range to server");
                        networkControl.sendLogin(FC_REQUEST_QUIZ_LOGIN, "sorts login password");
                    } else if (o == testInvalidUserID) { 
			//                      if(e.getActionCommand().equalsIgnoreCase("test invalid user id")) {
			System.out.println(JHAVETranslator.translateMessage("sendInvalidID"));
			//                        System.out.println("Sending invalid user ID to server");
                        networkControl.sendLogin(FC_REQUEST_QUIZ_LOGIN, "hanoi leo password");
                    } else if (o == testInvalidPassword) { 
			//                      if(e.getActionCommand().equalsIgnoreCase("test invalid password")) {
			System.out.println(JHAVETranslator.translateMessage("sendInvalidPassword"));
			//                        System.out.println("Sending invalid password to server");
                        networkControl.sendLogin(FC_REQUEST_QUIZ_LOGIN, "hanoi login twit");
                    } else if (o == testValidXML) { 
			//                      if(e.getActionCommand().equalsIgnoreCase("test valid xml")) {
			System.out.println(JHAVETranslator.translateMessage("sendValidXML"));
			//                        System.out.println("Sending valid XML string to server");
                        BufferedReader br = new BufferedReader( new FileReader( "src/validXML.xml" ));
                        String xmlString = "";
                        int temp;
                        temp = br.read();
                        while( temp != -1 ) {
                            if( (char)temp == '\'' ) xmlString = xmlString + "\'";
                            else xmlString = xmlString + (char)temp;
                            temp = br.read();
                        }
                        networkControl.sendXMLString(FC_QUIZ_COMPLETED,xmlString);
                    } else if (o == testInvalidXML) { 
			//                      if(e.getActionCommand().equalsIgnoreCase("test invalid xml")) {
			System.out.println(JHAVETranslator.translateMessage("sendInvalidXML"));
			//                        System.out.println("Sending invalid XML string to server");
                        BufferedReader br = new BufferedReader( new FileReader( "src/invalidXML.xml"));
                        String xmlString = "";
                        int temp;
                        temp = br.read();
                        while( temp != -1 ) {
                            if( (char)temp == '\'' ) xmlString = xmlString + "\'";
                            else xmlString = xmlString + (char)temp;
                            temp = br.read();
                        }
                        networkControl.sendXMLString(FC_QUIZ_COMPLETED,xmlString);
                    }
                }catch(Exception ex){System.out.println(ex.toString());}
                //FAKE CLIENT JN
                
            }
        }
        
        
        // Add action listeners to menu items
        MenuListener menuListener = new MenuListener();
        exitMenuItem.addActionListener(menuListener);
        aboutMenuItem.addActionListener(menuListener);
        pingMenuItem.addActionListener(menuListener);
	showQuestionsMenuItem.setSelected(true);
        showQuestionsMenuItem.addActionListener(menuListener);
	audioActiveMenuItem.setSelected(true);
        audioActiveMenuItem.addActionListener(menuListener);
        //FAKE CLIENT JN
        testValidLogin.addActionListener(menuListener);
        testOutsideTimeRange.addActionListener(menuListener);
        testInvalidUserID.addActionListener(menuListener);
        testInvalidPassword.addActionListener(menuListener);
        testValidXML.addActionListener(menuListener);
        testInvalidXML.addActionListener(menuListener);
        //FAKE CLIENT JN
        
        //FIXME: Removed because I think it would be odd to enter a password but not log in immediatly.
        //loginMenuItem.addActionListener(menuListener);
        
        connectionConfigMenuItem.addActionListener(menuListener);
        quizModeMenuItem.addActionListener(menuListener);
        showAnswersModeMenuItem.addActionListener(menuListener);

        // If in debug mode add the load script menu item
        if(debug) {
            loadScriptMenuItem.addActionListener(menuListener);
            fileMenu.add(loadScriptMenuItem);
        }
        
        // Add menu items and sub menus to main menus
        // FIXME: See above.
        // optionsMenu.add(loginMenuItem);
        optionsMenu.add(connectionConfigMenuItem);
        optionsMenu.add(showQuestionsMenuItem);
        optionsMenu.add(audioActiveMenuItem);
        
        //fileMenu.add(optionsMenu);
        fileMenu.add(quizModeMenuItem);
	fileMenu.add(showAnswersModeMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        helpMenu.add(aboutMenuItem);
        helpMenu.add(pingMenuItem);
        //FAKE CLIENT JN
        testsMenu.add(testValidLogin);
        testsMenu.add(testOutsideTimeRange);
        testsMenu.add(testInvalidUserID);
        testsMenu.add(testInvalidPassword);
        testsMenu.add(testValidXML);
        testsMenu.add(testInvalidXML);
        //FAKE CLIENT JN
        
        // Add menus to the menu bar
        returnedMenuBar.add(fileMenu);
        returnedMenuBar.add(optionsMenu);
        returnedMenuBar.add(Box.createHorizontalGlue());
        returnedMenuBar.add(helpMenu);
        //FAKE CLIENT JN
        returnedMenuBar.add(testsMenu);
        //FAKE CLIENT JN
        
        return returnedMenuBar;
    }
    
    // End GUI Build Methods
    ///////////////////////////////////////////////////////////////////////////
    // Get, Set, and Is Methods
    
    /**
     * Enables/Disables all of the tabs other then the setup tab.
     * @param aFlag The state you want to set the tabs to
     */
    private void setNonSetupTabsEnabled(boolean aFlag) {
        for(int index = 0; index < mainGUI.getTabCount(); index++) {
            if(!(index == CONNECTION_TAB_POSITION || index == DEBUG_TAB_POSITION)) {
                mainGUI.setEnabledAt(index, aFlag);
            }
        }
        mainGUI.setSelectedIndex(CONNECTION_TAB_POSITION);
    }
    
    /**
     * Returns if the student is currently taking the quiz rather then just in quiz mode.
     * @return boolean if the student is taking a quiz.
     */
    public boolean isTakingQuiz() {
        return isTakingQuiz;
    }
    
    /**
     * Sets if the student is currently taking a quiz.
     * @param aFlag if the student is taking a quiz.
     */
    protected void setTakingQuiz(boolean aFlag) {
        quizModeMenuItem.setEnabled(!aFlag);
        loginMenuItem.setEnabled(!aFlag);
	showAnswersModeMenuItem.setEnabled(!aFlag);
        mainGUI.getComponentAt(SETUP_TAB_POSITION).setEnabled(!aFlag);
        isTakingQuiz = aFlag;
	if(aFlag) showAnswers = false;
    }
    
    /**
     * Set if the user is running in show answer mode.
     * @param showAnswers if the user is running in show answer mode.
     */
    public void setShowAnswers(boolean showAnswers)
    {
	this.showAnswers = showAnswers;
	if(showAnswers) setQuizForGrade(false);
	networkControl.setShowAnswers(showAnswers);
    }
 
    /**
     * Returns if the user is in show answer mode.
     * @return if the user is in show answer mode.
     */
    public boolean getShowAnswers()
    {
	return networkControl.getShowAnswers();
    }

    /**
     * Sets if the student is taking a quiz for a grade or not.
     * @param quizForGrade if student is taking quiz for grade.
     */
    public void setQuizForGrade(boolean quizForGrade) {
	if(quizForGrade) setShowAnswers(false);
	networkControl.setQuizForGrade(quizForGrade);
    }
    
    /**
     * Returns if the student is taking a quiz for a grade.
     * @return boolean if the student is taking a quiz for a grade.
     */
    public boolean getQuizForGrade() {
        return networkControl.getQuizForGrade();
    }
    
    /**
     *
     */
    public static boolean getAudioActive() {
        return audioActive;
    }
    
    /**
     *
     */
    public static boolean getDebug() {
        return debug;
    }
    
    /**
     *
     */
    public static void setDebug(boolean setting) {
        debug = setting;
    }
    
    /**
     *
     */
    protected Visualizer getVisualizer(String name, InputStream script) {
        return VisualizerLoader.loadByName(name, script);
    }
    
    protected Visualizer getCurrentVisualizer() {
        return currentVisualizer;
    }
    
    protected void setCurrentVisualizer(Algorithm a, URL u) throws IOException {
        // Setup and show the infinite progress panel
        /*InfiniteProgressPanel ipp;
	  if(getGlassPane() instanceof InfiniteProgressPanel) {
	  ipp = (InfiniteProgressPanel)getGlassPane();
	  ipp.setText(JHAVETranslator.translateMessage("prepVis"));
	  //            ipp.setText("Preparing Visualization...");
	  } else {
          ipp = new InfiniteProgressPanel(JHAVETranslator.translateMessage("prepVis"));
	  //            ipp = new InfiniteProgressPanel("Preparing Visualization...");
	  }
	  setGlassPane(ipp);
	  ipp.start();*/
        
        String name = a.GetVisualizerType();
        InputStream script = u.openStream();
        
        Visualizer v = getVisualizer(name, script);
        if(v != null) {
            currentVisualizer = v;
        } else {
	    JOptionPane.showMessageDialog(this, 
					  JHAVETranslator.translateMessage("cnlVis"));
	    //            JOptionPane.showMessageDialog(this, "Could not load visualizer");
            // Hide the infinite progress panel.
            //ipp.setVisible(false);
            return;
        }
        
        visController = new ControlPanel(v);
        
        class QuestionHandler implements QuestionListener {
            public void handleQuestion(QuestionEvent e) {
		if(isTakingQuiz() && !e.getQuestion().getDisplayed()) {
		    QuestionFactory.showQuizQuestion(e.getQuestion());
		    e.getQuestion().setDisplayed(true);
		    visController.setQuizLock(true);
		} else if(showAnswers && !isTakingQuiz) { 
		    QuestionFactory.showQuestionDialogWithAnswer(e.getQuestion());
		} else if(showQuestions && !isTakingQuiz()) {
                    QuestionFactory.showQuestionDialog(e.getQuestion());
                }
            }
        }
        v.addQuestionListener(new QuestionHandler());
        
	if(networkControl.isLoggedIn() && getQuizForGrade()){
	    quizInfo = new QuizInfo((networkControl.getSelectedAlgorithm()).GetAlgoName(),
				    networkControl.getUsername(), networkControl);
	    v.addQuestionListener(quizInfo);
	    visController.setQuizInfo(quizInfo);
	}
        
        mainGUI.setComponentAt(VISUALIZER_TAB_POSITION, visController);
        setNonSetupTabsEnabled(true);
        mainGUI.setSelectedIndex(VISUALIZER_TAB_POSITION);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        // Hide the infinite progress panel.
        //ipp.stop();
    }

    // End Get, Set, and Is Methods
    ///////////////////////////////////////////////////////////////////////////
    // Listener Methods
    
    /**
     * Method called when client is closed by conventional means. Won't allow the
     * user to exit if they are taking a test. Also asks the user to confirm that
     * they want to exit before actually exiting.
     * @param event window event describing window conditions.
     */
    public void windowClosing(WindowEvent event) {

	int returned = JOptionPane.showConfirmDialog(this, 
						     JHAVETranslator.translateMessage("reallyLeave"), 
						     JHAVETranslator.translateMessage("reallyLeaveOption"), 
						     JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	//	int returned = JOptionPane.showConfirmDialog(this, "Are you sure you want to leave?", "Really leave?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	if(returned == JOptionPane.YES_OPTION) {
	    try {
		if(isTakingQuiz() && !quizInfo.getQuizSent()) {
		    String xmlString = quizInfo.toString();
		    System.out.println(JHAVETranslator.translateMessage("sendXMLString", 
									xmlString));
		    //		    System.out.println("Sending XML String:\n" + xmlString);
		    networkControl.sendXMLString(FC_QUIZ_COMPLETED, xmlString);
		}
		networkControl.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    setVisible(false);
	    dispose();
	    System.exit(0);
	}
    }
    
    
    /**
     * Method invoked when any inbound transactions are made
     * @param networkEvent Event sent from the network controller
     */
    public void inboundTransaction(NetworkEvent networkEvent) {
        switch(networkEvent.getTransactionCode()) {
	case NetworkEvent.FS_SEND_STATIC_SCRIPT:
	case NetworkEvent.FS_SEND_GENERATED_SCRIPT:
	    StringTokenizer message = networkEvent.getMessageTokenized();
	    Algorithm visualizerAlgorithm = networkControl.getAlgorithmByName(message.nextToken());
                
	    // Step past the visualizer token
	    message.nextToken();

	    if(getShowAnswers()) showAnswers = true;
	    else showAnswers = false;

	    // Check to see if the quiz is for a grade.
	    if(getQuizForGrade()) {
		setTakingQuiz(true);
	    } else {
		setTakingQuiz(false);
	    }
                
	    try {
		URL scriptURL = new URL(message.nextToken());
		try {
		    setCurrentVisualizer(visualizerAlgorithm, scriptURL);
		} catch (IOException e) {
		    System.out.println(
				       JHAVETranslator.translateMessage("problemStarting", 
									scriptURL.toString()));
		    //                        System.out.println("Problem starting up visualizer with " + scriptURL.toString());
		    // FIXME - Do something to let the user know
		}
	    } catch (MalformedURLException e) {
		// FIXME - Do something
	    }
                
	    break;
	case NetworkEvent.FS_NO_QUIZ_IN_DATABASE:
	    JOptionPane.showMessageDialog(this,
					  JHAVETranslator.translateMessage("noTestInDB"),
					  JHAVETranslator.translateMessage("noTestInDBHeader"),
					  JOptionPane.ERROR_MESSAGE);
	    //                    "There is no test available for the algorithm you selected.\nPlease choose an algorithm assigned by the teacher", 
	    //                    "Test not in database", 
	    //                    JOptionPane.ERROR_MESSAGE);
	    break;
	case NetworkEvent.NO_TRANSACTION_CODE:
	    // Do nothing.
	    break;
        }
    }
    
    /**
     * Method invoked when any outbound transactions are made
     * @param networkEvent Event sent from the network controller
     */
    public void outboundTransaction(jhave.event.NetworkEvent networkEvent) {
	try {
	    if(quizInfo != null && quizInfo.getQuizSent()) JOptionPane.showMessageDialog(getParent(), "Your score is "+quizInfo.getNumCorrect()+" out of "+quizInfo.getNumQuestions());

	} catch (NullPointerException exc) {
	    exc.printStackTrace();
	} catch (Exception exc) {
	    exc.printStackTrace();
	}
    }
 
    // End Listener Methods
    ///////////////////////////////////////////////////////////////////////////
    // Print Help Method
    
    /**
     * This method is called to create a string that will be printed to the screen when -h or --help is added to the command line.
     * @return String the help message.
     */
    private static String printHelp() {
	//      For some reason, Guido's tranlator didn't work for
	//      this help message, so I temporarily converted back to
	//      hard-coded English

	//      return JHAVETranslator.translateMessage("jhaveClientHelp");
	StringBuffer helpMessage = new StringBuffer(200);
       
	helpMessage.append("JHAVE Client Version 2.0\n");
	helpMessage.append("\nCommand Line Options:\n");
	helpMessage.append("\t-c {category} \tSpecify the category of algorithms to use.\n");
	helpMessage.append("\t-s {server} \tSpecify the server.\n");
	helpMessage.append("\t-w {webroot} \tSpecify the webroot for documentation files.\n");
	helpMessage.append("\t-debug        \tDisplay the debug tab during execution.\n");
	helpMessage.append("\t-q        \tStart-up in quiz mode (must specify username).\n");
	helpMessage.append("\t-u {username} \tSpecify JHAVE username as an email address.\n");
	helpMessage.append("\t-r {algoName} \tautoload a particular visualization on startup \n\tThis visualization defaults to first on category list unless specified \n\tas colon-delimited string of the form in a category file: \n\te.g.\t-r Best_First_Search:bestfirstsearch:new_data_only:gaigs \n");
	helpMessage.append("\t-tu {TRAKLAusername} \tSpecify the TRAKLA student ID.\n");
	helpMessage.append("\t-ts {TRAKLAseed} \tSpecify the TRAKLA seed.\n");
	helpMessage.append("\t-tc {TRAKLAcourse} \tSpecify the TRAKLA course ID.\n");
	helpMessage.append("\t-tq {TRAKLAquizid} \tSpecify the TRAKLA quiz ID in x.y form.\n");
	helpMessage.append("\t-tn {TRAKLAnormalizedquizvalue} \tSpecify the TRAKLA quiz value.\n");
	helpMessage.append("\t-h or --help  \tDisplay this help message.\n");
	return helpMessage.toString();
    }
    
    // End Print Help Method
    ///////////////////////////////////////////////////////////////////////////
    // Main
    
    /**
     * Main method that launches the JHAVE Client written by Chris Gaffney.
     * @param args the command line arguments.
     */
    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Will never happen since if a native look and feel is not found
            // it will default to the normal look and feel.
        }
        new Client(args).setVisible(true);
    }

    public void resetConnection()
    {
	try
	    {
		networkControl = new ClientNetworkController(clientProperties);
		setupPanel.setController(networkControl);
	    } catch(IOException e){}
    }
    
    public void buildSetupTab(AlgorithmComboBoxModel algos)
    {
	JPanel tempPanel = new JPanel(new BorderLayout());
	tempPanel.add(BorderLayout.WEST, setupPanel.buildLogoPanel());
	tempPanel.add(BorderLayout.CENTER, setupPanel.buildCenterPanel(1));
	tempPanel.add(BorderLayout.SOUTH, setupPanel.buildComboBoxPanel(algos));
	mainGUI.setComponentAt(SETUP_TAB_POSITION, tempPanel);
	mainGUI.setEnabledAt(SETUP_TAB_POSITION, true);
	mainGUI.setSelectedIndex(SETUP_TAB_POSITION);
    }
    // End Main
    ///////////////////////////////////////////////////////////////////////////
}
