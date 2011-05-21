<?php
$pgm = array(
"<b>Array Practice</b><br><br><br><br>",
"1   int[] array = { -4 , 9 , -7 , 3 , 0 , -4 , 2 , -10 , 3 , 2 , 5 , -2 };",
" ",
"2   for(int k = 2; k <= 11 ; k = k + 3)",
"3   {",
"4      array[ k ] = array[ k ] / 7;",
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