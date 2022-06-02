package com.matchamc.core.bukkit.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

public class GamemodeCmd extends CoreCommand {
	private String permissionCreative, permissionSurvival, permissionAdventure, permissionSpectator, permissionOthers;
	private List<String> gamemodes = Arrays.asList("creative", "survival", "adventure", "spectator");
	public GamemodeCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
		this.permissionCreative = permissionNode + ".creative";
		this.permissionSurvival = permissionNode + ".survival";
		this.permissionAdventure = permissionNode + ".adventure";
		this.permissionSpectator = permissionNode + ".spectator";
		this.permissionOthers = permissionNode + ".others";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(MsgUtils.color("&cUsage: /gamemode <mode> <player>"));
			return true;
		}
		if(args.length == 1) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
				return true;
			}
			Player player = (Player) sender;
			switch(args[0].toLowerCase()) {
			case "c":
			case "creative":
			case "1":
				if(!(sender.hasPermission(permissionCreative))) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionCreative));
					return true;
				}
				player.setGameMode(GameMode.CREATIVE);
				player.sendMessage(formatGamemodeMsg(player, GameMode.CREATIVE, null));
				break;
			case "s":
			case "survival":
			case "0":
				if(!(sender.hasPermission(permissionSurvival))) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionSurvival));
					return true;
				}
				player.setGameMode(GameMode.SURVIVAL);
				player.sendMessage(formatGamemodeMsg(player, GameMode.SURVIVAL, null));
				break;
			case "a":
			case "adventure":
			case "2":
				if(!(sender.hasPermission(permissionAdventure))) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionAdventure));
					return true;
				}
				player.setGameMode(GameMode.ADVENTURE);
				player.sendMessage(formatGamemodeMsg(player, GameMode.ADVENTURE, null));
				break;
			case "sp":
			case "spectator":
			case "3":
				if(!(sender.hasPermission(permissionSpectator))) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionSpectator));
					return true;
				}
				player.setGameMode(GameMode.SPECTATOR);
				player.sendMessage(formatGamemodeMsg(player, GameMode.SPECTATOR, null));
				break;
			default:
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.gamemode.invalid")));
				break;
			}
			return true;
		}
		if(!sender.hasPermission(permissionOthers)) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionOthers));
		}
		Player target = Bukkit.getPlayer(args[1]);
		if(target == null) {
			sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
			return true;
		}
		switch(args[0].toLowerCase()) {
		case "c":
		case "creative":
		case "1":
			target.setGameMode(GameMode.CREATIVE);
			target.sendMessage(formatGamemodeMsg(target, GameMode.CREATIVE, null));
			sender.sendMessage(formatGamemodeMsg(sender, GameMode.CREATIVE, target));
			break;
		case "s":
		case "survival":
		case "0":
			target.setGameMode(GameMode.SURVIVAL);
			target.sendMessage(formatGamemodeMsg(target, GameMode.SURVIVAL, null));
			sender.sendMessage(formatGamemodeMsg(sender, GameMode.SURVIVAL, target));
			break;
		case "a":
		case "adventure":
		case "2":
			target.setGameMode(GameMode.ADVENTURE);
			target.sendMessage(formatGamemodeMsg(target, GameMode.ADVENTURE, null));
			sender.sendMessage(formatGamemodeMsg(sender, GameMode.ADVENTURE, target));
			break;
		case "sp":
		case "spectator":
		case "3":
			target.setGameMode(GameMode.SPECTATOR);
			target.sendMessage(formatGamemodeMsg(target, GameMode.SPECTATOR, null));
			sender.sendMessage(formatGamemodeMsg(sender, GameMode.SPECTATOR, target));
			break;
		default:
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.gamemode.invalid")));
			break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) 
			return gamemodes.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		if(args.length == 1)
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
		return Collections.emptyList();
	}

	private String formatGamemodeMsg(CommandSender sender, GameMode gamemode, Player target) {
		if(target == null) {
			return MsgUtils.color(instance.messages().getString("commands.gamemode.updated").replace("%gamemode%", gamemode.name().toLowerCase()));
		}
		return MsgUtils.color(instance.messages().getString("commands.gamemode.updated_others").replace("%player%", target.getName()).replace("%gamemode%", gamemode.name().toLowerCase()));
	}
}
