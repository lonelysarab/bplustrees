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
 * QuestionEvent.java
 *
 * Created on July 29, 2004, 11:15 PM
 */

package jhave.event;

import java.util.EventObject;

import jhave.question.Question;
/**
 * Event used in the QuestionListener class to alert listeners of happenings
 * with questions.
 * @author  Chris Gaffney
 */
public class QuestionEvent extends EventObject {
    /** The question attached to this event. */
    private Question question;
    
    /** 
     * Creates a new instance of QuestionEvent.
     * @param source the object who fired that event.
     * @param question the question which was the reason for the event.
     */
    public QuestionEvent(Object source, Question question) {
        super(source);
        this.question = question;
    }
    
    /**
     * Returns the question attached to this event.
     * @return Question the question attached to this event.
     */
    public Question getQuestion() {
        return question;
    }
}
