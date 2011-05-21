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
 * LoadingSplashScreen.java
 *
 * Created on June 30, 2002, 6:21 PM
 */
package jhave.client;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * Splash screen that comes up while initial information is being loaded.
 * This was added so that people running the application know something
 * is happening.
 *
 * @author Chris Gaffney
 */
public class SplashScreen extends JWindow {
    /** 
     * Creates a new instance of LoadingSplashScreen.
     * @param parent Frame with which the splash screen is modal to.
     */
    public SplashScreen(Image image) {        
        ImageIcon icon = new ImageIcon(image);
        
        JPanel content = new JPanel(new BorderLayout());
        content.add(BorderLayout.CENTER, new JLabel(icon));
        
        setContentPane(content);
        setSize(icon.getIconWidth(), icon.getIconHeight());
        setLocationRelativeTo(null);
        setVisible(true);
    }
}