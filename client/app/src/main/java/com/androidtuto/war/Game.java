package com.androidtuto.war;

import android.content.Context;

import com.androidtuto.packet.Packet_Player_Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

/**
 * Created by 초록 on 2015-06-12.
 */
public enum Game {
    INSTANCE;

    static final int GAME_PVP = -1;

    static final float CAMERA_SPEED = 0.1f;

    Context m_context;
    
    Random m_random;

    HashMap<Integer, Unit> m_units;
    HashMap<Integer, Character> m_characters;
    HashMap<Integer, Bullet> m_bullets1;
    HashMap<Integer, Bullet> m_bullets2; // collidable
    HashMap<Integer, Unit> m_coins;
    TreeSet<Unit> m_drawables;

    ArrayList<Unit> m_reserveDeleteUnit;
    boolean m_bUpdating;

    Player m_player;
    Unit m_bg;

    HashMap<Integer, Player> m_enemyPlayer = new HashMap<Integer, Player>();

    int m_map;
    int m_exp;
    boolean m_bossKilled;
    Npc m_boss;

    Vec3 m_camera;

    Vec2 m_accumMove;
    Vec2 m_playerPosPast;
    float m_distGenNpc;

    public void init(Context context, Random random){

        m_context = context;
        m_random = random;

        m_units = new HashMap<Integer, Unit>();
        m_characters = new HashMap<Integer, Character>();
        m_bullets1 = new HashMap<Integer, Bullet>();
        m_bullets2 = new HashMap<Integer, Bullet>();
        m_coins = new HashMap<Integer, Unit>();
        m_drawables = new TreeSet<Unit>();

        m_reserveDeleteUnit = new ArrayList<Unit>();
        m_bUpdating = false;

        m_bg = new Unit(0f, 0f, Manager.MAP_WIDTH*2f, Manager.MAP_HEIGHT*2f, 0f, -1);

        createMap(User.INSTANCE.getMapMax() + 1);
    }

    public Player getPlayer(){return m_player;}

    public Player addEnemyPlayer(int id){
        Player p = createPlayer(id);
        m_enemyPlayer.put(id, p);
        return p;
    }

    public HashMap<Integer, Player> getEnemyPlayerAll(){
        return m_enemyPlayer;
    }

    public void clearEnemyPlayer(){
        m_enemyPlayer.clear();
    }

    public Player getEnemyPlayer(int id){
        Player p = m_enemyPlayer.get(id);
        if(p == null)
            p = addEnemyPlayer(id);

        return p;
    }

    public void deleteEnemyPlayer(int id){
        killCharacter(m_enemyPlayer.get(id));
        m_enemyPlayer.remove(id);
    }

    public HashMap<Integer, Character> getCharacters(){return m_characters;}
    public HashMap<Integer, Bullet> getBullets1(){return m_bullets1;}
    public HashMap<Integer, Bullet> getBullets2(){return m_bullets2;}

    public float getRandomFloat(float f1,float f2){
        if(f1 > f2)
            return getRandomFloat(f2, f1);
        else
            return m_random.nextFloat() * (f2-f1) + f1;
    }

    public int getRandomInt(int n1, int n2){
        return m_random.nextInt(n2-n1+1) + n1;
    }

    public Character getClosestEnemy(int team, final Vec2 pos, float range){

        Character result = null;
        float min = Float.MAX_VALUE;

        for(Character c : m_characters.values()){
            if(c.getTeam() != team) {
                if (Vec2.checkSqrAndDist(pos, c.getPosition(), range)) {
                    float x = Math.abs(pos.x - c.getPosition().x);
                    float y = Math.abs(pos.y - c.getPosition().y);
                    float length = Vec2.sqrt(x * x + y * y);
                    if (length < min) {
                        min = length;
                        result = c;
                    }
                }
            }
        }

        return result;
    }

    public Unit addBox(float x, float y, float xr, float yr, float angle, int picture)
    {
        Unit unit = new Unit(x, y, xr, yr, angle, picture);

        int index = Manager.INSTANCE.registerUnit(unit);
        m_drawables.add(unit);

        return unit;
    }

    public Bullet addBullet(int team, int type, float power, float fromX, float fromY, float dirX, float dirY)
    {
        /*if(dirX == 0f && dirY == 0f)
            return null;*/

        Bullet bullet = new Bullet(type, fromX, fromY, dirX, dirY);
        bullet.setPower(power);
        bullet.setTeam(team);

        int index = Manager.INSTANCE.registerUnit(bullet);
        m_units.put(index, bullet);
        if(type == Bullet.DEFAULT || type == Bullet.WAVE)
            m_bullets1.put(index, bullet);
        else if(type == Bullet.MISSILE || type == Bullet.GUIDEDMISSILE)
            m_bullets2.put(index, bullet);
        m_drawables.add(bullet);

        return bullet;
    }

    public Bullet addBullet(int team, int type, float power, Vec2 from, Vec2 dir){
        return addBullet(team, type, power, from.x, from.y, dir.x, dir.y);
    }

    /*static final int COIN_BRONZE = 0;
    static final int COIN_SILVER = 1;
    static final int COIN_GOLD = 2;
    static final int COIN_RAINBOW = 3;

    public Unit addCoin(int type, float x, float y){
        Unit coin = new Unit(x, y, 1f, 1f, 0f, -1);

        if(type == COIN_BRONZE){
            //coin.addPicture();
        }

        int index = Manager.INSTANCE.registerUnit(coin);
        m_units.put(index, coin);
        m_coins.put(index, coin);
        m_drawables.add(coin);

        return coin;
    }*/

    public void addCoin(Unit u, int num){

        for(int i=0; i<num; ++i)
        {
            Unit coin;
            if(num == 1)
                coin = new Unit(u.getPosition().x, u.getPosition().y, 0.4f, 0.4f, 0f, R.drawable.coin, 10);
            else if(num <= 10)
                coin = new Unit(u.getPosition().x + getRandomFloat(-1.5f, 1.5f), u.getPosition().y + getRandomFloat(-1.5f, 1.5f), 0.4f, 0.4f, 0f, R.drawable.coin, 10);
            else
                coin = new Unit(u.getPosition().x + getRandomFloat(-2.5f, 2.5f), u.getPosition().y + getRandomFloat(-2.5f, 2.5f), 0.4f, 0.4f, 0f, R.drawable.coin, 10);

            coin.setZ(4);
            coin.animatePicture(0.04f, true);

            int index = Manager.INSTANCE.registerUnit(coin);
            m_units.put(index, coin);
            m_coins.put(coin.getIndex(), coin);
            m_drawables.add(coin);

            coin.setCallbackTrace(new CallbackAnimation() {
                public void animationEnd(Unit u) {
                    User.INSTANCE.addCoin(m_map);
                    Game.INSTANCE.deleteUnit(u);
                }
            });
        }
    }

    public Npc addNpc(int type, int level, float x, float y, float radius, float velocity, float area){
        Npc n = new Npc(type, x, y, radius, velocity, area, -1, 0f);
        n.setTeam(-1);
        n.setZ(1);
        n.setFxMove(Unit.FX_MOVE_SHADOW);

        if(type == Npc.ZOMBIE) {
            n.setPicture(R.drawable.npc_zombie01 + (level > 20 ? level - 20 : level) - 1);
            switch(level){
                case 1: n.setHp(20f); break;
                case 2: n.setHp(40f); break;
                case 3: n.setHp(60f); break;
                case 4: n.setHp(80f); break;
                case 5: n.setHp(100f); break;
                case 6: n.setHp(120f); break;
                case 7: n.setHp(140f); break;
                case 8: n.setHp(160f); break;
                case 9: n.setHp(180f); break;
                case 10: n.setHp(200f); break;
                case 11: n.setHp(220f); break;
                case 12: n.setHp(240f); break;
                case 13: n.setHp(260f); break;
                case 14: n.setHp(280f); break;
                case 15: n.setHp(300f); break;
                case 16: n.setHp(320f); break;
                case 17: n.setHp(340f); break;
                case 18: n.setHp(360f); break;
                case 19: n.setHp(380f); break;
                case 20: n.setHp(800f); break;
                case 21: n.setHp(820f); break;
                case 22: n.setHp(840f); break;
                case 23: n.setHp(860f); break;
                case 24: n.setHp(880f); break;
                case 25: n.setHp(900f); break;
                case 26: n.setHp(920f); break;
                case 27: n.setHp(940f); break;
                case 28: n.setHp(960f); break;
                case 29: n.setHp(980f); break;
                case 30: n.setHp(1000f); break;
                case 31: n.setHp(1020f); break;
                case 32: n.setHp(1040f); break;
                case 33: n.setHp(1060f); break;
                case 34: n.setHp(1080f); break;
                case 35: n.setHp(1100f); break;
                case 36: n.setHp(1120f); break;
                case 37: n.setHp(1140f); break;
                case 38: n.setHp(1160f); break;
                case 39: n.setHp(1180f); break;
                case 40: n.setHp(2000f); break;
            }
        }
        else if(type == Npc.BOMBER) {
            n.setPicture(R.drawable.npc_bomber01 + (level > 20 ? level - 20 : level) - 1);
            switch(level) {
                case 1: n.setHp(10f); n.setPower(5f); break;
                case 2: n.setHp(20f); n.setPower(5f); break;
                case 3: n.setHp(30f); n.setPower(5f); break;
                case 4: n.setHp(40f); n.setPower(5f); break;
                case 5: n.setHp(50f); n.setPower(10f); break;
                case 6: n.setHp(60f); n.setPower(10f); n.setSeeForward(true); break;
                case 7: n.setHp(70f); n.setPower(10f); break;
                case 8: n.setHp(80f); n.setPower(10f); break;
                case 9: n.setHp(90f); n.setPower(10f); break;
                case 10: n.setHp(100f); n.setPower(20f); n.setSeeForward(true); break;
                case 11: n.setHp(110f); n.setPower(20f); n.setSeeForward(true); break;
                case 12: n.setHp(120f); n.setPower(20f); break;
                case 13: n.setHp(130f); n.setPower(20f); break;
                case 14: n.setHp(140f); n.setPower(20f); break;
                case 15: n.setHp(150f); n.setPower(30f); break;
                case 16: n.setHp(160f); n.setPower(30f); break;
                case 17: n.setHp(170f); n.setPower(30f); n.setSeeForward(true); break;
                case 18: n.setHp(180f); n.setPower(30f); n.setSeeForward(true); break;
                case 19: n.setHp(190f); n.setPower(30f); n.setSeeForward(true); break;
                case 20: n.setHp(400f); n.setPower(50f); break;
                case 21: n.setHp(410f); n.setPower(5f); break;
                case 22: n.setHp(420f); n.setPower(5f); break;
                case 23: n.setHp(430f); n.setPower(5f); break;
                case 24: n.setHp(440f); n.setPower(5f); break;
                case 25: n.setHp(450f); n.setPower(10f); break;
                case 26: n.setHp(460f); n.setPower(10f); n.setSeeForward(true); break;
                case 27: n.setHp(470f); n.setPower(10f); break;
                case 28: n.setHp(480f); n.setPower(10f); break;
                case 29: n.setHp(490f); n.setPower(10f); break;
                case 30: n.setHp(500f); n.setPower(20f); n.setSeeForward(true); break;
                case 31: n.setHp(510f); n.setPower(20f); n.setSeeForward(true); break;
                case 32: n.setHp(520f); n.setPower(20f); break;
                case 33: n.setHp(530f); n.setPower(20f); break;
                case 34: n.setHp(540f); n.setPower(20f); break;
                case 35: n.setHp(550f); n.setPower(30f); break;
                case 36: n.setHp(560f); n.setPower(30f); break;
                case 37: n.setHp(570f); n.setPower(30f); n.setSeeForward(true); break;
                case 38: n.setHp(580f); n.setPower(30f); n.setSeeForward(true); break;
                case 39: n.setHp(590f); n.setPower(30f); n.setSeeForward(true); break;
                case 40: n.setHp(1200f); n.setPower(50f); break;
            }
        }
        else {
            n.setPicture(R.drawable.npc_shooter01 + (level > 20 ? level - 20 : level) - 1);
            switch(level) {
                case 1: n.setHp(40f); n.addWeapon(Weapon.NPC_PISTOL, 1); break;
                case 2: n.setHp(80f); n.addWeapon(Weapon.NPC_PISTOL, 1); break;
                case 3: n.setHp(120f); n.addWeapon(Weapon.NPC_PISTOL, 1); break;
                case 4: n.setHp(160f); n.addWeapon(Weapon.NPC_PISTOL, 1); break;
                case 5: n.setHp(200f); n.addWeapon(Weapon.NPC_SHOTGUN, 1); break;
                case 6: n.setHp(240f); n.addWeapon(Weapon.NPC_SHOTGUN, 1); break;
                case 7: n.setHp(280f); n.addWeapon(Weapon.NPC_SHOTGUN, 1); break;
                case 8: n.setHp(320f); n.addWeapon(Weapon.NPC_SHOTGUN, 1); break;
                case 9: n.setHp(360f); n.addWeapon(Weapon.NPC_SHOTGUN, 1); break;
                case 10: n.setHp(400f); n.addWeapon(Weapon.NPC_MACHINEGUN, 1); break;
                case 11: n.setHp(440f); n.addWeapon(Weapon.NPC_MACHINEGUN, 1); break;
                case 12: n.setHp(480f); n.addWeapon(Weapon.NPC_MACHINEGUN, 1); break;
                case 13: n.setHp(520f); n.addWeapon(Weapon.NPC_MACHINEGUN, 1); break;
                case 14: n.setHp(560f); n.addWeapon(Weapon.NPC_MACHINEGUN, 1); break;
                case 15: n.setHp(600f); n.addWeapon(Weapon.NPC_MISSILE, 1); break;
                case 16: n.setHp(640f); n.addWeapon(Weapon.NPC_MISSILE, 1); break;
                case 17: n.setHp(680f); n.addWeapon(Weapon.NPC_MISSILE, 1); break;
                case 18: n.setHp(720f); n.addWeapon(Weapon.NPC_MISSILE, 1); break;
                case 19: n.setHp(760f); n.addWeapon(Weapon.NPC_MISSILE, 1); break;
                case 20: n.setHp(1600f); n.addWeapon(Weapon.NPC_GUIDEDMISSILE, 1); break;
                case 21: n.setHp(1640f); n.addWeapon(Weapon.NPC_PISTOL, 2); break;
                case 22: n.setHp(1680f); n.addWeapon(Weapon.NPC_PISTOL, 2); break;
                case 23: n.setHp(1720f); n.addWeapon(Weapon.NPC_PISTOL, 2); break;
                case 24: n.setHp(1760f); n.addWeapon(Weapon.NPC_PISTOL, 2); break;
                case 25: n.setHp(1800f); n.addWeapon(Weapon.NPC_SHOTGUN, 2); break;
                case 26: n.setHp(1840f); n.addWeapon(Weapon.NPC_SHOTGUN, 2); break;
                case 27: n.setHp(1880f); n.addWeapon(Weapon.NPC_SHOTGUN, 2); break;
                case 28: n.setHp(1920f); n.addWeapon(Weapon.NPC_SHOTGUN, 2); break;
                case 29: n.setHp(1960f); n.addWeapon(Weapon.NPC_SHOTGUN, 2); break;
                case 30: n.setHp(2000f); n.addWeapon(Weapon.NPC_MACHINEGUN, 2); break;
                case 31: n.setHp(2040f); n.addWeapon(Weapon.NPC_MACHINEGUN, 2); break;
                case 32: n.setHp(2080f); n.addWeapon(Weapon.NPC_MACHINEGUN, 2); break;
                case 33: n.setHp(2120f); n.addWeapon(Weapon.NPC_MACHINEGUN, 2); break;
                case 34: n.setHp(2160f); n.addWeapon(Weapon.NPC_MACHINEGUN, 2); break;
                case 35: n.setHp(2200f); n.addWeapon(Weapon.NPC_MISSILE, 2); break;
                case 36: n.setHp(2240f); n.addWeapon(Weapon.NPC_GUIDEDMISSILE, 1); break;
                case 37: n.setHp(2280f); n.addWeapon(Weapon.NPC_GUIDEDMISSILE, 1); break;
                case 38: n.setHp(2320f); n.addWeapon(Weapon.NPC_GUIDEDMISSILE, 2); break;
                case 39: n.setHp(2360f); n.addWeapon(Weapon.NPC_GUIDEDMISSILE, 2); break;
                case 40: n.setHp(4500f); n.addWeapon(Weapon.NPC_GUIDEDMISSILE, 2); break;
            }
        }

        int index = Manager.INSTANCE.registerUnit(n);
            m_units.put(index, n);
        m_characters.put(index, n);
        m_drawables.add(n);

        return n;
    }

    public int getMap(){
        return m_map;
    }

    public int getExp(){
        return m_exp;
    }
    
    public int getExpMax(){
        switch(m_map){
            case 1: return 60;
            case 2: return 120;
            case 3: return 160;
            case 4: return 220;
            case 5: return 250;
            case 6: return 250;
            case 7: return 250;
            case 8: return 250;
            case 9: return 250;
            case 10: return 300;
            case 11: return 300;
            case 12: return 300;
            case 13: return 300;
            case 14: return 300;
            case 15: return 400;
            case 16: return 400;
            case 17: return 400;
            case 18: return 400;
            case 19: return 400;
            case 20: return 400;
            case 21: return 200;
            case 22: return 200;
            case 23: return 200;
            case 24: return 220;
            case 25: return 250;
            case 26: return 250;
            case 27: return 250;
            case 28: return 250;
            case 29: return 250;
            case 30: return 300;
            case 31: return 300;
            case 32: return 300;
            case 33: return 300;
            case 34: return 300;
            case 35: return 400;
            case 36: return 400;
            case 37: return 400;
            case 38: return 400;
            case 39: return 400;
            case 40: return 400;
        }
        return 1;
    }
    
    public boolean isCleared(){
        return m_exp >= getExpMax();
    }

    public Npc getBoss(){
        return m_boss;
    }

    public void setBossKilled(){
        m_boss = null;
        m_bossKilled = true;

        User.INSTANCE.setMapMax(m_map);
        Manager.INSTANCE.showStageClear();
        Manager.INSTANCE.highlightMap(true);
    }

    public void addNpcGroup(float x, float y){

        switch(m_map){
            case 1:
                if(m_bossKilled == false && isCleared() && m_boss == null) {
                    m_boss = addNpc(Npc.SHOOTER, 1, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 3f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.ZOMBIE, 1, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.BOMBER, 1, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.07f, 0.07f), 2f);
                    if(getRandomFloat(0f, 1f) <= 0.1f) addNpc(Npc.SHOOTER, 1, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 2:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 2, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 4f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 8); ++i) addNpc(Npc.ZOMBIE, 2, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.BOMBER, 2, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.07f, 0.07f), 2f);
                    if(getRandomFloat(0f, 1f) <= 0.2f) addNpc(Npc.SHOOTER, 1, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 3:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 3, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 5f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.ZOMBIE, 1, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 6); ++i) addNpc(Npc.ZOMBIE, 3, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 3, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.07f, 0.07f), 2f);
                    if(getRandomFloat(0f, 1f) <= 0.2f) addNpc(Npc.SHOOTER, 2, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 4:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 4, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 5f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 10); ++i) addNpc(Npc.ZOMBIE, 4, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 4, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    if(getRandomFloat(0f, 1f) <= 0.2f) addNpc(Npc.SHOOTER, 3, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 5:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 5, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 6f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.ZOMBIE, 2, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 13); ++i) addNpc(Npc.ZOMBIE, 5, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.BOMBER, 1, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 6); ++i) addNpc(Npc.BOMBER, 5, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 4, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 6:
                if(m_bossKilled == false && isCleared() && m_boss == null) {
                    m_boss = addNpc(Npc.BOMBER, 6, x, y, 2f, getRandomFloat(0.08f, 0.08f), 2f);
                    m_boss.setPower(1000f);
                    m_boss.setHp(600f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 15); ++i) addNpc(Npc.BOMBER, 6, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                }
                break;
            case 7:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 7, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 7f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 12); ++i) addNpc(Npc.ZOMBIE, 7, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.BOMBER, 7, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 4, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 6, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 8:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 8, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 7f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.ZOMBIE, 5, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 11); ++i) addNpc(Npc.ZOMBIE, 8, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.BOMBER, 8, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 4, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 7, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 9:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 9, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 8f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 13); ++i) addNpc(Npc.ZOMBIE, 9, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.BOMBER, 9, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 4, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 8, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 10:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 10, x, y, 2f, 0f, 0f);
                    m_boss.getWeapon(0).setFireRate(600f);
                    m_boss.setHp(m_boss.getHp() * 10f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.ZOMBIE, 9, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 18); ++i) addNpc(Npc.ZOMBIE, 10, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 5, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 4, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 9, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 11:
                if(m_bossKilled == false && isCleared() && m_boss == null) {
                    m_boss = addNpc(Npc.BOMBER, 11, x, y, 2f, getRandomFloat(0.08f, 0.08f), 2f);
                    m_boss.setPower(1000f);
                    m_boss.setHp(1500f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 6, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 15); ++i) addNpc(Npc.BOMBER, 11, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                }
                break;
            case 12:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 12, x, y, 2f, 0f, 0f);
                    m_boss.getWeapon(0).setFireRate(600f);
                    m_boss.setHp(m_boss.getHp() * 10f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.ZOMBIE, 10, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 16); ++i) addNpc(Npc.ZOMBIE, 12, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 7); ++i) addNpc(Npc.BOMBER, 12, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 9, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 11, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 13:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 13, x, y, 2f, 0f, 0f);
                    m_boss.getWeapon(0).setFireRate(600f);
                    m_boss.setHp(m_boss.getHp() * 12f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 12, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.SHOOTER, 4, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 9, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 12, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 14:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 14, x, y, 2f, 0f, 0f);
                    m_boss.getWeapon(0).setFireRate(600f);
                    m_boss.setHp(m_boss.getHp() * 12f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.ZOMBIE, 12, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 17); ++i) addNpc(Npc.ZOMBIE, 14, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 12, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 9, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 13, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 15:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 15, x, y, 2f, 0f, 0f);
                    m_boss.addWeapon(Weapon.NPC_SHOTGUN, 1);
                    m_boss.setHp(m_boss.getHp() * 12f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.ZOMBIE, 14, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 17); ++i) addNpc(Npc.ZOMBIE, 15, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 7); ++i) addNpc(Npc.BOMBER, 15, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 9, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 14, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 16:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 16, x, y, 2f, 0f, 0f);
                    m_boss.addWeapon(Weapon.NPC_SHOTGUN, 1);
                    m_boss.setHp(m_boss.getHp() * 15f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 25); ++i) addNpc(Npc.ZOMBIE, 16, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 14, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 17:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 17, x, y, 2f, 0f, 0f);
                    m_boss.addWeapon(Weapon.NPC_SHOTGUN, 1);
                    m_boss.setHp(m_boss.getHp() * 16f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.ZOMBIE, 16, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 20); ++i) addNpc(Npc.ZOMBIE, 17, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 7); ++i) addNpc(Npc.BOMBER, 15, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 14, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 16, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 18:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 18, x, y, 2f, 0f, 0f);
                    m_boss.addWeapon(Weapon.NPC_SHOTGUN, 1);
                    m_boss.setHp(m_boss.getHp() * 18f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 20); ++i) addNpc(Npc.ZOMBIE, 18, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 7); ++i) addNpc(Npc.BOMBER, 18, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 9, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 14, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 17, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 19:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 19, x, y, 2f, 0f, 0f);
                    m_boss.addWeapon(Weapon.NPC_SHOTGUN, 1);
                    m_boss.setHp(m_boss.getHp() * 19f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.ZOMBIE, 18, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 20); ++i) addNpc(Npc.ZOMBIE, 19, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 7); ++i) addNpc(Npc.BOMBER, 19, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 9, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 14, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 18, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 20:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 20, x, y, 2f, 0f, 0f);
                    m_boss.addWeapon(Weapon.NPC_SHOTGUN, 1);
                    m_boss.setHp(m_boss.getHp() * 20f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.ZOMBIE, 19, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 20); ++i) addNpc(Npc.ZOMBIE, 20, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 10); ++i) addNpc(Npc.BOMBER, 20, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 9, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.SHOOTER, 14, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.SHOOTER, 19, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 21:
                if(m_bossKilled == false && isCleared() && m_boss == null) {
                    m_boss = addNpc(Npc.SHOOTER, 21, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 3f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 15); ++i) addNpc(Npc.ZOMBIE, 21, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 21, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.07f, 0.07f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 21, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 22:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 22, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 4f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 18); ++i) addNpc(Npc.ZOMBIE, 22, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 22, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.07f, 0.07f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 21, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 23:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 23, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 5f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.ZOMBIE, 21, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 16); ++i) addNpc(Npc.ZOMBIE, 23, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 23, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.07f, 0.07f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 22, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 24:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 24, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 5f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 15); ++i) addNpc(Npc.ZOMBIE, 24, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 24, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 23, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 25:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 25, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 6f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.ZOMBIE, 22, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 17); ++i) addNpc(Npc.ZOMBIE, 25, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.BOMBER, 21, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 6); ++i) addNpc(Npc.BOMBER, 25, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 24, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 26:
                if(m_bossKilled == false && isCleared() && m_boss == null) {
                    m_boss = addNpc(Npc.BOMBER, 26, x, y, 2f, getRandomFloat(0.08f, 0.08f), 2f);
                    m_boss.setPower(1000f);
                    m_boss.setHp(5000f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 20); ++i) addNpc(Npc.BOMBER, 26, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                }
                break;
            case 27:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 27, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 7f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 15); ++i) addNpc(Npc.ZOMBIE, 27, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 27, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 24, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 26, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 28:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 28, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 7f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.ZOMBIE, 25, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 15); ++i) addNpc(Npc.ZOMBIE, 28, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 6); ++i) addNpc(Npc.BOMBER, 28, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 24, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 27, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 29:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 29, x, y, 2f, 0f, 0f);
                    m_boss.setHp(m_boss.getHp() * 8f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 16); ++i) addNpc(Npc.ZOMBIE, 29, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 6); ++i) addNpc(Npc.BOMBER, 29, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 24, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 28, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 30:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 30, x, y, 2f, 0f, 0f);
                    m_boss.getWeapon(0).setFireRate(600f);
                    m_boss.setHp(m_boss.getHp() * 10f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.ZOMBIE, 29, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 20); ++i) addNpc(Npc.ZOMBIE, 30, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 7); ++i) addNpc(Npc.BOMBER, 25, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 24, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 29, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 31:
                if(m_bossKilled == false && isCleared() && m_boss == null) {
                    m_boss = addNpc(Npc.BOMBER, 31, x, y, 2f, getRandomFloat(0.08f, 0.08f), 2f);
                    m_boss.setPower(1000f);
                    m_boss.setHp(12000f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 26, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 20); ++i) addNpc(Npc.BOMBER, 31, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                }
                break;
            case 32:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 32, x, y, 2f, 0f, 0f);
                    m_boss.getWeapon(0).setFireRate(600f);
                    m_boss.setHp(m_boss.getHp() * 10f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.ZOMBIE, 30, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 20); ++i) addNpc(Npc.ZOMBIE, 32, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 7); ++i) addNpc(Npc.BOMBER, 32, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 29, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 31, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 33:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 33, x, y, 2f, 0f, 0f);
                    m_boss.getWeapon(0).setFireRate(600f);
                    m_boss.setHp(m_boss.getHp() * 12f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.BOMBER, 32, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.SHOOTER, 24, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.SHOOTER, 29, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.SHOOTER, 32, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 34:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 34, x, y, 2f, 0f, 0f);
                    m_boss.getWeapon(0).setFireRate(600f);
                    m_boss.setHp(m_boss.getHp() * 12f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.ZOMBIE, 32, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 20); ++i) addNpc(Npc.ZOMBIE, 34, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 8); ++i) addNpc(Npc.BOMBER, 32, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 29, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 33, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 35:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 35, x, y, 2f, 0f, 0f);
                    m_boss.addWeapon(Weapon.NPC_SHOTGUN, 1);
                    m_boss.setHp(m_boss.getHp() * 12f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.ZOMBIE, 34, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 20); ++i) addNpc(Npc.ZOMBIE, 35, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 8); ++i) addNpc(Npc.BOMBER, 35, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 29, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 34, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 36:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 36, x, y, 2f, 0f, 0f);
                    m_boss.addWeapon(Weapon.NPC_SHOTGUN, 1);
                    m_boss.setHp(m_boss.getHp() * 15f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 25); ++i) addNpc(Npc.ZOMBIE, 36, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.SHOOTER, 34, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 37:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 37, x, y, 2f, 0f, 0f);
                    m_boss.addWeapon(Weapon.NPC_SHOTGUN, 1);
                    m_boss.setHp(m_boss.getHp() * 16f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.ZOMBIE, 36, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 20); ++i) addNpc(Npc.ZOMBIE, 37, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 7); ++i) addNpc(Npc.BOMBER, 35, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.SHOOTER, 34, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.SHOOTER, 36, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 38:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 38, x, y, 2f, 0f, 0f);
                    m_boss.addWeapon(Weapon.NPC_SHOTGUN, 1);
                    m_boss.setHp(m_boss.getHp() * 18f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 22); ++i) addNpc(Npc.ZOMBIE, 38, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 7); ++i) addNpc(Npc.BOMBER, 38, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 1); ++i) addNpc(Npc.SHOOTER, 29, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 34, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 37, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 39:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 39, x, y, 2f, 0f, 0f);
                    m_boss.addWeapon(Weapon.NPC_SHOTGUN, 1);
                    m_boss.setHp(m_boss.getHp() * 19f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.ZOMBIE, 38, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 22); ++i) addNpc(Npc.ZOMBIE, 39, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 7); ++i) addNpc(Npc.BOMBER, 39, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 29, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 2); ++i) addNpc(Npc.SHOOTER, 34, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.SHOOTER, 38, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
            case 40:
                if(m_bossKilled == false && isCleared() && m_boss == null){
                    m_boss = addNpc(Npc.SHOOTER, 40, x, y, 2f, 0f, 0f);
                    m_boss.addWeapon(Weapon.NPC_SHOTGUN, 1);
                    m_boss.setHp(m_boss.getHp() * 20f);
                }
                else{
                    final float RADIUS = 3f;
                    for(int i=0; i<getRandomInt(0, 5); ++i) addNpc(Npc.ZOMBIE, 39, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 25); ++i) addNpc(Npc.ZOMBIE, 40, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 10); ++i) addNpc(Npc.BOMBER, 40, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.1f, 0.1f), 2f);
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.SHOOTER, 29, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 3); ++i) addNpc(Npc.SHOOTER, 34, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                    for(int i=0; i<getRandomInt(0, 4); ++i) addNpc(Npc.SHOOTER, 39, getRandomFloat(x - RADIUS, x + RADIUS), getRandomFloat(y - RADIUS, y + RADIUS), 1f, getRandomFloat(0.02f, 0.02f), 2f);
                }
                break;
        }
    }

    public void addNpcGroup(){
        m_accumMove.normalize();

        for(int i=0; i<getRandomInt(1, 3); ++i)
        {
            Vec2 v = new Vec2(m_accumMove.x, m_accumMove.y);
            v.rotate(getRandomFloat(-(float)Math.PI / 6f, (float)Math.PI / 6f));

            float dist = Manager.MAP_WIDTH + 5.5f;
            v.x *= dist;
            v.y *= dist;

            addNpcGroup(m_player.getPosition().x + v.x, m_player.getPosition().y + v.y);
        }
    }

    public Unit addFx(float x, float y, float sx, float sy, float alphaStart, float alphaEnd, float alphaInterval, int picture)
    {
        Unit fx = new Unit(x, y, sx, sy, 0f, picture);
        fx.setZ(0);

        int index = Manager.INSTANCE.registerUnit(fx);
        m_units.put(index, fx);
        m_drawables.add(fx);

        //fx.animateSize(s, s / 2f, alphaInterval);
        fx.animateAlpha(alphaStart, alphaEnd, alphaInterval);
        fx.setCallbackAniAlpha(new CallbackAnimation() {
            public void animationEnd(Unit u) {
                deleteUnit(u);
                Manager.INSTANCE.deleteUnit(u);
            }
        });

        return fx;
    }

    public Unit addFx(float x, float y, float s, int tex, int numTex, float interval)
    {
        Unit u = new Unit(x, y, s, s, 0f, tex, numTex);
        u.setZ(4);

        int index = Manager.INSTANCE.registerUnit(u);
        m_units.put(index, u);
        m_drawables.add(u);

        u.animatePicture(interval, false);
        u.setCallbackAniPicture(new CallbackAnimation() {
            public void animationEnd(Unit u) {
                deleteUnit(u);
                Manager.INSTANCE.deleteUnit(u);
            }
        });

        return u;
    }

    public Unit addFx(float x, float y, float s, int tex, int numTex)
    {
        return addFx(x, y, s, tex, numTex, 0.05f);
    }

    public void killCharacter(Unit u){

        if(u == getPlayer()){
            if(Manager.INSTANCE.isPVPOn()){
                Manager.INSTANCE.setPVP(false);
                Unit white = Manager.INSTANCE.getClearImage();
                white.animateAlpha(1f, 0.001f);
                white.setCallbackAniAlpha(new CallbackAnimation() {
                    public void animationEnd(Unit u) {
                        Manager.INSTANCE.setPVP(true);
                        u.animateAlpha(0f, -0.001f);
                        u.setCallbackAniAlpha(null);
                    }
                });
            }
            else {
                Unit white = Manager.INSTANCE.getClearImage();
                white.animateAlpha(1f, 0.001f);
                white.setCallbackAniAlpha(new CallbackAnimation() {
                    public void animationEnd(Unit u) {
                        Game.INSTANCE.createMap(Game.INSTANCE.getMap());
                        u.animateAlpha(0f, -0.001f);
                        u.setCallbackAniAlpha(null);
                    }
                });
            }

            return;
        }
        else if(u instanceof Npc || u instanceof Player) {

            int coin = 0;

            if(u instanceof Npc) {
                if (m_boss == u) {
                    setBossKilled();
                    coin = 15;
                } else if (((Npc) u).getType() == Npc.SHOOTER)
                    coin = 3;
                else if (getRandomInt(1, 10) <= 8)
                    coin = 1;
            }
            else{
                coin = 20;
            }

            Game.INSTANCE.addFx(u.getPosition().x, u.getPosition().y, u.getSize().x, R.drawable.fx_explosion1, 42);

            if(coin > 0)
                addCoin(u, coin);

            ++m_exp;
            if (m_exp > getExpMax())
                m_exp = getExpMax();

            Manager.INSTANCE.updateStatus(m_player.getHp() / m_player.getHpMax(), m_exp / (float) getExpMax());
        }

        deleteUnit(u);
    }

    public void deleteUnit(Unit u)
    {
        if(u == null)
            return;

        Manager.INSTANCE.deleteUnit(u);

        if(m_bUpdating)
            m_reserveDeleteUnit.add(u);
        else {
            m_units.remove(u.getIndex());
            m_characters.remove(u.getIndex());
            m_bullets1.remove(u.getIndex());
            m_bullets2.remove(u.getIndex());
            m_drawables.remove(u);
            m_enemyPlayer.remove(u);

            boolean b1 = m_coins.isEmpty();
            m_coins.remove(u.getIndex());
            boolean b2 = m_coins.isEmpty();
            if(b1 != b2)
                User.INSTANCE.writeUserData();
        }
    }

    public boolean isExist(Unit u){
        return m_units.containsKey(u.getIndex());
    }

    public void createMap(int map)
    {
        if(map > 40 )
            map = 40;

        for(Unit u : m_units.values())
            Manager.INSTANCE.deleteUnit(u);

        Manager.INSTANCE.highlightMap(false);

        m_units.clear();
        m_characters.clear();
        m_bullets1.clear();
        m_bullets2.clear();
        m_coins.clear();
        m_drawables.clear();

        m_reserveDeleteUnit.clear();
        m_bUpdating = false;

        m_exp = 0;
        m_bossKilled = false;
        m_boss = null;

        m_camera = new Vec3(0f, 0f, Manager.INSTANCE.MAP_WIDTH);
        m_accumMove = new Vec2();
        m_playerPosPast = new Vec2();
        m_distGenNpc = 7f;

        m_map = map;
        m_bg.setPicture(R.drawable.bg_game01);

        // create player
        m_player = createPlayer(1);
        m_player.setWeaponMain(User.INSTANCE.getWeaponMain(), User.INSTANCE.getWeaponLevel());

        // initialize player movement info
        m_playerPosPast = new Vec2(0f, 0f);
        m_accumMove = new Vec2(0f, 0f);

        Manager.INSTANCE.highlightMap(false);

        // tutorial
        if(map == 1 && User.INSTANCE.getCoin() == 0){
        }
    }

    public Player createPlayer(int team){
        Player p = new Player(0, 0, 1f, 0.12f);

        p.setHp(100);
        p.setZ(1);

        int index = Manager.INSTANCE.registerUnit(p);
        p.setTeam(team);
        m_units.put(index, p);
        m_characters.put(index, p);
        m_drawables.add(p);

        p.setFxMove(Unit.FX_MOVE_PLAYER);

        return p;
    }

    public void setUpdating(boolean b){
        m_bUpdating = b;
        if(b == false && m_reserveDeleteUnit.size() > 0){
            if(m_reserveDeleteUnit.size() > 0){
                for(Unit u : m_reserveDeleteUnit)
                    deleteUnit(u);
                m_reserveDeleteUnit.clear();
            }
        }
    }

    private final float MAPMAX_X = Manager.MAP_WIDTH * 39f;
    private final float MAPMAX_Y = Manager.MAP_HEIGHT * 39f;

    private final float INTERVAL_PLAYERUPDATE = 0f;
    private float m_accumPlayerUpdate = 0f;

    public void update(float delta)
    {
        setUpdating(true);

        try {
            if (Manager.INSTANCE.isPVPOn()) {
                m_accumPlayerUpdate += delta;
                if (m_accumPlayerUpdate > INTERVAL_PLAYERUPDATE) {
                    Packet_Player_Update p = new Packet_Player_Update();
                    Vec2 pos = m_player.getPosition();
                    Vec2 dir = m_player.getDirection();
                    p.px = pos.x;
                    p.py = pos.y;
                    p.dx = dir.x;
                    p.dy = dir.y;
                    p.v = m_player.getVelocity();
                    Network.INSTANCE.write(p);

                    m_accumPlayerUpdate = 0f;
                }
            }

            m_camera.x += (m_player.getPosition().x - m_camera.x) * CAMERA_SPEED;
            m_camera.y += (m_player.getPosition().y - m_camera.y) * CAMERA_SPEED;

            if (Math.abs(m_camera.x) >= MAPMAX_X || Math.abs(m_camera.y) >= MAPMAX_Y) {
                for (Unit u : m_units.values()) {
                    Vec2 p = new Vec2(u.getPosition());
                    p.x -= m_camera.x;
                    p.y -= m_camera.y;
                    u.setPosition(p);
                }
                m_camera.x = 0f;
                m_camera.y = 0f;
            }

            for (Unit c : m_coins.values()) {
                c.traceStart(m_player.getPosition(), 0.008f, Unit.TRACE_LINE);
            }

            for (Character c : m_characters.values()) {
                if (c instanceof Npc && ((Npc) c).getType() == Npc.BOMBER && c.checkCollide(m_player)) {
                    m_player.damage(c.getPower());
                    if (c != m_boss)
                        c.damage(Float.MAX_VALUE);
                }
            }

            for (Bullet b : m_bullets1.values()) {
                if (b.getTeam() < 0) {
                    if (b.checkCollide(m_player)) {
                        m_player.damage(b.getPower());
                        deleteUnit(b);
                    }
                } else {
                    for (Character c : m_characters.values()) {
                        if (b.isEnemy(c) && b.checkCollide(c)) {
                            c.damage(b.getPower());
                            deleteUnit(b);
                        }
                    }
                }
            }

            for (Bullet b2 : m_bullets2.values()) {
                if (b2.getTeam() < 0) {
                    if (b2.checkCollide(m_player))
                        b2.damage(1);
                } else {
                    for (Character c : m_characters.values()) {
                        if (b2.isEnemy(c) && b2.checkCollide(c))
                            b2.damage(Float.MAX_VALUE);
                    }
                }

                for (Bullet b1 : m_bullets1.values()) {
                    if (b1.isEnemy(b2) && b1.checkCollide(b2)) {
                        b2.damage(1);
                        deleteUnit(b1);
                    }
                }

                for (Bullet b1 : m_bullets2.values()) {
                    if (b1.isEnemy(b2) && b1.checkCollide(b2)) {
                        b1.damage(1);
                        b2.damage(1);
                    }
                }
            }

            m_accumMove.x += m_player.getPosition().x - m_playerPosPast.x;
            m_accumMove.y += m_player.getPosition().y - m_playerPosPast.y;

            if (Math.abs(m_accumMove.x) > m_distGenNpc || Math.abs(m_accumMove.y) > m_distGenNpc) {
                addNpcGroup();
                m_accumMove = new Vec2(0f, 0f);
            }

            for (Unit u : m_units.values()) {
                if (u instanceof Player || u == m_boss)
                    continue;

                if (Math.abs(m_player.getPosition().x - u.getPosition().x) > 20f ||
                        Math.abs(m_player.getPosition().y - u.getPosition().y) > 20f)
                    deleteUnit(u);
            }

            m_playerPosPast.x = m_player.getPosition().x;
            m_playerPosPast.y = m_player.getPosition().y;

        }catch(Exception e){}

        // ending
        setUpdating(false);
    }

    public void draw(float [] m, float delta)
    {
        m_bg.draw(m, delta, -m_camera.x / 40f, -m_camera.y / 40f, m_bg.getSize().x, m_bg.getSize().y);


        for(Unit u : m_drawables){
            boolean result = u.draw(m, delta, m_camera);

            //if(result == false && u instanceof Npc){
            if(result == false && (u == m_boss || (u instanceof Player && u != m_player))){
                final float SPACE = 1f;

                float x = u.getPosition().x;
                float y = u.getPosition().y;
                int xpos = 0;
                int ypos = 0;
                float angle = 0f;

                if(x < m_camera.x-Manager.MAP_WIDTH + SPACE) {
                    x = m_camera.x-Manager.MAP_WIDTH + SPACE;
                    xpos = -1;
                }
                else if(m_camera.x+Manager.MAP_WIDTH - SPACE < x) {
                    x = m_camera.x+Manager.MAP_WIDTH - SPACE;
                    xpos = 1;
                }
                if(y < m_camera.y-Manager.MAP_HEIGHT + SPACE) {
                    y = m_camera.y-Manager.MAP_HEIGHT + SPACE;
                    ypos = -1;
                }
                else if(m_camera.y+Manager.MAP_HEIGHT - SPACE < y) {
                    y = m_camera.y+Manager.MAP_HEIGHT - SPACE;
                    ypos = 1;
                }

                if(!(xpos == 0 && ypos == 0)) {
                    if (xpos == 0 && ypos == 1) angle = 0f;
                    else if (xpos == 1 && ypos == 1) angle = -(float) Math.PI / 4f;
                    else if (xpos == 1 && ypos == 0) angle = -(float) Math.PI / 2f;
                    else if (xpos == 1 && ypos == -1) angle = -(float) Math.PI * 0.75f;
                    else if (xpos == 0 && ypos == -1) angle = -(float) Math.PI;
                    else if (xpos == -1 && ypos == -1) angle = (float) Math.PI * 0.75f;
                    else if (xpos == -1 && ypos == 0) angle = (float) Math.PI / 2f;
                    else if (xpos == -1 && ypos == 1) angle = (float) Math.PI / 4f;

                    /*float size = (Manager.MAP_WIDTH*3f - Vec2.length(u.getPosition(), m_camera.getVec2())) / (Manager.MAP_WIDTH*3f);
                    if(size < 0.3f)
                        size = 0.3f;*/
                    float size = 1f;

                    int picture = R.drawable.arrow_shooter;
                    /*if (n.getType() == Npc.ZOMBIE) picture = R.drawable.arrow_zombie;
                    else if (n.getType() == Npc.BOMBER) picture = R.drawable.arrow_bomber;
                    else if (n.getType() == Npc.SHOOTER) picture = R.drawable.arrow_shooter;*/
                    Unit arrow = new Unit(x, y, size, size, angle, picture);
                    arrow.draw(m, delta, m_camera);
                }
            }
        }
    }

    public void playerFire(float x, float y)
    {
        if(Manager.INSTANCE.isPVPOn() == false && m_player.getWeaponMain().isAssisted()) {
            Vec2 dir1 = new Vec2(x, y);
            dir1.normalize();

            Vec2 dir2 = new Vec2(-dir1.y, dir1.x);
            Vec2 dir3 = new Vec2(dir1.y, -dir1.x);

            float length = Manager.INSTANCE.MAP_WIDTH;

            Vec2 p1 = new Vec2(m_player.getPosition());
            Vec2 p2 = new Vec2(p1.x + (dir1.x * length) + (dir2.x * length), p1.y + (dir1.y * length) + (dir2.y * length));
            Vec2 p3 = new Vec2(p1.x + (dir1.x * length) + (dir3.x * length), p1.y + (dir1.y * length) + (dir3.y * length));

            ArrayList<Unit> arr = new ArrayList<Unit>();

            for (Unit u : m_characters.values()) {
                if(u == m_player)
                    continue;

                if (Vec2.checkTriangle(p1, p2, p3, u.getPosition()))
                    arr.add(u);
            }

            for (Unit u : m_bullets2.values()) {
                if (Vec2.checkTriangle(p1, p2, p3, u.getPosition()))
                    arr.add(u);
            }

            if(arr.size() > 0) {
                float min = Float.MAX_VALUE;
                Unit dest = null;

                for (Unit u : arr) {
                    float dist = Vec2.length(u.getPosition(), m_player.getPosition());
                    if (dist < min) {
                        min = dist;
                        dest = u;
                    }
                }

                x = dest.getPosition().x - m_player.getPosition().x;
                y = dest.getPosition().y - m_player.getPosition().y;
                m_player.getWeaponMain().setDestUnit(dest);
            }
        }

        m_player.getWeaponMain().touchDown(x, y);
    }
}
