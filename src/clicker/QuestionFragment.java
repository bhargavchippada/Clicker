package clicker;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import support.Question;
import support.Utils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.iitbombay.clicker.R;

public class QuestionFragment extends Fragment{

	private String classname = "QuestionFragment";

	RadioGroup rg_options;
	Question question;

	HashMap<Integer,Integer> optionIds;

	LayoutInflater layoutinflater;

	FragmentActivity fragactivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layoutinflater = inflater;
		fragactivity = getActivity();
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_question, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		optionIds = new HashMap<Integer, Integer>();

		question = ApplicationContext.getThreadSafeQuestion();

		if(question.type==-1) Toast.makeText(fragactivity, "Invalid Question!", Toast.LENGTH_SHORT).show();

		if(question.type==0) singleMCQInit();
	}

	void singleMCQInit(){
		rg_options = (RadioGroup) fragactivity.findViewById(R.id.rg_options);

		for(int i=0;i<question.options.length();i++){
			RadioButton row = (RadioButton) layoutinflater.inflate(R.layout.template_radiobtn, rg_options, false);
			try {
				row.setText(question.options.get(i).toString());
			} catch (JSONException e) {
				Utils.logv(classname,"Json Error while setting the options", e);
				e.printStackTrace();
			}
			rg_options.addView(row);
			optionIds.put(row.getId(), i);
			Utils.logv(classname, row.getId()+"");
		}

		rg_options.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Utils.logv(classname, checkedId+" is checked now");
				question.answers=new JSONArray();
				question.answers.put(optionIds.get(checkedId));
			}
		});
	}

}
