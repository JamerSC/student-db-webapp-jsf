package com.jamersc.jsf.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class StudentController {

	private List<Student> students; //create a var for list of students
	private StudentDbUtil studentDbUtil; //reference for db utility
	private Logger logger = Logger.getLogger(getClass().getName());
	
	//No argument constructor
	public StudentController() throws Exception {
		//empty initialization
		students = new ArrayList<>();
		//handle db util
		studentDbUtil = StudentDbUtil.getInstance();
	}
	
	public List<Student> getStudents() {
		return students;
	}
	
	public void loadStudents() {
		
		logger.info("Loading students");
		
		students.clear();
		
		try {
			//get all students from the database
			students = studentDbUtil.getStudents();
			
		} catch (Exception exc) {
			//send this to server logs
			logger.log(Level.SEVERE, "Error loading students", exc);
			
			//add error message at jsf page
			addErrorMessage(exc);
		}
	}

	
	private void addErrorMessage(Exception exc) {
		
		FacesMessage message = new FacesMessage("Error: " + exc.getMessage());
		
		FacesContext.getCurrentInstance().addMessage(null, message);
		//Note: Faces Message & FacesContext are classes on JSF API
	}
	
	
	public String addStudent(Student theStudent) {
		
		logger.info("Adding student" + theStudent);
		
		try {
			
			studentDbUtil.addStudent(theStudent);
			
		} catch (Exception exc) {
			//send this to server logs
			logger.log(Level.SEVERE, "Error adding student", exc);
			
			//add error message at jsf page
			addErrorMessage(exc);

			return null;
		}
		
		return "list-students?faces-redirect=true";
		
	}
	
	public String loadStudent(int studentId) {
		
		logger.info("The student id: " + studentId);
		
		try {
			
			Student theStudent = studentDbUtil.getStudent(studentId);
			
			ExternalContext externalContext =
					FacesContext.getCurrentInstance().getExternalContext();
			
			Map<String, Object> requestMap = externalContext.getRequestMap();
			requestMap.put("student", theStudent);
			
		} catch (Exception exc) {
			
			logger.log(Level.SEVERE, "Error loading student id: " + studentId, exc);
			
			//add error message at jsf page
			addErrorMessage(exc);
			
			return null;
		}
		
		return "update-student-form.xhtml";
	}
	
	
	public String updateStudent(Student theStudent) {
		
		logger.info("Update the student: " + theStudent);
		
		try {
			// update student in the database
			studentDbUtil.updateStudent(theStudent);

		} catch (Exception exc) {

			logger.log(Level.SEVERE, "Error loading the student " + theStudent, exc);
			//add error message at jsf page
			addErrorMessage(exc);
			return null;
		}
		
		return "list-students?faces-redirect=true";
	}
	
}
