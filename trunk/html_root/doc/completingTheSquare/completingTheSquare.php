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

print("<center><H2>Solving a quadratic equation<BR>");
print("by<BR>"); 
print("\"<font color='blue'>completing the square</font>\"</H2>");
print("<BR><BR>");

$pgm = array(
"Move c to the right side of the equation",
"Divide both sides by a, then simplify",
"Add a well-chosen constant to both sides",
"Factor the left side of the equation",
"Simplify the right side of the equation",
"Take the square root of each side",
"Move the highlighted fraction to the right side"
);
print("<table>");
for($i = 0; $i < count($pgm); $i++)
{
  $j = $i+1;
  print("<tr valign='top'><td width='10'></td><td align='left'>$j</td><td width='10'></td><td>");
  if($i==$line)
      if ($i==2)
          print("<font color='blue'><b>$pgm[$i]</b></font><br>");
      else	  
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