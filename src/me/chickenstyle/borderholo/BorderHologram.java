package me.chickenstyle.borderholo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BorderHologram extends JavaPlugin implements Listener {

    BukkitTask checkRunnable;
    private Location minLoc;
    private Location maxLoc;

    private HashMap<UUID,Hologram> holograms;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,this);
        this.getConfig().options().copyDefaults();
        saveDefaultConfig();

        this.holograms = new HashMap<>();



        if (getConfig().get("borderLocation.world") != null) {
            World world = getServer().getWorld(getConfig().getString("borderLocation.world"));
            int y = getConfig().getInt("borderLocation.y");
            int radius = getConfig().getInt("radius");
            minLoc = new Location(world,getConfig().getInt("borderLocation.minLocation.x"),y,getConfig().getInt("borderLocation.minLocation.z"));
            maxLoc = new Location(world,getConfig().getInt("borderLocation.maxLocation.x"),y,getConfig().getInt("borderLocation.maxLocation.z"));

            checkRunnable = new BukkitRunnable() {

                @Override
                public void run() {
                    HashMap<UUID,Distance> distances = new HashMap<>();
                    Location loopLoc = minLoc.clone().add(0.5,0,0.5);


                    for (int x = minLoc.getBlockX(); x < maxLoc.getBlockX(); x++) {

                        for (Entity entity : world.getNearbyEntities(loopLoc,radius,radius,radius)) {
                            if (!(entity instanceof Player)) continue;
                            Player player = (Player) entity;
                            double distance = loopLoc.distance(player.getLocation());
                            if (distances.containsKey(player.getUniqueId())) {
                                if (distance < distances.get(player.getUniqueId()).getDistance()) {
                                    distances.put(player.getUniqueId(),new Distance(loopLoc.clone(),distance,true));
                                }
                            } else {
                                distances.put(player.getUniqueId(),new Distance(loopLoc.clone(),distance,true));
                            }

                        }
                        loopLoc.add(1,0,0);
                    }

                    for (int z = minLoc.getBlockZ(); z < maxLoc.getBlockZ(); z++) {

                        for (Entity entity : world.getNearbyEntities(loopLoc,radius,radius,radius)) {
                            if (!(entity instanceof Player)) continue;
                            Player player = (Player) entity;
                            double distance = loopLoc.distance(player.getLocation());
                            if (distances.containsKey(player.getUniqueId())) {
                                if (distance < distances.get(player.getUniqueId()).getDistance()) {
                                    distances.put(player.getUniqueId(),new Distance(loopLoc.clone(),distance,false));
                                }
                            } else {
                                distances.put(player.getUniqueId(),new Distance(loopLoc.clone(),distance,false));
                            }

                        }
                        loopLoc.add(0,0,1);
                    }

                    for (int x = maxLoc.getBlockX(); x > minLoc.getBlockX();x--) {
                        for (Entity entity : world.getNearbyEntities(loopLoc,radius,radius,radius)) {
                            if (!(entity instanceof Player)) continue;
                            Player player = (Player) entity;
                            double distance = loopLoc.distance(player.getLocation());
                            if (distances.containsKey(player.getUniqueId())) {
                                if (distance < distances.get(player.getUniqueId()).getDistance()) {
                                    distances.put(player.getUniqueId(),new Distance(loopLoc.clone(),distance,true));
                                }
                            } else {
                                distances.put(player.getUniqueId(),new Distance(loopLoc.clone(),distance,true));
                            }

                        }
                        loopLoc.subtract(1,0,0);
                    }

                    for (int z = maxLoc.getBlockZ(); z > minLoc.getBlockZ();z--) {
                        for (Entity entity : world.getNearbyEntities(loopLoc,radius,radius,radius)) {
                            if (!(entity instanceof Player)) continue;
                            Player player = (Player) entity;
                            double distance = loopLoc.distance(player.getLocation());
                            if (distances.containsKey(player.getUniqueId())) {
                                if (distance < distances.get(player.getUniqueId()).getDistance()) {
                                    distances.put(player.getUniqueId(),new Distance(loopLoc.clone(),distance,false));
                                }
                            } else {
                                distances.put(player.getUniqueId(),new Distance(loopLoc.clone(),distance,false));
                            }

                        }
                        loopLoc.subtract(0,0,1);
                    }

                    for (UUID uuid : distances.keySet()) {

                        Player player = Bukkit.getPlayer(uuid);
                        Location playerLoc = player.getLocation();
                        if (holograms.containsKey(uuid)) {
                            Location spawnLocation = distances.get(uuid).getLoc().clone();


                            if (distances.get(uuid).isGoingOnX()) {
                                spawnLocation.setX(spawnLocation.getBlockX() + (playerLoc.getX() - ((int) playerLoc.getX())));
                                if (spawnLocation.getBlockX() < 0) {
                                    spawnLocation.add(1,0,0);
                                }
                            } else {
                                spawnLocation.setZ(spawnLocation.getBlockZ() + (playerLoc.getZ() - ((int) playerLoc.getZ())));;

                                if (spawnLocation.getBlockZ() < 0) {
                                    spawnLocation.add(0,0,1);
                                }


                            }
                            holograms.get(uuid).moveTo(spawnLocation);

                        } else {
                            Hologram hologram = new Hologram(distances.get(uuid).getLoc().clone(),player,BorderHologram.this);
                            hologram.spawn();
                            holograms.put(uuid,hologram);
                        }

                        Location hologramLoc = holograms.get(uuid).getLoc();

                        if (hologramLoc.getBlockX() == playerLoc.getBlockX() && hologramLoc.getBlockZ() == playerLoc.getBlockZ()) {

                            for (String cmd : (List<String>)getConfig().get("commandsToRun"))  {
                                getServer().dispatchCommand(Bukkit.getConsoleSender(),cmd.replace("%player%",player.getName()));
                            }
                        }
                    }

                    for (UUID uuid : holograms.keySet()) {
                        if (!distances.containsKey(uuid)) {
                            holograms.get(uuid).despawn();
                            holograms.remove(uuid);
                        }
                    }

                }
            }.runTaskTimer(this,0,4);
        }




        System.out.println(Utils.color("&aBorderHologram loaded!"));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (holograms.containsKey(e.getPlayer().getUniqueId())) {
            holograms.remove(e.getPlayer().getUniqueId());
        }
    }

    @Override
    public void onDisable() {
        for (UUID uuid:holograms.keySet()) {
            holograms.get(uuid).despawn();
        }
    }

    public static void main(String[] args) {
        double number = 69.4201;

        System.out.println(number - ((int)number));
    }
}
