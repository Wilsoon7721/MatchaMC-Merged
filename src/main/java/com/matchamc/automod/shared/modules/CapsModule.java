package com.matchamc.automod.shared.modules;

import com.matchamc.automod.shared.ChatPlayer;
import com.matchamc.automod.shared.Module;

public class CapsModule implements Module {
	private boolean enabled;
	private boolean replace;
	private int maxCaps;
	private int maxWarns;
	private String bypassPermission = "automod.bypass.caps";

	public void loadModule(boolean enabled, boolean replace, int maxCaps, int maxWarns) {
		this.enabled = enabled;
		this.replace = replace;
		this.maxCaps = maxCaps;
		this.maxWarns = maxWarns;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public boolean isReplace() {
		return replace;
	}

	@Override
	public boolean meetsCondition(ChatPlayer player, String message) {
		if(!enabled)
			return false;
		if(message.codePoints().filter(c -> (c >= 65 && c <= 90)).count() > maxCaps)
			return true;
		return false;
	}

	public int getCapsThreshold() {
		return maxCaps;
	}

	public int capsCount(String message) {
		return (int) message.codePoints().filter(c -> (c >= 65 && c <= 90)).count();
	}

	@Override
	public int getMaxWarnings() {
		return maxWarns;
	}

	@Override
	public String getModuleName() {
		return "Caps";
	}

	@Override
	public boolean isBypassable() {
		return true;
	}

	@Override
	public String getBypassPermission() {
		return bypassPermission;
	}

}
