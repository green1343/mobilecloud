package com.androidtuto.war;

/**
 * Created by 초록 on 2015-05-18.
 */
public class Vec2 {
    public float x;
    public float y;

    Vec2(){
        x = 0f;
        y = 0f;
    }

    Vec2(final Vec2 v){
        x = v.x;
        y = v.y;
    }

    Vec2(float x, float y){
        this.x = x;
        this.y = y;
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

    static final int ccw(final Vec2 p0, final Vec2 p1, final Vec2 p2)
    {
        float dx1, dx2, dy1, dy2;
        dx1 = p1.x - p0.x;
        dy1 = p1.y - p0.y;
        dx2 = p2.x - p0.x;
        dy2 = p2.y - p0.y;
        if (dx1*dy2 > dy1*dx2) return +1;
        if (dx1*dy2 < dy1*dx2) return -1;
        if ((dx1*dx2 < 0) || (dy1*dy2 < 0)) return -1;
        if ((dx1*dx1+dy1*dy1) < (dx2*dx2+dy2*dy2)) return +1;
        return 0;
    }

    static Vec2 add(final Vec2 v1, final Vec2 v2){return new Vec2(v1.x + v2.x, v1.y + v2.y);}

    static final boolean checkSquare(final Vec2 v1, final Vec2 v2, float size){
        return Math.abs(v1.x - v2.x) <= size && Math.abs(v1.y - v2.y) <= size;
    }

    static final boolean checkDistance(final Vec2 v1, final Vec2 v2, float dist){
        return length(v1, v2) <= dist;
    }

    static final boolean checkSqrAndDist(final Vec2 v1, final Vec2 v2, float dist){
        return checkSquare(v1, v2, dist) && checkDistance(v1, v2, dist);
    }

    static final float length(final Vec2 v1, final Vec2 v2){
        float x = v1.x - v2.x;
        float y = v1.y - v2.y;
        return sqrt(x*x + y*y);
    }

    static final boolean checkTriangle(final Vec2 p1, final Vec2 p2, final Vec2 p3, final Vec2 t){

        Vec2 ab = new Vec2(p2.x - p1.x, p2.y - p1.y);
        Vec2 bc = new Vec2(p3.x - p2.x, p3.y - p2.y);
        Vec2 ca = new Vec2(p1.x - p3.x, p1.y - p3.y);
        Vec2 ap = new Vec2(t.x - p1.x, t.y - p1.y);
        Vec2 bp = new Vec2(t.x - p2.x, t.y - p2.y);
        Vec2 cp = new Vec2(t.x - p3.x, t.y - p3.y);

        float a = ab.x*ap.y - ab.y*ap.x;
        float b = bc.x*bp.y - bc.y*bp.x;
        float c = ca.x*cp.y - ca.y*cp.x;

        return (a >= 0 && b >= 0 && c >= 0) || (a <= 0 && b <= 0 && c <= 0);
    }

    static final float getAngle(float x, float y){
        //return y > 0f ? (float)Math.acos(x) : -(float)Math.acos(x);
        return x > 0f ? -(float)Math.acos(y) : (float)Math.acos(y);
    }

    static final Vec2 getDirection(Vec2 s, Vec2 e){
        Vec2 d = new Vec2(e.x-s.x, e.y-s.y);
        d.normalize();
        return d;
    }

    public void normalize()
    {
        float len = length();
        x /= len;
        y /= len;
    }

    public void rotate(float angle){
        float newX = (float)(Math.cos(angle)*x - Math.sin(angle)*y);
        float newY = (float)(Math.sin(angle)*x + Math.cos(angle)*y);
        x = newX;
        y = newY;
    }

    public void setDirection(float angle){
        x = 1f;
        y = 0f;
        rotate(angle);
    }

    public float getAngle(){
        return getAngle(x, y);
    }

    public float length()
    {
        return sqrt(x * x + y * y);
    }
}
