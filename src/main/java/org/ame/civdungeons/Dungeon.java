package org.ame.civdungeons;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public abstract class Dungeon {
    /**
     * Creates a new dungeon, loading it from disc.
     */
    public Dungeon(Location spawnLocation, String name, int maxX, int maxY, int maxZ, Main mainPlugin) {
        this.spawnLocation = spawnLocation;
        this.mainPlugin = mainPlugin;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        WorldCreator worldCreator = new WorldCreator("dungeon_" + name);
        worldCreator.generator(new VoidGenerator());
        dungeonWorld = mainPlugin.getServer().createWorld(worldCreator);

        if (spawnLocation.getWorld() == null) {
            spawnLocation.setWorld(dungeonWorld);
        }

        mainPlugin.getServer().getPluginManager().registerEvents(new DungeonWorldBorder(this), mainPlugin);
    }

    protected World dungeonWorld;
    private Main mainPlugin;

    private Location spawnLocation;
    private int maxX;
    private int maxY;
    private int maxZ;

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public void teleportPlayerToSpawn(Player player) {
        player.teleport(spawnLocation);
    }

    /**
     * Builds the dungeon from a schematic, deleting the old dungeon if needed.
     */
    @SuppressWarnings("deprecation")
    protected void buildDungeon(File schematic) throws IOException, DataException {
        SchematicFormat schematicFormat = SchematicFormat.getFormat(schematic);
        CuboidClipboard paste = schematicFormat.load(schematic);

        EditSession session = new EditSession(BukkitUtil.getLocalWorld(dungeonWorld), -1);
        try {
            paste.paste(session, new Vector(0, 0, 0), true);
        } catch (MaxChangedBlocksException e) {
            throw new AssertionError("MaxChangedBlocks is supposed to be infinity", e);
        }
    }
}
