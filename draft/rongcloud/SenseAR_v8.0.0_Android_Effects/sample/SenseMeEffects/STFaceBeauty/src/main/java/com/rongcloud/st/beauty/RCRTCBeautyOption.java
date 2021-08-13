package com.rongcloud.st.beauty;

public class RCRTCBeautyOption {
    private int whitenessLevel;
    private int smoothLevel;
    private int ruddyLevel;

    public RCRTCBeautyOption(int whitenessLevel, int smoothLevel, int ruddyLevel) {
        this.whitenessLevel = whitenessLevel;
        this.smoothLevel = smoothLevel;
        this.ruddyLevel = ruddyLevel;
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
