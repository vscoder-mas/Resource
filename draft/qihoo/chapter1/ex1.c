#include <stdio.h>
#include <string.h>


struct Person {
    int age;
    char *name;
    char addr[20];
};

int main(int argc, char *argv[]) {
    printf("argc.count=%d, argv=%s\n", argc, argv[0]);
    int age = 10;
    int height = 88;
    printf("I am %d years old.\n", age);
    printf("I am %d inches tall.\n", height);


    struct Person p;
    p.age = 18;
    p.name = "lily";
    strcpy(p.addr, "beijing");
    printf("age=%d, name=%s, address=%s\n", p.age, p.name, p.addr);
    return 0;
}
