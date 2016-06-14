package com.androidtuto.war.packet;

import com.androidtuto.war.Network;

public class Packet_Player_Fire_On extends Packet_Command
{
    public int id;
    public float x, y;

    public Packet_Player_Fire_On(){
        setCommand((short) PACKET.PACKET_PLAYER_FIRE_ON);
        id = Network.INSTANCE.getID();
    }

    public Packet_Player_Fire_On(byte[] buf){

        super(buf);

        id = unpackInt();
        x = unpackFloat();
        y = unpackFloat();
    }

    public void GetBytes(byte[] buf){

        super.GetBytes(buf);
        pack(id);
        pack(x);
        pack(y);
    }
}
