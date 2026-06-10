import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Rental {
    private final Garage[] garages;
    private final List<Vehicle> vehicles;
    private static final String XML_FILE = "vehicles.xml";

    public Rental(int garageCount) {
        this.garages = new Garage[garageCount];
        for (int i = 0; i < garageCount; i++) {
            this.garages[i] = new Garage(i + 1);
        }
        this.vehicles = new ArrayList<>();
        loadFromXml();
    }

    public static void main(String[] args) {
        Rental rental = new Rental(5);
        rental.runMenu();
    }

    private void runMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Vehicle Rental System ---");
            System.out.println("1. Park a vehicle");
            System.out.println("2. Add a new vehicle");
            System.out.println("3. Remove a vehicle by ID");
            System.out.println("4. Print all vehicles (Sorted)");
            System.out.println("0. Save and Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    parkVehicle(scanner);
                    break;
                case "2":
                    addVehicle(scanner);
                    break;
                case "3":
                    removeVehicle(scanner);
                    break;
                case "4":
                    printVehicles();
                    break;
                case "0":
                    saveToXml();
                    running = false;
                    System.out.println("Exiting program.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }

    private void parkVehicle(Scanner scanner) {
        System.out.print("Enter vehicle ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        Vehicle v = findVehicleById(id);

        if (v == null) {
            System.out.println("Error: Vehicle not found.");
            return;
        }
        if (!(v instanceof Parkable)) {
            System.out.println("Error: Vehicle is not parkable (must be Car or Bicycle).");
            return;
        }

        System.out.print("Enter garage number (1-" + garages.length + "): ");
        int garageNum = Integer.parseInt(scanner.nextLine());

        if (garageNum < 1 || garageNum > garages.length) {
            System.out.println("Error: Invalid garage number.");
            return;
        }

        Garage g = garages[garageNum - 1];
        Parkable p = (Parkable) v;

        if (p.park(g)) {
            System.out.println("Success: Vehicle parked in garage " + garageNum);
        } else if (p.isParked()) {
            System.out.println("Error: Vehicle is already parked.");
        } else {
            System.out.println("Error: Garage is already occupied.");
        }
    }

    private void addVehicle(Scanner scanner) {
        System.out.println("Select type: 1. Car  2. Motorboat  3. Bicycle  4. Scooter");
        String type = scanner.nextLine();

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        Vehicle newVehicle = null;

        if (type.equals("1") || type.equals("2")) {
            System.out.print("Enter fuel type mask (1=DIESEL, 2=PETROL, 4=LPG, 8=CNG | e.g. 3 for PETROL+DIESEL): ");
            int mask = Integer.parseInt(scanner.nextLine());

            if (type.equals("1")) {
                newVehicle = new Car(name, mask);
            } else {
                newVehicle = new Motorboat(name, mask);
            }

            System.out.print("Enter initial fuel amount: ");
            double initialFuel = Double.parseDouble(scanner.nextLine());
            ((CombustionVehicle) newVehicle).refuel(mask, initialFuel); // Init with default supported fuel

        } else if (type.equals("3")) {
            newVehicle = new Bicycle(name);
        } else if (type.equals("4")) {
            newVehicle = new Scooter(name);
        } else {
            System.out.println("Error: Invalid type.");
            return;
        }

        vehicles.add(newVehicle);
        System.out.println("Success: Added " + newVehicle.getName() + " with ID " + newVehicle.getId());
    }

    private void removeVehicle(Scanner scanner) {
        System.out.print("Enter vehicle ID to remove: ");
        int id = Integer.parseInt(scanner.nextLine());
        Vehicle v = findVehicleById(id);

        if (v == null) {
            System.out.println("Error: Vehicle not found.");
            return;
        }

        if (v instanceof Parkable) {
            Parkable p = (Parkable) v;
            if (p.isParked()) {
                p.unpark();
                System.out.println("Vehicle unparked automatically prior to removal.");
            }
        }

        vehicles.remove(v);
        System.out.println("Success: Vehicle removed.");
    }

    private void printVehicles() {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles in the system.");
            return;
        }

        // Multi-criteria sort
        Comparator<Vehicle> sorter = (v1, v2) -> {
            boolean p1 = v1 instanceof Parkable && ((Parkable) v1).isParked();
            boolean p2 = v2 instanceof Parkable && ((Parkable) v2).isParked();
            if (p1 != p2) return p1 ? -1 : 1; // Parked first

            int typeDiff = Integer.compare(v1.getTypePriority(), v2.getTypePriority());
            if (typeDiff != 0) return typeDiff;

            int nameDiff = v1.getName().compareToIgnoreCase(v2.getName());
            if (nameDiff != 0) return nameDiff;

            int fType1 = v1 instanceof CombustionVehicle ? ((CombustionVehicle) v1).getSupportedFuelMask() : 0;
            int fType2 = v2 instanceof CombustionVehicle ? ((CombustionVehicle) v2).getSupportedFuelMask() : 0;
            if (fType1 != fType2) return Integer.compare(fType1, fType2);

            double fAmt1 = v1 instanceof CombustionVehicle ? ((CombustionVehicle) v1).getFuelAmount() : 0.0;
            double fAmt2 = v2 instanceof CombustionVehicle ? ((CombustionVehicle) v2).getFuelAmount() : 0.0;
            return Double.compare(fAmt1, fAmt2);
        };

        Collections.sort(vehicles, sorter);

        System.out.println("\n--- Vehicle List ---");
        for (Vehicle v : vehicles) {
            System.out.println(v.toString());
        }
    }

    private Vehicle findVehicleById(int id) {
        for (Vehicle v : vehicles) {
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
    }

    private void loadFromXml() {
        File file = new File(XML_FILE);
        if (!file.exists()) {
            System.out.println("XML file not found. Starting with an empty database.");
            return;
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String nodeName = element.getNodeName();
                    String name = element.getElementsByTagName("name").item(0).getTextContent();

                    if (nodeName.equals("car") || nodeName.equals("motorboat")) {
                        int fuelType = Integer.parseInt(element.getElementsByTagName("fuelType").item(0).getTextContent());
                        if (nodeName.equals("car")) {
                            vehicles.add(new Car(name, fuelType));
                        } else {
                            vehicles.add(new Motorboat(name, fuelType));
                        }
                    } else if (nodeName.equals("bicycle")) {
                        vehicles.add(new Bicycle(name));
                    } else if (nodeName.equals("scooter")) {
                        vehicles.add(new Scooter(name));
                    }
                }
            }
            System.out.println("Loaded " + vehicles.size() + " vehicles from " + XML_FILE);
        } catch (Exception e) {
            System.out.println("Error loading XML: " + e.getMessage());
        }
    }

    private void saveToXml() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("vehicles");
            doc.appendChild(rootElement);

            for (Vehicle v : vehicles) {
                Element vehicleElement;
                if (v instanceof Car) vehicleElement = doc.createElement("car");
                else if (v instanceof Motorboat) vehicleElement = doc.createElement("motorboat");
                else if (v instanceof Bicycle) vehicleElement = doc.createElement("bicycle");
                else vehicleElement = doc.createElement("scooter");

                Element nameElement = doc.createElement("name");
                nameElement.appendChild(doc.createTextNode(v.getName()));
                vehicleElement.appendChild(nameElement);

                if (v instanceof CombustionVehicle) {
                    Element fuelElement = doc.createElement("fuelType");
                    fuelElement.appendChild(doc.createTextNode(String.valueOf(((CombustionVehicle) v).getSupportedFuelMask())));
                    vehicleElement.appendChild(fuelElement);
                }

                rootElement.appendChild(vehicleElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(XML_FILE));
            transformer.transform(source, result);

        } catch (Exception e) {
            System.out.println("Error saving XML: " + e.getMessage());
        }
    }
}