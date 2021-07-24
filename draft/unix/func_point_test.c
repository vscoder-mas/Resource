#include "stdio.h"

int main(int argc, char const *argv[]) {
	/* code */
	void (*fp)() = printf;
	fp("hello world!\n");
	return 0;
}