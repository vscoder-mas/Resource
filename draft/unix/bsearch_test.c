#include "include/bsearch_test.h"



int main(int argc, char const *argv[]) {
	/* code */
	int i;
	struct person *p;
	struct person key = {"", "", "555-1965", 0};
	struct person people[] = {
		{"ford", "heny", "555-1903", 100},
		{"tomy", "lusy", "555-1865", 101},
		{"lile", "haha", "555-1965", 102},
		{"john", "jnes", "555-1988", 103}
	};

	qsort(people, 4, sizeof(people[0]), comp);
	p = people;
	for(i=0; i<sizeof(people) / sizeof(people[0]); i++, p++) {
		printf("%s, %s, %s, %d\n", p->last, p->first, p->phone, p->age);
	}

	p = (struct person *) bsearch(&key, people, 4, sizeof(people[0]), comp);
	if (p != NULL) {
		printf("%s, %s, %s, %d\n", p->last, p->first, p->phone, p->age);
	} else {
		printf("not found!\n");
	}

	return 0;
}

int comp(const void  *x, const void *y) {
	struct person *p1 = (struct person *)x;
	struct person *p2 = (struct person *)y;
	return strcmp(p1->phone, p2->phone);
}