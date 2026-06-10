public interface CombustionVehicle {
    int DIESEL = 1 << 0; // 1
    int PETROL = 1 << 1; // 2
    int LPG    = 1 << 2; // 4
    int CNG    = 1 << 3; // 8

    boolean refuel(int fuelMask, double liters);
    int getSupportedFuelMask();
    double getFuelAmount();
}