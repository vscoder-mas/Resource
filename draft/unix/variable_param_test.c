#include "stdio.h"
#include "stdarg.h"

double averge(int num, ...) {
	int i = 0;
	va_list valist;
	double sum;
	va_start(valist, num);
	for (i = 0; i < num; i++) {
		sum += va_arg(valist, int);
	}

	va_end(valist);
	return sum / num;
}

int main(int argc, char const *argv[]) {
	/* code */
	printf("averge=%f\n", averge(3, 5, 15, 10));
	return 0;
}