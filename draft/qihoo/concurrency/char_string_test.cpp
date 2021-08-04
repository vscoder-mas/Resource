#include <iostream>
#include <stdint.h>
#include <string.h>

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
	char test[3] = {'\0'};
	for(uint32_t i=0; i<sizeof(test)/sizeof(test[0]); i++) {
		if(strcmp(&test[i], "\0") == 0) {
			cout << "fix!" << endl;
		}
	}

	//没有'/0'，strlen会报错
	char test1[] = {'a', 'b', 'c', '\0'};
	cout << "test1 strlen=" << strlen(test1) << endl;
	cout << "test1 sizeof=" << sizeof(test1) << endl;

	char str1[] = "abc";
	char *str2 = "def";
	size_t length = sizeof(str1) + strlen(str2) + 1;
	cout << "pre alloate length=" << length << endl;

	char result[length];
	strcpy(result, str1);
	strcat(result, "_");
	strcat(result, str2);
	cout << "result=" << result << endl;

	for(uint32_t i=0; i<sizeof(result) / sizeof(result[0]); i++) {
		if(strcmp(&result[i], "\0") == 0) {
			cout << "string tail!" << ", index=" << i << endl;
			break;
		}
	}

	return 0;
}