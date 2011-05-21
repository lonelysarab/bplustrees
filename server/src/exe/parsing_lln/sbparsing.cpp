
#include "fibQuestion.h"
#include "tfQuestion.h"
#include "mcQuestion.h"
#include "questionCollection.h"
#include "randgen.h"
#include "stack.h"
#include <fstream>
#include <iostream>
#include <string>
#include <cstdio>
using namespace std;


const char END_TOKEN = '#';
stack<char> opStack;
ofstream out;
const int MAX_NUM_QUESTIONS = 50;
questionCollection Questions(out);
int Qindex = 0;

void infixToPostfix(string &postfix, char* expression, char* iplus, char* splus, 
		    char* iminus, char* sminus, char* itimes, char* stimes, 
		    char* idivide, char* sdivide, char* ipower, char* spower);
void drawSnapshot(int qid, string caption);
void showArray();
string tostr(char* word);
string infixPriority(char t);
string stackPriority(char t);
string intTOstring(int x);
void doQuestion(char token, int qn, string postfix);
string infixplus, stackplus, infixminus, stackminus, infixtimes, stacktimes;
string infixdivide, stackdivide, infixpower, stackpower;

void showArray(){
  char* theArray = opStack.getArray();
  int arraySize = opStack.getTop();
	
  cout << "The stack: ";
  for(int x = 0; x < arraySize; x++)
    cout << theArray[x] << " ";
  cout << endl;
}

void drawSnapshot(int qid, string caption){
  char* theArray = opStack.getArray();
  int arraySize = opStack.getTop();
  out << "VIEW WINDOWS 1" << endl;
  if((qid >= 0)&&(qid < Questions.size())) 
    Questions.insertQuestion(qid);
  out << "Stack 0.015 0.015 HeavyBold" << endl;
  out << "1" << endl;
  out << caption << endl;
  out << "***\\***" <<endl;
  for(int x = arraySize; x >= 0; x--){
    if(x == arraySize)
      out << "\\R";
    out << theArray[x] <<endl;
  }
  out << "***^***" << endl;
}

int main(int argc, char* argv[]){
  out.open(argv[1]);
  //	Questions = questionCollection(out);
  string postfix;
  cout << argv[2] << endl;
  infixToPostfix(postfix, argv[2], argv[3], argv[4], argv[5], argv[6], argv[7], argv[8], 
		 argv[9], argv[10], argv[11], argv[12]);
  cout << postfix << endl;
  Questions.writeQuestionsAtEOSF();
  return 1;
}
void doQuestion(char token, int qn, string postfix){
  Questions.addQuestion(new mcQuestion(out, intTOstring(qn)));
  drawSnapshot(qn, string("The next token is '"+(string("")+token)+"'.\n"+
			  "The current postfix expression is: "+postfix+"."));
  Questions[qn].setQuestionText(string("What will happen to the token '")
				+token+string("'?"));
  Questions[qn].addChoice(string("It will added directly to the postfix expression."));
  Questions[qn].addChoice(string("It will added to the operator stack on top of the token currently on the top of the stack."));
  Questions[qn].addChoice(string("It will added to the operator stack after removing at least one token from the stack."));
  Questions[qn].addChoice(string("All tokens up to the opening parenthesis will be popped off the stack and added to the postfix expression."));
}


void infixToPostfix(string &postfix, char* expression, char* iplus, char* splus, 
		    char* iminus, char* sminus, char* itimes, char* stimes, 
		    char* idivide, char* sdivide, char* ipower, char* spower){ 
  char item, ch;
  int x = 0;
  infixplus = string(iplus);
  stackplus = string(splus);
  infixminus = string(iminus);
  stackminus = string(sminus);
  infixtimes = string(itimes);
  stacktimes = string(stimes);
  infixdivide = string(idivide);
  stackdivide = string(sdivide);
  infixpower = string(ipower);
  stackpower = string(spower);
  drawSnapshot(-1, string(
			  string("Take note of the following values;\nyou will need them to answer stop-and-think quiz questions")+
			  string(".\nThe infix priority for '(' is 9, and the stack priority for '(' is 0")+
			  string(".\nThe infix priority for ')' is 0, and the stack priority for ')' is undefined")+
			  ".\nThe infix priority for '+' is "+infixplus+", and the stack priority for '+' is "+stackplus+
			  ".\nThe infix priority for '-' is "+infixminus+", and the stack priority for '-' is "+stackminus+
			  ".\nThe infix priority for '*' is "+infixtimes+", and the stack priority for '*' is "+stacktimes+
			  ".\nThe infix priority for '/' is "+infixdivide+", and the stack priority for '/' is "+stackdivide+
			  ".\nThe infix priority for '^' is "+infixpower+", and the stack priority for '^' is "+stackpower+"."));
  opStack.push(END_TOKEN);
  do{
    ch = expression[x];
    if(ch == '\0')
      break;
    if(x > 0){
      doQuestion(expression[x], Qindex, postfix);
    }
    if (('A' <= ch) && (ch <= 'Z')){
      if(x > 0){
	cout << "calling set answer " << 1 << endl;
	Questions[Qindex].setAnswer((int)1);
	Qindex++;
      }
      postfix = postfix + ch;	
      drawSnapshot(-1, string("The infix expression is: "+tostr(expression+x)+
			      "\nThe current token '")+ch+string(
								 "' is not an operator,\nso append it to the postfix expression.\nThe postfix expression is: ")
		   +postfix+string("."));
    }
    else if (ch == ')'){
      if(x > 0){
	Questions[Qindex].setAnswer((int)4);
	Qindex++;
      }
      item = opStack.pop();
      while (item != '('){
	postfix = postfix + item;
	item = opStack.pop();
      }
      drawSnapshot(-1, string("The infix expression is: "+tostr(expression+x)+
			      "\nWhen we reach a closing parenthesis\nwe pop items off the stack and append them to the postfix expression\nuntil we find an opening parenthesis.\nThe postfix expression is: "
			      )+postfix+string("."));	
    }
    else{
      drawSnapshot(-1, string("The infix expression is: "+tostr(expression+x)+
			      "\nThe current token '")+ch+string("' is an operator with infix priority of ")
		   +infixPriority(ch)+string(".\nPop '")+opStack.onTop()+string(
										"' off the operator stack to compare with this token."));
      item = opStack.pop();
      bool setAnswer = false;
      while (stackPriority(item) >= infixPriority(ch)){
	if((!setAnswer)&&(x > 0)){
	  Questions[Qindex].setAnswer((int)3);
	  Qindex++;
	  setAnswer = true;
	}
	postfix = postfix + item;
	drawSnapshot(-1, string("The infix expression is: "+tostr(expression+x)+"\n'")
		     +item+string("' has a stack priority of ")+stackPriority(item)+string(
											   ".\nThis is greater than or equal to the infix priority of the current token '")+
		     ch+string("',\nso pop '")+item+string(
							   "' and append it to the postfix expression.\nThe current postfix expression is: ")
		     +postfix+string("."));
	item = opStack.pop();
      }
      if((!setAnswer)&&(x > 0)){
	Questions[Qindex].setAnswer((int)2);
	Qindex++;
      }
      drawSnapshot(-1, string("The infix expression is: "+tostr(expression+x)+
			      "\n'")+item+string("' has a stack priority of ")+
		   stackPriority(item)+string(".\nThis is less than the infix priority of '")+
		   ch+string("',\nso push '")+item+string("' and the current token '")
		   +ch+string("' on to the operator stack."));
      opStack.push(item);
      opStack.push(ch);
    }
    showArray();
    x++;
  }while (ch != '\0');
  drawSnapshot(-1, string("Since the infix expression is empty,\npop the rest of the operators off the stack and\nappend them to the postfix expression."));
  while(opStack.onTop() != '#'){
    item = opStack.pop();
    postfix = postfix + item;
    drawSnapshot(-1, string("Pop '"+(string("")+item)+"' off the stack and append to the postfix expression.\nThe postfix expression is: ")+postfix+string("."));
  }
  drawSnapshot(-1, string("We're done.\nThe initial infix expression was "+string(expression)+".\nThe final postfix expression is: ")+postfix+string("."));

}
string tostr(char* word){
  string str;
  for(int x = 0; word[x] != '\0'; x++)
    str = str + word[x];
  return str;
}
string infixPriority(char t)
{
  string priority;
        
  switch (t)
    {
    case '^':                       priority = infixpower;
      break;
    case '*':						priority = infixtimes;
      break;
    case '/':                       priority = infixdivide;
      break;
    case '+':						priority = infixplus;
      break;
    case '-':                       priority = infixminus;
      break;
    case '(':                       priority = "9";
      break;
    case ')':
    case END_TOKEN:					priority = '0';
    }
  return priority;
}

string stackPriority(char t)
{
  string priority;

  switch (t)
    {
    case '^':                       priority = stackpower;
      break;
    case '*':						priority = stacktimes;
      break;
    case '/':                       priority = stackdivide;
      break;
    case '+':						priority = stackplus;
      break;
    case '-':                       priority = stackminus;
      break;
    case '(':
    case END_TOKEN: priority = '0';
    }
  return priority;
}

string intTOstring(int x){
//   char* c_str = new char(2);
//   c_str[0] = (char)('0' + x);
//   c_str[1] = '\0';
  char* c_str = new char(10);
  sprintf(c_str, "%d", x);
  return string(c_str);
}
        
