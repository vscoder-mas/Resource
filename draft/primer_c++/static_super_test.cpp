#include <iostream>
#include <stdint.h>
#include "stdio.h"
using namespace std;

class Test {
public:
	static void printfTest();
protected:
	virtual static void realBySub();
};

class SubTest : public Test {
public:
	static void printfSubTest();
	static void realBySub();
};

void Test::printfTest() {
	printf("printfTest().\n");
}

void SubTest::printfSubTest() {
	printf("printfSubTest()\n");
}

void SubTest::realBySub() {
	printf("realBySub()\n");
}

int main(int argc, char const *argv[]) {
	/* code */
	// SubTest t;
	// t.printfSubTest();
	// t.printfTest();

	SubTest::printfSubTest();
	SubTest::printfTest();
	SubTest::realBySub();
	return 0;
}