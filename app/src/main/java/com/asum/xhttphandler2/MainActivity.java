package com.asum.xhttphandler2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.asum.xhttphandler2.tools.XDownLoadManager;
import com.asum.xhttphandler2.vo.DownloadInfoVO;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int num = 10;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XDownLoadManager.initialize(getApplicationContext(),"ceshi ",null,5);

        findViewById(R.id.activity_main_textview).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // XParam param = new XParam("http://es.bnuz.edu.cn:8080/jwc/loginApi.do?method=getAccount");
                // param.addBodyParameter("username", "1001030138");
                // param.addBodyParameter("password", "qweasdzxc");
                //
                // new XHttpHandler().start(getApplicationContext(), Method.POST, param, new XHttpHandlerCallBack() {
                // public void execute(Result resultType, String returnString) {
                //
                // }
                // });

                num++;
                int id = XDownLoadManager.addTask("1", "2", "3" + num, true);
                Log.i("XJW", "ID：" + id);
                int count = XDownLoadManager.getAllTaskCount();
                Log.i("XJW", "数量：" + count);

                ArrayList<DownloadInfoVO> allTask = XDownLoadManager.getAllTask();
                if (allTask != null) {
                    for (int i = 0; i < allTask.size(); i++) {
                        Log.i("XJW", "" + allTask.get(i).getId());
                    }
                }
            }
        });
    }
}
