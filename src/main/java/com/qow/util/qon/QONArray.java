package com.qow.util.qon;

/**
 * {@link QONObject#getQONArray(String)}によって指定されたキーに対応した配列。
 *
 * @param list 配列
 * @version 2025/08/20
 * @since 1.0.0
 */
public record QONArray(String[] list) {

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
