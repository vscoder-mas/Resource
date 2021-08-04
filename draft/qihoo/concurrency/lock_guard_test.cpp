#include <list>
#include <iostream>
#include <mutex>
#include <stdint.h>
#include <thread>

using namespace std;

list<uint32_t> lst;
mutex t_mutex;

void addList(uint32_t x) {
	lock_guard<mutex> guard(t_mutex);
	lst.push_back(x);
}

bool listContains(uint32_t x) {
	lock_guard<mutex> guard(t_mutex);
	bool result = false;
	list<uint32_t>::iterator it = lst.begin();
	while(it != lst.end()) {
		if(*it == x) {
			result = true;
			break;
		}

		it++;
	}

	return result;
}

int main(int argc, char const *argv[]) {
	/* code */
	thread t1(addList, 100);
	t1.detach();
	
	bool result = listContains(100);
	cout << "result=" << result << endl;
	return 0;
}