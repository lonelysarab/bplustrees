#include "question.h"
#include <iostream>
#include <string>
#include <fstream>
using namespace std;

#ifndef _MCQUESTION_H
#define _MCQUESTION_H

class mcQuestion : public question {
 public:
  mcQuestion(const mcQuestion & q) : question(q) {
    numAnswers = q.numAnswers;
    correctChoice = q.correctChoice;
    correctChoiceStr = q.correctChoiceStr;
    for(int x = 0; x < numAnswers && x < 50; x++)
      choices[x] = q.choices[x];
  }
  mcQuestion(ofstream & o, string i) : question(o, i) {
    numAnswers = 0;
    correctChoice = 0;
    correctChoiceStr = "\0";
  }
  virtual void writeQuestionInfo(){
    my_out << "MCQUESTION " << id <<endl;
    my_out << questionText << endl;
    my_out << "ENDTEXT" << endl;
    for(int x = 0; x < numAnswers; x++){
      my_out << choices[x] <<endl;
      my_out << "ENDCHOICE" <<endl;
    }
    my_out << "ANSWER" << endl;
    my_out << correctChoice <<endl;
    my_out << "ENDANSWER" << endl;
  }
  virtual void insertQuestion(){
    my_out << "MCQUESTION " << id << endl;
  }
  virtual void addChoice(string choice){
    if(numAnswers < 50){
      if((correctChoiceStr != "\0")&&(correctChoiceStr == choice))
	correctChoice = numAnswers + 1;
      choices[numAnswers] = choice;
      numAnswers++;
    }
  }
  virtual void setAnswer(int choice){
    cout << "Setting answer " << choice << endl;
    correctChoice = choice;
  }
  virtual void setAnswer(string choice){
    for(int x = 0; x < numAnswers; x++){
      if(choices[x] == choice){
	correctChoice = x+1;
	return;
      }
    }
    correctChoiceStr = choice;
  }
 private:       
  string choices[50];
  int numAnswers;
  string correctChoiceStr;
  int correctChoice;
};

#endif

/*
***java code***
public class mcQuestion extends question{
private Vector choices;
private int correctChoice;
private String correctChoiceStr;
    
public mcQuestion(PrintWriter out, String id){
this.id = id.trim();
this.out = out;
choices = new Vector();
correctChoice = 0;
correctChoiceStr = null;
}
public void insertQuestion(){
try{
out.println("MCQUESTION "+id);
}catch(Exception e){
System.out.println(e.toString() + " thrown from insertQuestion of mcQuestion: "+id);
}
}
public void addChoice(String choice){
if((correctChoiceStr != null)&&(correctChoiceStr.equals(choice.trim())))
correctChoice = choices.size() + 1;
choices.addElement(choice.trim());
}
public void setAnswer(int choice){
correctChoice = choice;
}
public void setAnswer(String choice){
for(int x = 0; x < choices.size(); x++){
if(((String)choices.elementAt(x)).equals(choice.trim())){
correctChoice = x+1;
return;
}
}
correctChoiceStr = choice;
}
        
public void writeQuestionInfo(){
try{
out.println("MCQUESITON "+id);
out.println(questionText);
out.println("ENDTEXT");
for(int x = 0; x < choices.size(); x++){
out.println((String)choices.elementAt(x));
out.println("ENDCHOICE");
}
out.println("ANSWER");
out.println(correctChoice);
out.println("ENDANSWER");
}catch(Exception e){
System.out.println(e.toString()+" thrown from writeQuestionInfo() of tfQuestion: "+id);
e.printStackTrace();
}
}    
}*/
