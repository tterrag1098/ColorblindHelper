package tterrag.colorblindhelper;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ArrayUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemKey
{
    private final @Nonnull Item item;
    private final int damage;
 
    private transient int[] oreIds;

    public static ItemKey forStack(ItemStack stack)
    {
        if (stack.isEmpty() || stack.getItem() == null || stack.getItem() == Items.AIR)
        {
            throw new IllegalArgumentException("Cannot create item key for empty stack: " + stack);
        }
        return new ItemKey(stack.getItem(), stack.getItemDamage());
    }
    
    private @Nonnull ItemStack toStack()
    {
        return new ItemStack(item, 1, damage);
    }

    @Override
    public int hashCode()
    {
        return 1; // shh
    }

    // Adds checks for oredict+wildcard
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemKey other = (ItemKey) obj;
        if (oreIds == null)
        {
            oreIds = OreDictionary.getOreIDs(toStack());
        }
        if (other.oreIds == null)
        {
            other.oreIds = OreDictionary.getOreIDs(other.toStack());
        }
        for (int i : other.oreIds)
        {
            if (ArrayUtils.contains(oreIds, i))
            {
                return true;
            }
        }
        if (damage != other.damage && this.item == other.item)
            return this.damage == OreDictionary.WILDCARD_VALUE || other.damage == OreDictionary.WILDCARD_VALUE;
        else if (!item.equals(other.item))
            return false;
        return true;
    }
}
