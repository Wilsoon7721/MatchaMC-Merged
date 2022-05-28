package com.matchamc.core.bungee.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.matchamc.core.bungee.BungeeMain;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class SendToAllCmd extends Command implements TabExecutor {
	// Issue a command to all servers
	public SendToAllCmd() {
		super("sendtoall");

	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender.hasPermission("core.bungee.sendtoall"))) {
			sender.sendMessage(BungeeMain.NO_PERMISSION_ERROR);
			return;
		}
		if(args.length == 0) {
			sender.sendMessage(BungeeMain.INSUFFICIENT_PARAMETERS_ERROR);
			return;
		}
		// data_1VSGK is the send to all command data format
		String data = "data_1VSGK " + String.join(" ", args).trim();
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF("ALL");
		out.writeUTF("BungeeCord");
		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF(data);
		} catch(IOException ex) {
			sender.sendMessage(new ComponentBuilder().append(BungeeMain.PLUGIN_PREFIX).append(new ComponentBuilder("Failed to send the command to all servers - An internal error occurred. Check console for more information.").color(ChatColor.RED).create()).create());
			return;
		}
		byte[] msgbytearray = msgbytes.toByteArray();
		out.writeShort(msgbytearray.length);
		out.write(msgbytearray);
		sender.sendMessage(new ComponentBuilder("Successfully ran '").color(ChatColor.GREEN).append(new ComponentBuilder(data).color(ChatColor.YELLOW).create()).append(new ComponentBuilder("' on ").color(ChatColor.GREEN).create()).append(new ComponentBuilder(String.valueOf(ProxyServer.getInstance().getServers().keySet().size())).color(ChatColor.YELLOW).append(new ComponentBuilder(" servers on the network.").color(ChatColor.GREEN).create()).create()).create());

	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

}
