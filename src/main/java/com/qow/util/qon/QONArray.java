package com.qow.util.qon;

/**
 * {@link QONObject#getQONArray(String)}によって指定されたキーに対応した配列。
 *
 * @param list 配列
 * @version 2026/02/17
 * @since 1.0.0
 */
public record QONArray(String[] list) {

    /**
     * 配列として渡された要素をコメントアウトされているものを除き保持する。
     *
     * @param list 配列
     */
    public QONArray(String[] list) {
        int count = 0;
        for (String line : list) {
            if (!QONParser.isIgnored(line)) {
                count++;
            }
        }
        String[] relist = new String[count];
        int idx = 0;
        for (String line : list) {
            if (!QONParser.isIgnored(line)) {
                relist[idx++] = line;
            }
        }

        this.list = relist;
    }

    /**
     * このリスト内の指定された位置にある要素を返す。
     *
     * @param index 返される要素のインデックス
     * @return このリスト内の指定された位置にある要素
     */
    public String get(int index) {
        return list[index];
    }
}
