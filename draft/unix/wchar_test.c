#include <stdio.h>
#include <stddef.h>

int main(int argc, char const *argv[]) {
	/* code */
	char str[] = "hello";
	wchar_t wcs[] = L"hello";

	printf("sizeof(str)=%zu\n", sizeof(str));
	printf("sizeof(wcs)=%zu\n", sizeof(wcs));
	return 0;
}