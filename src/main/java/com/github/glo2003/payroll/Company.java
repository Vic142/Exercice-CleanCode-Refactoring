package com.github.glo2003.payroll;

import com.github.glo2003.payroll.employee.ContractEmployee;
import com.github.glo2003.payroll.employee.Employee;
import com.github.glo2003.payroll.employee.HourlyEmployee;
import com.github.glo2003.payroll.employee.BiWeeklyEmployee;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Company {
    final private List<Employee> employees;
    private final List<Paycheck> pendingPaychecks;
    private final List<Boolean> employeesTakingHolidays;


    public Company() {
        this.employees = new ArrayList<>();
        this.pendingPaychecks = new ArrayList<>();
        employeesTakingHolidays = new ArrayList<>();
    }


    public void processPending() {
        for (Paycheck pendingPaycheck : this.pendingPaychecks) {
            this.employeesTakingHolidays.set(this.employees.indexOf(pendingPaycheck.getOwner()), false);
            System.out.println("Sending " + pendingPaycheck.getAmount() + "$ to " + pendingPaycheck.getOwner());
        }
        this.pendingPaychecks.clear();
    }


    /***
     * add employee
     * @param employee: employee to add
     */
    public void addEmployee(Employee employee) {
        employees.add(employee);
        this.employeesTakingHolidays.add(false);
    }


    /***
     * find all the software engineers
     * @return found
     */
    public List<Employee> findAllSoftwareEngineers() {
        List<Employee> softwareEngineers = new ArrayList<>();
        for (Employee employee : this.employees) {
            if (employee.getRole().equals("engineer")) {
                softwareEngineers.add(employee);
            }
        }
        return softwareEngineers;
    }


    public List<Employee> findAllManagers() { // find managers
        List<Employee> managers = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.getRole().equals("manager")) {
                managers.add(employee);
            }
        }
        return managers;
    }


    public List<Employee> findAllVicePresidents() {
        List<Employee> vicePresidents = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.getRole().equals("vp")) {
                vicePresidents.add(employee);
            }
        }
        return vicePresidents;
    }


    public List<Employee> findAllInterns() {
        List<Employee> interns = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.getRole().equals("intern")) {
                interns.add(employee);
            }
        }
        return interns;
    }


    public void listEmployees() {
        List<Employee> vicePresidents = this.findAllVicePresidents();
        System.out.println("Vice presidents:");
        vicePresidents.forEach(e -> System.out.println("\t" + e.toString()));

        List<Employee> engineers = this.findAllSoftwareEngineers();
        System.out.println("Software Engineers:");
        engineers.forEach(e -> System.out.println("\t" + e.toString()));

        List<Employee> managers = this.findAllManagers();
        System.out.println("Managers:");
        managers.forEach(e -> System.out.println("\t" + e.toString()));

        List<Employee> interns = this.findAllInterns();
        System.out.println("Interns:");
        interns.forEach(e -> System.out.println("\t" + e.toString()));
    }


    public void createPending() {
        for (Employee employee : employees) {
            if (employee instanceof HourlyEmployee || employee instanceof BiWeeklyEmployee || employee instanceof ContractEmployee) {
                pendingPaychecks.add(new Paycheck(employee, employee.getSalary()));
            } else {
                throw new RuntimeException("Error : the employee is neither hourly, biweekly nor contract!");
            }
        }
    }


    public void raiseSalary(Employee employee, float raise) {
        if (raise < 0) {
            throw new RuntimeException("Error : the raise is negative!");
        }
        if (!this.employees.contains(employee)) {
            throw new RuntimeException("Error : the given employee is not part of the company's employee list!");
        }
        if (employee instanceof HourlyEmployee || employee instanceof BiWeeklyEmployee || employee instanceof ContractEmployee) {
            employee.raiseSalary(raise);
        } else {
            throw new RuntimeException("Error : the employee is neither hourly nor biweekly!");
        }
    }



    public void takeHolidayWithPayout(Employee employee, Integer amount) {
        if (employee instanceof HourlyEmployee) {
            HourlyEmployee hourlyEmployee = (HourlyEmployee) employee;
            pendingPaychecks.add(new Paycheck(employee, (hourlyEmployee).getAmount() * ((HourlyEmployee) employee).getRate() / 2f)); // pay 5 days
            employee.setVacation_days(employee.getVacation_days() - Optional.ofNullable(amount).orElse(5));
        }
        else if (employee instanceof BiWeeklyEmployee) {
            BiWeeklyEmployee salariedEmployee = (BiWeeklyEmployee) employee;
            pendingPaychecks.add(new Paycheck(employee, (salariedEmployee).getBiweekly() / 2f)); // pay a week
            employee.setVacation_days(employee.getVacation_days() - Optional.ofNullable(amount).orElse(5));
        }
        else {
            throw new RuntimeException("This type of employee cannot take a holiday!");
        }
        editEmployeeHolidays(employee);
    }

    public void takeHolidayWithoutPayout(Employee employee, Integer amount) {
        if (employee instanceof HourlyEmployee || employee instanceof BiWeeklyEmployee) {
            employee.setVacation_days(employee.getVacation_days() - Optional.ofNullable(amount).orElse(5));
            editEmployeeHolidays(employee);
        }
        else {
            throw new RuntimeException("This type of employee cannot take a holiday!");
        }
    }


    public void editEmployeeHolidays(Employee employee) {
        int i = this.employees.indexOf(employee);
        if (!employeesTakingHolidays.get(i) && !(employee instanceof ContractEmployee)) {
            employeesTakingHolidays.set(i, true);
        }
        else if (employee instanceof ContractEmployee) {
            throw new RuntimeException("This type of employee cannot take a holiday!");
        }
    }

    public void takeHoliday(Employee employee, boolean payoutTime, Integer amount) {
        if (!this.employees.contains(employee)) {
            throw new RuntimeException("Error : the given employee is not part of the company's employee list!");
        }
        if (employee instanceof ContractEmployee) {
            throw new RuntimeException("Error : this type of employee cannot take vacations !");
        }
        if (employee.getVacation_days() < Optional.ofNullable(amount).orElse(5)) {
            // throw new RuntimeException("Error : the amount given is more than the available amount !");
            System.out.println("The amount given is more than the available amount, the employee cannot take holidays !");
        }
        else if (payoutTime) {
            takeHolidayWithPayout(employee, amount);
        }
        else {
            takeHolidayWithoutPayout(employee, amount);
        }
    }




    public float getAveragePendingPayCheck() {
        if (this.pendingPaychecks.isEmpty()) {
            throw new RuntimeException("Error : the payckeck is empty, so there cannot be an average paycheck!");
        }

        float averagePendingPaycheck = 0.f;
        for (Paycheck paycheck : this.pendingPaychecks) {
            averagePendingPaycheck += paycheck.getAmount();
        }
        averagePendingPaycheck /= this.pendingPaychecks.size();
        return averagePendingPaycheck;
    }


    public float getAllPendingPaychecksSum() {
        float paychecksSum = 0.f;
        for (Paycheck paycheck : this.pendingPaychecks) {
            paychecksSum += paycheck.getAmount();
        }
        return paychecksSum;
    }


    public int getNumberOfEmployeesOnHoliday() {
        int nbEmployees = 0;
        for (Boolean employeesTakingHoliday : employeesTakingHolidays) {
            if (employeesTakingHoliday) {
                nbEmployees++;
            }
        }
        return nbEmployees;
    }

    public List<Paycheck> getPendingPayChecks() {
        return this.pendingPaychecks;
    }

}
