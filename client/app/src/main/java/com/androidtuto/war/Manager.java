
package com.androidtuto.war;

import android.content.Context;
import android.view.MotionEvent;

import com.androidtuto.packet.Packet_PVP_Off;
import com.androidtuto.packet.Packet_PVP_On;
import com.androidtuto.packet.Packet_Player_Fire_Off;
import com.androidtuto.packet.Packet_Player_Fire_On;
import com.androidtuto.packet.Packet_Player_Weapon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

public enum Manager
{
    INSTANCE;

    public final static int TOPMOST = Integer.MAX_VALUE;

    static public float MAP_WIDTH = 0f;
    static public float MAP_HEIGHT = 0f;

    float m_surfWidth = 0f;
    float m_surfHeight = 0f;

    MyActivity m_activity;
    Context m_context;

    int m_cntUnit;
    HashMap<Integer, Unit> m_units;
    TreeSet<Unit> m_drawables;
    TreeSet<Unit> m_buttons;

    Unit m_clearImage;
    Unit m_tutorial;
    Unit m_padMove;
    Unit m_padFire;
    Unit m_fxPadMove;
    Unit m_fxPadFire;
    Unit m_hp;
    Unit m_exp;
    Number m_coin;
    Unit m_stageClear;

    Unit m_uiMenuItem;
    Unit m_uiMenuMap;
    Unit m_uiMenuPVP;
    Unit m_uiMenuMapHightlight;
    Unit m_uiMenuItemHightlight;
    Unit m_uiMenuPVPHightlight;
    Unit m_uiMain;
    Unit m_uiItem;
    Unit m_uiMap;
    Unit m_uiShop;
    Unit m_uiClose;
    Unit m_uiShopBack;
    Unit m_uiItemCoin;
    Number m_uiItemCoinNum;
    Unit m_uiItemShop;
    Unit m_uiMapLeft;
    Unit m_uiMapRight;
    ArrayList<Unit> m_uiItemButtons1;
    ArrayList<Unit[]> m_uiItemLevel;
    ArrayList<Unit> m_uiItemButtons2;
    ArrayList<Number> m_uiItemPrices;
    ArrayList<Unit> m_uiMapButtons;
    ArrayList<Unit> m_uiShopButtons;

    ArrayList<Number> m_numbers;

    Unit m_currentEvent;

    HashMap<Integer, Unit> m_reserveRegisterUnit;
    ArrayList<Unit> m_reserveDeleteUnit;
    boolean m_bUpdating;

    Random m_random;

    Advertise m_ad;

    public static final int BOX_IMAGE = 0;
    public static final int BOX_BUTTON = 1;

    private static final int DIALOG_CANNOT_CONNECT_ID = 1;
    private static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 2;

    /*
            if(Math.random() < 0.05)
                m_ad.showAd();

            m_clearImage.addPicture(R.drawable.white);
            m_clearImage.setSize(MAP_WIDTH, MAP_HEIGHT);

            m_clearImage.animationAlphaStart(0f, 1f, 0.003f);
            m_clearImage.setCallbackAnimation(new CallbackAnimation() {
                public void animationEnd() {
                    Manager.INSTANCE.clear();
                    _callStage(m_changeStage);
                    m_clearImage.animationAlphaStart(1f, 0f, -0.003f);
                    m_clearImage.setCallbackAnimation(new CallbackAnimation() {
                        public void animationEnd() {
                            m_changeStage = 0;
                        }
                    });
                }
            });
     */

    public void init(Context context, float mapWidth, float mapHeight, Advertise ad)
    {
        m_context = context;

        MAP_WIDTH = mapWidth;
        MAP_HEIGHT = mapHeight;

        m_ad = ad;

        m_cntUnit = 0;
        m_units = new HashMap<Integer, Unit>();
        m_drawables = new TreeSet<Unit>();
        m_buttons = new TreeSet<Unit>();

        m_currentEvent = null;
        m_reserveRegisterUnit = new HashMap<Integer, Unit>();
        m_reserveDeleteUnit = new ArrayList<Unit>();
        m_bUpdating = false;

        m_random = new Random();

        m_numbers = new ArrayList<Number>();

        Game.INSTANCE.init(m_context, m_random);
        Network.INSTANCE.init();

        createCommonUI();
    }
    public void setActivity(MyActivity act)
    {
        m_activity = act;
    }

    public void clear()
    {
        m_units.clear();
        m_drawables.clear();
        m_buttons.clear();

        m_currentEvent = null;
    }

    public void buyItem( String item_name )
    {
        m_activity.buyItem(item_name);
    }

    public float getRandomFloat(float f1,float f2){
        if(f1 > f2)
            return getRandomFloat(f2, f1);
        else
            return m_random.nextFloat() * (f2-f1) + f1;
    }

    public int getRandomInt(int n1, int n2){
        return m_random.nextInt(n2-n1+1) + n1;
    }

    public int registerUnit(Unit u){
        int index = m_cntUnit++;
        u.setIndex(index);

        if(m_bUpdating == false)
            m_units.put(index, u);
        else
            m_reserveRegisterUnit.put(index, u);

        return index;
    }

    public void setSurfSize(float x, float y){
        m_surfWidth = x;
        m_surfHeight = y;
    }

    public Unit addBox(float x, float y, float sx, float sy, float angle, int boxType)
    {
        return addBox(null, x, y, sx, sy, angle, boxType, -1);
    }

    public Unit addBox(float x, float y, float sx, float sy, int boxType, int picture){
        return addBox(null, x, y, sx, sy, 0, boxType, picture);
    }

    public Unit addBox(Unit parent, float x, float y, float sx, float sy, int boxType, int picture){
        return addBox(parent, x, y, sx, sy, 0, boxType, picture);
    }

    public Unit addBox(float x, float y, float sx, float sy, int boxType){
        return addBox(null, x, y, sx, sy, 0, boxType, -1);
    }

    public Unit addBox(Unit parent, float x, float y, float sx, float sy, int boxType){
        return  addBox(parent, x, y, sx, sy, 0, boxType, -1);
    }

    public Unit addBox(float x, float y, float sx, float sy, float angle, int boxType, int picture)
    {
        return addBox(null, x, y, sx, sy, angle, boxType, picture);
    }

    public Unit addBox(Unit parent, float x, float y, float sx, float sy, float angle, int boxType, int picture)
    {
        Unit unit = new Unit(x, y, sx, sy, angle, picture);
        unit.setParent(parent);

        int index = registerUnit(unit);
        if (boxType == BOX_BUTTON) {
            unit.setCollisionBox(sx, sy);
            m_buttons.add(unit);
        }
        m_units.put(index, unit);
        m_drawables.add(unit);

        return unit;
    }

    public int getPrice(int weapon, int level){
        switch(weapon){
            case Weapon.MAIN_PISTOL:
                switch(level){
                    case 0: return 0;
                    case 1: return 10;
                    case 2: return 50;
                    case 3: return 100;
                    case 4: return 200;
                }
            case Weapon.MAIN_MACHINEGUN:
                switch(level){
                    case 0: return 100;
                    case 1: return 500;
                    case 2: return 2000;
                    case 3: return 5000;
                    case 4: return 10000;
                }
            case Weapon.MAIN_SHOTGUN:
                switch(level){
                    case 0: return 100;
                    case 1: return 500;
                    case 2: return 2000;
                    case 3: return 5000;
                    case 4: return 10000;
                }
            case Weapon.MAIN_WHIRLWIND:
                switch(level){
                    case 0: return 1000;
                    case 1: return 3000;
                    case 2: return 10000;
                    case 3: return 20000;
                    case 4: return 40000;
                }
            case Weapon.MAIN_MISSILE:
                switch(level){
                    case 0: return 3000;
                    case 1: return 6000;
                    case 2: return 15000;
                    case 3: return 30000;
                    case 4: return 60000;
                }
            case Weapon.MAIN_SNIPERRIFLE:
                switch(level){
                    case 0: return 3000;
                    case 1: return 6000;
                    case 2: return 15000;
                    case 3: return 30000;
                    case 4: return 60000;
                }
            case Weapon.MAIN_LASER:
                switch(level){
                    case 0: return 10000;
                    case 1: return 20000;
                    case 2: return 50000;
                    case 3: return 100000;
                    case 4: return 200000;
                }
            case Weapon.MAIN_LIGHTNING:
                switch(level){
                    case 0: return 30000;
                    case 1: return 60000;
                    case 2: return 100000;
                    case 3: return 200000;
                    case 4: return 300000;
                }
            case Weapon.MAIN_STRAFE:
                switch(level){
                    case 0: return 30000;
                    case 1: return 60000;
                    case 2: return 100000;
                    case 3: return 200000;
                    case 4: return 300000;
                }
            case Weapon.MAIN_BLACKHOLE:
                switch(level){
                    case 0: return 50000;
                    case 1: return 100000;
                    case 2: return 200000;
                    case 3: return 300000;
                    case 4: return 400000;
                }
            case Weapon.MAIN_GUIDEDMISSILE:
                switch(level){
                    case 0: return 50000;
                    case 1: return 100000;
                    case 2: return 200000;
                    case 3: return 300000;
                    case 4: return 400000;
                }
        }

        return 0;
    }

    public void createCommonUI()
    {
        m_clearImage = addBox(0,0,MAP_WIDTH, MAP_HEIGHT, 0, BOX_IMAGE, R.drawable.white);
        m_clearImage.setZ(TOPMOST);
        m_clearImage.setAlpha(0f);

        if(User.INSTANCE.getMapMax() == 0 && User.INSTANCE.getCoin() == 0 && User.INSTANCE.getWeaponLevel() == 1) {
            m_tutorial = addBox(0, 0, MAP_WIDTH, MAP_HEIGHT, 0, BOX_IMAGE, R.drawable.ui_tutorial);
            m_tutorial.setZ(TOPMOST);
        }
        else
            m_tutorial = null;

        m_padMove = addBox(-MAP_WIDTH + 3f, -MAP_HEIGHT + 3f, 2.5f, 2.5f, BOX_BUTTON, R.drawable.pad);
        m_padFire = addBox(MAP_WIDTH - 3f, -MAP_HEIGHT + 3f, 2.5f, 2.5f, BOX_BUTTON, R.drawable.pad);
        m_padMove.setZ(0);
        m_padFire.setZ(0);

        m_fxPadMove = addBox(0,0,1.5f, 1.5f, BOX_IMAGE, R.drawable.fx_touch);
        m_fxPadFire = addBox(0,0,1.5f, 1.5f, BOX_IMAGE, R.drawable.fx_touch);
        m_fxPadMove.setZ(1);
        m_fxPadFire.setZ(1);
        m_fxPadMove.setVisible(false);
        m_fxPadFire.setVisible(false);

        m_hp = addBox(-MAP_WIDTH + 4f, MAP_HEIGHT - 0.6f, 3f, 0.3f, BOX_BUTTON, R.drawable.ui_hp);
        m_exp = addBox(-MAP_WIDTH + 4f, MAP_HEIGHT - 1.2f, 3f, 0.3f, BOX_BUTTON, R.drawable.ui_exp);
        addBox(-MAP_WIDTH + 1.5f, MAP_HEIGHT - 3f, 0.5f, 0.5f, BOX_IMAGE, R.drawable.ui_coin);
        m_coin = createNumber(User.INSTANCE.getCoin(), -MAP_WIDTH + 2.5f, MAP_HEIGHT - 3f, 0.35f, Number.ALIGN_LEFT);
        addBox(-MAP_WIDTH + 4f, MAP_HEIGHT - 0.6f, 3f, 0.3f, BOX_BUTTON, R.drawable.ui_hp_back);
        addBox(-MAP_WIDTH + 4f, MAP_HEIGHT - 1.2f, 3f, 0.3f, BOX_BUTTON, R.drawable.ui_hp_back);
        updateStatus(1f, 0f);

        m_stageClear = addBox(0f, 3f, 5f, 0.7f, 0f, BOX_IMAGE, R.drawable.ui_stageclear);
        m_stageClear.setAlpha(0f);

        m_uiMenuItem = addBox(MAP_WIDTH - 0.6f, MAP_HEIGHT - 1.2f, 0.6f, 1.2f, BOX_BUTTON, R.drawable.ui_menu_item);
        m_uiMenuItem.setZ(1);
        m_uiMenuItem.setCallbackMenu(new CallbackMenu() {
            public void menuButton(Unit u) {
                Manager.INSTANCE.m_uiItem.setVisible(true);
                Manager.INSTANCE.m_uiMap.setVisible(false);
                Manager.INSTANCE.m_uiShop.setVisible(false);
                Manager.INSTANCE.showUI();
                Manager.INSTANCE.updateItem();
                Manager.INSTANCE.highlightItem(false);
            }
        });
        m_uiMenuMap = addBox(MAP_WIDTH - 0.6f, MAP_HEIGHT - 3.6f, 0.6f, 1.2f, BOX_BUTTON, R.drawable.ui_menu_map);
        m_uiMenuMap.setZ(1);
        m_uiMenuMap.setCallbackMenu(new CallbackMenu() {
            public void menuButton(Unit u) {
                Manager.INSTANCE.m_uiItem.setVisible(false);
                Manager.INSTANCE.m_uiMap.setVisible(true);
                Manager.INSTANCE.m_uiShop.setVisible(false);
                Manager.INSTANCE.showUI();
                Manager.INSTANCE.highlightMap(false);

                // update
                int max = User.INSTANCE.getMapMax();
                if (max > 39)
                    max = 39;

                for (int i = 0; i <= max; ++i)
                    m_uiMapButtons.get(i).setPicture(R.drawable.ui_map_button);

                for (int i = max + 1; i < 40; ++i)
                    m_uiMapButtons.get(i).setPicture(R.drawable.ui_map_button_black);

                m_uiMapButtons.get(Game.INSTANCE.getMap() - 1).setPicture(R.drawable.ui_map_button_current);
            }
        });
        m_uiMenuPVP = addBox(MAP_WIDTH - 0.6f, MAP_HEIGHT - 6.0f, 0.6f, 1.2f, BOX_BUTTON, R.drawable.ui_menu_pvp);
        m_uiMenuPVP.setZ(1);
        m_uiMenuPVP.setCallbackMenu(new CallbackMenu() {
            public void menuButton(Unit u) {
                setPVP(!isPVPOn());
            }
        });

        m_uiMenuMapHightlight = addBox(m_uiMenuMap, 0f, 0f, m_uiMenuMap.getSize().x, m_uiMenuMap.getSize().y, BOX_IMAGE, R.drawable.ui_menu_map_highlight);
        m_uiMenuMapHightlight.setAlpha(0f);
        m_uiMenuItemHightlight = addBox(m_uiMenuItem, 0f, 0f, m_uiMenuItem.getSize().x, m_uiMenuItem.getSize().y, BOX_IMAGE, R.drawable.ui_menu_item_highlight);
        m_uiMenuItemHightlight.setAlpha(0f);
        m_uiMenuPVPHightlight = addBox(m_uiMenuPVP, 0f, 0f, m_uiMenuItem.getSize().x, m_uiMenuItem.getSize().y, BOX_IMAGE, R.drawable.ui_menu_pvp_highlight);
        m_uiMenuPVPHightlight.setVisible(false);

        final float WIDTH_MAIN = 10f;
        final float HEIGHT_MAIN = 5.87f;
        final float WIDTH_ITEM = 2.8f;
        final float HEIGHT_ITEM = 1.3f;
        final float SPACE_WIDTH_ITEM = 0.2f;
        final float SPACE_HEIGHT_ITEM = 0.1f;
        final float WIDTH_LEVEL = 0.15f;
        final float HEIGHT_LEVEL = 0.4f;
        final float SPACE_SMALL = 0.3f;
        final float WIDTH_SHOP = 2.1f;
        final float HEIGHT_SHOP = 2.7f;
        final float WIDTH_MAP = 1.4f;
        final float HEIGHT_MAP = 1.1f;
        final float SPACE_WIDTH_MAP = 0.3f;
        final float SPACE_HEIGHT_MAP = 0.2f;
        final float SIZE_CLOSE = 0.6f;

        m_uiMain = addBox(MAP_WIDTH + WIDTH_MAIN, MAP_HEIGHT - HEIGHT_MAIN, WIDTH_MAIN, HEIGHT_MAIN, BOX_BUTTON, R.drawable.ui_main);
        m_uiMain.setZ(1);
        m_uiMain.setVisible(false);
        m_uiItem = addBox(m_uiMain, 0, 0, WIDTH_MAIN, HEIGHT_MAIN, BOX_IMAGE);
        m_uiItem.setVisible(false);
        m_uiShop = addBox(m_uiMain, 0, 0, WIDTH_MAIN, HEIGHT_MAIN, BOX_IMAGE);
        m_uiShop.setVisible(false);
        m_uiMap = addBox(m_uiMain, 0, 0, WIDTH_MAIN, HEIGHT_MAIN, BOX_IMAGE);
        m_uiMap.setVisible(false);

        m_uiItemCoin = addBox(m_uiItem, WIDTH_ITEM * 2 + SPACE_WIDTH_ITEM * 2, -HEIGHT_ITEM * 4 - SPACE_HEIGHT_ITEM * 2 + 0.6f, WIDTH_ITEM, 0.6f, BOX_IMAGE, R.drawable.ui_item_coin);
        m_uiItemCoinNum = createNumber(m_uiItemCoin, 0, WIDTH_ITEM - 1.5f, 0f, 0.3f, Number.ALIGN_RIGHT);
        m_uiItemShop = addBox(m_uiItem, WIDTH_ITEM * 2 + SPACE_WIDTH_ITEM * 2 + WIDTH_ITEM / 2f, -HEIGHT_ITEM * 3 - SPACE_HEIGHT_ITEM * 2 + 0.6f, WIDTH_ITEM / 2f, 0.6f, BOX_BUTTON, R.drawable.ui_item_shop);
        m_uiItemShop.setCallbackMenu(new CallbackMenu(){
            public void menuButton(Unit u) {
                Manager.INSTANCE.showUI();
                Manager.INSTANCE.m_uiItem.setVisible(false);
                Manager.INSTANCE.m_uiShop.setVisible(true);
            }
        });

        m_uiShopBack = addBox(m_uiShop, WIDTH_ITEM * 2 + SPACE_WIDTH_ITEM * 2 + WIDTH_ITEM / 2f, -HEIGHT_ITEM * 3 - SPACE_HEIGHT_ITEM * 2 + 0.6f, WIDTH_ITEM / 2f, 0.6f, BOX_BUTTON, R.drawable.ui_shop_back);
        m_uiShopBack.setCallbackMenu(new CallbackMenu() {
            public void menuButton(Unit u) {
                Manager.INSTANCE.m_uiItem.setVisible(true);
                Manager.INSTANCE.m_uiShop.setVisible(false);
                Manager.INSTANCE.updateItem();
            }
        });

        m_uiMapLeft = addBox(m_uiMap, -(WIDTH_MAP * 5 + SPACE_WIDTH_ITEM * 6 + 1f), 0f, 0.5f, 0.7f, BOX_BUTTON, R.drawable.ui_map_left);
        m_uiMapLeft.setCallbackMenu(new CallbackMenu() {
            public void menuButton(Unit u) {
                Manager.INSTANCE.setMapPage(0);
            }
        });

        m_uiMapRight = addBox(m_uiMap, WIDTH_MAP * 5 + SPACE_WIDTH_ITEM * 6 + 1f, 0f, 0.5f, 0.7f, BOX_BUTTON, R.drawable.ui_map_right);
        m_uiMapRight.setCallbackMenu(new CallbackMenu() {
            public void menuButton(Unit u) {
                Manager.INSTANCE.setMapPage(1);
            }
        });

        m_uiItemButtons1 = new ArrayList<Unit>();
        m_uiItemLevel = new ArrayList<Unit[]>();
        m_uiItemButtons2 = new ArrayList<Unit>();
        m_uiItemPrices = new ArrayList<Number>();
        m_uiMapButtons = new ArrayList<Unit>();
        m_uiShopButtons = new ArrayList<Unit>();

        int cnt = 0;
        for(float y = HEIGHT_ITEM*3 + SPACE_HEIGHT_ITEM*3; y >= -(HEIGHT_ITEM*3 + SPACE_HEIGHT_ITEM*3) - 0.1f; y -= HEIGHT_ITEM*2 + SPACE_HEIGHT_ITEM*2 - 0.000001f){
            for(float x = -(WIDTH_ITEM*2 + SPACE_WIDTH_ITEM*2); x <= WIDTH_ITEM*2 + SPACE_WIDTH_ITEM*2; x += WIDTH_ITEM*2 + SPACE_WIDTH_ITEM*2 - 0.000001f){
                Unit item = addBox(m_uiItem, x, y, WIDTH_ITEM, HEIGHT_ITEM, BOX_BUTTON, R.drawable.ui_item_button1);
                item.setUserData(cnt);
                item.setCallbackMenu(new CallbackMenu() {
                    public void menuButton(Unit u) {
                        if (User.INSTANCE.setWeaponMain(u.getUserData()))
                            Manager.INSTANCE.updateItem();
                    }
                });
                m_uiItemButtons1.add(item);
                Unit levels[] = new Unit[5];
                levels[0] = addBox(item, -WIDTH_ITEM + SPACE_SMALL + WIDTH_LEVEL, -HEIGHT_ITEM + SPACE_SMALL + HEIGHT_LEVEL, WIDTH_LEVEL, HEIGHT_LEVEL, BOX_IMAGE, R.drawable.ui_item_level_off);
                levels[1] = addBox(item, -WIDTH_ITEM + SPACE_SMALL + WIDTH_LEVEL*3, -HEIGHT_ITEM + SPACE_SMALL + HEIGHT_LEVEL, WIDTH_LEVEL, HEIGHT_LEVEL, BOX_IMAGE, R.drawable.ui_item_level_off);
                levels[2] = addBox(item, -WIDTH_ITEM + SPACE_SMALL + WIDTH_LEVEL*5, -HEIGHT_ITEM + SPACE_SMALL + HEIGHT_LEVEL, WIDTH_LEVEL, HEIGHT_LEVEL, BOX_IMAGE, R.drawable.ui_item_level_off);
                levels[3] = addBox(item, -WIDTH_ITEM + SPACE_SMALL + WIDTH_LEVEL*7, -HEIGHT_ITEM + SPACE_SMALL + HEIGHT_LEVEL, WIDTH_LEVEL, HEIGHT_LEVEL, BOX_IMAGE, R.drawable.ui_item_level_off);
                levels[4] = addBox(item, -WIDTH_ITEM + SPACE_SMALL + WIDTH_LEVEL*9, -HEIGHT_ITEM + SPACE_SMALL + HEIGHT_LEVEL, WIDTH_LEVEL, HEIGHT_LEVEL, BOX_IMAGE, R.drawable.ui_item_level_off);
                m_uiItemLevel.add(levels);
                Unit button = addBox(item, WIDTH_ITEM - SPACE_SMALL - 1.5f, -HEIGHT_ITEM + SPACE_SMALL + 0.5f, 1.5f, 0.5f, BOX_BUTTON, R.drawable.ui_item_button2);
                button.setUserData(cnt);
                button.setCallbackMenu(new CallbackMenu(){
                    public void menuButton(Unit u) {
                        if(User.INSTANCE.buy(u.getUserData())) {
                            User.INSTANCE.setWeaponMain(u.getUserData());
                            Manager.INSTANCE.updateItem();
                        }
                    }
                });
                m_uiItemPrices.add(createNumber(button, getPrice(cnt, User.INSTANCE.getWeaponLevel(cnt)), -0.1f, 0f, 0.25f, Number.ALIGN_CENTER));
                m_uiItemButtons2.add(button);

                ++cnt;
                if(cnt >= 11)
                    break;
            }
        }

        cnt = 0;
        for(float x = -(WIDTH_SHOP*3 + SPACE_WIDTH_ITEM*3); x <= WIDTH_SHOP*3 + SPACE_WIDTH_ITEM*3; x += WIDTH_SHOP*2 + SPACE_WIDTH_ITEM*2 - 0.000001f) {
            if(cnt == 0 && User.INSTANCE.getAd() == true) {
                ++cnt;
                continue;
            }

            int picture=0;
            switch(cnt){
                case 0: picture = R.drawable.ui_shop_ad; break;
                case 1: picture = R.drawable.ui_shop_gold1; break;
                case 2: picture = R.drawable.ui_shop_gold2; break;
                case 3: picture = R.drawable.ui_shop_gold3; break;
            }
            Unit item = addBox(m_uiShop, x, 0.5f, WIDTH_SHOP, HEIGHT_SHOP, BOX_BUTTON, picture);
            item.setUserData(cnt);
            item.setCallbackMenu(new CallbackMenu() {
                public void menuButton(Unit u) {
                    switch(u.getUserData()){
                        case 0: Manager.INSTANCE.buyItem("advertise"); break;
                        case 1: Manager.INSTANCE.buyItem("gold1"); break;
                        case 2: Manager.INSTANCE.buyItem("gold2"); break;
                        case 3: Manager.INSTANCE.buyItem("gold3"); break;
                    }
                }
            });
            m_uiShopButtons.add(item);
            ++cnt;
        }

        cnt = 0;
        for(int i=0; i<2; ++i) {
            for (float y = HEIGHT_MAP * 3 + SPACE_HEIGHT_MAP * 3; y >= -(HEIGHT_MAP * 3 + SPACE_HEIGHT_MAP * 3) - 0.1f; y -= HEIGHT_MAP * 2 + SPACE_HEIGHT_MAP * 2 - 0.000001f) {
                for (float x = -(WIDTH_MAP * 4 + SPACE_WIDTH_MAP * 4); x <= WIDTH_MAP * 4 + SPACE_WIDTH_MAP * 4; x += WIDTH_MAP * 2 + SPACE_WIDTH_MAP * 2 - 0.000001f) {
                    ++cnt;
                    Unit button = addBox(m_uiMap, x, y, WIDTH_MAP, HEIGHT_MAP, BOX_BUTTON, R.drawable.ui_map_button);
                    button.setUserData(cnt);
                    button.setCallbackMenu(new CallbackMenu() {
                        public void menuButton(Unit u) {
                            if (u.getUserData() <= User.INSTANCE.getMapMax() + 1) {
                                Game.INSTANCE.createMap(u.getUserData());
                                Manager.INSTANCE.m_uiMenuPVPHightlight.setVisible(false);
                                Manager.INSTANCE.hideUI();
                                if (/*User.INSTANCE.getAd() == false && */Manager.INSTANCE.getRandomInt(1, 5) <= 1)
                                    Manager.INSTANCE.showAd();
                            }
                        }
                    });
                    m_uiMapButtons.add(button);
                    createNumber(button, cnt, 0f, 0f, 0.8f, Number.ALIGN_CENTER);
                    if (cnt % 20 == 0)
                        addBox(button, 0f, 0f, WIDTH_MAP, HEIGHT_MAP, BOX_IMAGE, R.drawable.ui_map_button_boss);
                }
            }
        }
        setMapPage(User.INSTANCE.getMapMax()/21);

        m_uiClose = addBox(m_uiMain, WIDTH_MAIN - SIZE_CLOSE, HEIGHT_MAIN - SIZE_CLOSE, SIZE_CLOSE, SIZE_CLOSE, BOX_BUTTON, R.drawable.ui_close);
        m_uiClose.setCallbackMenu(new CallbackMenu() {
            public void menuButton(Unit u) {
                Manager.INSTANCE.hideUI();
            }
        });

        Unit u = addBox(m_uiItem, -0.1f, 0.5f, WIDTH_MAIN - 1.6f, HEIGHT_MAIN - 0.9f, BOX_IMAGE, R.drawable.ui_item_image);
        u.setZ(1);
    }

    public boolean isPVPOn(){
        return m_uiMenuPVPHightlight.isVisible();
    }

    public void setPVP(boolean on) {

        if (on) {
            Packet_PVP_On p1 = new Packet_PVP_On();
            p1.id = Network.INSTANCE.getID();
            Network.INSTANCE.write(p1);

            Packet_Player_Weapon p2 = new Packet_Player_Weapon();
            p2.id = Network.INSTANCE.getID();
            p2.weapon = Game.INSTANCE.getPlayer().getWeaponMain().getType();
            p2.level = Game.INSTANCE.getPlayer().getWeaponMain().getLevel();
            Network.INSTANCE.write(p2);

            Game.INSTANCE.createMap(Game.GAME_PVP);
        } else {
            Manager.INSTANCE.m_uiMenuPVPHightlight.setVisible(false);

            Packet_PVP_Off p = new Packet_PVP_Off();
            p.id = Network.INSTANCE.getID();
            Network.INSTANCE.write(p);

            Game.INSTANCE.createMap(User.INSTANCE.getMapMax() + 1);
        }
    }

    int m_mapPage = 0;

    public void setMapPage(int page){
        if(page == 0){
            for(int i=0; i<20; ++i) m_uiMapButtons.get(i).setVisible(true);
            for(int i=20; i<40; ++i) m_uiMapButtons.get(i).setVisible(false);
        }
        else {
            for (int i = 0; i < 20; ++i) m_uiMapButtons.get(i).setVisible(false);
            for (int i = 20; i < 40; ++i) m_uiMapButtons.get(i).setVisible(true);
        }
    }

    final static float UI_ANIMATION_SPEED = 1f;

    public void showUI(){
        m_uiMain.setVisible(true);
        m_uiShop.setVisible(false);
        m_uiMain.animatePos(new Vec2(MAP_WIDTH - m_uiMain.getSize().x, m_uiMain.getPosition().y), new Vec2(-1f, 0f), UI_ANIMATION_SPEED);
        m_uiMain.setCallbackAniPos(null);
        m_uiMenuItem.animatePos(new Vec2(MAP_WIDTH - m_uiMain.getSize().x * 2f - m_uiMenuItem.getSize().x, m_uiMenuItem.getPosition().y), new Vec2(-1f, 0f), UI_ANIMATION_SPEED);
        m_uiMenuMap.animatePos(new Vec2(MAP_WIDTH - m_uiMain.getSize().x * 2f - m_uiMenuMap.getSize().x, m_uiMenuMap.getPosition().y), new Vec2(-1f, 0f), UI_ANIMATION_SPEED);
        m_uiMenuPVP.animatePos(new Vec2(MAP_WIDTH - m_uiMain.getSize().x * 2f - m_uiMenuPVP.getSize().x, m_uiMenuPVP.getPosition().y), new Vec2(-1f, 0f), UI_ANIMATION_SPEED);
    }

    public boolean isMainVisible(){
        return m_uiMain.isVisible();
    }

    public void hideUI(){
        m_uiMain.animatePos(new Vec2(MAP_WIDTH + m_uiMain.getSize().x, m_uiMain.getPosition().y), new Vec2(1f, 0f), UI_ANIMATION_SPEED);
        m_uiMain.setCallbackAniPos(new CallbackAnimation() {
            public void animationEnd(Unit u) {
                Manager.INSTANCE.m_uiMain.setVisible(false);
                Manager.INSTANCE.m_uiItem.setVisible(false);
                Manager.INSTANCE.m_uiMap.setVisible(false);
                Manager.INSTANCE.m_uiShop.setVisible(false);
            }
        });
        m_uiMenuItem.animatePos(new Vec2(MAP_WIDTH - m_uiMenuItem.getSize().x, m_uiMenuItem.getPosition().y), new Vec2(1f, 0f), UI_ANIMATION_SPEED);
        m_uiMenuMap.animatePos(new Vec2(MAP_WIDTH - m_uiMenuMap.getSize().x, m_uiMenuMap.getPosition().y), new Vec2(1f, 0f), UI_ANIMATION_SPEED);
        m_uiMenuPVP.animatePos(new Vec2(MAP_WIDTH - m_uiMenuPVP.getSize().x, m_uiMenuPVP.getPosition().y), new Vec2(1f, 0f), UI_ANIMATION_SPEED);
    }

    public void showAd(){
        m_ad.showAd();
    }

    public Unit getClearImage(){return m_clearImage;}

    public void whiteout(){
        m_clearImage.animateAlpha(1f, 0.002f);
        m_clearImage.setCallbackAniAlpha(new CallbackAnimation() {
            public void animationEnd(Unit u) {
                u.animateAlpha(0f, -0.002f);
                u.setCallbackAniAlpha(null);
            }
        });
    }

    public void showStageClear(){
        m_stageClear.animateAlpha(1f, 0.001f);
        m_stageClear.setCallbackAniAlpha(new CallbackAnimation() {
            public void animationEnd(Unit u) {
                u.animateAlpha(0f, -0.001f);
                u.setCallbackAniAlpha(null);
            }
        });
    }

    public CallbackAnimation m_mapCallbackAlphaOn;
    public CallbackAnimation m_mapCallbackAlphaOff;

    public void highlightMap(boolean on){
        if(m_uiMenuMapHightlight == null || m_uiMenuMapHightlight.isAniAlphaOn() == on)
            return;

        if(on) {
            m_uiMenuMapHightlight.animateAlpha(0f, 1f, 0.005f);

            m_mapCallbackAlphaOn = new CallbackAnimation(){
                public void animationEnd(Unit u) {
                    u.animateAlpha(1f, 0.005f);
                    u.setCallbackAniAlpha(Manager.INSTANCE.m_mapCallbackAlphaOff);
                }
            };

            m_mapCallbackAlphaOff = new CallbackAnimation(){
                public void animationEnd(Unit u) {
                    u.animateAlpha(0f, -0.005f);
                    u.setCallbackAniAlpha(Manager.INSTANCE.m_mapCallbackAlphaOn);
                }
            };

            m_uiMenuMapHightlight.setCallbackAniAlpha(m_mapCallbackAlphaOff);
        }
        else{
            m_uiMenuMapHightlight.stopAniAlpha();
            m_uiMenuMapHightlight.setAlpha(0f);
        }
    }

    public CallbackAnimation m_itemCallbackAlphaOn;
    public CallbackAnimation m_itemCallbackAlphaOff;

    public void highlightItem(boolean on){
        if(m_uiMenuItemHightlight == null || m_uiMenuItemHightlight.isAniAlphaOn() == on)
            return;

        if(on) {
            m_uiMenuItemHightlight.animateAlpha(0f, 1f, 0.005f);

            m_itemCallbackAlphaOn = new CallbackAnimation(){
                public void animationEnd(Unit u) {
                    u.animateAlpha(1f, 0.005f);
                    u.setCallbackAniAlpha(Manager.INSTANCE.m_itemCallbackAlphaOff);
                }
            };

            m_itemCallbackAlphaOff = new CallbackAnimation(){
                public void animationEnd(Unit u) {
                    u.animateAlpha(0f, -0.005f);
                    u.setCallbackAniAlpha(Manager.INSTANCE.m_itemCallbackAlphaOn);
                }
            };

            m_uiMenuItemHightlight.setCallbackAniAlpha(m_itemCallbackAlphaOff);
        }
        else{
            m_uiMenuItemHightlight.stopAniAlpha();
            m_uiMenuItemHightlight.setAlpha(0f);
        }
    }

    Number createNumber(int num, float x, float y, float resize){
        return createNumber(num, x, y, resize, Number.ALIGN_CENTER);
    }

    Number createNumber(int num, float x, float y, float resize, int align) {
        Number number = new Number(num, x, y, resize, align);
        m_numbers.add(number);
        return number;
    }

    Number createNumber(Unit parent, int num, float x, float y, float resize, int align) {
        Number number = new Number(parent, num, x, y, resize, align);
        m_numbers.add(number);
        return number;
    }

    public void deleteUnit(Unit u)
    {
        if(u == null)
            return;

        if(m_bUpdating)
            m_reserveDeleteUnit.add(u);
        else {
            m_units.remove(u.getIndex());
            m_drawables.remove(u);
            m_buttons.remove(u);
        }
    }

    public Unit getCurrentEvent(){return m_currentEvent;}
    public void setCurrentEvent(Unit u){m_currentEvent = u;}

    public void updateStatus(float hp, float exp){
        m_hp.setSize(3f*hp, 0.3f);
        m_exp.setSize(3f*exp, 0.3f);
        m_hp.setPosition(-MAP_WIDTH + 4f - 3f*(1-hp), m_hp.getPosition().y);
        m_exp.setPosition(-MAP_WIDTH + 4f - 3f*(1-exp), m_exp.getPosition().y);
    }

    int m_reservedCoin = -1;
    public void updateCoin(int coin){
        m_reservedCoin = coin;
    }

    public void updateItem(){
        for(int i=0; i<m_uiItemButtons1.size(); ++i)
        {
            if(i == User.INSTANCE.getWeaponMain())
                m_uiItemButtons1.get(i).setPicture(R.drawable.ui_item_highlight);
            else
                m_uiItemButtons1.get(i).setPicture(R.drawable.ui_item_button1);

            for(int j=0; j<User.INSTANCE.getWeaponLevel(i); ++j)
                m_uiItemLevel.get(i)[j].setPicture(R.drawable.ui_item_level_on);
            for(int j=User.INSTANCE.getWeaponLevel(i); j<5; ++j)
                m_uiItemLevel.get(i)[j].setPicture(R.drawable.ui_item_level_off);

            if(User.INSTANCE.getWeaponLevel(i) >= 5) {
                m_uiItemButtons2.get(i).setPicture(R.drawable.ui_item_button2_max);
                m_uiItemPrices.get(i).setNumber(0);
                m_uiItemPrices.get(i).setVisible(false);
            }
            else {
                m_uiItemButtons2.get(i).setPicture(R.drawable.ui_item_button2);
                m_uiItemPrices.get(i).setNumber(getPrice(i, User.INSTANCE.getWeaponLevel(i)));
                m_uiItemPrices.get(i).setVisible(true);
            }
        }
        m_uiItemCoinNum.setNumber(User.INSTANCE.getCoin());
        updateCoin(User.INSTANCE.getCoin());
    }

    public void setUpdating(boolean update){
        m_bUpdating = update;
        if(m_bUpdating == false){
            if(m_reserveRegisterUnit.size() > 0){
                m_units.putAll(m_reserveRegisterUnit);
                m_reserveRegisterUnit.clear();
            }
            if(m_reserveDeleteUnit.size() > 0){
                for(Unit u : m_reserveDeleteUnit)
                    deleteUnit(u);
                m_reserveDeleteUnit.clear();
            }

            if(m_reservedCoin >= 0){
                m_coin.setNumber(m_reservedCoin);
                m_reservedCoin = -1;
            }
        }
    }

    private boolean m_pause = false;
    private boolean m_ignoreTime = false;
    public void setPause(boolean pause){
        m_pause = pause;
        if(m_pause)
            m_ignoreTime = true;
    }

    float m_accumSave = 0f;

    public void update(float delta)
    {
        if(m_pause)
            return;

        if(m_ignoreTime){
            delta = 0f;
            m_ignoreTime = false;
        }

        m_accumSave += delta;
        if(m_accumSave >= 10000f) {
            User.INSTANCE.writeUserData();
            m_accumSave = 0f;
        }

        setUpdating(true);

        for(Unit u : m_units.values())
            u.update(delta);
        //for(int i=0; i<m_numbers.size(); ++i)
        //    m_numbers.get(i).update(delta);

        Game.INSTANCE.update(delta);

        setUpdating(false);
    }

    public void draw(float [] m, float delta)
    {
        Game.INSTANCE.draw(m, delta);

        for(Unit u : m_drawables)
            u.draw(m, delta);
    }

    public void touchPadMoveOn(float x, float y)
    {
        if (m_padMove.getToggle() == false){
            Game.INSTANCE.getPlayer().setDirection(new Vec2(x-m_padMove.getPosition().x, y-m_padMove.getPosition().y));
            //Game.INSTANCE.getPlayer().setVelocity(Game.INSTANCE.getPlayer().getMaxVelocity());
            Game.INSTANCE.getPlayer().go();
            m_padMove.setToggle(true);
            m_fxPadMove.setPosition(x, y);
            m_fxPadMove.setVisible(true);
        }
        else
        {
            Game.INSTANCE.getPlayer().setDirection(new Vec2(x - m_padMove.getPosition().x, y - m_padMove.getPosition().y));
            //Game.INSTANCE.getPlayer().setVelocity(Game.INSTANCE.getPlayer().getMaxVelocity());
            m_fxPadMove.setPosition(x, y);
        }
    }

    public void touchPadMoveOff(){
        if(m_padMove.getToggle()){
            //Game.INSTANCE.getPlayer().setVelocity(0f);
            Game.INSTANCE.getPlayer().stop();
            m_padMove.setToggle(false);
            m_fxPadMove.setVisible(false);
        }
    }

    public void touchPadFireOn(float x, float y)
    {
        m_fxPadFire.setPosition(x, y);
        x -= m_padFire.getPosition().x;
        y -= m_padFire.getPosition().y;

        Game.INSTANCE.playerFire(x, y);

        Packet_Player_Fire_On p = new Packet_Player_Fire_On();
        p.id = Network.INSTANCE.getID();
        p.x = x;
        p.y = y;
        Network.INSTANCE.write(p);

        if (m_padFire.getToggle() == false){
            m_padFire.setToggle(true);
            m_fxPadFire.setVisible(true);
        }
    }

    public void touchPadFireOff(){
        if(m_padFire.getToggle()){
            Game.INSTANCE.getPlayer().getWeaponMain().touchUp();
            m_padFire.setToggle(false);
            m_fxPadFire.setVisible(false);

            Packet_Player_Fire_Off p = new Packet_Player_Fire_Off();
            p.id = Network.INSTANCE.getID();
            Network.INSTANCE.write(p);
        }
    }

    public void touchEvent(MotionEvent event)
    {
        float x = ((event.getX(event.getActionIndex()) - (m_surfWidth / 2)) * MAP_WIDTH) / (m_surfWidth / 2);
        float y = ((event.getY(event.getActionIndex()) - (m_surfHeight / 2)) * -MAP_HEIGHT) / (m_surfHeight / 2);

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_1_DOWN:
            case MotionEvent.ACTION_POINTER_2_DOWN:
            case MotionEvent.ACTION_POINTER_3_DOWN:

                if(m_tutorial != null){
                    deleteUnit(m_tutorial);
                    m_tutorial = null;
                    return;
                }

                boolean check = false;

                Iterator<Unit> it = m_buttons.descendingIterator();
                while(it.hasNext()){
                    Unit button = it.next();
                    if(/*button.getCallbackMenu() == null || */button.isVisible() == false)
                        continue;

                    if(button.checkCollide(x, y)) {
                        if(button == m_padMove) {
                            if(isMainVisible())
                                hideUI();
                            else
                                touchPadMoveOn(x, y);
                        }
                        else if(button == m_padFire) {
                            if (isMainVisible())
                                hideUI();
                            else
                                touchPadFireOn(x, y);
                        }
                        else {
                            button.doMenuButton();
                        }

                        check = true;
                        break;
                    }
                }

                if(check == false && isMainVisible())
                    hideUI();

                break;

            case MotionEvent.ACTION_MOVE:
                boolean padMoveOn = false;
                boolean padFireOn = false;
                for (int i = 0; i < event.getPointerCount(); ++i) {
                    x = ((event.getX(i) - (m_surfWidth / 2)) * MAP_WIDTH) / (m_surfWidth / 2);
                    y = ((event.getY(i) - (m_surfHeight / 2)) * -MAP_HEIGHT) / (m_surfHeight / 2);
                    if(m_padMove.checkCollide(x, y)) {
                        touchPadMoveOn(x, y);
                        padMoveOn = true;
                    }
                    else if(m_padFire.checkCollide(x, y)) {
                        touchPadFireOn(x, y);
                        padFireOn = true;
                    }
                }

                if(padMoveOn == false)
                    touchPadMoveOff();
                if(padFireOn == false)
                    touchPadFireOff();

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_1_UP:
            case MotionEvent.ACTION_POINTER_2_UP:
            case MotionEvent.ACTION_POINTER_3_UP:
                if(m_padMove.getToggle() && m_padMove.checkCollide(x, y))
                    touchPadMoveOff();
                else if(m_padFire.getToggle() && m_padFire.checkCollide(x, y))
                    touchPadFireOff();
        }
    }

}