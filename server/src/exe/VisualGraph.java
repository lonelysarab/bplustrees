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

 
package exe;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class is a tool for storing and manipulating graphs. A 
 * <code>VisualGraph</code> is composed of vertices of type 
 * <code>VisNode</code> and edges of type <code>VisEdge</code>. Methods are 
 * provided for creating specific types of graphs, manipulating these graphs to
 * produce algorithm visualizations, and writing these graphs to the showfiles 
 * used by the GAIGS visualizer.
 * <p>
 * Each vertex in a <code>VisualGraph</code> can have any unique alphanumeric
 * (A-Z, a-z, 0-9) value as its label. Therefore, the maximum number of nodes
 * in a <code>VisualGraph</code> is 62, which results in a maximum of 3844
 * edges. If the <code>VisualGraph</code> is a heuristic graph, the heuristic
 * value for each vertex will be printed beneath the node's label when it is
 * displayed in the GAIGS visualizer.
 * </p>
 * <p>
 * In order to use any of these graph classes in a script-producing program,
 * the script-producing program must import the package <code>exe</code>.
 * </p>
 *
 * @author Jeff Lucas (original author)
 * @author Dr. Tom Naps (node enumeration methods)
 * @author Richard Teviotdale (GAIGS adaptations and additions)
 * @author Andrew Jungwirth (more GAIGS adaptations and Javadoc comments)
 * @author Myles McNally (changed access status of some instance variables)
 */
public class VisualGraph{
    /* STATIC DATA: */

    /**
     * Stores the maximum number of nodes for this <code>VisualGraph</code>.
     */
    protected static final int MAX_NODES = 62;

    /*
     * Stores the maximum number of edges for this <code>VisualGraph</code>.
     */
//    private static final int NODE_SIZE = 15;			// never used

    /**
     * Contains a limit for Kamada iterations when organizing the layout of 
     * this <code>VisualGraph</code>.
     */
    private static final int ITERLIM1 = 600;

    /**
     * Contains a limit for Kamada iterations when organizing the layout of 
     * this <code>VisualGraph</code>.
     */
    private static final int ITERLIM2 = 60; 

    /**
     * Stores an epsilon value for use by Kamada when organizing the layout of
     * this <code>VisualGraph</code>.
     */
    private static final double EP1 = 0.00001;

    /**
     * Stores an epsilon value for use by Kamada when organizing the layout of
     * this <code>VisualGraph</code>.
     */
    private static final double EP2 = 0.000001;

    /** 
     * Contains a constant for Kamada to use when organizing the layout of
     * this <code>VisualGraph</code>.
     */
    private static final int K = 1;

    /**
     * Stores a big number required by Kamada when organizing the layout of
     * this <code>VisualGraph</code>.
     */
    private static final int BIG_NUM = Integer.MAX_VALUE / 2 - 1;

    /**
     * Stores the minimum distance allowed between nodes and edges in this
     * <code>VisualGraph</code>.
     */
    private static final double NODE_TOEDGE_MIN = 0.03;

    /**
     * Stores the minimum distance allowed between edge centers in this 
     * <code>VisualGraph</code>.
     */
    private static final double EDGE_CENTER_MIN = 0.03;

    /**
     * Stores the minimum length for edges in this <code>VisualGraph</code>.
     */
    private static final double EDGE_LENGTH_MIN = 0.03;

    /* DATA: */

    /**
     * Maintains the list of all possible nodes in this 
     * <code>VisualGraph</code>.
     * Initialized with all <code>VisNode</code> objects in the default,
     * unactivated state. See default <code>VisNode</code> constructor for
     * details.
     */
    protected VisNode[] my_nodeset;

    /**
     * Stores the list of all possible edges in this <code>VisualGraph</code>.
     * Initialized with all <code>VisEdge</code> objects in the default,
     * unactivated state. See default <code>VisEdge</code> constructor for
     * details.
     */
    protected VisEdge[][] my_edgeset;

    /**
     * Tracks the current number of activated nodes in this
     * <code>VisualGraph</code>.
     * Note that these activated nodes may not be consecutive within
     * <code>my_nodeset</code>.
     */
    protected int num_nodes;

    /**
     * Contains the current number of activated edges in this 
     * <code>VisualGraph</code>.
     */
    protected int num_edges;

    /**
     * Stores the left-most x-bound for this <code>VisualGraph</code> within
     * the normalized [0,1] space. Defaults to <code>0.0</code> if no bounds
     * are set. The bounds variables can be used to localize this
     * <code>VisualGraph</code> in a section of the drawing window in the new
     * GAIGS XML format to allow multiple structures to appear in the same
     * snapshot.     */
    protected double x1 = 0.0;

    /**
     * Stores the lower y-bound for this <code>VisualGraph</code> within the 
     * normalized [0,1] space. Defaults to <code>0.0</code> if no bounds are
     * set. The bounds variables can be used to localize this
     * <code>VisualGraph</code> in a section of the drawing window in the new
     * GAIGS XML format to allow multiple structures to appear in the same
     * snapshot.
     */
    protected double y1 = 0.0;

    /**
     * Stores the right-most x-bound for this <code>VisualGraph</code> within
     * the normalized [0,1] space. Defaults to <code>1.0</code> if no bounds
     * are set. The bounds variables can be used to localize this
     * <code>VisualGraph</code> in a section of the drawing window in the new
     * GAIGS XML format to allow multiple structures to appear in the same
     * snapshot.
     */
    protected double x2 = 1.0;

    /**
     * Stores the upper y-bound for this <code>VisualGraph</code> within the
     * normalized [0,1] space. Defaults to <code>1.0</code> if no bounds are 
     * set. The bounds variables can be used to localize this 
     * <code>VisualGraph</code> in a section of the drawing window in the new
     * GAIGS XML format to allow multiple structures to appear in the same
     * snapshot.
     */
    protected double y2 = 1.0;

    /**
     * Holds the minimum font size for this <code>VisualGraph</code> when drawn
     * in the new GAIGS XML format. Used to specify a font size that will be
     * readable when this <code>VisualGraph</code> is drawn in a smaller
     * portion of the screen.
     */
    protected double font_size = 0.03;

    /**
     * Maintains whether this <code>VisualGraph</code> has weighted or 
     * unweighted edges.
     * A value of <code>true</code> means that the edges are labeled with
     * weights, and <code>false</code> indicates they do not have labels.
     */
    protected boolean weighted = false;

    /**
     * Tracks whether this <code>VisualGraph</code> has directed edges or
     * if all edges are undirected.
     * A value of <code>true</code> means that there is at least one directed
     * edge, and <code>false</code> indicates that all edges are bidirectional.
     */
    protected boolean directed = false;

    /**
     * Maitains whether this <code>VisualGraph</code> contains heuristic values
     * or whether it has no heuristic information.
     * A value of <code>true</code> means heuristics are used, and
     * <code>false</code> indicates there are no heuristic values.
     */
    protected boolean heuristics = false;

    /* METHODS: */

    /**
     * Constucts a <code>VisualGraph</code> with default information.
     */
    public VisualGraph(){
    initializeGraph();
    }

    /**
     * Constructs a <code>VisualGraph</code> by specifying whether or not it
     * has edge weights, directed edges, and heuristic values for nodes.
     * The bounds and font size are assigned default values and must be set via
     * separate method calls.
     *
     * @param weighted   Indicates if this <code>VisualGraph</code> has
     *                   weighted edges. A value of <code>true</code> means 
     *                   that the edges have weights that are displayed near
     *                   the edges; <code>false</code> indicates that no
     *                   edge weights are present.
     * @param directed   Specifies whether the edges of this
     *                   <code>VisualGraph</code> are directed or
     *                   bidirectional. A value of <code>true</code> indicates
     *                   directed edges, and <code>false</code> means that all
     *                   edges are bidirectional.
     * @param heuristics Indicates if this <code>VisualGraph</code> contains
     *                   heuristic values. A value of <code>true</code> means
     *                   that the nodes have heuristic values that are
     *                   displayed under the node labels; <code>false</code>
     *                   indicates that no heuristic values should be shown.
     */
    public VisualGraph(boolean weighted, boolean directed, boolean heuristics){
    this.weighted = weighted;
    this.directed = directed;
    this.heuristics = heuristics;
    initializeGraph();
    }

    /**
     * Initializes <code>my_nodeset</code> and <code>my_edgeset</code> so that
     * they contain default, unactivated nodes/edges and clears all data
     * stored within this <code>VisualGraph</code> to their default values.
     */
    private void initializeGraph(){
    my_nodeset = new VisNode[MAX_NODES];
    my_edgeset = new VisEdge[MAX_NODES][MAX_NODES];
    for(int x = 0; x < MAX_NODES; x++){
        my_nodeset[x] = new VisNode();
        for(int y = 0; y < MAX_NODES; y++)
        my_edgeset[x][y] = new VisEdge();
    }
    clearGraph();
    }

    /** 
     * Clears this <code>VisualGraph</code> by returning the data for all nodes
     * and edges to their default values.
     */
    public void clearGraph(){
        //Go through activated members of the class
        for(int start = 0; start < MAX_NODES; start++){
        //Clear all activated nodes and deactivate them
        if(my_nodeset[start].isActivated()){
        my_nodeset[start].clearNode();
        my_nodeset[start].deactivate();
        }
        
        //Clear all activated edges and deactivate them
        for(int end = 0; end < MAX_NODES; end++)
        if(my_edgeset[start][end].isActivated()){
            my_edgeset[start][end].clearEdge();
            my_edgeset[start][end].deactivate();
        }            
    }
    
        //Set the number of nodes and edges to zero.
        num_nodes = num_edges = 0;
    
        //Since the graph is clear, we set weighted to false (default)
        //      and directed to false (default).  Also, isChanged
        //      is set to false for now; we need no redesign.
        weighted = directed = heuristics = false;
    }

    /**
     * Sets the bounds in which this <code>VisualGraph</code> is to be drawn.
     * These bounds specify the area in the normalized [0,1] space within which
     * this <code>VisualGraph</code> should appear in the snapshot. Used to
     * draw this <code>VisualGraph</code> in a portion of the viewing window so
     * that multiple structures can appear in a snapshot in the new GAIGS XML
     * format.
     *
     * @param x1 Sets the left-most bound on the horizontal axis.
     * @param y1 Sets the lower bound on the vertical axis.
     * @param x2 Sets the right-most bound on the horizontal axis.
     * @param y2 Sets the upper bound on the vertical axis.
     */
    public void setBounds(double x1, double y1, double x2, double y2){
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    }

    /**
     * Sets the minimum font size to keep the fonts in this
     * <code>VisualGraph</code> readable. Used to upsize the font when drawing
     * this <code>VisualGraph</code> in a portion of the viewing window in the
     * new GAIGS XML format.
     *
     * @param size Indicates the minimum font size for the fonts in this 
     *             <code>VisualGraph</code>.
     */
    public void setFontSize(double size){
    font_size = size;
    }

    /**
     * Specifies that this <code>VisualGraph</code> has weighted edges.
     * After calling this method, edges are labeled with their weights when 
     * this <code>VisualGraph</code> is written to a showfile.
     */ 
    public void setWeighted(){ weighted = true; }

    /**
     * Specifies that this <code>VisualGraph</code> has unweighted edges.
     * After calling this method, edges will not have labels when this 
     * <code>VisualGraph</code> is written to a showfile.
     */
    public void setUnweighted(){ weighted = false; }

    /**
     * Indicates that this <code>VisualGraph</code> has at least one directed
     * edge.
     * <p>
     * If there are already edges activated in this <code>VisualGraph</code>,
     * this method returns without doing anything.
     * </p>
     */
    public void setDirected(){
        //We cannot possibly change the directedness of a graph with edges
        if(num_edges > 0) return;

        directed = true;
    }

    /**
     * Indicates that this <code>VisualGraph</code> has only bidirectional 
     * edges.
     * Note that, after calling this method, this <code>VisualGraph</code> can
     * have no directed edges.
     * <p>
     * If there are already edges activated in this <code>VisualGraph</code>,
     * this method returns without doing anything.
     * </p>
     */
    public void setUndirected(){
        //We cannot possibly change the directedness of a graph with edges
        if(num_edges > 0) return;

        directed = false;
    }

    /** 
     * Sets whether this <code>VisualGraph</code> uses or does not use
     * heuristic values.
     * 
     * @param h Indicates if heuristic values are used. A value of 
     *          <code>true</code> means that heuristic values are displayed
     *          under the node labels, and <code>false</code> causes no
     *          heuristic values to be shown.
     */
    public void setHeuristics(boolean h){
    heuristics = h;
    }

    /**
     * Tells if this <code>VisualGraph</code> has weighted or unweighted edges.
     *
     * @return Gives <code>true</code> if the edges are displayed with weights
     *         and <code>false</code> if the edges are shown without weights
     */
    public boolean isWeighted(){ return weighted; }

    /**
     * Tells if this <code>VisualGraph</code> has directed or bidirectional
     * edges.
     *
     * @return Gives <code>true</code> if there is at least one directed edge
     *         in this <code>VisualGraph</code> and false if all edges are
     *         bidirectional.
     */
    public boolean isDirected(){ return directed; }

    /**
     * Indicates if this <code>VisualGraph</code> contains or does not contain
     * heuristic values.
     *
     * @return Yields <code>true</code> if heuristic values are shown under the
     *         node labels and <code>false</code> if no heuristic values are
     *         displayed.
     */
    public boolean hasHeuristics(){
    return heuristics;
    }

    /**
     * Returns the number of nodes that are currently activated in this
     * <code>VisualGraph</code>.
     * Note that these activated nodes do not necessarily occupy consecutive
     * indices within <code>my_nodeset</code>, the list of all possible nodes
     * in the graph.
     *
     * @return Gives the number of activated nodes in this
     *         <code>VisualGraph</code>.
     */
    public int getNumNodes(){ return num_nodes; }

    /**
     * Returns the number of edges that are currently activated in this
     * <code>VisualGraph</code>.
     * 
     * @return Gives the number of activated edges in this
     *         <code>VisualGraph</code>.
     */
    public int getNumEdges(){ return num_edges; }

    /**
     * Returns <code>my_nodeset</code> so that a script-producing program can
     * access the <code>VisNode</code> objects that comprise this 
     * <code>VisualGraph</code>.
     * This allows convenient and efficient manipulation of the coloring and
     * other attributes of the nodes so that the showfile produced by this
     * <code>VisualGraph</code> matches the state of the script-producing
     * program.
     *
     * @return Gives a reference to <code>my_nodeset</code>.
     */ 
    public VisNode[] getNodes(){
    return my_nodeset;
    }

    /**
     * Returns <code>my_edgeset</code> so that a script-producing program can
     * access the <code>VisEdge</code> objects that comprise this 
     * <code>VisualGraph</code>.
     * This allows convenient and efficient manipulation of the coloring and
     * other attributes of the edges so that the showfile produced by this
     * <code>VisualGraph</code> matches the state of the script-producing 
     * program.
     *
     * @return Gives a reference to <code>my_edgeset</code>.
     */
    public VisEdge[][] getEdges(){
    return my_edgeset;
    }

    /**
     * Adds a new node to this <code>VisualGraph</code> by specifying its
     * label, coordinates, and color.
     * The indicated node in the graph is activated and modified according to
     * the values given. If the node with this label is already activated, this
     * method returns without doing anything.
     *
     * @param c      Indicates which node is to be activated by giving its
     *               unique name/label. The node with this character is 
     *               activated and modified according to the other parameters.
     * @param my_x   Sets the Cartesian x-coordinate of the center of this
     *               node in [0,1] space.
     * @param my_y   Sets the Cartesian y-coordinate of the center of this
     *               node in [0,1] space.
     * @param my_col Indicates the color of this newly activated node. The 
     *               value must be a six-digit hexadecimal color 
     *               <code>String</code> of the form <code>#123456</code>. The
     *               symbol '#' must be included.
     */
    public void addNode(char c, double my_x, double my_y, String my_col){
        int my_index = translateCharIndex(c);     //Get the new node's index
    
        //If the node is already activated, we shouldn't change it.
        if(my_nodeset[my_index].isActivated()) return;

        //Set the node's color, position, highlight and character
        my_nodeset[my_index].setHexColor(my_col);
        my_nodeset[my_index].setX(my_x);
        my_nodeset[my_index].setY(my_y);
        my_nodeset[my_index].setChar(c);

        //Activate node and increase number of nodes we have
        my_nodeset[my_index].activate();
        num_nodes++;
    }

    /**
     * Adds a new node to this <code>VisualGraph</code> by specifying its
     * label and color.
     * The indicated node in the graph is activated and modified according to
     * the values given. If the node with this label is already activated, this
     * method returns without doing anything.
     *
     * @param c      Indicates which node is to be activated by giving its
     *               unique name/label. The node with this character is 
     *               activated and modified according to the other parameter.
     * @param my_col Indicates the color of this newly activated node. The 
     *               value must be a six-digit hexadecimal color 
     *               <code>String</code> of the form <code>#123456</code>. The
     *               symbol '#' must be included.
     */
    public void addNode(char c, String my_col){
        int my_index = translateCharIndex(c);     //Get the new node's index
    
    //If the node is already activated, we shouldn't change it.
        if(my_nodeset[my_index].isActivated()) return;

        //Set the node's color, character, & highlighting
        my_nodeset[my_index].setHexColor(my_col);
        my_nodeset[my_index].setChar(c);

        //Activate the node, increase the number of nodes we have, and 
        //make sure "isChanged" is true, so we determine the node's position
        my_nodeset[my_index].activate();
        num_nodes++;
    }

    /**
     * Adds a new edge to this <code>VisualGraph</code> by specifying its start
     * start node, end node, weight, and color.
     * The indicated edge in the graph is activated and modified according to
     * the values given. If the graph is directed, only the specified edge is
     * added; however, if the graph is not directed, the corresponding edge 
     * from <code>end</code> to <code>start</code> is activated as well. If the
     * edge with these start and end nodes is already activated, this method
     * returns without doing anything.
     *
     * @param start  Gives the label of the starting node for this edge.
     * @param end    Gives the label of the ending node for this edge.
     * @param weight Specifies the weight for this new edge. This value is
     *               ignored for an unweighted graph.
     * @param my_col Indicates the color of this newly activated edge. The
     *               value must be a six-digit hexadecimal color
     *               <code>String</code> of the form <code>#123456</code>. The
     *               symbol '#' must be included.
     */
    public void addEdge(char start, char end, double weight, String my_col){
        //Get the new edge's indices
        int s_ind = translateCharIndex(start);
        int e_ind = translateCharIndex(end);

        //If the edge is already activated, we shouldn't change it.
        if(my_edgeset[s_ind][e_ind].isActivated()) return;

        //Set the edge's color, weight, & hightlight;
        my_edgeset[s_ind][e_ind].setHexColor(my_col);
        my_edgeset[s_ind][e_ind].setWeight(weight);

        //Set the location for the edge ends
        my_edgeset[s_ind][e_ind].setSX(my_nodeset[s_ind].getX());
        my_edgeset[s_ind][e_ind].setSY(my_nodeset[s_ind].getY());
        my_edgeset[s_ind][e_ind].setEX(my_nodeset[e_ind].getX());
        my_edgeset[s_ind][e_ind].setEY(my_nodeset[e_ind].getY());

        //If the graph is not directed, add the opposite edge as well
        if(!directed){
        //Set the edge's color, weight, & highlight; activate!
        my_edgeset[e_ind][s_ind].setHexColor(my_col);
        my_edgeset[e_ind][s_ind].setWeight(weight);
        my_edgeset[e_ind][s_ind].activate();
        
        //Set the location for the edge ends
        my_edgeset[e_ind][s_ind].setSX(my_nodeset[e_ind].getX());
        my_edgeset[e_ind][s_ind].setSY(my_nodeset[e_ind].getY());
        my_edgeset[e_ind][s_ind].setEX(my_nodeset[s_ind].getX());
        my_edgeset[e_ind][s_ind].setEY(my_nodeset[s_ind].getY());
    }

        //Make sure "isChanged" is true, so we determine the edge's position
        //Increase the number of edges we have, activate the edge
        num_edges++;
        my_edgeset[s_ind][e_ind].activate();
    }

    /**
     * Removes a node from this <code>VisualGraph</code> by giving its label.
     * The indicated node is deactivated, along with any edges connected to it.
     * If the node is already deactivated, this method returns without doing
     * anything.
     *
     * @param c Indicates which node is to be deactivated by giving its unique
     *          name/label. This node is deactivated so that it will not be
     *          displayed.
     */     
    public void removeNode(char c){
        int index = translateCharIndex(c);  //Get the node's index

    //If the node is already deactivated, we needn't change it.
        if(!my_nodeset[index].isActivated()) return;

        my_nodeset[index].clearNode();      //Clear the node data
        my_nodeset[index].deactivate();     //Deactivate the node

        //Go through each edge that was connected to the node,
        //Clearing the data and deactivating each one!
        //Only bother with the already-activated edges!
        for(int endex = 0; endex < MAX_NODES; endex++)
            if(my_edgeset[index][endex].isActivated()){
        my_edgeset[index][endex].clearEdge();
        my_edgeset[index][endex].deactivate();

        //If the graph is not directed, we have to remove both edges
        if(!directed){
            my_edgeset[endex][index].clearEdge();
            my_edgeset[endex][index].deactivate();
        }

        //Decrease the number of edges we have
        num_edges--;
        }

        //Decrease the number of nodes we have
        num_nodes--;
    }

    /**
     * Removes an edge from this <code>VisualGraph</code> by giving its
     * starting and ending nodes.
     * The given edge is deactivated so that it will not be displayed with the
     * graph. If the graph is directed, only the specified edge is removed;
     * however, if the graph is not directed, the corresponding edge from
     * <code>end</code> to <code>start</code> is deactivated as well. If the
     * edge with these start and end nodes is already deactivated, this method
     * returns without doing anything.
     * 
     * @param start Gives the label for the starting node for this edge.
     * @param end   Gives the label for the ending node for this edge.
     */
    public void removeEdge(char start, char end){
        //Get the edge's indices
        int s_ind = translateCharIndex(start);
        int e_ind = translateCharIndex(end);

        //If the edge is already deactivated, we shouldn't change it.
        if(!my_edgeset[s_ind][e_ind].isActivated()) return;
        
        my_edgeset[s_ind][e_ind].clearEdge();   //Clear the edge
        my_edgeset[s_ind][e_ind].deactivate();  //Deactivate the edge       

        //If the graph is not directed, we have to remove both edges
        if(!directed){
        my_edgeset[e_ind][s_ind].clearEdge();
        my_edgeset[e_ind][s_ind].deactivate();
    }

        //Decrease the number of edges we have
        num_edges--;
    }

    /**
     * Modifies the color for the specified node in this 
     * <code>VisualGraph</code>. 
     * If the indicated node is not activated, this method returns without
     * doing anything.
     * 
     * @param c      Indicates the node that should be changed by giving its
     *               unique name/label.
     * @param my_col Gives the new color for this node. The value must be a 
     *               six-digit hexadecimal <code>String</code> of the form
     *               <code>#123456</code>. The symbol '#' must be included.
     */
    public void setNodeColor(char c, String my_col){
        //Get the node's index      
        int my_index = translateCharIndex(c);     

        //If the node isn't activated, we shouldn't change it.
        if(!my_nodeset[my_index].isActivated()) return;

        //Set the node's color
        my_nodeset[my_index].setHexColor(my_col);
    }

    /**
     * Modifies the color for the specified edge in this
     * <code>VisualGraph</code>.
     * If the indicated edge is not activated, this method returns without
     * doing anything.
     *
     * @param start  Indicates the starting node for the edge that should be 
     *               changed.
     * @param end    Indicates the ending node for the edge that should be 
     *               changed.
     * @param my_col Gives the new color for this edge. The value must be a 
     *               six-digit hexadecimal <code>String</code> of the form
     *               <code>#123456</code>. The symbol '#' must be included.
     */
    public void setEdgeColor(char start, char end, String my_col){
        //Get the edge's indices      
        int my_start = translateCharIndex(start);     
        int my_end = translateCharIndex(end);

        //If the edge isn't activated, we shouldn't change it.
        if(!my_edgeset[my_start][my_end].isActivated()) return;

        //Set the edge's color
        my_edgeset[my_start][my_end].setHexColor(my_col);
    }

    /**
     * Changes the edge weight for the specified edge in this 
     * <code>VisualGraph</code>.
     * If the indicated edge is not activated, this method returns without
     * doing anything.
     *
     * @param start      Indicates the starting node for the edge that should
     *                   be changed.
     * @param end        Indicates the ending node for the edge that should be
     *                   changed.
     * @param new_weight Gives the new weight for the indicated edge.
     */
    public void setEdgeWeight(char start, char end, double new_weight){
        //Get the edge's indices      
        int my_start = translateCharIndex(start);     
        int my_end = translateCharIndex(end);

        //If the edge isn't activated, we shouldn't change it.
        if(!my_edgeset[my_start][my_end].isActivated()) return;

        //Toggle the edge's bidirectionality
        my_edgeset[my_start][my_end].setWeight(new_weight);
    }

    /**
     * Changes the coordinates for the specified node in this 
     * <code>VisualGraph</code>.
     * Any edges connected to this node are adjusted so that they will still
     * properly connect to this node. If the indicated node is not activated,
     * this method returns without doing anything.
     *
     * @param index Indicates the node that should be changed by giving its
     *              unique name/label.
     * @param x     Gives the new Cartesian x-coordinate for the center of this
     *              node in [0,1] space.
     * @param y     Gives the new Cartesian y-coordinate for the center of this
     *              node in [0,1] space.
     */
    public void setNodePos(char index, double x, double y){
        //If the node isn't activated, simply return
        if(!my_nodeset[translateCharIndex(index)].isActivated()) return;

        //Otherwise, set the new position
        my_nodeset[translateCharIndex(index)].setX(x);
        my_nodeset[translateCharIndex(index)].setY(y);

        //Now, set any relevant new edge positions
        for(int z = 0; z < MAX_NODES; z++)
            if(my_nodeset[z].isActivated()){
        if(my_edgeset[translateCharIndex(index)][z].isActivated()){
            my_edgeset[translateCharIndex(index)][z].
            setSX(my_nodeset[translateCharIndex(index)].getX());
            my_edgeset[translateCharIndex(index)][z].
            setSY(my_nodeset[translateCharIndex(index)].getY());
        }
        if(my_edgeset[z][translateCharIndex(index)].isActivated()){
            my_edgeset[z][translateCharIndex(index)].
            setEX(my_nodeset[translateCharIndex(index)].getX());
            my_edgeset[z][translateCharIndex(index)].
            setEY(my_nodeset[translateCharIndex(index)].getY());
        }
        }
    }

    /** 
     * Changes the coordinates for the specified node in this
     * <code>VisualGraph</code>.
     * The coordinate values are limited to [0,1]. Therefore, if the value
     * given is greater than 1.0, it will be changed to 1.0, and, if the value
     * given is less than 0.0, it will be changed to 0.0. Any edges connected
     * to this node are adjusted so that they will still properly connect to
     * this node. If the indicated node is not activated, this method returns
     * without doing anything.
     *
     * @param index Indicates the node that should be changed by giving its 
     *              unique name/label.
     * @param x     Gives the new Cartesian x-coordinate for the center of this
     *              node in [0,1] space.
     * @param y     Gives the new Cartesian y-coordinate for the center of this
     *              node in [0,1] space.
     */
    public void setLimitedNodePos(char index, double x, double y){
        //If the node isn't activated, simply return
        if(!my_nodeset[translateCharIndex(index)].isActivated()) return;

        //Otherwise, set the new position
        my_nodeset[translateCharIndex(index)].setLimitedX(x);
        my_nodeset[translateCharIndex(index)].setLimitedY(y);

        //Now, set any relevant new edge positions
        for(int z = 0; z < MAX_NODES; z++)
            if(my_nodeset[z].isActivated()){
        if(my_edgeset[translateCharIndex(index)][z].isActivated()){
            my_edgeset[translateCharIndex(index)][z].
            setSX(my_nodeset[translateCharIndex(index)].getX());
            my_edgeset[translateCharIndex(index)][z].
            setSY(my_nodeset[translateCharIndex(index)].getY());
        }
        if(my_edgeset[z][translateCharIndex(index)].isActivated()){
            my_edgeset[z][translateCharIndex(index)].
            setEX(my_nodeset[translateCharIndex(index)].getX());
            my_edgeset[z][translateCharIndex(index)].
            setEY(my_nodeset[translateCharIndex(index)].getY());
        }
        }
    }

    /**
     * Returns the weight of the specified edge in this
     * <code>VisualGraph</code>.
     * The value returned for an unactivated edge will always be 0.0.
     *
     * @param start Indicates the starting node for the edge whose weight is to
     *              be returned.
     * @param end   Indicates the ending node for the edge whose weight is to 
     *              be returned.
     * @return      Gives the weight of the edge from <code>start</code> to
     *              <code>end</code>.
     */
    public double edgeWeight(char start, char end){
        //Get the edge's indices      
        int my_start = translateCharIndex(start);     
        int my_end = translateCharIndex(end);

        //If the edge isn't activated, it is not directed
        if(!my_edgeset[my_start][my_end].isActivated()) return 0.0;

        //Return the edge's directedness
        return my_edgeset[my_start][my_end].getWeight();
    }

    /**
     * Indicates if the specified edge is activated in this
     * <code>VisualGraph</code>.
     *
     * @param start Indicates the starting node for the edge whose existence is
     *              in question.
     * @param end   Indicates the ending node for the edge whose existence is 
     *              in question.
     * @return      Gives a value of <code>true</code> if the specified edge is
     *              activated; otherwise, <code>false</code> is returned.
     */
    public boolean edgeExists(char start, char end){
        //Get the edge's indices      
        int my_start = translateCharIndex(start);     
        int my_end = translateCharIndex(end);

        //Return whether the edge exists or not
        return my_edgeset[my_start][my_end].isActivated();
    }

    /**
     * Indicates if the specified node is activated in this
     * <code>VisualGraph</code>.
     *
     * @param c Indicates the node whose existence is in question.
     * @return  Gives a value of <code>true</code> if the specified node is
     *          activated; otherwise, <code>false</code> is returned.
     */
    public boolean nodeExists(char c){
        //Get the node's index      
        int my_index = translateCharIndex(c);     

        //Return whether the node is activated
        return my_nodeset[my_index].isActivated();
    }

    /**
     * Returns an <code>Enumeration</code> of all the nodes in this 
     * <code>VisualGraph</code>.
     *
     * @return Gives an <code>Enumeration</code> that contains all the nodes in
     *         this <code>VisualGraph</code>.
     */
    public Enumeration allNodes(){
    Vector <Character >the_nodes = new Vector <Character> (1);
    for(int i = 0; i < MAX_NODES; i++) {
        if(my_nodeset[i].isActivated())
        the_nodes.addElement(new Character(translateIndexChar(i)));
    }
    Enumeration enumeration = the_nodes.elements();
    return enumeration;
    }

    /**
     * Returns an <code>Enumeration</code> of all the nodes in this 
     * <code>VisualGraph</code> that are connected to the given node via an
     * edge.
     *
     * @param c Indicates the node for which the connecting nodes should be
     *          enumerated.
     * @return  Gives an <code>Enumeration</code> that contains all the nodes
     *          in this <code>VisualGraph</code> with an edge from
     *          <code>c</code>.
     */
    public Enumeration allAdjacentNodes(char c) {
    int index_c = translateCharIndex(c);
    Vector <Character> the_nodes = new Vector <Character> (1);
    if(my_nodeset[index_c].isActivated())
        for(int i = 0; i < MAX_NODES; i++) {
        if(my_edgeset[index_c][i].isActivated())
            the_nodes.addElement(new Character(translateIndexChar(i)));
        }
    Enumeration enumeration = the_nodes.elements();
    return enumeration;
    }

    /**
     * Indicates whether this <code>VisualGraph</code> is empty or contains
     * some data.
     * 
     * @return Yields a value of <code>true</code> if there are no nodes and no
     *         edges in this <code>VisualGraph</code>; otherwise, 
     *         <code>false</code> is returned.
     */
    public boolean empty(){ return((num_nodes == 0) && (num_edges == 0)); }

    /**
     * Indicates whether the maximum number of nodes has been reached or if
     * there are still unused nodes available in this <code>VisualGraph</code>.
     *
     * @return Yields a value of <code>true</code> if the maximum number of 
     *         nodes has been reached; otherwise, <code>false</code> is 
     *         returned.
     */
    public boolean full(){ return(num_nodes == MAX_NODES); }

    /**
     * Indicates whether the maximum number of edges has been reached or if 
     * there are still unused edges available in this <code>VisualGraph</code>.
     *
     * @return Gives a value of <code>true</code> if the maximum number of
     *         edges has been reached; otherwise, <code>false</code> is 
     *         returned.
     */
    public boolean fullEdge(){ 
        if(directed) return(num_edges == num_nodes * num_nodes); 
        else return(num_edges == 
            (num_nodes * num_nodes - num_nodes) / 2 + num_nodes);
    }

    /*************************************************************************/
    /**                           KAMADA FUNCTIONS                          **/
    /**                    USED TO LAYOUT GRAPH PRETTILY                    **/
    /*************************************************************************/

    /**
     * Executes the Kamada Algorithm on the nodes and edges currently in this
     * <code>VisualGraph</code> to produce a readable layout.
     * <p>
     * This method uses the following formulas:
     * <ul>m = current node for which to calculate
     * <br>dm = Delta(m) = sqrt(Eq7^2 + Eq8^2)</br>
     * <br>max_dm = max(Delta(m))</br>
     * <br>(In the following, i is an iterative variable)</br>
     * <br>diffx = x(m) - x(i)</br>
     * <br>diffy = y(m) - y(m)</br>
     * <br>ndist = sqrt({x(m) - x(i)}^2 + {y(m) - y(i)}^2)</br>
     * <br>Eq7 = dE/dx(m) = Sum(i): k(mi) * (diffx - l(mi) * diffx / ndist)
     * </br>
     * <br>Eq8 = dE/dy(m) = Sum(i): k(mi) * (diffy - l(mi) * diffy / ndist)
     * </br>
     * <br>Eq13 = d^2E/dx(m)^2</br>
     * <br>= Sum(i): k(mi) * [1 - (l(mi) * diffy^2)/ndist^3]</br>
     * <br>Eq14 = d^2E/dx(m)dy(m)</br>
     * <br>= Sum(i): k(mi) * [1 - (l(mi) * diffx * diffy)/ndist^3]</br>
     * <br>Eq16 = d^2E/dy(m)^2</br>
     * <br>= Sum(i): k(mi) * [1 - (l(mi) * diffx^2)/ndist^3]</br>
     * <br>deltay = (Eq14 * Eq7 - Eq13 * Eq8) / (Eq13 * Eq16 - Eq14^2)</br>
     * <br>deltax = -1 * (Eq16 * deltay + Eq8) / Eq14</br></ul>
     * </p>
     *
     * @throws IOException Indicates that an error occurred while organizing 
     *                     the graph. The graph may be left in an unreliable
     *                     state.
     */
    public void organizeGraph() throws IOException{
    int inds[];             //Quick-reference to indices needed for nodes
    int dist[][];           //Distances between the points
    double L[][];           //tension between points
    double k[][];           //spring between points
    double maxDist = 0;     //Maximum distance for the graph
    double bigl;            //Desired edge length for the drawing
    double diffx, diffy;    //Used to determine xm-xi, ym-yi
    double ndist;           //Distance between two nodes
    double Eq7, Eq8, Eq13, Eq14, Eq16;    //The five equations
    int m;                  //The current node to fix
    double dm, max_dm;      //delta m and maximum delta-m
    double iter1, iter2;    //Iteration variables
    double deltaX, deltaY;  //Delta x and Delta y
    double moved = 10;      //How much did I move in the last move?
    int last_char;          //What character did we move last?

    //Step 0: Find active indices, return early for num_nodes < 2
    if(num_nodes < 2) return;

    //Get indices that we use in inds for quicker access
    inds = new int[num_nodes];
    int temp = 0;
    for(int z = 0; z < MAX_NODES; z++)
        if(my_nodeset[z].isActivated())
        {
            inds[temp] = z;
            temp++;
        }

    //Step 1: Get initial points
    //Make Starintg points for the graph are on a unit circle
    double theta = 2.0 * Math.PI / num_nodes;   //Iteration factor
    for(int w = 0; w < num_nodes; w++)
        {
        my_nodeset[inds[w]].setX(Math.cos(w * theta) / 2 + 0.5);
        my_nodeset[inds[w]].setY(-1.0 * Math.sin(w * theta) / 2 + 0.5);
        }

    //Step 2:  Calculate distances & store (using Floyd's algorithm)
    dist = new int[num_nodes][num_nodes];
    //Part A:  Initial assignments, based upon edge existance
    for(int xx = 0; xx < num_nodes; xx++)
        for(int yy = 0; yy < num_nodes; yy++)
        if(xx == yy) dist[xx][yy] = 0;
        else if(my_edgeset[inds[xx]][inds[yy]].isActivated() ||
            my_edgeset[inds[yy]][inds[xx]].isActivated()) 
            dist[xx][yy] = 1;
        else dist[xx][yy] = BIG_NUM;
      
    //Part B:  Check connections through other nodes
    for(int h = 0; h < num_nodes; h++)
        for(int i = 0; i < num_nodes; i++)
        for(int j = 0; j < num_nodes; j++)
            {
            if(dist[i][j] == BIG_NUM)
                dist[i][j] = Math.min(dist[i][j], dist[i][h] + dist[h][j]);
            if((dist[i][j] < BIG_NUM) && (dist[i][j] > maxDist))
                maxDist = dist[i][j];
            }

    //Step 3: Initialize spring/tension arrays used by Kamada's algorithm
    L = new double[num_nodes][num_nodes];
    k = new double[num_nodes][num_nodes];
    //Richard - this makes a difference
    //bigl = 1.0 / maxDist;                     // L = L0 / max d(ij)
    bigl = 0.9 / maxDist;                     // L = L0 / max d(ij)
    for(int xx = 0; xx < num_nodes; xx++)
        for(int yy = 0; yy <= xx; yy++)
        {
            L[xx][yy] = bigl * dist[xx][yy];    // L(ij) = L * d(ij)
            L[yy][xx] = L[xx][yy];
            if(xx != yy) 
            k[xx][yy] = (double) (K) / Math.pow(dist[xx][yy], 2);
            else k[xx][yy] = 0;

            k[yy][xx] = k[xx][yy];              // k(ij) = K / d(ij)^2
        }

    //Step 4: Take what we have and MAKE IT PRETTY
    //        IE, the BA Loop of Death While Loop-a-mundo.
    //Part A: First, initialize the appropriate variables
    //        Set iterations to zero, find node with max dm
    iter1 = 0;
    max_dm = m = 0;
    for(int f = 0; f < num_nodes; f++)
        {
        //Part i: Calculate Eq7 and Eq8
        Eq7 = Eq8 = 0;
        for(int i = 0; i < num_nodes; i++)
            if(i != f)
            {
                //Get diffx, diffy, and ndist
                diffx = my_nodeset[inds[f]].getX() - my_nodeset[inds[i]].getX();
                diffy = my_nodeset[inds[f]].getY() - my_nodeset[inds[i]].getY();
                ndist = Math.sqrt(diffx * diffx + diffy * diffy);

                //Sum Eq7 and Eq8
                Eq7 += k[f][i] * (diffx - L[f][i] * diffx / ndist);
                Eq8 += k[f][i] * (diffy - L[f][i] * diffy / ndist);
            }
         
        //Part ii: Calculate dm = sqrt(Eq7^2 + Eq8^2)
        dm = Math.sqrt(Eq7 * Eq7 + Eq8 * Eq8);

        //Part iii: Check dm against max_dm, adjust m and max_dm
        if(dm > max_dm) {  m = f;  max_dm = dm;  }
        }
      
    //Part B:  LOOP!  Loop until max_dm is less than epsilon, or
    //         we've run too many iterations anyway
    last_char = m;
    while((max_dm > EP1) && (iter1 < ITERLIM1))
        {
        //Initialize inner loop
        dm = max_dm;
        iter2 = 0;
        iter1++;

        //Part C: INNER LOOP!  Loop until dm is less than epsilon2, or
        //        we've run too many iterations anyway
        while((moved > EP2) && (dm > EP2) && (iter2 < ITERLIM2))
            {
            iter2++;
            //Part D: Calculate Eq7, Eq8, Eq13, Eq14, and Eq16
            //Step i: Initialize to zero
            Eq7 = Eq8 = Eq13 = Eq14 = Eq16 = 0;
            for(int i = 0; i < num_nodes; i++)
                if(i != m)
                {
                    //Step ii: Calculate diffx, diffy, and ndist
                    diffx = my_nodeset[inds[m]].getX() - 
                    my_nodeset[inds[i]].getX();
                    diffy = my_nodeset[inds[m]].getY() - 
                    my_nodeset[inds[i]].getY();
                    ndist = Math.sqrt(diffx * diffx + diffy * diffy);

                    //Step iii: Sum Eq7, Eq8, Eq13, Eq14, and Eq16
                    Eq7 += k[m][i] * (diffx - L[m][i] * diffx / ndist);
                    Eq8 += k[m][i] * (diffy - L[m][i] * diffy / ndist);
                    Eq13 += k[m][i] * (1 - L[m][i] * Math.pow(diffy, 2) / 
                               Math.pow(ndist, 3));
                    Eq14 += k[m][i] * (L[m][i] * diffx * diffy /
                               Math.pow(ndist, 3));
                    Eq16 += k[m][i] * (1 - L[m][i] * Math.pow(diffx, 2) /
                               Math.pow(ndist, 3));
                }  //End if i != m

            //Part E:  Determine delta x and delta y
            deltaY = (Eq14 * Eq7 - Eq13 * Eq8) / 
                (Eq13 * Eq16 - Eq14 * Eq14);
            deltaX = -1 * (Eq16 * deltaY + Eq8) / Eq14;

            //Part F:  Determine new x and new y
            //MO
            my_nodeset[inds[m]].setX(my_nodeset[inds[m]].getX() + deltaX);
            my_nodeset[inds[m]].setY(my_nodeset[inds[m]].getY() + deltaY);

            //Part G:  Find the new dm using new Eq7 and Eq8
            //Part i: Calculate Eq7 and Eq8
            Eq7 = Eq8 = 0;
            for(int i = 0; i < num_nodes; i++)
                if(i != m)
                {
                    //Get diffx, diffy, and ndist
                    diffx = my_nodeset[inds[m]].getX() - 
                    my_nodeset[inds[i]].getX();
                    diffy = my_nodeset[inds[m]].getY() - 
                    my_nodeset[inds[i]].getY();
                    ndist = Math.sqrt(diffx * diffx + diffy * diffy);

                    //Sum Eq7 and Eq8
                    Eq7 += k[m][i] * (diffx - L[m][i] * diffx / ndist);
                    Eq8 += k[m][i] * (diffy - L[m][i] * diffy / ndist);
                }
         
            //Part ii: Calculate dm = sqrt(Eq7^2 + Eq8^2)
            dm = Math.sqrt(Eq7 * Eq7 + Eq8 * Eq8);
            moved = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            //for(int delay = 0; delay < 10000; delay++)
            //  for(int delay2 = 0; delay2 < 10000; delay2++);
            }  //End inner loop (adjust a particular node)

        //Part H:  Find max_dm, m, and dm again
        max_dm = m = 0;
        for(int f = 0; f < num_nodes; f++)
            {
            //Part i: Calculate Eq7 and Eq8
            Eq7 = Eq8 = 0;
            for(int i = 0; i < num_nodes; i++)
                if(i != f)
                {
                    //Get diffx, diffy, and ndist
                    diffx = my_nodeset[inds[f]].getX() - 
                    my_nodeset[inds[i]].getX();
                    diffy = my_nodeset[inds[f]].getY() - 
                    my_nodeset[inds[i]].getY();
                    ndist = Math.sqrt(diffx * diffx + diffy * diffy);

                    //Sum Eq7 and Eq8
                    Eq7 += k[f][i] * (diffx - L[f][i] * diffx / ndist);
                    Eq8 += k[f][i] * (diffy - L[f][i] * diffy / ndist);
                }
         
            //Part ii: Calculate dm = sqrt(Eq7^2 + Eq8^2)
            dm = Math.sqrt(Eq7 * Eq7 + Eq8 * Eq8);

            //Part iii: Check dm against max_dm, adjust m and max_dm
            if(dm > max_dm) {  m = f;  max_dm = dm;  }
            } //End loop checking dm's

        if(m == last_char) break; else last_char = m;
        }  //End outer loop (move each node)

    //Part I:  We are done!
      
    //Step 5:  Readjust nodes to fit nicely in the screen!
    //First, find the max and minimum nodes
    double minx = 100, miny = 100, maxx = -100, maxy = -100;
    for(int zz = 0; zz < num_nodes; zz++)
        {
        if(my_nodeset[inds[zz]].getX() < minx)
            minx = my_nodeset[inds[zz]].getX();
        if(my_nodeset[inds[zz]].getY() < miny)
            miny = my_nodeset[inds[zz]].getY();
        if(my_nodeset[inds[zz]].getX() > maxx)
            maxx = my_nodeset[inds[zz]].getX();
        if(my_nodeset[inds[zz]].getY() > maxy)
            maxy = my_nodeset[inds[zz]].getY();
        }
    
    //Now, go through each node & fit to the screen!
    for(int zz = 0; zz < num_nodes; zz++)
        {
        my_nodeset[inds[zz]].setX( (my_nodeset[inds[zz]].getX() - minx) / 
                       (maxx - minx) * 0.90 + 0.05);
        my_nodeset[inds[zz]].setY( (my_nodeset[inds[zz]].getY() - miny) / 
                       (maxy - miny) * 0.90 + 0.05);
        }

    //Step 6:  Assign start/end values to edges
    assignEdges();
    }

    /** 
     * Organizes the nodes of this <code>VisualGraph</code> around a unit 
     * circle to produce a more readable layout.
     * Note that the graph must have more than three nodes to use this method.
     */
    public void organizeCircle(){
    int inds[];             //Quick-reference to indices needed for nodes

    //Return early for num_nodes < 2
    if(num_nodes < 2) return;

    //Get indices that we use in inds for quicker access
    inds = new int[num_nodes];
    int temp = 0;
    for(int z = 0; z < MAX_NODES; z++)
        if(my_nodeset[z].isActivated())
        {
            inds[temp] = z;
            temp++;
        }

    //Make points for the graph be on a unit circle
    double theta = 2.0 * Math.PI / num_nodes;   //Iteration factor
    for(int w = 0; w < num_nodes; w++)
        {
        my_nodeset[inds[w]].setX(Math.cos(w * theta) / 2.25 + 0.5);
        my_nodeset[inds[w]].setY(-1.0 * Math.sin(w * theta) / 2.25 + 0.5);
        }

    //We're done now, assign the edges appropriately
    assignEdges();
    }

    /**
     * Assigns the edges in this <code>VisualGraph</code> to the correct
     * locations based on node locations.
     */
    private void assignEdges(){
    //For now, we have all straight edges
    //We use arrows to show direction for directed graphs
    //So, for simplicity, we put the edge lines in the background and have
    //each edge start at the start-node and end at the end-node
    for(int sn = 0; sn < MAX_NODES; sn++)
        for(int en = 0; en < MAX_NODES; en++)
        if(my_edgeset[sn][en].isActivated())
            {
            my_edgeset[sn][en].setSX(my_nodeset[sn].getX());
            my_edgeset[sn][en].setSY(my_nodeset[sn].getY());
            my_edgeset[sn][en].setEX(my_nodeset[en].getX());
            my_edgeset[sn][en].setEY(my_nodeset[en].getY());
            }
    }

    /**
     * Checks to make sure this <code>VisualGraph</code>'s layout does not have
     * any overlaps.
     * This involves three steps: 
     * <ul><br>Check to see if the graph has any nodes that fall within a
     *         parabola calculated around every edge.</br>
     * <br>Check to make sure all edge lengths meet a minimum length
     *     requirement.</br>
     * <br>Check to make sure no edge centers are too close together.</br></ul>
     * <p>
     * The tolerances for the above checks can be adjusted by changing
     * <code>NODE_TOEDGE_MIN</code>, <code>EDGE_LENGTH_MIN</code>, and
     * <code>EDGE_CENTER_MIN</code> respectively.
     * </p>
     *
     * @return Gives a value of <code>true</code> if one of the above checks
     *         finds an overlap in the graph layout; otherwise, 
     *         <code>false</code> is returned.
     */
    public boolean gaigsIsOverlap(){
    boolean reverse = false;
    double x1, y1, x2, y2, x3, y3, min, dist;
    double[] x = new double[num_edges];
    double[] y = new double[num_edges];
    int count = 0;

    for(int i = 0; i < num_nodes; i++){
        for(int j = i + 1; j < num_nodes; j++){
        if(my_edgeset[i][j].isActivated()){
            x1 = my_nodeset[i].getX();
            x2 = my_nodeset[j].getX();
            y1 = my_nodeset[i].getY();
            y2 = my_nodeset[j].getY();
            x[count] = (x1 + x2) / 2.0;
            y[count] = (y1 + y2) / 2.0;
            count++;
            min = gaigsDistPoints(x1, y1, x2, y2);

            // make sure edge has some length
            if(min < EDGE_LENGTH_MIN){
            //System.out.println(" edge too short");
            return ! reverse;
            }

            min += NODE_TOEDGE_MIN;
            for(int k = 0; k < num_nodes; k++){
            if(k != i && k != j){
                x3 = my_nodeset[k].getX();
                y3 = my_nodeset[k].getY();
                dist = gaigsDistPoints(x1, y1, x3, y3);
                dist += gaigsDistPoints(x2, y2, x3, y3);
                if(dist < min){
                //System.out.println(" node too close to edge");
                return ! reverse;
                }
            }
            }
        }
        }
    }
    for(int i = 0; i < num_edges; i++){
        for(int j = i + 1; j < num_edges; j++){
        if(gaigsDistPoints(x[i], y[i], x[j], y[j]) < EDGE_CENTER_MIN){
            //System.out.println(" edge centers too close");
            return ! reverse;
        }
        }
    }
    //System.out.print("\n");
    return reverse;
    }

 // this private method is never used
    /*
     * Checks if this <code>VisualGraph</code> is newly initialized or if it
     * has been changed from its original configuration.
     * 
     * @return Gives a value of <code>true</code> if this
     *         <code>VisualGraph</code> has not been changed from its initial
     *         state and <code>false</code> if it has been changed.
     */
/*    private boolean isNewGraph(){
        boolean isnewgraph = true;
        int node  = 0;

        while(isnewgraph && (node < MAX_NODES)){
        if(my_nodeset[node].isActivated())
        if((my_nodeset[node].getX() != 0.5) || 
           (my_nodeset[node].getY() != 0.5))
            isnewgraph = false;
        node++;
    }
       
        return isnewgraph;
    }
*/
    /*************************************************************************/
    /**                           LAYOUT FUNCTIONS                          **/
    /*************************************************************************/

    /**
     * Clears this <code>VisualGraph</code> and produces a randomly generated
     * graph according to the supplied constraints.
     *
     * @param node_c            Indicates the number of nodes to appear in the
     *                          graph. A value of 0 means that a random number
     *                          of nodes should be generated.
     * @param edge_c            Indicates the number of edges to appear in the
     *                          graph. A value of -1 means that a random number
     *                          of edges should be generated.
     * @param self_loop         Specifies if edges with the same start and goal
     *                          node are possible. A value of <code>true</code>
     *                          indicates that self-loops are allowed, and
     *                          <code>false</code> means they are not.
     * @param weights           Specifies if the edges in the graph should be 
     *                          weighted. Passing <code>true</code> results in
     *                          a weighted graph, and <code>false</code> yields
     *                          an unweighted graph.
     * @param dircted           Indicates if the edges in the graph should be 
     *                          directed. Giving a value of <code>true</code>
     *                          means that edges can be unidirectional, and
     *                          <code>false</code> makes all edges
     *                          bidirectional.
     * @param min_weight        Specifies the minimum weight for edges in the
     *                          graph. Ignored if the graph is to be
     *                          unweighted.
     * @param max_weight        Specifies the maximum weight for edges in the
     *                          graph. Ignored if the graph is to be
     *                          unweighted.
     * @throws RuntimeException Indicates a problem finding a random edge. This
     *                          <code>VisualGraph</code> will be in an
     *                          unreliable state, and this method should be
     *                          called again to generate the desired random 
     *                          graph.
     */
    public void randomGraph(int node_c, int edge_c, boolean self_loop,
                            boolean weights, boolean dircted,
                            double min_weight, double max_weight)
    throws RuntimeException{
        int ns_index, ne_index;     //Indices for randomness
        int max_edges;              //Max number of edges
        int edge_holder;            //Use these to randomize edges faster
        boolean first_edge;  
        clearGraph();               //Clears the current graph

        //If num_nodes is zero, find the number of nodes
        if(node_c == 0) node_c = (int) (Math.random() * MAX_NODES);

        //Activate the correct number of nodes
        for(int x = 0; x < node_c; x++)
        {
        my_nodeset[x].setChar(translateIndexChar(x));
        my_nodeset[x].activate();
        num_nodes++;
        }

        //Determine the maximum possible # of edges for this configuration
        max_edges = node_c * node_c;
        if(!dircted)  max_edges  = (max_edges - node_c) / 2 + node_c;
        if(!self_loop) max_edges -= node_c;

        //If num_edges is negative or otherwise invalid, find
        //   a valid number of edges
        if(edge_c < 0)         edge_c = (int) (Math.random() * max_edges);
        if(edge_c > max_edges) edge_c = max_edges;

        //Activate the correct number of edges
        for(int y = 0; y < edge_c; y++)
        {
        char booger = randomActiveNode();
        char snooger = randomActiveNode();
        ns_index = translateCharIndex(booger);
        ne_index = translateCharIndex(snooger);
        edge_holder = ns_index;     //Use these to quickly randomize
        first_edge = true;

        //Make sure you get a new, appropriate edge
        while((!self_loop && (ns_index == ne_index)) ||
              (my_edgeset[ns_index][ne_index].isActivated()))
            {
            //Choose a new node to start/end the edge with
            //Try getting new nodes for the start-node first
            if(first_edge)
                {
                ns_index = (ns_index + 1) % MAX_NODES;
                while((ns_index != edge_holder) && 
                      (!my_nodeset[ns_index].isActivated()))
                    ns_index = (ns_index + 1) % MAX_NODES;

                if(ns_index == edge_holder) 
                    {
                    first_edge = false;
                    edge_holder = ne_index;
                    }
                }

            //If that's not working, get new end-nodes
            if(!first_edge)
                {
                ne_index = (ne_index + 1) % MAX_NODES;
                while((ne_index != edge_holder) && 
                      (!my_nodeset[ne_index].isActivated()))
                    ne_index = (ne_index + 1) % MAX_NODES;
                
                //If we STILL can't find a good edge, something is
                //   seriously wrong... throw a runtimeException
                if(ne_index == edge_holder) 
                    throw new RuntimeException("Prob finding random edge.");
                }  //END iterate end-nodes
            }  //END Get valid edge to activate

        //We now have a valid edge... activate it appropriately!
        //If it's weighted, add a random weight
        my_edgeset[ns_index][ne_index].activate();
        if(weights) my_edgeset[ns_index][ne_index].setWeight(
                                     min_weight + Math.random() * (max_weight - min_weight));


        //If the graph is not directed, set the related edge too
        if(!dircted)            
            {
            my_edgeset[ne_index][ns_index].activate();
            my_edgeset[ne_index][ns_index].setWeight(
                                 my_edgeset[ns_index][ne_index].getWeight());
            }
        }  //END for(int y = 0; y < edge_c; y++)
        
        //Set various counts and true/false values for the graph
        num_edges = edge_c;
        weighted = weights;
        directed = dircted;
    }

    /**
     * Clears this <code>VisualGraph</code> and produces a randomly generated
     * complete graph according to the supplied constraints.
     *
     * @param node_c     Indicates the number of nodes to appear in the graph.
     *                   A value of 0 means that a random number of nodes
     *                   should be generated.
     * @param self_loop  Specifies if edges with the same start and goal node
     *                   are possible. A value of <code>true</code> indicates
     *                   that self-loops are allowed, and <code>false</code>
     *                   means they are not.
     * @param weights    Specifies if the edges in the graph should be 
     *                   weighted. Passing <code>true</code> results in a 
     *                   weighted graph, and <code>false</code> yields an
     *                   unweighted graph.
     * @param min_weight Specifies the minimum weight for edges in the graph.
     *                   Ignored if the graph is to be unweighted.
     * @param max_weight Specifies the maximum weight for edges in the graph.
     *                   Ignored if the graph is to be unweighted.
     */
    public void randomCompleteGraph(int node_c, boolean self_loop, 
                    boolean weights, double min_weight,
                    double max_weight){
//        int n_index;                //Index for randomness 	// never used
        int edge_c = 0;             //Number of edges
        clearGraph();               //Clears the current graph

        //If num_nodes is zero, find the number of nodes
        if(node_c == 0) node_c = (int) (Math.random() * MAX_NODES);

        //Activate the correct number of nodes (randomly)
        for(int x = 0; x < node_c; x++)
        {
        my_nodeset[x].setChar(translateIndexChar(x));
        my_nodeset[x].activate();
        num_nodes++;
        }

        //Activate edges between all active nodes!
        //Also, activate edge between node & self if self_loop
        for(int i = 0; i < MAX_NODES; i++)
        for(int j = 0; j <= i; j++)
        if(((i != j) || ((i == j) && self_loop)) &&
           my_nodeset[i].isActivated() && my_nodeset[j].isActivated())
            {
            if(weights)   //Get weights where appropriate
                {
                my_edgeset[i][j].setWeight(min_weight + 
                               Math.random() * (max_weight - min_weight));
                my_edgeset[j][i].setWeight(my_edgeset[i][j].getWeight());
                }
            my_edgeset[i][j].activate();
            my_edgeset[j][i].activate();
            edge_c++;
            }
        
        //Set various counts and true/false values for the graph
        num_edges = edge_c;
        weighted  = weights;
        directed  = false;
    }

    /**
     * Clears this <code>VisualGraph</code> and produces a randomly generated
     * graph containing a Hamiltonian Cycle according to the supplied
     * constraints.
     *
     * @param node_c            Indicates the number of nodes to appear in the
     *                          graph. A value of 0 means that a random number
     *                          of nodes should be generated.
     * @param edge_c            Indicates the number of edges to appear in the
     *                          graph. A value of -1 means that a random number
     *                          of edges should be generated.
     * @param self_loop         Specifies if edges with the same start and goal
     *                          node are possible. A value of <code>true</code>
     *                          indicates that self-loops are allowed, and
     *                          <code>false</code> means they are not.
     * @param weights           Specifies if the edges in the graph should be 
     *                          weighted. Passing <code>true</code> results in
     *                          a weighted graph, and <code>false</code> yields
     *                          an unweighted graph.
     * @param dircted           Indicates if the edges in the graph should be 
     *                          directed. Giving a value of <code>true</code>
     *                          means that edges can be unidirectional, and
     *                          <code>false</code> makes all edges
     *                          bidirectional.
     * @param min_weight        Specifies the minimum weight for edges in the
     *                          graph. Ignored if the graph is to be
     *                          unweighted.
     * @param max_weight        Specifies the maximum weight for edges in the
     *                          graph. Ignored if the graph is to be
     *                          unweighted.
     * @throws RuntimeException Indicates a problem finding a random edge. This
     *                          <code>VisualGraph</code> will be in an
     *                          unreliable state, and this method should be
     *                          called again to generate the desired random 
     *                          graph.
     */
    public void randomHamiltonianGraph(int node_c, int edge_c, 
                       boolean self_loop, boolean weights,
                       boolean dircted, double min_weight,
                       double max_weight)
    throws RuntimeException{
        int ns_index, ne_index;       //Indices for randomness
        int max_edges, min_edges;     //Max/min number of edges
        int edge_holder;              //Use these to randomize edges faster
        boolean first_edge;  
        boolean hc_nodes[];        //Takes note of Hamiltonian cycled nodes
        hc_nodes = new boolean[MAX_NODES]; 

        //Set all the nodes to "not-Hamiltonian-cycled"
        for(int xx = 0; xx < MAX_NODES; xx++)
            hc_nodes[xx] = false;
 
        clearGraph();               //Clears the current graph

        //If num_nodes is zero, find the number of nodes
        if(node_c == 0) node_c = (int) (Math.random() * MAX_NODES);

        //Activate the correct number of nodes
        for(int x = 0; x < node_c; x++)
        {
        my_nodeset[x].setChar(translateIndexChar(x));
        my_nodeset[x].activate();
        num_nodes++;
        }

        //Determine the max/min possible # of edges for this configuration
        max_edges = node_c * node_c;
        if(!dircted)  max_edges  = (max_edges - node_c) / 2 + node_c;
        if(!self_loop) max_edges -= node_c;
        min_edges = node_c;

        //If num_edges is negative or otherwise invalid, find a valid value
        if(edge_c < 0)         edge_c = (int) (Math.random() * max_edges);
        if(edge_c > max_edges) edge_c = max_edges;
        if(edge_c < min_edges) edge_c = min_edges;

        //HAMILTONIAN CYCLE ALL THE NODES
        //This is what makes this function special.  Pick a random active
        //    non-Hamiltonian-cycled node until all the nodes are in the
        //    Hamiltonian cycle!  Hoorah!
        //Get the first index and remember it
        ns_index = translateIndexChar(randomActiveNode());
        edge_holder = ns_index;
        
        //Connect up all the nodes into the loop (RANDOMLY) except for
        //  the first edge
        for(int hc = 0; hc < min_edges - 1; hc++)
        {
        //Get a random node not already in the Hamiltonian cycle
        //  (Note:  NOT the starting node!)
        ne_index = translateCharIndex(randomActiveNode());
        while(hc_nodes[ne_index] || (ne_index == edge_holder))
            ne_index = (ne_index + 1) % MAX_NODES;

        //Activate the appropriate edge
        //If it's weighted, add a random weight.
        my_edgeset[ns_index][ne_index].activate();
        if(weights) my_edgeset[ns_index][ne_index].setWeight(
                                     min_weight + Math.random() * (max_weight - min_weight));

        //If the graph is not directed, set the related edge too
        if(!dircted)            
            {
            my_edgeset[ne_index][ns_index].activate();
            my_edgeset[ne_index][ns_index].setWeight(
                                 my_edgeset[ns_index][ne_index].getWeight());
            }

        //Add the node to the cycle, and make it the next from-node
        hc_nodes[ne_index] = true;
        ns_index = ne_index;
            
        }  //END getting nodes added to the hamiltonian cycle

        //Connect the first edge.  If it's weighted, add a random weight.
        ne_index = edge_holder;
        my_edgeset[ns_index][ne_index].activate();
        if(weights) my_edgeset[ns_index][ne_index].setWeight(
                                 min_weight + Math.random() * (max_weight - min_weight));

        //If the graph is not directed, set the related edge too
        if(!dircted)            
        {
        my_edgeset[ne_index][ns_index].activate();
        my_edgeset[ne_index][ns_index].setWeight(
                             my_edgeset[ns_index][ne_index].getWeight());
        }
        //We've got a Hamiltonian cycle!  Hoorah!

        //Then, activate the remaining correct number of edges if needed
        for(int y = min_edges; y < edge_c; y++)
        {
        ns_index = translateCharIndex(randomActiveNode());
        ne_index = translateCharIndex(randomActiveNode());
        edge_holder = ns_index;     //Use these to quickly randomize
        first_edge = true;

        //Make sure you get a new, appropriate edge
        while((!self_loop && (ns_index == ne_index)) ||
              (my_edgeset[ns_index][ne_index].isActivated()))
            {
            //Choose a new node to start/end the edge with
            //Try getting new nodes for the start-node first
            if(first_edge)
                {
                ns_index = (ns_index + 1) % MAX_NODES;
                while((ns_index != edge_holder) && 
                      (!my_nodeset[ns_index].isActivated()))
                    ns_index = (ns_index + 1) % MAX_NODES;

                if(ns_index == edge_holder) 
                    {
                    first_edge = false;
                    edge_holder = ne_index;
                    }
                }

            //If that's not working, get new end-nodes
            if(!first_edge)
                {
                ne_index = (ne_index + 1) % MAX_NODES;
                while((ne_index != edge_holder) && 
                      (!my_nodeset[ne_index].isActivated()))
                    ne_index = (ne_index + 1) % MAX_NODES;
                
                //If we STILL can't find a good edge, something is
                //   seriously wrong... throw a runtimeException
                if(ne_index == edge_holder) 
                    throw new RuntimeException("Prob finding random edge.");
                }       
            }  //END Get valid edge to activate

        //We now have a valid edge... activate it appropriately!
        //If it's weighted, add a random weight.
        my_edgeset[ns_index][ne_index].activate();
        if(weights) my_edgeset[ns_index][ne_index].setWeight(
                                     min_weight + Math.random() * (max_weight - min_weight));

        //If the graph is not directed, set the related edge too
        if(!dircted)            
            {
            my_edgeset[ne_index][ns_index].activate();
            my_edgeset[ne_index][ns_index].setWeight(
                                 my_edgeset[ns_index][ne_index].getWeight());
            }
        }  //END For(y = 0; y < edge_c; y++)

        //Set various counts and true/false values for the graph
        num_edges = edge_c;
        weighted = weights;
        directed = dircted;
    }

    /**
     * Clears this <code>VisualGraph</code> and produces a randomly generated
     * connected graph according to the supplied constraints.
     *
     * @param node_c            Indicates the number of nodes to appear in the
     *                          graph. A value of 0 means that a random number
     *                          of nodes should be generated.
     * @param edge_c            Indicates the number of edges to appear in the
     *                          graph. A value of -1 means that a random number
     *                          of edges should be generated. Note that more
     *                          edges than specified may have to be added to
     *                          make the graph connected.
     * @param self_loop         Specifies if edges with the same start and goal
     *                          node are possible. A value of <code>true</code>
     *                          indicates that self-loops are allowed, and
     *                          <code>false</code> means they are not.
     * @param weights           Specifies if the edges in the graph should be 
     *                          weighted. Passing <code>true</code> results in
     *                          a weighted graph, and <code>false</code> yields
     *                          an unweighted graph.
     * @param dircted           Indicates if the edges in the graph should be 
     *                          directed. Giving a value of <code>true</code>
     *                          means that edges can be unidirectional, and
     *                          <code>false</code> makes all edges
     *                          bidirectional.
     * @param min_weight        Specifies the minimum weight for edges in the
     *                          graph. Ignored if the graph is to be
     *                          unweighted.
     * @param max_weight        Specifies the maximum weight for edges in the
     *                          graph. Ignored if the graph is to be
     *                          unweighted.
     * @throws RuntimeException Indicates a problem finding a random edge. This
     *                          <code>VisualGraph</code> will be in an
     *                          unreliable state, and this method should be
     *                          called again to generate the desired random 
     *                          graph.
     */
    public void randomConnectedGraph(int node_c, int edge_c, 
                     boolean self_loop, boolean weights,
                     boolean dircted, double min_weight,
                     double max_weight)
    throws RuntimeException{
        int ns_index, ne_index;       //Indices for randomness
        int max_edges, my_edge_c;     //Max number of edges, edge count
        int e_h1, e_h2;               //Use these to randomize edges faster
        boolean first_edge;  
        boolean completed = false;    //Are all the nodes connected?
        boolean con_nodes[][];     //Takes note of nodes that are connected
        con_nodes = new boolean[MAX_NODES][MAX_NODES]; 

        //Set all the nodes to "not-connected"
        //NOTE:  All nodes are automatically connected to themselves
        for(int xx = 0; xx < MAX_NODES; xx++)
            for(int yy = 0; yy < MAX_NODES; yy++)
                if(xx == yy) con_nodes[xx][yy] = true;
        else con_nodes[xx][yy] = false;
 
        clearGraph();               //Clears the current graph

        //If num_nodes is zero, find the number of nodes
        if(node_c == 0) node_c = (int) (Math.random() * MAX_NODES);
    //if(node_c == 0) node_c = (int) (Math.random() * 13);

        //Activate the correct number of nodes
        for(int x = 0; x < node_c; x++)
        {
        my_nodeset[x].setChar(translateIndexChar(x));
        my_nodeset[x].activate();
        num_nodes++;
        }

        //Determine the maximum possible # of edges for this configuration
        max_edges = node_c * node_c;
        if(!dircted)  max_edges  = (max_edges - node_c) / 2 + node_c;
        if(!self_loop) max_edges -= node_c;
    //max_edges = node_c * 2;

        //If num_edges is negative or otherwise invalid, find a valid value
        if(edge_c < 0)         edge_c = (int) (Math.random() * max_edges);
        if(edge_c > max_edges) edge_c = max_edges;

        //CONNECT ALL THE NODES -- This is what makes this function special
        //Add edges until all nodes are connected
        //Then, activate the remaining correct number of edges if needed
        for(my_edge_c = 0; !completed || (my_edge_c < edge_c); )
        {
        ns_index = translateCharIndex(randomActiveNode());
        ne_index = translateCharIndex(randomActiveNode());
        e_h1 = ns_index;            //Use these to quickly randomize
        e_h2 = ne_index;
        first_edge = true;

        //Make sure you get a new, appropriate edge
        while((!self_loop && (ns_index == ne_index)) ||
              (!completed && con_nodes[ns_index][ne_index]) ||
              (my_edgeset[ns_index][ne_index].isActivated()))
            {
            //Choose a new node to start/end the edge with
            //If it's the first edge, find the next available end-node
            if(first_edge)
                {
                ne_index = (ne_index + 1) % MAX_NODES;
                while(!my_nodeset[ne_index].isActivated())
                    ne_index = (ne_index + 1) % MAX_NODES;
                first_edge = false;
                }
            //Otherwise, if we have more edges to check
            else if((ne_index != e_h2) || (ns_index != e_h1))
                {
                ne_index = (ne_index + 1) % MAX_NODES;
                while((ne_index != e_h2) && 
                      (!my_nodeset[ne_index].isActivated()))
                    ne_index = (ne_index + 1) % MAX_NODES;

                //We checked all the end-nodes for this start-node,
                //   so move to the next start-node 
                if(ne_index == e_h2)
                    {
                    ns_index = (ns_index + 1) % MAX_NODES;
                    while((ns_index != e_h1) && 
                          (!my_nodeset[ns_index].isActivated()))
                        ns_index = (ns_index + 1) % MAX_NODES;
                    }
                }
            //Otherwise, we checked all possible edges, end program
            else
                throw new RuntimeException("Prob finding random edge.");
            }  //END Get valid edge to activate

        //We now have a valid edge... activate it appropriately!
        //If it's weighted, add a random weight.  Add to the edge count
        my_edge_c++;
        my_edgeset[ns_index][ne_index].activate();
        if(weights) my_edgeset[ns_index][ne_index].setWeight(
                                     min_weight + Math.random() * (max_weight - min_weight));

        //Connect up the start node with any nodes the end node
        //  is connected to
        for(int others = 0; others < MAX_NODES; others++)
            if(con_nodes[ne_index][others])
            con_nodes[ns_index][others] = true;

        //If the graph is not directed, set the related edge too
        if(!dircted)            
            {
            my_edgeset[ne_index][ns_index].activate();
            my_edgeset[ne_index][ns_index].setWeight(
                                 my_edgeset[ns_index][ne_index].getWeight());

            //Connect up the end node with any nodes the start node
            //  is connected to
            for(int others2 = 0; others2 < MAX_NODES; others2++)
                if(con_nodes[ns_index][others2])
                con_nodes[ne_index][others2] = true;
            }

        //If the graph was not "connected" complete on the last turn,
        //  check if it is "connected" now
        if(!completed)
            {
            //Assume that we are connected until proven otherwise
            //Not connected if any two active nodes are not connected
            completed = true;
            for(int ss = 0; (ss < MAX_NODES) && completed; ss++)
                for(int ee = 0; (ee < MAX_NODES) && completed; ee++)
                if(!con_nodes[ss][ee] && my_nodeset[ee].isActivated() &&
                   my_nodeset[ss].isActivated())
                    completed = false;
            }
        }  //END For(y = 0; y < edge_c; y++)

        //Set various counts and true/false values for the graph
        num_edges = my_edge_c;
        weighted = weights;
        directed = dircted;
    }

    /**
     * Clears this <code>VisualGraph</code> and produces a randomly generated
     * directed acyclic graph according to the supplied constraints.
     *
     * @param node_c     Indicates the number of nodes to appear in the graph.
     *                   A value of 0 means that a random number of nodes
     *                   should be generated.
     * @param edge_c     Indicates the number of edges to appear in the graph.
     *                   A value of -1 means that a random number of edges 
     *                   should be generated. Note that more or fewer edges
     *                   than specified may have to be added to produce a 
     *                   directed acyclic graph.
     * @param self_loop  Specifies if edges with the same start and goal node
     *                   are possible. A value of <code>true</code> indicates
     *                   that self-loops are allowed, and <code>false</code>
     *                   means they are not.
     * @param weights    Specifies if the edges in the graph should be 
     *                   weighted. Passing <code>true</code> results in a 
     *                   weighted graph, and <code>false</code> yields an
     *                   unweighted graph.
     * @param min_weight Specifies the minimum weight for edges in the graph.
     *                   Ignored if the graph is to be unweighted.
     * @param max_weight Specifies the maximum weight for edges in the graph.
     *                   Ignored if the graph is to be unweighted.
     */

// ++++++++++++  New version by MFM ++++++++++++++++++++++++++++++++++++++++    
    
    public void randomDAcyclicGraph(int node_c, int edge_c, 
                                    boolean self_loop, boolean weights,
                                    double min_weight, double max_weight){
        int ns_index, ne_index;     //Indices for randomness
        int max_edges	;			  //Max number of edges
        boolean con_nodes[][];     //Takes note of nodes that are connected
        con_nodes = new boolean[MAX_NODES][MAX_NODES]; 

        //Set all the nodes to "not-connected"
        //NOTE:  All nodes are automatically connected to themselves
        for (int xx = 0; xx < MAX_NODES; xx++)
            for (int yy = 0; yy < MAX_NODES; yy++)
                con_nodes[xx][yy] = (xx == yy);

        clearGraph();               //Clears the current graph

        //If num_nodes is zero, find the number of nodes
        if(node_c == 0)
            node_c = (int) (Math.random() * MAX_NODES);

        //Activate the correct number of nodes
        for(int x = 0; x < node_c; x++) {
            my_nodeset[x].setChar(translateIndexChar(x));
            my_nodeset[x].activate();
            num_nodes++;
        }
            
        //Determine the maximum possible # of edges for this configuration
        max_edges = node_c * node_c;
        if(!self_loop)
            max_edges -= node_c;
            
        int[] edgeArray = new int[max_edges];
        defineAndRandomizeEdges(node_c, self_loop, edgeArray);

        // If num_edges is negative or otherwise invalid, find
        // a valid number of edges
        if (edge_c < 0)
            edge_c = (int) (Math.random() * max_edges);
        else if(edge_c > max_edges)
            edge_c = max_edges;
			//System.out.println(edge_c);
           
        // Activate the correct number of edges
        int actualEdgeCount = 0;
        for (int edgeIndex = 0; edgeIndex < edgeArray.length && actualEdgeCount < edge_c; edgeIndex++) {
            ns_index = decodeStart(edgeArray[edgeIndex]);
            ne_index = decodeEnd(edgeArray[edgeIndex]);

            if (!con_nodes[ne_index][ns_index]) {
                                 //       System.out.println(ns_index + "  " + ne_index);       
                // We now have a valid edge... activate it appropriately!
                // If it's weighted, add a random weight
                my_edgeset[ns_index][ne_index].activate();
                if (weights)
                    my_edgeset[ns_index][ne_index].setWeight(
                                     min_weight + Math.random() * (max_weight - min_weight));
                                     
                con_nodes[ns_index][ne_index] = true;           
                transitiveClosure(con_nodes);
			    actualEdgeCount++;
             }
                        

        }
        
        //Set various counts and true/false values for the graph
        num_edges = actualEdgeCount;
        weighted = weights;
        directed = true;
    }

// --- MFM support routines ------------

    private void transitiveClosure (boolean [][] items) {
        for (int k = 0; k < items.length; k++)
            items[k][k] = true;
        for (int k = 0; k < items.length; k++)
            for (int i = 0; i < items.length; i++)
                for (int j = 0; j < items.length; j++)
                    items[i][j] = items[i][j] || (items[i][k] && items[k][j]);
    }

    private void defineAndRandomizeEdges (int node_c, boolean self_loop, int[] edgeArray) {
        int i = 0;
        for (int s = 0; s < node_c; s++)
            for (int e = 0; e < node_c; e++)
                if (self_loop || s != e)
                    edgeArray[i++] = encodeEdge(s,e);
 
        Random rand = new Random();
        for (int j = 0; j < edgeArray.length-1; j++) {
            swap(j, j + (Math.abs(rand.nextInt()) % (edgeArray.length-j)), edgeArray); 
//             System.out.println(decodeStart(edgeArray[j]) + "  " + decodeEnd(edgeArray[j]));
            }
    }
    
   /**
    * Swap two items in the array.
    */
   private void swap (int loc1, int loc2, int[] edgeArray) {
        int temp = edgeArray[loc1];
        edgeArray[loc1] = edgeArray[loc2];
        edgeArray[loc2] = temp;
    }
    
   /**
    * 
    */
   private int encodeEdge (int value, int value2) {
       return value * MAX_NODES + value2;
    }
    
   /**
    * 
    */
   private int decodeStart (int value) {
       return value / MAX_NODES;   
    }
    
   /**
    * 
    */
   private int decodeEnd (int value) {
       return value % MAX_NODES;   
    }
    
// --- end MFM support routines ------------
// ++++++++++++  end New version by MFM ++++++++++++++++++++++++++++++++++++++++  

    public void OLDrandomDAcyclicGraph(int node_c, int edge_c, 
                                    boolean self_loop, boolean weights,
                                    double min_weight, double max_weight){
        int ns_index, ne_index;     //Indices for randomness
        int max_edges, my_edge_c;   //Max number of edges, edge count
        int e_h1, e_h2;             //Use these to randomize edges faster
        boolean first_edge;  
        boolean acyc_full = false;  //True when we cannot add more edges
                                    //  lest we become cyclic
        boolean con_nodes[][];     //Takes note of nodes that are connected
        con_nodes = new boolean[MAX_NODES][MAX_NODES]; 

        //Set all the nodes to "not-connected"
        //NOTE:  All nodes are automatically connected to themselves
        for(int xx = 0; xx < MAX_NODES; xx++)
            for(int yy = 0; yy < MAX_NODES; yy++)
                if(xx == yy) con_nodes[xx][yy] = true;
        else con_nodes[xx][yy] = false;

        clearGraph();               //Clears the current graph

        //If num_nodes is zero, find the number of nodes
        if(node_c == 0) node_c = (int) (Math.random() * MAX_NODES);

        //Activate the correct number of nodes
        for(int x = 0; x < node_c; x++)
        {
        my_nodeset[x].setChar(translateIndexChar(x));
        my_nodeset[x].activate();
        num_nodes++;
        }

        //Determine the maximum possible # of edges for this configuration
        max_edges = node_c * node_c;
        if(!self_loop) max_edges -= node_c;

        //If num_edges is negative or otherwise invalid, find
        //   a valid number of edges
        if(edge_c < 0)         edge_c = (int) (Math.random() * max_edges);
        if(edge_c > max_edges) edge_c = max_edges;

        //Activate the correct number of edges
        for(my_edge_c = 0; !acyc_full && (my_edge_c < edge_c); )
        {
        ns_index = translateCharIndex(randomActiveNode());
        ne_index = translateCharIndex(randomActiveNode());
        e_h1 = ns_index;        //Use these to quickly randomize
        e_h2 = ne_index;
        first_edge = true;

        //Make sure you get a new, appropriate edge
        //Note:  We need a new edge if the end connects to the start
        while(((!self_loop && (ns_index == ne_index)) ||
               (con_nodes[ne_index][ns_index] && (ns_index != ne_index))
               || (my_edgeset[ns_index][ne_index].isActivated()))
              && !acyc_full)
            {
            //Choose a new node to start/end the edge with
            //If it's the first edge, find the next available end-node
            if(first_edge)
                {
                ne_index = (ne_index + 1) % MAX_NODES;
                while(!my_nodeset[ne_index].isActivated())
                    ne_index = (ne_index + 1) % MAX_NODES;
                first_edge = false;
                }
            //Otherwise, if we have more edges to check
            else if((ne_index != e_h2) || (ns_index != e_h1))
                {
                ne_index = (ne_index + 1) % MAX_NODES;
                while((ne_index != e_h2) && 
                      (!my_nodeset[ne_index].isActivated()))
                    ne_index = (ne_index + 1) % MAX_NODES;

                //We checked all the end-nodes for this start-node,
                //   so move to the next start-node 
                if(ne_index == e_h2)
                    {
                    ns_index = (ns_index + 1) % MAX_NODES;
                    while((ns_index != e_h1) && 
                          (!my_nodeset[ns_index].isActivated()))
                        ns_index = (ns_index + 1) % MAX_NODES;
                    }
                }
            //If we STILL can't find a good edge, then obviously
            //   we've exhausted the acyclic edges we can use,
            //   so we should stop adding edges
            else
                acyc_full = true;
            }  //END Get valid edge to activate

        //If we aren't adding an edge lest we become cyclical, break
        if(acyc_full) break;

        //We now have a valid edge... activate it appropriately!
        //If it's weighted, add a random weight
        my_edgeset[ns_index][ne_index].activate();
        if(weights) my_edgeset[ns_index][ne_index].setWeight(
                                     min_weight + Math.random() * (max_weight - min_weight));

        //Connect up the start node with any nodes the end node
        //  is connected to (we know they don't include the start node!)
        for(int others = 0; others < MAX_NODES; others++)
            if(con_nodes[ne_index][others])
            con_nodes[ns_index][others] = true;
        }  //END for(int y = 0; y < edge_c; y++)
        
        //Set various counts and true/false values for the graph
        num_edges = edge_c;
        weighted = weights;
        directed = true;
    }

    /**
     * Clears this <code>VisualGraph</code> and produces a randomly generated
     * sparsely connected graph suitable for many algorithm visualizations.
     *
     * @param numNodes   Indicates the number of nodes to appear in the graph.
     *                   Note that, unlike the other graph generation methods,
     *                   a value of 0 <b>does not</b> indicate that a random
     *                   number of nodes should be added.
     * @param numEdges   Indicates the number of edges to appear in the graph.
     *                   This number may be changed to produce a connected
     *                   graph. Note that, unlike the other graph generation
     *                   methods, a value of -1 <b>does not</b> indicate that a
     *                   random number of edges should be added.
     * @param min_weight Specifies the minimum weight for edges in the graph.
     * @param max_weight Specifies the maximum weight for edges in the graph.
     */
    public void randomGAIGSGraph(int numNodes, int numEdges, 
                 double min_weight, double max_weight){
    Random r = new Random();
    int ri;

    if(min_weight > max_weight){
        double temp = min_weight;
        min_weight = max_weight;
        max_weight = temp;
    }

    initializeGraph(); // setup the graph

    directed = false;
    weighted = true;

    if(numEdges > (numNodes * (numNodes + 1)) / 2)
        numEdges = (numNodes * (numNodes + 1)) / 2;
    if(numEdges < numNodes - 1)
        numEdges = numNodes - 1;

    // Add a bit of random extra nodes and edges
    ri = r.nextInt(3);
    numNodes += ri;
    numEdges += ri;

        
    //Activate the correct number of nodes
    for(int x = 0; x < numNodes; x++){
        my_nodeset[x].setChar(translateIndexChar(x));
        my_nodeset[x].activate();
        num_nodes++;
    }

    /********************************/
    /** Make a spanning tree    */
    /********************************/
    int tmp, left, tmp2, weight;
    int[] free = new int[numNodes];
    int[] join = new int[numNodes];

    // load the free array with all the nodes (verticies)
    for(int i = 0; i < numNodes; i++){
        free[i] = i; //my_nodeset[i].getChar();
    }

    // choose a random node to begin with
    join[0] = r.nextInt(numNodes);
    dropInt(free, join[0], numNodes);
    left = numNodes - 1;

    while(left > 0){
        tmp = r.nextInt(left);
        tmp2 = r.nextInt(numNodes - left);
        weight = r.nextInt((int) (max_weight - min_weight)) + 
        (int) min_weight;
        my_edgeset[join[tmp2]][free[tmp]].activate();
        my_edgeset[join[tmp2]][free[tmp]].setWeight(weight);
        my_edgeset[free[tmp]][join[tmp2]].activate();
        my_edgeset[free[tmp]][join[tmp2]].setWeight(weight);
        join[numNodes - left] = free[tmp];
        dropInt(free, tmp, left);
        num_edges++;
        left--;
    }

    /********************************/
    /** Add the shortcuts   */
    /********************************/

    //numNodes numEdges
    int i = numEdges - numNodes + 1;
    int x, y;

    while(i > 0){
        x = r.nextInt(numNodes - 1);
        y = r.nextInt(numNodes - x - 1) + x + 1;
        if(!my_edgeset[x][y].isActivated()){
        weight = r.nextInt((int) (max_weight - min_weight)) + 
            (int) min_weight;
        my_edgeset[x][y].activate();
        my_edgeset[x][y].setWeight(weight);
        my_edgeset[y][x].activate();
        my_edgeset[y][x].setWeight(weight);
        i--;
        num_edges++;
        }
    }
    }

    /**
     * Helps <code>randomGAIGSGraph</code> in generating a sparsely connected
     * graph. Drops the specified node index from <code>array</code>.
     *
     * @param array Gives an array of the nodes that must be connected.
     * @param item  Indicates the index of the newly connected node that is to
     *              be removed from <code>array</code>.
     * @param num   The number of nodes in <code>array</code>.
     */
    private void dropInt(int[] array, int item, int num){
    for(int i = item; i < num - 1; i++){
        array[i] = array[i + 1];
    }
    }

    /** 
     * Clears this <code>VisualGraph</code> and produces a randomly generated
     * sparsely connected graph with heuristic values for the nodes.
     * This method uses the A* Algorithm to test for the shortest path through
     * this graph that has the greatest number of nodes. It then initializes
     * the heuristic values for the nodes based on their Euclidean distances
     * from the goal node for this path and returns the start and goal nodes
     * for this path.
     *
     * @param node_c Indicates the number of nodes to appear in the graph. A 
     *               value of 0 means that a random number of nodes should be
     *               generated.
     * @param edge_c Indicates the number of edges to appear in the graph. A
     *               value of -1 means that a random number of edges should be
     *               generated.
     * @return       Gives a two-element array containing the start and goal
     *               nodes for the shortest path through the graph that has the
     *               greatest number of nodes. The value in index 0 is the
     *               start node for this path, and the value in index 1 is the
     *               goal node for this path to which the heuristic values have
     *               been initialized. These values are both <code>int</code>s
     *               that give the nodes' indices in <code>my_nodeset</code>.
     */
    public int[] randomHeuristicGraph(int node_c, int edge_c){
    Random rand = new Random();
    int start = 0, goal = 0, nodes = node_c, edges = edge_c, temp;
    int bestLength = 0, bestStart = 0, bestGoal = 0, bestH = 0;
    String path;
    boolean good_graph = false, error = false;
    int graphs = 0, min_length = 5;

    initializeGraph();

    while(!good_graph){
        temp = rand.nextInt(2);
        clearGraph();

        if(node_c == 0){
        nodes = temp + 8;
        }
        if(edge_c == -1){
        edges = temp + 10 + rand.nextInt(2);
        }

        do{
        randomGAIGSGraph(nodes, edges, 1, 13);
        if(++graphs == 200){
            min_length = 4;
        }else if(graphs == 700){
            min_length = 3;
        }

        try{
            organizeGraph();
        }
        catch(IOException e){
            System.err.println("Kamada layout error: " + e.toString());
            error = true;
        }
        }while(error || gaigsIsOverlap());

        initializeEdgeWeights();

        for(goal = 0; goal < nodes; goal++){
        if(my_nodeset[goal].isActivated()){
            initializeHValues(goal);
            
            for(start = 0; start < nodes; start++){
            if(start != goal && my_nodeset[start].isActivated()){
                path = runAStar(translateIndexChar(start), 
                        translateIndexChar(goal));
                resetNodes();
                
                if(path.length() > bestLength){
                bestLength = path.length();
                bestStart = start;
                bestGoal = goal;
                bestH = my_nodeset[start].getHeuristic();
                }else if(path.length() == bestLength){
                if(my_nodeset[start].getHeuristic() > bestH){
                    bestStart = start;
                    bestGoal = goal;
                    bestH = my_nodeset[start].getHeuristic();
                }
                }
            }
            }
        }
        }

        if(bestLength > min_length){
        good_graph = true;
        }else{
        bestLength = 0;
        bestStart = 0;
        bestGoal = 0;
        bestH = 0;
        }
    }

    resetNodes();
    initializeHValues(bestGoal);
    int[] start_and_goal = { bestStart, bestGoal };
    return start_and_goal;
    }

    /** 
     * Clears this <code>VisualGraph</code> and produces a randomly generated
     * sparsely connected heuristic graph that demonstrates the unique feature
     * of the A* Algorithm, namely the reopening of a closed node when a poor
     * heuristic value caused the node to be closed with a greater-than-optimal
     * cost. It then returns the start and goal nodes for this path.
     *
     * @param node_c Indicates the number of nodes to appear in the graph. A 
     *               value of 0 means that a random number of nodes should be
     *               generated.
     * @param edge_c Indicates the number of edges to appear in the graph. A
     *               value of -1 means that a random number of edges should be
     *               generated.
     * @return       Gives a two-element array containing the start and goal
     *               nodes for the shortest path through the graph that has the
     *               greatest number of nodes and demonstrates the unique
     *               feature of the A* Algorithm. The value in index 0 is the
     *               start node for this path, and the value in index 1 is the
     *               goal node for this path to which the heuristic values have
     *               been initialized. These values are both <code>int</code>s
     *               that give the nodes' indices in <code>my_nodeset</code>.
     */
    public int[] randomAStarSearchGraph(int node_c, int edge_c){
    Random rand = new Random();
    int start = 0, goal = 0, nodes = node_c, edges = edge_c, temp;
    int bestLength = 0, bestStart = 0, bestGoal = 0, bestH = 0;
    String path;
    boolean good_graph = false, error = false;
    int graphs = 0, min_length = 5;

    initializeGraph();

    while(!good_graph){
        temp = rand.nextInt(2);
        clearGraph();

        if(node_c == 0){
        nodes = temp + 8;
        }
        if(edge_c == -1){
        edges = temp + 10 + rand.nextInt(2);
        }

        do{
        randomGAIGSGraph(nodes, edges, 1, 13);
        if(++graphs == 200){
            min_length = 4;
        }else if(graphs == 700){
            min_length = 3;
        }

        try{
            organizeGraph();
        }
        catch(IOException e){
            System.err.println("Kamada layout error: " + e.toString());
            error = true;
        }
        }while(error || gaigsIsOverlap()); 

        initializeEdgeWeights();

        for(goal = 0; goal < nodes; goal++){
        if(my_nodeset[goal].isActivated()){
            initializeHValues(goal);

            for(start = 0; start < nodes; start++){
            if(start != goal && my_nodeset[start].isActivated()){
                path = testAStar(translateIndexChar(start),
                         translateIndexChar(goal));
                resetNodes();
                
                if(path != ""){
                if(path.length() > bestLength){
                    bestLength = path.length();
                    bestStart = start;
                    bestGoal = goal;
                    bestH = my_nodeset[start].getHeuristic();
                }else if(path.length() == bestLength){
                    if(my_nodeset[start].getHeuristic() >
                       bestH){
                    bestStart = start;
                    bestGoal = goal;
                    bestH =
                        my_nodeset[start].getHeuristic();
                    }
                }
                }
            }
            }
        }
        }

        if(bestLength > min_length){
        good_graph = true;
        }else{
        bestLength = 0;
        bestStart = 0;
        bestGoal = 0;
        bestH = 0;
        }
    }

    resetNodes();
    initializeHValues(bestGoal);
    int[] start_and_goal = { bestStart, bestGoal };
    return start_and_goal;
    }

    /*************************************************************************/
    /**                          UTILITY  FUNCTIONS                         **/
    /*************************************************************************/

    /**
     * Returns the name/label for a randomly selected node.
     * The node chosen may be activated or unactivated.
     *
     * @return Yields the <code>char</code> name/label value for a randomly
     *         chosen node.
     */
    public char randomNode()
    { return translateIndexChar((int) (Math.random() * MAX_NODES)); }

    /**
     * Returns the name/label for a randomly selected activated node.
     * 
     * @return Gives the <code>char</code> name/label value for a randomly
     *         chosen activated node. If there are currently no activated
     *         nodes, the value returned is the invalid <code>char '~'</code>.
     */
    public char randomActiveNode(){
        int my_randchar = (int) (Math.random() * MAX_NODES);

        //If there are no nodes that are active, return a goof char
        if(num_nodes <= 0) return '~';

        //Make sure the random node-char is activated
        //Look for the next activated node-char
        while(!my_nodeset[my_randchar].isActivated())
            my_randchar = (my_randchar + 1) % MAX_NODES;

        return translateIndexChar(my_randchar);
    }

    /**
     * Returns the name/label for a randomly selected unactivated node.
     *
     * @return Gives the <code>char</code> name/label value for a randomly
     *         chosen unactivated node. If there are currently no unactivated
     *         nodes, the value returned is the invalid <code>char '~'</code>.
     */
    public char randomNewNode(){
        int my_randchar = (int) (Math.random() * MAX_NODES);

        //If all nodes are active, return a goof char
        if(num_nodes >= MAX_NODES) return '~';

        //Make sure the random node-char is deactivated
        //Look for the next deactivated node-char
        while(my_nodeset[my_randchar].isActivated())
            my_randchar = (my_randchar + 1) % MAX_NODES;

        return translateIndexChar(my_randchar);
    }

    /**
     * Returns the <code>my_nodeset</code> index that corresponds to the
     * specified name/label value.
     *
     * @param c Indicates the name/label of the node for which the index is to
     *          be found.
     * @return  Yields the index of the specified node in 
     *          <code>my_nodeset</code>. If the value passed to <code>c</code>
     *          is not a valid node name/label, this method returns 
     *          <code>-1</code>.
     */
    public int translateCharIndex(char c){
        //Capital letters are the first 26 indices
        if(Character.isUpperCase(c)) return ((int) (c - 'A'));

        //Lowercase letters are the next 26 indices
        if(Character.isLowerCase(c)) return ((int) (c - 'a') + 26);

        //Numbers are the final 10 indices
        if(Character.isDigit(c)) return ((int) (c - '0') + 52);

        //Otherwise we return a goof value
        return -1;
    }

    /**
     * Returns the name/label of the node at the specified index in 
     * <code>my_nodeset</code>.
     *
     * @param index Indicates the index in <code>my_nodeset</code> for which
     *              the name/label is to be found.
     * @return      Gives the name/label of the node at <code>index</code> in
     *              <code>my_nodeset</code>. If the value passed to
     *              <code>index</code> is invalid, this method returns 
     *              <code>'~'</code>.
     */
    public char translateIndexChar(int index){
        //Capital letters are the first 26 indices
        if((index >= 0) && (index < 26)) return ((char) (index + 'A'));

        //Lowercase letters are the next 26 indices
        if((index > 25) && (index < 52)) return ((char) (index - 26 + 'a'));

        //Numbers are the final 10 indices
        if((index > 51) && (index < 62)) return ((char) (index - 52 + '0'));

        //Otherwise we return a goof value
        return '~';
    }

 //private method is never used
    /*
     * Gives the Euclidean distance between the nodes at indices <code>a</code>
     * and <code>b</code> in <code>my_nodeset</code>.
     *
     * @param a Specifies the index of the first of the two nodes for which the
     *          Euclidean distance is to be calculated.
     * @param b Indicates the index of the  second of the two nodes for which
     *          the Euclidean distance is to be calculated.
     * @return  Yields the Euclidean distance between the two nodes. If either
     *          of the given indices is invalid, a 
     *          <code>RuntimeException</code> is generated.
     */
 /*   private double nodeDistance(int a, int b){
    //Make various safety checks
    if((a < 0) || (a > MAX_NODES))
        throw new RuntimeException("nodeDistance, A is out of range");
    if((b < 0) || (b > MAX_NODES))
        throw new RuntimeException("nodeDistance, B is out of range");
    if(!my_nodeset[a].isActivated() || !my_nodeset[b].isActivated())
        throw new RuntimeException("nodeDistance, inactive node refed");

    //d = sqrt{ (Xa - Xb)^2 + (Ya - Yb)^2 }
    return Math.sqrt
        (Math.pow(my_nodeset[a].getX() - my_nodeset[b].getX(), 2) + 
         Math.pow(my_nodeset[b].getY() - my_nodeset[b].getY(), 2));
    }
*/
    
 // this private method is never used
    /*
     * Returns the Euclidean distance between two points specified by their
     * Cartesian coordinates.
     *
     * @param x1 Indicates the x-coordinate of the first point for which the 
     *           Euclidean distance is to be found.
     * @param y1 Specifies the y-coordinate of the first point for which the
     *           Euclidean distance is to be found.
     * @param x2 Indicates the x-coordinate of the second point for which the
     *           Euclidean distance is to be found.
     * @param y2 Specifies the y-coordinate of the second point for which the 
     *           Euclidean distance is to be found.
     */
 /*   private double euclidianDistance(double x1, double y1,
                     double x2, double y2){
        return Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
    }
*/
    
    /**
     * Gives the <code>char</code> name/label for the next unactivated node.
     *
     * @return Yields the <code>char</code> value that serves as the unique
     *         name/label for the next unactivated node in this
     *         <code>VisualGraph</code>. Returns <code>'~'</code> if all 
     *         available nodes are already activated.
     */
    public char getNextNode(){
        int index = 0;
        while(my_nodeset[index].isActivated() && (index < MAX_NODES))
            index++;

        if(index == MAX_NODES) return '~';
        else return translateIndexChar(index);
    }

    /**
     * Changes any nodes in this <code>VisualGraph</code> with the color
     * indicated by <code>search</code> to have the color specified by
     * <code>replace</code>.
     *
     * @param search  Gives the color that should be changed to the new color.
     *                This value must be a six-digit hexadecimal
     *                <code>String</code> of the form <code>#123456</code>. The
     *                symbol '#' must be included.
     * @param replace Indicates the new color for the nodes that should be
     *                changed. This value must be a six-digit hexadecimal 
     *                <code>String</code> of the form <code>#123456</code>. The
     *                symbol '#' must be included.
     */
    public void gaigsChangeHexNodeColors(String search, String replace){
    for(int i = 0; i < num_nodes; i++){
        if(my_nodeset[i].getHexColor().compareTo(search) == 0){
        my_nodeset[i].setHexColor(replace);
        }
    }
    }

    /**
     * Changes any edges in this <code>VisualGraph</code> with the color
     * indicated by <code>search</code> to have the color specified by
     * <code>replace</code>.
     *
     * @param search  Gives the color that should be changed to the new color.
     *                This value must be a six-digit hexadecimal 
     *                <code>String</code> of the form <code>#123456</code>. The
     *                symbol '#' must be included.
     * @param replace Indicates the new color for the edges that should be
     *                changed. This value must be a six-digit hexadecimal
     *                <code>String</code> of the form <code>#123456</code>. The
     *                symbol '#' must be included.
     */
    public void gaigsChangeHexEdgeColors(String search, String replace){
    for(int i = 0; i < num_edges; i++){
        for(int j = 0; j < num_edges; j++){
        if(my_edgeset[i][j].getHexColor() == search)
            {
            my_edgeset[i][j].setHexColor(replace);
            }
        }
    }
    }

    /**
     * Returns the Euclidean distance between two points specified by their
     * Cartesian coordinates.
     *
     * @param x1 Indicates the x-coordinate of the first point for which the 
     *           Euclidean distance is to be found.
     * @param y1 Specifies the y-coordinate of the first point for which the
     *           Euclidean distance is to be found.
     * @param x2 Indicates the x-coordinate of the second point for which the
     *           Euclidean distance is to be found.
     * @param y2 Specifies the y-coordinate of the second point for which the 
     *           Euclidean distance is to be found.
     */
    public double gaigsDistPoints(double x1, double y1, double x2, double y2){
    return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * Initializes the heuristic values of the nodes based on their Euclidean
     * distances from the specified goal node.
     *
     * @param g Indicates the goal node for which the heuristic values should
     *          be initialized. This value is the goal node's index in
     *          <code>my_nodeset</code>.
     */
    public void initializeHValues(int g){
    heuristics = true;
    int h;

    for(int i = 0; i < MAX_NODES; i++){
        if(my_nodeset[i].isActivated()){
        h = ((int) (7 * gaigsDistPoints(my_nodeset[i].getX(), 
                        my_nodeset[i].getY(), 
                        my_nodeset[g].getX(), 
                        my_nodeset[g].getY())));
        if(h < 1 && i != g){
            h = 1;
        }
        my_nodeset[i].setHeuristic(h);
        }
    }
    }

    /**
     * Initializes the edge weights based on the the Euclidean distances
     * between their start and goal nodes.
     * The weights are partially based on Euclidean distance and partly
     * determined randomly.
     * <p>
     * Note that this method sets edge weights bidirectionally and is not 
     * intended for directed graphs.
     * </p>
     */
    public void initializeEdgeWeights(){
    Random rand = new Random();
    int edge;

    weighted = true;

    for(int i = 0; i < MAX_NODES; i++){
        for(int j = i; j < MAX_NODES; j++){
        if(my_edgeset[i][j].isActivated()){
            edge = (int) ((7 + rand.nextInt(13)) * 
                  gaigsDistPoints(my_nodeset[i].getX(),
                          my_nodeset[i].getY(),
                          my_nodeset[j].getX(),
                          my_nodeset[j].getY()));

            if(edge == 0){
            edge = rand.nextInt(2) + 1;
            }
            my_edgeset[i][j].setWeight(edge);
            my_edgeset[j][i].setWeight(edge);
        }
        }
    }
    }

    /**
     * Reinitializes the nodes after running a test algorithm for layout 
     * purposes.
     * This entails setting each node to be the color <code>#FFFFFF</code>
     * (white), making each node's <code>closed</code> data member 
     * <code>false</code>, setting each node's <code>cost</code> to be a large
     * number, and reseting each node's <code>pred</code> to the default value.
     * Heuristic values are left unchanged because it is assumed that any
     * changes to heuristic values are meant to bring about the desired 
     * operation when the actual script-producing program is run.
     */
    public void resetNodes(){
    int length = my_nodeset.length;

    for(int i = 0; i < length; i++){
        my_nodeset[i].setHexColor("#FFFFFF");
        my_nodeset[i].setClosed(false);
        my_nodeset[i].setCost(10000);
        my_nodeset[i].setPred('~');
    }
    }

    /** 
     * Runs the A* Algorithm on this <code>VisualGraph</code> to monitor if the
     * algorithm will have to reopen a closed node to find the shortest path to
     * the specified goal node from the indicated start node.
     * After calling this method, the nodes in the graph will contain the
     * information from running the search. Therefore, {@link #resetNodes()}
     * should be called before running the script-producing program to produce
     * the GAIGS animation so that the script-producing program will execute
     * correctly.
     * 
     * @param start Indicates the start node for this A* Search by giving its
     *              name/label.
     * @param goal  Specifies the goal node for this A* Search by giving its
     *              name/label.
     * @return      If a node was reopened, gives the shortest path from the
     *              start node to the goal node in the form <code>"ABC"</code>
     *              (the path from A to B to C). If a node was not reopened to
     *              find the shortest path, returns the empty
     *              <code>String</code>, <code>""</code>.
     */
    public String testAStar(char start, char goal){
    boolean reopen = false;
    String path = "";
    PriorityQueue <VisNode> open = new PriorityQueue <VisNode> (num_nodes * 2);
    VisNode current;
    VisNode successor;
    int currentI;
    int startI = translateCharIndex(start);
//    int goalI = translateCharIndex(goal);				// never used

    current = new VisNode(my_nodeset[startI]);
    current.setCost(0);
    open.add(current);

    try{
        current = (VisNode) open.remove();
    }
    catch(NoSuchElementException e){
        System.err.println("Tried to remove from empty list: " + 
                   e.toString());
    }
    currentI = translateCharIndex(current.getChar());
    while(current.getChar() != goal){
        if(!my_nodeset[currentI].isClosed()){
        current.setClosed(true);
        my_nodeset[currentI] = current;

        for(int n = 0; n < MAX_NODES; n++){
            if(my_edgeset[currentI][n].isActivated()){
            if(!my_nodeset[n].isClosed()){
                successor = new VisNode(my_nodeset[n]);
                successor.setCost
                (current.getCost() + (int) 
                 my_edgeset[currentI][n].getWeight());
                successor.setPred(current.getChar());
                open.add(successor);
            }else if((current.getCost() + 
                  (int) my_edgeset[currentI][n].getWeight()) < 
                 my_nodeset[n].getCost()){
                reopen = true;
                my_nodeset[n].setClosed(false);
                successor = new VisNode(my_nodeset[n]);
                successor.setCost
                (current.getCost() + (int)
                 my_edgeset[currentI][n].getWeight());
                successor.setPred(current.getChar());
                open.add(successor);
            }
            }
        }
        }

        try{
        current = (VisNode) open.remove();
        }
        catch(NoSuchElementException e){
        System.err.println("Open list is empty: " + e.toString());
        reopen = false;
        break;
        }
        currentI = translateCharIndex(current.getChar());
    }

    if(!reopen){
        return path;
    }else{
        my_nodeset[currentI] = current;

        path = goal + path;

        while(path.charAt(0) != start){
        path = 
            my_nodeset[translateCharIndex(path.charAt(0))].getPred() + 
            path;
        }

        return path;
    }
    }

    /** 
     * Runs the A* Algorithm on this <code>VisualGraph</code> to find the
     * shortest path to the specified goal node from the indicated start node.
     * After calling this method, the nodes in the graph will contain the
     * information from running the search. Therefore, {@link #resetNodes()}
     * should be called before running the script-producing program to produce
     * the GAIGS animation so that the script-producing program will execute
     * correctly.
     * 
     * @param start Indicates the start node for this A* Search by giving its
     *              name/label.
     * @param goal  Specifies the goal node for this A* Search by giving its
     *              name/label.
     * @return      Gives the shortest path from the start node to the goal 
     *              node in the form <code>"ABC"</code> (the path from A to B
     *              to C).
     */
    public String runAStar(char start, char goal){
    String path = "";
    PriorityQueue <VisNode> open = new PriorityQueue <VisNode> (num_nodes * 2);
    VisNode current;
    VisNode successor;
    int currentI;
    int startI = translateCharIndex(start);
 //   int goalI = translateCharIndex(goal);			// never used

    current = new VisNode(my_nodeset[startI]);
    current.setCost(0);
    open.add(current);

    try{
        current = (VisNode) open.remove();
    }
    catch(NoSuchElementException e){
        System.err.println("Tried to remove from empty list: " + 
                   e.toString());
    }
    currentI = translateCharIndex(current.getChar());
    while(current.getChar() != goal){
        if(!my_nodeset[currentI].isClosed()){
        current.setClosed(true);
        my_nodeset[currentI] = current;

        for(int n = 0; n < MAX_NODES; n++){
            if(my_edgeset[currentI][n].isActivated()){
            if(!my_nodeset[n].isClosed()){
                successor = new VisNode(my_nodeset[n]);
                successor.setCost
                (current.getCost() + (int) 
                 my_edgeset[currentI][n].getWeight());
                successor.setPred(current.getChar());
                open.add(successor);
            }else if((current.getCost() + 
                  (int) my_edgeset[currentI][n].getWeight()) < 
                 my_nodeset[n].getCost()){
                my_nodeset[n].setClosed(false);
                successor = new VisNode(my_nodeset[n]);
                successor.setCost
                (current.getCost() + (int)
                 my_edgeset[currentI][n].getWeight());
                successor.setPred(current.getChar());
                open.add(successor);
            }
            }
        }
        }

        try{
        current = (VisNode) open.remove();
        }
        catch(NoSuchElementException e){
        System.err.println("Open list is empty: " + e.toString());
        }
        currentI = translateCharIndex(current.getChar());
    }

    my_nodeset[currentI] = current;

    path = goal + path;
    
    while(path.charAt(0) != start){
        path = my_nodeset[translateCharIndex(path.charAt(0))].getPred() + 
        path;
    }
    
    return path;
    }

    /**
     * Writes this <code>VisualGraph</code> to the given 
     * <code>PrintWriter</code> output stream using the original GAIGS
     * specifications for Graphs/Networks.
     * If the graph is weighted, a Network structure will be produced;
     * otherwise, a Graph structure will be printed to the indicated output
     * stream. This method also takes the slide's title so that it can be
     * inserted into the appropriate place in the snapshot.
     *
     * @param out   Specifies the output stream to which the Graph/Network
     *              snapshot is to be written.
     * @param title Indicates the title for the Graph/Network snapshot that is
     *              written to this output stream.
     */
    public void writeGAIGSGraph(PrintWriter out, String title){
    if(weighted){
        out.println("Network");
    }else{
        out.println("Graph");
    }
    if(heuristics){
        out.println("2\n" + title + "\n***\\***");
    }else{
        out.println("1\n" + title + "\n***\\***");
    }
    for(int n = 0; n < MAX_NODES; n++){
        if(my_nodeset[n].isActivated()){
        out.println((n + 1) + " " + 
                (my_nodeset[n].getX()) + " " + 
                (my_nodeset[n].getY()));
        for(int e = 0; e < MAX_NODES; e++){
            if(my_edgeset[n][e].isActivated()){
            if(directed){
                out.println("\\A\\" + 
                    my_edgeset[n][e].getHexColor() +
                    (e + 1));
            }else{
                out.println("\\" + 
                    my_edgeset[n][e].getHexColor() + 
                    (e + 1));
            }
            if(weighted){
                out.println((int) my_edgeset[n][e].getWeight());
            }
            }
        }
        out.println("32767");
        out.print("\\" + my_nodeset[n].getHexColor());
        out.println(my_nodeset[n].getChar());
        if(heuristics){
            out.println(my_nodeset[n].getHeuristic());
        }
        }
    }
    out.println("***^***");
    }

    /** 
     * Writes this <code>VisualGraph</code> to the given 
     * <code>PrintWriter</code> output stream using the new GAIGS XML format.
     * This method only writes the <code>&lt;graph&gt;</code> portion of a
     * snapshot so that other structures can accompany the graph in the
     * snapshot.
     *
     * @param out Indicates the output stream to which the graph should be 
     *            written.
     */
    public void writeGAIGSXMLGraph(PrintWriter out){
    out.println("<graph weighted = \"" + weighted + "\">");
    out.println("<bounds x1 = \"" + x1 + "\" y1 = \"" + y1 +
            "\" x2 = \"" + x2 + "\" y2 = \"" + y2 + 
            "\" fontsize = \"" + font_size + "\"/>");

    for(int n = 0; n < MAX_NODES; n++){
        if(my_nodeset[n].isActivated()){
        out.println("<vertex color = \"" + 
                my_nodeset[n].getHexColor() + "\" id = \"" + n + 
                "\">");

        if(heuristics){
            out.println("<label>" + my_nodeset[n].getChar() + "\n" + 
                my_nodeset[n].getHeuristic() + "</label>");
        }else{
            out.println("<label>" + my_nodeset[n].getChar() + 
                "</label>");
        }

        out.println("<position x = \"" + my_nodeset[n].getX() + 
                "\" y = \"" + my_nodeset[n].getY() + "\"/>");

        for(int e = 0; e < MAX_NODES; e++){
            if(my_edgeset[n][e].isActivated()){
            out.println("<edge target = \"" + e + 
                    "\" directed = \"" + directed + 
                    "\" color = \"" + 
                    my_edgeset[n][e].getHexColor() + "\">");
            if(weighted){
                out.println("<label>" + 
                    (int)my_edgeset[n][e].getWeight() + 
                    "</label>");
            }
            out.println("</edge>");
            }
        }
        out.println("</vertex>");
        }
    }
    out.println("</graph>");
    }

    /**
     * Reads in a showfile and extracts the information contained within the
     * first set of <code>&lt;graph&gt;</code> tags to initialize this
     * <code>VisualGraph</code>.
     * Note that this method is meant to read in a graph that was written to
     * the showfile using <code>writeGAIGSXMLGraph</code> and may not work for
     * graph showfiles produced in another manner because it assumes that all
     * attributes will be explicitly listed.
     *
     * @param filename               Indicates the file from which the graph
     *                               should be read.
     * @throws FileNotFoundException Indicates that the file specified by
     *                               <code>filename</code> could not be found
     *                               when trying to open it.
     * @throws IOException           Indicates that the format of the 
     *                               <code>&lt;graph&gt;</code> specification
     *                               was not as expected. This method is only
     *                               assured to work with showfiles produced
     *                               using <code>writeGAIGSXMLGraph</code>.
     */
    public void readGAIGSXMLGraph(String filename)throws FileNotFoundException,
                             IOException{
    BufferedReader in = new BufferedReader(new FileReader(filename));
    String line = in.readLine();
    String value;
    int index;
    int node, target;

    initializeGraph();

    while(!line.substring(1, 6).equals("graph")){
        line = in.readLine();
    }

    if(line.charAt(19) == 't'){
        weighted = true;
    }

    line = in.readLine();
    index = line.indexOf('"');

    for(int i = 0; i < 5; i++){
        value = line.substring(index + 1, line.indexOf('"', index + 1));

        switch(i){
        case 0: 
        x1 = Double.parseDouble(value);
        break;
        case 1:
        y1 = Double.parseDouble(value);
        break;
        case 2:
        x2 = Double.parseDouble(value);
        break;
        case 3:
        y2 = Double.parseDouble(value);
        break;
        case 4:
        font_size = Double.parseDouble(value);
        break;
        }

        index = line.indexOf('"', line.indexOf('"', index + 1) + 1);

        if(index == -1){
        break;
        }
    }

    line = in.readLine();

    while(line.substring(1, 7).equals("vertex")){
        num_nodes++;
        index = line.indexOf('"');
        value = line.substring(index + 1, line.indexOf('"', index + 1));
        index = line.indexOf('"', line.indexOf('"', index + 1) + 1);
        node = 
        Integer.parseInt(line.substring(index + 1, 
                        line.indexOf('"', index + 1)));
        my_nodeset[node].activate();
        my_nodeset[node].setChar(translateIndexChar(node));
        my_nodeset[node].setHexColor(value);

        line = in.readLine();
        line = in.readLine();

        if(line.charAt(0) != '<'){
        my_nodeset[node].setHeuristic
            (Integer.parseInt(line.substring(0, line.indexOf('<'))));
        heuristics = true;
        line = in.readLine();
        }

        index = line.indexOf('"');
        value = line.substring(index + 1, line.indexOf('"', index + 1));
        my_nodeset[node].setX(Double.parseDouble(value));
        index = line.indexOf('"', line.indexOf('"', index + 1) + 1);
        value = line.substring(index + 1, line.indexOf('"', index + 1));
        my_nodeset[node].setY(Double.parseDouble(value));

        line = in.readLine();

        while(line.substring(1, 5).equals("edge")){
        index = line.indexOf('"');
        target = 
            Integer.parseInt(line.substring(index + 1, line.indexOf
                            ('"', index + 1)));
        index = line.indexOf('"', line.indexOf('"', index + 1) + 1);
        if(line.charAt(index + 1) == 't'){
            index = 
            line.indexOf('"', line.indexOf('"', index + 1) + 1);
            value = line.substring(index + 1, 
                       line.indexOf('"', index + 1));
            my_edgeset[node][target].activate();
            my_edgeset[node][target].setHexColor(value);
            directed = true;
            num_edges++;

            line = in.readLine();

            if(!line.equals("</edge>")){
            value = line.substring(line.indexOf('>') + 1,
                           line.indexOf('<', 1));
            my_edgeset[node][target].setWeight
                (Integer.parseInt(value));
            line = in.readLine();
            }

            line = in.readLine();
        }else{
            index = 
            line.indexOf('"', line.indexOf('"', index + 1) + 1);
            value = line.substring(index + 1,
                       line.indexOf('"', index + 1));
            my_edgeset[node][target].activate();
            my_edgeset[target][node].activate();
            my_edgeset[node][target].setHexColor(value);
            my_edgeset[target][node].setHexColor(value);
            if(node < target){
            num_edges++;
            }

            line = in.readLine();

            if(!line.equals("</edge>")){
            value = line.substring(line.indexOf('>') + 1,
                           line.indexOf('<', 1));
            my_edgeset[node][target].setWeight
                (Integer.parseInt(value));
            my_edgeset[target][node].setWeight
                (Integer.parseInt(value));
            line = in.readLine();
            }

            line = in.readLine();
        }
        }

        line = in.readLine();
    }
    }

    /**
     * Reads in a showfile in order to extract the start and goal nodes from
     * the first <code>&lt;title&gt;</code> element in the showfile.
     * This method was intended to accompany the <code>readGAIGSXMLGraph</code>
     * method for the AStarSearch, BestFirstSearch, and LeastCostSearch
     * programs. It will only be useful to find the start and goal nodes in a
     * graph-search showfile if the first title in the show is of the form:
     * <code>". . . Start Node {start node's name} . . .
     * Goal Node {goal node's name} . . ."</code>.
     * 
     * @param filename                Indicates the file from which the title 
     *                                should be read.
     * @return                        Returns the indices in
     *                                <code>my_nodeset</code> of the start and
     *                                goal nodes in a two-element array. The 
     *                                first element is the start node's index,
     *                                and the second element is the goal node's
     *                                index.
     * @throws FileNotFoundException  Indicates that the file specified by
     *                                <code>filename</code> could not be found
     *                                when trying to open it.
     * @throws IOException            Indicates that the first title was not in
     *                                the correct format for this method to
     *                                extract the start and goal nodes.
     * @throws NoSuchElementException Indicates that the first title was not in
     *                                the correct format for this method to
     *                                extract the start and goal nodes.
     */
    public int[] readStartGoal(String filename)throws FileNotFoundException,
                              IOException,
                              NoSuchElementException{
    BufferedReader in = new BufferedReader(new FileReader(filename));
    String line = in.readLine();
    int[] start_and_goal = { -1, -1 };

    while(!line.substring(1, 6).equals("title")){
        line = in.readLine();
    }

    StringTokenizer title = new StringTokenizer(line);
    line = title.nextToken().toLowerCase();

    while(start_and_goal[0] == -1){
        while(!line.equals("start") && title.hasMoreTokens()){
        line = title.nextToken().toLowerCase();
        }
        
        if(line.equals("start")){
        if(title.nextToken().toLowerCase().equals("node")){
            line = title.nextToken();
            start_and_goal[0] = translateCharIndex(line.charAt(0));
        }
        }else{
        // If the above while loop exited without line equaling
        // "start," then title has no more tokens, and the start and
        // goal nodes were not properly specified in the first title.
        throw new IOException("Could not find start and goal nodes.");
        }
    }

    line = title.nextToken().toLowerCase();

    while(start_and_goal[1] == -1){
        while(!line.equals("goal") && title.hasMoreTokens()){
        line = title.nextToken().toLowerCase();
        }

        if(line.equals("goal")){
        if(title.nextToken().toLowerCase().equals("node")){
            line = title.nextToken();
            start_and_goal[1] = translateCharIndex(line.charAt(0));
        }
        }else{
        // If the above while loop exited without line equaling
        // "goal," then title has no more tokens, and the start and
        // goal nodes were not properly specified in the first title.
        throw new IOException("Could not find start and goal nodes.");
        }
    }

    return start_and_goal;
    }
}

