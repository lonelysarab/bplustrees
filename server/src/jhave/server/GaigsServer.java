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

package jhave.server;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.*;
import java.util.logging.Logger;
import java.util.*;
import java.lang.reflect.*;

/**
 * The Gaigs Server listener - waits for a client to connect and creates a new
 * ClientConnection thread when one does, then goes back to listening.
 *
 * The Gaigs Server follows this structure:  the GaigsServer listens for
 * incoming client connections.  When a client connects, a ClientConnection
 * thread is spawned.  The ClientConnection handles all the communication to and
 * from that client.
 *
 * On the local filesystem, we use the following directory structure for all of
 * Gaigs' files:
 * <pre>
 * GaigsServer.class
 * html_root/cat/<i>*</i>.lis                           category listings
 *          /uid/<i>uid</i>.{dat,sho,sam,etc}           client-associated files
 *          /script/<i>prefab-script</i>.{sho,sam,etc}  pre-fabricated scriptfiles
 *          /doc/<i>algo</i>/<i>*</i>                          algorithm-specific documentation
 *          /ingen/<i>algo</i>.igs                      input-generator scripts
 *          /javaapp/<i>algo</i>/<i>*</i>                      standalone Java Applets
 *          /applet/<i>algo</i>.html                    category applet pages
 *          exe/<i>algo</i>/<i>algo</i>_dat.{exe,class,jar}    optional data generators
 *          /<i>algo</i>.{exe,class,jar}                algorithm executables
 * <pre>
 *
 * @see ClientConnection
 * @author JRE
 */
public class GaigsServer {
    // Logging (see logs directory for output)
    private static Logger logger = Logger.getLogger(GaigsServer.class.getName());
    
    public static Hashtable visualizerExtensionTab; // Ugly kludge... see comment at top of main()
    
    public static String WEBROOT = "";
    
    public static String DBDRIVER = "org.gjt.mm.mysql.Driver";    //Default RDBMS Driver
    public static String DBURL = "jdbc:mysql://localhost/JhaveQuiz";        //Default URL for DB
    public static String DBLOGIN = "root";                      //Default login to DB
    public static String DBPASSWD;                    //Default password for DB
    
    public static void main(String[] args) {
        new SimpleFormatter();
        // Initialize logging facilities (this only needs to be done once)
        try {
            // Set the properties
            LogManager.getLogManager().readConfiguration(
                    new FileInputStream(new File("logs/log.properties")));
        } catch (IOException e) {
            System.err.println("Could not initialize logging.");
            e.printStackTrace();
        }
        
        //Set the WEBROOT to be on our webserver
        try {
            WEBROOT = "http://" + InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            WEBROOT = "http://localhost";
            logger.log(Level.WARNING, "Unable to get host address -- " +
                    "Setting to localhost");
        }
        
        // Parse the command line options
        boolean withWebRoot = false;
        boolean withScriptRoot = false;
        boolean withUserDir = false;
        for(int i = 0; i < args.length; i++) {
            if(args[i].startsWith("--with-webroot=") || args[i].startsWith("-r=")) {
                if(withWebRoot) {
                    // print usage.
                    System.out.println("Only specify the webroot once.");
                    System.exit(0);
                }
                
                // If user has chosen to run in localhost mode,
                if (args[i].indexOf("localhost") >=0) {
                    // cancel effect of previous assignment -- WEBROOT must be fully specified by user
                    WEBROOT = args[i].substring(args[i].indexOf("=") + 1);
		} else if(args[i].indexOf("http://") >= 0) {
		    // If we specify a weberver that is not running on the current system we 
		    // set the webroot as such.
		    WEBROOT = args[i].substring(args[i].indexOf("=") + 1);
                } else { 		// append command line arg to WEBROOT
                    WEBROOT += args[i].substring(args[i].indexOf("=") + 1);
                }
                withWebRoot = true;
            } else if(args[i].startsWith("--with-script-root=") || args[i].startsWith("-s=")) {
                if(withScriptRoot) {
                    System.out.println("Only specify the script root once.");
                    System.exit(0);
                }
                String scriptRoot = args[i].substring(args[i].indexOf("=") + 1);
                if(scriptRoot.charAt(scriptRoot.length() - 1) != '/' ||
                        scriptRoot.charAt(scriptRoot.length() - 1) != '\\') {
                    scriptRoot += '/';
                }
                try {
                    String path = new File(scriptRoot).getCanonicalPath() + File.separatorChar;
                    System.setProperty("jhave.server.scriptroot", path);
                } catch (IOException e) {
                    logger.severe("Could not get Script root path");
                    System.exit(-1);
                }
                withScriptRoot = true;
            } else if(args[i].startsWith("--with-user-dir=") || args[i].startsWith("-u=")) {
                if(withUserDir) {
                    System.out.println("Only specify the user directory once.");
                    System.exit(0);
                }
                String userDir = args[i].substring(args[i].indexOf("=") + 1);
                if(userDir.charAt(userDir.length() - 1) != '/' ||
                        userDir.charAt(userDir.length() - 1) != '\\') {
                    userDir += '/';
                }
                //try {
                    String path = new File(userDir).getPath() + File.separatorChar;
                    System.setProperty("jhave.server.userdir", path);
		    /*} catch (IOException e) {
                    logger.severe("Could not convert user directory path");
                    System.exit(-1);
		    }*/
                
                withUserDir = true;
            } else if(args[i].startsWith("--db-password=") || args[i].startsWith("-p=")) {
		System.setProperty("jhave.server.dbpassword",args[i].substring(args[i].indexOf("=")+1));
		DBPASSWD = System.getProperty("jhave.server.dbpassword");
	    }
	    else if(args[i].startsWith("--email-password=") || args[i].startsWith("-e=")) {
		System.setProperty("jhave.server.emailpassword",args[i].substring(args[i].indexOf("=")+1));
	    }
	    else if(args[i].startsWith("--smtp-server=") || args[i].startsWith("-m=")) {
		System.setProperty("jhave.server.smtpserver",args[i].substring(args[i].indexOf("=")+1));
	    }
	     else if(args[i].startsWith("--email-login=") || args[i].startsWith("-l=")) {
		System.setProperty("jhave.server.emaillogin",args[i].substring(args[i].indexOf("=")+1));
	    }
	    else if(args[i].startsWith("--xmlrpc-host=") || args[i].startsWith("-xh=")) {
		System.setProperty("xmlrpc.server.host",args[i].substring(args[i].indexOf("=")+1));
	    }
	    else if(args[i].startsWith("--xmlrpc-port=") || args[i].startsWith("-xp=")) {
		System.setProperty("xmlrpc.server.port",args[i].substring(args[i].indexOf("=")+1));
	    }
	    else if(args[i].startsWith("-l"))
	    {
		System.setProperty("jhave.server.location",args[i].substring(args[i].indexOf("=")+1));
		}
	    else {
                // Print usage
            }
        }
        
        // Specify the default / non-command line options
        if(!withWebRoot) {
            WEBROOT += "/jhave/";
        }
        if(!withScriptRoot) {
            System.setProperty("jhave.server.scriptroot",
                    ("." + File.separatorChar).trim());
        }
        if(!withUserDir) {
            System.setProperty("jhave.server.userdir",
                    new File("." + File.separatorChar).getAbsolutePath().trim());
        }
        
        logger.info("Using WEBROOT = " + WEBROOT);
        logger.info("Using Script Root of "
                + System.getProperty("jhave.server.scriptroot"));
        logger.info("Using User Script Directory of "
                + System.getProperty("jhave.server.userdir"));
        
        /* Add any additional visualizers here */
        //FIXME: Is there a better way to do this?
        //sure... put this into a configuration file that can be parsed at runtime.
        visualizerExtensionTab = new Hashtable();
        visualizerExtensionTab.put("gaigs", ".sho");
        visualizerExtensionTab.put("samba", ".sam");
        visualizerExtensionTab.put("xaal", ".xaal");
        visualizerExtensionTab.put("animal", ".asu");
        visualizerExtensionTab.put("matrix", ".matrix");
        
        int uid = 1;
        List clients = new ArrayList();
        try {
            /* The default queue size of 50 waiting on a ServerSocket ought to be
             * more than sufficient */
            ServerSocket s = new ServerSocket(7004);
            logger.log(Level.INFO, "JHAVE Server started. Listening on port "
                    + s.getLocalPort());
            
            while(true) {
                /* Wait for a client to connect.  When one does, spawn a new
                 * ClientConnection thread for it and go back to listening for
                 * more clients.
                 */
                Socket client = s.accept();
                logger.log(Level.INFO, "Accepted connection " + uid +
                        " from " + client.getInetAddress().getHostAddress());
		ClientConnection conn;
		Class connDef;
		Class[] argClass = {Socket.class,int.class};
		Integer userID = new Integer(uid++);
		Object[] arguments = {client,userID};
		Constructor argsConstructor;
		String serverLocation;
		if(System.getProperty("jhave.server.location") != null) serverLocation = "jhave.server."+System.getProperty("jhave.server.location");
		else serverLocation = "jhave.server.UWOSHClientConnection";
		try
		{
		    connDef = Class.forName(serverLocation);    
		    argsConstructor = connDef.getConstructor(argClass);
		    conn = (ClientConnection) createObject(argsConstructor,arguments);
		    clients.add(conn);
		    conn.start();
		}catch(Exception e){
		    System.err.println(e);
		    System.err.println("Error in server location");
		    System.exit(1);
		}
            }
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1); // 1 == random non-zero integere to signify abnormal exit condition
        }
        
        System.exit(0); // signal normal exit
    }
    
    public static Object createObject(Constructor constructor, Object[] arguments)
    {
	Object object = null;
	try
	{
	    logger.info("Constructor: "+constructor.toString());
	    object = constructor.newInstance(arguments);
	    logger.info("Object: "+object.toString());
	}catch(Exception e){System.err.println(e);System.exit(1);}
	return object;
    }
}
		       
