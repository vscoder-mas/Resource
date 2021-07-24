#include <iostream>
#include <thread>

using namespace std;

void counter(int id, int num) {
	for(int i=0; i<num; ++i) {
		cout << "Counter " << id << " has value=" << i << endl;
	}
}

int main(int argc, char const *argv[]) {
	/* code */
	// make sure cout is thread safe
	cout.sync_with_stdio(true);

	thread thread1(counter, 1, 6);
	thread thread2(counter, 2, 4);
	thread1.join();
	thread2.join();

	return 0;
}