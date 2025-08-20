package com.qow.util.qon;

/**
 * qon構文に不備がある場合呼び出される。
 *
 * @version 2025/08/20
 * @since 1.0.0
 */
public class UntrustedQONException extends Exception {
    /**
     * @param message 例外メッセージ
     */
    public UntrustedQONException(String message) {
        super(message);
    }
}
