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
 * Build a GAIGSRefStruct adhering to the following DTD
 *
 *      <!-- REFSTRUCT -->                                  
 *                                                          
 * <!ELEMENT refstructroot (name?, bounds?, (refbox)+)>     
 *                                                          
 * <!ELEMENT refstruct ((refbox)+)>                         
 *                                                          
 * <!ELEMENT refbox ((refstruct)?)>                         
 * <!ATTLIST refbox color CDATA "black"                     
 *                  boxname CDATA ""                        
 * 		            boxdata CDATA ""                        
 * 		            hasarrow CDATA "false">                 
 */


public class GAIGSRefStruct extends GAIGSbase {

	private double x1,y1,x2,y2,fontSize;
	private RefBox[] refboxes;

	/**
	 * Create a RefStruct containing the given RefBoxes with the default bounds and font size.
	 * @param refboxes The array containing all of the RefBoxes that comprise this RefStruct.
	 */
	public GAIGSRefStruct(RefBox[] refboxes)
	{
		setVariables(refboxes,0.0,0.0,1.0,1.0,0.5);
	}
	
	/**
	 * Create a RefStruct with the given Refboxes and he given bounds and font size.
	 * @param refboxes The array containing all of the RefBoxes that comprise the RefStruct.
	 * @param x1 The lower x bound of the visualization.
	 * @param y1 The lower y bound of the visualization.
	 * @param x2 The upper x bound of the visualization.
	 * @param y2 The upper y bound of the visualization.
	 * @param fontSize The font size of the visualization title.
	 */
	public GAIGSRefStruct(RefBox[] refboxes,double x1,double y1,double x2,double y2,double fontSize)
	{
		setVariables(refboxes,x1,y1,x2,y2,fontSize);
	}
	
	private void setVariables(RefBox[] refboxes,double x1,double y1,double x2,double y2,double fontSize)
	{
		this.refboxes = refboxes;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.fontSize = fontSize;
	}
	
	/**
	 * Returns the array of RefBoxes contained in this RefStruct.
	 * @return The array of RefBoxes contained in the RefStruct.
	 */
	public RefBox[] getRefBoxes()
	{
		return refboxes;
	}
		
//---------------------- XML Methods -------------------------------------------

    /**
     * Creates and returns GAIGS XML code for the current state of the ref struct
     * 
     * @return     A String containing GAIGS XML code for the ref struct
     */   
    public String toXML() 
    {
    	String snapShot = "";
    	snapShot = snapShot+"<refstructroot>\n";
    	snapShot = snapShot+"<bounds x1=\""+x1+"\" y1=\""+y1+"\" x2=\""+x2+"\" y2=\""+y2+"\" fontsize=\""+fontSize+"\"/>\n";
    	for(int i = 0; i < refboxes.length; i++)
    	{
    		RefBox rb = refboxes[i];
    		snapShot = snapShot+"<refbox boxname=\""+rb.getLabel()+"\" boxdata=\""+rb.getData()+"\" color=\""+rb.getColor()+"\" hasarrow=\""+!rb.hasData()+"\">\n";
    		if(!rb.hasData()) snapShot = snapShot+auxToXML(rb.getReference());
    		snapShot = snapShot+"</refbox>\n";
    	}
    	snapShot = snapShot+"</refstructroot>\n";
    	return snapShot;
    }
    
    private String auxToXML(GAIGSRefStruct grs)
    {
    	String snapShot = "";
    	snapShot = snapShot+"<refstruct>\n";
    	RefBox[] rbs = grs.getRefBoxes();
    	for(int i = 0; i < rbs.length; i++)
    	{
    		RefBox rb = rbs[i];
    		snapShot = snapShot+"<refbox boxname=\""+rb.getLabel()+"\" boxdata=\""+rb.getData()+"\" color=\""+rb.getColor()+"\" hasarrow=\""+!rb.hasData()+"\">\n";
    		if(!rb.hasData()) snapShot = snapShot+auxToXML(rb.getReference());
    		snapShot = snapShot+"</refbox>\n";
    	}
    	snapShot = snapShot+"</refstruct>\n";
    	return snapShot;
    }
}