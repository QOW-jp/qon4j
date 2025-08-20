package com.qow.util.qon;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class QONObject {
    private final Map<String, String> map;

    public QONObject(File file) throws IOException, UntrustedQONException {
        map = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!isCommentOuted(line)) {
                    if (isObject(line)) {

                    } else if (isArray(line)) {

                    }else if (isValue(line)) {
                        String[] strings = line.split("=", 2);
                        String key = strings[0];
                        String value = strings[1];
                        map.put(key, value);
                    } else {
                        throw new UntrustedQONException("no equal sign.");
                    }
                }
            }
        }
    }
    public String getProperty(String key) {
        return map.get(key);
    }


    private boolean isCommentOuted(String target) {
        return target.startsWith("#");
    }
    private boolean isObject(String target){
        return target.endsWith("{");
    }
    private boolean isArray(String target){
        return target.endsWith("[");
    }
    private boolean isValue(String target){
        return target.contains("=");
    }
}