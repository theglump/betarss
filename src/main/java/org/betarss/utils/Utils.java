package org.betarss.utils;

import org.betarss.exception.BetarssException;

public class Utils {

	public interface Try<T> {
		T doTry() throws Exception;
	}

	public static <T> T doTry(int max, Try<T> t) {
		int times = 0;
		while (true) {
			try {
				return t.doTry();
			} catch (Exception e) {
				if (++times == max) {
					throw new BetarssException(e);
				}
			}
		}
	}

}
