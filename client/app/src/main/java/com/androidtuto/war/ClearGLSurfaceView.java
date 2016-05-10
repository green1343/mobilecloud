package com.androidtuto.war;
 

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class ClearGLSurfaceView extends GLSurfaceView
{
    ClearRenderer _refRenderer;
    public ClearGLSurfaceView(Context context) {
        super(context);
        _refRenderer = new ClearRenderer(context);
        setRenderer(_refRenderer);
    }

    public boolean onTouchEvent(final MotionEvent event) {
        _refRenderer.setSize(this.getWidth(),this.getHeight());
        //for(int i=0; i<event.getPointerCount(); ++i)
        //    _refRenderer.touchEvent(event.getX(i), event.getY(i), event.getAction());

        queueEvent(new Runnable(){
            public void run() {
                // event.findPointerIndex(event.getPointerId(i)
                _refRenderer.touchEvent(event);
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
