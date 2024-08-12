package com.github.glo2003.payroll;

import com.github.glo2003.payroll.employee.Employee;
import com.github.glo2003.payroll.employee.HourlyEmployee;
import com.github.glo2003.payroll.employee.BiWeeklyEmployee;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

class CompanyPayrollTest {

    public static final float HOURLY_RATE = 10;
    public static final float HOURLY_AMOUNT = 25;
    public static final String HOURLY_NAME = "William";
    public static final String SALARIED_NAME = "Xavier";
    public static final float BIWEEKLY_AMOUNT = 10_000;
    public static final float RAISE = 10;
    public static final float ANOTHER_MONTHLY_AMOUNT = 20_000;
    public static final int VACATION_DAYS = 12;

    Company company;
    Employee vp;
    Employee eng;
    Employee manager;
    Employee intern1;
    Employee intern2;
    HourlyEmployee hourlyEmployee;
    BiWeeklyEmployee salariedEmployee;
    BiWeeklyEmployee anotherSalariedEmployee;

    @BeforeEach
    void setUp() {
        company = new Company();
        vp = new HourlyEmployee("Alice", "vp", 25, 100, 35.5f * 2);
        eng = new BiWeeklyEmployee("Bob", "engineer", 4, 1500);
        manager = new BiWeeklyEmployee("Charlie", "manager", 10, 2000);
        intern1 = new HourlyEmployee("Ernest", "intern", 10, 5, 50 * 2);
        intern2 = new HourlyEmployee("Fred", "intern", 10, 5, 50 * 2);

        hourlyEmployee = new HourlyEmployee(HOURLY_NAME, "engineer", VACATION_DAYS, HOURLY_RATE, HOURLY_AMOUNT);
        salariedEmployee = new BiWeeklyEmployee(SALARIED_NAME, "engineer", VACATION_DAYS, BIWEEKLY_AMOUNT);
        anotherSalariedEmployee = new BiWeeklyEmployee("Yan", "manager", VACATION_DAYS, ANOTHER_MONTHLY_AMOUNT);
    }

    @Test
    void givenAnHourlyEmployee_whenCreatePendings_thenCorrectHourlyPaycheck() {
        company.addEmployee(hourlyEmployee);

        company.createPending();

        Paycheck paycheck = company.getPendingPayChecks().get(0);
        assertThat(paycheck.getOwnerName()).isEqualTo(HOURLY_NAME);
        assertThat(paycheck.getAmount()).isEqualTo(HOURLY_RATE * HOURLY_AMOUNT);
    }

    @Test
    void givenASalariedEmployee_whenCreatePending_thenCorrectSalariedPaycheckIsCreated() {
        company.addEmployee(salariedEmployee);

        company.createPending();

        Paycheck paycheck = company.getPendingPayChecks().get(0);
        assertThat(paycheck.getOwnerName()).isEqualTo(SALARIED_NAME);
        assertThat(paycheck.getAmount()).isEqualTo(BIWEEKLY_AMOUNT);
    }

    @Test
    void givenSeveralEmployeesAndPayCheckPending_whenProcessPending_thenPendingPaychecksIsEmpty() {
        company.addEmployee(vp);
        company.addEmployee(eng);
        company.addEmployee(manager);
        company.addEmployee(intern1);
        company.addEmployee(intern2);
        company.createPending();

        company.processPending();

        assertThat(company.getPendingPayChecks().size()).isEqualTo(0);
    }

    @Test
    void givenOneEngineer_whenFindAllSoftwareEngineers_thenTheEngineerIsReturned() {
        company.addEmployee(eng);

        List<Employee> es = company.findAllSoftwareEngineers();

        assertThat(es).containsExactly(eng);
    }

    @Test
    void givenOneManager_whenFindAllManagers_thenTheManagerIsReturned() {
        company.addEmployee(manager);

        List<Employee> es = company.findAllManagers();
        assertThat(es).containsExactly(manager);
    }

    @Test
    void givenOneVicePresident_whenFindAllVicePresidents_ThenTheVicePresidentIsReturned() {
        company.addEmployee(vp);

        List<Employee> es = company.findAllVicePresidents();

        assertThat(es).containsExactly(vp);
    }

    @Test
    void givenInterns_whenFindAllInterns_thenAllTheInternsAreFound() {
        company.addEmployee(intern1);
        company.addEmployee(intern2);

        List<Employee> es = company.findAllInterns();

        assertThat(es).containsExactly(intern1, intern2);
    }

    @Test
    void givenSeveralEmployees_whenCreatePending_thenAllThePendingPaychecksAreCreated() {
        company.addEmployee(vp);
        company.addEmployee(eng);
        company.addEmployee(manager);
        company.addEmployee(intern1);
        company.addEmployee(intern2);

        company.createPending();

        assertThat(company.getPendingPayChecks().size()).isEqualTo(5);
    }

    @Test
    void givenOneHourlyEmployee_whenSalaryRaise_thenTheHourlySalaryIsRaised() {
        company.addEmployee(hourlyEmployee);

        company.raiseSalary(hourlyEmployee, RAISE);
        company.createPending();
        Paycheck paycheck = company.getPendingPayChecks().get(0);

        assertThat(paycheck.getAmount()).isEqualTo((HOURLY_RATE + RAISE) * HOURLY_AMOUNT);
    }

    @Test
    void givenOneSalariedEmployee_whenSalaryRaise_thenTheMonthlySalaryIsRaised() {
        company.addEmployee(salariedEmployee);

        company.raiseSalary(salariedEmployee, RAISE);
        company.createPending();
        Paycheck paycheck = company.getPendingPayChecks().get(0);

        assertThat(paycheck.getAmount()).isEqualTo(BIWEEKLY_AMOUNT + RAISE);
    }

    @Test
    void givenAnEngineer_whenSalaryRaise_thenCannotGiveRaise() {
        company.addEmployee(eng);

        Assert.assertThrows(RuntimeException.class, () -> company.raiseSalary(eng, -1));
    }

    @Test
    void givenAnEmployeeNotInTheCompany_whenSalaryRaise_thenCannotGiveRaise() {
        Assert.assertThrows(RuntimeException.class, () -> company.raiseSalary(eng, 10));
    }

    @Test
    void givenOneSalariedEmployee_whenTakeHolidayWithPayout_thenHeIsPayedOneWeek() {
        company.addEmployee(salariedEmployee);

        company.takeHoliday(salariedEmployee, true, null);
        Paycheck pending = company.getPendingPayChecks().get(0);

        assertThat(pending.getAmount()).isEqualTo(BIWEEKLY_AMOUNT / 2);
        assertThat(salariedEmployee.getVacation_days()).isEqualTo(VACATION_DAYS - 5);
    }

    @Test
    void givenOneSalariedEmployee_whenTakeHolidayWithPayout_thenHisTakenDaysAreRemovedFromHisAvailableVacationDays() {
        company.addEmployee(salariedEmployee);

        company.takeHoliday(salariedEmployee, true, null);

        assertThat(salariedEmployee.getVacation_days()).isEqualTo(VACATION_DAYS - 5);
    }

    @Test
    void givenOneSalariedEmployee_whenTakeHolidayWithoutPayout_thenHeIsNotPayed() {
        company.addEmployee(salariedEmployee);
        int amount = 2;

        company.takeHoliday(salariedEmployee, false, amount);

        assertThat(company.getPendingPayChecks()).hasSize(0);
    }

    @Test
    void givenOneSalariedEmployee_whenTakeHolidayWithoutPayout_thenHisTakenDaysAreRemovedFromHisAvailableVacationDays() {
        company.addEmployee(salariedEmployee);
        int amount = 2;

        company.takeHoliday(salariedEmployee, false, amount);

        assertThat(salariedEmployee.getVacation_days()).isEqualTo(VACATION_DAYS - amount);
    }

    @Test
    void givenOneHourlyEmployee_whenTakeHolidayWithPayout_thenHeIsPayedOneWeek() {
        company.addEmployee(hourlyEmployee);

        company.takeHoliday(hourlyEmployee, true, null);
        Paycheck pending = company.getPendingPayChecks().get(0);

        assertThat(pending.getAmount()).isEqualTo(HOURLY_AMOUNT * HOURLY_RATE / 2f);
        assertThat(hourlyEmployee.getVacation_days()).isEqualTo(VACATION_DAYS - 5);
    }

    @Test
    void givenOneHourlyEmployee_whenTakeHolidayWithPayout_thenHisTakenDaysAreRemovedFromHisAvailableVacationDays() {
        company.addEmployee(hourlyEmployee);

        company.takeHoliday(hourlyEmployee, true, null);

        assertThat(hourlyEmployee.getVacation_days()).isEqualTo(VACATION_DAYS - 5);
    }

    @Test
    void givenOneHourlyEmployee_whenTakeHolidayWithoutPayout_thenHeIsNotPayed() {
        company.addEmployee(hourlyEmployee);
        int amount = 2;

        company.takeHoliday(hourlyEmployee, false, amount);

        assertThat(company.getPendingPayChecks()).hasSize(0);
    }

    @Test
    void givenOneHourlyEmployee_whenTakeHolidayWithoutPayout_thenHisTakenDaysAreRemovedFromHisAvailableVacationDays() {
        company.addEmployee(hourlyEmployee);
        int amount = 2;

        company.takeHoliday(hourlyEmployee, false, amount);

        assertThat(hourlyEmployee.getVacation_days()).isEqualTo(VACATION_DAYS - amount);
    }

    @Test
    void givenSeveralSalariedEmployeesWithPendingPaychecks_whenGetAveragePendingPaycheck_thenTheAverageOfAllThePendingPaychecksIsReturned() {
        company.addEmployee(salariedEmployee);
        company.addEmployee(anotherSalariedEmployee);
        company.createPending();

        float avg = company.getAveragePendingPayCheck();

        assertThat(avg).isEqualTo((BIWEEKLY_AMOUNT + ANOTHER_MONTHLY_AMOUNT) / 2);
    }

    @Test
    void givenSeveralSalariedEmployeesWithPendingPaychecks_whenGetAllPendingPaychecksSum_thenTheSumOfAllThePendingPaychecksIsReturned() {
        company.addEmployee(salariedEmployee);
        company.addEmployee(anotherSalariedEmployee);
        company.createPending();

        float t = company.getAllPendingPaychecksSum();

        assertThat(t).isEqualTo(BIWEEKLY_AMOUNT + ANOTHER_MONTHLY_AMOUNT);
    }

    @Test
    void givenSeveralEmployeesWithNoOneOnHoliday_whenGetNumberOfEmployeesOnHoliday_thenItFindsNoOneOnHoliday() {
        company.addEmployee(vp);
        company.addEmployee(eng);
        company.addEmployee(manager);
        company.addEmployee(intern1);
        company.addEmployee(intern2);

        int x = company.getNumberOfEmployeesOnHoliday();

        assertThat(x).isEqualTo(0);
    }

    @Test
    void givenSeveralEmployeesWithSomeTakingHolidays_whenGetNumberOfEmployeesOnHoliday_thenTheCorrectNumberOfEmployeesOnVacationIsFound() {
        company.addEmployee(vp);
        company.addEmployee(eng);
        company.addEmployee(manager);
        company.addEmployee(intern1);
        company.addEmployee(intern2);
        company.takeHoliday(vp, false, 2);
        company.takeHoliday(vp, false, 2);
        company.takeHoliday(eng, false, 2);
        company.takeHoliday(manager, false, 2);

        int x = company.getNumberOfEmployeesOnHoliday();

        assertThat(x).isEqualTo(3);
    }
}