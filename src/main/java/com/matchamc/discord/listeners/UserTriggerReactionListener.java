package com.matchamc.discord.listeners;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.util.logging.ExceptionLogger;

import com.matchamc.discord.MatchaMC_Discord;
import com.matchamc.discord.general.Ticket;

public class UserTriggerReactionListener implements ReactionAddListener {
	private MatchaMC_Discord instance;
	private String emoteMentionTag, emoteReactionTag;

	public UserTriggerReactionListener(MatchaMC_Discord instance) {
		this.instance = instance;
		emoteMentionTag = this.instance.getDiscordConfig().getString("ticket.trigger.reaction.emote-mention-tag");
		emoteReactionTag = this.instance.getDiscordConfig().getString("ticket.trigger.reaction.emote-reaction-tag");
	}

	@Override
	public void onReactionAdd(ReactionAddEvent event) {
		User user = event.requestUser().join();
		Reaction reaction =  event.requestReaction().join().orElse(null);
		if(user.getId() == instance.api().getYourself().getId())
			return;
		if(reaction == null || !reaction.getEmoji().getMentionTag().equalsIgnoreCase(emoteMentionTag))
			return;
		event.removeReaction().join();
		event.getChannel().sendMessage(new EmbedBuilder().setTitle("Check your DMs!").setDescription(user.getMentionTag() + ", please check your DMs for the message sent by me.").setColor(Color.CYAN)).exceptionally(ExceptionLogger.get()).thenAccept(msg -> event.getApi().getThreadPool().getScheduler().schedule(() -> msg.delete(), 30, TimeUnit.SECONDS));
		Ticket ticket = Ticket.createTicket(instance, user);
		ticket.beginStage();
	}
}
