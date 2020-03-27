package bll.buu.medicinalwatcher;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kongzue.dialog.interfaces.OnMenuItemClickListener;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.BottomMenu;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bll.buu.medicinalwatcher.activity.CaptureActivity;


public class AddItemActivity extends AppCompatActivity implements TextWatcher {
    boolean scanflag=false;
    private Connection connection = null;
    private PreparedStatement ps = null;
    Button btn_scan,bt_submit;
    TextView txtbarcode;
    EditText editname,editsymbol;
    CalendarView timepick;
TextView txtdia;
List<String> numdata;
String datestr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_addmedicial);
        timepick = findViewById(R.id.limit_picker);
        timepick.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                datestr = i+"年"+i1+"月"+i2+"日";
            }
        });
        btn_scan = findViewById(R.id.btn_scan);
        txtbarcode = findViewById(R.id.txt_barcode_scan_result);
        editname = findViewById(R.id.edit_item_name);
        editsymbol = findViewById(R.id.edit_item_symbol);
        bt_submit = findViewById(R.id.bt_submit);
        bt_submit.setBackgroundResource(R.drawable.bg_login_submit_lock);
        bt_submit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
        editsymbol.addTextChangedListener(this);
        editname.addTextChangedListener(this);
        DialogSettings.isUseBlur = true;
        DialogSettings.style = DialogSettings.STYLE.STYLE_IOS;
        DialogSettings.isUseBlur = true;                   //是否开启模糊效果，默认关闭
        DialogSettings.modalDialog = true;                 //是否开启模态窗口模式，一次显示多个对话框将以队列形式一个一个显示，默认关闭
        DialogSettings.cancelable = false;
        txtdia = findViewById(R.id.txtdialog);
        numdata = new ArrayList<>();
        for(int i=1;i<=100;i++){
            numdata.add(i+"");
        }
btn_scan.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent openCameraIntent = new Intent(AddItemActivity.this, CaptureActivity.class);
        startActivityForResult(openCameraIntent, 520);
    }
});

        txtdia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomMenu.show( AddItemActivity.this, "请选择数量",numdata, new OnMenuItemClickListener() {
                    @Override
                    public void onClick(String s, int i) {
                        txtdia.setText(s);
                    }
                });
            }
        });
//        numberPicker = findViewById(R.id.numpicker);
//        numberPicker.setMinValue(1);
//        numberPicker.setMaxValue(100);




    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String itemname = editname.getText().toString().trim();
        String itsymbol = editsymbol.getText().toString().trim();



        //登录按钮是否可用
        if (!TextUtils.isEmpty(itemname) && !TextUtils.isEmpty(itsymbol) && scanflag) {
            bt_submit.setBackgroundResource(R.drawable.bg_login_submit);
            bt_submit.setTextColor(getResources().getColor(R.color.white));
            bt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                                final boolean result=  insertItem(connection);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(result==true){
                                            Toast.makeText(AddItemActivity.this,"提交药品信息成功！",Toast.LENGTH_LONG).show();
                                            Intent it = new Intent(AddItemActivity.this,SecondActivity.class);
                                            startActivity(it);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(AddItemActivity.this,"提交失败，数据已存在",Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
            });
        } else {
            bt_submit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            bt_submit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
        }
    }

    private boolean insertItem(Connection connection) {
        boolean isSuccess=false;
        String sql = "INSERT INTO shopinfo (bar_code,item_name,item_count,time_limit,med_function) VALUES(?,?,?,?,?)";		//插入sql语句
        try {
            ps = connection.prepareStatement(sql);

            /**
             * 插入的各个字段的值
             * 注意参数占位的位置
             * 通过set方法设置参数的位置
             * 通过get方法取参数的值
             */
            ps.setString(1, txtbarcode.getText().toString());
            ps.setString(2, editname.getText().toString());
            ps.setString(3, txtdia.getText().toString());
           // ps.setString(4, new SimpleDateFormat("yyyy年MM月dd日").format(new Date(timepick.getDate())));
            ps.setString(4, datestr);
            ps.setString(5, editsymbol.getText().toString());

            ps.executeUpdate();			//执行sql语句

            System.out.println("插入成功(*￣︶￣)");
            isSuccess =true;
        } catch (SQLException e) {
            e.printStackTrace();
            isSuccess=false;
        }

        return  isSuccess;
    }

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtbarcode.setText(scanBarCodeResult);
                            scanflag=true;
                        }
                    });
                }
            }).start();

        }
    }
}
