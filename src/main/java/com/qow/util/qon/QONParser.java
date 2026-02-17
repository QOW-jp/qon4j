package com.qow.util.qon;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.regex.Pattern;

record QONParser(QONObject target) {
    static final String COMMENT_LINE = "#";
    static final String OBJECT_START = "{";
    static final String OBJECT_END = "}";
    static final String ARRAY_START = "[";
    static final String ARRAY_END = "]";
    static final String VARIABLE_SPLIT = "=";
    static final String VARIABLE_START = "$(";
    static final String VARIABLE_END = ")";
    static final int OBJECT_KEY_INDEX = OBJECT_START.length();
    static final int ARRAY_KEY_INDEX = ARRAY_START.length();

    static final Pattern VARIABLE_PATTERN = Pattern.compile(Pattern.quote(VARIABLE_START) + "([^)]*)" + Pattern.quote(VARIABLE_END));

    QONParser(QONObject target) {
        this.target = target;
    }

    private static boolean isCommentOuted(String target) {
        return target.startsWith(COMMENT_LINE);
    }

    private static boolean isNoMean(String target) {
        return target.isBlank();
    }

    static boolean isIgnored(String target) {
        return isCommentOuted(target) || isNoMean(target);
    }

    private static boolean isObjectStart(String target) {
        return target.endsWith(OBJECT_START);
    }

    private static boolean isObjectEnd(String target) {
        return target.equals(OBJECT_END);
    }

    private static boolean isArrayStart(String target) {
        return target.endsWith(ARRAY_START);
    }

    private static boolean isArrayEnd(String target) {
        return target.equals(ARRAY_END);
    }

    private static boolean isValue(String target) {
        return target.contains(VARIABLE_SPLIT);
    }

    private static boolean hasVariable(String target) {
        return target.contains(VARIABLE_START) && target.contains(VARIABLE_END);
    }

    void parse(String[] targetLines) throws UntrustedQONException {
        int ignoredCount = 0;
        for (int i = 0; i < targetLines.length; i++) {
            targetLines[i] = targetLines[i].replaceFirst("^\\s+", "");
            if (isIgnored(targetLines[i])) {
                ignoredCount++;
            }
        }
        String[] lines = new String[targetLines.length - ignoredCount];
        int ignoredIndex = 0;
        for (String targetLine : targetLines) {
            if (!isIgnored(targetLine)) {
                lines[ignoredIndex++] = targetLine;
            }
        }

        boolean object = false, array = false;
        int indent = 0;
        int objectStartIndex = 0, arrayStartIndex = 0;

        Queue<Integer> objectIndex = new ArrayDeque<>();
        Queue<Integer> arrayIndex = new ArrayDeque<>();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (object) {
                if (isObjectEnd(line)) {
                    indent--;
                    if (indent == 0) {
                        object = false;

                        int comp = (objectStartIndex << 14) | (i - objectStartIndex);
                        objectIndex.add(comp);
                    }
                } else if (isObjectStart(line)) {
                    indent++;
                }
            } else if (array) {
                if (isArrayEnd(line)) {
                    array = false;

                    int comp = (arrayStartIndex << 14) | (i - arrayStartIndex);
                    arrayIndex.add(comp);
                } else if (isArrayStart(line) || isObjectStart(line)) {
                    throw new UntrustedQONException("Multiple array at " + i + ": " + line);
                }
            } else if (isObjectStart(line)) {
                object = true;
                objectStartIndex = i;
                indent++;
            } else if (isArrayStart(line)) {
                array = true;
                arrayStartIndex = i;
            } else if (isValue(line)) {
                String[] strings = line.split(VARIABLE_SPLIT, 2);
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

        if (object || array) throw new UntrustedQONException("Extra indent.");

        for (int i = 0; i < objectIndex.size(); i++) {
            int comp = objectIndex.poll();
            int startIndex = comp >>> 14;
            int endIndex = startIndex + (comp & 0x3FFF);

            String key = lines[startIndex].substring(0, lines[startIndex].length() - OBJECT_KEY_INDEX);
            target.putObject(key, new QONObject(target, Arrays.copyOfRange(lines, startIndex + 1, endIndex)));
        }

        for (Integer comp : arrayIndex) {
            int startIndex = comp >>> 14;
            int endIndex = startIndex + (comp & 0x3FFF);

            String[] absoluteLines = Arrays.copyOfRange(lines, startIndex + 1, endIndex);
            for (int i = 0; i < absoluteLines.length; i++) {
                if (hasVariable(absoluteLines[i])) {
                    absoluteLines[i] = target.getAbsoluteVariable(absoluteLines[i]);
                }
            }
            String key = lines[startIndex].substring(0, lines[startIndex].length() - ARRAY_KEY_INDEX);
            target.putArray(key, new QONArray(absoluteLines));
        }
    }
}