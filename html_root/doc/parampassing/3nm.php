<?php
$pgm = array(
"0     int m = 2;", "1", "2     int main() { ", "3       int m = 1;", "4       int g = 2;", "5", "6       By_Name/Macro(m, g);", "7     }", "8", "9", "10    void By_Name/Macro(int m, int p){", "11       int g = 1;", "12", "13       m = p;", "14       g = m;", "15       p = m;", "16    }", " ");
for($i = 0; $i < count($pgm); $i++){
if($i ==$line)
print("<font color = 'red'>$pgm[$i]</font><br>");
else
print("$pgm[$i]<br>");
}
?>
