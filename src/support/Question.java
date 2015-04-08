package support;

import org.json.JSONArray;
import org.json.JSONException;

public class Question{
	private String classname = "Question";
	
	public String ID;
	public String title;
	public String question;
	public int type;
	public JSONArray options = new JSONArray();
	public boolean feedback;
	public boolean timed;
	public int time;
	
	public long startTime;
	public long timeTook;
	public long submitTime;
	
	public JSONArray answers = new JSONArray();
	
	public void clear(){
		title=null;
		question=null;
		type=-1;
		options = new JSONArray();
		answers = new JSONArray();
		feedback=false;
		timed=false;
		time=-1;
	}
}
