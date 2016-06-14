package com.androidtuto.war.packet;

import com.androidtuto.war.Network;

public class Packet_Player_Update extends Packet_Command
{
    public int id;
    public float px, py, dx, dy, v;

    public Packet_Player_Update(){
        setCommand((short) PACKET.PACKET_PLAYER_UPDATE);
        id = Network.INSTANCE.getID();
    }

    public Packet_Player_Update(byte[] buf){

        super(buf);

        id = unpackInt();
        px = unpackFloat();
        py = unpackFloat();
        dx = unpackFloat();
        dy = unpackFloat();
        v = unpackFloat();
    }

    public void GetBytes(byte[] buf){

        super.GetBytes(buf);
        pack(id);
        pack(px);
        pack(py);
        pack(dx);
        pack(dy);
        pack(v);
    }
}
