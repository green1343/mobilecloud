package com.androidtuto.packet;

import com.androidtuto.war.Network;

public class Packet_PVP_Off extends Packet_Command
{
    public int id;

    public Packet_PVP_Off(){
        setCommand((short) PACKET.PACKET_PVP_OFF);
        id = Network.INSTANCE.getID();
    }

    public Packet_PVP_Off(byte[] buf){

        super(buf);

        id = unpackInt();
    }

    public void GetBytes(byte[] buf){

        super.GetBytes(buf);
        pack(id);
    }
}
