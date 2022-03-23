package com.greenminer.transformation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.greenminer.ga.BitMapping;
import com.greenminer.ga.Chromosome;
import com.greenminer.main.ProjectConfiguration;

public abstract class ConfigFileUtility {
	
	
	
	static String configString[] = { 
			"\"ReBindRefsPass\"" ,
					"\"BridgePass\"" ,
					"\"SynthPass\"" ,
					"\"FinalInlinePass\"" ,
					"\"DelSuperPass\"" ,
					"\"SimpleInlinePass\"" ,
					"\"PeepholePass\"" ,
					"\"ConstantPropagationPass\"" ,
					"\"LocalDcePass\"" ,
					"\"RemoveUnreachablePass\"" ,
					"\"RemoveGotosPass\"" ,
					"\"DedupBlocksPass\"" ,
					"\"SingleImplPass\"" ,
					"\"ReorderInterfacesPass\"" ,
					"\"RemoveEmptyClassesPass\"" ,
					"\"ShortenSrcStringsPass\"" ,
					"\"RegAllocPass\"" ,
					"\"CopyPropagationPass\"" ,
					"\"LocalDcePass\""};
	
	
	public static String getConfigurationString(Chromosome c){
		ArrayList<String> stringGenes = BitMapping.getStringGene(c.getGenes());
		String configuration = "";
		
		for(String cs : configString){
			for(String sg: stringGenes){
				if(cs.contains(sg)){
					configuration += cs + ",\n";
				}
			}
		}
		
		return trimConfigurationLastComma(configuration);
		
	}	

	private static String trimConfigurationLastComma(String str) {
		return str.replaceAll(",$", "");
	}

	public static void generateConfigurationFile(Chromosome c){
		
		String configString = getConfigurationString(c);
		
		String fileName = BitMapping.calculateChromoseBinaryName(c.getGenes());
		
		String redexPath = "/home/abdulali/Desktop/CMPUT680/Project/RedexProject/redex/config/"
				+ ProjectConfiguration.projectName + "/";

		String configurationFileBody = "{\n" +
				"  \"redex\" : {\n" +
				"    \"passes\" : [\n" +
				configString +
				"    ]\n" +
				"  },\n" +
				"  \"FinalInlinePass\" : {\n" +
				"    \"propagate_static_finals\": true,\n" +
				"    \"replace_encodable_clinits\": true\n" +
				"  },\n" +
				"  \"SimpleInlinePass\": {\n" +
				"    \"throws\": true,\n" +
				"    \"multiple_callers\": true,\n" +
				"    \"black_list\": []\n" +
				"  },\n" +
				"  \"ConstantPropagationPassV3\" : {\n" +
				"    \"blacklist\": [],\n" +
				"    \"replace_moves_with_consts\": true,\n" +
				"    \"fold_arithmetic\": true\n" +
				"  },\n" +
				"  \"RegAllocPass\" : {\n" +
				"    \"live_range_splitting\": \"0\"\n" +
				"  },\n" +
				"  \"CopyPropagationPass\" : {\n" +
				"    \"eliminate_const_literals\": false,\n" +
				"    \"full_method_analysis\": true\n" +
				"  },\n" +
				"  \"PeepholePass\" : {\n" +
				"    \"disabled_peepholes\": [\n" +
				"      \"Replace_PutGet\",\n" +
				"      \"Replace_PutGetWide\",\n" +
				"      \"Replace_PutGetObject\",\n" +
				"      \"Replace_PutGetShort\",\n" +
				"      \"Replace_PutGetChar\",\n" +
				"      \"Replace_PutGetByte\",\n" +
				"      \"Replace_PutGetBoolean\"\n" +
				"    ]\n" +
				"  },\n" +
				"  \"keep_packages\": [\n" +
				"    \"Lcom/fasterxml/jackson/\",\n" +
				"    \"Lcom/google/dexmaker/mockito/\"\n" +
				"  ],\n" +
				"  \"keep_annotations\": [\n" +
				"    \"Lcom/google/common/annotations/VisibleForTesting;\"\n" +
				"  ],\n" +
				"  \"proguard_map_output\": \"redex_pg_mapping.txt\",\n" +
				"  \"stats_output\": \"stats.txt\",\n" +
				"  \"bytecode_offset_map\": \"bytecode_offset_map.txt\",\n" +
				"  \"line_number_map_v2\": \"redex-line-number-map-v2\",\n" +
				"  \"method_move_map\" : \"redex-moved-methods-map.txt\",\n" +
				"  \"string_sort_mode\" : \"class_order\",\n" +
				"  \"bytecode_sort_mode\" : \"class_order\",\n" +
				"  \"ir_type_checker\": {\n" +
				"    \"run_after_each_pass\" : false,\n" +
				"    \"polymorphic_constants\" : false,\n" +
				"    \"verify_moves\" : false\n" +
				"  }\n" +
				"}";
	
		try {
			File file = new File(redexPath+fileName+".config");
			file.createNewFile();
			PrintWriter out = new PrintWriter(file);
			out.println(configurationFileBody);
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
}
