#include <stdlib.h>
#include <stdio.h>
#include <string.h>

int main(int argc, char const *argv[]) {
	/* code */
	char *pch;
	char str[] = "example string";
	pch = (char *)memchr(str, 'p', strlen(str));

	if(pch != NULL) {
		printf("p found at position=%s\n", pch);
	}
	return 0;
}