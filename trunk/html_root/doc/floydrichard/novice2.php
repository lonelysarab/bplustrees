<?php
	$h_name = '<font class=h>through</font>';
	$i_name = '<font class=i>start</font>';
	$j_name = '<font class=j>end</font>';
?>
<html>
<head>
<style type="text/css">
	<!--
		body
		{
			background-color: #FFFFFF;
		}

		font.h
		{
			font-weight: bold;
			color: #00FF00;
		}

		font.i
		{
			font-weight: bold;
			color: #0066FF;
		}

		font.j
		{
			font-weight: bold;
			color: #FF9933;
		}

		font.lt_blue
		{
			background-color: #99FFFF;
			/*
			color: #99FFFF;
			font-weight: bold;
			*/
		}
		font.yellow
		{
			background-color: #EFEF00;
			/*
			color: #99FFFF;
			font-weight: bold;
			*/
		}


		font.lt_orange
		{
			font-weight: bold;
			color: #FFCC33;
		}

		table
		{
			padding : 4;
			border : 1px solid black;
			text-align : center;
		}

		th
		{
			border : 1px solid black;
			padding : 8;
		}

		td
		{
			border : 1px solid black;
		}
	-->
</style>
</head>
<body>
<h2>Floyd's Algorithm (Novice)</h2>
<p>
<p>Here's some pseudo code outlining Floyd's algorithm:</p>
<table align=center cellpadding=4>
<tr align=left><td><code><pre>
for ( <?php print $h_name; ?> = A to endNode ) {
    for ( <?php print $i_name; ?> = A to endNode ) {
        for ( <?php print $j_name; ?> = A to endNode ) {
            array[<?php print $i_name; ?>][<?php print $j_name; ?>] =
                min ( array[<?php print $i_name; ?>][<?php print $j_name; ?>],
                    array[<?php print $i_name; ?>][<?php print $h_name; ?>]
                    + array[<?php print $h_name; ?>][<?php print $j_name; ?>] );
        }
    }
}</pre></code></td></tr></table>
<p></p>
<hr>
<p>
The next table illustrates the variable values for each iteration of floyd's for a graph with three verteces. To reduce many superfluous comparisons we will only display snapshots where we have a unique vertex for all three variables (<?php print $h_name; ?>, <?php print $i_name; ?> &amp; <?php print $j_name; ?>). In the table these particular comparisons are shaded <font class=lt_blue>light blue</font>.
</p>
<table align=center cellspacing=0 border=0>
<tr><th><?php print $h_name; ?></th><th><?php print $i_name; ?></th><th><?php print $j_name; ?></th><th>see if there's a cheaper path</th><th>count</th></tr>
<?php
	$vertexes = 3;
	$count = 0;
	for($h = 0; $h < $vertexes; $h++)
	{
		for($i = 0; $i < $vertexes; $i++)
		{
			for($j = 0; $j < $vertexes; $j++)
			{
				$hc = chr($h + 65);
				$ic = chr($i + 65);
				$jc = chr($j + 65);
				if($h != $i && $h != $j && $i != $j)
					print '<tr bgcolor=#CCFFFF>';
				else
					print '<tr>';
				print "<td>$hc</td><td>$ic</td><td>$jc</td>";
				print "<td>from $ic to $jc through $hc</td>";
				print '<td>' . ++$count . "</td></tr>\n";
			}
		}
	}
?>
</table>
<p>
You can see from the table that a graph with three verteces will require twenty seven comparisons. The number of evaluations made is equal to the number of verteces raised to the power of three, and we say Floyds algorithm is <em><big>O</big>(n^3)</em>.
</p>
<hr>
<p>
The following illustrations outline how the animation will depict floyds algorithm.
This first image shows the first comparison the animation will display (remember we skip comparisons whenever two or more of our variables have the same value).
In this example we're going to compare the value in the array at position <font class=lt_orange>(B, C)</font> with the value obtained by summing the array values in positions <font class=i>(B, A)</font> and <font class=h>(A, C)</font>.
</p>
<table align=center><tr><td><img src=no_path.gif><br />
<b><font class=lt_orange>(B, C)</font> = min ( <font class=lt_orange>3</font>, <font class=i>Big</font> + <font class=h>Big</font> )</b>
</td></tr></table>
<p>
Three is smaller than two big numbers so we didn't find a cheaper path in this case and the weight for cell <font class=lt_orange>(B, C)</font> remains three. You can look at the corresponding graph to get an idea why this makes sense; there's a path of weight three between node B and C and no cheaper route going first from B to A and then from A to C.
</p>
<hr>
<p>
Further on the algorithm has discovered a shorter path from D to B and colored the verteces along the way (in order) from <font class=i>blue</font> to <font class=h>green</font>, to <font class=j>orange</font> (floyd's algorithm does not record this information, we're just displaying it to aid understanding).
Note that the cell weight at <font class=j>(D, B)</font> was Big in the prior iteration of the loop.
</p>
<table align=center><tr><td><img src=short_path.gif><br>
<b><font class=j>(D, B)</font> = min ( <font class=j>Big</font>, <font class=i>4</font> + <font class=h>3</font> )</b>
</td></tr></table>
<p>
</p>
<hr>
<p>
<!-- You may have wondered why some of the cells in the last diagram were shaded <font class=lt_blue>light blue</font>.
As the alogorithm loops it looks for cheaper paths via the <font class=h>though</font> vertex.
In the current snapshot every path is the shortest possible if possible by going through vertex A (and some through B), so to remind you which
Need a little help here.
-->
Once a cell is shaded <font class=lt_blue>light blue</font> you are guaranteed to have the cheapest path for each cell on the same row, which may include any <font class=lt_blue>light blue</font> cell as an interior vertex (edge along the path).
</p><p>
A cell in the array is shaded <font class=lt_blue>light blue</font> when we have checked all the paths
</p>
<p>
In this example we have found a cheaper path that makes use of a previously discovered cheaper path from D to B.
</p>
<table align=center><tr><td><img src=long_path.gif></td></tr></table>
<p></p>
<hr>
<p>
Whenever the <font class=i>blue</font> or <font class=h>green</font> cells have a value other than "Big", it represents the cost of a valid path in the graph. These paths are highlighted in the graph with edges of the same color as the cell.
</p>
<table align=center><tr><td><img src=no_path2.gif><br>
<b><font class=lt_orange>(B, E)</font> = min ( <font class=lt_orange>4</font>, <font class=i>3</font> + <font class=h>3</font> )</b>
</td></tr></table>
<p></p>
<hr>
<p>Sometimes the <font class=i>blue</font> and <font class=h>green</font> paths share edges in common. When this happens these edges are colored <font class=yellow>yellow</font> as in this example.</p>
<table align=center><tr><td><img src=crossed_path.gif><br>
<b><font class=lt_orange>(A, B)</font> = min ( <font class=lt_orange>3</font>, <font class=i>7</font> + <font class=h>10</font> )</b>
</td></tr></table>
<script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
</script>
<script type="text/javascript">
_uacct = "UA-3174792-1";
urchinTracker();
</script>
</body>
</html>
