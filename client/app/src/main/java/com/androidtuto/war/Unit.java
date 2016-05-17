package com.androidtuto.war;

import java.util.ArrayList;

/**
 * Created by 초록 on 2015-04-29.
 */
public class Unit implements Comparable
{
    private int m_index;
    private int m_team;

    private int m_userData;

    private Vec2 m_position;
    private Vec2 m_size;
    private Vec2 m_collisionBox;

    private float m_angle;
    private float m_alpha;
    private int m_z;
    private Vec2 m_direction;
    private float m_velocity;
    private float m_maxVelocity;
    private float m_accel;
    private float m_speed;
    private ArrayList<Picture> m_pictures;
    private boolean m_visible;
    private Unit m_parent;

    public static final int TRACE_LINE = 0;
    public static final int TRACE_CURVE = 1;

    private boolean m_bTrace;
    private int m_traceMode;
    private Vec2 m_dest;
    private float m_torque;

    private boolean m_bSeeForward;

    static final int FX_MOVE_NONE = 0;
    static final int FX_MOVE_PLAYER = 1;
    static final int FX_MOVE_SHADOW = 2;
    static final int FX_MOVE_MISSILE = 3;

    int m_fxMove;
    float m_accumTimeFxMove;
    float m_fxMoveRate;

    private boolean m_toggle;

    private CallbackMenu m_callbackMenu;
    private CallbackAnimation m_callbackAniTrace;

    private boolean m_aniPictureOn;
    private float m_aniPictureInterval;
    private float m_aniPictureAccum;
    private int m_aniPictureIndex;
    private boolean m_aniPictureReverse;
    private boolean m_aniPictureRepeat;
    private CallbackAnimation m_callbackAniPicture;

    private boolean m_aniAlphaOn;
    private float m_aniAlphaInterval;
    private float m_aniAlphaEnd;
    private CallbackAnimation m_callbackAniAlpha;

    private boolean m_aniPosOn;
    private Vec2 m_aniPosEnd;
    private CallbackAnimation m_callbackAniPos;

    private boolean m_aniSizeOn;
    private Vec2 m_aniSizeInterval;
    private Vec2 m_aniSizeEnd;
    private CallbackAnimation m_callbackAniSize;

    private boolean m_aniAngleOn;
    private float m_aniAngleInterval;
    private float m_aniAngleEnd;
    private boolean m_aniAngleRepeat;
    private CallbackAnimation m_callbackAniRotate;

    private boolean m_aniTwingOn;
    private Vec2 m_aniTwingDir;
    private Vec2 m_aniTwingSize;
    private Vec2 m_aniTwingPower;
    private Vec2 m_aniTwingDamping;
    private Vec2 m_aniTwingEnd;

    public Unit()
    {
        m_index = 0;
        m_userData = 0;
        m_position = new Vec2();
        m_size = new Vec2();
        m_collisionBox = new Vec2();
        m_angle = 0f;
        m_alpha = 1f;
        m_z = 0;
        m_direction = new Vec2(0f, 1f);
        m_velocity = 0f;
        m_maxVelocity = 0f;
        m_accel = 0f;
        m_speed = 0f;
        m_pictures = new ArrayList<Picture>();
        m_visible = true;
        m_parent = null;

        m_bTrace = false;
        m_dest = new Vec2();
        m_torque = 0f;

        m_fxMove = 0;
        m_accumTimeFxMove = 0f;
        m_fxMoveRate = 0f;

        m_toggle = false;

        m_callbackMenu = null;
        m_callbackAniTrace = null;

        m_aniPictureOn = false;
        m_aniPictureInterval = 0f;
        m_aniPictureAccum = 0f;
        m_aniPictureIndex = 0;
        m_aniPictureReverse = false;
        m_aniPictureRepeat = false;
        m_callbackAniPicture = null;

        m_aniAlphaOn = false;
        m_aniAlphaInterval = 0.0f;
        m_aniAlphaEnd = 0.0f;
        m_callbackAniAlpha = null;

        m_aniPosOn = false;
        m_aniPosEnd = new Vec2();
        m_callbackAniPos = null;

        m_aniSizeOn = false;
        m_aniSizeInterval = new Vec2();
        m_aniSizeEnd = new Vec2();
        m_callbackAniSize = null;

        m_aniAngleOn = false;
        m_aniAngleInterval = 0.0f;
        m_aniAngleEnd = 0.0f;
        m_aniAngleRepeat = false;
        m_callbackAniRotate = null;

        m_aniTwingOn = false;
        m_aniTwingDir = new Vec2();
        m_aniTwingSize = new Vec2();
        m_aniTwingPower = new Vec2();
        m_aniTwingDamping = new Vec2();
        m_aniTwingEnd = new Vec2();
    }

    public Unit(float x, float y, float xr, float yr, float angle, int picture)
    {
        this();

        setPosition(x, y); // position
        setSize(xr, yr); // size
        setAngle(angle); // angle

        if(picture >= 0)
            setPicture(picture);
    }

    public Unit(float x, float y, float xr, float yr, float angle, int picture, int numPictures)
    {
        this();

        setPosition(x, y); // position
        setSize(xr, yr); // size
        setAngle(angle); // angle

        for(int i=0; i<numPictures; ++i)
            addPicture(picture + i);
    }

    public int compareTo(Object o){
        Unit rhs = (Unit)o;
        if(getZ() == rhs.getZ())
            return getIndex() - rhs.getIndex();
        else
            return getZ() - rhs.getZ();
    }

    public void setIndex(int index){m_index = index;}
    public int getIndex(){return m_index;}

    public void setUserData(int data){m_userData = data;}
    public int getUserData(){return m_userData;}

    public void setTeam(int team){m_team = team;}
    public int getTeam(){return m_team;}
    public boolean isEnemy(Unit u){return m_team != u.getTeam();}

    public Vec2 getPosition(){
        if(m_parent != null) {
            Vec2 result = new Vec2();
            Vec2 parentPos = m_parent.getWorldPosition();
            result.x = parentPos.x + m_position.x;
            result.y = parentPos.y + m_position.y;
            return result;
        }
        else
            return new Vec2(m_position);
    }

    public Vec2 getWorldPosition(){
        if(m_parent != null) {
            Vec2 result = new Vec2();
            Vec2 parentPos = m_parent.getWorldPosition();
            result.x = parentPos.x + m_position.x;
            result.y = parentPos.y + m_position.y;
            return result;
        }
        else
            return new Vec2(m_position);
    }
    public void setPosition(float x, float y)
    {
        m_position.x = x; m_position.y = y;
    }
    public void setPosition(Vec2 v){m_position = v;}

    public Vec2 getSize(){return new Vec2(m_size);}
    public void setSize(float x, float y){
        m_size.x = x; m_size.y = y;
        setCollisionBox(x, y);
    }
    public void setSize(Vec2 v){setSize(v.x, v.y);}

    public void setLine(float x1, float y1, float x2, float y2, float size)
    {
        setPosition((x1+x2)/2f, (y1+y2)/2f);

        float dx = x2-x1;
        float dy = y2-y1;
        Vec2 v = new Vec2(dx, dy);
        v.normalize();

        float size2 = Vec2.sqrt(dx*dx+dy*dy) / 2f;
        setSize(size, size > size2 ? size : size2);
        setAngle(v.getAngle());
    }

    public Vec2 getCollisionBox(){return new Vec2(m_collisionBox);}
    public void setCollisionBox(float x, float y){m_collisionBox.x = x; m_collisionBox.y = y;}
    public void setCollisionBox(Vec2 v){m_collisionBox = v;}

    public float getAngle(){return m_angle;}
    public void setAngle(float angle){m_angle = angle;}

    public float getAlpha(){return m_alpha;}
    public void setAlpha(float alpha){m_alpha = alpha;}

    public void clearPicture(){
        m_pictures.clear();
    }

    public void addPicture(int pic)
    {
        Picture picture = ResourceManager.INSTANCE.getPicture(pic);
        if(picture != null)
            m_pictures.add(picture);
    }

    public void setPicture(int pic){
        m_pictures.clear();
        addPicture(pic);
    }

    public boolean isVisible(){
        if(m_parent != null)
            return m_parent.isVisible() && m_visible;
        else
            return m_visible;
    }
    public void setVisible(boolean visible){m_visible = visible;}
    public void toggleVisible(){m_visible = !m_visible;}

    public Unit getParent(){return m_parent;}
    public void setParent(Unit parent){m_parent = parent;}

    public CallbackMenu getCallbackMenu(){return m_callbackMenu;}
    public void setCallbackMenu(CallbackMenu callback){ m_callbackMenu = callback; }
    public CallbackAnimation getCallbackTrace(){return m_callbackAniTrace;}
    public void setCallbackTrace(CallbackAnimation callback){m_callbackAniTrace = callback;}
    public CallbackAnimation getCallbackAniPicture(){return m_callbackAniPicture;}
    public void setCallbackAniPicture(CallbackAnimation callback){m_callbackAniPicture = callback;}
    public CallbackAnimation getCallbackAniAlpha(){return m_callbackAniAlpha;}
    public void setCallbackAniAlpha(CallbackAnimation callback){ m_callbackAniAlpha = callback; }
    public CallbackAnimation getCallbackAniPos(){return m_callbackAniPos;}
    public void setCallbackAniPos(CallbackAnimation callback){ m_callbackAniPos = callback;}
    public CallbackAnimation getCallbackAniSize(){return m_callbackAniSize;}
    public void setCallbackAniSize(CallbackAnimation callback){ m_callbackAniSize = callback; }
    public CallbackAnimation getCallbackAniRotate(){return m_callbackAniRotate;}
    public void setCallbackAniRotate(CallbackAnimation callback){ m_callbackAniRotate = callback; }

    public void doMenuButton(){
        if(m_callbackMenu == null)
            return;

        Manager.INSTANCE.setCurrentEvent(this);
        m_callbackMenu.menuButton(this);
    }

    public int getZ(){
        if(m_parent != null)
            return m_parent.getZ() + m_z;
        else
            return m_z;
    }
    public void setZ(int z){m_z = z;}

    public Vec2 getDirection(){return new Vec2(m_direction);}
    public void setDirection(Vec2 v){setDirection(v, true);}
    public void setDirection(Vec2 v, boolean normalize){
        m_direction = v;
        if(normalize)
            m_direction.normalize();
        if(isSeeForward())
            setAngle(m_direction.getAngle());
    }
    public void setDirection(float x, float y){
        setDirection(new Vec2(x, y));
    }

    public float getVelocity(){return m_velocity;}
    public void setVelocity(float v){m_velocity = v;}
    public void addVelocity(float v){
        m_velocity = v;
        if(m_velocity > m_maxVelocity)
            m_velocity = m_maxVelocity;
    }

    public float getMaxVelocity(){return m_maxVelocity;}
    public void setMaxVelocity(float v){m_maxVelocity = v;}

    public float getAccel(){return m_accel;}
    public void setAccel(float accel){m_accel = accel;}

    public float getSpeed(){return m_speed;}
    public void setSpeed(float speed){m_speed = speed;}

    public void go(){m_accel = m_speed;}
    public void stop(){m_accel = -m_speed;}

    public void setDest(float x, float y){
        m_dest.x = x;
        m_dest.y = y;
    }

    public void setTorque(float torque){
        m_torque = torque;
    }

    public void setTraceMode(int mode){
        m_traceMode = mode;
    }

    public void traceStart(final Vec2 v, float torque, int mode) {traceStart(v.x, v.y, torque, mode);}

    public void traceStart(float x, float y, float torque, int mode){
        m_bTrace = true;
        setDest(x, y);
        setTorque(torque);
        setTraceMode(mode);

        if(mode == TRACE_CURVE)
            go();
    }

    public void traceStop(){
        m_bTrace = false;
        if(m_callbackAniTrace != null)
            m_callbackAniTrace.animationEnd(this);
    }

    public boolean checkCollide(float x, float y)
    {
        Vec2 p = getPosition();
        return Math.abs(x - p.x) <= m_collisionBox.x && Math.abs(y - p.y) <= m_collisionBox.y;
    }

    public boolean checkCollide(Vec2 v){return checkCollide(v.x, v.y);}

    public boolean checkCollide(Vec2 pos, Vec2 size){
        return checkCollide(pos.x, pos.y, size.x, size.y);
    }

    public boolean checkCollide(float x, float y, float hw, float hh){
        Vec2 p = getPosition();
        return x - hw <= p.x && p.x <= x + hw && y - hh <= p.y && p.y <= y + hh;
    }

    int GetRectZone( float circleX, float circleY )
    {
        Vec2 p = getPosition();
        int xZone = ( circleX <  p.x - m_collisionBox.x ) ? 0 :
                ( circleX >  p.x + m_collisionBox.x ) ? 2 : 1;
        int yZone = ( circleY <  p.y - m_collisionBox.y) ? 0 :
                ( circleY >  p.y + m_collisionBox.y) ? 2 : 1;
        int nZone = xZone + 3*yZone;
        return nZone;
    }

    /*boolean checkCollide(Vec2 v, float radius)
    {
        if(checkCollide(v.x, v.y, radius, radius) == false)
            return false;

        boolean collisionDetected = false;
        int  nZone = GetRectZone( v.x, v.y );

        switch (nZone )
        {
            // top, bottom 변의 영역에서, 원의 센터와 수직거리를 검사한다.
            case 1:
            case 7:
            {
                float distY = Math.abs( v.y - m_position.y);
                if( distY <= ( radius + m_collisionBox.y ) )
                    collisionDetected = true;
            }
            break;
            // left, right 변의 영역에서. 원의 센터와 수평거리를 검사한다.
            case 3:
            case 5:
            {
                float distX = Math.abs( v.x - m_position.x);
                if( distX <= ( radius + m_collisionBox.x ) )
                    collisionDetected = true;
            }
            break;
            // 사각형 영역의 내부
            case 4:
                collisionDetected = true;
                break;
            // 모서리 영역, 모서리가 원의 내부에 포함되는지 검사한다.
            default:
            {
                float cornerX = ( nZone == 0 || nZone == 6 ) ? m_position.x - m_collisionBox.x : m_position.x + m_collisionBox.x;
                float cornerY = ( nZone == 0 || nZone == 2 ) ? m_position.y - m_collisionBox.y : m_position.y + m_collisionBox.y;
                float diffX = Math.abs(cornerX - v.x);
                float diffY = Math.abs(cornerY - v.y);
                if(Vec2.sqrt((diffX*diffX) + (diffY*diffY)) <= radius)
                    collisionDetected = true;
            }
            break;
        }

        return collisionDetected;
    }*/

    boolean checkCollide(Unit u){
        return checkCollide(u.getPosition(), u.getCollisionBox());
    }

    public Picture getPicture(int index){return m_pictures.get(index);}

    public void setFxMove(int fx)
    {
        m_fxMove = fx;
        m_accumTimeFxMove = 0f;

        switch(fx){
            default:
            case FX_MOVE_NONE: break;
            case FX_MOVE_PLAYER: m_fxMoveRate = 100f; break;
            case FX_MOVE_SHADOW: m_fxMoveRate = 800f; break;
            case FX_MOVE_MISSILE: m_fxMoveRate = 50f; break;
        }
    }

    public boolean getToggle(){return m_toggle;}
    public void setToggle(boolean toggle){m_toggle = toggle;}

    public boolean isSeeForward(){return m_bSeeForward;}
    public void setSeeForward(boolean b){
        m_bSeeForward = b;
        setAngle(m_direction.getAngle());
    }

    private void animatePicture(float interval, boolean repeat, boolean reverse){
        m_aniPictureInterval = interval;
        m_aniPictureReverse = reverse;
        if(m_aniPictureReverse)
            m_aniPictureIndex = m_pictures.size()-1;
        m_aniPictureRepeat = repeat;
        m_aniPictureOn = true;
    }

    public void animatePicture(float interval, boolean repeat){
        animatePicture(interval, repeat, false);
    }

    public boolean isAniAlphaOn(){return m_aniAlphaOn;}

    public void animateAlpha(float end, float interval){
        animateAlpha(m_alpha, end, interval);
    }

    public void animateAlpha(float start, float end, float interval){
        m_alpha = start;
        m_aniAlphaEnd = end;
        m_aniAlphaInterval = interval;
        m_aniAlphaOn = true;
    }

    public void animatePos(Vec2 start, Vec2 end, Vec2 dir, float velocity){
        setPosition(start);
        m_aniPosOn = true;
        m_aniPosEnd = end;
        setDirection(dir, false);
        setVelocity(velocity);
    }

    public void animatePos(Vec2 end, Vec2 dir, float velocity){
        m_aniPosOn = true;
        m_aniPosEnd = end;
        setDirection(dir, false);
        setVelocity(velocity);
    }

    public void animateSize(float end, float interval){
        animateSize(m_size.x, end, interval);
    }

    public void animateSize(float start, float end, float interval){
        m_size.x = start;
        m_size.y = start;
        m_aniSizeEnd.x = end;
        m_aniSizeEnd.y = end;
        m_aniSizeInterval.x = interval;
        m_aniSizeInterval.y = interval;
        m_aniSizeOn = true;
    }

    public void animateSize(Vec2 end, Vec2 interval){
        animateSize(m_size, end, interval);
    }

    public void animateSize(Vec2 start, Vec2 end, Vec2 interval){
        m_size = start;
        m_aniSizeEnd = end;
        m_aniSizeInterval = interval;
        m_aniSizeOn = true;
    }

    public void animateAngle(float end, float interval){
        animateAngle(m_angle, end, interval, false);
    }

    public void animateAngle(float start, float end, float interval, boolean repeat){
        m_angle = start;
        m_aniAngleEnd = end;
        m_aniAngleInterval = interval;
        m_aniAngleRepeat = repeat;
        m_aniAngleOn = true;
    }

    public void animateTwing(float end, float size, float power, float damping){
        animateTwing(m_size.x, end, size, power, damping);
    }

    public void animateTwing(float start, float end, float size, float power, float damping){
        m_size.x = start;
        m_size.y = start;
        m_aniTwingEnd.x = end;
        m_aniTwingEnd.y = end;
        m_aniTwingDir.x = m_aniTwingEnd.x > m_size.x ? 1f : -1f;
        m_aniTwingDir.y = m_aniTwingEnd.y > m_size.y ? 1f : -1f;
        m_aniTwingSize.x = size;
        m_aniTwingSize.y = size;
        m_aniTwingPower.x = power;
        m_aniTwingPower.y = power;
        m_aniTwingDamping.x = damping;
        m_aniTwingDamping.y = damping;
        m_aniTwingOn = true;
    }

    public void animateTwing(Vec2 end, Vec2 size, Vec2 power, Vec2 damping){
        animateTwing(m_size, end, size, power, damping);
    }

    public void animateTwing(Vec2 start, Vec2 end, Vec2 size, Vec2 power, Vec2 damping){
        m_size = start;
        m_aniTwingEnd = end;
        m_aniTwingDir.x = m_aniTwingEnd.x - m_size.x;
        m_aniTwingDir.y = m_aniTwingEnd.y - m_size.y;
        m_aniTwingSize = size;
        m_aniTwingPower = power;
        m_aniTwingDamping = damping;
        m_aniTwingOn = true;
    }

    void stopAniAlpha(){
        m_aniAlphaOn = false;
    }

    public void update(float delta)
    {
        if(delta > 2000f)
            return;

        // auto tracing for npc, guided missile
        if(m_bTrace)
        {
            if(Vec2.checkSquare(m_position, m_dest, m_size.x)) {
                if(m_callbackAniTrace != null)
                    m_callbackAniTrace.animationEnd(this);
                traceStop();
            }
            else if(m_traceMode == TRACE_LINE){
                Vec2 d = new Vec2(m_dest.x - m_position.x, m_dest.y - m_position.y);
                d.normalize();

                m_position.x += d.x * m_torque*delta;
                m_position.y += d.y * m_torque*delta;
            }
            else {
                int ccw1 = Vec2.ccw(m_position, Vec2.add(m_position, m_direction), m_dest);
                m_direction.rotate(ccw1 * m_torque * delta);
                int ccw2 = Vec2.ccw(m_position, Vec2.add(m_position, m_direction), m_dest);
                if(ccw1 != ccw2)
                    setDirection(new Vec2(m_dest.x - m_position.x, m_dest.y - m_position.y));
            }
        }

        if(isSeeForward()){
            setAngle(m_direction.getAngle());
        }

        if(m_accel != 0f)
        {
            m_velocity += m_accel * delta;
            if(m_accel > 0f && m_velocity >= m_maxVelocity){
                m_velocity = m_maxVelocity;
                m_accel = 0f;
            }
            if(m_accel < 0f && m_velocity <= 0f){
                m_velocity = 0f;
                m_accel = 0f;
            }
        }

        if(m_velocity > 0f)
        {
            m_position.x += m_direction.x * m_velocity;
            m_position.y += m_direction.y * m_velocity;
        }

        if(m_aniPictureOn)
        {
            m_aniPictureAccum += m_aniPictureInterval * delta;
            if(m_aniPictureAccum >= 1f){
                m_aniPictureAccum -= 1f;
                if(m_aniPictureReverse)
                    --m_aniPictureIndex;
                else
                    ++m_aniPictureIndex;
                if((m_aniPictureReverse == false && m_aniPictureIndex >= m_pictures.size()) || (m_aniPictureReverse == true && m_aniPictureIndex <= -1)){
                    m_aniPictureIndex = 0;
                    if(m_aniPictureRepeat == false)
                        m_aniPictureOn = false;
                    if(m_callbackAniPicture != null)
                        m_callbackAniPicture.animationEnd(this);
                }
            }
        }

        if(m_aniAlphaOn)
        {
            boolean end = false;
            if(m_aniAlphaInterval > 0.0f)
                end = m_alpha > m_aniAlphaEnd;
            else
                end = m_alpha < m_aniAlphaEnd;

            if(end)
            {
                m_aniAlphaOn = false;
                m_alpha = m_aniAlphaEnd;

                if(m_callbackAniAlpha != null)
                    m_callbackAniAlpha.animationEnd(this);
            }
            else {
                m_alpha += m_aniAlphaInterval * delta;
            }
        }

        if(m_aniPosOn){
            if(Vec2.checkSquare(m_position, m_aniPosEnd, m_velocity * 1.1f)){
                m_position = m_aniPosEnd;
                setVelocity(0f);
                m_aniPosOn = false;
                if(m_callbackAniPos != null)
                    m_callbackAniPos.animationEnd(this);
            }
        }

        if(m_aniSizeOn)
        {
            boolean endX = false;
            if(m_aniSizeInterval.x > 0.0f)
                endX = m_size.x > m_aniSizeEnd.x;
            else
                endX = m_size.x < m_aniSizeEnd.x;

            if(endX)
            {
                m_size.x = m_aniSizeEnd.x;
                m_aniSizeInterval.x = 0f;
            }
            else {
                m_size.x += m_aniSizeInterval.x * delta;
            }

            boolean endY = false;
            if(m_aniSizeInterval.y > 0.0f)
                endY = m_size.y > m_aniSizeEnd.y;
            else
                endY = m_size.y < m_aniSizeEnd.y;

            if(endY)
            {
                m_size.y = m_aniSizeEnd.y;
                m_aniSizeInterval.y = 0f;
            }
            else {
                m_size.y += m_aniSizeInterval.y * delta;
            }

            if(endX && endY) {
                m_aniSizeOn = false;
                if (m_callbackAniSize != null)
                    m_callbackAniSize.animationEnd(this);
            }
        }

        if(m_aniAngleOn)
        {
            boolean end = false;
            if(m_aniAngleRepeat == false) {
                if (m_aniAngleInterval > 0.0f)
                    end = m_angle > m_aniAngleEnd;
                else
                    end = m_angle < m_aniAngleEnd;
            }

            if(m_angle > (float)Math.PI*2)
                m_angle -= (float)Math.PI*2;

            if(end)
            {
                m_aniAngleOn = false;
                m_angle = m_aniAngleEnd;

                if(m_callbackAniRotate != null)
                    m_callbackAniRotate.animationEnd(this);
            }
            else {
                m_angle += m_aniAngleInterval * delta;
            }
        }

        if(m_aniTwingOn)
        {
            m_size.x += m_aniTwingDir.x * m_aniTwingPower.x * delta;
            if((m_aniTwingDir.x > 0f && m_size.x > m_aniTwingEnd.x + m_aniTwingSize.x) ||
                    (m_aniTwingDir.x < 0f && m_size.x < m_aniTwingEnd.x - m_aniTwingSize.x))
            {
                m_aniTwingDir.x *= -1f;
                m_aniTwingSize.x -= m_aniTwingDamping.x;
            }

            m_size.y += m_aniTwingDir.y * m_aniTwingPower.y * delta;
            if((m_aniTwingDir.y > 0f && m_size.y > m_aniTwingEnd.y + m_aniTwingSize.y) ||
                    (m_aniTwingDir.y < 0f && m_size.y < m_aniTwingEnd.y - m_aniTwingSize.y))
            {
                m_aniTwingDir.y *= -1f;
                m_aniTwingSize.y -= m_aniTwingDamping.y;
            }

            if(Math.abs(m_aniTwingDir.x) < 0.01f && Math.abs(m_size.x - m_aniTwingEnd.x) < 0.01f && Math.abs(m_aniTwingDir.y) < 0.01f && Math.abs(m_size.y - m_aniTwingEnd.y) < 0.01f){
                m_aniTwingOn = false;
                m_size.x = m_aniTwingEnd.x;
                m_size.y = m_aniTwingEnd.y;
            }
        }

        if(m_fxMove != 0) {
            if (getVelocity() > 0f) {
                m_accumTimeFxMove += delta;
                if (m_accumTimeFxMove >= m_fxMoveRate) {
                    switch(m_fxMove){
                        case FX_MOVE_PLAYER: Game.INSTANCE.addFx(getPosition().x, getPosition().y, 0.5f, 0.5f, 1f, 0f, -0.002f, R.drawable.fx_fog_player); break;
                        case FX_MOVE_SHADOW:
                            Unit fx = Game.INSTANCE.addFx(getPosition().x, getPosition().y, 0.7f, 0.7f, 0.7f, 0f, -0.00025f, m_pictures.get(0).getTexture());
                            fx.setAngle(m_angle);
                            break;
                        case FX_MOVE_MISSILE: Game.INSTANCE.addFx(getPosition().x, getPosition().y, 0.25f, 0.25f, 1f, 0f, -0.004f, R.drawable.fx_fog_player); break;
                    }
                    m_accumTimeFxMove = 0f;
                }
            } else
                m_accumTimeFxMove = 0f;
        }
    }

    private void _draw(float [] m, float delta, float x, float y, float sx, float sy)
    {
        m_pictures.get(m_aniPictureIndex).draw(m, x, y, 0f, m_angle * (180f / (float) Math.PI), sx, sy, m_alpha);
    }

    public void draw(float [] m, float delta, float x, float y, float sx, float sy)
    {
        if(isVisible() == false || m_pictures == null || m_pictures.size() == 0)
            return;

        _draw(m, delta, x, y, sx, sy);
    }

    public void draw(float [] m, float delta)
    {
        if(isVisible() == false || m_pictures == null || m_pictures.size() == 0)
            return;

        Vec2 p = getWorldPosition();
        _draw(m, delta, p.x, p.y, m_size.x, m_size.y);
    }

    public boolean draw(float [] m, float delta, Vec3 camera)
    {
        if(isVisible() == false || m_pictures == null || m_pictures.size() == 0)
            return false;

        float rate = 10f / camera.z;
        Vec2 p = getWorldPosition();
        float x = (p.x - camera.x) * rate;
        float y = (p.y - camera.y) * rate;
        float sx = m_size.x * rate;
        float sy = m_size.y * rate;

        if(-Manager.MAP_WIDTH <= x + sx && x - sx <= Manager.MAP_WIDTH && -Manager.MAP_HEIGHT <= y + sy && y - sy <= Manager.MAP_WIDTH) {
            _draw(m, delta, (p.x - camera.x) * rate, (p.y - camera.y) * rate, m_size.x * rate, m_size.y * rate);
            return true;
        } else
            return false;
    }
}
