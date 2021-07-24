#include <iostream>
#include <regex>
#include "string.h"
#include <string>

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
	const char *test = "ffffff8fe4489214";
	string jsonString(test);
	std::regex_constants::syntax_option_type fl = std::regex_constants::icase;  
    std::regex regReplaceExp("[A-Za-z_0-9]", fl);
    string result = std::regex_replace(jsonString, regReplaceExp, "\"$1\":");  
    cout << "result=" << result.c_ctr() << endl;
	return 0;
}