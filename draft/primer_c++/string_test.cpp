#include <iostream>
#include <stdlib.h>
#include <stdint.h>
#include <stdio.h>

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
	// std::string str = "Hello";
	// std::string phrase = "Hello World";
	// std::string slang = "Hiya";

	// bool result1 = (str > phrase) ? true : false;
	// bool result2 = (slang > phrase) ? true : false;

	// cout << "result1=" << result1 << "\n"
	// << "result2=" << result2 << endl;

	char *test = "[    1.687214] 3b20: ffffffc174919a00 0000008000000000 ffffffc176693cf0 ffffff8008abfcd0";
	string str = test;
	size_t index_1 = str.find_first_of("]") + 1;
	size_t index_2 = str.find_first_of(":");
	string result = str.substr(index_1, index_2 - index_1);

	uint32_t value = strtoul(result.c_str(), NULL, 16);
	cout << "index1=" << index_1 << endl;
	cout << "index2=" << index_2 << endl;
	cout << "sub str=" << result << endl;
	cout << "value=" << value << endl;
	return 0;
}