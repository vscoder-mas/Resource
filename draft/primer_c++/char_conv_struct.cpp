#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <iostream>

using namespace std;

// struct t_header {
// 	uint32_t magic;
// 	uint32_t count;
// 	uint32_t crc;
// 	uint8_t reserved[64];
// 	uint32_t list[0];
// };

struct t_header {
	uint32_t magic;
	uint32_t count;
};

int main(int argc, char const *argv[]) {
	/* code */
	t_header header = {
		.magic = 111,
		.count = 1888888888
	};

	char temp[20] = {0};
	char small[6] = {0};
	memcpy(temp, &header, sizeof(t_header));
	memcpy(small, temp, sizeof(small));

	t_header *tt = (t_header *)small;
	cout << "sizeof=" << sizeof(t_header) << ", tt->count=" << tt->count << endl;

	char *test = "hello world!";
	uintptr_t p = (uintptr_t) test;
	printf("p=%p, p1=%p\n", (void *)p, test);
	printf("p2=%p\n", &header);

	return 0;
}