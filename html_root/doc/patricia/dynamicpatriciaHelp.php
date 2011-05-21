<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DT/xhtml1-transitional.dtd">

<html xmlns = "http://www.w3.org/1999/xhtml">
<head>
<title> Help for <?php print("$help of $node"); ?> </title>
</head>
<body>
<table border = "1">
<caption>Key to visualization</caption>
<thead>
<tr>
   <th>Image</th>
   <th>Description</th>
</tr>
</thead>
<tbody>
<tr>
   <td>Circle</td>
   <td>Interior node</td>
</tr>
<tr>
   <td>Number</td>
   <td>Index of important test, zero based</td>
</tr>
<tr>
   <td>Character</td>
   <td>Value to test against to determine which child to go to, $ is a space</td>
</tr>
<tr>
   <td>Rectangle</td>
   <td>Exterior node</td>
</tr>
</tbody>
</table>

<?php
	print("<pre>");
	if(strcmp($help,"deletion") == 0)    // deletion
	{
print("delete(...)
{
   search for $node
   if ($node is not found)
      return
   else
   {
      update parent pointer of the sibling of $node
      update child pointer of the grandparent of $node
      delete parent of $node
      delete $node
   }
}

");
	}
	elseif(strcmp($help,"insertion")==0)
	{
print("insert(...)
{
   if(tree = NULL)
      insert external node with $node at root
   elseif (tree = external node)
   {
      create new internal and external node
      find first index that differ between old external node and $node
      update internal node's index and index_char
      find which node has smaller character at index and link internal->left = node
      internal->right = other node
      update parent pointer of $node and old external node to new internal node
   }
   else (tree is internal node)
   {
      if (index skipped a number from parent)
      {
	 startNode = tree
	 find a leaf in the left subtree 
	 if (character(s) are not the same)
	 {       //then string can not be inserted in startNode sub-tree
	    create new internal and external node 
	    find first index that differ between leaf and $node
	    update internal node index and index_char
	    find which node has smaller character at index and link interal->left = node
	    internal->right = other node
	    update parent pointers of $node and startNode to point to new internal node
	  }  
	  else
	    call insertion algorithm on tree->left or tree->right
      }
      else
	 call insertion algorithm on tree->left or tree->right
   }
}

");

	}
	else if (strcmp($help, "searching") == 0) // searching
	{
print("search(...)
{
   if (tree = NULL)
      return NULL
   while (tree is internal)
   {
      index = find index of internal node
      if (char at index of $node <= char of internal node)
	 tree = tree->left
      else
	 tree = tree->right
   }
   if ($node = tree->string)
      return tree
   else
      return NULL
}");
	
	}
		print("</pre>");
?>
<script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
</script>
<script type="text/javascript">
_uacct = "UA-3174792-1";
urchinTracker();
</script>
</body>
</html>