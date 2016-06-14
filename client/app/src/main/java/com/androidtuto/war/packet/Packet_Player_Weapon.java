package com.androidtuto.war.packet;

import com.androidtuto.war.Network;

public class Packet_Player_Weapon extends Packet_Command
{
    public int id;
    public int weapon;
    public int level;

    public Packet_Player_Weapon(){
        setCommand((short) PACKET.PACKET_PLAYER_WEAPON);
        id = Network.INSTANCE.getID();
    }

    public Packet_Player_Weapon(byte[] buf){

        super(buf);

        id = unpackInt();
        weapon = unpackInt();
        level = unpackInt();
    }

    public void GetBytes(byte[] buf){

        super.GetBytes(buf);
        pack(id);
        pack(weapon);
        pack(level);
    }
}
