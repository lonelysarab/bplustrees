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
// DBTransaction.java

import java.io.*;
import java.sql.*;
import java.lang.*;
import java.util.*;

/**
* This class contains methods to handle database transactions
* that are require for Jhave instructor application and administrator application
*/
public class DBTransaction
{
  private static boolean debug = true;	// are we debugging?
  private Connection db;		// database connection

  /**
  * Instantiate the class.
  */
  public DBTransaction()
  {
  }
  
  /**
  * Add a student to a course if not already so.
  * If student not registered in the student table yet, the user are required to enter the student information so that the student can be added.
  * Throws InvalidDBRequestException if user name belongs to an instructor, student already registered in the course, student information differs than the one in the database, student information not available when needed, error occured during insertion, or if other exception occured
  * Throws FileFailureException if fail to create folder for the student.
  * @param username student's user name
  * @param courseID course id (course number + instructor name)
  * @param lastname student's last name
  * @param firstname student's first name
  * @param initPass initial password for the student
  * @throws InvalidDBRequestException
  */
  public void addStudent(String courseID, String username, String lastName, String firstName, String initPass) throws InvalidDBRequestException, FileFailureException
  {
    try
    {
      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      ResultSet rs;

      int count = 0;

      //check if user name belongs to an instructor
      rs = stmt.executeQuery("select login from instructor where login = '"+username+"'");
      if (rs.next())
        throw new InvalidDBRequestException("User name is reserved by an instructor");
      
      //check if student in the database or not
      rs = stmt.executeQuery("select last_name, first_name from student where login = '"+username+"'");
      if (rs.next())
      {
        //check if user name belongs to another student
        if (!lastName.equals("") && (!lastName.equalsIgnoreCase(rs.getString(1).trim()) || !firstName.equalsIgnoreCase(rs.getString(2).trim())))
          throw new InvalidDBRequestException("User name is used by: "+rs.getString(2).trim()+" "+rs.getString(1).trim());
      }
      else
      {
        //check if student's information is given
        if (lastName.equals(""))
          throw new InvalidDBRequestException("Student is not registered in the database. Please enter the student's name"); 
        
        // insert student into hte database
        count = stmt.executeUpdate("insert into student values ('"+username+"', '"+lastName+"', '"+firstName+"', '"+initPass+"', now())");  
        if (count != 1)
          throw new InvalidDBRequestException("Error occured during insertion!");
    
        //create student's folder
        File newStudentDir = new File("./StudentQuizzes/"+username);
        if (!(newStudentDir.mkdir()))
        {
          System.err.println ("Error in creating new folder for student: "+username);
          throw new FileFailureException("huh");
        }
      }
      
      //check if student is registered in the course
      rs = stmt.executeQuery("select * from courseRoster where course_id = '"+courseID+"' and user_login = '"+username+"'");
      if (rs.next())
	throw new InvalidDBRequestException("Student is already registered to the course");
	
      //register student to the course
      count = stmt.executeUpdate("insert into courseRoster values ('"+courseID+"', '"+username+"')");  
      if (count != 1)
        throw new InvalidDBRequestException("Error occured during insertion!");

      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in addstudent: "+e.getMessage());
      throw new InvalidDBRequestException("???");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Internal Server Error");
    }
    
  }

  /**
  * Add a quiz to a course if not already so. Also write the menu file for the course.
  * Throws InvalidDBRequestException if quiz already in the course, error occured during insertion, or other exception occured.
  * Throws FileFailureException if fail to append quiz to the menu.
  * @param quizname the quiz name
  * @param courseID the course id (course number + instructor name)
  * @throws InvalidDBRequestException
  */
  public void addQuiz(String quizName, String courseID) throws InvalidDBRequestException, FileFailureException
  {
    try
    {
      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      Statement cstmt = db.createStatement();
      ResultSet rs, courseRS;

      int count = 0;

      //get quiz info
      courseRS = cstmt.executeQuery("select menu_text, script_type, visual_type from test where name = '"+quizName+"'");

      //check if quiz in the database
      if (!courseRS.next())
        throw new InvalidDBRequestException("Quiz is not registered in the database");
      else
      {
        //check if quiz already in the course
        rs = stmt.executeQuery("select test_name from courseTest where test_name = '"+quizName+"' and course_id = '"+courseID+"'");
        if (rs.next())
          throw new InvalidDBRequestException("Quiz is already added for the course");
        else
	{
          count = stmt.executeUpdate("insert into courseTest (course_id, test_name) values ('"+courseID+"', '"+quizName+"')");
          if (count != 1)
            throw new InvalidDBRequestException("Error occured during insertion");
          else
	  {
	    //append quiz info to the course menu
            try
	    {
              PrintWriter fileOStream = new PrintWriter(new FileOutputStream("./html_root/cat/"+courseID+".list", true));
	      if (debug) System.out.println(courseRS.getString(1).trim()+"\n"+quizName+" "+courseRS.getString(2).trim()+" "+courseRS.getString(3).trim().toLowerCase()+"\n****\n");
	      fileOStream.print(courseRS.getString(1).trim()+"\n"+quizName+" "+courseRS.getString(2).trim()+" "+courseRS.getString(3).trim().toLowerCase()+"\n****\n");
	      fileOStream.flush();
	      fileOStream.close();
            }
	    catch (Exception e)
	    {
              System.err.println("Error in creating the menu: "+e.getMessage());
	      throw new FileFailureException("Error in creating the menu: "+e.getMessage());
            }
          }
        }
      }

      courseRS.close();
      rs.close();
      stmt.close();
      cstmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in addQuiz: "+e.getMessage());
      throw new InvalidDBRequestException("??? ");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Internal Server Error");
    }
  }

  /**
  * Add a course into the database if not already so.
  * Throws InvalidDBRequestException if course already in the DB, error occured during insertion, or other exception occured.
  * @param courseNum course number
  * @param courseName name of the course
  * @param instructor name of the instructor who owns the course
  * @throws InvalidDBRequestException
  */
  public void addCourse(String courseNum, String courseName, String instructor) throws InvalidDBRequestException
  {
    String courseId = courseNum+instructor;

    try
    {
      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      ResultSet rs;

      int count = 0;

      //check if course already in the database
      rs = stmt.executeQuery("select course_id from course where course_id = '"+courseId+"'");
      if (rs.next())
        throw new InvalidDBRequestException("Course is already in the database");
        
      //insert course into database
      count = stmt.executeUpdate("insert into course values ('"+courseId+"', '"+courseName+"', '"+courseNum+"', '"+instructor+"')");
      if (count != 1)
        throw new InvalidDBRequestException("Something happen during insertion!");

      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in addCourse: "+e.getMessage());
      throw new InvalidDBRequestException("??? ");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Internal Server Error");
    }
  }

  /**
  * Remove a student from a particular course.
  * Also Deletes all the quiz vizualisation files in the student's directory which relates to the course.
  * Caution: vizualisation file will be deleted eventhough it also relates to another course if the student is also registered to that course. (FIX ME!)
  * Throws InvalidDBRequestException if the student is not registered in the course, error occured during deletion, or other exception occured.
  * @param username student's user name
  * @param courseID course id (course number + instructor name)
  * @throws InvalidDBRequestException
  */
  public void deleteStudent(String username, String courseID) throws InvalidDBRequestException
  {
    try
    {
      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      ResultSet rs;

      int count = 0;

      //check if student registered to the course
      rs = stmt.executeQuery("select * from courseRoster where course_id = '"+courseID+"' and user_login = '"+username+"'");
      if (!rs.next())
	throw new InvalidDBRequestException("Student is not registered to the course");
	
      //remove student from the course
      count = stmt.executeUpdate("delete from courseRoster where course_id = '"+courseID+"' and user_login = '"+username+"'");  
      if (count != 1)
        throw new InvalidDBRequestException("Error occured during deletion!");

      //delete the quiz visualization files
      rs = stmt.executeQuery(
        "select distinct unique_id, s.test_name from scores s, courseTest t "+
        "where s.test_name = t.test_name "+
        "and course_id = '"+courseID+"' "+
        "and user_login = '"+username+"'");
      while (rs.next())
      {
        deleteVisualization(rs.getString(1), username,rs.getString(2));
        count = stmt.executeUpdate("delete from scores where unique_id = "+rs.getString(1).trim());
      }
    
      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in addstudent: "+e.getMessage());
      throw new InvalidDBRequestException("???");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Internal Server Error");
    }
 }

  /**
  * Remove a quiz from a course.
  * Also deletes all the quiz visualization files from the students' folder who is registered in the course.
  * Caution: vizualisation file will be deleted eventhough it also relates to anther course if the student is also registered to that course. (FIX ME!)
  * Throws InvalidDBRequestException if the quiz is not registered in the course, error occured during deletion, or other exception occured.
  * @param quizName quiz name
  * @param courseID course id (course number + instructor name)
  * @throws InvalidDBRequestException
  */
  public void deleteQuiz(String quizName, String courseID) throws InvalidDBRequestException, FileFailureException
  {
    try
    {
      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      ResultSet rs;

      int count = 0;

      //check if quiz is used in the course
      rs = stmt.executeQuery("select test_name from courseTest where test_name = '"+quizName+"' and course_id = '"+courseID+"'");
      if (!rs.next())
          throw new InvalidDBRequestException("Quiz is not registered for the course");
      else
      {
        //remove quiz from course
        count = stmt.executeUpdate("delete from courseTest where course_id = '"+courseID+"' and test_name = '"+quizName+"'");
        if (count != 1)
          throw new InvalidDBRequestException("Error occured during deletion");
        else
        {
          //delete quiz visualization files
          rs = stmt.executeQuery(
            "select distinct unique_id, scores.user_login from scores, courseRoster "+
            "where courseRoster.user_login = scores.user_login "+
            "and course_id = '"+courseID+"' "+
            "and test_name = '"+quizName+"'");
          while(rs.next())
          {
            deleteVisualization(rs.getString(1), rs.getString(2), quizName);
            count = stmt.executeUpdate("delete from scores where unique_id = "+rs.getString(1).trim());
          }

          //rewrite the menu for the course
          rs = stmt.executeQuery(
            "select distinct menu_text, name, script_type, visual_type from test t, courseTest c "+
            "where t.name = c.test_name "+
            "and course_id = '"+courseID+"'");
          PrintWriter fileOStream = new PrintWriter(new FileOutputStream("./html_root/cat/"+courseID+".list"));
          while(rs.next())
          {
	      if (debug) System.out.println(rs.getString(1).trim()+"\n"+rs.getString(2).trim()+" "+rs.getString(3).trim()+" "+rs.getString(4).trim().toLowerCase()+"\n****\n");
	      fileOStream.print(rs.getString(1).trim()+"\n"+rs.getString(2).trim()+" "+rs.getString(3).trim()+" "+rs.getString(4).trim().toLowerCase()+"\n****\n");
          }
          fileOStream.flush();
	  fileOStream.close();
        }
      }

      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in addQuiz: "+e.getMessage());
      throw new InvalidDBRequestException("??? ");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Internal Server Error");
    }
    catch (Exception e)
    {
      System.err.println("Error in recreating menu for course: "+courseID);
      System.err.println(e.getMessage());
      throw new FileFailureException();
    }
  }
  
  /**
  * Deletes a course from the database.
  * Also deletes all the quiz visualization files from the students' folder who is registered in the course for all quizzes related to the course.
  * Caution: vizualisation file will be deleted eventhough it also relates to anther course if the student is also registered to that course. (FIX ME!)
  * Throws InvalidDBRequestException if the course is not in the database, error occured during deletion, or other exception occured.
  * @param courseNum course number
  * @param instructor instructor's user name who owns the course
  * @throws InvalidDBRequestException
  */
  public void deleteCourse(String courseNum, String instructor) throws InvalidDBRequestException
  {
    String courseId = new String(courseNum+instructor);

    try
    {
      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      ResultSet rs;

      int count = 0;

      //check if course in the database
      rs = stmt.executeQuery("select course_id from course where course_id = '"+courseId+"'");
      if (!rs.next())
        throw new InvalidDBRequestException("Course is not in the database");
      else
      {
        //delete course from course table
        count = stmt.executeUpdate("delete from course where course_id = '"+courseId+"'");
        if (count != 1)
          throw new InvalidDBRequestException("Something happen during deletion!");
        else
        {
          //delete quiz visualization files
          rs = stmt.executeQuery(
            "select distinct unique_id, s.user_login, s.test_name from scores s, courseRoster r, courseTest t "+
            "where s.test_name = t.test_name "+
            "and r.user_login = s.user_login "+
            "and r.course_id = t.course_id "+
            "and t.course_id = '"+courseId+"'");
          while (rs.next())
          {
            deleteVisualization(rs.getString(1).trim(), rs.getString(2).trim(), rs.getString(3).trim());
            count = stmt.executeUpdate("delete from scores where unique_id = "+rs.getString(1).trim());
          }
         
          //delete course from other tables
          count = stmt.executeUpdate("delete from courseRoster where course_id = '"+courseId+"'");
          count = stmt.executeUpdate("delete from courseTest where course_id = '"+courseId+"'");

          //delete menu file
          File menuFile = new File("./html_root/cat/"+courseId+".list");
          if (menuFile.exists())
            if (!menuFile.delete())
              System.err.println("Failed to delete the menu");
        }
      }

      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in addCourse: "+e.getMessage());
      throw new InvalidDBRequestException("??? ");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Internal Server Error");
    }
  }

  /**
  * Private method to delete a visualization script file of a quiz taken by a student.
  * @param uniqueId a unique number of that indicates the instance of the quiz taking in the database
  * @param studentName student's user name
  * @param testName quiz name
  */
  private void deleteVisualization (String uniqueId, String studentName, String testName)
  {
    try
    {
      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      
      //get quiz visulization type
      ResultSet rs = stmt.executeQuery("select visual_type from test where name = '"+testName+"'");
      //construct the quiz vizualisation file name
      String visFileName = new String("./StudentQuizzes/"+studentName+"/"+testName+uniqueId);
      if (rs.next())
        if (rs.getString(1).trim().equalsIgnoreCase("gaigs"))
          visFileName = visFileName+".sho";
         else 
           if (rs.getString(1).trim().equalsIgnoreCase("samba"))
             visFileName = visFileName+".sam";
           else 
             if (rs.getString(1).trim().equalsIgnoreCase("animal"))
               visFileName = visFileName+".ani";
             else
             {
               System.err.println("visualization type unknown for quiz: "+rs.getString(3)+"\n");
             }
      else
      {
        System.err.println("Visulization type not found for quiz: "+rs.getString(3)+"\n");
      }

      //delete the file
      File visFile = new File(visFileName);
      if (debug) System.out.println("deleting "+visFileName);
      if (visFile.exists())
         if (!visFile.delete())
           System.err.println("can't delete the visualization file");

      rs.close();
      stmt.close();
      db.close();
    }
    catch(Exception e)
    {
      System.err.println("Execption thrown from deleteVisualisation: "+e.getMessage());
    }
  }
 
  /**
  * Deletes students who are older than a certain number of years and not registered to any course.
  * @param year students older than year are candidates to be deleted
  * @throws InvalidDBRequestException
  */
  public void deleteOldStudent (int year) throws InvalidDBRequestException
  {
    try
    {
      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();

      //query all student who have been in the database longer than a number of years and not registered to any course
      ResultSet rs = stmt.executeQuery(
        "select login, count(course_id) "+
        "from student s left join courseRoster r on login = user_login "+
        "where date_entered < SUBDATE(now(), INTERVAL "+new String().valueOf(year).trim()+" YEAR) "+
        "group by login, date_entered");
      //delete them
      while(rs.next())
        if (rs.getInt(2) == 0)
          purgeStudent(rs.getString(1).trim());

      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in addCourse: "+e.getMessage());
      throw new InvalidDBRequestException("??? ");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Internal Server Error");
    }
  }

  /**
  * Delete a student from the database.
  * Also deletes the student's folders.
  * Throws InvalidDBRequestException if any error in database connection.
  * @param username student's user name
  * @throws InvalidDBRequestException
  */
  private void purgeStudent (String username) throws InvalidDBRequestException
  {
    try
    {
      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      int count;

      // delete from scores
      count = stmt.executeUpdate("delete from scores where user_login = '"+username+"'");
      
      // delete from student
      count = stmt.executeUpdate("delete from student where login = '"+username+"'");
      
      // delete student's folder
      File studentDir = new File("./StudentQuizzes/"+username);
      if (!(studentDir.delete()))
      {
        System.err.println ("Error in deleting folder for student: "+username);
      }

      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in addCourse: "+e.getMessage());
      throw new InvalidDBRequestException("??? ");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Internal Server Error");
    }
  }
  
  /**
  * Deletes an instructor from the database.
  * Deletes the instructor's courses by invoking the deleteCourse method.
  * Throws InvalidDBRequestException if instructor not in the database or other database connection problems.
  * @see deleteCourse
  * @param instructor instructor's user name
  * @throws InvalidDBRequestException
  */
  public void deleteInstructor (String instructor) throws InvalidDBRequestException
  {
    try
    {
      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      int count;
      
      //delete the instructor's courses
      ResultSet rs = stmt.executeQuery("select course_num from course where instructor = '"+instructor+"'");
      while (rs.next())
        deleteCourse(rs.getString(1).trim(), instructor);

      //delete the instructor's record
      count = stmt.executeUpdate("delete from instructor where login ='"+instructor+"'");

      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in addCourse: "+e.getMessage());
      throw new InvalidDBRequestException("??? ");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Internal Server Error");
    }
  }

  /**
  * Changes the password of a user.
  * Throws InvalidNameException if user's not registered in the database.
  * Throws InvalidPassException if user's password was invalid.
  * Throws InvalidDBRequestException if error occured during insertion or other database connection problems.
  * @param name user's user name
  * @param pass user's password
  * @param newPAss user's new password
  * @throws InvalidNameException, InvalidPassException, InvalidDBRequestException
  */
  public void changePassword(String name, String pass, String newPass) throws InvalidNameException, InvalidPassException, InvalidDBRequestException
  {
    try
    {
      Connection db;

      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      int count;
      Statement stmt = db.createStatement();
      ResultSet rs;
      rs = stmt.executeQuery("select password from student where login = '"+name+"'");

      // check if it's not a student in database
      if (!rs.next())
      {
        rs = stmt.executeQuery("select password from instructor where login = '"+name+"'");

        // check if it's an instructor in database
        if (!rs.next())
        {
          if (debug)
            System.out.println("User not found in the user table");
          throw new InvalidNameException("User not registered");
        }
        
	// check for password
        if (!rs.getString(1).equals(pass))
        {
          if (debug)
            System.out.println("Invalid password for user: "+name);
          throw new InvalidPassException("Invalid password for user: "+name);
        }

        // update the password
        count = stmt.executeUpdate("update instructor set password = '"+newPass+"' where login = '"+name+"' and password ='"+pass+"'");
      }
      else
      {
        // check for password
        if (!rs.getString(1).equals(pass))
        {
          if (debug)
            System.out.println("Invalid password for user: "+name);
          throw new InvalidPassException("Invalid password for user: "+name);
        }

        // update the password
        count = stmt.executeUpdate("update student set password = '"+newPass+"' where login = '"+name+"' and password ='"+pass+"'");
      }
      
      // check if successful
      if (count!=1)
      {
        System.err.println("Error in updating password");
        throw new InvalidDBRequestException("Error in updating password");
      }

      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)								// catch sql exception
    {
      System.err.println("Invalid SQL in ChangePassword: "+e.getMessage());
      throw new InvalidDBRequestException("Server request error: "+e.getMessage());
    }
    catch (ClassNotFoundException e)							// catch driver not found exception
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Server Error");
    }
  }

  /**
  * Gets a list of courses that belongs to an instructor.
  * Throws InvalidDBRequestException if any error occured to the database connection.
  * @param name the instructor's user name
  * @return a vector containing the list of courses
  * @throws InvalidDBRequestException
  */
  public Vector getCourseList(String name) throws InvalidDBRequestException
  {
    Vector courseList = new Vector();
    
    try
    {
      Connection db;

      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      ResultSet rs;
      
      //get the course list
      rs = stmt.executeQuery("select course_num, course_name from course where instructor = '"+name+"' order by course_num");
      while (rs.next())
        courseList.add(rs.getString(1)+" - "+rs.getString(2));        

      rs.close();
      stmt.close();
      db.close();
     }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in getCourseList: "+e.getMessage());
      throw new InvalidDBRequestException("???");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Server Error");
    }

    return courseList;
  }

  /**
  * Query a quiz list from the database.
  * If there is no student user name specified, the list will contain all quizzes that are used in the instructor's courses.
  * Otherwise, the list will contain all quizzes that the student has taken and from the instructor's courses which the student is registered.
  * Throws InvalidDBRequestException if any error occured to the database connection.
  * @param instructor instructor's user name
  * @param student student's user name. Can be empty to get a list of all quizzes in the instructor's courses.
  * @return a vector containing the list of quizzes
  * @throws InvalidDBRequestException
  */
  public Vector getQuizList (String instructor, String student) throws InvalidDBRequestException
  {
    Vector list = new Vector();

    try
    {
      Connection db;

      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      ResultSet rs;

      if (!student.equals(""))
      {
        //get the list that contains all quizzes that the student has taken and from the instructor's courses which the student is registered
        rs = stmt.executeQuery (
	  "select courseTest.test_name, scores.start_time from scores, courseTest, course "+ 
	  "where courseTest.test_name = scores.test_name "+
	  "and courseTest.course_id = course.course_id "+
          "and instructor = '"+instructor+"' and user_login = '"+student+"' "+
	  "order by scores.start_time");
        
	while(rs.next())
        {
          list.add(rs.getString(1)+" <"+rs.getString(2)+">");
        }
      }
      else
      {
        //get the list that contains all quizzes that are used in the instructor's courses
        rs = stmt.executeQuery(
	  "select test_name from courseTest, course "+
	  "where courseTest.course_id = course.course_id "+
	  "and instructor = '"+instructor+"' ");

        while(rs.next())
        {
          list.add(rs.getString(1));
        }
      }

      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in getQuizList: "+e.getMessage());
      throw new InvalidDBRequestException("???");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Internal Server Error");
    }

    return list;
  }

  /**
  * Query a student list from the database.
  * If quiz name is not specified, the list will contain all students who are registered in the instructor's courses.
  * Otherwise, it will contain all students who has taken the quiz and are registered to the instructor's course(s) that use the quiz
  * Throws InvalidDBRequestException if any error occured to the database connection.
  * @param name instructor's user name
  * @param quiz quiz name. Can be empty String to get the list of all students who are registered in the instructor's courses.
  * @return a vector that contains the list of students
  * @throws InvalidDBRequestException
  */
  public Vector getStudentList (String name, String quiz) throws InvalidDBRequestException
  {
    Vector list = new Vector();

    try
    {
      Connection db;

      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      ResultSet rs;

      if (!quiz.equals(""))
      {
        //get the list that contains all students who has taken the quiz and are registered to the instructor's course(s) that use the quiz
        rs = stmt.executeQuery(
	  "select login, last_name, first_name, scores.start_time from student, scores, courseRoster, course "+
	  "where student.login = scores.user_login "+
	  "and scores.user_login = courseRoster.user_login "+
	  "and courseRoster.course_id = course.course_id "+
	  "and scores.test_name = '"+quiz+"' "+
	  "and instructor = '"+name+"' "+
	  "order by last_name, first_name, login, scores.start_time");
      
        while(rs.next())
        {
          list.add(rs.getString(2)+", "+rs.getString(3)+" ("+rs.getString(1)+") <"+rs.getString(4)+">");
        }
      }
      else
      {
        //get the list that contains all students who are registered in the instructor's courses
        rs = stmt.executeQuery(
	  "select distinct login, last_name, first_name from student, courseRoster, course "+
	  "where student.login = courseRoster.user_login "+
	  "and courseRoster.course_id = course.course_id "+
	  "and instructor = '"+name+"' "+
	  "order by last_name, first_name, login");
      
        while(rs.next())
        {
          list.add(rs.getString(2)+", "+rs.getString(3)+" ("+rs.getString(1)+")");
        }
      }

      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in getStudentList: "+e.getMessage());
      throw new InvalidDBRequestException("???");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Internal Server Error");
    }

    return list;
  }

  /**
  * Obtains information on a quiz that a student has taken, 
  * which includes the student's answer to the quiz, a unique id of the instance, number of questions in the quiz, number of questions answered correctly, and vizualisation type of the quiz.
  * Throws InvalidDBRequestException if cannot find the record of the stuent taking the quiz, quiz is not registered in the database, or if any error occured to the database connection.
  * @param student student's user name
  * @param quiz quiz name
  * @param startTime the time the student started the quiz
  * @return a string tokenizer containing: answer,uniqueID, numQuestions, numCorrect, and  visualType. It uses "@" as the delimiter.
  * @throws InvalidDBRequestException
  */
  public StringTokenizer getAnswerAndVisualType (String student, String quiz, String startTime) throws InvalidDBRequestException
  {
    String answer;
    String uniqueID;
    String numQuestions;
    String numCorrect;
    String visualType;
    try
    {
      Connection db;

      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      ResultSet rs;

      //get student's answer, unique id, number of questions, and number of questions answered correctly
      rs = stmt.executeQuery(
        "select transcript, unique_id, num_questions, num_correct from scores "+
	"where test_name = '"+quiz+"' and user_login = '"+student+"' and start_time = '"+startTime+"'");
      if (!rs.next())
        throw new InvalidDBRequestException("Student has not taken the quiz");
      else
      {      
        answer = rs.getString(1).trim();
        uniqueID = rs.getString(2).trim();
	numQuestions = rs.getString(3).trim();
	numCorrect = rs.getString(4).trim();
      }

      //get quiz vizualisation type
      rs = stmt.executeQuery("select visual_type from test where name = '"+quiz+"' ");
      if (!rs.next())
        throw new InvalidDBRequestException("Quiz was not found! Can't retrieve visualization type.");
      else
      {
        visualType = rs.getString(1);
      }

      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in getAnswerAndVisualType: "+e.getMessage());
      throw new InvalidDBRequestException("???");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Internal Server Error");
    }
    return new StringTokenizer(answer+"@"+uniqueID+"@"+numQuestions+"@"+numCorrect+"@"+visualType,"@");
  }

  /**
  * Check the user's name and password and verify that the user is an instructor.
  * Throws InvalidDBRequestException if user is not an instructor, wrong password, or if any error occured to the database connection.
  * @param name user's user name
  * @param pass user's password
  * @throws InvalidDBRequestException
  */
  public void instructorLogin (String name, String pass) throws InvalidDBRequestException
  {
    try
    {
      Connection db;

      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      ResultSet rs;
      
      // check if instructor
      rs = stmt.executeQuery("select password from instructor where login = '"+name+"'");
      if (!rs.next())
      {
        if (debug)
	  System.out.println("User not found in the instructor table");
        throw new InvalidDBRequestException("Instructor not registered");
      }

      //check password
      if (!rs.getString(1).equals(pass))
      {
        if (debug)
          System.out.println("Invalid password for user: "+name);
        throw new InvalidDBRequestException("Invalid password for user: "+name);
      }

      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in instructor login: "+e.getMessage());
      throw new InvalidDBRequestException("???");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Server Error");
    }
  }

  /**
  * Query students grades for a course on a particular quiz
  * Throws InvalidDBRequestException if quiz is not used in the course, or if any error occured to the database connection.
  * @param courseID course unique id
  * @param quizname quiz unique name
  * @return a vector that contains the students information and the grades
  * @throws InvalidDBRequestException
  */
  public Vector getQuizGrades (String courseID, String quizName) throws InvalidDBRequestException
  {
    Vector grades = new Vector();

    try
    {
      Connection db;

      Class.forName(GaigsServer.DBDRIVER);
      db = DriverManager.getConnection(GaigsServer.DBURL,GaigsServer.DBLOGIN,GaigsServer.DBPASSWD);

      Statement stmt = db.createStatement();
      ResultSet rs;
      
      rs = stmt.executeQuery("select valid from courseTest where course_id = '"+courseID+"' and test_name = '"+quizName+"'");
      if (rs.next())
      {
        rs = stmt.executeQuery(
	  "select login, last_name, first_name, start_time, num_questions, num_correct from student, scores, courseRoster "+
	  "where student.login = scores.user_login "+
	  "and scores.user_login = courseRoster.user_login "+
	  "and scores.test_name = '"+quizName+"' "+
	  "and course_id = '"+courseID+"' "+
	  "order by last_name, first_name, login, scores.start_time");

        // use @ as delimiter since the date_time contains space
        while (rs.next())
          grades.add(rs.getString(1)+"@"+rs.getString(2)+"@"+rs.getString(3)+"@"+rs.getString(4)+"@"+rs.getString(5)+"@"+rs.getString(6));
      }
      else
        throw new InvalidDBRequestException("Quiz is not used for the course");

      rs.close();
      stmt.close();
      db.close();
    }
    catch (SQLException e)
    {
      System.err.println("Invalid SQL in instructor login: "+e.getMessage());
      throw new InvalidDBRequestException("???");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Driver Not Loaded");
      throw new InvalidDBRequestException("Server Error");
    } 
    return grades;
  }
}
