package org.tensorflow.demo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zoltan Szabo on 12/17/17.
 */

public class Yolov2 {
    private float tx;
    private float ty;
    private float tw;
    private float th;
    private float to;
    private final List<Float> classProbabilities;
    private final List<Float> calculatedProbablilities;

    public Yolov2() {
        classProbabilities = new ArrayList();
        calculatedProbablilities = new ArrayList();
    }

    public float getTx() {
        return tx;
    }

    public void setTx(float tx) {
        this.tx = tx;
    }

    public float getTy() {
        return ty;
    }

    public void setTy(float ty) {
        this.ty = ty;
    }

    public float getTw() {
        return tw;
    }

    public void setTw(float tw) {
        this.tw = tw;
    }

    public float getTh() {
        return th;
    }

    public void setTh(float th) {
        this.th = th;
    }

    public float getTo() {
        return to;
    }

    public void setTo(float to) {
        this.to = to;
    }

    public List<Float> getClassProbabilities() {
        return classProbabilities;
    }

    public void addProbability(Float probability) {
        classProbabilities.add(probability);
    }

    public List<Float> getCalculatedProbablilities() {
        return calculatedProbablilities;
    }

    public void addCalculatedProbabilities(Float calculatedProbability) {
        calculatedProbablilities.add(calculatedProbability);
    }
}
