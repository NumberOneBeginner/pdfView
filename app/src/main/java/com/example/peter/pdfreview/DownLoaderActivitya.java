package com.example.peter.pdfreview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.lidong.pdf.PDFView;
import com.lidong.pdf.listener.OnDrawListener;
import com.lidong.pdf.listener.OnLoadCompleteListener;
import com.lidong.pdf.listener.OnPageChangeListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownLoaderActivitya extends AppCompatActivity
        implements OnPageChangeListener,OnLoadCompleteListener,OnDrawListener {
    private OkHttpClient okHttpClient;
    private ProgressBar progressBar;
    File file;
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;
//    private String url= "https://pic.bincrea.c"+
//            "om/bc_bg_6D40C91A170D41C182511ABBB8A634A4.pdf";
//private String url= "http://demoappdownload.oss-cn-hangzhou.aliyuncs.com/" +
//        "uploadeinvoice/20161219/%E7%94%B5%E5%AD%90%E5%8F%91%E7%A5%A8.pdf";

private String url="http://uploadmedia.oss-cn-shenzhen.aliyuncs.com/upload/einvoice/20170316/01390794_E100027367.pdf";
    private TextView textView;
    private PDFView pdfView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //进度条的值
                    int i = msg.arg1;
                    progressBar.setProgress(i);
                    textView.setText("下载进度："+i+"%");
            }
            if (msg.arg1==100){
                displayFromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), url.substring(url.lastIndexOf("/") + 1)));
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_loader_activitya);
        textView = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        pdfView = (PDFView) findViewById(R.id.pdfView3);


    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
    }

    public void requestPermission(){
        //判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this,"please give me the permission",Toast.LENGTH_SHORT).show();
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_REQ_CODE);
            }

        }else{
            downloadPdfFile();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQ_CODE: {
                // 如果请求被拒绝，那么通常grantResults数组为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请成功，进行相应操作
                    downloadPdfFile();
                } else {
                    //申请失败，可以继续向用户解释。
                    requestPermission();
                }
                return;
            }
        }
    }
    private void downloadPdfFile(){
        okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("h_bl", "onFailure"+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
//                String SDPath = Environment.getExternalStorageDirectory()+"/pdf";
                String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    file = new File(SDPath, url.substring(url.lastIndexOf("/") + 1));
                        fos = new FileOutputStream(file);
                        long sum = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            sum += len;
                            int progress = (int) (sum * 1.0f / total * 100);
                            Log.d("h_bl", "progress=" + progress);
                            Message msg = handler.obtainMessage();
                            msg.what = 1;
                            msg.arg1 = progress;
                            handler.sendMessage(msg);
                        }
                        fos.flush();
                        Log.d("h_bl", "文件下载成功");

                } catch (Exception e) {
                    Log.d("h_b2", "文件下载失败"+"   ----"+e.getMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    private void displayFromFile( File file ) {
        pdfView.fromFile(file)   //设置pdf文件地址
                .defaultPage(1)         //设置默认显示第1页
                .onPageChange(this)     //设置翻页监听
//                .onLoad(this)           //设置加载监听
//                .onDraw(this)            //绘图监听
//                .showMinimap(false)     //pdf放大的时候，是否在屏幕的右上角生成小地图
//                .swipeVertical( false )  //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
//                .enableSwipe(true)   //是否允许翻页，默认是允许翻
                // .pages( 2 ，5  )  //把2  5 过滤掉
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        Toast.makeText( DownLoaderActivitya.this , " " + page +
                " / " + pageCount , Toast.LENGTH_SHORT).show();

    }
    /**
     * 加载完成回调
     * @param nbPages  总共的页数
     */
    @Override
    public void loadComplete(int nbPages) {
        Toast.makeText( DownLoaderActivitya.this ,  "加载完成" + nbPages  , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float v, float v1, int i) {

    }

//    @Override
//    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
//        // Toast.makeText( MainActivity.this ,  "pageWidth= " + pageWidth + "
//        // pageHeight= " + pageHeight + " displayedPage="  + displayedPage , Toast.LENGTH_SHORT).show();
//    }

}
