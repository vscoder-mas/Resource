#include <iostream>
#include<stdio.h>
#include<stdint.h>
#include<list>

using namespace std;

#pragma pack(1)
struct Patch {
    uint8_t cmd;
    uint8_t state;
    uint16_t size;
    uint32_t id;
    uintptr_t start;
    uintptr_t end;
    uint8_t  count;
    uint32_t error;

    size_t addrCount;
    std::list<uintptr_t> lstAddr;
};

struct Test {
    size_t size;
    uint8_t count;    
};

#pragma pack()

union Union_Test {
    size_t id;
    uint8_t type;
};

int main(int argc, char const *argv[]) {
	/* code */
	size_t size = sizeof(Test);
	cout << "Patch sizeof=" << size << endl;
    cout << "Union_Test sizeof=" << sizeof(Union_Test) << endl;
	return 0;
}