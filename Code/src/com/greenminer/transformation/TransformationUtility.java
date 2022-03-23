package com.greenminer.transformation;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.greenminer.ga.BitMapping;
import com.greenminer.ga.Chromosome;
import com.greenminer.logger.Logger;
import com.greenminer.main.ProjectConfiguration;

public abstract class TransformationUtility {
	
	static int totalCount = 0;

	public static final String STRING_MINIFCATION = "string";
	public static final String FUNCTION_INLINE = "inline";
	public static final String SINGLE_INTERFACE = "interface";
	public static final String AGGREGATED = "aggregate";

	public static ArrayList<String> apks = new ArrayList<String>(0);

	public static void traverseMainFolder(boolean forValidation) {
		File apksMainFolder = new File("/home/abdulali/Desktop/CMPUT680/Project/APKs");
		File apkChildFolder = null;

		File[] listOfFiles = apksMainFolder.listFiles();

		for (File file : listOfFiles) {
			if (file.isDirectory()) {
				apkChildFolder = new File(file.getAbsolutePath());
				if (!forValidation)
					traverseApkFolder(apkChildFolder);
				else{
					validateDirectories(apkChildFolder);
					Logger.getInstance().writeLog("Total APKS: "+totalCount);
				}
					
			}
		}

	}

	private static void traverseApkFolder(File apkChildFolder) {
		File[] listOfFiles = apkChildFolder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				apks.add(file.getAbsolutePath());
			}
		}
	}

	public static void runAllApkRedexTransformation(String transformationType) {
		String redexPath = "/home/abdulali/Desktop/CMPUT680/Project/RedexProject/redex";

		for (String apk : apks) {
			String inputApkFile = apk;
			String configFile = redexPath + "/config/" + ProjectConfiguration.projectName + "/"  + transformationType + ".config";
			String outputApkFile = apk.split(".apk")[0] + "-" + transformationType + ".apk";
			String command = "" + redexPath + "/redex";
			command += " --sign -c " + configFile + " " + inputApkFile + " -o " + outputApkFile;

			Process proc;
			try {
				proc = Runtime.getRuntime().exec(command);
				Logger.getInstance().writeLog("Configuration:- " + transformationType);
				Logger.getInstance().writeLog("Command being executed:- " + command);
				// Initial Redex Command
				proc.waitFor();
				// Read the output

				BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

				String line = "";
				while ((line = reader.readLine()) != null) {
					System.out.print(line + "\n");
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	public static String runSingleTransformation(Chromosome c) {
		
		String transformationType = BitMapping.calculateChromoseBinaryName(c.getGenes());
		String redexPath = "/home/abdulali/Desktop/CMPUT680/Project/RedexProject/redex";
		String sourceAPK = "/home/abdulali/Desktop/CMPUT680/Project/SourceAPK/";
		String targetAPK = "/home/abdulali/Desktop/CMPUT680/Project/TargetAPK/";

		String inputApkFile = sourceAPK + ProjectConfiguration.mainAPKName;
		String configFile = redexPath + "/config/" + ProjectConfiguration.projectName + "/" + transformationType
				+ ".config";
		String outputApkFile = targetAPK + transformationType + ".apk";
		String command = "" + redexPath + "/redex";
		command += " --sign -c " + configFile + " " + inputApkFile + " -o " + outputApkFile;

		if (transformationIsBasic(c)) {
			command = "cp " + inputApkFile + " " + outputApkFile;
			Process proc;
			try {
				proc = Runtime.getRuntime().exec(command);
				// Copy to server command
				proc.waitFor();
				// Read the output

				BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

				String line = "";
				while ((line = reader.readLine()) != null) {
					System.out.print(line + "\n");
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return outputApkFile;
		}

			Process proc;
			try {
				proc = Runtime.getRuntime().exec(command);
				Logger.getInstance().writeLog("Configuration:- " + transformationType);
				Logger.getInstance().writeLog("Command being executed:- " + command);
				// Initial Redex Command
				proc.waitFor();
				// Read the output

				BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

				String line = "";
				while ((line = reader.readLine()) != null) {
					System.out.print(line + "\n");
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		return outputApkFile;
	}
	
	private static boolean transformationIsBasic(Chromosome c) {
		boolean[] genes = c.getGenes();
		int total = genes.length;
		int falseGene = 0;
		for(boolean g: genes){
			if(g==false)
				falseGene++;
		}
		return falseGene == total? true: false;
		
	}

	public static void sendTransformedFileToServer(String sourceAPKPath) {

		String targetAPKPath = "greenminer@pizza.cs.ualberta.ca:/../../var/uploads/apks/" + ProjectConfiguration.projectName+ "/";
		String command = "scp " + sourceAPKPath +  " " + targetAPKPath;
		
		Process proc;
		try {
			Logger.getInstance().writeLog("Running copy APK to server command");
			Logger.getInstance().writeLog(command);
			proc = Runtime.getRuntime().exec(command);
			// Copy to server command
			proc.waitFor();
			// Read the output

			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.print(line + "\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}

	private static void validateDirectories(File apkChildFolder) {
		
		/*
		 * 
		 * public static final String STRING_MINIFCATION = "string"; public
		 * static final String FUNCTION_INLINE = "inline"; public static final
		 * String SINGLE_INTERFACE = "interface"; public static final String
		 * AGGREGATED = "aggregate";
		 */

		int countNormalApks = 0;
		int countStringApks = 0;
		int countInlineApks = 0;
		int countInterfaceApks = 0;
		int countAggregateApks = 0;
		
		Logger.getInstance().writeLog("Folder investigated:- "+apkChildFolder.getAbsolutePath());
		File[] listOfFiles = apkChildFolder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				
				if(!file.getName().contains(".apk"))
					continue;
				
				totalCount++;
				if(file.getName().contains(STRING_MINIFCATION)){
					countStringApks++;
				}else if(file.getName().contains(FUNCTION_INLINE)){
					countInlineApks++;
				}else if(file.getName().contains(SINGLE_INTERFACE)){
					countInterfaceApks++;
				}else if(file.getName().contains(AGGREGATED)){
					countAggregateApks++;
				}else{
					countNormalApks++;
				}
			}
		}
		
		Logger.getInstance().writeLog("STRING_MINIFCATION:-"+countStringApks);
		Logger.getInstance().writeLog("FUNCTION_INLINE:-"+countInlineApks);
		Logger.getInstance().writeLog("SINGLE_INTERFACE:-"+countInterfaceApks);
		Logger.getInstance().writeLog("AGGREGATED:-"+countAggregateApks);
		
	}
}
