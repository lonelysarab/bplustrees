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
 * ClientProperties.java
 *
 * Created on June 14, 2002, 6:33 PM
 */

package jhave.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import jhave.client.misc.IllegalPropertyException;
import jhave.core.JHAVETranslator;

/**
 * This is a wrapper class for a properties file. Constructed with a File
 * it loads a properties object from this file. It then checks all the keys designated
 * and loads them into variables. These variables cannot be changed after instantiation.
 * If any of the properties are changed either by loading the defaults, or by modifying
 * them through the modification dialog they can be saved but wont be used until the next
 * time the object is created. This was done because the properties deal with the connection
 * between server and client. If any of these properties was changed then it would require
 * a new socket connection to the server. Something that at the current stage is best delt
 * with only at startup.
 *
 * @author Chris Gaffney
 * @version 1.0
 */
public class ClientProperties extends Object {
    //////////////////////////////////////////////////////////////////////////////////////
    // Key constants
    
    /** Key for the Server's URL. */
    private static final String SERVER_INETADDRESS_KEY = "Server_URL";
    /** Key for the Server's Port. */
    private static final String PORT_KEY = "Port";
    /** Key for the Webroot. */
    private static final String SERVER_WEBROOT_KEY = "Server_Webroot";
    /** Key for the Algorithm Category. */
    private static final String ALGORITHM_CATEGORY_KEY = "Algorithm_Category";
    
    // End key constants
    //////////////////////////////////////////////////////////////////////////////////////
    // Default values
    
    /** The relative location of the default properties file. */
    private static final String DEFAULTS_LOCATION = "jhave/client/default.properties";
    
    // End default values
    //////////////////////////////////////////////////////////////////////////////////////
    // Dialog variables
    
    /** Return value from showing properties dialog if Save is clicked. */
    public static final int SAVE_OPTION = JOptionPane.OK_OPTION;
    /** Return value from showing properties dialog if Cancel is clicked. */
    public static final int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;
    /** File from which connection properties are loaded. */
    public static final String CLIENT_PROPERTIES_FILENAME = "client.properties";
    
    // Text fields
    /** Textfield used to gather information about the servers location. */
    private static final JTextField serverInetAddressField = new JTextField(15);
    /** Textfield used to gather information about the servers port. */
    private static final JTextField portField = new JTextField(5);
    /** Textfield used to gather information about the webroot. */
    private static final JTextField webrootField = new JTextField(15);
    /** Textfield used to gather information about what gategory to use. */
    private static final JTextField categoryField = new JTextField(10);
    
    // Here we disable editing of the port text fields.
    static {
        portField.setEditable(false);
    }
    
    // Text field labels - Here for quick changes
    /** Label for the server address. */
    private static final String SERVER_INETADDRESS_LABEL =
    	JHAVETranslator.translateMessage("defaultServer");
//    	"Default Server Address";
    /** Label for the server port. */
    private static final String SERVER_PORT_LABEL =
    	JHAVETranslator.translateMessage("defaultServerPort");
//    "Server Port:";
    /** Label for the webroot. */
    private static final String SERVER_WEBROOT_LABEL =
    	JHAVETranslator.translateMessage("defaultWebroot");
//    "Webroot";
    /** Label for the category. */
    private static final String ALGORITHM_CATEGORY_LABEL = 
    	JHAVETranslator.translateMessage("defaultAlgCat");
//    private static final String ALGORITHM_CATEGORY_LABEL = "Default Algorithm Category";
    
    // Various other variables
    /** Dialog that is displayed. */
    private JDialog dialog;
    /** Value that is returned when a button is clicked. */
    private int returnedInt = CANCEL_OPTION;
    /** If the user is connected to the server. */
    private boolean isConnected = false;
    
    // End dialog variables
    //////////////////////////////////////////////////////////////////////////////////////
    // Properties variables
    
    /** The properties that this object wraps for */
    private static Properties clientProperties = new Properties();
    /** The file we load and save default properties to */
    private File propertiesFile;
    
    /** URL of the Server */
    private String serverAddress;
    /** Port to connect to on the server - Port the server is listening on */
    private int port;
    /** Webroot for all documentation */
    private String serverWebroot;
    /** URL of the Category file listing all the algorithms
     * Form: webroot + "cat/" + category + ".list"
     */
    private String category;
    /** If the defaults are being used */
    private boolean isDefaults = false;

    private String username = null;
    
    /** The only instance of client properties that we will need. */
    private static ClientProperties singleton = null;
    
    // End properties variables
    //////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a new instance of ClientProperties with default values.
     * This is the same as calling ClientProperties with a null constructor.
     * @throws IllegalPropertyException one of the set properties is illegal.
     * @throws UnknownHostException either internet connection is not available or ip address cannot be found.
     * @throws IOException there was a problem loading the default values.
     */
    public ClientProperties() throws IllegalPropertyException, UnknownHostException, IOException {
        this(null);
    }
    
    /**
     * Creates a new instance of ClientProperties. Given a file that contains the properties.
     * If the file is not found or is given a null parameter the client will load the default values.
     * @param propertiesFile File that contains the properties
     * @throws IllegalPropertyException one of the set properties is illegal.
     * @throws UnknownHostException either internet connection is not available or ip address cannot be found.
     * @throws IOException there was a problem loading the default values.
     */
    public ClientProperties(File propertiesFile) throws IllegalPropertyException, UnknownHostException, IOException {
        super();
        this.propertiesFile = propertiesFile;
        
        if(propertiesFile != null && propertiesFile.exists()) {
            try {
                clientProperties.load(new FileInputStream(propertiesFile));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                    JHAVETranslator.translateMessage("errorLoadingProps", 
                        propertiesFile.toString()));
//                    "Error loading properties file: " + propertiesFile.toString() + ". Loading default values.");
                clientProperties = getDefaults();
                isDefaults = true;
            }
        } else {
            clientProperties = getDefaults();
            isDefaults = true;
        }
        
        // Will throw UnknownHostException if not internet connection is available
        // or server IP address cannot be found.
        serverAddress = clientProperties.getProperty(SERVER_INETADDRESS_KEY);
        
        // Check to see if server port property is valid, otherwise throw an IllegalPropertyException
        port = verifyPort(clientProperties.getProperty(PORT_KEY).trim());
        
        // Check to see if the Webroot is a valid URL, otherwise throw a IllegalPropertyException
        //serverWebroot = verifyServerWebroot(clientProperties.getProperty(SERVER_WEBROOT_KEY).trim());
        serverWebroot = clientProperties.getProperty(SERVER_WEBROOT_KEY).trim();
        
        // Concatinates the category url and make sure its valid, otherwise throw a IllegalPropertyException
        category = clientProperties.getProperty(ALGORITHM_CATEGORY_KEY).trim();
    }
    
    /**
     *
     */
    public static ClientProperties getInstance() throws Exception {
        if(singleton == null) {
            singleton = new ClientProperties();
        }
        return singleton;
    }
    
    /**
     * Returns the InetAddress of the host server.
     * @return InetAddress Server to connect to.
     */
    public final String getServer() {
        return serverAddress;
    }
    
    /**
     * Returns the port to connect to on the server.
     * @return int Server port to connect to.
     */
    public final int getPort() {
        return port;
    }
    

    /**
     * Returns the username
     * @return The username (null if this has not been entered)
     */
    public final String getUsername() {
        return username;
    }
    
    /**
     * @param username -- the username
     */
    public final void setUsername(String username) {
        this.username = username;
    }
    


    /**
     * Returns the base webroot.
     * @return URL Webroot URL.
     */
    public final String getWebroot() {
        return serverWebroot;
    }
    
    /**
     * Returns the category of algorithms we are using.
     * @return The category of algorithms to use.
     */
    public final String getCategory() {
        return category;
    }
    
    /**
     * Allows the setting of a temporary category variable, allows the user to change a different category before connecting.
     * @param category the new category of algorithms to use.
     * @throws IllegalPropertyException the category given is not valid.
     */
    public final void setCategory(String category) {
        clientProperties.setProperty(ALGORITHM_CATEGORY_KEY, category);
        this.category = category;
    }
    
    /**
     * Changes the location of the server that the clients connect to.
     * @param server the new server to connect to.
     * @throws UnknownHostException the server address specified is not valid.
     */
    public final void setServer(String server) throws UnknownHostException {
            clientProperties.setProperty(SERVER_INETADDRESS_KEY, server);
            serverAddress = server;
    }
    
    
    /**
     * Changes the location of the WEBROOT that the clients connect to.
     * @param webroot the new webroot to connect to.
     * @throws UnknownHostException the webroot address specified is not valid.
     */
    public final void setWebroot(String webroot) throws IllegalPropertyException {
        if(!isConnected) {
            //URL webrootURL = verifyServerWebroot(webroot);
            clientProperties.setProperty(SERVER_WEBROOT_KEY, webroot);
            serverWebroot = webroot;
        }
    }
    
    /**
     * Sets if the user is connected to the server.
     * @param isConnected if the user is connected to the user.
     */
    public final void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
    
    /**
     * Returns if the user is connected to the server.
     * @return boolean if the user is connected to the server.
     */
    public final boolean getIsConnected() {
        return isConnected;
    }
    
    /**
     * Returns the URL of the category list file.
     * @return Category list location.
     */
    public final String getCategoryList() {
        return serverWebroot + "cat/" + category + ".list";
    }
    
    /**
     * Creates a properties file containing all default values as specified with
     * the JHAVE server at the University of Wisconsin Oshkosh
     * @return Properties A properties object containing all the default values
     * @throws IOException a problem occured loading the default values.
     */
    public final Properties getDefaults() throws IOException {
        Properties returnedProperties = new Properties();
        
        ClassLoader cl = getClass().getClassLoader();
        returnedProperties.load(cl.getResource(DEFAULTS_LOCATION).openStream());
        
        return returnedProperties;
    }
    
    /**
     * Convenience method to verify that a valid port is being used.
     * @param port the port to verify if it is a correct setting.
     * @return int the parsed value fo the port.
     * @throws IllegalPropertyException the server port could not be varified.
     */
    public int verifyPort(String port) throws IllegalPropertyException {
        int returnedInt = -1;
        
        try {
            returnedInt = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new IllegalPropertyException(PORT_KEY, port,
                JHAVETranslator.translateMessage("mustBeInt"),
//                "it must be an integer", 
                isDefaults);
        }
        
        return returnedInt;
    }
    
    /**
     * Convenience method to verify that a valid webroot is being used.
     * @param webroot the webroot to verify if it is valid or not.
     * @return URL the parsed value for the webroot.
     * @throws IllegalPropertyException the webroot could not be varified.
     */
    public URL verifyServerWebroot(String webroot) throws IllegalPropertyException {
        URL returnedWebroot;
        // Test if the web root ends with a /
        if(!webroot.endsWith("/")) {
            webroot += "/";
        }
        // Convert the webroot to a URL and make sure it is valid, otherwise throw a IllegalArgumentException
        try {
            returnedWebroot = new URL(webroot);
        } catch (MalformedURLException e) {
            throw new IllegalPropertyException(SERVER_WEBROOT_KEY, webroot,
                JHAVETranslator.translateMessage("notLegitURL"),
//                "it is not a legitimate URL", 
                isDefaults);
        }
        
        return returnedWebroot;
    }
    
    /**
     * Displays a dialog from which the connection properties can be modified.
     * @param parent component that the dialog is modal to.
     * @return int option (Save or Cancel) that the user selected.
     */
    public int showPropertiesDialog(Component parent) {
        serverInetAddressField.setText(clientProperties.getProperty(SERVER_INETADDRESS_KEY));
        portField.setText(clientProperties.getProperty(PORT_KEY));
        webrootField.setText(clientProperties.getProperty(SERVER_WEBROOT_KEY));
        categoryField.setText(clientProperties.getProperty(ALGORITHM_CATEGORY_KEY));
        
        dialog = buildDialog(parent);
        dialog.setVisible(true);
        dialog.dispose();
        dialog = null;
        return returnedInt;
    }
    
    /**
     * Builds the dialog for changing the client.properties file.
     * @param parent the parent component for the dialog.
     * @return JDialog the dialog that contains the components for modifying the client.properties file.
     */
    private JDialog buildDialog(Component parent) {
        Frame parentFrame = (parent instanceof Frame) ? (Frame)parent : (Frame)SwingUtilities.getAncestorOfClass(java.awt.Frame.class, parent);
        JDialog returnedDialog = new JDialog(parentFrame,
            JHAVETranslator.translateMessage("connConfig"),
//            "Connection Config", 
            true);
        returnedDialog.getContentPane().setLayout(new BorderLayout());
        
        returnedDialog.getContentPane().add(BorderLayout.CENTER, buildTextFieldPanel());
        returnedDialog.getContentPane().add(BorderLayout.SOUTH, buildButtonPanel());
        
        returnedDialog.pack();
        returnedDialog.setSize(320, returnedDialog.getHeight());
        returnedDialog.setResizable(false);
        returnedDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        returnedDialog.setLocationRelativeTo(parentFrame);
        return returnedDialog;
    }
    
    /**
     * Builds the panel that contains the text fields and labels.
     * @return JPanel containing the textfields.
     */
    private JPanel buildTextFieldPanel() {
        JPanel returnedPanel = new JPanel(new BorderLayout());
        
        returnedPanel.add(BorderLayout.NORTH, buildCategoryPanel());
        returnedPanel.add(BorderLayout.CENTER, buildServerPropertiesPanel());
        returnedPanel.add(BorderLayout.SOUTH, buildPortPanel());
        
        // Set the border and return
        Border outerBorder = 
          JHAVETranslator.getGUIBuilder().generateTitledBorder("connConfigBorder");
//        Border outerBorder = BorderFactory.createTitledBorder("Connection Config");
        Border innerBorder = BorderFactory.createEmptyBorder(1, 3, 2, 3);
        returnedPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
        return returnedPanel;
    }
    
    /**
     * Builds a panel that will editing of the local and server port.
     * @return JPanel panel that contains elements for modifying the local and server ports.
     */
    private JPanel buildPortPanel() {
        JPanel returnedPanel = new JPanel(new GridLayout(1, 2, 3, 2));
        
        // Add the server port label and field to the panel.
        returnedPanel.add(new JLabel(SERVER_PORT_LABEL));
        returnedPanel.add(portField);
        
        Border outerBorder =
          JHAVETranslator.getGUIBuilder().generateTitledBorder("portSettingsBorder");
//        Border outerBorder = BorderFactory.createTitledBorder("Port Settings");
        Border innerBorder = BorderFactory.createEmptyBorder(1, 3, 2, 3);
        returnedPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
        return returnedPanel;
    }
    
    /**
     * Builds a panel that will allow editing of the server and webroot properties.
     * @return JPanel panel that contains elements for modifying the server and webroot properties
     */
    private JPanel buildServerPropertiesPanel() {
        JPanel returnedPanel = new JPanel(new GridLayout(4, 1, 2, 2));
        
        // Add the server location label and field to the panel
        returnedPanel.add(new JLabel(SERVER_INETADDRESS_LABEL));
        returnedPanel.add(serverInetAddressField);
        
        // Add the webroot label and field to the panel
        returnedPanel.add(new JLabel(SERVER_WEBROOT_LABEL));
        returnedPanel.add(webrootField);
        
        Border outerBorder =
          JHAVETranslator.getGUIBuilder().generateTitledBorder("serverSettingsBorder");
//        Border outerBorder = BorderFactory.createTitledBorder("Server Settings");
        Border innerBorder = BorderFactory.createEmptyBorder(1, 3, 2, 3);
        returnedPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
        return returnedPanel;
    }
    
    /**
     *
     */
    private JPanel buildCategoryPanel() {
        JPanel returnedPanel = new JPanel(new GridLayout(2, 1));
        
        // Add the category field and label to the panel.
        returnedPanel.add(new JLabel(ALGORITHM_CATEGORY_LABEL));
        returnedPanel.add(categoryField);
        
        Border outerBorder = 
          JHAVETranslator.getGUIBuilder().generateTitledBorder("categoryBorder");
//        Border outerBorder = BorderFactory.createTitledBorder("Category");
        Border innerBorder = BorderFactory.createEmptyBorder(1, 3, 2, 3);
        returnedPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
        return returnedPanel;
    }
    
    /**
     * Builds the panel that contains the buttons for saving or canceling.
     * Also builds the buttons and adds their listeners.
     * @return JPanel contains the save and cancel buttons.
     */
    private JPanel buildButtonPanel() {
        final JPanel returnedPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 2));
        
        class ReturnListener extends Thread implements ActionListener {
            
            /**
             * Method invoked when the associated button is clicked.
             * @param event action event that was fired.
             */
            public void actionPerformed(ActionEvent event) {
                if(event.getActionCommand().equalsIgnoreCase("Save")) {
                    String errorMessage = "";
                    
                    // Check the server port
                    try {
                        verifyPort(portField.getText());
                    } catch (IllegalPropertyException e) {
                      errorMessage += JHAVETranslator.translateMessage("serverPortError",
                          e.getReason());
//                        errorMessage += "Server port: " + e.getReason() + "\n";
                    }
                    
                    // Check the server webroot
                    try {
                        verifyServerWebroot(webrootField.getText());
                    } catch (IllegalPropertyException e) {
                      errorMessage += JHAVETranslator.translateMessage("webrootError", 
                          e.getReason());
//                        errorMessage += "Webroot: " + e.getReason() + "\n";
                    }
                    
                    if(errorMessage.equals("")) {
                        clientProperties.put(SERVER_INETADDRESS_KEY, serverInetAddressField.getText());
                        clientProperties.put(PORT_KEY, portField.getText());
                        clientProperties.put(SERVER_WEBROOT_KEY, webrootField.getText());
                        clientProperties.put(ALGORITHM_CATEGORY_KEY, categoryField.getText());
                        
                        if(propertiesFile == null) {
                            JFileChooser jfc = new JFileChooser(".");
			    //                            JFileChooser jfc = new JFileChooser(System.getProperty("lax.dir"));
                            jfc.setSelectedFile(new File(CLIENT_PROPERTIES_FILENAME));
                            if(jfc.showSaveDialog(dialog) == jfc.APPROVE_OPTION) {
                                propertiesFile = jfc.getSelectedFile();
                            } else {
                                return;
                            }
                        }
                        try {
                            clientProperties.store(new FileOutputStream(propertiesFile), 
                                "Client Connection Properties - DO NOT MODIFY");
                            returnedInt = SAVE_OPTION;
                            // All changes that need to repaint should occur in the event queue.
                            JOptionPane.showMessageDialog(dialog, 
                                JHAVETranslator.translateMessage("propsSavedRestart"));
//                                "Properties saved succesfully.\nYou must restart the client before changes take effect.");
                            EventQueue.invokeLater(this);
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(dialog, 
                                JHAVETranslator.translateMessage("errorSavingProps"));
//                                "An error occured while trying to save properties file.\nProperties were not saved!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(returnedPanel, 
                            JHAVETranslator.translateMessage("errorsToBeFixed", 
                                errorMessage),
//                            "The following errors need to be fixed:\n" + errorMessage,
                                JHAVETranslator.translateMessage("propErrors"),
//                            "Properties Errors", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else if(event.getActionCommand().equalsIgnoreCase("Cancel")) {
                    returnedInt = CANCEL_OPTION;
                    // All changes that need to repaint should occur in the event queue.
                    EventQueue.invokeLater(this);
                }
            }
            
            /**
             * Executes when the thread is started.
             */
            public void run() {
                if(dialog != null) {
                    dialog.setVisible(false);
                }
            }
        }
        ReturnListener listener = new ReturnListener();
        
//        JButton saveButton = new JButton("Save");
//        saveButton.setMnemonic(KeyEvent.VK_S);
//        saveButton.addActionListener(listener);
        AbstractButton saveButton = 
          JHAVETranslator.getGUIBuilder().generateJButton("saveButton");
        saveButton.addActionListener(listener);
        saveButton.setActionCommand("Save");
        returnedPanel.add(saveButton);
        
        AbstractButton cancelButton = 
          JHAVETranslator.getGUIBuilder().generateJButton("cancelPropsButton");
        cancelButton.addActionListener(listener);
        cancelButton.setActionCommand("Cancel");
//       JButton cancelButton = new JButton("Cancel");
//        cancelButton.setMnemonic(KeyEvent.VK_C);
//        cancelButton.addActionListener(listener);
        returnedPanel.add(cancelButton);
        
        return returnedPanel;
    }
    
    /**
     * Main method, used to save defaults to a file or modify client properties file.
     * @param args the command line arguments.
     * @throws Exception a problem occured somewhere within the process.
     */
    public static void main(String[] args) throws Exception {
        ClientProperties cp;
        cp = new ClientProperties();
        
        // If this program was run outside of the client we allow changing the port fields
        portField.setEditable(true);
        cp.showPropertiesDialog(null);
        System.exit(0);
    }
}
