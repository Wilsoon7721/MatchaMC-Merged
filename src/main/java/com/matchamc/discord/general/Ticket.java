package com.matchamc.discord.general;

import com.matchamc.discord.MatchaMC_Discord;

public class Ticket {
	private MatchaMC_Discord instance;
	private Integer id;

	public Ticket(MatchaMC_Discord instance) { // make one and auto generate id
		this.instance = instance;
		this.id = instance.getDiscordConfig().getInt("next_ticket_no")
	}

	public int getTicketId() {
		return id;
	}
}
