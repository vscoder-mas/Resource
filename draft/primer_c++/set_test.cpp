#include <iostream>
#include <set>

using namespace std;
int main(int argc, char const *argv[]) {
	/* code */
	set<int> list = {1,2,3};
	for(int item : list) {
		cout << "value=" << item << endl;
	}

	pair<set<int>::iterator, bool> result = list.insert(4);
	if(result.second) {
		cout << "insert success value=" << *result.first << endl;
	} else {
		cout << "insert failed" << endl;
	}

	return 0;
}