#include <iostream>
#include <fstream>
#include <cassert>
#include <cstdlib>
#include <string>
#include "question.h"
using namespace std;

#ifndef _QUESTIONCOLLECTION_H
#define _QUESTIONCOLLECTION_H

//  class for adding groups of questions to gaigs or samba script files
    
class questionCollection{
	public:
  //  		questionCollection(){}
		questionCollection(ofstream & o) : my_out(o) {
		  //			my_out = o;
			numQuest = 0;
			maxSize = 25;
			Questions = new question* [maxSize];
			for (int i = 0; i < maxSize; i++) Questions[i] = new question(my_out, string(""));	
		}
		void insertQuestion(int index){
			if((index < 0)||(index >= numQuest)){
				cerr << "Illegal vector index: " << index << " max index = ";
				cerr << (numQuest-1) << endl;
				abort();
			}
			Questions[index] -> insertQuestion();
		}
		void writeQuestionsAtEOSF(){
			my_out << "STARTQUESTIONS" << endl;
			for(int x = 0; x < numQuest; x++)
				Questions[x] -> writeQuestionInfo();
		}
		int size(){
			return numQuest;
		}
		void addQuestion(question * q){
			if(numQuest >= maxSize)
				expand(2*maxSize);
			Questions[numQuest] = q;
			numQuest++;
		}
		question & operator[](int index){
			if((index < 0)||(index >= numQuest)){
				cerr << "Illegal vector index: " << index << " max index = ";
				cerr << (numQuest-2) << endl;
				abort();
			}
			return *(Questions[index]);
		}
		const question & operator[](int index) const{
			if((index < 0)||(index >= numQuest)){
				cerr << "Illegal vector index: " << index << " max index = ";
				cerr << (numQuest-2) << endl;
				abort();
			}
			return *(Questions[index]);
		}
	private:
		void expand(int new_max){

			Questions = new question* [maxSize];
			for (int i = 0; i < maxSize; i++) Questions[i] = new question(my_out, string(""));	


			question ** tempArray;
			tempArray = new question* [new_max];
			for (int i = 0; i < new_max; i++)
			  if (i < maxSize)
			    tempArray[i] = Questions[i];
			  else
			    tempArray[i] = new question(my_out, string(""));	
			Questions = tempArray;
			maxSize = new_max;
		}
		question ** Questions;
		ofstream& my_out;
		int numQuest;
		int maxSize;
};

#endif    /* _QUESTIONCOLLECTION_H not defined */

/*
Java Code

public class questionCollection{
    PrintWriter out;
    Vector Questions;
    
    public questionCollection(PrintWriter out){
        this.out = out;
        Questions = new Vector();
    }
    public void addQuestion(question q){
        Questions.addElement(q);
    }
    public void insertQuestion(int index){
        if((index >= 0)&&(index < Questions.size()))
            ((question)Questions.elementAt(index)).insertQuestion();
    }
    public void writeQuestionsAtEOSF(){
        try{
            out.println("STARTQUESTIONS");
        }catch(Exception e){
            System.out.println(e.toString() + " thrown from writeQuestionsAtEndOfFile");
            e.printStackTrace();
        }
        for(int x = 0; x < Questions.size(); x++){
            ((question)Questions.elementAt(x)).writeQuestionInfo();
        }
    }
}*/
