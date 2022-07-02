package com.matchamc.core.bukkit.listeners;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.PlayerRegistrar;
import com.matchamc.core.bukkit.util.Report;
import com.matchamc.core.bukkit.util.Report.Status;
import com.matchamc.core.bukkit.util.Reports;
import com.matchamc.core.bukkit.util.Staffs;
import com.matchamc.core.conversation.OtherOffencePrompt;
import com.matchamc.core.conversation.StatusMessagePrompt;
import com.matchamc.shared.MsgUtils;

import net.md_5.bungee.api.ChatColor;

public class ReportsGUIListener implements Listener {
	private BukkitMain instance;
	private Reports reports;
	private Staffs staffs;
	private PlayerRegistrar registrar;

	public ReportsGUIListener(BukkitMain instance, Staffs staffs, Reports reports, PlayerRegistrar registrar) {
		this.instance = instance;
		this.reports = reports;
		this.staffs = staffs;
		this.registrar = registrar;
	}

	// Reports#openReportReasonGUI()
	@EventHandler
	public void onReportReasonGUIInteract(InventoryClickEvent event) {
		if(event.getClickedInventory() == null || event.getCurrentItem() == null)
			return;
		if(!(event.getView().getTitle().startsWith("Report Player: ")))
			return;
		event.setCancelled(true);
		Player p = (Player) event.getWhoClicked();
		p.closeInventory();
		switch(event.getCurrentItem().getType()) {
		case NETHERITE_SWORD:
			// Unfair Advantage
			reports.openUnfairAdvantagesGUI(p);
			break;
		case PAPER:
			// Chat
			reports.openChatOffencesGUI(p);
			break;
		case BEDROCK:
			// Other
			reports.openOtherOffencesGUI(p);
			break;
		default:
			break;
		}
	}

	// Reports#openUnfairAdvantagesGUI()
	@EventHandler
	public void onUnfairAdvantagesGUIInteract(InventoryClickEvent event) {
		if(!(event.getView().getTitle().startsWith("Unfair Advantages - ")))
			return;
		if(event.getCurrentItem() == null)
			return;
		event.setCancelled(true);
		UUID against = reports.getQueuedReports().get(event.getWhoClicked().getUniqueId());
		if(against == null) {
			event.getWhoClicked().sendMessage(MsgUtils.color("&cUnable to continue with report: An internal error occurred."));
			return;
		}
		String againstName = registrar.getNameFromRegistrar(against), reason;
		Report report;
		event.getWhoClicked().closeInventory();
		switch(event.getCurrentItem().getType()) {
		case IRON_SWORD: // PvP Hacks
			reason = "Unfair Advantage - PvP Related Hacks";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case FEATHER: // Fly
			reason = "Unfair Advantage - Fly Related Hacks";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case DIAMOND_ORE: // Xray
			reason = "Unfair Advantage - Xray";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case SUGAR: // movement hacks
			reason = "Unfair Advantage - Movement Hacks";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case MUSHROOM_STEW: // auto hacks
			reason = "Unfair Advantage - Auto Hacks";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case ENDER_PEARL: // teleport hacks
			reason = "Unfair Advantage - Teleport Hacks";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case COMPASS: // render hacks
			reason = "Unfair Advantage - Render Hacks";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case BRICKS: // Other hacks
			reason = "Unfair Advantage - Other Hacks";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case BARRIER: // Cancel
			event.getWhoClicked().closeInventory();
			reports.openReportReasonGUI((Player) event.getWhoClicked(), against);
			break;
		default:
			break;
		}
	}

	// Reports#openChatOffencesGUI()
	@EventHandler
	public void onChatOffencesGUIInteract(InventoryClickEvent event) {
		if(!(event.getView().getTitle().startsWith("Chat Offences - ")))
			return;
		if(event.getCurrentItem() == null)
			return;
		event.setCancelled(true);
		UUID against = reports.getQueuedReports().get(event.getWhoClicked().getUniqueId());
		if(against == null) {
			event.getWhoClicked().sendMessage(MsgUtils.color("&cUnable to continue with report: An internal error occurred."));
			return;
		}
		String againstName = registrar.getNameFromRegistrar(against), reason;
		Report report;
		event.getWhoClicked().closeInventory();
		switch(event.getCurrentItem().getType()) {
		case PUFFERFISH: // Swearing
			reason = "Chat Offence - Swearing";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case FIRE_CHARGE: // Illict links
			reason = "Chat Offence - Illict Links";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case SKELETON_SKULL: // Toxicity
			reason = "Chat Offence - Toxicity";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case MAP: // Spam
			reason = "Chat Offence - Spamming";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case CREEPER_HEAD: // Chat Trolling
			reason = "Chat Offence - Chat Trolling";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case PAPER: // Advertisingg
			reason = "Chat Offence - Advertising";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case BARRIER: // Cancel
			event.getWhoClicked().closeInventory();
			reports.openReportReasonGUI((Player) event.getWhoClicked(), against);
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void onOtherOffencesGUIInteract(InventoryClickEvent event) {
		if(!(event.getView().getTitle().startsWith("Other Offences - ")))
			return;
		if(event.getCurrentItem() == null)
			return;
		event.setCancelled(true);
		UUID against = reports.getQueuedReports().get(event.getWhoClicked().getUniqueId());
		if(against == null) {
			event.getWhoClicked().sendMessage(MsgUtils.color("&cUnable to continue with report: An internal error occurred."));
			return;
		}
		String againstName = registrar.getNameFromRegistrar(against), reason;
		Report report;
		event.getWhoClicked().closeInventory();
		switch(event.getCurrentItem().getType()) {
		case GUNPOWDER: // DDoS/DoX Threats
			reason = "DDoS/DoX Threats";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case NAME_TAG: // Inappropriate Name/Skin
			reason = "Inappropriate Name/Skin";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case GOLD_INGOT: // Scamming
			reason = "Scamming";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case REDSTONE_TORCH: // Lag Machines
			reason = "Creating Lag Machines";
			report = reports.createReport(event.getWhoClicked().getUniqueId(), against, reason, false);
			event.getWhoClicked().sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + reason + "&e."));
			break;
		case HEART_OF_THE_SEA: // Other offences
			Player p = (Player) event.getWhoClicked();
			ConversationFactory factory = new ConversationFactory(instance);
			Conversation conv = factory.withFirstPrompt(new OtherOffencePrompt(reports, registrar, against, false)).buildConversation(p);
			p.beginConversation(conv);
			break;
		case BARRIER:
			event.getWhoClicked().closeInventory();
			reports.openReportReasonGUI((Player) event.getWhoClicked(), against);
			break;
		default:
			break;
		}
	}

	// /reports - Both player and staff
	@EventHandler
	public void onPlayerReportsClick(InventoryClickEvent event) {
		if(!(event.getView().getTitle().equalsIgnoreCase("Your Reports")))
			return;
		if(event.getCurrentItem() == null)
			return;
		event.setCancelled(true);
		if(event.getCurrentItem().getType() != Material.PAPER)
			return;
		ItemStack i = event.getCurrentItem();
		int id = Integer.parseInt(i.getItemMeta().getLore().get(0).replaceAll("\\D*", ""));
		Report report = reports.getReport(id);
		reports.openManageReportGUI((Player) event.getWhoClicked(), report);
	}

	@EventHandler
	public void onStaffReportsClick(InventoryClickEvent event) {
		if(!(event.getView().getTitle().startsWith("Player Reports (")))
			return;
		if(event.getCurrentItem() == null)
			return;
		Player p = (Player) event.getWhoClicked();
		event.setCancelled(true);
		if(event.getCurrentItem().getType() == Material.ARROW) {
			int page = Integer.parseInt(ChatColor.stripColor(event.getView().getTitle()).split("/")[0].replaceAll("\\D*", ""));
			if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Previous Page")) {
				if(page == 1)
					return;
				reports.openStaffReportsGUI(p, (page - 1));
				return;
			}
			if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Next Page")) {
				reports.openStaffReportsGUI(p, (page + 1));
				return;
			}
		}
		if(event.getCurrentItem().getType() != Material.PAPER)
			return;
		int id = Integer.parseInt(event.getCurrentItem().getItemMeta().getLore().get(0).replaceAll("\\D*", ""));
		Report report = reports.getReport(id);
		reports.openManageReportGUI(p, report);
	}

	@EventHandler
	public void onManageReportClick(InventoryClickEvent event) {
		if(!(event.getView().getTitle().startsWith("Manage Report #")))
			return;
		if(event.getCurrentItem() == null)
			return;
		event.setCancelled(true);
		int id = Integer.parseInt(event.getView().getTitle().replaceAll("\\D*", ""));
		Report report = reports.getReport(id);
		Material type = event.getCurrentItem().getType();
		if(type == Material.BARRIER) {
			event.getWhoClicked().closeInventory();
			if(staffs.isStaff((Player) event.getWhoClicked()))
				reports.openStaffReportsGUI((Player) event.getWhoClicked(), 1);
			else
				reports.openPlayerReportsGUI((Player) event.getWhoClicked());
			return;
		}
		if(type == Material.RED_STAINED_GLASS) {
			event.getWhoClicked().closeInventory();
			event.getWhoClicked().sendMessage(MsgUtils.color("&eTo speak to a staff member about this report, please contact any of the online staff and quote Report ID &a#" + report.getId() + "&e."));
			return;
		}
		ConversationFactory factory = new ConversationFactory(instance);
		if(type == Material.RED_WOOL) {
			Conversation conv = factory.withFirstPrompt(new StatusMessagePrompt(report, Status.CLOSED)).buildConversation((Player) event.getWhoClicked());
			((Player) event.getWhoClicked()).beginConversation(conv);
			return;
		}
		if(type == Material.GREEN_WOOL) {
			Conversation conv = factory.withFirstPrompt(new StatusMessagePrompt(report, Status.RESOLVED)).buildConversation((Player) event.getWhoClicked());
			((Player) event.getWhoClicked()).beginConversation(conv);
			return;
		}
		if(type == Material.DIAMOND) {
			// TODO Report followup
			// GUI that contains stuff like teleport to player etc.
			event.getWhoClicked().closeInventory();
			reports.openReportFollowupGUI((Player) event.getWhoClicked());
			return;
		}
	}

	@EventHandler
	public void onReportFollowupClick(InventoryClickEvent event) {
		if(!(event.getView().getTitle().equalsIgnoreCase("Player Action Menu")))
			return;
		if(event.getCurrentItem() == null)
			return;

	}
}

