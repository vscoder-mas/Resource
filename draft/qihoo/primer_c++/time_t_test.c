#include <stdio.h>
#include <string.h>
#include <time.h>
#include <stdlib.h>
#include <stdint.h>

int main(int argc, char const *argv[]) {
    /* code */
    // struct tm tmp_time;
    // tmp_time.tm_year = 2019 - 1900;
    // tmp_time.tm_mon = 6;
    // tmp_time.tm_mday = 20;
    // tmp_time.tm_hour = 11;
    // tmp_time.tm_min = 50;
    // tmp_time.tm_sec = 8;
    // tmp_time.tm_isdst = 0;
    // time_t t = mktime(&tmp_time);
    // printf("%s\n", ctime(&t));
    // char buffer[128];
    // strftime(buffer, sizeof(buffer), "%Y-%m-%d_%H-%M-%S", &tmp_time);
    // printf("time=%s\n", buffer);
    // printf("! line=%lu\n", t);


    struct tm tmp_time;
    char buf[32];
    sprintf(buf, "%s/%s/%d %s", "10", "Jun", 2019, "11:58:08");
    strptime(buf, "%d/%b/%Y %H:%M:%S", &tmp_time);
    time_t t = mktime(&tmp_time);
    printf("value=%lu\n", t);

	time_t t1;
	t1 = time(NULL);
	int ii = time(&t1);
	printf("ii=%d, t=%lu\n", ii, t1);
    return 0;
}
