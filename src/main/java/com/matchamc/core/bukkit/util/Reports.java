package com.matchamc.core.bukkit.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

public class Reports {
	private BukkitMain instance;
	private Map<UUID, UUID> queuedReports = new HashMap<>();
	private PlayerRegistrar registrar;
	private File reportsDirectory;
	public UUID consoleUUID = UUID.fromString("5aa66c90-aee9-4bb1-987b-1b307d77e4ca");
	public String notifyReportMadePermission = "staffcore.notify.reports.created";
	public String notifyReportClosedPermission = "staffcore.notify.reports.closed";


	public Reports(BukkitMain instance, PlayerRegistrar registrar) {
		this.instance = instance;
		this.registrar = registrar;
		reportsDirectory = new File(this.instance.getDataFolder(), "/reports/");
		if(!reportsDirectory.exists())
			reportsDirectory.mkdirs();
	}

	public Report createReport(UUID reporter, UUID against, String reason, boolean priority) {
		Report report = new Report(this, reporter, against, reason, priority);
		notifyReportMade(report);
		return report;
	}

	public Report getReport(int id) {
		return new Report(this, id);
	}

	public int getNextAvailableId() {
		if(reportsDirectory.listFiles().length == 0)
			return 1;
		try {
			List<Integer> reportIds = Stream.of(reportsDirectory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if(name.endsWith(".yml"))
						return true;
					return false;
				}
			})).map(File::getName).map(s -> s.replaceAll("\\D*", "")).map(Integer::parseInt).collect(Collectors.toList());
			int highest = Collections.max(reportIds);
			return (highest + 1);
		} catch(NumberFormatException ex) {
			MsgUtils.sendBukkitConsoleMessage("&cFailed to get next available id from Reports.");
			MsgUtils.sendBukkitConsoleMessage("&cConfiguration error - A Report ID is unable to parse through Integer#parseInt.");
			return -1;
		}
	}

	public void notifyReportMade(Report report) {
		String reporterName = registrar.getNameFromRegistrar(report.getReporterUUID()), againstName = registrar.getNameFromRegistrar(report.getAgainstUUID());
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(!player.hasPermission(notifyReportMadePermission)) continue;
			if(report.isPrioritised()) player.sendMessage(MsgUtils.color("&c&l[REPORT CREATED] &c(ID #" + report.getId() + ") &6" + reporterName + " &chas reported &6" + againstName + " &cfor &6" + report.getReason() + "&c."));
			else player.sendMessage(MsgUtils.color("&3[&bREPORT CREATED&3] &9(ID #" + report.getId() + ") &b" + reporterName + " &3has reported &b" + againstName + " &3for &b" + report.getReason() + "&3."));
		}
	}

	public File getReportsDirectory() {
		return reportsDirectory;
	}

	// GUIs
	public void openReportReasonGUI(Player player, UUID against) {
		queuedReports.put(player.getUniqueId(), against);
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

		// Default Item: Black Stained Glass Pane to fill up slots
		ItemStack glasspane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta glasspanemeta = glasspane.getItemMeta();
		glasspanemeta.setDisplayName(" ");
		glasspane.setItemMeta(glasspanemeta);
		for(int i = 0; i < 9; i++) {
			if(inv.getItem(i) == null)
				inv.setItem(i, glasspane);
			continue;
		}

		player.openInventory(inv);
	}

	// Get 'against' UUID from the queuedReports map
	public void openUnfairAdvantagesGUI(Player player) {
		UUID against = queuedReports.get(player.getUniqueId());
		if(against == null) {
			player.sendMessage(MsgUtils.color("&cUnable to continue with report: An internal error occurred."));
			return;
		}
		String againstName = registrar.getNameFromRegistrar(queuedReports.get(player.getUniqueId()));
		Inventory inv = Bukkit.createInventory(null, 9, "Unfair Advantages - " + againstName);
		ItemStack pvphacks = new ItemBuilder(Material.IRON_SWORD).withDisplayName("&cCombat Related Hacks").withLore(Arrays.asList("&eE.g. Killaura, TP-Aura, Aimbot, Autoclicker")).toItemStack();
		ItemStack fly = new ItemBuilder(Material.FEATHER).withDisplayName("&cFly Hacks").withLore(Arrays.asList("&eE.g. Flight, CreativeFly, Jetpack etc.")).toItemStack();
		ItemStack xray = new ItemBuilder(Material.DIAMOND_ORE).withDisplayName("&cXray Hacks").toItemStack();
		ItemStack movementhacks = new ItemBuilder(Material.SUGAR).withDisplayName("&cMovement Related Hacks").withLore(Arrays.asList("&eE.g. Speed, Bunnyhop, Spider, Step")).toItemStack();
		ItemStack autohacks = new ItemBuilder(Material.MUSHROOM_STEW).withDisplayName("&cAutomatic Hacks").withLore(Arrays.asList("&eE.g. Autosoup, AutoPot, AutoTool, AutoMine")).toItemStack();
		ItemStack teleporthacks = new ItemBuilder(Material.ENDER_PEARL).withDisplayName("&cTeleportation Hacks").withLore(Arrays.asList("&eE.g. ClickTP")).toItemStack();
		ItemStack renderhacks = new ItemBuilder(Material.COMPASS).withDisplayName("&cRender Hacks").withLore(Arrays.asList("&eE.g. ESPs (Player, Mob, Chest, Other), Fullbright")).toItemStack();
		ItemStack otherhacks = new ItemBuilder(Material.BRICKS).withDisplayName("&cOther Cheats").withLore(Arrays.asList("&eHacks that are not listed here.")).toItemStack();
		inv.addItem(new ItemStack[] { fly, xray, pvphacks, movementhacks, autohacks, teleporthacks, renderhacks, otherhacks });
	}
}
