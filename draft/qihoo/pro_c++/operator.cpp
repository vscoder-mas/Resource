//
// Created by mashuai-s on 2017/10/31.
//

#include <iostream>
#include <stdint.h>

using namespace std;

class RMB {
public:
    RMB(double d) {
        yuan = d;
        jf = (d - yuan) / 100;
    }

    RMB interest(double rate);

    RMB add(RMB d);

    const void display() {
        cout << (yuan + jf / 100.0) << endl;
    }

    RMB operator+(RMB d) {
        return RMB(yuan + d.yuan + (jf + d.jf) / 100);
    }

    RMB operator*(double rate) {
        return RMB((yuan + jf / 100) * rate);
    }

private:
    uint32_t yuan;
    uint32_t jf;
};

RMB RMB::interest(double rate) {
    return RMB((yuan + jf / 100.0) * rate);
}

RMB RMB::add(RMB d) {
    return RMB(yuan + d.yuan + jf / 100.0 + d.jf / 100.0);
}

RMB expense1(RMB principle, double rate) {
    RMB interset = principle.interest(rate);
    return principle.add(interset);
}

RMB expense2(RMB princlple, double rate) {
    RMB interset = princlple * rate;
    return princlple + interset;
}

int main() {
    RMB x(10000.0);
    double rate = 0.035;
    expense1(x, rate).display();
    expense2(x, rate).display();
    return 0;
}
