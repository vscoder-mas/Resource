#include <iostream>
#include <thread>

using namespace std;

class Request {
public:
	Request(int id) : mId(id) {
	}

	void process() {
		cout << "Processing request=" << mId << endl;
	}

protected:
	int mId;
};

int main(int argc, char const *argv[]) {
	/* code */
	cout.sync_with_stdio(true);
	Request req(100);
	thread t{&Request::process, &req};
	t.join();
	return 0;
}