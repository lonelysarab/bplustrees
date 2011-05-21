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
 * Created on 28.07.2007 by Guido Roessling <roessling@acm.org>
 */
package jhave.core;

import java.util.Locale;

import translator.TranslatableGUIElement;
import translator.Translator;

public class JHAVETranslator {
  private static Locale locale;
  private static String resourceName;
  private static Translator translator;
  
  public JHAVETranslator() {
    this(Locale.US);
  }
  
  public JHAVETranslator(Locale targetLocale) {
    setResourceName("jhaveRes");
    setTargetLocale(targetLocale);
  }
    
  public static Translator getJHAVETranslator() {
    if (translator == null)
      translator = new Translator(JHAVETranslator.getResourceName(),
          JHAVETranslator.getLocale());
    return translator;
  }
  
  public static TranslatableGUIElement getGUIBuilder() {
    return getJHAVETranslator().getGenerator();
  }
  
  public static Locale getLocale() {
    if (locale == null)
      locale = Locale.US;
    return locale;
  }

  public static String getResourceName() {
    if (resourceName == null)
      resourceName = "jhaveRes";
    return resourceName;
  }
  
  public static void setResourceName(String targetResourceName) {
    if (targetResourceName == null)
      resourceName = "jhaveRes";
    else
      resourceName = targetResourceName;
  }
 
  public static void setTargetLocale(Locale targetLocale) {
    if (targetLocale == null && locale == null)
      locale = Locale.US;
    else
      locale = targetLocale;
  }
  
  public static String translateMessage(String key) {
    return getJHAVETranslator().translateMessage(key);
  }

  public static String translateMessage(String key, Object param) {
    return translateMessage(key, new Object[] { param });
  }

  public static String translateMessage(String key, Object[] params) {
    return getJHAVETranslator().translateMessage(key, params);
  }
}
