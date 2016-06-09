#include <cstdlib>
#include <fstream>

#include "stdafx.h"
#include "Network.h"


namespace Massacre{

using namespace std;

Network * Network::m_instance = NULL;

Network::Network()
{
	int param;
	m_hThread = (HANDLE)_beginthreadex(NULL, 0, Network::HandleServer, &param, 0, NULL);
}

Network::~Network(){
}

void Network::init()
{
	m_instance = new Network();
}

Network * Network::get()
{
	if (m_instance == NULL)
		init();

	return m_instance;
}


unsigned WINAPI Network::HandleServer(void * arg)
{
	WSADATA wsaData;
	SOCKET hServSock, hClntSock;
	SOCKADDR_IN servAdr, clntAdr;
	int clntAdrSz;
	HANDLE  hThread;

	if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0)
		return 0;

	Network::get()->m_hMutex = CreateMutex(NULL, FALSE, NULL);
	hServSock = socket(PF_INET, SOCK_STREAM, 0);

	memset(&servAdr, 0, sizeof(servAdr));
	servAdr.sin_family = AF_INET;
	servAdr.sin_addr.s_addr = htonl(INADDR_ANY);
	//servAdr.sin_addr.s_addr = inet_addr("192.168.3.222");
	servAdr.sin_port = htons(13431);

	if (bind(hServSock, (SOCKADDR*)&servAdr, sizeof(servAdr)) == SOCKET_ERROR)
		return 0;
	if (listen(hServSock, 5) == SOCKET_ERROR)
		return 0;

	while (1)
	{
		clntAdrSz = sizeof(clntAdr);
		hClntSock = accept(hServSock, (SOCKADDR*)&clntAdr, &clntAdrSz);

		WaitForSingleObject(Network::get()->m_hMutex, INFINITE);
		User * u = new User;
		u->mutex = CreateMutex(NULL, FALSE, NULL);
		Network::get()->m_sockets.insert(make_pair(hClntSock, u));
		ReleaseMutex(Network::get()->m_hMutex);

		hThread = (HANDLE)_beginthreadex(NULL, 0, Network::HandleClnt, (void*)&hClntSock, 0, NULL);


		Packet_Assign_ID p;
		p.id = hClntSock;
		Network::get()->write(&p, p.id);

	}
	closesocket(hServSock);
	WSACleanup();
	return 0;
}

unsigned WINAPI Network::HandleClnt(void * arg)
{
	SOCKET hClntSock = *((SOCKET*)arg);
	int strLen = 0, i;
	char msgBuf[BUF_SIZE];

	while ((strLen = recv(hClntSock, msgBuf, sizeof(msgBuf), 0)) != 0)
	{
		Packet_Command cmd((BYTE*)msgBuf);
		switch ((PACKET)cmd.getCommand())
		{
		case PACKET_PVP_ON:{
			Packet_PVP_On p((BYTE*)msgBuf);
			Network * n = Network::get();

			User * u = Network::get()->m_sockets.find(hClntSock)->second;
			u->room = RoomManager::get()->getRoomForNewUser(hClntSock);

			// Set position
			Packet_Player_Update reply;
			reply.id = p.id;
			reply.px = 0.0;
			reply.py = 0.0;
			reply.dx = 0.0;
			reply.dy = 0.0;
			reply.v = 0.0;

			if (u->room->sockets.size() >= 2){
				reply.px = u->room->minX;
				reply.py = u->room->minY;
				int d = rand() % 4;
				if (d == 0) reply.px = u->room->maxX + DIST_SUMMON;
				else if (d == 1) reply.px = u->room->minX - DIST_SUMMON;
				else if (d == 2) reply.py = u->room->maxY + DIST_SUMMON;
				else if (d == 3) reply.py = u->room->minY - DIST_SUMMON;
			}

			Network::get()->writeRoom(&reply, reply.id, true);

			Packet_Room_Info roomInfo;
			Room * room = n->m_sockets.find(hClntSock)->second->room;
			hash_set<SOCKET>::iterator it;
			for (it = room->sockets.begin(); it != room->sockets.end(); ++it){
				User * u = n->m_sockets.find(*it)->second;
				Packet_Room_Info::Info info;
				info.id = *it;
				info.weaponType = u->weaponType;
				info.weaponLevel = u->weaponLevel;

				roomInfo.infos.push_back(info);
			}

			Network::get()->write(&roomInfo, hClntSock);

			break;
		}
		case PACKET_PVP_OFF:{
			Packet_PVP_Off p((BYTE*)msgBuf);

			Room * r = Network::get()->m_sockets.find(p.id)->second->room;
			r->sockets.erase(p.id);
			if (r->sockets.size() == 0)
				RoomManager::get()->eraseRoom(r->index);

			Network::get()->writeRoom(&p, p.id, true);

			break;
		}
		case PACKET_PLAYER_UPDATE:{
			Packet_Player_Update p((BYTE*)msgBuf);
			Network * n = Network::get();

			User * u = n->m_sockets.find(p.id)->second;
			u->px = p.px;
			u->py = p.py;
			u->dx = p.dx;
			u->dy = p.dy;
			u->v = p.v;

			Room * r = u->room;
			/*if (r->minX > p.px) r->minX = p.px;
			if (r->minY > p.py) r->minY = p.py;
			if (r->maxX < p.px) r->maxX = p.px;
			if (r->maxY < p.py) r->maxY = p.py;*/

			r->minX = p.px;
			r->minY = p.py;
			r->maxX = p.px;
			r->maxY = p.py;

			Network::get()->writeRoom(&p, p.id, false);

			break;
		}
		case PACKET_PLAYER_WEAPON:{
			Packet_Player_Weapon p((BYTE*)msgBuf);
			User * u = Network::get()->m_sockets.find(hClntSock)->second;
			u->weaponType = p.weapon;
			u->weaponLevel = p.level;
			Network::get()->writeRoom(&p, p.id, false);
			break;
		}
		case PACKET_PLAYER_FIRE_ON:{
			Packet_Player_Fire_On p((BYTE*)msgBuf);
			Network::get()->writeRoom(&p, p.id, false);
			break;
		}
		case PACKET_PLAYER_FIRE_OFF:{
			Packet_Player_Fire_Off p((BYTE*)msgBuf);
			Network::get()->writeRoom(&p, p.id, false);
			break;
		}
		case PACKET_PLAYER_DEAD:{
			Packet_Player_Dead p((BYTE*)msgBuf);
			Network::get()->writeRoom(&p, p.id, false);
			break;
		}

		default:
			break;
		}


		/*
		WaitForSingleObject(hMutex, INFINITE);
		*Snake[msg.number].begin() = msg.p;
		ReleaseMutex(hMutex);
		*/

		/*MsgPos msg;
		msg.feed = Feed;
		for (i = 0; i<Snake.size(); ++i)
			msg.heads.push_back(*Snake[i].begin());

		SendMsg(&msg);*/

	}

	WaitForSingleObject(Network::get()->m_hMutex, INFINITE);
	ReleaseMutex(Network::get()->m_sockets.find(hClntSock)->second->mutex);
	Network::get()->m_sockets.erase(hClntSock);
	ReleaseMutex(Network::get()->m_hMutex);
	closesocket(hClntSock);
	return 0;
}

void Network::writeAll(BYTE * buf, int size)
{
	//WaitForSingleObject(m_hMutex, INFINITE);
	hash_map<SOCKET, User *>::iterator it;
	for (it = m_sockets.begin(); it != m_sockets.end(); ++it)
		send(it->first, (char *)buf, BUF_SIZE, 0);
	//ReleaseMutex(m_hMutex);
}

void Network::writeAll(Packet_Command * p)
{
	BYTE buf[BUF_SIZE];
	p->GetBytes(buf);
	int size = p->place;
	writeAll(buf, size);
}

void Network::write(BYTE * buf, int size, SOCKET socket)
{
	send(socket, (char *)buf, BUF_SIZE, 0);
}

void Network::write(Packet_Command * p, SOCKET socket)
{
	BYTE buf[BUF_SIZE];
	p->GetBytes(buf);
	int size = p->place;
	write(buf, size, socket);
}

void Network::writeRoom(BYTE * buf, int size, const Room * room)
{
	hash_set<SOCKET>::iterator it;
	for (it = room->sockets.begin(); it != room->sockets.end(); ++it){
		send(*it, (char *)buf, BUF_SIZE, 0);
	}
}

void Network::writeRoom(Packet_Command * p, const Room * room)
{
	BYTE buf[BUF_SIZE];
	p->GetBytes(buf);
	int size = p->place;
	writeRoom(buf, size, room);
}

void Network::writeRoom(BYTE * buf, int size, SOCKET socket, bool includeMe)
{
	Room * room = m_sockets.find(socket)->second->room;
	hash_set<SOCKET>::iterator it;
	for (it = room->sockets.begin(); it != room->sockets.end(); ++it){
		if (*it != socket || includeMe)
			send(*it, (char *)buf, BUF_SIZE, 0);
	}
}

void Network::writeRoom(Packet_Command * p, SOCKET socket, bool includeMe)
{
	BYTE buf[BUF_SIZE];
	p->GetBytes(buf);
	int size = p->place;
	writeRoom(buf, size, socket, includeMe);
}


}