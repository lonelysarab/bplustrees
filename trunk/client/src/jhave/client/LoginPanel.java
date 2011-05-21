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
 * LoginPanel.java
 *
 * Created on July 1, 2002, 2:17 AM
 */
package jhave.client;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

import jhave.core.JHAVETranslator;
/**
 * JPanel that displays an area for a username and password that can then be used by another
 * class to login to the server.
 *
 * @author Chris Gaffney
 */
public class LoginPanel extends JPanel {
    
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    
    /** JTextfield where the user enters their user name. */
    private final JTextField userNameField = new JTextField(18);
    /** Label for the user name JTextfield. */
    private static final JLabel userNameLabel =
      JHAVETranslator.getGUIBuilder().generateJLabel("loginNameLabel");
      //new JLabel("Login name:");
    
    /** JPasswordField where the user enters their password. */
    private final JPasswordField passwordField = new JPasswordField(18);
    /** Label for the password JTextfield. */
    private static final JLabel passwordLabel =
      JHAVETranslator.getGUIBuilder().generateJLabel("passwordLabel");      
//      new JLabel("Password:");
    
    // End Constants
    /////////////////////////////////////////////////////////////////////////////////
    // Constructors
    
    /**
     * Creates a new instance of LoginPanel. FIXME: Will eventually be constructed
     * using a <code>ClientNetworkController</code> to be able to communicate with
     * the server.
     */
    public LoginPanel() {
        super(new BorderLayout());
        
        add(BorderLayout.NORTH, buildNameAndPasswordPanel());

        Border outerTitleBorder = 
          JHAVETranslator.getGUIBuilder().generateTitledBorder("userLoginBorder");
//        Border outerTitleBorder = BorderFactory.createTitledBorder("User login");
        Border innerEmptyBorder = BorderFactory.createEmptyBorder(2, 2, 0, 2);
        setBorder(BorderFactory.createCompoundBorder(outerTitleBorder, innerEmptyBorder));
    }
    
    // End Constructors
    /////////////////////////////////////////////////////////////////////////////////
    // Build GUI Methods
    
    /**
     * Builds the portion of the panel that contains the user name components.
     * @return JPanel contains the user name components.
     */
    private JPanel buildUserNamePanel() {
        JPanel returnedPanel = new JPanel();
        
        returnedPanel.add(userNameLabel);
        returnedPanel.add(userNameField);
        
        
        return returnedPanel;
    }
    
    /**
     * Builds the portion of the panel that contains the password components.
     * @return JPanel contains the password components.
     */
    private JPanel buildPasswordPanel() {
        JPanel returnedPanel = new JPanel();
        
        returnedPanel.add(passwordLabel);
        returnedPanel.add(passwordField);
        
        return returnedPanel;
    }
    
    /**
     * Glues user name and password panels.
     * @return JPanel glued version of the two panels.
     */
    private JPanel buildNameAndPasswordPanel() {
        JPanel returnedPanel = new JPanel();
        returnedPanel.setLayout(new BoxLayout(returnedPanel, BoxLayout.X_AXIS));
        
        returnedPanel.add(buildUserNamePanel());
        returnedPanel.add(Box.createHorizontalGlue());
        returnedPanel.add(buildPasswordPanel());
        
        return returnedPanel;
    }
    
    // End Build GUI Methods
    /////////////////////////////////////////////////////////////////////////////////
    // Get and Set Methods
    
    /**
     * Returns the username entered by the user.
     * @return String the username.
     */
    public String getUsername() {
        return userNameField.getText();
    }
    
    /**
     * Returns the password entered by the user as a char array.
     * @return char[] the password entered by the user.
     */
    public char[] getPassword() {
        return passwordField.getPassword();
    }
    
    // End Get and Set Methods
    /////////////////////////////////////////////////////////////////////////////////
}