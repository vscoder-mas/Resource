#include <iostream>
#include <string>
#include <algorithm>
#include <vector>
#include <stdint.h>
#include <functional>

using namespace std;


// bool isShorter(const string &s1, const string &s2) {
// 	return s1.size() > s2.size();
// }

bool check_size(const string &s, string::size_type sz) {
	return s.size() > sz;
}

int main(int argc, char const *argv[]) {
	/* code */
	std::vector<string> v = {"abcdef","abcd","abc" };
	// sort(v.begin(), v.end(), isShorter);
	size_t length = 3;
	sort(v.begin(), v.end(), [] (const string &s1, const string &s2) -> bool {
		return s1.size() < s2.size();
	});

	std::vector<string>::iterator it = v.begin();
	while(it != v.end()) {
		cout << "value=" << *it << endl;
		it++;
	}

 	auto func = [length](const string &s) -> bool {
		return s.size() > length;
	};
	vector<string>::iterator result = find_if(v.begin(), v.end(), bind(check_size, std::placeholders::_1, length));

	cout << "reult=" << *result << endl;
	return 0;
}