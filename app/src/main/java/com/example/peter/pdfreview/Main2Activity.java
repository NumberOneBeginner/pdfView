package com.example.peter.pdfreview;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lidong.pdf.PDFView;
import com.lidong.pdf.listener.OnPageChangeListener;


public class Main2Activity extends AppCompatActivity {
    private Context mContext=this;
    private Button button;
    private Handler handler=new Handler();
    private PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        button= (Button) findViewById(R.id.button);
        pdfView = (PDFView) findViewById(R.id.pdfView2);

        // 在我这个测试例子中，事先准备一个叫做sample.pdf的pdf大文件放到assets目录下。
        // 从assets文件目录下读取名为 sample.pdf的文件，缺省把该pdf定位到第一页。
        pdfView.fromAsset("log.pdf").defaultPage(1).onPageChange(new OnPageChangeListener() {

            @Override
            public void onPageChanged(int page, int pageCount) {
                // 当用户在翻页时候将回调。
                Toast.makeText(getApplicationContext(), page + " / " + pageCount, Toast.LENGTH_SHORT).show();
            }
        }).load();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,DownLoaderActivitya.class);
                startActivity(intent);
            }
        });
    }

}
