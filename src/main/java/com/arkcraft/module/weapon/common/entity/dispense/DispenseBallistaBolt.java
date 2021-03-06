package com.arkcraft.module.weapon.common.entity.dispense;

import java.util.Random;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;

import com.arkcraft.module.weapon.WeaponModule;
import com.arkcraft.module.weapon.common.entity.EntityBallistaBolt;

public class DispenseBallistaBolt extends BehaviorDefaultDispenseItem
{
	private Random rand;
	private boolean normalDispense;

	public DispenseBallistaBolt()
	{
		super();
		rand = new Random();
		normalDispense = false;
	}

	@Override
	public ItemStack dispenseStack(IBlockSource blocksource, ItemStack itemstack)
	{

		BlockPos blockpos = blocksource.getBlockPos();
		boolean canfire = false;
		normalDispense = false;

		// / double x = MathHelper.floor_double(blockpos.getX());
		// double y = MathHelper.floor_double(blockpos.getY());
		// double z = MathHelper.floor_double(blockpos.getZ());

		TileEntity tileentity = blocksource.getWorld().getTileEntity(blockpos);
		if (tileentity instanceof TileEntityDispenser)
		{
			TileEntityDispenser dispenser = ((TileEntityDispenser) tileentity);
			Item itemtocheck = null;
			if (itemstack.getItem() == Items.gunpowder)
			{
				itemtocheck = WeaponModule.items.ballista_bolt;
			}
			else if (itemstack.getItem() == WeaponModule.items.ballista_bolt)
			{
				itemtocheck = Items.gunpowder;
			}

			for (int i = 0; i < dispenser.getSizeInventory(); i++)
			{
				ItemStack itemstack1 = dispenser.getStackInSlot(i);
				if (itemstack1 != null && itemstack1.getItem() == itemtocheck)
				{
					dispenser.decrStackSize(i, 1);
					canfire = true;
					break;
				}
			}
		}

		if (!canfire)
		{
			normalDispense = true;
			return super.dispenseStack(blocksource, itemstack);
		}

		EnumFacing face = BlockDispenser.getFacing(blocksource.getBlockMetadata());
		double xvel = face.getFrontOffsetX() * 1.5D;
		double yvel = face.getFrontOffsetY() * 1.5D;
		double zvel = face.getFrontOffsetZ() * 1.5D;
		IPosition pos = BlockDispenser.getDispensePosition(blocksource);

		EntityBallistaBolt entityballistabolt = new EntityBallistaBolt(blocksource.getWorld(),
				pos.getX() + xvel, pos.getY() + yvel, pos.getZ() + zvel);
		entityballistabolt.setThrowableHeading(xvel, yvel + 0.15D, zvel, 2.0F, 2.0F);
		blocksource.getWorld().spawnEntityInWorld(entityballistabolt);
		itemstack.splitStack(1);
		return itemstack;
	}

	@Override
	protected void playDispenseSound(IBlockSource blocksource)
	{
		if (normalDispense)
		{
			super.playDispenseSound(blocksource);
			return;
		}
		blocksource.getWorld()
				.playSoundEffect(blocksource.getX(), blocksource.getY(), blocksource.getZ(),
						"random.explode", 8.0F, 1.0F / (rand.nextFloat() * 0.8F + 0.9F));
		blocksource.getWorld().playSoundEffect(blocksource.getX(), blocksource.getY(),
				blocksource.getZ(), "ambient.weather.thunder", 8.0F,
				1.0F / (rand.nextFloat() * 0.4F + 0.6F));
	}

	@Override
	protected void spawnDispenseParticles(IBlockSource blocksource, EnumFacing face)
	{
		super.spawnDispenseParticles(blocksource, face);
		if (!normalDispense)
		{
			IPosition pos = BlockDispenser.getDispensePosition(blocksource);
			blocksource.getWorld().spawnParticle(EnumParticleTypes.FLAME,
					pos.getX() + face.getFrontOffsetX(), pos.getY(),
					pos.getZ() + face.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D);
		}
	}
}