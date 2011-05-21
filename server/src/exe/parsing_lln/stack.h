// Class declaration file: stack.h

// Declaration section

#ifndef STACK_H

// We assume that the constant MAX_STACK_SIZE is 
// already defined by the application in size.h

#include "size.h"

template <class E> class stack
        {

        public:
        
        // Class constructors

        stack();
        stack(const stack<E> &s);
        
        // Member functions

        bool empty();
        void push(const E &item);
        E pop();
        E onTop();
        stack<E>& operator = (const stack<E> &s);

		E* getArray();
		int getTop();
        
        protected:
        
        // Data members
        
        int top;
        E data[MAX_STACK_SIZE]; 

        };

#include "stack.cpp"
        
#define STACK_H
#endif

