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
import java.net.*;
import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;
import org.apache.xml.resolver.tools.CatalogResolver;

import jhave.core.*;
import jhave.question.*;
import jhave.event.*;

public class GaigsAV extends Visualizer /*implements WindowListener */ {
    
    public final static boolean debug = false;
    public static String URLName = "gaigs.html";
    public final static boolean fancy = true;
    public static boolean clickedPrev = false;
    public static boolean SeenResultsFrame = false;
    public final static int animation_frame_interval = 10; // In millisecs
    public final static int preferred_width = 400;
    public final static int preferred_height = 400;
    
    public int snapAt[]={1,1,1,1};         //the snapshot that a specific window is currently at
    private int snapTotal=0;                //total number of snapshots
    private boolean scriptLoaded=false;     //Have we yet loaded a script?
    //    private URL BaseDoc = null;             //base URL (set in readScript) -- no longer needed -- TN 7/25/07
    public Graphprim graphWin;              //this is the graphics window
    private double scaleFactor;
    private Map questions;
    public static Collection questionCollection;
    //    public static Hashtable qTable;                 // table of questions keyed by id
    public static Hashtable qCtlTable;                    // table of question ids keyed by snapshot number
    public static Hashtable docCtlTable;                  // table of documentation pages keyed by snapshot number
    public static Hashtable pseudoCtlTable;               // table of pseudocode pages keyed by snapshot number
    public static Hashtable audioCtlTable;               // table of audio text ques keyed by snapshot number
    
    
    public GaigsAV(InputStream script) throws IOException {
        super(script);
        setCapabilities(CAP_CONTROLLABLE + CAP_STEP_FORWARD + CAP_STEP_BACKWARD + CAP_GOTO_FRAME + CAP_ZOOM);
	Locale.setDefault(Locale.US);
        
        init();
        
        // FOOBAR these calls were ripped out of the constrcutor
        readScript(script);
        runScript();

        if(questionCollection != null) {
            Iterator itr = questionCollection.iterator();
            questions = new HashMap(questionCollection.size());
            while(itr.hasNext()) {
                //System.out.println("Going thru question collection");
                Question q = (Question)itr.next();
                questions.put(q.getID(), q);
            }
        }
        
        try {
            fireDocumentationEvent(
                    new URI("http://csf11.acs.uwosh.edu/jhave/html_root/doc/gaigs.html"),
                    DocumentEvent.TYPE_INFORMATION_PAGE);
        } catch (URISyntaxException e) {
            // Tell us whats wrong, print out the stack trace, and skip the event
            // Then fix the URI (this should never happen in production since the URI is hard coded)
            e.printStackTrace();
        }
        gotoFrame(0);
    }
    
    public void init() {
        scaleFactor = 1.0;
        qCtlTable = new Hashtable();
        docCtlTable = new Hashtable();
        pseudoCtlTable = new Hashtable();
        audioCtlTable = new Hashtable();
        
        graphWin = new Graphprim(this);                 //initialize the graphics window
        snapAt[graphWin.workingWindow]=1;               //set the intial snapAt
        
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
	    reader.mark(100);
	    String line = reader.readLine();
	    reader.reset();

	    if( line.trim().charAt(0) == '<' )
		// assume it's an XML file
		scriptLoaded = readXMLScript(reader);
	    else
		scriptLoaded = readOldScript(reader);

	    inStream.close();
	    return scriptLoaded;
	}
	catch (Exception e) {
            e.printStackTrace();
            Alert alertWin =
            new Alert("An error occured trying to read the slideshow: " + e.toString());
            alertWin.setVisible(true);
            return false;
        }
    }

    protected boolean readXMLScript(BufferedReader reader) throws Exception {
	//	BaseDoc = null;
	
	//org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
	SAXBuilder builder = new SAXBuilder();
	builder.setEntityResolver( new CatalogResolver() );
	Document doc = builder.build(reader);
	Element root = doc.getRootElement();

	if( root.getName().compareTo("show") != 0 )
	    throw new Exception("Expected a \"show\" as the root element of the document.");

	java.util.List children = root.getChildren("snap");
	Iterator iterator = children.iterator();
	while( iterator.hasNext() ) {
	    Element snap = (Element) iterator.next();
	    graphWin.createSnap(snap);
	    snapTotal++;
	}

	//System.out.println("Non-questions parsed");

	Element questions = root.getChild("questions");
	if(questions != null)
	    graphWin.createQuestions(questions);

	return true;
    }

    /* Here we now read the SHO file
     *
     * We build a multiline string containing a single snapshot description,
     * including the ***^*** terminator.  We then create the snapshot itself
     * and move along to the next snapshot in the SHO until we reach the EOF.
     */
    protected boolean readOldScript(BufferedReader reader) throws Exception {
	//	BaseDoc = null;
	
	String inStr;
	String snapStr = "";
	
	inStr = reader.readLine();
	while (inStr != null){
	    //System.out.println("reading -- " + inStr);
	    snapStr += "\n" + inStr; //build the snapshot descriptor
	    if(inStr.equals("***^***")){
		//we've reached snapshot terminator.  build the sucker.
		graphWin.createSnap(snapStr);
		snapTotal++;
		// System.out.println("After creating snapshot in GaigsAV " + snapTotal);
		snapStr = "";
	    }
	    inStr = reader.readLine();
	}
	
	if (!snapStr.equals(""))
	    graphWin.createSnap(snapStr); //for STARTQUESTIONS

	return true;
    }
    
    // Runs the currently loaded script.  If no script is loaded, returns false
    public boolean runScript() {
        if (!scriptLoaded) return scriptLoaded;
        //         if(!GaigsAV.locked)
        //                 Next.setEnabled(true);
        //             graphWin.multiTrigger(); //show the appropriate number of views (as spec'd in SHO file)
        
        snapAt[0]=1;  //makes sure the first window is set to the first snapshot
        
        /* Is it safe to assume that getNumViews() <= snapTotal?
         * Yes, it is.  If getNumViews() > snapTotal, the last slide will be displayed
         * multiple times.  Oh well.  That's not really so bad.
         */
        for     (int x=1;x<graphWin.getNumViews();x++)
            snapAt[x]=x+1;
        
        return true;
    }
    
    public void zoom(double level) {
        graphWin.clickedScaleBar( ((int)(level * 100.0)) );
        scaleFactor = level;
    }
    
    public double getZoom() {
        return scaleFactor;
    }
    
    public int getCurrentFrame() {
        return (snapAt[0] - 1);
    }
    
    public int getFrameCount() {
        return snapTotal;
    }
    
    public void gotoFrame(int frame) {
        
        snapAt[0] = frame + 1;
        
        for (int fr = 1; fr < graphWin.getNumViews(); fr++) {
            if (debug) System.out.println("snapAt[" + fr + "] = " + snapAt[0] + " + " + fr);
            snapAt[fr] = snapAt[0] + fr;
        }
        
        graphWin.setVisible(true);
        graphWin.execute(snapAt);
        
        
        int sn = snapAt[0] - 1;
        String s = (String)qCtlTable.get(new Integer(sn));
        // System.out.println(s);
        if(s != null) {
            Question q = (Question)questions.get(s);
            if(q != null) {
                fireQuestionEvent(q);
            }
        }
        
        URI u1 = (URI)docCtlTable.get(new Integer(sn));
        if(u1 != null) {
            //System.out.println("Found documentation " + u1.toString() + " for " + sn);
            fireDocumentationEvent(u1, DocumentEvent.TYPE_INFORMATION_PAGE);
        }
        
        URI u2 = (URI)pseudoCtlTable.get(new Integer(sn));
        if(u2 != null) {
            //System.out.println("Found pseudocode " + u2.toString() + " for " + sn);
            fireDocumentationEvent(u2, DocumentEvent.TYPE_PSEUDOCODE_PAGE);
        }
        
        String the_text = (String)audioCtlTable.get(new Integer(sn));
        if(the_text != null) {
            fireAudioTextEvent(the_text);
        }
        
    }
    
    public void stepForward() {
        
        if ( snapAt[ graphWin.getNumViews() - 1 ] + 1 /*graphWin.getJump()*/ > snapTotal )
            snapAt[0] = snapTotal;
        else
            snapAt[0] += 1 /*graphWin.getJump()*/;
        
        for (int frame = 0; frame < graphWin.getNumViews(); frame++) {
            if (debug) System.out.println("snapAt[" + frame + "] = " + snapAt[0] + " + " + frame);
            snapAt[frame] = snapAt[0] + frame;
        }
        
        graphWin.setVisible(true);
	//        graphWin.animate(snapAt,true);  // For time being filmstrip slide disabled to speed performance
	graphWin.execute(snapAt);
        
        int sn = snapAt[0] - 1;
        String s = (String)qCtlTable.get(new Integer(sn));
        // System.out.println(s);
        if(s != null) {
            Question q = (Question)questions.get(s);
            if(q != null) {
                fireQuestionEvent(q);
            }
        }
        
        URI u1 = (URI)docCtlTable.get(new Integer(sn));
        if(u1 != null) {
            //System.out.println("Found documentation " + u1.toString() + " for " + sn);
            fireDocumentationEvent(u1, DocumentEvent.TYPE_INFORMATION_PAGE);
        }
        
        URI u2 = (URI)pseudoCtlTable.get(new Integer(sn));
        if(u2 != null) {
            //System.out.println("Found pseudocode " + u2.toString() + " for " + sn);
            fireDocumentationEvent(u2, DocumentEvent.TYPE_PSEUDOCODE_PAGE);
        }

        String the_text = (String)audioCtlTable.get(new Integer(sn));
        if(the_text != null) {
            fireAudioTextEvent(the_text);
        }
        
    }
    
    public void stepBackward() {
        // clickedPrev = true;
        if ( snapAt[0] - 1 /*graphWin.getJump()*/ < 1 )
            snapAt[0] = 1;
        else
            snapAt[0] -= 1; // graphWin.getJump();
        for (int frame = 0; frame < graphWin.getNumViews(); frame++) {
            if (debug) System.out.println("snapAt[" + frame + "] = " + snapAt[0] + " + " + frame);
            snapAt[frame] = snapAt[0] + frame;
        }
        //         prevbut.setEnabled(snapAt[0] > 1);
        //             Next.setEnabled(true);
        graphWin.setVisible(true);
	//        graphWin.animate(snapAt,false);  // For time being, filmstrip slide disabled to improve performance
	graphWin.execute(snapAt);
        
        int sn = snapAt[0] - 1;
        String s = (String)qCtlTable.get(new Integer(sn));
        // System.out.println(s);
        if(s != null) {
            Question q = (Question)questions.get(s);
            if(q != null) {
                fireQuestionEvent(q);
            }
        }
        
        URI u1 = (URI)docCtlTable.get(new Integer(sn));
        if(u1 != null) {
            //System.out.println("Found documentation " + u1.toString() + " for " + sn);
            fireDocumentationEvent(u1, DocumentEvent.TYPE_INFORMATION_PAGE);
        }
        
        URI u2 = (URI)pseudoCtlTable.get(new Integer(sn));
        if(u2 != null) {
            //System.out.println("Found pseudocode " + u2.toString() + " for " + sn);
            fireDocumentationEvent(u2, DocumentEvent.TYPE_PSEUDOCODE_PAGE);
        }

        String the_text = (String)audioCtlTable.get(new Integer(sn));
        if(the_text != null) {
            fireAudioTextEvent(the_text);
        }
        
    }
}

