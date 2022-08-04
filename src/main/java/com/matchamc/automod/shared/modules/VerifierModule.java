package com.matchamc.automod.shared.modules;

import java.text.Normalizer;
import java.util.Collection;
import java.util.regex.Pattern;

import com.matchamc.automod.shared.ChatPlayer;
import com.matchamc.automod.shared.Module;

public class VerifierModule implements Module {
	private boolean enabled;
	private boolean modified = false;
	private boolean whitelistNames;
	private Collection<String> expressions;
	private Collection<String> playerNames;
	private Collection<String> commands;
	private Pattern pattern, namesPattern;

	public void loadModule(boolean enabled, boolean whitelistNames, Collection<String> commands, Collection<String> expressions, Collection<String> names) {
		this.enabled = enabled;
		this.expressions = expressions;
		this.whitelistNames = whitelistNames;
		this.playerNames = names;
		this.commands = commands;
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
		return this.enabled;
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

	public Collection<String> getCheckedCommands() {
		return commands;
	}

	public Pattern getExpressionsPattern() {
		return pattern;
	}

	public Pattern getNamesPattern() {
		return namesPattern;
	}

	public void addName(String name) {
		modified = playerNames.add(name);
	}

	public void removeName(String name) {
		modified = playerNames.remove(name);
	}

	public void reloadPatterns() {
		if(!(modified))
			return;
		this.pattern = createPatternFromCollection(expressions);
		this.namesPattern = createPatternFromCollection(playerNames);
		modified = false;
	}

	public boolean checkCommandUsage(String playerMessage) {
		for(String cmd : this.commands) {
			if(playerMessage.toLowerCase().startsWith(cmd + ' '))
				return true;
		}
		return false;
	}

	public String formatMessage(String message) {
		char[] out = new char[message.length()];
		message = Normalizer.normalize(message, Normalizer.Form.NFD);
		for(int j = 0, i = 0, n = message.length(); i < n; i++) {
			char c = message.charAt(i);
			if(c <= ' ')
				out[j++] = c;
		}
		return new String(out).replace("(punto)", ".").replace("(dot)", ".");
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
