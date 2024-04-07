package de.dennisguse.opentracks.settings;

import static android.provider.Settings.Secure.ANDROID_ID;
import static de.dennisguse.opentracks.settings.PreferencesUtils.getUnitSystem;

import android.app.AlertDialog;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.core.app.ActivityCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.JsonObject;


import java.util.Objects;

import de.dennisguse.opentracks.R;
import de.dennisguse.opentracks.data.models.Height;
import de.dennisguse.opentracks.data.models.HeightFormatter;
import de.dennisguse.opentracks.data.models.Speed;
import de.dennisguse.opentracks.data.models.SpeedFormatter;
import de.dennisguse.opentracks.data.models.Weight;
import de.dennisguse.opentracks.data.models.WeightFormatter;
import de.dennisguse.opentracks.data.FirestoreCRUDUtil;
import de.dennisguse.opentracks.data.interfaces.JSONSerializable;
import de.dennisguse.opentracks.data.interfaces.ReadCallback;
import de.dennisguse.opentracks.data.interfaces.ActionCallback;
import de.dennisguse.opentracks.data.adapters.FireStoreAdapter;
import de.dennisguse.opentracks.data.models.CRUDConstants;



import de.dennisguse.opentracks.data.models.UserModel;



import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Calendar;
import java.util.Locale;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.Arrays;
import java.util.Locale;



 // You can choose any value for the request code


public class UserProfileFragment extends PreferenceFragmentCompat {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int IMAGE_PICKER_REQUEST_CODE = 101;


    SwitchPreference leaderboardSwitch;
    private Context applicationContext;

    private void startImagePicker() {
        try {
            ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start();
        } catch (Exception e) {
            Log.e("UserProfileFragment", "Error starting image picker: " + e.getMessage());
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_user_profile);

        Preference editPreference = findPreference(getString(R.string.edit_profile_key));
        if (editPreference != null) {
            editPreference.setOnPreferenceClickListener(preference -> {
                showEditProfileDialog();
                return true;
            });
        }

        Preference editProfilePic = findPreference("edit_profile_pic");
        if (editProfilePic != null) {
            editProfilePic.setOnPreferenceClickListener(preference -> {
                Log.d("UserProfileFragment", "Edit profile picture button clicked");
                // Check if permission is granted
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it from the user
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CODE);
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        // Permission is already granted, start the image picker
                        Log.d("UserProfileFragment", "Calling startImagePicker()");
                        startImagePicker();
                    }
                }
                return true;
            });
        }

        // Check toggle status for leaderboard preferences
        leaderboardSwitch = findPreference("leaderboard_switch");
        assert leaderboardSwitch != null;
        leaderboardSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                if(leaderboardSwitch.isChecked())
                {
                    // Form to check/ uncheck shared details

                    displayCustomSharingDialog();

                }
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Log.d("UserProfileFragment", "Permissions granted, starting image picker");
                startImagePicker();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri = data.getData();
        if (imageUri != null) {
            // Update the profile picture with the selected image
            updateProfilePicture(imageUri);
        }
    }

    private void updateProfilePicture(Uri imageUri) {
        // Load the image from the URI and set it to the profile picture ImageView
        ImageView profilePictureImageView = requireView().findViewById(R.id.profileImageView);
        profilePictureImageView.setImageURI(imageUri);
    }

    private void showEditProfileDialog() {
        // Inflate the custom layout for the edit dialog.
        View formView = LayoutInflater.from(getContext()).inflate(R.layout.edit_profile_form, null);

        // Initialize all the EditText fields and Spinners.
        EditText editNickname = formView.findViewById(R.id.editNickname);
        EditText editDateOfBirth = formView.findViewById(R.id.editDateOfBirth);
        EditText editHeight = formView.findViewById(R.id.editHeight);
        EditText editWeight = formView.findViewById(R.id.editWeight);

        Spinner spinnerGender = formView.findViewById(R.id.spinnerGender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        Spinner spinnerLocation = formView.findViewById(R.id.spinnerLocation);
        String[] isoCountryCodes = Locale.getISOCountries();
        String[] countryNames = new String[isoCountryCodes.length];

        for (int i = 0; i < isoCountryCodes.length; i++) {
            Locale locale = new Locale("", isoCountryCodes[i]);
            countryNames[i] = locale.getDisplayCountry();
        }

        Arrays.sort(countryNames); // Sort the country names alphabetically

        // Move Canada to the beginning of the array
        for (int i = 0; i < countryNames.length; i++) {
            if (countryNames[i].equalsIgnoreCase("Canada")) {
                String temp = countryNames[i];
                System.arraycopy(countryNames, 0, countryNames, 1, i);
                countryNames[0] = temp;
                break;
            }
        }

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, countryNames);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);


        // Set up the AlertDialog.
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.edit_profile_title)
                .setView(formView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Collect data from the fields.
                    String nickname = editNickname.getText().toString();
                    String dateOfBirth = editDateOfBirth.getText().toString();
                    String height = editHeight.getText().toString();
                    String weight = editWeight.getText().toString();
                    String gender = spinnerGender.getSelectedItem().toString();
                    String location = spinnerLocation.getSelectedItem().toString();

                    // Validate and save the data if valid
//TODO: Add back validation
                    if(true){
                    //if (validateInputs(nickname, dateOfBirth, height, weight, gender, location)) {
                        saveProfileData(nickname, location, dateOfBirth, gender, height, weight);
                        showToast("Profile updated successfully!");

                    } else {
                        showToast("Please check your inputs.");
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        editDateOfBirth.setOnClickListener(view -> {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year1, monthOfYear, dayOfMonth) -> {
                // The month value is 0-based, so we add 1 to it for display.
                String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year1);
                editDateOfBirth.setText(selectedDate);
            }, year, month, day);

            // Prevent future dates from being selected
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            // Show the date picker dialog
            datePickerDialog.show();
        });
    }

    // TODO: Implement saving logic here.
    private void saveProfileData(String nickname, String location, String dateOfBirth, String gender, String height, String weight) {

        UserModel user = new UserModel(nickname, location, (long)Integer.valueOf(dateOfBirth), gender, Integer.valueOf(height), Integer.valueOf(weight));

        FirestoreCRUDUtil.getInstance().createEntry("users", systemID(), user.toJSON(), null);
        FirestoreCRUDUtil.getInstance().getEntry("users", systemID(), callback);
    }

    ReadCallback callback = new ReadCallback() {
        @Override
        public void onSuccess(JsonObject data) {

            UserModel readUser = JSONSerializable.fromJSON(data, UserModel.class);

            TextView textView_nickname = getView().findViewById(R.id.nickname);
            TextView textView_location = getView().findViewById(R.id.userLocation);
            TextView textView_DOB = getView().findViewById(R.id.dateOfBirth);
            TextView textView_height = getView().findViewById(R.id.userHeight);
            TextView textView_weight = getView().findViewById(R.id.userWeight);

            //TODO: create separate unit conversion method.
            UnitSystem unitSystem = getUnitSystem();
            Height height = new Height(readUser.getHeight());
            Pair<String, String> heightStrings = HeightFormatter.Builder().setUnit(unitSystem).build(getContext()).getHeightParts(height);

            Weight weight = new Weight(readUser.getWeight());
            Pair<String, String> weightStrings = WeightFormatter.Builder().setUnit(unitSystem).build(getContext()).getWeightParts(weight);

            textView_nickname.setText(readUser.getNickname());
            textView_location.setText(readUser.getCountry());
            textView_DOB.setText(Long.toString(readUser.getDateOfBirth()));
            textView_height.setText(heightStrings.first + heightStrings.second);
            textView_weight.setText(weightStrings.first + weightStrings.second);

        }

        @Override
        public void onFailure() {
            showToast("User profile was not saved!");
        }
    };



    // A simple method to show toast messages.
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // A method to validate the user inputs.
    private boolean validateInputs(String nickname, String dateOfBirth, String height, String weight, String gender, String location) {
        if (nickname.isEmpty() || gender.isEmpty()) {
            showToast("Nickname and gender cannot be empty.");
            return false;
        }
        try {
            if (Double.parseDouble(height) < 0) {
                showToast("Height cannot be negative.");
                return false;
            }
            if (Double.parseDouble(weight) < 0) {
                showToast("Weight cannot be negative.");
                return false;
            }
        } catch (NumberFormatException e) {
            showToast("Height and weight must be valid numbers.");
            return false;
        }

        return true;
    }
    private void displayCustomSharingDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Array to store user information
        String[] userInfo = new String[5];
        int[] textViewIds = {R.id.nickname, R.id.userLocation, R.id.dateOfBirth, R.id.userHeight, R.id.userWeight};

        // Array to store detail labels
        String[] detailNames = {"Nickname", "Location", "Date of Birth", "Height", "Weight"};

        StringBuilder alertMessageBuilder = new StringBuilder("Do you allow OpenTracks to store and display the following information on the leaderboard?\n\n");

        // Retrieve values from TextViews and populate user info
        for (int i = 0; i < textViewIds.length; i++) {

            TextView textView = getView().findViewById(textViewIds[i]);

            if(textView!=null) {
                userInfo[i] = textView.getText().toString();

                // Construct custom message
                alertMessageBuilder.append(detailNames[i]).append(": ").append(userInfo[i]).append("\n");

            }
        }

        String alertMessage = alertMessageBuilder.toString();

        builder.setTitle("Confirm Selection")

                .setMessage(alertMessage)
                .setPositiveButton("ALLOW", (dialog, which) -> {

                    showToast("Updated sharing permissions");

                    // TODO: Implement saving sharing permissions here.

                })
                .setNegativeButton("CANCEL", (dialog, which) -> {
                    // Un-toggle leaderboard switch

                    leaderboardSwitch.setChecked(false);
                })
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((SettingsActivity) getActivity()).getSupportActionBar().setTitle(R.string.settings_ui_title);
    }


    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;

        if (preference instanceof ResetDialogPreference) {
            dialogFragment = ResetDialogPreference.ResetPreferenceDialog.newInstance(preference.getKey());
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getParentFragmentManager(), getClass().getSimpleName());
            return;
        }

        super.onDisplayPreferenceDialog(preference);
    }

    @Override
    public void onResume() {
        super.onResume();
        FirestoreCRUDUtil.getInstance().getEntry("users", systemID(), callback);
    }

    //TODO: Remove once there is offline data persistence through Firestore
    public static String systemID() {
        String id = System.getProperty("http.agent");
        id = id.replace(" ", "");
        return id;
    }

}
