#pragma once

#include <fstream>
#include <windows.h>
#include <process.h>
#include <winsock.h>
#include <hash_map>
#include <hash_set>

#include "Packet.h"
#include "RoomManager.h"

namespace Massacre{

#define BUF_SIZE 256
#define DIST_SUMMON 15.0

using namespace std;

class Network{

private:
	Network();
	~Network();

	static Network * m_instance;

public:
	static Network * get();
	static void init();

private:
	// default
	hash_map<SOCKET, User *> m_sockets;
	HANDLE m_hThread;
	HANDLE m_hMutex;

public:
	void writeAll(BYTE * buf, int size);
	void writeAll(Packet_Command * p);
	void write(BYTE * buf, int size, SOCKET socket);
	void write(Packet_Command * p, SOCKET socket);
	void writeRoom(BYTE * buf, int size, const Room * room);
	void writeRoom(Packet_Command * p, const Room * room);
	void writeRoom(BYTE * buf, int size, SOCKET socket, bool includeMe = true);
	void writeRoom(Packet_Command * p, SOCKET socket, bool includeMe = true);

public:
	static unsigned WINAPI HandleServer(void * arg);
	static unsigned WINAPI HandleClnt(void * arg);

	const hash_map<SOCKET, User *> * getSockets(){ return &m_sockets; }
};


}