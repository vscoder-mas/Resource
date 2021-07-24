#include <stdio.h>
#include <assert.h>
#include <sys/stat.h>

int safecopy(int from_len, char *from, int to_len, char *to) {
	assert(from != NULL && to != NULL && "from and to  can't be NULL");
	int i = 0;
	int max = from_len > to_len -1 ? to_len-1 : from_len;
	printf("safecopy() max=%d\n", max);
	if(from_len < 0 || to_len <= 0) return -1;

	for(i = 0; i < max; i++) {
	    to[i] = from[i];
	}

	to[to_len - 1] = '\0';
	printf("safecopy() i=%d\n", i);
	return i;
}


int main(int argc, char *argv[]) {
	char from[] = "9876543210";
	int from_len = sizeof(from);
	printf("from_len=%d\n", from_len);

	int bits = sizeof(char *);
	char *result = (bits == 4) ? "32bit\n" : "64bit\n";
	printf("bits=%s\n", result);

	char to[] = "0123456";
	int to_len = sizeof(to);
	printf("Copying '%s':%d to '%s':%d\n", from, from_len, to, to_len);

	int rc = safecopy(from_len, from, to_len, to);
	//check(rc > 0, "failed to safecopy.");
	//check(to[to_len -1] == '\0', "string not terminated.");
	printf("result is: to='%s':%d\n", to, to_len);
	return 0;
}
