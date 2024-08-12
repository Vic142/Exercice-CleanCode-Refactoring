package com.github.glo2003;

import com.github.glo2003.payroll.Company;
import com.github.glo2003.payroll.employee.ContractEmployee;
import com.github.glo2003.payroll.employee.Employee;
import com.github.glo2003.payroll.employee.HourlyEmployee;
import com.github.glo2003.payroll.employee.BiWeeklyEmployee;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Company companyPayroll = new Company();

        Employee e1 = new HourlyEmployee("Alice", "vp", 25, 100, 35.5f * 4);
        Employee e2 = new BiWeeklyEmployee("Bob", "engineer", 4, 1500);
        Employee e3 = new BiWeeklyEmployee("Charlie", "manager", 4, 2000);
        Employee e4 = new HourlyEmployee("Ernest", "intern", 1, 5, 50 * 4);
        Employee e5 = new HourlyEmployee("Fred", "intern", 1, 5, 50 * 4);
        //Employee e5 = new ContractEmployee("Fred", "engineer", new ArrayList<>(Arrays.asList(20f,546f,3004f))) ;

        companyPayroll.addEmployee(e1);
        companyPayroll.addEmployee(e2);
        companyPayroll.addEmployee(e3);
        companyPayroll.addEmployee(e4);
        companyPayroll.addEmployee(e5);

        System.out.println("----- Listing employees -----");
        companyPayroll.listEmployees();

        System.out.println("----- Giving raises -----");
        companyPayroll.raiseSalary(e1, 10);
        companyPayroll.raiseSalary(e2, 100);

        System.out.println("\n----- Holidays -----");
        companyPayroll.takeHoliday(e1, true, null);
        companyPayroll.takeHoliday(e2, false, 10);
        companyPayroll.takeHoliday(e3, true, null);
        System.out.println("Number of employees in holidays: " + companyPayroll.getNumberOfEmployeesOnHoliday());

        System.out.println("\n----- Create paychecks -----");
        companyPayroll.createPending();

        System.out.println("\n----- Pay statistics -----");
        System.out.println("Total money spent: ");
        float avg = companyPayroll.getAveragePendingPayCheck();
        System.out.println("Average paycheck: " + avg);

        System.out.println("\n----- Pay -----");
        companyPayroll.processPending();
    }
}
