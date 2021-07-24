#include <iostream>
#include <string>
#include <sstream>
#include <string.h>
#include "stdio.h"
#include "stdint.h"
#include <stdlib.h>

using namespace std;

void pass(int *ptr) {
    *ptr = 456;
}

int main(int argc, char const *argv[]) {
	/* code */
	// string test ("abc");
	// const char *key = "ad";
 //    std::string::size_type index = test.find(key);
 //    cout << "index=" << index << endl;



	// std::string::size_type index = 0;
 //    string test("{\"address\":[{\"id\":10,\"state\":0,\"start\":\"0xffffff8fdd40c000\",\"end\":\"0xffffff8fdd40d550\"},{\"id\":11,\"state\":1,\"start\":\"0x0\",\"end\":\"0x0\"},{\"id\":12,\"state\":0,\"start\":\"0xffffff8fdd412000\",\"end\":\"0xffffff8fdd4134b0\"}]}");

 //    while ((index = test.find("start\":\"", index)) != string::npos) {
 //    	index += strlen("start") + 3;
 //    	string sub = test.substr(index, test.length());
 //    	std::string::size_type after = sub.find_first_of("\"");
 //    	if (after != string::npos) {
 //    		string start = sub.substr(0, after);
 //    		cout << "start=" << start << endl;
 //    	}

 //    	index += after;
 //    }

	// std::string::size_type index = 0;
 //    string test("{\"address\":[{\"id\":10,\"state\":0,start\":\"0xffffff8fdd40c000\",\"end\":\"0xffffff8fdd40d550\"},{\"id\":11,\"state\":1,\"start\":\"0x0\",\"end\":\"0x0\"},{\"id\":12,\"state\":0,\"start\":\"0xffffff8fdd412000\",\"end\":\"0xffffff8fdd4134b0\"}]}");

 //    while ((index = test.find("start", index)) != string::npos) {
 //        string sub = test.substr(index - 1, 1);
 //        if (sub.compare(",") == 0) {
 //        	cout << "sub=" << sub << endl;	
 //        	break;
 //        }
 //    }

 //    const char *tailPattern = "---[ end trace ";
 //    char *ttt = "[  180.558670] -(1)[392:Binder:325_1]---[ end trace 0de33c20367672aa ]---";
 //    if (strstr(ttt, tailPattern) != NULL) {
 //        printf("!!!!!!!get\n");
 //    }
    

 //    char tempName[50] ={0};
 //    char template_t[] = "md5_test.XXXXXX";
 //    memset(tempName, 0, strlen(tempName));
 //    cout << "tempName strlen=" << strlen(tempName) << ", sizeof=" << sizeof(tempName) << endl;
 //    snprintf(tempName, sizeof(template_t), template_t);
 //    cout << "strlen=" << strlen(template_t) << ", sizeof=" << sizeof(template_t) << endl;
 //    cout << "result=" << tempName << endl;

    // string str = "abc8";
    // string x = str.substr(3, 1);
    // cout << x << endl;

    // size_t a = 88;
    // ostringstream os;
    // os << a;
    // cout << "result=" << os.str() << endl;

    cout << sizeof(size_t) << endl;
    cout << sizeof(uint8_t) << endl;
    cout << sizeof(uint16_t) << endl;
    cout << sizeof(int64_t) << endl;
    cout << sizeof(int32_t) << endl;
    cout << sizeof(int) << endl;

    char token[32] = "1234567890123456789012345678901";
    cout << "strlen=" << strlen(token) << ", sizeof=" << sizeof(token) << endl;

    uint32_t aaa = 1 << 10;
    cout << "aaa=" << aaa << endl;
    int bbb = 3 | (1 << 10);
    cout << "bbb=" << bbb << endl;


    int x = 123;
    printf("x=%d\n", x);
    pass(&x);
    printf("x=%d\n", x);


    char desc[10] = "abcdefghi";
    char src[6] = "12345";
    strncpy(desc, src, sizeof(desc));
    printf("desc=%s\n", desc);

    int i;
    for (i=0; i<sizeof(desc); i++) {
        if (desc[i] == '\0') {
            printf("!!! empty !!!\n");
        } else {
            printf("%c\n", desc[i]);
        }
    }

	return 0;
}