package com.matchamc.core.bukkit.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.matchamc.shared.MsgUtils;

public class ItemBuilder {
	private Material material;
	private ItemStack item;
	private ItemMeta meta;

	public ItemBuilder(Material material) {
		this.material = material;
		item = new ItemStack(this.material);
		meta = item.getItemMeta();
	}

	public ItemBuilder withDisplayName(String name) {
		meta.setDisplayName(MsgUtils.color(name));
		item.setItemMeta(meta);
		meta = item.getItemMeta();
		return this;
	}

	public ItemBuilder withLore(List<String> list) {
		List<String> lore = new ArrayList<>();
		for(String s : list) {
			lore.add(MsgUtils.color(s));
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		meta = item.getItemMeta();
		return this;
	}

	public ItemBuilder withAmount(int amount) {
		item.setAmount(amount);
		return this;
	}

	public ItemBuilder withEnchant(Enchantment enchantment, int level) {
		meta.addEnchant(enchantment, level, true);
		item.setItemMeta(meta);
		meta = item.getItemMeta();
		return this;
	}

	public ItemBuilder withItemFlag(ItemFlag... flags) {
		meta.addItemFlags(flags);
		item.setItemMeta(meta);
		meta = item.getItemMeta();
		return this;
	}

	public ItemBuilder withAttribute(Attribute attr, AttributeModifier modifier) {
		meta.addAttributeModifier(attr, modifier);
		item.setItemMeta(meta);
		meta = item.getItemMeta();
		return this;
	}

	public ItemBuilder unbreakable(boolean b) {
		meta.setUnbreakable(b);
		item.setItemMeta(meta);
		meta = item.getItemMeta();
		return this;
	}

	public ItemStack toItemStack() {
		item.setItemMeta(meta);
		return item;
	}

}
