#include <iostream>

using namespace std;

class Screen {
public:
	typedef std::string::size_type pos;
	Screen(pos cur, std::string str):cursor(cur), contents(str) {
	}

	Screen(pos h, pos w) : height(h), width(w) {
	}

	pos getWidth() {
		return width;
	}

	pos getHeight() {
		return height;
	}

	pos getCursor() {
		return cursor;
	}

	std::string getContents() {
		return contents;
	}
	
	char get_cursor() const {return contents[cursor];}
	char get() const;
	char get(pos ht, pos wt) const;

	std::string contents;

	const std::string Screen::*data() {
		return &Screen::contents;
	} 
private:
	pos cursor;
	pos height, width;
};


char Screen::get() const {
	return contents[0];
}

char Screen::get(pos ht, pos wt) const {
	cout << "ht=" << ht << ", wt=" << wt << endl;
	return contents[4];	
}



int main(int argc, char const *argv[]) {
	/* code */
	Screen s(2, "hello");
	const string Screen::*pStr;
	Screen *pScreen = &s;

	// pStr = &Screen::contents;
	// const string result = pScreen->*pStr;

	// pStr = s.data();
	// const string result = pScreen->*pStr;
	// cout << "result=" << result << endl;

	char (Screen::*pget) (Screen::pos, Screen::pos) const;
	pget = &Screen::get;
	char value = (pScreen->*pget)(7,8);
	cout << "value=" << value << endl;

	char (Screen::*pget1) () const;
	pget1 = &Screen::get;
	char value1 = (pScreen->*pget1)();
	cout << "value1=" << value1 << endl;
	return 0;
}