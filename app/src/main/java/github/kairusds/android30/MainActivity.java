package github.kairusds.android30;

import android.Manifest;
import android.content.Intent;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
// import android.widget.EditText;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
 
public class MainActivity extends AppCompatActivity{

	private Path filePath = Paths.get("/sdcard", "test.txt");

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void showManageFiles(View view){
		try{
			var uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
			startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, uri));
		}catch(Exception err){
			if(!ActivityCompat.shouldShowRequestPermissionRationale(AndroidLauncher.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
				ActivityCompat.requestPermissions(AndroidLauncher.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
			if(!ActivityCompat.shouldShowRequestPermissionRationale(AndroidLauncher.this, Manifest.permission.READ_EXTERNAL_STORAGE))
				ActivityCompat.requestPermissions(AndroidLauncher.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
		}
	}

	public void createTestFile(View view){
		try{
			Files.writeString(filePath, "Hello World", StandardOpenOption.APPEND);
		}catch(IOException err){
			err.printStackTrace();
		}
	}

	public void readTestFile(View view){
		try{
			var output = findViewById(R.id.output);
			output.setText(Files.readString(filePath));
		}catch(IOException err){
			err.printStackTrace();
		}
	}

	public void showLicenses(View view){
		startActivity(new Intent(this, OssLicensesMenuActivity.class));
	}

}