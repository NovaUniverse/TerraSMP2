package net.novauniverse.terrasmp.utils;

import org.bukkit.Location;

public class XZLocation {
	private double x;
	private double z;

	public XZLocation(double x, double z) {
		this.x = x;
		this.z = z;
	}

	public XZLocation(Location location) {
		this.x = location.getX();
		this.z = location.getZ();
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof XZLocation) {
			XZLocation l2 = (XZLocation) obj;

			return this.getX() == l2.getX() && this.getZ() == l2.getZ();
		}

		return false;
	}
}