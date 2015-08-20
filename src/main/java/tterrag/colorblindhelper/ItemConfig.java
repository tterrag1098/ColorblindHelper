package tterrag.colorblindhelper;

import java.awt.Color;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class ItemConfig
{
    private Color underlay = Color.WHITE;
    private String overlay = "";
    private Color overlayColor = Color.WHITE;
}
