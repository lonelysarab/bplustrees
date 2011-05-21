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
 * NetworkListener.java
 *
 * Created on June 12, 2002, 3:00 AM
 */

package jhave.event;

/** 
 * Interface for receiving NetworkEvents.
 *
 * @author  Chris Gaffney
 * @verson 1.0
 */
public interface NetworkListener extends java.util.EventListener {
    /** 
     * Method invoked when any inbound transactions are made
     * @param networkEvent Event sent from the network controller
     */
    public void inboundTransaction(jhave.event.NetworkEvent networkEvent);
    
    /** 
     * Method invoked when any outbound transactions are made
     * @param networkEvent Event sent from the network controller
     */
    public void outboundTransaction(jhave.event.NetworkEvent networkEvent);
}