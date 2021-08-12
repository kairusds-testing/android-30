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
import android.widget.EditText;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.StandardOpenOption;
 
public class MainActivity extends AppCompatActivity{

	private Path filePath = Paths.get("/storage/emulated/0", "test.txt");

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void showManageFiles(View view){
		try{
			var uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
			startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
		}catch(Exception err){
			if(!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
				ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
			if(!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
				ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
		}
	}

	public void createTestFile(View view){
		try{
			var text = "Hello World";
			var textBytes = text.getBytes();
			Files.write(filePath, textBytes);
			// Files.writeString(filePath, "Hello World", StandardOpenOption.APPEND);
		}catch(IOException err){
			writeError(err);
		}
	}

	public void readTestFile(View view){
		try{
			((EditText) findViewById(R.id.output)).setText(Files.readAllLines(filePath).get(0));
			// output.setText(Files.readString(filePath));
		}catch(IOException err){
			writeError(err);
		}
	}

	private void writeError(Exception err){
		var sw = new StringWriter();
		var pw = new PrintWriter(sw);
		err.printStackTrace(pw);
		((EditText) findViewById(R.id.output)).setText(sw.toString());
	}

	public void showLicenses(View view){
		startActivity(new Intent(this, OssLicensesMenuActivity.class));
	}

}