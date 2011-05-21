
#include "fibQuestion.h"
#include "tfQuestion.h"
#include "mcQuestion.h"
#include "questionCollection.h"
#include "randgen.h"
#include <fstream>
#include <iostream>
#include <string>
using namespace std;


const int HEAP_SIZE = 9; //This is the number of numbers to sort.
const int MAX_INT = 100; //The numbers are randomly generated between 0 and this number.
int numQuestions; //This int keeps track of the current number of questions.
int Heap[HEAP_SIZE]; //This is the array of numbers to sort;
ofstream my_out; //This is the stream that writes to the script file.
questionCollection Questions(my_out); //This is the group of questions for this visualization.
const bool debug = false; //If 'debug' is true, we output the conents of the array to the screen. 

void fillArray();
//This function puts randomly generated numbers between 0 and MAX_INT in to the Heap array.

void makeHeap();
//This function arranges the numbers in the Heap array into a heap and generates questions.

void doHeapSort();
//This function sorts the Heap array and generates questions.

int walkDown(int index, int maxIndex, int questionIndex);
//The functions 'makeHeap' and 'doHeapSort' use this function to walk the node located at 'index'
//down the heap, so that the tree rooted at 'index' and ending with 'maxIndex' forms a heap.
//This function also calls drawHeap and inserts the question at 'questionIndex' into the 
//visualization.

void drawHeap(string caption, int redindex, int yellowindex, int questionIndex);
//One call to this function writes two snapshots to the script file.  The first snapshot is the
//current view of the array.  The second is the current view of the array arranged in a binary tree.
//'caption' is the title of the binary tree snapshot.  'redindex' is the array node that will be
//colored red in the snapshots, if 'redindex' is less than HEAP_SIZE and greater than or equal to 
//zero.  All array nodes greater than or equal to 'yellowindex' will be colored yellow in the
//snapshots.  'questionIndex' is the index in the question array if a question is to be inserted
//in the scripfile at this point, -1 if not. 

void preOrderTraversal(bool leftchild, int level, int index, int redindex, int yellowindex);
//'drawHeap' uses this recursive function to do a pre-order traversal through the array and output 
//the contents to the script file.  If 'leftchild' is true, and 'L' is output, else an 'R' is 
//written to the file for the specified 'index' in the Heap array.  'level' is the level in the
//binary tree.  'redindex' and 'yellowindex' are the same as in 'drawHeap.

void swap(int index1, int index2);
//This function swaps the two indices in the Heap array.

void showArrayContents();
//This function outputs the array contents to the console window.

int leftchild(int index, int maxIndex);
//This returns the left child of 'index' if the left child exists and is less than 'maxIndex';
// -1 if not.

int rightchild(int index, int maxIndex);
//This returns the right child of 'index' if the left child exists and is less than 'maxIndex';
// -1 if not.

string intTOstring(int x);
//This function translates an integer to an string in a really cheap way.

//The name of the script file to write to is a command-line arguement.
int main(int argc, char* argv[]){
	numQuestions = 0;
	my_out.open(argv[1]);
	//	Questions = questionCollection(my_out);
	fillArray();
	if(debug) showArrayContents();
	makeHeap();
	if(debug) showArrayContents();
	doHeapSort();
	if(debug) showArrayContents();
	Questions.writeQuestionsAtEOSF();
	my_out.close();
	return 1;
}

//This function puts randomly generated numbers between 0 and MAX_INT in to the Heap array.
void fillArray(){
	RandGen r;
	for(int x = 0; x < HEAP_SIZE; x++)
		Heap[x] = r.RandInt(MAX_INT);
	drawHeap("The Original Unsorted Array", HEAP_SIZE, HEAP_SIZE, -1);
}

//This function arranges the numbers in the Heap array into a heap and generates questions.
void makeHeap(){
	for(int x = ((HEAP_SIZE-2)/2); x >= 0; x--){
		Questions.addQuestion(new fibQuestion(my_out, intTOstring(numQuestions)));
		Questions[numQuestions].setQuestionText(
			"At what index will the red node be located after it is walked down the tree?");
		int answer = walkDown(x, HEAP_SIZE, numQuestions);
		Questions[numQuestions].setAnswer(intTOstring(answer));
		numQuestions++;
	}
	drawHeap("Done walking down nodes.  We have a heap.", HEAP_SIZE, HEAP_SIZE, -1);
}

//The functions 'makeHeap' and 'doHeapSort' use this function to walk the node located at 'index'
//down the heap, so that the tree rooted at 'index' and ending with 'maxIndex' forms a heap.
//This function also calls drawHeap and inserts the question at 'questionIndex' into the 
//visualization.
int walkDown(int index, int maxIndex, int questionIndex){
	int rc = rightchild(index,maxIndex);
	int lc = leftchild(index,maxIndex);
	if(maxIndex == HEAP_SIZE)
		drawHeap("Walk the red node down the tree.", index, maxIndex, questionIndex);
	else
		drawHeap("The yellow nodes are sorted, walk down the red node.", index, maxIndex, questionIndex);
	
	while(!((Heap[index] >= rc)&&(Heap[index] >= lc))){	
		if((lc >= Heap[index])&&(lc >= rc)){
			swap(index, index*2+1);
			index = 2*index + 1;
			rc = rightchild(index,maxIndex);
			lc = leftchild(index,maxIndex);
		}
		else{	
			swap(index, index*2+2);
			index = 2*index + 2;
			rc = rightchild(index,maxIndex);
			lc = leftchild(index,maxIndex);
		}
	}
	if(maxIndex == HEAP_SIZE)
		drawHeap("Done walking down the red node.", index, maxIndex, -1);
	else
		drawHeap("The unsorted nodes form a heap.", index, maxIndex, -1);
	return index;
}

//This function sorts the Heap array and generates questions.
void doHeapSort(){
	for(int x = HEAP_SIZE - 1; x > 0; x--){
		swap(0, x);
		drawHeap("Swap the root node with last unsorted node.", 0, x, -1);
		Questions.addQuestion(new fibQuestion(my_out, intTOstring(numQuestions)));
		Questions[numQuestions].setQuestionText(
			"At what index will the red node be located after it is walked down the tree?");
		int answer = walkDown(0, x, numQuestions);
		Questions[numQuestions].setAnswer(intTOstring(answer));
		numQuestions++;	
	}
	drawHeap("The array is sorted.  We're done!", HEAP_SIZE, 0, -1);	
}

//This function translates an integer to an string in a really cheap way.
string intTOstring(int x){
	char* c_str = new char(2);
	c_str[0] = (char)('0' + x);
	c_str[1] = '\0';
	return string(c_str);
}

//One call to this function writes two snapshots to the script file.  The first snapshot is the
//current view of the array.  The second is the current view of the array arranged in a binary tree.
//'caption' is the title of the binary tree snapshot.  'redindex' is the array node that will be
//colored red in the snapshots, if 'redindex' is less than HEAP_SIZE and greater than or equal to 
//zero.  All array nodes greater than or equal to 'yellowindex' will be colored yellow in the
//snapshots.  'questionIndex' is the index in the question array if a question is to be inserted
//in the scripfile at this point, -1 if not. 
void drawHeap(string caption, int redindex, int yellowindex, int questionIndex){
	//draw array slide
	my_out << "VIEW WINDOWS 2" << endl;
	my_out << "VIEW JUMP 2" << endl;
	if((questionIndex >= 0)&&(questionIndex < Questions.size()))
		Questions.insertQuestion(questionIndex);
	my_out << "MD_Array 0.015 0.015 HeavyBold" << endl;
	my_out << "1" << endl;
	my_out << "***\\***" <<endl;
	my_out << HEAP_SIZE <<endl;
	my_out << "1" << endl;
	for(int x = 0; x < HEAP_SIZE; x++){
		my_out << x << endl;
		my_out << "1" << endl;
		if(x == redindex)
			my_out << "\\R ";
		else if(x >= yellowindex)
			my_out << "\\Y ";
		my_out << Heap[x] <<endl;
	}
	my_out << "***^***" << endl;
	//draw tree slide
	my_out << "VIEW WINDOWS 2" << endl;
	my_out << "VIEW JUMP 2" << endl;
	my_out << "BinaryTree   0.02 0.015 HeavyBold"<<endl;
	my_out << "2 1.5 1.5" << endl;
	my_out << caption << endl;
	my_out << "***\\***" << endl;
	preOrderTraversal(false, 0, 0, redindex, yellowindex);
	my_out << "***^***" <<endl;
}

//'drawHeap' uses this recursive function to do a pre-order traversal through the array and output 
//the contents to the script file.  If 'leftchild' is true, and 'L' is output, else an 'R' is 
//written to the file for the specified 'index' in the Heap array.  'level' is the level in the
//binary tree.  'redindex' and 'yellowindex' are the same as in 'drawHeap.
void preOrderTraversal(bool leftchild, int level, int index, int redindex, int yellowindex){
	my_out << level << endl;
	if(leftchild)
		my_out << "L" << endl;
	else
		my_out << "R" << endl;
	if(index == redindex)
		my_out << "\\R ";
	else if(index >= yellowindex)
		my_out << "\\Y ";
	my_out << index << endl;
	my_out << Heap[index] << endl;
	if(index*2 + 1 < HEAP_SIZE)
		preOrderTraversal(true, level+1, index*2 + 1, redindex, yellowindex);
	if(index*2 + 2 < HEAP_SIZE)
		preOrderTraversal(false, level+1, index*2 + 2, redindex, yellowindex);
}

//This returns the left child of 'index' if the left child exists and is less than 'maxIndex';
// -1 if not.
int leftchild(int index, int maxIndex){
	if(2*index + 1 >= maxIndex)
		return -1;
	return Heap[2*index + 1];
}

//This returns the right child of 'index' if the left child exists and is less than 'maxIndex';
// -1 if not.
int rightchild(int index, int maxIndex){
	if(2*index + 2 >= maxIndex)
		return -1;
	return Heap[2*index + 2];
}

//This function swaps the two indices in the Heap array.
void swap(int index1, int index2){
	int temp = Heap[index1];
	Heap[index1] = Heap[index2];
	Heap[index2] = temp;
}

//This function outputs the array contents to the console window.
void showArrayContents(){
	cout << "Heap contents:" <<endl;
	for(int x = 0; x < HEAP_SIZE; x++)
		cout << "index: " << x << " contents: "<< Heap[x] <<endl;
}
















