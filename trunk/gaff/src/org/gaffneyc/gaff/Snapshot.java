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
 * Snapshot.java
 *
 * Created on August 7, 2004, 1:13 PM
 */

package org.gaffneyc.gaff;

import java.net.*;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.StringTokenizer;
import java.awt.image.BufferedImage;
/**
 * The main data model for this visualizer. The snapshot class is given a line
 * of the script from which image and question id (if there is one) is parsed.
 * @author  Chris Gaffney
 */
public class Snapshot {
    /** Id of the question associated with this snapshot. */
    private String questionID = null;
    /** Snapshot image. */
    private BufferedImage image = null;
    
    /**
     * Creates a new snapshot from a line of the script. Parses the script and
     * throws an exception if there are any problems with it.
     * @param line the script to parse.
     * @throws Exception this will change.
     */
    public Snapshot(String line) throws Exception {
        StringTokenizer tok = new StringTokenizer(line, " ");
        
        String image = tok.nextToken();
        
        if(tok.hasMoreTokens()) {
            questionID = tok.nextToken();
        }
        
        URI imageURI = new URI(image);
        URL imageLocation;
        if(imageURI.getScheme().equalsIgnoreCase("res")) {
            ClassLoader cl = getClass().getClassLoader();
            imageLocation = cl.getResource(imageURI.getSchemeSpecificPart());
        } else {
            imageLocation = imageURI.toURL();
        }
        this.image = javax.imageio.ImageIO.read(imageLocation);
    }
    
    /**
     * Returns the questions id associated with this question, null if there
     * isn't one.
     * @return question id, null if not question with snapshot.
     */
    public String getQuestionID() {
        return questionID;
    }
    
    /**
     * Returns the image associated with this snapshot.
     * @return snapshot's image.
     */
    public Image getImage() {
        return image;
    }
}
