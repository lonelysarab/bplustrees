<?php
$pgm = array(
"0     int k = 2;", "1     int f[4] = {4, 4, 1, 7};", "2", "3     int main() { ", "4       int f[4] = {10, 2, 5, 8};", "5       int d = k + k;", "6       int g = 6;", "7", "8       By_Reference/CopyRestore(g, g, f[3], d);", "9     }", "10", "11", "12    void By_Reference/CopyRestore(int m, int g, int c, int p){", "13       int m[4] = {6, 6, 9, 3};", "14", "15       g = p;", "16       f[0] = p + g;", "17       p = g + g;", "18       c = k + k;", "19       m[3] = f[0] + k;", "20       k = c + k;", "21       p = k;", "22    }", " ");
for($i = 0; $i < count($pgm); $i++){
if($i ==$line){
print("<font color = 'red'>$pgm[$i]</font><br>");
}
else
print("$pgm[$i]<br>");
}
?>
