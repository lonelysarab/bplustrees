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

import jhave.core.TransactionCodes;
import javax.mail.*;
import javax.mail.internet.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.input.JDOMParseException;
import javax.xml.xpath.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** Sends an email with the results of the quiz to the student upon
 * completion
 */
public class QuizEmailProcessor extends QuizResultProcessor implements TransactionCodes
{
	private String dbLogin;
	private String dataSource;
	private String visualExt;
	private int uid;
	private String xmlString;
	private String givenInput;

	/** Constructor
	 *
	 * @param clientConn The associated ClientConnection (should just be "this"
	 * @param quizID name of the quiz taken
	 * @param studentID login of the student
	 * @param numQuestions number of questions in the quiz
	 * @param numCorrect number of question answered correctly
	 * @param endTime the time the quiz was finished
	 * @param DBlogin login name for the database
	 * @param dataSource location of the database
	 * @param visualExt private variable in ClientConnection
	 * @param uid private variable in ClientConnection
	 * @param xmlString private variable in ClientConnection
	 * @param givenInput private variable in ClientConnection
	 */
	public QuizEmailProcessor(ClientConnection clientConn,String quizID,String studentID,String numQuestions,
			String numCorrect,String endTime,String dbLogin,String dataSource,String visualExt,
			int uid,String xmlString,String givenInput)
	{
		super(clientConn,quizID,studentID,numQuestions,numCorrect,endTime);

		this.dbLogin = dbLogin;
		this.dataSource = dataSource;
		this.visualExt = visualExt;
		this.uid = uid;
		this.xmlString = xmlString;
		this.givenInput = givenInput;
	}

	/** Sends the results email using the parameters given
	 */
	public void processResults() throws Exception
	{
		String human_friendly_algo_name = new String("");
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			//String dataSource = "//localhost/Jhave";
			String dbURL = "jdbc:mysql:" + dataSource;
			Connection conn = DriverManager.getConnection(dbURL,dbLogin,System.getProperty("jhave.server.dbpassword"));

			Statement stmt1 = conn.createStatement();

			ResultSet algoInfo = stmt1.executeQuery("SELECT algorithm FROM quiz WHERE quiz_name = '" + quizID + "'");

			algoInfo.next();
			human_friendly_algo_name = algoInfo.getString("algorithm");
			stmt1.close();
			conn.close();
		}    
		catch(SQLException e)
		{
			clientConn.logger.info(e.toString());
			clientConn.logger.info("Sending " + FS_UNSUCCESSFUL_QUIZ_LOGIN_NO_STUDENT_ID + " to client");
			clientConn.sendtoClient(""+FS_UNSUCCESSFUL_QUIZ_LOGIN_NO_STUDENT_ID+" invalid user ID");
		}
		catch(Exception e)
		{
			clientConn.logger.info(e.toString());
		}
		// ******** End of logic to get human-friendly algoname ************

		String algoString = null;
		boolean dynamic_script_at_uid = true;
		try {
			File algoFile = new File(System.getProperty("jhave.server.userdir")+"html_root/uid/"+uid+visualExt);
			byte[] b = new byte[(int)algoFile.length()];
			FileInputStream fis = new FileInputStream(algoFile);
			fis.read(b);
			algoString = new String(b);

			StringBuffer sb1 = new StringBuffer(algoString);

			int temp1 = sb1.indexOf("\\");
			while( temp1 != -1 )
			{
				sb1.replace(temp1,temp1+1,"\\\\");
				temp1 = sb1.indexOf("\\",temp1+2);
			}

			algoString = new String(sb1);

			StringBuffer sb = new StringBuffer(algoString);

			int temp = sb.indexOf("'");
			while( temp != -1 )
			{
				sb.replace(temp,temp+1,"\\'");
				temp = sb.indexOf("'",temp+2);
			}

			algoString = new String(sb);
		} catch (java.io.FileNotFoundException e)  {
			dynamic_script_at_uid = false;
		}

		String questionAsked = "";
		String possibleAnswers = "";
		String correctAnswer = "";
		String givenAnswer = "";
		String questionType = "";
		StringBuffer emailText = new StringBuffer("");

		emailText.append("Quiz taken: " +
				(human_friendly_algo_name.equals("") ? quizID : human_friendly_algo_name) +
								"\n\n");

		if( !givenInput.equals("null") && !givenInput.equals("*"))
		{
			emailText.append("User Input Provided: "+givenInput+"\n\n");
		}

		emailText.append("Number of " + (visualExt.equals(".matrix") ? "steps: " : "questions: ") + numQuestions+"\n\n");
		emailText.append("Number correct: "+numCorrect+"\n\n");

		boolean isXML;
		if(dynamic_script_at_uid && algoString.charAt(0) == '<') isXML = true;
		else isXML = false;

		//   Here we build the information on questions to put
		//   in the email.  Right now we only parse the script for the questions in the case of a GAIGS XML script.  
		//   If it isn't an XML GAIGS script,
		//   we skip the grief of trying to get the text of
		//   the questions.   This is a long if-else -- sorry!-:)

		if(isXML && visualExt.equals(".sho"))
		{
			StringTokenizer st1 = new StringTokenizer(algoString,"\n");
			String justQuestions = "";

			while(st1.hasMoreTokens())
			{
				String tempStr1 = st1.nextToken();
				if(tempStr1.equals("<questions>"))
				{
					justQuestions = justQuestions+tempStr1+"\n";
					while(st1.hasMoreTokens())
					{
						tempStr1 = st1.nextToken();
						if(!tempStr1.equals("</show>")) justQuestions = justQuestions+tempStr1+"\n";
					}
				}
			}

			SAXBuilder builder1 = new SAXBuilder();
			org.jdom.Document doc1 = builder1.build(new StringReader(justQuestions));
			Iterator jqit = doc1.getDescendants();
			SAXBuilder builder2 = new SAXBuilder();
			org.jdom.Document doc2 = builder2.build(new StringReader(xmlString));
			Iterator xsit = doc2.getDescendants();

			Element questionsElement = (Element)jqit.next();
			xsit.next();
			Element quizResultsElement = (Element)xsit.next();
			List questionsChildren = questionsElement.getChildren();
			List quizResultsChildren = quizResultsElement.getChildren();
			Iterator questionsIterator = questionsChildren.iterator();
			Iterator quizResultsIterator = quizResultsChildren.iterator();

			int numQuestionsInt = Integer.parseInt(numQuestions);
			ArrayList questionsAsked = new ArrayList();
			for(int count = 1; count <= quizResultsChildren.size(); count++)
			{
				XPathFactory factory = XPathFactory.newInstance();
				XPath xp = factory.newXPath();
				InputSource input = new InputSource(new StringReader(xmlString));
				String questionNum = xp.evaluate("/quizResults/question["+count+"]/@questionNumber",input);
				questionsAsked.add(questionNum);
			}

			for(int q = 0; q < numQuestionsInt; q++)
			{
				XPathFactory factory = XPathFactory.newInstance();
				XPath xp = factory.newXPath();
				InputSource input = new InputSource(new StringReader(justQuestions));
				String questionNum = xp.evaluate("/questions/question["+(q+1)+"]/@id",input);
				//if(!questionsAsked.contains(questionNum)) continue;
				Element currentQuestions = (Element)questionsIterator.next();
				Element currentQuizResults = (Element)quizResultsIterator.next();
				List currentQuestionsList = currentQuestions.getChildren();
				List currentQuizResultsList = currentQuizResults.getChildren();
				Iterator currentQuestionsIterator = currentQuestionsList.iterator();
				Iterator currentQuizResultsIterator = currentQuizResultsList.iterator();  
				List currentQuizResultsAttList = currentQuizResults.getAttributes();
				Iterator quizResultsAttIterator = currentQuizResultsAttList.iterator();

				while(quizResultsAttIterator.hasNext())
				{
					Attribute tempQuizResultAtt = (Attribute)quizResultsAttIterator.next();
					if(tempQuizResultAtt.getName().equals("questionType")) questionType = tempQuizResultAtt.getValue();
				}

				while(currentQuestionsIterator.hasNext())
				{
					Element tempQuestionEle = (Element)currentQuestionsIterator.next();
					if(tempQuestionEle.getName().equals("question_text")) questionAsked = tempQuestionEle.getValue();
					else if(tempQuestionEle.getName().equals("answer_option"))
					{
						List tempQuestionAttList = tempQuestionEle.getAttributes();
						Iterator tempQuestionAttIterator = tempQuestionAttList.iterator();
						while(tempQuestionAttIterator.hasNext())
						{
							Attribute tempQuestionAtt = (Attribute)tempQuestionAttIterator.next();
							if(tempQuestionAtt.getName().equals("is_correct"))
							{
								if(tempQuestionAtt.getValue().equals("yes")) correctAnswer = correctAnswer+tempQuestionEle.getValue()+" ";
								possibleAnswers = possibleAnswers+tempQuestionEle.getValue()+"\n";
							}
						}
					}
				}

				while(currentQuizResultsIterator.hasNext())
				{
					Element tempQuizResultsEle = (Element)currentQuizResultsIterator.next();
					if(tempQuizResultsEle.getName().equals("givenAnswer")) givenAnswer = tempQuizResultsEle.getValue();
				}

				if(questionType.equals("TF")) possibleAnswers = "True or False";

				emailText.append("Question "+(q+1)+"\n\n");
				emailText.append("You were asked: "+questionAsked+"\n");
				emailText.append("Possible answer(s): "+possibleAnswers+"\n");
				emailText.append("You answered: "+givenAnswer+"\n");
				emailText.append("Correct answer: "+correctAnswer+"\n\n");

				questionType = "";
				givenAnswer = "";
				questionAsked = "";
				possibleAnswers = "";
				correctAnswer = "";
			}	
		} // end isXml and gaigs
		else if (!visualExt.equals(".matrix"))  // Visual simulation of algorithm does not generate real questions
		{
			SAXBuilder builder2 = new SAXBuilder();
			org.jdom.Document doc2 = builder2.build(new StringReader(xmlString));
			Iterator xsit = doc2.getDescendants();

			xsit.next();
			Element quizResultsElement = (Element)xsit.next();
			List quizResultsChildren = quizResultsElement.getChildren();
			Iterator quizResultsIterator = quizResultsChildren.iterator();

			int numQuestionsInt = Integer.parseInt(numQuestions);
			ArrayList questionsAsked = new ArrayList();
			for(int count = 1; count <= quizResultsChildren.size(); count++)
			{
				XPathFactory factory = XPathFactory.newInstance();
				XPath xp = factory.newXPath();
				InputSource input = new InputSource(new StringReader(xmlString));
				String questionNum = xp.evaluate("/quizResults/question["+count+"]/@questionNumber",input);
				questionsAsked.add(questionNum);
			}

			for(int q = 0; q < numQuestionsInt; q++)
			{
				Element currentQuizResults = (Element)quizResultsIterator.next();
				List currentQuizResultsList = currentQuizResults.getChildren();
				Iterator currentQuizResultsIterator = currentQuizResultsList.iterator();  
				List currentQuizResultsAttList = currentQuizResults.getAttributes();
				Iterator quizResultsAttIterator = currentQuizResultsAttList.iterator();

				while(quizResultsAttIterator.hasNext())
				{
					Attribute tempQuizResultAtt = (Attribute)quizResultsAttIterator.next();
					if(tempQuizResultAtt.getName().equals("questionType")) questionType = tempQuizResultAtt.getValue();
				}

				while(currentQuizResultsIterator.hasNext())
				{
					Element tempQuizResultsEle = (Element)currentQuizResultsIterator.next();
					if(tempQuizResultsEle.getName().equals("givenAnswer")) givenAnswer = tempQuizResultsEle.getValue();
					if(tempQuizResultsEle.getName().equals("correctAnswer")) correctAnswer = tempQuizResultsEle.getValue();
				}

				emailText.append("Question "+(q+1)+"\n\n");
				emailText.append("You answered: "+givenAnswer+"\n");
				emailText.append("Correct answer: "+correctAnswer+"\n\n");

				questionType = "";
				givenAnswer = "";
				questionAsked = "";
				possibleAnswers = "";
				correctAnswer = "";
			}	

		}

		// End of long if to construct info on quiz questions
		// to put in email 

		// Next we tell the user how to view the sho file
		// attached to their message.  There's a legacy issue
		// here since, at jhave.org, the code root is not at
		// the same level as html_root, but rather one level
		// above it.  Hence the special CODEROOT fix below.
		// In the future, all installations should put the
		// code directory as a sibling of html_root.
		String CODEROOT = (clientConn.WEBROOT.contains("jhave.org") ? "http://jhave.org/" : clientConn.WEBROOT);
		if (dynamic_script_at_uid) {
			emailText.append("\n\nTo view this visualization again, first download the attached script to \nyour local file system.   Then launch JHAVE using the web link below.\nHowever, instead of clicking 'connect' at that point, use the \n'Load Script' option from JHAVE's File Menu to view the downloaded script.\n\n");
			emailText.append(CODEROOT+"code/general_start.php?webroot="+clientConn.WEBROOT+"html_root/doc/"+quizID);
		}

		// this part sends the email
		try
		{
			Properties props = new Properties();
			props.setProperty("mail.transport.protocol","smtp");
			props.setProperty("mail.host",System.getProperty("jhave.server.smtpserver"));
			props.setProperty("mail.user", System.getProperty("jhave.server.emaillogin"));
			props.setProperty("mail.password",System.getProperty("jhave.server.emailpassword"));
			props.setProperty("mail.smtp.starttls.enable","true");
			props.setProperty("mail.smtps.auth", "true");
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.smtp.port","465");
			props.setProperty("mail.smtp.socketFactory.port","465");
			props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.smtp.socketFactory.fallback", "false");

			Session mailSession = Session.getDefaultInstance(props,null);
			Transport transport = mailSession.getTransport("smtp");

			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject("Jhave Quiz Results");

			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setContent(emailText.toString(),"text/plain");
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(textPart);

			if (dynamic_script_at_uid) {
				MimeBodyPart attachFilePart = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(new File(System.getProperty("jhave.server.userdir")+"html_root/uid/"+uid+visualExt));
				attachFilePart.setDataHandler(new DataHandler(fds));
				//				attachFilePart.setFileName("visualization.sho");
				attachFilePart.setFileName("visualization" + visualExt);
				mp.addBodyPart(attachFilePart);
			}

			message.setContent(mp);
			message.addRecipient(Message.RecipientType.TO,new InternetAddress(studentID));

			transport.connect(System.getProperty("jhave.server.smtpserver"), System.getProperty("jhave.server.emaillogin"), System.getProperty("jhave.server.emailpassword"));
			//			transport.connect("smtp.gmail.com", "jhavedotorg", System.getProperty("jhave.server.emailpassword"));
			message.saveChanges();
			transport.sendMessage(message,message.getAllRecipients());
			transport.close();
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}

		clientConn.logger.info("email sent");
	}
}