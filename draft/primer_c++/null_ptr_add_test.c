#include <stdio.h>

int main(int argc, char const *argv[]) {
    /* code */
    void* obj = NULL;
    int i = 100;
    int* p = &i;
    printf("obj=%p, *p=%p, p=%p\n", &obj, &p, p);
    return 0;
}
