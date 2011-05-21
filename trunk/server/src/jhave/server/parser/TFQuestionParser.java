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
 * Utility Class to grok Visualizer true/false Question specification strings.
 *
 * @author JRE
 */

public class TFQuestionParser extends QuestionParser {
    
    private boolean correctAnswer;  //is the correct response true or false?
    
    /**
     * Create a new TFQuestionParser from the specification in the Tokenizer.
     *
     * @param specTkzr a StringTokenizer pointing to the beginning of a valid TFQuestion specification.
     *                 Once constructed, this will point to the first token beyond the end of the
     *                 TFQuestion specification.
     */
    public TFQuestionParser(StringTokenizer tkzr) {
        super(tkzr);
        if(debug)
            System.out.println("parsing TFQuestion");
        
        String tok;
        
        tok = tkzr.nextToken();
        if (! tok.equals("TFQUESTION"))
            question = tok + " ";
        tok = tkzr.nextToken();
        while (! tok.equals("ENDTEXT")) {
            question += tok + " ";
            tok = tkzr.nextToken();
        }
        tkzr.nextToken();
        tok = tkzr.nextToken();
        correctAnswer = (tok.startsWith("T") || tok.startsWith("t"));
        tkzr.nextToken(); //ENDANSWER
    }
    
    /**
     * Check if a provided response is correct.
     *
     * @param response - a case-insensitive string containing "true" or "false"
     * @return true if the response matches the criteria specified in the specTkzr passed to the
     *         constructor or false otherwise.
     */
    public boolean isCorrect( String response ) {
        System.out.println("TFQuestion (?) " + response + " ??> " + correctAnswer);
        if (response.startsWith("T") || response.startsWith("t"))
            return correctAnswer == true;
        else
            return correctAnswer == false;
    }
    
    /**
     * Check if a provided response is correct.
     *
     * @param response - the response.
     * @return true if the response matches the criteria specified in the specTkzr passed to the
     *         constructor or false otherwise.
     */
    public boolean isCorrect( boolean response ) {
        return response == correctAnswer;
    }
    
    /**
     * @return the string representation of the correct answer
     */
    public String getAnswer() {
        String answer = new String();
        
        answer = answer.valueOf(correctAnswer);
        return answer;
    }
    
    public boolean getCorrectAnswer() {
        return correctAnswer;
    }
    
    public TFQuestionParser(StreamTokenizer stok) {
        super(stok);
        if (debug) System.out.println("parsing TFQuestion -- from a stream tokenizer");
        
        String tmp;
        
        stok.wordChars(33,126); //include every printable characters except whitespaces.
        
        question = parseUntil(stok, "ENDTEXT");
        tmp = parseUntil(stok, "ANSWER");
        correctAnswer = parseUntil(stok, "ENDANSWER").equalsIgnoreCase("t");
        
        //try to reset the stream tokenizer
        //is it good enough? (FIXME)
        stok.quoteChar('"');
        stok.quoteChar('\'');
    }
    
}
