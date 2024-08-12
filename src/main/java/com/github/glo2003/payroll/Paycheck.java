package com.github.glo2003.payroll;

import com.github.glo2003.payroll.employee.Employee;

public class Paycheck {
    private Employee owner;
    private float amount;

    public Paycheck(Employee owner, float amount) {
        this.owner = owner;
        this.amount = amount;
    }

    public Employee getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return owner.getName();
    }

    public float getAmount() {
        return amount;
    }
}
