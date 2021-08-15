package com.rongcloud.st.beauty;

public class RCRTCBeautyOption {
    private int whitenessLevel;
    private int smoothLevel;
    private int ruddyLevel;

    private RCRTCBeautyOption(Builder builder) {
        this.whitenessLevel = builder.whitenessLevel;
        this.smoothLevel = builder.smoothLevel;
        this.ruddyLevel = builder.ruddyLevel;
    }

    public static class Builder {
        private int whitenessLevel;
        private int smoothLevel;
        private int ruddyLevel;

        public Builder() {

        }

        public Builder white(int white) {
            this.whitenessLevel = white;
            return this;
        }

        public Builder smooth(int smooth) {
            this.smoothLevel = smooth;
            return this;
        }

        public Builder red(int red) {
            this.ruddyLevel = red;
            return this;
        }

        public RCRTCBeautyOption build() {
            return new RCRTCBeautyOption(this);
        }
    }

    // 设置美白级别 取值范围 [0 ~ 9] 0是正常值
    public void setWhitenessLevel(int whitenessLevel) {
        this.whitenessLevel = whitenessLevel;
    }

    public void setSmoothLevel(int smoothLevel) {
        this.smoothLevel = smoothLevel;
    }

    public void setRuddyLevel(int ruddyLevel) {
        this.ruddyLevel = ruddyLevel;
    }

    public int getWhitenessLevel() {
        return this.whitenessLevel;
    }

    public int getSmoothLevel() {
        return this.smoothLevel;
    }

    public int getRuddyLevel() {
        return this.ruddyLevel;
    }
}
