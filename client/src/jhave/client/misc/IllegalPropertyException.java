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
 * IllegalPropertyException.java
 *
 * Created on June 19, 2002, 12:31 AM
 */

package jhave.client.misc;

import jhave.core.JHAVETranslator;

/**
 * Exception when stands for an Illegal value being entered for a property
 * in the ClientProperties class.
 * @author  Chris Gaffney
 */
public class IllegalPropertyException extends java.lang.Exception {
    
    /** Key of illegal property. */
    private String key;
    /** Value of illegal property. */
    private String property;
    /** Reason why it is illegal. */
    private String reason;
    /** If defaults are being used. */
    private boolean defaults = false;
    
    /** 
     * Creates a new instance of <code>IllegalPropertyException</code> without detail message.
     * @param key Key of illegal property.
     * @param property Value of illegal proprty.
     * @param reason Reason for being illegal.
     * @param defaults if the default values are being used.
     */
    public IllegalPropertyException(String key, String property, String reason, boolean defaults) {
        super();
        
        this.key = key;
        this.property = property;
        this.reason = reason;
        this.defaults = defaults;
    }
    
    /**
     * Returns the key of the illegal propety.
     * @return String Key of illegal property.
     */
    public String getKey() {
        return key;
    }
    
    /**
     * Returns the property that is illegal.
     * @return String Illegal property.
     */
    public String getProperty() {
        return property;
    }
    
    /** 
     * Returns the reason for being illegal.
     * @return String Reason for being illegal.
     */
    public String getReason() {
        return reason;
    }
    
    /**
     * Returns if default values are being used.
     * @return boolean if default values are being used.
     */
    public boolean isDefaults() {
        return defaults;
    }
    
    /** 
     * Returns a message detailing the problem with the property.
     * @return String error message detailing the problem.
     */
    public String getMessage() {
        String returnedString;
        
        if(isDefaults()) {
          returnedString = JHAVETranslator.translateMessage("illegalPropertyDefault",
              new String[] {getKey(), getProperty(), getReason() });
//            returnedString = "Default value: Key - " + getKey() + " Property - " + getProperty() + " is illegal because " + getReason();
        } else {
          returnedString = JHAVETranslator.translateMessage("illegalPropertyOther",
              new String[] {getKey(), getProperty(), getReason() });
//           returnedString = "Line: \"" + getKey() + "=" + getProperty() + "\" is illegal because " + getReason();
        }
        
        return returnedString;
    }
}