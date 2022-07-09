package com.matchamc.core.bukkit.util;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
import com.matchamc.core.conversation.PunishmentOtherOffencePrompt;
import com.matchamc.core.conversation.PunishmentReasonPrompt;
import com.matchamc.shared.MsgUtils;

public class Punishments implements Listener {
	public static final String PLUGIN_PUNISHMENT_NAME = "MatchaMC Punishments (%executor%)";
	public static final UUID PLUGIN_PUNISHMENT_UUID = UUID.fromString("740c1ecf-8cde-4c24-8b02-9ffbc76ff7f2");
	private BukkitMain instance;
	private PlayerRegistrar registrar;
	private Map<PunishmentData, Punishment> punishments = new HashMap<>();
	private String permissionBan, permissionIpBan, permissionMute, permissionKick, permissionWarn;

	public Punishments(BukkitMain instance, PlayerRegistrar registrar) {
		this.instance = instance;
		this.registrar = registrar;
		permissionBan = "staffcore.punish.ban";
		permissionIpBan = "staffcore.punish.ipban";
		permissionMute = "staffcore.punish.mute";
		permissionKick = "staffcore.punish.kick";
		permissionWarn = "staffcore.punish.warn";
	}

	public Punishment createPunishment(Punishment.Type type, UUID executor, UUID punished, String reason, Duration duration) {
		Punishment p = new Punishment(instance, registrar, type, executor, punished, reason, duration);
		PunishmentData data = new PunishmentData(executor, punished);
		for(Entry<PunishmentData, Punishment> entry : punishments.entrySet()) {
			if(!entry.getValue().getPunished().toString().equalsIgnoreCase(punished.toString()))
				continue;
			punishments.remove(entry.getKey());
		}
		punishments.put(data, p);
		return p;
	}

	// GUIs
	public void openPunishmentGUI(Player player, UUID punished) {
		String punishedName = registrar.getNameFromRegistrar(punished);
		Inventory inv = Bukkit.createInventory(null, 9, "Issue Punishment: " + punishedName);
		if(player.hasPermission(permissionBan)) {
			ItemStack banItem = new ItemBuilder(Material.DIAMOND_AXE).withDisplayName("&cBan player").toItemStack();
			inv.addItem(banItem);
		}
		if(player.hasPermission(permissionIpBan)) {
			ItemStack ipBanItem = new ItemBuilder(Material.BEDROCK).withDisplayName("&cIP-Ban player").toItemStack();
			inv.addItem(ipBanItem);
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

	public void openPunishmentCategoryGUI(Player player, Punishment punishment) {
		String punishedName = registrar.getNameFromRegistrar(UUID.fromString(punishment.getPunished()));
		Inventory inv = Bukkit.createInventory(null, 9, "Punishment Category: " + punishedName);
		ItemStack modifications = new ItemBuilder(Material.NETHERITE_SWORD).withDisplayName("&cUnfair Advantages").withLore(Arrays.asList("&eE.g. Killaura, Aimbot, AutoClicker etc.")).toItemStack();
		ItemStack chat = new ItemBuilder(Material.PAPER).withDisplayName("&cChat Offences").withLore(Arrays.asList("&eE.g. Spamming, Swearing, Advertising, Toxic Behaviour etc.")).toItemStack();
		ItemStack other = new ItemBuilder(Material.BEDROCK).withDisplayName("&cOther").withLore(Arrays.asList("&eE.g. DDOS/DoX Threats, Inappropriate Name/Skin, Scamming, Lag Machines etc.")).toItemStack();
		ItemStack bsgp = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).withDisplayName(" ").toItemStack();
		inv.setItem(2, modifications);
		inv.setItem(4, chat);
		inv.setItem(6, other);
		for(int i = 0; i < 9; i++) {
			if(inv.getItem(i) == null)
				inv.setItem(i, bsgp);
			continue;
		}
		player.openInventory(inv);
	}

	public void openUnfairAdvantagesGUI(Player player, Punishment punishment) {
		String punishedName = registrar.getNameFromRegistrar(UUID.fromString(punishment.getPunished()));
		Inventory inv = Bukkit.createInventory(null, 9, "Unfair Advantages: " + punishedName);
		ItemStack pvphacks = new ItemBuilder(Material.IRON_SWORD).withDisplayName("&cCombat Related Hacks").withLore(Arrays.asList("&eE.g. Killaura, TP-Aura, Aimbot, Autoclicker")).toItemStack();
		ItemStack fly = new ItemBuilder(Material.FEATHER).withDisplayName("&cFly Hacks").withLore(Arrays.asList("&eE.g. Flight, CreativeFly, Jetpack etc.")).toItemStack();
		ItemStack xray = new ItemBuilder(Material.DIAMOND_ORE).withDisplayName("&cXray Hacks").toItemStack();
		ItemStack movementhacks = new ItemBuilder(Material.SUGAR).withDisplayName("&cMovement Related Hacks").withLore(Arrays.asList("&eE.g. Speed, Bunnyhop, Spider, Step")).toItemStack();
		ItemStack autohacks = new ItemBuilder(Material.MUSHROOM_STEW).withDisplayName("&cAutomatic Hacks").withLore(Arrays.asList("&eE.g. Autosoup, AutoPot, AutoTool, AutoMine")).toItemStack();
		ItemStack teleporthacks = new ItemBuilder(Material.ENDER_PEARL).withDisplayName("&cTeleportation Hacks").withLore(Arrays.asList("&eE.g. ClickTP")).toItemStack();
		ItemStack renderhacks = new ItemBuilder(Material.COMPASS).withDisplayName("&cRender Hacks").withLore(Arrays.asList("&eE.g. ESPs (Player, Mob, Chest, Other), Fullbright")).toItemStack();
		ItemStack otherhacks = new ItemBuilder(Material.BRICKS).withDisplayName("&cOther Cheats").withLore(Arrays.asList("&eHacks that are not listed here.")).toItemStack();
		ItemStack cancel = new ItemBuilder(Material.BARRIER).withDisplayName("Cancel").withLore(Arrays.asList("&eReturn to the report category menu")).toItemStack();
		inv.addItem(new ItemStack[] { fly, xray, pvphacks, movementhacks, autohacks, teleporthacks, renderhacks, otherhacks });
		inv.setItem(8, cancel);
		player.openInventory(inv);
	}

	public void openChatOffencesGUI(Player player, Punishment punishment) {
		String punishedName = registrar.getNameFromRegistrar(UUID.fromString(punishment.getPunished()));
		Inventory inv = Bukkit.createInventory(null, 9, "Chat Offences: " + punishedName);
		ItemStack swearing = new ItemBuilder(Material.PUFFERFISH).withDisplayName("&cSwearing").toItemStack();
		ItemStack illictLinks = new ItemBuilder(Material.FIRE_CHARGE).withDisplayName("&cIllict Links").toItemStack();
		ItemStack toxicity = new ItemBuilder(Material.SKELETON_SKULL).withDisplayName("&cToxic Behaviour").toItemStack();
		ItemStack spam = new ItemBuilder(Material.MAP).withDisplayName("&cSpamming").toItemStack();
		ItemStack chatTrolling = new ItemBuilder(Material.CREEPER_HEAD).withDisplayName("&cChat Trolling").toItemStack();
		ItemStack advertising = new ItemBuilder(Material.PAPER).withDisplayName("&cAdvertising").toItemStack();
		ItemStack cancel = new ItemBuilder(Material.BARRIER).withDisplayName("Cancel").withLore(Arrays.asList("&eReturn to the report category menu")).toItemStack();
		inv.addItem(new ItemStack[] { swearing, illictLinks, toxicity, spam, chatTrolling, advertising });
		inv.setItem(8, cancel);
		player.openInventory(inv);
	}

	public void openOtherOffencesGUI(Player player, Punishment punishment) {
		String punishedName = registrar.getNameFromRegistrar(UUID.fromString(punishment.getPunished()));
		Inventory inv = Bukkit.createInventory(null, 9, "Other Offences: " + punishedName);
		ItemStack ddosDoxThreats = new ItemBuilder(Material.GUNPOWDER).withDisplayName("&cDDoS/DoX Threats").withLore(Arrays.asList("&ePlayer threatened to DDoS/DoX you or another player.")).toItemStack();
		ItemStack inappropriatenameskin = new ItemBuilder(Material.NAME_TAG).withDisplayName("&cInappropriate Name/Skin").withLore(Arrays.asList("&ePlayer has an inappropriate name or is using a skin that depicts nudity or other inappropriate stuff.")).toItemStack();
		ItemStack scamming = new ItemBuilder(Material.GOLD_INGOT).withDisplayName("&cScamming").withLore(Arrays.asList("&ePlayer scammed you of in-game money or stuff")).toItemStack();
		ItemStack lagmachines = new ItemBuilder(Material.REDSTONE_TORCH).withDisplayName("&cLag Machines").withLore(Arrays.asList("&ePlayer is creating lag machines that contribute lag to the server, or causing fps spikes to occur.")).toItemStack();
		ItemStack other = new ItemBuilder(Material.HEART_OF_THE_SEA).withDisplayName("Other").withLore(Arrays.asList("&ePlayer is breaking a network rule that is not listed here.", "&bThis will allow you to speak to a staff member.")).toItemStack();
		ItemStack cancel = new ItemBuilder(Material.BARRIER).withDisplayName("Cancel").withLore(Arrays.asList("&eReturn to the report category menu")).toItemStack();
		inv.addItem(new ItemStack[] { ddosDoxThreats, inappropriatenameskin, scamming, lagmachines, other });
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

	@EventHandler
	public void onPunishmentGUICategoryClick(InventoryClickEvent event) {
		if(!event.getView().getTitle().startsWith("Punishment Category: "))
			return;
		if(event.getCurrentItem() == null)
			return;
		event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		UUID punished = registrar.resolveUUIDFromName(event.getView().getTitle().split(" ")[2]);
		Punishment punishment = null;
		for(Entry<PunishmentData, Punishment> entry : punishments.entrySet()) {
			PunishmentData data = entry.getKey();
			if(data.getPunishmentExecutor().toString().equals(event.getWhoClicked().getUniqueId().toString()) && data.getPunished().toString().equals(punished.toString()))
				punishment = entry.getValue();
			continue;
		}
		if(punishment == null) {
			event.getWhoClicked().sendMessage(MsgUtils.color("&cAn internal error occurred."));
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Punishments] The plugin was unable to find the Punishment object in the map >> All PunishmentData objects did not match the given arguments.");
			return;
		}
		switch(event.getCurrentItem().getType()) {
		case NETHERITE_SWORD: // modifications
			event.getWhoClicked().closeInventory();
			openUnfairAdvantagesGUI(player, punishment); // get PunishmentData from map
			break;
		case PAPER: // chat
			event.getWhoClicked().closeInventory();
			openChatOffencesGUI(player, punishment);
			break;
		case BEDROCK: // others
			event.getWhoClicked().closeInventory();
			openOtherOffencesGUI(player, punishment);
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void onUnfairAdvantagesGUIInteract(InventoryClickEvent event) {
		if(!event.getView().getTitle().startsWith("Unfair Advantages: "))
			return;
		if(event.getCurrentItem() == null)
			return;
		event.setCancelled(true);
		UUID punished = registrar.resolveUUIDFromName(event.getView().getTitle().split(" ")[2]);
		Punishment punishment = null;
		for(Entry<PunishmentData, Punishment> entry : punishments.entrySet()) {
			PunishmentData data = entry.getKey();
			if(data.getPunishmentExecutor().toString().equals(event.getWhoClicked().getUniqueId().toString()) && data.getPunished().toString().equals(punished.toString()))
				punishment = entry.getValue();
			continue;
		}
		if(punishment == null) {
			event.getWhoClicked().sendMessage(MsgUtils.color("&cAn internal error occurred."));
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Punishments] The plugin was unable to find the Punishment object in the map >> All PunishmentData objects did not match the given arguments.");
			return;
		}
		String reason;
		event.getWhoClicked().closeInventory();
		switch(event.getCurrentItem().getType()) {
		case IRON_SWORD: // PvP Hacks
			reason = "Unfair Advantage - PvP Related Hacks";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case FEATHER: // Fly
			reason = "Unfair Advantage - Fly Related Hacks";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case DIAMOND_ORE: // Xray
			reason = "Unfair Advantage - Xray";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case SUGAR: // movement hacks
			reason = "Unfair Advantage - Movement Hacks";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case MUSHROOM_STEW: // auto hacks
			reason = "Unfair Advantage - Auto Hacks";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case ENDER_PEARL: // teleport hacks
			reason = "Unfair Advantage - Teleport Hacks";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case COMPASS: // render hacks
			reason = "Unfair Advantage - Render Hacks";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case BRICKS: // Other hacks
			reason = "Unfair Advantage - Other Hacks";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case BARRIER: // Cancel
			event.getWhoClicked().closeInventory();
			openPunishmentCategoryGUI((Player) event.getWhoClicked(), punishment);
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void onChatOffencesGUIClick(InventoryClickEvent event) {
		if(!event.getView().getTitle().startsWith("Chat Offences: "))
			return;
		if(event.getCurrentItem() == null)
			return;
		event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		UUID punished = registrar.resolveUUIDFromName(event.getView().getTitle().split(" ")[2]);
		Punishment punishment = null;
		for(Entry<PunishmentData, Punishment> entry : punishments.entrySet()) {
			PunishmentData data = entry.getKey();
			if(data.getPunishmentExecutor().toString().equals(event.getWhoClicked().getUniqueId().toString()) && data.getPunished().toString().equals(punished.toString()))
				punishment = entry.getValue();
			continue;
		}
		if(punishment == null) {
			event.getWhoClicked().sendMessage(MsgUtils.color("&cAn internal error occurred."));
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Punishments] The plugin was unable to find the Punishment object in the map >> All PunishmentData objects did not match the given arguments.");
			return;
		}
		String reason;
		event.getWhoClicked().closeInventory();
		switch(event.getCurrentItem().getType()) {
		case PUFFERFISH: // Swearing
			reason = "Chat Offence - Swearing";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case FIRE_CHARGE: // Illict links
			reason = "Chat Offence - Illict Links";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case SKELETON_SKULL: // Toxicity
			reason = "Chat Offence - Toxicity";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case MAP: // Spam
			reason = "Chat Offence - Spamming";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case CREEPER_HEAD: // Chat Trolling
			reason = "Chat Offence - Chat Trolling";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case PAPER: // Advertisingg
			reason = "Chat Offence - Advertising";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case BARRIER: // Cancel
			event.getWhoClicked().closeInventory();
			openPunishmentCategoryGUI(player, punishment);
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void onOtherOffencesGUIClick(InventoryClickEvent event) {
		if(!event.getView().getTitle().startsWith("Other Offences: "))
			return;
		if(event.getCurrentItem() == null)
			return;
		event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		UUID punished = registrar.resolveUUIDFromName(event.getView().getTitle().split(" ")[2]);
		Punishment punishment = null;
		for(Entry<PunishmentData, Punishment> entry : punishments.entrySet()) {
			PunishmentData data = entry.getKey();
			if(data.getPunishmentExecutor().toString().equals(event.getWhoClicked().getUniqueId().toString()) && data.getPunished().toString().equals(punished.toString()))
				punishment = entry.getValue();
			continue;
		}
		if(punishment == null) {
			event.getWhoClicked().sendMessage(MsgUtils.color("&cAn internal error occurred."));
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Punishments] The plugin was unable to find the Punishment object in the map >> All PunishmentData objects did not match the given arguments.");
			return;
		}
		String reason;
		event.getWhoClicked().closeInventory();
		switch(event.getCurrentItem().getType()) {
		case GUNPOWDER: // DDoS/DoX Threats
			reason = "DDoS/DoX Threats";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case NAME_TAG: // Inappropriate Name/Skin
			reason = "Inappropriate Name/Skin";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case GOLD_INGOT: // Scamming
			reason = "Scamming";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case REDSTONE_TORCH: // Lag Machines
			reason = "Creating Lag Machines";
			punishment.setReason(reason);
			punishment.executePunishment();
			break;
		case HEART_OF_THE_SEA: // Other offences
			Player p = (Player) event.getWhoClicked();
			ConversationFactory factory = new ConversationFactory(instance);
			Conversation conv = factory.withFirstPrompt(new PunishmentOtherOffencePrompt(punishment)).buildConversation(p);
			p.beginConversation(conv);
			break;
		case BARRIER:
			event.getWhoClicked().closeInventory();
			openPunishmentCategoryGUI(player, punishment);
			break;
		default:
			break;
		}
	}
}
