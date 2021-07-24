#include <iostream>
#include <thread>
#include <stdint.h>
#include <string.h>

using namespace std;

void setValue(uint32_t w, uint32_t &h) {
	w = w+1;
	h = h+1;
}

int main(int argc, char const *argv[]) {
	/* code */
	uint32_t x = 100;
	uint32_t y = 200;
	std::thread tt(setValue, x, ref(y));
	cout << "thread tt.id=" << tt.get_id() << endl;
	tt.join();

	cout << "x=" << x << ", y=" << y << endl;
	cout << "main thread id=" << this_thread::get_id() << endl;
	cout << "number of threads=" << thread::hardware_concurrency() << endl;

	return 0;
}