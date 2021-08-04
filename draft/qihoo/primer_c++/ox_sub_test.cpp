#include <iostream>
#include <stdint.h>
#include "stdio.h"

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
	uintptr_t x = 0xffffff8fdd40d550;
	uintptr_t y = 0xffffff8fdd40c000;
	// unsigned int r = x - y;
	// cout << "result=" << r << endl;
	// printf("0x%02x\n", r);

	uintptr_t r = x - y;
	printf("x=/abc, %p\n", (void *)r);
	return 0;
}