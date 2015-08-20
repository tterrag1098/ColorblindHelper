package tterrag.colorblindhelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.api.client.render.IWidgetMap;

@AllArgsConstructor
@Getter
public enum ShapeWidget implements IWidgetIcon
{
    SQUARE(0, 0),
    CIRCLE(16, 0),
    TRIANGLE(32, 0),
    STAR(48, 0),
    PLUS(0, 16),
    CROSS(16, 16),
    HORIZ_BAR(32, 16),
    VERT_BAR(48, 16),
    SQUARE_OUTLINE(0, 32),
    DIAMOND(16, 32);
    
    private static final int TEX_SIZE = 64;
    public static final ResourceLocation TEXTURE = new ResourceLocation("colorblindhelper:textures/shapes.png");

    public final int x;
    public final int y;
    public final int width = 16;
    public final int height = 16;
    public final IWidgetIcon overlay = null;

    public static final IWidgetMap map = new IWidgetMap.WidgetMapImpl(TEX_SIZE, TEXTURE);

    @Override
    public IWidgetMap getMap()
    {
        return map;
    }
}
