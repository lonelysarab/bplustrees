<?php
$pgm = array(
"0     int h = 1;", "1     int j = 2;", "2", "3     int main() { ", "4       int b[4] = {1, 4, 1, 1};", "5", "6       By_Reference/CopyRestore(b[3], b[3]);", "7     }", "8", "9", "10    void By_Reference/CopyRestore(int k, int d){", "11       int o[4] = {10, 5, 9, 8};", "12", "13       h = d + j;", "14       k = h + k;", "15       d = j + h;", "16       j = d + d;", "17       o[2] = k;", "18       d = h + h;", "19    }", " ");
for($i = 0; $i < count($pgm); $i++){
if($i ==$line){
print("<font color = 'red'>$pgm[$i]</font><br>");
}
else
print("$pgm[$i]<br>");
}
?>
