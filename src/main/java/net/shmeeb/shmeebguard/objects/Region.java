package net.shmeeb.shmeebguard.objects;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Region {
    private final String name;
    private final AABB box;
    private final String worldName;
    private List<FlagTypes> flagTypes;

    public Region(String name, AABB box, String worldName, List<FlagTypes> flagTypes) {
        this.name = name;
        this.box = box;
        this.worldName = worldName;
        this.flagTypes = flagTypes == null ? new ArrayList<>() : flagTypes;
    }
    public String getName() {
        return name;
    }

    public AABB getBox() {
        return box;
    }

    public String getWorldName() {
        return worldName;
    }

    public List<FlagTypes> getFlagTypes() {
        return flagTypes;
    }

    public void updateFlagTypes(List<FlagTypes> flagTypes) {
        this.flagTypes = flagTypes;
        List<String> stringFlagTypes = flagTypes.stream().map(Enum::name).collect(Collectors.toList());

        ConfigurationNode data_root = ShmeebGuard.getData();
        data_root.getNode("regions", name, "flagTypes").setValue(stringFlagTypes);
        ShmeebGuard.setData(data_root);
    }

    public Text toText() {
        Text hover = Text.of(TextColors.YELLOW, "Current flags for ", name, Text.NEW_LINE);

        for (int i = 0; i < FlagTypes.values().length; i++) {
            Text current_line = flagTypes.contains(FlagTypes.values()[i]) ? Text.of(TextColors.RED, "DENY") : Text.of(TextColors.GREEN, "ALLOW");
            Text new_line = i == FlagTypes.values().length - 1 ? Text.EMPTY : Text.NEW_LINE;

            hover = hover.concat(Text.of(TextColors.GRAY, FlagTypes.values()[i].name(), ": ", current_line, new_line));
        }

        Text flags = Utils.getText(" &d[flags]").toBuilder()
                .onHover(TextActions.showText(hover))
                .onClick(TextActions.suggestCommand("/sg edit " + name + " "))
                .build();

        Text pos1 = Utils.getText(" &d[pos1]").toBuilder()
                .onHover(TextActions.showText(Utils.getText("&aClick to teleport to:")
                        .concat(Text.NEW_LINE).concat(Utils.getText("&7x: " + box.getMin().getFloorX() + ", y: " + box.getMin().getFloorY() + ", z: " + box.getMin().getFloorZ()))))
                .onClick(TextActions.runCommand("/tppos " + box.getMin().getFloorX() + "," + box.getMin().getFloorY() + "," + box.getMin().getFloorZ()))
                .build();

        Text pos2 = Utils.getText(" &d[pos2]").toBuilder()
                .onHover(TextActions.showText(Utils.getText("&aClick to teleport to:")
                        .concat(Text.NEW_LINE).concat(Utils.getText("&7x: " + box.getMax().getFloorX() + ", y: " + box.getMax().getFloorY() + ", z: " + box.getMax().getFloorZ()))))
                .onClick(TextActions.runCommand("/tppos " + box.getMax().getFloorX() + "," + box.getMax().getFloorY() + "," + box.getMax().getFloorZ()))
                .build();

        Text del = Utils.getText(" &c[del]").toBuilder()
                .onHover(TextActions.showText(Utils.getText("&cClick to delete")))
                .onClick(TextActions.runCommand("/sg delete " + name))
                .build();

        return Utils.getText("&eName: &a" + name + " &eWorld: &a" + worldName).concat(flags).concat(pos1).concat(pos2).concat(del);
    }
}