 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DT/xhtml1-transitional.dtd">

<html xmlns = "http://www.w3.org/1999/xhtml">
<head><title>Program</title></head>
<body><pre><?php
        if(isset($_GET['line']))
        {
                $line = $_GET['line'];
        }

print("<center><table width=\"90%\" border='1' bgcolor=\"#DDDDDD\"><tr><td align='center'><H2><i>Slope-intercept equation of a line</i></H2><H3><i>y</i> = <i>mx</i> + <i>b</i></H3</td></tr></table></center><BR>");
print("<BR>");

$line0 = "Finding the point-slope equation of a line is a three-step process:";

$line1 = "<b>Step 1</b>: Calculate the slope <i>m</i> of the line passing through the two given points  (<i>x<sub><font size='-1'>1</font></sub></i>,<i>y<sub><font size='-1'>1</font></sub></i>) and (<i>x<sub><font size='-1'>2</font></sub></i>,<i>y<sub><font size='-1'>2</font></sub></i>).";

$line2 = "For the next step, you may use EITHER point. Even though both choices are depicted in this visualization, you need ONLY choose and execute ONE of them.";



$line3 = "<b>Step 2 - CHOICE 1</b>: Plug in the coordinates of the FIRST point in the equation shown above, then solve for <i>b</i>.";

$line4 = "<b>Step 2 - CHOICE 2</b>: Plug in the coordinates of the SECOND point in the equation shown above, then solve for <i>b</i>.";

$line5 = "<b>Step 3</b>: Plug in the values of <i>m</i> and <i>b</i> that you just computed to obtain the slope-intercept equation."; 


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

if ($line==5)
  print("<p style='margin-left:20; margin-right:10; color:red'>$line5</p>");
else
  print("<p style='margin-left:20; margin-right:10'>$line5</p>");

?></pre>
</body>
</html>