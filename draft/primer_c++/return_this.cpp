#include <iostream>
#include <stdint.h>
#include <list>

using namespace std;

class Screen {
public:
	Screen() = default;
	Screen(uint32_t w, uint32_t h) : width(w), height(h) {
	}

	uint32_t getWidth();
	uint32_t getHeight();
	Screen& setWidth(uint32_t w);
	Screen& setHeight(uint32_t h);
private:
	uint32_t width;
	uint32_t height;
};

uint32_t Screen::getWidth() {
	return width;
}

uint32_t Screen::getHeight() {
	return height;
}

Screen& Screen::setWidth(uint32_t w) {
	width = w;
	return *this;
}

Screen& Screen::setHeight(uint32_t h) {
	height = h;
	return *this;
}


int main(int argc, char const *argv[]) {
	/* code */
	// Screen s = Screen(100, 200);
	// s.setWidth(1).setHeight(2);
	// cout << "s.width=" << s.getWidth() << ", s.height=" << s.getHeight() << endl;

	Screen s;
	cout << "s.width=" << s.getWidth() << ", s.height=" << s.getHeight() << endl;
	return 0;
}