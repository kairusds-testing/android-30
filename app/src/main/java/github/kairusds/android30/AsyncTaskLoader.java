package github.kairusds.android30;

import android.os.AsyncTask;

public class AsyncTaskLoader extends AsyncTask<AsyncCallback, Void, Boolean>{

	private AsyncCallback[] callbacks;

	@Override
	protected Boolean doInBackground(AsyncCallback... callbacks) {
		this.callbacks = callbacks;
		for(AsyncCallback callback : callbacks){
			callback.run();
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result){
		for(AsyncCallback callback : callbacks){
			callback.onComplete();
		}
	}

}
