package com.example.demo.dtos;

import java.util.Date;

public class Tweet {
	int id;
	String content;
	int userid;
	Date date;

	public Tweet(int id, String content, int userid, Date date) {
		this.id = id;
		this.content = content;
		this.userid = userid;
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
