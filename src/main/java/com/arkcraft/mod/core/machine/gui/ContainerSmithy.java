package com.arkcraft.mod.core.machine.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/***
 * 
 * @author Vastatio
 *
 */
public class ContainerSmithy extends Container {

	public InventoryCrafting craftMatrix;
	public IInventory craftResult;
	private World world;
	private BlockPos pos;

	public ContainerSmithy(InventoryPlayer invPlayer, World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
		craftMatrix = new InventoryCrafting(this, 5, 5);
		craftResult = new InventoryCraftResult();

		/* Output slot */
		this.addSlotToContainer(new SlotCrafting(invPlayer.player, craftMatrix,
				craftResult, 0, 131, 36));

		/* Crafting Matrix */
		for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 5; col++) {
				this.addSlotToContainer(new Slot(craftMatrix, row + col * 5,
						4 + row * 18, 3 + col * 18));
			}
		}

		/* Player inventory */
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlotToContainer(new Slot(invPlayer, row + col * 9 + 9,
						8 + row * 18, 94 + col * 18));
			}
		}

		/* Player hotbar */
		for (int col = 0; col < 9; col++) {
			this.addSlotToContainer(new Slot(invPlayer, col, 8 + col * 18, 148));
		}

		this.onCraftMatrixChanged(craftMatrix);
	}

    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!this.world.isRemote)
        {
            for (int i = 0; i < 9; ++i)
            {
                ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);

                if (itemstack != null)
                {
                    playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }
	
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index == 0) {
				if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (index >= 10 && index < 37) {
				if (!this.mergeItemStack(itemstack1, 37, 46, false)) {
					return null;
				}
			} else if (index >= 37 && index < 46) {
				if (!this.mergeItemStack(itemstack1, 10, 37, false)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(playerIn, itemstack1);
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return false;
	}

}
