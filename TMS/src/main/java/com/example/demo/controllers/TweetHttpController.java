/*
 * package com.example.demo.controllers;
 * 
 * import java.util.Date;
 * import java.util.HashMap;
 * import java.io.BufferedReader;
 * import java.io.IOException;
 * import java.io.InputStreamReader;
 * import java.net.HttpURLConnection;
 * import java.net.MalformedURLException;
 * import java.net.URL;
 * import java.sql.ResultSet;
 * import java.sql.SQLException;
 * import java.sql.Statement;
 * 
 * 
 * import org.springframework.web.bind.annotation.DeleteMapping;
 * import org.springframework.web.bind.annotation.GetMapping;
 * import org.springframework.web.bind.annotation.PostMapping;
 * import org.springframework.web.bind.annotation.PutMapping;
 * import org.springframework.web.bind.annotation.RequestBody;
 * import org.springframework.web.bind.annotation.RequestParam;
 * import org.springframework.web.bind.annotation.RestController;
 * 
 * import com.example.demo.dao.TweetDBManager;
 * import com.example.demo.dtos.Tweet;
 * 
 * 
 * 
 * 
 * @RestController
 * public class TweetHttpController {
 * 
 * 
 * TweetDBManager db;
 * 
 * 
 * public TweetHttpController() {
 * db = new TweetDBManager();
 * //test.sendMessage(null); this line of code is used to send a message to the
 * other application.
 * }
 * 
 * //tweet section
 * //get- GetTweetByUUID Post- CreateTweet Put- UpdateTweet Del- DeleteTweet
 * 
 * @GetMapping("receive")
 * public String getUUID(@RequestParam String ACCESSTOKEN, @RequestParam String
 * USERNAME) throws SQLException{
 * Statement stmt=db.con.createStatement();
 * ResultSet rs = stmt.executeQuery("SELECT ID FROM AUTH ORDER BY ID DESC");
 * rs.next();
 * int ID = rs.getInt("ID");
 * ID++;
 * rs.close();
 * 
 * @SuppressWarnings("unused")
 * int Execute=stmt.executeUpdate("INSERT INTO AUTH VALUES(" + ID +", '" +
 * USERNAME + "', " + ACCESSTOKEN + ")");
 * return"Done";
 * }//end of GetTweetByUUID
 * 
 * 
 * @GetMapping("tweet")
 * public String GetTweetByUUID(@RequestParam String Token) throws SQLException{
 * StringBuilder returnString = new StringBuilder();
 * //not sure if this should be done using a UUID or a UserID but its a simple
 * fix
 * Statement stmt=db.con.createStatement();
 * ResultSet rs=stmt.executeQuery("select USERID from AUTH WHERE TOKEN = '" +
 * Token + "'");
 * rs.next();
 * int UserID = rs.getInt("USERID");
 * try {
 * rs=stmt.executeQuery("select * from TWEET WHERE USERID = '" + UserID + "'");
 * returnString.append("{\"Status\": \"Success\",");
 * 
 * for (int i = 0; i < rs.getFetchSize(); i++) {
 * rs.next();
 * Tweet tweet = new Tweet(rs.getInt("ID"), rs.getString("CONTENT"),
 * rs.getInt("USERID"), rs.getDate("DATE"));
 * returnString.append("\"Tweet" + i + "\": {"
 * + "\"TweetID\": \"" + tweet.id
 * + "\", \"TweetContent\": \"" + tweet.content
 * + "\", \"TweetUserID\": \"" + tweet.userid
 * + "\", \"TweetDate\": \"" + tweet.date + "\"}, ");
 * }
 * rs.next();
 * Tweet tweet = new Tweet(rs.getInt("ID"), rs.getString("CONTENT"),
 * rs.getInt("USERID"), rs.getDate("DATE"));
 * returnString.append("\"Tweet" + rs.getFetchSize() + "\": {"
 * + "\"TweetID\": \"" + tweet.id
 * + "\", \"TweetContent\": \"" + tweet.content
 * + "\", \"TweetUserID\": \"" + tweet.userid
 * + "\", \"TweetDate\": \"" + tweet.date + "\"}");
 * 
 * }catch(Exception e) {
 * System.out.println(e);
 * }
 * 
 * 
 * 
 * return returnString.toString();
 * }//end of GetTweetByUUID
 * 
 * @PostMapping("tweet")
 * public String CreateTweet(@RequestParam String UUID, @RequestBody String
 * data) throws SQLException {
 * 
 * int UserID = 0;
 * Statement stmt=db.con.createStatement();
 * ResultSet rs;
 * Boolean alpha = false;
 * try {
 * rs=stmt.executeQuery("select USERID from AUTH WHERE TOKEN = '" + UUID+ "'");
 * rs.next();
 * UserID = rs.getInt("USERID");
 * }catch (Exception e) {
 * System.out.println(e);
 * System.out.println("Error collecting UserID");
 * }
 * int TweetID = 0;
 * try {
 * rs=stmt.executeQuery("select ID from TWEET ORDER BY ID DESC");
 * rs.next();
 * TweetID = rs.getInt("ID");
 * TweetID++;
 * }catch (Exception e) {
 * System.out.println(e);
 * System.out.println("Error collecting TweetID");
 * }
 * Date date = new Date();
 * java.sql.Date formattedDate = new java.sql.Date(date.getTime());
 * try {
 * 
 * @SuppressWarnings("unused")
 * int executeQeury=stmt.executeUpdate("INSERT INTO TWEET VALUES( " + TweetID +
 * ", '" + data + "', " + UserID + ", '" + formattedDate + "')");
 * alpha = true;
 * }catch (Exception e) {
 * System.out.println(e);
 * System.out.println("Error Inserting Tweet");
 * }
 * 
 * if (alpha == true) {
 * return "Tweet Created";
 * }else {
 * return "Failed to Create Tweet";
 * }
 * }//end of CreateTweet
 * 
 * 
 * 
 * @PutMapping("tweet")
 * public String UpdateTweet(@RequestParam String UUID, @RequestBody String
 * NewData) throws SQLException {
 * 
 * userdata array set up is as follows;
 * 1.ID 2. CONTENT
 * 
 * //this section takes the data from the request body and parses it into an
 * array to be set into the db
 * String[] userdata = NewData.split(",", 3);
 * int a = Integer.parseInt(userdata[0]);
 * 
 * Statement stmt=db.con.createStatement();
 * Boolean alpha = false;
 * try {
 * 
 * @SuppressWarnings("unused")
 * int rs=stmt.executeUpdate("UPDATE TWEET SET CONTENT = '" + userdata[1] +
 * "' WHERE ID = " + a);
 * alpha = true;
 * }catch(Exception e) {
 * System.out.println(e);
 * System.out.println("Error Updating Tweet");
 * alpha = false;
 * }
 * if (alpha == true) {
 * return "Update Succesful";
 * }else {return "Update Failed";}
 * }//end of UpdateUser
 * 
 * @DeleteMapping("tweet")
 * public String DeleteTweet(@RequestParam String TweetID) throws SQLException {
 * //this section is the delete from the database using the request param.
 * Statement stmt=db.con.createStatement();
 * Boolean alpha = false;
 * try {
 * 
 * @SuppressWarnings("unused")
 * int rs=stmt.executeUpdate("DELETE FROM TWEET WHERE ID = " + TweetID);
 * alpha = true;
 * }catch(Exception e) {
 * System.out.println(e);
 * System.out.println("Error Deleting Tweet");
 * alpha = false;
 * }
 * if (alpha == true) {
 * return "Delete Succesful";
 * }else {return "Delete Failed";}
 * }
 * 
 * @GetMapping("subscriber")
 * public String GetProducer(@RequestParam String Token, @RequestParam String
 * SubscriberID) throws SQLException{
 * Boolean TokenCheck = false;
 * Boolean updateCheck = false;
 * StringBuilder returnString = new StringBuilder();
 * 
 * 
 * 
 * //start of the Token check, we first collect all tokens from the database
 * that match the Token Given. This still needs a date Checker
 * Statement stmt=db.con.createStatement();
 * try {
 * ResultSet query = stmt.executeQuery("SELECT * FROM AUTH WHERE ACCESSCODE = '"
 * + Token + "'");
 * query.next();
 * 
 * //this is the algorithm to save the token confirmation as a boolean.
 * if (query.getString("UUID") == Token) {TokenCheck = true;}else {TokenCheck =
 * false;}
 * }catch (Exception e) {
 * System.out.println(e);
 * }
 * if (TokenCheck == true) {
 * //From here we start what the actual Mapping method should do.
 * 
 * 
 * 
 * 
 * 
 * 
 * HttpURLConnection connection = null;
 * StringBuffer response = null;
 * 
 * 
 * try {
 * String base = "http://localhost:8084/subscriber?";
 * 
 * String parameters = "SubscriberID='" + SubscriberID + "'";
 * URL url = new URL(base + parameters);
 * connection = (HttpURLConnection) url.openConnection();
 * 
 * connection.setRequestMethod("GET");
 * connection.setRequestProperty("Content-Type", "application/json" );
 * 
 * connection.connect();
 * connection.getErrorStream();
 * BufferedReader in = new BufferedReader(new
 * InputStreamReader(connection.getInputStream()));
 * String inputLine;
 * response = new StringBuffer();
 * 
 * while ((inputLine = in.readLine()) != null) {
 * response.append(inputLine);
 * }
 * in.close();
 * 
 * 
 * connection.disconnect();
 * 
 * } catch (MalformedURLException e) {
 * 
 * e.printStackTrace();
 * } catch (IOException e) {
 * 
 * e.printStackTrace();
 * }finally {
 * if (connection != null) {
 * connection.disconnect();
 * }
 * }
 * int producers = 1;
 * HashMap<String, String> list = new HashMap<String, String>();
 * String toParse = response.toString();
 * for (int i = 0; i < toParse.length(); i++) {
 * Boolean alpha = Character.isDigit(toParse.charAt(i));
 * if (alpha == true) {
 * int first = i + 3;
 * int second = i + 4;
 * Boolean beta = Character.isDigit(toParse.charAt(second));
 * if (beta == true) {
 * 
 * String doubleDigits = String.valueOf(toParse.charAt(first)) +
 * String.valueOf(toParse.charAt(second));
 * 
 * list.put("Producer" + producers, doubleDigits);
 * i = i+6;
 * producers++;
 * }else {
 * String singleDigit = String.valueOf(toParse.charAt(first));
 * list.put("Producer" + producers, singleDigit);
 * i = i+5;
 * producers++;
 * }
 * }
 * }
 * try {
 * 
 * stmt=db.con.createStatement();
 * ResultSet rs = null;
 * 
 * for (int i = 0; i < list.size(); i++) {
 * String userTemp = list.get("Producer" + (i + 1));
 * int UserID = Integer.valueOf(userTemp);
 * 
 * rs=stmt.executeQuery("select * from TWEET WHERE USERID = " + UserID);
 * for (int j = 0; j < rs.getFetchSize(); j++) {
 * rs.next();
 * Tweet tweet = new Tweet(rs.getInt("ID"), rs.getString("CONTENT"),
 * rs.getInt("USERID"), rs.getDate("DATE"));
 * returnString.append("\"Tweet" + i + "\": {"
 * + "\"TweetID\": \"" + tweet.id
 * + "\", \"TweetContent\": \"" + tweet.content
 * + "\", \"TweetUserID\": \"" + tweet.userid
 * + "\", \"TweetDate\": \"" + tweet.date + "\"}, ");
 * }
 * rs.next();
 * Tweet tweet = new Tweet(rs.getInt("ID"), rs.getString("CONTENT"),
 * rs.getInt("USERID"), rs.getDate("DATE"));
 * returnString.append("\"Tweet" + i + "\": {"
 * + "\"TweetID\": \"" + tweet.id
 * + "\", \"TweetContent\": \"" + tweet.content
 * + "\", \"TweetUserID\": \"" + tweet.userid
 * + "\", \"TweetDate\": \"" + tweet.date + "\"}");
 * 
 * }
 * }catch(Exception e) {
 * System.out.println(e);
 * updateCheck = false;
 * 
 * 
 * }
 * 
 * 
 * 
 * 
 * 
 * if (updateCheck != true) {
 * returnString.
 * append("{\"Error\": {\"Status\": 401, \"Description\": \"Failed to update ROLE Table\"}}"
 * );
 * return returnString.toString();
 * }else{return returnString.toString();}
 * 
 * }else{
 * returnString.
 * append("{\"Error\": {\"Status\": 400, \"Description\": \"Invalid Token\"}}");
 * return returnString.toString();}
 * 
 * 
 * //this is the final portion to either return the built String or to send an
 * error stating an invalid token.
 * 
 * }//end of AuthSession
 * 
 * 
 * @GetMapping("auth")
 * public String AuthSession(@RequestParam String Token) throws SQLException{
 * //necessary for the TokenCheck and return String
 * Boolean TokenCheck = false;
 * Boolean updateCheck = false;
 * StringBuilder returnString = new StringBuilder();
 * 
 * 
 * 
 * //start of the Token check, we first collect all tokens from the database
 * that match the Token Given. This still needs a date Checker
 * Statement stmt=db.con.createStatement();
 * try {
 * ResultSet query = stmt.executeQuery("SELECT * FROM AUTH WHERE ACCESSCODE = '"
 * + Token + "'");
 * query.next();
 * 
 * //this is the algorithm to save the token confirmation as a boolean.
 * if (query.getString("UUID") == Token) {TokenCheck = true;}else {TokenCheck =
 * false;}
 * }catch (Exception e) {
 * System.out.println(e);
 * }
 * if (TokenCheck == true) {
 * //From here we start what the actual Mapping method should do.
 * try {
 * stmt=db.con.createStatement();
 * ResultSet rs=stmt.executeQuery("select USERID from AUTH WHERE TOKEN = '" +
 * Token + "'");
 * rs.next();
 * updateCheck = true;
 * }catch(Exception e) {
 * System.out.println(e);
 * System.out.println("Error Authorizing Session");
 * updateCheck = false;
 * }
 * 
 * if (updateCheck == true) {
 * 
 * returnString.append("{\"Status\": \"Success\"}");
 * }else {
 * 
 * returnString.
 * append("{\"Error\": {\"Status\": 401, \"Description\": \"Failed to update ROLE Table\"}}"
 * );
 * }
 * 
 * 
 * //this is the final portion to either return the built String or to send an
 * error stating an invalid token.
 * 
 * return returnString.toString();
 * }else{
 * returnString.
 * append("{\"Error\": {\"Status\": 400, \"Description\": \"Invalid Token\"}}");
 * return returnString.toString();}
 * 
 * 
 * 
 * 
 * 
 * }//end of AuthSession
 * 
 * 
 * public static Tweet[] addX(int n, Tweet arr[], Tweet x)
 * {
 * int i;
 * 
 * // create a new array of size n+1
 * Tweet newarr[] = new Tweet[n + 1];
 * 
 * // insert the elements from
 * // the old array into the new array
 * // insert all elements till n
 * // then insert x at n+1
 * for (i = 0; i < n; i++)
 * newarr[i] = arr[i];
 * 
 * newarr[n] = x;
 * 
 * return newarr;
 * }
 * }//end of HttpController Class
 */