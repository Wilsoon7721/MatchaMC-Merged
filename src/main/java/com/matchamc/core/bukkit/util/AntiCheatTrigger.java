package com.matchamc.core.bukkit.util;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import AntiAuraAPI.DragBackEvent;
import AntiAuraAPI.ViolationEvent;

public class AntiCheatTrigger implements Listener {
	public static int ping, dragbackPing;
	public static String target, hack, dragbackTarget, dragbackHack;
	public static UUID uuid;
	@EventHandler
	public int ping(ViolationEvent event) {
		ping = event.getNMSPing();
		uuid = event.getPlayer().getUniqueId();
		return ping;
	}

	@EventHandler
	public String target(ViolationEvent event) {
		target = event.getPlayer().getName();
		uuid = event.getPlayer().getUniqueId();
		return target;
	}

	@EventHandler
	public String hack(ViolationEvent event) {
		hack = event.getHack();
		uuid = event.getPlayer().getUniqueId();
		return hack;
	}

	@EventHandler
	public int dbping(DragBackEvent event) {
		dragbackPing = event.getNMSPing();
		return dragbackPing;
	}

	@EventHandler
	public String dbtarget(DragBackEvent event) {
		dragbackTarget = event.getPlayer().getName();
		return dragbackTarget;
	}

	@EventHandler
	public String dbhack(DragBackEvent event) {
		dragbackHack = event.getHack();
		return dragbackHack;
	}
}
