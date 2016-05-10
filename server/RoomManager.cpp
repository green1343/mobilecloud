#include <cstdlib>
#include <fstream>

#include "stdafx.h"
#include "RoomManager.h"
#include "Packet.h"
#include "Network.h"


namespace Massacre{

using namespace std;

RoomManager * RoomManager::m_instance = NULL;

Room::Room(){
	index = 0;
	minX = INT_MAX;
	minY = INT_MAX;
	maxX = INT_MIN;
	maxY = INT_MIN;

	//hThread = (HANDLE)_beginthreadex(NULL, 0, Room::HandleRoom, (void*)this, 0, NULL);
}

unsigned WINAPI Room::HandleRoom(void * arg)
{
	const Room * room = (const Room *)arg;

	while (1)
	{
		Packet_Room_Update roomUpdate;
		hash_set<SOCKET>::iterator it;
		for (it = room->sockets.begin(); it != room->sockets.end(); ++it){
			User * u = Network::get()->getSockets()->find(*it)->second;
			Packet_Room_Update::Info info;
			info.id = *it;
			info.px = u->px;
			info.py = u->py;
			info.dx = u->dx;
			info.dy = u->dy;
			info.v = u->v;

			roomUpdate.infos.push_back(info);
		}

		Network::get()->writeRoom(&roomUpdate, room);


		Sleep(50);
	}
	return 0;
}

RoomManager::RoomManager()
{
	m_roomCnt = 0;
}

RoomManager::~RoomManager(){
	hash_map<int, Room *>::iterator it;
	for (it = m_rooms.begin(); it != m_rooms.end(); ++it)
		delete it->second;
}

void RoomManager::init()
{
	m_instance = new RoomManager();
}

RoomManager * RoomManager::get()
{
	if (m_instance == NULL)
		init();

	return m_instance;
}

Room * RoomManager::getRoomForNewUser(SOCKET socket)
{
	hash_map<int, Room *>::iterator it;
	for (it = m_rooms.begin(); it != m_rooms.end(); ++it)
	{
		if (it->second->sockets.size() < ROOM_SIZE){
			Room * room = it->second;
			room->sockets.insert(socket);
			return room;
		}
	}

	Room * room = new Room();
	room->index = m_roomCnt;
	room->sockets.insert(socket);
	m_rooms.insert(make_pair(m_roomCnt, room));
	++m_roomCnt;

	return room;
}

void RoomManager::eraseRoom(int index)
{
	m_rooms.erase(index);
}

}