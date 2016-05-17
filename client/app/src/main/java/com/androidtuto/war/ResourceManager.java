package com.androidtuto.war;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 초록 on 2015-05-01.
 */
public enum ResourceManager
{
    INSTANCE;

    private HashMap<Integer, Picture> m_pictures = new HashMap<Integer, Picture>();

    private SoundPool m_soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    private HashMap<Integer, Integer> m_sounds = new HashMap<Integer, Integer>();

    void addPicture(Context context, int tex, int programImage, int programSolidColor){
        m_pictures.put(tex, new Picture(context, tex, programImage, programSolidColor));
    }
    /*void addPicture(GL10 gl, Context context, int tex, int x, int y, int width, int height){
        for(int yy=0; yy<y; ++yy) {
            for (int xx = 0; xx < x; ++xx)
                m_pictures.put((tex - 0x7f020000) * 100000 + (yy * x) + xx + 1, new Picture(gl, context, tex, xx * width, yy * height, width, height));
        }
    }*/
    Picture getPicture(int tex){ return m_pictures.get(tex);}

    void addSound(Context context, int sound){m_sounds.put(sound, m_soundPool.load(context, sound, 0));}
    void playSound(int sound){m_soundPool.play(m_sounds.get(sound), 1f, 1f, 1, 0, 1f);}
}
