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

package gaigs2;
/*
 * Gary Cornell and Cay S. Horstmann, Core Java (Book/CD-ROM)
 * Published By SunSoft Press/Prentice-Hall
 * Copyright (C) 1996 Sun Microsystems Inc.
 * All Rights Reserved. ISBN 0-13-565755-5
 *
 * Permission to use, copy, modify, and distribute this
 * software and its documentation for NON-COMMERCIAL purposes
 * and without fee is hereby granted provided that this
 * copyright notice appears in all copies.
 *
 * THE AUTHORS AND PUBLISHER MAKE NO REPRESENTATIONS OR
 * WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR 8NON-INFRINGEMENT. THE AUTHORS
 * AND PUBLISHER SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED
 * BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

/**
 * A simple implementation of a linked list
 * @version 1.01 15 Feb 1996
 * @author Cay Horstmann
 */
public class LinkedList {
    /**
     * resets the cursor
     */
    public void reset() {
        pre = null;
    }
    
    /**
     * @return true iff the cursor is not at the end of the
     * list
     */
    public boolean hasMoreElements() {
        return cursor() != null;
    }
    
    /**
     * move the cursor to the next position
     * @return the current element (before advancing the
     * position)
     * @exception java.util.NoSuchElementException if already at the
     * end of the list
     */
    public Object nextElement() {
        if (pre == null) {
            pre = head;
        } else {
            pre = pre.next;
        }
        if (pre == null) {
            throw new java.util.NoSuchElementException();
        }
        return pre.data;
    }
    
    /**
     * @return the current element under the cursor
     * @exception java.util.NoSuchElementException if already at the
     * end of the list
     */
    public Object currentElement() {
        Link cur = cursor();
        if (cur == null) {
            throw new java.util.NoSuchElementException();
        }
        return cur.data;
    }
    
    /**
     * insert before the iterator position
     * @param n the object to insert
     */
    public void insert(Object n) {
        Link p = new Link(n, cursor());
        
        if (pre != null) {
            pre.next = p;
            if (pre == tail) {
                tail = p;
            }
        } else {
            if (head == null) {
                tail = p;
            }
            head = p;
        }
        
        pre = p;
        len++;
    };
    
    /**
     * insert after the tail of the list
     * @param n - the value to insert
     */
    public void append(Object n) {
        Link p = new Link(n, null);
        if (head == null) {
            head = tail = p;
        } else {
            tail.next = p;
            tail = p;
        }
        len++;
    }
    
    /**
     * remove the element under the cursor
     * @return the removed element
     * @exception java.util.NoSuchElementException if already at the
     * end of the list
     */
    public Object remove() {
        Link cur = cursor();
        if (cur == null) {
            throw new java.util.NoSuchElementException();
        }
        if (tail == cur) {
            tail = pre;
        }
        if (pre != null) {
            pre.next = cur.next;
        } else {
            head = cur.next;
        }
        len--;
        return cur.data;
    }
    
    /**
     * @return the number of elements in the list
     */
    public int size() {
        return len;
    }
    
    /**
     * @return an enumeration to iterate through all elements
     * in the list
     */
    public java.util.Enumeration elements() {
        return new ListEnumeration(head);
    }
    
    
    public static void main(String[] args) {
        LinkedList a = new LinkedList();
        for (int i = 1; i <= 10; i++) {
            a.insert(new Integer(i));
        }
        java.util.Enumeration e = a.elements();
        //while (e.hasMoreElements())
        //System.out.println(e.nextElement());
        
        a.reset();
        while (a.hasMoreElements()) {
            a.remove();
            a.nextElement();
        }
        a.reset();
        //while (a.hasMoreElements())
        //System.out.println(a.nextElement());
    }
    
    private Link cursor() {
        if (pre == null) {
            return head;
        } else {
            return pre.next;
        }
    }
    
    private Link head;
    private Link tail;
    private Link pre; // predecessor of cursor
    private int len;
    
    private class Link {
        Object data;
        Link next;
        Link(Object d, Link n) { data = d; next = n; }
    }
    
    /**
     * A class for enumerating a linked list
     * implements the Enumeration interface
     */
    private class ListEnumeration implements java.util.Enumeration {
        public ListEnumeration( Link l) {
            cursor = l;
        }
        
        /**
         * @return true iff the iterator is not at the end of the
         * list
         */
        public boolean hasMoreElements() {
            return cursor != null;
        }
        
        /**
         * move the iterator to the next position
         * @return the current element (before advancing the
         * position)
         * @exception NoSuchElementException if already at the
         * end of the list
         */
        public Object nextElement() {
            if (cursor == null) {
                throw new java.util.NoSuchElementException();
            }
            Object r = cursor.data;
            cursor = cursor.next;
            return r;
        }
        
        private Link cursor;
    }
}