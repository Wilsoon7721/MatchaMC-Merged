package com.matchamc.core.bungee.commands;

import com.matchamc.core.bungee.BungeeMain;
import com.matchamc.shared.util.MsgUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class AlertCmd extends Command {

	public AlertCmd() {
		super("alert");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender.hasPermission("core.bungee.alert"))) {
			sender.sendMessage(BungeeMain.NO_PERMISSION_ERROR);
			return;
		}
		if(args.length == 0) {
			sender.sendMessage(BungeeMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(new ComponentBuilder("Usage: /alert (message)").color(ChatColor.RED).create());
			return;
		}
		String text = MsgUtils.color(String.join(" ", args).trim());
		ProxyServer.getInstance().broadcast(new ComponentBuilder("[").color(ChatColor.YELLOW).append(new ComponentBuilder("ALERT").color(ChatColor.DARK_RED).create()).append(new ComponentBuilder("]").color(ChatColor.YELLOW).create()).append(TextComponent.fromLegacyText(text)).create());
	}
}
