package com.greenminer.ga.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestCurlCommand {

	public static void main(String[] args) throws IOException {
		String command = "curl 'https://pizza.cs.ualberta.ca/gm/queue.pl?add_tests' -H 'User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:63.0) Gecko/20100101 Firefox/63.0' -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8' -H 'Accept-Language: en-CA,en-US;q=0.7,en;q=0.3' --compressed -H 'Referer: https://pizza.cs.ualberta.ca/gm/add_tests.html' -H 'Content-Type: application/x-www-form-urlencoded' -H 'Connection: keep-alive' -H 'Upgrade-Insecure-Requests: 1' --data 'app=abdul680_2048&test=abdul680_s_2048_energy&repetitions=10&batch_name=abdul-777&versions=version_10_2048-interface%0D%0Aversion_10_2048-string%0D%0Aversion_10_2048&how=suggest'";

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
		builder.redirectErrorStream(true);
		Process p = builder.start();
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while (true) {
			line = r.readLine();
			if (line == null) {
				break;
			}
			sb.append(line);
		}
		System.out.println("Running CurL command: "+sb);

	}

}
