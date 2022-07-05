package com.matchamc.core.conversation;

import java.time.Duration;
import java.time.format.DateTimeParseException;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.util.Punishment;
import com.matchamc.shared.MsgUtils;

public class PunishmentDurationPrompt extends StringPrompt {
	private Punishment punishment;

	public PunishmentDurationPrompt(Punishment punishment) {
		this.punishment = punishment;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		return "Please input a duration for this punishment, or 'permanent' for permanent.";
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {
		if(input.equalsIgnoreCase("permanent")) {
			punishment.setDuration(null);
			return Prompt.END_OF_CONVERSATION;
		}
		try {
			Duration duration = Duration.parse("PT" + input);
			punishment.setDuration(duration);
			((Player) context.getForWhom()).sendMessage(MsgUtils.color("&eSuccessfully set the duration for this punishment to &a" + duration.to));
		} catch(DateTimeParseException ex) {
			((Player) context.getForWhom()).sendMessage(MsgUtils.color("&cThe duration you specified was invalid."));
			return this;
		}
		return null;
	}

}
