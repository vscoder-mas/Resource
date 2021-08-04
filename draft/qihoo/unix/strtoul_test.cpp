#include "stdio.h"
#include <string.h>
#include "stdint.h"
#include <string>

using namespace std;
#define PATCH_SEPARATE "========  OAE_SERVER  ========"

int main(int argc, char const *argv[]) {
	/* code */
	// const char *test = "binder_update_page_range+0x2f8/0x314^M";
	// // const char *test = "lpm_cpuidle_enter+0xb28/0xb94";
	// uintptr_t ul = strtoul(test, NULL, 16);
	// printf("ul=%p\n", (void *)ul);

	// if(ul == 0) {
	// 	printf("fadsfdfdfsa\n");
	// }

	// unsigned long start = 0xffffffbffc0422a6;
	// unsigned long end = 0xffffffbffc0422a9;
	// uint32_t result = end - start;
	// printf("%lu\n", result);

	// const char *test = "binder_update_page_range+0x2f8/0x314";
	// string str(test);
	// std::string::size_type index = str.find_first_of("0x");
	// if(index == string::npos) {
	// 	printf("sb\n");
	// } else {
	// 	printf("index=%zu\n", index);	
	// }


	// const char *test = "========  OAE_SERVER  ========";
	// if (strstr(test, PATCH_SEPARATE) != NULL) {
	// 	printf("112233\n");	
	// }

	char result[30] = {0};
	uintptr_t test = 0xffffff8fdd40c000;
	sprintf(result, "%p", (void *) test);
	printf("%s, length=%zu\n", result, strlen(result));
	
	return 0;
}