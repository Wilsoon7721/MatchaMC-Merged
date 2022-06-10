package com.matchamc.discord;

import java.awt.Color;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.util.logging.ExceptionLogger;

import com.matchamc.discord.general.DiscordCmd;

public class CloseTicketCmd extends DiscordCmd {
	private String strippedChannelFormat;
	public CloseTicketCmd(MatchaMC_Discord instance) {
		super(instance);
		this.strippedChannelFormat = this.instance.getDiscordConfig().getString("ticket.creation.ticket-channel-format").replace("%id%", "");
	}

//TODO CLOSE TICKET
	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		if(event.getMessageAuthor().isBotUser() || event.getMessageAuthor().isWebhook())
			return;
		if(!event.getMessageContent().startsWith(prefix + "close"))
			return;
		if(event.getChannel().asServerTextChannel().isEmpty()) {
			event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Error").setDescription("This is not a ticket!").setFooter("Channel is not a ServerTextChannel! | MatchaMail")).exceptionally(ExceptionLogger.get()).thenAccept(msg -> event.getApi().getThreadPool().getScheduler().schedule(() -> msg.delete(), 10, TimeUnit.SECONDS));
			return;
		}
		String[] t = event.getMessageContent().split(" ");
		String[] args = Arrays.copyOfRange(t, 1, t.length);
		String channelName = event.getServerTextChannel().get().getName();
		if(!channelName.contains(strippedChannelFormat)) {
			event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Error").setDescription("This is not a ticket!").setFooter("Channel format does not match the stripped ticket channel format! | MatchaMail")).exceptionally(ExceptionLogger.get()).thenAccept(msg -> event.getApi().getThreadPool().getScheduler().schedule(() -> msg.delete(), 10, TimeUnit.SECONDS));
			return;
		}
		if(args.length == 0) {
			event.getChannel().sendMessage(new EmbedBuilder().setTitle("Closing Ticket...").setFooter("Initiated by " + event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()).setDescription("This ticket will be closed in 10 seconds. Send a message to cancel ticket closure.")).exceptionally(ExceptionLogger.get());
			return;
		}
	}

}
