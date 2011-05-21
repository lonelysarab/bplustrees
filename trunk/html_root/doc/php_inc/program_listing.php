<?php
	function startElement($parser, $tag, $attributes) {
	    global $line;
	    global $var;
            global $noclose;
	    
	    if ($tag == 'PSEUDOCODE')
		 print '';

	    else if ($tag == 'CALL_STACK')
		 print '<div class="stack">';

	    else if ($tag == 'PROGRAM_LISTING')
		 print '<div class="codeArea">';

	    else if ($tag == 'SIGNATURE')
		 print '<div class="signature">';

      	    else if($tag == 'LINE') {
		 if ($attributes['LINE_NUMBER'] == $line)
		     print '<pre class="highlightCode">';
		 else
		     print '<pre class="plainCode">';

	    } else if ($tag == 'VARIABLES')
		 print '<div class="variables">Variables: ';

	    else if ($tag == 'VARIABLE')
		 print '<div class="variable">';

	     else if ($tag == 'REPLACE') {
	          $noclose = true;
		  if (isset($var[$attributes['VAR']])){
		      print $var[$attributes['VAR']];
		  } else {
		      print $attributes['VAR'];
		  }
	    }
	}

	function endElement($parser, $tag) {
	      global $noclose;

	      if ($tag == 'PSEUDOCODE')
		   print '';
	      else if ($tag == 'LINE')
	           print '</pre>';
              else if ($noclose == false)
	           print '</div>';
              else
		   $noclose = false;
	}

	function characterData($parser, $data)
	{
		print $data;
	}

	if(isset($_GET['file']))
	{
		$file = $_GET['file'];
	}
	else if(!(isset($file)))
	{
		print '<code>Error: no file specified.</code>';
		die;
	}

	if(isset($_GET['line']))
	{
		$line = $_GET['line'];
	}
	else
	{
		$line = 0;
	}

	if(isset($_GET['var']))
	{
		$var = $_GET['var'];
	}
	else
	{
		$var = null;
	}

	$noclose = false;
	
	if(is_file($file))
		$fh = fopen($file, "r");
	else
	{
		print '<code>Error: unable to open file.</code>';
		die;
	}

	// get the xml parser ready
	$parser = xml_parser_create();
	xml_set_element_handler($parser, "startElement", "endElement");
	xml_set_character_data_handler($parser, "characterData");

	$data = fread($fh, 8000);


	if(!(xml_parse_into_struct($parser, $data, $vals, $index)))
	{
		die("Error on line " . xml_get_current_line_number($parser));
	}

	/*
	if(!(xml_parse($parser, $data, feof($fh))))
	{
		die("Error on line " . xml_get_current_line_number($parser));
	}
	*/

	/*
	print '<p>Vals:</p><pre>';
	print_r($vals);
	print '</pre><p>Index:</p><pre>';
	print_r($index);
	print '</pre>';
	*/

	fclose($fh);
	xml_parser_free($parser);
?>
