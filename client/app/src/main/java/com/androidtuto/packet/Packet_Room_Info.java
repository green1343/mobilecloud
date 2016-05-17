package com.androidtuto.packet;

import java.util.ArrayList;

public class Packet_Room_Info extends Packet_Command
{
    public class Info{
        public int id;
        public int weaponType;
        public int weaponLevel;
    }

    public ArrayList<Info> infos = new ArrayList<Info>();

    public Packet_Room_Info(){
        setCommand((short) PACKET.PACKET_ROOM_INFO);
    }

    public Packet_Room_Info(byte[] buf){

        super(buf);

        int size = unpackInt();
        for(int i=0; i<size; ++i) {
            Info info = new Info();
            info.id = unpackInt();
            info.weaponType = unpackInt();
            info.weaponLevel = unpackInt();
            infos.add(info);
        }
    }

    public void GetBytes(byte[] buf){

        super.GetBytes(buf);

        pack(infos.size());
        for(Info info : infos) {
            pack(info.id);
            pack(info.weaponType);
            pack(info.weaponLevel);
        }
    }
}
