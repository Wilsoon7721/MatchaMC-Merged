package com.matchamc.core.bukkit.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.PlayerRegistrar;
import com.matchamc.core.bukkit.util.Report;
import com.matchamc.core.bukkit.util.Reports;
import com.matchamc.shared.MsgUtils;

public class ReportCmd extends CoreCommand implements Listener {
	private Reports reports;
	private PlayerRegistrar registrar;

	public ReportCmd(BukkitMain instance, Reports reports, PlayerRegistrar registrar, String permissionNode) {
		super(instance, permissionNode);
		this.reports = reports;
		this.registrar = registrar;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		// /report <name> <reason>
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(MsgUtils.color("&cUsage: /report <player> <name>"));
			return true;
		}
		if(args.length < 2) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
				sender.sendMessage(MsgUtils.color("&cYou need to provide a reason for this report."));
				return true;
			}
			Player player = (Player) sender;
			Player against = Bukkit.getPlayer(args[1]);
			UUID againstUUID;
			if(against != null)
				againstUUID = against.getUniqueId();
			else
				againstUUID = registrar.resolveUUIDFromName(args[1]);
			if(againstUUID == null) {
				sender.sendMessage(MsgUtils.color("&cThis player has not joined the server before."));
				return true;
			}
			openReportReasonGUI(player, againstUUID);
			return true;
		}
		Player against = Bukkit.getPlayer(args[1]);
		UUID againstUUID;
		if(against != null)
			againstUUID = against.getUniqueId();
		else
			againstUUID = registrar.resolveUUIDFromName(args[1]);
		if(againstUUID == null) {
			sender.sendMessage(MsgUtils.color("&cThis player has not joined the server before."));
			return true;
		}
		// TODO create report
		String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();
		Report report = reports.createReport(reports.consoleUUID, againstUUID, reason, (!(sender instanceof Player)));
		sender.sendMessage(MsgUtils.color("&eYou have created a report &a#" + report.getId() + " &eagainst &a" + registrar.getNameFromRegistrar(report.getAgainstUUID()) + " &efor &a" + report.getReason() + "&e."));
		return true;
	}

	public void openReportReasonGUI(Player player, UUID against) {
		String againstName = registrar.getNameFromRegistrar(against);
		Inventory inv = Bukkit.createInventory(null, 9, "Report Player: " + againstName);

		// Item 1: Blacklisted Modifications AT SLOT 2
		ItemStack modifications = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta modificationsmeta = modifications.getItemMeta();
		modificationsmeta.setDisplayName(MsgUtils.color("&cBlacklisted Modifications"));
		modificationsmeta.setLore(Arrays.asList(MsgUtils.color("&eE.g. Killaura, Aimbot, AutoClicker etc.")));
		modifications.setItemMeta(modificationsmeta);
		inv.setItem(2, modifications);

		// Item 2: Chat Related Offences AT SLOT 4
		ItemStack chat = new ItemStack(Material.PAPER);
		ItemMeta chatmeta = chat.getItemMeta();
		chatmeta.setDisplayName(MsgUtils.color("&cChat Offences"));
		chatmeta.setLore(Arrays.asList(MsgUtils.color("&eE.g. Spamming, Swearing, Advertising, Toxic Behaviour etc.")));
		chat.setItemMeta(chatmeta);
		inv.setItem(4, chat);

		// Item 3: Other Offences AT SLOT 6
		ItemStack other = new ItemStack(Material.BEDROCK);
		ItemMeta othermeta = other.getItemMeta();
		othermeta.setDisplayName(MsgUtils.color("&cOther"));
		othermeta.setLore(Arrays.asList(MsgUtils.color("&eE.g. DDOS/DoX Threats, Inappropriate Name/Skin, Scamming, Lag Machines etc.")));
		other.setItemMeta(othermeta);
		inv.setItem(6, other);

		ItemStack glasspane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta glasspanemeta = glasspane.getItemMeta();
		glasspanemeta.setDisplayName(" ");
		glasspane.setItemMeta(glasspanemeta);
		for(int i = 0; i < 9; i++) {
			if(inv.getItem(i) == null)
				inv.setItem(i, glasspane);
			continue;
		}
	}

	@EventHandler
	public void onReportReasonGUIInteract(InventoryClickEvent event) {
		if(event.getClickedInventory() == null || event.getCurrentItem() == null)
			return;
		if(!(event.getView().getTitle().startsWith("Report Player: ")))
			return;
		event.setCancelled(true);
		switch(event.getCurrentItem().getType()) {
		case NETHERITE_SWORD:
			// Unfair Advantage
			break;
		case PAPER:
			// Chat
			break;
		case BEDROCK:
			// Other
			break;
		default:
			break;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return null;
		return Collections.emptyList();
	}

}
