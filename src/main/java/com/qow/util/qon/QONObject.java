package com.qow.util.qon;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * qon(Qow Object Notation)の読み込みをする<br>
 *
 * @version 2025/10/07
 * @since 1.0.0
 */
public class QONObject {
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
     * @throws NoSuchKeyException キーが存在しなかった場合
     */
    public QONObject getQONObject(String key) throws NoSuchKeyException {
        if (objectMap.containsKey(key)) {
            return objectMap.get(key);
        } else {
            throw new NoSuchKeyException("not exist key.", key);
        }
    }

    /**
     * キーに対応する{@link QONArray}を返す。
     *
     * @param key キー
     * @return キーに対応するQONArray
     * @throws NoSuchKeyException キーが存在しなかった場合
     */
    public QONArray getQONArray(String key) throws NoSuchKeyException {
        if (arrayMap.containsKey(key)) {
            return arrayMap.get(key);
        } else {
            throw new NoSuchKeyException("not exist key.", key);
        }
    }

    /**
     * キーに対応する値を返す。
     *
     * @param key キー
     * @return キーに対応する値
     * @throws NoSuchKeyException キーが存在しなかった場合
     */
    public String get(String key) throws NoSuchKeyException {
        if (valueMap.containsKey(key)) {
            return valueMap.get(key);
        } else {
            throw new NoSuchKeyException("not exist key.", key);
        }
    }

    /**
     * 変数のキーリストを返す
     *
     * @return キーリスト
     */
    public String[] getValueKeyList() {
        return valueMap.keySet().toArray(new String[0]);
    }

    /**
     * {@link QONObject}のキーリストを返す
     *
     * @return キーリスト
     */
    public String[] getQONObjectKeyList() {
        return objectMap.keySet().toArray(new String[0]);
    }

    /**
     * {@link QONArray}のキーリストを返す
     *
     * @return キーリスト
     */
    public String[] getQONArrayKeyList() {
        return arrayMap.keySet().toArray(new String[0]);
    }

    protected void putValue(String key, String value) {
        valueMap.put(key, value);
    }

    protected void putObject(String key, QONObject qonObject) {
        objectMap.put(key, qonObject);

    }

    protected void putArray(String key, QONArray qonArray) {
        arrayMap.put(key, qonArray);
    }

    private void init(String[] lines) throws UntrustedQONException {
        new QONParser(this).parse(lines);
    }

    protected String getAbsoluteVariable(String value) {
        Matcher matcher = QONParser.VARIABLE_PATTERN.matcher(value);

        List<String> matchList = new ArrayList<>();
        while (matcher.find()) {
            matchList.add(matcher.group(1));
        }
        String replacedValue = value;
        for (String match : matchList) {
            try {
                String regex = Pattern.quote(QONParser.VARIABLE_START + match + QONParser.VARIABLE_END);
                String replacement = getMatchedVariable(match);
                replacedValue = replacedValue.replaceFirst(regex, replacement);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return replacedValue;
    }

    protected String getMatchedVariable(String key) {
        if (valueMap.containsKey(key)) {
            return valueMap.get(key);
        } else {
            if (parentQONObject == null) {
                return QONParser.VARIABLE_START + key + QONParser.VARIABLE_END;
            } else {
                return parentQONObject.getMatchedVariable(key);
            }
        }
    }
}