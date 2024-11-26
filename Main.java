package Ddbms_proj;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

	static final String JDBC_Driver = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/auditorium_booking_system?useSSL=false";
	private static final String USER = "root";
	private static final String PASS = "roots";
	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws SQLException {
		int x = 8;
		int k=-1;
		do
		{
		System.out.println("Enter your login email id: ");
		String email = sc.next();

		boolean result = insertIntoLogin(email, x++);
		if (result) {
			x++; 
			System.out.println("Email and role inserted into login table.");
			k=0;
			Main.choices(email);
		} else {
			System.out.println("Invalid email.");
		}
		}while(k!=0);
	}

	public static boolean insertIntoLogin(String email, int x) {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
		
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			if (!email.equals("admin@cumminscollege.in")) {
	
				String query1 = "SELECT role FROM Club_Details WHERE club_email_id = ?";
				stmt = conn.prepareStatement(query1);
				stmt.setString(1, email);
				rs = stmt.executeQuery();
				String role = null;
				if (rs.next()) {
					role = rs.getString(1);
					boolean insertResult = insertLoginRecord(conn, x, email, role);
					if (insertResult) {
						return true; 
					}
				}

				String query2 = "SELECT role FROM Instructor WHERE i_email_id = ?";
				stmt = conn.prepareStatement(query2);
				stmt.setString(1, email);
				rs = stmt.executeQuery();

				if (rs.next()) {
					role = "instructor"; 
					boolean insertResult = insertLoginRecord(conn, x, email, role);
					if (insertResult) {
						return true; 
					}
				}
			} else {
				Main.adminView(conn);
			}
			return false; 

		} catch (SQLException e) {
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException se) {

			}
		}
	}
	private static boolean insertLoginRecord(Connection conn, int x, String email, String role) {
		PreparedStatement insertStmt = null;
		try {
			String insertQuery = "INSERT INTO login (login_id, email_id, role) VALUES (?, ?, ?)";
			insertStmt = conn.prepareStatement(insertQuery);

			insertStmt.setInt(1, x);
			insertStmt.setString(2, email);
			insertStmt.setString(3, role);

			int rowsAffected = insertStmt.executeUpdate();
			if (rowsAffected > 0) {
				return true; 
			} else {
				return false;
			}
		} catch (SQLException e) {
			//e.printStackTrace();
			return false;
		} finally {
			try {
				if (insertStmt != null)
					insertStmt.close();
			} catch (SQLException e) {
				//e.printStackTrace();
			}
		}
	}

	public static void choices(String email) throws SQLException {
		Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		PreparedStatement stmt = null;
		ResultSet rs = null;

		int n;
		do {
			System.out.println("Enter Your Choice: \n1.Booking Auditorium Slots \n2.Checking Availability of Auditorium \n0.Log Out");
			n = sc.nextInt();

			switch (n) {
			case 1:
				
				System.out.println("Enter the booking date: ");
				String query = "insert into booking (audi_name, email_id, date, start_time, end_time, purpose, status, priority) values (?, ?, ?, ?, ?, ?, ?, ?)";
				stmt = conn.prepareStatement(query);
				LocalDate currentDate = LocalDate.now();
				int a = 1; 
				Date date = null;
				while (a != 0) {
					System.out.println("Enter date in YYYY-MM-DD format: ");
					String userInput = sc.next();
					date = Date.valueOf(userInput); 
					LocalDate userDate = date.toLocalDate(); 
					if (userDate.isAfter(currentDate)) {
						System.out.println("Valid date entered: " + userDate);
						a = 0; 
					} else {
						System.out.println("Wrong Date Entered.");
					}
				}
				System.out.println("Enter venue: ");
				sc.nextLine(); 
				String venue = sc.nextLine();

				System.out.println("Enter start time in HH-MM-SS format: ");
				Time start = Time.valueOf(sc.next());

				System.out.println("Enter end time in HH-MM-SS format: ");
				Time end = Time.valueOf(sc.next());

				sc.nextLine();

				System.out.println("Enter the Purpose of Booking: ");
				String purpose = sc.nextLine();

				String status = "Pending";

				String checkVenueQuery = "SELECT a_name FROM audi WHERE a_name = ?";
				PreparedStatement checkVenueStmt = conn.prepareStatement(checkVenueQuery);
				checkVenueStmt.setString(1, venue);
				ResultSet venueRs = checkVenueStmt.executeQuery();

				if (!venueRs.next()) {
					System.out.println("The venue '" + venue + "' does not exist.");
					return; 
				}
				String role = null;


				String roleQuery = "SELECT role FROM club_details WHERE club_email_id = ?";
				PreparedStatement roleStmt = conn.prepareStatement(roleQuery);
				roleStmt.setString(1, email);
				rs = roleStmt.executeQuery();

				if (rs.next()) {
					role = rs.getString(1); 
				} else {
				
					String roleQuery1 = "SELECT role FROM instructor WHERE i_email_id = ?";
					PreparedStatement roleStmt1 = conn.prepareStatement(roleQuery1);
					roleStmt1.setString(1, email);
					ResultSet rs1 = roleStmt1.executeQuery();

					if (rs1.next()) {
						role = rs1.getString(1);
					} else {
						System.out.println("No Instructor Found!");
						return; 
					}
				}

				
				stmt.setString(1, venue); 
				stmt.setString(2, email); 
				stmt.setDate(3, date); 
				stmt.setTime(4, start); 
				stmt.setTime(5, end);
				stmt.setString(6, purpose); 
				stmt.setString(7, status); 
				stmt.setString(8, role);

				stmt.executeUpdate();
				System.out.println("Booking registered. Slot allotted for Admin Approval.");
				break;

			case 2:

				int choice1;
				do {
		            System.out.println("Enter 1 to see this month's bookings for KB Joshi Hall");
		            System.out.println("Enter 2 to see this month's bookings for Mechanical Auditorium");
		            System.out.println("Enter 3 to see this month's bookings for Instrumentation Auditorium");
		            System.out.println("Enter 4 to see this month's bookings for all auditoriums");

		            choice1 = sc.nextInt();

		            Calendar calendar = Calendar.getInstance();
		            int currentMonth = calendar.get(Calendar.MONTH) + 1; 
		            int currentYear = calendar.get(Calendar.YEAR);

		            switch (choice1) {
		                case 1:
		                    String audi_name = "KB Joshi Hall";
		                    query = "SELECT * FROM upcoming_bookings WHERE audi_name = ? AND MONTH(date) = ? AND YEAR(date) = ?;";

		                    PreparedStatement stmt1 = conn.prepareStatement(query);
		                    stmt1.setString(1, audi_name);
		                    stmt1.setInt(2, currentMonth);
		                    stmt1.setInt(3, currentYear);
		                    ResultSet rs1 = stmt1.executeQuery();

		                    System.out.println("+----------------------------+------------+------------+----------+");
		                    System.out.println("| audi_name                  | date       | start_time | end_time |");
		                    System.out.println("+----------------------------+------------+------------+----------+");

		                    while (rs1.next()) {
		                        String audi_name1 = rs1.getString("audi_name");
		                        Date date1 = rs1.getDate("date");
		                        Time start_time = rs1.getTime("start_time");
		                        Time end_time = rs1.getTime("end_time");

		                        System.out.printf("| %-26s | %-10s | %-10s | %-8s |\n", audi_name1, date1.toString(),
		                                start_time.toString(), end_time.toString());
		                    }

		                    System.out.println("+----------------------------+------------+------------+----------+");
		                    break;

		                case 2:
		                    String audi_name2 = "Mechanical Auditorium";
		                    query = "SELECT * FROM upcoming_bookings WHERE audi_name = ? AND MONTH(date) = ? AND YEAR(date) = ?;";

		                    PreparedStatement stmt2 = conn.prepareStatement(query);
		                    stmt2.setString(1, audi_name2);
		                    stmt2.setInt(2, currentMonth);
		                    stmt2.setInt(3, currentYear);
		                    ResultSet rs2 = stmt2.executeQuery();

		                    System.out.println("+----------------------------+------------+------------+----------+");
		                    System.out.println("| audi_name                  | date       | start_time | end_time |");
		                    System.out.println("+----------------------------+------------+------------+----------+");

		                    while (rs2.next()) {
		                        String audi_name_mech = rs2.getString("audi_name");
		                        Date date2 = rs2.getDate("date");
		                        Time start_time = rs2.getTime("start_time");
		                        Time end_time = rs2.getTime("end_time");

		                        System.out.printf("| %-26s | %-10s | %-10s | %-8s |\n", audi_name_mech, date2.toString(),
		                                start_time.toString(), end_time.toString());
		                    }

		                    System.out.println("+----------------------------+------------+------------+----------+");
		                    break;

		                case 3:
		                    String audi_name3 = "Instrumentation Auditorium";
		                    query = "SELECT * FROM upcoming_bookings WHERE audi_name = ? AND MONTH(date) = ? AND YEAR(date) = ?;";

		                    PreparedStatement stmt3 = conn.prepareStatement(query);
		                    stmt3.setString(1, audi_name3);
		                    stmt3.setInt(2, currentMonth);
		                    stmt3.setInt(3, currentYear);
		                    ResultSet rs3 = stmt3.executeQuery();

		                    System.out.println("+----------------------------+------------+------------+----------+");
		                    System.out.println("| audi_name                  | date       | start_time | end_time |");
		                    System.out.println("+----------------------------+------------+------------+----------+");

		                    while (rs3.next()) {
		                        String audi_name_instr = rs3.getString("audi_name");
		                        Date date3 = rs3.getDate("date");
		                        Time start_time = rs3.getTime("start_time");
		                        Time end_time = rs3.getTime("end_time");

		                        System.out.printf("| %-26s | %-10s | %-10s | %-8s |\n", audi_name_instr, date3.toString(),
		                                start_time.toString(), end_time.toString());
		                    }

		                    System.out.println("+----------------------------+------------+------------+----------+");
		                    break;

		                case 4:
		                    String unionQuery = "SELECT audi_name, date, start_time, end_time FROM upcoming_bookings "
		                            + "WHERE audi_name = 'KB Joshi Hall' AND MONTH(date) = ? AND YEAR(date) = ? "
		                            + "UNION "
		                            + "SELECT audi_name, date, start_time, end_time FROM upcoming_bookings "
		                            + "WHERE audi_name = 'Mechanical Auditorium' AND MONTH(date) = ? AND YEAR(date) = ? "
		                            + "UNION "
		                            + "SELECT audi_name, date, start_time, end_time FROM upcoming_bookings "
		                            + "WHERE audi_name = 'Instrumentation Auditorium' AND MONTH(date) = ? AND YEAR(date) = ? "
		                            + "ORDER BY date, start_time;";

		                    PreparedStatement stmt4 = conn.prepareStatement(unionQuery);
		                    stmt4.setInt(1, currentMonth);
		                    stmt4.setInt(2, currentYear);
		                    stmt4.setInt(3, currentMonth);
		                    stmt4.setInt(4, currentYear);
		                    stmt4.setInt(5, currentMonth);
		                    stmt4.setInt(6, currentYear);

		                    ResultSet rs4 = stmt4.executeQuery();

		                    System.out.println("+----------------------------+------------+------------+----------+");
		                    System.out.println("| audi_name                  | date       | start_time | end_time |");
		                    System.out.println("+----------------------------+------------+------------+----------+");

		                    boolean foundResults = false;
		                    while (rs4.next()) {
		                        foundResults = true;

		                        String audi_name1 = rs4.getString("audi_name");
		                        Date date1 = rs4.getDate("date");
		                        Time start_time = rs4.getTime("start_time");
		                        Time end_time = rs4.getTime("end_time");

		                        System.out.printf("| %-26s | %-10s | %-10s | %-8s |\n", audi_name1, date1.toString(),
		                                start_time.toString(), end_time.toString());
		                    }

		                    if (!foundResults) {
		                        System.out.println("No bookings found for the current month.");
		                    }

		                    System.out.println("+----------------------------+------------+------------+----------+");
		                    break;
					}
				} while (choice1 != 0);

				break;

			case 0:
				System.out.println("Exiting Login");
				String deleteQuery = "Delete from login where email_id= ?";
				stmt = conn.prepareStatement(deleteQuery);
				stmt.setString(1, email);
				stmt.executeUpdate();
				break;

			default:
				System.out.println("Wrong Choice");
				break;
			}
		} while (n != 0);
	}

	static void adminView(Connection conn) throws SQLException {
		int choiceOfAdmin;
		do {
			System.out.println(
					"Enter \n1 Club Menu \n2 Instructor Menu \n3 Club Head Menu \n4 Auditorium Staff Menu \n5 Booking Approval\n0 Exit");
			choiceOfAdmin = sc.nextInt();
			switch (choiceOfAdmin) {
			case 1:
				int choiceClubs;
				do {
					System.out.println(
							"Enter \n1 to display club names who have no bookings for a given month \n2 find booking under club name \n3 number of bookings per month for a club\n4 monthly bookings per club\n0 to exit");
					choiceClubs = sc.nextInt();
					switch (choiceClubs) {
					case 1:
						System.out.println("Enter month (1-12): ");
						int month = sc.nextInt();

						if (month < 1 || month > 12) {
							System.out.println("Invalid month. Please enter a value between 1 and 12.");
							return; 
						}

						String find_club_bookings = "SELECT cd.club_name FROM Club_Details cd LEFT JOIN Club_Connections cc ON cd.club_email_id = cc.club_email_id LEFT JOIN Booking b ON cc.club_email_id = b.email_id AND MONTH(b.date) = ? GROUP BY cd.club_name HAVING COUNT(b.b_id) = 0;";

						boolean flag = false;

						PreparedStatement roleStmt3 = conn.prepareStatement(find_club_bookings);
						roleStmt3.setInt(1, month);
						ResultSet rs3 = roleStmt3.executeQuery();

						while (rs3.next()) {
							String club_name = rs3.getString(1);
							System.out.println(club_name);
							flag = true;
						}
						if (!flag) {
							System.out.println("No clubs found with no bookings in this month.");
						}
						break;
					case 2:
						System.out.print("Enter club email id: ");
						String club_email_id = sc.next();

						String find_club_booking = "SELECT email_id, audi_name, date, purpose FROM booking WHERE email_id = ? AND status = 'Approved';";

						PreparedStatement stmt = conn.prepareStatement(find_club_booking);
						stmt.setString(1, club_email_id);
						rs3 = stmt.executeQuery();

						boolean foundSessions = false;
						while (rs3.next()) {
							String email_name = rs3.getString(1);
							String audi_name = rs3.getString(2);
							Date date = rs3.getDate(3); 
							String formattedDate = date != null
									? new java.text.SimpleDateFormat("yyyy-MM-dd").format(date)
									: "N/A";
							String purpose = rs3.getString(4);

							System.out.println("The name of the club is " + email_name + ", \n the name of auditorium for booking is " + audi_name+ ",\n the date for booking is " + formattedDate + ",\nthe purpose for booking is " +purpose);
							foundSessions = true;
						}

						if (!foundSessions) {
							System.out.println("\nNo approved sessions found.");
						}
						break;
					case 3:
						System.out.print("Enter club email id: ");
						String club_email_id1 = sc.next();

						String find_club_booking1 = "SELECT MONTH(date) AS Month, COUNT(*) AS ApprovedBookingsCount " +
                                "FROM booking " +
                                "WHERE email_id = ? AND status = 'Approved'" +
                                "GROUP BY MONTH(date) " +
                                "ORDER BY Month DESC;";

						PreparedStatement stmt1 = conn.prepareStatement(find_club_booking1);
						stmt1.setString(1, club_email_id1);
						rs3 = stmt1.executeQuery();

						boolean foundSessions1 = false;
						System.out.printf("%-25s %-20s %n", "Month", "Count");
						System.out.println();

						while (rs3.next()) {
						
							int month1 = rs3.getInt(1);
							int cnt = rs3.getInt(2);

							System.out.printf("%-25s %-20s %n", month1, cnt);
							foundSessions1 = true;
						}

						if (!foundSessions1) {
							System.out.println("\nNo approved sessions found.");
						}

						break;

					case 4:

						System.out.print("Enter month : ");
						int months = sc.nextInt();

						String find_club_booking2 = "SELECT cd.club_name, COUNT(b.b_id) AS bookings_count FROM Booking b JOIN Club_Connections cc ON b.email_id = cc.club_email_id JOIN Club_Details cd ON cc.club_email_id = cd.club_email_id WHERE MONTH(b.date) = ? GROUP BY cd.club_name;";

						stmt1 = conn.prepareStatement(find_club_booking2);
						stmt1.setInt(1, months);
						rs3 = stmt1.executeQuery();
						System.out.printf("%-35s %-20s %n", "Club name", "Count");
						System.out.println();

						boolean foundSessions2 = false;
						while (rs3.next()) {
							String club_name = rs3.getString(1);
							int cnt = rs3.getInt(2);

							System.out.printf("%-35s %-20s %n", club_name, cnt);
							foundSessions2 = true;
						}

						if (!foundSessions2) {
							System.out.println("\nNo bookings in this month.");
						}

						break;

					}

				} while (choiceClubs != 0);
				break;
			case 2:
				int choiceIns;
				do {
					System.out.println(
							"Enter \n1 to display instructors without clubs\n2 to find instructor sessions\n0 to exit");
					choiceIns = sc.nextInt();
					sc.nextLine(); 

					switch (choiceIns) {
					case 1:
						String find_instructors = "SELECT i.i_email_id, i.i_name FROM Club_Connections cc "
								+ "RIGHT JOIN Instructor i ON i.i_email_id = cc.instructor_id "
								+ "WHERE cc.club_email_id IS NULL;";
						System.out.printf("%-50s %-35s %n", "Instructor id", "Instructor name");
						System.out.println();

						try (PreparedStatement roleStmt3 = conn.prepareStatement(find_instructors);
								ResultSet resultSet = roleStmt3.executeQuery()) {
							while (resultSet.next()) {
								String email = resultSet.getString("i_email_id");
								String name = resultSet.getString("i_name");

								System.out.printf("%-50s %-35s %n", email, name);

							}

						} catch (SQLException e) {
							//e.printStackTrace();
						}
						break;

					case 2:

						System.out.print("Enter instructor email id: ");
						String instructor_email_id = sc.nextLine(); 
						String find_instructor_sessions = "SELECT email_id, audi_name, date, purpose FROM booking WHERE email_id = ? AND status = 'Approved';";

						PreparedStatement stmt = conn.prepareStatement(find_instructor_sessions);
						stmt.setString(1, instructor_email_id);
						ResultSet rs3 = stmt.executeQuery();
						System.out.printf("%-40s %-40s %-20s %-40s%n", "Instructor id", "Auditorium name", "Date",
								"Purpose");
						System.out.println();

						boolean foundSessions = false;
						while (rs3.next()) {
							String email_name = rs3.getString(1);
							String audi_name = rs3.getString(2);
							Date date = rs3.getDate(3); 
							String formattedDate = date != null
									? new java.text.SimpleDateFormat("yyyy-MM-dd").format(date)
									: "N/A";
							String purpose = rs3.getString(4);
							System.out.printf("%-40s %-40s %-20s %-40s%n", email_name, audi_name, formattedDate,
									purpose);
							foundSessions = true;
						}
						if (!foundSessions) {
							System.out.println("\nNo approved sessions found for the given instructor email ID.");
						}

						break;
					}

				} while (choiceIns != 0);

				break;
			case 3:
				sc.nextLine();
				System.out.println("Enter head email id: ");
				String head_email_id = sc.nextLine();
				String find_club_head = "SELECT Club_Count_Head(?) as Club_Head;";

				PreparedStatement roleStmt3 = conn.prepareStatement(find_club_head);
				roleStmt3.setString(1, head_email_id);
				ResultSet rs3 = roleStmt3.executeQuery();
				boolean flag = false;
				while (rs3.next()) {
					flag = true;
					String club_name = rs3.getString(1);
					System.out.println(club_name);
				}
				if (!flag) {
					System.out.println("NULL");
					return;
				}

				break;
			case 4:

				String details = "SELECT * from audi_incharge natural join audi;";

				roleStmt3 = conn.prepareStatement(details);
				rs3 = roleStmt3.executeQuery();
				boolean flag1 = false;
				while (rs3.next()) {
					flag1 = true;

					String a_name = rs3.getString("a_name");
					String incharge_email_id = rs3.getString("incharge_email_id");
					int a_capacity = rs3.getInt("a_capacity");

					System.out.printf("Auditorium: %-30s Incharge Email: %-30s Capacity: %-10d%n", a_name,
							incharge_email_id, a_capacity);
				}
				if (!flag1) {
					System.out.println("NULL");
					return;
				}

				break;
			case 5:

				int choice_booking;
				do {
					System.out.println(
							"Enter \n1 to see current date events\n2 to update approval\n3 to display events between a time frame\n0 to exit");
					choice_booking = sc.nextInt();
					switch (choice_booking) {
					case 1:

						String currDateDetails = "SELECT b.audi_name, b.email_id, cd.club_name, b.date, b.start_time, b.end_time, b.purpose, b.status "
								+ "FROM Booking b " + "INNER JOIN Club_Details cd ON b.email_id = cd.club_email_id "
								+ "WHERE b.date = CURDATE();";

						roleStmt3 = conn.prepareStatement(currDateDetails);
						rs3 = roleStmt3.executeQuery();
						flag1 = false;
						System.out.printf("%-30s %-25s %-25s %-15s %-20s %-20s %-30s %-15s%n", "Auditorium name", "Email id",
								"Name", "Date", "Start time", "End time", "Purpose", "Status");
						System.out.println();
						while (rs3.next()) {
							flag1 = true;
							String audi_name = rs3.getString("audi_name");
							String email_id = rs3.getString("email_id");
							String club_name = rs3.getString("club_name");
							Date date = rs3.getDate("date");
							Time start_time = rs3.getTime("start_time");
							Time end_time = rs3.getTime("end_time");
							String purpose = rs3.getString("purpose");
							String status = rs3.getString("status");

							System.out.printf("%-30s %-25s %-25s %-15s %-20s %-20s %-30s %-15s%n", audi_name, email_id,
									club_name, date, start_time, end_time, purpose, status);
						}

						if (!flag1) {
							System.out.println("No bookings found for today.");
						}

						break;
						
					case 2:
						sc.nextLine();
						System.out.println("Enter venue name to check bookings:");
				        String venue = sc.nextLine();
				        sc.nextLine();
				        System.out.println("Enter booking date in YYYY-MM-DD format:");
				        String dateInput = sc.nextLine();
				        Date date = Date.valueOf(dateInput);

				        String query = "SELECT * FROM booking WHERE status = 'Pending' AND date = ? AND audi_name = ?";
				        PreparedStatement stmt = conn.prepareStatement(query);
				        stmt.setDate(1, date);
				        stmt.setString(2, venue);

				        ResultSet rs = stmt.executeQuery();
				        List<Map<String, Object>> pendingBookings = new ArrayList<>();

				  
				        while (rs.next()) {
				            String email = rs.getString("email_id");
				            Time startTime = rs.getTime("start_time");
				            Time endTime = rs.getTime("end_time");
				            String status = rs.getString("status");
				            String role = rs.getString("priority");
				            int bId = rs.getInt("b_id");

				            Map<String, Object> booking = new HashMap<>();
				            booking.put("b_id", bId);
				            booking.put("email", email);
				            booking.put("startTime", startTime);
				            booking.put("endTime", endTime);
				            booking.put("status", status);
				            booking.put("role", role);

				            pendingBookings.add(booking);
				        }

				        if (pendingBookings.isEmpty()) {
				            System.out.println("No pending bookings found for this date and venue.");
				        } else {
				          
				            System.out.println("Sorting pending bookings by priority...");
				            Collections.sort(pendingBookings, new Comparator<Map<String, Object>>() {
				                @Override
				                public int compare(Map<String, Object> b1, Map<String, Object> b2) {
				                    int priorityComparison = getPriority((String) b1.get("role")) - getPriority((String) b2.get("role"));
				                    if (priorityComparison == 0) {
				                        return (int) b1.get("b_id") - (int) b2.get("b_id");
				                    }
				                    return priorityComparison;
				                }
				            });

				            for (int i = 0; i < pendingBookings.size(); i++) {
				                Map<String, Object> currentBooking = pendingBookings.get(i);
				                String currentEmail = (String) currentBooking.get("email");
				                Time currentStart = (Time) currentBooking.get("startTime");
				                Time currentEnd = (Time) currentBooking.get("endTime");
				                int currentBId = (int) currentBooking.get("b_id");

				                String checkConflictQuery = "SELECT * FROM booking WHERE audi_name = ? AND date = ? " +
				                                            "AND ((start_time <= ? AND end_time > ?) OR (start_time < ? AND end_time >= ?)) " +
				                                            "AND email_id != ? AND status = 'Pending'";

				                PreparedStatement checkStmt = conn.prepareStatement(checkConflictQuery);
				                checkStmt.setString(1, venue);
				                checkStmt.setDate(2, date);
				                checkStmt.setTime(3, currentStart);
				                checkStmt.setTime(4, currentStart);
				                checkStmt.setTime(5, currentEnd);
				                checkStmt.setTime(6, currentEnd);
				                checkStmt.setString(7, currentEmail);

				                ResultSet conflictResult = checkStmt.executeQuery();

				                if (conflictResult.next()) {
				                   
				                    String conflictingEmail = conflictResult.getString("email_id");
				                    String conflictingRole = conflictResult.getString("priority");
				                    int conflictingBId = conflictResult.getInt("b_id");

				                   
				                    int currentPriority = getPriority((String) currentBooking.get("role"));
				                    int conflictingPriority = getPriority(conflictingRole);
				                   

				                    if (currentPriority < conflictingPriority) {
				                      
				                        updateBookingStatus(conn, conflictingEmail, "Rejected");
				                        updateBookingStatus(conn, currentEmail, "Approved");
				                        
				                        System.out.println("Booking for " + currentEmail + " approved. " + conflictingEmail + "'s booking rejected due to higher priority.");
				                    } else if (currentPriority > conflictingPriority) {
				                    	
				                        updateBookingStatus(conn, currentEmail, "Rejected");
				                        updateBookingStatus(conn, conflictingEmail, "Approved");
				                        System.out.println("Booking for " + conflictingEmail + " approved. " + currentEmail + "'s booking rejected due to higher priority.");
				                        
				                    } 
				                    else {
				                      
				                        if (currentBId < conflictingBId) {
				                           
				                            updateBookingStatus(conn, conflictingEmail, "Rejected");
				                            updateBookingStatus(conn, currentEmail, "Approved");
				                            System.out.println("Booking for " + currentEmail + " approved as it was earlier.");
				                        } else {
				                     
				                            updateBookingStatus(conn, currentEmail, "Rejected");
				                            updateBookingStatus(conn, conflictingEmail, "Approved");
				                            System.out.println("Booking for " + conflictingEmail + " approved as it was earlier.");
				                        }
				                    }
				                } else {
				          
				                    updateBookingStatus(conn, currentEmail, "Approved");
				                    //System.out.println("Booking for " + currentEmail + " approved without any conflicts.");
				                }
				            }
				        }
					
					    break;

					
					case 3:
					      System.out.println("Enter start of the time frame in (YYYY-MM-DD) format: ");
					        String t1 = sc.next();
					        System.out.println("Enter end of the time frame in (YYYY-MM-DD) format: ");
					        String t2 = sc.next();

					 
					        String createViewSQL = "CREATE OR REPLACE VIEW time_frame_bookings AS "
					                + "SELECT cd.club_name, b.email_id, b.audi_name, b.date, b.start_time, b.end_time "
					                + "FROM booking b "
					                + "INNER JOIN Club_Details cd ON b.email_id = cd.club_email_id "
					                + "WHERE b.date BETWEEN ? AND ? "
					                + "ORDER BY b.date, b.start_time;";

					  
					        PreparedStatement roleStmt4 = conn.prepareStatement(createViewSQL);
					        roleStmt4.setString(1, t1);
					        roleStmt4.setString(2, t2);
					        roleStmt4.executeUpdate();  
					        String selectFromViewSQL = "SELECT * FROM time_frame_bookings";

					     
					        PreparedStatement stmt1 = conn.prepareStatement(selectFromViewSQL);
					        ResultSet rs4 = stmt1.executeQuery();

					        boolean f = false;
					   
					        while (rs4.next()) {
					            f = true;
					            String club_name = rs4.getString("club_name");
					            String email_id = rs4.getString("email_id");
					            String audi_name = rs4.getString("audi_name");
					             date = rs4.getDate("date");
					            Time start_time = rs4.getTime("start_time");
					            Time end_time = rs4.getTime("end_time");

					   
					            System.out.printf("%-40s %-40s %-30s %-20s %-20s %-20s%n", club_name, email_id, audi_name,
					                    date, start_time, end_time);
					        }

					        // If no results were found
					        if (!f) {
					            System.out.println("No bookings found for the given time frame.");
					        }
						break;

					}

				} while (choice_booking != 0);

				break;
			case 0:

				System.out.println("Exiting Login");
				String deleteQuery = "Delete from login where email_id= ?";
				PreparedStatement stmt = conn.prepareStatement(deleteQuery);
				stmt.setString(1, "admin@cumminscollege.in");
				stmt.executeUpdate();
				break;
			}

		} while (choiceOfAdmin != 0);
	}
	
	private static int getPriority(String role) {
        switch (role) {
            case "TNP":
                return 1;
            case "Instructor":
                return 2; 
            case "Club":
                return 3;
            case "Panel":
                return 4;
            default:
                return Integer.MAX_VALUE;
        }
    }

    private static void updateBookingStatus(Connection conn, String email, String status) throws SQLException {
        String updateQuery = "UPDATE booking SET status = ? WHERE email_id = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
        updateStmt.setString(1, status);
        updateStmt.setString(2, email);
        updateStmt.executeUpdate();
    }
    
    
    
}
