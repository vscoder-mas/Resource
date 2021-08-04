#include <stdio.h>
#include <stdint.h>
#include <list>

using namespace std;

struct Patch {
    uint8_t cmd;
    uint8_t state;
    uint16_t size;
    uint32_t id;
    uintptr_t start;
    uintptr_t end;
    uint8_t  count;
    int32_t error;
};

struct PatchLocal {
    Patch p;
    size_t addrCount;
    std::list<uintptr_t> lstAddr;
};

struct PatchData {
	PatchLocal mPatch;
};

int main(int argc, char const *argv[]) {
	/* code */
	Patch p;
	p.cmd = 1;	
    p.state = 2;
    p.size = 3;
    p.id = 4;
    p.start = 0x1234567890123456;
    p.end = 0x1111111122222222;
    p.count = 5;
    p.error = 6;

    list<uintptr_t> lstAddr;
    lstAddr.push_back(0x8888888888888888);
    lstAddr.push_back(0x9999999999999999);

    PatchLocal local;
    local.p = p;
    local.addrCount = 100;
    local.lstAddr = lstAddr;

    list<PatchData> lstPd;
    PatchData pd;
    pd.mPatch = local;
    lstPd.push_back(pd);

    FILE *msg = fopen("json.txt", "a+");
    if (msg != NULL) {
        //保存patch地址信息
        if (!lstPd.empty()) {
            size_t i = 0;
            fprintf(msg, "%s", "{\"address\":[");
            std::list<PatchData>::const_iterator iterator = lstPd.begin();
            while (iterator != lstPd.end()) {
                fprintf(msg, "%s", "{\"id\":");
                fprintf(msg, "%d", iterator->mPatch.p.id);
                fprintf(msg, "%s", ",\"state\":");
                fprintf(msg, "%d", iterator->mPatch.p.state);

                if(!iterator->mPatch.lstAddr.empty()) {
                    size_t j = 0;
                    fprintf(msg, "%s", ",\"addr_list\":[{");
                    std::list<uintptr_t>::const_iterator addr = iterator->mPatch.lstAddr.begin();
                    while (addr != iterator->mPatch.lstAddr.end()) {
                        fprintf(msg, "%s", "\"addr\":");
                        fprintf(msg, "%p", (void *) iterator->mPatch.p.start);
                        if(j == iterator->mPatch.lstAddr.size()-1) {
                            fprintf(msg, "%s", "}]");
                        } else {
                            fprintf(msg, "%s", ",");
                        }
                        addr++;
                        j++;
                    }
                }

                fprintf(msg, "%s", ",\"start\":");
                fprintf(msg, "%p", (void *) iterator->mPatch.p.start);
                fprintf(msg, "%s", ",\"end\":");

                if (i == lstPd.size() - 1) {
                    fprintf(msg, "%p}]", (void *) iterator->mPatch.p.end);
                } else {
                    fprintf(msg, "%p},", (void *) iterator->mPatch.p.end);
                }

                iterator++;
                i++;
            }

            fprintf(msg, "%s\n", "}");
            fprintf(msg, "%s\n", "================");
        }

        fprintf(msg, "\n%s\n", "++++++++++++++++");
    }

    fclose(msg);
	return 0;
}