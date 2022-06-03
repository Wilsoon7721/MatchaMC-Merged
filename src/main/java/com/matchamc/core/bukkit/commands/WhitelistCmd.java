package com.matchamc.core.bukkit.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.ServerWhitelist;
import com.matchamc.shared.MsgUtils;

public class WhitelistCmd extends CoreCommand implements Listener {
	private String permissionAdd, permissionRemove, permissionView, permissionEnable, permissionDisable;

	private ServerWhitelist whitelist;

	public WhitelistCmd(BukkitMain instance, ServerWhitelist whitelist, String permissionNode) {
		super(instance, permissionNode);
		this.permissionEnable = permissionNode + ".enable";
		this.permissionDisable = permissionNode + ".disable";
		this.permissionAdd = permissionNode + ".add";
		this.permissionRemove = permissionNode + ".remove";
		this.permissionView = permissionNode + ".view";
		this.whitelist = whitelist;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission(permissionNode)) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(MsgUtils.color("Usage: /whitelist <on/off/add/remove/view> [player]"));
			return true;
		}
		if(args.length == 1) {
			switch(args[0].toLowerCase()) {
			case "enable":
			case "on":
				if(!sender.hasPermission(permissionEnable)) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionEnable));
					return true;
				}
				if(whitelist.isEnabled()) {
					sender.sendMessage(MsgUtils.color("&cThe whitelist is already on!"));
					return true;
				}
				whitelist.setEnabled(true);
				sender.sendMessage(MsgUtils.color("&eThe whitelist has been turned on."));
				break;
			case "disable":
			case "off":
				if(!sender.hasPermission(permissionDisable)) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionDisable));
					return true;
				}
				if(!whitelist.isEnabled()) {
					sender.sendMessage(MsgUtils.color("&cThe whitelist is already off!"));
					return true;
				}
				whitelist.setEnabled(false);
				sender.sendMessage(MsgUtils.color("&eThe whitelist has been turned off."));
				break;
			case "refresh":
				whitelist.refreshWhitelist();
				sender.sendMessage(MsgUtils.color("&eThe whitelist has been refreshed. All non-whitelisted players have been kicked."));
				break;
			case "view":
				if(!sender.hasPermission(permissionView)) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionView));
					return true;
				}
				sender.sendMessage(MsgUtils.color("&eWhitelisted players: &r" + String.join(", ", this.whitelist.getWhitelistedPlayers().stream().map(uuid -> Bukkit.getOfflinePlayer(uuid)).map(offlinePlayer -> offlinePlayer.getName()).collect(Collectors.toSet()))));
				break;
			case "add":
			case "remove":
				sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
				break;
			default:
				sender.sendMessage(MsgUtils.color("&cValid choices are: add, remove, view"));
				return true;
			}
			return true;
		}
		switch(args[0].toLowerCase()) {
		case "view":
			if(!sender.hasPermission(permissionView)) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionView));
				return true;
			}
			sender.sendMessage(MsgUtils.color("&eWhitelisted players: &r" + String.join(", ", this.whitelist.getWhitelistedPlayers().stream().map(uuid -> Bukkit.getOfflinePlayer(uuid)).map(offlinePlayer -> offlinePlayer.getName()).collect(Collectors.toSet()))));
			break;
		case "add":
			if(!sender.hasPermission(permissionAdd)) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionAdd));
				return true;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			if(whitelist.addPlayer(target)) {
				sender.sendMessage(MsgUtils.color("&aSuccessfully added &e" + target.getName() + " &ato the whitelist."));
			} else {
				sender.sendMessage(MsgUtils.color("&cThis player could not be whitelisted."));
			}
			if(target.getLastPlayed() == 0)
				sender.sendMessage(MsgUtils.color("&cWarning: This player has never joined this server before."));
			break;
		case "remove":
			if(!sender.hasPermission(permissionRemove)) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionRemove));
				return true;
			}
			OfflinePlayer a = Bukkit.getOfflinePlayer(args[1]);
			if(whitelist.removePlayer(a)) {
				sender.sendMessage(MsgUtils.color("&aSuccessfully removed &e" + a.getName() + " &afrom the whitelist."));
			} else {
				sender.sendMessage(MsgUtils.color("&cThis player could not be unwhitelisted."));
			}
			break;
		default:
			sender.sendMessage(MsgUtils.color("&cValid choices are: add, remove, view"));
			return true;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return Arrays.asList("add", "remove", "view");
		return Collections.emptyList();
	}

	@EventHandler
	public void whitelistEvent(PlayerLoginEvent event) {
		if(!whitelist.isEnabled())
			return;
		if(whitelist.isWhitelisted(event.getPlayer()))
			return;
		event.disallow(Result.KICK_WHITELIST, MsgUtils.color("&cKicked: Server is currently whitelisted, and you are not permitted to join."));
	}

}
