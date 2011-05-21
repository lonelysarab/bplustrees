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
 * QuizEvent.java
 *
 * Created on August 26, 2005, 1:13 AM
 */

package jhave.event;

import java.util.EventObject;

/**
 * Event used with QuizListener to inform listeners when a quiz dialog
 * is shown or disposed.
 * @author Adam Klein
 */
public class QuizEvent extends EventObject {

    /** If the event signals a shown dialog. */
    private boolean openQuestion = false;

    /**
     * Creates a new instance of QuizEvent.
     * @param source the object that fired the event.
     * @param dialogOpen if the QuestionDialog is open.
     */
    public QuizEvent(Object source, boolean dialogOpen) {

	super(source);
	openQuestion = dialogOpen;
    }

    /**
     * Returns whether the question dialog is open.
     * @return openQuestion if the dialog is open.
     */
    public boolean open() {
	
	return openQuestion;
    }
}
