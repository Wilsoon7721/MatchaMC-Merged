package com.matchamc.discord;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.util.logging.ExceptionLogger;

import com.matchamc.core.bungee.util.Configurations;

import net.md_5.bungee.config.Configuration;

public class RequestStaffReactionListener implements ReactionAddListener {
	private MatchaMC_Discord discordInstance;
	private Server server;
	private Configuration discordConfig;
	private String modMailMentionTag = "<:mail:759413078843719750>";

	public RequestStaffReactionListener(Server server, MatchaMC_Discord discordInstance, Configurations configurations) {
		this.server = server;
		this.discordInstance = discordInstance;
		this.discordConfig = configurations.get("discord.yml");
	}

	@Override
	public void onReactionAdd(ReactionAddEvent event) {
		if(event.getUser().get().getId() == event.getApi().getYourself().getId()) return;
		if(!event.getMessage().isPresent())
			return;
		long checkMessageId = discordConfig.getLong("help-channel.message-to-check");
		if(event.getMessage().get().getId() != checkMessageId)
			return;
		if(!event.getReaction().isPresent())
			return;
		if(discordInstance.canRemoveReactions()) {
			event.removeReaction().exceptionally(ExceptionLogger.get());	
		} else {
			server.getTextChannelById(discordConfig.getLong("error-output-channel")).ifPresent(channel -> {
				channel.sendMessage(new EmbedBuilder().setTitle("Error").setColor(Color.CYAN).setDescription("Attempted to remove reaction, but the bot does not have permission")).exceptionally(ExceptionLogger.get());
			});
		}
		User targetUser = event.requestUser().join();
		event.getChannel().sendMessage(new EmbedBuilder().setTitle("Check your DMs!").setDescription(targetUser.getMentionTag() + ", please check your DMs for the message sent by me!").setFooter("MatchaMail")).thenAccept(msg -> event.getApi().getThreadPool().getScheduler().schedule(() -> msg.delete(), 15, TimeUnit.SECONDS));
		targetUser.removeList
	}

	public int getTicketNumberAndIncrease() {
		int ticketNumber = discordConfig.getInt("help-channel.next-ticket-number");
		discordConfig.set("help-channel.next-ticket-number", (ticketNumber + 1));
		return ticketNumber;
	}
}
