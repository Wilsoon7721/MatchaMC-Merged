package com.matchamc.core.bukkit.commands.staff;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.PlayerRegistrar;
import com.matchamc.core.bukkit.util.Punishments;
import com.matchamc.shared.MsgUtils;

public class PunishCmd extends CoreCommand {
	private Punishments punishments;
	private PlayerRegistrar registrar;

	public PunishCmd(BukkitMain instance, Punishments punishments, PlayerRegistrar registrar, String permissionNode) {
		super(instance, permissionNode);
		this.registrar = registrar;
		this.punishments = punishments;
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
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			return true;
		}
		UUID targetUUID = registrar.resolveUUIDFromName(args[0]);
		if(targetUUID == null) {
			sender.sendMessage(MsgUtils.color("&cThis player has never joined before."));
			return true;
		}
		punishments.openPunishmentGUI(player, targetUUID);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return StringUtil.copyPartialMatches(args[0], registrar.getAllRegisteredPlayerNames(), new ArrayList<>());
	}
}
