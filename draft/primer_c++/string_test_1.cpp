#include <iostream>
#include <string.h>
#include <stdint.h>

using namespace std;

struct Test {
	uint32_t x;
	uint8_t y;
	uintptr_t z;

	Test(uint32_t a, uint8_t b , uintptr_t c) : x(a), y(b), z(c) {}
};

int main(int argc, char const *argv[]) {
	/* code */
	const char *str1 = "A string example";
	const char *str2 = "A different string";

	bool result = (strcmp(str1, str2) < 0) ? true : false;
	cout << "result=" << result << endl;

	Test t(1,50,3);
	cout << "x=" << t.x << ", y=" << t.y << ", z=" << t.z << endl;

	string path = "system/lib64/libblackmagic_jni.so";
	if(path.find("libblackmagic_jni.so") != string::npos) {
		cout << "find sub string." << endl;
	}

	return 0;
}