package com.matchamc.automod.bukkit;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.matchamc.automod.shared.ChatPlayer;
import com.matchamc.automod.shared.Module;
import com.matchamc.automod.shared.modules.BlacklistModule;
import com.matchamc.automod.shared.modules.CapsModule;
import com.matchamc.automod.shared.modules.CooldownModule;
import com.matchamc.automod.shared.modules.FloodModule;
import com.matchamc.automod.shared.modules.VerifierModule;
import com.matchamc.shared.MsgUtils;

import net.md_5.bungee.api.ChatColor;

public class ModuleListeners implements Listener {
	private AutoMod autoMod;
	private CapsModule capsModule;
	private BlacklistModule blacklistModule;
	private FloodModule floodModule;
	private CooldownModule cooldownModule;
	private VerifierModule verifierModule;

	protected ModuleListeners(AutoMod autoMod) {
		this.autoMod = autoMod;
		for(Module module : autoMod.getActiveModules()) {
			try {
				capsModule = AutoMod.getModuleAs(module, CapsModule.class);
				if(capsModule != null)
					continue;
				blacklistModule = AutoMod.getModuleAs(module, BlacklistModule.class);
				if(blacklistModule != null)
					continue;
				floodModule = AutoMod.getModuleAs(module, FloodModule.class);
				if(floodModule != null)
					continue;
				cooldownModule = AutoMod.getModuleAs(module, CooldownModule.class);
				if(cooldownModule != null)
					continue;
				verifierModule = AutoMod.getModuleAs(module, VerifierModule.class);
				if(verifierModule != null)
					continue;
			} catch(ClassCastException ex) {
			}
		}
	}

	// VerifierModule (EVENTPRIORITY IS LOWEST TO ENSURE THAT MESSAGE IS FORMATTED BEFORE THE OTHER LISTENERS USE THE MESSAGE)
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerMessageFormat(AsyncPlayerChatEvent event) {
		if(verifierModule == null)
			return;
		String modifiedMessage = verifierModule.formatMessage(event.getMessage());
		if(!verifierModule.isEnabled())
			return;
		Pattern pattern = verifierModule.getExpressionsPattern(), namesPattern = verifierModule.getNamesPattern();
		modifiedMessage = pattern.matcher(namesPattern.matcher(modifiedMessage).replaceAll("")).replaceAll("").trim();
		event.setMessage(modifiedMessage);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerCommandFormat(PlayerCommandPreprocessEvent event) {
		if(verifierModule == null)
			return;
		String[] s = event.getMessage().split(" ");
		String cmd = "/" + s[0];
		if(!verifierModule.getCheckedCommands().contains(cmd))
			return;
		String msg;
		Pattern pattern = verifierModule.getExpressionsPattern(), namesPattern = verifierModule.getNamesPattern();
		if(cmd.equalsIgnoreCase("/whisper") || cmd.equalsIgnoreCase("/msg") || cmd.equalsIgnoreCase("/message") || cmd.equalsIgnoreCase("/tell")) {
			msg = String.join(" ", Arrays.copyOfRange(s, 1, s.length)).trim();
			String modifiedMessage = verifierModule.formatMessage(msg);
			modifiedMessage = pattern.matcher(namesPattern.matcher(modifiedMessage).replaceAll("")).replaceAll("").trim();
			event.setMessage(s[0] + " " + s[1] + " " + modifiedMessage);
			return;
		}
		if(cmd.equalsIgnoreCase("/r") || cmd.equalsIgnoreCase("/reply") || cmd.equalsIgnoreCase("/bc") || cmd.equalsIgnoreCase("/broadcast")) {
			msg = String.join(" ", s).trim();
			String modifiedMessage;
			modifiedMessage = verifierModule.formatMessage(msg);
			modifiedMessage = pattern.matcher(namesPattern.matcher(modifiedMessage).replaceAll("")).replaceAll("").trim();
			event.setMessage(s[0] + " " + modifiedMessage);
			return;
		}
	}

	// CapsModule
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerCapsChat(AsyncPlayerChatEvent event) {
		if(capsModule == null)
			return;
		if(event.getPlayer().hasPermission(capsModule.getBypassPermission()))
			return;
		ChatPlayer chatPlayer = ChatPlayer.getChatPlayer(event.getPlayer().getUniqueId());
		String msg = ChatColor.stripColor(event.getMessage());
		int capsCount = capsModule.capsCount(msg);
		if(!capsModule.meetsCondition(chatPlayer, msg))
			return;
		event.setCancelled(true);
		String[][] placeholders = { { "player", event.getPlayer().getName() }, { "caps", String.valueOf(capsCount) }, { "threshold", String.valueOf(capsModule.getCapsThreshold()) } };
		autoMod.getStaffs().getAllStaff().stream().map(Bukkit::getPlayer).filter(player -> (player != null)).forEach(staff -> staff.sendMessage(autoMod.getMessage("caps.staff_notification", placeholders)));
		String warningMessage = autoMod.getMessage("caps.warning", null);
		if(capsModule.isReplace()) {
			Player player = chatPlayer.toBukkitPlayer();
			if(player == null)
				return;
			player.sendMessage(warningMessage);
			if(event.isCancelled())
				return; // do not send any message
			String formattedMessage = event.getMessage().toLowerCase();
			player.chat(formattedMessage);
			return;
		}
		Player player = chatPlayer.toBukkitPlayer();
		if(player == null)
			return;
		player.sendMessage(warningMessage);

	}

	// BlacklistModule
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerMessageBlacklisted(AsyncPlayerChatEvent event) {
		if(blacklistModule == null)
			return;
		if(event.getPlayer().hasPermission(blacklistModule.getBypassPermission()))
			return;
		ChatPlayer chatPlayer = ChatPlayer.getChatPlayer(event.getPlayer().getUniqueId());
		String msg = ChatColor.stripColor(event.getMessage());
		if(!blacklistModule.meetsCondition(chatPlayer, msg))
			return;
		event.setCancelled(true);
		String[][] staffNotificationPlaceholders = { { "player", event.getPlayer().getName() }, { "message", msg } };
		String warningMessage = MsgUtils.color(autoMod.getMessage("blacklist.warning", null));
		String filterMessage = MsgUtils.color(autoMod.getMessage("blacklist.filtered_message", null));
		String staffNotification = MsgUtils.color(autoMod.getMessage("blacklist.staff_notification", staffNotificationPlaceholders));
		autoMod.getStaffs().getAllStaff().stream().map(Bukkit::getPlayer).filter(player -> (player != null)).forEach(staff -> staff.sendMessage(staffNotification));
		event.getPlayer().sendMessage(warningMessage);
		if(blacklistModule.filterEnabled()) {
			String filteredMsg = blacklistModule.getPattern().matcher(msg).replaceAll("***");
			event.getPlayer().sendMessage(filterMessage);
			event.getPlayer().chat(filteredMsg);
		}
		return;
	}

	// FloodModule
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerMessageFlood(AsyncPlayerChatEvent event) {
		if(floodModule == null)
			return;
		if(event.getPlayer().hasPermission(floodModule.getBypassPermission()))
			return;
		ChatPlayer chatPlayer = ChatPlayer.getChatPlayer(event.getPlayer().getUniqueId());
		String msg = ChatColor.stripColor(event.getMessage());
		if(!(floodModule.meetsCondition(chatPlayer, msg)))
			return;
		event.setCancelled(true);
		String[][] staffNotificationPlaceholders = { { "player", event.getPlayer().getName() }, { "message", msg } };
		String warningMessage = MsgUtils.color(autoMod.getMessage("flood.warning", null));
		String filterMessage = MsgUtils.color(autoMod.getMessage("flood.filtered_message", null));
		String staffNotification = MsgUtils.color(autoMod.getMessage("flood.staff_notification", staffNotificationPlaceholders));
		autoMod.getStaffs().getAllStaff().stream().map(Bukkit::getPlayer).filter(player -> (player != null)).forEach(staff -> staff.sendMessage(staffNotification));
		event.getPlayer().sendMessage(warningMessage);
		if(floodModule.isReplace()) {
			String filteredMsg = floodModule.replace(msg);
			event.getPlayer().sendMessage(filterMessage);
			event.getPlayer().chat(filteredMsg);
			return;
		}
	}

	// CooldownModule
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onPlayerOnCooldown(AsyncPlayerChatEvent event) {
		if(cooldownModule == null)
			return;
		if(event.getPlayer().hasPermission(cooldownModule.getBypassPermission()))
			return;
		ChatPlayer chatPlayer = ChatPlayer.getChatPlayer(event.getPlayer().getUniqueId());
		if(cooldownModule.meetsCondition(chatPlayer, event.getMessage()))
			return;
		String[][] warningPlaceholders = { { "delay", String.valueOf(cooldownModule.getCooldown(event.getPlayer().getUniqueId())) } };
		event.getPlayer().sendMessage(autoMod.getMessage("cooldown.warning", warningPlaceholders));
	}
}
