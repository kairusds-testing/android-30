package github.kairusds.android30;

import android.Manifest;
import android.content.Intent;
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
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
 
public class MainActivity extends AppCompatActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		((EditText) findViewById(R.id.output)).setText(Paths.get("/sdcard").toFile().getAbsolutePath());
	}

	public void showManageFiles(View view){
		var uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
		startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
	}

	public void createTestFile(View view){
		/*var filePath = Path.of(Environment.getStorageDirectory(), "test.txt");
 
		try
		{
			//Write content to file
			Files.writeString(filePath, "Hello World !!", StandardOpenOption.APPEND);
 
			//Verify file content
			String content = Files.readString(filePath);
 
			System.out.println(content);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}*/
	}

	public void readTestFile(View view){
	}

	public void showLicenses(View view){
		startActivity(new Intent(this, OssLicensesMenuActivity.class));
	}

}