package com.gvozditskiy.watermeter.activityNfragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.TwoStatePreference;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.Utils;
import com.gvozditskiy.watermeter.interfaces.OnSaveListener;
import com.gvozditskiy.watermeter.interfaces.RegisterInterface;
import com.gvozditskiy.watermeter.interfaces.RegisterSaveInterface;

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

    RegisterSaveInterface registerInterface;

    SharedPreferences sp;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences(Utils.PREFS_PROFILE, Context.MODE_PRIVATE);
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

        streetLayout.setHint((String)spinner.getSelectedItem());

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
                ((TextView)d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            }
        });

        /**
         * если savedState!=null, заполняем поля из Bundle
         * иначе данные берем из SharedPrefs
         */
        if (savedInstanceState==null) {
            name.setText(sp.getString(Utils.PREFS_PROFILE_NAME, ""));
            secName.setText(sp.getString(Utils.PREFS_PROFILE_SECNAME, ""));
            otch.setText(sp.getString(Utils.PREFS_PROFILE_OTCH, ""));
            street.setText(sp.getString(Utils.PREFS_PROFILE_STREET, ""));
            spinner.setSelection(sp.getInt(Utils.PREFS_PROFILE_STREET_TYPE,0),true);
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
            spinner.setSelection(savedInstanceState.getInt(Utils.PREFS_PROFILE_STREET_TYPE,0),true);
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
        outState.putString(Utils.PREFS_PROFILE_TELE, telephone.getText().toString().replace(" ",""));
        outState.putInt(Utils.PREFS_PROFILE_STREET_TYPE, spinner.getSelectedItemPosition());
    }

    private boolean checkFields() {
        Boolean b = true;

        if (secName.getText().toString().equals("")) {
            secName.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_nosecname_message), Toast.LENGTH_SHORT).show();
            b=false;
            return false;

        }

        if (name.getText().toString().equals("")) {
            name.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_noname_message), Toast.LENGTH_SHORT).show();
            b=false;
            return false;
        }

        if (otch.getText().toString().equals("")) {
            otch.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_nootch_message), Toast.LENGTH_SHORT).show();
            b=false;
            return false;

        }
        if (street.getText().toString().equals("")) {
            street.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_nostreet_message), Toast.LENGTH_SHORT).show();
            b=false;
            return false;

        }
        if (building.getText().toString().equals("")) {
            building.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_nobuilding_message), Toast.LENGTH_SHORT).show();
            b=false;
            return false;

        }
        if (flat.getText().toString().equals("")) {
            flat.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_noflat_message), Toast.LENGTH_SHORT).show();
            b=false;
            return false;

        }
        if (telephone.getText().toString().equals("")) {
            telephone.requestFocus();
            Toast.makeText(getContext(), getString(R.string.frag_prof_notelephone_message), Toast.LENGTH_SHORT).show();
            b=false;
            return false;

        }
        return b;
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
            editor.putString(Utils.PREFS_PROFILE_TELE, telephone.getText().toString().replace(" ",""));
            editor.putInt(Utils.PREFS_PROFILE_STREET_TYPE, spinner.getSelectedItemPosition());
            editor.commit();
            getActivity().finish();
//            editor.apply();
        }
        // 2 вносим все данные в SharedPrefs
    }
}
