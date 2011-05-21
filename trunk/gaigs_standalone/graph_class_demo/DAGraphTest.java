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

// file: GraphTest.java
// author: Andrew Jungwirth
// date: 20 July 2005
//
// modified by TN to create a DAG instead of an arbitrary graph.

// Note that the VisualGraph.java class only supports node names of a
// single letter or digit. Also, the initial nodes and edges are
// randomly placed via the class's randomGraph method. After this
// initial graph is generated, it is organized using the Kamada
// algorithm.

// NOTE: THE FOLLWING COMMENT IS ONLY RELEVANT WITH RESPECT TO
// ANDREW'S ORIGINAL PROGRAM.  IT DOES NOT APPLY TO DAG CREATION.
// Because of the limitations of the randomGraph method, the initial
// number of edges specified should be less than the initial number of
// nodes.  This is because randomGraph apparently tries to construct a
// random disconnected graph.  If you want as many or more edges than
// nodes, then add them after the initial numbering for nodes and
// edges (as indicated in the sample input above) or change this
// program to call on VisualGraph's randomConnectedGraph method, which
// allows the number of edges to match or exceed the number of nodes.

// package exe.graphtest;

import exe.*;
import java.io.*;
import java.lang.*;
import java.util.*;

public class DAGraphTest {
    public static void main(String[] args) throws IOException {
	ShowFile out = new ShowFile(args[0]);

	GAIGSgraph graph = new GAIGSgraph(); // GAIGSgraph is
					     // essentially a wrapper
					     // class that let a
					     // VisualGraph (which
					     // GAIGSgraph extends)
					     // used the ShowFile.
					     // Almost all graph
					     // manipulation
					     // operations you will
					     // use are actually
					     // methods from the
					     // parent VisualGraph
					     // class and its VisNodes
					     // and VisEdges.
	graph.setBounds(0.0, 0.0, 1.0, 0.9);
	String key, color;
	boolean error;
	int nodes = (Integer.decode(args[1])).intValue();
	int edges = (Integer.decode(args[2])).intValue();
	int index;
	VisNode[] node_set;

	do {
	    error = false;

	    try{
		// Andrew's original call
		//		graph.randomGraph(nodes, edges, false, false, false, 1.0, 9.0);
		// I create a DAG instead -- TN
		graph.randomDAcyclicGraph(nodes, edges, false, true, 1.0, 9.0);	    }
	    catch(RuntimeException e){
		System.err.println("Problem generating random graph: " + 
				   e.toString());
		error = true;
	    }
	    if(!error){
		try{
		    graph.organizeGraph();
		}
		catch(IOException e){
		    System.err.println("Kamada layout error: " + e.toString());
		    error = true;
		}
	    }
	} while(error);

	node_set = graph.getNodes();


	out.writeSnap("New Random DAG with " + nodes + " Nodes and " +
		      edges + " Edges", graph);

	// Here you could do thing like add nodes, edges, have an
	// algorithm execute and write its tracing snapshots on the
	// graph

	// For good measure we'll write on final snapshot even though
	// nothing has really changed in this simple demo.  The call
	// to organizeGraph, which runs Kamada's algortihm to do a new
	// geometric layout of node positions, would only be
	// meaningful, if some nodes and edges had been added.  As it
	// stands we will just see the same graph twice.

	try{
	    graph.organizeGraph();
	}
	catch(IOException e){
	    System.err.println("Kamada layout error: " + e.toString());
	}

	out.writeSnap("The same Random DAG with " + nodes + " Nodes and " +
		      edges + " Edges", graph);
		
	out.close();
    }

}
//}

