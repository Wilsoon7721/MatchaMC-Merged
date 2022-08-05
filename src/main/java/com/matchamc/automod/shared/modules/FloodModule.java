package com.matchamc.automod.shared.modules;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.matchamc.automod.shared.ChatPlayer;
import com.matchamc.automod.shared.Module;
import com.matchamc.shared.MsgUtils;

public class FloodModule implements Module {
	private boolean enabled;
	private boolean replace;
	private int maxWarns;
	private String bypassPermission = "automod.bypass.flood";
	private Pattern pattern;
	private Map<Integer, String> actionCommands;

	public void loadModule(boolean enabled, boolean replace, int maxWarns, String pattern, Map<Integer, String> actionCommands) {
		this.enabled = enabled;
		this.replace = replace;
		this.maxWarns = maxWarns;
		try {
			this.pattern = Pattern.compile(pattern);
		} catch(PatternSyntaxException ex) {
			MsgUtils.sendBukkitConsoleMessage("AutoMod - FloodModule: Failed to load module - The pattern could not be compiled.");
			ex.printStackTrace();
		}
		this.actionCommands = actionCommands;
		return;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public boolean isReplace() {
		return replace;
	}

	@Override
	public boolean isBypassable() {
		return true;
	}

	@Override
	public boolean meetsCondition(ChatPlayer player, String message) {
		return (this.enabled && pattern.matcher(message).find());
	}

	@Override
	public int getMaxWarnings() {
		return maxWarns;
	}

	@Override
	public String getModuleName() {
		return "Flood";
	}

	@Override
	public String getBypassPermission() {
		return bypassPermission;
	}

	public String replace(String msg) {
		return MsgUtils.color(pattern.matcher(msg).replaceAll(""));
	}

	@Override
	public Map<Integer, String> getActionCommands() {
		return actionCommands;
	}
}
