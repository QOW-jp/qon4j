package com.qow.util.qon;

import java.util.Arrays;
import java.util.regex.Pattern;

record QONParser(QONObject target) {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\(([^)]*)\\)");

    QONParser(QONObject target) {
        this.target = target;
    }

    public static String getRelativeVariable(String key) {
        return "$(" + key + ")";
    }

    public static String getMatchedVariable(String match) {
        return "\\$\\(" + Pattern.quote(match) + "\\)";
    }

    void parse(String[] lines) throws UntrustedQONException {
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
                        String key = lines[objectStartIndex].substring(0, lines[objectStartIndex].length() - 1);
                        target.putObject(key, new QONObject(target, Arrays.copyOfRange(lines, objectStartIndex + 1, i)));
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
                            absoluteLines[j] = target.getAbsoluteVariable(absoluteLines[j]);
                        }
                    }
                    String key = lines[arrayStartIndex].substring(0, lines[arrayStartIndex].length() - 1);
                    target.putArray(key, new QONArray(absoluteLines));
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
                    target.putValue(key, target.getAbsoluteVariable(value));
                } else {
                    target.putValue(key, value);
                }
            } else {
                throw new UntrustedQONException("Invalid line at " + i + ": " + line);
            }
        }
        if (object || array) throw new UntrustedQONException("extra indent.");
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