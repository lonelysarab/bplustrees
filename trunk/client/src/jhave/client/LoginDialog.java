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
 * LoginDialog.java
 *
 * Created on March 3, 2003, 12:41 PM
 */
package jhave.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jhave.core.JHAVETranslator;
/**
 * A dialog that displays a username and password field so the user can login
 * to the JHave server.
 * @author Chris Gaffney
 */
public class LoginDialog extends JComponent {
    
    /** Integer value returned when the user selects the login button. */
    public static final int LOGIN_OPTION = JOptionPane.OK_OPTION;
    /** Integer value returned when the user selects the cancel button. */
    public static final int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;
    
    /** The value returned when either the login or cancel button is clicked. */
    private int returnedInt = CANCEL_OPTION;
    /** The login button. */
    private AbstractButton loginButton =
      JHAVETranslator.getGUIBuilder().generateJButton("loginButton");
//    private JButton loginButton = new JButton("Login");
    /** The cancel button. */
    private AbstractButton cancelButton =
      JHAVETranslator.getGUIBuilder().generateJButton("cancelLoginButton");
//    private JButton cancelButton = new JButton("Cancel");
    /** The panel that holds the username and password JTextAreas. */
    private LoginPanel inputPanel = new LoginPanel();
    /** The dialog that is shown to the user. */
    private JDialog dialog = null;
    
    /** Creates a new instance of LoginDialog */
    public LoginDialog() {
    }
    
    /**
     * Shows the dialog to the user. 
     * @param parent the parent component of the dialog.
     * @return int the button that was clicked by the user.
     */
    public int showDialog(Component parent) {
        returnedInt = CANCEL_OPTION;
        
        dialog = createDialog(parent,
            JHAVETranslator.translateMessage("enterLogin"),
//            "Enter Login Name and Password", 
            true);
        dialog.setVisible(true);
        dialog.dispose();
        dialog = null;
        
        return returnedInt;
    }
    
    /**
     * Creates the dialog that is shown to the user.
     * @param parent the parent component of the dialog.
     * @param title the title of the dialog.
     * @param modal if the dialog is modal or not.
     * @return JDialog the dialog that is shown to the user.
     */
    private JDialog createDialog(Component parent, String title, boolean modal) {
        Frame frame = (parent instanceof Frame) ? (Frame)parent : (Frame)SwingUtilities.getAncestorOfClass(java.awt.Frame.class, parent);
        JDialog returnedDialog = new JDialog(frame, title, modal);
        
        returnedDialog.getContentPane().setLayout(new BorderLayout());
        returnedDialog.getContentPane().add(BorderLayout.CENTER, inputPanel);
        returnedDialog.getContentPane().add(BorderLayout.SOUTH, buildButtonPanel());
        
        returnedDialog.pack();
        returnedDialog.setResizable(false);
        returnedDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        returnedDialog.setLocationRelativeTo(frame);
        return returnedDialog;
    }
    
    /**
     * Builds the panel that contains the login and cancel buttons.
     * @return JPanel the panel that contains the login and cancel buttons.
     */
    private JPanel buildButtonPanel() {
        JPanel returnedPanel = new JPanel(new GridLayout(1, 2));
        
        returnedPanel.add(loginButton);
        returnedPanel.add(cancelButton);
        
        class ButtonActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
              if (e.getSource() == loginButton) {
//                if(e.getActionCommand().equalsIgnoreCase("login")) {
                    if(inputPanel.getUsername().length() < 1 || inputPanel.getPassword().length < 1) {
                        JOptionPane.showMessageDialog(getParent(), 
                            JHAVETranslator.translateMessage("fillInBoth"));
//                            "You must fill in both your user name and your password");
                    } else {
                        returnedInt = LOGIN_OPTION;
                    }
                } else if (e.getSource() == cancelButton) {
//                } else if(e.getActionCommand().equalsIgnoreCase("cancel")) {
                    returnedInt = CANCEL_OPTION;
                }
                if(dialog != null) {
                    dialog.setVisible(false);
                }
            }
        }
        loginButton.addActionListener(new ButtonActionListener());
        cancelButton.addActionListener(new ButtonActionListener());
        
        return returnedPanel;
    }
    
    /**
     * Returns the username entered by the user.
     * @return String the username entered by the user.
     */
    public String getUsername() {
        return inputPanel.getUsername();
    }
    
    /**
     * Returns the password entered by the user as a char array.
     * @return char[] the password entered by the user.
     */
    public char[] getPassword() {
        return inputPanel.getPassword();
    }
}