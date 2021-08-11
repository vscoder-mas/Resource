package sensetime.senseme.com.effects.view;

import android.graphics.Bitmap;
import android.text.TextUtils;

public class MakeupItem {

    public String name;
    public Bitmap icon;
    public String path;

    public StickerState state = StickerState.NORMAL_STATE;

    public MakeupItem(String name, Bitmap icon, String path) {
        this.name = name;
        this.icon = icon;
        this.path = path;
        if (TextUtils.isEmpty(this.path)) {
            state = StickerState.NORMAL_STATE;
        } else {
            state = StickerState.DONE_STATE;
        }
    }

    public MakeupItem(String name, Bitmap icon, String path, StickerState state) {
        this.name = name;
        this.icon = icon;
        this.path = path;
        this.state = state;
    }

    @Override
    public String toString() {
        return "MakeupItem{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", state=" + state +
                '}';
    }
}
