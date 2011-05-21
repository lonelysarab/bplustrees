// Class implementation file: stack.cpp

// Array implementation

#include <assert.h>

template <class E>
stack<E>::stack()
{
        top = -1;
}

template <class E>
stack<E>::stack(const stack<E> &s)
{
        top = s.top;
        for (int i = 0; i < top; ++i)
                data[i] = s.data[i];
}

template <class E>
stack<E>& stack<E>::operator = (const stack<E> &s)
{
        top = s.top;
        for (int i = 0; i < top; ++i)
                data[i] = s.data[i];
        return *this;
}

template <class E>
bool stack<E>::empty()
{
        return top == -1;
}


template <class E>
void stack<E>::push(const E &item)
{
        assert(top < MAX_STACK_SIZE - 1);
        ++top;
        data[top] = item;
}

template <class E>
E stack<E>::pop()
{
        E item;
        assert(! empty());
        item = data[top];
        --top;
        return item;
}

template <class E>
E stack<E>::onTop()
{
        assert(! empty());
        return data[top];
}
template <class E>
E* stack<E>::getArray()
{
	return data;
}
template <class E>
int stack<E>::getTop()
{
	return top;
}












