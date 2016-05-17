package com.androidtuto.war;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

// 유닛
public class Picture {
    // 기본적인 이미지 처리를 위한 변수
    protected static float mVertices[];
    protected static short mIndices[];
    protected static float mUvs[];
    protected static int mProgramImage;
    protected static int mProgramSolidColor;
    protected int mPositionHandle;
    protected int mTexCoordLoc;
    protected int mtrxhandle;
    protected int mSamplerLoc;
    protected int mAlphaLoc;
    protected FloatBuffer mVertexBuffer;
    protected ShortBuffer mDrawListBuffer;
    protected FloatBuffer mUvBuffer;
    protected FloatBuffer mColorBuffer;
    // 매트릭스변환을 위한 변수
    protected final float[] mMVPMatrix = new float[16];
    protected final float[] mMVPMatrix2 = new float[16];
    protected float[] mRotationMatrix = new float[16];
    protected float[] mScaleMatrix = new float[16];
    protected float[] mTranslationMatrix = new float[16];

    // 비트맵 이미지 핸들관리 (여러건 처리를 위해 배열로 정의)
    protected int[] mHandleBitmap;
    protected int mBitmapCount = 0;
    protected Bitmap mBitmap[];
    // 유닛의 움직임을 관리하는 변수
    protected int mCount = 0;
    // 여러개의 이미지 중 화면에 표시할 인덱스번호
    protected int mBitmapState = 0;
    // 현재 객체의 활성화 여부
    protected boolean mIsActive = false;


    int _texIndex;

    public Picture(Context context, int tex, int programImage, int programSolidColor)
    {
        mProgramImage = programImage;
        mProgramSolidColor = programSolidColor;
        mPositionHandle = GLES20.glGetAttribLocation(mProgramImage, "vPosition");
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramImage, "a_texCoord");
        mAlphaLoc = GLES20.glGetAttribLocation(mProgramImage, "a_alpha");
        mtrxhandle = GLES20.glGetUniformLocation(mProgramImage, "uMVPMatrix");
        mSamplerLoc = GLES20.glGetUniformLocation(mProgramImage, "s_texture");

        loadTexture(context, tex);
    }

    public int getTexture(){return _texIndex;}

    // 이미지 처리를 위한 버퍼를 설정함.
    public void setupBuffer(){
        mVertices = new float[] {
                -1, 1, 0,
                -1, -1, 0,
                1, -1, 0,
                1, 1, 0,
        };
        mIndices = new short[] {0, 1, 2, 0, 2, 3};
        // The order of vertexrendering.
        ByteBuffer bb = ByteBuffer.allocateDirect(mVertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(mVertices);
        mVertexBuffer.position(0);
        ByteBuffer dlb = ByteBuffer.allocateDirect(mIndices.length * 2);

        dlb.order(ByteOrder.nativeOrder());
        mDrawListBuffer = dlb.asShortBuffer();
        mDrawListBuffer.put(mIndices);
        mDrawListBuffer.position(0);
        mUvs = new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        ByteBuffer bbUvs = ByteBuffer.allocateDirect(mUvs.length * 4);
        bbUvs.order(ByteOrder.nativeOrder());
        mUvBuffer = bbUvs.asFloatBuffer();
        mUvBuffer.put(mUvs);
        mUvBuffer.position(0);
    }

    public void loadTexture(Context mContext, int mTex) {
        _texIndex = mTex;

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), mTex);
        int[] texturenames = new int[1];
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glGenTextures(1, texturenames, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        setupBuffer();
        mHandleBitmap = new int[1];
        mHandleBitmap[0] = texturenames[0];
        bitmap.recycle();
    }

    public void draw(float [] m, float x, float y, float z, float rot, float scaleX, float scaleY, float alpha) {
        Matrix.setIdentityM(mTranslationMatrix, 0);
        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.setIdentityM(mScaleMatrix, 0);
        Matrix.translateM(mTranslationMatrix, 0, x, y, z);
        Matrix.setRotateM(mRotationMatrix, 0, rot, 0, 0, 1.0f);
        Matrix.scaleM(mScaleMatrix, 0, scaleX, scaleY, 1f);
        Matrix.multiplyMM(mMVPMatrix, 0, m, 0, mTranslationMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix2, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix2, 0, mMVPMatrix2, 0, mScaleMatrix, 0);

        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mHandleBitmap[0]);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, mUvBuffer);
        GLES20.glVertexAttrib1f(mAlphaLoc, alpha);
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, mMVPMatrix2, 0);
        GLES20.glUniform1i(mSamplerLoc, 0);
        // 이미지 핸들을 출력한다.
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.length, GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
        /*
        Matrix.setIdentityM(mTranslationMatrix, 0);
        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.setIdentityM(mScaleMatrix, 0);
        Matrix.translateM(mTranslationMatrix, 0, x, y, z);
        Matrix.setRotateM(mRotationMatrix, 0, rot, 0, 0, -1f);
        Matrix.scaleM(mScaleMatrix, 0, scaleX, scaleY, 1f);

        this.draw(m);*/
    }

}
