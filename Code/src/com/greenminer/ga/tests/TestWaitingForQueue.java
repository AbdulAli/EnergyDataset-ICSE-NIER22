package com.greenminer.ga.tests;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.greenminer.main.ProjectConfiguration;


public class TestWaitingForQueue {
	
	public static void main(String[] args) throws IOException {

		int stuck = 0;
		System.setProperty("webdriver.gecko.driver",
				"/home/abdulali/Desktop/CMPUT680/Project/680WS/geckodriver-v0.23.0-linux64/geckodriver");
		String originalBatch = "abdul680_2048-00";
		WebDriver driver;
		while (true) {
			driver = new FirefoxDriver();
			stuck++;
			if(stuck>20){
				System.out.println("I am stuck in calculating energy please change ManualFitness file value to true and remove batch after that");
			}
			System.out.println("Batch " + originalBatch + " still being processed .... will check after 3 mins ....");
			try {
				TimeUnit.MINUTES.sleep((long) 0.5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			driver.get("https://pizza.cs.ualberta.ca/gm/queue.html");

			Document doc = Jsoup.parse(driver.getPageSource());

			Element batchElement = null;
			if (doc.select("h3").size() != 0) {
				batchElement = doc.select("h3").first();
				String batchName = batchElement.childNodes().get(0).toString();
				driver.close();
				if (!batchName.contains(originalBatch)) {
					break;
				}
			} else {
				driver.close();
				break;
			}

		}
		System.out.println("Batch processing ended ...");
	
	}
}
