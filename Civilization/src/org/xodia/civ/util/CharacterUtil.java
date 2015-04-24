package org.xodia.civ.util;

public class CharacterUtil {

	private CharacterUtil(){}
	
	public static boolean isPunctuation(char c){
		if(c == ',' ||
			c == '.' ||
			c == ';' ||
			c == ':' ||
			c == '!' ||
			c == '?'){
			return true;
		}else{
			return false;
		}
	}
	
}
