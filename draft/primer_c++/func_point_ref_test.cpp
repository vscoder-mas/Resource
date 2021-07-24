#include <iostream>
#include <stdint.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>

using namespace std;

class Test {
public:
	Test() {

	}

	Test(size_t qty, double disc) : quantitiy(qty), discount(disc) {

	}

	Test(const Test &t) {
		cout << "copy !!!" << endl;
		quantitiy = t.quantitiy;
		discount = t.discount;
	}

	void getResult() {
		cout << "quantitiy=" << quantitiy << ", discount=" << discount << endl;
	}

	~Test() {

	}
public:
	size_t quantitiy;
	double discount;
};


void swap(Test test) {
	Test *addr = &test;
	cout << "step 2 addr=" << addr << endl;
	test.quantitiy = 20;
	test.discount = 9.9;
}

void swap1(Test *p) {
	p = new Test(20, 9.9);
	cout << "step 2 addr=" << p << endl;
}

void swap2(Test* &p) {
	p = new Test(20, 9.9);
	cout << "step 2 addr=" << p << endl;
}

void writeLine(FILE* &file, const char *data) {
    if (file == NULL) {
        file = fopen("111.txt", "a");
    }

    fprintf(file, "%s", data);
    int fd = fileno(file);
    fsync(fd);
    cout << "file addr2=" << file << endl;
}

int main(int argc, char const *argv[]) {
	/* code */
	Test test(10, 8.8);
	Test *addr = &test;
	cout << "step 1 addr=" << addr << endl;
	// swap(test);
	swap1(addr);
	// swap2(addr);
	cout << "step 3 addr=" << addr << endl;
	cout << "test.quantitiy=" << test.quantitiy << ", test.discount=" << test.discount << endl;
	cout << "addr->quantitiy=" << addr->quantitiy << ", addr->discount=" << addr->discount << endl;

	// FILE *file = fopen("111.txt", "a");
	// cout << "file addr1=" << file << endl;
	// writeLine(file, "hello world!!!\n");
	// cout << "file addr3=" << file << endl;

	FILE *file = NULL;
	if (strcmp(argv[1], "-c") == 0) {
		file = fopen("111.txt", "w");
		int a = -5;
		size_t b = 100;
		char test[] = "hello world !!!";
		fwrite(&a, sizeof(int), 1, file);
		fwrite(&b, sizeof(size_t), 1, file);
		fwrite(test, sizeof(char), strlen(test), file);
		fclose(file);
	} else if (strcmp(argv[1], "-i") == 0) {
		file = fopen("111.txt", "rb+");
		fseek(file, 4, SEEK_SET);
		size_t x = 300;
		fwrite(&x, sizeof(size_t), 1, file);
		fclose(file);
	} else if (strcmp(argv[1], "-r") == 0) {
		file = fopen("111.txt", "rb+");
		fseek(file, 4, SEEK_SET);
		size_t x = 0;
		fread(&x, sizeof(size_t), 1, file);
		cout << "-r before x=" << x <<  endl;

		x = 1;
		fseek(file, 4, SEEK_SET);
		fwrite(&x, sizeof(size_t), 1, file);
		cout << "-r after x=" << x <<  endl;
		fclose(file);
	}
	
	return 0;
}