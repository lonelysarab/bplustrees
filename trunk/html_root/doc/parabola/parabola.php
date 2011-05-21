 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DT/xhtml1-transitional.dtd">

<html xmlns = "http://www.w3.org/1999/xhtml">
<head><title>Program</title></head>
<body><pre><?php
        if(isset($_GET['file']))
        {
                $file = $_GET['file'];
        }
        if(isset($_GET['line']))
        {
                $line = $_GET['line'];
        }
        if(isset($_GET['aval']))
        {
                $aval = $_GET['aval'];
        }
        if(isset($_GET['bval']))
        {
                $bval = $_GET['bval'];
        }
        if(isset($_GET['cval']))
	{
                $cval = $_GET['cval'];
	}

print("<center><H2>Graphing a parabola</H2><BR>");

if ($line==-1)
    print("<font color='red'>Given a quadratic equation y = f(x) = ax<sup><font size='-1'>2</font></sup> + bx + c:</font>");
else
    print("Given a quadratic equation y = f(x) = ax<sup><font size='-1'>2</font></sup> + bx + c:");

print("<BR><BR>");
$pgm = array(

"Determine the curvature of the parabola.",
"Compute the x-coordinate of the vertex.",
"Compute the y-coordinate of the vertex.",
"Compute the x-intercept(s).",
"Compute the y-intercept.",
"Graph the parabola."
);
print("<table>");
for($i = 0; $i < count($pgm); $i++)
{
  $j = $i+1;
  print("<tr valign='top'><td width='10'></td><td align='left'>$j</td><td width='10'></td><td>");
  if($i==$line)
      print("<font color='red'>$pgm[$i]</font><br>");
  
  else
      print("$pgm[$i]<br>");
  print("</td></tr>");
}
print("</table>");

print("<BR><BR><BR>");
print("<b>Coefficients of this quadratic equation</b><br><BR>");
print("<table>");
print("<tr><td align='right'>a =</td><td width='5'></td><td align='right'>$aval</td></tr>");
print("<tr><td align='right'>b =</td><td width='5'></td><td align='right'>$bval</td></tr>");
print("<tr><td align='right'>c =</td><td width='5'></td><td align='right'>$cval</td></tr>");
print("</table>");

print("</center>");
?></pre>
</body>
</html>