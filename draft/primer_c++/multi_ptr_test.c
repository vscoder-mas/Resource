#include <stdio.h>
#include <string.h>
#include <stdlib.h>

int main(int argc, char const *argv[]) {
    /* code */
    char* ptr = (char*)calloc(100, 1);
    snprintf(ptr, 100, "%s", "hello world!");
    printf("1 str=%s\n", ptr);
    char* ptr1 = ptr;
    snprintf(ptr1, 100, "%s", "i am ptr two!");
    printf("2 str=%s\n", ptr);

    free(ptr);
    // free(ptr1); //double free or corruption (fasttop)
    if (ptr1 != NULL) {
        printf("3 str=%s\n", ptr1);
    }
    return 0;
}
