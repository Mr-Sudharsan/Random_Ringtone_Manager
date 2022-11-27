package com.randomringtonemanager;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_SETTINGS;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ListView listOfRingtones;
    private ToggleButton toggleRandomizer;
    private TextView textView;
    private static TextView txt_nxt_tone;
    private List<Ringtone> ringtones;
    private Button chooseRingtone;
    private RecyclerView ringtonerecyclerview;
    private RingtoneAdaptor ringtoneAdaptor;
    private static  int TotalCount =0;
    private ArrayList<String> permissionsList;
    boolean isEnable = true;
    private static final int COMMON_REQUEST_CODE = 1000;
    public PhoneStateReceiver phoneStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggleRandomizer = (ToggleButton) findViewById(R.id.toggle);
        textView = findViewById(R.id.textView);
        chooseRingtone = findViewById(R.id.chooseRingtoneBtn);
        chooseRingtone.setVisibility(View.GONE);
        ringtonerecyclerview = findViewById(R.id.ringtonerecyclerview);
        txt_nxt_tone = findViewById(R.id.txt_nxt_tone);
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_RINGTONES);
        System.out.println("Path : "+path);
        ringtones = new ArrayList<>();
        ringtonerecyclerview.setLayoutManager(new LinearLayoutManager(this));
        Log.i("media", "Current Default Ringtone : " + RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE).getPath());

        if (checkSystemWritePermission()) {
            initializeToggle();
        } else {
            Toast.makeText(getApplicationContext(), "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
        }
        ringtones = RingtoneHelper.fetchAvailableRingtones(MainActivity.this);
        ringtoneAdaptor = new RingtoneAdaptor(this, ringtones);
        ringtonerecyclerview.setAdapter(ringtoneAdaptor);
        System.out.println("Rintones stored : "+Session.getListUri("available_ringtones",MainActivity.this));
        chooseRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickRingtone();
            }
        });

//        chooseRingtone.setOnClickListener(view -> {
//            Intent intent = new Intent(MainActivity.this, ExternalMainActivity.class);
//            startActivity(intent);
//        });

        try {
            if (!Session.getSession("Count",MainActivity.this).equals(""))
                RingtoneHelper.set_count(Integer.parseInt(Session.getSession("Count", MainActivity.this)));
            txt_nxt_tone.setText("Next Tone is : "+Session.getSession("Title", MainActivity.this));
        }catch (Exception e){
            e.printStackTrace();
        }
        PermissionAsk();

        startService(new Intent( this, RingtoneService.class ));

        Intent pushIntent1 = new Intent(getApplicationContext(), PhoneStateReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(pushIntent1);
        } else {
            getApplicationContext().startService(pushIntent1);
        }
    }

    public static void change_tone_txt(){
        if(txt_nxt_tone != null)
            txt_nxt_tone.setText("Next Tone is : "+RingtoneHelper.title_tone);
    }

    private void PermissionAsk(){

        permissionsList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(READ_EXTERNAL_STORAGE);
            permissionsList.add(WRITE_SETTINGS);
            permissionsList.add(READ_PHONE_STATE);
        }
        for (int i = 0; i < permissionsList.size(); i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permissionsList.get(i)) != PackageManager.PERMISSION_GRANTED) {
                isEnable = false;
                break;
            } else
                isEnable = true;
        }
        if (!isEnable)
            makeRequest(permissionsList);

    }

    private void makeRequest(ArrayList<String> permissionsList) {
        String[] permissionStringList = new String[permissionsList.size()];
        for (int i = 0; i < permissionsList.size(); i++) {
            permissionStringList[i] = permissionsList.get(i);
        }
        if (permissionStringList.length > 0)
            ActivityCompat.requestPermissions(MainActivity.this, permissionStringList, COMMON_REQUEST_CODE);
    }

    private void initializeToggle() {
        final SharedPreferences preferences = getSharedPreferences("randomizer", Context.MODE_PRIVATE);
        boolean active = preferences.getBoolean("active", false);
        toggleRandomizer.setChecked(active);
        toggleRandomizer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("active", isChecked).commit();
            }
        });
    }

    private boolean checkSystemWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(getApplicationContext()))
                return true;
            else
                openAndroidPermissionsMenu();
        }
        return false;
    }

    private void openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    public void pickRingtone() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("audio/mp3"); // specify "audio/mp3" to filter only mp3 files
//        startActivityForResult(intent, 1);

//        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone:");
//        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,
//                true);
//        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALL);
//        startActivityForResult(intent, 1);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Choose Sound File"), 1);
    }

    @SuppressLint({"NotifyDataSetChanged", "LongLogTag"})
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//            Uri audio = data.getData();
//            Log.d("media", "onActivityResult: " + audio);
////            ringtones.add(audio);
////            ringtoneAdaptor.notifyDataSetChanged();
//            RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this,
//                    RingtoneManager.TYPE_RINGTONE,audio);
//        }
        /*
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
//                    Toast.makeText(getBaseContext(), RingtoneManager.ID_COLUMN_INDEX, Toast.LENGTH_SHORT).show();
                    Uri targetUri = data.getData();

                    Uri uri = data.getData();
                    String[] ringTonePath = new String[0];
                    ringTonePath[0] = uri.getPath();
                    Cursor c = getContentResolver().query(uri, ringTonePath, null, null, null);
                    int columnIndex = c.getColumnIndex(ringTonePath[0]);
                    String tonepath = c.getString(columnIndex);
//                    ringtones.add();
                    Log.w("Path of tone from home......******************.........", tonepath + "");

                    RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(),
                            RingtoneManager.TYPE_NOTIFICATION, uri);

                    break;

                default:
                    break;
            }
        }

         */

        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri i = data.getData();  // getData
            String s = i.getPath(); // getPath
            File k = new File(s);
            if (s != null) {      // file.exists

                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
                values.put(MediaStore.MediaColumns.TITLE, "ring");
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                values.put(MediaStore.MediaColumns.SIZE, k.length());
                values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
//                values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
//                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
//                values.put(MediaStore.Audio.Media.IS_ALARM, true);
//                values.put(MediaStore.Audio.Media.IS_MUSIC, false);

                Uri uri = MediaStore.Audio.Media.getContentUriForPath(k
                        .getAbsolutePath());
//                getContentResolver().delete(
//                        uri,
//                        MediaStore.MediaColumns.DATA + "=\""
//                                + k.getAbsolutePath() + "\"", null);
                Uri newUri = getContentResolver().insert(uri, values);

                try {
                    RingtoneManager.setActualDefaultRingtoneUri(
                            MainActivity.this, RingtoneManager.TYPE_RINGTONE,
                            newUri);
                } catch (Throwable t) {

                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}

