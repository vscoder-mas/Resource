package sensetime.senseme.com.effects.encoder.mediacodec.utils;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * @Description
 * @Author Lu Guoqiang
 * @Time 4/22/21 4:50 PM
 */
public class CollectionUtils {

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static int getKey(TreeMap<Integer, String> map, String value) {
        int result = 0;
        for (Integer key : map.keySet()) {
            String s = map.get(key);
            if (s.equals(value)) result = key;
        }
        return result;
    }

    public static void removeByValue(TreeMap<Integer, String> map, String value) {
        if (map == null) return;
        Iterator<TreeMap.Entry<Integer, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            if (value.equals(iterator.next().getValue())) {
                iterator.remove();
            }
        }
    }
}
