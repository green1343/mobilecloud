package com.androidtuto.war.packet;

public class Packet_Player_Fire_Off extends Packet_Command
{
    public int id;

    public Packet_Player_Fire_Off(){
        setCommand((short) PACKET.PACKET_PLAYER_FIRE_OFF);
    }

    public Packet_Player_Fire_Off(byte[] buf){

        super(buf);

        id = unpackInt();
    }

    public void GetBytes(byte[] buf){

        super.GetBytes(buf);
        pack(id);
    }
}
