<?php
$pgm = array(
"0     int c = 1;", "1     int k = 0;", "2     int m = 0;", "3", "4     int main() { ", "5       int f = 3;", "6", "7       By_Name/Macro(f);", "8     }", "9", "10", "11    void By_Name/Macro(int c){", "12       int f = 0;", "13", "14       k = c;", "15       m = c;", "16       f = k;", "17       c = m;", "18    }", " ");
for($i = 0; $i < count($pgm); $i++){
if($i ==$line)
print("<font color = 'red'>$pgm[$i]</font><br>");
else
print("$pgm[$i]<br>");
}
?>
