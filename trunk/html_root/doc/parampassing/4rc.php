<?php
$pgm = array(
"0     int e = 5;", "1", "2     int main() { ", "3       int i[4] = {9, 4, 1, 2};", "4       int h = 1;", "5", "6       By_Reference/CopyRestore(h, h, i[1]);", "7     }", "8", "9", "10    void By_Reference/CopyRestore(int o, int f, int g){", "11       int k = 4;", "12       int i[4] = {9, 1, 5, 8};", "13", "14       f = g + f;", "15       e = g + f;", "16       o = e + e;", "17       g = k;", "18    }", " ");
for($i = 0; $i < count($pgm); $i++){
if($i ==$line){
print("<font color = 'red'>$pgm[$i]</font><br>");
}
else
print("$pgm[$i]<br>");
}
?>
