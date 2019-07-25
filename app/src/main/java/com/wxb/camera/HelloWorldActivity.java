package com.wxb.camera;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HelloWorldActivity extends AppCompatActivity {

    private static final String TAG = "HelloWorldActivity";
    private String myfolder;
    private String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_world_layout);
        myfolder=Environment.getExternalStorageDirectory().toString()+"/MYPHOTO";
        File file=new File(myfolder);
        if(!file.exists()) {
            file.mkdirs();
        }
        ((Button)findViewById(R.id.take_photo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                fileName = myfolder + "/" + createName();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)//大于7.0
                {
                    try {
                        ContentValues contentValues = new ContentValues(1);
                        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                        contentValues.put(MediaStore.Images.Media.DATA, fileName);//保存照片到image.jpg
                        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        if (uri != null) {

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                        }
                        startActivityForResult(intent, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
            else{
                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    uri=Uri.fromFile(new File(fileName));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                    startActivityForResult(intent, 1);
                }

            }
        });
        if(AndPermission.hasPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)){


        }
        else{
            AndPermission.with(this)
                    .requestCode(100)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA)
                    .send();
        }



    }
private String createName(){
        String name;
    Date date=new Date();
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
    name=simpleDateFormat.format(date)+".jpg";
    return name;


}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==1) {
                //take_photo(data);
                FileInputStream fileInputStream= null;
                try {
                    fileInputStream = new FileInputStream(fileName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap=BitmapFactory.decodeStream(fileInputStream);
                ((ImageView)findViewById(R.id.picture)).setImageBitmap(bitmap);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }


    @PermissionYes(100)
    private void getPermission(List<String> grantedPermission){
        Toast.makeText(HelloWorldActivity.this,"接受了权限",Toast.LENGTH_SHORT).show();

    }
    @PermissionNo(100)
    private void refusePermission(List<String> grantedPermission){

        Toast.makeText(HelloWorldActivity.this,"拒接了权限",Toast.LENGTH_SHORT).show();

    }
    private  void  take_photo(Intent data){//从Intent 的data获得的照片很模糊，因此舍弃。
        Bundle bundle=data.getExtras();
        Bitmap bitmap=(Bitmap)bundle.get("data");
        String myfolder=Environment.getExternalStorageDirectory().toString()+"/MYPHOTO";
        File file=new File(myfolder);
        file.mkdirs();
        String fileName=myfolder+"/image.jpg";
        FileOutputStream image=null;
        try {
            image=new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                image.flush();
                image.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        ((ImageView)findViewById(R.id.picture)).setImageBitmap(bitmap);
    }

}
