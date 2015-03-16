package org.betarss.utils;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.betarss.exception.BetarssException;

import com.google.common.collect.Lists;

public class Utils {

	public interface Function<T> {
		T doCall() throws Exception;
	}

	public static <T> T doTry(int max, Function<T> t) {
		int times = 0;
		while (true) {
			try {
				return t.doCall();
			} catch (Exception e) {
				if (++times == max) {
					throw new BetarssException(e);
				}
			}
		}
	}

	//	public static Config override(Config config1, Config config2) {
	//		Config result = new Config();
	//		result.date = firstNonNull(config2.date, config1.date);
	//		result.magnet = firstNonNull(config2.magnet, config1.magnet);
	//		result.providers = firstNonNull(config2.providers, config1.providers);
	//		return result;
	//	}
	//
	//	private static <T> T firstNonNull(T o1, T o2) {
	//		return o1 != null ? o1 : o2;
	//	}

	public static <T> List<T> multiThreadCalls(List<Function<T>> functions, int timeout) {
		final List<T> results = Lists.newArrayList();
		ExecutorService es = Executors.newCachedThreadPool();
		for (final Function<T> function : functions) {
			es.execute(new Runnable() {

				@Override
				public void run() {
					try {
						results.add(function.doCall());
					} catch (Exception e) {
						throw new BetarssException(e);
					}

				}
			});
		}
		es.shutdown();
		try {
			es.awaitTermination(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new BetarssException(e);
		}
		return results;
	}

	static {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}

			} };
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
		}
	}

	public static Date parseDefaultDate(String toParse, String pattern, Locale locale) {
		try {
			return new SimpleDateFormat(pattern, locale).parse(toParse);
		} catch (java.text.ParseException e) {
			return null;
		}
	}

	public static void main(String[] argz) throws Exception {
	}

	public static class TimeWatch {
		long starts;

		private TimeWatch() {
			reset();
		}

		public TimeWatch reset() {
			starts = System.currentTimeMillis();
			return this;
		}

		public long time() {
			long ends = System.currentTimeMillis();
			return ends - starts;
		}

		public long time(TimeUnit unit) {
			return unit.convert(time(), TimeUnit.MILLISECONDS);
		}
	}

}
