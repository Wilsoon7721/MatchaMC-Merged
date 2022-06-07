package com.matchamc.discord.listeners;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import com.matchamc.discord.general.DMQuestion;

public class QuestionMsgCreateListener implements MessageCreateListener {
	private DMQuestion question;

	public QuestionMsgCreateListener(DMQuestion question) {
		this.question = question;
	}

	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		if(event.getMessageAuthor().getId() != question.getUser().getId())
			return;
		if(!event.getPrivateChannel().isPresent())
			return;
		if(event.getReadableMessageContent().equalsIgnoreCase("cancel")) {
			question.cancel();
			return;
		}
		question.setReply(event.getReadableMessageContent());
		question.getFuture().complete(event.getReadableMessageContent());
	}
}
