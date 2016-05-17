package com.androidtuto.packet;

import com.androidtuto.war.Network;

public class Packet_Player_Dead extends Packet_Command
{
    public int id;
    public int killer;

    public Packet_Player_Dead(){
        setCommand((short) PACKET.PACKET_PLAYER_DEAD);
        id = Network.INSTANCE.getID();
    }

    public Packet_Player_Dead(byte[] buf){

        super(buf);

        id = unpackInt();
        killer = unpackInt();
    }

    public void GetBytes(byte[] buf){

        super.GetBytes(buf);
        pack(id);
        pack(killer);
    }
}
