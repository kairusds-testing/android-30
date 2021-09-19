package github.kairusds.android30;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
 
public class MainActivity extends AppCompatActivity{

	private Path filePath = Paths.get("/sdcard", "test.txt");
	private List<StorageVolume> volumes;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		switchTheme();
		volumes = ((StorageManager) getSystemService(StorageManager.class)).getStorageVolumes();
		if(preferences.getBoolean("Fullscreen", false)) hideSystemUI();
		setContentView(R.layout.activity_main);
		setOnClickListeners();
	}

	private void setOnClickListeners(){
		getButton(R.id.requestFilePermissions).setOnClickListener(v -> {
			var uri = Uri.parse("package:" + getPackageName());
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
				/* var sharedStorage = new File("/sdcard");
				if(!sharedStorage.canWrite()) TODO: REWRITE WITH NEW ACTIVITYRESULT API
					filePath = Paths.get(getExternalFilesDir().getAbsolutePath(), "test.txt"); */ 
				startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
			}else{
				if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
					startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri));
				}
				var writeExternalStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
				if(!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, writeExternalStorage))
					ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, writeExternalStorage}, 0);
			}
		});

		getButton(R.id.showStorageVolumes).setOnClickListener(v -> {
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
				showSnackbar("This feature is only available to Android 11+");
				return;
			}

			var alertDialog = new AlertDialog.Builder(this);
			alertDialog.setIcon(R.drawable.ic_launcher);
			alertDialog.setTitle("List of usable storage volumes");
	
			var adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
			var volumeDescs = new ArrayList<String>();
			for(int i = 0; i < volumes.size(); i++){
				var currentVolume = volumes.get(i);
				if(currentVolume.getState() == Environment.MEDIA_MOUNTED){
					adapter.add(currentVolume.getDirectory().getAbsolutePath());
					volumeDescs.add(currentVolume.getDescription(MainActivity.this));
				}
			}
	
			alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
				}
			});
			alertDialog.setAdapter(adapter, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which){
					var innerDialog = new AlertDialog.Builder(MainActivity.this);
					innerDialog.setMessage(volumeDescs.get(which));
					innerDialog.setTitle(adapter.getItem(which));
					innerDialog.setPositiveButton("Close", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog1, int which1){
							dialog1.dismiss();
						}
					});
					innerDialog.setOnDismissListener(dialogg -> {
						filePath = Paths.get(adapter.getItem(which), "test.txt");
						showSnackbar("Test file path: " + filePath.toFile().getAbsolutePath());
					});
					innerDialog.show();
				}
			});
			alertDialog.show();
		});

		getButton(R.id.getHostInfo).setOnClickListener(v -> {
			var text = getTextFromURL("http://ip-api.com/line");
			((TextInputLayout) findViewById(R.id.output)).getEditText().setText(text);
		});

		getButton(R.id.getUserAgent).setOnClickListener(v -> {
			var text = getTextFromURL("https://ibancho.herokuapp.com/ua.php");
			((TextInputLayout) findViewById(R.id.output)).getEditText().setText(text);
		});

		getButton(R.id.createTestFile).setOnClickListener(v -> {
			try{
				var text = "Hello World!";
				var textBytes = text.getBytes();
	
				Files.write(filePath, textBytes);
				// Files.writeString(filePath, "Hello World", StandardOpenOption.APPEND);
			}catch(IOException err){
				writeError(err);
			}
		});

		getButton(R.id.readTestFile).setOnClickListener(v -> {
			try{
				((TextInputLayout) findViewById(R.id.output)).getEditText().setText(Files.readAllLines(filePath).get(0));
				// output.setText(Files.readString(filePath));
			}catch(IOException err){
				writeError(err);
			}
		});
	}

	private Button getButton(int id){
		return ((Button) findViewById(id));
	}

	private void switchTheme(){
		AppCompatDelegate.setDefaultNightMode(preferences.getBoolean("DarkTheme", isNightMode()) ?
			AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
		);
	}

	public boolean isNightMode(){
		return (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
	}

	private String getTextFromURL(String url){
		new AsyncTaskLoader().execute(new AsyncCallback(){
			public void run(){
				try{
					var api = new URL(url);
					var in = new BufferedReader(new InputStreamReader(api.openStream()));
					var line = "";
					var builder = new StringBuilder();
			
					while((line = in.readLine()) != null){
						builder.append(line + "\n");
					}
			
					in.close();
					return builder.toString();
				}catch(Exception err){
					throw new RuntimeException(err.getMessage());
				}
			}
			public void onComplete(){}
		});
		return null;
	}

	private void showSnackbar(String text){
		Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).show();
	}

	private void writeError(Exception err){
		var sw = new StringWriter();
		var pw = new PrintWriter(sw);
		err.printStackTrace(pw);
		((TextInputLayout) findViewById(R.id.output)).getEditText().setText(sw.toString());
	}	

	private void hideSystemUI() {
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(
			View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
			// | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

	private void showSystemUI() {
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(
			// View.SYSTEM_UI_FLAG_LAYOUT_STABLE
			View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_main, menu);
		menu.findItem(R.id.settings_dark_theme).setChecked(preferences.getBoolean("DarkTheme", isNightMode()));
		menu.findItem(R.id.settings_fullscreen).setChecked(preferences.getBoolean("Fullscreen", false));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.isCheckable()){
			if(item.isChecked()) item.setChecked(false);
			else item.setChecked(true);
		}

		switch(item.getItemId()){
			case R.id.menu_licenses:
				startActivity(new Intent(this, OssLicensesMenuActivity.class));
				return true;
			case R.id.settings_dark_theme:
				preferences.edit().putBoolean("DarkTheme", item.isChecked()).apply();
				switchTheme();
				return true;
			case R.id.settings_fullscreen:
				preferences.edit().putBoolean("Fullscreen", item.isChecked()).apply();
				if(item.isChecked()) hideSystemUI();
				else showSystemUI();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus && preferences.getBoolean("Fullscreen", false)) hideSystemUI();
	}

}