package net.shmeeb.shmeebguard;

import com.google.inject.Inject;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.shmeeb.shmeebguard.commands.Base;
import net.shmeeb.shmeebguard.listeners.*;
import net.shmeeb.shmeebguard.managers.RegionManager;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.event.*;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(id = "shmeebguard",
        name = "ShmeebGuard",
        version = "1.0"
)

public class ShmeebGuard {
    public static String PERM = "shmeebguard.admin";
    private static ShmeebGuard instance;
    private static RegionManager regionManager;
    @Inject private EventManager eventManager;
    private Logger logger = LoggerFactory.getLogger("ShmeebGuard");

    @Inject @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> config_loader;

    @Inject @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> data_loader;

    @Inject @DefaultConfig(sharedRoot = false)
    public Path config_path;

    @Inject @ConfigDir(sharedRoot = false)
    public Path directory_path;

    private static ConfigurationNode config_root;
    private static ConfigurationNode data_root;

    @Listener
    public void init(GameInitializationEvent e) throws IOException {
        File data_file = new File(directory_path.toFile(), "data.conf");

        if (!Files.exists(config_path))
            Sponge.getAssetManager().getAsset(this, "default.conf").get().copyToFile(config_path);

        if (!data_file.exists()) data_file.createNewFile();

        config_loader = HoconConfigurationLoader.builder().setPath(config_path).build();
        data_loader = HoconConfigurationLoader.builder().setFile(data_file).build();
        instance = this;

        loadConfig();
        regionManager = new RegionManager();
        registerEventListeners();
        registerCommands();

        Task.builder().delay(15, TimeUnit.MINUTES).interval(15, TimeUnit.MINUTES).execute(task -> {
            int killed = 0;

            for (World world : Sponge.getServer().getWorlds()) {
                for (Entity entity : world.getEntities()) {

                    if (entity instanceof Animal || entity instanceof Monster) {

                        for (Region region : ShmeebGuard.getRegionManager().getAllRegionsAtPosition(entity.getLocation())) {
                            if (region.getFlagTypes().contains(FlagTypes.AUTO_BUTCHER)) {
                                entity.remove();
                                killed++;
                            }
                        }
                    }
                }
            }

            Utils.verbose("&7&oShmeebGuard AutoButcher cleared " + killed + " entities");

        }).submit(instance);
    }

    public void loadConfig() {
        try {
            config_root = config_loader.load();
            data_root = data_loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerCommands() {
        Sponge.getCommandManager().register(this, Base.build(), "shmeebguard", "sg");
    }


    private void registerEventListeners() {
        registerListener(ChangeBlockEvent.class, Order.LATE, new BlockChangeListener());
        registerListener(SpawnEntityEvent.class, Order.LATE, new SpawnEntityListener());
        registerListener(MoveEntityEvent.Teleport.class, Order.LATE, new TeleportListener());
        registerListener(InteractEntityEvent.class, Order.LATE, new InteractEntityListener());
        registerListener(DropItemEvent.Dispense.Pre.class, Order.LATE, new DropListener());
        registerListener(DamageEntityEvent.class, Order.EARLY, new DamageListener());
        Pixelmon.EVENT_BUS.register(new SpawnEntityListener());
    }

    public <T extends Event> void registerListener(Class<T> eventClass, Order order, EventListener<? super T> listener) {
        try {
            eventManager.registerListener(this, eventClass, order, listener);
        } catch (Exception e) {
            logger.error("Failed to register listener: " + listener.toString());
            logger.error("With the event class: " + eventClass.getName());
            logger.error("This Listener will not respond to events", e);
        }
    }

    public static ShmeebGuard getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public static RegionManager getRegionManager() {
        return regionManager;
    }

    public static ConfigurationNode getData() {
        try {
            return getInstance().data_loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ConfigurationNode getConfig() {
        try {
            return getInstance().config_loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void setData(ConfigurationNode node) {
        try {
            getInstance().data_loader.save(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setConfig(ConfigurationNode node) {
        try {
            getInstance().config_loader.save(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}