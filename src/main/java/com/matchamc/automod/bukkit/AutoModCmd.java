package com.matchamc.automod.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class AutoModCmd extends CoreCommand {
	private AutoMod autoMod;

	public AutoModCmd(AutoMod autoMod, BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
		this.autoMod = autoMod;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg("MANAGE_AUTOMOD"));
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(MsgUtils.color("&cUsage: /automod players <list/add/remove> [player]"));
			sender.sendMessage(MsgUtils.color("&cUsage: /automod modules <list/enable/disable> [module]"));
			return true;
		}
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("modules")) {
				sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
				sender.sendMessage(MsgUtils.color("&cUsage: /automod modules <list/enable/disable> [module]"));
				return true;
			}
			if(args[0].equalsIgnoreCase("players")) {
				sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
				sender.sendMessage(MsgUtils.color("&cUsage: /automod players <list/add/remove> [player]"));
				return true;
			}
			if(args[0].equalsIgnoreCase("reload")) {
				autoMod.reloadConfig();
				sender.sendMessage(MsgUtils.color("&eAutoMod has been reloaded."));
				return true;
			}
			sender.sendMessage(MsgUtils.color("&cUnrecognized option: " + args[0]));
			return true;
		}
		switch(args[0].toLowerCase()) {
		case "modules":
			switch(args[1].toLowerCase()) {
			case "list":
				sender.spigot().sendMessage(new ComponentBuilder("Available Modules: ").color(ChatColor.YELLOW).append(new ComponentBuilder("Caps").color(ChatColor.AQUA).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Chat Module - Filters excessive capital letters"))).create()).create());
				//
				break;
			case "enable":
				if(args.length < 3) {
					sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
					sender.sendMessage(MsgUtils.color("&cYou need to insert a module name. Use '/automod modules list' to view a list of modules."));
					return true;
				}
				//
				return true;
			case "disable":
				if(args.length < 3) {
					sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
					sender.sendMessage(MsgUtils.color("&cYou need to insert a module name. Use '/automod modules list' to view a list of modules."));
					return true;
				}
				//
				return true;
			default:
				sender.sendMessage(MsgUtils.color("&cUnrecognized option: " + args[0]));
				return true;
			}
			return true;
		case "players":
			switch(args[1].toLowerCase()) {
			case "list":
				//
				break;
			case "add":
				if(args.length < 3) {
					sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
					sender.sendMessage(MsgUtils.color("&cYou need to insert a player name."));
					return true;
				}
				//
				break;
			case "remove":
				if(args.length < 3) {
					sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
					sender.sendMessage(MsgUtils.color("&cYou need to insert a player name. Use '/automod players list' to view a list of players."));
					return true;
				}
				//
				break;
			}
			return true;
		case "reload":
			autoMod.reloadConfig();
			sender.sendMessage(MsgUtils.color("&eAutoMod has been reloaded."));
			return true;
		default:
			sender.sendMessage(MsgUtils.color("&cUnrecognized option: " + args[0]));
			return true;
		}
	}

}
