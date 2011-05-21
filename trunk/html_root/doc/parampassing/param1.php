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
//echo "$file";
include($file);
?></pre>
</body>
</html>
