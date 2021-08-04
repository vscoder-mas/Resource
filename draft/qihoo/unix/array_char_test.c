#include "stdio.h"

int main(int argc, char const *argv[]) {
	/* code */
	char s[] = "desolate";
	char *p = s;

	// printf("*p++=%c\n", *p++);
	printf("*(p++)=%c\n", *(p++));
	printf("*(p++)=%c\n", *p);
	return 0;
}