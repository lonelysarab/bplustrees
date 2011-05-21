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
 * AboutDialog.java
 *
 * Created on April 25, 2003, 2:18 AM
 */
package jhave.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;

import jhave.core.JHAVETranslator;
/**
 * A dialog that displays information about the JHAVE project as well as the client.
 * @author Chris Gaffney
 */
public class AboutDialog {
    
    /** Error message ot be displayed if loading fails */
    private static final String ERROR_MESSAGE =
      JHAVETranslator.translateMessage("errorLoadingPage");
//      "Error loading page...";
    /** Filename of the error display page. Used due to how the setText() method works */
    private static final String ERROR_PAGE_FILENAME = "jhave/client/docs/error_loading";
    /** The location of the about jhave page. */
    private static final String ABOUT_JHAVE_URL = "http://csf11.acs.uwosh.edu/jhave/html_root/doc/jhave.html";
    /** The location of the history page. */
    private static final String README_FILE = "jhave/client/docs/ReadMe";
//    private static final String README_FILE = "jhave/client/docs/ReadMe.html";
    /** The location of the about client page. */
    private static final String ABOUT_CLIENT_FILENAME = "jhave/client/docs/about_gvsu";
    /** The location of the graphic for switching to the about page. */
    private static final String ABOUT_GRAPHIC_FILENAME = "jhave/client/graphics/About24.gif";
    /** The location of the graphic for switching to the history page. */
    private static final String HISTORY_GRAPHIC_FILENAME = "jhave/client/graphics/History24.gif";
    /** The about message that was displayed in the original client (it is the top window). */
    private static JEditorPane aboutJhavePane = new JEditorPane();
    /** The pane that displays the information about the client. */
    private static JEditorPane aboutClientPane = new JEditorPane();
    /** If the about menu has been display already. */
    private static boolean hasBeenDisplayed = false;
    /** If the about or history menu is being displayed. */
    private static boolean aboutIsDisplayed = false;
    /** The button that is clicked to dispose the dialog. */
    private JButton okButton = new JButton("Ok");
    /** The JButton that is used for switching between about and history. */
    private static JButton switchButton = null;
    
    /**
     * Made the about dialogs static so that they sort of cache themselves and won't need
     * to be reloaded next time the about menu is displayed.
     */
    static {
        aboutJhavePane.setEditorKit(new HTMLEditorKit());
        aboutJhavePane.setEditable(false);
        
        aboutClientPane.setEditorKit(new HTMLEditorKit());
        aboutClientPane.setEditable(false);
        aboutClientPane.setBackground(new JPanel().getBackground());
        aboutClientPane.setPreferredSize(new Dimension(0, 90));
    }
    
    /**
     * Creates a new instance of AboutDialog.
     */
    public AboutDialog() {
        if(!hasBeenDisplayed || !aboutIsDisplayed) {
	    String localeAppendix = "_" + JHAVETranslator.getLocale() +".html";
//           String localeAppendix = JHAVETranslator.getLocale().getLanguage()
//           +"_" + JHAVETranslator.getLocale() +".html";
            ClassLoader cl = getClass().getClassLoader();
            // Load the about jhave page.
            try {
                aboutJhavePane.setPage(ABOUT_JHAVE_URL);
            } catch (IOException e) {
                try {
                    aboutJhavePane.setPage(cl.getResource(ERROR_PAGE_FILENAME  + localeAppendix));
                } catch (IOException innerE) {
                    aboutJhavePane.setText(ERROR_MESSAGE);
                }
            }
            
            // Since the about the client page never changes we only load it if this is the
            // first time loading the about page.
            if(!hasBeenDisplayed) {
                try {
                    aboutClientPane.setPage(cl.getResource(ABOUT_CLIENT_FILENAME + localeAppendix));
                } catch (IOException e) {
                    try {
                        aboutClientPane.setPage(cl.getResource(ERROR_PAGE_FILENAME  + localeAppendix));
                    } catch (IOException innerE) {
                        aboutClientPane.setText(ERROR_MESSAGE);
                    }
                }
            }
            
            // Set the various variables and icon.
            hasBeenDisplayed = true;
            aboutIsDisplayed = true;
            
            if(switchButton == null) {
                switchButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource(HISTORY_GRAPHIC_FILENAME)));
                
                switchButton.setPreferredSize(new Dimension(24, 24));
                switchButton.setToolTipText(
                    JHAVETranslator.translateMessage("switchAboutHistory"));
//                    "Switch between About and History information.");
                
                switchButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        ClassLoader cl = getClass().getClassLoader();
			String localeAppendix = "_" + JHAVETranslator.getLocale() +".html";
//                         String localeAppendix = JHAVETranslator.getLocale().getLanguage()
//                         +"_" + JHAVETranslator.getLocale() +".html";
                        if(aboutIsDisplayed) {
                            try {
                                aboutIsDisplayed = false;
                                aboutJhavePane.setPage(cl.getResource(README_FILE +localeAppendix));
                                switchButton.setIcon(new ImageIcon(cl.getResource(ABOUT_GRAPHIC_FILENAME)));
                            } catch (IOException exception) {
                                aboutJhavePane.setText(
                                    JHAVETranslator.translateMessage("errorLoadingRevision"));
//                                    "An error occured while loading the Revision History page.");
                            }
                        } else {
                            try {
                                aboutIsDisplayed = true;
                                aboutJhavePane.setPage(ABOUT_JHAVE_URL);
                                switchButton.setIcon(new ImageIcon(cl.getResource(HISTORY_GRAPHIC_FILENAME)));
                            } catch (IOException exception) {
                              aboutJhavePane.setText(JHAVETranslator.translateMessage("errorLoadingAbout"));
//                                aboutJhavePane.setText("An error occured while loading the About page.");
                            }
                        }
                    }
                });
            }
        }
    }
    
    /**
     * Builds the content pane for the dialog.
     * @return JPanel the panel that contains the content pane.
     */
    private JPanel buildContentPane() {
        JPanel returnedPanel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new BorderLayout());
        
        
        infoPanel.add(BorderLayout.CENTER, new JScrollPane(aboutJhavePane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        infoPanel.add(BorderLayout.SOUTH, new JScrollPane(aboutClientPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        
        returnedPanel.add(BorderLayout.CENTER, infoPanel);
        returnedPanel.add(BorderLayout.SOUTH, buildButtonPanel());
        
        returnedPanel.setBorder(
            JHAVETranslator.getGUIBuilder().generateTitledBorder("aboutJHAVEBorder"));
//        returnedPanel.setBorder(BorderFactory.createTitledBorder("About JHAV\u00C9"));
        return returnedPanel;
    }
    
    /**
     * Builds the panel that contains the ok button. We have a special
     * panel for this so it doesn't get stretched by the BorderLayout used
     * to arrange the components.
     * @return JPanel the panel that contains the ok button.
     */
    private JPanel buildButtonPanel() {
        JPanel returnedPanel = new JPanel(new BorderLayout());
        
        returnedPanel.add(BorderLayout.EAST, switchButton);
        returnedPanel.add(BorderLayout.CENTER, okButton);
        
        return returnedPanel;
    }
    
    /**
     * Displays an about dialog that contains information about JHAVE and the client.
     * @param parent the parent component of the about dialog.
     */
    public void displayDialog(Component parent) {
        Frame frame = (parent instanceof Frame) ? (Frame)parent : (Frame)SwingUtilities.getAncestorOfClass(java.awt.Frame.class, parent);
        final JDialog dialog = new JDialog(frame);
        dialog.setSize(440, 500);
        dialog.setResizable(false);
//        dialog.setTitle("About JHAV\u00C9");
        dialog.setTitle(JHAVETranslator.translateMessage("aboutJHAVE"));
        
        class OkButtonListener implements Runnable, ActionListener {
            public void run() {
                dialog.dispose();
            }
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(this);
            }
        }
        okButton.setMnemonic(KeyEvent.VK_O);
        
        okButton.addActionListener(new OkButtonListener());
        dialog.setContentPane(buildContentPane());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}