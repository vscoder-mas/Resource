#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <sys/wait.h>

void pr_exit(int status) {
	if(WIFEXITED(status)) {
		printf("normal termination, exit status=%d\n", WEXITSTATUS(status));
	} else if(WIFSIGNALED(status)) {
		printf("abnormal termination, signal number=%d\n", WTERMSIG(status));
	} else if(WIFSTOPPED(status)) {
		printf("child stopped, signal number=%d\n", WSTOPSIG(status));
	}
}

int main(int argc, char const *argv[]) {
	/* code */
	pid_t pid;
	int status;

	if((status == system("date")) < 0) {
		printf("system() error.\n");
	}

	pr_exit(status);

	// if((pid == fork()) < 0) {
	// 	printf("fork error.\n");
	// } else if(pid == 0) {
	// 	abort();
	// }

	// if(wait(&status) != pid) {
	// 	printf("wait error.\n");
	// }
	// pr_exit(status);

	// if((pid == fork()) < 0) {
	// 	printf("fork error.\n");
	// } else if(pid == 0) {
	// 	status /= 0;
	// }

	// if(wait(&status) != pid) {
	// 	printf("wait error.\n");
	// }
	// pr_exit(status);

	// if((pid == fork()) < 0) {
	// 	printf("fork error.\n");
	// } else if(pid == 0) {
	// 	exit(7);
	// }

	// if(wait(&status) != pid) {
	// 	printf("wait error.\n");
	// } else {
	// 	int t = wait(&status);
	// 	printf("t=%d, pid=%d\n", t, pid);
	// }
	// pr_exit(status);	

	return 0;
}