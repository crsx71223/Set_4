public interface Parkable {
    boolean park(Garage garage);
    boolean unpark();
    boolean isParked();
    Garage getGarage();
}