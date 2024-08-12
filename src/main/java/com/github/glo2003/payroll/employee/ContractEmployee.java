package com.github.glo2003.payroll.employee;

import java.util.List;

public class ContractEmployee extends Employee {

    private final List<Float> milestonesPay;
    private int currentMilestone;

    public ContractEmployee(String name, String role, List<Float> milestonesPay) {
        super(name, role, 0);
        this.milestonesPay = milestonesPay;
        this.currentMilestone = 0;
    }

    public List<Float> getMilestonesPay() {
        return this.milestonesPay;
    }

    public int getCurrentMilestone() {
        return this.currentMilestone;
    }

    public void incrementMilestone() {
        this.currentMilestone++;
    }

    public boolean canGetPaid() {
        return this.currentMilestone < this.milestonesPay.size();
    }

    @Override
    public void raiseSalary(float raise) {
        if (canGetPaid()) {
            this.milestonesPay.set(this.currentMilestone, this.milestonesPay.get(this.currentMilestone)+raise);
        }
    }

    @Override
    public float getSalary() {
        this.currentMilestone++;
        return this.milestonesPay.get(this.currentMilestone-1);
    }

}