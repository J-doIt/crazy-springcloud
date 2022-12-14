package com.crazymaker.springcloud.common.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class StringUtils {
    public static final String EMPTY_STRING = "";
    public static final char DEFAULT_DELIMITER_CHAR = ',';
    public static final char DEFAULT_QUOTE_CHAR = '"';

    public StringUtils() {
    }

    public static boolean hasText(String str) {
        if (!hasLength(str)) {
            return false;
        } else {
            int strLen = str.length();

            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean hasLength(String str) {
        return str != null && str.length() > 0;
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str != null && prefix != null) {
            if (str.startsWith(prefix)) {
                return true;
            } else if (str.length() < prefix.length()) {
                return false;
            } else {
                String lcStr = str.substring(0, prefix.length()).toLowerCase();
                String lcPrefix = prefix.toLowerCase();
                return lcStr.equals(lcPrefix);
            }
        } else {
            return false;
        }
    }

    public static String clean(String in) {
        String out = in;
        if (in != null) {
            out = in.trim();
            if (out.equals("")) {
                out = null;
            }
        }

        return out;
    }

    public static String toString(Object[] array) {
        return toDelimitedString(array, ",");
    }

    public static String toDelimitedString(Object[] array, String delimiter) {
        if (array != null && array.length != 0) {
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < array.length; ++i) {
                if (i > 0) {
                    sb.append(delimiter);
                }

                sb.append(array[i]);
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    public static String toDelimitedString(Collection c, String delimiter) {
        return c != null && !c.isEmpty() ? join(c.iterator(), delimiter) : "";
    }

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (str == null) {
            return null;
        } else {
            StringTokenizer st = new StringTokenizer(str, delimiters);
            ArrayList tokens = new ArrayList();

            while(true) {
                String token;
                do {
                    if (!st.hasMoreTokens()) {
                        return toStringArray(tokens);
                    }

                    token = st.nextToken();
                    if (trimTokens) {
                        token = token.trim();
                    }
                } while(ignoreEmptyTokens && token.length() <= 0);

                tokens.add(token);
            }
        }
    }

    public static String[] toStringArray(Collection collection) {
        return collection == null ? null : (String[])collection.toArray(new String[collection.size()]);
    }

    public static String[] splitKeyValue(String aLine) throws ParseException {
        String line = clean(aLine);
        if (line == null) {
            return null;
        } else {
            String[] split = line.split(" ", 2);
            String msg;
            if (split.length != 2) {
                split = line.split("=", 2);
                if (split.length != 2) {
                    msg = "Unable to determine Key/Value pair from line [" + line + "].  There is no space from which the split location could be determined.";
                    throw new ParseException(msg, 0);
                }
            }

            split[0] = clean(split[0]);
            split[1] = clean(split[1]);
            if (split[1].startsWith("=")) {
                split[1] = clean(split[1].substring(1));
            }

            if (split[0] == null) {
                msg = "No valid key could be found in line [" + line + "] to form a key/value pair.";
                throw new ParseException(msg, 0);
            } else if (split[1] == null) {
                msg = "No corresponding value could be found in line [" + line + "] for key [" + split[0] + "]";
                throw new ParseException(msg, 0);
            } else {
                return split;
            }
        }
    }

    public static String[] split(String line) {
        return split(line, ',');
    }

    public static String[] split(String line, char delimiter) {
        return split(line, delimiter, '"');
    }

    public static String[] split(String line, char delimiter, char quoteChar) {
        return split(line, delimiter, quoteChar, quoteChar);
    }

    public static String[] split(String line, char delimiter, char beginQuoteChar, char endQuoteChar) {
        return split(line, delimiter, beginQuoteChar, endQuoteChar, false, true);
    }

    public static String[] split(String aLine, char delimiter, char beginQuoteChar, char endQuoteChar, boolean retainQuotes, boolean trimTokens) {
        String line = clean(aLine);
        if (line == null) {
            return null;
        } else {
            List<String> tokens = new ArrayList();
            StringBuilder sb = new StringBuilder();
            boolean inQuotes = false;

            for(int i = 0; i < line.length(); ++i) {
                char c = line.charAt(i);
                if (c == beginQuoteChar) {
                    if (inQuotes && line.length() > i + 1 && line.charAt(i + 1) == beginQuoteChar) {
                        sb.append(line.charAt(i + 1));
                        ++i;
                    } else {
                        inQuotes = !inQuotes;
                        if (retainQuotes) {
                            sb.append(c);
                        }
                    }
                } else if (c == endQuoteChar) {
                    inQuotes = !inQuotes;
                    if (retainQuotes) {
                        sb.append(c);
                    }
                } else if (c == delimiter && !inQuotes) {
                    String s = sb.toString();
                    if (trimTokens) {
                        s = s.trim();
                    }

                    tokens.add(s);
                    sb = new StringBuilder();
                } else {
                    sb.append(c);
                }
            }

            String s = sb.toString();
            if (trimTokens) {
                s = s.trim();
            }

            tokens.add(s);
            return (String[])tokens.toArray(new String[tokens.size()]);
        }
    }

    public static String join(Iterator<?> iterator, String separator) {
        String empty = "";
        if (iterator == null) {
            return null;
        } else if (!iterator.hasNext()) {
            return "";
        } else {
            Object first = iterator.next();
            if (!iterator.hasNext()) {
                return first == null ? "" : first.toString();
            } else {
                StringBuilder buf = new StringBuilder(256);
                if (first != null) {
                    buf.append(first);
                }

                while(iterator.hasNext()) {
                    if (separator != null) {
                        buf.append(separator);
                    }

                    Object obj = iterator.next();
                    if (obj != null) {
                        buf.append(obj);
                    }
                }

                return buf.toString();
            }
        }
    }

    public static Set<String> splitToSet(String delimited, String separator) {
        if (delimited != null && separator != null) {
            String[] split = split(delimited, separator.charAt(0));
            return asSet(split);
        } else {
            return null;
        }
    }

    public static String uppercaseFirstChar(String in) {
        if (in != null && in.length() != 0) {
            int length = in.length();
            StringBuilder sb = new StringBuilder(length);
            sb.append(Character.toUpperCase(in.charAt(0)));
            if (length > 1) {
                String remaining = in.substring(1);
                sb.append(remaining);
            }

            return sb.toString();
        } else {
            return in;
        }
    }

    private static <E> Set<E> asSet(E... elements) {
        if (elements != null && elements.length != 0) {
            if (elements.length == 1) {
                return Collections.singleton(elements[0]);
            } else {
                LinkedHashSet<E> set = new LinkedHashSet(elements.length * 4 / 3 + 1);
                Collections.addAll(set, elements);
                return set;
            }
        } else {
            return Collections.emptySet();
        }
    }
}

