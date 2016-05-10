package com.androidtuto.war;

/**
 * Created by 초록 on 2015-06-19.
 */
public class Player extends Character {

    Weapon m_weaponMain;
    Weapon m_weaponSub;

    Player(){
        super();

        m_hpRecover = 0.002f;
        m_weaponMain = new Weapon(this, Weapon.MAIN_PISTOL, 1);
        m_weaponSub = new Weapon(this, Weapon.MAIN_PISTOL, 1);
    }

    Player(float x, float y, float radius, float velocity){
        super(x, y, radius, velocity);

        m_hpRecover = 0.003f;
        m_weaponMain = new Weapon(this, Weapon.MAIN_PISTOL, 1);
        m_weaponSub = new Weapon(this, Weapon.MAIN_PISTOL, 1);
    }

    public Weapon getWeaponMain(){return m_weaponMain;}
    public Weapon getWeaponSub(){return m_weaponSub;}

    public void setWeaponMain(int type, int level){
        m_weaponMain = new Weapon(this, type, level);
        setPicture(Weapon.getWeaponPicture(m_weaponMain.getType()));
    }

    public void setWeaponPicture(){setPicture(Weapon.getWeaponPicture(m_weaponMain.getType()));}
    public void setWeaponSub(int type, int level){m_weaponSub = new Weapon(this, type, level);}

    @Override
    public boolean damage(float d){
        boolean result = super.damage(d);
        Manager.INSTANCE.updateStatus(getHp() / getHpMax(), Game.INSTANCE.getExp() / (float)Game.INSTANCE.getExpMax());
        return result;
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);

        if(m_weaponMain != null)
            m_weaponMain.update(delta);
        if(m_weaponSub != null)
            m_weaponSub.update(delta);
    }
}
