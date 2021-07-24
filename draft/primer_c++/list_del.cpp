#include <iostream>
#include <list>

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
	list<int> lst = {0,1,2,3,4,5,6,7,8,9};
	std::list<int>::iterator it = lst.begin();
	while (it != lst.end()) {
		if(*it % 2) {
			it = lst.erase(it);
		} else {
			it++;	
		}
	}

	std::list<int>::iterator iterator = lst.begin();
	while (iterator != lst.end()) {
		cout << "value=" << *iterator << endl;
		iterator++;
	}

	return 0;
}