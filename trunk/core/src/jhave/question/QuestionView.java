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
 * QuestionPanel.java
 *
 * Created on August 8, 2004, 4:26 PM
 */

package jhave.question;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.text.html.HTMLEditorKit;

import jhave.core.JHAVETranslator;
import jhave.event.QuizEvent;
import jhave.event.QuizListener;
/**
 *
 * @author  Chris Gaffney
 */
public class QuestionView extends JPanel {

    /** Initial number of QuizListeners */
    private static final int INITIAL_QUIZ_LISTENERS = 2;

    /** */
    private Question question;
    /** */
    private QView view;
    /** */
    private JLabel responseLabel;
    /** */
    private AbstractButton checkButton;
    /** */
    private JEditorPane questionText = null;

    /** Array of QuizListeners */
    private static QuizListener[] quizListeners = new QuizListener[INITIAL_QUIZ_LISTENERS];
    /** Number of QuizListeners currently registered */
    private static int numQuizListeners = 0;
    /** If the student is taking a quiz. */
    private static boolean takingQuiz = false;
    /** If the user is in show answer mode */
    private static boolean showAnswers = false;
    /** */
    private static JDialog dialog = null;
    /** */
    private static Point dialogLastLocation = null;
    
    /** Creates a new instance of QuestionView */
    public QuestionView(Question question, boolean quizQuestion, boolean answerQuestion) {
        super(new BorderLayout());
        this.question = question;
	takingQuiz = quizQuestion;
	showAnswers = answerQuestion;
        
        switch(question.getQuestionType()) {
            case Question.TYPE_TRUE_FALSE:
                view = new TrueFalseView();
                break;
            case Question.TYPE_MULTIPLE_CHOICE:
                view = new MultipleChoiceView();
                break;
            case Question.TYPE_MULTIPLE_SELECTION:
                view = new MultipleSelectionView();
                break;
            case Question.TYPE_FILL_IN_THE_BLANK:
                view = new FillInTheBlankView();
                break;
        }
        
	if (takingQuiz)
//    checkButton = new JButton("Submit Answer");
    checkButton = JHAVETranslator.getGUIBuilder().generateJButton("submitAnswer");
	else 
//    checkButton = new JButton("Check Answer");
    checkButton = JHAVETranslator.getGUIBuilder().generateJButton("checkAnswer");
        checkButton.addActionListener(new CheckListener());
        responseLabel = new JLabel("");
        responseLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel northPanel = new JPanel(new GridLayout(1, 1));
        northPanel.add(getQuestionPane());
        questionText.setText(question.getQuestion());
        questionText.setBorder(
            JHAVETranslator.getGUIBuilder().generateTitledBorder("questionBorder"));
//        questionText.setBorder(BorderFactory.createTitledBorder("Question"));
        
        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.add(responseLabel);
        southPanel.add(checkButton);
        
        add(BorderLayout.NORTH, northPanel);
        add(BorderLayout.CENTER, view);
        add(BorderLayout.SOUTH, southPanel);
    }
    
    /**
     * Set the entered answer to the question and check if it is correct,
     * notifying the user of this.
     */
    protected void checkAnswer() {
        if (takingQuiz && checkButton.getText().startsWith("Close")) { // then they've seen the right answer, so let's get out of here
	    dialog.dispose();
	    return;
        }

        view.setAnswer();
        
        if (takingQuiz) {
	    //	    dialog.dispose();
	    checkButton.setText("Close this dialog to continue quiz");
	    checkButton.setMnemonic( (int) 'X');
	    // checkButton.setEnabled(false);
        }
	//	else if (showAnswers) {
	if (showAnswers) {
        	if (question.isCorrect()) {
//      		responseLabel.setText("Correct");
        		responseLabel.setText(
        				JHAVETranslator.translateMessage("correctAnswer"));
        		responseLabel.setForeground(Color.green.darker().darker());
        	} else {
        		Object answerObject = question.getCorrectAnswer();
        		String answerClass = answerObject.getClass().getSimpleName();
        		if (answerClass.equals("HashSet")) {
        			HashSet hash = (HashSet)answerObject;
        			Iterator answers = hash.iterator();
        			String correctAnswer = (String)answers.next();
        			responseLabel.setForeground(Color.red);
        			responseLabel.setText(
        					JHAVETranslator.translateMessage("wrongHashSet",
        							correctAnswer));
//        			responseLabel.setText("Wrong, the correct answer is "+correctAnswer);
        		} else if (answerClass.equals("Integer")) {
        			Integer integer = (Integer)answerObject;
//        			int answer = integer.intValue();
        			responseLabel.setForeground(Color.red);
        			responseLabel.setText(
        					JHAVETranslator.translateMessage("wrongInteger",
        							integer));
//        			responseLabel.setText("Wrong, the correct answer is option "+answer);
        		} else if (answerClass.equals("Boolean")) {
        			Boolean bool = (Boolean)answerObject;
//        			boolean answer = bool.booleanValue();
        			responseLabel.setForeground(Color.red);
        			responseLabel.setText(
        					JHAVETranslator.translateMessage("wrongBoolean",
        							bool));
//        			responseLabel.setText("Wrong, the correct answer is "+answer);
        		} else if (answerClass.equals("LinkedList")) {
        			LinkedList linklist = (LinkedList)answerObject;
        			StringBuffer answers = new StringBuffer(256);
        			boolean moreThanOne = (linklist.size() != 1); 
        			for (int i = 0; i < linklist.size(); i++) {
        				answers.append(linklist.get(i));
        				if (moreThanOne && i+1 < linklist.size()) 
        					answers.append(", ");
        				else if (i+1 < linklist.size()) 
        					answers.append(" ");
        			}
        			responseLabel.setForeground(Color.red);
        			responseLabel.setText(
        					JHAVETranslator.translateMessage("wrongLinkedList",
        							answers.toString()));
//        			responseLabel.setText("Wrong, the correct answers are options "+answers);
        		}
        	}
        } else {
        	if (question.isCorrect()) {
        		responseLabel.setForeground(Color.green.darker().darker());
        		responseLabel.setText(
        				JHAVETranslator.translateMessage("correctAnswer"));
//        		responseLabel.setText("Correct");
        	} else {
        		responseLabel.setForeground(Color.red);
        		responseLabel.setText(
        				JHAVETranslator.translateMessage("wrongAnswer"));
//        		responseLabel.setText("Wrong, try again");
        	}
        }
    }
    
    /**
     * Builds a JEditorPane with standard settings that should be used in all child classes
     * to display the question to the user. The purpose of this is for consistency. The JEditorPane
     * will resemble JLabels and their functionality to be centered. The JEditorPane uses the HTML
     * EditorKit and has a custom setText method that breaks a question down so questions will not
     * span the entire length of the screen, the effect is a multi line question that is shorter
     * width wise then normal.
     * @return JEditorPane a JEditorPane with standard settings used to correctly display a long question.
     */
    protected final JEditorPane getQuestionPane() {
        if(questionText == null) {
            JEditorPane returnedPane = new JEditorPane() {
                public void setText(String text) {
                    int splitPoint = 35;
                    StringBuffer parsedText = new StringBuffer(100);
                    parsedText.append("<center>");
                    
                    if((text.length() / splitPoint) > 0) {
                        int position = 0;
                        while(position < text.length()) {
                            int newPosition = text.indexOf(" ", position + splitPoint);
                            if(newPosition > -1) {
                                parsedText.append(text.substring(position, newPosition));
                                parsedText.append("<br>");
                                position = newPosition;
                            } else {
                                parsedText.append(text.substring(position));
                                break;
                            }
                        }
                    } else {
                        parsedText.append(text);
                    }
                    parsedText.append("</center>");
                    super.setText(parsedText.toString());
                }
            };
            
            returnedPane.setEditorKit(new HTMLEditorKit());
            returnedPane.setEditable(false);
            returnedPane.setBackground(new JPanel().getBackground());
            
            questionText = returnedPane;
        }
        
        return questionText;
    }
    
    /**
     *
     */
    public static void showQuestionDialog(Question question, boolean quizQuestion, boolean answerQuestion) {
        if(dialog != null) {
            dialog.dispose();
        }
        
        dialog = new JDialog() {
	    public void dispose() {
		fireQuizEvent(dialog, false);
                dialogLastLocation = getLocation();
                super.dispose();
            }
        };
        dialog.setContentPane(new QuestionView(question, quizQuestion, answerQuestion));
        dialog.pack();
        
        if(dialog.getWidth() < 300) {
            dialog.setSize(300, dialog.getHeight());
        }
	dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	//	if(takingQuiz) setCloseable(false);
        
        if(dialogLastLocation == null) {
            dialog.setLocationRelativeTo(null);
        } else {
            dialog.setLocation(dialogLastLocation);
        }
        dialog.setVisible(true);
    }
    
    /**
     *
     */
    public void addCheckListener(ActionListener listener) {
        checkButton.addActionListener(listener);
    }
    
    /**
     *
     */
    public void removeCheckListener(ActionListener listener) {
        checkButton.removeActionListener(listener);
    }

    /**
     * Register a QuizListener
     * @param listener the QuizListener to register
     */
    public static void addQuizListener(QuizListener listener) {
	
	if(quizListeners.length == numQuizListeners) {
	    QuizListener[] temparray = new QuizListener[quizListeners.length * 2];
	    for(int i = 0; i < quizListeners.length; i++) {
		temparray[i] = quizListeners[i];
	    }
	    quizListeners = temparray;
	}
	quizListeners[numQuizListeners] = listener;

	numQuizListeners++;
    }

    /**
     * Unregister a QuizListener
     * @param listener the QuizListener to unregister
     */
    public static void removeQuizListener(QuizListener listener) {
	
	for(int i = 0; i < quizListeners.length; i++) {
	    
	    if(quizListeners[i].equals(listener)) {
		
		quizListeners[i] = null;
		for(int j = i + 1; j < quizListeners.length; j++) {
		    
		    quizListeners[j - 1] = quizListeners[j];
		}
		numQuizListeners--;
	    }
	}
    }
    
    /**
     * Fire a QuizEvent to all registered listeners.
     * @param o the object associated with the event
     * @param dialogShown if the question dialog is shown on this event
     */
    private static void fireQuizEvent(Object o, boolean dialogShown) {
	
	QuizEvent e = new QuizEvent(o, dialogShown);
	fireQuizEvent(e);
    }
	    
    /**
     * Fire a QuizEvent to all registered listeners.
     * @param e the QuizEvent to fire.
     */
    private static void fireQuizEvent(QuizEvent e) {
	
	for(int i = 0; i < numQuizListeners; i++) {
	    
	    quizListeners[i].handleQuizEvent(e);
	}
    }

    
    private abstract class QView extends JPanel {
        public abstract void setAnswer();
    }
    
    private class TrueFalseView extends QView {
      private JToggleButton trueRadio;
      private JToggleButton falseRadio;
//        private JRadioButton trueRadio;
//        private JRadioButton falseRadio;
        private ButtonGroup radioGroup;
        
        public TrueFalseView() {
          trueRadio = JHAVETranslator.getGUIBuilder().generateJToggleButton(
              "trueRadio", null, null, true);
          falseRadio = JHAVETranslator.getGUIBuilder().generateJToggleButton(
              "falseRadio", null, null, true);
//            trueRadio = new JRadioButton("True");
//            falseRadio = new JRadioButton("False");
            
//            trueRadio.setToolTipText("Select an answer");
//            falseRadio.setToolTipText("Select an answer");
            
            radioGroup = new ButtonGroup();
            radioGroup.add(trueRadio);
            radioGroup.add(falseRadio);
            
            trueRadio.setSelected(true);
            
            setLayout(new GridLayout(2, 1));
            add(trueRadio);
            add(falseRadio);
        }
        
        public void setAnswer() {
            question.setAnswer(Boolean.toString(trueRadio.isSelected()));
        }
    }
    private class MultipleChoiceView extends QView {
        /** */
        private ButtonGroup radioGroup;
        public MultipleChoiceView() {
            String[] answers = question.getPossibleAnswers();
            
            if(answers.length > 8) {
                setLayout(new GridLayout(8, (int)Math.ceil(answers.length / 8d)));
            } else {
                setLayout(new GridLayout(answers.length, 1));
            }
            
//            String tooltip = "Select one of the possible answers";
            String tooltip = JHAVETranslator.translateMessage("selectAnswer");
            radioGroup = new ButtonGroup();
            for(int i = 0; i < answers.length; i++) {
                JRadioButton radio = new JRadioButton(answers[i]);
                radio.setActionCommand(Integer.toString(i + 1));
                radio.setToolTipText(tooltip);
                radioGroup.add(radio);
                add(radio);
            }
        }
        
        public void setAnswer() {
            if(radioGroup.getSelection() == null) {
                return;
            }
            question.setAnswer(radioGroup.getSelection().getActionCommand());
        }
    }
    private class MultipleSelectionView extends QView {
        private java.util.List checkboxes = new LinkedList();
        public MultipleSelectionView() {
            String[] answers = question.getPossibleAnswers();
            
            if(answers.length > 8) {
                setLayout(new GridLayout(8, (int)Math.ceil(answers.length / 8d)));
            } else {
                setLayout(new GridLayout(answers.length, 1));
            }
            
//            String tooltip = "Select one or more of the given answers";
            String tooltip = JHAVETranslator.translateMessage("select1+Answer");
            for(int i = 0; i < answers.length; i++) {
                JCheckBox check = new JCheckBox(answers[i]);
                check.setActionCommand(Integer.toString(i));
                check.setToolTipText(tooltip);
                checkboxes.add(check);
                add(check);
            }
        }
        public void setAnswer() {
            StringBuffer buff = new StringBuffer();
            
            Iterator itr = checkboxes.iterator();
            for(int i = 0; itr.hasNext(); i++) {
                if(((JCheckBox)itr.next()).isSelected()) {
                    buff.append(Integer.toString(i + 1));
                    buff.append(' ');
                }
            }
            
            question.setAnswer(buff.toString());
        }
    }
    private class FillInTheBlankView extends QView {
        private JTextField answerField;
        public FillInTheBlankView() {
          answerField = JHAVETranslator.getGUIBuilder().generateJTextField(
              "fibField", null, 30, "");
//            answerField = new JTextField(30);
            answerField.addKeyListener(new FIBKeyListener());
//            answerField.setToolTipText("Enter your answer");
            
            add(answerField);
        }
        public void setAnswer() {
            question.setAnswer(answerField.getText());
        }
        
        private class FIBKeyListener extends KeyAdapter {
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkAnswer();
                }
            }
        }
    }
    private class CheckListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            checkAnswer();
        }
    }
}
