import java.util.Scanner;
import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
public class CSCI3170Proj {
  // Prob: insertion of new craft; lots of foreign keys to take care of.
	//public static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2312/db026";
	public static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2312/db23";
	public static String dbUsername = "Group23";
	//public static String dbPassword = "vmh5mf83";
  public static String dbPassword = "223092870";

	public static Connection connectToOracle(){
		Connection con = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
		} catch (ClassNotFoundException e){
			System.out.println("[Error]: Java MySQL DB Driver not found!!");
			System.exit(0);
		} catch (SQLException e){
			System.out.println(e);
		}
		return con;
	}

	public static void createTables(Connection mySQLDB) throws SQLException{
		String NEASQL = "CREATE TABLE NEA (";
		NEASQL += "NID VARCHAR(10) PRIMARY KEY NOT NULL,";
		NEASQL += "Distance DOUBLE PRECISION NOT NULL,";
		NEASQL += "Family VARCHAR(6) NOT NULL,";
		NEASQL += "Duration INT NOT NULL,";
		NEASQL += "Energy DOUBLE PRECISION NOT NULL,";
		NEASQL += "CHECK (Distance>0),";
		NEASQL += "CHECK (Duration BETWEEN 1 AND 999),";
		NEASQL += "CHECK (Energy>0) )";


		String Spacecraft_ModelSQL = "CREATE TABLE Spacecraft_Model(";
		Spacecraft_ModelSQL += "Agency VARCHAR(4) NOT NULL,";
		Spacecraft_ModelSQL += "MID  VARCHAR(4) NOT NULL,";
		Spacecraft_ModelSQL += "Num INT NOT NULL,";
		Spacecraft_ModelSQL += "Charge INT NOT NULL,";
    Spacecraft_ModelSQL += "Duration INT NOT NULL,";
		Spacecraft_ModelSQL += "Energy DOUBLE PRECISION NOT NULL,";
		Spacecraft_ModelSQL += "PRIMARY KEY(Agency, MID),";
		Spacecraft_ModelSQL += "CHECK (Num BETWEEN 1 AND 99),";
		Spacecraft_ModelSQL += "CHECK (Charge BETWEEN 1 AND 99999),";
		Spacecraft_ModelSQL += "CHECK (Duration BETWEEN 1 AND 999),";
		Spacecraft_ModelSQL += "CHECK (Energy>0) )";

		String A_ModelSQL = "CREATE TABLE A_Model(";
		A_ModelSQL += "Agency VARCHAR(4),";
		A_ModelSQL += "MID  VARCHAR(4),";
		A_ModelSQL += "Num INT NOT NULL,";
		A_ModelSQL += "Charge INT NOT NULL,";
    A_ModelSQL += "Duration INT NOT NULL,";
		A_ModelSQL += "Energy DOUBLE PRECISION NOT NULL,";
		A_ModelSQL += "Capacity INT NOT NULL,";
		A_ModelSQL += "PRIMARY KEY (Agency, MID),";
    A_ModelSQL += "FOREIGN KEY (Agency, MID) REFERENCES Spacecraft_Model (Agency, MID),";//can add more foreign key constr
		//A_ModelSQL += "FOREIGN KEY (MID) REFERENCES Spacecraft_Model(MID),";
		A_ModelSQL += "CHECK (Capacity BETWEEN 1 AND 99) )";
		/*A_ModelSQL += "CHECK (Num BETWEEN 1 AND 99),";
		A_ModelSQL += "CHECK (Charge BETWEEN 1 AND 99999),";
		A_ModelSQL += "CHECK (Duration BETWEEN 1 AND 999),";
		A_ModelSQL += "CHECK (Energy>0) )";*/

		String ResourceSQL = "CREATE TABLE Resource(";
		ResourceSQL += "Rtype VARCHAR(2) PRIMARY KEY NOT NULL,";
		ResourceSQL += "Density DOUBLE PRECISION NOT NULL,";
		ResourceSQL += "Value DOUBLE PRECISION NOT NULL,";
		ResourceSQL += "CHECK (Density>0),";
		ResourceSQL += "CHECK (Value>0) )";

		String RentalRecordSQL = "CREATE TABLE RentalRecord(";
		RentalRecordSQL += "Agency VARCHAR(4),";
		RentalRecordSQL += "MID  VARCHAR(4),";
		RentalRecordSQL += "SNum INT NOT NULL,";
		RentalRecordSQL += "CheckoutDate DATE,";
		RentalRecordSQL += "ReturnDate DATE,";
		RentalRecordSQL += "PRIMARY KEY (Agency, MID, SNum),";
		RentalRecordSQL += "FOREIGN KEY (Agency, MID) REFERENCES Spacecraft_Model (Agency, MID),";
		RentalRecordSQL += "CHECK (SNum BETWEEN 1 AND 99) )";

		String ContainSQL = "CREATE TABLE Contain (";
		ContainSQL += "NID VARCHAR(10) NOT NULL,";
		ContainSQL += "Rtype VARCHAR(2) NOT NULL,";
                ContainSQL += "PRIMARY KEY (NID),";
		ContainSQL += "FOREIGN KEY (NID) REFERENCES NEA(NID), ";
		ContainSQL += "FOREIGN KEY (Rtype) REFERENCES Resource(Rtype) )";


    /*
		manufacturerSQL += "m_name VARCHAR(20) NOT NULL,";
		manufacturerSQL += "m_addr VARCHAR(50) NOT NULL,";
		manufacturerSQL += "m_phone INT NOT NULL,";
		manufacturerSQL += "CHECK (m_id BETWEEN 1 AND 99),";
		manufacturerSQL += "CHECK (m_phone BETWEEN 10000000 AND 99999999))";

		String salespersonSQL = "CREATE TABLE salesperson (";
		salespersonSQL += "s_id INT PRIMARY KEY NOT NULL,";
		salespersonSQL += "s_name VARCHAR(20) NOT NULL,";
		salespersonSQL += "s_addr VARCHAR(50) NOT NULL,";
		salespersonSQL += "s_phone INT NOT NULL,";
		salespersonSQL += "s_experience INT NOT NULL,";
		salespersonSQL += "CHECK (s_id BETWEEN 1 AND 99),";
		salespersonSQL += "CHECK (s_phone BETWEEN 10000000 AND 99999999),";
		salespersonSQL += "CHECK (s_experience BETWEEN 1 AND 9))";

		String partSQL = "CREATE TABLE part (";
		partSQL += "p_id INT PRIMARY KEY NOT NULL,";
		partSQL += "p_name VARCHAR(20) NOT NULL,";
		partSQL += "p_price INT NOT NULL,";
		partSQL += "m_id INT NOT NULL,";
		partSQL += "c_id INT NOT NULL,";
		partSQL += "p_quantity INT NOT NULL,";
		partSQL += "p_warranty INT NOT NULL,";
		partSQL += "FOREIGN KEY (m_id) REFERENCES manufacturer(m_id),";
		partSQL += "FOREIGN KEY (c_id) REFERENCES category(c_id),";
		partSQL += "CHECK (p_id BETWEEN 1 AND 999),";
		partSQL += "CHECK (p_price BETWEEN 1 AND 99999),";
		partSQL += "CHECK (p_warranty BETWEEN 1 AND 99),";
		partSQL += "CHECK (p_quantity BETWEEN 0 AND 99))";

		String transactionSQL = "CREATE TABLE transaction (";
		transactionSQL += "t_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,";
		transactionSQL += "p_id INT NOT NULL,";
		transactionSQL += "s_id INT NOT NULL,";
		transactionSQL += "t_date DATE NOT NULL,";
		transactionSQL += "FOREIGN KEY (p_id) REFERENCES part(p_id),";
		transactionSQL += "FOREIGN KEY (s_id) REFERENCES salesperson(s_id),";
		transactionSQL += "CHECK (t_id BETWEEN 1 AND 9999))";
    */
		Statement stmt  = mySQLDB.createStatement();
		System.out.print("Processing...");

		//System.err.println("Creating NEA Table.");
		stmt.execute(NEASQL);

		//System.err.println("Creating Spacecraft Table.");
		stmt.execute(Spacecraft_ModelSQL);

		//System.err.println("Creating A_Model Table.");
		stmt.execute(A_ModelSQL);

		//System.err.println("Creating Resource Table.");
		stmt.execute(ResourceSQL);

		//System.err.println("Creating RentalRecord Table.");
		stmt.execute(RentalRecordSQL);

		//System.err.println("Creating Contain Table.");
		stmt.execute(ContainSQL);

		System.out.println("Done! Database is initialized!");
		stmt.close();
	}

	public static void deleteTables(Connection mySQLDB) throws SQLException{
		Statement stmt  = mySQLDB.createStatement();
		System.out.print("Processing...");
		stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
		stmt.execute("DROP TABLE IF EXISTS NEA");
		stmt.execute("DROP TABLE IF EXISTS Spacecraft_Model");
		stmt.execute("DROP TABLE IF EXISTS A_Model");
		stmt.execute("DROP TABLE IF EXISTS Resource");
		stmt.execute("DROP TABLE IF EXISTS RentalRecord");
		stmt.execute("DROP TABLE IF EXISTS Contain");
		stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
		System.out.println("Done! Database is removed!");
		stmt.close();
	}

	public static void loadTables(Scanner menuAns, Connection mySQLDB) throws SQLException{

		String NEASQL = "INSERT INTO NEA (NID, Distance, Family, Duration, Energy) VALUES (?,?,?,?,?)";
		String Spacecraft_ModelSQL = "INSERT INTO Spacecraft_Model(Agency, MID, Num, Charge, Duration, Energy) VALUES(?,?,?,?,?,?)";
		String A_ModelSQL = "INSERT INTO A_Model(Agency, MID, Num, Charge, Duration, Energy, Capacity) VALUES(?,?,?,?,?,?,?)";
		String ResourceSQL = "INSERT INTO Resource(Rtype, Density, Value) VALUES(?,?,?)";
		String RentalRecordSQL = "INSERT INTO RentalRecord(Agency, MID, SNum, CheckoutDate, ReturnDate) VALUES(?,?,?,?,?)";
    String ContainSQL = "INSERT INTO Contain(NID, Rtype) VALUES(?,?)";
		/*
		String categorySQL = "INSERT INTO category (c_id, c_name) VALUES (?,?)";
		String manufacturerSQL = "INSERT INTO manufacturer (m_id, m_name, m_addr, m_phone) VALUES (?,?,?,?)";
		String partSQL = "INSERT INTO part (p_id, p_name, p_price, m_id, c_id, p_warranty, p_quantity) VALUES (?,?,?,?,?,?,?)";
		String salespersonSQL = "INSERT INTO salesperson (s_id, s_name, s_addr, s_phone, s_experience) VALUES (?,?,?,?,?)";
		String transactionSQL = "INSERT INTO transaction (t_id, p_id, s_id, t_date) VALUES (?,?,?,STR_TO_DATE(?,'%d/%m/%Y'))";
    */
		String filePath = "";
		String targetTable = "";

		DatabaseMetaData dbm = mySQLDB.getMetaData();
    ResultSet tables = dbm.getTables(null, null, "NEA", null);
    if (!tables.next()) {
      System.out.println("The tables have not been created yet. Create the table first!");
			return;
    }
		while(true){
			System.out.println("");
			System.out.print("Type in the Source Data Folder Path: ");
			filePath = menuAns.nextLine();
			if((new File(filePath)).isDirectory()) break;
                        System.out.println("Invalid folder path. Try again.");
		}

		System.out.print("Processing...");

		//System.err.println("Loading Resource");
		try{
			PreparedStatement stmt = mySQLDB.prepareStatement(ResourceSQL);
			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/Resources Details.txt"));

			line = dataReader.readLine();
			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				stmt.setString( 1, dataFields[0] );
				stmt.setDouble( 2, Double.parseDouble(dataFields[1]) );
				stmt.setDouble( 3, Double.parseDouble(dataFields[2]) );
				stmt.addBatch();
			}
			stmt.executeBatch();
			stmt.close();
		}catch (Exception e){
			System.out.println(e);
		}

		//System.err.println("Loading NEA and Contain");
		try{
			PreparedStatement NEA_stmt = mySQLDB.prepareStatement(NEASQL);
			PreparedStatement Contain_stmt = mySQLDB.prepareStatement(ContainSQL);
			boolean NEA_Empty = true;
			boolean Contain_Empty = true;

			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/Near-Earth Asteroids.txt"));

			line = dataReader.readLine();
			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				NEA_stmt.setString( 1, dataFields[0] );
				NEA_stmt.setDouble( 2, Double.parseDouble(dataFields[1]) );
				NEA_stmt.setString( 3, dataFields[2] );
				NEA_stmt.setInt( 4, Integer.parseInt(dataFields[3]) );
				NEA_stmt.setDouble( 5, Double.parseDouble(dataFields[4]) );
				NEA_stmt.addBatch();
				NEA_Empty = false;
				if(!dataFields[5].equals("null")){
					Contain_stmt.setString( 1, dataFields[0] );
					Contain_stmt.setString( 2, dataFields[5] );
					Contain_stmt.addBatch();
					Contain_Empty = false;
				}
			}
			if (!NEA_Empty){
			  NEA_stmt.executeBatch();
			}
			if (!Contain_Empty){
			  Contain_stmt.executeBatch();
			}
			NEA_stmt.close();
			Contain_stmt.close();
		}catch (Exception e){
			System.out.println(e);
		}

		//System.err.println("Loading Spacecraft_Model and A_Model");
		try{
			PreparedStatement Spacecraft_Model_stmt = mySQLDB.prepareStatement(Spacecraft_ModelSQL);
			PreparedStatement A_Model_stmt = mySQLDB.prepareStatement(A_ModelSQL);// to be continued
			boolean Spacecraft_Model_Empty = true;
			boolean A_Model_Empty = true;

			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/Space Agencies' Spacecrafts.txt"));

			line = dataReader.readLine();
			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				Spacecraft_Model_stmt.setString( 1, dataFields[0] );
				Spacecraft_Model_stmt.setString( 2, dataFields[1] );
				Spacecraft_Model_stmt.setInt( 3, Integer.parseInt(dataFields[2]) );
				Spacecraft_Model_stmt.setInt( 4, Integer.parseInt(dataFields[7]) );
				Spacecraft_Model_stmt.setInt( 5, Integer.parseInt(dataFields[5]) );
				Spacecraft_Model_stmt.setDouble( 6, Double.parseDouble(dataFields[4]) );
				Spacecraft_Model_stmt.addBatch();
				Spacecraft_Model_Empty = false;
				if(!dataFields[6].equals("null")){
					A_Model_stmt.setString( 1, dataFields[0] );
					A_Model_stmt.setString( 2, dataFields[1] );
					A_Model_stmt.setInt( 3, Integer.parseInt(dataFields[2]) );
					A_Model_stmt.setInt( 4, Integer.parseInt(dataFields[7]) );
				  A_Model_stmt.setInt( 5, Integer.parseInt(dataFields[5]) );
					A_Model_stmt.setDouble( 6, Double.parseDouble(dataFields[4]) );
					A_Model_stmt.setInt( 7, Integer.parseInt(dataFields[6]) );
					A_Model_stmt.addBatch();
					A_Model_Empty = false;
				}
			}
			if (!Spacecraft_Model_Empty){
			  Spacecraft_Model_stmt.executeBatch();
			}
			if (!A_Model_Empty){
			  A_Model_stmt.executeBatch();
			}
			Spacecraft_Model_stmt.close();
			A_Model_stmt.close();
		}catch (Exception e){
			System.out.println(e);
		}

		//System.err.println("Loading RentalRecord");
		try{
			PreparedStatement stmt = mySQLDB.prepareStatement(RentalRecordSQL);
			//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			String line = null;
                        java.util.Date utilDate = null;
                        java.sql.Date sqlDate = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/Spacecraft Rental Records.txt"));

			line = dataReader.readLine();
			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				stmt.setString( 1, dataFields[0] );
				stmt.setString( 2, dataFields[1] );
				stmt.setInt( 3, Integer.parseInt(dataFields[2]) );
				utilDate = formatter.parse(dataFields[3]);
                                sqlDate = new java.sql.Date(utilDate.getTime());
				stmt.setDate( 4, sqlDate );
                                utilDate = null;
                                sqlDate = null;
				if(dataFields[4].equals("null")){
					stmt.setNull( 5, java.sql.Types.DATE );
				}else{
                                        utilDate = formatter.parse(dataFields[4]);
                                        sqlDate = new java.sql.Date(utilDate.getTime());
					stmt.setDate( 5, sqlDate );
                                        sqlDate = null;
                                        utilDate = null;
				}
				stmt.addBatch();
			}
			stmt.executeBatch();//Perhaps I should check for empty input here.
			stmt.close();
		}catch (Exception e){
			System.out.println(e);
		}


		/*
		//System.err.println("Loading Category");
		try{
			PreparedStatement stmt = mySQLDB.prepareStatement(categorySQL);
			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/category.txt"));

			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				stmt.setInt(1, Integer.parseInt(dataFields[0]));
				stmt.setString(2, dataFields[1]);
				stmt.addBatch();
			}
			stmt.executeBatch();
			stmt.close();
		}catch (Exception e){
			System.out.println(e);
		}

		//System.err.println("Loading Manufacturer");
		try{
			PreparedStatement stmt = mySQLDB.prepareStatement(manufacturerSQL);
			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/manufacturer.txt"));

			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				stmt.setInt(1, Integer.parseInt(dataFields[0]));
				stmt.setString(2, dataFields[1]);
				stmt.setString(3, dataFields[2]);
				stmt.setInt(4, Integer.parseInt(dataFields[3]));
				stmt.addBatch();
			}
			stmt.executeBatch();
			stmt.close();
		}catch (Exception e){
			System.out.println(e);
		}

		//System.err.println("Loading Part");
		try{
			PreparedStatement stmt = mySQLDB.prepareStatement(partSQL);

			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/part.txt"));

			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				stmt.setInt(1, Integer.parseInt(dataFields[0]));
				stmt.setString(2, dataFields[1]);
				stmt.setInt(3, Integer.parseInt(dataFields[2]));
				stmt.setInt(4, Integer.parseInt(dataFields[3]));
				stmt.setInt(5, Integer.parseInt(dataFields[4]));
				stmt.setInt(6, Integer.parseInt(dataFields[5]));
				stmt.setInt(7, Integer.parseInt(dataFields[6]));
				stmt.addBatch();
			}

			stmt.executeBatch();
			stmt.close();
		}catch (Exception e){
			System.out.println(e);
		}

		//System.err.println("Loading Salesperson");
		try{
			PreparedStatement stmt = mySQLDB.prepareStatement(salespersonSQL);
			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/salesperson.txt"));

			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				stmt.setInt(1, Integer.parseInt(dataFields[0]));
				stmt.setString(2, dataFields[1]);
				stmt.setString(3, dataFields[2]);
				stmt.setInt(4, Integer.parseInt(dataFields[3]));
				stmt.setInt(5, Integer.parseInt(dataFields[4]));
				stmt.addBatch();
			}
			stmt.executeBatch();
			stmt.close();
		}catch (Exception e){
			System.out.println(e);
		}

		//System.err.println("Loading Transaction");
		try{
			PreparedStatement stmt = mySQLDB.prepareStatement(transactionSQL);
			String line = null;
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/transaction.txt"));

			while ((line = dataReader.readLine()) != null) {
				String[] dataFields = line.split("\t");
				stmt.setInt(1, Integer.parseInt(dataFields[0]));
				stmt.setInt(2, Integer.parseInt(dataFields[1]));
				stmt.setInt(3, Integer.parseInt(dataFields[2]));
				stmt.setString(4, dataFields[3]);

				//System.err.println("Record: " + i);
				stmt.addBatch();
				//i++;
			}
			stmt.executeBatch();
			stmt.close();
		}catch (Exception e){
			System.out.println(e);
		}
		*/

		System.out.println("Data successfully loaded!");
	}

  public static void printNEA(Scanner menuAns, Connection mySQLDB) throws SQLException{

	  Statement stmt  = mySQLDB.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM NEA");
//    for (int i=1;i<=5;i++)
 //   { System.out.print(rs.getString(i)+" ");}
   // System.out.println();
    while(rs.next()){
      for (int i=1; i<=5;i++){
        System.out.print(rs.getString(i)+" ");
      }
      System.out.println();
    }
		rs.close();
		stmt.close();
  }

	public static void printRentalRecord(Scanner menuAns, Connection mySQLDB) throws SQLException{

		Statement stmt  = mySQLDB.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM RentalRecord");
		while(rs.next()){
      for (int i=1; i<=5;i++){
        System.out.print(rs.getString(i)+" ");
      }
      System.out.println();
    }
		rs.close();
		stmt.close();
	}

	public static void printA_Model(Scanner menuAns, Connection mySQLDB) throws SQLException{
		Statement stmt  = mySQLDB.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM A_Model");
		while(rs.next()){
			for (int i=1; i<=7;i++){
				System.out.print(rs.getString(i)+" ");
			}
			System.out.println();
		}
		rs.close();
		stmt.close();
	}

 public static void printSpacecraft_Model(Scanner menuAns, Connection mySQLDB) throws SQLException{
		Statement stmt  = mySQLDB.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM Spacecraft_Model");
		while(rs.next()){
			for (int i=1; i<=6;i++){
				System.out.print(rs.getString(i)+" ");
			}
			System.out.println();
		}
		rs.close();
		stmt.close();
	}

	public static void printResource(Scanner menuAns, Connection mySQLDB) throws SQLException{
		Statement stmt  = mySQLDB.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM Resource");
		while(rs.next()){
			for (int i=1; i<=3;i++){
				System.out.print(rs.getString(i)+" ");
			}
			System.out.println();
		}
		rs.close();
		stmt.close();
	}

	public static void printContain(Scanner menuAns, Connection mySQLDB) throws SQLException{
		Statement stmt  = mySQLDB.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM Contain");
		while(rs.next()){
			for (int i=1; i<=2;i++){
				System.out.print(rs.getString(i)+" ");
			}
			System.out.println();
		}
		rs.close();
		stmt.close();
	}

	public static void showTables(Scanner menuAns, Connection mySQLDB) throws SQLException{
		String[] table_name = {"NEA", "Contain", "Spacecraft_Model", "A_Model", "Resource", "RentalRecord"};

		DatabaseMetaData dbm = mySQLDB.getMetaData();
    ResultSet tables = dbm.getTables(null, null, "NEA", null);
    if (!tables.next()) {
      System.out.println("The tables have not been created yet. Create the table first!");
			return;
    }

		System.out.println("Number of records in each table:");
		for (int i = 0; i < 6; i++){
			Statement stmt  = mySQLDB.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM "+table_name[i]);
			rs.next();
			System.out.println(table_name[i]+": "+rs.getString(1));
			rs.close();
			stmt.close();
		}
                printRentalRecord(menuAns, mySQLDB);
	}


	public static void adminMenu(Scanner menuAns, Connection mySQLDB) throws SQLException{
		String answer = null;

		while(true){
			System.out.println();
			System.out.println("-----Operations for administrator menu-----");
			System.out.println("What kinds of operation would you like to perform?");
			System.out.println("1. Create all tables");
			System.out.println("2. Delete all tables");
			System.out.println("3. Load data from a dataset");
			System.out.println("4. Show number of records in each table");
			System.out.println("0. Return to the main menu");
			System.out.print("Enter Your Choice: ");
			answer = menuAns.nextLine();

			if(answer.equals("1")||answer.equals("2")||answer.equals("3")||answer.equals("4")||answer.equals("0"))
				break;
			System.out.println("[Error]: Wrong Input, Type in again!!!");
		}

		if(answer.equals("1")){
			createTables(mySQLDB);
		}else if(answer.equals("2")){
			deleteTables(mySQLDB);
		}else if(answer.equals("3")){
			loadTables(menuAns, mySQLDB);
		}else if(answer.equals("4")){
			showTables(menuAns, mySQLDB);
		}else if(answer.equals("0")){
                        return;
                }
                adminMenu(menuAns,mySQLDB);
	}

	public static void searchParts(Scanner menuAns, Connection mySQLDB) throws SQLException{
		String ans = null, keyword = null, method = null, ordering = null;
		String searchSQL = "";
		PreparedStatement stmt = null;

		searchSQL += "SELECT P.p_id, P.p_name, M.m_name, C.c_name, P.p_quantity, P.p_warranty, P.p_price ";
		searchSQL += "FROM part P, manufacturer M, category C ";
		searchSQL += "WHERE P.m_id = M.m_id AND P.c_id = C.c_id ";

		while(true){
			System.out.println("Choose the Search criterion:");
			System.out.println("1. Part Name");
			System.out.println("2. Manufacturer Name");
			System.out.print("Choose the search criterion: ");
			ans = menuAns.nextLine();
			if(ans.equals("1")||ans.equals("2")) break;
		}
		method = ans;

		while(true){
			System.out.print("Type in the Search Keyword:");
			ans = menuAns.nextLine();
			if(!ans.isEmpty()) break;
		}
		keyword = ans;

		while(true){
			System.out.println("Choose ordering:");
			System.out.println("1. By price, ascending order");
			System.out.println("2. By price, descending order");
			System.out.print("Choose the search criterion: ");
			ans = menuAns.nextLine();
			if(ans.equals("1")||ans.equals("2")) break;
		}
		ordering = ans;

		if(method.equals("1")){
			searchSQL += " AND P.p_name LIKE ? ";
		}else if(method.equals("2")){
			searchSQL += " AND M.m_name LIKE ? ";
		}

		if(ordering.equals("1")){
			searchSQL += " ORDER BY P.p_price ASC";
		}else if(ordering.equals("2")){
			searchSQL += " ORDER BY P.p_price DESC";
		}

		stmt = mySQLDB.prepareStatement(searchSQL);
		stmt.setString(1, "%" + keyword + "%");

		String[] field_name = {"ID", "Name", "Manufacturer", "Category", "Quantity", "Warranty", "Price"};
		for (int i = 0; i < 7; i++){
			 System.out.print("| " + field_name[i] + " ");
		}
		System.out.println("|");

		ResultSet resultSet = stmt.executeQuery();
		while(resultSet.next()){
			for (int i = 1; i <= 7; i++){
				System.out.print("| " + resultSet.getString(i) + " ");
			}
			System.out.println("|");
		}
		System.out.println("End of Query");
		resultSet.close();
		stmt.close();
	}

	public static void sellProducts(Scanner menuAns, Connection mySQLDB) throws SQLException{
		String updateProductSQL = "UPDATE part set p_quantity = p_quantity - 1 WHERE p_id = ? and p_quantity > 0";
		String insertRecordSQL = "INSERT INTO transaction VALUES (NULL, ?, ?, CURDATE())";
		String remainQuantitySQL = "SELECT p_id, p_name, p_quantity FROM part WHERE p_id = ?";

		String p_id = null, s_id = null;

		while(true){
			System.out.print("Enter The Part ID: ");
			p_id = menuAns.nextLine();
			if(!p_id.isEmpty()) break;
		}

		while(true){
			System.out.print("Enter The Salesperson ID: ");
			s_id = menuAns.nextLine();
			if(!s_id.isEmpty()) break;
		}

		PreparedStatement stmt = mySQLDB.prepareStatement(updateProductSQL);
		stmt.setString(1, p_id);

		int retVal = stmt.executeUpdate();
		if(retVal == 0){
			System.err.println("[Error]: This Product is currently out of stock");
			return;
		}
		stmt.close();

		PreparedStatement stmt2 = mySQLDB.prepareStatement(insertRecordSQL);
		stmt2.setString(1, p_id);
		stmt2.setString(2, s_id);
		stmt2.executeUpdate();
		stmt2.close();

		PreparedStatement stmt3 = mySQLDB.prepareStatement(remainQuantitySQL);
		stmt3.setString(1, p_id);
		ResultSet resultSet = stmt3.executeQuery();
		resultSet.next();
		System.out.println("Product: "+ resultSet.getString(2) + "(id: " + resultSet.getString(1) + ") Remaining Quality: " + resultSet.getString(3));

		stmt3.close();
	}

	public static void staffMenu(Scanner menuAns, Connection mySQLDB) throws SQLException{
		String answer = "";

		while(true){
			System.out.println();
			System.out.println("-----Operations for salesperson menu-----");
			System.out.println("What kinds of operation would you like to perform?");
			System.out.println("1. Search for parts");
			System.out.println("2. Sell a part");
			System.out.println("3. Return to the main menu");
			System.out.print("Enter Your Choice: ");
			answer = menuAns.nextLine();

			if(answer.equals("1")||answer.equals("2")||answer.equals("3"))
				break;
			System.out.println("[Error]: Wrong Input, Type in again!!!");
		}

		if(answer.equals("1")){
			searchParts(menuAns, mySQLDB);
		}else if(answer.equals("2")){
			sellProducts(menuAns, mySQLDB);
		}
	}

	public static void countSalespersonRecord(Scanner menuAns, Connection mySQLDB) throws SQLException{
		String recordSQL = "SELECT S.s_id, S.s_name, S.s_experience, COUNT(T.t_id) ";
		recordSQL += "FROM transaction T, salesperson S ";
		recordSQL += "WHERE T.s_id = S.s_id AND S.s_experience >= ? AND S.s_experience <= ? ";
		recordSQL += "GROUP BY S.s_id, S.s_name, S.s_experience ";
		recordSQL += "ORDER BY S.s_id DESC";

		String expBegin = null, expEnd = null;

		while(true){
			System.out.print("Type in the lower bound for years of experience: ");
			expBegin = menuAns.nextLine();
			if(!expBegin.isEmpty()) break;
		}

		while(true){
			System.out.print("Type in the upper bound for years of experience: ");
			expEnd = menuAns.nextLine();
			if(!expEnd.isEmpty()) break;
		}

		PreparedStatement stmt  = mySQLDB.prepareStatement(recordSQL);
		stmt.setInt(1, Integer.parseInt(expBegin));
		stmt.setInt(2, Integer.parseInt(expEnd));

		ResultSet resultSet = stmt.executeQuery();

		System.out.println("Transaction Record:");

		System.out.println("| ID | Name | Years of Experience | Number of Transaction |");
		while(resultSet.next()){
			for (int i = 1; i <= 4; i++){
				System.out.print("| " + resultSet.getString(i) + " ");
			}
			System.out.println("|");
		}
		System.out.println("End of Query");
	}

	public static void showPopularPart(Scanner menuAns, Connection mySQLDB) throws SQLException{
		String ans;
		int booknum = 0, i = 0;
		String sql = "SELECT P.p_id, P.p_name, count(*) "+
					 "FROM part P, transaction T "+
					 "WHERE P.p_id = T.p_id "+
					 "GROUP BY P.p_id, P.p_name "+
					 "ORDER BY count(*) DESC";

		while(true){
			System.out.print("Type in the number of parts: ");
			ans = menuAns.nextLine();
			if(!ans.isEmpty()) break;
		}

		booknum = Integer.parseInt(ans);
		Statement stmt  = mySQLDB.createStatement();
		ResultSet resultSet = stmt.executeQuery(sql);
		System.out.println("| Part ID | Part Name | No. of Transaction |");
		while(resultSet.next() && i < booknum){
			System.out.println( "| " + resultSet.getString(1) + " " +
								"| " + resultSet.getString(2) + " " +
								"| " + resultSet.getString(3) + " " +
								"|");
			i++;
		}
		System.out.println("End of Query");
		stmt.close();
	}

	public static void showTotalSales(Scanner menuAns, Connection mySQLDB) throws SQLException{
		String sql = "SELECT M.m_id, M.m_name, SUM(P.p_price) as total_sum "+
					 "FROM transaction T, part P, manufacturer M " +
					 "WHERE T.p_id = P.p_id AND P.m_id = M.m_id " +
					 "GROUP BY M.m_id, M.m_name " +
					 "ORDER by total_sum DESC";

		Statement stmt  = mySQLDB.createStatement();
		ResultSet resultSet = stmt.executeQuery(sql);

		System.out.println("| Manufacturer ID | Manufacturer Name | Total Sales Value |");
		while(resultSet.next()){
			System.out.println(	"| " + resultSet.getString(1) + " " +
								"| " + resultSet.getString(2) + " " +
								"| " + resultSet.getString(3) + " " +
								"|");
		}
		System.out.println("End of Query");
		stmt.close();
	}

	public static void managerMenu(Scanner menuAns, Connection mySQLDB) throws SQLException{
		String answer = "";

		while(true){
			System.out.println();
			System.out.println("-----Operations for manager menu-----");
			System.out.println("What kinds of operation would you like to perform?");
			System.out.println("1. Count the no. of sales record of each salesperson under a specific range on years of experience");
			System.out.println("2. Show the total sales value of each manufacturer");
			System.out.println("3. Show the N most popular part");
			System.out.println("4. Return to the main menu");
			System.out.print("Enter Your Choice: ");
			answer = menuAns.nextLine();

			if(answer.equals("1")||answer.equals("2")||answer.equals("3")||answer.equals("4"))
				break;
			System.out.println("[Error]: Wrong Input, Type in again!!!");
		}

		if(answer.equals("1")){
			countSalespersonRecord(menuAns, mySQLDB);
		}else if(answer.equals("2")){
			showTotalSales(menuAns, mySQLDB);
		}else if(answer.equals("3")){
			showPopularPart(menuAns, mySQLDB);
		}
	}

	public static void main(String[] args) {
		Scanner menuAns = new Scanner(System.in);

		while(true){
			try{
				Connection mySQLDB = connectToOracle();
				System.out.println();
				System.out.println("-----Main menu-----");
				System.out.println("What kinds of operation would you like to perform?");
				System.out.println("1. Operations for administrator");
				System.out.println("2. Operations for exploration companies (rential customers)");
				System.out.println("3. Operations for spacecraft rental staff");
				System.out.println("0. Exit this program");
				System.out.print("Enter Your Choice: ");

				String answer = menuAns.nextLine();

				if(answer.equals("1")){
					adminMenu(menuAns, mySQLDB);
				}else if(answer.equals("2")){
					staffMenu(menuAns, mySQLDB);
				}else if(answer.equals("3")){
					managerMenu(menuAns, mySQLDB);
				}else if(answer.equals("0")){
					break;
				}else{
					System.out.println("[Error]: Wrong Input, Type in again!!!");
				}
			}catch (SQLException e){
				System.out.println(e);
			}
		}

		menuAns.close();
		System.exit(0);
	}
}
