package com.androidtuto.war;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.SystemClock;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ClearRenderer implements GLSurfaceView.Renderer
{
    private float MAP_WIDTH = 0f;
    private float MAP_HEIGHT = 0f;

    private Context m_context;
    private int m_width;
    private int m_height;

    private float m_lastFrameTime;

    Advertise m_ad;

    public ClearRenderer(Context context)
    {
        MAP_WIDTH = 11f;
        MAP_HEIGHT = 6.53f;

        m_context = context;
        m_lastFrameTime = 0;

        m_ad = new Advertise(context);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        GLU.gluOrtho2D(gl, -MAP_WIDTH, MAP_WIDTH, -MAP_HEIGHT, MAP_HEIGHT);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
        GL10.GL_REPEAT);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
        GL10.GL_REPEAT);

        for(int i = 0x7f020000; i <= R.drawable.zzz; ++i)
            ResourceManager.INSTANCE.addPicture(gl, m_context, i);

        //for(int i = 0x7f040000; i <= 0x7f040001; ++i)
        //    ResourceManager.INSTANCE.addSound(m_context, i);

        Manager.INSTANCE.init(m_context, MAP_WIDTH, MAP_HEIGHT, m_width, m_height, m_ad);
    }
    public void onSurfaceChanged(GL10 gl, int w, int h)
    {
        gl.glViewport(0, 0, w, h);
    }
    public void onDrawFrame(GL10 gl)
    {
        // time
        float currentTime = SystemClock.elapsedRealtime();
        float deltaTime = currentTime - m_lastFrameTime;
        if(m_lastFrameTime == 0)
            deltaTime = 0;
        m_lastFrameTime = currentTime;

        gl.glClearColor(1f, 1f, 1f, 1f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL10.GL_BLEND);
        gl.glColor4f(1f, 1f, 1f, 1f);

        Manager.INSTANCE.update(deltaTime);
        Manager.INSTANCE.draw(gl, deltaTime);
    }
    public void touchEvent(MotionEvent event) {
        Manager.INSTANCE.touchEvent(event);
    }
    /*public void touchEvent(float x, float y, int eventCode)
    {
        float _worldX = ((x - (this.m_width / 2)) * MAP_WIDTH) / (this.m_width / 2);
        float _worldY = ((y - (this.m_height / 2)) * -MAP_HEIGHT) / (this.m_height / 2);

        Manager.INSTANCE.touchEvent(_worldX, _worldY, eventCode);
    }*/
    public void setSize(int x, int y)
    {
        this.m_width = x;
        this.m_height = y;
        Manager.INSTANCE.setSurfSize(x, y);
    }
}
