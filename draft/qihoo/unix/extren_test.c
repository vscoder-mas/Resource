#include <stdio.h>

// int func();

// int main(int argc, char const *argv[]) {
// 	/* code */
// 	func();
// 	extern int num;
// 	printf("%d\n", num);
// 	return 0;
// }

// int num = 3;

// int func() {
// 	printf("%d\n", num);
// }

int main(int argc, char const *argv[]) {
	/* code */
	extern int num;
	printf("num in main() %d\n", num);
	return 0;
}