#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <iostream>
#include <string>

using namespace std;
int main(int argc, char const *argv[]) {
	/* code */
	// string path = "./data/oae/c";
	string path = "list_del";
	if (access(path.c_str(), R_OK) == 0) {
		struct stat st;
		int ttt = stat(path.c_str(), &st);
		cout << "-> ttt=" << ttt << endl;
		
		if (st.st_mode & S_IFDIR) {
			cout << "directory exist !" << endl;
		} else {
			cout << "the path is a file !" << endl;
		}	 
	} else {
		cout << "path not exist !" << endl;
	}

	return 0;
}