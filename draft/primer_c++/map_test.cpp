#include <stdint.h>
#include <stdlib.h>
#include <iostream>
#include <map>
#include <list>

using namespace std;

struct __s_patch_info_ {
    uint32_t id;
    uint32_t load_type;
};

void test(list<void *> lst) {
	list<void *>::iterator ttt = lst.begin();
	while (ttt != lst.end()) {
		cout << "item2.addr=" << (uint32_t *)(*ttt) << endl;
		++ttt;
	}
}

void test1(map<void *, uint32_t> &ttt) {
	map<void *, uint32_t>::iterator mm = ttt.begin();
	while (mm != ttt.end()) {
		cout << "key=" << mm->first << ", value=" << mm->second << endl;
		++mm;
	}
}

int main(int argc, char const *argv[]) {
	/* code */
	// map<string, string> author = {{"jone", "james"},{"tom", "jack"},{"lucy", "lily"}};
	// for(pair<const string, string> &item : author) {
	// 	cout << "key=" << item.first << ", value=" << item.second << endl;
	// }

	// std::map<string, string>::iterator it = author.begin();
	// while (it != author.end()) {
	// 	cout << "key=" << it->first << ", value=" << it->second << endl;
	// 	it++;
	// }

	// // auto result = author.insert({"lilei", "haimeimei"});
	// pair<map<string, string>::iterator, bool> result = author.insert({"lilei", "haimeimei"});
	// if(result.second) {
	// 	cout << "result=" << result.first->second << endl;
	// }

	// list<void *> lst;
	// uint32_t x = 100;
	// uint32_t y = 200;
	// uint32_t z = 300;

	// lst.push_back(&x);
	// lst.push_back(&y);
	// lst.push_back(&z);
	// cout << "x=" << &x << ", y=" << &y << ", z=" << &z << endl;

	// list<void *>::iterator ttt = lst.begin();
	// while (ttt != lst.end()) {
	// 	cout << "item1.addr=" << (uint32_t *)(*ttt) << endl;
	// 	++ttt;
	// }

	// test(lst);

	// map<void *, uint32_t> ttt;
	// uint32_t x = 100;
	// uint32_t y = 200;
	// uint32_t z = 300;
	// cout << "x=" << &x << ", y=" << &y << ", z=" << &z << endl;

	// ttt.insert(pair<void *, uint32_t>(&x, x));
	// ttt.insert(pair<void *, uint32_t>(&y, y));
	// ttt.insert(pair<void *, uint32_t>(&z, z));

	// map<void *, uint32_t>::iterator mm = ttt.begin();
	// while (mm != ttt.end()) {
	// 	cout << "key=" << mm->first << ", value=" << mm->second << endl;
	// 	++mm;
	// }

	// test1(ttt);

	list<__s_patch_info_> lst;

	// __s_patch_info_ *info = (__s_patch_info_  *) malloc(sizeof(__s_patch_info_));
	__s_patch_info_ *info = new __s_patch_info_();
	info->id = 1;
	info->load_type = 100;
	lst.push_back(*info);
	delete info;
	info = NULL;

	cout << "-> list.size=" << lst.size() << endl;
	list<__s_patch_info_>::iterator it = lst.begin();
	while (it != lst.end()) {
		cout << "id=" << it->id << ", type=" << it->load_type << endl;
		++it;
	}

	return 0;
}