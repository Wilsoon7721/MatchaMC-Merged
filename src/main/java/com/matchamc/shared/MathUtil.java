package com.matchamc.shared;

public class MathUtil {
	private MathUtil() {
	}

	public static int getInventorySize(int size) {
		if(size <= 0)
			return 9;
		int q = (int) Math.ceil(size / 9);
		return q > 5 ? 54 : q * 9;
	}
}
