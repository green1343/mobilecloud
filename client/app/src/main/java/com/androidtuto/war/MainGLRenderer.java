package com.androidtuto.war;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// 랜더링
public class MainGLRenderer implements GLSurfaceView.Renderer {
    private float MAP_WIDTH = 0f;
    private float MAP_HEIGHT = 0f;

    // 매트릭스
    private final float[] mMtrxProjection = new float[16];
    private final float[] mMtrxView = new float[16];
    private final float[] mMtrxProjectionAndView = new float[16];
    // 프로그램색상, 이미지
    private static int mProgramSolidColor;
    private static int mProgramImage;
    float m_lastFrameTime;
    // 디바이스의 넓이, 높이
    public static int mDeviceWidth = 0;
    public static int mDeviceHeight = 0;
    // 주 액티비티
    MyActivity mActivity;
    Context mContext;

    Advertise m_ad;

    // 생성자
    public MainGLRenderer(MyActivity activity, int width, int height) {
        //MAP_WIDTH = 11f;
        //MAP_HEIGHT = 6.53f;
        MAP_WIDTH = 11f;
        MAP_HEIGHT = MAP_WIDTH * (height / (float)width);

        m_lastFrameTime = 0f;
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mDeviceWidth = width;
        mDeviceHeight = height;

        m_ad = new Advertise(activity);
    }
    // 멈춤
    public void onPause() {
    }

    // 재시작

    public void onResume() {
        m_lastFrameTime = SystemClock.elapsedRealtime();
    }

    // 서피스뷰 변경
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Manager.INSTANCE.setSurfSize(width, height);
        GLES20.glViewport(0, 0, (int) mDeviceWidth, (int) mDeviceHeight);
        Matrix.setIdentityM(mMtrxProjection, 0);
        Matrix.setIdentityM(mMtrxView, 0);
        Matrix.setIdentityM(mMtrxProjectionAndView, 0);
        Matrix.orthoM(mMtrxProjection, 0, 0f, mDeviceWidth, 0.0f, mDeviceHeight, 0, 50);
        Matrix.setLookAtM(mMtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    // 서피스뷰 생성
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.5f, 0.0f, 1);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vs_Image);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fs_Image);
        mProgramImage = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgramImage, vertexShader);
        GLES20.glAttachShader(mProgramImage, fragmentShader);
        GLES20.glLinkProgram(mProgramImage);
        GLES20.glUseProgram(mProgramImage);

        for(int i = 0x7f020000; i <= R.drawable.zzz; ++i)
            ResourceManager.INSTANCE.addPicture(mContext, i, mProgramImage, mProgramSolidColor);

        //for(int i = 0x7f040000; i <= 0x7f040001; ++i)
        //    ResourceManager.INSTANCE.addSound(m_context, i);

        Manager.INSTANCE.init(mContext, MAP_WIDTH, MAP_HEIGHT, m_ad);
    }

    // 쉐이더 이미지
    public static final String vs_Image =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 a_texCoord;" +
                    "attribute float a_alpha;" +
                    "varying vec2 v_texCoord;" +
                    "varying float v_alpha;" +
                    "void main() {" +
                    "  v_alpha = a_alpha;" +
                    "  v_texCoord = a_texCoord;" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    public static final String fs_Image =
            "precision lowp float;" +
                    "varying vec2 v_texCoord;" +
                    "varying float v_alpha;" +
                    "uniform sampler2D s_texture;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D( s_texture, v_texCoord );" +
                    "  gl_FragColor.a *= v_alpha;" +
                    "}";
    // 쉐이더 로딩
    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    // 그리기 호출
    @Override
    public void onDrawFrame(GL10 unused) {
        // time
        float currentTime = SystemClock.elapsedRealtime();
        float deltaTime = currentTime - m_lastFrameTime;
        if(m_lastFrameTime == 0)
            deltaTime = 0;

        // 그리기를 시작한다.

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1);
        Matrix.orthoM(mMtrxProjection, 0, -MAP_WIDTH, MAP_WIDTH, -MAP_HEIGHT, MAP_HEIGHT, 0, 5);
        Matrix.multiplyMM(mMtrxProjectionAndView, 0, mMtrxProjection, 0, mMtrxView, 0);

        // 투명한 배경을 처리한다.
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        Manager.INSTANCE.update(deltaTime);
        Manager.INSTANCE.draw(mMtrxProjectionAndView, deltaTime);

        m_lastFrameTime = currentTime;
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
}
