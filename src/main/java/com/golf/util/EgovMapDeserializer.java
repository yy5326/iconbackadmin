package com.golf.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class EgovMapDeserializer implements JsonDeserializer<EgovMap> {
	
	@Override @SuppressWarnings("unchecked")
	public EgovMap deserialize(JsonElement json, Type typeOfT,
		JsonDeserializationContext context) throws JsonParseException {
			return (EgovMap) read(json);
		}
	
	public Object read(JsonElement in) {
		if(in.isJsonArray()){
			EgovMap map = new EgovMap();
			//JsonArray인 경우
			List<EgovMap> list = new ArrayList<EgovMap>();
			JsonArray arr = in.getAsJsonArray();
			for (JsonElement anArr : arr) {
				//JsonPrimitive 나올 떄까지 for문
				list.add((EgovMap)read(anArr));
			}
			map.put("listData", list);
			return map;
		}else if(in.isJsonObject()){
			EgovMap map = new EgovMap();
			JsonObject obj = in.getAsJsonObject();
			Set<Entry<String, JsonElement>> entitySet = obj.entrySet();
			for(Map.Entry<String, JsonElement> entry: entitySet){
				//JsonPrimitive 나올 떄까지 for문
				map.put(entry.getKey(), read(entry.getValue()));
			}
			return map;
		}else if( in.isJsonPrimitive()){
			JsonPrimitive prim = in.getAsJsonPrimitive();
			if(prim.isBoolean()){
				//true , fales 형으로
				return prim.getAsBoolean();
			}else if(prim.isString()){
				//String으로
				return prim.getAsString();
			}else if(prim.isNumber()){
				Number num = prim.getAsNumber();
				//Math.ceil 소수점을 올림한다.
				if(Math.ceil(num.doubleValue()) == num.longValue())
					//소수점 버림, Int형으로.
					return num.longValue();
				else{
					//소수점 안버림, Double 형으로
					return num.doubleValue();
				}
			}
		}
		return null;
	}
}
