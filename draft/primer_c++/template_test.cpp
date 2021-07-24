#include <iostream>
#include <stdint.h>
using namespace std;

template <typename T>
int compare(const T &v1, const T &v2) {
	if(v1 < v2) return -1;
	if(v1 > v2) return 1;
	return 0;
}

int main(int argc, char const *argv[]) {
	int result = compare(1, 2);
	cout << "result=" << result << endl;

	char *s1 = "abc";
	char *s2 = "abc";
	int result2 =compare(s1, s2);
	cout << "result2=" << result2 << endl;	
	/* code */
	return 0;
}