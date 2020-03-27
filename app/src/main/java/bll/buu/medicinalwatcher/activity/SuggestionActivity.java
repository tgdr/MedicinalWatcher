package bll.buu.medicinalwatcher.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import bll.buu.medicinalwatcher.AppConfig;
import bll.buu.medicinalwatcher.R;

public class SuggestionActivity extends AppCompatActivity {
    Connection connection=null;
    PreparedStatement ps=null;
    Spinner spinner;
    ArrayList getnameresult;
    MaterialEditText editinfo;
    Button bt_submit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        spinner = findViewById(R.id.sp_name);
        editinfo = findViewById(R.id.etinput);
        bt_submit = findViewById(R.id.bt_submit);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(editinfo.getText().toString().trim())){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final boolean s = insertItem(editinfo.getText().toString().trim());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(s==true){
                                        Toast.makeText(SuggestionActivity.this,"提交反馈成功！",Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(SuggestionActivity.this,"提交反馈失败",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    }).start();

                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getnameresult = getItemName();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                final ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(SuggestionActivity.this, android.R.layout.simple_spinner_item, getnameresult);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setAdapter(_Adapter);
                    }
                });

            }
        }).start();

    }


    private boolean insertItem(String editinfo) {
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
        boolean isSuccess=false;
        String sql = "INSERT INTO suggestion (sug_title,sug_content) VALUES(?,?)";		//插入sql语句
        try {
            ps = connection.prepareStatement(sql);

            /**
             * 插入的各个字段的值
             * 注意参数占位的位置
             * 通过set方法设置参数的位置
             * 通过get方法取参数的值
             */
            ps.setString(1, spinner.getSelectedItem().toString());
            ps.setString(2, editinfo);


            int re =ps.executeUpdate();			//执行sql语句

            if(re>=1){
                isSuccess =true;
            }
            else{
                isSuccess= false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            isSuccess=false;
        }

        return  isSuccess;
    }

    public ArrayList getItemName() throws SQLException {
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
        ArrayList namelist = new ArrayList();
        String sql = "select item_name from shopinfo;";        //查询表名为“user”的所有内容
        Statement stmt = connection.createStatement();        //创建Statement
        ResultSet rs = stmt.executeQuery(sql);          //ResultSet类似Cursor
        while (rs.next()) {
            namelist.add(rs.getString("item_name"));

        }
        //<code>ResultSet</code>最初指向第一行


        rs.close();
        stmt.close();
        connection.close();
        return namelist;
    }
}
