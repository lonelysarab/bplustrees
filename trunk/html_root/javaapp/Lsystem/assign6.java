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

//******************************************************************************
// LSystem.java:        Applet
//
//******************************************************************************
import java.applet.*;
import java.awt.*;

//==============================================================================
// Main Class for applet LSystem
//
//==============================================================================
public class LSystem extends Applet
{

        Label dirLabel, angleLabel, SLabel, FLabel, LLabel, RLabel, fLabel, levelLabel;
        TextField dirText, angleText, SText, FText, LText, RText, fText, levelText;
        Checkbox printMoves;
        TextArea moves;
        Button drawButton;
        Panel p;
        static final int TurtleFrameSize = 400;
        TurtleFrame drawingSurface;


        // LSystem Class Constructor
        //--------------------------------------------------------------------------
        public LSystem()
        {
                // TODO: Add constructor code as necessary here
        }

        // APPLET INFO SUPPORT:
        //              The getAppletInfo() method returns a string describing the applet's
        // author, copyright date, or miscellaneous information.
    //--------------------------------------------------------------------------
        public String getAppletInfo()
        {
                return "Name: LSystem\r\n" +
                       "Author: Tom Naps\r\n";
        }


        // The init() method is called by the AWT when an applet is first loaded or
        // reloaded.  Override this method to perform whatever initialization your
        // applet needs, such as initializing data structures, loading images or
        // fonts, creating frame windows, setting the layout manager, or adding UI
        // components.
    //--------------------------------------------------------------------------
        public void init()
        {
        // If you use a ResourceWizard-generated "control creator" class to
        // arrange controls in your applet, you may want to call its
        // CreateControls() method from within this method. Remove the following
        // call to resize() before adding the call to CreateControls();
        // CreateControls() does its own resizing.
        //----------------------------------------------------------------------
                resize(250, 350);
                p = new Panel();
                p.setLayout (new GridLayout (10,2) );
                dirLabel = new Label ("Start direction");
                p.add (dirLabel);
                dirText = new TextField("90",20);
                p.add (dirText);
                angleLabel = new Label ("Turn angle");
                p.add (angleLabel);
                angleText = new TextField("60");
                p.add (angleText);
                SLabel = new Label ("Start string");
                p.add (SLabel);
                SText = new TextField("F++F++F++");
                p.add (SText);
                FLabel = new Label ("F rewrite rule");
                p.add (FLabel);
                FText = new TextField("F-F++F-F");
                p.add (FText);
                LLabel = new Label ("L rewrite rule");
                p.add (LLabel);
                LText = new TextField("");
                p.add (LText);
                RLabel = new Label ("R rewrite rule");
                p.add (RLabel);
                RText = new TextField("");
                p.add (RText);
                fLabel = new Label ("f rewrite rule");
                p.add (fLabel);
                fText = new TextField("");
                p.add (fText);
                levelLabel = new Label ("Drawing Level");
                p.add (levelLabel);
                levelText = new TextField("1");
                p.add (levelText);
                drawButton = new Button ("Draw It!");
                p.add (drawButton);
                printMoves = new Checkbox("Print moves?");
        p.add (printMoves);
                add (p);
                moves = new TextArea(5,10);
                add (moves);
        drawingSurface = new TurtleFrame(this);
        drawingSurface.show(); 
        drawingSurface.hide(); 
                drawingSurface.resize(2*(drawingSurface.insets().left + drawingSurface.insets().right)  + TurtleFrameSize,       
                                         2*(drawingSurface.insets().top  + drawingSurface.insets().bottom) + TurtleFrameSize); 
        drawingSurface.init();
        drawingSurface.show(); 

        }

        // Place additional applet clean up code here.  destroy() is called when
        // when you applet is terminating and being unloaded.
        //-------------------------------------------------------------------------
        public void destroy()
        {
                // TODO: Place applet cleanup code as needed here
        }

        // LSystem Paint Handler
        //--------------------------------------------------------------------------
        public void paint(Graphics g)
        {
                        drawingSurface.show();
                        drawingSurface.toFront();
        }

        //              The start() method is called when the page containing the applet
        // first appears on the screen. The AppletWizard's initial implementation
        // of this method starts execution of the applet's thread.
        //--------------------------------------------------------------------------
        public void start()
        {
                // TODO: Place additional applet start code as needed here
        }
        
        //              The stop() method is called when the page containing the applet is
        // no longer on the screen. The AppletWizard's initial implementation of
        // this method stops execution of the applet's thread.
        //--------------------------------------------------------------------------
        public void stop()
        {
        }

        public boolean action( Event e, Object o) {
                
                if (e.target == drawButton)      {
                        drawingSurface.repaint();
                }
                return true;
        }




        // TODO: Place additional applet code as needed here

}

class TurtleFrame extends Frame {

        double xlow, xhigh, ylow, yhigh, xlen, ylen;
        int xoffset, yoffset;
        LSystem grammar;
        Turtle tu;
        double angle;

        public TurtleFrame (LSystem g) {
                super ("Fractal Drawing");
                grammar = g;
                angle = Double.valueOf( grammar.angleText.getText() ).doubleValue();
        }

        // You will have to complete the drawFractal method.  The parameters to this
        // method are:
        //
        //              g -- a Graphics drawing context
        //              n -- the current recursive drawing level (bottoming out at 1)
        //              grammarString -- the rewrite rule we are working with at this time
        //                  (the initial call to drawFractal is with the start string)
        //              t -- the drawing turtle
        //
        // Other global variables you should know about are:
        //
        //              angle -- the current turning angle for Turtle t
        //              grammar.FText.getText() -- the String containing the F rewrite rule
        //              grammar.LText.getText() -- the String containing the L rewrite rule
        //              grammar.RText.getText() -- the String containing the R rewrite rule
        //              grammar.fText.getText() -- the String containing the f rewrite rule
        void drawFractal(Graphics g, int n, String grammarString, Turtle t) {
           int i = 0;
           Turtle copy = null;
           if (grammarString.length() == 0) 
               return;  
           else
               while (i < grammarString.length()) {
                
                // Loop through grammarString, testing grammarString.charAt(i) for each
                // of its possibilities and taking the appropriate action in each case.
                // You have to complete this method in the 
                // programming part of the assignment

                i = i + 1;
               }  // end while
         } end drawFractal     



        public void init () {

        }

        public void paint ( Graphics g )  {
                  xlow = 0;
                  xhigh = 0;
                  ylow = 0;
                  yhigh = 0;
                  xlen = 0;
                  ylen = 0;
                  xoffset = 0;
                  yoffset = 0;
                  angle = Double.valueOf( grammar.angleText.getText() ).doubleValue();
                  tu = new Turtle (0.0,0.0,Double.valueOf( grammar.dirText.getText() ).doubleValue(),1.0);
                  grammar.moves.setText("");
          Fmove(g, Integer.valueOf( grammar.levelText.getText() ).intValue(), grammar.SText.getText(), 
                            tu, true);
                  tu = new Turtle (0.0,0.0,Double.valueOf( grammar.dirText.getText() ).doubleValue(),1.0);
          //Fmove(g, Integer.valueOf( grammar.levelText.getText() ).intValue(), grammar.SText.getText(), 
                            //tu, false);
          drawFractal(g, Integer.valueOf( grammar.levelText.getText() ).intValue(), grammar.SText.getText(), 
                            tu);
   } 

        int devX(double x) {

                double stretch;

                if (xlen == 0.0)
                        return(grammar.TurtleFrameSize/2);
                else {
                        stretch = (((double) grammar.TurtleFrameSize) - (2.0 * ((double) xoffset)))/xlen;
                        return ((int) Math.round((x-xlow)*stretch)+xoffset + insets().left/2);
                }
        }

        int devY(double y) {

                double stretch;

                if (ylen == 0.0)
                        return(grammar.TurtleFrameSize/2);
                else {
                        stretch = (((double) grammar.TurtleFrameSize) - (2.0 * ((double) yoffset)))/ylen;
                        return (grammar.TurtleFrameSize - (int)Math.round((y-ylow)*stretch) - yoffset + (insets().top/2));
                }
        }

        void Fmove(Graphics g, int n, String foobar, Turtle bud, boolean setscale) {
        int foo;
        Turtle temp = null;
                if (foobar.length() == 0) return;
        for (foo = 0; foo < foobar.length(); ++foo) {
                switch (foobar.charAt(foo)){
                        case 'F':
                                if (n > 1) {
                                        Fmove(g,n-1, grammar.FText.getText(), bud, setscale);
                               }
                                else {
                                        if (setscale){
                                                bud.Skip();
                                                bud.AdjustBounds(this);
                                                 if (grammar.printMoves.getState())
                                                        grammar.moves.appendText("F\n");
                                        }
                                        else {
                                                bud.Forward(this);
                                        }
                                }
                                break;
        
                        case 'L':
                                if (n > 1) {
                                        Fmove(g,n-1, grammar.LText.getText(), bud, setscale);
                                }
                                else {
                                        if (setscale){
                                                bud.Skip();
                                                bud.AdjustBounds(this);
                                                if (grammar.printMoves.getState())
                                                        grammar.moves.appendText("L\n");
                                        }
                                        else {
                                                bud.Forward(this);
                                        }
                                }
                                break;
        
                        case 'R':
                                if (n > 1) {
                                        Fmove(g,n-1, grammar.RText.getText(), bud, setscale);
                                }
                                else {
                                        if (setscale){
                                                bud.Skip();
                                                bud.AdjustBounds(this);
                                                if (grammar.printMoves.getState())
                                                        grammar.moves.appendText("R\n");
                                        }
                                        else {
                                                bud.Forward(this);
                                        }
                                }
                                break;
        
                        case 'f':
                                if (n > 1) {
                                        Fmove(g,n-1, grammar.fText.getText(), bud, setscale);
                                }
                                else {
                                        if (setscale){
                                                bud.Skip();
                                                bud.AdjustBounds(this);
                                                if (grammar.printMoves.getState())
                                                        grammar.moves.appendText("f\n");
                                        }
                                        else {
                                                bud.Skip();
                                        }
                                }
                                break;
        
                        case '+': 
                                bud.Left(angle);
                                if ((setscale) && grammar.printMoves.getState())
                                                        grammar.moves.appendText("+\n");
                                break;
                                
                        
                        case '-': 
                                bud.Right(angle);
                                if ((setscale) && grammar.printMoves.getState())
                                                        grammar.moves.appendText("-\n");
                                break;
                                
                                
                        case '[':
                                if (n > 0) {
                                        temp = new Turtle(bud);
                                }
                                else {
                                        if (setscale){
                                                temp = new Turtle(bud);
                                                bud.AdjustBounds(this);
                                                if (grammar.printMoves.getState())
                                                        grammar.moves.appendText("[\n");
                                        }
                                        else {
                                        }
                                }
                                break;
                                
                                
                        case ']':
                                if (n > 0) {
                                        bud.x = temp.x;
                                        bud.y = temp.y;
                                        bud.a = temp.a;
                                }
                                else {
                                        if (setscale){
                                                bud.x = temp.x;
                                                bud.y = temp.y;
                                                bud.a = temp.a;
                                                                                                bud.d = temp.d;
                                                                                                bud.c = temp.c;
                                                bud.AdjustBounds(this);
                                                if (grammar.printMoves.getState())
                                                     grammar.moves.appendText("]\n");
                                        }
                                        else {
                                        }
                                }
                                break;
                                
                                                case ' ':   break;  // ignore spaces
                        default: if (grammar.printMoves.getState())
                                     grammar.moves.appendText(foobar.charAt(foo) + "not valid\n");

                }
        }
  }      


}

class Turtle {

            double x,y;                                     // turtle position
        double a;                                       // angle alpha in degrees
        double d;                                       // distance traveled in one step
        Color c;                                        // current turtle color

        public Turtle (double xcoord, double ycoord, double angle, double dist)  {
                x = xcoord;
                y = ycoord;
                a = angle;
                d = dist;
                c = Color.black;
        }

        public Turtle (Turtle t)  {
                x = t.x;
                y = t.y;
                a = t.a;
                d = t.d;
                c = t.c;
        }

        void AdjustBounds(TurtleFrame tf) {

                int numpix;
                
                if (x < tf.xlow)
                        tf.xlow = x;
                else if (x > tf.xhigh)
                        tf.xhigh = x;
                                if (tf.xhigh-tf.xlow > tf.xlen)
                                        tf.xlen = tf.xhigh-tf.xlow;
                        
                if (y < tf.ylow)
                        tf.ylow = y;
                else if (y > tf.yhigh)
                        tf.yhigh = y;
                                if (tf.yhigh-tf.ylow > tf.ylen)
                                        tf.ylen = tf.yhigh-tf.ylow;

                                if (tf.xlen < tf.ylen)  {
                                        numpix = (int) Math.round((tf.xlen/tf.ylen)*((double)tf.grammar.TurtleFrameSize));
                                        tf.xoffset = (tf.grammar.TurtleFrameSize - numpix)/2;
                                }
                                else 
                                        tf.xoffset = 0;

                                if (tf.ylen < tf.xlen)  {
                                        numpix = (int) Math.round((tf.ylen/tf.xlen)*((double)tf.grammar.TurtleFrameSize));
                                        tf.yoffset = (tf.grammar.TurtleFrameSize - numpix)/2;
                                }
                                else 
                                        tf.yoffset = 0;

        }
                                
   void Skip() {

           double radians;
       double newx, newy;

       radians = a * Math.PI / 180.0;
       x = x + (d * Math.cos(radians));
       y = y + (d * Math.sin(radians));
   }
                
   void Forward(TurtleFrame tf) {   // forward one step 

           double oldx, oldy;
                                          

       tf.getGraphics().setColor(c);
       //g.setColor(c);
           //tf.grammar.moves.appendText(tf.getGraphics().getColor().toString()+"\n");
       oldx = x;
           oldy = y;
       Skip();       // same as move without drawing
       //g.drawLine(tf.devX(oldx),tf.devY(oldy),
                          //tf.devX(x),tf.devY(y)); // Must convert to device coords
       tf.getGraphics().drawLine(tf.devX(oldx),tf.devY(oldy),
                          tf.devX(x),tf.devY(y)); // Must convert to device coords
   }
        
        // LOGO turns
        
   void Left(double angle) {
                a = a + angle;
                                if (a >= 360) a = a - 360;
                                else if (a < 0) a = a + 360;
                
   }
                                                
   void Right(double angle) {
        Left(-angle);
   }  
   
   void Forward(TurtleFrame tf, Graphics g) {   // forward one step 

           double oldx, oldy;
                                          

       //tf.getGraphics().setColor(c);
       g.setColor(c);
           //tf.grammar.moves.appendText(tf.getGraphics().getColor().toString()+"\n");
       oldx = x;
           oldy = y;
       Skip();       // same as move without drawing
       g.drawLine(tf.devX(oldx),tf.devY(oldy),
                          tf.devX(x),tf.devY(y)); // Must convert to device coords
       //tf.getGraphics().drawLine(tf.devX(oldx),tf.devY(oldy),
                          //tf.devX(x),tf.devY(y)); // Must convert to device coords
   }

   void setColor(Color co) {
       c = co;
   }
      
   void restoreToPrevious(Turtle tur) {
           a = tur.a;
           x = tur.x;
           y = tur.y;
           c = tur.c;
   }

}
