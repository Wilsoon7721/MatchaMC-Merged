package com.matchamc.discord;

import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class TicketCreationPMListener implements MessageCreateListener {
	private User targetUser;

	public TicketCreationPMListener(User targetUser) {
		this.targetUser = targetUser;
	}

	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		if(event.getMessageAuthor().getId() != targetUser.getId())
			return;
		String message = event.getReadableMessageContent();
		if(message.equalsIgnoreCase("cancel")) {
			// Cancel everything
		}
		// Mess with data
	}
}
