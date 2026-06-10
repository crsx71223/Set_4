public class Car extends Vehicle implements CombustionVehicle, Parkable {
    private int supportedFuelMask;
    private double fuelAmount;
    private Garage garage;

    public Car(String name, int supportedFuelMask) {
        super(name);
        this.supportedFuelMask = supportedFuelMask;
        this.fuelAmount = 0.0;
    }

    @Override
    public boolean refuel(int fuelMask, double liters) {
        if (liters > 0 && (this.supportedFuelMask & fuelMask) != 0) {
            this.fuelAmount += liters;
            return true;
        }
        return false;
    }

    @Override
    public int getSupportedFuelMask() {
        return supportedFuelMask;
    }

    @Override
    public double getFuelAmount() {
        return fuelAmount;
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
        return 1;
    }

    @Override
    public String toString() {
        String parkStatus = isParked() ? String.valueOf(garage.getNumber()) : "-";
        return String.format("[%d] Car: %s | Fuel Mask: %d | Fuel Amt: %.2f | Parked: %s",
                getId(), getName(), supportedFuelMask, fuelAmount, parkStatus);
    }
}