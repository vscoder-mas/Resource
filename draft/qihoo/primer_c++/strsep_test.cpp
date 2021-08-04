#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <sys/time.h>
#include <iostream>

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
	char ptr[] = "abcdefghijklmnopqrstuvwxyz ";
	char *p;
	char *str = "m";
	p = ptr;
	char *after = strsep(&p, str);
	printf("%s\n", after);
	printf("%s\n", p);

	//rand_r
	time_t now = time(0);
	unsigned int param = now;
	int r = rand_r(&param);
	cout << "now=" << now << ", r=" << r << endl;

	struct timeval tv = {
		.tv_sec = 0,
		.tv_usec = 0
	};
	gettimeofday(&tv, NULL);
	cout << "tv_sec=" << tv.tv_sec << ", tv_usec=" << tv.tv_usec << endl;

	return 0;
}