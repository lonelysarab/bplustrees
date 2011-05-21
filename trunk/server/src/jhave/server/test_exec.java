// Useful utility for testing how the JHAVE server will launch a script-producing program

import java.io.*;

public class test_exec {

    public static void main (String [] args) {
	String root = "/home/naps/sandboxes/open_algo_viz_rev1/jhave2/server/build/classes/";
	String cmd = "java -classpath ../../lib/jdom.jar:../../lib/jaxen.jar:. exe.ParameterPassing.ParameterPassing /srv/www/htdocs/jhave/html_root/uid/1 * null";
	//	String cmd = "java -classpath ../../lib/jdom.jar:. exe.Loops.Loops /srv/www/htdocs/jhave/html_root/uid/1 * null";
        Runtime rt = Runtime.getRuntime();
	StringBuffer outBuf = null;
	InputStream inStream = null;
	int ch;
        try {
	    System.out.println("Running ... ");
            Process prog = rt.exec(cmd, null, new File(root));
	    // This loop is for nasty script producers that write a
	    // lot of debugging output to the console.  Without it
	    // there is interaction between that ouput and the waitFor
	    // that causes the Process to never finish from waitFor's
	    // perspective.
	    inStream = prog.getInputStream();
	    outBuf = new StringBuffer();
	    while ((ch = inStream.read()) != -1)
		{
		    outBuf.append((char)ch + "");
		}
	    System.out.println(outBuf.toString());
	    // End of loop for nasty script producers
	    System.out.println("Waiting ... ");
            prog.waitFor(); //wait until the program has finished execution
            System.out.println( "Done " + prog.exitValue() );
        } catch (InterruptedException e) { } 
        catch (IOException e) { } catch (SecurityException e) {
            System.err.println("Security Exception caught trying to run " + cmd + ": "
                    + e.getMessage());
        }
	
    }

}

