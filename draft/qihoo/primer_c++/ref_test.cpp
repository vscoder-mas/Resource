#include <iostream>

using namespace std;

void f_ck(int &i) {
	i++;
}

int main(int argc, char const *argv[]) {
	/* code */
	int x = 6;
	f_ck(x);
	cout << "x=" << x << endl;
	return 0;
}