package com.matchamc.core.conversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import com.matchamc.core.bukkit.util.Punishment;

public class PunishmentOtherOffencePrompt extends StringPrompt {
	private Punishment punishment;

	public PunishmentOtherOffencePrompt(Punishment punishment) {
		this.punishment = punishment;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		return "Insert a reason for the punishment of this player.";
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {
		punishment.setReason(input);
		punishment.executePunishment();
		return Prompt.END_OF_CONVERSATION;
	}
}
