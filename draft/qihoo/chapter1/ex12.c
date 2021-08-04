#include <stdio.h>
//#include <conio.h>
#include <string.h>

int main(int argc, char *argv[]) {
    char *s = "Golden Global View";
    char *l = "lob";
    char *p;
    
    p = strstr(s, l);
    if(p != NULL) {
        printf("%s\n", p);
    }

    return 0;
}
