#include <iostream>
#include <ctype.h>
#include <string.h>
#include <string>


using namespace std;

    void string_replace( std::string &strBig, const std::string &strsrc, const std::string &strdst) {
        std::string::size_type pos = 0;
        std::string::size_type srclen = strsrc.size();
        std::string::size_type dstlen = strdst.size();

        while((pos=strBig.find(strsrc, pos)) != std::string::npos) {
            strBig.replace( pos, srclen, strdst );
            pos += dstlen;
        }
    }


int main(int argc, char const *argv[]) {
	/* code */
	// string str("hello world!");
	// for(char &c : str) {
	// 	c = toupper(c);
	// }

	// cout << "result=" << str << endl;

	// char *test = "binder_update_page_range+0x2f8/0x314";
	// char *key = "";
	// string t(test);
	// string::size_type index = t.find_first_of(key) + 1;
	// string sub = t.substr(index, strlen(test) - index);
	// cout << "result=" << sub.c_str() << endl;

	// const char *test = "binder_update_page_range+0x2f8/0x314";
	// unsigned long lr = strtoul(test, NULL, 16);
	// cout << "result=" << lr << endl;


	string str = "Nnd2ExtdiV8RiSfTZIoWHzssUajLfBrcyeoFcVemKNvcmQizfyr4TB+1OzmKqXtaiUqQseIgvoSgYxrk/PLrRO7ANKc7tp1QBuh/eLlVqFw3PcKr3znDh6FS1pq1jObht6LO/0rasyNTXWnX/7Dlcv9twN9Zwb0Ygk0zKgxo6g7weZvPlyExvW0zvquJNArjtnmw+zGaU28/MOyr18bg9KzCKD/0Y/mNAKaiXdqMbM60Q6EZuMjVq7yQc5JFGMHh";
	// str=str.replace(str.find("+"), str.find("+") , "%2B");  //从第一个a位置开始的两个字符替换成#
	// cout<<str<<endl;
	string_replace(str, "+", "%2B");
    cout<<str<<endl;
	return 0;
}