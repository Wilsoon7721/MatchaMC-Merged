package com.matchamc.shared;

import java.util.ArrayList;
import java.util.List;

public class MathUtil {
	private MathUtil() {
	}

	public static int getInventorySize(int size) {
		if(size <= 0)
			return 9;
		int q = (int) Math.ceil(size / 9);
		return q > 5 ? 54 : q * 9;
	}

	public static <T> List<List<T>> separateList(List<T> originalList, int length) {
		List<List<T>> parts = new ArrayList<>();
		int originalSize = originalList.size();
		for(int i = 0; i < originalSize; i += length) {
			parts.add(new ArrayList<T>(originalList.subList(i, Math.min(originalSize, i + length))));
		}
		return parts;
	}
}
