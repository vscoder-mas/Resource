package com.sensetime.stmobile.model;

public class STMobileHandInfo {
    int handId;
    STRect handRect;
    STPoint[] keyPoints;
    int keyPointsCount;
    long handAction;
    float handActionScore;

    public void setKeyPointsCount(int keyPointsCount) {
        this.keyPointsCount = keyPointsCount;
    }

    public void setKeyPoints(STPoint[] keyPoints) {
        this.keyPoints = keyPoints;
    }

    public int getKeyPointsCount() {
        return keyPointsCount;
    }

    public float getHandActionScore() {
        return handActionScore;
    }

    public int getHandId() {
        return handId;
    }

    public long getHandAction() {
        return handAction;
    }

    public STPoint[] getKeyPoints() {
        return keyPoints;
    }

    public STRect getHandRect() {
        return handRect;
    }

    public void setHandAction(long handAction) {
        this.handAction = handAction;
    }

    public void setHandActionScore(float handActionScore) {
        this.handActionScore = handActionScore;
    }

    public void setHandId(int handId) {
        this.handId = handId;
    }

    public void setHandRect(STRect handRect) {
        this.handRect = handRect;
    }
}
