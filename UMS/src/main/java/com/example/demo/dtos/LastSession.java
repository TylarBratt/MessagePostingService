package com.example.demo.dtos;

public class LastSession {

	int lastLoginTimeStamp;
	int lastLogoutTimeStamp;

	public LastSession(int int1, int int2) {
		lastLoginTimeStamp = int1;
		lastLogoutTimeStamp = int2;
	}
}
