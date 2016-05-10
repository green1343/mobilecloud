#pragma once

#include <string>
#include <tuple>
#include <vector>
#include <cstdlib>


namespace Massacre
{
/*
* read는 Command를 따로 읽고 해당 클래스를 생성하여 읽음
* write는 Getbyte함수 호출하여 바로 write
*/

using namespace std;

enum PACKET
{
	PACKET_ASSIGN_ID = 1,
	PACKET_PVP_ON = 2,
	PACKET_PVP_OFF = 3,
	PACKET_PLAYER_UPDATE = 4,
	PACKET_PLAYER_WEAPON = 5,
	PACKET_PLAYER_FIRE_ON = 6,
	PACKET_PLAYER_FIRE_OFF = 7,
	PACKET_PLAYER_DEAD = 8,
	PACKET_ROOM_INFO = 9,
	PACKET_ROOM_UPDATE = 10,
};

typedef char BYTE;

class Packet_Command
{
private:
	short m_command;

public:
	int place;
	BYTE * buf;

protected:
	Packet_Command()
	{
		place = 0;
		m_command = 0;
	}

	Packet_Command(short command)
	{
		place = 0;
		m_command = command;
	}

public:
	Packet_Command(BYTE buf[])
	{
		place = 0;
		this->buf = buf;
		unpack(m_command);
	}

	short getCommand()
	{
		return m_command;
	}

	void setCommand(short command)
	{
		m_command = command;
	}

	virtual void GetBytes(BYTE buf[])
	{
		place = 0;
		this->buf = buf;
		pack(m_command);
	}

protected:
	void pack(bool value);
	void pack(short value);
	void pack(int value);
	void pack(long long value);
	void pack(float value);
	void pack(double value);
	void pack(string value);

	void unpack(bool &value);
	void unpack(short &value);
	void unpack(int &value);
	void unpack(long long &value);
	void unpack(float &value);
	void unpack(double &value);
	void unpack(string &value);
};

class Packet_Assign_ID : public Packet_Command
{
public:
	int id;

public:
	Packet_Assign_ID() : 
		Packet_Command()
	{
		setCommand(PACKET_ASSIGN_ID);
	}

public:
	Packet_Assign_ID(BYTE buf[]) :
		Packet_Command(buf)
	{
		unpack(id);
	}

	virtual void GetBytes(BYTE buf[])
	{
		Packet_Command::GetBytes(buf);
		pack(id);
	}
};

class Packet_PVP_On : public Packet_Command
{
public:
	int id;

public:
	Packet_PVP_On() :
		Packet_Command()
	{
		setCommand(PACKET_PVP_ON);
	}

public:
	Packet_PVP_On(BYTE buf[]) :
		Packet_Command(buf)
	{
		unpack(id);
	}

	virtual void GetBytes(BYTE buf[])
	{
		Packet_Command::GetBytes(buf);
		pack(id);
	}
};

class Packet_PVP_Off : public Packet_Command
{
public:
	int id;

protected:
	Packet_PVP_Off() :
		Packet_Command()
	{
		setCommand(PACKET_PVP_OFF);
	}

public:
	Packet_PVP_Off(BYTE buf[]) :
		Packet_Command(buf)
	{
		unpack(id);
	}

	virtual void GetBytes(BYTE buf[])
	{
		Packet_Command::GetBytes(buf);
		pack(id);
	}
};

class Packet_Player_Update : public Packet_Command
{
public:
	int id;
	float px, py, dx, dy, v;

public:
	Packet_Player_Update() :
		Packet_Command()
	{
		setCommand(PACKET_PLAYER_UPDATE);
	}

public:
	Packet_Player_Update(BYTE buf[]) :
		Packet_Command(buf)
	{
		unpack(id);
		unpack(px);
		unpack(py);
		unpack(dx);
		unpack(dy);
		unpack(v);
	}

	virtual void GetBytes(BYTE buf[])
	{
		Packet_Command::GetBytes(buf);
		pack(id);
		pack(px);
		pack(py);
		pack(dx);
		pack(dy);
		pack(v);
	}
};

class Packet_Player_Weapon : public Packet_Command
{
public:
	int id;
	int weapon;
	int level;

public:
	Packet_Player_Weapon() :
		Packet_Command()
	{
		setCommand(PACKET_PLAYER_WEAPON);
	}

public:
	Packet_Player_Weapon(BYTE buf[]) :
		Packet_Command(buf)
	{
		unpack(id);
		unpack(weapon);
		unpack(level);
	}

	virtual void GetBytes(BYTE buf[])
	{
		Packet_Command::GetBytes(buf);
		pack(id);
		pack(weapon);
		pack(level);
	}
};

class Packet_Player_Fire_On : public Packet_Command
{
public:
	int id;
	float x, y;

public:
	Packet_Player_Fire_On() :
		Packet_Command()
	{
		setCommand(PACKET_PLAYER_FIRE_ON);
	}

public:
	Packet_Player_Fire_On(BYTE buf[]) :
		Packet_Command(buf)
	{
		unpack(id);
		unpack(x);
		unpack(y);
	}

	virtual void GetBytes(BYTE buf[])
	{
		Packet_Command::GetBytes(buf);
		pack(id);
		pack(x);
		pack(y);
	}
};

class Packet_Player_Fire_Off : public Packet_Command
{
public:
	int id;
	float x, y;

public:
	Packet_Player_Fire_Off() :
		Packet_Command()
	{
		setCommand(PACKET_PLAYER_FIRE_OFF);
	}

public:
	Packet_Player_Fire_Off(BYTE buf[]) :
		Packet_Command(buf)
	{
		unpack(id);
		unpack(x);
		unpack(y);
	}

	virtual void GetBytes(BYTE buf[])
	{
		Packet_Command::GetBytes(buf);
		pack(id);
		pack(x);
		pack(y);
	}
};

class Packet_Player_Dead : public Packet_Command
{
public:
	int id;
	int killer;

public:
	Packet_Player_Dead() :
		Packet_Command()
	{
		setCommand(PACKET_PLAYER_DEAD);
	}

public:
	Packet_Player_Dead(BYTE buf[]) :
		Packet_Command(buf)
	{
		unpack(id);
		unpack(killer);
	}

	virtual void GetBytes(BYTE buf[])
	{
		Packet_Command::GetBytes(buf);
		pack(id);
		pack(killer);
	}
};

class Packet_Room_Info: public Packet_Command
{
public:
	class Info{
	public:
		int id;
		int weaponType, weaponLevel;
	};

	vector<Info> infos;

public:
	Packet_Room_Info() :
		Packet_Command()
	{
		setCommand(PACKET_ROOM_INFO);
	}

public:
	Packet_Room_Info(BYTE buf[]) :
		Packet_Command(buf)
	{
		int size;
		unpack(size);
		for (int i = 0; i < size; ++i){
			Info info;
			unpack(info.id);
			unpack(info.weaponType);
			unpack(info.weaponLevel);
			infos.push_back(info);
		}
	}

	virtual void GetBytes(BYTE buf[])
	{
		Packet_Command::GetBytes(buf);

		pack((int)infos.size());
		for (int i = 0; i < infos.size(); ++i){
			pack(infos[i].id);
			pack(infos[i].weaponType);
			pack(infos[i].weaponLevel);
		}
	}
};

class Packet_Room_Update: public Packet_Command
{
public:
	class Info{
	public:
		int id;
		float px, py, dx, dy, v;
	};

	vector<Info> infos;

public:
	Packet_Room_Update() :
		Packet_Command()
	{
		setCommand(PACKET_ROOM_UPDATE);
	}

public:
	Packet_Room_Update(BYTE buf[]) :
		Packet_Command(buf)
	{
		int size;
		unpack(size);
		for (int i = 0; i < size; ++i){
			Info info;
			unpack(info.id);
			unpack(info.px);
			unpack(info.py);
			unpack(info.dx);
			unpack(info.dy);
			unpack(info.v);
			infos.push_back(info);
		}
	}

	virtual void GetBytes(BYTE buf[])
	{
		Packet_Command::GetBytes(buf);

		pack((int)infos.size());
		for (int i = 0; i < infos.size(); ++i){
			pack(infos[i].id);
			pack(infos[i].px);
			pack(infos[i].py);
			pack(infos[i].dx);
			pack(infos[i].dy);
			pack(infos[i].v);
		}
	}
};

}