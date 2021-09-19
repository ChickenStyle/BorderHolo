package me.chickenstyle.borderholo;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Nameable;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    private BorderHologram main;
    private Location loc;
    private Player player;
    private List<Line> lines;

    public Hologram(Location loc,Player player, BorderHologram main) {
        this.main = main;
        this.player = player;
        this.loc = loc.clone();
        this.lines = new ArrayList<>();

        double space = main.getConfig().getDouble("spaceBetweenLines");
        double moveY = main.getConfig().getDouble("moveHologramY");
        this.loc.add(0,moveY,0);
        List<String> lines = (List<String>) main.getConfig().get("hologram");
        //Loads lines
        for (int i = 0; i < lines.size(); i++) {
            this.lines.add(new Line(this.loc.clone().subtract(0,space * i,0),lines.get(i)));
        }

    }

    public void spawn() {
        for (Line line:this.lines) {
            PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(line);
            PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(line.getId(), line.getDataWatcher(), true);
            PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(line);
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

            connection.sendPacket(spawnPacket);
            connection.sendPacket(metadataPacket);
            connection.sendPacket(teleportPacket);

        }
    }



    public void moveTo(Location loc) {
        double space = main.getConfig().getDouble("spaceBetweenLines");
        double moveY = main.getConfig().getDouble("moveHologramY");
        this.loc = loc.clone().add(0,moveY,0);


        for (int i = 0; i < this.lines.size(); i++) {
            lines.get(i).moveTo(this.loc.clone().subtract(0,space * i,0));
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityTeleport(lines.get(i)));
        }
    }

    public void despawn() {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;

        for (Line line : lines) {
            connection.sendPacket(new PacketPlayOutEntityDestroy(line.getId()));
        }
    }

    public BorderHologram getMain() {
        return main;
    }

    public void setMain(BorderHologram main) {
        this.main = main;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    private class Line extends EntityArmorStand {

        public Line(Location loc,String line) {
            super(EntityTypes.ARMOR_STAND, ((CraftWorld)loc.getWorld()).getHandle());
            setCustomName(new ChatComponentText(Utils.color(line)));
            setCustomNameVisible(true);
            setNoGravity(true);
            setInvisible(true);
            setPosition(loc.getX(),loc.getY(),loc.getZ());
        }

        public void moveTo(Location loc) {
            setPosition(loc.getX(),loc.getY(),loc.getZ());
        }

    }
}
