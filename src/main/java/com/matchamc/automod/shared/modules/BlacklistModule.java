package com.matchamc.automod.shared.modules;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.matchamc.automod.shared.ChatPlayer;
import com.matchamc.automod.shared.Module;
import com.matchamc.shared.MsgUtils;

public class BlacklistModule implements Module {
	private boolean enabled;
	private boolean filter;
	private int maxWarns;
	private Pattern pattern;
	private String bypassPermission = "automod.bypass.blacklist";

	public void loadModule(boolean enabled, boolean filter, int maxWarns, String[] patterns) {
		this.enabled = enabled;
		this.filter = filter;
		this.maxWarns = maxWarns;
		String patternString = "";
		byte b;
		int i;
		String[] arrayOfString;
		for(i = (arrayOfString = patterns).length, b = 0; b < i;) {
			String s = arrayOfString[b];
			patternString = String.format("%s(%s)|", new Object[] { patternString, s });
		}
		try {
			this.pattern = Pattern.compile("(?i)(" + patternString + "(?!x)x)");
		} catch(PatternSyntaxException ex) {
			MsgUtils.sendBukkitConsoleMessage("&cAutoMod - BlacklistModule: Failed to load module - The pattern could not be compiled.");
			ex.printStackTrace();
			return;
		}
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public boolean filterEnabled() {
		return filter;
	}

	@Override
	public boolean isBypassable() {
		return true;
	}

	@Override
	public String getBypassPermission() {
		return bypassPermission;
	}

	@Override
	public boolean meetsCondition(ChatPlayer player, String message) {
		return (this.enabled && this.pattern.matcher(message).find());
	}

	@Override
	public int getMaxWarnings() {
		return maxWarns;
	}

	@Override
	public String getModuleName() {
		return "Blacklist";
	}

	public Pattern getPattern() {
		return pattern;
	}
}
