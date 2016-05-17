package com.androidtuto.war;
 

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MainGLSurfaceView extends GLSurfaceView {
    // 랜더러
    private final MainGLRenderer mGLRenderer;

    //생성자
    public MainGLSurfaceView(MyActivity activity, int width, int height) {
        super(activity.getApplicationContext());
        // OpenGL ES 2.0 context를 생성한다.
        setEGLContextClientVersion(2);
        // GLSerfaceView를 사용하기 위해 Context를 이용해 랜더러를 생성한다.
        mGLRenderer = new MainGLRenderer(activity, width, height);
        setRenderer(mGLRenderer);
        // 랜더모드를 변경될 경우 그린다.
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void onPause() {
        super.onPause();
        mGLRenderer.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGLRenderer.onResume();
    }

    public boolean onTouchEvent(final MotionEvent event) {
        //for(int i=0; i<event.getPointerCount(); ++i)
        //    _refRenderer.touchEvent(event.getX(i), event.getY(i), event.getAction());

        queueEvent(new Runnable(){
            public void run() {
                // event.findPointerIndex(event.getPointerId(i)
                mGLRenderer.touchEvent(event);
                /*if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    for (int i = 0; i < event.getPointerCount(); ++i)
                        _refRenderer.touchEvent(event.getX(i), event.getY(i), event.getAction());
                }
                else
                    _refRenderer.touchEvent(event.getX(event.getActionIndex()), event.getY(event.getActionIndex()), event.getAction());*/
            }});

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }
}