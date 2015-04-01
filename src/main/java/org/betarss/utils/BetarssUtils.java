package org.betarss.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.betarss.exception.BetarssException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetarssUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(BetarssUtils.class);

	public interface Function<T> {
		T doCall() throws Exception;
	}

	public interface Procedure {
		void doCall() throws Exception;
	}

	public static <T> T doTry(int max, Function<T> t) {
		int times = 0;
		while (true) {
			try {
				return t.doCall();
			} catch (Exception e) {
				if (++times == max) {
					LOGGER.error("Too many consecutive errors during function execution", e);
					throw new BetarssException(e);
				}
			}
		}
	}

	public static void multiThreadCalls(List<Procedure> procedures, int second) {
		ExecutorService es = Executors.newCachedThreadPool();
		for (final Procedure procedure : procedures) {
			es.execute(new Runnable() { // Must use callable instead of Runnable in order to catch thread exceptions in main thread

				@Override
				public void run() {
					try {
						procedure.doCall();
					} catch (Exception e) {
						LOGGER.error("An error occured during procedure execution", e);
						throw new BetarssException(e);
					}

				}
			});
		}
		es.shutdown();
		try {
			es.awaitTermination(second, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOGGER.error("An error occured during procedure execution", e);
			throw new BetarssException(e);
		}
	}

	public static Date parseDate(String toParse, String pattern, Locale locale) {
		try {
			return new SimpleDateFormat(pattern, locale).parse(toParse);
		} catch (java.text.ParseException e) {
			return null;
		}
	}

}
