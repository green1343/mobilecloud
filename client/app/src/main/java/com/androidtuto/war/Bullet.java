package com.androidtuto.war;

/**
 * Created by 초록 on 2015-06-04.
 */
public class Bullet extends Unit {

    public static final int DEFAULT = 0;
    public static final int MISSILE = 2;
    public static final int SNIPERRIFLE = 4;
    public static final int WAVE = 5;
    public static final int GUIDEDMISSILE = 6;

    public static final int SHIELD = 8;
    public static final int WHIRLWIND = 9;
    public static final int LASER = 10;
    public static final int BLACKHOLE = 11;
    public static final int LIGHTNING = 12;
    public static final int STRAFE = 13;

    int m_type;
    float m_power;
    float m_hp;
    boolean m_special;

    // guided missile
    Unit m_enemy;

    Bullet()
    {
        super();
        m_type = DEFAULT;
        m_power = 0f;
        m_hp = 0f;
        m_special = false;

        m_enemy = null;
    }

    Bullet(int type, float fromX, float fromY, float dirX, float dirY)
    {
        this();

        setType(type);
        setPosition(fromX, fromY);
        setDirection(new Vec2(dirX, dirY), false);
    }

    public void setType(int type)
    {
        m_type = type;
        switch(type) {
            default:
            case DEFAULT:
                setSize(0.3f, 0.3f);
                setVelocity(0.35f);
                setPicture(R.drawable.bullet_circle_orange);
                break;
            case MISSILE:
                setSize(0.3f, 0.5f);
                setVelocity(0.3f);
                setSeeForward(true);
                setFxMove(FX_MOVE_MISSILE);
                setZ(1);
                setPicture(R.drawable.bullet_missile);
                break;
            case SNIPERRIFLE:
                setSize(Manager.INSTANCE.MAP_WIDTH / 6f, Manager.INSTANCE.MAP_WIDTH / 1.5f);
                setPicture(R.drawable.bullet_sniper);
                animateAlpha(1f, 0f, -0.004f);
                setCallbackAniAlpha(new CallbackAnimation(){
                    public void animationEnd(Unit u){
                        Game.INSTANCE.deleteUnit(u);
                    }
                });
                break;
            case WAVE:
                setSize(0.6f, 0.3f);
                setVelocity(0.3f);
                setPicture(R.drawable.num0);
                break;
            case GUIDEDMISSILE:
                setSize(0.3f, 0.5f);
                setVelocity(0.3f);
                setSeeForward(true);
                setFxMove(FX_MOVE_MISSILE);
                setZ(1);
                setPicture(R.drawable.bullet_missile);
                break;
            case WHIRLWIND:
                setSize(4f, 4f);
                setPicture(R.drawable.bullet_whirlwind);
                animateAngle((float) Math.PI, 0, -(float) Math.PI / 200f, true);
                animateAlpha(0f, 1f, 0.01f);
                break;
            case LASER:
                setSize(Manager.INSTANCE.MAP_WIDTH / 6f, Manager.INSTANCE.MAP_WIDTH / 1.5f);
                setPicture(R.drawable.bullet_laser);
                animateAlpha(0f, 1f, 0.005f);
                break;
            case BLACKHOLE:
                setSize(4f, 4f);
                setPicture(R.drawable.bullet_blackhole);
                setAngle(Game.INSTANCE.getRandomFloat(0f, (float) Math.PI * 2f));
                animateAngle((float) Math.PI, 0, -(float) Math.PI / 400f, true);
                animateAlpha(0f, 1f, 0.003f);
                break;
            case LIGHTNING:
                setPicture(Game.INSTANCE.getRandomInt(R.drawable.bullet_lightning1, R.drawable.bullet_lightning6));
                animateAlpha(1f, 0f, -0.005f);
                setCallbackAniAlpha(new CallbackAnimation() {
                    public void animationEnd(Unit u) {
                        Game.INSTANCE.deleteUnit(u);
                    }
                });
                break;
            case STRAFE:
                setSize(1.5f, 1.5f);
                setPicture(R.drawable.bullet_strafe);
                animateAlpha(1f, 0f, -0.005f);
                setCallbackAniAlpha(new CallbackAnimation() {
                    public void animationEnd(Unit u) {
                        Game.INSTANCE.deleteUnit(u);
                    }
                });
                break;
        }

        setCollisionBox(0.3f, 0.3f);
    }

    public float getPower(){return m_power;}
    public void setPower(float power){m_power = power;}

    public void setHp(float hp){m_hp = hp;}

    public void setSpecial(boolean special){m_special = special;}

    public boolean damage(float d)
    {
        m_hp -= d;
        if(m_hp <= 0) {
            if(m_type == Bullet.MISSILE || m_type == Bullet.GUIDEDMISSILE) {
                float SIZE = 3.5f;
                if(m_special)
                    SIZE = 5f;

                if(getTeam() < 0){
                    Character c = Game.INSTANCE.getPlayer();
                    if (c.checkCollide(getPosition().x, getPosition().y, SIZE, SIZE) &&
                            Vec2.length(c.getPosition(), getPosition()) <= SIZE)
                        c.damage(getPower());
                }
                else {
                    for (Character c : Game.INSTANCE.getCharacters().values()) {
                        if (isEnemy(c)) {
                            if (c.checkCollide(getPosition().x, getPosition().y, SIZE, SIZE) &&
                                    Vec2.length(c.getPosition(), getPosition()) <= SIZE)
                                c.damage(getPower());
                        }
                    }
                }

                Game.INSTANCE.addFx(getPosition().x, getPosition().y, SIZE+1f, R.drawable.fx_explosion2, 15);
            }
            Game.INSTANCE.deleteUnit(this);
            return true;
        }
        else
            return false;
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);

        if(m_type == GUIDEDMISSILE){
            if(m_enemy != null) {
                if(Game.INSTANCE.isExist(m_enemy))
                    traceStart(m_enemy.getPosition(), 0.004f, Unit.TRACE_CURVE);
                else{
                    traceStop();
                    m_enemy = null;
                }
                return;
            }
            else
                m_enemy = Game.INSTANCE.getClosestEnemy(getTeam(), getPosition(), Manager.INSTANCE.MAP_WIDTH);
        }
    }
}
