package clickr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.iitbombay.clickr.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import support.Question;
import support.Utils;

/**
 * Fragment to display question for attempting inside activity
 *
 * @author bhargav
 */
public class QuestionFragment extends Fragment {

    private static String CLASSNAME = "QuestionFragment";
    private final static Logger LOGGER = Logger.getLogger(CLASSNAME);

    TextView txtvw_title;
    TextView txtvw_question;
    RadioGroup rg_options;
    LinearLayout ll_checkboxes;
    LinearLayout ll_truefalse;
    Button btn_true;
    Button btn_false;

    EditText edtxt_textual;

    String title;
    String qtext;
    String qtype;
    String qkind;
    Integer option_count;

    HashMap<Integer, Integer> optionIds = new HashMap<Integer, Integer>();
    JSONArray mcqtemp = new JSONArray();


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

        Question.startTime = new Date().getTime();

        txtvw_title = (TextView) fragactivity.findViewById(R.id.txtvw_title);
        txtvw_question = (TextView) fragactivity.findViewById(R.id.txtvw_question);

        try {
            title = (String) Question.question.get("title");
            qtext = (String) Question.question.get("qtext");
            qtype = (String) Question.question.get("qtype");
            qkind = (String) Question.question.get("qkind");
            option_count = (Integer) Question.question.get("option_count");
        } catch (JSONException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Parsing Question/Options error", e);
            Toast.makeText(fragactivity, "Parsing Question/Options error", Toast.LENGTH_SHORT).show();
            gotoLoginPage();
        }

        if (!title.equals("")) {
            txtvw_title.setVisibility(View.VISIBLE);
            txtvw_title.setText(title);
        }

        txtvw_question.setText(Html.fromHtml("<b>Q) </b>" + qtext));

        //call that particular method according to question type
        if (qtype.equals("single")) singleMCQInit();
        else if (qtype.equals("multiple")) multipleMCQinit();
        else if (qtype.equals("truefalse")) truefalseInit();
        else wordTextualInit();
    }

    /**
     * Display single mcq question
     */
    void singleMCQInit() {
        rg_options = (RadioGroup) fragactivity.findViewById(R.id.rg_options);
        rg_options.setVisibility(View.VISIBLE);

        for (int i = 0; i < option_count; i++) {
            RadioButton row = (RadioButton) layoutinflater.inflate(R.layout.template_radiobtn, rg_options, false);
            try {
                JSONObject option = (JSONObject) Question.options.get(i);
                row.setText((String) option.get("optext"));
            } catch (JSONException e) {
                LOGGER.log(Level.SEVERE, "Json Error while setting the options", e);
                e.printStackTrace();
                gotoLoginPage();
            }
            rg_options.addView(row);
            row.setId(Utils.generateViewId());
            optionIds.put(row.getId(), i + 1);
        }

        rg_options.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LOGGER.info(checkedId + " is checked now");
                Question.answers = new JSONArray();
                Question.answers.put(optionIds.get(checkedId));
            }
        });
    }

    /**
     * Display multiple mcq question
     */
    void multipleMCQinit() {
        ll_checkboxes = (LinearLayout) fragactivity.findViewById(R.id.ll_checkboxes);
        ll_checkboxes.setVisibility(View.VISIBLE);

        mcqtemp = new JSONArray();
        for (int i = 0; i < option_count; i++) {
            CheckBox row = (CheckBox) layoutinflater.inflate(R.layout.template_checkbox, ll_checkboxes, false);
            try {
                JSONObject option = (JSONObject) Question.options.get(i);
                row.setText((String) option.get("optext"));
            } catch (JSONException e) {
                LOGGER.log(Level.SEVERE, "Json Error while setting the options", e);
                e.printStackTrace();
                gotoLoginPage();
            }
            ll_checkboxes.addView(row);
            row.setId(Utils.generateViewId());
            optionIds.put(row.getId(), i + 1);

            mcqtemp.put(false);

            row.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    boolean checked = ((CheckBox) view).isChecked();

                    int checkedid = optionIds.get(view.getId());

                    try {
                        mcqtemp.put(checkedid, checked);
                        Question.answers = new JSONArray();
                        for (int i = 0; i < mcqtemp.length(); i++) {
                            if (mcqtemp.getBoolean(i)) Question.answers.put(i + 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LOGGER.log(Level.SEVERE, "Json on checkbox click error: ", e);
                    }
                }
            });
        }
    }

    /**
     * Display true or false question
     */
    void truefalseInit() {
        ll_truefalse = (LinearLayout) fragactivity.findViewById(R.id.ll_truefalse);
        ll_truefalse.setVisibility(View.VISIBLE);
        btn_true = (Button) fragactivity.findViewById(R.id.btn_true);
        btn_false = (Button) fragactivity.findViewById(R.id.btn_false);


        btn_true.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                btn_true.setBackgroundResource(R.drawable.btn_green_style);
                btn_true.setTextColor(fragactivity.getResources().getColor(android.R.color.white));
                btn_false.setBackgroundResource(R.drawable.btn_grey_style);
                btn_false.setTextColor(fragactivity.getResources().getColor(android.R.color.black));

                Question.answers = new JSONArray();
                Question.answers.put(true);
            }
        });


        btn_false.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                btn_false.setBackgroundResource(R.drawable.btn_red_style);
                btn_false.setTextColor(fragactivity.getResources().getColor(android.R.color.white));
                btn_true.setBackgroundResource(R.drawable.btn_grey_style);
                btn_true.setTextColor(fragactivity.getResources().getColor(android.R.color.black));

                Question.answers = new JSONArray();
                Question.answers.put(false);
            }
        });
    }

    /**
     * Display word question
     */
    void wordTextualInit() {
        edtxt_textual = (EditText) fragactivity.findViewById(R.id.edtxt_textual);

        edtxt_textual.setVisibility(View.VISIBLE);

        Question.answers = new JSONArray();

        edtxt_textual.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = "";

                if (qtype.equals("word")) {
                    result = s.toString().replaceAll(" ", "");
                    if (!s.toString().equals(result)) {
                        edtxt_textual.setText(result);
                        edtxt_textual.setSelection(result.length());
                    }
                } else if (qtype.equals("short")) result = s.toString();
                else if (qtype.equals("integer")) {
                    result = s.toString();
                    if (result.matches("[-+]?")) return;
                    else {
                        try {
                            Integer test = Integer.parseInt(result);
                            test++;
                        } catch (NumberFormatException e) {
                            result = result.substring(0, result.length() - 1);
                            edtxt_textual.setText(result);
                            edtxt_textual.setSelection(result.length());
                        }
                    }
                } else if (qtype.equals("float")) {
                    result = s.toString();
                    if (result.matches("[-+]?")) return;
                    else {
                        try {
                            Double test = Double.parseDouble(result);
                            test++;
                        } catch (NumberFormatException e) {
                            result = result.substring(0, result.length() - 1);
                            edtxt_textual.setText(result);
                            edtxt_textual.setSelection(result.length());
                        }
                    }
                } else {
                    result = "";
                    LOGGER.info("Textual with undefined type!");
                }

                try {
                    Question.answers.put(0, result.trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                    LOGGER.log(Level.SEVERE, "Json error!", e);
                }
            }
        });
    }

    /**
     * disable question options
     */
    public void disableBtns() {
        if (qtype.equals("single")) {
            for (int i = 0; i < rg_options.getChildCount(); i++) {
                rg_options.getChildAt(i).setEnabled(false);
            }
        } else if (qtype.equals("multiple")) {
            for (int i = 0; i < ll_checkboxes.getChildCount(); i++) {
                ll_checkboxes.getChildAt(i).setEnabled(false);
            }
        } else if (qtype.equals("truefalse")) {
            btn_true.setEnabled(false);
            btn_false.setEnabled(false);
        } else {
            edtxt_textual.setEnabled(false);
        }
    }

    public void gotoLoginPage() {
        Intent intent = new Intent(fragactivity, LoginPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
