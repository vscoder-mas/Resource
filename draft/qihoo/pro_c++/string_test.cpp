#include <iostream>

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
	std::string str = "Hello";
	std::string phrase = "Hello World";
	std::string slang = "Hiya";

	bool result1 = (str > phrase) ? true : false;
	bool result2 = (slang > phrase) ? true : false;

	cout << "result1=" << result1 << "\n"
	<< "result2" << result2 << endl;
	return 0;
}