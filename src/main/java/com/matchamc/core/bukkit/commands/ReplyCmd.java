package com.matchamc.core.bukkit.commands;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.Messenger;
import com.matchamc.shared.MsgUtils;

public class ReplyCmd extends CoreCommand {
	private Messenger messenger;

	public ReplyCmd(BukkitMain instance, Messenger messenger, String permissionNode) {
		super(instance, permissionNode);
		this.messenger = messenger;
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
		Player player = (Player) sender;
		UUID targetUUID = messenger.getLastMessagedPlayer(player);
		if(targetUUID == null) {
			sender.sendMessage(instance.messages().getString("commands.reply.no_previous_player"));
			return true;
		}
		Player target = Bukkit.getPlayer(targetUUID);
		if(target == null) {
			sender.sendMessage(instance.messages().getString("commands.reply.player_now_offline"));
			messenger.setLastMessagedPlayer(player, null);
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(instance.messages().getString("commands.reply.last_messaged_player").replace("%player%", target.getName()));
			return true;
		}
		String message = MsgUtils.color(String.join(" ", args).trim());
		messenger.sendMessage(player, target, message);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}
}
