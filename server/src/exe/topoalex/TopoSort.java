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

/** Creates a visual demonstration of a Topological Sort Algorithm. Usage:
 *  { java TopoSort datFile shoFile }
 *  Where datFile is the name of the .DAT file from which to read and shoFile
 *  is the name of the .SHO file to be written. The data file must contain
 *  a table of nodes in a directed graph with appropriate (x,y)-coordinates
 *  as well as a table of edges between these nodes.
 *
 *  @author Tom Naps
 *  @author Alex Zimmerman
 *  @version 2.0
 */

package exe.topoalex;

import java.io.*;
import java.util.*;
import exe.*;

/** Implementation of a node in a directed graph which is to be topologically
 *  sorted.
 *
 *  @author Tom Naps
 *  @author Alex Zimmerman
 */
class TNode {
    int ident, connCount;
    boolean added;
    float x, y;
    TNode link;

/** Constructor for the <code>TNode</code> object.
 *
 *  @param xPos         The horizontal coordinate of the node, generally between
 *                      0 and 1.
 *  @param yPos         The vertical coordinate of the node, generally between 0
 *                      and 1.
 *  @param i            A unique integer identifier for the node, used for disp-
 *                      lay purposes.
 */
    TNode(float xPos, float yPos, int i) {
        x = xPos;
        y = yPos;
        ident = i;
        added = false;
        connCount = 0;
        link = null;
    }
}

/** Implementation of an edge in a directed graph to be topologically sorted.
 *
 *  @author Tom Naps
 *  @author Alex Zimmerman
 */
class TEdge {
    TNode left;
    TNode right;

/** Constructor for the <code>TEdge</code> object.
 *
 *  @param l            The starting node of the edge, arbitrarily designated
 *                      as left.
 *  @param r            The node to which the edge connects, arbitrarily
 *                      designated as right.
 */
    TEdge(TNode l, TNode r) {
        left = l;
        right = r;
    }
}

/** Rudimentary implementation of a queue data structure for use with
 *  topological sorting.
 *
 *  @author Tom Naps
 *  @author Alex Zimmerman
 */
class Queue {
    TNode head;
    TNode tail;

/** Constructor for the <code>Queue</code> object.
 */
    Queue() {
        head = null;
        tail = null;
    }

/** Removes a node from the head of the queue.
 */
    void dequeue() {
        if(head != null) {
            int i = head.ident;
            head = head.link;
        }
    }

/** Adds a given node at the tail of the queue.
 *
 *  @param insert       The node to be added to the queue.
 */
    void enqueue(TNode insert) {
        if(head == null) {
            head = insert;
            tail = insert;
        }
        else {
            tail.link = insert;
            tail = insert;
        }
    }
}

/** Framework for topological sort demonstration.
 *
 *  @author Tom Naps
 *  @author Alex Zimmerman
 */
public class TopoSort {
    private PrintWriter out;
    private BufferedReader in;
    private String s;
    private StringTokenizer st;
    private questionCollection questions;
    private int qIndex = 0;

/** Initiates input and output streams.
 *
 *  @param datFile          Name of the file from which to input.
 *  @param gaigsFile        Name of the file to be written.
 *  @throws IOException     Error creating file streams.
 */
    public TopoSort(String datFile, String gaigsFile) {
        try {
            s = "";
            in = new BufferedReader(new FileReader(datFile));
            out = new PrintWriter(new BufferedWriter(new FileWriter(gaigsFile)));
            questions = new questionCollection(out);
            st = new StringTokenizer(s);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

/** Reads data from input file and calls appropriate procedures to create
 *  topological sort demonstration. The format of the file to input must be
 *  as follows:
 *  ----------------------------------------
 *  # of nodes   # of connections
 *  Gaigs
 *  1   x-coord of node 1   y-coord of node 1
 *  ...
 *  n   x-coord of node n   y-coord of node n
 *  left node of connection 1   right node of connection 1   edge weight
 *  [edge weight is ignored here, but should always be 1]
 *  ...
 *  left node of connection m   right node of connection m   edge weight
 *  ----------------------------------------
 *
 *  @param args             Array of command line arguments.
 *  @throws IOException     Error manipulating file streams.
 */
    public static void main(String[] args) {
        try {
            String datFile = args[0];
            String gaigsFile = args[1];
            float xPos, yPos;
            int node1, node2, weight;
            TopoSort graph = new TopoSort(datFile, gaigsFile);
            graph.s = graph.in.readLine();
            graph.st = new StringTokenizer(graph.s);
            int nodeNum = Integer.parseInt(graph.st.nextToken());
            int connNum = Integer.parseInt(graph.st.nextToken());
            TNode[] nodeArray = new TNode[nodeNum];
            graph.s = graph.in.readLine();
            for(int i = 0; i < nodeNum; i++) {
                graph.s = graph.in.readLine();
                graph.st = new StringTokenizer(graph.s);
                xPos = Float.parseFloat(graph.st.nextToken());
                yPos = Float.parseFloat(graph.st.nextToken());
                nodeArray[i] = new TNode(xPos, yPos, i + 1);
            }
            TEdge[] connArray = new TEdge[connNum];
            for(int j = 0; j < connNum; j++) {
                graph.s = graph.in.readLine();
                graph.st = new StringTokenizer(graph.s);
                node1 = Integer.parseInt(graph.st.nextToken());
                node2 = Integer.parseInt(graph.st.nextToken());
                weight = Integer.parseInt(graph.st.nextToken());
                connArray[j] = new TEdge(nodeArray[node1 - 1], nodeArray[node2 - 1]);
            }
            graph.in.close();
            Queue list = new Queue();
            for(int n = 0; n < nodeNum; n++) {
                for(int m = 0; m < connNum; m++) {
                    if(connArray[m].right == nodeArray[n])
                            nodeArray[n].connCount++;
                }
                if(nodeArray[n].connCount == 0) {
                    list.enqueue(nodeArray[n]);
                    nodeArray[n].connCount = -1;
                }
            }
            graph.sort(nodeArray, connArray, list);
            graph.questions.writeQuestionsAtEOSF();
            graph.out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

/** Performs the topological sort, using queues and the data fields of the
 *  <code>TNode</code> objects. Note that a <code>connCount</code> of 0
 *  means a node has no further connections coming in and will be added to
 *  the queue the next time around. Once a node has been added to the queue,
 *  its <code>connCount</code> is changed to -1. The boolean field <code>added</code>
 *  means added to the topological ordering, not the queue.
 *
 *  @param nodeArray        Array of nodes to be added to the ordering.
 *  @param connArray        Array of directed edges connecting all the nodes
 *                          in <code>nodeArray</code>.
 *  @param list             A queue containing all the nodes which have no
 *                          connections into them.
 */
    void sort(TNode[] nodeArray, TEdge[] connArray, Queue list) {
        Queue topoOrder = new Queue();
        printGraph(nodeArray, connArray, list, topoOrder, 0);
        while(list.head != null) {
            for(int j = 0; j < nodeArray.length; j++) {
                for(int k = 0; k < connArray.length; k++) {
                    if(connArray[k].left == list.head && connArray[k].right == nodeArray[j])
                        nodeArray[j].connCount--;
                }
            }
            printGraph(nodeArray, connArray, list, topoOrder, 1);
            list.head.added = true;
            topoOrder.enqueue(list.head);
            list.dequeue();
            for(int m = 0; m < nodeArray.length; m++) {
                if(nodeArray[m].connCount == 0) {
                    list.enqueue(nodeArray[m]);
                    nodeArray[m].connCount = -1;
                }
            }
        }
        printGraph(nodeArray, connArray, list, topoOrder, 2);
    }

/** Creates a snapshot of the topological ordering.
 *
 *  @param nodeArray        Array of nodes in the graph.
 *  @param connArray        Array of directed edges in the graph.
 *  @param list             The queue of nodes to be added to the topological
 *                          ordering
 *  @param topoOrder        A queue representing all the nodes currently in-
 *                          cluded in the topological ordering.
 *  @param qType            An integer specifying the format of the answer
 *                          to the question associated with this snapshot:
 *                          0 == Answer is all the nodes in <code>list</code>.
 *                          1 -- Answer is all the nodes with a <code>connCount</code>
 *                               of 0.
 *                          2 -- No question is to be asked.
 */
    void printGraph(TNode[] nodeArray, TEdge[] connArray, 
                    Queue list, Queue topoOrder, int qType) {
        out.println("VIEW DOCS topo.htm");
        if(qType != 2) {
            fibQuestion q = new fibQuestion(out, (new Integer(qIndex)).toString());
            //q.setQuestionText("Which nodes will be added to the queue next?");
            String answer = "";
            if(qType == 0) {
		q.setQuestionText("Which nodes will be added to the queue initially?");
                answer = "" + list.head.ident;
                TNode next = list.head;
                while(next != list.tail) {
                    next = next.link;
                    answer += " " + next.ident;
                }
            }
            else if(qType == 1) {
		q.setQuestionText("Which nodes will be added to the queue next? (Enter \"none\" if none)");
                for(int j = 0; j < nodeArray.length; j++) {
                    if(nodeArray[j].connCount == 0) {
                        if(answer == "")
                            answer = "" + nodeArray[j].ident;
                        else
                            answer += " " + nodeArray[j].ident;
                    }
                }
            }
            if(answer == "")
                answer = "None";
            q.setAnswer("" + answer);
            if(answer == "None") {
                q.setAnswer("none");
                q.setAnswer("NONE");
            }
            questions.addQuestion(q);
            questions.insertQuestion(qIndex);
            qIndex++;
        }
        out.println("Graph");
        out.println("1");
        out.print("Topo order:");
        String topo = "";
        TNode next1 = topoOrder.head;
        while(next1 != topoOrder.tail) {
            topo += " " + next1.ident;
            next1 = next1.link;
        }
        if(next1 != null)
            topo += " " + topoOrder.tail.ident;
        if(topo == "")
            topo = " Empty";
        out.println(topo);
        out.print("Queue:");
        String queue = "";
        if(qType != 0) {
            TNode next2 = list.head;
            while(next2 != null) {
                queue += " " + next2.ident;
                next2 = next2.link;
            }
        }
        if(queue == "")
            queue = " Empty";
        out.println(queue);
        out.println("***\\***");
        for(int n = 0; n < nodeArray.length; n++) {
            out.println("" + nodeArray[n].ident + " " + nodeArray[n].x + " " + nodeArray[n].y);
            for(int m = 0; m < connArray.length; m++) {
                if(connArray[m].left == nodeArray[n]) {
                    out.print("\\A");
                    if(nodeArray[n].added)
                        out.print("\\R");
                    out.println("" + connArray[m].right.ident);
                }
            }
            out.println("32767");
            if(nodeArray[n].added)
                out.print("\\G");
            else if(qType != 0) {
                if(nodeArray[n].connCount == -1)
                    out.print("\\Y");
            }
            out.println("" + nodeArray[n].ident);
        }
        out.println("***^***");
    }
}
