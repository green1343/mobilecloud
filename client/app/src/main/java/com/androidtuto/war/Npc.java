package com.androidtuto.war;

import java.util.ArrayList;

/**
 * Created by 초록 on 2015-06-06.
 */
public class Npc extends Character{

    static final int ZOMBIE = 0;
    static final int BOMBER = 1;
    static final int SHOOTER = 2;

    int m_type;

    // move
    float m_accumTime;
    float m_timeMove;
    Vec2 m_areaMin;
    Vec2 m_areaMax;

    // attack
    Unit m_enemy;
    float m_range;

    ArrayList<Weapon> m_weapons;

    void init(){
        m_type = ZOMBIE;
        m_accumTime = 0f;
        setTimeMove();
        m_areaMin = new Vec2();
        m_areaMax = new Vec2();
        m_enemy = null;
        m_range = Manager.INSTANCE.MAP_WIDTH - 1f;

        m_weapons = new ArrayList<Weapon>();
    }

    Npc(){
        super();
        init();
    }

    Npc(int type, float x, float y, float radius, float velocity, float area, int picture, float hp)
    {
        super(x, y, radius, velocity);
        init();
        setType(type);
        setArea(area);
        setPicture(picture);
        setHp(hp);
    }

    Npc(int type, float x, float y, float radius, float velocity, float area, int picture, float hp, float power)
    {
        this(type, x, y, radius, velocity, area, picture, hp);
        setPower(power);
    }

    public int getType(){return m_type;}

    public void setType(int type){
        m_type = type;
        //if(m_type == BOMBER)
        //    setSeeForward(true);
    }

    public void setArea(float area){
        m_areaMin.x = getPosition().x - area;
        m_areaMin.y = getPosition().y - area;
        m_areaMax.x = getPosition().x + area;
        m_areaMax.y = getPosition().y + area;
    }

    public void setTimeMove(){
        m_timeMove = Manager.INSTANCE.getRandomFloat(2000f, 5000f);
    }

    public boolean checkRange(Unit u){
        boolean result = u.checkCollide(getPosition().x, getPosition().y, m_range, m_range);
        if(result)
            m_enemy = u;
        return result;
    }

    public Vec2 getAttackDir(){
        Vec2 v = new Vec2();
        if(m_enemy == null)
            return v;

        v.x = m_enemy.getPosition().x - getPosition().x;
        v.y = m_enemy.getPosition().y - getPosition().y;
        return v;
    }

    public void addWeapon(int type, int level){
        m_weapons.add(new Weapon(this, type, level));
    }
    public Weapon getWeapon(int index){return m_weapons.get(index);}
    public int getWeaponSize(){return m_weapons.size();}

    @Override
    public void update(float delta)
    {
        super.update(delta);

        if(m_type == BOMBER){
            if(m_enemy != null) {
                traceStart(m_enemy.getPosition(), 0.004f, Unit.TRACE_CURVE);
                return;
            }
            else if(Vec2.checkSqrAndDist(Game.INSTANCE.getPlayer().getPosition(), getPosition(), m_range))
                m_enemy = Game.INSTANCE.getPlayer();
                //m_enemy = Game.INSTANCE.getClosestEnemy(getTeam(), getPosition(), m_range);
        }

        m_accumTime += delta;
        if (m_accumTime >= m_timeMove) {
            m_accumTime = 0f;
            traceStart(Game.INSTANCE.getRandomFloat(m_areaMin.x, m_areaMax.x), Game.INSTANCE.getRandomFloat(m_areaMin.y, m_areaMax.y), 0.004f, Unit.TRACE_CURVE);
        }

        if(m_type == SHOOTER) {
            if (m_enemy != null) {
                if (checkRange(m_enemy) == false) {
                    m_enemy = null;
                    for(Weapon w : m_weapons)
                        w.touchUp();
                } else {
                    Vec2 v = getAttackDir();

                    for(Weapon w : m_weapons)
                        w.touchDown(v.x, v.y);
                }
            }
            else if(Vec2.checkSqrAndDist(Game.INSTANCE.getPlayer().getPosition(), getPosition(), m_range))
                m_enemy = Game.INSTANCE.getPlayer();
                //m_enemy = Game.INSTANCE.getClosestEnemy(getTeam(), getPosition(), m_range);
        }

        for(Weapon w : m_weapons)
            w.update(delta);
    }
}
