package com.example.dmfi.serviceapp.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmfi on 10/10/2015.
 */
public class SensorDataConverter {

    public static List<Float> convertToDiff(List<Float> sensorData) {
        List<Float> result = new ArrayList<>();
        if (sensorData.size() > 1) {
            for (int i = 1; i < sensorData.size(); i++) {
                result.add(sensorData.get(i) - sensorData.get(i - 1));
            }
        }
        return result;
    }

}
