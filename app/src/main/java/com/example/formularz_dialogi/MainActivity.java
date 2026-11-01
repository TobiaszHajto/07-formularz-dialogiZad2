
package com.example.formularz_dialogi;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etEmail;
    private Button btnBirthDate, btnSubmit;
    private Spinner spCourse;
    private RadioGroup rgMode;
    private SeekBar sbLevel;
    private TextView tvLevelValue, tvSummary;
    private MaterialSwitch swNewsletter;
    private CheckBox cbTerms;

    private String birthDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        btnBirthDate = findViewById(R.id.btnBirthDate);
        btnSubmit = findViewById(R.id.btnSubmit);
        spCourse = findViewById(R.id.spCourse);
        rgMode = findViewById(R.id.rgMode);
        sbLevel = findViewById(R.id.sbLevel);
        tvLevelValue = findViewById(R.id.tvLevelValue);
        tvSummary = findViewById(R.id.tvSummary);
        swNewsletter = findViewById(R.id.swNewsletter);
        cbTerms = findViewById(R.id.cbTerms);

        setUpCourseSpinner();

        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) ->
                btnSubmit.setEnabled(isChecked));

        btnBirthDate.setOnClickListener(v -> showDatePicker());

        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                askForConfirmation();
            }
        });

        sbLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvLevelValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        tvLevelValue.setText(String.valueOf(sbLevel.getProgress()));
    }

    private void setUpCourseSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.courses, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCourse.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    birthDate = String.format(Locale.US, "%04d-%02d-%02d",
                            year, month + 1, dayOfMonth);
                    btnBirthDate.setText(birthDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private boolean validateForm() {
        String name = etName.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Podaj imię");
            etName.requestFocus();
            return false;
        }

        String email = etEmail.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Podaj poprawny adres e-mail");
            etEmail.requestFocus();
            return false;
        }

        if (birthDate.isEmpty()) {
            Toast.makeText(this, "Wybierz datę urodzenia", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spCourse.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Wybierz kurs", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rgMode.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Wybierz tryb nauki", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void askForConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Potwierdzenie")
                .setMessage("Czy na pewno chcesz wysłać formularz?")
                .setPositiveButton("Tak", (dialog, which) -> submitForm())
                .setNegativeButton("Nie", null)
                .show();
    }

    private void submitForm() {
        tvSummary.setText(buildSummary());

        Snackbar.make(findViewById(R.id.main), "Formularz został wysłany",
                        Snackbar.LENGTH_LONG)
                .setAction("Cofnij", v -> tvSummary.setText(""))
                .show();
    }

    private String buildSummary() {
        String mode = rgMode.getCheckedRadioButtonId() == R.id.rbOnsite
                ? "Stacjonarny" : "Zdalny";

        String newsletter = swNewsletter.isChecked() ? "Tak" : "Nie";

        return "Imię: " + etName.getText().toString().trim()
                + "\nEmail: " + etEmail.getText().toString().trim()
                + "\nData urodzenia: " + birthDate
                + "\nKurs: " + spCourse.getSelectedItem().toString()
                + "\nTryb: " + mode
                + "\nPoziom: " + sbLevel.getProgress()
                + "\nNewsletter: " + newsletter;
    }
}
