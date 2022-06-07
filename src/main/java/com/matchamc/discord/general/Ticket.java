package com.matchamc.discord.general;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.file.YamlConfiguration;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;

import com.matchamc.discord.MatchaMC_Discord;
import com.matchamc.shared.MsgUtils;

public class Ticket {
	private MatchaMC_Discord instance;
	private User ticketCreator;
	private Integer id;
	private Stage stage;
	private boolean cancelled;
	private File ticketFile;
	private String ign;
	private String supportType;
	private boolean updated = false;

	protected Ticket(MatchaMC_Discord instance, User ticketCreator) { // make one and auto generate id
		this.instance = instance;
		this.ticketCreator = ticketCreator;
		this.id = this.instance.getTicketNumberAndIncrement();
		this.stage = Stage.PRE_CREATION;
		this.cancelled = false;
		this.ticketFile = createTicketFile();
	}

	protected Ticket(MatchaMC_Discord instance, int id) {
		this.instance = instance;
		this.id = id;
		this.ticketFile = new File(instance.getTicketsDirectory(), (String.format("%02d", this.id) + ".yml"));
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(this.ticketFile);
		long creatorId = yc.getLong("ticket-creator");
		String stringStage = yc.getString("stage");
		this.cancelled = yc.getBoolean("cancelled");
		if(stringStage == null || stringStage.isBlank()) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] Ticket file '" + ticketFile.getName() + "' is corrupt.");
			return;
		}
		if(creatorId == 0L) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] Ticket file '" + ticketFile.getName() + "' is corrupt.");
			return;
		}
		this.ticketCreator = instance.api().getUserById(creatorId).join();
		this.stage = Stage.valueOf(stringStage);
	}

	public static Ticket createTicket(MatchaMC_Discord instance, User ticketCreator) {
		return new Ticket(instance, ticketCreator);
	}

	public static Ticket getTicketFromId(MatchaMC_Discord instance, int id) {
		return new Ticket(instance, id);
	}

	public void beginStage() {
		Message[] message = new Message[8];
		if(stage == Stage.PRE_CREATION) {
			// ask questions and await reply using CompletableFuture<String>
				DMQuestion question = new DMQuestion(ticketCreator, this, "What is your in-game name? [Input 'N/A' if this is not applicable, 'cancel' to cancel ticket creation]", 1);
				DMQuestion question2 = new DMQuestion(ticketCreator, this, "What type of support do you need? [In-game, Discord, Buycraft issues etc., 'cancel' to cancel this ticket creation]", 2);
				Thread thread = new Thread() {
					@Override
					public void run() {
						question.ask();
						String reply = question.getFuture().join(); // Locks thread until complete
						if(reply.equalsIgnoreCase("cancel"))
							return;
						question.getTicket().setIngameName(reply);
						question2.ask();
						String reply2 = question2.getFuture().join(); // Locks thread until complete
						if(reply2.equalsIgnoreCase("cancel"))
							return;
						question.getTicket().setSupportType(reply2);
						message[0] = ticketCreator.sendMessage(new EmbedBuilder().setColor(Color.CYAN).setTitle("Creating ticket...").setDescription("Your ticket is being created, please wait...")).exceptionally(ExceptionLogger.get()).join();
						question.getTicket().nextStage();
						question.getTicket().beginStage();
						question.getTicket().saveChanges();
					}
				};
				thread.start();
				return;
		}
		if(stage == Stage.ONGOING) {
			YamlConfiguration config = instance.getDiscordConfig();
			Permissions permissions = new PermissionsBuilder().setAllowed(PermissionType.SEND_MESSAGES, PermissionType.READ_MESSAGES, PermissionType.READ_MESSAGE_HISTORY).setDenied(PermissionType.CREATE_PUBLIC_THREADS, PermissionType.CREATE_INSTANT_INVITE, PermissionType.CREATE_PRIVATE_THREADS).build();
			// Create channel under proper category, create permission overwrites, etc.
			ServerTextChannel channel = instance.getServer().createTextChannelBuilder().setName(config.getString("ticket.creation.ticket-channel-format").replace("%id%", String.format("%02d", this.id))).setCategory(instance.api().getChannelById(config.getLong("ticket.creation.category-id-to-create-ticket")).get().asChannelCategory().get()).addPermissionOverwrite(instance.api().getRoleById(config.getLong("ticket.staff-role-id")).get(), permissions).addPermissionOverwrite(ticketCreator, permissions).addPermissionOverwrite(instance.api().getYourself(), new PermissionsBuilder().setAllAllowed().build()).addPermissionOverwrite(instance.getServer().getEveryoneRole(), new PermissionsBuilder().setAllDenied().build()).create().join();
			if(message.length > 0)
				message[0].edit(new EmbedBuilder().setColor(Color.GREEN).setTitle("Ticket Created!").setDescription("Your ticket has been created on the server as " + channel.getMentionTag() + "."));
			channel.sendMessage(new EmbedBuilder().setColor(Color.CYAN).setFooter("MatchaMail").setTitle("Ticket Details").addField("Player IGN", ign).addField("Support Type Needed", supportType)).exceptionally(ExceptionLogger.get());
			channel.sendMessage(new EmbedBuilder().setColor(Color.YELLOW).setTitle("Please wait patiently...").setDescription(ticketCreator.getMentionTag() + ", please wait patiently and a staff will be here to assist you")).exceptionally(ExceptionLogger.get()).thenAccept(msg -> instance.api().getThreadPool().getScheduler().schedule(() -> msg.delete(), 30, TimeUnit.SECONDS));
			return;
		}
		return;
	}

	public void setIngameName(String name) {
		ign = name;
		updated = true;
	}

	public void setSupportType(String type) {
		supportType = type;
		updated = true;
	}

	public void saveChanges() {
		if(!updated)
			return;
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(ticketFile);
		yc.set("ticket-creator", ticketCreator.getId());
		yc.set("stage", stage.name().toUpperCase());
		yc.set("cancelled", cancelled);
		yc.set("id", this.id);
		yc.set("mc-ign", ign);
		yc.set("supportType", supportType);
		try {
			yc.save(ticketFile);
		} catch(IOException ex) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] Failed to save ticket file - Changes could not be saved.");
			ex.printStackTrace();
			return;
		}
		YamlConfiguration.loadConfiguration(ticketFile);
		updated = false;
	}

	private File createTicketFile() {
		String stringId = String.format("%02d", this.id);
		File file = new File(this.instance.getTicketsDirectory(), stringId + ".yml");
		if(file.exists())
			return file;
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
		yc.set("ticket-creator", ticketCreator.getId());
		yc.set("stage", stage.name().toUpperCase());
		yc.set("cancelled", cancelled);
		yc.set("id", this.id);
		yc.set("mc-ign", "");
		yc.set("supportType", "");
		try {
			yc.save(file);
		} catch(IOException ex) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] Failed to create ticket file - Could not save.");
			ex.printStackTrace();
			return ticketFile;
		}
		YamlConfiguration.loadConfiguration(file);
		return file;
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
			updated = true;
			return stage;
		case ONGOING:
			stage = Stage.CLOSED;
			updated = true;
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
		cancelled = true;
		updated = true;
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
