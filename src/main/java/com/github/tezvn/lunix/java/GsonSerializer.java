package com.github.tezvn.lunix.java;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonSerializer {

	public static String serialize(Object obj) {
		return new Gson().toJson(obj);
	}
	
	public static Object deserialize(String args){
		return new Gson().fromJson(args, new TypeToken<Object>() {}.getType());
	}
	
}
