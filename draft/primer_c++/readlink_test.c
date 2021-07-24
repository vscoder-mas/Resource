#include <stdio.h>
#include <unistd.h>
#include <string.h>

int main(int argc, char const *argv[]) {
	/* code */
	char path[1024] = {0};
	int result = readlink("/usr/bin/awk", path, 1023);
	printf("path=%s\n, strlen=%zu\n", path, strlen(path));
	return 0;
}