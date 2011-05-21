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

package jhave.server.parser;

import java.util.*;
import java.io.*;
/** 
 * Utility Class to grok Visualizer Fill-in-the-Blank Question specification strings.
 *
 * @author JRE
 */

public class FIBQuestionParser extends QuestionParser {

    private Vector correctAnswers;  //Vector containing the list of acceptable answers

    /**
     * Create a new FIBQuestionParser from the specification in the Tokenizer.
     *
     * @param specTkzr a StringTokenizer pointing to the beginning of a valid FIBQuestion specification.
     *                 Once constructed, this will point to the first token beyond the end of the
     *                 FIBQuestion specification.
     */
    public FIBQuestionParser(StringTokenizer tkzr){
        super(tkzr);
        if(debug)
            System.out.println("parsing FIBQuestion");
        String tok;

        question = "";
        tok = tkzr.nextToken();
        if (!tok.equals("FIBQUESTION"))
            /* Just in case we're given a tkzr that points to the first line after the FIBQUESTION
             * header, we can just go ahead and read the first line of the question; otherwise
             * we discard the first line of the header and move right along.
             */
            question = tok + " ";
        tok = tkzr.nextToken();
        while (!tok.equals("ENDTEXT")){
            question = question + tok + " ";
            tok = tkzr.nextToken();
        }

        tkzr.nextToken("\n"); //skip to the end of the line
        tok = tkzr.nextToken("\n");
        correctAnswers = new Vector();
        while (! tok.equals("ENDANSWER"))
        {
            correctAnswers.addElement(tok);
            tok = tkzr.nextToken("\n");
        }
    }

    /**
     * Check if a provided response is correct.
     *
     * @return true if the response matches the criteria specified in the specTkzr passed to the
     *         constructor or false otherwise.
     */
    public boolean isCorrect(String response){
        System.out.println("FIBQuestion (?) " + response);
        for (int i = 0; i < correctAnswers.size(); i++){
            System.out.println("comparing with: " +((String)correctAnswers.elementAt(i)).trim());
            if (response.equalsIgnoreCase(((String)correctAnswers.elementAt(i)).trim()))
                return true;
        }
        return false;
    }

    /**
     * return the string of acceptable answers.
     */
    public String getAnswer()
    {
        String answer = new String();

        for (int i = 0; i < correctAnswers.size(); i++)
            if (i==0)
                answer = ((String)correctAnswers.elementAt(i)).trim();
            else
                answer = answer+", "+((String)correctAnswers.elementAt(i)).trim();

        return answer;
    }

    public Vector getCorrectAnswer()
    {
      return correctAnswers;
    }
    
    public FIBQuestionParser(StreamTokenizer stok)
    {
      super(stok);
      if (debug) System.out.println("parsing FIBQuestion -- from a stream tokenizer");

      int tokenType;
      String tmp = "";

      stok.wordChars(33,126); //include every printable characters except whitespaces.

      question = parseUntil(stok, "ENDTEXT");

      tmp = parseLine(stok);
      while (tmp.equalsIgnoreCase("ENDANSWER"))
      {
        if (tmp != null && !tmp.equals(""))
          correctAnswers.addElement(tmp);
        tmp = parseLine(stok);        
      }
 
      //try to reset the stream tokenizer
      //is it good enough? (FIXME)
      stok.quoteChar('"');
      stok.quoteChar('\'');
    }

    private String parseLine(StreamTokenizer stok)
    {
      int type;
      String tmp = "";

      try
      {
        type = stok.nextToken();
        while (type != StreamTokenizer.TT_EOL)
        {
          switch(type)
          {
            case StreamTokenizer.TT_WORD:
              tmp = tmp+stok.sval + " ";
              break;
            case StreamTokenizer.TT_NUMBER:
              tmp = tmp+String.valueOf(stok.nval) + " ";
              break;
            default:
              tmp = tmp+String.valueOf((char)stok.ttype)+" ";
              break;
          }
          type = stok.nextToken();
        }
      }
      catch (Exception e)
      {
      }
      return tmp.trim();
    }
}
