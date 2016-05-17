package com.androidtuto.war;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

/**
 * Created by 초록 on 2015-06-12.
 */
public enum User {
    INSTANCE;

    Context m_context;

    // UserData
    int m_coin;
    int m_weaponMain;
    int m_weaponLevel[];
    int m_mapMax;
    boolean m_ad;

    public void init(Context context)
    {
        m_context = context;
        m_coin = 0;
        m_weaponMain = Weapon.MAIN_PISTOL;
        m_weaponLevel = new int[Weapon.TYPE_COUNT];
        m_weaponLevel[Weapon.MAIN_PISTOL] = 1;
        m_mapMax = 0;
        m_ad = false;

        //writeUserData();
        readUserData();

        // debug
        m_coin = 87000;
        for(int i=0; i<11; ++i)
            m_weaponLevel[i] = 3;
        m_mapMax = 30;
    }

    public void readUserData()
    {
        FileInputStream stream;

        try {
            stream = m_context.openFileInput("config.txt");
            Scanner s = new Scanner(stream);
            m_coin = s.nextInt();
            m_weaponMain = s.nextInt();
            for(int i=0; i<Weapon.TYPE_COUNT; ++i)
                m_weaponLevel[i] = s.nextInt();
            m_mapMax = s.nextInt();
            m_ad = s.nextBoolean();
            stream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeUserData()
    {
        FileOutputStream outputStream;
        String s = new String(" ");

        try {
            outputStream = m_context.openFileOutput("config.txt", Context.MODE_PRIVATE);
            outputStream.write(Integer.toString(m_coin).getBytes());
            outputStream.write(s.getBytes());
            outputStream.write(Integer.toString(m_weaponMain).getBytes());
            outputStream.write(s.getBytes());
            for(int i=0; i<Weapon.TYPE_COUNT; ++i) {
                outputStream.write(Integer.toString(m_weaponLevel[i]).getBytes());
                outputStream.write(s.getBytes());
            }
            outputStream.write(Integer.toString(m_mapMax).getBytes());
            outputStream.write(s.getBytes());
            outputStream.write(Boolean.toString(m_ad).getBytes());
            outputStream.write(s.getBytes());
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCoin(){return m_coin;}
    public void setCoin(int coin){
        m_coin = coin;
        Manager.INSTANCE.updateCoin(m_coin);
    }
    public int addCoin(int coin){
        m_coin += coin;
        Manager.INSTANCE.updateCoin(m_coin);
        for(int i=Weapon.TYPE_COUNT-1; i >= 0; --i){
            if(m_weaponLevel[i] > 0)
                break;
            else if(Manager.INSTANCE.getPrice(i, 0) <= m_coin){
                Manager.INSTANCE.highlightItem(true);
                break;
            }
        }

        return m_coin;
    }

    public boolean buy(int weapon){
        int price = Manager.INSTANCE.getPrice(weapon, m_weaponLevel[weapon]);
        if(m_coin < price)
            return false;
        else
        {
            addCoin(-price);
            addWeaponLevel(weapon);
            return true;
        }
    }

    public int getWeaponMain(){return m_weaponMain;}
    public boolean setWeaponMain(int weapon){
        if(m_weaponLevel[weapon] >= 1) {
            m_weaponMain = weapon;
            writeUserData();
            Game.INSTANCE.getPlayer().setWeaponMain(m_weaponMain, m_weaponLevel[m_weaponMain]);
            return true;
        }

        return false;
    }

    public int getWeaponLevel(int weapon){return m_weaponLevel[weapon];}
    public int getWeaponLevel(){return m_weaponLevel[m_weaponMain];}
    public void addWeaponLevel(int weapon){
        if(m_weaponLevel[weapon] < Weapon.LEVEL_MAX) {
            ++m_weaponLevel[weapon];
            writeUserData();
            if(weapon == m_weaponMain)
                Game.INSTANCE.getPlayer().setWeaponMain(m_weaponMain, m_weaponLevel[m_weaponMain]);
        }
    }

    public int getMapMax(){return m_mapMax;}
    public void setMapMax(int max){
        if(m_mapMax < max && max <= 40) {
            m_mapMax = max;
            writeUserData();
        }
    }

    public boolean getAd(){return m_ad;}
    public void setAd(boolean b){
        m_ad = b;
        writeUserData();
    }
}
