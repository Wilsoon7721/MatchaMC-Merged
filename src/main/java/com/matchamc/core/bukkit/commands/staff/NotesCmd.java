package com.matchamc.core.bukkit.commands.staff;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.Note;
import com.matchamc.core.bukkit.util.Notes;
import com.matchamc.core.bukkit.util.TimeZones;
import com.matchamc.shared.MsgUtils;

public class NotesCmd extends CoreCommand {
	private Notes notes;
	private TimeZones timezones;
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

	public NotesCmd(BukkitMain instance, TimeZones timezones, Notes notes, String permissionNode) {
		super(instance, permissionNode);
		this.timezones = timezones;
		this.notes = notes;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(!(sender instanceof Player)) {
			List<Note> consoleNotes = notes.getNotesByConsole().stream().collect(Collectors.toList());
			if(consoleNotes.isEmpty()) {
				sender.sendMessage(MsgUtils.color("&cYou do not have any notes saved."));
				return true;
			}
			DateTimeFormatter usageFormatter = formatter.withZone(ZoneOffset.UTC);
			consoleNotes.stream().sorted(Comparator.comparing(Note::getCreationTimeInMillis)).forEachOrdered(note -> sender.sendMessage(MsgUtils.color("&7[&e%CREATION%&7] &e#%ID% - '%CONTENT%'".replace("%CREATION%", usageFormatter.format(Instant.ofEpochMilli(note.getCreationTimeInMillis()))).replace("%ID%", String.valueOf(note.getId())).replace("%CONTENT%", note.getContent()))));
			return true;
		}
		Player player = (Player) sender;
		List<Note> playerNotes = notes.getNotesByCreator(player.getUniqueId()).stream().collect(Collectors.toList());
		if(playerNotes.isEmpty()) {
			sender.sendMessage(MsgUtils.color("&cYou do not have any notes saved."));
			return true;
		}
		displayGUI(player, playerNotes);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}

	private void displayGUI(Player player, List<Note> notes) {
		notes.sort(Comparator.comparing(Note::getCreationTimeInMillis));
		DateTimeFormatter usageFormatter = formatter.withZone(timezones.getEntry(player.getUniqueId()));
		Inventory inv = Bukkit.createInventory(null, getInventorySize(notes.size()), "Your Notes");
		for(Note note : notes) {
			ItemStack item = new ItemStack(Material.OAK_SIGN);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(MsgUtils.color("&bNote: " + note.getContent()));
			meta.setLore(Arrays.asList(MsgUtils.color("&7ID: &e" + note.getId()), MsgUtils.color("&7Created: &e" + usageFormatter.format(Instant.ofEpochMilli(note.getCreationTimeInMillis())))));
			item.setItemMeta(meta);
			inv.addItem(item);
		}
		player.closeInventory();
		Bukkit.getScheduler().runTask(instance, () -> player.openInventory(inv));
	}

	private int getInventorySize(int size) {
		if(size <= 0)
			return 9;
		int q = (int) Math.ceil(size / 9);
		return q > 5 ? 54 : q * 9;
	}

}
