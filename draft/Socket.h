#include <sys/socket.h>
#define DEFAULT_SOCKET_TIMEOUT 10
#define SOCKET_FAILURE_RETRY(exp) ({         \
    __typeof__(exp) _rc;                   \
    do {                                   \
        _rc = (exp);                       \
    } while (_rc == -1 && (errno == EINTR || errno == EAGAIN)); \
    _rc; })

class Socket{
public:
    Socket(struct timeval &tv);
    virtual ~Socket();
    int setTimeout();
    bool Connect(const char* name);
    int Open(int domain, int type, int protocol, int opt);
    void Close();
	bool sendMsg(const void* data, uint32_t size);
	bool recvMsg(void *data, uint32_t size);

private:
    int fd;
    struct timeval tv;
};
