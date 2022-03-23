package com.greenminer.ga.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.greenminer.ga.BitMapping;
import com.greenminer.main.ProjectConfiguration;


public class TestDownloadCSV {
	
	public static void main(String[] args) throws IOException {

		File input = new File("/tmp/input.html");
		input.createNewFile();
		System.setProperty("webdriver.gecko.driver", "/home/abdulali/Desktop/CMPUT680/Project/680WS/geckodriver-v0.23.0-linux64/geckodriver");
		String transformationType = "01";
		WebDriver driver = new FirefoxDriver();
		driver.get("https://pizza.cs.ualberta.ca/gm/graphs.py?"
				+ "batch="
				+ ProjectConfiguration.projectName + "-" + transformationType
				+ "&device=&"
				+ "test="
				+ ProjectConfiguration.testSuiteName
				+ "&graph=graph");
		
		Document doc = Jsoup.parse(driver.getPageSource());

		Element totalCsvLink = doc.select("a[href$=totals.csv]").first(); // a with href

		String link = "https://pizza.cs.ualberta.ca/gm/" + totalCsvLink.attr("href");
		String resultFolder = "/home/abdulali/Desktop/CMPUT680/Project/Result/"+ProjectConfiguration.projectName+"/";
		ProjectConfiguration.savedResultFile = resultFolder+  transformationType +".csv";
		
		URL website = new URL(link);
		File file = new File(ProjectConfiguration.savedResultFile);
		file.createNewFile();
		FileUtils.copyURLToFile(website, file);
		driver.close();
	
	}

}
