package com.androidtuto.war;

import java.util.ArrayList;

/**
 * Created by 초록 on 2015-05-22.
 */
public class Number {

    static final int ALIGN_CENTER = 0;
    static final int ALIGN_LEFT = 1;
    static final int ALIGN_RIGHT = 2;

    ArrayList<Unit> m_units;
    float m_num;
    int m_align;
    Vec2 m_position;
    float m_size;

    private CallbackAnimation m_callbackAnimation;

    private boolean m_bAnimating = false;
    private float m_aniNumberInterval = 0;
    private int m_aniNumberEnd = 0;

    Number(int num, float x, float y, float resize, int align) {
        m_units = new ArrayList<Unit>();
        m_position = new Vec2();
        setNumber(num, x, y, resize, align);
        m_callbackAnimation = null;
    }

    Number(Unit parent, int num, float x, float y, float resize, int align){
        m_units = new ArrayList<Unit>();
        m_position = new Vec2();
        setNumber(parent, num, x, y, resize, align);
        m_callbackAnimation = null;
    }

    public void clear(){
        for(Unit u : m_units)
            Manager.INSTANCE.deleteUnit(u);

        m_units.clear();
    }

    public void setZ(int z){
        for(Unit u : m_units)
            u.setZ(z);
    }

    public void setVisible(boolean visible){
        for(Unit u : m_units)
            u.setVisible(visible);
    }

    public void setNumber(float num, float x, float y, float resize, int align) {
        setNumber((int)num, x, y, resize, align);
        m_num = num;
    }

    public void setNumber(float num)
    {
        setNumber(num, m_position.x, m_position.y, m_size, m_align);
    }

    public void setNumber(int num, float x, float y, float resize, int align) {
        setNumber(null, num, x, y, resize, align);
    }

    public void setNumber(Unit p, int num, float x, float y, float resize, int align){
        Manager m = Manager.INSTANCE;

        Unit parent = p;
        if(m_units.size() > 0 && parent == null)
            parent = m_units.get(0).getParent();

        clear();

        m_num = num;
        m_position.x = x;
        m_position.y = y;
        m_size = resize;
        m_align = align;

        int zero = R.drawable.num0;

        if (num >= 10) {
            int numArr[] = new int[20];
            int numCnt = 0;

            while(num > 0) {
                numArr[numCnt++] = num % 10;
                num /= 10;
            }

            float right = 0f;
            if(align == ALIGN_CENTER) {
                right = x + ((numCnt / 2) * resize * 1.0f);
                if (numCnt % 2 == 0)
                    right -= resize * 0.5f;
            }
            else if(align == ALIGN_LEFT)
                right = x + (numCnt * resize*1.0f) - (resize*0.5f);
            else if(align == ALIGN_RIGHT)
                right = x;

            for(int i=0; i<numCnt; ++i)
                m_units.add(m.addBox(parent, right-(i*resize*1.0f), y, resize, resize, 0, Manager.BOX_IMAGE, zero + numArr[i]));

        } else {
            if(align == ALIGN_CENTER)
                m_units.add(m.addBox(parent, x, y, resize, resize, 0, Manager.BOX_IMAGE, zero + num));
            else if(align == ALIGN_LEFT)
                m_units.add(m.addBox(parent, x + resize*0.5f, y, resize, resize, 0, Manager.BOX_IMAGE, zero + num));
            else if(align == ALIGN_RIGHT)
                m_units.add(m.addBox(parent, x - resize*0.5f, y, resize, resize, 0, Manager.BOX_IMAGE, zero + num));
        }

        if(parent != null){
            for(Unit u : m_units)
                u.setParent(parent);
        }
    }

    public void setNumber(int num)
    {
        setNumber(num, m_position.x, m_position.y, m_size, m_align);
    }

    public float getNumber(){return m_num;}

    public void setAlpha(float alpha){
        for(Unit u : m_units){
            u.setAlpha(alpha);
        }
    }

    public void setCallbackAnimation(CallbackAnimation c){
        m_callbackAnimation = c;
    }

    public void animationStart(int end, float interval){
        m_aniNumberEnd = end;
        m_aniNumberInterval = interval;
        m_bAnimating = true;
    }

    public void animationStart(int start, int end, float interval){
        setNumber(start);
        m_aniNumberEnd = end;
        m_aniNumberInterval = interval;
        m_bAnimating = true;
    }

    public void update(float delta)
    {
        if(m_bAnimating)
        {
            m_num += m_aniNumberInterval * delta;

            if((m_aniNumberInterval > 0.0f && m_num > (float)m_aniNumberEnd) || (m_aniNumberInterval < 0.0f && m_num < (float)m_aniNumberEnd))
            {
                m_bAnimating = false;
                setNumber(m_aniNumberEnd);

                boolean result = m_aniNumberInterval < 0.0f;

                if(m_callbackAnimation != null)
                    m_callbackAnimation.animationEnd(null);

                if(result)
                    return;
            }
            else
                setNumber(m_num);
        }
    }

    public void draw(float [] m, float delta)
    {
        for(Unit u : m_units)
            u.draw(m, delta);
    }
}
