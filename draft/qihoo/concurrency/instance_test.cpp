#include <iostream>
#include <thread>

using namespace std;

class background_task {
public:
	void do_something() {
		cout << "-> background_task do_something!" << endl;
	}

	void operator()() {
		do_something();
	}
};

int main(int argc, char const *argv[]) {
	/* code */
	background_task f;
	thread t(f);
	return 0;
}