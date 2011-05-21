<?php
$pgm = array(
"0     int g = 0;", "1", "2     int main() { ", "3       int c = 3;", "4       int j = 2;", "5", "6       By_Name/Macro(c, j);", "7     }", "8", "9", "10    void By_Name/Macro(int o, int c){", "11       int j = 0;", "12       int g = 0;", "13       int m = 2;", "14", "15       c = o;", "16       g = m;", "17       o = c;", "18       j = o;", "19    }", " ");
for($i = 0; $i < count($pgm); $i++){
if($i ==$line)
print("<font color = 'red'>$pgm[$i]</font><br>");
else
print("$pgm[$i]<br>");
}
?>
