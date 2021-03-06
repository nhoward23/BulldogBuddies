package com.example.ktran.wannabetinder;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.example.ktran.wannabetinder.models.Constants;
import com.example.ktran.wannabetinder.models.Friend;
import com.example.ktran.wannabetinder.models.FriendAdapter;
import com.example.ktran.wannabetinder.models.RequestAdapter;
import com.example.ktran.wannabetinder.models.RetroInterfaces;
import com.example.ktran.wannabetinder.models.ServerResponse;
import com.example.ktran.wannabetinder.models.User;
import com.example.ktran.wannabetinder.models.UserAdapter;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Profile extends AppCompatActivity{
    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mName;
    private User mUser;
    private User[] matched_users;
    private Friend[] all_friends;
    private TextView tv_name;
    private TextView tv_token;
    private ListView popUpListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initValues();
        initSharedPreferences();
        loadProfile();

        //view all friends
        ImageButton find_friends_btn  = findViewById(R.id.add_friend);
        find_friends_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Retrofit retrofit = getRetro();
                RetroInterfaces friendsInterface = retrofit.create(RetroInterfaces.class);
                Call<ServerResponse> response = friendsInterface.getFriends(mToken);
                response.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        ServerResponse resp = response.body();
                        if(resp.getSuccess()){
                            all_friends = resp.getFriends();
                            ArrayList<Friend> aList= new ArrayList<Friend>(Arrays.asList(all_friends));
                                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                                    int width = metrics.widthPixels;
                                    int height = metrics.heightPixels;
                                    final Dialog dialog = new Dialog(Profile.this);
                                    dialog.setContentView(R.layout.pop_up);
                                    dialog.setTitle("All Users");
                                    popUpListView= dialog.findViewById(R.id.list_friends);
                                    FriendAdapter adapter = new FriendAdapter(Profile.this, aList);
                                    popUpListView.setAdapter(adapter);
                                    dialog.show();
                                    dialog.getWindow().setLayout((6 * width)/7, (4 * height)/5);
                        }
                        else{
                            Snackbar.make(findViewById(R.id.ap),"Donezo", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Snackbar.make(findViewById(R.id.ap), t.getCause() + " shits busted", Snackbar.LENGTH_LONG).show();
                    }
                });

            }
        });

        //view matches
        TextView match_btn = findViewById(R.id.match_me);
        match_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
               final Retrofit retrofit = getRetro();
                RetroInterfaces friendsInterface = retrofit.create(RetroInterfaces.class);
                Call<ServerResponse> response = friendsInterface.getMyMatches(mToken);
                response.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        ServerResponse resp = response.body();
                        if(resp.getSuccess()){
                            matched_users = resp.getUsers();
                            ArrayList<User> aList= new ArrayList<User>(Arrays.asList(matched_users));
                            DisplayMetrics metrics = getResources().getDisplayMetrics();
                            int width = metrics.widthPixels;
                            int height = metrics.heightPixels;
                            final Dialog dialog = new Dialog(Profile.this);
                            dialog.setContentView(R.layout.pop_up);
                            dialog.setTitle("My Requests");
                            popUpListView= dialog.findViewById(R.id.list_friends);

                            UserAdapter adapter = new UserAdapter(Profile.this, aList);
                            popUpListView.setAdapter(adapter);
                            dialog.show();
                            dialog.getWindow().setLayout((6 * width)/7, (4 * height)/5);
                        }
                        else{
                            Snackbar.make(findViewById(R.id.ap),"Donezo", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Snackbar.make(findViewById(R.id.ap), t.getCause() + " shits busted", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });

        // View Requested
        TextView req_btn = findViewById(R.id.find_requested);
        req_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Retrofit retrofit = getRetro();
                RetroInterfaces friendsInterface = retrofit.create(RetroInterfaces.class);
                Call<ServerResponse> response = friendsInterface.getMyRequests(mToken);
                response.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        ServerResponse resp = response.body();
                        if(resp.getSuccess()){
                            all_friends = resp.getFriends();
                            ArrayList<Friend> aList= new ArrayList<Friend>(Arrays.asList(all_friends));
                            DisplayMetrics metrics = getResources().getDisplayMetrics();
                            int width = metrics.widthPixels;
                            int height = metrics.heightPixels;
                            final Dialog dialog = new Dialog(Profile.this);
                            dialog.setContentView(R.layout.pop_up);
                            dialog.setTitle("My Requests");
                            popUpListView= dialog.findViewById(R.id.list_friends);
                            RequestAdapter adapter = new RequestAdapter(Profile.this, aList);
                            popUpListView.setAdapter(adapter);
                            dialog.show();
                            dialog.getWindow().setLayout((6 * width)/7, (4 * height)/5);
                        }
                        else{
                            Snackbar.make(findViewById(R.id.ap),"Donezo", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Snackbar.make(findViewById(R.id.ap), t.getCause() + " shits busted", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });

        ImageButton log_out_btn = findViewById(R.id.logout);
        log_out_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("myName", "");
                editor.putString("myToken", "");
                editor.putString("myUser", "");
                editor.apply();
                finish();
            }
        });
    }

    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mName = mSharedPreferences.getString("myName","");
        mToken = mSharedPreferences.getString("myToken","");
        Gson gson = new Gson();
        String json = mSharedPreferences.getString("myUser", "");
        mUser = gson.fromJson(json, User.class);
    }

    private void initValues() {
        tv_name = findViewById(R.id.user_profile_name);
        tv_token = findViewById(R.id.user_profile_short_bio);
    }

    private void loadProfile(){
        Retrofit retrofit = getRetro();
        RetroInterfaces profileInterface = retrofit.create(RetroInterfaces.class);
        Call<ServerResponse> response = profileInterface.getProfile(mToken, mName);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("ERROR", "Working");
                Snackbar.make(findViewById(R.id.ap), resp.getMessage() + ", Sucessful:" + resp.getSuccess(), Snackbar.LENGTH_LONG).show();
                tv_name.setText(Constants.getSplitName(resp.getMessage()));
                tv_token.setText("Phone: " + resp.getuser().getPhone() + " Department: " + resp.getuser().getDepartment());
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("ERROR", "TEST" + t.getCause());
            }
        });
    }
    public Retrofit getRetro(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
