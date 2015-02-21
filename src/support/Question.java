package support;

import org.json.JSONArray;
import org.json.JSONException;

public class Question{
	public String classname = "Question";
	
	public String questionContent;
	public int quesType=-1;
	public JSONArray options = new JSONArray();
	
	public void clear(){
		questionContent=null;
		quesType=-1;
		options = new JSONArray();
	}
	
	public void addQuestionContent(String content){
		content = content.trim();
		questionContent = content;
	}
	
	public void addOption(String op){
		op = op.trim();
		if(op.length()!=0) options.put(op);
	}
	
	public void addOptions(JSONArray ops) throws JSONException{
		for(int i=0; i<ops.length();i++) addOption(ops.get(i).toString());
	}
	
	public void print() throws JSONException{
		Utils.logv(classname, questionContent);
		for(int i=0; i<options.length();i++) Utils.logv(classname, options.get(i).toString());
	}
}
