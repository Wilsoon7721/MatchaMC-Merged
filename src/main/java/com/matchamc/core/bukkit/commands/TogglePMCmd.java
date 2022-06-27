package com.matchamc.core.bukkit.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.Messenger;
import com.matchamc.shared.MsgUtils;

public class TogglePMCmd extends CoreCommand {
	private Messenger messenger;

	public TogglePMCmd(BukkitMain instance, Messenger messenger, String permissionNode) {
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
		if(messenger.isPMDisabled(player.getUniqueId())) {
			messenger.setPMDisabled(player.getUniqueId(), false);
			sender.sendMessage(MsgUtils.color("&eYour private messages have been &cdisabled&e."));
			return true;
		}
		messenger.setPMDisabled(player.getUniqueId(), true);
		sender.sendMessage(MsgUtils.color("&eYour private messages have been &aenabled&e."));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}

}
