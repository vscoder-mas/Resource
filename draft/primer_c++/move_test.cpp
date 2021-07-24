#include <iostream>
#include <stdint.h>

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
	// uint16_t v = (uint16_t) (1 << 10);
	// uint16_t t = 3;
	uint16_t result = 1 & 3;
	cout << "result=" << result << endl;

	// uint16_t result = 3;
	// result |= result + 1;
	// uint16_t t = (result + 1) & 0xFFFF;
	// cout << "result=" << t << endl;

	return 0;
}