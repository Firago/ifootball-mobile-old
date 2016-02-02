package com.example.dmfi.serviceapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmfi on 12/10/2015.
 */
public class Vector {

    private Float x;
    private Float y;
    private Float z;

    public Vector(Float x, Float y, Float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getZ() {
        return z;
    }

    public void setZ(Float z) {
        this.z = z;
    }

    public Double getVectorLength() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public static List<Vector> convertPointsToVectors(List<Float> x, List<Float> y, List<Float> z) {
        List<Vector> result = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            result.add(new Vector(x.get(i), y.get(i), z.get(i)));
        }
        return result;
    }
}
