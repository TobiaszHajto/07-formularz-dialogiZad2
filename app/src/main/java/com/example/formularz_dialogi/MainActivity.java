
package com.example.formularz_dialogi;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private Button btnDate, btnSubmit;
    private Spinner spDestination;
    private SeekBar sbPeople;
    private TextView tvPeople, tvSummary;
    private Switch swInsurance;
    private CheckBox cbTerms;

    private String departureDate = "";
    private String previousSummary = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        btnDate = findViewById(R.id.btnDate);
        btnSubmit = findViewById(R.id.btnSubmit);
        spDestination = findViewById(R.id.spDestination);
        sbPeople = findViewById(R.id.sbPeople);
        tvPeople = findViewById(R.id.tvPeople);
        tvSummary = findViewById(R.id.tvSummary);
        swInsurance = findViewById(R.id.swInsurance);
        cbTerms = findViewById(R.id.cbTerms);

        setUpSpinner();
        setUpSeekBar();
        setUpListeners();
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.destinations, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spDestination.setAdapter(adapter);
    }

    private void setUpSeekBar() {
        tvPeople.setText("1");

        sbPeople.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        tvPeople.setText(String.valueOf(progress + 1));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
    }

    private void setUpListeners() {
        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) ->
                btnSubmit.setEnabled(isChecked));

        btnDate.setOnClickListener(v -> showDatePicker());

        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                askForConfirmation();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    departureDate = String.format(Locale.US, "%04d-%02d-%02d",
                            year, month + 1, dayOfMonth);

                    btnDate.setText(departureDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private boolean validateForm() {
        String name = etName.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Podaj imię i nazwisko");
            etName.requestFocus();
            return false;
        }

        if (spDestination.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Wybierz kierunek wycieczki",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (departureDate.isEmpty()) {
            Toast.makeText(this, "Wybierz datę wyjazdu",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void askForConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Potwierdzenie")
                .setMessage("Czy na pewno chcesz zapisać się na wycieczkę?")
                .setPositiveButton("Tak", (dialog, which) -> submitForm())
                .setNegativeButton("Nie", null)
                .show();
    }

    private void submitForm() {
        previousSummary = tvSummary.getText().toString();

        tvSummary.setText(buildSummary());

        Snackbar.make(findViewById(R.id.main),
                        "Zapisano na wycieczkę",
                        Snackbar.LENGTH_LONG)
                .setAction("Cofnij", v -> tvSummary.setText(previousSummary))
                .show();
    }

    private String buildSummary() {
        String insurance = swInsurance.isChecked() ? "Tak" : "Nie";

        return "PODSUMOWANIE ZAPISU\n\n"
                + "Imię i nazwisko: "
                + etName.getText().toString().trim()
                + "\nKierunek: "
                + spDestination.getSelectedItem().toString()
                + "\nLiczba osób: "
                + (sbPeople.getProgress() + 1)
                + "\nData wyjazdu: "
                + departureDate
                + "\nUbezpieczenie: "
                + insurance;
    }
}
