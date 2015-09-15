package com.arkcraft.mod.core.items;

import java.util.List;

import com.arkcraft.mod.core.GlobalAdditions;
import com.arkcraft.mod.core.lib.BALANCE;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.registry.GameRegistry;

@SuppressWarnings("all")
public class ARKTranqAmmo extends ItemSaddle {
	
	public static double tranqArrowDamage = BALANCE.WEAPONS.TRANQ_ARROW_DAMAGE;
	
	public ARKTranqAmmo(String name) {
		super();
		this.setUnlocalizedName(name);
		this.setCreativeTab(GlobalAdditions.tabARK);
		GameRegistry.registerItem(this, name);
	}	
}