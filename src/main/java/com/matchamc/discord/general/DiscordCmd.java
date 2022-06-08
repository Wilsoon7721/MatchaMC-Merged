package com.matchamc.discord.general;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import com.matchamc.discord.MatchaMC_Discord;

public abstract class DiscordCmd implements MessageCreateListener {
	protected MatchaMC_Discord instance;
	protected String prefix;

	public DiscordCmd(MatchaMC_Discord instance) {
		this.instance = instance;
		this.prefix = this.instance.prefix;
	}
	@Override
	public abstract void onMessageCreate(MessageCreateEvent event);
}
