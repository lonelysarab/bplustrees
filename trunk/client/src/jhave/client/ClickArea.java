/*
   This file is part of JHAVE -- Java Hosted Algorithm Visualization
   Environment, developed by Tom Naps, David Furcy (both of the
   University of Wisconsin - Oshkosh), Myles McNally (Alma College), and
   numerous other contributors who are listed at the http://jhave.org
   site

   JHAVE is free software: you can redistribute it and/or modify it under
   the terms of the GNU General Public License as published by the Free
   Software Foundation, either version 3 of the License, or (at your
   option) any later version.

   JHAVE is distributed in the hope that it will be useful, but WITHOUT
   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
   for more details.

   You should have received a copy of the GNU General Public License
   along with the JHAVE. If not, see:
   <http://www.gnu.org/licenses/>.
*/

/*
 * ClickArea.java
 *
 * Created on June 23, 2010, 11:27:05 AM
 */

//Alejandro
package jhave.client;

import java.awt.*;
import java.awt.geom.*;
import java.applet.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.awt.Graphics2D;

class ClickArea extends JScrollPane implements MouseListener, ItemListener, ActionListener, MouseMotionListener  {
    String[] exerciseOptions;

    String[] exerciseArgs;
    JComboBox exerciseList;

    int exercise = 1;

    Boolean standard;
    Boolean clippingPolygon;
    Boolean inClickingArea;
    Boolean dragged;

    Graphics2D comp2D;

    String text;

    String [][] exerciseTokens = new String[7][];
    //Clipping Visualization Mode
    private JRadioButton[] radioButtonMode;
    ButtonGroup optionGroupMode;
    JPanel radioPanelMode;
    String[] btnTextMode = {"Clipping Visualization Standard Mode","Clipping Visualization Excercise Mode"};
    //Polygon input
    private JRadioButton[] radioButtonPolygon;
    ButtonGroup optionGroupPolygon;
    JPanel radioPanelPolygon;
    String[] btnTextPolygon = {"Clipping Polygon","Subject Polygon"};

    JPanel panel;

    ArrayList<Integer> cpX = new ArrayList<Integer>();
    ArrayList<Integer> cpY = new ArrayList<Integer>();
    ArrayList<Integer> spX = new ArrayList<Integer>();
    ArrayList<Integer> spY = new ArrayList<Integer>();

    JButton undo = new JButton ("Undo");

    int cpClicks,spClicks,cpSides=7,spSides=7,textY;

    public ClickArea(JPanel p){
        super(p);
	panel = p;
        super.addMouseListener(this);
	super.addMouseMotionListener(this);
	setExersices();
//Visualization Mode
	radioPanelMode = new JPanel((new GridLayout(3,0)));
	radioPanelMode.setBorder(BorderFactory.createTitledBorder("Select Clipping Visualization Mode:"));
	optionGroupMode = new ButtonGroup();
	radioButtonMode = new JRadioButton[btnTextMode.length];
	for(int i=0;i<btnTextMode.length;i++){
	  radioButtonMode[i]= new JRadioButton(btnTextMode[i]);
	  radioButtonMode[i].addItemListener(this);
	  radioButtonMode[i].addMouseMotionListener(this);
	  optionGroupMode.add(radioButtonMode[i]);
	  radioPanelMode.add(radioButtonMode[i]);
	}

	p.add(radioPanelMode,BorderLayout.EAST);
//Exercise ComboBox
	exerciseList = new JComboBox(exerciseOptions);
	exerciseList.setSelectedIndex(0);
	exerciseList.addActionListener(this);
//Choose polygon
	radioPanelPolygon = new JPanel((new GridLayout(3,0)));
	radioPanelPolygon.setBorder(BorderFactory.createTitledBorder("Select polygon:"));
	optionGroupPolygon = new ButtonGroup();
	radioButtonPolygon = new JRadioButton[btnTextPolygon.length];
	for(int i=0;i<btnTextPolygon.length;i++){
	  radioButtonPolygon[i]= new JRadioButton(btnTextPolygon[i]);
	  radioButtonPolygon[i].addItemListener(this);
	  radioButtonPolygon[i].addMouseMotionListener(this);
	  optionGroupPolygon.add(radioButtonPolygon[i]);
	  radioPanelPolygon.add(radioButtonPolygon[i]);
	}
	radioButtonPolygon[0].setSelected(true);
	radioButtonMode[0].setSelected(true);
	radioPanelPolygon.add(undo);
	undo.addActionListener(this);
	undo.addMouseMotionListener(this);
	//repaint();
	textY = radioPanelPolygon.getHeight() + radioPanelMode.getHeight() + 20;

	radioPanelMode.add(exerciseList,BorderLayout.SOUTH);
	panel.add(radioPanelPolygon,BorderLayout.SOUTH);
    }

    public void paint(Graphics g){
        super.paint(g);
        comp2D = (Graphics2D) g;
	comp2D.setFont(new Font("",Font.BOLD,15));
	if(!standard){
	  comp2D.drawString("Drawing Area",25,250);
	  //g.drawLine(125,260,125,700);
	  //g.drawLine(575,260,575,700);
	}
	g.setColor(Color.red);
	if(cpX.size()>1){
	    cpSides=cpClicks;
	    for(int i=0;i<cpClicks;i++){
	      g.drawLine(cpX.get(i), cpY.get(i), cpX.get((i+1)%cpSides),cpY.get((i+1)%cpSides));
	      if(cpX.size()>2)
		comp2D.drawString("E"+i,(cpX.get(i)+cpX.get((i+1)%cpSides))/2,(cpY.get(i)+cpY.get((i+1)%cpSides))/2);
	    }
	}else if(cpX.size() == 1){
	  g.drawOval(cpX.get(0), cpY.get(0),2,2);
	}

	g.setColor(Color.blue);
	if(spX.size()>1){
	    spSides=spClicks;
	    for(int i=0;i<spClicks;i++){
	      g.drawLine(spX.get(i), spY.get(i), spX.get((i+1)%spSides),spY.get((i+1)%spSides));
	    }
	}else if(spX.size() == 1){
	  g.drawOval(spX.get(0), spY.get(0),2,2);
	}
	try{
	  if(dragged){
	    if(clippingPolygon){
	      cpClicks--;
	      cpX.remove(cpClicks);
	      cpY.remove(cpClicks);
	    }else{
	      spClicks--;
	      spX.remove(spClicks);
	      spY.remove(spClicks);
	    }
	  }
	}catch(NullPointerException e){}
      }

    public void mouseClicked(MouseEvent key){

    }
    public void mousePressed(MouseEvent mouse)
    {
	if(mouse.getY()>250 && mouse.getY()<700)
	  inClickingArea=true;
	else
	  inClickingArea=false;

        switch (mouse.getButton()) {
            case MouseEvent.BUTTON1:
                break;
            case MouseEvent.BUTTON2:
                break;
            case MouseEvent.BUTTON3:
                break;
        }
    }
    /**
     * Method called when a mouse click is released,
     * Used to calculate polygon vertices
     */
    public void mouseReleased(MouseEvent mouse){
        //System.out.println(mouse.getX() + "," + mouse.getY());
	if(!standard && inClickingArea){
	  if(clippingPolygon && cpClicks<8){
	    cpX.add(mouse.getX());
	    cpY.add(mouse.getY());
	    cpClicks++;
	    cpSides=8;
	  }
	  else if(spClicks<8 && !clippingPolygon){
	    spX.add(mouse.getX());
	    spY.add(mouse.getY());
	    spClicks++;
	    spSides=8;
	  }

	}
	dragged=false;
	repaint();
    }
    public void mouseEntered(MouseEvent mouse){
	inClickingArea=true;
    }

    public void mouseExited(MouseEvent mouse){
	inClickingArea=false;
    }

    public void mouseDragged(MouseEvent mouse){
      dragged=true;
      if((cpClicks+spClicks)>0){
	if(!standard && inClickingArea){
	  if(clippingPolygon && cpClicks<8){
	    cpX.add(cpClicks,mouse.getX());
	    cpY.add(cpClicks,mouse.getY());
	    cpClicks++;
	    cpSides=8;
	  }
	  else if(spClicks<8 && !clippingPolygon){
	    spX.add(spClicks,mouse.getX());
	    spY.add(spClicks,mouse.getY());
	    spClicks++;
	    spSides=8;
	  }
	  repaint();
      }
      }
    }
    public void mouseMoved(MouseEvent mouse){



    }

    public void actionPerformed(ActionEvent event){
	System.out.println("This is button event " + event.getSource());
	if(event.getActionCommand()=="Undo"){
	    if(clippingPolygon && cpClicks!=0){
	    cpX.remove(cpX.size()-1);
	    cpY.remove(cpY.size()-1);
	    cpSides=cpY.size();
	    cpClicks--;
	   }else if(spClicks!=0){
	    spX.remove(spX.size()-1);
	    spY.remove(spY.size()-1);
	    spSides=spY.size();
	    spClicks--;
	   }
	}
	if(event.getSource().toString().indexOf("JComboBox")!=-1){
	  exercise = Integer.parseInt(event.getSource().toString().substring(event.getSource().toString().indexOf(". Give")-1,
		      event.getSource().toString().indexOf(". Give")));
	  System.out.println("exercise " + exercise);
	}
	repaint();
    }

    public void itemStateChanged(ItemEvent event){
	//System.out.println("This is event " + event);
	standard = radioButtonMode[0].isSelected();

        if(radioButtonMode[1].isSelected()){
	  //radioPanelMode.add(exerciseList,BorderLayout.SOUTH);
	  //panel.add(radioPanelPolygon,BorderLayout.SOUTH);
        }
	clippingPolygon = radioButtonPolygon[0].isSelected();

    }

    public String getText(){

      int minX=Integer.MAX_VALUE;
      int maxX=Integer.MIN_VALUE;
      int minY=Integer.MAX_VALUE;
      int maxY=Integer.MIN_VALUE;

      for(int i=0;i<cpX.size();++i){
	if(minX > cpX.get(i)) {
	  minX = cpX.get(i);
	}
	if(maxX < cpX.get(i)) {
	  maxX = cpX.get(i);
	}
      }

      for(int i=0;i<cpY.size();++i){
	if(minY > cpY.get(i)) {
	  minY = cpY.get(i);
	}
	if(maxY < cpY.get(i)) {
	  maxY = cpY.get(i);
	}
      }

      for(int i=0;i<spX.size();++i){
	if(minX > spX.get(i)) {
	  minX = spX.get(i);
	}
	if(maxX < spX.get(i)) {
	  maxX = spX.get(i);
	}
      }

      for(int i=0;i<spY.size();++i){
	if(minY > spY.get(i)) {
	  minY = spY.get(i);
	}
	if(maxY < spY.get(i)) {
	  maxY = spY.get(i);
	}
      }

      maxX=maxX-minX;
      maxY=maxY-minY;
      if(standard){
	 return "0";
      }
      else {
	 text = "1 " + cpClicks + " ";
	 for(int i = 0;i<cpClicks;++i){
	    text += String.format("%.5g",0.1+0.8*(cpX.get(i)-minX)/(double)(maxX)) +
		  " " + String.format("%.5g",0.1+(1-(cpY.get(i)-minY)/(double)(maxY))*0.8) + " ";
	 }
	 text+=spClicks+" ";
	 for(int i = 0;i<spClicks;++i){
	    text += String.format("%.5g",0.1+0.8*(spX.get(i)-minX)/(double)(maxX)) +
		  " " + String.format("%.5g",0.1+(1-(spY.get(i)-minY)/(double)(maxY))*0.8) + " ";
	 }
	 text+= exercise + " ";
	 switch(exercise){
	    case 1:
		text+=exerciseTokens[0][0] + " " + exerciseTokens[0][1];
	      break;
	    case 2:
		text+=exerciseTokens[1][0] + " " + (exerciseTokens[1][1].equals("created") ? "true":"false");
	      break;
	    case 3:
		text+=exerciseTokens[2][0];
	      break;
	    case 4:
		text+=exerciseTokens[3][0]+ " " + exerciseTokens[3][1];
	      break;
	    case 5:
		text+=exerciseTokens[4][0]+ " " +exerciseTokens[4][1] + " " + exerciseTokens[4][2];
	      break;
	    case 6:
		text+=exerciseTokens[5][0]+ " " +exerciseTokens[5][1] + " " + exerciseTokens[5][2];
	      break;
	    case 7:
		text+=exerciseTokens[6][0]+ " " +exerciseTokens[6][1];
	      break;
	 }
	 return text;
      }
    }

    public void setExersices(){
      Random rand = new Random(System.currentTimeMillis());
      exerciseArgs = new String[7];
      String [] temp1,temp2;
      int tmp=0,prv=0,counter2=0,counter4=0;
      String e6tmp="",tmp2="";
      //String [][] exerciseTokens = new String[7][];
      exerciseArgs[0]=(rand.nextInt(4)+1) + " " + (rand.nextInt(11)+10); //Exercise 1
      exerciseTokens[0] = exerciseArgs[0].split("\\s+");
      tmp = rand.nextInt(5);
      exerciseArgs[1]=(tmp) + " " + ((tmp == 1 || rand.nextInt(2) == 0) ? "deleted":"created"); //Exercise 2
      exerciseTokens[1] = exerciseArgs[1].split("\\s+");
      exerciseArgs[2]=(rand.nextInt(4)+4) + " "; //Exercise 3
      exerciseTokens[2] = exerciseArgs[2].split("\\s+");
      exerciseArgs[3]=(rand.nextInt(8)) + " " + (rand.nextInt(2)+1); //Exercise 4
      exerciseTokens[3] = exerciseArgs[3].split("\\s+");
      exerciseArgs[4]=(rand.nextInt(8)) + "," + (rand.nextInt(6)+2) + ","; //Exercise 5
      temp1 = exerciseArgs[4].split(",");
      for(int i=0;i<Integer.parseInt(temp1[1]);++i){
	      tmp = rand.nextInt(4)+1;
	      System.out.println("prv = " + prv + "tmp = " + tmp);
	      if((prv==1 && tmp==4)||(prv==1 && tmp==3)||(prv==2 && tmp==1)||(prv==2 && tmp==2)||(prv==3 && tmp==1)
	          ||(prv==3 && tmp==2)||(prv==4 && tmp==3)||(prv==4 && tmp==4))
	      {
	        i--;
	      }else{
	        exerciseArgs[4]+=tmp+" ";
          tmp2+=tmp+" ";
	        prv = tmp;
	      }
      }
      e6tmp=tmp2;
      while(e6tmp.indexOf("4") != -1){
        e6tmp=e6tmp.substring(e6tmp.indexOf("4")+1,e6tmp.length());
        counter4++;
      }

      e6tmp=tmp2;
      while(e6tmp.indexOf("2") != -1){
        e6tmp=e6tmp.substring(e6tmp.indexOf("2")+1,e6tmp.length());
        counter2++;
      }


      int valuebecausejhaveistrashingit = Integer.parseInt(temp1[1]) + 1;
      System.out.println("c2 " + counter2 +" c4 " + counter4 + " size " + temp1[1] + " vbjiti " +
            valuebecausejhaveistrashingit);
      temp1[1]=","+temp1[1];
      tmp2=","+valuebecausejhaveistrashingit;
      if(counter2>counter4){
        exerciseArgs[4]+="4 ";
        exerciseArgs[4]=exerciseArgs[4].replaceFirst((temp1[1]),tmp2);
      }
      else if(counter2<counter4){
        exerciseArgs[4]+="2 ";
        exerciseArgs[4]=exerciseArgs[4].replaceFirst((temp1[1]),tmp2);
      }
      System.out.println(exerciseArgs[4]);
      exerciseTokens[4] = exerciseArgs[4].split(",");

      exerciseArgs[5]=(rand.nextInt(8)) + "," + (rand.nextInt(4)+1) + ","; //Exercise 6
      temp2 = exerciseArgs[5].split(",");
      prv = Integer.parseInt(temp2[1]);
      ArrayList<Integer> vals = new ArrayList<Integer>();
      for(int i=0;i<prv;++i){
	      int val = rand.nextInt(4) + 1;
        if(!vals.contains(val)) {
          vals.add(val);
        } else {
          --i;
        }
      }


      valuebecausejhaveistrashingit = Integer.parseInt(temp2[1]) + 1;
      temp2[1]=","+temp2[1];
      tmp2=","+valuebecausejhaveistrashingit;


      if(vals.contains(2) && !vals.contains(4)) {
        vals.add(4);
        exerciseArgs[5]=exerciseArgs[5].replaceFirst((temp2[1]),tmp2);
      }
      if(vals.contains(4) && !vals.contains(2)) {
        vals.add(2);
        exerciseArgs[5]=exerciseArgs[5].replaceFirst((temp2[1]),tmp2);
      }
      TreeSet<Integer> tree = new TreeSet<Integer>(vals);
      Iterator iter = tree.iterator();
      vals.clear();
      while(iter.hasNext()) {
        vals.add((Integer)iter.next());
      }
      for(int i = 0; i < vals.size(); ++i) {
        exerciseArgs[5]+=vals.get(i) + " ";
      }

      exerciseTokens[5] = exerciseArgs[5].split(",");

      exerciseArgs[6]=(rand.nextInt(5)+3) + " " + (rand.nextInt(5)+3); //Exercise 7
      exerciseTokens[6] = exerciseArgs[6].split("\\s+");


      exerciseOptions = new String[7];
      exerciseOptions[0] = "1. Give a data set where case " + exerciseTokens[0][0] + " occurs exactly "
        + exerciseTokens[0][1] + " times during the entire operation of the algorithm.";
      exerciseOptions[1] = "2. Give a data set where exactly " + exerciseTokens[1][0]  +
        ((exerciseTokens[1][0].equals("1")) ? " vertex " : " vertices ") + " will be " + exerciseTokens[1][1];
      exerciseOptions[2] = "3. Give a data set where each case occurs exactly " + exerciseTokens[2][0]  + " times " +
        "during the entire operation of the algorithm.";
      exerciseOptions[3] = "4. Give a data set where for edge " + exerciseTokens[3][0] + " all of the cases will occur " +
			      exerciseTokens[3][1] + (exerciseTokens[3][1].equals("1") ? " time." : " times.");
      exerciseOptions[4] = "5. Give a data set where for edge " + exerciseTokens[4][0] + " cases " +
        exerciseTokens[4][2].trim().replace(" ", ", ") + " will occur in the specified order.";
      exerciseTokens[4][2] = exerciseTokens[4][2].trim();
      exerciseOptions[5] = "6. Give a data set where for edge " + exerciseTokens[5][0] + " only cases " +
        exerciseTokens[5][2].trim().replace(" ", ", ") + " may occur.";
      exerciseOptions[6] = "7. Give a data set where the subject polygon will go from " + exerciseTokens[6][0] +
			     " vertices before clipping to " + exerciseTokens[6][1] + " after clipping.";
    }
}