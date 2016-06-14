package com.androidtuto.war.packet;

public class Packet_Assign_ID extends Packet_Command
{
    public int id;

    public Packet_Assign_ID(){
        setCommand((short) PACKET.PACKET_ASSIGN_ID);
    }

    public Packet_Assign_ID(byte[] buf){

        super(buf);

        id = unpackInt();
    }

    public void GetBytes(byte[] buf){

        super.GetBytes(buf);
        pack(id);
    }
}
