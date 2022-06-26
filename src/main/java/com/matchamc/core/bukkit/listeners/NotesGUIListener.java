package com.matchamc.core.bukkit.listeners;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
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
		if(event.getClickedInventory() == null || event.getCurrentItem() == null)
			return;
		if(!event.getView().getTitle().equalsIgnoreCase("Your Notes") && !event.getView().getTitle().equalsIgnoreCase("Your Deleted Notes"))
			return;
		if(event.getCurrentItem().getType() != Material.OAK_SIGN)
			return;
		event.setCancelled(true);
		String stringNoteId = event.getCurrentItem().getItemMeta().getLore().get(0).replaceAll("\\D*", "");
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
		int id = Integer.parseInt(event.getView().getTitle().replaceAll("\\D*", ""));
		Note note = new Note(notes, id);
		switch(event.getCurrentItem().getType()) {
		case ARROW:
			event.getWhoClicked().closeInventory();
			((Player) event.getWhoClicked()).performCommand("notes");
			break;
		case BARRIER:
			boolean result = note.delete();
			if(!result) {
				event.getWhoClicked().sendMessage(MsgUtils.color("&cThis note is already deleted."));
				return;
			}
			event.getWhoClicked().closeInventory();
			event.getWhoClicked().sendMessage(MsgUtils.color("&eThis note has been deleted. To restore or permanently delete it, you may use the command '/notes viewdeleted' to view your deleted notes."));
			break;
		case GREEN_WOOL:
			if(!note.isDeleted()) {
				event.getWhoClicked().sendMessage(MsgUtils.color("&cAn internal error occurred. More details have been printed in the console."));
				MsgUtils.sendBukkitConsoleMessage("&cAttempted to restore Note #" + note.getId() + ", but the note is not deleted.");
				MsgUtils.sendBukkitConsoleMessage("&cThis indicates a problem with the GUI as it is not supposed to display the option to restore.");
				return;
			}
			note.restore();
			event.getWhoClicked().closeInventory();
			event.getWhoClicked().sendMessage(MsgUtils.color("&eThis note has been restored."));
			break;
		case FLINT_AND_STEEL:
			event.getWhoClicked().closeInventory();
			note.destroyNote();
			event.getWhoClicked().sendMessage(MsgUtils.color("&eThis note has been permanently deleted."));
			break;
		default:
			break;
		}
	}

	private void openNoteManagementGUI(Player player, Note note) {
		// 12 is where the sign is at
		// 30 is where option 3 is at
		// 28 is where option 2 is at (if applicable)
		// 26 is where option 1 is at
		DateTimeFormatter usageFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss").withZone(timezones.getEntry(player.getUniqueId()));
		Inventory inv = Bukkit.createInventory(null, 54, "Manage Note #" + note.getId());
		ItemStack sign = new ItemStack(Material.OAK_SIGN);
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta signmeta = sign.getItemMeta();
		ItemMeta backmeta = back.getItemMeta();
		signmeta.setDisplayName(MsgUtils.color("&bNote: " + note.getContent()));
		signmeta.setLore(Arrays.asList(MsgUtils.color("&7ID: &e" + note.getId()), MsgUtils.color("&7Created: &e" + usageFormatter.format(Instant.ofEpochMilli(note.getCreationTimeInMillis())))));
		backmeta.setDisplayName("Back");
		back.setItemMeta(backmeta);
		sign.setItemMeta(signmeta);
		inv.setItem(12, sign);
		inv.setItem(26, back);
		if(!note.isDeleted()) {
			ItemStack delete = new ItemStack(Material.BARRIER);
			ItemMeta deletemeta = delete.getItemMeta();
			deletemeta.setDisplayName("Delete Note");
			delete.setItemMeta(deletemeta);
			inv.setItem(30, delete);
			player.closeInventory();
			player.openInventory(inv);
			return;
		}
		ItemStack restore = new ItemStack(Material.GREEN_WOOL);
		ItemStack permdelete = new ItemStack(Material.FLINT_AND_STEEL);
		ItemMeta restoremeta = restore.getItemMeta();
		ItemMeta permdeletemeta = permdelete.getItemMeta();
		restoremeta.setDisplayName("Restore");
		permdeletemeta.setDisplayName("Permanently Delete");
		permdeletemeta.setLore(Arrays.asList(MsgUtils.color("&c&lWARNING: This action is irrevocable.")));
		permdeletemeta.addEnchant(Enchantment.DURABILITY, 10, true);
		permdeletemeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		restore.setItemMeta(restoremeta);
		permdelete.setItemMeta(permdeletemeta);
		inv.setItem(28, permdelete);
		inv.setItem(30, restore);
		player.closeInventory();
		player.openInventory(inv);
	}
}
