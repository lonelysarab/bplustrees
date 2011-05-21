#ifndef _QUESTION_H
#define _QUESTION_H

//  class for adding questions to gaigs or samba script files


#include <fstream>
#include <string>
using namespace std;
    
class question{
 public:
  question(ofstream & o, string i) : my_out(o), id(i) {
  }
  question(const question & q) : my_out(q.my_out) {
    id = q.id;
  }
  void setQuestionText(string questText){
    questionText = questText;
  }
  virtual void writeQuestionInfo(){}
  virtual void insertQuestion(){}
  virtual void addChoice(string choice){}
  virtual void setAnswer(string choice){}
  virtual void setAnswer(bool answer){}
  virtual void setAnswer(int answer){}

 protected:
  string id;
  string questionText;
  ofstream& my_out;              
};

#endif    /* _QUESTION_H not defined */
/*
***java code***
public abstract class question{
protected String id;
protected PrintWriter out;
protected String questionText;
protected boolean doneWithScript;
    
public question(){}
public void setQuestionText(String questionText){
this.questionText = questionText.trim();
}
public abstract void endOfScriptWriteQuestionInfo();
public abstract void insertQuestion();
}*/
