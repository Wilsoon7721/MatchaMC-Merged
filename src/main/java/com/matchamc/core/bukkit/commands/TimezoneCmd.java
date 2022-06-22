package com.matchamc.core.bukkit.commands;

import java.time.DateTimeException;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.TimeZones;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;


public class TimezoneCmd extends CoreCommand {
	private TimeZones timezones;

	public TimezoneCmd(BukkitMain instance, TimeZones timezones, String permissionNode) {
		super(instance, permissionNode);
		this.timezones = timezones;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(BukkitMain.NO_PERMISSION_ERROR);
			return true;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.spigot().sendMessage(new ComponentBuilder("Usage: /timezone <Zone Offset/reset>").color(ChatColor.RED).create());
			sender.spigot().sendMessage(new ComponentBuilder("Examples of Zone Offsets are '+02:00', '-0800' and '0600'.").color(ChatColor.RED).create());
			return true;
		}
		Player p = (Player) sender;
		String val = args[0];
		switch(val.toLowerCase()) {
		case "reset":
			if(timezones.hasEntry(p.getUniqueId())) {
				timezones.removeEntry(p.getUniqueId());
				sender.spigot().sendMessage(new ComponentBuilder("Entry removed.").color(ChatColor.YELLOW).create());
				return true;
			}
			sender.spigot().sendMessage(new ComponentBuilder("You do not have a specific timezone entered.").color(ChatColor.RED).create());
			break;
		default:
			if(!(val.startsWith("-")))
				val = "+" + val;
			ZoneOffset offset;
			try {
				offset = ZoneOffset.of(val);
			} catch(DateTimeException ex) {
				sender.spigot().sendMessage(new ComponentBuilder("The timezone offset you provided is not valid.").color(ChatColor.RED).create());
				return true;
			}
			if(timezones.hasEntry(p.getUniqueId())) {
				String oldEntry = timezones.getEntry(p.getUniqueId()).toString();
				sender.spigot().sendMessage(new ComponentBuilder("Overwriting your previous entry (").color(ChatColor.RED).append(new ComponentBuilder(oldEntry).color(ChatColor.YELLOW).create()).append(new ComponentBuilder(") with the current one.").color(ChatColor.YELLOW).create()).create());
				timezones.addEntry(p.getUniqueId(), offset);
			}
			sender.spigot().sendMessage(new ComponentBuilder("Entry added.").color(ChatColor.RED).create());
			break;
		}
		return true;
	}	

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}
}
