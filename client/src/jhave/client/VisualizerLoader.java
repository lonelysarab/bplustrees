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
 * VisualizerLoader.java
 *
 * Created on June 16, 2005, 5:55 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package jhave.client;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jhave.core.JHAVETranslator;
import jhave.core.Visualizer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author gaffneyc
 */
public class VisualizerLoader {
    private static VisualizerLoader singleton = null;    
    
    private List visualizers;
    
    /** Creates a new instance of VisualizerLoader */
    private VisualizerLoader() {
        visualizers = new ArrayList();
        
	// NOTE: On Jan 21, 2209, I tried commenting out these two
	// line and then using the alternate getResource call below,
	// but that did not correct the problem with webstart on Mac
	// OS X 10.4

	ClassLoader loader = VisualizerLoader.class.getClassLoader();
	URL u = loader.getResource("jhave/client/visualizers.xml");
        
        SAXBuilder builder = new SAXBuilder();
        
        // command line should offer URIs or file names
        Document doc;
        try {
	    doc = builder.build(u);
	    //            doc = builder.build(this.getClass().getClassLoader().getResource("jhave/client/visualizers.xml"));
        }
        // indicates a well-formedness error
        catch (JDOMException e) {
//            System.out.println("The doc is not well-formed.");
          System.out.println(JHAVETranslator.translateMessage("invalidDocFormat",
              e.getMessage()));
//            System.out.println(e.getMessage());
            System.exit(0);
            return;
        } catch (IOException e) {
//            System.out.println("Could not check the doc");
//            System.out.println(" because " + e.getMessage());
          System.out.println(JHAVETranslator.translateMessage(
              "couldNotCheckDoc", e.getMessage()));
            System.exit(0);
            return;
        }
        
        Element elm = doc.getRootElement();
        Iterator itr = elm.getChildren().iterator();
        while(itr.hasNext()) {
            Element element = (Element)itr.next();
            visualizers.add(new VisRecord(element));
        }
    }
    
    private String getClassByExtension(String ext) {
        Iterator itr = visualizers.iterator();
        
        while(itr.hasNext()) {
            VisRecord visDef = (VisRecord)itr.next();
            if(visDef.containsExtension(ext)) {
                return visDef.getClassName();
            }
        }
        
        // Could not locate a visualizer for the given extension
        return null;
    }
    
    public static void main(String[] args) {
        new VisualizerLoader();
    }
    private String getClassByName(String name) {
        Iterator itr = visualizers.iterator();
        
        while(itr.hasNext()) {
            VisRecord visDef = (VisRecord)itr.next();
            if(visDef.getName().equalsIgnoreCase(name)) {
                return visDef.getClassName();
            }
        }
        
        // Could not locate a visualizer for the given name
        return null;
    }
    
    public static Visualizer loadByExtension(String ext, InputStream script) {
        if(singleton == null) {
            singleton = new VisualizerLoader();
        }
        return loadByClassName(singleton.getClassByExtension(ext), script);
    }
    
    public static Visualizer loadByName(String name, InputStream script) {
        if(singleton == null) {
            singleton = new VisualizerLoader();
        }
        return loadByClassName(singleton.getClassByName(name), script);
    }
    
    private static Visualizer loadByClassName(String className, InputStream script) {
        if(className == null) {
            // throw an exception.
        	// GR: better than nothing approach to avoid the exception
        	return null;
        }
        
        ClassLoader clo = VisualizerLoader.class.getClassLoader();
        try {
            Class c = clo.loadClass(className);
            Constructor c1 = c.getConstructor(new Class[] {InputStream.class});
            Visualizer vis = (Visualizer)c1.newInstance(new Object[] {script});
            return vis;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private class VisRecord {
        private String name;
        private String className = null;
        private Set extensions;
        
        public VisRecord(Element vis) {
            extensions = new HashSet();
            name = vis.getAttributeValue("name");
            
            Iterator itr = vis.getChildren().iterator();
            while(itr.hasNext()) {
                Element e = (Element)itr.next();
                
                if(e.getName().equalsIgnoreCase("class")) {
                    if(className == null) {
                        className = e.getAttributeValue("value");
                    }
                } else if(e.getName().equalsIgnoreCase("extension")) {
                    extensions.add(e.getAttributeValue("value").toLowerCase());
                }
            }
            
            if(className == null) {
                // TODO: class name wasn't set, throw an exception
            }
            if(extensions.size() == 0) {
                // TODO: No extensions were added so throw an exception
            }
        }
        public String getName() {
            return name;
        }
        public String getClassName() {
            return className;
        }
        public boolean containsExtension(String extension) {
            return extensions.contains(extension.toLowerCase());
        }
        public int hashCode() {
            return className.hashCode();
        }
        public boolean equals(Object obj) {
            if(obj instanceof String) {
                return className.equals(obj);
            } else {
                return false;
            }
        }
    }
}
