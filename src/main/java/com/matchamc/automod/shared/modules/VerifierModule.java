package com.matchamc.automod.shared.modules;

import java.util.Collection;
import java.util.regex.Pattern;

import com.matchamc.automod.shared.ChatPlayer;
import com.matchamc.automod.shared.Module;

public class VerifierModule implements Module {
	private boolean enabled;
	private boolean whitelistNames;
	private Pattern pattern, namesPattern;

	public void loadModule(boolean enabled, boolean whitelistNames, Collection<String> expressions, Collection<String> names) {
		this.enabled = enabled;
		this.whitelistNames = whitelistNames;
		this.pattern = createPatternFromCollection(expressions);
		this.namesPattern = createPatternFromCollection(names);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isBypassable() {
		return false;
	}

	@Override
	public String getBypassPermission() {
		return null;
	}

	@Override
	public boolean meetsCondition(ChatPlayer player, String message) {

	}

	@Override
	public int getMaxWarnings() {
		return -1;
	}

	@Override
	public String getModuleName() {
		return "Verifier";
	}

	public boolean isWhitelistNames() {
		return whitelistNames;
	}

	public Pattern getExpressionsPattern() {
		return pattern;
	}

	public Pattern getNamesPattern() {
		return namesPattern;
	}

	private Pattern createPatternFromCollection(Collection<String> collection) {
		if(collection.isEmpty())
			return Pattern.compile("(?!x)x");
		StringBuilder regex = new StringBuilder();
		for(String s : collection)
			regex.append("|" + s);
		return Pattern.compile("(?i)((?!x)x" + regex.toString() + ")");
	}

}
