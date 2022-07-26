package com.matchamc.core.bukkit.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

public class BanWave extends CoreCommand {
	private BukkitMain instance;
	private PlayerRegistrar registrar;
	private Duration duration;
	private Integer[] seconds = new Integer[1];
	private Set<UUID> banWavePlayers = new HashSet<>();
	private Set<UUID> exclusions = new HashSet<>();

	public BanWave(BukkitMain instance, PlayerRegistrar registrar, String permissionNode) {
		super(instance, permissionNode);
		this.registrar = registrar;
		duration = Duration.parse("PT" + this.instance.getConfig().getString("banwave.interval").toUpperCase());
		seconds[0] = ((Long) duration.getSeconds()).intValue();
		exclusions.addAll(this.instance.getConfig().getStringList("banwave.exclusions").stream().map(s -> UUID.fromString(s)).collect(Collectors.toSet()));
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this.instance, () -> {
			banWavePlayers.removeAll(exclusions);
			if(banWavePlayers.isEmpty())
				return;
			for(UUID uuid : banWavePlayers) {
				String name = this.registrar.getNameFromRegistrar(uuid);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + name + " 30d Blacklisted Modifications -s");
				MsgUtils.sendBukkitConsoleMessage("&e" + name + " &7has been banned by the banwave.");
				banWavePlayers.remove(uuid);
			}
		}, 20L, duration.getSeconds() * 20L);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this.instance, () -> seconds[0]--, 20L, 20L);
	}

	public void add(UUID uuid) {
		banWavePlayers.add(uuid);
	}

	public void remove(UUID uuid) {
		banWavePlayers.remove(uuid);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(MsgUtils.color("&cUsage: /banwave <add/remove/list/nextwave> [player]"));
			return true;
		}
		switch(args[0].toLowerCase()) {
		case "add":
			if(args.length <= 1) {
				sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
				return true;
			}
			Player target = Bukkit.getPlayer(args[1]);
			UUID uuid;
			if(target == null)
				uuid = registrar.resolveUUIDFromName(args[1]);
			else
				uuid = target.getUniqueId();
			if(banWavePlayers.contains(uuid)) {
				sender.sendMessage(MsgUtils.color("&cThe player is already inside the upcoming banwave."));
				return true;
			}
			banWavePlayers.add(uuid);
			break;
		case "remove":
			if(args.length <= 1) {
				sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
				return true;
			}
			Player target2 = Bukkit.getPlayer(args[1]);
			UUID uuid2;
			if(target2 == null)
				uuid2 = registrar.resolveUUIDFromName(args[1]);
			else
				uuid2 = target2.getUniqueId();
			if(!banWavePlayers.contains(uuid2)) {
				sender.sendMessage(MsgUtils.color("&cThe player is not inside the upcoming banwave."));
				return true;
			}
			banWavePlayers.remove(uuid2);
			break;
		case "list":
			String players = banWavePlayers.stream().map(u -> registrar.getNameFromRegistrar(u)).collect(Collectors.joining(", ")).trim();
			if(players.isBlank()) {
				sender.sendMessage(MsgUtils.color("&cNo players are queued for the next banwave."));
				return true;
			}
			sender.sendMessage(MsgUtils.color("&eThe following players are queued for the next banwave: &c" + players));
			sender.sendMessage(MsgUtils.color("&eThe next banwave will execute in &7" + seconds[0] + " seconds&e."));
			break;
		case "next":
		case "nextwave":
			sender.sendMessage(MsgUtils.color("&eNext banwave executes in &7" + seconds[0] + " seconds&e."));
			sender.sendMessage(MsgUtils.color("&7The next banwave contains &e" + banWavePlayers.size() + " players&7."));
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return StringUtil.copyPartialMatches(args[0], Arrays.asList("add", "remove", "list", "nextwave"), new ArrayList<>());
		if(args.length == 1) {
			switch(args[0].toLowerCase()) {
			case "add":
			case "remove":
				return StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), new ArrayList<>());
			default:
				return Collections.emptyList();
			}
		}
		return Collections.emptyList();
	}
}
