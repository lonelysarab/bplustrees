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

package exe;
import java.io.*;


/**
 * <p><code>ShowFile</code> is a GAIGS XML Support class design to handle all writing to
 * a GAIGS XML Script file. Client classes can contruct a ShowFile instance,
 * write snaps to it, and close the ShowFile.</p>
 * 
 * @author Myles McNally (Tom Naps -- audio parameters added)
 * @version 5/28/06 (6/15/07 -- audio added)
 */


public class ShowFile extends PrintWriter{


//----------- Instance Variables ----------------------------------------------------------


    /**
     * The question collection for this ShowFile
     */
    XMLquestionCollection qColl;
     
    
//----------- Constructors ----------------------------------------------------------------


    /**
     * Create a <code>ShowFile</code> instance.  Open the output file, write the 
     * preliminary XML to it, and create a default question collection.
     * 
     * @param   fileName    The file name of the ShowFile to be created.
     */
    public ShowFile(String fileName) throws IOException {
        super(new FileWriter(new File(fileName)));      
        write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n");
        write("<!DOCTYPE show PUBLIC \"-//JHAVE//DTD GAIGS SHO//EN\" \"gaigs_sho.dtd\">"+"\n"+"\n");
        write("<show>"+"\n"+"\n");
        qColl = new XMLquestionCollection(this);
    }
    
    /**
     * Create a <code>ShowFile</code> instance.  Open the output file, write the
     * preliminary XML to it, and create a default question collection.
     * Uses probabilistic questioning based on <code>questionCount</code>.
     *
     * @param   fileName         The file name of the ShowFile to be created.
     * @param   questionCount    The number of questions to be asked.
     */
    public ShowFile(String fileName, int questionCount) throws IOException {
        super(new FileWriter(new File(fileName)));
        write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n");
        write("<!DOCTYPE show PUBLIC \"-//JHAVE//DTD GAIGS SHO//EN\" \"gaigs_sho.dtd\">"+"\n"+"\n");
        write("<show>"+"\n"+"\n");
        qColl = new XMLquestionCollection(this, questionCount);
    }

    /**
     * Create a <code>ShowFile</code> instance.  Open the output file, write the 
     * preliminary XML to it, and create a default question collection.
     * Uses probabilistic questioning based on <code>questionCount</code>
     * and <code>opCount</code>.
     * 
     * @param   fileName         The file name of the ShowFile to be created.
     * @param   questionCount    The number of questions to be asked.
     * @param   opCount          The estimated number of questions opportunities.
     */
    public ShowFile(String fileName, int questionCount, int opCount) throws IOException {
        super(new FileWriter(new File(fileName)));      
        write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n");
        write("<!DOCTYPE show PUBLIC \"-//JHAVE//DTD GAIGS SHO//EN\" \"gaigs_sho.dtd\">"+"\n"+"\n");
        write("<show>"+"\n"+"\n");
        qColl = new XMLquestionCollection(this, questionCount, opCount);
    }
    
    
//----------- Instance Methods ------------------------------------------------------------


    /**
     * Write a snapshot to the output file.
     * 
     * No documentation, pseudocode, audio or questions are associated
     * with this snapshot.
     * 
     * @param   title            The title of the snapshot.
     * @param   ds               A varargs list of GAIGS structures in the snapshot.
     */    
    // 1-7
    public void writeSnap (String title, GAIGSdatastr... ds) throws IOException {
        writeSnap(title, -1, "", "", "", fakeQuestion(), ds);
    }
    
    /**
     * Write a snapshot to the output file.
     * 
     * No documentation, pseudocode, audio, or questions are
     * associated with this snapshot.
     * 
     * @param   title            The title of the snapshot.
     * @param   titleFontSize    The font size of the title.
     * @param   ds               A varargs list of GAIGS structures in the snapshot.
     */    
    // 1-2-7
    public void writeSnap (String title, double titleFontSize, GAIGSdatastr... ds) throws IOException {
        writeSnap(title, titleFontSize, "", "", "", fakeQuestion(), ds);
    }

    
    /**
     * Write a snapshot to the output file.
     * 
     * Only documentation is associated with this snapshot.
     * 
     * @param   title            The title of the snapshot.
     * @param   docURI           The uri of the associated documentation  
     *                           (Do not pass <code>null</code> for docURI!).
     * @param   ds               A varargs list of GAIGS structures in the snapshot.
     */    
    // 1-3-7
    public void writeSnap (String title,  String docURI, GAIGSdatastr... ds) throws IOException {
        writeSnap(title, -1, docURI, "", "", fakeQuestion(), ds);
    }
        
    /**
     * Write a snapshot to the output file.
     * 
     * Only documentation is associated with this snapshot.
     * 
     * @param   title            The title of the snapshot.
     * @param   titleFontSize    The font size of the title.
     * @param   docURI           The uri of the associated documentation  
     *                           (Do not pass <code>null</code> for documentation!).
     * @param   ds               A varargs list of GAIGS structures in the snapshot.
     */    
    // 1-2-3-7
    public void writeSnap (String title,  double titleFontSize, String docURI, GAIGSdatastr... ds) throws IOException {
        writeSnap(title, titleFontSize, docURI, "", "", fakeQuestion(), ds);
    }
    
    /**
     * Write a snapshot to the output file.
     * 
     * Documentation, pseudocode but no questions are associated with
     * this snapshot.
     * 
     * @param   title            The title of the snapshot.
     * @param   docURI           The uri of the associated documentation  
     *                           (pass <code>null</code> for no documentation).
     * @param   pseudoURI        The uri of the associated pseudocode
     *                           (Do not pass <code>null</code> for pseudocode!).
     * @param   ds               A varargs list of GAIGS structures in the snapshot.
     */    
    // 1-3-4-7
    public void writeSnap (String title,  String docURI, String pseudoURI, GAIGSdatastr... ds) throws IOException {
        writeSnap(title, -1, docURI, pseudoURI, "", fakeQuestion(), ds);
    }
    
    /**
     * Write a snapshot to the output file.
     * 
     * Documentation, pseudocode, audio, but no questions are
     * associated with this snapshot.
     * 
     * @param   title            The title of the snapshot.
     * @param   docURI           The uri of the associated documentation  
     *                           (pass <code>null</code> for no documentation).
     * @param   pseudoURI        The uri of the associated pseudocode
     *                           (Do not pass <code>null</code> for pseudocode!).
     * @param   audio_text       The string to be spoken.  
     *                           (If the string contains '.au' or
     *                           '.wav', the assumption is made that
     *                           this is a url for an audio file.
     *                           Otherwise the speech-to-text module
     *                           is used to speak the text.)
     *                           (pass <code>null</code> for no audio).
     * @param   ds               A varargs list of GAIGS structures in the snapshot.
     */    
    // 1-3-4-5-7
    public void writeSnap (String title,  String docURI, String pseudoURI, String audio_text, GAIGSdatastr... ds) throws IOException {
        writeSnap(title, -1, docURI, pseudoURI, audio_text, fakeQuestion(), ds);
    }
    
    /**
     * Write a snapshot to the output file.
     * 
     * Documentation, pseudocode but no questions are associated with
     * this snapshot.
     * 
     * @param   title            The title of the snapshot.
     * @param   titleFontSize    The font size of the title.
     * @param   docURI           The uri of the associated documentation  
     *                           (pass <code>null</code> for no documentation).
     * @param   pseudoURI        The uri of the associated pseudocode
     *                           (Do not pass <code>null</code> for pseudocode!).
     * @param   ds               A varargs list of GAIGS structures in the snapshot.
     */    
    // 1-2-3-4-7
    public void writeSnap (String title,  double titleFontSize,  String docURI, String pseudoURI, GAIGSdatastr... ds) throws IOException {
        writeSnap(title, titleFontSize, docURI, pseudoURI, "", fakeQuestion(), ds);
    }

    /**
     * Write a snapshot to the output file.
     * 
     * Documentation, pseudocode, audio, but no questions are
     * associated with this snapshot.
     * 
     * @param   title            The title of the snapshot.
     * @param   titleFontSize    The font size of the title.
     * @param   docURI           The uri of the associated documentation  
     *                           (pass <code>null</code> for no documentation).
     * @param   pseudoURI        The uri of the associated pseudocode
     *                           (Do not pass <code>null</code> for pseudocode!).
     * @param   audio_text       The string to be spoken.  
     *                           (If the string contains '.au' or
     *                           '.wav', the assumption is made that
     *                           this is a url for an audio file.
     *                           Otherwise the speech-to-text module
     *                           is used to speak the text.)
     *                           (pass <code>null</code> for no audio).
     * @param   ds               A varargs list of GAIGS structures in the snapshot.
     */    
    // 1-2-3-4-5-7
    public void writeSnap (String title,  double titleFontSize,  String docURI, String pseudoURI, String audio_text, GAIGSdatastr... ds) throws IOException {
        writeSnap(title, titleFontSize, docURI, pseudoURI, audio_text, fakeQuestion(), ds);
    }

    /**
     * Write a snapshot to the output file.
     * 
     * Documentation, pseudocode and questions are associated with this
     * snapshot.
     * 
     * @param   title            The title of the snapshot.
     * @param   docURI           The uri of the associated documentation  
     *                           (pass <code>null</code> for no documentation).
     * @param   pseudoURI        The uri of the associated pseudocode
     *                           (Do not pass <code>null</code> for pseudocode!).
     * @param   ds               A varargs list of GAIGS structures in the snapshot.
     */    
    // 1-3-4-6-7
    public void writeSnap (String title,  String docURI, String pseudoURI, question q, GAIGSdatastr... ds) throws IOException {
        writeSnap(title, -1, docURI, pseudoURI, "", q, ds);
    }
    
    /**
     * Write a snapshot to the output file.
     * 
     * Documentation, pseudocode, audio, and questions are associated
     * with this snapshot.
     * 
     * @param   title            The title of the snapshot.
     * @param   docURI           The uri of the associated documentation  
     *                           (pass <code>null</code> for no documentation).
     * @param   pseudoURI        The uri of the associated pseudocode
     *                           (Do not pass <code>null</code> for pseudocode!).
     * @param   audio_text       The string to be spoken.  
     *                           (If the string contains '.au' or
     *                           '.wav', the assumption is made that
     *                           this is a url for an audio file.
     *                           Otherwise the speech-to-text module
     *                           is used to speak the text.)
     *                           (pass <code>null</code> for no audio).
     * @param   ds               A varargs list of GAIGS structures in the snapshot.
     */    
    // 1-3-4-5-6-7
    public void writeSnap (String title,  String docURI, String pseudoURI, String audio_text, question q, GAIGSdatastr... ds) throws IOException {
        writeSnap(title, -1, docURI, pseudoURI, audio_text, q, ds);
    }
    
    /**
     * Write a snapshot to the output file.
     * 
     * Documentation, psuedocode and a question are associated with this
     * snapshot.
     * 
     * @param   title            The title of the snapshot.
     * @param   titleFontSize    The font size of the title.
     * @param   docURI           The uri of the associated documentation  
     *                           (pass <code>null</code> for no documentation).
     * @param   pseudoURI        The uri of the associated pseudocode
     *                           (pass <code>null</code> for no pseudocode).
     * @param   q                A question attached to this snapshot
     *                           (Do not pass <code>null</code> for question!).
     * @param   ds               The varargs list of GAIGS structures in the snapshot.
     */    
    // 1-2-3-4-5-7
    public void writeSnap (String title, double titleFontSize, String docURI, String pseudoURI, question q, GAIGSdatastr... ds) throws IOException {
	writeSnap(title, titleFontSize, docURI, pseudoURI, "", q, ds);
    }

    /**
     * Write a snapshot to the output file.
     * 
     * Documentation, psuedocode, audio, and a question are associated
     * with this snapshot.
     * 
     * @param   title            The title of the snapshot.
     * @param   titleFontSize    The font size of the title.
     * @param   docURI           The uri of the associated documentation  
     *                           (pass <code>null</code> for no documentation).
     * @param   pseudoURI        The uri of the associated pseudocode
     *                           (pass <code>null</code> for no pseudocode).
     * @param   audio_text       The string to be spoken.  
     *                           (If the string contains '.au' or
     *                           '.wav', the assumption is made that
     *                           this is a url for an audio file.
     *                           Otherwise the speech-to-text module
     *                           is used to speak the text.)
     *                           (pass <code>null</code> for no audio).
     * @param   q                A question attached to this snapshot
     *                           (Do not pass <code>null</code> for question!).
     * @param   ds               The varargs list of GAIGS structures in the snapshot.
     */    
    // 1-2-3-4-5-6-7
    public void writeSnap (String title, double titleFontSize, String docURI, String pseudoURI, String audio_text, question q, GAIGSdatastr... ds) throws IOException {
       write("<snap>"+"\n"); 
       
       write("<title");
       if (titleFontSize != -1)      
            write(" fontsize=\"" + titleFontSize +"\"");
       write(">");
       if (title != null)
           write(title);
       write( "</title>"+"\n");
       
       if ((docURI != null) && (docURI != ""))
            write("<doc_url>" + docURI + "</doc_url>"+"\n");
            
       if ((pseudoURI != null) && (pseudoURI != ""))
            write("<pseudocode_url>" + convertPHPurl(pseudoURI) + "</pseudocode_url>"+"\n");   
            
       if ((audio_text != null) && (audio_text != ""))
            write("<audio_text>" + audio_text + "</audio_text>"+"\n");   
            
       for (int i = 0; i < ds.length; i++)
            write(ds[i].toXML());
            
       if ((q != null) && (q.getID() != "-1234"))
           if (qColl.addQuestion(q))
                q.insertQuestion();
        
       write("</snap>"+"\n"+"\n");
    }

    /**
     * Close the <code>ShowFile</code>.  First write the <code>QuestionCollection</code> XML,
     * then the terminal show tag, and then finally actually close the file.
     */
    public void close(){
       qColl.writeQuestionsAtEOSF();
       write("</show>");
       super.close();
    }
    
    // ----------------  Support methods ------------------------------------------------
    
    private question fakeQuestion () {
       return ( new XMLfibQuestion(this, "-1234" ));
    }
    
    private String convertPHPurl (String s) {
        return s.replace('&', ';');
    }
}
