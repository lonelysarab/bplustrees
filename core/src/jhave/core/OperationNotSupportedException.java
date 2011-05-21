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
 * OperationNotSupported.java
 *
 * Created on August 2, 2004, 1:46 PM
 */

package jhave.core;

/**
 * Runtime Exception thrown from Visualizer methods such as play or stepForward
 * that are not supported by a specific visualizer.
 * @author  Chris Gaffney
 */
public class OperationNotSupportedException extends RuntimeException {
    
    /** 
     * Creates a new instance of a OperationNotSupportedException with no message.
     */
    public OperationNotSupportedException() {
        super();
    }
    
    /**
     * Creates a new instance of a OperationNotSupportedException with given
     * message.
     * @param message the message attached to the exception.
     */
    public OperationNotSupportedException(String message) {
        super(message);
    }
    
}
