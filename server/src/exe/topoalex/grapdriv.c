#include <stdlib.h>
#include <string.h>

char jb_file[80];
char kamada_file[80];
#include "kamada.h"
#include "johnsonb.h"
//#include "network.h"


int main (int argc, char *argv[])
{  int pause;
   strcpy(jb_file, /*"t1.t"*/ argv[1]);
   strcpy(kamada_file, /*"t2.t"*/ argv[2]);
   //strcpy(jb_file, "kamada_bad.tmp");
   //strcpy(kamada_file, "kamada_bad.dat");
   johnsonb ();
   kamada ();
   return 0;
   // scanf("%d", &pause);
}
