package de.korrelator.overview.mispauthv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.korrelator.overview.mispauthv2.models.Organisation;
import de.korrelator.overview.mispauthv2.models.OrganisationBuilder;
import de.korrelator.overview.mispauthv2.models.Server;
import de.korrelator.overview.mispauthv2.models.ServerBuilder;
import de.korrelator.overview.mispauthv2.models.User;
import de.korrelator.overview.mispauthv2.models.UserBuilder;
import de.korrelator.overview.mispauthv2.network.MispRequests;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MISPAUTH";
    private static final int SYNC_EVENTS_REQUEST = 1000;

    private MispRequests mispRequests;

    private String myOrgName = "My Orgname";
    private String myEmail = "example@email.bar";
    private String baseURL = "http://192.168.178.200";

    private TextView textViewResponse;

    private int tmpOrgID, tmpSyncUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mispRequests = new MispRequests(getApplicationContext(), baseURL);
        textViewResponse = findViewById(R.id.textview_json_result);

        Button syncEventsButton = findViewById(R.id.button_sync_events);
        syncEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncEvents();
            }
        });

        Button shareEventsButton = findViewById(R.id.button_share_events);
        shareEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareEvents();
            }
        });

        Button receiveEventsButton = findViewById(R.id.button_receive_events);
        receiveEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiveEvents();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case SYNC_EVENTS_REQUEST:

                String qrContent = data.getStringExtra("qr_data");

                try {

                    JSONArray qrData = new JSONArray(qrContent);

                    JSONObject serverInfo = Server.fromJSON(qrData.getJSONObject(0)).toJSON();
                    JSONObject userInfo = User.fromJSON(qrData.getJSONObject(1)).toJSON();
                    JSONObject orgInfo = Organisation.fromJSON(qrData.getJSONObject(2)).toJSON();

                    mispRequests.EditOrganisation(tmpOrgID, orgInfo, new MispRequests.EditOrganisationResponse() {
                        @Override
                        public void result(boolean success) {

                        }

                        @Override
                        public void error(VolleyError volleyError) {

                        }
                    });

                    mispRequests.EditUser(tmpSyncUserID, userInfo, new MispRequests.EditUserResponse() {
                        @Override
                        public void result(JSONObject msg) {

                        }

                        @Override
                        public void error(VolleyError volleyError) {

                        }
                    });

                    mispRequests.AddServer(serverInfo, new MispRequests.AddServerResponse() {
                        @Override
                        public void result(JSONObject result) {

                        }

                        @Override
                        public void error(VolleyError volleyError) {

                        }
                    });

                } catch (JSONException e){
                    e.printStackTrace();
                }

                break;

            default:
                Log.e(TAG, "requestCode " + requestCode + " is not known!");
        }
    }


    private void SyncEvents(){

        JSONObject tmpOrgBody = new OrganisationBuilder().local(true).build().toJSON();

        mispRequests.AddOrganisation(tmpOrgBody, new MispRequests.AddOrganisationResponse() {
            @Override
            public void result(int orgID) {

                tmpOrgID = orgID;

                JSONObject tmpSyncUser = new UserBuilder().orgID(orgID).roleType(User.RoleType.SYNC_USER).build().toJSON();

                mispRequests.AddUser(tmpSyncUser, new MispRequests.AddUserResponse() {
                    @Override
                    public void result(int id, String authKey) {

                        tmpSyncUserID = id;

                        JSONArray array = new JSONArray();


                        JSONObject myServerInformation = new ServerBuilder()
                                .url(baseURL)
                                .name(myOrgName)
                                .authKey(authKey)
                                .build().toJSON();

                        JSONObject myUserInformation = new UserBuilder()
                                .email("felixpk@outlug.de")
                                .build()
                                .toJSON();

                        JSONObject myOrganisationInformation = new OrganisationBuilder()
                                .name(myOrgName)
                                .local(true)
                                .description("sample description")
                                .nationality("DE")
                                .sector("Programming")
                                .build()
                                .toJSON();

                        array.put(myServerInformation);
                        array.put(myUserInformation);
                        array.put(myOrganisationInformation);

                        Intent syncIntent = new Intent(getApplicationContext(), SyncActivity.class);
                        syncIntent.putExtra("qr_data", array.toString());
                        startActivityForResult(syncIntent, SYNC_EVENTS_REQUEST);
                    }

                    @Override
                    public void error(VolleyError volleyError) {
                        textViewResponse.setText(volleyError.toString());
                    }
                });
            }

            @Override
            public void error(VolleyError error) {

            }
        });
    }

    private void ShareEvents(){

    }

    private void ReceiveEvents(){

    }
}
