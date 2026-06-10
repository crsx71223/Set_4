public class Bicycle extends Vehicle implements Parkable {
    private Garage garage;

    public Bicycle(String name) {
        super(name);
    }

    @Override
    public boolean park(Garage g) {
        if (g != null && g.isEmpty() && !this.isParked()) {
            this.garage = g;
            g.setParkedVehicle(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean unpark() {
        if (this.isParked()) {
            this.garage.setParkedVehicle(null);
            this.garage = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean isParked() {
        return this.garage != null;
    }

    @Override
    public Garage getGarage() {
        return this.garage;
    }

    @Override
    public int getTypePriority() {
        return 3;
    }

    @Override
    public String toString() {
        String parkStatus = isParked() ? String.valueOf(garage.getNumber()) : "-";
        return String.format("[%d] Bicycle: %s | Parked: %s", getId(), getName(), parkStatus);
    }
}