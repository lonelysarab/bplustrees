<?php
$pgm = array(
"0     int m = 4;", "1", "2     int main() { ", "3       int p = 8;", "4       int j = m + m;", "5", "6       By_Reference/CopyRestore(p, p, j);", "7     }", "8", "9", "10    void By_Reference/CopyRestore(int f, int m, int l){", "11       int p = 4;", "12       int n = 1;", "13", "14       p = f;", "15       l = n + p;", "16       f = n + f;", "17       n = f;", "18       m = n + p;", "19       f = m + n;", "20       n = m;", "21    }", " ");
for($i = 0; $i < count($pgm); $i++){
if($i ==$line){
print("<font color = 'red'>$pgm[$i]</font><br>");
}
else
print("$pgm[$i]<br>");
}
?>
