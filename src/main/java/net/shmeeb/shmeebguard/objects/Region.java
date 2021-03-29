package net.shmeeb.shmeebguard.objects;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.AABB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Region {
    private final String name;
    private final AABB box;
    private final String worldName;
    private List<FlagTypes> flagTypes;
    private HashMap<FlagTypes, String> customFlagValues;

    public Region(String name, AABB box, String worldName, List<FlagTypes> flagTypes, HashMap<FlagTypes, String> customFlagValues) {
        this.name = name;
        this.box = box;
        this.worldName = worldName;
        this.flagTypes = flagTypes == null ? new ArrayList<>() : flagTypes;
        this.customFlagValues = customFlagValues == null ? new HashMap<>() : customFlagValues;
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

    public HashMap<FlagTypes, String> getCustomFlagValues() {
        return customFlagValues;
    }

    public void saveFlags() {
        List<String> stringFlagTypes = flagTypes.stream().map(Enum::name).collect(Collectors.toList());

        ConfigurationNode data_root = ShmeebGuard.getData();
        data_root.getNode("regions", name, "flagTypes").setValue(stringFlagTypes);

        if (customFlagValues.size() > 0) {
            customFlagValues.forEach((flag, value) -> {
                data_root.getNode("regions", name, "customFlagValues", flag.name()).setValue(value);
            });
        }

        ShmeebGuard.setData(data_root);
    }

    public Text flagsToText() {
        Text text = Text.of(TextColors.YELLOW, "Current flags for ", name, Text.NEW_LINE);

        for (int i = 0; i < FlagTypes.values().length; i++) {
            FlagTypes type = FlagTypes.values()[i];
            Text current_line;

            if (type.equals(FlagTypes.ENTER_COMMANDS) || type.equals(FlagTypes.EXIT_COMMANDS)) {

                if (getCustomFlagValues().containsKey(type)) {
                    Text hover = Text.of(TextColors.GREEN, type.name() + " settings for ", name, Text.NEW_LINE);
                    List<String> customFlags = new ArrayList<>(Utils.stringToList(getCustomFlagValues().get(type)));


                    for (int j = 0; j < customFlags.size(); j++) {
                        Text new_line = j == customFlags.size() - 1 ? Text.EMPTY : Text.NEW_LINE;

                        hover = hover.concat(Text.of(TextColors.GRAY, (j + 1), ") ", customFlags.get(j), new_line));
                    }

                    current_line = Text.builder()
                            .append(Text.of(TextColors.GOLD, "[x" + Utils.stringToList(getCustomFlagValues().get(type)).size() + "]"))
                            .onHover(TextActions.showText(hover))
                            .onClick(TextActions.suggestCommand("sg edit " + name + " " + type + " "))
                            .build();

//                    current_line = Text.of(TextColors.GOLD, "[x" + Utils.stringToList(getCustomFlagValues().get(type)).size() + "]");
                } else {
                    current_line = Text.of(TextColors.RED, "NONE");
                }

            } else {
                if (flagTypes.contains(type)) {

                    if (type.equals(FlagTypes.TELEPORT_IN))
                        current_line = Utils.getText("&aALLOW&7 for &b" + getCustomFlagValues().get(FlagTypes.TELEPORT_IN));
                    else
                        current_line = Text.of(TextColors.RED, "DENY");

                } else {
                    current_line = Text.of(TextColors.GREEN, "ALLOW");
                }
            }

            Text new_line = i == FlagTypes.values().length - 1 ? Text.EMPTY : Text.NEW_LINE;
            text = text.concat(Text.of(TextColors.GRAY, type.name(), ": ", current_line, new_line));
        }

        return text;
    }

    public Text toText() {
        Text flags = Utils.getText(" &d[flags]").toBuilder()
                .onHover(TextActions.showText(flagsToText()))
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