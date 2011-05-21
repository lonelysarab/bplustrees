 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DT/xhtml1-transitional.dtd">

<html xmlns = "http://www.w3.org/1999/xhtml">
<head><title>Program</title></head>
<body><pre><?php
        if(isset($_GET['line']))
        {
                $line = $_GET['line'];
        }

print("<center><table width=\"90%\" border='1' bgcolor=\"#DDDDDD\"><tr><td align='center'><H2>Synthetic division of polynomials</H2></td></tr></table><BR>");

print("</center><BR>");


$pgm = array( 

"Initialize the first row in the table of coefficients with the value of <i>c</i> and the coefficients of the dividend.",

"Bring down the first coefficient of the dividend.",

"Multiply <i>c</i> with the value obtained at the previous step, and write the result in the next column, under the next coefficient of the dividend.",

"Add the number obtained at the previous step and the number above it in the table. Then, if the rightmost column is full, go to step 5. Otherwise, go to step 3.", 

"The division is now complete."

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