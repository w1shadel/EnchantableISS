package com.maxwell.enchantable_iss.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SchoolResistanceEnchantment extends MagicEnchantment {
    private final ResourceLocation schoolResource;

    public SchoolResistanceEnchantment(Rarity rarity, int maxLevel, ResourceLocation schoolResource) {
        super(rarity, maxLevel, EnchantmentCategory.ARMOR,
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
        this.schoolResource = schoolResource;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    public ResourceLocation getSchoolResource() {
        return schoolResource;
    }
}