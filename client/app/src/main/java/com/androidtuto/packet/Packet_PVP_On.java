package com.androidtuto.packet;

import com.androidtuto.war.Network;

public class Packet_PVP_On extends Packet_Command
{
    public int id;

    public Packet_PVP_On(){
        setCommand((short) PACKET.PACKET_PVP_ON);
        id = Network.INSTANCE.getID();
    }

    public Packet_PVP_On(byte[] buf){

        super(buf);

        id = unpackInt();
    }

    public void GetBytes(byte[] buf){

        super.GetBytes(buf);
        pack(id);
    }
}
