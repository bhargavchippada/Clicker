package support;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class Question{
	
	public static String classname = "Question";
	
	public static String questionContent="null";
	public static int quesType=-1;
	public static JSONArray options = new JSONArray();
	
	public static void initialize(){
		questionContent="null";
		quesType=-1;
		options = new JSONArray();
	}
	
	
	public static void addQuestionContent(String content){
		content = content.trim();
		questionContent = content;
	}
	
	public static void addOption(String op){
		op = op.trim();
		if(op.length()!=0) options.put(op);
	}
	
	public static void addOptions(JSONArray ops) throws JSONException{
		for(int i=0; i<ops.length();i++) addOption(ops.get(i).toString());
	}
	
	public static void print() throws JSONException{
		Utils.logv(classname, questionContent);
		for(int i=0; i<options.length();i++) Utils.logv(classname, options.get(i).toString());
	}
}
