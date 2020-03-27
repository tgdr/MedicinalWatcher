package bll.buu.medicinalwatcher.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import bll.buu.medicinalwatcher.AddItemActivity;
import bll.buu.medicinalwatcher.AppConfig;
import bll.buu.medicinalwatcher.R;
import bll.buu.medicinalwatcher.SecondActivity;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

FragmentManager manager;
FragmentTransaction transaction;
Connection connection=null;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            final String scanBarCodeResult = bundle.getString("result");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        connection = DriverManager.getConnection("jdbc:mysql://" + AppConfig.serverip + ":3306/workshop?useUnicode=true&characterEncoding=UTF-8&useSSL=false", "root", "091920gwy");
                        Log.e("连接成功","success");
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        Log.e("连接失败1",e.toString());
                        e.printStackTrace();
                    } catch (SQLException e1) {
                        Log.e("连接失败2",e1.toString());
                        e1.printStackTrace();
                    }
                    try {
                        final Bundle getdata =   getItemInfoByBarCode(connection,scanBarCodeResult);
                       runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(getdata.getString("bar_code")!=null && !getdata.getString("bar_code").equals("")){

                                    AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
                                    builder.setTitle("检索成功！").setMessage("条形码：" + getdata.getString("bar_code") + "\n"
                                            + "药品名称:" + getdata.getString("item_name") + "\n"
                                            + "剩余数量" + getdata.getString("item_count")+"\n"
                                            +"药品到期时间："+getdata.getString("time_limit")+"\n"
                                            +"药物功能："+getdata.getString("med_function"))
                                            .show();

                                }
                                else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
                                    builder.setTitle("系统中无此药品记录").setMessage("没有检索到该条形码对应的药品信息！").show();
                                }
                            }
                        });

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        manager= getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.contentnav,new SecondActivity());
        transaction.commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);

        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_alarm) {
            // Handle the camera action
           // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Intent it = new Intent(NavigationActivity.this, AlarmActivity.class);
            startActivity(it);
        }
        else if(id == R.id.nav_scan){
                      Intent openCameraIntent = new Intent(NavigationActivity.this, CaptureActivity.class);
            startActivityForResult(openCameraIntent, 0);


        }
        else if(id==R.id.nav_addcase){
            Intent it = new Intent(NavigationActivity.this, AddItemActivity.class);
            startActivity(it);
        }
        else if(id==R.id.nav_suggestion){
            Intent it = new Intent(NavigationActivity.this, SuggestionActivity.class);
            startActivity(it);
        }
        return true;
    }
    public Bundle getItemInfoByBarCode(Connection con1, String barcode) throws SQLException {

        String sql = "select * from shopinfo where bar_code= '" + barcode + "'";        //查询表名为“user”的所有内容
        Statement stmt = con1.createStatement();        //创建Statement
        ResultSet rs = stmt.executeQuery(sql);          //ResultSet类似Cursor

        //<code>ResultSet</code>最初指向第一行
        Bundle bundle = new Bundle();
        while (rs.next()) {
            bundle.clear();
            bundle.putString("bar_code", rs.getString("bar_code"));
            bundle.putString("item_name", rs.getString("item_name"));
            bundle.putString("item_count", rs.getString("item_count"));
            bundle.putString("time_limit",rs.getString("time_limit"));
            bundle.putString("med_function",rs.getString("med_function"));
//                Message msg = new Message();
//                msg.setData(bundle);
            // myHandler.sendMessage(msg);
        }

        rs.close();
        stmt.close();
        return bundle;
    }
}
