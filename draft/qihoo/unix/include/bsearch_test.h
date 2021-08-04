#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct person {
	char last[16];
	char first[11];
	char phone[13];
	int age;
};

int comp(const void  *, const void  *);