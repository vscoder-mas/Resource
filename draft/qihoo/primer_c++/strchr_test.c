#include <string.h>
#include <stdio.h>

int main(int argc, char const *argv[]) {
	/* code */
	char *str = "dnsmasq[337]: query[A] hacker.test.cn from 127.0.0.1";
	char *left = strstr(str, "query");
	if (left != NULL) {
		char *domain = strchr(left, ']');
		if (domain != NULL) {
			printf("domain1=%s\n", domain);
			domain += 2;
			printf("domain2=%s\n", domain);

			char *opt = NULL;
			char buffer[256] = {0};
			snprintf(buffer, sizeof(buffer), "%s", domain);
			char *ptr = buffer;
			while ((ptr = strtok_r(ptr, " ", &opt)) != NULL) {
				printf("sub=%s, opt=%s\n", ptr, opt);
				ptr = NULL;
			}
		}
	}

	return 0;
}