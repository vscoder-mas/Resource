// #include "stdio.h"
// #include "unistd.h"
// #include "string.h"
#include "apue.h"

int globalvar = 6;
char buf[] = "a write to stdout\n";

int main(int argc, char const *argv[]) {
	/* code */
	int var;
	pid_t pid;

	var = 88;
	if(write(STDOUT_FILENO, buf, sizeof(buf) -1) != sizeof(buf) -1) {
		printf("write error.\n");
	}

	if((pid == fork()) < 0) {
		printf("fork error.\n");
	} else if(pid == 0) {
		globalvar++;
		var++;
	} else {
		sleep(10);
	}


	printf("\npid=%ld, glob=%d, var=%d\n", (long)getpid(), globalvar, var);
	exit(0);
}