package com.united.lovender;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kevalpatel2106.rulerpicker.RulerValuePicker;
import com.kevalpatel2106.rulerpicker.RulerValuePickerListener;
import java.util.Calendar;

public class IntroFragmentTwo extends Fragment implements CalendarDatePickerDialogFragment.OnDateSetListener {

    String[] bodyList = {"Lean", "Athletic", "Muscular"
            , "Fat", "Chubby", "Slim", "fit", "not prefer to say"};
    private RulerValuePicker heightRanger;
    String dob;
    private TextView height_number_tv;
    private TextView dob_tv;
    private String body_type, height;
    ImageButton next_b;
    FragmentActivity context;

    public IntroFragmentTwo() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.context = (FragmentActivity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_introfragment_two, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        height_number_tv = view.findViewById(R.id.fi_height_number_tv);
        heightRanger = view.findViewById(R.id.fi_height_range);
        dob_tv = view.findViewById(R.id.fi_dob_tv);
        MaterialSpinner body_type_spinner = view.findViewById(R.id.fi_body_type_spinner);
        next_b = view.findViewById(R.id.fi_next_btn);


        ((Intro)context).enablePaging(false);
        checkIfAllConditionsMet();

        body_type_spinner.setItems(bodyList);
        body_type_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                body_type = item.toString();
                if (TextUtils.equals(body_type, "not prefer to say")){
                    body_type = "";
                }
                checkIfAllConditionsMet();
            }
        });

        dob_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDatePicker();

            }
        });

        next_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConditionsOnButton()){

                    MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                    mySharedPrefs.setIntroProfileData2(body_type, dob, height);
                    //  ((Intro)context).enablePaging(true);
                    ((Intro)context).setCurrentItem(2, true);
                }
            }
        });

        selectHeight();
    }


    //    select height
    private void selectHeight() {
        heightRanger.setValuePickerListener(new RulerValuePickerListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onValueChange(final int selectedValue) {
                Double value = (double) selectedValue;
                int feet = (int) Math.floor(value / 30.48);
                int inches = (int) Math.round((value / 2.54) - (feet * 12));
                if (inches == 12) inches = 11;
                height_number_tv.setText(selectedValue+"cm/ "+feet+"'"+inches+"''");
                height = String.valueOf(selectedValue);
                checkIfAllConditionsMet();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onIntermediateValueChange(final int selectedValue) {
                Double value = (double) selectedValue;
                int feet = (int) Math.floor(value / 30.48);
                int inches = (int) Math.round((value / 2.54) - (feet * 12));
                if (inches == 12) inches = 11;

                height_number_tv.setText(selectedValue+"cm/ "+feet+"'"+inches+"''");

            }
        });
    }
//    select height

    //    date picker
    private void openDatePicker() {

        int d1 = 1;
        int m1 = 1;
        int y1 = 1990;

        MonthAdapter.CalendarDay min = new MonthAdapter.CalendarDay();
        MonthAdapter.CalendarDay max = new MonthAdapter.CalendarDay();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);

        min.setDay(1950,1,1);
        max.setDay(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(this)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(y1, m1, d1)
                .setDateRange(min, max )
                .setThemeLight()
                .setDoneText("Confirm")
                .setCancelText("Cancel");
        cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                int mo = monthOfYear+1;
                String day1, month1, year1;
                day1 = String.valueOf(dayOfMonth);
                month1 = String.valueOf(mo);
                year1 = String.valueOf(year);

                if (dayOfMonth < 10) day1 = "0"+dayOfMonth;
                if (mo < 10) month1 = "0"+mo;

                String selected_dob = day1+"-"+month1+"-"+year1;
                dob_tv.setText(day1+"       /       "+wordMonth(Integer.parseInt(month1))+"     /       "+year1);
                dob = selected_dob;
                checkIfAllConditionsMet();
            }

        });
        cdp.show(context.getSupportFragmentManager(), "fd");
    }
//    date picker


    //    numeric month to word
    private String wordMonth(int month){
        switch (month){
            case 1: return "JAN";
            case 2: return "FEB";
            case 3: return "MAR";
            case 4: return "APR";
            case 5: return "MAY";
            case 6: return "JUN";
            case 7: return "JUL";
            case 8: return "AUG";
            case 9: return "SEP";
            case 10: return "OCT";
            case 11: return "NOV";
            case 12: return "DEC";
            default:return null;

        }
    }
    //    numeric month to word

    private void checkIfAllConditionsMet() {
        if (!TextUtils.isEmpty(body_type) && !TextUtils.isEmpty(dob) && !TextUtils.isEmpty(height)){
            next_b.setBackground(getResources().getDrawable(R.drawable.ic_next_blue));
           // ((Intro)context).enablePaging(true);
        }
        else {
            next_b.setBackground(getResources().getDrawable(R.drawable.ic_next_gray));
           // ((Intro)context).enablePaging(false);
        }
    }

    private boolean checkConditionsOnButton(){
        ShowToastyMessage showToastyMessage = new ShowToastyMessage(context);
        if (TextUtils.isEmpty(body_type)){
            showToastyMessage.warningMessage("body-type required");
            return false;
        }
        if (TextUtils.isEmpty(dob)){
            showToastyMessage.warningMessage("Birthdate required");
            return false;
        }
        if (TextUtils.isEmpty(height)){
            showToastyMessage.warningMessage("height required");
            return false;
        }
        return true;
    }


    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {

    }


    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

//    end
}
