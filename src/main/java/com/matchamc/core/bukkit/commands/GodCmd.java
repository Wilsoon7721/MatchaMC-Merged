package com.matchamc.core.bukkit.commands;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

public class GodCmd extends CoreCommand implements Listener {
	private Set<UUID> godEnabled = new HashSet<>();
	public GodCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission(permissionNode)) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(args.length == 0) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
				return true;
			}
			Player player = (Player) sender;
			if(godEnabled.contains(player.getUniqueId())) {
				godEnabled.remove(player.getUniqueId());
				player.sendMessage(MsgUtils.color(instance.messages().getString("commands.god.disabled")));
				return true;
			}
			godEnabled.add(player.getUniqueId());
			player.sendMessage(MsgUtils.color(instance.messages().getString("commands.god.enabled")));
			return true;
		}
		if(!(sender.hasPermission(permissionNode + ".others"))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode + ".others"));
			return true;
		}
		Player target = Bukkit.getPlayer(args[0]);
		if(target == null) {
			sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
			return true;
		}
		if(godEnabled.contains(target.getUniqueId())) {
			godEnabled.remove(target.getUniqueId());
			target.sendMessage(MsgUtils.color(instance.messages().getString("commands.god.disabled")));
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.god.others").replace("%player%", target.getName()).replace("%state%", "&cdisabled")));
			return true;
		}
		godEnabled.add(target.getUniqueId());
		target.sendMessage(MsgUtils.color(instance.messages().getString("commands.god.enabled")));
		sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.god.others").replace("%player%", target.getName()).replace("%state%", "&aenabled")));
		return true;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		if(!godEnabled.contains(player.getUniqueId()))
			return;
		event.setCancelled(true);
		event.setDamage(0);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return Collections.emptyList();
	}

}
