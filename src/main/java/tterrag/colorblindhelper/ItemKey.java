package tterrag.colorblindhelper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.ArrayUtils;

@Getter
@RequiredArgsConstructor
public class ItemKey
{
    private final Item item;
    private final int damage;
 
    private transient int[] oreIds;

    public static ItemKey forStack(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null)
        {
            return new ItemKey(null, 0);
        }
        return new ItemKey(stack.getItem(), stack.getItemDamage());
    }
    
    public ItemStack toStack()
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
        if (other.item != null && item != null)
        {
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
        }
        if (damage != other.damage && this.item == other.item)
            return this.damage == OreDictionary.WILDCARD_VALUE || other.damage == OreDictionary.WILDCARD_VALUE;
        if (item == null)
        {
            if (other.item != null)
                return false;
        }
        else if (!item.equals(other.item))
            return false;
        return true;
    }
}
