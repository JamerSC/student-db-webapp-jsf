package com.jamersc.jsf.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.annotation.PostConstruct;
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
	private String searchName;
	
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
	
	// I removed this because its not working when I added search method
    //@PostConstruct
    //public void initialize() {
    //    loadStudents();
    //}
    
	public void loadStudents() {
		
		logger.info("Loading students");
		
		logger.info("Search name: " + searchName);
		
		students.clear();
		
		try {
			//get all students from the database
			if(searchName != null && searchName.trim().length() > 0) {
				
				// search students method
				students = studentDbUtil.searchStudents(searchName);
				
			} else {
				
				// display all students
				students = studentDbUtil.getStudents();
				
			}
			
		} catch (Exception exc) {
			//send this to server logs
			logger.log(Level.SEVERE, "Error loading students", exc);
			
			//add error message at jsf page
			addErrorMessage(exc);
			
		} finally {
			
			searchName = null;
			
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
		
		logger.info("Loading the student id: " + studentId);
		
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
	
	public String deleteStudent(int studentId) {
		
		logger.info("Deleting student id: " + studentId);
		
		try {
			
			studentDbUtil.deleteStudent(studentId);
			
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Error deleting the student id:" + studentId, exc);
			//add error message at jsf page
			addErrorMessage(exc);
		}
		
		return "list-students";
	}

	/**
	 * @return the searchName
	 */
	public String getSearchName() {
		return searchName;
	}

	/**
	 * @param searchName the searchName to set
	 */
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	
	
}
