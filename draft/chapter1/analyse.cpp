//
// Created by mashuai-s on 2017/10/19.
//

#include <iostream>
#include <unistd.h>
#include <list>
#include <regex.h>
#include <cstdlib>
#include <stdio.h>
#include <errno.h>
#include <stdint.h>
#include <linux/types.h>
#include <cstring>



#ifndef SSIZE_MAX
# define SSIZE_MAX ((ssize_t) (SIZE_MAX / 2))
#endif

#if USE_UNLOCKED_IO
# include "unlocked-io.h"
# define getc_maybe_unlocked(fp)        getc(fp)
#elif !HAVE_FLOCKFILE || !HAVE_FUNLOCKFILE || !HAVE_DECL_GETC_UNLOCKED
# undef flockfile
# undef funlockfile
# define flockfile(x) ((void) 0)
# define funlockfile(x) ((void) 0)
# define getc_maybe_unlocked(fp)        getc(fp)
#else
# define getc_maybe_unlocked(fp)        getc_unlocked(fp)
#endif

static const char LAST_MSG_FILE[] = "/proc/last_kmsg";
static const char PSTORE_PATCH[] = "/sys/fs/pstore";
static const char PESORE_PREFIX[] = "console-ramoops";

static const char *LAST_ANALYSE_INFO = "info";
static const char *LAST_CRASH_INFO_PATH = "crash";

static const char *GetKernelLog();

ssize_t getline(char **lineptr, size_t *n, FILE *stream);

ssize_t getdelim(char **lineptr, size_t *n, int delimiter, FILE *fp);

ssize_t getline(char **lineptr, size_t *n, FILE *stream) {
    return getdelim(lineptr, n, '\n', stream);
}

ssize_t getdelim(char **lineptr, size_t *n, int delimiter, FILE *fp) {
    ssize_t result;
    size_t cur_len = 0;

    if (lineptr == NULL || n == NULL || fp == NULL) {
        errno = EINVAL;
        return -1;
    }

    flockfile (fp);

    if (*lineptr == NULL || *n == 0) {
        char *new_lineptr;
        *n = 120;
        new_lineptr = (char *) realloc(*lineptr, *n);
        if (new_lineptr == NULL) {
            result = -1;
            goto unlock_return;
        }
        *lineptr = new_lineptr;
    }

    for (;;) {
        int i;

        i = getc_maybe_unlocked (fp);
        if (i == EOF) {
            result = -1;
            break;
        }

        /* Make enough space for len+1 (for final NUL) bytes.  */
        if (cur_len + 1 >= *n) {
            size_t needed_max =
                    SSIZE_MAX < SIZE_MAX ? (size_t) SSIZE_MAX + 1 : SIZE_MAX;
            size_t needed = 2 * *n + 1;   /* Be generous. */
            char *new_lineptr;

            if (needed_max < needed)
                needed = needed_max;
            if (cur_len + 1 >= needed) {
                result = -1;
                errno = EOVERFLOW;
                goto unlock_return;
            }

            new_lineptr = (char *) realloc(*lineptr, needed);
            if (new_lineptr == NULL) {
                result = -1;
                goto unlock_return;
            }

            *lineptr = new_lineptr;
            *n = needed;
        }

        (*lineptr)[cur_len] = i;
        cur_len++;

        if (i == delimiter)
            break;
    }
    (*lineptr)[cur_len] = '\0';
    result = cur_len ? cur_len : result;

    unlock_return:
    funlockfile (fp); /* doesn't set errno */

    return result;
}

const char *GetKernelLog() {
    if (access(LAST_MSG_FILE, R_OK) == 0) {
	return LAST_MSG_FILE;
    }

    char patch[256];
    sprintf(patch, "%s/%s", PSTORE_PATCH, PESORE_PREFIX);
    if (access(patch, R_OK) == 0) {
	return patch;
    }

    sprintf(patch, "%s/%s-0", PSTORE_PATCH, PESORE_PREFIX);
    if (access(patch, R_OK) == 0) {
	return patch;
    }

    return NULL;
}

int main(int argc, char **argv) {
    printf("-> main() begin.");
    //const char *log = GetKernelLog();
    //const char *log = argv[1];
    printf("-> path=%s", argv[1]);

    char buf[1024];
    FILE *file = fopen(argv[1], "r");
    if (file == NULL) {
        printf("last_kmsg =%s\n", argv[1]);
        return 0;
    }

    setbuffer(file, buf, 1024);
    char *line = NULL;
    size_t len = 0;
    std::list<std::string> lstLine;

    regmatch_t pm[1];
    const size_t match = 1;

    regex_t regHead64;
    const char *headPattern64 = "CPU: [0-9]* PID: [0-9]* Comm: [A-Za-z_0-9/._:]* Tainted: [A-Za-z_0-9]*";
    regcomp(&regHead64, headPattern64, 0);

    regex_t regHead32;
    const char *headPattern32 = "CPU: [0-9]*    Not tainted.*";
    regcomp(&regHead32, headPattern32, 0);

    bool crashPrinted = false;
    bool crashed = false;
    bool findCpu = false;
    bool findPc = false;
    bool findLr = false;
    bool findPcLr = false;

    regex_t regPc1;
    const char *pc1Pattern = "PC is at.*";
    regcomp(&regPc1, pc1Pattern, 0);

    regex_t regLr1;
    const char *lr1Pattern = "LR is at.*";
    regcomp(&regLr1, lr1Pattern, 0);

    regex_t regPc2;
    const char *pc2Pattern = "pc : \\[<[A-Za-z_0-9]*>\\]";
    regcomp(&regPc2, pc2Pattern, 0);

    regex_t regLr2;
    const char *lr2Pattern = "lr : \\[<[A-Za-z_0-9]*>\\]";
    regcomp(&regLr2, lr2Pattern, 0);
    const char *tailPattern = "---[ end trace ";

    time_t t;
    t = time(NULL);
    int ii = time(&t);

    char infoPath[256];
    sprintf(infoPath, "%s_%d.txt", LAST_ANALYSE_INFO, ii);
    char crashPath[256];
    sprintf(crashPath, "%s_%d.txt", LAST_CRASH_INFO_PATH, ii);

    FILE *dest = fopen(infoPath, "w");
    FILE *crash = fopen(crashPath, "w");

    if (dest != NULL) {
        while (getline(&line, &len, file) != -1) {
	    //printf("-> line=%s\n", line);
            if (lstLine.size() < 10) {
                lstLine.push_back(std::string(line));
            } else {
                lstLine.pop_front();
                lstLine.push_back(std::string(line));
            }   

            if (REG_NOMATCH != regexec(&regHead64, line, match, pm, 0) ||
                REG_NOMATCH != regexec(&regHead32, line, match, pm, 0)) {
                findCpu = true;
                fprintf(dest, "%s", "================");
                fprintf(dest, "\n");

                std::list<std::string>::iterator it = lstLine.begin();
                while (it != lstLine.end()) {
                    fprintf(dest, "%s", it->c_str());
                    it++;
                }

                printf("%s", line);
                continue;
            }

            if (REG_NOMATCH != regexec(&regPc1, line, match, pm, 0)) {
                if (findCpu) {
                    fprintf(dest, "%s", line);
                }

                findPc = true;
                printf("%s", line);
                continue;
            }

            if (REG_NOMATCH != regexec(&regLr1, line, match, pm, 0)) {
                if (findCpu) {
                    fprintf(dest, "%s", line);
                }

                findLr = true;
                printf("%s", line);
                continue;
            }

            if (REG_NOMATCH != regexec(&regPc2, line, match, pm, 0) &&
                REG_NOMATCH != regexec(&regLr2, line, match, pm, 0)) {
                if (findCpu) {
                    fprintf(dest, "%s", line);
                }

                findPcLr = true;
                printf("%s", line);
                continue;
            }

            if (findCpu && findPc && findLr && findPcLr) {
                crashed = true;
            }

            if (crashed && !crashPrinted) {
                crashPrinted = true;
                std::list<std::string>::iterator it1 = lstLine.begin();
                while (it1 != lstLine.end()) {
                    fprintf(crash, "%s", it1->c_str());
                    it1++;
                }

                continue;
            }

	    if (std::strstr(line, tailPattern) != NULL) {
		if (findCpu) {
                    fprintf(dest, "%s", line);
                    fprintf(dest, "%s", "================");
                    fprintf(dest, "\n");
                    findCpu = false;

                    if (crashed) {
                        findPc = false;
                        findLr = false;
                        findPcLr = false;
                        crashed = false;
                        crashPrinted = false;
                        fprintf(crash, "%s", line);
                    }
                }
            } else {
		if (findCpu) {
                    fprintf(dest, "%s", line);

                    if (crashed) {
                        fprintf(crash, "%s", line);
                    }
                }
            }    
        }

        fclose(dest);
        fclose(crash);
    }

    free(line);
    fclose(file);
    return 0;
}
