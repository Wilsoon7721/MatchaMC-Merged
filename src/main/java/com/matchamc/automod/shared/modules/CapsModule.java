package com.matchamc.automod.shared.modules;

import com.matchamc.automod.shared.ChatPlayer;
import com.matchamc.automod.shared.Module;

public class CapsModule implements Module {
	private boolean enabled;
	private boolean replace;
	private int maxCaps;
	private int maxWarns;
	private String warnNotification;

	public void loadModule() {

	}

	@Override
	public boolean meetsCondition(ChatPlayer player, String message) {

	}

	@Override
	public int getMaxWarnings() {

	}

	@Override
	public String getModuleName() {
		return "Caps";
	}

	@Override
	public String getWarnNotification() {

	}

}
