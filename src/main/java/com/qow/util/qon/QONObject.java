package com.qow.util.qon;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * qon(Qow Object Notation)の読み込みをする<br>
 *
 * @version 2025/10/06
 * @since 1.0.0
 */
public class QONObject {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\(([^)]*)\\)");
    private final Map<String, String> valueMap;
    private final Map<String, QONObject> objectMap;
    private final Map<String, QONArray> arrayMap;
    private QONObject parentQONObject;

    private QONObject() {
        valueMap = new HashMap<>();
        objectMap = new HashMap<>();
        arrayMap = new HashMap<>();
        parentQONObject = null;
    }

    /**
     * qonファイルからqonを読み込む。
     *
     * @param file qonファイルパス
     * @throws IOException           qonファイルの読み込みに問題が生じた場合
     * @throws UntrustedQONException qon構文に不備がある場合
     */
    public QONObject(File file) throws IOException, UntrustedQONException {
        this();

        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        init(lines.toArray(new String[0]));
    }

    /**
     * qon構文に従った配列からqonを読み込む。
     *
     * @param lines qonドキュメント
     * @throws UntrustedQONException qon構文に不備がある場合
     */
    public QONObject(String[] lines) throws UntrustedQONException {
        this();

        init(lines);
    }

    /**
     * qon構文に従った配列からqonを読み込む。
     *
     * @param parentQONObject 親オブジェクト
     * @param lines           qonドキュメント
     * @throws UntrustedQONException qon構文に不備がある場合
     */
    public QONObject(QONObject parentQONObject, String[] lines) throws UntrustedQONException {
        this();
        this.parentQONObject = parentQONObject;

        init(lines);
    }

    /**
     * キーに対応する{@link QONObject}を返す。
     *
     * @param key キー
     * @return キーに対応するQONObject
     */
    public QONObject getQONObject(String key) {
        return objectMap.get(key);
    }

    /**
     * キーに対応する{@link QONArray}を返す。
     *
     * @param key キー
     * @return キーに対応するQONArray
     */
    public QONArray getQONArray(String key) {
        return arrayMap.get(key);
    }

    /**
     * キーに対応する値を返す。
     *
     * @param key キー
     * @return キーに対応する値
     */
    public String get(String key) {
        return valueMap.get(key);
    }

    private void init(String[] lines) throws UntrustedQONException {
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].replaceFirst("^\\s+", "");
        }

        boolean object = false, array = false;
        int indent = 0;
        int objectStartIndex = 0, arrayStartIndex = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (isCommentOuted(line) || isNoMean(line)) continue;

            if (object) {
                if (isObjectEnd(line)) {
                    indent--;
                    if (indent == 0) {
                        object = false;
                        objectMap.put(lines[objectStartIndex].substring(0, lines[objectStartIndex].length() - 1), new QONObject(this, Arrays.copyOfRange(lines, objectStartIndex + 1, i)));
                    }
                } else if (isObjectStart(line)) {
                    indent++;
                }
            } else if (array) {
                if (isArrayEnd(line)) {
                    array = false;
                    String[] absoluteLines = Arrays.copyOfRange(lines, arrayStartIndex + 1, i);
                    for (int j = 0; j < absoluteLines.length; j++) {
                        if (hasVariable(absoluteLines[j])) {
                            absoluteLines[j] = getAbsoluteVariable(absoluteLines[j]);
                        }
                    }
                    arrayMap.put(lines[arrayStartIndex].substring(0, lines[arrayStartIndex].length() - 1), new QONArray(absoluteLines));
                } else if (isArrayStart(line) || isObjectStart(line)) {
                    throw new UntrustedQONException("multiple array at " + i + ": " + line);
                }
            } else if (isObjectStart(line)) {
                object = true;
                objectStartIndex = i;
                indent++;
            } else if (isArrayStart(line)) {
                array = true;
                arrayStartIndex = i;
            } else if (isValue(line)) {
                String[] strings = line.split("=", 2);
                String key = strings[0];
                String value = strings[1];
                if (hasVariable(value)) {
                    valueMap.put(key, getAbsoluteVariable(value));
                } else {
                    valueMap.put(key, value);
                }
            } else {
                throw new UntrustedQONException("Invalid line at " + i + ": " + line);
            }
        }
        if (object || array) throw new UntrustedQONException("extra indent.");
    }

    private String getAbsoluteVariable(String value) {
        Matcher matcher = VARIABLE_PATTERN.matcher(value);

        List<String> matchList = new ArrayList<>();
        while (matcher.find()) {
            matchList.add(matcher.group(1));
        }
        String replacedValue = value;
        for (String match : matchList) {
            try {
                replacedValue = replacedValue.replaceFirst("\\$\\(" + Pattern.quote(match) + "\\)", getMatchedVariable(match));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return replacedValue;
    }

    private String getMatchedVariable(String key) {
        if (valueMap.containsKey(key)) {
            return valueMap.get(key);
        } else {
            if (parentQONObject == null) {
                return "$(" + key + ")";
            } else {
                return parentQONObject.getMatchedVariable(key);
            }
        }
    }

    private boolean isCommentOuted(String target) {
        return target.startsWith("#");
    }

    private boolean isNoMean(String target) {
        return target.isBlank();
    }

    private boolean isObjectStart(String target) {
        return target.endsWith("{");
    }

    private boolean isObjectEnd(String target) {
        return target.equals("}");
    }

    private boolean isArrayStart(String target) {
        return target.endsWith("[");
    }

    private boolean isArrayEnd(String target) {
        return target.equals("]");
    }

    private boolean isValue(String target) {
        return target.contains("=");
    }

    private boolean hasVariable(String target) {
        return target.contains("$(") && target.contains(")");
    }
}