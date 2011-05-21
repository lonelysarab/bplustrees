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
        if(isset($_GET['vname']))
	  {
	    $vname = $_GET['vname'];
	  }
        if(isset($_GET['vvalue']))
	  {
	    $vvalue = $_GET['vvalue'];
	  }
//echo "file = $file<BR>";
//echo "line = $line<BR>";
include($file);
print("<br>&nbsp;<b>Variables:</b><br>");
print("      $vname  = $vvalue");
?></pre>
</body>
</html>