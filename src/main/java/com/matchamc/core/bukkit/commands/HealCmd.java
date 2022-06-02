package com.matchamc.core.bukkit.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

public class HealCmd extends CoreCommand {
	private List<PotionEffectType> negativeEffects;
	public HealCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
		this.negativeEffects = Arrays.asList(PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.CONFUSION, PotionEffectType.BLINDNESS, PotionEffectType.HUNGER, PotionEffectType.WEAKNESS, PotionEffectType.POISON, PotionEffectType.WITHER, PotionEffectType.UNLUCK, PotionEffectType.HARM, PotionEffectType.BAD_OMEN);
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
			Player player = ((Player) sender);
			player.setHealth(20);
			for(PotionEffectType type : player.getActivePotionEffects().stream().filter(e -> negativeEffects.contains(e.getType())).map(effect -> effect.getType()).collect(Collectors.toSet())) {
				player.removePotionEffect(type);
			}
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.heal.main_message")));
			return true;
		}
		if(!sender.hasPermission(permissionNode + ".others")) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode + ".others"));
			return true;
		}
		Player target = Bukkit.getPlayer(args[0]);
		if(target == null) {
			sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
			return true;
		}
		for(PotionEffectType type : target.getActivePotionEffects().stream().filter(e -> negativeEffects.contains(e.getType())).map(effect -> effect.getType()).collect(Collectors.toSet())) {
			target.removePotionEffect(type);
		}
		target.setHealth(20);
		target.sendMessage(MsgUtils.color(instance.messages().getString("commands.heal.main_message")));
		sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.heal.healed_others")));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return Collections.emptyList();
	}
}
