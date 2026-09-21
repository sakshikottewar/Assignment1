import java.io.*;
import java.util.*;

 
public class EmployeeManagementSystem {

    public static void main(String[] args) {
        EmployeeManager manager = new EmployeeManager("employees.txt");
        Scanner sc = new Scanner(System.in);

        while (true) {
            showMenu();
            System.out.print("Enter your choice (1-9): ");
            String choiceStr = sc.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please enter a number from 1 to 9.");
                continue;
            }

            switch (choice) {
                case 1: {
                    System.out.println("\n-- Add Employee --");
                    System.out.print("Name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Department: ");
                    String dept = sc.nextLine().trim();
                    double salary = readDouble(sc, "Salary: ");
                    Employee emp = manager.addEmployee(name, dept, salary);
                    System.out.println("\nEmployee added successfully with ID " + emp.getId() + ".");
                    break;
                }
                case 2: {
                    System.out.println("\n-- Update Employee --");
                    int id = readInt(sc, "Enter Employee ID to update: ");
                    Employee existing = manager.findById(id);
                    if (existing == null) {
                        System.out.println("Employee not found.");
                        break;
                    }
                    System.out.println("Leave field blank to keep current value.");
                    System.out.print("Name [" + existing.getName() + "]: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Department [" + existing.getDepartment() + "]: ");
                    String dept = sc.nextLine().trim();
                    System.out.print("Salary [" + existing.getSalary() + "]: ");
                    String salaryInput = sc.nextLine().trim();
                    Double salary = null;
                    if (!salaryInput.isEmpty()) {
                        try {
                            salary = Double.parseDouble(salaryInput);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid salary entered, keeping old value.");
                        }
                    }
                    Employee updated = manager.updateEmployee(
                            id,
                            name.isEmpty() ? null : name,
                            dept.isEmpty() ? null : dept,
                            salary
                    );
                    System.out.println("\nEmployee updated successfully:");
                    System.out.println(updated);
                    break;
                }
                case 3: {
                    System.out.println("\n-- Delete Employee --");
                    int id = readInt(sc, "Enter Employee ID to delete: ");
                    boolean deleted = manager.deleteEmployee(id);
                    System.out.println(deleted ? "Employee deleted successfully." : "Employee not found.");
                    break;
                }
                case 4: {
                    System.out.println("\n-- Search Employee --");
                    System.out.print("Enter Employee ID or Name: ");
                    String keyword = sc.nextLine().trim();
                    List<Employee> results = manager.searchEmployee(keyword);
                    printTable(results);
                    break;
                }
                case 5: {
                    System.out.println("\n-- All Employees --");
                    printTable(manager.listEmployees());
                    break;
                }
                case 6: {
                    System.out.println("\n-- Highest Salary Employee --");
                    Employee emp = manager.highestSalaryEmployee();
                    System.out.println(emp != null ? emp.toString() : "No employees found.");
                    break;
                }
                case 7: {
                    System.out.println("\n-- Average Salary --");
                    double avg = manager.averageSalary();
                    System.out.printf("Average Salary: %.2f%n", avg);
                    break;
                }
                case 8: {
                    System.out.println("\n-- Filter by Department --");
                    System.out.print("Enter Department name (e.g. Development): ");
                    String dept = sc.nextLine().trim();
                    List<Employee> results = manager.filterByDepartment(dept);
                    printTable(results);
                    break;
                }
                case 9: {
                    System.out.println("\nExiting Employee Management System. Goodbye!");
                    sc.close();
                    return;
                }
                default:
                    System.out.println("Invalid choice. Please select a number from 1 to 9.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n===== EMPLOYEE MANAGEMENT SYSTEM  =====");
        System.out.println("1. Add Employee");
        System.out.println("2. Update Employee");
        System.out.println("3. Delete Employee");
        System.out.println("4. Search Employee");
        System.out.println("5. List All Employees");
        System.out.println("6. Highest Salary Employee");
        System.out.println("7. Average Salary");
        System.out.println("8. Filter by Department");
        System.out.println("9. Exit");
        System.out.println("====================================================");
    }

    private static void printTable(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            System.out.println("No employees to display.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("-".repeat(75)).append(System.lineSeparator());
        for (Employee emp : employees) {
            sb.append(emp).append(System.lineSeparator());
        }
        sb.append("-".repeat(75));
        System.out.println(sb);
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}


final class Employee {
    private final int id;
    private String name;
    private String department;
    private double salary;

    public Employee(int id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }

    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setSalary(double salary) { this.salary = salary; }

    /** Serializes this record as a single CSV line for file storage. */
    public String toCsv() {
        return id + "," + name.replace(",", " ") + "," + department.replace(",", " ") + "," + salary;
    }

    public static Employee fromCsv(String line) {
        String[] parts = line.split(",", -1);
        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        String department = parts[2].trim();
        double salary = Double.parseDouble(parts[3].trim());
        return new Employee(id, name, department, salary);
    }

    @Override
    public String toString() {
        return String.format("ID: %-4d | Name: %-20s | Department: %-15s | Salary: %.2f",
                id, name, department, salary);
    }
}

class EmployeeManager {

    private final TreeMap<Integer, Employee> employees = new TreeMap<>();
    private final TreeMap<Double, Set<Integer>> salaryIndex = new TreeMap<>();
    private final Map<String, Set<Integer>> departmentIndex = new HashMap<>();

    private double salarySum = 0.0;
    private int nextId = 1;
    private final String filename;

    public EmployeeManager(String filename) {
        this.filename = filename;
        load();
    }

    // ---- Persistence -----------------------------------------------
    private void load() {
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                Employee emp = Employee.fromCsv(line);
                employees.put(emp.getId(), emp);
                indexSalary(emp.getSalary(), emp.getId());
                indexDepartment(emp.getDepartment(), emp.getId());
                salarySum += emp.getSalary();
                nextId = Math.max(nextId, emp.getId() + 1);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not load existing data (" + e.getMessage() + ").");
        }
    }

    private void save() {
        // Single buffered write instead of many println() calls -> one I/O flush.
        StringBuilder sb = new StringBuilder();
        for (Employee emp : employees.values()) {
            sb.append(emp.toCsv()).append(System.lineSeparator());
        }
        try (Writer writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Warning: could not save data (" + e.getMessage() + ").");
        }
    }

    // ---- Index helpers -------------------------------------------------
    private void indexSalary(double salary, int id) {
        salaryIndex.computeIfAbsent(salary, k -> new HashSet<>()).add(id);
    }

    private void deindexSalary(double salary, int id) {
        Set<Integer> ids = salaryIndex.get(salary);
        if (ids == null) return;
        ids.remove(id);
        if (ids.isEmpty()) salaryIndex.remove(salary);
    }

    private void indexDepartment(String department, int id) {
        departmentIndex.computeIfAbsent(department.toLowerCase(), k -> new HashSet<>()).add(id);
    }

    private void deindexDepartment(String department, int id) {
        Set<Integer> ids = departmentIndex.get(department.toLowerCase());
        if (ids == null) return;
        ids.remove(id);
        if (ids.isEmpty()) departmentIndex.remove(department.toLowerCase());
    }

    // ---- Add : O(log n) --------------------------------------------
    public Employee addEmployee(String name, String department, double salary) {
        Employee emp = new Employee(nextId++, name, department, salary);
        employees.put(emp.getId(), emp);
        indexSalary(salary, emp.getId());
        indexDepartment(department, emp.getId());
        salarySum += salary;
        save();
        return emp;
    }

    // ---- Update : O(log n) -------------------------------------------
    public Employee updateEmployee(int id, String name, String department, Double salary) {
        Employee emp = employees.get(id);
        if (emp == null) return null;

        if (name != null) {
            emp.setName(name);
        }
        if (department != null && !department.equalsIgnoreCase(emp.getDepartment())) {
            deindexDepartment(emp.getDepartment(), id);
            emp.setDepartment(department);
            indexDepartment(department, id);
        }
        if (salary != null && salary != emp.getSalary()) {
            deindexSalary(emp.getSalary(), id);
            salarySum += salary - emp.getSalary();
            emp.setSalary(salary);
            indexSalary(salary, id);
        }
        save();
        return emp;
    }

    // ---- Delete : O(log n) ---------------------------------------------
    public boolean deleteEmployee(int id) {
        Employee emp = employees.remove(id);
        if (emp == null) return false;
        deindexSalary(emp.getSalary(), id);
        deindexDepartment(emp.getDepartment(), id);
        salarySum -= emp.getSalary();
        save();
        return true;
    }

    // ---- Search : O(log n) for ID match, O(n) for name substring -------
    public List<Employee> searchEmployee(String keyword) {
        List<Employee> results = new ArrayList<>();

        // Try as a direct ID lookup first -> O(log n)
        try {
            int id = Integer.parseInt(keyword.trim());
            Employee byId = employees.get(id);
            if (byId != null) {
                results.add(byId);
                return results;
            }
        } catch (NumberFormatException ignored) {
            // Not a number, fall through to name search.
        }

        // Substring name search genuinely needs to look at every record;
        // there's no ordering that makes "contains" lookups faster
        // without building a specialized text index (e.g. a trie).
        String kw = keyword.toLowerCase();
        for (Employee emp : employees.values()) {
            if (emp.getName().toLowerCase().contains(kw)) {
                results.add(emp);
            }
        }
        return results;
    }

    // ---- List : O(n), already sorted by ID (TreeMap) -------------------
    public List<Employee> listEmployees() {
        return new ArrayList<>(employees.values());
    }

    // ---- Highest Salary : O(log n) --------------------------------------
    public Employee highestSalaryEmployee() {
        if (salaryIndex.isEmpty()) return null;
        Map.Entry<Double, Set<Integer>> top = salaryIndex.lastEntry();
        int anyId = top.getValue().iterator().next();
        return employees.get(anyId);
    }

    // ---- Average Salary : O(1) ------------------------------------------
    public double averageSalary() {
        if (employees.isEmpty()) return 0.0;
        return salarySum / employees.size();
    }

    // ---- Filter by Department : O(k), k = matches only -------------------
    public List<Employee> filterByDepartment(String department) {
        Set<Integer> ids = departmentIndex.get(department.toLowerCase());
        List<Employee> results = new ArrayList<>();
        if (ids == null) return results;
        for (int id : ids) {
            results.add(employees.get(id));
        }
        results.sort(Comparator.comparingInt(Employee::getId));
        return results;
    }

    // ---- Helper : O(log n) ------------------------------------------------
    public Employee findById(int id) {
        return employees.get(id);
    }
}
