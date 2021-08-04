#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <sys/un.h>
#include "../config.h"
#include "Socket.h"

Socket::Socket(struct timeval &tv){
	this->fd = -1;
	this->tv = tv;
}

Socket::~Socket(){
	this->Close();
}

int Socket::Open(int domain, int type, int protocol, int opt){
	int fd = -1, opts = 0, fds[3] = {-1, -1, -1}, i;
	fd = socket(domain, type, protocol);
	do{
		fd = socket(domain, type, protocol);
		if(fd == -1)
			break;
		if(fd < 3)
			fds[fd] = fd;
	}while(fd < 3);
	for(i = 0; i<3; i++){
		if(fds[i] >= 0)
			close(fds[i]);
	}
	if(fd < 0)
		goto end;
	opts = fcntl(fd, F_GETFL);
	if (opts < 0)
		goto end;
	opts = opts | opt;
	if (fcntl(fd, F_SETFL, opts) < 0)
		goto end;
	return fd;
end:
	if(fd >= 0)
		close(fd);
	LOGE("%s %s", __func__, strerror(errno));
	return -1;
}

void Socket::Close()
{
	if (this->fd >= 0){
		close(fd);
	}
	this->fd = -1;
}

bool Socket::Connect(const char *name)
{
	struct sockaddr_un addr;
	int err = 0;
	this->Close();
	this->fd = this->Open(PF_UNIX, SOCK_STREAM, 0, O_NONBLOCK);
	if(this->fd < 0)
		return false;
	memset(&addr, 0, sizeof(addr));
	addr.sun_family = AF_UNIX;
	snprintf(addr.sun_path, sizeof(addr.sun_path), "%s", name);
	err = SOCKET_FAILURE_RETRY(connect(this->fd, (struct sockaddr *)&addr, sizeof(addr)));
	if(err == -1)
		goto end;
	LOGD("Connected to server socket %s : fd=%d", name, this->fd); 
	return true;
end:
	LOGE("failed: %s %s %s", __func__, name, strerror(errno));
	this->Close();
	return false;
}

bool Socket::sendMsg(const void* data,  uint32_t size)
{
    int err = 0;
	uint32_t left = size;
    //LOGD("will send data %d bytes", size);
	while(left > 0){
		err = SOCKET_FAILURE_RETRY(send(this->fd, data, size, MSG_DONTWAIT | MSG_NOSIGNAL));
		if (err < 0)
			goto end;
		left -= err;
		data = (void*)((uintptr_t)data + err);
	}
    //LOGD("send data %d bytes done", err);
    return true;
end:
	LOGE("failed: %s %d %s", __func__, this->fd, strerror(errno));
	this->Close();
	return false;
}
bool Socket::recvMsg(void *data, uint32_t size)
{
	uint32_t left = size;
	int err = 0;
	fd_set fs;
	while(left > 0){
		FD_ZERO(&fs);
		FD_SET(this->fd, &fs);	
		LOGD("timeout: %ld %ld", this->tv.tv_sec, this->tv.tv_usec);
		err = select(this->fd + 1, &fs, NULL, NULL, &(this->tv));
		switch(err){
		case -1:{
			LOGD("%s:%d",__func__, __LINE__);
			goto end;
		}
		case 0:{
			LOGD("timeout: %ld %ld", this->tv.tv_sec, this->tv.tv_usec);
			LOGD("%s:%d timeout",__func__, __LINE__);
			goto end;
		}
		default:{
			if(!FD_ISSET(this->fd, &fs))
				continue;
			err = SOCKET_FAILURE_RETRY(recv(this->fd, data, left, MSG_DONTWAIT | MSG_NOSIGNAL));
			switch(err){
			case 0:
			case -1:{
				LOGD("%s:%d",__func__, __LINE__);
				goto end;
			}
			default:{
				left -= err;
				data = (void*)((uintptr_t)data + err);
				break;
			}
			}
			break;
		}
		}
	}
	return true;
end:
	LOGE("failed: %s %d %s", __func__, this->fd, strerror(errno));
	this->Close();
	return false;
}
