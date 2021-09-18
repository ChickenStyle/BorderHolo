package me.chickenstyle.borderholo;

import org.bukkit.Location;

public class Distance {
    private Location loc;
    private double distance;
    private boolean goingOnX;

    public Distance(Location loc, double distance,boolean goingOnX) {
        this.loc = loc;
        this.distance = distance;
        this.goingOnX = goingOnX;
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

    public boolean isGoingOnX() {
        return goingOnX;
    }

    public void setGoingOnX(boolean goingOnX) {
        this.goingOnX = goingOnX;
    }
}
