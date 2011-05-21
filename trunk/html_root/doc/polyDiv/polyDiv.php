 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DT/xhtml1-transitional.dtd">

<html xmlns = "http://www.w3.org/1999/xhtml">
<head><title>Program</title></head>
<body><pre><?php
        if(isset($_GET['line']))
        {
                $line = $_GET['line'];
        }

print("<center><table width=\"90%\" border='1' bgcolor=\"#DDDDDD\"><tr><td align='center'><H2>Polynomial division</H2></td></tr></table><BR>");

print("</center><BR>");


$pgm = array( 

"Divide the highest-degree term of the divisor (red box) into the highest-degree term remaining in the dividend (blue box) to obtain the next term in the quotient.",

"Multiply the divisor (blue box) by the quotient term obtained at the previous step (red box).",

"Subtract the polynomial obtained at the previous step (blue box) from the corresponding terms in the remaining dividend (red box).<BR><BR> Then, if there are no more terms in the dividend to bring down, go to step 5. Otherwise, go to step 4.",

"Bring down the next term in the dividend, then go to step 1.",

"The division is now complete. The quotient and remainder are boxed in red and blue, respectively."

);


print("<table>"); 


for($i = 0; $i < count($pgm); $i++) 
{
    $j = $i+1; 
    print("<tr valign='top'><td width='10'></td><td align='left'><font size='+0'>$j.</font></td><td width='10'></td><td>"); 

    if($i==$line)
       print("<font color='red' size='+0'>$pgm[$i]</font><br>");
    else
      print("<font size='+0'>$pgm[$i]</font><br>");
  print("</td></tr>");
}
print("</table>");


print("</font></center>");
?></pre>
</body>
</html>