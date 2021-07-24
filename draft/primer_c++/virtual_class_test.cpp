#include <iostream>
#include <stdint.h>

using namespace std;

class Test {
public:
	Test() = default;
	Test(size_t qty, double disc) : quantitiy(qty), discount(disc) {

	}

	void getResult() {
		cout << "quantitiy=" << quantitiy << ", discount=" << discount << endl;
	}

	virtual double net_price(size_t t) const = 0;

protected:
	size_t quantitiy;
	double discount;
};

class SubTest : public Test {
public:
	SubTest() = default;
	SubTest(size_t qty, double disc, uint32_t c) : Test(qty, disc), count(c) {

	}

	double net_price(size_t t) const override;
private:
	uint32_t count;
};

double SubTest::net_price(size_t t) const {
	cout << "result=" << t * discount << endl;
}

int main(int argc, char const *argv[]) {
	/* code */
	// Test t(100, 8.88);
	// t.getResult();

	SubTest sb(1, 2.22, 6);
	sb.net_price(9);
	return 0;
}