package com.androidtuto.war;

/**
 * Created by 초록 on 2015-06-12.
 */
public class Vec3 {
    public float x;
    public float y;
    public float z;

    Vec3(){
        x = 0f;
        y = 0f;
        z = 0f;
    }

    Vec3(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Vec3(Vec2 v){
        this.x = v.x;
        this.y = v.y;
        z = 0f;
    }

    static final public float sqrt(float d)
    {
        if(d == 0f)
            return 0f;

        double s = Double.longBitsToDouble( ( ( Double.doubleToLongBits(d)-(1l<<52) )>>1 ) + ( 1l<<61 ) );
        double better = (s + d/s)/2.0;
        double evenbetter = (better + d/better)/2.0;
        return (float)evenbetter;
    }

    public void normalize()
    {
        float len = length();
        x /= len;
        y /= len;
        z /= len;
    }

    public float length()
    {
        return sqrt(x * x + y * y + z * z);
    }

    public Vec2 getVec2(){return new Vec2(x, y);}
}
