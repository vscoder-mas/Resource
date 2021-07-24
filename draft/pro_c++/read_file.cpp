#include <iostream>
#include<stdio.h>
// #include <cstdio>

using namespace std;

int main(int argc, char const *argv[]) {
	/* code */
	char const *path = "333.txt";
	FILE  *file = fopen(path, "w");
	fseek(file, 0L, SEEK_END);
	long size = ftell(file);
	cout << "file size=" << size << endl;
	fclose(file);
	return 0;
}