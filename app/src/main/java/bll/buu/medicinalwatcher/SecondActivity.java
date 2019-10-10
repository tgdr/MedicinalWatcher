package bll.buu.medicinalwatcher;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import bll.buu.medicinalwatcher.activity.AlarmActivity;
import bll.buu.medicinalwatcher.activity.CaptureActivity;


public class SecondActivity extends AppCompatActivity {
    Button btnlogin;
    Handler myHandler;
    ListView list;
    private Connection connection = null;
    FrameLayout linearLayout;
    LoadingDialog loadingDialog;
    FloatingActionButton floatingActionButton,floatingActionButton2;
    SearchManager searchManager;
    SearchView searchView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            final String scanBarCodeResult = bundle.getString("result");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                     final Bundle getdata =   getItemInfoByBarCode(connection,scanBarCodeResult);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(getdata.getString("bar_code")!=null && !getdata.getString("bar_code").equals("")){

                                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                                    builder.setTitle("检索成功！").setMessage("条形码：" + getdata.getString("bar_code") + "\n"
                                            + "药品名称:" + getdata.getString("item_name") + "\n"
                                            + "剩余数量" + getdata.getString("item_count")+"\n"
                                                    +"药品到期时间："+getdata.getString("time_limit")+"\n"
                                                    +"药物功能："+getdata.getString("med_function"))
                                    .show();

                                }
                                else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.ab_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final ArrayList datas= getNamebySymbol(connection,s);
                            if(datas!=null && datas.size()>=1){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                                        builder.setTitle("成功获取结果");
                                        builder.setMessage("适用于"+s+"的药物有："+
                                                datas.toString());
                                        builder.show();
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                                        builder.setTitle("没有结果");
                                        builder.setMessage("没有找到适用于"+s
                                                +"的药物！");
                                        builder.show();
                                    }
                                });
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {

                return false;
            }
        });
        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        floatingActionButton = findViewById(R.id.floatbutton);
        floatingActionButton2 = findViewById(R.id.floatbutton2);
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(SecondActivity.this, AlarmActivity.class);
                startActivity(it);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(SecondActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);

            }
        });
        linearLayout = findViewById(R.id.linear);
        linearLayout.setBackgroundResource(R.mipmap.list_bg);
        list = findViewById(R.id.mylist);
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
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
                   final ArrayList namedata=  getItemName(connection);
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {

                          ArrayAdapter adapter = new ArrayAdapter(SecondActivity.this, android.R.layout.simple_list_item_1, namedata);
                          list.setAdapter(adapter);
loadingDialog.dismiss();
                          list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                              @Override
                              public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                                 new Thread(new Runnable() {
                                     @Override
                                     public void run() {
                                         try {
                                             final String allinfo[] = getItemInfoByItemName(connection,namedata.get(i)+"");
                                             if(allinfo[0] !=null && !allinfo[0].equals("")){
                                                 runOnUiThread(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                                                         builder.setTitle("成功获取结果");

                                                         builder.setMessage("条形码：" + allinfo[1] + "\n"
                                                                 + "药品名称:" + allinfo[0] + "\n"
                                                                 + "剩余数量:" + allinfo[2]+"\n"
                                                         +"药品到期时间："+allinfo[3]+"\n"
                                                         +"药物功能："+allinfo[4]);
                                                         builder.show();
                                                     }
                                                 });
                                             }
                                         } catch (SQLException e) {
                                             e.printStackTrace();
                                         }
                                     }
                                 }).start();

                              }
                          });
                      }
                  });
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();

        myHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = new Bundle();
                data = msg.getData();
                System.out.println("barcode" + data.get("bar_code").toString());
                System.out.println("name" + data.get("item_name").toString());
                System.out.println("count" + data.get("item_count").toString());


                AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                builder.setTitle("成功获取到扫描结果");
                builder.setMessage("条形码：" + data.get("bar_code").toString() + "\n"
                        + "名称" + data.get("item_name").toString() + "\n"
                        + "剩余数量" + data.get("item_count").toString());
                builder.show();
            }
        };

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

    public String[] getItemInfoByItemName(Connection con1, String name) throws SQLException {
       String []data = new String[5];

            String sql = "select * from shopinfo where item_name= '" + name + "'";        //查询表名为“user”的所有内容
            Statement stmt = con1.createStatement();        //创建Statement
            ResultSet rs = stmt.executeQuery(sql);          //ResultSet类似Cursor
            while (rs.next()) {
               data[0]=name;
               data[1]=rs.getString("bar_code");
               data[2]=rs.getString("item_count");
               data[3]=rs.getString("time_limit");
               data[4]=rs.getString("med_function");
            }

            rs.close();
            stmt.close();
            return data;
        }




    public ArrayList getItemName(Connection con1) throws SQLException {
        ArrayList namelist = new ArrayList();
        String sql = "select item_name from shopinfo;";        //查询表名为“user”的所有内容
        Statement stmt = con1.createStatement();        //创建Statement
        ResultSet rs = stmt.executeQuery(sql);          //ResultSet类似Cursor
        while (rs.next()) {
            namelist.add(rs.getString("item_name"));

        }
        //<code>ResultSet</code>最初指向第一行


        rs.close();
        stmt.close();

        return namelist;
    }

    public ArrayList getNamebySymbol(Connection con1,String symbol) throws SQLException {
        ArrayList namelist = new ArrayList();
        String sql = "select item_name from shopinfo where med_function LIKE '%" +
               symbol +"%';";        //查询表名为“user”的所有内容
        Statement stmt = con1.createStatement();        //创建Statement
        ResultSet rs = stmt.executeQuery(sql);          //ResultSet类似Cursor
        while (rs.next()) {
            namelist.add(rs.getString("item_name"));

        }
        //<code>ResultSet</code>最初指向第一行


        rs.close();
        stmt.close();

        return namelist;
    }


//
//    public String[] finditeminfo(String code) {
//        //显示查询到的信息
//        //数据库中表的结构为 name(varchar(10)),id(varchar(10)) ,age(int)
//        String[] myresult = new String[3];
//        String sql = "select * from shopinfo where bar_code=" + code;
//        Statement stmt = connection.createStatement();        //创建
//        try {
//            ResultSet rs = stmt.executeQuery(sql);
//            while (rs.next()) {
//                System.out.println("bar_code: " + rs.getString("bar_code")
//                        + " iten_name:" + rs.getString("iten_name")
//                        + "iten_count:" + rs.getString("iten_count"));
//                myresult[0] = rs.getString("bar_code");
//                myresult[1] = rs.getString("iten_name");
//                myresult[2] = rs.getString("iten_count");
//            }
//            rs.close();
//        } catch (SQLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return myresult;
//    }






}
