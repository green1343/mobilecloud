package com.androidtuto.war;

/**
 * Created by 초록 on 2015-06-04.
 */
public class Character extends Unit{

    float m_hp;
    float m_hpMax;
    float m_hpRecover;

    float m_power; // collide

    Character(){
        super();

        m_hp = 30f;
        m_hpMax = 30f;
        m_hpRecover = 0f;

        m_power = 0f;
    }

    Character(float x, float y, float radius, float velocity){
        this();

        setPosition(x, y);
        setSize(radius, radius);
        setCollisionBox(radius * 0.7f, radius * 0.7f);

        animateTwing(radius-0.2f, radius, 0.03f, 0.0003f, 0f);
        setMaxVelocity(velocity);
        setSpeed(velocity / 200f);
    }

    public float getHp(){return m_hp;}
    public void setHp(float hp){
        m_hp = hp;
        if(m_hp > m_hpMax)
            m_hpMax = m_hp;
    }
    public void recoverHp(float hp){
        m_hp += hp;
        if(m_hp > m_hpMax)
            m_hp = m_hpMax;
    }
    public float getHpMax(){return m_hpMax;}

    public void setPower(float power){m_power = power;}
    public float getPower(){return m_power;}

    public boolean damage(float d){
        float SIZE = 1.3f;
        if(Game.INSTANCE.getBoss() == this)
            SIZE = 2.3f;

        setSize(SIZE, SIZE);

        m_hp -= d;
        if(m_hp <= 0) {
            Game.INSTANCE.killCharacter(this);
            return true;
        }
        else
            return false;
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        if(m_hpRecover > 0f) {
            recoverHp(m_hpRecover * delta);
            if(this instanceof Player)
                Manager.INSTANCE.updateStatus(getHp() / getHpMax(), Game.INSTANCE.getExp() / (float)Game.INSTANCE.getExpMax());
        }
    }
}
