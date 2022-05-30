package com.matchamc.core.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.util.MsgUtils;

public class ClearChatCmd extends CoreCommand {
	public ClearChatCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission(permissionNode)) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		for(Player player : Bukkit.getOnlinePlayers()) {
			for(int x = 0; x < 100; x++) {
				player.sendMessage(" ");
			}
			player.sendMessage(MsgUtils.color(instance.messages().getString("commands.clear_chat.global").replace("%player%", sender.getName())));
		}
		return true;
	}
}
