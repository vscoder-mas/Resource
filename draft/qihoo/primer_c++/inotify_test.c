#include <sys/inotify.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>

/* 
struct inotify_event { 
   int      wd;       // Watch descriptor  
   uint32_t mask;     // Mask of events  
   uint32_t cookie;   // Unique cookie associating related  events (for rename(2)) 
   uint32_t len;      // Size of name field  
   char     name[];   // Optional null-terminated name  
}; 
 
*/  

int watch_inotify_events(int fd) {
	char event_buf[512];
	int ret;
	int event_pos = 0;
	int event_size = 0;
	struct inotify_event *event;

	//读事件是否发生，没有发生就会阻塞
	ret = read(fd, event_buf, sizeof(event_buf));
	printf("-> ret=%d\n", ret);

    //如果read的返回值，小于inotify_event大小出现错误
    if(ret < (int)sizeof(struct inotify_event)) {  
        printf("counld not get event!\n");  
        return -1;  
    }

	//因为read的返回值存在一个或者多个inotify_event对象，需要一个一个取出来处理
	while(ret >= (int)sizeof(struct inotify_event)) {
		event = (struct inotify_event *)(event_buf + event_pos);
		if (event->len) {
			if (event->mask & IN_CREATE) {
				printf("create file:%s, len=%d\n", event->name, event->len);
			} else {
				printf("delete file:%s, len=%d\n", event->name, event->len);
			}
		}

		event_size = sizeof(struct inotify_event) + event->len;
		ret -= event_size;
		event_pos += event_size;
	}

	return 0;
}

int main(int argc, char const *argv[]) {
	/* code */
	int inotifyFd;
	int ret;

	char *path = "/tmp";
	inotifyFd = inotify_init();
	ret = inotify_add_watch(inotifyFd, path, IN_CREATE | IN_DELETE);

	//处理事件
	watch_inotify_events(inotifyFd);

	if (inotify_rm_watch(inotifyFd, ret) == -1) {
		printf("inotify_rm_watch failed !!!\n");
		return -1;
	}

	close(inotifyFd);
	return 0;
}