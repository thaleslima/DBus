package net.dublin.bus.ui.view.utilities;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtil {
    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(Context context, String filePath) {
        try {
            final InputStream stream = context.getResources().getAssets().open(filePath);

            String ret = convertStreamToString(stream);
            stream.close();
            return ret;

        } catch (Exception ex) {
            return "";
        }
    }
}
