package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController()
@RequestMapping("/employees")
@AllArgsConstructor
public class EmployeeController {

    private EmployeeRepository employeeRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable int id) {
        Employee employee = employeeRepository.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/prepared/{id}")
    public ResponseEntity<Employee> getEmployeeByIdPrepared(@PathVariable int id) {
        Employee employee = employeeRepository.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/template/{id}")
    public ResponseEntity<Employee> getEmployeeByIdJdbTemplate(@PathVariable int id) {
        Employee employee = employeeRepository.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/named/{id}")
    public ResponseEntity<Employee> getEmployeeByIdNamed(@PathVariable int id) {
        Employee employee = employeeRepository.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employee);
    }
}
