package com.kskkbys.loop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Asynchronous task to search youtube videos
 * @author Keisuke Kobayashi
 */
public class YouTubeSearchTask extends AsyncTask<String, Integer, String> {

	private static final String TAG = YouTubeSearchTask.class.getSimpleName();
	
	private MainActivity mParent;
	private YouTubeSearchResult mResult;
	
	private ProgressDialog mProgressDialog;
	
	public YouTubeSearchTask(MainActivity parent) {
		this.mParent = parent;
	}
	
	@Override
	protected String doInBackground(String... query) {
		
		String uri = null;
		try {
			uri = "http://gdata.youtube.com/feeds/api/videos?q="
			+ URLEncoder.encode(query[0], "UTF-8") + "&alt=json";
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(uri);
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Log.v(TAG, "OK");
				InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());
				BufferedReader br = new BufferedReader(isr);
				String line = br.readLine();
				Log.v(TAG, line);
				
				Gson gson = new Gson();
				this.mResult = gson.fromJson(line, YouTubeSearchResult.class);
			} else {
				Log.e(TAG, "NG: " + response.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected void onPreExecute() {
		mProgressDialog = new ProgressDialog(this.mParent);
		//mProgressDialog.setTitle(R.string.app_name);
		mProgressDialog.setMessage(mParent.getText(R.string.main_dialog_searching));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
	}
	
	@Override
	protected void onPostExecute(String result) {
		// Dismiss dialog and start to play
		mProgressDialog.dismiss();
		this.mParent.startVideoPlayer(createVideoList(this.mResult));
	}
	
	/**
	 * Create list of Video objects
	 * @param result
	 * @return
	 */
	private List<Video> createVideoList(YouTubeSearchResult result) {
		List<Video> videoList = new ArrayList<Video>();
		for (YouTubeSearchResult.Feed.Entry entry : result.feed.entry) {
			Video v = new Video(
					getVideoId(entry.id.$t),
					entry.title.$t, 
					//entry.media$group.media$content[2].url, 
					entry.media$group.media$content[0].duration * 1000);	// msec
			videoList.add(v);
		}
		return videoList;
	}
	
	/**
	 * Get video ID
	 * @param str
	 * @return
	 */
	private String getVideoId(String str) {
		return str.replaceAll("http://gdata.youtube.com/feeds/api/videos/", "");
	}
}