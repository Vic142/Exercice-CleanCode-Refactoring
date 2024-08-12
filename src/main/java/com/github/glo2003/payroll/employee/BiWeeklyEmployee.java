package com.github.glo2003.payroll.employee;

public class BiWeeklyEmployee extends Employee {
    private float biweekly;

    public BiWeeklyEmployee(String name, String role, int vacation_days, float biweekly) {
        super(name, role, vacation_days);
        this.biweekly = biweekly;
    }

    public float getBiweekly() {
        return biweekly;
    }

    public void setBiweekly(float biweekly) {
        this.biweekly = biweekly;
    }

    @Override
    public String toString() {
        return "SalariedEmployee{" +
                "name='" + this.getName() + '\'' +
                ", role='" + this.getRole() + '\'' +
                ", vacation_days=" + this.getVacation_days() +
                ", monthly=" + biweekly +
                '}';
    }

    @Override
    public void raiseSalary(float raise) {
        setBiweekly(this.getBiweekly() + raise);
    }

    @Override
    public float getSalary() {
        return getBiweekly();
    }
}
