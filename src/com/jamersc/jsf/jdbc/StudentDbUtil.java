package com.jamersc.jsf.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class StudentDbUtil {
	
	private static StudentDbUtil instance;
	private DataSource dataSource;
	private String jndiName = "java:comp/env/jdbc/student_tracker";
	
	
	public static StudentDbUtil getInstance() throws Exception {
		// TODO Auto-generated method stub
		if(instance == null) {
			instance = new StudentDbUtil();
		}
		return instance;
	}
	
	private StudentDbUtil() throws Exception {
		dataSource = getDataSource();
	}

	private DataSource getDataSource() throws NamingException {
		// TODO Auto-generated method stub
		InitialContext context = new InitialContext();
		
		DataSource theDataSource = (DataSource) context.lookup(jndiName);
		
		return theDataSource;
	}

	public List<Student> getStudents() throws Exception {
		
		List<Student> students = new ArrayList<>();
		
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
			myConn = dataSource.getConnection();
			
			String sql = "SELECT * FROM student";
			
			myStmt = myConn.createStatement();
			
			myRs = myStmt.executeQuery(sql);
			
			while(myRs.next()) {
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				//create a object to get students
				Student tempStudents = new Student(id, firstName, lastName, email);
				//array list add method
				students.add(tempStudents);
			}
			//return the array list "students" var;
			return students;
			
		} finally {
			close(myConn, myStmt, myRs);
		}

	}

	
	
	//Close the Database connection
	private void close(Connection myConn, Statement myStmt, ResultSet myRs) {
		// TODO Auto-generated method stub
		try {
			if(myConn != null) {
				myConn.close();
			}
			
			if(myStmt != null) {
				myStmt.close();
			}
			
			if(myRs != null) {
				myRs.close();
			}
			
		} catch (Exception exc) {
			// TODO: handle exception
			exc.printStackTrace();
		}
		
	}

	public void addStudent(Student theStudent) throws Exception {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			
			myConn = dataSource.getConnection();
			
			String sql = "INSERT INTO student "
					+ "(first_name, last_name, email) "
					+ "VALUES (?, ?, ?)";
			
			myStmt = myConn.prepareStatement(sql);
			
			//
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			
			myStmt.execute();
			
		} finally {
			close(myConn, myStmt, null);
		}
		
	}

	public Student getStudent(int studentId) throws Exception {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		
		
		try {
		
			myConn = dataSource.getConnection();
			
			String sql = "SELECT * FROM student WHERE id=?";
			
			myStmt = myConn.prepareStatement(sql);
			
			myStmt.setInt(1, studentId);
			
			myRs = myStmt.executeQuery();
			
			Student theStudent = null;
			
			if(myRs.next()) {
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				theStudent = new Student(id, firstName, lastName, email);
			
			} else {
				
				throw new Exception("Could not find the id: " + studentId);
			}
			
			return theStudent;
			
		} finally {
			close(myConn, myStmt, myRs);
		}
		

	}

	
	
	public void updateStudent(Student theStudent) throws Exception {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			
			myConn = dataSource.getConnection();
			
			String sql = "UPDATE student "
					   + "SET first_name=?, last_name=?, email=? "
					   + "WHERE id=?";
			
			myStmt = myConn.prepareStatement(sql);
			
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			myStmt.setInt(4, theStudent.getId());
			
			myStmt.execute();
			
			
			
		} finally {
			close(myConn, myStmt, null);
		}
	}


}
