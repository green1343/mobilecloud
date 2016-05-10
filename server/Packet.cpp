#include "stdafx.h"
#include "Packet.h"


namespace Massacre
{
	void Packet_Command::pack(bool value)
	{
		buf[place] = value ? 1 : 0;
		place += 1;
	}

	void Packet_Command::pack(short value)
	{
		union temp {
			short value;
			BYTE    c[2];
		} in, out;
		in.value = value;
		out.c[0] = in.c[1];
		out.c[1] = in.c[0];
		memcpy(buf+place, out.c, 2);
		place += 2;
	}

	void Packet_Command::pack(int value)
	{
		union temp {
			int value;
			BYTE    c[4];
		} in, out;
		in.value = value;
		out.c[0] = in.c[3];
		out.c[1] = in.c[2];
		out.c[2] = in.c[1];
		out.c[3] = in.c[0];
		memcpy(buf + place, out.c, 4);
		place += 4;
	}

	void Packet_Command::pack(long long value)
	{
		union temp {
			long long value;
			BYTE    c[8];
		} in, out;
		in.value = value;
		out.c[0] = in.c[7];
		out.c[1] = in.c[6];
		out.c[2] = in.c[5];
		out.c[3] = in.c[4];
		out.c[4] = in.c[3];
		out.c[5] = in.c[2];
		out.c[6] = in.c[1];
		out.c[7] = in.c[0];
		memcpy(buf + place, out.c, 8);
		place += 8;
	}

	void Packet_Command::pack(float value)
	{
		union temp {
			float   value;
			BYTE    c[4];
		} in, out;
		in.value = value;
		out.c[0] = in.c[3];
		out.c[1] = in.c[2];
		out.c[2] = in.c[1];
		out.c[3] = in.c[0];
		memcpy(buf + place, out.c, 4);
		place += 4;
	}

	void Packet_Command::pack(double value)
	{
		union temp {
			double  value;
			BYTE    c[8];
		} in, out;
		in.value = value;
		out.c[0] = in.c[7];
		out.c[1] = in.c[6];
		out.c[2] = in.c[5];
		out.c[3] = in.c[4];
		out.c[4] = in.c[3];
		out.c[5] = in.c[2];
		out.c[6] = in.c[1];
		out.c[7] = in.c[0];
		memcpy(buf + place, out.c, 8);
		place += 8;
	}

	void Packet_Command::pack(string value)
	{
		pack((int)value.size());
		for (int i = 0; i < value.size(); ++i)
			buf[place + i] = value[i];
		place += value.size();
	}

	void Packet_Command::unpack(bool &value)
	{
		value = buf[place] == 1;
		place += 1;
	}

	void Packet_Command::unpack(short &value)
	{
		union temp {
			short value;
			BYTE    c[2];
		} in, out;
		memcpy(in.c, buf + place, 2);
		out.c[0] = in.c[1];
		out.c[1] = in.c[0];
		memcpy(&value, &out.value, 2);
		place += 2;
	}

	void Packet_Command::unpack(int &value)
	{
		union temp {
			int value;
			BYTE    c[4];
		} in, out;
		memcpy(in.c, buf + place, 4);
		out.c[0] = in.c[3];
		out.c[1] = in.c[2];
		out.c[2] = in.c[1];
		out.c[3] = in.c[0];
		memcpy(&value, &out.value, 4);
		place += 4;
	}

	void Packet_Command::unpack(long long &value)
	{
		union temp {
			long long value;
			BYTE    c[8];
		} in, out;
		memcpy(in.c, buf + place, 8);
		out.c[0] = in.c[7];
		out.c[1] = in.c[6];
		out.c[2] = in.c[5];
		out.c[3] = in.c[4];
		out.c[4] = in.c[3];
		out.c[5] = in.c[2];
		out.c[6] = in.c[1];
		out.c[7] = in.c[0];
		memcpy(&value, &out.value, 8);
		place += 8;
	}

	void Packet_Command::unpack(float &value)
	{
		union temp {
			float   value;
			BYTE    c[4];
		} in, out;
		memcpy(in.c, buf + place, 4);
		out.c[0] = in.c[3];
		out.c[1] = in.c[2];
		out.c[2] = in.c[1];
		out.c[3] = in.c[0];
		memcpy(&value, &out.value, 4);
		place += 4;
	}

	void Packet_Command::unpack(double &value)
	{
		union temp {
			double  value;
			BYTE    c[8];
		} in, out;
		memcpy(in.c, buf + place, 8);
		out.c[0] = in.c[7];
		out.c[1] = in.c[6];
		out.c[2] = in.c[5];
		out.c[3] = in.c[4];
		out.c[4] = in.c[3];
		out.c[5] = in.c[2];
		out.c[6] = in.c[1];
		out.c[7] = in.c[0];
		memcpy(&value, &out.value, 8);
		place += 8;
	}

	void Packet_Command::unpack(string &value)
	{
		int size;
		unpack(size);
		for (int i = 0; i < size; ++i)
			value.push_back(buf[place + i]);
		place += size;
	}
}