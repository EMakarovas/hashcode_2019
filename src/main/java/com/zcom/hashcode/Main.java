package com.zcom.hashcode;

import java.io.File;

import com.zcom.hashcode.files.FileReader;

public class Main {

	public static void main(String[] args) {
		final String inputFilePath = args[0];
		new FileReader().parseInputFile(new File(inputFilePath));
		
		System.out.println("LET'S WIN!!!");
	}
	
}
