package com.androidtuto.war;

import java.util.HashMap;

/**
 * Created by 초록 on 2015-06-04.
 */
public class Weapon {

    public static final int MAIN_PISTOL = 0;
    public static final int MAIN_MACHINEGUN = 1;
    public static final int MAIN_SHOTGUN = 2;
    public static final int MAIN_WHIRLWIND = 3;
    public static final int MAIN_MISSILE = 4;
    public static final int MAIN_SNIPERRIFLE = 5;
    public static final int MAIN_LASER = 6;
    public static final int MAIN_LIGHTNING = 7;
    public static final int MAIN_STRAFE = 8;
    public static final int MAIN_BLACKHOLE = 9;
    public static final int MAIN_GUIDEDMISSILE = 10;

    public static final int NPC_PISTOL = 11;
    public static final int NPC_MACHINEGUN = 12;
    public static final int NPC_SHOTGUN = 13;
    public static final int NPC_MISSILE = 14;
    public static final int NPC_GUIDEDMISSILE = 15;

    public static final int TYPE_COUNT = 11;

    public static final int LEVEL_MAX = 5;

    int m_type;
    int m_level;
    Character m_character;
    float m_power;
    boolean m_bFiring;
    Bullet m_fixedBullet;

    Vec2 m_direction;
    float m_dist;

    float m_fireRate;
    float m_accumTime;

    float m_overheat;
    float m_overheatMax;
    float m_overheatRecover;

    Unit m_destUnit;

    Weapon(Character character) {
        m_type = MAIN_PISTOL;
        m_level = 1;
        m_character = character;
        m_power = 0f;
        m_bFiring = false;
        m_fixedBullet = null;
        m_direction = new Vec2();
        m_dist = 0f;
        m_fireRate = 0.5f;
        m_accumTime = 0f;
        m_overheat = 100f;
        m_overheatMax = 100f;
        m_overheatRecover = 20f;
        m_destUnit = null;
    }

    Weapon(Character character, int type, int level){
        this(character);
        create(type, level);
    }
    
    public void create(int type, int level){
        m_type = type;
        m_level = level;

        switch(type){
            default:
            case MAIN_PISTOL:
                if(level == 1)      {m_power = 10.1f; m_fireRate = 500f;}
                else if(level == 2) {m_power = 20.1f; m_fireRate = 500f;}
                else if(level == 3) {m_power = 40.1f; m_fireRate = 300f;}
                else if(level == 4) {m_power = 80.1f; m_fireRate = 300f;}
                else if(level == 5) {m_power = 160.1f; m_fireRate = 300f;}
                break;
            case MAIN_MACHINEGUN:
                if(level == 1)      {m_power = 10.1f; m_fireRate = 200f;}
                else if(level == 2) {m_power = 20.1f; m_fireRate = 200f;}
                else if(level == 3) {m_power = 40.1f; m_fireRate = 150f;}
                else if(level == 4) {m_power = 80.1f; m_fireRate = 150f;}
                else if(level == 5) {m_power = 160.1f; m_fireRate = 150f;}
                break;
            case MAIN_SHOTGUN:
                if(level == 1)      {m_power = 10.1f; m_fireRate = 700f;}
                else if(level == 2) {m_power = 20.1f; m_fireRate = 700f;}
                else if(level == 3) {m_power = 40.1f; m_fireRate = 400f;}
                else if(level == 4) {m_power = 80.1f; m_fireRate = 400f;}
                else if(level == 5) {m_power = 160.1f; m_fireRate = 400f;}
                break;
            case MAIN_WHIRLWIND:
                if(level == 1)      {m_power = 1.5f; m_fireRate = 10f;}
                else if(level == 2) {m_power = 3f; m_fireRate = 10f;}
                else if(level == 3) {m_power = 6f; m_fireRate = 10f;}
                else if(level == 4) {m_power = 12f; m_fireRate = 10f;}
                else if(level == 5) {m_power = 24f; m_fireRate = 10f;}
                break;
            case MAIN_MISSILE:
                if(level == 1)      {m_power = 80.1f; m_fireRate = 1200f;}
                else if(level == 2) {m_power = 150.1f; m_fireRate = 1200f;}
                else if(level == 3) {m_power = 250.1f; m_fireRate = 1200f;}
                else if(level == 4) {m_power = 500.1f; m_fireRate = 900f;}
                else if(level == 5) {m_power = 1000.1f; m_fireRate = 900f;}
                break;
            case MAIN_SNIPERRIFLE:
                if(level == 1)      {m_power = 100.1f; m_fireRate = 1000f;}
                else if(level == 2) {m_power = 200.1f; m_fireRate = 1000f;}
                else if(level == 3) {m_power = 350.1f; m_fireRate = 500f;}
                else if(level == 4) {m_power = 700.1f; m_fireRate = 500f;}
                else if(level == 5) {m_power = 1400.1f; m_fireRate = 500f;}
                break;
            case MAIN_LASER:
                if(level == 1)      {m_power = 6.1f; m_fireRate = 10f;}
                else if(level == 2) {m_power = 12.1f; m_fireRate = 10f;}
                else if(level == 3) {m_power = 20.1f; m_fireRate = 10f;}
                else if(level == 4) {m_power = 40.1f; m_fireRate = 10f;}
                else if(level == 5) {m_power = 80.1f; m_fireRate = 10f;}
                break;
            case MAIN_LIGHTNING:
                if(level == 1)      {m_power = 200.1f; m_fireRate = 500f;}
                else if(level == 2) {m_power = 400.1f; m_fireRate = 500f;}
                else if(level == 3) {m_power = 600.1f; m_fireRate = 250f;}
                else if(level == 4) {m_power = 800.1f; m_fireRate = 250f;}
                else if(level == 5) {m_power = 1000.1f; m_fireRate = 200f;}
                break;
            case MAIN_STRAFE:
                if(level == 1)      {m_power = 120.1f; m_fireRate = 200f;}
                else if(level == 2) {m_power = 240.1f; m_fireRate = 200f;}
                else if(level == 3) {m_power = 350.1f; m_fireRate = 200f;}
                else if(level == 4) {m_power = 550.1f; m_fireRate = 150f;}
                else if(level == 5) {m_power = 800.1f; m_fireRate = 150f;}
                break;
            case MAIN_BLACKHOLE:
                if(level == 1)      {m_power = 20.1f; m_fireRate = 10f;}
                else if(level == 2) {m_power = 40.1f; m_fireRate = 10f;}
                else if(level == 3) {m_power = 60.1f; m_fireRate = 10f;}
                else if(level == 4) {m_power = 100.1f; m_fireRate = 10f;}
                else if(level == 5) {m_power = 150.1f; m_fireRate = 10f;}
                break;
            case MAIN_GUIDEDMISSILE:
                if(level == 1)      {m_power = 400.1f; m_fireRate = 500f;}
                else if(level == 2) {m_power = 700.1f; m_fireRate = 500f;}
                else if(level == 3) {m_power = 1000.1f; m_fireRate = 500f;}
                else if(level == 4) {m_power = 1000.1f; m_fireRate = 500f;}
                else if(level == 5) {m_power = 1200.1f; m_fireRate = 400f;}
                break;

            case NPC_PISTOL:
                if(level == 1)      {m_power = 10.1f; m_fireRate = 5000f;}
                else if(level == 2) {m_power = 15.1f; m_fireRate = 3000f;}
                else if(level == 3) {m_power = 20.1f; m_fireRate = 3000f;}
                break;
            case NPC_MACHINEGUN:
                if(level == 1)      {m_power = 10.1f; m_fireRate = 1000f;}
                else if(level == 2) {m_power = 15.1f; m_fireRate = 800f;}
                else if(level == 3) {m_power = 20.1f; m_fireRate = 800f;}
                break;
            case NPC_SHOTGUN:
                if(level == 1)      {m_power = 10.1f; m_fireRate = 8000f;}
                else if(level == 2) {m_power = 15.1f; m_fireRate = 5000f;}
                else if(level == 3) {m_power = 20.1f; m_fireRate = 5000f;}
                break;
            case NPC_MISSILE:
                if(level == 1)      {m_power = 20.1f; m_fireRate = 4000f;}
                else if(level == 2) {m_power = 30.1f; m_fireRate = 3000f;}
                else if(level == 3) {m_power = 40.1f; m_fireRate = 3000f;}
                break;
            case NPC_GUIDEDMISSILE:
                if(level == 1)      {m_power = 20.1f; m_fireRate = 2000f;}
                else if(level == 2) {m_power = 30.1f; m_fireRate = 1000f;}
                else if(level == 3) {m_power = 40.1f; m_fireRate = 2000f;}
                break;

        }

        m_accumTime = m_fireRate;
    }

    public void setFireRate(float rate){
        m_fireRate = rate;
    }

    static int getWeaponPicture(int weapon){

        switch(weapon){
            default:
            case MAIN_PISTOL:       return R.drawable.weapon_default;
            case MAIN_MACHINEGUN:   return R.drawable.weapon_main_machinegun;
            case MAIN_SHOTGUN:      return R.drawable.weapon_main_shotgun;
            case MAIN_MISSILE:      return R.drawable.weapon_main_missile;
            case MAIN_SNIPERRIFLE:  return R.drawable.weapon_main_sniperrifle;
            case MAIN_GUIDEDMISSILE:return R.drawable.weapon_main_guidedmissile_main;
            case MAIN_WHIRLWIND:    return R.drawable.weapon_main_whirlwind;
            case MAIN_LASER:        return R.drawable.weapon_main_laser;
            case MAIN_BLACKHOLE:    return R.drawable.weapon_main_blackhole;
            case MAIN_LIGHTNING:    return R.drawable.weapon_main_lightning;
            case MAIN_STRAFE:       return R.drawable.weapon_main_strafe;
        }
    }

    public void setPower(float power){m_power = power;}

    public int getType(){return m_type;}
    public int getLevel(){return m_level;}

    public void setOverheatMax(float f){
        m_overheatMax = f;
        if(m_overheat > m_overheatMax)
            m_overheat = m_overheatMax;
    }

    public boolean isAssisted(){
        return m_type == MAIN_PISTOL ||
                m_type == MAIN_MACHINEGUN ||
                m_type == MAIN_SNIPERRIFLE ||
                m_type == MAIN_LIGHTNING ||
                m_type == MAIN_MISSILE;
    }

    public void touchDown(){
        touchDown(0f, 0f);
    }

    public void touchDown(float x, float y) {

        m_bFiring = true;

        if(m_type == MAIN_WHIRLWIND || m_type == MAIN_STRAFE)
            return;

        m_direction.x = x;
        m_direction.y = y;
        m_dist = m_direction.length();
        if(m_type != MAIN_BLACKHOLE){
            m_direction.x /= m_dist;
            m_direction.y /= m_dist;
        }
    }

    public void touchUp()
    {
        m_bFiring = false;
        if(m_fixedBullet != null) {
            m_fixedBullet.animateAlpha(0f, -0.01f);
            m_fixedBullet.setCallbackAniAlpha(new CallbackAnimation() {
                public void animationEnd(Unit u) {
                    Game.INSTANCE.deleteUnit(u);
                }
            });
            m_fixedBullet = null;
        }
        if(m_destUnit != null)
            m_destUnit = null;
    }

    public void setDestUnit(Unit u)
    {
        m_destUnit = u;
    }
    
    public void shootSniperrifle(Vec2 dir){
        float length = Manager.INSTANCE.MAP_WIDTH;

        Vec2 p = new Vec2(m_character.getPosition());
        p.x += dir.x * length / 1.5f;
        p.y += dir.y * length / 1.5f;
        Bullet b = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.SNIPERRIFLE, m_power, p, dir);
        b.setAngle(dir.getAngle());

        Game.INSTANCE.setUpdating(true);

        Vec2 dir2 = new Vec2(-dir.y, dir.x);
        Vec2 dir3 = new Vec2(dir.y, -dir.x);

        final float SIZE = 2f;
        Vec2 p1 = new Vec2(m_character.getPosition());
        Vec2 p2 = new Vec2(p1.x + (dir.x * length) + (dir2.x * SIZE), p1.y + (dir.y * length) + (dir2.y * SIZE));
        Vec2 p3 = new Vec2(p1.x + (dir.x * length) + (dir3.x * SIZE), p1.y + (dir.y * length) + (dir3.y * SIZE));
        Vec2 p4 = new Vec2(p1.x + (dir3.x * SIZE), p1.y + (dir3.y * SIZE));
        p1.x += dir2.x * SIZE;
        p1.y += dir2.y * SIZE;

        for (Character c : Game.INSTANCE.getCharacters().values()) {
            if (b.isEnemy(c)) {
                if (Vec2.checkTriangle(p1, p2, p3, c.getPosition()) || Vec2.checkTriangle(p4, p1, p3, c.getPosition())) {
                    c.damage(b.getPower());
                }
            }
        }

        for (Bullet b2 : Game.INSTANCE.getBullets2().values()) {
            if (b.isEnemy(b2)) {
                if (Vec2.checkTriangle(p1, p2, p3, b2.getPosition()) || Vec2.checkTriangle(p3, p4, p1, b2.getPosition())) {
                    b2.damage(1);
                }
            }
        }

        Game.INSTANCE.setUpdating(false);
    }

    public void shootLightning(Vec2 p1, Vec2 p2, int team, Unit u)
    {
        Vec2 d = new Vec2(p2.x - p1.x, p2.y - p1.y);
        Vec2 p = new Vec2(p1.x + d.x/2f, p1.y + d.y/2f);

        float length = Vec2.length(p1, p2);
        d.x /= length;
        d.y /= length;
        float angle = d.getAngle();

        Bullet b = Game.INSTANCE.addBullet(team, Bullet.LIGHTNING, m_power, p, m_direction);
        b.setSize(Manager.INSTANCE.MAP_WIDTH / 6f, length / 2f);
        b.setAngle(angle);

        if(u instanceof Character)
            ((Character)u).damage(b.getPower());
        else if(u instanceof Bullet)
            ((Bullet)u).damage(b.getPower());
    }

    public void update(float delta){
        if(m_bFiring){
            m_accumTime += delta;
            if(m_accumTime >= m_fireRate){
                // shoot
                switch(m_type){
                    default:
                    case MAIN_PISTOL:
                        Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.DEFAULT, m_power, m_character.getPosition(), m_direction);
                        break;
                    case MAIN_MACHINEGUN:
                        Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.DEFAULT, m_power, m_character.getPosition(), m_direction);
                        break;
                    case MAIN_SHOTGUN: {
                        Vec2 d1 = new Vec2(m_direction);
                        Vec2 d2 = new Vec2(m_direction);
                        Vec2 d3 = new Vec2(m_direction);
                        Vec2 d4 = new Vec2(m_direction);
                        d1.rotate((float) Math.PI / 16f);
                        d2.rotate(-(float) Math.PI / 16f);
                        d3.rotate((float) Math.PI / 32f);
                        d4.rotate(-(float) Math.PI / 32f);
                        Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.DEFAULT, m_power, m_character.getPosition(), m_direction);
                        Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.DEFAULT, m_power, m_character.getPosition(), d1);
                        Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.DEFAULT, m_power, m_character.getPosition(), d2);
                        Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.DEFAULT, m_power, m_character.getPosition(), d3);
                        Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.DEFAULT, m_power, m_character.getPosition(), d4);
                        break;
                    }
                    case MAIN_MISSILE: {
                        Bullet b = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.MISSILE, m_power, m_character.getPosition(), m_direction);
                        if (m_level >= 3)
                            b.setSpecial(true);
                        break;
                    }
                    case MAIN_SNIPERRIFLE: {
                        shootSniperrifle(m_direction);
                        break;
                    }
                    case MAIN_GUIDEDMISSILE:
                        if(m_level <= 2)
                            Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.GUIDEDMISSILE, m_power, m_character.getPosition(), m_direction);
                        else if(m_level == 3) {
                            Vec2 v1 = new Vec2(m_direction.y, -m_direction.x);
                            Vec2 v2 = new Vec2(-m_direction.y, m_direction.x);
                            Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.GUIDEDMISSILE, m_power, m_character.getPosition(), new Vec2((m_direction.x+v1.x)/2f, (m_direction.y+v1.y)/2f));
                            Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.GUIDEDMISSILE, m_power, m_character.getPosition(), new Vec2((m_direction.x+v2.x)/2f, (m_direction.y+v2.y)/2f));
                        }
                        else{
                            Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.GUIDEDMISSILE, m_power, m_character.getPosition(), m_direction);
                            Vec2 v1 = new Vec2(m_direction.y, -m_direction.x);
                            Vec2 v2 = new Vec2(-m_direction.y, m_direction.x);
                            Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.GUIDEDMISSILE, m_power, m_character.getPosition(), new Vec2((m_direction.x+v1.x)/2f, (m_direction.y+v1.y)/2f));
                            Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.GUIDEDMISSILE, m_power, m_character.getPosition(), new Vec2((m_direction.x+v2.x)/2f, (m_direction.y+v2.y)/2f));
                        }
                        break;
                    case MAIN_WHIRLWIND:
                    {
                        if (m_fixedBullet == null)
                            m_fixedBullet = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.WHIRLWIND, m_power, m_character.getPosition(), m_direction);

                        if(m_level >= 3)
                            m_fixedBullet.setSize(5f, 5f);

                        m_fixedBullet.setPosition(m_character.getPosition());

                        Game.INSTANCE.setUpdating(true);

                        for (Character c : Game.INSTANCE.getCharacters().values()) {
                            if (m_fixedBullet.isEnemy(c)) {
                                if (Vec2.checkSqrAndDist(c.getPosition(), m_fixedBullet.getPosition(), m_fixedBullet.getSize().x))
                                    c.damage(m_fixedBullet.getPower());
                            }
                        }

                        for (Bullet b2 : Game.INSTANCE.getBullets2().values()) {
                            if (m_fixedBullet.isEnemy(b2)) {
                                if (Vec2.checkSqrAndDist(b2.getPosition(), m_fixedBullet.getPosition(), m_fixedBullet.getSize().x))
                                    b2.damage(1);
                            }
                        }

                        Game.INSTANCE.setUpdating(false);
                        break;
                    }
                    case MAIN_LASER:
                    {
                        float length = Manager.INSTANCE.MAP_WIDTH;

                        Vec2 p = new Vec2(m_character.getPosition());
                        p.x += m_direction.x * length / 1.5f;
                        p.y += m_direction.y * length / 1.5f;

                        if (m_fixedBullet == null)
                            m_fixedBullet = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.LASER, m_power, p, m_direction);

                        m_fixedBullet.setPosition(p);
                        m_fixedBullet.setAngle(m_direction.getAngle());

                        Game.INSTANCE.setUpdating(true);

                        Vec2 dir2 = new Vec2(-m_direction.y, m_direction.x);
                        Vec2 dir3 = new Vec2(m_direction.y, -m_direction.x);

                        float SIZE = 2f;
                        if(m_level >= 3){
                            m_fixedBullet.setSize(Manager.INSTANCE.MAP_WIDTH / 4f, Manager.INSTANCE.MAP_WIDTH / 1.5f);
                            SIZE = 3f;
                        }

                        Vec2 p1 = new Vec2(m_character.getPosition());
                        Vec2 p2 = new Vec2(p1.x + (m_direction.x * length) + (dir2.x * SIZE), p1.y + (m_direction.y * length) + (dir2.y * SIZE));
                        Vec2 p3 = new Vec2(p1.x + (m_direction.x * length) + (dir3.x * SIZE), p1.y + (m_direction.y * length) + (dir3.y * SIZE));
                        Vec2 p4 = new Vec2(p1.x + (dir3.x * SIZE), p1.y + (dir3.y * SIZE));
                        p1.x += dir2.x * SIZE;
                        p1.y += dir2.y * SIZE;

                        for (Character c : Game.INSTANCE.getCharacters().values()) {
                            if (m_fixedBullet.isEnemy(c)) {
                                if (Vec2.checkTriangle(p1, p2, p3, c.getPosition()) || Vec2.checkTriangle(p4, p1, p3, c.getPosition())) {
                                    c.damage(m_fixedBullet.getPower());
                                }
                            }
                        }

                        for (Bullet b2 : Game.INSTANCE.getBullets2().values()) {
                            if (m_fixedBullet.isEnemy(b2)) {
                                if (Vec2.checkTriangle(p1, p2, p3, b2.getPosition()) || Vec2.checkTriangle(p3, p4, p1, b2.getPosition())) {
                                    b2.damage(1);
                                }
                            }
                        }

                        Game.INSTANCE.setUpdating(false);
                        break;
                    }
                    case MAIN_BLACKHOLE:
                    {
                        if (m_fixedBullet == null)
                            m_fixedBullet = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.BLACKHOLE, m_power, m_character.getPosition(), m_direction);

                        if(m_level >= 3)
                            m_fixedBullet.setSize(5f, 5f);

                        Vec2 p = new Vec2(m_character.getPosition());
                        p.x += m_direction.x * 4f;
                        p.y += m_direction.y * 3f;
                        m_fixedBullet.setPosition(p);

                        Game.INSTANCE.setUpdating(true);

                        for (Character c : Game.INSTANCE.getCharacters().values()) {
                            if (m_fixedBullet.isEnemy(c)) {
                                if (Vec2.checkSqrAndDist(c.getPosition(), m_fixedBullet.getPosition(), m_fixedBullet.getSize().x))
                                    c.damage(m_fixedBullet.getPower());
                            }
                        }

                        for (Bullet b2 : Game.INSTANCE.getBullets2().values()) {
                            if (m_fixedBullet.isEnemy(b2)) {
                                if (Vec2.checkSqrAndDist(b2.getPosition(), m_fixedBullet.getPosition(), m_fixedBullet.getSize().x))
                                    b2.damage(1);
                            }
                        }

                        Game.INSTANCE.setUpdating(false);
                        break;
                    }
                    case MAIN_LIGHTNING: {
                        if(m_destUnit == null) {
                            Vec2 p = new Vec2(m_character.getPosition());
                            p.x += m_direction.x * Manager.INSTANCE.MAP_WIDTH / 1.5f;
                            p.y += m_direction.y * Manager.INSTANCE.MAP_WIDTH / 1.5f;
                            shootLightning(m_character.getPosition(), p, m_character.getTeam(), null);
                        }
                        else {
                            shootLightning(m_character.getPosition(), m_destUnit.getPosition(), m_character.getTeam(), m_destUnit);

                            float range = 6f;
                            HashMap<Integer, Unit> list = new HashMap<Integer, Unit>();
                            list.put(m_character.getIndex(), m_character);
                            list.put(m_destUnit.getIndex(), m_destUnit);
                            Unit u = m_destUnit;
                            for(int i=0; i<5; ++i) {
                                boolean check = false;
                                for (Character c : Game.INSTANCE.getCharacters().values()) {
                                    if(list.containsKey(c.getIndex()) == false && m_character.isEnemy(c) &&
                                            Vec2.checkSquare(u.getPosition(), c.getPosition(), range))
                                    {
                                        shootLightning(u.getPosition(), c.getPosition(), m_character.getTeam(), c);
                                        list.put(c.getIndex(), c);
                                        u = c;
                                        check = true;
                                        break;
                                    }
                                }
                                if(check == false){
                                    for (Bullet c : Game.INSTANCE.getBullets2().values()) {
                                        if(list.containsKey(c.getIndex()) == false && m_character.isEnemy(c) &&
                                                Vec2.checkSquare(u.getPosition(), c.getPosition(), range))
                                        {
                                            shootLightning(u.getPosition(), c.getPosition(), m_character.getTeam(), c);
                                            list.put(c.getIndex(), c);
                                            u = c;
                                            check = true;
                                            break;
                                        }
                                    }
                                }
                                if(check == false)
                                    break;
                            }
                            m_destUnit = null;
                        }

                        break;
                    }
                    case MAIN_STRAFE: {

                        Game.INSTANCE.setUpdating(true);

                        float range = Manager.INSTANCE.MAP_HEIGHT;
                        int check = 0;
                        int num = 2;
                        if(m_level >= 3)
                            num = 3;

                        for (Bullet c : Game.INSTANCE.getBullets2().values()) {
                            if (m_character.isEnemy(c) &&
                                    Vec2.checkSquare(m_character.getPosition(), c.getPosition(), range))
                            {
                                Vec2 d = new Vec2(c.getPosition().x - m_character.getPosition().x, c.getPosition().y - m_character.getPosition().y);
                                d.normalize();
                                Vec2 p = new Vec2(m_character.getPosition().x + d.x*2f, m_character.getPosition().y + d.y*2f);
                                Bullet b = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.STRAFE, m_power, p, m_direction);
                                b.setAngle(d.getAngle());
                                c.damage(b.getPower());

                                ++check;
                                if(check >= num)
                                    break;
                            }
                        }

                        if(check < num) {
                            for (Character c : Game.INSTANCE.getCharacters().values()) {
                                if (c instanceof Npc && ((Npc)c).getType() == Npc.BOMBER && m_character.isEnemy(c) &&
                                        Vec2.checkSquare(m_character.getPosition(), c.getPosition(), range))
                                {
                                    Vec2 d = new Vec2(c.getPosition().x - m_character.getPosition().x, c.getPosition().y - m_character.getPosition().y);
                                    d.normalize();
                                    Vec2 p = new Vec2(m_character.getPosition().x + d.x*2f, m_character.getPosition().y + d.y*2f);
                                    Bullet b = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.STRAFE, m_power, p, m_direction);
                                    b.setAngle(d.getAngle());
                                    c.damage(b.getPower());

                                    d.x = -d.x;
                                    d.y = -d.y;
                                    Unit fx = Game.INSTANCE.addFx(c.getPosition().x + d.x*(c.getSize().x+0.2f), c.getPosition().y + d.y*(c.getSize().y+0.2f), 1f, 1f, 1f, 0f, -0.003f, R.drawable.fx_hit);
                                    fx.setAngle(d.getAngle());

                                    ++check;
                                    if(check >= num)
                                        break;
                                }
                            }
                        }

                        if(check < num) {
                            for (Character c : Game.INSTANCE.getCharacters().values()) {
                                if (m_character.isEnemy(c) &&
                                        Vec2.checkSquare(m_character.getPosition(), c.getPosition(), range))
                                {
                                    Vec2 d = new Vec2(c.getPosition().x - m_character.getPosition().x, c.getPosition().y - m_character.getPosition().y);
                                    d.normalize();
                                    Vec2 p = new Vec2(m_character.getPosition().x + d.x*2f, m_character.getPosition().y + d.y*2f);
                                    Bullet b = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.STRAFE, m_power, p, m_direction);
                                    b.setAngle(d.getAngle());
                                    c.damage(b.getPower());

                                    d.x = -d.x;
                                    d.y = -d.y;
                                    Unit fx = Game.INSTANCE.addFx(c.getPosition().x + d.x*(c.getSize().x+0.2f), c.getPosition().y + d.y*(c.getSize().y+0.2f), 1f, 1f, 1f, 0f, -0.003f, R.drawable.fx_hit);
                                    fx.setAngle(d.getAngle());

                                    ++check;
                                    if(check >= num)
                                        break;
                                }
                            }
                        }

                        if(check == 0){
                            Vec2 d = new Vec2(0f, 1f);
                            float angle = Game.INSTANCE.getRandomFloat(0f, (float)Math.PI*2f);
                            d.rotate(angle);
                            Vec2 p = new Vec2(m_character.getPosition().x + d.x*2f, m_character.getPosition().y + d.y*2f);
                            Bullet b = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.STRAFE, m_power, p, m_direction);
                            b.setAngle(angle);
                        }

                        Game.INSTANCE.setUpdating(false);

                        break;
                    }


                    case NPC_PISTOL: {
                        Unit b = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.DEFAULT, m_power, m_character.getPosition(), m_direction);
                        b.setVelocity(0.2f);
                        b.setPicture(R.drawable.bullet_circle_red);
                        break;
                    }
                    case NPC_MACHINEGUN: {
                        Unit b =Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.DEFAULT, m_power, m_character.getPosition(), m_direction);
                        b.setVelocity(0.2f);
                        b.setPicture(R.drawable.bullet_circle_red);
                        break;
                    }
                    case NPC_SHOTGUN: {
                        Vec2 d1 = new Vec2(m_direction);
                        Vec2 d2 = new Vec2(m_direction);
                        d1.rotate((float) Math.PI / 16f);
                        d2.rotate(-(float) Math.PI / 16f);
                        Unit b1 = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.DEFAULT, m_power, m_character.getPosition(), m_direction);
                        Unit b2 = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.DEFAULT, m_power, m_character.getPosition(), d1);
                        Unit b3 = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.DEFAULT, m_power, m_character.getPosition(), d2);
                        b1.setVelocity(0.2f);
                        b2.setVelocity(0.2f);
                        b3.setVelocity(0.2f);
                        b1.setPicture(R.drawable.bullet_circle_red);
                        b2.setPicture(R.drawable.bullet_circle_red);
                        b3.setPicture(R.drawable.bullet_circle_red);
                        break;
                    }
                    case NPC_MISSILE: {
                        Unit b = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.MISSILE, m_power, m_character.getPosition(), m_direction);
                        b.setVelocity(0.1f);
                        b.setPicture(R.drawable.bullet_missile_red);
                        break;
                    }
                    case NPC_GUIDEDMISSILE: {
                        Unit b = Game.INSTANCE.addBullet(m_character.getTeam(), Bullet.GUIDEDMISSILE, m_power, m_character.getPosition(), m_direction);
                        b.setVelocity(0.1f);
                        b.setPicture(R.drawable.bullet_missile_red);
                        break;
                    }
                }
                m_accumTime = 0f;
            }
        }
        else if(m_accumTime < m_fireRate){
            m_accumTime += delta;
            if(m_accumTime >= m_fireRate)
                m_accumTime = m_fireRate;
        }
    }
}