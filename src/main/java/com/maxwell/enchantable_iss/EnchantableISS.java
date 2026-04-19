package com.maxwell.enchantable_iss;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EnchantableISS.MODID)
public class EnchantableISS {
    public static final String MODID = "enchantable_iss";

    public EnchantableISS(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
    }
}
