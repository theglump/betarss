package org.betarss.infrastructure.http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.exception.BetarssException;
import org.betarss.infrastructure.ConfigurationService;
import org.betarss.infrastructure.http.NetHttpClient.Parameter;
import org.betarss.utils.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.Files;

@Service
public class ResponseSerializationHelper {

	@Autowired
	private ConfigurationService configurationService;

	public void serialize(String url, String response) throws IOException {
		serialize(url, response, (Parameter[]) null);
	}

	public void serialize(String url, String response, Parameter... parameters) throws IOException {
		File f = computeDataFile(url, parameters);
		if (!f.exists()) {
			Files.createParentDirs(f);
		}

		Files.write(response.getBytes(), f);
	}

	public void serializeRequestForHtmlTags(String url, String tag, List<String> response) throws IOException {
		File f = computeTagDataFile(url, tag);
		if (!f.exists()) {
			Files.createParentDirs(f);
		}
		Files.write(Joiner.on("---").join(response).getBytes(), f);
	}

	public String deserializeRequestResult(String url) throws IOException {
		return deserializeRequestResult(url);
	}

	public String deserializeRequestResult(String url, Parameter... parameters) throws IOException {
		File f = computeDataFile(url, parameters);
		return Files.toString(f, Charsets.UTF_8);
	}

	public List<String> deserializeRequestForHtmlTags(String url, String htmlTag) throws IOException {
		File f = computeTagDataFile(url, htmlTag);
		String data = Files.toString(f, Charsets.UTF_8);
		return Splitter.on("---").splitToList(data);
	}

	private File computeTagDataFile(String url, String htmlTag) {
		return computeDataFile(url + "_tag_" + htmlTag);
	}

	private File computeDataFile(String url, Parameter... parameters) {
		Pattern p = Pattern.compile(".*:\\/\\/(((?!\\/).)*)(.*)");
		Matcher m = p.matcher(url);
		if (m.find()) {
			String baseUrl = m.group(1);
			String parametersString = m.group(3);
			String pageUrl = parametersString.length() > 1 ? format(parametersString.substring(1)) : "index";
			if (parameters != null) {
				for (Parameter parameter : parameters) {
					try {
						pageUrl += parameter.getName() + "=" + UriUtils.encodeQueryParam(parameter.getValue(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						throw new BetarssException("Encoding url error " + url);
					}
				}
			}
			return new File(concat(getHttpSerializationDirectory(), baseUrl, pageUrl) + ".html");
		}
		throw new BetarssException("Malformed url " + url);

	}

	private String format(String elem) {
		if (Strings.isEmpty(elem)) {
			return "";
		}
		return elem.replaceAll("(\\.|\\/| |\\?)", "_");
	}

	private String concat(String... elems) {
		StringBuilder sb = new StringBuilder();
		for (String elem : elems) {
			if (sb.length() != 0) {
				sb.append(File.separator);
			}
			sb.append(elem);
		}
		return sb.toString();
	}

	private String getHttpSerializationDirectory() {
		return configurationService.getHttpSerializationDirectory();
	}
}
