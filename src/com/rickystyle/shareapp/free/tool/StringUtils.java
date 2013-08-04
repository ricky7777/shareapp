package com.rickystyle.shareapp.free.tool;

/**
 * 字串常用到的工具
 * @author Ricky
 */
public class StringUtils {
    public static boolean isBlankorNull(String str) {
	return (str == null) || (str.trim().length() <= 0);
    }
}
