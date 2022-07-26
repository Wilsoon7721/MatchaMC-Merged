package com.matchamc.automod.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.matchamc.automod.shared.Module;
import com.matchamc.automod.shared.modules.BlacklistModule;
import com.matchamc.automod.shared.modules.CapsModule;
import com.matchamc.automod.shared.modules.CooldownModule;
import com.matchamc.automod.shared.modules.VerifierModule;
import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.Configurations;
import com.matchamc.core.bukkit.util.PlayerRegistrar;
import com.matchamc.core.bukkit.util.Staffs;
import com.matchamc.shared.MsgUtils;

public class AutoMod {
	private BukkitMain instance;
	private File config;
	private YamlConfiguration yc;
	private Staffs staffs;
	private PlayerRegistrar registrar;
	private Set<Module> activeModules = new HashSet<>();

	// TODO Violations - Max Warns and actions
	// TODO VerifierModule doesn't have the commands check
	public AutoMod(BukkitMain instance, Configurations configurations, Staffs staffs, PlayerRegistrar registrar) {
		// TODO Spigot Sequence
		this.instance = instance;
		this.registrar = registrar;
		this.staffs = staffs;
		configurations = Objects.requireNonNull(configurations);
		config = new File(this.instance.getDataFolder(), "automod.yml");
		if(!config.exists())
			configurations.create("automod.yml");
		Bukkit.getPluginManager().registerEvents(new ModuleListeners(this), this.instance);
		Bukkit.getScheduler().runTask(this.instance, () -> initializeModules());
	}

	public void initializeModules() {
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(config);
		for(String key : yc.getConfigurationSection("modules").getKeys(false).stream().map(s -> s.toLowerCase()).collect(Collectors.toSet())) {
			boolean enabled = yc.getBoolean("modules." + key + ".enabled");
			switch(key.toLowerCase()) {
			case "caps":
				if(!enabled)
					continue;
				CapsModule capsModule = new CapsModule();
				boolean replace = yc.getBoolean("modules." + key + "replace");
				int maxCaps = yc.getInt("modules." + key + ".max-caps");
				capsModule.loadModule(enabled, replace, maxCaps, maxWarns);
				activeModules.add(capsModule);
				break;
			case "verifier":
				if(!enabled)
					continue;
				VerifierModule verifierModule = new VerifierModule();
				boolean names = yc.getBoolean("modules." + key + ".names");
				Collection<String> verifierExpressions = yc.getStringList("modules." + key + ".expressions");
				if(verifierExpressions == null || verifierExpressions.isEmpty()) {
					enabled = false;
					MsgUtils.sendBukkitConsoleMessage("&c[AutoMod] VerifierModule: Expressions cannot be empty - Module disabled.");
					continue;
				}
				Collection<String> commands = yc.getStringList("modules." + key + ".commands");
				if(commands == null || commands.isEmpty())
					commands = null; // TODO VerifierModule: If commands is null, check all commands
				verifierModule.loadModule(enabled, names, commands, verifierExpressions, registrar.getAllRegisteredPlayerNames()); // WARNING: Unsafe method calling -- could possibly hang main thread.
				activeModules.add(verifierModule);
				break;
			case "blacklist":
				if(!enabled)
					continue;
				BlacklistModule blacklistModule = new BlacklistModule();
				boolean filter = yc.getBoolean("modules." + key + ".filter");
				Collection<String> blacklistExpressions = yc.getStringList("modules." + key + ".expressions");
				if(blacklistExpressions == null || blacklistExpressions.isEmpty()) {
					enabled = false;
					MsgUtils.sendBukkitConsoleMessage("&c[AutoMod] BlacklistModule: Expressions cannot be empty - Module disabled.");
					continue;
				}
				blacklistModule.loadModule(enabled, filter, maxWarns, blacklistExpressions.toArray(String[]::new));
				activeModules.add(blacklistModule);
				break;
			case "cooldown":
				if(!enabled)
					continue;
				CooldownModule cooldownModule = new CooldownModule(this);
				int delaySeconds = yc.getInt("modules." + key + ".delay");
				cooldownModule.loadModule(delaySeconds);
				activeModules.add(cooldownModule);
				break;
			default:
				MsgUtils.sendBukkitConsoleMessage("&c[AutoMod] Found an unknown key: &e" + key + "&c. Deleting...");
				yc.set("modules." + key, null);
				try {
					yc.save(config);
				} catch(IOException ex) {
					MsgUtils.sendBukkitConsoleMessage("&cFailed to delete the unknown key.");
					ex.printStackTrace();
					return;
				}
				reloadConfig();
				return;
			}
		}
	}

	public String getMessage(String key, String[][] placeholders) {
		// Key starts with messages.(key)
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(config);
		String msg = yc.getString("messages." + key);
		if(placeholders == null)
			return MsgUtils.color(msg);
		for(String[] placeholder : placeholders) {
			msg = msg.replace("%" + placeholder[0] + "%", placeholder[1]);
		}
		return MsgUtils.color(msg);
	}

	public YamlConfiguration getConfig() {
		yc = YamlConfiguration.loadConfiguration(config);
		return yc;
	}

	public void reloadConfig() {
		yc = YamlConfiguration.loadConfiguration(config);
		try {
			yc.save(config);
		} catch(IOException ex) {
			MsgUtils.sendBukkitConsoleMessage("&cAutoMod could not be reloaded.");
			ex.printStackTrace();
			return;
		}
		yc = YamlConfiguration.loadConfiguration(config);
	}

	public Set<Module> getActiveModules() {
		return activeModules;
	}

	public BukkitMain getBukkitInstance() {
		return instance;
	}

	public Staffs getStaffs() {
		return staffs;
	}

	public PlayerRegistrar getPlayerRegistrar() {
		return registrar;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getModuleAs(Module module, Class<T> clazz) throws ClassCastException {
		return (T) module;
	}
}
