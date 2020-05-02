package me.uniodex.uniolobi.utils.packages.itembuilder;

import java.util.HashMap;
import java.util.Map;

public class Formattable {

	protected Map<String, String> formatMap = new HashMap<>();

	protected Formattable withFormat(Map<String, String> map) {
		formatMap.putAll(map);
		return this;
	}

	/**
	 * Adds a string replacement for text being loaded
	 *
	 * @param key   string to be replaced
	 * @param value replacement
	 */
	public Formattable withFormat(String key, String value) {
		formatMap.put(key, value);
		return this;
	}

	protected String format(String string) {
		String formatted = string;
		for (Map.Entry<String, String> entry : formatMap.entrySet()) {
			formatted = formatted.replace(entry.getKey(), entry.getValue());
		}
		return formatted;
	}
}
