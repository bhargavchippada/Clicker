package support;

import org.json.JSONArray;
import org.json.JSONException;

public class Question{
	public String classname = "Question";
	
	public String title;
	public String question;
	public int type;
	public JSONArray options = new JSONArray();
	public boolean feedback;
	public boolean timed;
	public int time;
	
	public void clear(){
		title=null;
		question=null;
		type=-1;
		options = new JSONArray();
		feedback=false;
		timed=false;
		time=-1;
	}
	
	public void print() {
		Utils.logv(classname, "title: "+title);
		Utils.logv(classname, "question: "+question);
		Utils.logv(classname, "type: "+type);
		Utils.logv(classname, "options: ");
		for(int i=0; i<options.length();i++)
			try {
				Utils.logv(classname, options.get(i).toString());
			} catch (JSONException e) {
				Utils.logv(classname, "json array error: ",e);
				e.printStackTrace();
			}
		Utils.logv(classname, "feedback: "+feedback);
		Utils.logv(classname, "timed: "+timed);
		Utils.logv(classname, "time: "+time);
	}
}
