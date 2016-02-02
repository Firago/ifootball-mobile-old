package com.example.dmfi.serviceapp.model;

import com.example.dmfi.serviceapp.utils.RootMeanSquare;
import com.example.dmfi.serviceapp.utils.SensorDataConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmfi on 09/10/2015.
 */
public class SensorData {

    private List<Float> roll;
    private List<Float> pitch;
    private List<Float> yaw;

    public SensorData() {
        initialize();
    }

    private void initialize() {
        this.roll = new ArrayList<>();
        this.yaw = new ArrayList<>();
        this.pitch = new ArrayList<>();
    }

    public void addSensorValues(float[] values) {
        roll.add(values[0]);
        pitch.add(values[1]);
        yaw.add(values[2]);
    }

    public void clear() {
        roll.clear();
        pitch.clear();
        yaw.clear();
    }

    public double getRms() {
        List<Double> vectorsLength = new ArrayList<>();
        // convert plain sensor data to difference
        List<Float> x = SensorDataConverter.convertToDiff(roll);
        List<Float> y = SensorDataConverter.convertToDiff(pitch);
        List<Float> z = SensorDataConverter.convertToDiff(yaw);
        // calculate vectors for each measurement
        List<Vector> vectors = Vector.convertPointsToVectors(x, y, z);
        for (Vector vector : vectors) {
            vectorsLength.add(vector.getVectorLength());
        }
        // return root mean square for vectors length dataset
        return RootMeanSquare.evaluate(vectorsLength);
    }
}
