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
 * GaffVisualizer.java
 *
 * Created on August 6, 2004, 12:09 AM
 */

package org.gaffneyc.gaff;

import java.io.*;
import java.util.*;
import javax.swing.*;

import jhave.core.*;
import jhave.question.*;
/**
 *
 * @author  Chris Gaffney
 */
public class GaffVisualizer extends Visualizer {
    /** */
    private RenderingPane renderPane = null;
    /** */
    private Map questions;
    /** */
    private ArrayList snapshots;
    
    /** Creates a new instance of GaffVisualizer */
    public GaffVisualizer(InputStream script) throws IOException {
        super(script);
        setCapabilities(CAP_CONTROLLABLE + CAP_STEP_FORWARD + CAP_STEP_BACKWARD + CAP_GOTO_FRAME + CAP_PLAY);
        snapshots = new ArrayList();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(script));
        reader.mark(8192);
        
        Collection c;
        try {
            c = QuestionFactory.parseScript(reader);
        } catch (QuestionParseException e) {
            e.printStackTrace();
            System.out.println("Error parsing questions.");
            throw new IOException();
        }
        reader.reset();
        
        if(c != null) {
            Iterator itr = c.iterator();
            questions = new HashMap(c.size());
            while(itr.hasNext()) {
                Question q = (Question)itr.next();
                questions.put(q.getID(), q);
            }
        }
        
        String line;
        while((line = reader.readLine()) != null) {
            if(line.equalsIgnoreCase("startquestions")) {
                break;
            }
            if(line.trim().length() == 0) {
                continue;
            }
            
            Snapshot s;
            try {
                s = new Snapshot(line);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            snapshots.add(s);
        }
        renderPane = new RenderingPane(snapshots);
        gotoFrame(0);
    }
    
    public int getCurrentFrame() {
        return renderPane.getIndex();
    }
    
    public int getFrameCount() {
        return snapshots.size();
    }
    
    public javax.swing.JComponent getRenderPane() {
        return renderPane;
    }
    
    public void stepForward() {
        if(getCurrentFrame() == getFrameCount() - 1) {
            return;
        }
        renderPane.stepForward();
        
        Snapshot s = (Snapshot)snapshots.get(getCurrentFrame());
        if(s.getQuestionID() != null) {
            Question q = (Question)questions.get(s.getQuestionID());
            if(q != null) {
                fireQuestionEvent(q);
            }
        }
    }
    
    public void stepBackward() {
        if(getCurrentFrame() == 0) {
            return;
        }
        renderPane.stepBackward();
        
        Snapshot s = (Snapshot)snapshots.get(getCurrentFrame());
        if(s.getQuestionID() != null) {
            Question q = (Question)questions.get(s.getQuestionID());
            if(q != null) {
                fireQuestionEvent(q);
            }
        }
    }
    
    public void play() {
        for(int i = getCurrentFrame(); i < getFrameCount() - 1; i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                break;
            }
            stepForward();
        }
    }
    
    public void gotoFrame(int frame) {
        renderPane.gotoFrame(frame);
        
        Snapshot s = (Snapshot)snapshots.get(frame);
        if(s.getQuestionID() != null) {
            Question q = (Question)questions.get(s.getQuestionID());
            if(q != null) {
                fireQuestionEvent(q);
            }
        }
    }
}
