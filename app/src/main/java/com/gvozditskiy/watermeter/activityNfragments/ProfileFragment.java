package com.gvozditskiy.watermeter.activityNfragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gvozditskiy.watermeter.ColdRecyclerAdapter;
import com.gvozditskiy.watermeter.Flat;
import com.gvozditskiy.watermeter.HotRecyclerAdapter;
import com.gvozditskiy.watermeter.Meter;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.Utils;
import com.gvozditskiy.watermeter.interfaces.OnSaveListener;
import com.gvozditskiy.watermeter.interfaces.OnUpdate;
import com.gvozditskiy.watermeter.interfaces.RegisterSaveInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements OnSaveListener {
    TextInputEditText name;
    TextInputEditText secName;
    TextInputEditText otch;
    TextInputEditText street;
    TextInputEditText building;
    TextInputEditText flat;
    TextInputEditText telephone;
    ImageButton infoBtn;
    AppCompatSpinner spinner;
    TextInputLayout streetLayout;
    ImageButton flatEditBtn;
    RadioGroup radioGroup;
    TextView flatName;
    RecyclerView coldRecycler;
    RecyclerView hotRecycler;
    ImageButton addCold;
    ImageButton addHot;
    RegisterSaveInterface registerInterface;

    SharedPreferences sp;
    final List<Meter> coldMeterList = new ArrayList<>();
    final List<Meter> hotMeterList = new ArrayList<>();
    final List<Map<String, Object>> savedPage = new ArrayList<>();

    final ColdRecyclerAdapter coldAdapter = new ColdRecyclerAdapter(getContext(), coldMeterList);
    final HotRecyclerAdapter hotAdapter = new HotRecyclerAdapter(getContext(), hotMeterList);


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences(Utils.PREFS_PROFILE, Context.MODE_PRIVATE);
        coldAdapter.setContext(getContext());
        hotAdapter.setContext(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterSaveInterface) {
            registerInterface = (RegisterSaveInterface) context;
        } else {
            throw new ClassCastException("Activitie should implements RegisterSaveInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        registerInterface.onRegisterSaveInterface(this);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = (TextInputEditText) view.findViewById(R.id.frag_prof_name);
        secName = (TextInputEditText) view.findViewById(R.id.frag_prof_secname);
        otch = (TextInputEditText) view.findViewById(R.id.frag_prof_otch);
        street = (TextInputEditText) view.findViewById(R.id.frag_prof_street);
        building = (TextInputEditText) view.findViewById(R.id.frag_prof_building);
        flat = (TextInputEditText) view.findViewById(R.id.frag_prof_flat);
        telephone = (TextInputEditText) view.findViewById(R.id.frag_prof_tele);
        infoBtn = (ImageButton) view.findViewById(R.id.frag_profile_info);
        spinner = (AppCompatSpinner) view.findViewById(R.id.frag_prof_spinner);
        streetLayout = (TextInputLayout) view.findViewById(R.id.frag_prof_street_layout);
        flatEditBtn = (ImageButton) view.findViewById(R.id.frag_prof_editbtn);
        radioGroup = (RadioGroup) view.findViewById(R.id.frag_prof_radiogroup);
        flatName = (TextView) view.findViewById(R.id.frag_prof_flatname);
        coldRecycler = (RecyclerView) view.findViewById(R.id.frag_prof_recycler_cold);
        hotRecycler = (RecyclerView) view.findViewById(R.id.frag_prof_recycler_hot);
        addCold = (ImageButton) view.findViewById(R.id.frag_prof_addCold);
        addHot = (ImageButton) view.findViewById(R.id.frag_prof_addHot);

        streetLayout.setHint((String) spinner.getSelectedItem());

        flatEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlatEditorFragment fragment = new FlatEditorFragment();
                FragmentManager fm = getChildFragmentManager();
                fragment.setOnUpdateListener(new OnUpdate() {
                    @Override
                    public void onUpdate() {
                        setUpRadioGroup();

                    }
                });
                fragment.show(fm, "");

                //// TODO: 27.12.2016 переделать в обычный фрагмент 
            }
        });

        setUpRadioGroup();
        initColdRecycler();
        initHotRecycler();

        addCold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int coldListSize = coldMeterList.size();
                String uid = Utils.getFlatList(getContext()).get(radioGroup.getCheckedRadioButtonId()).getUuid().toString();
                String name = coldListSize >= 1 ? "ХВ" + String.valueOf(coldListSize+1) : "ХВ";
                coldMeterList.add(new Meter(name, Meter.TYPE_COLD, uid));
                coldAdapter.notifyItemInserted(coldListSize);
            }
        });

        addHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hotListSize = hotMeterList.size();
                String uid = Utils.getFlatList(getContext()).get(radioGroup.getCheckedRadioButtonId()).getUuid().toString();
                String name = hotListSize >= 1 ? "ГВ" + String.valueOf(hotListSize+1) : "ГВ";
                hotMeterList.add(new Meter(name, Meter.TYPE_HOT, uid));
                hotAdapter.notifyItemInserted(hotListSize);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i != -1) {
                    flatName.setText(Utils.getFlatList(getContext()).get(i).getName());
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] array = getResources().getStringArray(R.array.streets);
                streetLayout.setHint(array[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Linkify the message
        String msg = getString(R.string.frag_prof_info_message);
        final SpannableString s = new SpannableString(msg);
        Linkify.addLinks(s, Linkify.ALL);
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog d = new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.frag_prof_info_title))
                        .setMessage(s)
                        .setPositiveButton(android.R.string.ok, null)
                        .create();
                d.show();
                Linkify.addLinks((TextView) d.findViewById(android.R.id.message), Linkify.ALL);
                ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            }
        });

        /**
         * если savedState!=null, заполняем поля из Bundle
         * иначе данные берем из SharedPrefs
         */
        if (savedInstanceState == null) {
            name.setText(sp.getString(Utils.PREFS_PROFILE_NAME, ""));
            secName.setText(sp.getString(Utils.PREFS_PROFILE_SECNAME, ""));
            otch.setText(sp.getString(Utils.PREFS_PROFILE_OTCH, ""));
            street.setText(sp.getString(Utils.PREFS_PROFILE_STREET, ""));
            spinner.setSelection(sp.getInt(Utils.PREFS_PROFILE_STREET_TYPE, 0), true);
            building.setText(sp.getString(Utils.PREFS_PROFILE_BUILDING, ""));
            flat.setText(sp.getString(Utils.PREFS_PROFILE_FLAT, ""));
            telephone.setText(sp.getString(Utils.PREFS_PROFILE_TELE, ""));
        } else {
            name.setText(savedInstanceState.getString(Utils.PREFS_PROFILE_NAME, ""));
            secName.setText(savedInstanceState.getString(Utils.PREFS_PROFILE_SECNAME, ""));
            otch.setText(savedInstanceState.getString(Utils.PREFS_PROFILE_OTCH, ""));
            street.setText(savedInstanceState.getString(Utils.PREFS_PROFILE_STREET, ""));
            building.setText(savedInstanceState.getString(Utils.PREFS_PROFILE_BUILDING, ""));
            flat.setText(savedInstanceState.getString(Utils.PREFS_PROFILE_FLAT, ""));
            telephone.setText(savedInstanceState.getString(Utils.PREFS_PROFILE_TELE, ""));
            spinner.setSelection(savedInstanceState.getInt(Utils.PREFS_PROFILE_STREET_TYPE, 0), true);
        }
    }

    private void setUpRadioGroup() {
        final List<Flat> flatList = Utils.getFlatList(getContext());
        int id = 0;
        radioGroup.removeAllViews();
        for (Flat flat : flatList) {
            RadioButton rBtn = new RadioButton(getContext());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(150, 150);
            params.setMargins(20, 20, 20, 20);
            rBtn.setLayoutParams(params);
            rBtn.setId(id++);
//                            rBtn.setText(flat.getName());
            rBtn.setButtonDrawable(null);
            rBtn.setBackground(getResources().getDrawable(R.drawable.flat_selector));
            radioGroup.addView(rBtn);
        }
        try {
            flatName.setText(flatList.get(0).getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        radioGroup.clearCheck();
        radioGroup.check(0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.PREFS_PROFILE_NAME, name.getText().toString());
        outState.putString(Utils.PREFS_PROFILE_SECNAME, secName.getText().toString());
        outState.putString(Utils.PREFS_PROFILE_OTCH, otch.getText().toString());
        outState.putString(Utils.PREFS_PROFILE_STREET, street.getText().toString());
        outState.putString(Utils.PREFS_PROFILE_BUILDING, building.getText().toString());
        outState.putString(Utils.PREFS_PROFILE_FLAT, flat.getText().toString());
        outState.putString(Utils.PREFS_PROFILE_TELE, telephone.getText().toString().replace(" ", ""));
        outState.putInt(Utils.PREFS_PROFILE_STREET_TYPE, spinner.getSelectedItemPosition());
    }

    private boolean checkFields() {
        Boolean b = true;

        if (secName.getText().toString().equals("")) {
            secName.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_nosecname_message), Toast.LENGTH_SHORT).show();
            b = false;
            return false;

        }

        if (name.getText().toString().equals("")) {
            name.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_noname_message), Toast.LENGTH_SHORT).show();
            b = false;
            return false;
        }

        if (otch.getText().toString().equals("")) {
            otch.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_nootch_message), Toast.LENGTH_SHORT).show();
            b = false;
            return false;

        }
        if (street.getText().toString().equals("")) {
            street.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_nostreet_message), Toast.LENGTH_SHORT).show();
            b = false;
            return false;

        }
        if (building.getText().toString().equals("")) {
            building.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_nobuilding_message), Toast.LENGTH_SHORT).show();
            b = false;
            return false;

        }
        if (flat.getText().toString().equals("")) {
            flat.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_noflat_message), Toast.LENGTH_SHORT).show();
            b = false;
            return false;

        }
        if (telephone.getText().toString().equals("")) {
            telephone.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_notelephone_message), Toast.LENGTH_SHORT).show();
            b = false;
            return false;

        }
        return b;
    }

    private void initColdRecycler() {
        coldMeterList.add(new Meter("ХВ1", Meter.TYPE_COLD, "g"));
//        coldMeterList.add(new Meter("ХВ2", Meter.TYPE_COLD, "ghh"));
        coldAdapter.setOnClickInterface(new ColdRecyclerAdapter.OnClickInterface() {
            @Override
            public void onClick(int i) {
                coldMeterList.remove(i);
//                coldAdapter.notifyDataSetChanged();
                coldAdapter.notifyItemRemoved(i);
                coldAdapter.notifyItemRangeChanged(i, coldMeterList.size()-i);
            }
        });
        coldRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        coldRecycler.setAdapter(coldAdapter);

    }

    private void initHotRecycler() {
        hotMeterList.add(new Meter("ГВ1", Meter.TYPE_HOT, "g"));
//        hotMeterList.add(new Meter("ГВ2", Meter.TYPE_HOT, "ghh"));
//        hotMeterList.add(new Meter("ГВ3", Meter.TYPE_HOT, "ghh"));
        hotAdapter.setOnClickInterface(new HotRecyclerAdapter.OnClickHotInterface() {
            @Override
            public void onClick(int i) {
                hotMeterList.remove(i);
                hotAdapter.notifyItemRemoved(i);
                hotAdapter.notifyItemRangeChanged(i, hotMeterList.size()-i);

            }
        });
        hotRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        hotRecycler.setAdapter(hotAdapter);

    }



    @Override
    public void onSave() {
        // 1 проверяем все ли введены данные
        if (checkFields()) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(Utils.PREFS_PROFILE_NAME, name.getText().toString());
            editor.putString(Utils.PREFS_PROFILE_SECNAME, secName.getText().toString());
            editor.putString(Utils.PREFS_PROFILE_OTCH, otch.getText().toString());
            editor.putString(Utils.PREFS_PROFILE_STREET, street.getText().toString());
            editor.putString(Utils.PREFS_PROFILE_BUILDING, building.getText().toString());
            editor.putString(Utils.PREFS_PROFILE_FLAT, flat.getText().toString());
            editor.putString(Utils.PREFS_PROFILE_TELE, telephone.getText().toString().replace(" ", ""));
            editor.putInt(Utils.PREFS_PROFILE_STREET_TYPE, spinner.getSelectedItemPosition());
            editor.commit();
            getActivity().finish();
//            editor.apply();
        }
        // 2 вносим все данные в SharedPrefs
    }
}
