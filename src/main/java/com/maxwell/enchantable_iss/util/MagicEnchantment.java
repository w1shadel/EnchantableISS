package com.maxwell.enchantable_iss.util;

import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MagicEnchantment extends Enchantment {
    private final int maxLevel;

    public MagicEnchantment(Rarity rarity, int maxLevel, EnchantmentCategory category, EquipmentSlot... slots) {
        super(rarity, category, slots);
        this.maxLevel = maxLevel;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof StaffItem ||
                item instanceof SpellBook ||
                item instanceof ArmorItem;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return this.canEnchant(stack);
    }

    @Override
    public int getMinCost(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }
}