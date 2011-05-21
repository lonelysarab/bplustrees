<?php
$pgm = array(
"<b>Array Practice</b><br><br><br><br>",
"1   int[] a = { 6 , 9 , -3 , -2 , -7 , 1 , -2 };",
" ",
"2   for(int i = 0; i < 6 ; i = i + 1)",
"3   {",
"4      a[ i ] = a[ i ] * 7;",
"5   }"
);
for($i = 0; $i < count($pgm); $i++){
if($i ==$line){
print("<font color = 'red'>$pgm[$i]</font><br>");
}
else
print("$pgm[$i]<br>");
}
?>