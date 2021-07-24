#include <stdio.h>
#include <pthread.h>
#include <unistd.h>


void *thr_fn1(void *arg) {
	printf("thread 1 returning.\n");
	return ((void *)1);
}

void *thr_fn2(void *arg) {
	printf("thread 2 exiting.\n");
	pthread_exit((void *)2);
}

void err_exit(int err, const char *msg) {
	printf("err=%d, msg=%s\n", err, msg);
}

int main(int argc, char const *argv[]) {
	/* code */
	int err;
	pthread_t tid1, tid2;
	void *tret;

	printf("tret=%p\n", tret);
	err = pthread_create(&tid1, NULL, thr_fn1, NULL);
	if(err != 0) {
		err_exit(err, "can't create thread 1.");
	}

	err = pthread_create(&tid2, NULL, thr_fn2, NULL);
	if(err != 0) {
		err_exit(err, "can't create thread 2.");
	}

	err = pthread_join(tid1, &tret);
	if(err != 0) {
		err_exit(err, "can't join with thread 1.");
	}

	printf("tret=%p\n", tret);
	printf("thread 1 exit code=%ld\n", (long)tret);

	err = pthread_join(tid2, &tret);
	if(err != 0) {
		err_exit(err, "can't join with thread 2.");
	}

	printf("tret=%p\n", tret);
	printf("thread 2 exit code=%ld\n", (long)tret);

	int x =100;
	int *p = &x;
	void *v = p;
	printf("x=%d, p=%p\n", x, (void *)p);
	printf("x=%d\n", *(int *)v);
	return 0;
}