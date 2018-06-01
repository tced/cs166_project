/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Plane");
				System.out.println("2. Add Pilot");
				System.out.println("3. Add Flight");
				System.out.println("4. Add Technician");
				System.out.println("5. Book Flight");
				System.out.println("6. List number of available seats for a given flight.");
				System.out.println("7. List total number of repairs per plane in descending order");
				System.out.println("8. List total number of repairs per year in ascending order");
				System.out.println("9. Find total number of passengers with a given status");
				System.out.println("10. < EXIT");
				
				switch (readChoice()){
					case 1: AddPlane(esql); break;
					case 2: AddPilot(esql); break;
					case 3: AddFlight(esql); break;
					case 4: AddTechnician(esql); break;
					case 5: BookFlight(esql); break;
					case 6: ListNumberOfAvailableSeats(esql); break;
					case 7: ListsTotalNumberOfRepairsPerPlane(esql); break;
					case 8: ListTotalNumberOfRepairsPerYear(esql); break;
					case 9: FindPassengersCountWithStatus(esql); break;
					case 10: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	public static boolean flag = true; 
	public static void AddPlane(DBproject esql) {//1
		String query = "INSERT INTO Plane (id, make, model, age, seats) VALUES(";  
		String plane_ID, plane_make, plane_model = "";  
		int plane_age, plane_seats = 0;
		try { 
			
 			//do while loop to find the plane_ID 
			do {
			 	System.out.print("Insert the plane's ID: "); 
		  		plane_ID = in.readLine();
				if (plane_ID.length() == 0) {
				   System.out.print("Invalid ID, please enter again\n"); 
				   flag = false; 
				}
				else {flag = true; }
				 	
			}while(!flag);	
		
			//do while loop to find the plane make
			do {
				System.out.print("Insert the Plane's make: ");
		  		plane_make = in.readLine();	
				if (plane_make.length() == 0 || plane_make.length() > 32) {
				   System.out.print("Please enter a vaid plane make: "); 
				   flag = false; 
				}
				else{ flag = true; }
			}while(!flag);
 
			//do while loop to find the plane model 
		        do {
				System.out.print("Insert the Plane's model: ");
		  		plane_model = in.readLine();	
				if (plane_model.length() == 0 || plane_model.length() > 64) {
				   System.out.print("Invalid plane model, please enter again\n"); 
				   flag = false; 
				}
				else{ flag = true; }	
			}while(!flag); 
	
			//do while loop to find plane age	
		        do {
				System.out.print("Insert the Plane's age: "); 
		  		plane_age = Integer.parseInt(in.readLine()); 
				if (plane_age < 0) {
				   System.out.print("Error plane age cannot be less than 0, please enter again\n"); 
				   flag = false; 
				}
				else{ flag = true; }	
			}while(!flag); 
			//do while loop to find plane's seats 
			do {
				System.out.print("Insert how many seats the Plane contains: ");
		  		plane_seats = Integer.parseInt(in.readLine()); 
				if (plane_seats < 0 || plane_seats > 500) {
				   System.out.print("Error: you entered a plane seat that's out of bounds. Please Try again.\n"); 
				   flag = false; 
				}
				else{
				   flag = true; 
				}
			}while(!flag); 
			query +=  plane_ID + ", \'" + plane_make + "\'" + ", \'" + plane_model + "\'" + "," + plane_age + "," + plane_seats + ")\n";
			//int rowCount = esql.executeQuery(query); 
			//System.out.println("total rows(s): " + rowCount); 

		 	System.out.print(query);
		  	esql.executeUpdate(query); 
			    
  					}
		catch (Exception e) {
			System.err.println(e.getMessage());

		}
	}

	public static void AddPilot(DBproject esql) {//2
		String query2 = "INSERT INTO Pilot (id, fullname, nationality) VALUES(";
		String pilot_ID, pilot_name, pilot_nationality = ""; 
		try {
                 
		  //do while loop for pilot id 
		  do {
		  	System.out.print("Insert Pilot's ID: ");
                  	pilot_ID = in.readLine();
			if (pilot_ID.length() == 0) {
			   System.out.print("Error: you did not enter a valid pilot ID. Please try again\n"); 
			   flag = false; 
			}
			else {flag = true;}
                  }while(!flag);
	         
		  //do while loop for pilot name 
                  do {
			System.out.print("Insert Pilot's fullname: ");
                  	pilot_name = in.readLine();
			if (pilot_name.length() > 128) {
			   System.out.print("Error: invalid pilot name. Please try again\n"); 
			   flag = false; 
			}
			else {flag = true;} 
                  }while(!flag); 

		  //do while loop for pilot's nationality 
		  do {
		   	System.out.print("Insert the Pilot's nationality: ");
                 	pilot_nationality = in.readLine();
 			if (pilot_nationality.length() > 25) {
			   System.out.print("Error: invalid nationality. Please try again\n"); 
			}
			else {flag = true;}
		  }while(!flag);
		  query2 +=  pilot_ID + ", \'" + pilot_name + "\'" + ", \'" + pilot_nationality + "\');\n";
		  
                  System.out.print(query2);
                  esql.executeUpdate(query2);
                }
                catch (Exception e) {
                  System.err.println(e.getMessage());
                }
	}

	public static void AddFlight(DBproject esql) {//3
		// Given a pilot, plane and flight, adds a flight in the DB
		String query3 = "INSERT INTO Flight (fnum, cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_airport, departure_airport) VALUES("; 
		String flight_fnum, flight_cost, num_seats_sold, num_flight_stops, plane_dept_date, plane_arrival_date, airport_arrival, airport_depart = "";

		try { 
		  do { 
 		  	System.out.print("Insert the flight's fnum: "); 
		  	flight_fnum = in.readLine();
		  	if (flight_fnum.length() == 0) {
				System.out.print("Error, you did not type a valid flight number. Please try again\n");
			        flag = false;  
			}
 			else {flag = true;}
                  }while(!flag); 

		  do {
		  	System.out.print("Insert the flight's cost: ");
		  	flight_cost = in.readLine();
			if (flight_cost.length() == 0) {
				System.out.print("Error, you did not type a valid flight cost. Please try again\n"); 
				flag = false; 
			}
			else {flag = true;}
		  }while(!flag); 

		  do{ 
		  	System.out.print("Insert how many seats have been sold: ");
		  	num_seats_sold = in.readLine(); 
		  	if (num_seats_sold.length() == 0) {
				System.out.print("Error, you did not type a valid number for seats sold. Please try again\n"); 
				flag = false; 
			}
			else {flag = true;}

		  }while(!flag); 

		  do{
		  	System.out.print("Insert the number of stops that flight has: "); 
		  	num_flight_stops = in.readLine(); 
		  	if (num_flight_stops.length() == 0) {
				System.out.print("Error, you did not type a valid number of flights stop. Please try again\n"); 
				flag = false; 
			}
			else {flag = true;}

		  }while(!flag); 

		  do{
		  	System.out.print("Insert the flight's actual departure date in the following format YYYY-MM-DD: ");
		  	plane_dept_date = in.readLine(); 
		  	if (plane_dept_date.length() == 0) {
				System.out.print("Error, you did not type a valid departure date. Please try again\n"); 
				flag = false; 
			}
			else {flag = true;}

		  }while(!flag); 

		  do{
		  	System.out.print("Insert the flight's actual arrival date in the following format YYYY-MM-DD: "); 
		  	plane_arrival_date = in.readLine(); 
		  	if (plane_arrival_date.length() == 0) {
				System.out.print("Error, you did not type a valid flight arrival date. Please try again\n"); 
				flag = false; 
			}
			else {flag = true;}

		  }while(!flag); 

		  do{
		  	System.out.print("Insert the airport's code that the flight will be arriving at: "); 
		  	airport_arrival = in.readLine(); 
		  	if (airport_arrival.length() > 5 || airport_arrival.length() == 0) {
				System.out.print("Error, you did not type a valid airport name. Please try again\n"); 
				flag = false; 
			}
			else {flag = true;}

		  }while(!flag); 

	          do {
		  	System.out.print("Insert the airport's code that the flight will be departing from: "); 
		  	airport_depart = in.readLine(); 
		  	if (airport_depart.length() == 0 || airport_depart.length() > 5) {
				System.out.print("Error, you did not type a valid airport code for departure. Please try again\n"); 
				flag = false; 
			}
			else {flag = true;}

		  }while(!flag); 	
	
	          query3 +=  flight_fnum + ", " + flight_cost + ", " + num_seats_sold + "," + num_flight_stops + ", \'" + plane_dept_date + "\'" + ", \'" + plane_arrival_date + "\'" + ", \'" + airport_arrival + "\'" + ", \'" + airport_depart + "\')\n"; 
		  
		  System.out.print(query3);
		  esql.executeUpdate(query3); 	
		}
		catch (Exception e) {
		  System.err.println(e.getMessage());
		}

 	}
	
	public static void AddTechnician(DBproject esql) {//4
                String query4 = "INSERT INTO Technician (id, full_name) VALUES(";
		String tech_ID, tech_name = ""; 
		try {
		  do{ 
                  	System.out.print("Insert Technician's ID: ");
                  	tech_ID = in.readLine();
                  	if (tech_ID.length() == 0) {
			  System.out.print("Error, you did not insert a technician's ID. Please Try Again\n"); 
	  		  flag = false; 
			}
                        else {flag = true;} 
		  }while(!flag); 
	
		  do{
		  	System.out.print("Insert Technician's fullname: ");
                  	tech_name = in.readLine();
		        if (tech_name.length() == 0 || tech_name.length() > 128) {
			  System.out.print("Error, you did not insert a technician's full name. Please try again\n"); 
			  flag = false; 
			}
 			else {flag = true;} 
          	  }while(!flag); 
		
                  query4 +=  tech_ID + ", \'" + tech_name + "\');\n";
                  System.out.print(query4);
                  esql.executeUpdate(query4);
                }
                catch (Exception e) {
                  System.err.println(e.getMessage());
                }
	}

	public static void BookFlight(DBproject esql) {//5
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB
		try {
		  String query5 = "INSERT INTO Reservation (rnum, cid, fid, status) VALUES("; 
		  System.out.print("Enter customer's id: ");
		  String customer_id = in.readLine(); 
		  System.out.print("Enter flight's id that the customer wants to book: ");
		  String flight_id = in.readLine(); 

		//if (flight.num_sold == seats) {
		//   make customer's status as waitlisted.
		//   if not make it a reservation  
		// }
		
		} 
		catch (Exception e) {
		 System.err.println(e.getMessage()); 
		} 
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) {//6
		// For flight number and date, find the number of availalbe seats (i.e. total plane capacity minus booked seats )
		String flight_num, dept_date = ""; 
		try {
		
		   //do while loop to find flight number 
                   do{
                   	System.out.print("Enter flight num: "); 
		   	flight_num = in.readLine();
			if (flight_num.length() == 0) {
			   System.out.print("Error, you did not enter a flight number. Please try again\n"); 
			   flag = false; 
			}
			else {flag = true;}
                   }while(!flag); 
	           
		   //do-while loop to find departure date 
		   do {
			System.out.print("Enter departure_date in the form YYYY-MM-DD: "); 
   		   	dept_date = in.readLine();
		        if (dept_date.length() == 0) {
			   System.out.print("Error, you did not enter a departure date. Please try again\n"); 
			   flag = false; 
			}
			else {flag = true;}
		   }while(!flag);   
		    
		   
		   String find_available_seats = "SELECT (p.seats - f.num_sold) AS Seats_Available FROM Flight f INNER JOIN Schedule s ON s.flightNum = f.fnum INNER JOIN FlightInfo FI on FI.flight_id = f.fnum INNER JOIN Plane p ON p.id = FI.plane_id WHERE f.fnum = " + flight_num + " AND f.actual_departure_date = '" + dept_date + "'";                   
		   //int rowCount = esql.executeQueryAndPrintResult(find_seats);
		   //System.out.println ("total row(s): " + rowCount);  
	          
		   //System.out.println(find_available_seats); 
		   System.out.print("\n--------\nNumber of seats available for flight number " + flight_num + "\n"); 
		   esql.executeQueryAndPrintResult(find_available_seats); 
	           System.out.print("\n---------\n"); 
		}
		catch (Exception e) {
		 System.err.println(e.getMessage()); 
		}
	}

	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {//7
		// Count number of repairs per planes and list them in descending order
		try {
		  String query7; 
		}
		catch (Exception e) {
		 System.err.println(e.getMessage()); 
		}	
	}

	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {//8
		// Count repairs per year and list them in ascending order
		try {
		  String query8; 
		}
		catch (Exception e) {
		 System.err.println(e.getMessage()); 
		}
	}
	
	public static void FindPassengersCountWithStatus(DBproject esql) {//9
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
		try {
		  String query9; 
		}
		catch (Exception e) {
		 System.err.println(e.getMessage()); 
		}
	}
}
