#include <iostream>
#include <stdio.h>
#include <time.h>
#include <list>
#include <stdint.h>
#include <string.h>

using namespace std;

class Test {
public:
	char path1[20] = {0};
	uint32_t value;

	Test(char _path1[20], uint32_t _value) : value(_value) {
		// strcpy(path1, _path1);
		strncpy(path1, _path1, sizeof(path1) - 1);
		path1[sizeof(path1) - 1] = '\0';
	}
};

class Test_1 {
public:
	char *str;
	char array[20];
};

int main(int argc, char const *argv[]) {
	/* code */
	time_t rawtime;
	struct tm *timeinfo;
	char buffer[128];

	time(&rawtime);
	printf("%ld\n", rawtime);

	timeinfo = localtime(&rawtime);
	strftime(buffer, sizeof(buffer), "%Y-%m-%d_%H-%M-%S", timeinfo);
	printf("%s\n", buffer);

	time_t timep;
	time (&timep);
	printf("%s\n", ctime(&timep));

	// char _path1[20] = "hello world!";
	// uint32_t _value = 100;
	// Test test(_path1, _value);
	// cout << "value=" << test.value << ", path1=" << test.path1 << endl;


	// Test_1 t;
	// t.str = "abc";
	// strcpy(t.array, "def");
	// cout << "str=" << t.str << ", array=" << t.array << endl;

	return 0;
}