package com.matchamc.core.bungee;

import com.matchamc.shared.util.MsgUtils;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin {

	@Override
	public void onEnable() {
		MsgUtils.sendConsoleMessage("&aEnabling MatchaMC [Bungee] version " + getDescription().getVersion());

	}
}
