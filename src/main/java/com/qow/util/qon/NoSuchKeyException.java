package com.qow.util.qon;

/**
 * 指定されたキーが存在しない場合呼び出される
 *
 * @version 2025/10/07
 * @since 1.1.5
 */
public class NoSuchKeyException extends Exception {
    /**
     * @param message 例外メッセージ
     */
    public NoSuchKeyException(String message, String key) {
        super(message + " at: " + key);
    }
}
