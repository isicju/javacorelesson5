package com.example.demo.repository;

import com.example.demo.model.Employee;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
@AllArgsConstructor
public class EmployeeRepository {

    private DataSource postgresDataSource;
    private JdbcTemplate postgresJdbcTemplate;
    private NamedParameterJdbcTemplate namedPosgresJdbcTemplate;

    public Employee getEmployeeById(int employeeId) {
        String sql = "SELECT employee_id, first_name, last_name, email, phone_number, hire_date, " +
                "job_id, salary, commission_pct, manager_id, department_id " +
                "FROM employees WHERE employee_id = " + employeeId;

        try (Connection connection = postgresDataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                Employee employee = new Employee();
                employee.setEmployeeId(rs.getInt("employee_id"));
                employee.setFirstName(rs.getString("first_name"));
                employee.setLastName(rs.getString("last_name"));
                employee.setEmail(rs.getString("email"));
                employee.setPhoneNumber(rs.getString("phone_number"));

                Timestamp hireTimestamp = rs.getTimestamp("hire_date");
                employee.setHireDate(hireTimestamp != null ? hireTimestamp.toLocalDateTime() : null);

                employee.setJobId(rs.getString("job_id"));
                employee.setSalary(rs.getBigDecimal("salary"));
                employee.setCommissionPct(rs.getBigDecimal("commission_pct"));

                employee.setManagerId((Integer) rs.getObject("manager_id"));
                employee.setDepartmentId((Integer) rs.getObject("department_id"));

                return employee;
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching employee with id " + employeeId, e);
        }
    }

    public Employee getEmployeeByIdWithPreparedStatement(int employeeId) {
        String sql = "SELECT employee_id, first_name, last_name, email, phone_number, hire_date, " +
                "job_id, salary, commission_pct, manager_id, department_id " +
                "FROM employees WHERE employee_id = ?";

        try (Connection connection = postgresDataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Employee.builder()
                        .employeeId(rs.getInt("employee_id"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .email(rs.getString("email"))
                        .phoneNumber(rs.getString("phone_number"))
                        .hireDate(rs.getTimestamp("hire_date") != null
                                ? rs.getTimestamp("hire_date").toLocalDateTime()
                                : null)
                        .jobId(rs.getString("job_id"))
                        .salary(rs.getBigDecimal("salary"))
                        .commissionPct(rs.getBigDecimal("commission_pct"))
                        .managerId((Integer) rs.getObject("manager_id"))
                        .departmentId((Integer) rs.getObject("department_id"))
                        .build();
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching employee with id " + employeeId, e);
        }
    }

    public Employee getEmployeeByIdJdbcTemplate(int employeeId) {
        String sql = "SELECT employee_id, first_name, last_name, email, phone_number, hire_date, " +
                "job_id, salary, commission_pct, manager_id, department_id " +
                "FROM employees WHERE employee_id = ?";

        return postgresJdbcTemplate.queryForObject(sql, new Object[]{employeeId}, (rs, rowNum) -> Employee.builder()
                .employeeId(rs.getInt("employee_id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .email(rs.getString("email"))
                .phoneNumber(rs.getString("phone_number"))
                .hireDate(rs.getTimestamp("hire_date") != null
                        ? rs.getTimestamp("hire_date").toLocalDateTime() : null)
                .jobId(rs.getString("job_id"))
                .salary(rs.getBigDecimal("salary"))
                .commissionPct(rs.getBigDecimal("commission_pct"))
                .managerId((Integer) rs.getObject("manager_id"))
                .departmentId((Integer) rs.getObject("department_id"))
                .build());
    }

    public Employee getEmployeeByIdWithNamedParameter(int employeeId) {
        String sql = "SELECT employee_id, first_name, last_name, email, phone_number, hire_date, " +
                "job_id, salary, commission_pct, manager_id, department_id " +
                "FROM employees WHERE employee_id = :employeeId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("employeeId", employeeId);

        return namedPosgresJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> Employee.builder()
                .employeeId(rs.getInt("employee_id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .email(rs.getString("email"))
                .phoneNumber(rs.getString("phone_number"))
                .hireDate(rs.getTimestamp("hire_date") != null
                        ? rs.getTimestamp("hire_date").toLocalDateTime()
                        : null)
                .jobId(rs.getString("job_id"))
                .salary(rs.getBigDecimal("salary"))
                .commissionPct(rs.getBigDecimal("commission_pct"))
                .managerId((Integer) rs.getObject("manager_id"))
                .departmentId((Integer) rs.getObject("department_id"))
                .build());
    }

    private final RowMapper<Employee> employeeRowMapper = (rs, rowNum) -> Employee.builder()
            .employeeId(rs.getInt("employee_id"))
            .firstName(rs.getString("first_name"))
            .lastName(rs.getString("last_name"))
            .email(rs.getString("email"))
            .phoneNumber(rs.getString("phone_number"))
            .hireDate(rs.getTimestamp("hire_date") != null
                    ? rs.getTimestamp("hire_date").toLocalDateTime()
                    : null)
            .jobId(rs.getString("job_id"))
            .salary(rs.getBigDecimal("salary"))
            .commissionPct(rs.getBigDecimal("commission_pct"))
            .managerId((Integer) rs.getObject("manager_id"))
            .departmentId((Integer) rs.getObject("department_id"))
            .build();

}
