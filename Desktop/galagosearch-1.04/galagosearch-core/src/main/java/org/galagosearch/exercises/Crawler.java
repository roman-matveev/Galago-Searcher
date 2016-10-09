/* Crawler.java
 * Roman Matveev
 * 
 * The crawler program is implemented fully, compiles fully,
 * and is feature-full with UTF-8 encoding and 256k file truncation.
 */



package org.galagosearch.exercises;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import org.galagosearch.core.parse.Document;
import org.galagosearch.core.parse.Tag;
import org.galagosearch.core.parse.TagTokenizer;



/**
 * This is a simple single-threaded web crawler that does not parse robots.txt.
 * 
 * @author roman
 */

public class Crawler {
	
	// Crawler constructor
	public Crawler() {}

	// Main base directory and base path initialization
	File m_baseDir;
	String m_basePath;
	
	
	
	/**
	 * Sets folder directory.
	 * 
	 * @param folder
	 */

	public void setFolder(String folder) {
	
		File dir = new File(folder);
		m_baseDir = dir;
		dir.mkdir();
	}
	
	
	
	/**
	 * Sets base path.
	 * 
	 * @param url
	 */

	public void setBasePath(URL url) {
		
		m_basePath = url.getProtocol() + "://" + url.getHost();
	}
	
	
	
	/**
	 * Gets base path for usage.
	 * 
	 * @return m_basePath
	 */

	public String getBasePath() {
		
		return m_basePath;
	}
	
	
	
	/**
	 * Runs crawler using seedUrl and produces I/O feedback via internal state processes.
	 * 
	 * @param seedUrl
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */

	public void run(URL seedUrl) throws IOException, ParserConfigurationException, SAXException {
		
		setBasePath(seedUrl);
		
		System.out.println("\n .\n .\n .\n .\n .\n \nInitiating crawler for seed url...");

		System.out.println("\nSaving the original page... \n ");
		printURL(seedUrl, "0.html", 0);
		
		System.out.println("Converting URL to document... \n ");
		String mainPage = convertUrlToString(seedUrl, 0);
		
		TagTokenizer tokenizer = new TagTokenizer();
		Document document = new Document();

		System.out.println("Parsing page using tokenizer... \n ");
		document.text = mainPage;
		tokenizer.process(document);
		
		System.out.println("Cycling through href link from seed... \n \n ---------------------------------------- \n");
		
		int counter = 0;
		
		for (Tag tag : document.tags) {
			
			if (tag.name.equals("a") && tag.attributes.containsKey("href")) {
				
				String link = tag.attributes.get("href");
				if (link.charAt(0) == '#') {
					
					continue;
				}
				
				counter++;
				
				String fullLink = getBasePath() + link;
				URL newUrl = new URL(fullLink);
				
				String fileName = "" + counter + ".html";
				System.out.println("Writing file" + fileName + " from found source:");
				printURL(newUrl, fileName, 256000);
				
				System.out.println(fullLink);
				System.out.println(" ");
				
				try {
					Thread.sleep(5000);
				} catch(InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			
			if (counter >= 10) {
				
				System.out.println(" .\n .\n .\n .\n .\n \nFinished processing.");
				break;
			}
		}
	}
	
	
	
	/**
	 * Format incoming stream into a string, allow it to become readable.
	 * 
	 * @param input
	 */

	public String convertStreamToString(InputStream input) {
		
		Scanner scanner = new Scanner(input, "UTF-8").useDelimiter("\\A");
		return scanner.hasNext() ? scanner.next() : "";
	}
	
	
	
	/**
	 * Converts URL contents into a string, and truncates files which are over 256kb.
	 * 
	 * @param url
	 * @param maxSize
	 * @throws IOException
	 */

	public String convertUrlToString(URL url, int maxSize) throws IOException {
		
		URLConnection connection = url.openConnection();
		connection.connect();
		InputStream stream = connection.getInputStream();
		String convertedString = convertStreamToString(stream);
		
		if (maxSize == 0 ) {
			
			return convertedString;		
		}
		
		else {
			
			if (convertedString.length() > maxSize) {
				
				return convertedString.substring(0, maxSize);
			}
			
			else {
				return convertedString;
			}
		}
	}
	
	
	
	/**
	 * Prints out the page URL and the containing path in the created directory.
	 * 
	 * @param url
	 * @param fileName
	 * @param maxSize
	 * @throws IOException
	 */

	public void printURL(URL url, String fileName, int maxSize) throws IOException {
		
		String pageContent = convertUrlToString(url, maxSize);

		PrintWriter out = new PrintWriter(m_baseDir.getPath() + "/" + fileName);
		out.println(pageContent);
	}
}