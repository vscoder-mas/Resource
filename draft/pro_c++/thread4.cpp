#include <iostream>
#include <thread>

using namespace std;

void doSomeWork() throw(runtime_error) {
	for(int i=0; i<5; ++i) {
		cout << i << endl;
	}

	cout << "thread throwing a runtime_error exception..." << endl;
	throw runtime_error("exception from thread");
}

void threadFunc(exception_ptr &err) {
	try {
		doSomeWork();
	} catch(...) {
		cout << "thread caught exception, returning exception..." << endl;
		err = current_exception();
	}
}

void doWorkInThread() throw(runtime_error) {
	exception_ptr error;
	thread t(threadFunc, ref(error));
	t.join();

	if(error) {
		cout << "main thread received exceptoin, rethrowing it!" << endl;
		rethrow_exception(error);
	} else {
		cout << "main thread did not receive any exception." << endl;
	}
}

int main(int argc, char const *argv[]) {
	/* code */
	cout.sync_with_stdio(true);
	try {
		doWorkInThread();
	} catch (const exception &e) {
		cout << "main function cought:'" << e.what() << "'" << endl;
	}

	return 0;
}