package com.matchamc.automod.shared.modules;

import com.matchamc.automod.shared.ChatPlayer;
import com.matchamc.automod.shared.Module;

public class CapsModule implements Module {
	private boolean enabled;
	private boolean replace;
	private int maxCaps;
	private int maxWarns;
	private String warnNotification;

	public void loadModule(boolean enabled, boolean replace, int maxCaps, int maxWarns, String warnNotification) {
		this.enabled = enabled;
		this.replace = replace;
		this.maxCaps = maxCaps;
		this.maxWarns = maxWarns;
		this.warnNotification = warnNotification;
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
		if(message.codePoints().filter(c -> (c >= 65 && c <= 90)).count() > maxCaps)
			return true;
		return false;
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
	public String getWarnNotification() {
		return warnNotification;
	}

}
