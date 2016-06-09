#pragma once

#define _SILENCE_STDEXT_HASH_DEPRECATION_WARNINGS

#include <fstream>
#include <windows.h>
#include <process.h>
#include <winsock.h>
#include <hash_map>
#include <hash_set>

namespace Massacre{

#define ROOM_SIZE 5

using namespace std;

class Room;
class User;

class Room{
public:
	int index;
	hash_set<SOCKET> sockets;
	float minX, minY;
	float maxX, maxY;
	HANDLE hThread;

	Room();

public:
	static unsigned WINAPI HandleRoom(void * arg);

};

class User{
public:
	int weaponType, weaponLevel;
	float px, py, dx, dy, v;
	HANDLE mutex;
	Room * room;

	User(){
		weaponType = 0;
		weaponLevel = 0;
		px = 0.0;
		py = 0.0;
		dx = 0.0;
		dy = 0.0;
		v = 0.0;

		room = NULL;
	}
	~User(){
	}
};

class RoomManager{

private:
	RoomManager();
	~RoomManager();

	static RoomManager * m_instance;

public:
	static RoomManager * get();
	static void init();

private:
	int m_roomCnt;
	hash_map<int, Room *> m_rooms;

public:
	Room * getRoomForNewUser(SOCKET socket);
	void eraseRoom(int index);
};


}