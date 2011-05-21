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

package jhave;

import java.io.Serializable;

import java.util.Vector;

import jhave.core.JHAVETranslator;


/**
 * Algorithm Class
 * @author Laura L. Norton
*/
public class Algorithm implements Serializable
{  
  /**
   * This is a line of text describing the algorithm.
   */
  private String DescriptiveText = ""; 

 
  /**
   * This is a one word name of the algorithm.
   * Two different algorithms should not have the same name.
   */
  private String AlgoName = "";        
  

  /**
   * If true, the server generates its script file.
   * If false, its script file is fixed.
   */
  private boolean Dynamic = false;
  

  /**
   * If true, this algorithm may share data with itself or other algorithms.
   * If false, the server always creates a new script for it, or the script is fixed. 
   */
  private boolean May_Have_Friends = true;
  

  /**
   * If true and this algorithm requires an input generator, this means that the server
   * should always send the imput generator.
   * If false and this algorithm requires an input generator, this means that the server
   * should only send the input generator when new data is choosen by the user.
   */
  private boolean Always_Needs_Input_Generator = true;
  

  /**
   * This indicates the visualizer that can run the visualizations.  
   * i.e. Samba, Gaigs, JavaApp  
   */
  private String VisualizerType = "";
  

  /**
   * This holds pointers to all the algorithms that may share data with this one.
   */
  private Vector AlgoFriendVect;
    
  
  /**
   * Construct an algorithm with an empty friend vector.
   */
  public Algorithm(){
    AlgoFriendVect = new Vector();
  }
  

  /**
   * This allows the client to set a line of descriptive text for the algorithm.
   */
  public void SetDescriptiveText(String text)
  {
    DescriptiveText = text;
  }
  

  /**
   * This returns a line of descriptive text about the algorithm.
   */
  public String GetDescriptiveText()
  {
    return DescriptiveText;
  }
  

  /**
   * This allows the client to set the one word name of the algorithm.
   * Two different algorithms should not have the same name.
   */
  public void SetAlgoName(String name)
  {
    AlgoName = name;
  }
  

  /**
   * This returns the one word name of the algorithm.
   * This algorithm name may be followed by a separator slash and an
   * input generator name if there exist more than one input generator
   * for this particular algorithm.  If that is the case, this
   * function will return that full string.  If you need just the algo
   * name with out the input generator, use the utility method
   * GetAlgoNameMinusIngen
   * Two different algorithms should not have the same name.
   */
  public String GetAlgoName()
  {
    return AlgoName;
  }

  /**
   * Given the full AlgoName as returned by GetAlgoName, this method
   * return only that portion of the algo name up to but not including
   * the separator and ingen name
   */
  
    public static String GetAlgoNameMinusIngen(String theFullName) {
	int the_slash = theFullName.indexOf('/');
	if (the_slash == -1)
	    return theFullName;
	else
	    return theFullName.substring(0, the_slash);
    }

  /**
   * This allows the client to set all the pertinant information about the algorithm.
   * "static" means the script is fixed, so the algorithm cannot share data/has no friends.
   * "new_data_only" means that a script file is generated for the algorithm each time,
   * but new data must always be generated for it.
   * "dynamic" means that the algorithm has a script generated for it and it may share
   * its data with itself and the algorithms in its friend vector.
   * "dynamic-ingen" means the same as dynamic, but if the algorithm requires an input
   * generator the server needs to send it only when new data when the user
   * requests new data be generated.
   */
  public void SetDynamicStatus(String Status)
  {
    if ((Status.trim()).equalsIgnoreCase("static"))
    {
      May_Have_Friends = false;
      AlgoFriendVect.removeAllElements();
      Dynamic = false;
    }
    else
    {
      Dynamic = true;
      if((Status.trim()).equalsIgnoreCase("new_data_only"))
      {
        May_Have_Friends = false;
        AlgoFriendVect.removeAllElements();
        Always_Needs_Input_Generator = true;
      }
      else if((Status.trim()).equalsIgnoreCase("dynamic-ingen"))
      {
        Always_Needs_Input_Generator = false;
        May_Have_Friends = true;
      }
      else if((Status.trim()).equalsIgnoreCase("dynamic"))
      {
        Always_Needs_Input_Generator = true;
        May_Have_Friends = true;
      }
      else
        System.out.println(JHAVETranslator.translateMessage("invalidStatus", 
            Status.trim()));
//        System.out.println(Status.trim() + " is invalid, check \"cat\" file.");
    }      
  }
  

  /**
   * This returns true, if the server needs to generate a script file.
   * false, if the script is fixed.
   */             
  public boolean GetDynamicStatus()
  {
    return Dynamic;
  }
  

  /**
   * This returns true, if the server always needs to send its input generator.
   * false, if the input generator is only neccessary with new data.
   */ 
  public boolean GetAlwaysNeedsInputGenerator()
  {
    return Always_Needs_Input_Generator;
  }
  

  /**
   * This allows the client to set a one word string identifying the visualizer that
   * can run this algorithm's script file.  i.e. Samba, Gaigs, or JavaApp
   */
  public void SetVisualizerType(String vis)
  {
    VisualizerType = vis;
  }


  

  /**
   * This returns the a one word string identifying the visualizer.
   */
  public String GetVisualizerType()
  {
    return VisualizerType;
  }
  

  /**
   * If this algorithm can share data, this allows the client to put 
   * an algorithm in this algorithm's friend vector.
   */
  public void AddAlgoFriend(Algorithm friend)
  {
    if(May_Have_Friends)
      AlgoFriendVect.addElement(friend);
  }
  

  /**
   * This returns true if this algorithm may share data one in queation.
   * false if not.
   */
  public boolean IsAlgoFriend(Algorithm friend)
  {
    return AlgoFriendVect.contains(friend);
  }
  

  /**
   * This compares two algorithms, making the assumption that each algorithm's name is unique.
   */
  public boolean equals(Algorithm algo)
  {
    return AlgoName.equalsIgnoreCase(algo.GetAlgoName());
  }


  /**
   * Converts the algorithm to a meaningful string.
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder(4096);
    sb.append(JHAVETranslator.translateMessage("algoToString", 
        new Object[] {AlgoName, DescriptiveText, VisualizerType,
        Boolean.valueOf(Dynamic), Boolean.valueOf(Always_Needs_Input_Generator)
    }));
    
//    String AlgoStr = "";
//    AlgoStr = AlgoStr + "AlgoName: " + AlgoName + "\n";
//    AlgoStr = AlgoStr + "DescriptiveText: " + DescriptiveText + "\n";
//    AlgoStr = AlgoStr + "Visualizer: " + VisualizerType + "\n";
//    AlgoStr = AlgoStr + "Dynamic: " + Dynamic + "\n";
//    AlgoStr = AlgoStr + "Always needs ingen: " + Always_Needs_Input_Generator + "\n";
//    AlgoStr = AlgoStr + "Has friends: " + "\n";
    if (AlgoFriendVect.size() == 0)
      sb.append(JHAVETranslator.translateMessage("noFriends"));
//      AlgoStr = AlgoStr + "none\n";
    else
    {
      for (int index = 0; index < AlgoFriendVect.size(); index++)
        sb.append(((Algorithm)AlgoFriendVect.elementAt(index)).GetAlgoName()).append(" ");
//        AlgoStr = AlgoStr+((Algorithm)AlgoFriendVect.elementAt(index)).GetAlgoName()+" ";
    }      
    return sb.toString();
//    return AlgoStr;
  }   
}
