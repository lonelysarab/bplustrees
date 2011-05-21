// quickSort.cpp
//    Robert Lowe  IHRTHC
//
// Non-functioning code...
//
// Approach:
// 1. Modify the special stream classes to use an overloaded brackets operator
// 2. Provide a generic swap() function which can utilize the streams' get
//      and put functions
// 3. Implement caching operations for cstream's get/put functions
//
// Problems/Solutions:
// a. Brackets operator appeared to work.
// b. How to provide a generic swap function? The brackets operator will only
//    return a copy of the value, not the address.
// c. How can caching be turned on/off, since the quickSort functions have
//    no way to signal the underlying stream that the chunks are too large?
//    This means that the get/put functions have to do this, but how will
//    they know about the size of chunk being sorted???
//    Perhaps assume caching is on, keep track of cache hits and misses, 
//    and turn it off if the percentage of hits vs. misses drops to some
//    level!???  How would this affect the ability to move the cached
//    range???
//
//  I never got past #2 above, which effectively blocked progress on the
//  more interesting problems.  :-(

#include <vector>
#include <iostream.h>
#include <multiset.h>
#include <algorithm>
#include <string>
#include "rstream.h"

/*  This didn't work!  Need a generic replacement for the swap function
    to allow use of unmodified/generic quickSort functions.
    
template <class T> void swap(T &a[ai], T &b[bi])
{
  T aTemp, aTemp;
  a.get(ai,aTemp);
  b.get(bi,bTemp);
  a.put(ai,bTemp);
  b.put(bi,aTemp);
}
*/

template <class T>
unsigned int pivot (T & v, unsigned int start, 
	unsigned int stop, unsigned int position)

	// partition vector into two groups
	// values smaller than or equal to pivot
	// values larger than pivot
	// return location of pivot element

{
		// swap pivot into starting position

	swap (v[start], v[position]);

		// partition values

	unsigned int low = start + 1;
	unsigned int high = stop;

	while (low < high)
		if (v[low] < v[start])
			low++;
		else if (v[--high] < v[start])
			swap (v[low], v[high]);

		// then swap pivot back into place
	swap (v[start], v[--low]);
	return low;
}

template <class T>
void quickSort(T & v, unsigned int low, unsigned int high)
{
	// no need to sort a vector of zero or one elements
	if (low >= high)
		return;

	// select the pivot value
	unsigned int pivotIndex = (low + high) / 2;

	// partition the vector
	pivotIndex = pivot (v, low, high, pivotIndex);

	// sort the two sub vectors
	if (low < pivotIndex)
		quickSort(v, low, pivotIndex);
	if (pivotIndex < high)
		quickSort(v, pivotIndex + 1, high);
}

template <class T> void quickSort(T & v)
{
	unsigned int numberElements = v.size ();
	if (numberElements > 1) 
		quickSort(v, 0, numberElements);
}


// create an "array stream" of 10^6 random numbers
// while writing the integers to *two* output files
// close the files;
// re-open the files, one each using rstream (file-only);
//                    the other using cstream (file + cache)
//
// time the sorting of each of the three streams
// Additional includes: ctime

void main()
{
  string filnam1 = "int1.dat";
  string filnam2 = "int2.dat";
  rstream<int> rfile(filnam1);
  rstream<int> rfile2(filnam2);
  astream<int> av(100);

  int r;
  for (int i = 0; i < 100; i++) {
    r = rand()%32768;
    av.put(i,r);
    rfile.put(i,r);
    rfile2.put(i,r);
    cout << r << " ";
  }
  rfile2.close();
  //cstream<int> cfile(10,filnam2);

  cout << "\n\nRead from astream...\n";
  for (int i = 0; i < 100; i++) {
    // av.get(i,r);
    cout << av[i] << " ";
  }
  cout << "\n\nRead from rstream...\n";
  for (int i = 0; i < 100; i++) {
    rfile.get(i,r);
    cout << r << " ";
  }
  cout << "\n";

/*  cout << "\n\nNow sorted astream...\n";
  quickSort(av);
  for (int i = 0; i < 100; i++) {
    av.get(i,r);
    cout << r << " ";
  }
*/
  
/*  cout << "\n\nNow sorted in-memory array...\n";
  quickSort(v);
  vector<int>::iterator itr = v.begin();
  while (itr != v.end()) {
    cout << *itr << " ";
    itr++;
  }
  // copy (v.begin(), v.end(), ostream_iterator<int>(cout, ":"));
  cout << "\n";
*/

}

//---------- rstream.h ------------//

//#pragma once

#include <fstream>

// Array "stream" class
template <class T> class astream
{
  public:
    astream(size_t iSize) : data(iSize) { }
    bool get(size_t index, T &value);
    void put(size_t index, T &value);
    operator[](size_t index) { return data[index]; }
    size_t size() { return data.size(); }

  protected:
    vector<T> data;
};

template <class T> bool astream<T>::get(size_t index, T &value)
{
  if (index <= data.size()) {
    value = data[index];
    return true;
  } else {
    return false;
  }
}

template <class T> void astream<T>::put(size_t index, T &value)
{
  data[index] = value;
}

class cacheDesc
{
    public:
      cacheDesc() { cachesize = 0; cStart,cEnd = -1; }

      size_t cachesize; // Total possible size
      int cStart;       // Starting element number
      int cEnd;         // Ending element number
      bool On;          // Is caching turned on?
};


// Cache "stream" class
template <class T> class cstream
{
  public:
    cstream(size_t size, string &name);
    bool get(size_t index, T &value);
    void put(size_t index, T &value);
    size_t size();
    size_t getCachesize() {return cacheD.cachesize;}
    //void turnoncache(); // if quickSort is using a small enough chunk
    //void turnoffcache();

  protected:
    void loadcache(size_t index);
    void flushcache();
    fstream theStream;
    vector<T> cache;
    cacheDesc cacheD;
};

template <class T> cstream<T>::cstream(size_t size, string &name)
{
  const char *cName = name.c_str();
  theStream.open(cName,ios::in | ios::out);
  //cache(size);
  cacheD.cachesize = size;
  cacheD.On = true;
  loadcache(0);     // Load first segment into cache
};

template <class T> void cstream<T>::loadcache(size_t index)
{
  int s, e;    // start and end indices of cached info
  s = index - (cacheD.cachesize/2);
  s = s < 0 ? 0 : s;
  e = s + cacheD.cachesize - 1;

  // read from theStream into cache
  bool flag = true;
  int c = 0;
  int i = s;
  while (i < e || !flag) {
    flag = get(i, cache[c]);
    ++c;
    ++i;
  }
  cacheD.cStart = s;
  if (i == e)
    cacheD.cEnd = i;
  else
    cacheD.cEnd = i-1;
}

template <class T> void cstream<T>::flushcache()
{
  T value;
  for (int i = cacheD.cStart; i <= cacheD.cEnd; ++i) {
    value = cache[i-cacheD.cStart];
    put(i, value);
  }
}

template <class T> bool cstream<T>::get(size_t index, T &value)
{
  if (index < cacheD.cStart || index > cacheD.cEnd) {
    loadcache(index);
  }
  value = cache[index - cacheD.cStart];
  
  // theStream.seekg(index*sizeof(T));
  // char* ptr = (char*) &value;
  // theStream.read(ptr,sizeof(T));
  // return (theStream.gcount() == sizeof(T));

  return true;  // for now...
}

template <class T> void cstream<T>::put(size_t index, T &value)
{
  // if cache turned on...
  //    if index is not in the cache
  //       move the cache
  //    update the cache
  // else update directly to the file emulating rstream's put function
}

template <class T> size_t cstream<T>::size()
{
  theStream.seekg(0,ios::end);
  return (theStream.tellg() / sizeof(T)) + 1;
}


template <class T> class rstream
{
  public:
    rstream(string &name);
    bool get(unsigned int index,T& value);
    void put(unsigned int index,T& value);
    void close() { theStream.close(); }
    
    unsigned int size();
  
  protected:
    fstream theStream;
};

template <class T> rstream<T>::rstream(string& name)
{
  const char* cName = name.c_str();
  theStream.open(cName,ios::in | ios::out);
}

template <class T> bool rstream<T>::get(unsigned int index,T& value)
{
  theStream.seekg(index*sizeof(T));
  char* ptr = (char*) &value;
  theStream.read(ptr,sizeof(T));
  return (theStream.gcount() == sizeof(T));
}

template <class T> void rstream<T>::put(unsigned int index,T& value)
{
  theStream.seekp(index*sizeof(T));
  char* ptr = (char*) &value;
  theStream.write(ptr,sizeof(T));
}

template <class T> unsigned int rstream<T>::size()
{
  theStream.seekg(0,ios::end);
  return (theStream.tellg() / sizeof(T)) + 1;
}

