#include <iostream>
using namespace std;

int main() {
    int a, b , result;
    cout << "please input 2 numbers: \n";
    cin >> a >> b;
    result = 3 * a - 2 * b + 1;
    cout << "result is " << result << endl;


    try {
   	int i = 1 / 0;
    } catch (...) {
	cout <<" exception \n"<< endl;
    }

    return 0;
}
