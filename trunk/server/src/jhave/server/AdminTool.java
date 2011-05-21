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

package jhave.server;
//AdminTool.java

import java.io.*;

/**
* This class provide an application for administrative purposes.
*/
public class AdminTool
{  
  private DBTransaction adminDB;	//DBTransaction class

  /**
  * Standard constructor. Instantiates DBTransaction class.
  */
  public AdminTool()
  {
    adminDB = new DBTransaction();
  }

  /**
  * Instantiate and run AdminTool.
  */
  public static void main(String[] args)
  {
    AdminTool adm = new AdminTool();
    adm.executeAdminTool();
  }

  /**
  * AdminTool main loop.
  */
  private void executeAdminTool()
  {
    try
    {
      int selection = 0;

      System.out.println("\nWelcome to JHAVE admin application.");
      displayMenu();
      selection = readInt();

      while (selection != 4)
      {
        switch (selection)
        {
          case 1:
            deleteCourse();
            break;
          case 2:
            deleteOldStudent();
            break;
          case 3:
            deleteInstructor();
            break;
          default:
            System.out.println("Invalid selection!");
            break;
        }

        displayMenu();
        selection = readInt();
      }
    }
    catch (Exception e)
    {
      System.err.println("Exception occured in executting administrative tool : "+e.toString());
    }
   
  }
    
  /**
  * Display main menu.
  */
  private void displayMenu()
  {
    System.out.println("\nPlease choose from the selection below:\n");
    System.out.println("1. Delete a course");
    System.out.println("2. Purge old students");
    System.out.println("3. Purge old instructor");
    System.out.println("4. Quit");
    System.out.print("\nEnter your selection number: ");
  }
  
  /**
  * Reads a line of string from the standard input.
  * @return the string
  */
  private String readString()
  {
    String stringRead;
    try
    {
      BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
      do
        stringRead = userIn.readLine().trim();
      while (stringRead.equals(""));
    }
    catch (IOException e)
    {
      stringRead = new String("");
      System.err.println("Exception occured in reading user input\n"+e.toString());
    }
    return stringRead;
  }

  /**
  * Reads a string using readString method and converts it to a int.
  * @return the int
  */
  private int readInt()
  {
    int value = 0;
    try
    {
      String input = readString();
      value = (new Integer(input)).intValue();
    }
    catch (Exception e)
    {
      System.err.println("Not reading an integer!");
    }
    return value;
  }

  /**
  * Deletes a course from the database.
  * @see DBTransaction.deleteCourse
  */
  private void deleteCourse()
  {
    String courseNum = new String("");
    String instructor = new String("");

    try
    {
      System.out.print("Please enter the instructor username: ");
      instructor = readString();

      System.out.print("Please enter the course number      : ");
      courseNum = readString();

      adminDB.deleteCourse(courseNum, instructor);
    }
    catch (Exception e)
    {
      System.err.println("Exception occured in delting course: "+courseNum+" for instructor: "+instructor);
      System.err.println(e.toString());
    }
  }
  
  /**
  * Deletes old students from the database
  * @see DBTransaction.deleteOldStudent
  */
  private void deleteOldStudent()
  {
    try
    {
      int old;
      System.out.print("How old: ");
      old = readInt();
      adminDB.deleteOldStudent(old);
    }
    catch (Exception e)
    {
      System.err.println(e.toString());
    }
  }	     
  
  /**
  * Deletes an instructor.
  * @see DBTransaction.deleteInstructor
  */
  private void deleteInstructor()
  {
    try
    {
      String instructor;
      System.out.print("Please enter instructor's username: ");
      instructor = readString();
      adminDB.deleteInstructor(instructor);
    }
    catch (Exception e)
    {
      System.err.println(e.toString());
    }
  }	     
}
