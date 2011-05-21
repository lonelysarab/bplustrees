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
 * QuizListener.java
 *
 * Created on August 26, 2005, 1:20 AM
 */

package jhave.event;

/**
 * Interface for commonality between classes that need to recieve QuizEvents.
 * @author Adam Klein
 */
public interface QuizListener extends java.util.EventListener {

    /**
     * Handle a QuizEvent sent QuizView.
     * @param e the QuizEvent that was recieved.
     */
    public void handleQuizEvent(QuizEvent e);
}
