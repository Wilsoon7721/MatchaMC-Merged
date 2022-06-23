package com.matchamc.core.bukkit.listeners;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.matchamc.core.bukkit.util.Note;
import com.matchamc.core.bukkit.util.Notes;
import com.matchamc.core.bukkit.util.TimeZones;
import com.matchamc.shared.MsgUtils;

public class NotesGUIListener implements Listener {
	private Notes notes;
	private TimeZones timezones;

	public NotesGUIListener(Notes notes, TimeZones timezones) {
		this.notes = notes;
		this.timezones = timezones;
	}
	@EventHandler
	public void onNotesClick(InventoryClickEvent event) {
		if(event.getClickedInventory() == null || !event.getView().getTitle().equalsIgnoreCase("Your Notes") || event.getCurrentItem() == null)
			return;
		if(event.getCurrentItem().getType() != Material.OAK_SIGN)
			return;
		event.setCancelled(true);
		String stringNoteId = event.getCurrentItem().getItemMeta().getLore().get(0).split(" ")[1];
		int noteId;
		try {
			noteId = Integer.parseInt(stringNoteId);
		} catch(NumberFormatException ex) {
			event.getWhoClicked().sendMessage(MsgUtils.color("&cAn internal error occurred."));
			MsgUtils.sendBukkitConsoleMessage("&cThe plugin was unable to parse '" + stringNoteId + "' as an integer.");
			return;
		}
		Note note = new Note(notes, noteId);
		event.getWhoClicked().closeInventory();
		openNoteManagementGUI((Player) event.getWhoClicked(), note);
	}

	@EventHandler
	public void onNoteMgmtClick(InventoryClickEvent event) {
		if(event.getClickedInventory() == null || !event.getView().getTitle().startsWith("Manage Note #") || event.getCurrentItem() == null)
			return;
		if(event.getCurrentItem() == null)
			return;
		event.setCancelled(true);
		if(event.getCurrentItem().getType() == Material.ARROW) {
			event.getWhoClicked().closeInventory();
			((Player) event.getWhoClicked()).performCommand("notes");
			return;
		}
		if(event.getCurrentItem().getType() == Material.BARRIER) {
			// TODO Delete note
		}
	}

	private void openNoteManagementGUI(Player player, Note note) {
		// 12 is where the sign is at
		// 30 is where option 2 is at
		// 26 is where option 1 is at
		DateTimeFormatter usageFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss").withZone(timezones.getEntry(player.getUniqueId()));
		Inventory inv = Bukkit.createInventory(null, 54, "Manage Note #" + note.getId());
		ItemStack sign = new ItemStack(Material.OAK_SIGN);
		ItemStack back = new ItemStack(Material.ARROW);
		ItemStack delete = new ItemStack(Material.BARRIER);
		ItemMeta signmeta = sign.getItemMeta();
		ItemMeta backmeta = back.getItemMeta();
		ItemMeta deletemeta = delete.getItemMeta();
		signmeta.setDisplayName(MsgUtils.color("&bNote: " + note.getContent()));
		signmeta.setLore(Arrays.asList(MsgUtils.color("&7ID: &e" + note.getId()), MsgUtils.color("&7Created: &e" + usageFormatter.format(Instant.ofEpochMilli(note.getCreationTimeInMillis())))));
		backmeta.setDisplayName("Back");
		deletemeta.setDisplayName("Delete Note");
		back.setItemMeta(backmeta);
		sign.setItemMeta(signmeta);
		delete.setItemMeta(deletemeta);
		inv.setItem(12, sign);
		inv.setItem(26, back);
		inv.setItem(30, delete);
		player.closeInventory();
		player.openInventory(inv);
	}
}
