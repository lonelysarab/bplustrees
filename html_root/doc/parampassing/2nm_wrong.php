<?php
$pgm = array(
"0     int h = 3;", "1     int j = 0;", "2", "3     int main() { ", "4       int j = 1;", "5       int e = 0;", "6", "7       By_Name/Macro(j, e);", "8     }", "9", "10", "11    void By_Name/Macro(int h, int e){", "12       int j = 0;", "13", "14       e = h;", "15       h = j;", "16       j = h;", "17       h = h;", "18    }", " ");
for($i = 0; $i < count($pgm); $i++){
if($i ==$line)
print("<font color = 'red'>$pgm[$i]</font><br>");
else
print("$pgm[$i]<br>");
}
?>
