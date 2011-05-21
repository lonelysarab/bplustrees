<html>
<head>
<title>Program Listing</title>
<!-- you will need this style sheet declaration if you want red highlighting -->
<style>
	<!--
		@import url(../php_inc/program_listing.css);
	-->
</style>
</head>
<body>
<h2>Program Listing Utility</h2>
<p>This is a demonstration of adding a program
listing into a web page for jháve. Click on the following links to see some variations of it's use.
<br>
<a href="index.php?line=1">highlight line 1</a><br>
<a href="index.php?line=2&var[x]=32">highlight line 2, x = 32</a><br>
<a href="index.php?file=other.xml&line=3&var[x]=17">different file, highlight line 1, x = 17</a><br>
</p>
<p>Here is the program listing:</p>

<?php $file = 'template.xml'; ?> <!-- this is optional and is overidden by the url -->
<?php include '../php_inc/program_listing.php'; ?> <!-- this is the only required tag -->

<!-- this prints the xml file at the bottom of the page. You will want
	to remove this -->
<?php
	if(isset($data))
	print '<p>This is the xml file that\'s being parsed:</p><pre>';
	print(htmlentities($data) . '</pre>');
?>

</body>
</html>
