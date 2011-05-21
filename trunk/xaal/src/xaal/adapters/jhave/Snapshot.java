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

package xaal.adapters.jhave;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import xaal.objects.Animation;
import xaal.objects.Xaal;
import xaal.objects.animation.AnimationOperation;
import xaal.objects.animation.NarrativeOperation;
import xaal.objects.animation.SeqOperation;
import xaal.objects.animation.ParOperation;

class Snapshot {

    public String narrative;
    public ArrayList graphicals;
    public ArrayList forward;
    public ArrayList backward;
    public Font font = null;

    Snapshot(String narrative, ArrayList forwardOps)
    {
	this.narrative = narrative;
	this.forward = forwardOps;
    }

    Snapshot(String narrative, ArrayList gr, ArrayList f, ArrayList b)
    {
	this(narrative, f);
	this.graphicals = gr;
	this.backward = b;
    }

    public String getNarrative() { return narrative; }
}
