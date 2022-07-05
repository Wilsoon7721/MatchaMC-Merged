package com.matchamc.core.bukkit.util;

import java.time.Duration;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.Punishment.Type;
import com.matchamc.core.conversation.PunishmentDurationPrompt;
import com.matchamc.core.conversation.PunishmentReasonPrompt;

public class Punishments implements Listener {
	public static final String PLUGIN_PUNISHMENT_NAME = "MatchaMC Punishments (%executor%)";
	public static final UUID PLUGIN_PUNISHMENT_UUID = UUID.fromString("740c1ecf-8cde-4c24-8b02-9ffbc76ff7f2");
	private BukkitMain instance;
	private PlayerRegistrar registrar;
	private String permissionBan, permissionMute, permissionKick, permissionWarn;

	public Punishments(BukkitMain instance, PlayerRegistrar registrar) {
		this.instance = instance;
		this.registrar = registrar;
		permissionBan = "staffcore.punish.ban";
		permissionMute = "staffcore.punish.mute";
		permissionKick = "staffcore.punish.kick";
		permissionWarn = "staffcore.punish.warn";
	}

	public Punishment createPunishment(Punishment.Type type, UUID executor, UUID punished, String reason, Duration duration) {
		return new Punishment(instance, registrar, type, executor, punished, reason, duration);
	}

	// GUIs
	public void openPunishmentGUI(Player player, UUID punished) {
		String punishedName = registrar.getNameFromRegistrar(punished);
		Inventory inv = Bukkit.createInventory(null, 9, "Issue Punishment: " + punishedName);
		if(player.hasPermission(permissionBan)) {
			ItemStack banItem = new ItemBuilder(Material.DIAMOND_AXE).withDisplayName("&cBan player").toItemStack();
			inv.addItem(banItem);
		}
		if(player.hasPermission(permissionKick)) {
			ItemStack kickItem = new ItemBuilder(Material.IRON_BOOTS).withDisplayName("&cKick player").toItemStack();
			inv.addItem(kickItem);
		}
		if(player.hasPermission(permissionMute)) {
			ItemStack muteItem = new ItemBuilder(Material.INK_SAC).withDisplayName("&cMute player").toItemStack();
			inv.addItem(muteItem);
		}
		if(player.hasPermission(permissionWarn)) {
			ItemStack warnItem = new ItemBuilder(Material.PAPER).withDisplayName("&cWarn player").toItemStack();
			inv.addItem(warnItem);
		}
		if(inv.getContents().length == 0) {
			ItemStack none = new ItemBuilder(Material.BARRIER).withDisplayName("&cYou do not have permission to perform any action.").toItemStack();
			inv.addItem(none);
		}
		ItemStack cancel = new ItemBuilder(Material.BARRIER).withDisplayName("&cCancel").toItemStack();
		inv.setItem(8, cancel);
		player.openInventory(inv);
	}

	@EventHandler
	public void onPunishmentGUIClick(InventoryClickEvent event) {
		if(!event.getView().getTitle().startsWith("Issue Punishment: "))
			return;
		if(event.getCurrentItem() == null)
			return;
		event.setCancelled(true);
		String punishedName = event.getView().getTitle().split(" ")[2];
		UUID punished = registrar.resolveUUIDFromName(punishedName);
		ConversationFactory factory = new ConversationFactory(instance);
		Punishment punishment;
		Conversation conv;
		switch(event.getCurrentItem().getType()) {
		case DIAMOND_AXE:
			punishment = createPunishment(Type.BAN, event.getWhoClicked().getUniqueId(), punished, null, null);
			conv = factory.withFirstPrompt(new PunishmentReasonPrompt(punishment, new PunishmentDurationPrompt(punishment))).buildConversation((Player) event.getWhoClicked());
			((Player) event.getWhoClicked()).beginConversation(conv);
			break;
		case IRON_BOOTS:
			punishment = createPunishment(Type.KICK, event.getWhoClicked().getUniqueId(), punished, null, null);
			conv = factory.withFirstPrompt(new PunishmentReasonPrompt(punishment, Prompt.END_OF_CONVERSATION)).buildConversation((Player) event.getWhoClicked());
			((Player) event.getWhoClicked()).beginConversation(conv);
			break;
		case INK_SAC:
			punishment = createPunishment(Type.MUTE, event.getWhoClicked().getUniqueId(), punished, null, null);
			conv = factory.withFirstPrompt(new PunishmentReasonPrompt(punishment, new PunishmentDurationPrompt(punishment))).buildConversation((Player) event.getWhoClicked());
			((Player) event.getWhoClicked()).beginConversation(conv);
			break;
		case PAPER:
			punishment = createPunishment(Type.WARN, event.getWhoClicked().getUniqueId(), punished, null, null);
			conv = factory.withFirstPrompt(new PunishmentReasonPrompt(punishment, Prompt.END_OF_CONVERSATION)).buildConversation((Player) event.getWhoClicked());
			((Player) event.getWhoClicked()).beginConversation(conv);
			break;
		default:
			break;
		}
	}
}
