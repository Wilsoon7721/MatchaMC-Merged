package com.matchamc.core.bukkit.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

public class Reports {
	private BukkitMain instance;
	private Map<UUID, UUID> queuedReports = new HashMap<>();
	private PlayerRegistrar registrar;
	private File reportStats;
	private File reportsDirectory;
	public UUID consoleUUID = UUID.fromString("5aa66c90-aee9-4bb1-987b-1b307d77e4ca");
	public String notifyReportMadePermission = "staffcore.notify.reports.created";
	public String notifyReportClosedPermission = "staffcore.notify.reports.closed";

	public Reports(BukkitMain instance, PlayerRegistrar registrar) {
		this.instance = instance;
		this.registrar = registrar;
		reportStats = new File(this.instance.getDataFolder(), "reports_stats.yml");
		reportsDirectory = new File(this.instance.getDataFolder(), "/reports/");
		if(!reportsDirectory.exists())
			reportsDirectory.mkdirs();
		if(!reportStats.exists()) {
			try {
				reportStats.createNewFile();
			} catch(IOException ex) {}
			YamlConfiguration yc = YamlConfiguration.loadConfiguration(reportStats);
			yc.set("total-reports", 0);
			yc.createSection("reports");
			try {
				yc.save(reportStats);
			} catch(IOException ex) {}
			YamlConfiguration.loadConfiguration(reportStats);
		}
	}

	public Report createReport(UUID reporter, UUID against, String reason, boolean priority) {
		Report report = new Report(this, reporter, against, reason, priority);
		notifyReportMade(report);
		return report;
	}

	public Report getReport(int id) {
		return new Report(this, id);
	}

	// Note: Follow ConsoleUUID
	public Collection<Report> getReportsByUUID(UUID uuid) {
		List<Report> reports = new ArrayList<>();
		for(File file : getReportsDirectory().listFiles()) {
			YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
			String stringuuid = yc.getString("reporter");
			if(!stringuuid.equalsIgnoreCase(uuid.toString()))
				continue;
			int id = yc.getInt("id");
			Report report = new Report(this, id);
			reports.add(report);
		}
		Collections.sort(reports, Comparator.comparing(Report::getId));
		return reports;
	}

	public Collection<Report> getReportsByStatus(Report.Status status) {
		List<Report> reports = new ArrayList<>();
		for(File file : getReportsDirectory().listFiles()) {
			YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
			String stringstatus = yc.getString("status");
			if(!stringstatus.equalsIgnoreCase(status.name()))
				continue;
			int id = yc.getInt("id");
			Report report = new Report(this, id);
			reports.add(report);
		}
		return reports;
	}

	// TODO Run this method when a staff changes the status of a report.
	public void updatePlayerReportStats(UUID playerUUID, Report.Status recentReportStatus) {
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(reportStats);
		yc.set("reports." + playerUUID.toString() + ".total", yc.getInt("reports." + playerUUID.toString() + ".total") + 1);
		if(recentReportStatus == Report.Status.RESOLVED)
			yc.set("reports." + playerUUID.toString() + ".correct", yc.getInt("reports." + playerUUID.toString() + ".correct") + 1);
		try {
			yc.save(reportStats);
		} catch(IOException ex) {
			MsgUtils.sendBukkitConsoleMessage("&c[Reports] Player Reports: Failed to update report accuracy for " + playerUUID.toString() + " as the file could not be saved.");
			ex.printStackTrace();
			return;
		}
		YamlConfiguration.loadConfiguration(reportStats);
		return;
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
			return(highest + 1);
		} catch(NumberFormatException ex) {
			MsgUtils.sendBukkitConsoleMessage("&cFailed to get next available id from Reports.");
			MsgUtils.sendBukkitConsoleMessage("&cConfiguration error - A Report ID is unable to parse through Integer#parseInt.");
			return -1;
		}
	}

	public int cleanUpReports(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		boolean verbose = false;
		if(player != null)
			verbose = true;
		if(verbose)
			player.sendMessage(MsgUtils.color("&ePlease wait while your old reports are being deleted..."));
		int count = 0;
		for(File file : reportsDirectory.listFiles()) {
			YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
			int id = yc.getInt("id");
			String reporter = yc.getString("reporter");
			String status = yc.getString("status");
			if(reporter.equalsIgnoreCase(uuid.toString()) && !status.equalsIgnoreCase(Report.Status.OPEN.name())) {
				if(file.delete()) {
					count++;
					MsgUtils.sendBukkitConsoleMessage("&c[Reports] Cleanup: Deleted Report ID #" + id + ".");
				}
			}
		}
		return count;
	}

	public void notifyReportMade(Report report) {
		String reporterName = registrar.getNameFromRegistrar(report.getReporterUUID()), againstName = registrar.getNameFromRegistrar(report.getAgainstUUID());
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(!player.hasPermission(notifyReportMadePermission))
				continue;
			if(report.isPrioritised())
				player.sendMessage(MsgUtils.color("&c&l[REPORT CREATED] &c(ID #" + report.getId() + ") &6" + reporterName + " &chas reported &6" + againstName + " &cfor &6" + report.getReason() + "&c."));
			else
				player.sendMessage(MsgUtils.color("&3[&bREPORT CREATED&3] &9(ID #" + report.getId() + ") &b" + reporterName + " &3has reported &b" + againstName + " &3for &b" + report.getReason() + "&3."));
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
		player.openInventory(inv);
	}

	public void openPlayerReportsGUI(Player player) {
		Collection<Report> playerReports = getReportsByUUID(player.getUniqueId());
		if(playerReports.isEmpty()) {
			player.sendMessage(MsgUtils.color("&cYou do not have any reports."));
			return;
		}
		int count = playerReports.size();
		if(count > 45) {
			int c = cleanUpReports(player.getUniqueId());
			if(c == 0 || (count - c) > 45) {
				player.sendMessage(MsgUtils.color("&cYou are not able to make a new report."));
				player.sendMessage(MsgUtils.color("&cPlease wait for your old reports to be resolved before making a new report."));
				return;
			}
			player.sendMessage(MsgUtils.color("&eThe plugin has deleted &a" + c + " &eof your resolved/closed reports."));
			player.sendMessage(MsgUtils.color("&ePlease retry the command again."));
			return;
		}
		Inventory inv = Bukkit.createInventory(null, 54, "Your Reports");
		for(Report report : playerReports) {
			String status = "", statusMessage = "";
			switch(report.getStatus()) {
			case OPEN:
				status = MsgUtils.color("&aOPEN");
				break;
			case CLOSED:
				status = MsgUtils.color("&c&lCLOSED");
				statusMessage = report.getStatusMessage();
				break;
			case RESOLVED:
				status = MsgUtils.color("&a&lRESOLVED");
				statusMessage = report.getStatusMessage();
				break;
			default:
				status = MsgUtils.color("&cINVALID");
				statusMessage = "&7There is a configuration error in this report.";
				break;
			}
			String againstName = registrar.getNameFromRegistrar(report.getAgainstUUID());
			if(report.getStatus() == Report.Status.OPEN) {
				ItemStack item = new ItemBuilder(Material.PAPER).withDisplayName("&eReport #" + report.getId() + ": " + againstName).withLore(Arrays.asList("&eID: &a" + report.getId(), "&eReported Player: &a" + againstName, "&eReason: &a" + report.getReason(), "&eStatus: " + status)).toItemStack();
				inv.addItem(item);
			} else {
				ItemStack item = new ItemBuilder(Material.PAPER).withDisplayName("&eReport #" + report.getId() + ": " + againstName).withLore(Arrays.asList("&eID: &a" + report.getId(), "&eReported Player: &a" + againstName, "&eReason: &a" + report.getReason(), "&eStatus: " + status, "&eMessage from Staff: &a" + statusMessage)).toItemStack();
				inv.addItem(item);
			}
		}
		ItemStack bsgp = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).withDisplayName(MsgUtils.color(" ")).toItemStack();
		for(int x = 45; x < 54; x++) {
			inv.setItem(x, bsgp);
		}
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(reportStats);
		int correct = yc.getInt("reports." + player.getUniqueId().toString() + ".correct");
		int total = yc.getInt("reports." + player.getUniqueId().toString() + ".total");
		double accuracy = (correct / total) * 100;
		String saccuracy;
		List<String> lore;
		if(accuracy >= 85) {
			saccuracy = MsgUtils.color("&a" + accuracy);
			lore = Arrays.asList(MsgUtils.color("&aGood job on reporting others accurately, keep "), MsgUtils.color("&aup the good work!"), MsgUtils.color("&aAccurate reports made: &e" + correct + "/" + total));
		} else if(accuracy >= 50 && accuracy < 85) {
			saccuracy = MsgUtils.color("&e" + accuracy);
			lore = Arrays.asList(MsgUtils.color("&eSeems like some reports made by you were "), MsgUtils.color("&enot accurate, but you are almost there!"), MsgUtils.color("&aAccurate reports made: &e" + correct + "/" + total));
		} else {
			saccuracy = MsgUtils.color("&c" + accuracy);
			lore = Arrays.asList(MsgUtils.color("&eSeems like some reports made by you were "), MsgUtils.color("&enot accurate, you can do better!"), MsgUtils.color("&aAccurate reports made: &e" + correct + "/" + total));
		}
		ItemStack iaccuracy = new ItemBuilder(Material.COMPASS).withDisplayName(MsgUtils.color("&eYour Report Accuracy: " + saccuracy + "%")).withLore(lore).withEnchant(Enchantment.DURABILITY, 1).withItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
		inv.setItem(53, iaccuracy);
		player.openInventory(inv);
	}

	public void openStaffReportsGUI(Player player) {
		// TODO Staff Reports GUI
		Inventory inv 
	}
}
