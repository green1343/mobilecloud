package com.androidtuto.war;

import com.androidtuto.packet.PACKET;
import com.androidtuto.packet.Packet_Assign_ID;
import com.androidtuto.packet.Packet_Command;
import com.androidtuto.packet.Packet_PVP_Off;
import com.androidtuto.packet.Packet_Player_Dead;
import com.androidtuto.packet.Packet_Player_Fire_Off;
import com.androidtuto.packet.Packet_Player_Fire_On;
import com.androidtuto.packet.Packet_Player_Update;
import com.androidtuto.packet.Packet_Player_Weapon;
import com.androidtuto.packet.Packet_Room_Info;
import com.androidtuto.packet.Packet_Room_Update;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 초록 on 2015-10-05.
 */
public class NetworkListener extends Thread{

    private InputStream m_stream;
    private boolean m_kill = false;

    public NetworkListener(InputStream stream)
    {
        m_stream = stream;
    }

    /*****************************************************
     *		Main loop
     ******************************************************/

    @Override
    public void run()
    {
        while(!Thread.interrupted())
        {
            try {
                byte stream[] = new byte[Network.BUF_SIZE];
                m_stream.read(stream, 0, Network.BUF_SIZE);
                Packet_Command cmd = new Packet_Command(stream);

                switch (cmd.getCommand())
                {
                    case PACKET.PACKET_ASSIGN_ID: {
                        Packet_Assign_ID p = new Packet_Assign_ID(stream);
                        Network.INSTANCE.setID(p.id);
                        Game.INSTANCE.getPlayer().setTeam(p.id);
                        break;
                    }
                    case PACKET.PACKET_PVP_OFF: {
                        Packet_PVP_Off p = new Packet_PVP_Off(stream);
                        Game.INSTANCE.deleteEnemyPlayer(p.id);
                        break;
                    }
                    case PACKET.PACKET_PLAYER_UPDATE: {
                        Packet_Player_Update p = new Packet_Player_Update(stream);

                        if(p.id == Network.INSTANCE.getID()){
                            Player player = Game.INSTANCE.getPlayer();
                            player.setPosition(p.px, p.py);
                            player.setDirection(p.dx, p.dy);
                            player.setVelocity(p.v);
                        }
                        else {
                            Player enemy = Game.INSTANCE.getEnemyPlayer(p.id);
                            if (enemy != null) {
                                enemy.setPosition(p.px, p.py);
                                enemy.setDirection(p.dx, p.dy);
                                enemy.setVelocity(p.v);
                            }
                        }
                        break;
                    }
                    case PACKET.PACKET_PLAYER_WEAPON: {
                        Packet_Player_Weapon p = new Packet_Player_Weapon(stream);
                        Player enemy = Game.INSTANCE.getEnemyPlayer(p.id);
                        if(enemy != null)
                            enemy.setWeaponMain(p.weapon, p.level);
                        break;
                    }
                    case PACKET.PACKET_PLAYER_FIRE_ON: {
                        Packet_Player_Fire_On p = new Packet_Player_Fire_On(stream);
                        Player enemy = Game.INSTANCE.getEnemyPlayer(p.id);
                        if(enemy != null)
                            enemy.getWeaponMain().touchDown(p.x, p.y);
                        break;
                    }
                    case PACKET.PACKET_PLAYER_FIRE_OFF: {
                        Packet_Player_Fire_Off p = new Packet_Player_Fire_Off(stream);
                        Player enemy = Game.INSTANCE.getEnemyPlayer(p.id);
                        if(enemy != null)
                            enemy.getWeaponMain().touchUp();
                        break;
                    }
                    case PACKET.PACKET_PLAYER_DEAD: {
                        Packet_Player_Dead p = new Packet_Player_Dead(stream);
                        Game.INSTANCE.killCharacter(Game.INSTANCE.getEnemyPlayer(p.id));
                        break;
                    }
                    case PACKET.PACKET_ROOM_INFO: {
                        Packet_Room_Info p = new Packet_Room_Info(stream);

                        Game g = Game.INSTANCE;
                        g.clearEnemyPlayer();
                        for(Packet_Room_Info.Info info : p.infos){
                            if(info.id == Network.INSTANCE.getID())
                                continue;
                            Player enemy = g.getEnemyPlayer(info.id);
                            enemy.setWeaponMain(info.weaponType, info.weaponLevel);
                        }

                        break;
                    }
                    case PACKET.PACKET_ROOM_UPDATE: {
                        Packet_Room_Update p = new Packet_Room_Update(stream);

                        Game g = Game.INSTANCE;
                        for(Packet_Room_Update.Info info : p.infos){
                            if(info.id == Network.INSTANCE.getID())
                                continue;
                            Player enemy = g.getEnemyPlayer(info.id);
                            enemy.setPosition(info.px, info.py);
                            enemy.setDirection(info.dx, info.dy);
                            enemy.setVelocity(info.v);
                        }

                        break;
                    }
                    default:
                        break;
                }
            } catch (IOException ex) {
                //System.err.println(ex);
            }

            if(m_kill)
                break;

        }	// End of while() loop

        // Finalize
        //finalizeThread();

    }	// End of run()

    void setKill(){
        m_kill = true;
    }
}
