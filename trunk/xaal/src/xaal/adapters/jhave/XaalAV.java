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

package xaal.adapters.jhave;

import jhave.event.*;

import java.net.*;
import java.io.*;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.ArrayList;

import java.awt.Color;

import javax.swing.JOptionPane;

import jhave.core.*;
import jhave.question.*;
import jhave.event.*;

import jhave.core.Visualizer;
import xaal.objects.Animation;
import xaal.objects.Xaal;
import xaal.objects.graphical.GraphicalPrimitive;
import xaal.objects.graphical.Text;
import xaal.objects.graphical.Line;
import xaal.objects.graphical.Polyline;
import xaal.objects.graphical.Parabola;
import xaal.objects.graphical.Style;
import xaal.objects.animation.AnimationOperation;
import xaal.objects.animation.GraphicalOperation;
import xaal.objects.animation.NarrativeOperation;
import xaal.objects.animation.SeqOperation;
import xaal.objects.animation.ParOperation;
import xaal.objects.animation.graphical.MoveOperation;
import xaal.objects.animation.graphical.HideOperation;
import xaal.objects.animation.graphical.ShowOperation;
import xaal.objects.animation.graphical.ChangeStyleOperation;
import xaal.parser.ParserModule;
import xaal.parser.XmlParser;
import xaal.parser.modules.XaalAnimParserModule;
import xaal.parser.modules.XaalDSAnimParserModule;
import xaal.parser.modules.XaalGPAnimParserModule;
import xaal.parser.modules.XaalGPParserModule;
import xaal.parser.modules.XaalMainParserModule;

import org.jdom.*;
import org.jdom.input.*;
//import org.apache.xml.resolver.tools.CatalogResolver;

public class XaalAV extends Visualizer {

    public final static int NO_ANIMATION = 0;
    public final static int FORWARD      = 1;
    public final static int BACKWARD     = 2;

    public final static boolean debug = false;
    public static boolean SeenResultsFrame = false;
    public final static int preferred_width = 400;
    public final static int preferred_height = 400;

    public Snapshot[] snapshots;
    public int snapAt = 1;         //the snapshot that the graphics window is currently at
    private int snapTotal = 0;                //total number of snapshots
    private boolean scriptLoaded = false;     //Have we yet loaded a script?

    public Graphprim graphWin;              //this is the graphics window
    private double scaleFactor = 1.0;
    private boolean soundEnabled = false;   // set this to true to enable sound
    public static Hashtable audioCtlTable;   // table of audio text ques keyed by snapshot number

    public static Hashtable qCtlTable;                    // table of question ids keyed by snapshot number                                                    
    public static Hashtable docCtlTable;                  // table of documentation pages keyed by snapshot number                                             
    public static Hashtable pseudoCtlTable;               // table of pseudocode pages keyed by snapshot number                                                

    private Map questions;
    public static Collection questionCollection;
    //    public static Hashtable qTable;                 // table of questiodocs keyed by id


    public XaalAV(InputStream script) throws IOException {
        super(script);
        setCapabilities(CAP_CONTROLLABLE + CAP_STEP_FORWARD + CAP_STEP_BACKWARD + CAP_GOTO_FRAME + CAP_ZOOM);
        Locale.setDefault(Locale.US);

        init();

        readScript(script);
        snapAt = 1;  

        gotoFrame(0);
    }

    public void init() {
        audioCtlTable = new Hashtable();
        qCtlTable = new Hashtable();
        docCtlTable = new Hashtable();
        pseudoCtlTable = new Hashtable();

        graphWin = new Graphprim(this);                 //initialize the graphics window
    }


    public javax.swing.JComponent getRenderPane() {
        return graphWin;
    }

    /* Sets the script file for the visualizer to the contents of the specified
     * InputStream.  In this case, we should read a complete .sho file.  If the script
     * is successfully read, a true value is returned.  If any IO error occurs, a false
     * value is returned.  No action is taken once the script is loaded.
     */
    public boolean readScript(InputStream inStream){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            scriptLoaded = readXaalDocument(reader);

            inStream.close();
            return scriptLoaded;
        }
        catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(graphWin, "An error occured trying to load the animation: " + e.toString(), 
                    "Something is wrong", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


	    /*
	    String tmpfile = "copy_of_script.xaal.xaal";
	    OutputStream copy = new FileOutputStream( new File(tmpfile));

	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = inStream.read(buf)) > 0){
		copy.write(buf, 0, len);
	    }
	    inStream.close();
	    copy.close();

            BufferedReader reader = 
		new BufferedReader( new InputStreamReader( 
		  new FileInputStream ( new File( tmpfile ))));	    

	    scriptLoaded = readXaalDocument(reader);
            reader.close();
 
            reader =  new BufferedReader( new InputStreamReader( 
			  new FileInputStream ( new File( tmpfile ) )));


	    // reader.mark( 50000 );  // this works fine
	    //String line = reader.readLine();  
	    //System.out.println(line);
	    //reader.reset();
	    //line = reader.readLine();
	    //System.out.println(line);

	    // use second parse of XML tree for questions
	    // "temporary" until the XAAL parser can handle questions
	    SAXBuilder builder = new SAXBuilder();
	    //builder.setEntityResolver( new CatalogResolver() );
	    Document doc = builder.build( reader);

	    //reader.reset(); // this throws a
	                      // java.io.IOException: Stream closed
	       // at java.io.BufferedReader.ensureOpen(BufferedReader.java:97)

	    parseQuestions( doc.getRootElement() );


            reader.close();


            return scriptLoaded;
	}
        catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(graphWin, "An error occured trying to load the animation: " + e.toString(), 
					  "Something is wrong", JOptionPane.ERROR_MESSAGE);
            return false;
        }	
	    */	    

    //catch(FileNotFoundException ex){
    // System.out.println(ex.getMessage() + " in the specified directory.");
    // System.exit(0);
    //}
   

    private void parseQuestions( Element root )
    {
	Element qs = root.getChild("questions");

	if (qs!=null)
	try {
	    
	    questionCollection = 
		QuestionFactory.questionCollectionFromXML( qs );
	} catch(QuestionParseException e) {
	    System.out.println("ERROR: could not parse the questions.");
	}

        if(questionCollection != null) {
            Iterator itr = questionCollection.iterator();
            questions = new HashMap(questionCollection.size());
            while(itr.hasNext()) {
                //System.out.println("Going thru question collection");
                Question q = (Question)itr.next();
		String id = q.getID();
                questions.put(id , q);
		qCtlTable.put(new Integer(Integer.parseInt(id)), 
			      id); //add map snap -> q (assumes <= 1 q/snap)            
            }
	    

	    qCtlTable.put(new Integer(2), "2"); //add map snap -> q (assumes <= 1 q/snap)
        }


    }

    private boolean readXaalDocument(BufferedReader reader) throws Exception {
        XmlParser xp = new XmlParser();        
        xp.registerParserModule(XaalDSAnimParserModule.class.getName());
        xp.registerParserModule(XaalGPParserModule.class.getName());
        xp.registerParserModule(XaalGPAnimParserModule.class.getName());
        xp.registerParserModule(XaalAnimParserModule.class.getName());
        xp.registerParserModule(XaalMainParserModule.class.getName());
	xp.registerParserModule(JhaveQuestionParserModule.class.getName());
        xp.parse(reader);
        Xaal x = (Xaal) ParserModule.getProperty(
                                   XaalMainParserModule.XAAL_ROOT_OBJECT);

	Element qs = (Element)
	    ParserModule.getProperty(
		  JhaveQuestionParserModule.QUESTION_COLLECTION_PROPERTY);

	if (qs!=null)
	try {
	    questionCollection = 
		QuestionFactory.questionCollectionFromXML( qs );
	} catch(QuestionParseException e) {
	    System.out.println("ERROR: could not parse the questions.");
	}

        if(questionCollection != null) {
            Iterator itr = questionCollection.iterator();
            questions = new HashMap(questionCollection.size());
            while(itr.hasNext()) {
                //System.out.println("Going thru question collection");
                Question q = (Question)itr.next();
		String id = q.getID();
                questions.put(id , q);
		qCtlTable.put(new Integer(Integer.parseInt(id)), 
			      id); //add map snap -> q (assumes <= 1 q/snap)            
            }
        }

        // Iterator initialPrimitives = x.getInitial().getGraphicals();
	snapshots = new Snapshot[ x.getAnimation().getNumOperations()+1 ] ;
	ArrayList initialGP = (ArrayList) x.getInitial().getGraphicalsList();

	/* create one snapshot for each seqOperation in the animation */
        Iterator animOps = x.getAnimation().getOperations();
	while (animOps != null && animOps.hasNext())
	{
	    SeqOperation seqOp = (SeqOperation) animOps.next();
	    snapshots[snapTotal] = new Snapshot(seqOp.getNarrative(),
						seqOp.getOperationsList() );
	    snapTotal++;
	}

	/* last snapshot contains no forward animation operations */
	snapshots[snapTotal] = new Snapshot("Done",  null );
	snapTotal++;

	/* compute the backward operations */
	for(int s=0; s<snapshots.length-1; s++)
	{
	    snapshots[s].backward = new ArrayList(20);
	    ArrayList forward = snapshots[s].forward;
	    for(int parOpIndex=forward.size()-1; parOpIndex>=0; parOpIndex--)
	    {
		ParOperation parOp = (ParOperation) forward.get( parOpIndex );
		ParOperation newParOp = new ParOperation();
		Iterator ops = parOp.getOperations();
		while (ops.hasNext())
		{
		    GraphicalOperation op = (GraphicalOperation) ops.next();
		    GraphicalOperation op2 = null;
		    if (op instanceof MoveOperation)
		    {
			op2 = (MoveOperation) ((MoveOperation) op).clone();
			if (((MoveOperation)op2).getType().equals("translate"))
			{
			    ((MoveOperation)op2).getNewCoordinate().setX( 
				       - ((MoveOperation)op2).getNewCoordinate().getX() );
			    ((MoveOperation)op2).getNewCoordinate().setY( 
                                       - ((MoveOperation)op2).getNewCoordinate().getY() );
			}
			/* else: type = "move", which means:
			   delay (do not change the value of x */

		    }
		    else if (op instanceof ShowOperation)
		    {
			op2 = new HideOperation();
			op2.addObjects( op.getGraphicalPrimitives() );

		    }
		    else if (op instanceof HideOperation)
		    {
			op2 = new ShowOperation();
			op2.addObjects( op.getGraphicalPrimitives() );

		    }
		    else if (op instanceof ChangeStyleOperation)
		    {
			op2 = (ChangeStyleOperation) ((ChangeStyleOperation) op).clone();
			Style oldStyle = ((ChangeStyleOperation)op2).getStyle();
			if (oldStyle.getColor().equals(Color.BLACK))
			    oldStyle.setColor( Color.RED);
			else
			    oldStyle.setColor( Color.BLACK);
		    }
		    newParOp.addAnimationOperation(op2);
		}
		snapshots[s].backward.add( newParOp );
	    }
	}
	/* Build the sequence of GraphicalPrimitives for each snapshot */
	snapshots[0].graphicals = cloneArrayList( initialGP );
	for(int snap=1; snap<snapshots.length; snap++)
	{    
	    Iterator parOps = snapshots[snap-1].forward.iterator();
	    while (parOps!=null && parOps.hasNext())
	    {
		ParOperation parOp = (ParOperation)(parOps.next());
		Iterator ops = parOp.getOperations();
		while (ops!=null && ops.hasNext())
		{
		    /* op refers to/modifies elements in initialGP */
		    ((GraphicalOperation) ops.next()).apply();
		}
	    }
	    snapshots[snap].graphicals = cloneArrayList( initialGP );	 
	}

	for(int snap=0; snap<snapshots.length; snap++)
	    graphWin.createSnap(snapshots[snap].graphicals.iterator());

	/*
        while (a != null && a.hasNextOperation()) {
            AnimationOperation ao = a.getCurrentOperation();
            graphWin.createSnap(a.applyNextOperation());
            if (soundEnabled  && ao instanceof NarrativeOperation) {
                NarrativeOperation no = (NarrativeOperation) ao;
                if (no.getNarrative() != null && !no.getNarrative().trim().equals(""))
                    audioCtlTable.put(new Integer(snapTotal), no.getNarrative());
            }
            snapTotal++;
        }
	*/
        return true;
    }

    private ArrayList cloneArrayList(ArrayList l) 
	throws CloneNotSupportedException
    {
	ArrayList copy = new ArrayList( 30 );
	for(int i=0; i<l.size(); i++)
	{
	    GraphicalPrimitive gp = (GraphicalPrimitive) l.get(i);
	    if (gp instanceof Text)
		copy.add(((Text)gp).clone());
	    else if (gp instanceof Line)
		copy.add(((Line)gp).clone());
	    else if (gp instanceof Polyline)
		copy.add(((Polyline)gp).clone());
	    else if (gp instanceof Parabola)
		copy.add(((Parabola)gp).clone());
	    else
	    {
		System.err.println("GraphicalPrimitive not supported " +
				   "in cloneArrayList");
	    }

	}
	return copy;
    }

    public void zoom(double level) {
        graphWin.clickedScaleBar( ((int)(level * 100.0)) );
        scaleFactor = level;
    }

    public double getZoom() {
        return scaleFactor;
    }

    public int getCurrentFrame() {
        // return (snapAt - 1);
	return snapAt;
    }

    public int getFrameCount() {
        return snapTotal;
    }

    public void gotoFrame(int frame) {
        //snapAt = frame + 1;
	snapAt = frame;

        executeSnapAt( NO_ANIMATION );
    }

    public void stepForward() {
	/*
	  if ( snapAt + 1  > snapTotal )
	  snapAt = snapTotal;
	  else
	  snapAt += 1;
	*/
	if (snapAt < snapTotal-1)
	    snapAt++;

	    

	//System.out.println("go forward to snapshot " + snapAt);

        executeSnapAt( FORWARD );

        String s = (String)qCtlTable.get(new Integer(snapAt));
        if(s != null) {
            Question q = (Question)questions.get(s);
            if(q != null) {
                fireQuestionEvent(q);
            }
        }

    }

    public void stepBackward() {
	/*
	  if ( snapAt - 1 < 1 )
            snapAt = 1;
	  else
            snapAt -= 1; 
	*/
	if (snapAt > 0)
	    snapAt--;

        executeSnapAt( BACKWARD );
    }

    private void executeSnapAt(int DIRECTION) {
        graphWin.setVisible(true);

	URI u1 = (URI)docCtlTable.get(new Integer(snapAt));               

	try {
	    //u1 = new URI("http://localhost/~davidfurcy/html_root/test.html");
	    //u1 = new URI("completingTheSquare.php?;amp;line=" +	 (snapAt-1) +
	    //";amp;aval=1;amp;bval=234;amp;cval=-3");
	    if (snapAt==0)
	    {
		if (snapshots[0].getNarrative() != null)
		    u1 = new URI(snapshots[0].getNarrative());
	    }
	    else
	    {
		if (snapshots[snapAt-1].getNarrative() != null)
		    u1 = new URI(snapshots[snapAt-1].getNarrative());
	    }
	    //System.out.println( "snap = " + snapAt +
	    //snapshots[snapAt].getNarrative() );
	} catch (URISyntaxException e) {
	    e.printStackTrace();
	}
        if(u1 != null) {                                                        
            fireDocumentationEvent(u1, DocumentEvent.TYPE_INFORMATION_PAGE);
            fireDocumentationEvent(u1, DocumentEvent.TYPE_PSEUDOCODE_PAGE);
        }       


        graphWin.execute(snapAt,DIRECTION);

	/*
        String the_text = (String)audioCtlTable.get(new Integer(snapAt - 1));
        if(the_text != null) {
            fireAudioTextEvent(the_text);
        } 
	*/       
    }
}
