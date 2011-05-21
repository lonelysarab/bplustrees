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
 * AlgorithmCellRenderer.java
 *
 * Created on June 26, 2002, 4:32 AM
 */

package jhave.client.misc;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import jhave.Algorithm;
/**
 * A ListCellRenderer designed to be able to display different information about
 * the algorithms. It can display the Algorithm's name, descriptive text, dynamic status,
 * and visualizer type.
 * @author  Chris Gaffney
 */
public class AlgorithmListCellRenderer extends DefaultListCellRenderer {
    /** Display the algorithms name. */
    public static final int DISPLAY_ALGORITHM_NAME = 1;
    /** Display the algorithms descriptive text. */
    public static final int DISPLAY_DESCRIPTIVE_TEXT = 2;
    /** Display the algorithms dynamic status. */
    public static final int DISPLAY_DYNAMIC_STATUS = 3;
    /** Display the algorithms visualizer. */
    public static final int DISPLAY_VISUALIZER_TYPE = 4;
    /** The type of information that is being displayed. */
    private int displayType = 1;
    
    /**
     * Creates a new instance of AlgorithmCellRenderer.
     * @param displayType the type of information to be displayed.
     */
    public AlgorithmListCellRenderer(int displayType) {
        this.displayType = displayType;
    }
    
    /**
     * Returns a component that is displayed as the item in the list box this is part of.
     * @param list the list of items.
     * @param value ?
     * @param index the index in the list.
     * @param isSelected if the item is selected.
     * @param hasFocus if the item has focus.
     * @return Component the component displayed in the list.
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
        if(value instanceof Algorithm) {
            Algorithm display = (Algorithm)value;
            switch(displayType) {
                case DISPLAY_DESCRIPTIVE_TEXT:
                    setText(display.GetDescriptiveText());
                    break;
                case DISPLAY_DYNAMIC_STATUS:
                    setText(display.GetDynamicStatus() + "");
                    break;
                case DISPLAY_VISUALIZER_TYPE:
                    setText(display.GetVisualizerType());
                    break;
                case DISPLAY_ALGORITHM_NAME:
                default:
                    setText(display.GetAlgoName());
                    break;
            }
        }
        return this;
    }
    
    /** 
     * Sets what kind of information will be displayed.
     * @param type new type to display.
     */
    public void setDisplayType(int type) {
        this.displayType = (type < 1 || type > 4) ? 1 : type;
    }
}
