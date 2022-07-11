package com.matchamc.automod.shared;

public interface Module {

	boolean meetsCondition(ChatPlayer player, String message);

	int getMaxWarnings();

	String getModuleName();

	String getWarnNotification();
}
