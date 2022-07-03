package com.matchamc.core.bukkit.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.StringUtil;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.Chat.Channel;
import com.matchamc.shared.MsgUtils;

public class ChatHistory extends CoreCommand implements Listener {
	private Chat chat;
	private Staffs staffs;
	private PlayerRegistrar registrar;
	private File historyDirectory;
	private Map<UUID, ArrayList<String>> history = new HashMap<>();
	private Collection<String> c;

	public ChatHistory(BukkitMain instance, Staffs staffs, PlayerRegistrar registrar, Chat chat, String permissionNode) {
		super(instance, permissionNode);
		this.registrar = registrar;
		this.staffs = staffs;
		this.chat = chat;
		historyDirectory = new File(this.instance.getDataFolder(), "/chathistory/");
		if(!(historyDirectory.exists()))
			historyDirectory.mkdir();
		c = registrar.getAllRegisteredPlayerNames();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(MsgUtils.color("&cUsage: /chathistory <player>"));
			return true;
		}
		String username = args[0];
		Collection<UUID> results = registrar.getAllUUIDsMatchingName(username, true);
		if(results.isEmpty()) {
			sender.sendMessage(MsgUtils.color("&cNo matching player found."));
			return true;
		}
		UUID result = results.iterator().next();
		if(!registrar.isRegistered(result)) {
			sender.sendMessage(MsgUtils.color("&cA severe internal error has occurred."));
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC] ChatHistory - Severe error: PlayerRegistrar#getAllUUIDsMatchingName found this result &e" + result.toString() + " &cbut PlayerRegistrar#isRegistered could not verify this player's registration. (Where did the UUID come from?)");
			return true;
		}
		// TODO get player chat history
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return StringUtil.copyPartialMatches(args[0], c, new ArrayList<>());
		return Collections.emptyList();
	}

	// TODO Other methods to retrieve chat history of player

	public void saveData() {
		for(Entry<UUID, ArrayList<String>> entry : history.entrySet()) {
			File file = new File(historyDirectory, entry.getKey().toString() + ".txt");
			try(FileWriter fw = new FileWriter(file, true); BufferedWriter bw = new BufferedWriter(fw); PrintWriter pw = new PrintWriter(bw)) {
				entry.getValue().stream().forEachOrdered(s -> pw.println(s));
			} catch(IOException ex) {
				MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC] ChatHistory - Save failed.");
				ex.printStackTrace();
				continue;
			}
			history.remove(entry.getKey());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		checkPlayerFiles(event.getPlayer().getUniqueId());
		ArrayList<String> playerChat = history.get(event.getPlayer().getUniqueId());
		playerChat.add("" + System.currentTimeMillis() + " |> PLAYER LOGGED IN");
		history.remove(event.getPlayer().getUniqueId());
		history.put(event.getPlayer().getUniqueId(), playerChat);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		checkPlayerFiles(event.getPlayer().getUniqueId());
		ArrayList<String> playerChat = history.get(event.getPlayer().getUniqueId());
		playerChat.add("" + System.currentTimeMillis() + " |> PLAYER LOGGED OUT");
		history.remove(event.getPlayer().getUniqueId());
		history.put(event.getPlayer().getUniqueId(), playerChat);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event) {
		if(chat.getPlayerChannel(event.getPlayer()) != Channel.ALL)
			return;
		checkPlayerFiles(event.getPlayer().getUniqueId());
		ArrayList<String> playerChat = history.get(event.getPlayer().getUniqueId());
		playerChat.add("" + System.currentTimeMillis() + " | " + event.getMessage());
		history.remove(event.getPlayer().getUniqueId());
		history.put(event.getPlayer().getUniqueId(), playerChat);
	}

	private void checkPlayerFiles(UUID uuid) {
		if(history.get(uuid) == null)
			history.put(uuid, new ArrayList<String>());
		File playerFile = new File(historyDirectory, uuid.toString() + ".txt");
		if(!playerFile.exists())
			try {
				playerFile.createNewFile();
			} catch(IOException ex) {
				ex.printStackTrace();
				return;
			}
	}
}
