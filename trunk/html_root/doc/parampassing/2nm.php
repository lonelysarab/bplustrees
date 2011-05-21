<?php
$pgm = array(
"0     int m = 2;", "1     int n = 2;", "2     int g = 3;", "3", "4     int main() { ", "5       int g = 0;", "6", "7       By_Name/Macro(g);", "8     }", "9", "10", "11    void By_Name/Macro(int o){", "12       int n = 0;", "13       int g = 2;", "14", "15       o = m;", "16       m = n;", "17       n = o;", "18    }", " ");
for($i = 0; $i < count($pgm); $i++){
if($i ==$line)
print("<font color = 'red'>$pgm[$i]</font><br>");
else
print("$pgm[$i]<br>");
}
?>
