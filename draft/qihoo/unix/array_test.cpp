#include <iostream>

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
	int a[] = {10, 15, 4, 25, 3, -8};
	int *p = &a[2];

	cout << "*(p+1)=" << *(p+1) << endl;
	cout << "p[-1]=" << p[-1] << endl;
	cout << "p-a=" << p - a << endl;
	return 0;
}