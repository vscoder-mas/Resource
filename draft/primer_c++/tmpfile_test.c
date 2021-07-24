#include <stdio.h>

int main(int argc, char const *argv[]) {
	/* code */
	FILE *file;
	file = tmpfile();
	printf("temporary file creted!\n");
	//fprintf(file, "%s", "hello world!");
	
	fclose(file);
	return 0;
}