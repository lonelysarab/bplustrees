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
 * Base Class to grok Visualizer Question specification strings.
 *
 * @author JRE
 */

abstract public class QuestionParser {

    protected boolean debug = true;
    protected String question; //the question itself

    /**
     * Create a new QuestionParser from the specification in the Tokenizer.
     *
     * @param specTkzr a StringTokenizer pointing to the beginning of a valid Question specification.
     *                 Once constructed, this will point to the first token beyond the end of the
     *                 Question specification.
     */
    public QuestionParser(StringTokenizer specTkzr) { }
    public QuestionParser(StreamTokenizer stok) {}

    /**
     * @return the question specified by the specTkzr provided to the constructor.
     */
    public String getQuestion()
    {
        return question;
    }

    /**
     * Check if a provided response is correct.
     *
     * @return true if the response matches the criteria specified in the specTkzr passed to the
     *         constructor or false otherwise.
     */
    abstract public boolean isCorrect( String response );

    /**
     * Return a string representing the correct / acceptable answer(s)
     */
    abstract public String getAnswer ();
    
    protected String parseUntil(StreamTokenizer stok, String endpoint)
    {
      int tokenType;
      String result = "";

      try
      {
        tokenType = stok.nextToken();

        while (!stok.sval.trim().equals(endpoint))
        {
          switch(tokenType)
          {
            case StreamTokenizer.TT_WORD:
              result = result+stok.sval + " ";
              break;
            case StreamTokenizer.TT_NUMBER:
              result = result+String.valueOf(stok.nval) + " ";
              break;
            case StreamTokenizer.TT_EOL:
              result = result+"\n";
              break;
            default:
              result = result+String.valueOf((char)stok.ttype)+" ";
              break;
          }
          tokenType = stok.nextToken();
        }
      }
      catch (Exception e)
      {
      }

      return result.trim();
    }
}
