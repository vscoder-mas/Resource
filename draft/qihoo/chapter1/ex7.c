#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>

#define MAX_DATA 512
#define MAX_ROWS 100

struct Address {
	int id;
	int set;
	char name[MAX_DATA];
	char email[MAX_DATA];
};

struct Database {
	struct Address rows[MAX_ROWS];
};

struct Connection {
	FILE *file;
	struct Database *db;
};

void Address_print(struct Address *addr) {
	printf("%d %s %s \n", addr->id, addr->name, addr->email);
}

void die(const char *message)
{
	if(errno) {
		perror(message);
	} else {
		printf("ERROR: %s\n", message);
	}
	exit(1);
}

void Database_load(struct Connection *conn) {
	int rc = fread(conn->db, sizeof(struct Database), 1, conn->file);
	if(rc != 1) {
		printf("failed to load database.");
		exit(1);
	}
}


struct Coniection *Database_open(const char *fileName, char mode) {
	struct Connection *conn = malloc(sizeof(struct Connection));
		if(!conn) {
			printf("init conn failed! memory error.");
			exit(1);
		}

	conn->db = malloc(sizeof(struct Database));
	if(!conn->db) {
		printf("init database failed! memory error.");
		exit(1);
	}

	if(mode == 'c') {
		printf("fopen begin\n");
		conn->file = fopen(fileName, "W");
		printf("fopen end\n");
	} else {
		conn->file = fopen(fileName, "r+");
		if(conn->file) {
			Database_load(conn);
		}
	}

	printf("Database_open end\n");
	return conn;
}


void Database_close(struct Connection *conn) {
	if(conn) {
		if(conn->file) {
			fclose(conn->file);
		}

		if(conn->db) {
			free(conn->db);
		}
		free(conn);
	}
}


void Database_write(struct Connection *conn) {
	rewind(conn->file);
	int rc = fwrite(conn->db, sizeof(struct Database), 1, conn->file);
	rc = fflush(conn->file);
}

void Database_create(struct Connection *conn) {
	int i=0;
	for(i=0; i<MAX_ROWS; i++){
		struct Address addr = {.id=i, .set=0};
		conn->db->rows[i] = addr;
	}
}

int main(int argc, char *argv[]) {
	if(argc < 3) {
		printf("params count invalid!\n");
		exit(1);
	}

	char *fileName = argv[1];
	char action = argv[2][0];
	printf("fileName=%s, action=%c\n", fileName, action);
	struct Connection *conn = Database_open(fileName, action);
	

	int id = 0;
//	if(argc > 3) id = atoi(argv[3]);
//	if(id >= MAX_ROWS) die("There's not that many records.");
	printf("id=%d", id);

	
	switch(action) {
		case 'c':
			Database_create(conn);
			Database_write(conn);
			break;
//		case 'g':
//			if(argc != 4) die("Need an id to get");
//			Database_get(conn, id);
//			break;
//		case 's':
//			if(argc != 6) die("Need id, name, email to set");
//			Database_set(conn, id, argv[4], argv[5]);
//			Database_write(conn);
//			break;
//		case 'd':
//			if(argc != 4) die("Need id to delete");
//			Database_delete(conn, id);
//			Database_write(conn);
//			break;
//		case 'l':
//			Database_list(conn);
//			break;
//		default:
	//		die("Invalid action, only: c=create, g=get, s=set, d
	//				=del, l=list");
//			break;
	}

	Database_close(conn);
	return 0;
}


















