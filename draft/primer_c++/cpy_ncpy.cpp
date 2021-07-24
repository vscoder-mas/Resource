#include <iostream>
#include <string.h>
#include <stdio.h>

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
  // char str1[]= "To_be*or!not^to@be";
  // char str2[40];
  // char str3[40];

  // /* copy to sized buffer (overflow safe): */
  // strncpy ( str2, str1, sizeof(str1) );
  // //partial copy (only 5 chars): 
  // strncpy ( str3, str2, 5 );
  // //null character manually added
  // str3[5] = '\0';
  // printf("dest=%s\n", str3);

  // printf("\n");
  // cout << "str1=" << str1 << endl;
  // cout << "str2=" << str2 << endl;
  // cout << "str3=" << str3 << endl;

  // const char* src = "hi";
  // char dest[6] = {'u', 'v', 'w', 'x', 'y', 'z'};
  // char x[10] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
  // // strncpy(dest, src, 5);
  // // //多出的用空字符填充
  // // printf("dest=%s\n", dest);

  // // for (char c : dest) {
  // //     if (c) {
  // //         std::cout << c << ' ';
  // //     } else {
  // //         std::cout << "\\0" << ' ';
  // //     }
  // // }

  // // strncpy(x, dest, 3);
  // strncpy(x, dest, 10);
  // x[10 - 1]= '\0';
  // printf("x=%s\n", x);

  // for (char c : x) {
  //     if (c) {
  //         std::cout << c << ' ';
  //     } else {
  //         std::cout << "\\0" << ' ';
  //     }
  // }

//begin test4
  // char dest111[20];
  // char *src111 = "Golden_Global_View";
  // size_t n = 8;
  // strncpy(dest111, src111, n);
  // dest111[n] = '\0';
  // cout << "dest111=" << dest111 << endl;

  // for (char c : dest111) {
  //     if (c) {
  //         std::cout << c << ' ';
  //     } else {
  //         std::cout << "\\0" << ' ';
  //     }
  // }

  printf("\n");
//end test4

  char dest[256] = {0};
  strncpy(dest, "abcdef", 256);
  dest[256-1] = '\0';
  strncpy(dest, "123", 256);
  dest[256-1] = '\0';

  printf("dest=%s\n", dest);

	return 0;
}