package com.androidtuto.packet;

import java.util.ArrayList;

public class Packet_Room_Update extends Packet_Command
{
    public class Info{
        public int id;
        public float px, py, dx, dy, v;
    }

    public ArrayList<Info> infos = new ArrayList<Info>();

    public Packet_Room_Update(){
        setCommand((short) PACKET.PACKET_ROOM_UPDATE);
    }

    public Packet_Room_Update(byte[] buf){

        super(buf);

        int size = unpackInt();
        for(int i=0; i<size; ++i) {
            Info info = new Info();
            info.id = unpackInt();
            info.px = unpackFloat();
            info.py = unpackFloat();
            info.dx = unpackFloat();
            info.dy = unpackFloat();
            info.v = unpackFloat();
            infos.add(info);
        }
    }

    public void GetBytes(byte[] buf){

        super.GetBytes(buf);

        pack(infos.size());
        for(Info info : infos) {
            pack(info.id);
            pack(info.px);
            pack(info.py);
            pack(info.dx);
            pack(info.dy);
            pack(info.v);
        }
    }
}
