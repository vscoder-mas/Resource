TARGET := muma
#ROOT := $(QDEAMON_ROOT)
# ROOT := /opt/root/openwrt_aarch64
#CC := gcc
CC := aarch64-openwrt-linux-gcc
RM := rm -f
# CFLAGS := -I$(ROOT)/include -I$(shell pwd)
CFLAGS += -DSKIP_PEER_VERIFICATION -DSKIP_HOSTNAME_VERIFICATION
CFLAGS += -DWC_RSA_BLINDING -DWOLFSSL_SHA3
LDFLAGS := -lm -lpthread
# LIBS := $(ROOT)/lib/libcurl.a $(ROOT)/lib/libwolfssl.a $(ROOT)/lib/libz.a $(ROOT)/lib/libjansson.a
SRCS := while_bomb.c
OBJS := $(SRCS:.c=.o)

.PHONY: all clean objs
all: $(TARGET)

objs: $(OBJS)
	$(CC) $(CFLAGS) -c $<  -o $@

$(TARGET): objs
	$(CC) -o $@ $(OBJS) $(LDFLAGS)

clean:
	$(RM) $(OBJS)
	$(RM) $(TARGET)
