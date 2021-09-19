package github.kairusds.android30;

import android.os.AsyncTask;

public class AsyncTaskLoader extends AsyncTask<AsyncCallback, Void, Boolean>{

	private AsyncCallback callback;

	@Override
	protected Boolean doInBackground(AsyncCallback callback) {
		this.callback = callback;
		callback.run();
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		callback.onComplete();
	}

}
