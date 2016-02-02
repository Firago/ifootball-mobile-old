package com.example.dmfi.serviceapp.utils;

import java.util.List;

/**
 * Created by dmfi on 09/10/2015.
 */
public class RootMeanSquare {

    public static double evaluate(List<Double> dataset) {
        float meanSquare = 0;
        for (Double data : dataset)
            meanSquare += data * data;
        meanSquare /= dataset.size();
        return Math.sqrt(meanSquare);
    }

}
