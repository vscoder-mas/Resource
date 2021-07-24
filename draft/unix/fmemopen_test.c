#include <stdio.h>
#include <unistd.h>
#include <string.h>

#define BSZ 48

int main(int argc, char const *argv[]) {
	/* code */
	FILE *fp;
	char buf[BSZ];

	printf("buf.size=%zu, buf.len=%zu\n", sizeof(buf), strlen(buf));
	memset(buf, 'a', BSZ - 2);
	buf[BSZ-2] = '\0';
	buf[BSZ-1] = 'X';
	printf("init buf: %s\n", buf);

	if((fp = fmemopen(buf, BSZ, "w+")) == NULL) {
		printf("fmemopen failed.\n");
	}

	printf("initial buffer contents: %s\n", buf);
	fprintf(fp, "hello world");
	printf("before flush: %s\n", buf);
	fflush(fp);
	printf("after flush: %s\n", buf);
	printf("len of string in buf=%ld\n", (long)strlen(buf));

	memset(buf, 'b', BSZ - 2);
	buf[BSZ-2] = '\0';
	buf[BSZ-1] = 'X';
	fprintf(fp, "hello world");
	fseek(fp, 0, SEEK_SET);
	printf("after fseek: %s\n", buf);
	printf("len of string in buf=%ld\n", (long)strlen(buf));

	memset(buf, 'c', BSZ - 2);
	buf[BSZ-2] = '\0';
	buf[BSZ-1] = 'X';
	fprintf(fp, "hello world");
	fclose(fp);
	printf("after fclose: %s\n", buf);
	printf("len of string in buf=%ld\n", (long)strlen(buf));
	
	return 0;
}
