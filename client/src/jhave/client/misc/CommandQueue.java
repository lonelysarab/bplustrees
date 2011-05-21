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
 * CommandQueue.java
 *
 * Created on August 3, 2004, 4:49 PM
 */

package jhave.client.misc;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 *
 * @author  Chris Gaffney
 */
public class CommandQueue {
    /** Queue of commands to be run. */
    private List queue;
    /** Processing thread. */
    private Thread processor;
    
    /**
     * Creates a new instance of CommandQueue.
     */
    public CommandQueue() {
        // Additions will be done from a different thread than removals or reads
        // so we need to used a synchronized Collection.
        queue = Collections.synchronizedList(new LinkedList());
        processor = new ProcessingThread();
        processor.start();
    }
    
    /**
     * Add a new command to the queue.
     * @param command the new command.
     */
    public void enqueue(Runnable command) {        
        queue.add(command);
        
        // Get the lock on the thread so we can notify it.
        synchronized(processor) {
            processor.notify();
        }
    }
    
    /**
     * Thread for processing commands in order.
     */
    private class ProcessingThread extends Thread {
        public ProcessingThread() {
            setDaemon(true);
        }
        
        public void run() {
            while(true) {
                if(queue.size() == 0) {
                    try {
                        // Get the lock on ourself so we can wait.
                        synchronized(this) {
                            wait();
                        }
                    } catch (InterruptedException e) {
                        // Do something? Maybe break
                        //break;
                        continue;
                    }
                }
                
                Runnable r = (Runnable)queue.get(0);
                r.run();
                queue.remove(0);
            }
        }
    }
}
