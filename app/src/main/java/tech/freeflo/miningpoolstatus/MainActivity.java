package tech.freeflo.miningpoolstatus;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    //ProgressDialog progress;
    TextView totalpaid,validblocks,orphaned,confirmed,pending,hashrate;
    String Totalpaid="",ValidBlocks="",Orphaned="",Confirmed="",Pending="",Hashrate="";
    ProgressDialog progress;
    Button queryButton;
    String Id;

    static final String API_URL = "http://107.173.118.210:8080/api/stats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        queryButton = (Button) findViewById(R.id.buttonPanel);
        //responseView = (TextView) findViewById(R.id.responseview);
        //emailText = (EditText) findViewById(R.id.emailText);
        // progressBar=(ProgressBar)findViewById(R.id.parentPanel);
        //queryButton=(Button)findViewById(R.id.butt);
        totalpaid=(TextView)findViewById(R.id.tv_totalpaid);
        validblocks=(TextView)findViewById(R.id.tv_validblocks);
        orphaned=(TextView)findViewById(R.id.tv_orphaned);
        confirmed=(TextView)findViewById(R.id.tv_confirmed);
        pending=(TextView)findViewById(R.id.tv_pending);
        hashrate=(TextView)findViewById(R.id.tv_hashrate);



        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
            }
        });
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            // progressBar.setVisibility(View.VISIBLE);
            progress=new ProgressDialog(MainActivity.this);
            progress.setIndeterminate(true);
            progress.setTitle("Fetching");
            progress.setMessage("Please wait...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();

        }

        protected String doInBackground(Void... urls) {
            //String email = emailText.getText().toString();
            // Do some validation here

            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            // progressBar.setVisibility(View.GONE);

            /*try{
                JSONObject jobj = new JSONObject(response);
                String workers=jobj.getString("poolStats");
                responseView.setText(workers);
            }
            catch (Exception e)
            {

            }*/

            try {
                progress.hide();
                JSONObject obj1 = new JSONObject(response);
                JSONObject result = obj1.getJSONObject("pools");
                //JSONObject result1 = new JSONObject(result.toString());
                JSONObject result1 = result.getJSONObject("zen");
                JSONObject result2 = result1.getJSONObject("poolStats");
                JSONObject result3=result1.getJSONObject("blocks");

                Totalpaid=result2.getString("totalPaid");
                ValidBlocks=result2.getString("validBlocks");
                Orphaned=result3.getString("orphaned");
                Confirmed=result3.getString("confirmed");
                Pending=result3.getString("pending");
                Hashrate=result1.getString("hashrate");
                //Log.e("response:", result.toString());
                //Log.i("MyActivity", "got work obj as " + Id);
                /*JSONArray workinfo = result.optJSONArray(0);
                JSONArray workinfo1 = workinfo.optJSONArray(3);*/
                //JSONArray values = result.optJSONArray();
                /*for(int i=0;i<=result.length();i++)
                {

                     Id=result.getString(2);
                    //String terminalType=result.getString("terminal_type");
                }
                */
            } catch (Exception e) {
                e.printStackTrace();
            }
             totalpaid.setText(Totalpaid);
            orphaned.setText(Orphaned);
            validblocks.setText(ValidBlocks);
            pending.setText(Pending);
            hashrate.setText(Hashrate);
            confirmed.setText(Confirmed);
            // TODO: check this.exception
            // TODO: do something with the feed

//            try {
//                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
//                String requestID = object.getString("requestId");
//                int likelihood = object.getInt("likelihood");
//                JSONArray photos = object.getJSONArray("photos");
//                .
//                .
//                .
//                .
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }
    private void setupWindowAnimations() {
        Slide slide = (Slide)TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setExitTransition(slide);
    }
}
