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
 * A RefBox represents a single box within a RefStruct.  It contains the RefBox's label, color, and
 * either the data it contains or the RefStruct it references.
 */
public class RefBox
{
	private String label, color;
	private Object data;
	private GAIGSRefStruct struct;
	
	/**
	 * Create an empty, black RefBox with the given label.
	 * @param l The label that you want to have above the RefBox.
	 */
	public RefBox(String l)
	{
		setVariables(l,"black","",null);
	}
	
	/**
	 * Create an empty RefBox of the given color witht the given label.
	 * @param l The label that you want to have above the RefBox.
	 * @param c The color that you want the RefBox to be.
	 */
	public RefBox(String l,String c)
	{
		setVariables(l,c,"",null);
	}
	
	/**
	 * Create a black RefBox with the given label and containing the given data.
	 * @param l The label that you want to have above the RefBox.
	 * @param d The data that you want inside of the RefBox.
	 */
	public RefBox(String l,Object d)
	{
		setVariables(l,"black",d,null);
	}
	
	/**
	 * Create a black RefBox with the given label and an arrow pointing to the given RefStruct.
	 * @param l The label that you want to have above the RefBox.
	 * @param s The RefStruct that this RefBox references.
	 */
	public RefBox(String l,GAIGSRefStruct s)
	{
		setVariables(l,"black",null,s);
	}
	
	/**
	 * Create a RefBox of the given color with the given label above it containing the given data.
	 * @param l The label that you want to have above the RefBox.
	 * @param c The color that you want the RefBox to be.
	 * @param d The data the you want inside of the RefBox.
	 */
	public RefBox(String l,String c,Object d)
	{
		setVariables(l,c,d,null);
	}
	
	/**
	 * Create a RefBox of the given color with the given label above it with an arrow pointing to the given RefStruct.
	 * @param l The label that you want to have above the RefBox.
	 * @param c The color that you want the RefBox to be.
	 * @param s The data that you want inside of the RefBox.
	 */
	public RefBox(String l,String c,GAIGSRefStruct s)
	{
		setVariables(l,c,null,s);
	}
	
	private void setVariables(String l,String c,Object d,GAIGSRefStruct s)
	{
		label = l;
		color = c;
		data = d;
		struct = s;
	}
	
	/**
	 * Returns the RefBox's label
	 * @return The RefBox's label
	 */
	public String getLabel()
	{
		return label;
	}
	
	/**
	 * Returns the RefBox's color
	 * @return The RefBox's color
	 */
	public String getColor()
	{
		return color;
	}
	
	/**
	 * Returns the RefBox's data
	 * @return The RefBox's data
	 */
	public Object getData()
	{
		return data;
	}
	
	/**
	 * Returns the RefStruct that is referenced by the RefBox
	 * @return The RefStruct that is referenced by the RefBox
	 */
	public GAIGSRefStruct getReference()
	{
		return struct;
	}
	
	/**
	 * Returns true iff the RefBox contains data
	 * @return If the RefBox contains data
	 */
	public boolean hasData()
	{
		return (data!=null);
	}
}