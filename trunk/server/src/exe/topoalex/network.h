#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include "\gsscgi\include\gdefines.h"
#include "\gsscgi\include\devices.h"
#include "\gsscgi\include\fonts.h"
#include "\gsscgi\include\gdp.h"
#include "\gsscgi\include\hatch.h"
#include "\gsscgi\include\gksbind.h"
#include "\gsscgi\include\colors.h"

#define EXIT 500
#define BLOCK 499
#define ADDNODE 501
#define DELNODE 502
#define ADDEDGE 503
#define DELEDGE 504
#define radius 0.04
#define maxnodes 21
typedef  struct  {
		double weight;
		int edgesegnum;
		int opposite;  /*1-is an opposite,0-is not*/
		 } gridpiece;

typedef struct {float x;
		float y;
		} Gpoint;

  int err_fil;
  int status;
  int flag=0;
  float scale,xndc,yndc;
  char conid=0;
  int echo=1;
  int type=0;
  int ws_id=1;
  int device=1;
  int transform=1;
  int i,j,p0;
  int o=1;
  int t=0;
  int currnode=1;
  int l=100;
  int len=1;
  char ch[80];
  int numbernode=0;
  int numberedge=0;
  float Xlist[5];
  float Ylist[5];
  static Gpoint centers[maxnodes];
  static gridpiece grid[maxnodes][maxnodes];
  float r[3]={0,1,.8};
  float g[3]={0,1,.1};
  float b[3]={0,1,0};

  FILE *fin,*gaigsout;


 int init ( )
 /* given:nothing
    task: open gks, open errorfile for errormessages,initialize colors
	  and grid datastruct
    return:nothing
 */

  {
   int err_fil;
   err_fil = creat ("errors", 0644);
   gopn_gks (err_fil, 5000);
   ws_id=DISPLAY;
   gopn_wk(ws_id,conid,ws_id);
   gact_wk(ws_id);
   for (i=0;i<3;++i);
    gscl_rep(ws_id,i,r[i],g[i],b[i]);
   for (i=0;i<maxnodes;++i)
    {centers[i].x=-1;centers[i].y=-1;
     for (j=0;j<maxnodes;++j)
      {grid[i][j].weight=(-1);
       grid[i][j].edgesegnum=(-1);
       grid[i][j].opposite=0;}
     }
   return (err_fil);
  }

 void finish ( )
  /*closes gks and and errorfile*/
  {
  gdct_wk(ws_id);
  gcls_wk(ws_id);
  gcls_gks( );
  close(err_fil);
  }

 void seterase ( )
  {
  gsf_color(2);
  gsf_inter(GSOLID);
  }

 void setdraw ( )
  {
  gsf_color(1);
  gsf_inter(GHOLLOW);
  }


  void makebox (float xmin, float xmax, float ymin, float ymax)

  /*given lower left and upper right box coordinates, makes a box */

  {
	float Xlist[4],Ylist[4];

	Xlist[0]=xmin;
	Ylist[0]=ymin;
	Xlist[1]=xmax;
	Ylist[1]=ymin;
	Xlist[2]=xmax;
	Ylist[2]=ymax;
	Xlist[3]=xmin;
	Ylist[3]=ymax;
	g_fillarea (4, Xlist,Ylist);
	return;
  }


 void makeadd ( )

   /*given:nothing
   task:create the addnodes segment menu option on the left side of the screen,
	and make it detectable.
   return:nothing, segments are drawn later
  */


  {
    char word[]="Add Nodes";

    gcreat_seg(ADDNODE);
    makebox(.1,.9,4.1,4.9);
    g_text(.5,4.5,word);
    gcls_seg ( );
    gs_sdetec(ADDNODE,GDETECTABLE);
  };

 void makeedge ( )
   /*given:nothing
   task:create the makeedge segment menu option on the left side of the screen,
	and make it undetectable.
   return:nothing, segments are drawn later
  */

   {char word[]="Add Edges";
    makebox(.1,.9,3.1,3.9);
    gcreat_seg(ADDEDGE);
    makebox(.1,.9,3.1,3.9);
    g_text(.5,3.5,word);
    gcls_seg ( );
  };

 void makedelnode ( )
   /*given:nothing
   task:create the delnode segment menu option on the left side of the screen,
	and make it undetectable.
   return:nothing, segments are drawn later
  */

   {char word[]="Delete Node";
    gcreat_seg(DELNODE);
    makebox(.1,.9,2.1,2.9);
    g_text(.5,2.5,word);
    gcls_seg( );
   };
 void makedeledge ( )
   /*given:nothing
   task:create the deledge segment menu option on the left side of the screen,
	and make it undetectable.
   return:nothing, segments are drawn later
  */

   {char word[]="Delete Edge";
    gcreat_seg(DELEDGE);
    makebox(.1,.9,1.1,1.9);
    g_text(.5,1.5,word);
    gcls_seg( );
   };
 void makeexit ( )

   /*given:nothing
   task:create the exit segment menu option on the left side of the screen,
	and make it detectable.
   return:nothing, segments are drawn later
  */

   {char word[]="Exit";
    gcreat_seg(EXIT);
    makebox(.1,.9,.1,.9);
    g_text(.45,.5,word);
    gcls_seg ( );
    gs_sdetec(EXIT,GDETECTABLE);
   };

  void invis ( )
  /* makes menu invisible by setting the visible segment attribute.
     this operates on the current transform, which is 2, for the menu */
   {int i;
    for (i=501;i<505;++i)
     gs_svisi(i,0);
    gupdate_wk(ws_id,GPERFORM);
   };

 void makevis ( )
   /* makes menu visible by setting the visible segment attribute.
     this operates on the current transform, which is 2, for the menu */

   {int i;
    float transmat[2][3];
    int vis,hig,det;
    float pri;
    g_seltrn(2);
    for (i=500;i<505;++i)
     {
     gs_svisi(i,1);
     gqsg_attr(i,transmat,&vis,&hig,&pri,&det);
     }
    gupdate_wk(ws_id,GPERFORM);

   };

int nextnodeavail ()
 /* given:nothing
    task: find the first index from 1 to maxnodes that has not been used
    return: this index
 */

 {int i;
  int index;
  index=0;
 for (i=1;i<maxnodes;++i)
  if (centers[i].x==-1 && centers[i].y==-1)
       {index=i;break;}
  return(index);
  }

void addnode ( )
/* task: weights for user mouse input.  If the user clicks in exit box,
   then he exits add, else a node is added, if there is an index available
*/

{


 int transnum;
 float lx,ly;
 char text[3];

  do {
 grq_loc(ws_id,device,&status,&transnum,&lx,&ly);
 currnode=nextnodeavail();
 if (transnum==1 && currnode !=0) {
 centers[currnode].x=lx;
 centers[currnode].y=ly;
 Xlist[0]=lx;
 Ylist[0]=ly;
 Xlist[1]=lx+radius;
 Ylist[1]=ly;
 g_seltrn(transnum);
 gcreat_seg(currnode);
 seterase ( );
 g_gdp(2,Xlist,Ylist,GCIRCLE,len,ch);
 setdraw ( );
 g_gdp(2,Xlist,Ylist,GCIRCLE,len,ch);
 t=currnode/10;
 o=currnode-(t*10);
 text[0]=48+t;
 text[1]=48+o;
 text[2]=0;
 gsc_height (0.02);
 gst_align(GNORMAL,GNORMAL);
 g_text(Xlist[0],Ylist[0],text);
 gcls_seg( );
 gs_sprior(currnode,1);
 numbernode+=1;
 if (numbernode==1) gs_sdetec(DELNODE,GDETECTABLE);
 if (numbernode==2) gs_sdetec(ADDEDGE,GDETECTABLE);
 gs_sdetec(currnode,GDETECTABLE);
 /*gupdate_wk(ws_id,GPERFORM);*/
 }} while (transnum==1);
 }


 void addedge (int networkflag, int useredge, float edgescale, int dir)
 /*given:networkflag(0-not network, 1-network)
	 useredge(0-geometrically determined, 1-userinput)
	 edgescale(multiplication factor if edges are geometrically determined)
	 dir(whether the graph/network is directed or not)
   task:wait for the user to click on two different nodes and draw an edge
	between them, until he clicks exit.
   return:updated screen, and updated grid and centers data structures
 */

 {
 int kont=0;
 int seg;
 int pickid;
 float xtemp[2], ytemp[2];
 int lenstr;
 double tempweight;
 char respstring[5];
 int transnum;
 float lx,ly;
 char m3[]="Enter edgeweight, then click edgeweight location.";
 g_seltrn(1);
 setdraw ();
 do {
  grq_pck(ws_id,device,&status,&seg,&pickid);
  if (status==2) continue;
  if (seg <99) {

    if (kont==0) {kont+=1;xtemp[0]=centers[seg].x;
		  ytemp[0]=centers[seg].y;p0=seg;
		   continue;}
    if ((kont==1) && (p0==seg)) continue;
    if (kont==1) {kont=0;
       xtemp[1]=centers[seg].x;
       ytemp[1]=centers[seg].y;
       g_pline(2,xtemp,ytemp);
       gsc_height (0.02);
       gst_align(GNORMAL,GNORMAL);
       if (networkflag && useredge) g_text(.2,.95,m3);
       gcreat_seg(l);
       g_pline(2,xtemp,ytemp);
       Xlist[0]=xtemp[1]-.3*(xtemp[1]-xtemp[0]);
       Ylist[0]=ytemp[1]-.3*(ytemp[1]-ytemp[0]);
       Xlist[1]=Xlist[0]+.01;
       Ylist[1]=Ylist[0];
       if (dir) g_gdp(2,Xlist,Ylist,GCIRCLE,len,ch);
       if (networkflag && useredge)
       {strcpy(respstring,"xxxxx");
       grq_str(ws_id,DISPLAY,&status,&lenstr,respstring);
       grq_loc(ws_id,device,&status,&transnum,&lx,&ly);
       g_text(lx,ly,respstring);
       grid[p0][seg].weight=atof(respstring);
       if (dir) grid[seg][p0].weight=atof(respstring);
       };
       if (networkflag && !useredge)
       {tempweight=sqrt((double)((centers[p0].x-centers[seg].x)*
	(centers[p0].x-centers[seg].x)+(centers[p0].y-centers[seg].y)*
	(centers[p0].y-centers[seg].y)));
	grid[p0][seg].weight=(double)(tempweight*edgescale);
	if (dir) grid[seg][p0].weight=(double)(tempweight*edgescale);
	}
       if (!networkflag)
       {grid[p0][seg].weight=1;
	if (dir) grid[seg][p0].weight=1;}
       gcls_seg( );
       gs_sprior(l,.5);
       gs_sdetec(l,GDETECTABLE);
       if (l==100) gs_sdetec(DELEDGE,GDETECTABLE);

       grid[p0][seg].edgesegnum=l;
       grid[seg][p0].opposite=1;
       l+=1;};

 gupdate_wk(ws_id,GPERFORM);
 }
 } while (seg!=EXIT);
 g_redraw(ws_id); }

  void deledge ( )  {
  /* given:nothing
     task: wait for user to click an edge, and delete it, until
	   he clicks exit. (no sexism intended by assuming a male user,
	   and it beats passive voice)
     return: updated screen and grid and centers data structures.
  */


   int seg;
   int pickid;


 do {

   grq_pck(ws_id,device,&status,&seg,&pickid);
   if (status==2) continue;
   if (seg >99 && seg<500) {gdelet_seg(seg);
   gupdate_wk(ws_id,GPERFORM);
    for (i=0;i<maxnodes;++i)
	for (j=0;j<maxnodes;++j)
	  if (grid[i][j].edgesegnum==seg) grid[i][j].weight=(-1);}}
    while (seg != EXIT);
   }

 void delnode ( )
 /* given:nothing
    task: wait for user to click a node, then delete the node and
	   all edges connected to it, until users clicks exit.
    return: updated screen, and grid and centers data structures.
 */
 {

 int j;
 gridpiece erase={-1,-1,0};
 int seg;
 int pickid;

 do {
   grq_pck(ws_id,device,&status,&seg,&pickid);
   if (status==2) continue;
   if (seg<99)
   {gdelet_seg(seg);
     numbernode-=1;
     centers[seg].x=-1;
     centers[seg].y=-1;
     for (j=1;j<maxnodes;++j)
      {if (grid[seg][j].weight>=0)
	{gdelet_seg(grid[seg][j].edgesegnum);numberedge-=1;
	 if (grid[seg][j].opposite==0)
	      grid[seg][j]=erase;};
       if((grid[seg][j].opposite==1) &&
	   (grid[j][seg].weight>=0))
	  {gdelet_seg(grid[j][seg].edgesegnum);numberedge-=1;
	    grid[j][seg]=erase;};
      }
     gupdate_wk(ws_id,GPERFORM);}
     } while (seg!=EXIT);
    }

 void getoptions (int *networkflag,int *useredge, float *edgescale, int *dir)
 /*given:nothing
   task: prompt user to determine values for the flags:
	 networkflag(0-not network, 1-network)
	 useredge(0-geometrically determined, 1-userinput)
	 edgescale(multiplication factor if edges are geometrically determined)
	 dir(0-not directed, 1-directed)
   return:the flags
 */
 {int choice;
  int done=0;
  do{
  printf("1 -- Directed (default)\n");
  printf("2 -- Undirected\n");
  printf("Enter choice: ");
  scanf("%d", &choice);
  if (choice == 2) *dir=0;
  else *dir=1;
  printf("1 -- Enter a Graph\n");
  printf("2 -- Enter a Network with user-inputted edgeweights\n");
  printf("3 -- Enter a Network with geometrically determined edgeweights\n");
  printf("Enter choice: ");
  scanf("%d",&choice);
  done=1;
  switch (choice) {
   case 1: {*networkflag=0;*useredge=0;*edgescale=0;break;};
   case 2: {*networkflag=1;*useredge=1;*edgescale=0;break;};
   case 3: {*networkflag=1;*useredge=0;
	    printf("Enter non-zero edgeweight scaling factor: ");
	    scanf("%f",edgescale);break;};
   default: done=0;
   }
   } while (!done);
  }

 void network ( )
 /* main procedure.  initializes the gks input devices, and then handles
    user menu requests. upon quitting, prompts user to save graph/network
    in johnsonbaugh and gaigs format.
 */
 {
 float dcx,dcy;
 int rasx,rasy;
 int seg;
 int pickid;
 int numberedge;
 int networkflag,useredge,dir;
 float edgescale;
 char save[80];
 char savefilename[80];

 char inistr[5]={0,0,0,0,0};
 getoptions (&networkflag, &useredge, &edgescale, &dir);
 err_fil=init();
 gqmax_disp(ws_id,&device,&dcx,&dcy,&rasx,&rasy);
 scale=dcx;
 xndc=1;
 yndc=(float)dcy/scale;
 gswk_wind (ws_id,0,xndc,0,yndc);
 gswk_view(ws_id,0,dcx,0,dcy);
 transform=1;
 gs_wind(transform,0,1,0,1);
 gs_view(transform,0,.74*xndc,0,yndc);
 transform=2;
 gs_wind(transform,0,1,0,5);
 gs_view(transform,.75*xndc,xndc,0,yndc);
 g_seltrn(1);
 gcreat_seg(BLOCK);
 makebox(0,.99,0,.99);
 g_seltrn(2);
 makebox(0,1,0,5);
 gcls_seg ( );
 gst_align(2,GNORMAL);
 gsview_pri(1,0,GHIGHER);
 gsview_pri(2,1,GHIGHER);
 gsc_height (0.1);
 gst_ftpr (GSIMPLEX, GSTROKE);
 seterase( );
 makeadd ( );
 makeedge( );
 makedelnode( );
 makedeledge( );
 makeexit( );
 device=JOYSTIK;
 seg=1;
 pickid=1;
 g_loc_ini(ws_id,device,1,.5,.5,1,0,dcx,0,dcy,len,ch);
 g_str_ini(ws_id,DISPLAY,5,inistr,1,0,dcx,0,dcy,5,1,len,ch);
 g_pck_ini(ws_id,device,status,seg,pickid,echo,0,dcx,0,dcy,len,ch);
 do{
 do {grq_pck(ws_id,device,&status,&seg,&pickid);} while (status!=GOK);
 if (seg==ADDNODE) {invis( );addnode ( );makevis( );};
 if (seg==ADDEDGE) {invis( );addedge (networkflag,useredge,edgescale,dir );makevis( );
      seg=0;};
 if (seg==DELEDGE) {invis( );deledge ( );makevis( );
      seg=0;};
 if (seg==DELNODE) {invis( );delnode ( );makevis( );
      seg=0;};

 }
 while (seg!=EXIT);
  finish ( );
  numberedge=0;
  printf("Save work as Johnsonbaugh textfile (y/n): ");
  scanf("%s",&save);

 if (save[0]=='y' || save[0] == 'Y')
   {printf("Enter filename: ");
   scanf("%s",&savefilename);
  fin=fopen(savefilename,"w");
  for (i=1;i<maxnodes;++i)
  for (j=1;j<maxnodes;++j)
   if (grid[i][j].weight>=0) numberedge+=1;
  fprintf(fin, "%d %d\n",numbernode,numberedge);
 for (i=1;i<maxnodes;++i)
  for (j=1;j<maxnodes;++j)
   if (grid[i][j].weight>0) fprintf(fin,"%d %d %f \n",i,j,grid[i][j].weight);
 fclose(fin);
  }
  printf("Save work as Gaigs showfile (y/n): ");
  scanf("%s",&save);
   if (save[0]=='y' || save[0] == 'Y')
  { printf("Enter filename: ");
   scanf("%s", &savefilename);
  gaigsout=fopen(savefilename,"w");
  if (!networkflag) fprintf(gaigsout,"Graph\n");
  if (networkflag) fprintf(gaigsout,"Network\n");
  fprintf(gaigsout,"1\n");
  fprintf(gaigsout,"***\\***\n");
  for (i=1;i<maxnodes;++i)
   if (centers[i].x != -1)
   {fprintf(gaigsout,"%d %f %f\n",i,centers[i].x,centers[i].y);
    for (j=1;j<maxnodes;++j)
     {
     if (grid[i][j].weight > 0)
      {if (dir) fprintf(gaigsout,"\\A%d\n",j);
      else fprintf(gaigsout,"%d\n",j);
      if (networkflag) fprintf(gaigsout,"%d\n",(int)grid[i][j].weight);
      }
     }
    fprintf(gaigsout,"32767\n");
    fprintf(gaigsout,"%d\n",i);
   }
   fprintf(gaigsout,"***^***");
   fclose(gaigsout);
   }
}

