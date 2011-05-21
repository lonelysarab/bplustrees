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

/**
 * This class maintains the information for a single edge in the 
 * <code>Tree</code> data structure. An <code>Edge</code> object inherits
 * from the <code>VisEdge</code> class and adds additional data members and
 * methods to allow these edges to be used in binary and general trees.
 * <p>
 * In order to use any of these tree classes in a script-producing program, the
 * script-producing program must import the package <code>exe</code>.
 * </p>
 *
 * @author Sven Moen (original author)
 * @author Ethan Dereszynski (adaptations)
 * @author Ben Tidman (GAIGS adaptations)
 * @author Andrew Jungwirth (more GAIGS adaptations and Javadoc comments)
 */
public class Edge extends VisEdge{
    /* DATA: */

    // The starting and ending Node of the edge
    private TreeNode start;
    private TreeNode end;

    /* METHODS: */

    /**
     * Constructs an <code>Edge</code> with default values.
     */
    public Edge(){}

    /**
     * Constructs an <code>Edge</code> by specifying the <code>TreeNode</code>
     * from which it begins and the <code>TreeNode</code> at which it ends.
     *
     * @param s Indicates the <code>TreeNode</code> at which this 
     *          <code>Edge</code> begins. By convention, this is the parent
     *          node.
     * @param e Indicates the <code>TreeNode</code> at which this
     *          <code>Edge</code> ends. By convention, this is the child node.
     */
    public Edge(TreeNode s, TreeNode e){
	start = s;
	end = e;
    }

    /**
     * Assigns the starting <code>TreeNode</code> for this <code>Edge</code>.
     *
     * @param s Specifies the <code>TreeNode</code> at which this
     *          <code>Edge</code> begins. By convention, this is the parent
     *          node. A reference to <code>s</code> is assigned to 
     *          <code>start</code>.
     */
    public void setStart(TreeNode s){ start = s; }

    /**
     * Assigns the ending <code>TreeNode</code> for this <code>Edge</code>.
     *
     * @param e Specifies the <code>TreeNode</code> at which this 
     *          <code>Edge</code> ends. By convention, this is the child node.
     *          A reference to <code>e</code> is assigned to <code>end</code>.
     */
    public void setEnd(TreeNode e){ end = e; }

    /**
     * Accesses the starting <code>TreeNode</code> for this <code>Edge</code>.
     *
     * @return Gives the <code>TreeNode</code> reference stored in 
     *         <code>start</code>. If <code>start</code> has not been
     *         initialized, returns <code>null</code>.
     */
    public TreeNode getStart(){ return start; }

    /**
     * Accesses the ending <code>TreeNode</code> for this <code>Edge</code>.
     *
     * @return Gives the <code>TreeNode</code> reference stored in
     *         <code>end</code>. If <code>end</code> has not been initialized,
     *         returns <code>null</code>.
     */
    public TreeNode getEnd(){ return end; }
}

