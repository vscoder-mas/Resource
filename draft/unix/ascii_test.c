#include <ctype.h>
#include <string.h>
#include <stdio.h>

int main(int argc, char const *argv[]) {
	/* code */
	char xdigs[] = "0123456789ABCDEF";
	char x = 'a';
	char *result = strchr(xdigs, toupper(x));
	printf("%s\n", result);
	printf("%d\n", BUFSIZ);
	return 0;
}