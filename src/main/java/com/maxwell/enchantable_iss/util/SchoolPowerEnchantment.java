package com.maxwell.enchantable_iss.util;

import io.redspace.ironsspellbooks.item.Scroll;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SchoolPowerEnchantment extends MagicEnchantment {
    private final ResourceLocation schoolResource;

    public SchoolPowerEnchantment(Rarity rarity, int maxLevel, ResourceLocation schoolResource) {
        super(rarity, maxLevel, EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND);
        this.schoolResource = schoolResource;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack) || stack.getItem() instanceof Scroll;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canEnchant(stack);
    }

    public ResourceLocation getSchoolResource() {
        return schoolResource;
    }
}