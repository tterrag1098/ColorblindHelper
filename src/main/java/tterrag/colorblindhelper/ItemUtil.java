package tterrag.colorblindhelper;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.oredict.OreDictionary;

public class ItemUtil
{
	public static Object parseStringIntoRecipeItem(String string, boolean forceItemStack) {
		if ("null".equals(string)) {
			return null;
		} else if (OreDictionary.getOres(string).isEmpty()) {
			ItemStack stack = null;
			
			String[] info = string.split(";");
			Object temp = null;
			int damage = OreDictionary.WILDCARD_VALUE;
			temp = Item.REGISTRY.getObject(new ResourceLocation(info[0]));
			if (info.length > 1) {
				damage = Integer.parseInt(info[1]);
			}
			
			if (temp instanceof Item) {
				stack = new ItemStack((Item) temp, 1, damage);
			} else if (temp instanceof Block) {
				stack = new ItemStack((Block) temp, 1, damage);
			} else if (temp instanceof ItemStack) {
				stack = ((ItemStack) temp).copy();
				stack.setItemDamage(damage);
			} else {
				throw new IllegalArgumentException(string
						+ " is not a vaild string. Strings should be either an oredict name, or in the format objectname;damage (damage is optional)");
			}
			
			return stack;
		} else if (forceItemStack) {
			return OreDictionary.getOres(string).get(0).copy();
		} else {
			return string;
		}
	}
	
	public static ItemStack parseStringIntoItemStack(String string) {
		int size = 1;
		int numIdx = string.indexOf('#');
		int nbtIdx = string.indexOf('$');
		
		NBTTagCompound tag = null;
		
		if (numIdx != -1) {
			String num = string.substring(numIdx + 1, nbtIdx == -1 ? string.length() : nbtIdx);
			
			try {
				size = Integer.parseInt(num);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(num + " is not a valid stack size", e);
			}
			
			string = string.replace('#' + num, "");
			nbtIdx -= num.length() + 1;
		}
		
		if (nbtIdx != -1) {
			String nbt = string.substring(nbtIdx + 1);
			try {
				tag = JsonToNBT.getTagFromJson(nbt);
			} catch (NBTException e) {
				throw new IllegalArgumentException(nbt + " is not valid NBT json.", e);
			}
			
			string = string.replace('$' + nbt, "");
		}
		
		
		
		ItemStack stack = (ItemStack) parseStringIntoRecipeItem(string, true);
		stack.setCount(MathHelper.clamp(size, 1, stack.getMaxStackSize()));
		stack.setTagCompound(tag);
		return stack;
	}
}
