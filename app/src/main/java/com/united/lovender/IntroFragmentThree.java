package com.united.lovender;


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
import android.widget.EditText;
import android.widget.ImageButton;
import com.jaredrummler.materialspinner.MaterialSpinner;


public class IntroFragmentThree extends Fragment {

    FragmentActivity context;
    private String[] religionsList = {"Ahmadiyya","Aladura","Amish","Anglicanism","Asatru","Assemblies of God","Atheism","Baha'i Faith","Baptists","Bon","Buddhism","Candomble","Cao Dai","Cathari","Catholicism","Charismatic movement","Chinese Religion","Christadelphians","Christian Science","Christianity","Church of God","Church of God in Christ","Church of Satan","Confucianism","Conservative Judaism","Deism","Donatism","Dragon Rouge","Druze","Eastern Orthodox Church","Eckankar","ELCA","Epicureanism","Evangelicalism","Falun Gong","Foursquare Church","Gnosticism","Greek Religion","Hare Krishna","Hasidism","Hellenic Reconstructionism","Hinduism","Illuminati","Intelligent Design","Islam","Jainism","Jehovah's Witnesses","Judaism","Kabbalah","Kemetic Reconstructionism","Lutheranism","Mahayana Buddhism","Mayan Religion","Methodism","Mithraism","Mormonism","Neopaganism","New Age","New Thought","Nichiren","Norse Religion","Olmec Religion","Oneness Pentecostalism","Orthodox Judaism","Pentecostalism","Presbyterianism","Priory of Sion","Protestantism","Pure Land Buddhism","Quakers","Rastafarianism","Reform Judaism","Rinzai Zen Buddhism","Roman Religion","Satanism","Scientology","Seventh-Day Adventism","Shaivism","Shi'a Islam","Shinto","Sikhism","Soto Zen Buddhism","Spiritualism","Stoicism","Sufism","Sunni Islam","Taoism","Tendai Buddhism","Theravada Buddhism","Tibetan Buddhism","Typhonian Order","Umbanda","Unification Church","Unitarian Universalism","Vaishnavism","Vajrayana Buddhism","Vedanta","Vineyard Churches","Voodoo","Westboro Baptist Church","Wicca","Worldwide Church of God","Yezidi","Zen","Zionism","Zoroastrianism","not prefer to say"};
    private String[] zodiacList = {"Aquarius", "Scorpio", "Pisces","Aries","Taurus","Gemini","Cancer","Leo","Virgo","Libra","Sagittarius","Capricorn","not prefer to say"};
    private String zodiac, religion;
    private EditText about_et,school_et;
    ImageButton next_b;

    public IntroFragmentThree() {
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
        return inflater.inflate(R.layout.fragment_intro_fragment_three, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialSpinner religion_spinner = view.findViewById(R.id.ft_religion_spinner);
        MaterialSpinner zodiac_spinner = view.findViewById(R.id.ft_zodiac_spinner);
        about_et = view.findViewById(R.id.ft_about_et);
        school_et = view.findViewById(R.id.ft_school_et);
        next_b = view.findViewById(R.id.ft_next_btn);

        religion_spinner.setItems(religionsList);
        zodiac_spinner.setItems(zodiacList);

        ((Intro)context).enablePaging(false);

        religion_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                religion = item.toString();
                if (TextUtils.equals(religion, "not prefer to say")){
                    religion = "";
                }
            }
        });
        zodiac_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                zodiac = item.toString();
                if (TextUtils.equals(zodiac, "not prefer to say")){
                    zodiac = "";
                }
            }
        });

        next_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String about = about_et.getText().toString().trim();
                String school = school_et.getText().toString().trim();

                MySharedPrefs mySharedPrefs = new MySharedPrefs(context);
                mySharedPrefs.setIntroProfileData3(about, school, religion, zodiac);
                ((Intro)context).setCurrentItem(3, true);
            }
        });
    }


    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

//    end
}
