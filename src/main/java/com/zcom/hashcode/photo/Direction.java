package com.zcom.hashcode.photo;

public enum Direction {
	HORIZONTAL("H"),
	VERTICAL("V");
	
	public final String letter;
	
	private Direction(String letter) {
		this.letter = letter;
	}
	
	public String getLetter() {
		return letter;
	}
	
	public static Direction getByLetter(String letter) {
		for(Direction d : Direction.values()) {
			if(d.getLetter().equals(letter)) {
				return d;
			}
		}
		return null;
	}
	
}
