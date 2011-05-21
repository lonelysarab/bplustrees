// My changes marked by "t.n. change"

#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>

#define maxgnodes 51
/* Iteration limits used to guard against runaway loop in MakePretty() */
#define ITERLIM1 300
#define ITERLIM2 30

struct graphtype {int numverts;
		 int numedges;
		 float cons[maxgnodes][maxgnodes];
		 float x[maxgnodes];
		 float y[maxgnodes];
		 };

float gMax;
float kSideLength=1;
float kConstant=1;
float tension[maxgnodes][maxgnodes];
float spring[maxgnodes][maxgnodes];
float conscopy[maxgnodes][maxgnodes];
int Gint,gTemp;
int networkflag=0;
FILE *fin;

struct graphtype graph;
/************************** Min ***********************/
/* receives:   two numbers in one and two
   operations: determines the minimum of the two numbers
   returns:    the minimum of the two numbers
*/

int Min(int one, int two)
{
  int one1, two2;

  one1 = one;
  two2 = two;
  if(one1 < two2)
    return one1;
  else
    return two2;
}

void loadfile (struct graphtype *graph, int *networkflag)
/* receives: empty datastruct and networkflag
   task: prompt for file, read data from file in Johnsonbaugh
    format into datastruct
   returns: graph, and networkflag, which is 0 is all weights
    read were 1, and 1 otherwise
*/
 {// t.n. change
  // char nameoffile[80];
  int i,j;
  float weight;
  double theta;
  int done=0;
  for (i=1;i<maxgnodes;++i)
   {
   for (j=1;j<maxgnodes;++j)
     {if (i==j) (*graph).cons[i][j]=0;
      else (*graph).cons[i][j]=999;
      conscopy[i][j]=-1;
      }
    }
  while (!done)
  // t.n. change
  {  done = 1;
    // printf("Enter name of file to load: ");
    // scanf("%s",&nameoffile);
    if ((fin=fopen(jb_file /* nameoffile */,"r")) ==NULL)
      printf("Cannot open file\n");
    else {done=1;
	   fscanf(fin, "%d %d", &((*graph).numverts), &((*graph).numedges));
	   while (fscanf(fin,"%d %d %f",&i,&j,&weight) != EOF)
	    {conscopy[i][j]=weight;
	      if (weight !=1) *networkflag=1;
	      (*graph).cons[i][j]=1;
	      (*graph).cons[j][i]=1;}
       fclose(fin);
	   }
   }

  /*starting node positions roughly around the unit circle translated
    to a (0,0) (1,1) screen */
  theta=(double) (2*3.1415/(*graph).numverts);
  // t.n. change
  for (i=1;i<=(*graph).numverts;++i)
   {(*graph).x[i]=(double) (cos( i*theta)+1.4/*1.2*/)/3;  /* Brian had */
    (*graph).y[i]=(double) (sin( i*theta)+1.4/*1.2*/)/3;  /* 1.2 but 1.4 better */
   }

  }
/****************************** FindDistance ************************/
/* receives:   nothing
   operations: uses Floyd's algorithm to determine the distance between all
	       nodes in the graph and puts this distance into the gDistance
	       array used in Kamada's algorithm
   returns:    gDistance array initialized
*/

void FindDistance( )
{
  int k, i, j;

  gMax = -1;
  for(k=1;k<=(graph).numverts;++k)
    for(i=1;i<=(graph).numverts;++i)
      for(j=1;j<=graph.numverts;++j)
	{
	(graph).cons[i][j] = Min((graph).cons[i][j], (graph).cons[i][k]+(graph).cons[k][j]);
	if(((graph).cons[i][j] > gMax)&&((graph).cons[i][j] != 999))
	  gMax = (graph).cons[i][j];
	}

}

/*************************** OtherParams ****************************/
/* receives:   nothing
   operations: initializes the spring and tension arrays used in Kamaka's
	       algorithm
   returns:    the initialized arrays
*/

void OtherParams(void)
{
  int i, k, j;

  for(i=1;i<=graph.numverts;++i)
    for(j=1;j<=graph.numverts;++j)
      spring[i][j] = (graph).cons[i][j] * kSideLength / gMax;
  for(i=1;i<=graph.numverts;++i)
    for(j=1;j<=graph.numverts;++j)
      if((graph).cons[i][j] == 0)
	tension[i][j] = 0;
      else
	tension[i][j] = kConstant / ((graph).cons[i][j] * (graph).cons[i][j]);
   }

/****************************** MaxDelta ************************/
/* this function is an extension of the equations found in Kamada's
   algorithm
*/

double MaxDelta(int *Gint)
{

  double temp = -999.0, temp1;
  int j;
  double Delta(int num);

  for(j=1;j<=graph.numverts;++j)
  {
    temp1 = Delta(j);
    if(temp1 > temp)
      {
      *Gint = j;
      temp = temp1;
      }
  }
  return temp;
}

/*************************** Equation7 *************************/
/* this function is an extension of the equations found in Kamada's
   algorithm
*/


double Equation7(int num)
{

  double sum1 = 0, diffx, diffy;
  int i;


  for(i=1;i<=graph.numverts;++i)
    if(i != num)
      {

       diffx = (graph).x[num] - (graph).x[i];
       diffy = (graph).y[num] - (graph).y[i];
       sum1 += (tension[num][i]*(diffx - (spring[num][i]*diffx/sqrt(diffx*diffx
		+ diffy*diffy))));
      }
      return sum1;
}

/*************************** Equation8 *************************/
/* this function is an extension of the equations found in Kamada's
   algorithm
*/


double Equation8(int num)
{

  double sum1 = 0.0, diffx, diffy;
  int i;


  for(i=1;i<=graph.numverts;++i)
    if(i != num)
      {
       diffx = (graph).x[num] - (graph).x[i];
       diffy = (graph).y[num] - (graph).y[i];
       sum1 += (tension[num][i]*(diffy - (spring[num][i]*diffy/sqrt(diffx*diffx
		+ diffy*diffy))));
      }
      return sum1;
}

/*************************** Equation13 *************************/
/* this function is an extension of the equations found in Kamada's
   algorithm
*/


double Equation13(int num)
{

  double sum1 = 0.0, diffx, diffy;
  int i;


  for(i=1;i<=graph.numverts;++i)
    if(i != num)
      {
      diffx = (graph).x[num] - (graph).x[i];
      diffy = (graph).y[num] - (graph).y[i];
      sum1 += (tension[num][i]*(1 - (spring[num][i]*pow(diffy,2)
	      /pow(diffx*diffx + diffy*diffy, 1.5))));
      }
      return sum1;
}

/*************************** Equation14 *************************/
/* this function is an extension of the equations found in Kamada's
   algorithm
*/


double Equation14(int num)
{

  double sum1 = 0.0, diffx, diffy;
  int i;


  for(i=1;i<=graph.numverts;++i)
    if(i != num)
      {
       diffx = (graph).x[num] - (graph).x[i];
       diffy = (graph).y[num] - (graph).y[i];
       sum1 += (tension[num][i]*((spring[num][i]*diffx*diffy
	      /pow(diffx*diffx + diffy*diffy, 1.5))));
      }
      return sum1;
}


/*************************** Equation16 *************************/
/* this function is an extension of the equations found in Kamada's
   algorithm
*/


double Equation16(int num)
{

  double sum1 = 0.0, diffx, diffy;
  int i;


  for(i=1;i<=graph.numverts;++i)
    if(i != num)
      {
       diffx = (graph).x[num] - (graph).x[i];
       diffy = (graph).y[num] - (graph).y[i];
       sum1 += (tension[num][i]*(1 - (spring[num][i]*pow(diffx,2)
	      /pow(diffx*diffx + diffy*diffy, 1.5))));
      }
      return sum1;
}




/****************************** Delta ***************************/
/* this function is an extension of the equations found in Kamada's
   algorithm
*/


double Delta(int num)
{
  double tmp1, tmp2, deltaret;

  tmp1 = Equation7(num);
  tmp2 = Equation8(num);
  deltaret = sqrt(tmp1*tmp1 + tmp2*tmp2);
  return deltaret;
}

/****************************** MakePretty ************************/
/* this function is an extension of the equations found in Kamada's
   algorithm
*/


void MakePretty(void)
{
  double temp, temp1, temp2;
  double deltax, deltay;
  short counter, counter2;
  double epsilon = 0.0001;
  double epsilon1 = 0.0001;
  int gCurr;
  double MaxDelta(int *Gint);
  int iter1 = 0;
  int iter2 = 0;

  FindDistance();
  OtherParams();
  temp = MaxDelta(&Gint);
  while((temp > epsilon) && (iter1<ITERLIM1))
     {
     iter2 = 0;
     iter1++;
     gCurr=Gint;
     while((temp > epsilon1)&&(iter2<ITERLIM2))
      {
       iter2++;
       deltax = (-1*Equation7(Gint)*Equation16(Gint) - Equation14(Gint)*
		-1*Equation8(Gint))/
		(Equation13(Gint)*Equation16(Gint) - Equation14(Gint)*
		 Equation14(Gint));
       deltay = (-1*Equation13(Gint)*Equation8(Gint) - Equation14(Gint)*
		-1*Equation7(Gint))/
		(Equation13(Gint)*Equation16(Gint) - Equation14(Gint)*
		 Equation14(Gint));
       (graph).x[gCurr]+= deltax;
       (graph).y[gCurr]+= deltay;
       temp = Delta(Gint);
      }
     temp = MaxDelta(&Gint);
   }
     gCurr = gTemp;

}

void kamada ()
 {int i,j;
  char save[10 /*80*/];
  // t.n. change
  // char savefilename[80];
  int networkflag=0;
  FILE *gaigsout;

  loadfile (&graph, &networkflag);
  MakePretty ();
  /*save the results*/
  // t.n. change
  // printf("Save work as Gaigs showfile (y/n): ");
  // scanf("%s",&save);
  strcpy(save, "yes");
   if (save[0]=='y' || save[0] == 'Y')
  { //printf("Enter filename: ");
   //scanf("%s", &savefilename);
  gaigsout=fopen(kamada_file /*savefilename*/,"w");
  //if (!networkflag) fprintf(gaigsout,"Graph\n");
  //if (networkflag) fprintf(gaigsout,"Network\n");
  //fprintf(gaigsout,"1\n");
  //fprintf(gaigsout,"***\\***\n");
  fprintf(gaigsout,"%d %d \n", graph.numverts, graph.numedges);
  fprintf(gaigsout,"Gaigs\n");
  for (i=1;i<=graph.numverts;++i)
    // t.n. change -- Forgive me for this crude hack that gets around an obscure
    // problem with y-coords sometimes being slightly negative
    // and a later problem that emerged with one or both coords
    // being negative when a vertex in the DAG is disconnected 
    // from other vertices in the graph (which apparetnly is
    // possible in Johnsonbaugh's algoithm
   { float hack = 0.02;
   float hack1 = 0.98;

     if (graph.x[i] > 0.0 && graph.y[i] > 0.0 && graph.x[i] < 1.0 && graph.y[i] < 1.0)
       fprintf(gaigsout,"%f %f\n",graph.x[i],graph.y[i]);
     else if (graph.x[i] > 0.0 && graph.y[i] <= 0.0)
        fprintf(gaigsout,"%f %f\n",graph.x[i],hack);
     else if (graph.y[i] > 0.0 && graph.x[i] <= 0.0)
        fprintf(gaigsout,"%f %f\n",hack, graph.y[i]);
     else if (graph.x[i] < 1.0 && graph.y[i] >= 1.0)
        fprintf(gaigsout,"%f %f\n",graph.x[i],hack1);
     else if (graph.y[i] < 1.0 && graph.x[i] >= 1.0)
        fprintf(gaigsout,"%f %f\n",hack1, graph.y[i]);
     else 
       fprintf(gaigsout, "%f %f\n",hack, hack);
     }
  for (i=1;i<=graph.numverts;++i)
   {//fprintf(gaigsout,"%d %f %f\n",i,graph.x[i],graph.y[i]);
    for (j=1;j<=graph.numverts;++j)
     {if (conscopy[i][j] != -1)
      {
      //fprintf(gaigsout,"\\A%d\n",j);
      fprintf(gaigsout, " %d %d ",i,j);
      //if (networkflag) fprintf(gaigsout,"%d\n",(int)conscopy[i][j]);
      if (networkflag) fprintf(gaigsout,"%d\n",(int)conscopy[i][j]);
       else fprintf(gaigsout, " 1\n");
      }
     }
    //fprintf(gaigsout,"32767\n");
    //fprintf(gaigsout,"%d\n",i);
   }
   //fprintf(gaigsout,"***^***");
   fclose(gaigsout);
   }

 }
