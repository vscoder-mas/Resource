#include <iostream>

using namespace std;

class Simple {
public:
	Simple() {
		mIntPtr = new int;
		cout << "Simple()" << endl;
	}

	~Simple() {
		cout << "~Simple()" << endl;
		delete mIntPtr;
	}

	void setIntPtr(int i) {
		*mIntPtr = i;
		cout << "value=" << *mIntPtr << endl;
	}

	int *mIntPtr;

protected:
};

void doSomething(Simple* &outSimplePtr) {
	outSimplePtr = new Simple();
}

int main() {
	Simple *simplePtr = new Simple();
	doSomething(simplePtr);
	delete simplePtr;
	return 0;
}