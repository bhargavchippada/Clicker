package support;

import java.util.ArrayList;

public class Question{
	
	public static String classname = "Question";
	
	public static String questionContent="null";
	public static int quesType=-1;
	public static ArrayList<String> options = new ArrayList<String>();
	
	public static void initialize(){
		questionContent="null";
		quesType=-1;
		options.clear();
	}
	
	
	public static void addQuestionContent(String content){
		content = content.trim();
		questionContent = content;
	}
	
	public static void addOption(String op){
		op = op.trim();
		if(op.length()!=0) options.add(op);
	}
	
	public static void addOptions(ArrayList<String> ops){
		for(int i=0; i<ops.size();i++) addOption(ops.get(i));
	}
	
	public static void print(){
		Utils.logv(classname, questionContent);
		for(int i=0; i<options.size();i++) Utils.logv(classname, options.get(i));
	}
}
