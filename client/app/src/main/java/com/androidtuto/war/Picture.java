package com.androidtuto.war;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Picture
{
    private FloatBuffer _vertexBuffer;
    private ShortBuffer _indexBuffer;
    private FloatBuffer _texBuffer;
    private int _vertexCount = 0;
    private boolean _hasTexture = false;
    private int[] _texture = new int[1];
    private int _textIndex;

    public Picture(GL10 gl, Context context, int tex)
    {
        float[] coords = new float[]{-1,-1,0,1,-1,0,1,1,0,-1,1,0};
        float[] tcoords = new float[]{ 0f,1f, 1f,1f, 1f,0f,0f,0f};
        short[] icoords = new short[]{0,1,2,3,0};
        int vertexes = 5;

        _vertexCount = vertexes;
        _vertexBuffer = makeFloatBuffer(coords);
        _indexBuffer = makeShortBuffer(icoords);
        _texBuffer = makeFloatBuffer(tcoords);

        loadTexture(gl, context, tex);
    }

    public Picture(GL10 gl, Context context, int tex, int x, int y, int width, int height)
    {
        float[] coords = new float[]{-1,-1,0,1,-1,0,1,1,0,-1,1,0};
        float[] tcoords = new float[]{ 0f,1f, 1f,1f, 1f,0f,0f,0f};
        short[] icoords = new short[]{0,1,2,3,0};
        int vertexes = 5;

        _vertexCount = vertexes;
        _vertexBuffer = makeFloatBuffer(coords);
        _indexBuffer = makeShortBuffer(icoords);
        _texBuffer = makeFloatBuffer(tcoords);

        loadTexture(gl, context, tex, x, y, width, height);
    }

    protected static FloatBuffer makeFloatBuffer(float[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    protected static ShortBuffer makeShortBuffer(short[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer ib = bb.asShortBuffer();
        ib.put(arr);
        ib.position(0);
        return ib;
    }

    public int getTexture(){return _textIndex;}

    public void loadTexture(GL10 gl, Context mContext, int mTex) {
        _textIndex = mTex;
        _hasTexture = true;
        gl.glGenTextures(1, _texture, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture[0]);
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), mTex);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR );
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR );
    }

    public void loadTexture(GL10 gl, Context mContext, int mTex, int x, int y, int width, int height) {
        _textIndex = mTex;
        _hasTexture = true;
        gl.glGenTextures(1, _texture, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture[0]);
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), mTex);
        bitmap = Bitmap.createBitmap(bitmap, x*2, y*2, width*2, height*2);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR );
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR );
    }

   public void draw(GL10 gl) {
            if (_hasTexture) {
            gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture[0]);
                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _texBuffer);
            } else {
                gl.glDisable(GL10.GL_TEXTURE_2D);
            }
            gl.glFrontFace(GL10.GL_CCW);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
            gl.glDrawElements(GL10.GL_TRIANGLE_FAN, _vertexCount, GL10.GL_UNSIGNED_SHORT, _indexBuffer);
            gl.glDisable(GL10.GL_TEXTURE_2D);
    }

    public void draw(GL10 gl, float x, float y, float z, float rot, float scale) {
            this.draw(gl, x, y, z, rot, scale, scale);
    }

    public void draw(GL10 gl, float x, float y, float z, float rot, float scaleX, float scaleY) {
            gl.glPushMatrix();
            gl.glTranslatef(x, y, z);
            gl.glRotatef(rot, 0f, 0f, 1f);
            gl.glScalef(scaleX, scaleY, 1f);
            this.draw(gl);
            gl.glPopMatrix();
    }

}
