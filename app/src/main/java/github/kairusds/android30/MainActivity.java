package github.kairusds.android30;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
 
public class MainActivity extends AppCompatActivity{

	private Path filePath = Paths.get("/storage/emulated/0", "test.txt");
	private List<StorageVolume> volumes;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void requestFilePermissions(View view){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
			/* var sharedStorage = new File("/sdcard");
			if(!sharedStorage.canWrite()) TODO: REWRITE WITH NEW ACTIVITYRESULT API
				filePath = Paths.get(getExternalFilesDir().getAbsolutePath(), "test.txt"); */ 

			var uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
			startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
		}else{
			if(!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
				ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
			if(!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
				ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
		}
	}

	public void showStorageVolumes(View view){
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
			showSnackbar("This isn't supported for devices below Android 11.");
			return;
		}

		List<StorageVolume> volumes = ((StorageManager) getSystemService(StorageManager.class)).getStorageVolumes();

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
	}

	public void createTestFile(View view){
		try{
			var text = "Hello World!";
			var textBytes = text.getBytes();

			Files.write(filePath, textBytes);
			// Files.writeString(filePath, "Hello World", StandardOpenOption.APPEND);
		}catch(IOException err){
			writeError(err);
		}
	}

	public void readTestFile(View view){
		try{
			((TextInputLayout) findViewById(R.id.output)).getEditText().setText(Files.readAllLines(filePath).get(0));
			// output.setText(Files.readString(filePath));
		}catch(IOException err){
			writeError(err);
		}
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

	public void showLicenses(View view){
		startActivity(new Intent(this, OssLicensesMenuActivity.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_main, menu);
		menu.setGroupCheckable(R.id.settings_items, true, false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.menu_about:
				// startActivity(new Intent(this, AboutActivity.class));
				return true;
			case R.id.settings_dark_theme:
				showSnackbar(item.isChecked() ? "Checked" : "Unchecked");
				return true;
			case R.id.settings_fullscreen:
				showSnackbar(item.isChecked() ? "Checked" : "Unchecked");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}