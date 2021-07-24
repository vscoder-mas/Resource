#include <stdio.h>

int main(int argc, char *argv[]) {
	int a = 99;
	int *pa = &a;
	int **ppa = &pa;

	printf("%08x\r\n", a);
	printf("%08x\r\n", pa);
	printf("%08x\r\n", *pa);
	printf("%08x\r\n", ppa);
	printf("%08x\r\n", *ppa);
	printf("%08x\r\n", **ppa);
	
	return 0;
}
