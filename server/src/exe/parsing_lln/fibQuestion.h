#include "question.h"
#include <string>
#include <fstream>
using namespace std;

#ifndef _FIBQUESTION_H
#define _FIBQUESTION_H

class fibQuestion : public question {
	public:
		fibQuestion(ofstream & o, string i) : question(o, i) {
			numAnswers = 0;
		}
		fibQuestion(const fibQuestion & q) : question(q) {
			numAnswers = q.numAnswers;
			for(int x = 0; x < numAnswers && x < 50; x++)
				answers[x] = q.answers[x];
		}
		virtual void writeQuestionInfo(){
			my_out << "FIBQUESTION " << id <<endl;
			my_out << questionText << endl;
			my_out << "ENDTEXT" << endl;
			my_out << "ANSWER" << endl;
			for(int x = 0; x < numAnswers; x++)
				my_out << answers[x] <<endl;
			my_out << "ENDANSWER" << endl;
		}
		virtual void insertQuestion(){
			my_out << "FIBQUESTION " << id << endl;
		}
		virtual void setAnswer(string answer){
			if(numAnswers < 50){
				answers[numAnswers] = answer;
				numAnswers++;
			}
		}
	private:       
		string answers[50];
		int numAnswers;
};

#endif

/*
***java code***
public class fibQuestion extends question{
    private Vector correctAnswers;
    
    public fibQuestion(PrintWriter out, String id){
        this.id = id.trim();
        this.out = out;
        correctAnswers = new Vector();
    }
    public void insertQuestion(){
        try{
            out.println("FIBQUESTION "+id);
        }catch(Exception e){
            System.out.println(e.toString() + " thrown from insertQuestion of fibQuestion: "+id);
        }
    }
    public void setAnswer(String answer){
        correctAnswers.addElement(answer.trim());
    }
    public void writeQuestionInfo(){
        try{
            out.println("FIBQUESTION "+id);
            out.println(questionText);
            out.println("ENDTEXT");
            out.println("ANSWER");
            for(int x = 0; x < correctAnswers.size(); x++)
                out.println((String)correctAnswers.elementAt(x));
            out.println("ENDANSWER");
        }catch(Exception e){
            System.out.println(e.toString()+" thrown from writeQuestionInfo() of tfQuestion: "+id);
            e.printStackTrace();
        }
    }
}
*/
