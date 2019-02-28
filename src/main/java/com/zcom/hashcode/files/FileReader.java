package com.zcom.hashcode.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReader {

	public void parseInputFile(File f) {
		try {
			final Scanner sc = new Scanner(f);
			while(sc.hasNextLine()) {
				
			}
		} catch (FileNotFoundException e) {
			// ignore
		}
	}
	
}
