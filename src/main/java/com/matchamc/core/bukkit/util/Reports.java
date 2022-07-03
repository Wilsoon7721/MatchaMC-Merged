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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MathUtil;
import com.matchamc.shared.MsgUtils;

public class Reports {
	private BukkitMain instance;
	private Map<UUID, UUID> queuedReports = new HashMap<>();
	private Set<UUID> cooldown = new HashSet<>();
	private PlayerRegistrar registrar;
	private Staffs staffs;
	private File reportStats;
	private File reportsDirectory;
	public UUID consoleUUID = UUID.fromString("5aa66c90-aee9-4bb1-987b-1b307d77e4ca");
	public String notifyReportMadePermission = "staffcore.notify.reports.created";
	public String notifyReportClosedPermission = "staffcore.notify.reports.closed";

	public Reports(BukkitMain instance, Staffs staffs, PlayerRegistrar registrar) {
		this.instance = instance;
		this.registrar = registrar;
		this.staffs = staffs;
		reportStats = new File(this.instance.getDataFolder(), "reports_stats.yml");
		reportsDirectory = new File(this.instance.getDataFolder(), "/reports/");
		if(!reportsDirectory.exists())
			reportsDirectory.mkdirs();
		if(!reportStats.exists()) {
			try {
				reportStats.createNewFile();
			} catch(IOException ex) {
			}
			YamlConfiguration yc = YamlConfiguration.loadConfiguration(reportStats);
			yc.set("total-reports", 0);
			yc.createSection("reports");
			try {
				yc.save(reportStats);
			} catch(IOException ex) {
			}
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

	public Map<UUID, UUID> getQueuedReports() {
		return queuedReports;
	}

	public void setOnCooldown(UUID uuid) {
		if(cooldown.contains(uuid))
			return;
		cooldown.add(uuid);
		Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> cooldown.remove(uuid), 30 * 20L);
	}

	public Set<UUID> getCooldown() {
		return cooldown;
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
		for(int i = 0; i < inv.getSize(); i++) {
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
		String againstName = registrar.getNameFromRegistrar(against);
		Inventory inv = Bukkit.createInventory(null, 9, "Unfair Advantages - " + againstName);
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

	public void openChatOffencesGUI(Player player) {
		UUID against = queuedReports.get(player.getUniqueId());
		if(against == null) {
			player.sendMessage(MsgUtils.color("&cUnable to continue with report: An internal error occurred."));
			return;
		}
		String againstName = registrar.getNameFromRegistrar(against);
		Inventory inv = Bukkit.createInventory(null, 9, "Chat Offences - " + againstName);
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

	public void openOtherOffencesGUI(Player player) {
		UUID against = queuedReports.get(player.getUniqueId());
		if(against == null) {
			player.sendMessage(MsgUtils.color("&cUnable to continue with report: An internal error occurred."));
			return;
		}
		String againstName = registrar.getNameFromRegistrar(against);
		Inventory inv = Bukkit.createInventory(null, 9, "Other Offences - " + againstName);
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

	public void openStaffReportsGUI(Player player, int page) {
		InventoryView openInventory = player.getOpenInventory();
		// TODO Staff Reports GUI
		player.closeInventory();
		List<Report> orderedReports = new ArrayList<>(getReportsByStatus(Report.Status.OPEN));
		Collections.sort(orderedReports, Comparator.comparing(Report::getId));
		List<List<Report>> parts = MathUtil.separateList(orderedReports, 45);
		Inventory inv = Bukkit.createInventory(null, 54, "Player Reports (" + page + "/" + parts.size() + ")");
		if(page > parts.size()) {
			player.sendMessage(MsgUtils.color("&cThere is no page &e" + page + "&c."));
			if(openInventory != null)
				Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> player.openInventory(openInventory), 15L);
			return;
		}
		if(orderedReports.size() > 45) {
			parts.get((page - 1)).stream().forEachOrdered(report -> {
				String status;
				switch(report.getStatus()) {
				case OPEN:
					status = MsgUtils.color("&aOPEN");
					break;
				case CLOSED:
					status = MsgUtils.color("&c&lCLOSED");
					break;
				case RESOLVED:
					status = MsgUtils.color("&a&lRESOLVED");
					break;
				default:
					status = MsgUtils.color("&cINVALID");
					break;
				}
				String againstName = registrar.getNameFromRegistrar(report.getAgainstUUID());
				ItemStack item = new ItemBuilder(Material.PAPER).withDisplayName("&eReport #" + report.getId() + ": " + againstName).withLore(Arrays.asList("&eID: &a" + report.getId(), "&eReporter: &a" + registrar.getNameFromRegistrar(report.getReporterUUID()), "&eReported Player: &a" + againstName, "&eReason: &a" + report.getReason(), "&eStatus: " + status)).toItemStack();
				inv.addItem(item);
			});
		} else {
			orderedReports.stream().forEachOrdered(report -> {
				String status;
				switch(report.getStatus()) {
				case OPEN:
					status = MsgUtils.color("&aOPEN");
					break;
				case CLOSED:
					status = MsgUtils.color("&c&lCLOSED");
					break;
				case RESOLVED:
					status = MsgUtils.color("&a&lRESOLVED");
					break;
				default:
					status = MsgUtils.color("&cINVALID");
					break;
				}
				String againstName = registrar.getNameFromRegistrar(report.getAgainstUUID());
				ItemStack item = new ItemBuilder(Material.PAPER).withDisplayName("&eReport #" + report.getId() + ": " + againstName).withLore(Arrays.asList("&eID: &a" + report.getId(), "&eReporter: &a" + registrar.getNameFromRegistrar(report.getReporterUUID()), "&eReported Player: &a" + againstName, "&eReason: &a" + report.getReason(), "&eStatus: " + status)).toItemStack();
				inv.addItem(item);
			});
		}
		ItemStack bsgp = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).withDisplayName(MsgUtils.color(" ")).toItemStack();
		for(int x = 45; x < 54; x++) {
			inv.setItem(x, bsgp);
		}
		// 48 is previous page
		// 50 is next page
		if(page != 1)
			inv.setItem(48, new ItemBuilder(Material.ARROW).withDisplayName("&ePrevious Page").withLore(Arrays.asList("&eClick to go to page &a" + (page - 1))).toItemStack());
		if(page != parts.size())
			inv.setItem(50, new ItemBuilder(Material.ARROW).withDisplayName("&eNext Page").withLore(Arrays.asList("&eClick to go to page &a" + (page + 1))).toItemStack());
		player.openInventory(inv);
	}

	public void openManageReportGUI(Player player, Report report) {
		Inventory inv = Bukkit.createInventory(null, 54, "Manage Report #" + report.getId());
		if(!report.getReporterUUID().toString().equalsIgnoreCase(player.getUniqueId().toString()) && !staffs.isStaff(player)) {
			if(player.getOpenInventory() != null)
				player.closeInventory();
			player.sendMessage(MsgUtils.color("&cYou are not authorised to manage this report."));
			return;
		}
		String status;
		switch(report.getStatus()) {
		case OPEN:
			status = MsgUtils.color("&aOPEN");
			break;
		case CLOSED:
			status = MsgUtils.color("&c&lCLOSED");
			break;
		case RESOLVED:
			status = MsgUtils.color("&a&lRESOLVED");
			break;
		default:
			status = MsgUtils.color("&cINVALID");
			break;
		}
		String againstName = registrar.getNameFromRegistrar(report.getAgainstUUID());
		if(player.getUniqueId().toString().equalsIgnoreCase(report.getReporterUUID().toString())) {
			ItemStack reportItem = new ItemBuilder(Material.PAPER).withDisplayName("&eReport #" + report.getId() + ": " + againstName).withLore(Arrays.asList("&eID: &a" + report.getId(), "&eReported Player: &a" + againstName, "&eReason: &a" + report.getReason(), "&eStatus: " + status)).toItemStack();
			inv.setItem(13, reportItem);
			// 29 is back, 33 is request help
			ItemStack back = new ItemBuilder(Material.BARRIER).withDisplayName("&eBack").withLore(Arrays.asList("&eReturn to the reports list")).toItemStack();
			ItemStack help = new ItemBuilder(Material.RED_STAINED_GLASS).withDisplayName("&eRequest for help").withLore(Arrays.asList("&eTalk to a staff member regarding this report.")).toItemStack();
			inv.setItem(29, back);
			inv.setItem(33, help);
		} else {
			ItemStack reportItem = new ItemBuilder(Material.PAPER).withDisplayName("&eReport #" + report.getId() + ": " + againstName).withLore(Arrays.asList("&eID: &a" + report.getId(), "&eReporter: &a" + registrar.getNameFromRegistrar(report.getReporterUUID()), "&eReported Player: &a" + againstName, "&eReason: &a" + report.getReason(), "&eStatus: " + status)).toItemStack();
			inv.setItem(13, reportItem);
		}
		if(staffs.isStaff(player)) {
			ItemStack reportFollowup = new ItemBuilder(Material.DIAMOND).withDisplayName("&bReport Followup").toItemStack();
			ItemStack switchClosed = new ItemBuilder(Material.RED_WOOL).withDisplayName("&eSet this report to &cClosed").withLore(Arrays.asList("&eSet this report status to &cClosed")).toItemStack();
			ItemStack switchResolved = new ItemBuilder(Material.GREEN_WOOL).withDisplayName("&eSet this report to &aResolved").withLore(Arrays.asList("&eSet this report status to &aResolved")).toItemStack();
			switch(report.getStatus()) {
			case OPEN:
				inv.setItem(48, switchClosed);
				inv.setItem(50, switchResolved);
				break;
			case CLOSED:
				inv.setItem(49, switchResolved);
				break;
			case RESOLVED:
				inv.setItem(49, switchClosed);
				break;
			}
			inv.setItem(53, reportFollowup);
			ItemStack bsgp = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).withDisplayName(MsgUtils.color(" ")).toItemStack();
			for(int x = 0; x < 54; x++) {
				if(inv.getItem(x) != null)
					continue;
				inv.setItem(x, bsgp);
			}
		}
		player.openInventory(inv);
	}

	public void openReportFollowupGUI(Player player, Report report) {
		Inventory inv = Bukkit.createInventory(null, 9, "Player Action Menu | #" + report.getId());
		ItemStack tp = new ItemBuilder(Material.ENDER_PEARL).withDisplayName("&eTeleport to Reported Player").toItemStack();
		ItemStack punish = new ItemBuilder(Material.STONE_AXE).withDisplayName("&ePunish this player").toItemStack();
		ItemStack cancel = new ItemBuilder(Material.BARRIER).withDisplayName("&eBack").toItemStack();
		inv.addItem(new ItemStack[] { tp, punish });
		inv.setItem(8, cancel);
		ItemStack bsgp = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).withDisplayName(" ").toItemStack();
		for(int x = 0; x < 9; x++) {
			if(inv.getItem(x) == null)
				inv.setItem(x, bsgp);
		}
		player.openInventory(inv);
	}
}
