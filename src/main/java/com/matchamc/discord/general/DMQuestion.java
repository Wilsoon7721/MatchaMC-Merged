package com.matchamc.discord.general;

import java.awt.Color;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;

import com.matchamc.discord.listeners.QuestionMsgCreateListener;

public class DMQuestion {
	private User userToAsk;
	private String reply = "";
	private String question;
	private Ticket ticket;
	private int questionNumber;
	private CompletableFuture<String> future = new CompletableFuture<>();

	protected DMQuestion(User userToAsk, Ticket ticket, String question, int questionNumber) {
		this.userToAsk = userToAsk;
		this.ticket = ticket;
		this.question = question;
		this.questionNumber = questionNumber;
		future = future.orTimeout(30, TimeUnit.SECONDS);
	}

	public void ask() {
		this.userToAsk.openPrivateChannel().join().sendMessage(new EmbedBuilder().setColor(Color.YELLOW).setTitle("Question " + questionNumber).setDescription(question).setImage("https://toppng.com/uploads/preview/question-mark-icon-png-11552242874yprntn7fkf.png")).exceptionally(ExceptionLogger.get()).join();
		this.userToAsk.addMessageCreateListener(new QuestionMsgCreateListener(this));
	}

	public CompletableFuture<String> getFuture() {
		return future;
	}

	public String getReply() {
		return reply;
	}

	public void cancel() {
		ticket.cancelTicket(userToAsk, true);
	}

	public User getUser() {
		return userToAsk;
	}

	public boolean replyGiven() {
		return !(reply.isBlank());
	}

	public void setReply(String message) {
		reply = message;
	}

	public Ticket getTicket() {
		return ticket;
	}
}
