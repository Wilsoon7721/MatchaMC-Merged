package com.matchamc.core.bukkit.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

public class SkullCmd extends CoreCommand {

	public SkullCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
	}

	@SuppressWarnings("deprecation")
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
		if(args.length == 0) {
			sender.sendMessage(MsgUtils.color("&cUsage: /skull (name)"));
			return true;
		}
		Player player = (Player) sender;
		String name = args[0];
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(name));
		item.setItemMeta(meta);
		if(player.getInventory().firstEmpty() == -1) {
			player.getWorld().dropItem(player.getLocation(), item);
			player.sendMessage(BukkitMain.INSUFFICIENT_INVENTORY_SPACE);
			return true;
		}
		player.getInventory().addItem(item);
		player.sendMessage(MsgUtils.color(instance.messages().getString("commands.skull").replace("%player%", name)));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}

}
