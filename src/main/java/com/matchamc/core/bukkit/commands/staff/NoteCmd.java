package com.matchamc.core.bukkit.commands.staff;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.Notes;
import com.matchamc.shared.MsgUtils;

import net.md_5.bungee.api.ChatColor;

public class NoteCmd extends CoreCommand {
	private Notes notes;

	public NoteCmd(BukkitMain instance, Notes notes, String permissionNode) {
		super(instance, permissionNode);
		this.notes = notes;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(MsgUtils.color("&cUnable to create a blank note."));
			sender.sendMessage(MsgUtils.color("&cUsage: /note <note>"));
			return true;
		}
		String note = ChatColor.stripColor(MsgUtils.color(String.join(" ", args).trim()));
		if(!(sender instanceof Player)) {
			notes.createNote("@CONSOLE", note);
			return true;
		}
		notes.createNote((Player) sender, note);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}

}
