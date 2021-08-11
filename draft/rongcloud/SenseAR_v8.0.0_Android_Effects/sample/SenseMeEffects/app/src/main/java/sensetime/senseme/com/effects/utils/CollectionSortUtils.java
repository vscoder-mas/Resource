package sensetime.senseme.com.effects.utils;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import sensetime.senseme.com.effects.view.MakeupItem;

public class CollectionSortUtils {
    private static final String TOP = "original";

    public static void sort(List<MakeupItem> list) {
        Collections.sort(list, new Comparator<MakeupItem>() {
            @Override
            public int compare(MakeupItem o1, MakeupItem o2) {
                if (o1.name.equals(TOP)) {
                    return -1;
                } else if (o2.name.equals(TOP)) {
                    return 1;
                } else {
                    return Collator.getInstance(Locale.CHINESE).compare(o1.name, o2.name);
                }
            }
        });
    }
}
