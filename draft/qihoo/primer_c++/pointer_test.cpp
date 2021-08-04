#include <iostream>
#include <memory>

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
	int *p = new int(88);
	cout << "p value=" << *p << endl;

	int *q = p;
	// delete p;
	// p = NULL;

	cout << "q value=" << *q << endl;

	unique_ptr<string> p1(new string("hello world!"));
	unique_ptr<string> p2(p1);
	return 0;
}