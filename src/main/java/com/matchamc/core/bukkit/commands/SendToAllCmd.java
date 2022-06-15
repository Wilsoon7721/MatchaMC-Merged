package com.matchamc.core.bukkit.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class SendToAllCmd extends CoreCommand {
	// Issue a command to all servers

	public SendToAllCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(BukkitMain.NO_PERMISSION_ERROR);
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(MsgUtils.color("&cUsage: /sendtoall <command>"));
			return true;
		}
		// data_1VSGK is the send to all command data format
		String data = String.join(" ", args).trim();
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF("ALL");
		out.writeUTF("MatchaMC_ServerPlugin");
		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF(data);
		} catch(IOException ex) {
			sender.sendMessage(MsgUtils.color("&cFailed to send command to all proxies. An internal error occurred, check console for more details."));
			ex.printStackTrace();
			return true;
		}
		byte[] msgbytearray = msgbytes.toByteArray();
		out.writeShort(msgbytearray.length);
		out.write(msgbytearray);
		Bukkit.getServer().sendPluginMessage(instance, "MatchaMC_ServerPlugin", out.toByteArray());
		sender.spigot().sendMessage(new ComponentBuilder("Successfully ran '").color(ChatColor.GREEN).append(new ComponentBuilder(data).color(ChatColor.YELLOW).create()).append(new ComponentBuilder("' on ").color(ChatColor.GREEN).create()).append(new ComponentBuilder(String.valueOf(ProxyServer.getInstance().getServers().keySet().size())).color(ChatColor.YELLOW).append(new ComponentBuilder(" servers on the network.").color(ChatColor.GREEN).create()).create()).create());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}

}
