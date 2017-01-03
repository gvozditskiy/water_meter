package com.gvozditskiy.watermeter.activityNfragments;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.util.Log;
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
import com.gvozditskiy.watermeter.Person;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.Utils;
import com.gvozditskiy.watermeter.database.BaseHelper;
import com.gvozditskiy.watermeter.database.DbSchema;
import com.gvozditskiy.watermeter.interfaces.OnSaveListener;
import com.gvozditskiy.watermeter.interfaces.OnUpdate;
import com.gvozditskiy.watermeter.interfaces.RegisterSaveInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gvozditskiy.watermeter.database.DbSchema.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements OnSaveListener {

    private final String TAG_LOG = "ProfileFragment";
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

    //объекты, привязанные к квартире
    private Person person;
    private List<Meter> coldMeterList = new ArrayList<>();
    private List<Meter> hotMeterList = new ArrayList<>();
    private final List<Map> savedPage = new ArrayList<>();

    private ColdRecyclerAdapter coldAdapter = new ColdRecyclerAdapter(getContext(), coldMeterList);
    private HotRecyclerAdapter hotAdapter = new HotRecyclerAdapter(getContext(), hotMeterList);

    private boolean isRadioGroupSetup;

    int prevId;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG, "onCreate");
        sp = getActivity().getSharedPreferences(Utils.PREFS_PROFILE, Context.MODE_PRIVATE);
        coldAdapter.setContext(getContext());
        hotAdapter.setContext(getContext());


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG_LOG, "onAttach");
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
        Log.d(TAG_LOG, "onCreateViewCreateView");
        registerInterface.onRegisterSaveInterface(this);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG_LOG, "onViewCreated");
        if (savedInstanceState != null) {
            savedPage.addAll((List<Map>) savedInstanceState.getSerializable("savePage"));
//            isRadioGroupSetup = true; //выставляем флаг, чтобы не сохранять состояние квартиры при инициализации радиогруппы
        }
        //инициализация Views
        initViews(view);

        streetLayout.setHint((String) spinner.getSelectedItem());

        flatEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FlatEditorFragment fragment = new FlatEditorFragment();
                FragmentManager fm = getChildFragmentManager();
                fragment.setOnUpdateListener(new OnUpdate() {
                    @Override
                    public void onUpdate() {
                        setUpRadioGroup(savedInstanceState);

                    }
                });
                fragment.show(fm, "");
            }
        });

        setUpRadioGroup(savedInstanceState);
//        initColdRecycler(savedInstanceState);
//        initHotRecycler(savedInstanceState);

        addCold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int coldListSize = coldMeterList.size();
                String uid = Utils.getFlatList(getContext()).get(radioGroup.getCheckedRadioButtonId()).getUuid().toString();
                String name = coldListSize >= 1 ? "ХВ" + String.valueOf(coldListSize + 1) : "ХВ";
                coldMeterList.add(new Meter(name, Meter.TYPE_COLD, uid));
                coldAdapter.notifyItemInserted(coldListSize);
            }
        });

        addHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hotListSize = hotMeterList.size();
                String uid = Utils.getFlatList(getContext()).get(radioGroup.getCheckedRadioButtonId()).getUuid().toString();
                String name = hotListSize >= 1 ? "ГВ" + String.valueOf(hotListSize + 1) : "ГВ";
                hotMeterList.add(new Meter(name, Meter.TYPE_HOT, uid));
                hotAdapter.notifyItemInserted(hotListSize);
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

        //
        setupInfoDialog();


        /**
         * если savedState!=null, заполняем поля из Bundle
         * иначе данные берем из SharedPrefs
         */
       /* if (savedInstanceState == null) {
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
        }             */

    }

    /**
     * Задает onClickListener для infoBtn, создает AlertDialog
     */
    private void setupInfoDialog() {
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
    }

    /**
     * Инициализирует все view (view.findViewById() )
     *
     * @param view View корневого лэйаута
     */
    private void initViews(View view) {
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
    }

    private void setUpRadioGroup(final Bundle savedState) {
        Log.d(TAG_LOG, "setUpRadioGroup()");
        isRadioGroupSetup = true;
        final List<Flat> flatList = Utils.getFlatList(getContext());
        if (flatList.size()==0) {
            Flat flat = new Flat("Моя квартира");
            flatList.add(flat);
            SQLiteDatabase db = new BaseHelper(getContext()).getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(FlatsTable.Cols.NAME, flat.getName());
            cv.put(FlatsTable.Cols.UUID, flat.getUuid().toString());
            db.insert(FlatsTable.NAME, null, cv);
        }
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
            radioGroup.setOnCheckedChangeListener(null);
            radioGroup.clearCheck();
        } catch (Exception e) {
            e.printStackTrace();
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                /**
                 * при переключении радиогруппы сохранить состояние текущего экрана
                 * не делать сохранение, если это инициализация после изменения конфигурации!!!!
                 */
                if (!isRadioGroupSetup) { //////////////????????????????????
                    saveFlatState();
                }
                if (i != -1) {
                    Log.d(TAG_LOG, "OnCheckedChange");
                    try {
                        flatName.setText(Utils.getFlatList(getContext()).get(i).getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    setupFlat();
                }
            }
        });


        radioGroup.check(0);
        prevId = radioGroup.getCheckedRadioButtonId();
        isRadioGroupSetup = false;

    }

    /**
     * Заполняет все View квартиры
     */
    private void setupFlat() {
        Log.d(TAG_LOG, "setupFlat");
        int pos = radioGroup.getCheckedRadioButtonId();
        Person person;

        if (pos < savedPage.size() && savedPage.size()!=0) {
            Map map = savedPage.get(pos);
            person = Person.personFromMap((Map<String, String>) map.get("person"));
            Bundle tempBundle = new Bundle();
            tempBundle.putSerializable("cold", (Serializable) map.get("cold"));
            tempBundle.putSerializable("hot", (Serializable) map.get("hot"));
            initColdRecycler(tempBundle);
            initHotRecycler(tempBundle);
        } else {
            //// TODO: 30.12.2016 подтягивать данные из базы, если первый запуск 
            // TODO: 02.01.2017 что здесь происходит???? 
//            coldMeterList.clear();
//            hotMeterList.clear();
            coldMeterList = new ArrayList<>();
            hotMeterList = new ArrayList<>();
            person = new Person();
            initColdRecycler(null);
            initHotRecycler(null);
        }
        name.setText(person.getName());
        secName.setText(person.getSurname());
        otch.setText(person.getPatronymic());
        street.setText(person.getStreet());
        building.setText(person.getBuilding());
        flat.setText(person.getFlat());
        telephone.setText(person.getPhone());
        spinner.setSelection(
                Arrays.asList(getResources().getStringArray(R.array.streets)).indexOf(person.getsType()),
                true
        );

    }

    private void saveFlatState() {
        Log.d(TAG_LOG, "saveFlatState");
        /**
         * сохранение состояния квартиры
         */
        //person
        person = new Person();
        person.setName(name.getText().toString());
        person.setSurname(secName.getText().toString());
        person.setPatronymic(otch.getText().toString());
        person.setStreet(street.getText().toString());
        person.setBuilding(building.getText().toString());
        person.setFlat(flat.getText().toString());
        person.setFlat_uuid(Utils.getFlatList(getContext()).get(prevId).getUuid().toString());
        person.setPhone(telephone.getText().toString().replace(" ", ""));
        person.setsType(String.valueOf(spinner.getSelectedItem()));

        Map<String, Object> saveMap = new HashMap<>();
        saveMap.put("cold", coldMeterList);
        saveMap.put("hot", hotMeterList);
        saveMap.put("person", person.personToMap());

        try {
            savedPage.set(prevId, saveMap);
        } catch (Exception e) {
            savedPage.add(saveMap);
        }
        prevId = radioGroup.getCheckedRadioButtonId();
    }

    private void saveFlatState(int pos) {
        Log.d(TAG_LOG, "saveFlatState");
        /**
         * сохранение состояния квартиры
         */
        //person
        person = new Person();
        person.setName(name.getText().toString());
        person.setSurname(secName.getText().toString());
        person.setPatronymic(otch.getText().toString());
        person.setStreet(street.getText().toString());
        person.setBuilding(building.getText().toString());
        person.setFlat(flat.getText().toString());
        person.setFlat_uuid(Utils.getFlatList(getContext()).get(pos).getUuid().toString());
        person.setPhone(telephone.getText().toString().replace(" ", ""));
        person.setsType(String.valueOf(spinner.getSelectedItem()));

        Map<String, Object> saveMap = new HashMap<>();
        saveMap.put("cold", coldMeterList);
        saveMap.put("hot", hotMeterList);
        saveMap.put("person", person.personToMap());

        try {
            savedPage.set(pos, saveMap);
        } catch (Exception e) {
            savedPage.add(saveMap);
        }
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

        saveFlatState();
        outState.putSerializable("savePage", (Serializable) savedPage);
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

    private void initColdRecycler(Bundle savedState) {
        if (savedState != null) {
            List<Meter> savedColdList = (List<Meter>) savedPage.get(radioGroup.getCheckedRadioButtonId()).get("cold");

            coldMeterList = new ArrayList<>();
            coldMeterList.addAll(savedColdList);
        } else {
            String flatUuid = "";
            try {
                flatUuid = Utils.getFlatList(getContext()).get(radioGroup.getCheckedRadioButtonId()).getUuid().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            coldMeterList.add(new Meter("ХВ1", Meter.TYPE_COLD, flatUuid));
        }
        coldAdapter = new ColdRecyclerAdapter(getContext(), coldMeterList);
//        coldMeterList.add(new Meter("ХВ2", Meter.TYPE_COLD, "ghh"));
        coldAdapter.setOnClickInterface(new ColdRecyclerAdapter.OnClickInterface() {
            @Override
            public void onClick(int i) {
                coldMeterList.remove(i);
//                coldAdapter.notifyDataSetChanged();
                coldAdapter.notifyItemRemoved(i);
                coldAdapter.notifyItemRangeChanged(i, coldMeterList.size() - i);
            }
        });
        coldRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        coldRecycler.setAdapter(coldAdapter);

    }

    private void initHotRecycler(Bundle savedState) {
        if (savedState != null) {
            List<Meter> savedHotList = (List<Meter>) savedPage.get(radioGroup.getCheckedRadioButtonId()).get("hot");
            hotMeterList = new ArrayList<>();
            hotMeterList.addAll(savedHotList);
        } else {
            String flatUuid = "";
            try {
                flatUuid = Utils.getFlatList(getContext()).get(radioGroup.getCheckedRadioButtonId()).getUuid().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            hotMeterList.add(new Meter("ГВ1", Meter.TYPE_HOT, flatUuid));
        }
        hotAdapter = new HotRecyclerAdapter(getContext(), hotMeterList);
//        hotMeterList.add(new Meter("ГВ2", Meter.TYPE_HOT, "ghh"));
//        hotMeterList.add(new Meter("ГВ3", Meter.TYPE_HOT, "ghh"));
        hotAdapter.setOnClickInterface(new HotRecyclerAdapter.OnClickHotInterface() {
            @Override
            public void onClick(int i) {
                hotMeterList.remove(i);
                hotAdapter.notifyItemRemoved(i);
                hotAdapter.notifyItemRangeChanged(i, hotMeterList.size() - i);

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
        saveFlatState(radioGroup.getCheckedRadioButtonId());
        List<Meter> list = new ArrayList<>();
        List<Person> pList = new ArrayList<>();
        int rSize = radioGroup.getChildCount();
        for (int i = 0; i < rSize; i++) {
            list.addAll((List<Meter>) savedPage.get(i).get("cold"));
            list.addAll((List<Meter>) savedPage.get(i).get("hot"));
            pList.add(new Person().personFromMap((Map<String, String>) savedPage.get(i).get("person")));
        }
        SQLiteDatabase db = new BaseHelper(getContext()).getWritableDatabase();
        db.execSQL("delete from " + MeterTable.NAME);
        db.execSQL("delete from " + UserTable.NAME);
        for (Meter meter : list) {
            ContentValues meterCV = new ContentValues();
            meterCV.put(MeterTable.Cols.NAME, meter.getName());
            meterCV.put(MeterTable.Cols.TYPE, meter.getType());
            meterCV.put(MeterTable.Cols.FLAT_UUID, meter.getFlatUUID());
            meterCV.put(MeterTable.Cols.UUID, meter.getUuid().toString());
            db.insert(MeterTable.NAME, null, meterCV);
        }

        for (Person p : pList) {
            ContentValues pCV = new ContentValues();
            pCV.put(UserTable.Cols.FIRSTNAME, p.getName());
            pCV.put(UserTable.Cols.SECONDNAME, p.getSurname());
            pCV.put(UserTable.Cols.PATRONYMIC, p.getPatronymic());
            pCV.put(UserTable.Cols.STREET_TYPE, p.getsType());
            pCV.put(UserTable.Cols.STREET, p.getStreet());
            pCV.put(UserTable.Cols.BUILDING, p.getBuilding());
            pCV.put(UserTable.Cols.FLAT, p.getFlat());
            pCV.put(UserTable.Cols.FLAT_UUID, p.getFlat_uuid());
            pCV.put(UserTable.Cols.PHONE, p.getPhone());
            db.insert(UserTable.NAME, null, pCV);
        }
        // TODO: 30.12.2016 сохранить все данные в базу данных 
        // 2 вносим все данные в SharedPrefs
    }
}
