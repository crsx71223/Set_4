public abstract class Vehicle {
    private final int id;
    private String name;
    private static int nextId = 1;

    public Vehicle(String name) {
        this.id = nextId++;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Helper method to establish mandatory sort order: Car(1) < Motorboat(2) < Bicycle(3) < Scooter(4)
    public abstract int getTypePriority();

    @Override
    public abstract String toString();
}