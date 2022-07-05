package com.matchamc.core.bukkit.commands.staff;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.Staffs;
import com.matchamc.shared.MsgUtils;

public class StaffRandomTPCmd extends CoreCommand {
	private Staffs staffs;

	public StaffRandomTPCmd(BukkitMain instance, Staffs staffs, String permissionNode) {
		super(instance, permissionNode);
		this.staffs = staffs;
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
		Random random = new Random();
		Stream<? extends Player> stream = Bukkit.getOnlinePlayers().stream().filter(p -> !staffs.isStaff(p)).filter(p -> !p.getName().equals(sender.getName()));
		Player target = stream.skip(random.nextInt((int) stream.count())).findAny().get();
		player.teleport(target);
		player.sendMessage(MsgUtils.color("&eYou have randomly teleported to &a" + target.getName() + "&e."));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}

}
