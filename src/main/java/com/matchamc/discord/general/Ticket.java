package com.matchamc.discord.general;

import java.awt.Color;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import com.matchamc.discord.MatchaMC_Discord;

public class Ticket {
	private MatchaMC_Discord instance;
	private User ticketCreator;
	private Integer id;
	private Stage stage;
	private boolean cancelled;

	public Ticket(MatchaMC_Discord instance, User ticketCreator) { // make one and auto generate id
		this.instance = instance;
		this.ticketCreator = ticketCreator;
		this.id = this.instance.getTicketNumberAndIncrement();
		this.stage = Stage.PRE_CREATION;
		this.cancelled = false;
	}

	public int getTicketId() {
		return id;
	}

	public Stage getStage() {
		return stage;
	}

	public Stage nextStage() {
		switch(stage) {
		case PRE_CREATION:
			stage = Stage.ONGOING;
			return stage;
		case ONGOING:
			stage = Stage.CLOSED;
			return stage;
		default:
			return stage;
		}
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean cancelTicket(User canceller, boolean sendCancellationMessage) {
		if(isCancelled())
			return false;
		if(sendCancellationMessage)
			if(canceller.getId() == ticketCreator.getId())
				ticketCreator.sendMessage(new EmbedBuilder().setTitle("Ticket Cancelled").setDescription("Ticket #" + id + " has been cancelled as per your request.").setColor(Color.CYAN).setFooter("MatchaMail"));
			else
				ticketCreator.sendMessage(new EmbedBuilder().setTitle("Ticket Cancelled").setDescription("Ticket #" + id + " has been cancelled by " + canceller.getDiscriminatedName() + ".").setColor(Color.CYAN).setFooter("MatchaMail"));
		return true;
	}

	public static enum Stage {
		PRE_CREATION, ONGOING, CLOSED;
	}
}
