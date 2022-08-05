package com.matchamc.automod.shared;

import java.util.Map;

public interface Module {

	boolean isEnabled();

	boolean isBypassable();

	String getBypassPermission();

	boolean meetsCondition(ChatPlayer player, String message);

	int getMaxWarnings();

	String getModuleName();

	Map<Integer, String> getActionCommands();
}
