package support;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author bhargav
 *
 */
public class Question{
	private String classname = "Question";
	
	public String ID; //unique question ID
	public String title; //question title
	public String question; //question content
	/**Type of the question<br>
	 * 0 => single mcq<br>
	 * 1 => multiple mcq<br>
	 * 2 => true or false<br>
	 * 3 => word answer<br>
	 * 4 => short answer
	 */
	public int type;
	public JSONArray options = new JSONArray(); //question options
	public boolean feedback; // feedback enabled or not
	public boolean timed; // timed quiz or not
	public int time; // if timed then this is the time
	
	public long startTime; //quiz start time
	public long timeTook; //time taken to submit answer (submitTime - startTime)
	public long submitTime; //quiz submit time
	
	public JSONArray answers = new JSONArray();
	
	
	/**
	 * Clear the Question info
	 */
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
	
	/**
	 * Print the question
	 */
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
