#include <iostream>
#include <iterator>
#include <cstddef>

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
	int array[] = {0,1,2,3,4,5,6,7,8,9};
	int *beg = begin(array);
	int *last = end(array);

	while(beg != last) {
		cout << "index=" << *beg << endl;
		beg++;
	}

	ptrdiff_t length = last - beg;
	cout << "array length=" << length << endl;
	return 0;
}