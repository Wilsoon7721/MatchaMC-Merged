package com.matchamc.core.bukkit.commands;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

public class NightVisionCmd extends CoreCommand {
	private Set<UUID> nightVisionEnabled = new HashSet<>();
	public NightVisionCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this.instance, () -> {
			if(nightVisionEnabled.isEmpty()) return;
			for(UUID uuid : nightVisionEnabled) {
				Player target = Bukkit.getPlayer(uuid);
				if(target == null) continue;
				if(target.getPotionEffect(PotionEffectType.NIGHT_VISION) != null) return;
				target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 5, false, false));
			}
		}, 0L, (long) (0.5 * 20L));
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
		if(nightVisionEnabled.contains(player.getUniqueId())) {
			nightVisionEnabled.remove(player.getUniqueId());
			player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			player.sendMessage(MsgUtils.color(instance.messages().getString("commands.night_vision.disabled")));
			return true;
		}
		nightVisionEnabled.add(player.getUniqueId());
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 5, false, false));
		player.sendMessage(MsgUtils.color(instance.messages().getString("commands.night_vision.enabled")));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}

}
