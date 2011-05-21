 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DT/xhtml1-transitional.dtd">

<html xmlns = "http://www.w3.org/1999/xhtml">
<head><title>Program</title></head>
<body><pre><?php
        if(isset($_GET['line']))
        {
                $line = $_GET['line'];
        }


print("<center><table width=\"90%\" border='1' bgcolor=\"#DDDDDD\"><tr><td align='center'><H2><i>Point-slope equation of a line</i></H2><H3><i>y</i> - <i>y<sub><font size='-1'>i</font></sub></i> = <i>m</i>(<i>x</i> - <i>x<sub><font size='-1'>i</font></sub></i>)</H3</td></tr></table></center><BR>");
print("<BR>");


	     $line0 = "Finding the point-slope equation of a line is a two-step process:";

	     $line1 = "<b>Step 1</b>: Calculate the slope <i>m</i> of the line passing through the two given points  (<i>x<sub><font size='-1'>1</font></sub></i>,<i>y<sub><font size='-1'>1</font></sub></i>) and (<i>x<sub><font size='-1'>2</font></sub></i>,<i>y<sub><font size='-1'>2</font></sub></i>).";

	     $line2 = "For the next step, you may use EITHER point. Even though both choices are depicted in this visualization, you need ONLY choose and execute ONE of them.";


	     $line3 = "<b>Step 2 - CHOICE 1</b>: Plug in the value of <i>m</i> and the coordinates of the FIRST point in the equation shown above.";

	     $line4 = "<b>Step 2 - CHOICE 2</b>: Plug in the value of <i>m</i> and the coordinates of the SECOND point in the equation shown above.";

if ($line==0)
  print("<p style='margin-left:10; margin-right:10; color:red'>$line0</p>");
else
  print("<p style='margin-left:10; margin-right:10'>$line0</p>");

if ($line==1)
  print("<p style='margin-left:20; margin-right:10; color:red'>$line1</p>");
else
  print("<p style='margin-left:20; margin-right:10'>$line1</p>");

if ($line>1)
  print("<p style='margin-left:10; margin-right:10; color:blue'>$line2</p>");
else
  print("<p style='margin-left:10; margin-right:10'>$line2</p>");

if ($line==3)
  print("<p style='margin-left:20; margin-right:10; color:red'>$line3</p>");
else
  print("<p style='margin-left:20; margin-right:10'>$line3</p>");

if ($line==4)
  print("<p style='margin-left:20; margin-right:10; color:red'>$line4</p>");
else
  print("<p style='margin-left:20; margin-right:10'>$line4</p>");


?></pre>
</body>
</html>