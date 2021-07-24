#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int bar() {
    char *ptr = NULL;
    fprintf(stderr, "i'm bar, i will core dump !!!\n");
    fprintf(stderr, "%s", ptr);
    return 0;
}

int foo() {
    int i;
    fprintf(stderr, "i'm foo, i will call bar.\n");
    bar();
    return 0;
}

int main(int argc, char const *argv[]) {
    /* code */
    fprintf(stderr, "i'm main, i will call foo.\n");
    foo();
    return 0;
}
