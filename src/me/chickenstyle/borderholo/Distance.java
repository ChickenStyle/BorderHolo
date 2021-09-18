package me.chickenstyle.borderholo;

import org.bukkit.Location;

public class Distance {
    private Location loc;
    private double distance;

    public Distance(Location loc, double distance) {
        this.loc = loc;
        this.distance = distance;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
