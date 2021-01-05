package net.shmeeb.shmeebguard.managers;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class RegionManager {
    private HashMap<String, List<Region>> regions = new HashMap<>();

    public RegionManager() {
        loadRegions();
    }

    public void loadRegions() {
        ConfigurationNode regions_root = ShmeebGuard.getData().getNode("regions");
        int count = 0;

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : regions_root.getChildrenMap().entrySet()) {
            String world = entry.getValue().getNode("world").getString();
            String name = entry.getKey().toString();
            List<FlagTypes> types = null;

            AABB box = new AABB(
                    entry.getValue().getNode("x1").getInt(),
                    entry.getValue().getNode("y1").getInt(),
                    entry.getValue().getNode("z1").getInt(),
                    entry.getValue().getNode("x2").getInt(),
                    entry.getValue().getNode("y2").getInt(),
                    entry.getValue().getNode("z2").getInt()
            );

            try {
                types = entry.getValue().getNode("flagTypes").getList(TypeToken.of(String.class))
                        .stream().map(FlagTypes::valueOf).collect(Collectors.toList());
            } catch (ObjectMappingException ignored) {}

            if (types == null) {
                ShmeebGuard.getInstance().getLogger().error("Couldn't find any flag types for the region " + name);
            }

            List<Region> current = regions.getOrDefault(world, new ArrayList<>());
            current.add(new Region(name, box, world, types));

            regions.put(world, current);
            count++;
        }

        ShmeebGuard.getInstance().getLogger().info("Successfully loaded " + count + " regions!");
    }

    public Optional<Region> getRegion(String name) {
        for (Map.Entry<String, List<Region>> entry : regions.entrySet()) {
            for (Region region : entry.getValue()) {
                if (region.getName().equalsIgnoreCase(name)) return Optional.of(region);
            }
        }

        return Optional.empty();
    }

    public Optional<List<Region>> getAllRegionsAtPosition(Location<World> location) {
        if (!regions.containsKey(location.getExtent().getName())) return Optional.empty();
        List<Region> options = new ArrayList<>();
        Vector3d pos =  location.getPosition();

        for (Region region : regions.get(location.getExtent().getName())) {
            if (region.getBox().contains(pos)) options.add(region);
        }

        return options.isEmpty() ? Optional.empty() : Optional.of(options);
    }

    public Optional<Region> getRegionAtPosition(Location<World> location) {
        if (!regions.containsKey(location.getExtent().getName())) return Optional.empty();
        Vector3d pos =  location.getPosition();

        for (Region region : regions.get(location.getExtent().getName())) {
            if (region.getBox().contains(pos)) return Optional.of(region);
        }

        return Optional.empty();
    }

    public void deleteRegion(String name) {
        Optional<Region> region = getRegion(name);
        if (!region.isPresent()) return;

        ConfigurationNode data_root = ShmeebGuard.getData();
        data_root.getNode("regions", region.get().getName()).setValue(null);
        ShmeebGuard.setData(data_root);

        List<Region> current = regions.getOrDefault(region.get().getWorldName(), new ArrayList<>());
        current.removeIf(r -> r.getName().equalsIgnoreCase(region.get().getName()));

        regions.put(region.get().getWorldName(), current);
    }

    public void createRegion(Region region) {
        ConfigurationNode data_root = ShmeebGuard.getData();

        data_root.getNode("regions", region.getName(), "world").setValue(region.getWorldName());

        data_root.getNode("regions", region.getName(), "x1").setValue(region.getBox().getMin().getX());
        data_root.getNode("regions", region.getName(), "y1").setValue(region.getBox().getMin().getY());
        data_root.getNode("regions", region.getName(), "z1").setValue(region.getBox().getMin().getZ());

        data_root.getNode("regions", region.getName(), "x2").setValue(region.getBox().getMax().getX());
        data_root.getNode("regions", region.getName(), "y2").setValue(region.getBox().getMax().getY());
        data_root.getNode("regions", region.getName(), "z2").setValue(region.getBox().getMax().getZ());

        ShmeebGuard.setData(data_root);
        List<Region> current = regions.getOrDefault(region.getWorldName(), new ArrayList<>());
        current.add(region);

        regions.put(region.getWorldName(), current);
    }

    public HashMap<String, List<Region>> getRegions() {
        return regions;
    }
}