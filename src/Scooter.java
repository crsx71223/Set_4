public class Scooter extends Vehicle {
    public Scooter(String name) {
        super(name);
    }

    @Override
    public int getTypePriority() {
        return 4;
    }

    @Override
    public String toString() {
        return String.format("[%d] Scooter: %s", getId(), getName());
    }
}