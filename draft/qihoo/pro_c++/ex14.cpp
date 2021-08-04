#include <fstream>
#include <iostream>

using namespace std;

int main(int argc, char *argv[]) {
   ofstream fout("test.out");
   if(!fout.good()) {
       cout << "open file failed!" << endl;
       return 1;
   }

   fout << "12345";
   ios_base::streampos curPos = fout.tellp();
   if(curPos == 5) {
       cout << "Test passed: currently at position=5" << endl;
   } else {
       cout << "Test failed: not at position 5" << endl;
   }

   fout.seekp(2, ios_base::beg);
   fout << 0;
   fout.close();

   ifstream fin("test.out");
   if(!fin) {
       cout << "open file failed! 1" << endl;
       return 1;
   }

   int testVal;
   fin >> testVal;

   if(testVal == 12045) {
       cout << "test passed value=" << testVal << endl;
   }

   ifstream infile("input.txt");
   ofstream outfile("input.txt");
   infile.tie(&outfile);

   outfile << "helloworld!";
   string token;
   infile >> token;
   cout << "token=" << token << endl;
    return 0;
}