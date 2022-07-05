package com.matchamc.core.conversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.util.Punishment;
import com.matchamc.shared.MsgUtils;

public class PunishmentReasonPrompt extends StringPrompt {
	private Punishment punishment;
	private Prompt nextPrompt;

	public PunishmentReasonPrompt(Punishment punishment, Prompt nextPrompt) {
		this.punishment = punishment;
		this.nextPrompt = nextPrompt;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		return "Please input your custom reason for this punishment.";
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {
		punishment.setReason(input);
		((Player) context.getForWhom()).sendMessage(MsgUtils.color("&eSet the reason for this punishment to &a'" + input + "'&e."));
		return nextPrompt;
	}
}
