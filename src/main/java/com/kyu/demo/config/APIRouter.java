package com.kyu.demo.config;

import com.kyu.demo.handler.EmployeeHandler;
import com.kyu.demo.repository.Employee;
import com.kyu.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


/**
 * @Project : spring_boot_webflux
 * @Date : 2019-05-09
 * @Author : nklee
 * @Description :
 */
@Configuration
public class APIRouter {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeHandler employeeHandler;

    @Bean
    public RouterFunction<ServerResponse> getAllEmployeesRoute() {
        return route(GET("/employees"), req -> ok().body(employeeRepository.findAllEmployees(), Employee.class));
    }

    @Bean
    public RouterFunction<ServerResponse> getEmployeeByIdRoute() {
        return route(GET("/employees/{id}"), employeeHandler::getEmployeeById);
    }

    @Bean
    public RouterFunction<ServerResponse> updateEmployeeRoute() {
        return route(POST("/employees/update"), employeeHandler::updateEmployee);
    }

    @Bean
    public RouterFunction<ServerResponse> composedRoutes() {
        return route(GET("/employees"), req -> ok().body(employeeRepository.findAllEmployees(), Employee.class))
                .andRoute(GET("/employees/{id}"), employeeHandler::getEmployeeById)
                .andRoute(POST("/employees/update"), employeeHandler::updateEmployee);
    }
}
