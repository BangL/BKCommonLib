package com.bergerkiller.bukkit.common;

import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;

/*
 * Note: do not use getContents()[index] = value on this!
 */
public class MergedInventory implements IInventory {
	
	private final IInventory[] inv;
	
	public MergedInventory(Inventory... inventories) {
		this.inv = new IInventory[inventories.length];
		for (int i = 0; i < this.inv.length; i++) {
			this.inv[i] = ((CraftInventory) inventories[i]).getInventory();
		}
	}
	public MergedInventory(IInventory... inventories) {
		this.inv = inventories;
	}
	
	@Override
	public boolean a(EntityHuman human) {
		//called when a human clicks on this inventory
		//if true is returned, something happened
		boolean flag = false;
		for (IInventory i : this.inv) if (i.a(human)) flag = true; 
		return flag;
	}
	
	public Inventory getInventory() {
		return new CraftInventory(this);
	}

	@Override
	public void f() {
		for (IInventory i : this.inv) i.f(); 
	}

	@Override
	public void g() {
		for (IInventory i : this.inv) i.g(); 
	}
	
	@Override
	public void update() {
		for (IInventory i : this.inv) i.update(); 
	}

	@Override
	public ItemStack[] getContents() {
		if (this.inv == null || this.inv.length == 0) {
			return new ItemStack[0];
		} else if (this.inv.length == 1) {
			return this.inv[0].getContents();
		} else {
			ItemStack[] rval = new ItemStack[this.getSize()];
			int i = 0;
			for (IInventory inv : this.inv) {
				for (ItemStack stack : inv.getContents()) {
					rval[i] = stack;
				}
			}
			return rval;
		}
	}

	@Override
	public ItemStack getItem(int index) {
		int size;
		for (IInventory i : this.inv) {
			size = i.getSize();
			if (index < size) {
				return i.getItem(index);
			} else {
				index -= size;
			}
		}
		return null;
	}

	@Override
	public int getMaxStackSize() {
		return this.inv[0].getMaxStackSize();
	}

	@Override
	public String getName() {
		return "MergedInventory";
	}

	@Override
	public int getSize() {
		int size = 0;
		for (IInventory i : this.inv) size += i.getSize(); 
		return size;
	}

	@Override
	public void setItem(int index, ItemStack arg1) {
		int size;
		for (IInventory i : this.inv) {
			size = i.getSize();
			if (index < size) {
				i.setItem(index, arg1);
				return;
			} else {
				index -= size;
			}
		}
	}

	@Override
	public ItemStack splitStack(int index, int count) {
		int size;
		for (IInventory i : this.inv) {
			size = i.getSize();
			if (index < size) {
				return i.splitStack(index, count);
			} else {
				index -= size;
			}
		}
		return null;
	}

}