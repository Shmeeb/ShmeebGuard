package net.shmeeb.shmeebguard.objects;

import net.shmeeb.shmeebguard.ShmeebGuard;
import ninja.leaping.configurate.ConfigurationNode;
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

    @Override
    public String toString() {
        return "name: " + name + ", world: " + worldName
                + ", pos1: (" + box.getMin().getFloorX() + "," + box.getMin().getFloorY() + "," + box.getMin().getFloorZ() + ")"
                + ", pos2: (" + box.getMax().getFloorX() + "," + box.getMax().getFloorY() + "," + box.getMax().getFloorZ() + ")";
    }
}