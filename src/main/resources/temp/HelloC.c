#include <stdio.h>
 #include <stdlib.h>
 int main(int argc, char *argv[]) {
   double n = atof(argv[1]);
 char str[100]; 
 sprintf(str, "%f", n); 
 printf("%f",n);
 return 0;
 }