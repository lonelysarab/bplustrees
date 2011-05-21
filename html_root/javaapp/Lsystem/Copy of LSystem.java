//******************************************************************************
// LSystem.java:	Applet
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
	//		The getAppletInfo() method returns a string describing the applet's
	// author, copyright date, or miscellaneous information.
    //--------------------------------------------------------------------------
	public String getAppletInfo()
	{
		return "Name: LSystem\r\n" +
		       "Author: Tom Naps\r\n" +
		       "Created with Microsoft Visual J++ Version 1.0";
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

	//		The start() method is called when the page containing the applet
	// first appears on the screen. The AppletWizard's initial implementation
	// of this method starts execution of the applet's thread.
	//--------------------------------------------------------------------------
	public void start()
	{
		// TODO: Place additional applet start code as needed here
	}
	
	//		The stop() method is called when the page containing the applet is
	// no longer on the screen. The AppletWizard's initial implementation of
	// this method stops execution of the applet's thread.
	//--------------------------------------------------------------------------
	public void stop()
	{
	}

	public boolean action( Event e, Object o) {
		
		if (e.target == drawButton)	 {
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
          Fmove(g, Integer.valueOf( grammar.levelText.getText() ).intValue(), grammar.SText.getText(), 
			    tu, false);
          //drawFractal(g, Integer.valueOf( grammar.levelText.getText() ).intValue(), grammar.SText.getText(), 
			    //tu);
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

	void Fmove(Graphics g, int n, String Fstring, Turtle t, boolean setscale) {
        int i = 0;
        Turtle temp = null;
		if (Fstring.length() == 0) return;
        while (i < Fstring.length()) {
                switch (Fstring.charAt(i)){
                        case 'F':
                                if (n > 1) {
                                        Fmove(g,n-1, grammar.FText.getText(), t, setscale);
                               }
                                else {
                                        if (setscale){
                                                t.Skip();
                                                t.AdjustBounds(this);
                                                 if (grammar.printMoves.getState())
                                                        grammar.moves.appendText("F\n");
                                        }
                                        else {
                                                t.Forward(this);
                                        }
                                }
                                break;
        
                        case 'L':
                                if (n > 1) {
                                        Fmove(g,n-1, grammar.LText.getText(), t, setscale);
                                }
                                else {
                                        if (setscale){
                                                t.Skip();
                                                t.AdjustBounds(this);
                                                if (grammar.printMoves.getState())
                                                        grammar.moves.appendText("L\n");
                                        }
                                        else {
                                                t.Forward(this);
                                        }
                                }
                                break;
        
                        case 'R':
                                if (n > 1) {
                                        Fmove(g,n-1, grammar.RText.getText(), t, setscale);
                                }
                                else {
                                        if (setscale){
                                                t.Skip();
                                                t.AdjustBounds(this);
                                                if (grammar.printMoves.getState())
                                                        grammar.moves.appendText("R\n");
                                        }
                                        else {
                                                t.Forward(this);
                                        }
                                }
                                break;
        
                        case 'f':
                                if (n > 1) {
                                        Fmove(g,n-1, grammar.fText.getText(), t, setscale);
                                }
                                else {
                                        if (setscale){
                                                t.Skip();
                                                t.AdjustBounds(this);
                                                if (grammar.printMoves.getState())
                                                        grammar.moves.appendText("f\n");
                                        }
                                        else {
                                                t.Skip();
                                        }
                                }
                                break;
        
                        case '+': 
                                t.Left(angle);
                                if ((setscale) && grammar.printMoves.getState())
                                                        grammar.moves.appendText("+\n");
                                break;
                                
                        
                        case '-': 
                                t.Right(angle);
                                if ((setscale) && grammar.printMoves.getState())
                                                        grammar.moves.appendText("-\n");
                                break;
                                
                                
                        case '[':
                                if (n > 0) {
                                        temp = new Turtle(t);
                                }
                                else {
                                        if (setscale){
                                                temp = new Turtle(t);
                                                t.AdjustBounds(this);
                                                if (grammar.printMoves.getState())
                                                        grammar.moves.appendText("[\n");
                                        }
                                        else {
                                        }
                                }
                                break;
                                
                                
                        case ']':
                                if (n > 0) {
                                        t.x = temp.x;
                                        t.y = temp.y;
                                        t.a = temp.a;
                                }
                                else {
                                        if (setscale){
                                                t.x = temp.x;
                                                t.y = temp.y;
                                                t.a = temp.a;
												t.d = temp.d;
												t.c = temp.c;
                                                t.AdjustBounds(this);
                                                if (grammar.printMoves.getState())
                                                     grammar.moves.appendText("]\n");
                                        }
                                        else {
                                        }
                                }
                                break;
                                

                        default: if (grammar.printMoves.getState())
                                     grammar.moves.appendText(Fstring.charAt(i) + "not valid\n");

                }
        i++;
        }
  }      


	void drawFractal(Graphics g, int n, String Fstring, Turtle t) {
        int i = 0;
        Turtle temp = null;
		if (Fstring.length() == 0) return;
        while (i < Fstring.length()) {
                        if (Fstring.charAt(i) == 'F')
                                if (n > 1)
                                        drawFractal(g,n-1, grammar.FText.getText(), t);
                                else 
                                        t.Forward(this);
                        else if (Fstring.charAt(i) == 'L')
                                if (n > 1) 
                                        drawFractal(g,n-1, grammar.LText.getText(), t);
                                else 
                                        t.Forward(this);
                        else if (Fstring.charAt(i) == 'R')
                                if (n > 1) 
                                        drawFractal(g,n-1, grammar.RText.getText(), t);
                                else 
                                        t.Forward(this);
                        else if (Fstring.charAt(i) == 'f')
                                if (n > 1) 
                                        drawFractal(g,n-1, grammar.fText.getText(), t);
                                else 
                                        t.Skip();
                        else if (Fstring.charAt(i) == '+')
                                t.Left(angle);
                        else if (Fstring.charAt(i) == '-')
                                t.Right(angle);
                        else if (Fstring.charAt(i) == '[') {
                                if (n > 0) 
                                        temp = new Turtle(t);
						}
                        else if (Fstring.charAt(i) == ']')
                                if (n > 0) {
                                        t.x = temp.x;
                                        t.y = temp.y;
                                        t.a = temp.a;
                                }
        i = i + 1;
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

       oldx = x;
	   oldy = y;
       Skip();       // same as move without drawing
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

}