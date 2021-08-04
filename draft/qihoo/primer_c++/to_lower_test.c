#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>

// void set(char** ptr) {
// 	*ptr = (char*)malloc(100);
// 	snprintf(*ptr, 100, "%s", "hello world!");
// 	printf("p1=%p\n", (void*)ptr);
// 	printf("p2=%p\n", (void*)*ptr);
// }
static uint8_t startsWith(const char *pre, const char *str) {
    size_t lenpre = strlen(pre);
    size_t lenstr = strlen(str);
	return lenstr < lenpre ? 0 : strncmp(pre, str, lenpre) == 0;
}

int main(int argc, char const *argv[]) {
	/* code */
	// int i=0;
	// char mac[20] = "B8:EE:65:D5:2E:EA";
	// for(i=0; i<sizeof(mac);i++) {
	// 	if (mac[i] >= 'A' && mac[i] <= 'Z') {
	// 		mac[i]+=32;
	// 	}
	// 	if (mac[i] == '\0') {
	// 		printf("index=%d !!!!!!!!!!!!\n", mac[i]);	
	// 	} else {
	// 		printf("%c\n", mac[i]);
	// 	}
	// }

	// printf("%s, len=%d, sizeof=%d\n", mac, strlen(mac), sizeof(mac));


	// char* test;
	// printf("p1=%p\n", (void*)test);
	// set(&test);
	// printf("%s\n", test);
	// printf("p2=%p\n", (void*)test);

	char* t = "scan_psng_udp_portscan";
	char* sub = "scan_";
	uint8_t r = startsWith(sub, t);
	printf("%u\n", r);
	return 0;
}