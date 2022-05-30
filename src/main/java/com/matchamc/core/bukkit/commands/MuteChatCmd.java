package com.matchamc.core.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.util.MsgUtils;
import com.matchamc.shared.util.Staffs;

public class MuteChatCmd extends CoreCommand implements Listener {
	private boolean chatMuted = false;
	private Staffs staffs;

	public MuteChatCmd(BukkitMain instance, Staffs staffs, String permissionNode) {
		super(instance, permissionNode);
		this.staffs = staffs;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		// TODO MuteChat
		if(chatMuted) {
			chatMuted = false;
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.mute_chat.chat_unmuted").replace("%player%", sender.getName())));
			return true;
		}
		chatMuted = true;
		sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.mute_chat.chat_muted").replace("%player%", sender.getName())));
		return true;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if(event.getPlayer().hasPermission(permissionNode + ".bypass"))
			return;
		if(!chatMuted)
			return;
		if(staffs.isStaff(event.getPlayer()))
			return;
		event.setCancelled(true);
		event.getPlayer().sendMessage(MsgUtils.color(instance.messages().getString("commands.mute_chat.message_while_muted")));
	}
}
