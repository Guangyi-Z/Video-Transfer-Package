package com.test;

import java.io.*;
import java.util.*;

public class Employee implements Serializable {
	
	public int employeeNumber;
	public Object employeeName;
	
	public Employee(int num, String name) {
		employeeNumber = num;
		employeeName= name;
	}
	
	public int getEmployeeNumber() {
		return employeeNumber ;
	}
	
	public void setEmployeeNumber(int num) {
		employeeNumber = num;
	}
	
	public Object getEmployeeName() {
		return employeeName ;
	}
	
	public void setEmployeeName(String name) {
		employeeName = name;
	}
}
