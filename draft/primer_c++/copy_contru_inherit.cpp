#include <iostream>
#include <stdint.h>
using namespace std;

class Base {
public:
	Base() = default;
	Base(uint32_t x1,uint32_t y1) : x(x1), y(y1) {
	}

	Base(const Base &base) {
		x = base.x;
		y = base.y;
	}

	Base& operator=(const Base &base) {
		x = base.x;
		y = base.y;
		return *this;
	} 

	void Base_printf() {
		cout << "x=" << x << ", y=" << y << endl;
	}
private:
	uint32_t x;
	uint32_t y;
};

class Sub : public Base {
public:
	Sub() = default;
	Sub(uint32_t x, uint32_t y, uint32_t w1, uint32_t h1) : Base(x, y), w(w1), h(h1) {

	}

	Sub(const Sub &param) : Base(param) {
		w = param.w;
		h = param.h;
	}

	Sub& operator=(const Sub &sub) {
		Base::operator=(sub);
		w = sub.w;
		h = sub.h;
		return *this;
	}

	void Sub__printf() {
		Base_printf();
		cout << "w=" << w << ", h=" << h << endl;
	}
private:
	uint32_t w;
	uint32_t h;
};


int main(int argc, char const *argv[]) {
	/* code */
	// Base b1(1, 2);
	// Base b2 = b1;
	// b2.Base_printf();

	// Sub s(5, 6, 7, 8);
	// s.Sub__printf();

	// Sub s1 = s;
	// s1.Sub__printf();
	// Sub s2;
	// s2 = s1;
	// s2.Sub__printf();


	//error example
	// Base t1;
	// Sub *sb = &t1;
	// Sub &sb2 = t1;

	//sliced down
	Sub s(1,2,3,4);
	Base b(s);
	b.Base_printf();

	return 0;
}