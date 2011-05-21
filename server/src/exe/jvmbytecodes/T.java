class T
{
 public static void main( String [ ] args )
 {
 l();
 }

 static void l()
 {
 int a = 1;
 int b = a + a;
 int c = (b*b);
 int d = -(b-a);
 int e = c/b;
 int f = a + b + c + d + e;
 int g = a & b;
 int h = a | b;
 int i = a ^ b;
 while(e < 5)
 {
 m();
 a++;
 e++;
 }
 }

 static void m()
 {
 float j = 2;
 j = j*1;
 }
}
