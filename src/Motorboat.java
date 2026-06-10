public class Motorboat extends Vehicle implements CombustionVehicle {
    private int supportedFuelMask;
    private double fuelAmount;

    public Motorboat(String name, int supportedFuelMask) {
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
    public int getTypePriority() {
        return 2;
    }

    @Override
    public String toString() {
        return String.format("[%d] Motorboat: %s | Fuel Mask: %d | Fuel Amt: %.2f",
                getId(), getName(), supportedFuelMask, fuelAmount);
    }
}