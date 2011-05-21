#include "question.h"
#include <string>
#include <fstream>
using namespace std;

#ifndef _TFQUESTION_H
#define _TFQUESTION_H

class tfQuestion : public question {
	public:
		tfQuestion(const tfQuestion & q) : question(q) {
			id = q.id;
			correctAnswer = q.correctAnswer;
		}
		tfQuestion(ofstream & o, string i) : question (o, i) {
		}
		virtual void writeQuestionInfo(){
			my_out << "TFQUESTION " << id <<endl;
			my_out << questionText << endl;
			my_out << "ENDTEXT" << endl;
			my_out << "ANSWER" << endl;
			if(correctAnswer)
				my_out << "T" << endl;
			else
				my_out << "F" << endl;
			my_out << "ENDANSWER" << endl;
		}
		virtual void insertQuestion(){
			my_out << "TFQUESTION " << id << endl;
		}
		virtual void setAnswer(bool answer){
			correctAnswer = answer;
		}
	private:   
		bool correctAnswer;
};

#endif 


/*
***java code***
public class tfQuestion extends question{
    private boolean correctAnswer;
    public tfQuestion(PrintWriter out, String id){
        this.id = id.trim();
        this.out = out;
        doneWithScript = false;
    }
    public void insertQuestion(){
        try{
            out.println("TFQUESTION "+id);
        }catch(Exception e){
            System.out.println(e.toString() + " thrown from insertQuestion of tfQuestion: "+id);
        }
    }
    public void setAnswer(boolean answer){
        correctAnswer = answer;
    }
    public void writeQuestionInfo(){
        try{
            out.println("TFQUESTION "+id);
            out.println(questionText);
            out.println("ENDTEXT");
            out.println("ANSWER");
            if(correctAnswer)
                out.println("T");
            else
                out.println("F");
            out.println("ENDANSWER");
        }catch(Exception e){
            System.out.println(e.toString()+" thrown from writeQuestionInfo() of tfQuestion: "+id);
            e.printStackTrace();
        }
    }
}*/
