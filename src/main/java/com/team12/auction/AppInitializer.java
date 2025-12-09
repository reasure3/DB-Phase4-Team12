package com.team12.auction;

import com.team12.auction.util.DBConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppInitializer implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("App Initializer Started");
		try {
			DBConnection.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
