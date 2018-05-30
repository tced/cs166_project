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

	public static void AddPlane(DBproject esql) {//1
		try {
		  String query = "INSERT INTO Plane (id, make, model, age, seats) VALUES("; 
 		  System.out.print("Insert the plane's ID: "); 
		  String plane_ID = in.readLine();
		  query +=  plane_ID; 
		  System.out.print("Insert the Plane's make: ");
		  String plane_make = in.readLine();
		  query += ", \'" + plane_make + "\'"; 
		  System.out.print("Insert the Plane's model: ");
		  String plane_model = in.readLine(); 
		  query += ", \'" + plane_model + "\'";  
		  System.out.print("Insert the Plane's age: "); 
		  String plane_age = in.readLine(); 
		  query += "," + plane_age; 
		  System.out.print("Insert how many seats the Plane contains: ");
		  String plane_seats = in.readLine(); 
		  query += "," + plane_seats + ")\n";
	
		  System.out.print(query);
		  esql.executeUpdate(query); 	
		}
		catch (Exception e) {
		  System.out.println("Your input is invalid!");
		}
	}

	public static void AddPilot(DBproject esql) {//2
		try {
                  String query2 = "INSERT INTO Pilot (id, fullname, nationality) VALUES(";
                  System.out.print("Insert Pilot's ID: ");
                  String pilot_ID = in.readLine();
                  query2 +=  pilot_ID;
                  System.out.print("Insert Pilot's fullname: ");
                  String pilot_name = in.readLine();
                  query2 += ", \'" + pilot_name + "\'";
                  System.out.print("Insert the Pilot's nationality: ");
                  String pilot_nationality = in.readLine();
                  query2 += ", \'" + pilot_nationality + "\');\n";

                  System.out.print(query2);
                  esql.executeUpdate(query2);
                }
                catch (Exception e) {
                  System.out.println("Your input is invalid!");
                }
	}

	public static void AddFlight(DBproject esql) {//3
		// Given a pilot, plane and flight, adds a flight in the DB
		try {
		  String query3 = "INSERT INTO Flight (fnum, cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_airport, departure_airport) VALUES("; 
 		  System.out.print("Insert the flight's fnum: "); 
		  String flight_fnum = in.readLine();
		  query3 +=  flight_fnum; 

		  System.out.print("Insert the flight's cost: ");
		  String flight_cost = in.readLine();
		  query3 += ", " + flight_cost; 
		  
		  System.out.print("Insert how many seats have been sold: ");
		  String num_seats_sold = in.readLine(); 
		  query3 += ", " + num_seats_sold;  
		  
		  System.out.print("Insert the number of stops that flight has: "); 
		  String num_flight_stops = in.readLine(); 
		  query3 += "," + num_flight_stops; 
		  
		  System.out.print("Insert the flight's actual departure date: ");
		  String plane_dept_date = in.readLine(); 
		  query3 += ", \'" + plane_dept_date + "\'";
		  
		  System.out.print("Insert the flight's actual arrival date: "); 
		  String plane_arrival_date = in.readLine(); 
		  query3 += ", \'" + plane_arrival_date + "\'"; 
		  
		  System.out.print("Insert the name of airport the flight will be arriving: "); 
		  String airport_arrival = in.readLine(); 
		  query3 += ", \'" + airport_arrival + "\'"; 
		  
		  System.out.print("Insert the name of airport the flight will be departing from: "); 
		  String airport_depart = in.readLine(); 
		  query3 += ", \'" + airport_depart + "\')\n"; 
			
	
		  System.out.print(query3);
		  esql.executeUpdate(query3); 	
		}
		catch (Exception e) {
		  System.out.println("Your input is invalid!");
		}

 	}

	public static void AddTechnician(DBproject esql) {//4
		try {
                  String query4 = "INSERT INTO Technician (id, full_name) VALUES(";
                  System.out.print("Insert Technician's ID: ");
                  String tech_ID = in.readLine();
                  query4 +=  tech_ID;
                  System.out.print("Insert Technician's fullname: ");
                  String tech_name = in.readLine();
                  query4 += ", \'" + tech_name + "\');\n";

                  System.out.print(query4);
                  esql.executeUpdate(query4);
                }
                catch (Exception e) {
                  System.out.println("Your input is invalid!");
                }
	}

	public static void BookFlight(DBproject esql) {//5
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB
		try {
		  String query5 = "INSERT INTO Reservation (rnum, cid, fid, status) VALUES("; 
		  System.out.print("Enter customer's id: ")
		  String customer_id = in.readLine(); 
		  System.out.print("Enter flight's id that the customer wants to book: ");
		  String flight_id = in.readLine(); 

		//if (flight.num_sold == seats) {
		//   make customer's status as waitlisted.
		//   if not make it a reservation  
		// }
		
		} 
		catch (Exception e) {
		  System.out.println("Your input is invalid!\n"); 
		} 
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) {//6
		// For flight number and date, find the number of availalbe seats (i.e. total plane capacity minus booked seats )

	}

	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {//7
		// Count number of repairs per planes and list them in descending order
	}

	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {//8
		// Count repairs per year and list them in ascending order
	}
	
	public static void FindPassengersCountWithStatus(DBproject esql) {//9
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
	}
}
