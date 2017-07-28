package com.mayulive.swiftkeyexi.util;

/**
 * Created by Roughy on 5/19/2017.
 */

public class TextUtils
{

	public static int VARIANT_SELECTOR_START = '\uFE00';
	public static int VARIANT_SELECTOR_END = '\uFE0F';

	public static int MONGOLIAN_VARIANT_SELECTOR_START = '\u180B';
	public static int MONGOLIAN_VARIANT_SELECTOR_END = '\u180D';


	public static final char ZWJ = '\u200D';

	public static String stripZeroWidthJoiner(String string)
	{
		return string.replace("\u200C", "");
	}

	public static boolean isZWJ(char charIn)
	{
		return ZWJ == charIn;
	}

	//Variation Selectors Supplement block:
	// "However, as of Unicode 9.0, they are not found in any standardized variation sequence."
	//Good, because in java they're multi-char ... characters, yet somehow not surrogate pairs?
	//I am very confused and will be ignroing this problem.
	public static boolean isVariantSelector(char charIn)
	{
		return (
				(charIn >= VARIANT_SELECTOR_START && charIn <= VARIANT_SELECTOR_END) ||
						(charIn >= MONGOLIAN_VARIANT_SELECTOR_START && charIn <= MONGOLIAN_VARIANT_SELECTOR_END)
		);
	}
}