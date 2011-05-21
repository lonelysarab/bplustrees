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
 * AlgorithmComboBoxModel.java
 *
 * Created on June 13, 2002, 1:11 AM
 */

package jhave.client.misc;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

import jhave.Algorithm;
/**
 * A ComboBoxModel designed for keeping a list of algorithms.
 * @author  Chris Gaffney
 */
public class AlgorithmComboBoxModel extends DefaultComboBoxModel {
    
    /**
     * Creates a new instance of AlgorithmComboBoxModel. 
     */
    public AlgorithmComboBoxModel() {
        super();
    }
    
    /** 
     * Instantiates a new instance of AlgorithmComboBoxModel from an Object array.
     * With initial data specified in initialItems
     * @param initialItems Initial data displayed
     */
    public AlgorithmComboBoxModel(Algorithm[] initialItems) {
        super(initialItems);
    }
    
    /**
     * Returns the algoritms in a vector.
     * @return Vector the algorithms displayed contained in a vector.
     */
    public Vector getAlgorithmVector() {
        Vector returnedVector = new Vector(getSize());
        
        // Creates a Vector out of the elements
        for(int index = 0; index < getSize(); index++) {
            returnedVector.addElement(getElementAt(index));
        }
        return returnedVector;
    }
    
    /** 
     * Adds an Algorithm to the combo box.
     * @param algorithm the algorithm to add.
     */
    public void addElement(Algorithm algorithm) {
        super.addElement(algorithm);
    }
}