package com.matchamc.core.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.Reports;

public class ReportsGUIListener implements Listener {
	private BukkitMain instance;
	private Reports reports;

	public ReportsGUIListener(BukkitMain instance, Reports reports) {
		this.instance = instance;
		this.reports = reports;
	}

	@EventHandler
	public void onReportReasonGUIInteract(InventoryClickEvent event) {
		if(event.getClickedInventory() == null || event.getCurrentItem() == null)
			return;
		if(!(event.getView().getTitle().startsWith("Report Player: ")))
			return;
		event.setCancelled(true);
		switch(event.getCurrentItem().getType()) {
		case NETHERITE_SWORD:
			// Unfair Advantage
			break;
		case PAPER:
			// Chat
			break;
		case BEDROCK:
			// Other
			break;
		default:
			break;
		}
	}
}
