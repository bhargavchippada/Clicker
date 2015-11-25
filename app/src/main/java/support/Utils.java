package support;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Class for extra helper functions
 *
 * @author bhargav
 */
public class Utils {

    /**
     * To generate random unique integers
     */
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * For lower api generate unique view id
     *
     * @return int ID
     */
    public static int lowerapiGenerateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * For higher api generate unique view id
     *
     * @return int ID
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {

            return lowerapiGenerateViewId();

        } else {

            return View.generateViewId();

        }
    }

}
