#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdio.h>

int main(int argc, char const *argv[]) {
	/* code */
	struct stat state;
	int result;

	result = stat("calc", &state);
	if (result == 0) {
		printf("file size=%ld\n", state.st_size);
		printf("file create time=%s\n", ctime(&state.st_ctime));
		printf("file visit time=%s\n", ctime(&state.st_atime));
		printf("file modify time=%s\n", ctime(&state.st_mtime));


		time_t c_time = state.st_ctime;
        struct tm *timeinfo;
        char buffer[128];
        timeinfo = localtime(&c_time);
        strftime(buffer, sizeof(buffer), "%Y-%m-%d_%H-%M-%S", timeinfo);
        printf("-> create time=%s\n", buffer);
	}

	return 0;
}