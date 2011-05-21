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

package gaigs2;
import java.awt.*;
import java.awt.event.*;
//import com.sun.java.swing.*;
import javax.swing.*;

class Alert extends JFrame implements WindowListener {
   private JButton b;
   private JLabel l;
   private JPanel p, p2;

   public Alert(String Message, boolean userkill) 
   {
       makeIt(Message, userkill);
   }

   public Alert(String message)
   {
       makeIt(message, true);
   }

   private void makeIt(String Message, boolean userkill)
   {
      p = new JPanel();
      p2 = new JPanel();
      l = new JLabel( Message );
      Container c = getContentPane();
      addWindowListener(this); //register ourself to receive window events

      if (userkill)
      {
          b = new JButton( "Ok" );
          p2.add( b );
      
          b.addActionListener(new okButtonListener()); //register a button listener
          c.add( "South", p2 );
      }

      p.add( l );
      c.add("Center", p);
      //setSize( 300, 200 ); //FIXME -- this is suboptimal
      pack();
      setTitle("Something is wrong");
      setVisible(true);      
      setLocation( 300, 300 );
   }

    /* null handlers to satisfy WindowListener implementation - we don't really care about
     * these events.
     */
   public void windowOpened(WindowEvent e)      { }
   public void windowClosed(WindowEvent e)      { }
   public void windowIconified(WindowEvent e)   { }
   public void windowDeiconified(WindowEvent e) { }
   public void windowActivated(WindowEvent e)   { }
   public void windowDeactivated(WindowEvent e) { }
   
   /* Kill the dialog when the user clicks the close button on the window manager */
   public void windowClosing(WindowEvent e)
   {
        kill();
   }
   
   /* Kill the dialog when the user clicks the OK button */
   private class okButtonListener implements ActionListener
   {
        public void actionPerformed(ActionEvent e)
        {
            kill();
        }
   }

   public void kill()
   {
      setVisible(false);
      dispose();
   }
}
