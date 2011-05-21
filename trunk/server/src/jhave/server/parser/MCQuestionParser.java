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
 * Utility Class to grok Visualizer multiple-guess Question specification strings.
 *
 * @author JRE
 */

public class MCQuestionParser extends QuestionParser {
    
    private int correctAnswer;  //The 1-based index of the correct response
    private int numChoices = 0;
    private Vector choices = new Vector();
    
    /**
     * Create a new MCQuestionParser from the specification in the Tokenizer.
     *
     * @param specTkzr a StringTokenizer pointing to the beginning of a valid MCQuestion specification.
     *                 Once constructed, this will point to the first token beyond the end of the
     *                 MCQuestion specification.
     */
    public MCQuestionParser(StringTokenizer tkzr) {
        super(tkzr);
        
        if(debug)
            System.out.println("parsing MCQuestion");
        String tok;
        String choice = new String();
        
        tok = tkzr.nextToken();
        if (!tok.equals("MCQUESTION"))
            question = tok + " ";
        tok = tkzr.nextToken();
        
        
        while (! tok.equals("ENDTEXT")){
            question += tok + " ";
            tok = tkzr.nextToken();
        }
        
        tok = tkzr.nextToken();
        choice = "";
        while (! tok.equals("ANSWER")) {
            if (debug) System.out.println("   Parsing Choice");
            while(!tok.equals("ENDCHOICE")) {
                choice = choice+tok+" ";
                tok = tkzr.nextToken();
            }
            choices.addElement(choice);
            numChoices++;
            choice = "";
            tok = tkzr.nextToken();
            if (debug) System.out.println("   End Parsing Choice");
        }
        
        if (debug) System.out.println("   Parsing Answer");
        tok = tkzr.nextToken(); //now tok is the index of the correct answer
        correctAnswer = Integer.parseInt(tok);
        tkzr.nextToken();//ENDANSWER
        if (debug) System.out.println("   End Parsing Answer");
    }
    
    /**
     * Check if a provided response is correct.
     *
     * @param response - a string containing the 1-based index of the response.
     * @return true if the response matches the criteria specified in the specTkzr passed to the
     *         constructor or false otherwise.
     */
    public boolean isCorrect( String response ) {
        if(debug)
            System.out.println("MCQuestion --- your answer:" + response + " the correct answer: " + correctAnswer);
        return ((Integer.parseInt(response)) == correctAnswer);
    }
    
    /**
     * Check if a provided response is correct.
     *
     * @param response - the 1-based index of the response.
     * @return true if the response matches the criteria specified in the specTkzr passed to the
     *         constructor or false otherwise.
     */
    public boolean isCorrect( int response ) {
        return (response == correctAnswer);
    }
    
    /**
     * return the correct choice
     */
    public String getAnswer() {
        String answer = new String();
        
        answer = answer.valueOf(correctAnswer);
        return answer;
    }
    
    public int getCorrectAnswer() {
        return correctAnswer;
    }
    
    public int getNumChoices() {
        return numChoices;
    }
    
    public Vector getChoices() {
        return choices;
    }
    
    public MCQuestionParser(StreamTokenizer stok) {
        super(stok);
        if (debug) System.out.println("parsing MCQuestion -- from a stream tokenizer");
        
        int tokenType;
        String tmp;
        
        try {
            stok.wordChars(33,126); //include every printable characters except whitespaces.
            
            question = parseUntil(stok, "ENDTEXT");
            
            while (!stok.sval.equalsIgnoreCase("ANSWER")) {
                choices.addElement(parseUntil(stok,"ENDCHOICE"));
                numChoices++;
                tokenType = stok.nextToken();
            }
            
            correctAnswer = Integer.parseInt(parseUntil(stok, "ENDANSWER"));
            
            //try to reset the stream tokenizer
            //is it good enough? (FIXME)
            stok.quoteChar('"');
            stok.quoteChar('\'');
        }
        catch (Exception e) {
        }
    }
    
}
