<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DT/xhtml1-transitional.dtd">

<html xmlns = "http://www.w3.org/1999/xhtml">
<head><title>Program</title></head>
<body><pre><?php
$pgm = array(
"0     int o = 7;", "1     int a[4] = {5, 7, 4, 6};", "2     int j = 5;", "3", "4     int main() { ", "5       int b = 6;", "6       int a[4] = {9, 2, 6, 1};", "7       int l = j + o;", "8       int e = 10;", "9", "10       By_Copy_Reference(l, l, b, a[1], e);", "11     }", "12", "13", "14    void By_Copy_Reference(int g, int b, int o, int l, int e){", "15       int o[4] = {3, 4, 9, 8};", "16       int d = 6;", "17       int n = 7;", "18       int l = 6;", "19       int m = 8;", "20", "21       j = b;", "22       g = a[0] + b;", "23       a[0] = g + e;", "24       b = a[0];", "25       n = a[0] + a[0];", "26       e = j + b;", "27       d = b;", "28    }", " ");

for($i = 0; $i < count($pgm); $i++){
  if(($i == $line ) && ((ereg("main()", $pgm[$i])) || (ereg("void", $pgm[$i])) || (ereg("\}",$pgm[$i])) || (ereg("\{",$pgm[$i])) || (strlen($pgm[$i]) == 1) || (strlen($pgm[$i]) == 2) )){
    print("$pgm[$i]<br>");
    $line++;
  }
    
  else
    if($i ==$line)
      print("<font color = 'red'>$pgm[$i]</font><br>");
    else
      print("$pgm[$i]<br>");
}
?>