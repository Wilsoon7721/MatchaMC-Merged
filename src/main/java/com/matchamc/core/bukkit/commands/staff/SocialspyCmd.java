package com.matchamc.core.bukkit.commands.staff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.Messenger;
import com.matchamc.shared.util.MsgUtils;

public class SocialspyCmd extends CoreCommand {
	private Messenger messenger;

	public SocialspyCmd(BukkitMain instance, Messenger messenger, String permissionNode) {
		super(instance, permissionNode);
		this.messenger = messenger;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission(permissionNode)) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 0) {
			boolean state = messenger.setSocialspyState(player, !messenger.getSocialspyState(player));
			if(state) {
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.socialspy.enabled")));
			} else {
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.socialspy.disabled")));
			}
			return true;
		}
		if(args.length > 0) {
			switch(args[0].toLowerCase()) {
			case "ON":
			case "TRUE":
				messenger.setSocialspyState(player, true);
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.socialspy.enabled")));
				break;
			case "OFF":
			case "FALSE":
				messenger.setSocialspyState(player, false);
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.socialspy.disabled")));
				break;
			default:
				boolean state = messenger.setSocialspyState(player, !messenger.getSocialspyState(player));
				if(state) {
					sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.socialspy.enabled")));
				} else {
					sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.socialspy.disabled")));
				}
				break;
			}
		}
		return true;
	}
}
