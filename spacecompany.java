import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class SpaceCompany {

    public static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2312/db23";
    public static String dbUsername = "Group23";
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
    public static int companyMenu(Scanner menuAns, Connection mySQLDB)throws SQLException{
        String answer = null;
        while(true){
		System.out.println();
		System.out.println("-----Operations for exploration companies(rental customers)-----");
		System.out.println("1. Search for NEAs based on some critera");
		System.out.println("2. Search for spacecrafts based on some critera");
		System.out.println("3. A certain NEA exploration mission design");
		System.out.println("4. Show number of records in each table");
        System.out.println("0. Return to the main menu");
		System.out.print("Enter Your Choice: ");
		answer = menuAns.nextLine();
		if(answer.equals("1")||answer.equals("2")||answer.equals("3")||answer.equals("4")||answer.equals("5"))
			break;
		System.out.println("[Error]: Wrong Input, Type in again!!!");
		}
        if(answer.equals("1")){
                NEAsearch(menuAns,mySQLDB);
		}else if(answer.equals("2")){
				spacecraftSearch(menuAns,mySQLDB);	
		}else if(answer.equals("3")){
				missionDesign(menuAns,mySQLDB);	
		}else if(answer.equals("4")){
			beneficialDesign(menuAns,mySQLDB);	
		}else if(answer.equals("5")){
			return 1;//go back to main menu(not in this file)
		}
		return 0;	
        
    }
    
    public static void NEAsearch(Scanner menuAns, Connection mySQLDB) throws SQLException{
	//this part 5.2.1 is finished
        String ans = null, keyword = null, method = null;
		String searchSQL = "";
		PreparedStatement stmt = null;
		searchSQL += "SELECT Contain.NID,Distance,Family,Duration,Energy,Resource.Resources ";
		//nea: nid, distance, family, (condition): duration, energy, resource:resources type contain: type
		searchSQL += "FROM NEA,Contains,Resource ";
		searchSQL += "WHERE Contain.NID = NEA.NID AND Contains.Rtype = Resource.Rtype ";
		//searchSQL += "WHERE P.m_id = M.m_id AND P.c_id = C.c_id ";
		
        while(true){
		System.out.println("Choose the Search criterion:");
		System.out.println("1. ID");
		System.out.println("2. Family");
                System.out.println("3. Resource type");
                System.out.print("My criterion: ");
		ans = menuAns.nextLine();
		if(ans.equals("1")||ans.equals("2")||ans.equals("3")) break;
	}
		method = ans;
        while(true){
                System.out.print("Type in the Search Keyword:");
                ans = menuAns.nextLine();
                if(!ans.isEmpty()) break;
        }
        keyword = ans;
		
		//like 在where里面搜索列，%代表通配符，用于部分匹配。
        if(method.equals("1")){
			searchSQL += "AND NID LIKE ? ";//exact matching
			stmt = mySQLDB.prepareStatement(searchSQL);
			stmt.setString(1, keyword);//第一个参数n 定义了字符串中第n个”?“字符的替换。
		}else if(method.equals("2")){
			searchSQL += "AND Family LIKE ? ";
			stmt = mySQLDB.prepareStatement(searchSQL);
			stmt.setString(1, "%" + keyword + "%");//partial matching
		}else if(method.equals("3")){
			searchSQL += "AND Resource LIKE ? ";
			stmt = mySQLDB.prepareStatement(searchSQL);
			stmt.setString(1, "%" + keyword + "%");//partial matching
		}
		
		
        String[] field_name = {"ID","Distance","Family", "Duration", "Energy","Resources"};
		for (int i = 0; i < 6; i++){
			 System.out.print("| " + field_name[i] + " ");
		}
		System.out.println("|");

		ResultSet resultSet = stmt.executeQuery();
		while(resultSet.next()){
			for (int i = 1; i <= 6; i++){
				System.out.print("| " + resultSet.getString(i) + " ");
			}    
			System.out.println("|");
		}
		System.out.println("End of Query");
		resultSet.close();
		stmt.close();
        
        
    }
    
    public static void spacecraftSearch(Scanner menuAns, Connection mySQLDB)throws SQLException{
	//this part for 5.2.2 finished
        String ans = null, keyword = null, method = null;
		String searchSQL = "";
		PreparedStatement stmt = null;
		
		//Spacecraft_Model:agency mid s num duration energy charge  A_Model:capacity  type就是填写A或者E
		searchSQL += "SELECT Spacecraft_Model.Agency, Spacecraft_Model.MID, RentalRecord.SNum, Spacecraft_Model.Energy, A_Model.Capacity, Spacecraft_Model.Charge, ";
		searchSQL += "(case  when Spacecraft_Model.MID = A_Model.MID then  'A' else  'E' END) Type ";//Snum 是因为最后要输出snum而不是num,什么鬼
		searchSQL += "FROM Spacecraft_Model LEFT JOIN A_Model ON Spacecraft_Model.MID = A_Model.MID JOIN RentalRecord ON Spacecraft_Model.MID = RentalRecord.MID";//left join
		// JOIN RentalRecord ON Spacecraft_Model.MID = RentalRecord.MID??? 
		
        while(true){
			System.out.println("Choose the Search criterion:");
			System.out.println("1. Agency name [km/s]");
			System.out.println("2. Type");//A?E? IS A_MODEL OR NOT
			System.out.println("3. Least energy");//
			System.out.println("4. Least working time [days]");
			System.out.println("5. Least capacity [m^3]");
			System.out.print("My criterion: ");
			ans = menuAns.nextLine();
			if(ans.equals("1")||ans.equals("2")||ans.equals("3")||ans.equals("4")||ans.equals("5")) break;
		}
		method = ans;
        while(true){
                System.out.print("Type in the Search Keyword:");
                ans = menuAns.nextLine();
                if(!ans.isEmpty()) break;
        }
        keyword = ans;
        
		if(method.equals("1")){
			searchSQL += "WHERE Spacecraft_Model.Agency LIKE ?";
			stmt = mySQLDB.prepareStatement(searchSQL);
			stmt.setString(1, keyword);//exact
		}else if(method.equals("2")){
			searchSQL += "WHERE Type LIKE ?";
			stmt = mySQLDB.prepareStatement(searchSQL);
			stmt.setString(1, keyword);//exact
		}else if(method.equals("3")){
			//searchSQL += " ";
			searchSQL += "WHERE Spacecraft_Model.Energy > (SELECT MIN(Energy) FROM Spacecraft_Model) ";
		}else if(method.equals("4")){
			searchSQL += "WHERE Spacecraft_Model.Duration > ? ";
			stmt = mySQLDB.prepareStatement(searchSQL);
			stmt.setString(1, keyword);
		
		}else if(method.equals("5")){
			searchSQL += "WHERE A_Model.Capacity > ? ";
			stmt = mySQLDB.prepareStatement(searchSQL);
			stmt.setString(1, keyword);
		}
		
        
        String[] field_name = {"Agency", "MID", "SNum", "Type", "Energy", "Capacity", "Charge"};
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
    
    public static void missionDesign(Scanner menuAns, Connection mySQLDB)throws SQLException{
	//this part for 5.2.3 finished
	//Agency, MID, SNum, Cost, Benefit
        String ans = null, keyword = null;
		String searchSQL = "";
		PreparedStatement stmt = null;
		searchSQL += "SELECT Spacecraft_Model.Agency, Spacecraft_Model.MID,RentalRecord.SNum, ";
		searchSQL += "(Spacecraft_Model.Charge * NEA.Duration)as Cost, (A_Model.Capacity * Resource.Density* Resource.Value-(Spacecraft_Model.Charge * NEA.Duration)) as Benefit ";//for output
		
		//searchSQL += "Spacecraft_Model.Charge, NEA.Duration,NEA.Energy,Spacecraft_Model.Duration,Spacecraft_Model.Energy, ";//for data operation
		//searchSQL += "A_Model.Capacity, Resource.Density,Resource.Value ";
		searchSQL += "FROM Spacecraft_Model JOIN A_Model JOIN Resource JOIN RentalRecord JOIN NEA JOIN Contains ";
		searchSQL += "WHERE Spacecraft_Model.MID = A_Model.MID AND NEA.NID = Contain.NID AND Contain.Rtype = Resource.Rtype AND A_Model.MID = RentalRecord.MID ";
		searchSQL += "AND NEA.Energy <= A_Model.Energy AND Spacecraft_Model.Duration >= NEA.Duration AND RentalRecord.ReturnDate != null ";
		searchSQL += "AND NEA.NID LIKE ? ";
		searchSQL += "ORDER BY Benefit desc ";
		
		//spacecraft e > NEA Em, Tm是NEA duration
		//CM = CR * TM,charge * nea_duration
		//BM = Value *density * capacity -CM
		//CM <= budget   returndate!=null  available
		
        while(true){
		System.out.print("Typing in the NEA ID:");
		ans = menuAns.nextLine();
		if(!ans.isEmpty()) break;
	}
        keyword = ans;
		stmt = mySQLDB.prepareStatement(searchSQL);
		stmt.setString(1, keyword);
		
        System.out.println("All possible solutions");
		
		String[] field_name = {"Agency", "MID", "SNum", "Cost", "Benefit"};
		for (int i = 0; i < 5; i++){
			 System.out.print("| " + field_name[i] + " ");
		}
		System.out.println("|");

		ResultSet resultSet = stmt.executeQuery();
		while(resultSet.next()){
			for (int i = 1; i <= 5; i++){
				System.out.print("| " + resultSet.getString(i) + " ");
			}    
			System.out.println("|");
		}
		System.out.println("End of Query");
		resultSet.close();
		stmt.close();
    
    }
    
    public static void beneficialDesign(Scanner menuAns, Connection mySQLDB)throws SQLException{
	//this part for 5.2.4 finished
	//NEA ID, Family, Agency, MID, SNum,Duration, Cost, Benefit
	//budget: the upper bound of the cost; resource type of NEAs: a certain type of resource.
        String ans1 = null, ans2 = null, keyword = null;
		String searchSQL = "";
		PreparedStatement stmt = null;
		searchSQL += "SELECT NEA.NID,NEA.Family,Spacecraft_Model.Agency, Spacecraft_Model.MID,RentalRecord.SNum, NEA.Duration, ";
		searchSQL += "(Spacecraft_Model.Charge * NEA.Duration)as Cost, (A_Model.Capacity * Resource.Density* Resource.Value-(Spacecraft_Model.Charge * NEA.Duration)) as Benefit ";//for output
		searchSQL += "FROM Spacecraft_Model JOIN A_Model JOIN Resource JOIN RentalRecord JOIN NEA JOIN Contains ";
		searchSQL += "WHERE Spacecraft_Model.MID = A_Model.MID AND NEA.NID = Contain.NID AND Contain.Rtype = Resource.Rtype AND A_Model.MID = RentalRecord.MID ";
		searchSQL += "AND NEA.Energy <= A_Model.Energy AND Spacecraft_Model.Duration >= NEA.Duration AND RentalRecord.ReturnDate != null ";
		
		searchSQL += "AND (Spacecraft_Model.Charge * NEA.Duration) <= ? ";
		searchSQL += "AND Resource.Rtype LIKE ? ";
		searchSQL += "ORDER BY Benefit desc ";
		
		
        while(true){
		System.out.print("Typing in your budget:");
		ans1 = menuAns.nextLine();
		System.out.print("Typing in the resource type:");
		ans2 = menuAns.nextLine();
		if((!ans1.isEmpty())&&(!ans2.isEmpty())) break;
	}

        System.out.println("The most beneficial mission is");
		stmt = mySQLDB.prepareStatement(searchSQL);
		stmt.setString(1, ans1);
		stmt.setString(2, ans2);
		
		
		String[] field_name = {"NEA ID", "Family", "Agency", "MID", "SNum","Duration", "Cost", "Benefit"};
		for (int i = 0; i < 8; i++){
			 System.out.print("| " + field_name[i] + " ");
		}
		System.out.println("|");

		ResultSet resultSet = stmt.executeQuery();
		//only print one record
			for (int i = 1; i <= 8; i++){
				System.out.print("| " + resultSet.getString(i) + " ");
			}    
			System.out.println("|");
		
		System.out.println("End of Query");
		resultSet.close();
		stmt.close();
    
    }
    
    
    public static void main(String[] args) {
        
		Scanner menuAns = new Scanner(System.in);
		int goback=0;
		try{
			Connection mySQLDB = connectToOracle();
			goback = companyMenu(menuAns,mySQLDB);//goback still need to be implemented
		}catch (SQLException e){
			System.out.println(e);
		}

		menuAns.close();
		System.exit(0);
    }
    
}
