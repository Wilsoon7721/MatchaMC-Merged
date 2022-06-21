package com.matchamc.core.bukkit.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

import AntiAuraAPI.ViolationEvent;
import me.jumper251.replay.api.ReplayAPI;

public class AntiCheatHook extends CoreCommand implements Listener {
	private HashSet<UUID> alerts = new HashSet<>();
	private BanWave banwave;
	private String acNotifyAdvancedPermission, acNotifyBasicPermission;

	public AntiCheatHook(BukkitMain instance, BanWave banwave, String permissionNode) {
		super(instance, permissionNode);
		this.banwave = banwave;
		acNotifyAdvancedPermission = "staffcore.acnotify.advanced";
		acNotifyBasicPermission = "staffcore.acnotify.basic";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
			return true;
		}
		Player p = (Player) sender;
		if(!(alerts.contains(p.getUniqueId()))) {
			alerts.add(p.getUniqueId());
			p.sendMessage(MsgUtils.color("&e(!) Your anticheat alerts have been enabled."));
			return true;
		}
		alerts.remove(p.getUniqueId());
		p.sendMessage(MsgUtils.color("&e(!) Your anticheat alerts have been disabled."));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}

	@EventHandler
	public void message(ViolationEvent event) {
		if(event.getAmountOfTimesCaught() == 5) {
			banwave.add(AntiCheatTrigger.uuid);
		}
		if(event.getAmountOfTimesCaught() == 10)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + AntiCheatTrigger.target + " 30d Blacklisted Modifications -s");
		alerts.stream().map(uuid -> Bukkit.getPlayer(uuid)).filter(player -> player != null).filter(player -> player.hasPermission(acNotifyAdvancedPermission)).forEach(player -> {
			player.sendMessage(MsgUtils.color("&c&l(!) &c" + AntiCheatTrigger.target + " &7might be using &c" + AntiCheatTrigger.hack + " &7with a ping of &e" + AntiCheatTrigger.ping + " &c(VL " + event.getAmountOfTimesCaught() + ")"));
			ReplayAPI.getInstance().recordReplay(AntiCheatTrigger.target, Bukkit.getConsoleSender(), Bukkit.getPlayer(AntiCheatTrigger.target));
			if(event.getAmountOfTimesCaught() == 5)
				player.sendMessage(MsgUtils.color("&c&l(!) &c" + AntiCheatTrigger.target + " &ehas been added to the banwave."));
			if(event.getAmountOfTimesCaught() == 10)
				player.sendMessage(MsgUtils.color("&c&l(!) &c" + AntiCheatTrigger.target + " &ehas been auto-banned."));
		});
		alerts.stream().map(uuid -> Bukkit.getPlayer(uuid)).filter(player -> player != null).filter(player -> player.hasPermission(acNotifyBasicPermission)).filter(player -> !(player.hasPermission(acNotifyBasicPermission) && player.hasPermission(acNotifyAdvancedPermission))).forEach(player -> {
			player.sendMessage(MsgUtils.color("&c&l(!) &c" + AntiCheatTrigger.target + " &7might be hacking &c(VL " + event.getAmountOfTimesCaught() + ")"));
			ReplayAPI.getInstance().recordReplay(AntiCheatTrigger.target, Bukkit.getConsoleSender(), Bukkit.getPlayer(AntiCheatTrigger.target));
		});
	}
}
