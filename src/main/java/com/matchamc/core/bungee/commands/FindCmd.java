package com.matchamc.core.bungee.commands;

import java.util.Collections;
import java.util.stream.Collectors;

import com.matchamc.core.bungee.BungeeMain;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class FindCmd extends Command implements TabExecutor {

	public FindCmd() {
		super("find");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender.hasPermission("core.bungee.find"))) {
			sender.sendMessage(BungeeMain.NO_PERMISSION_ERROR);
			return;
		}
		if(args.length == 0) {
			sender.sendMessage(new ComponentBuilder("Usage: /find <player>").color(ChatColor.RED).create());
			return;
		}
		ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
		if(target == null) {
			sender.sendMessage(new ComponentBuilder("This player could not be found.").color(ChatColor.RED).create());
			return;
		}
		String targetConnectedServer = target.getServer().getInfo().getName();
		sender.sendMessage(new ComponentBuilder("" + sender.getName() + " is on " + targetConnectedServer + ".").color(ChatColor.DARK_AQUA).create());
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if(args.length == 0) {
			return ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toSet());
		}
		return Collections.emptyList();
	}

}
