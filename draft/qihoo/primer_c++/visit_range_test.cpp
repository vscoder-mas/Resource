#include <iostream>
#include <stdint.h>

using namespace std;

class Base {
public:
	void pub_mem() {
		
	}
protected:
	uint32_t prot_mem;
private:
	char priv_mem;
};

class Pub_Test : public Base {
public:
	uint32_t f() {
		return prot_mem;
	}
};

class Priv_Test : Base {
public:
	uint32_t f1() const {
		return prot_mem;
	}	
};


int main(int argc, char const *argv[]) {
	/* code */
	Pub_Test pub;
	pub.f();
	pub.pub_mem();

	Priv_Test priv;
	priv.f1();
	priv.pub_mem();
	return 0;
}