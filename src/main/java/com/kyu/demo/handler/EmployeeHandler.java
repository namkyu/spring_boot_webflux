package com.kyu.demo.handler;

import com.kyu.demo.repository.Employee;
import com.kyu.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * @Project : spring_boot_webflux
 * @Date : 2019-05-10
 * @Author : nklee
 * @Description :
 */
@Component
public class EmployeeHandler {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Mono<ServerResponse> getAllEmployees() {
        return ok().body(employeeRepository.findAllEmployees(), Employee.class);
    }

    public Mono<ServerResponse> getEmployeeById(ServerRequest req) {
        return ok().body(employeeRepository.findEmployeeById(req.pathVariable("id")), Employee.class);
    }

    public Mono<ServerResponse> updateEmployee(ServerRequest req) {
        return req.body(toMono(Employee.class))
                .doOnNext(employeeRepository::updateEmployee)
                .then(ok().build());
    }
}
