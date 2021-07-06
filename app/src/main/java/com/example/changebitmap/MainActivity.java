package com.example.changebitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;


import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class MainActivity extends AppCompatActivity {
    Bitmap newBitmap;
    ImageView img;
    Button btnColor, btnCrop, btnBlur, btnResize, btnTachMau, btnRED, btnGreen, btnBlue, btnBinary, btnCMYK;
    Bitmap bitmap;
    OutputStream outputStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestPermissions();
// Check bộ nhớ ngoài có thể ghi file
        isExternalStorageReadable();

//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 999);
//        }
        btnColor = (Button) findViewById(R.id.btn_color);
        btnCrop = (Button) findViewById(R.id.btn_crop) ;
        btnBlur = (Button) findViewById(R.id.btn_blur) ;
        btnResize = (Button) findViewById(R.id.btn_resize) ;
        btnTachMau = (Button) findViewById(R.id.btnTachMau);
        btnRED = (Button) findViewById(R.id.btnRED) ;
        btnGreen = (Button) findViewById(R.id.btnGreen);
        btnBlue = (Button) findViewById(R.id.btnBlue);
        btnBinary  = (Button)  findViewById(R.id.btn_binary);
        btnCMYK = (Button) findViewById(R.id.btnCMYK) ;


//        Drawable drawable = getResources().getDrawable(R.drawable.hhh);
        img = (ImageView) findViewById(R.id.imv_third);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
                openImagePicker();
            }


        });

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Bitmap newbitmap = changeBitmapColor(bitmap, Color.YELLOW);
//                Bitmap newbitmap = bitmap.eraseColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                newBitmap = GrayscaleLightness(bitmap);

                img.setImageBitmap(newBitmap);
            }
        });

        btnBinary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBitmap = Binary(bitmap, 100);
                img.setImageBitmap(newBitmap);
            }
        });

        btnCMYK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Bitmap> CMYK = RGB2CMYK(bitmap);
                img.setImageBitmap(CMYK.get(0));
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img.setImageBitmap(CMYK.get(1));
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                img.setImageBitmap(CMYK.get(2));
                                img.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        img.setImageBitmap(CMYK.get(3));
                                    }
                                });
                            }
                        });
                    }
                });


            }
        });

        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBitmap = getCircledBitmap(bitmap);
                img.setImageBitmap(newBitmap);

            }
        });

        btnBlur.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                newBitmap = ColorImageSmoothing(bitmap);
                img.setImageBitmap(newBitmap);
            }
        });

        btnResize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBitmap = bitmapResize(bitmap);
                img.setImageBitmap(newBitmap);

                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveImage(newBitmap);
                        Log.d("MainActivity OanhNTn", Environment.getExternalStorageDirectory().getAbsolutePath());

                    }
                });
            }
        });

        btnTachMau.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Bitmap[] bitmapColor = tachMauRGB(bitmap);
                btnRED.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap red = bitmapColor[0];
                        img.setImageBitmap(red);
                    }
                });
                btnGreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap green = bitmapColor[1];
                        img.setImageBitmap(green);
                    }
                });
                btnBlue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap blue = bitmapColor[2];
                        img.setImageBitmap(blue);
                    }
                });
            }
        });
    }

    public static List<Bitmap> RGB2CMYK(Bitmap bitmap){
       List<Bitmap> CMYK = new ArrayList<Bitmap>();
        Bitmap Cyan = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap Magenta = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap Yellow = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap Black = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        for (int x = 0; x < bitmap.getWidth(); x++)
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int pixel = bitmap.getPixel(x, y);
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);

                Cyan.setPixel(x, y, Color.rgb(0, G, B));
                Magenta.setPixel(x, y, Color.rgb(R, 0, B));
                Yellow.setPixel(x, y, Color.rgb(R, G, 0));

                int K = Math.min(R, Math.min(G, B));
                Black.setPixel(x, y, Color.rgb(K, K, K));

            }
        CMYK.add(Cyan);
        CMYK.add(Magenta);
        CMYK.add(Yellow);
        CMYK.add(Black);

        return CMYK;


    }

    // Tính histogram của ảnh xám
    public double[] Histogram (Bitmap bitmap) {
        double[] histogram = new double[256];
        for (int x = 0; x < bitmap.getWidth(); x++)
            for (int y = 0; y < bitmap.getHeight(); y++)
            {
                int pixel  = bitmap.getPixel(x, y);
                int gray = Color.red(pixel);
                histogram[gray]++;

            }
        return histogram;
    }

// Chuyển hình RGB sang nhị phân Binary
    public static Bitmap Binary (Bitmap bitmap, int threshold) {
        Bitmap bitMapBinary = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        for (int x = 0; x < bitmap.getWidth(); x++)
            for (int y = 0; y < bitmap.getHeight(); y++)
            {
                // lấy điểm ảnh
                int pixel = bitmap.getPixel(x, y);
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);
                int A = Color.alpha(pixel);

                // Tính giá trị mức xám cho điểm ảnh tại (x, y)
                          // GrayscaleLuminance
                int binary = (int) (0.2126*R + 0.7152*G + 0.0722*B);

                // Phân loại điểm ảnh sang nhị phân dựa vào giá trị ngưỡng
                if (binary < threshold)
                    binary = 0;
                else
                    binary = 255;


                // Gán giá trị nhị phân
                bitMapBinary.setPixel(x, y, Color.rgb(binary, binary, binary));



            }
        return bitMapBinary;
    }

// Chuyển ảnh mức xám
    public static Bitmap GrayscaleLightness (Bitmap bitmap) {

        Bitmap bitMapGraysacle = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        for (int x = 0; x < bitmap.getWidth(); x++)
            for (int y = 0; y < bitmap.getHeight(); y++)
            {
                // lấy điểm ảnh
                int pixel = bitmap.getPixel(x, y);
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);
                int A = Color.alpha(pixel);

                // Tính giá trị mức xám cho điểm ảnh tại (x, y)
                int max = Math.max(R, Math.max(G, B));
                int min = Math.min(R, Math.min(G, B));
//                int gray = (int)((max + min)/2);

                // GrayscaleAverage
//                int gray = (int) ((R + G + B)/3);

                // GrayscaleLuminance
                int gray = (int) (0.2126*R + 0.7152*G + 0.0722*B);

                // Gán giá trị mức xám
                bitMapGraysacle.setPixel(x, y, Color.rgb(gray, gray, gray));

            }
        return bitMapGraysacle;

    }
// làm mượt ảnh
    public static Bitmap ColorImageSmoothing (Bitmap bitmap) {
        Bitmap SmoothedTmage = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);


        for (int x=2; x < bitmap.getWidth() - 2; x++)
            for (int y=2; y < bitmap.getHeight() - 2; y++) {

                int Rs = 0, Gs = 0, Bs = 0;

                for (int i = x -2; i <= x+2; i++)
                    for (int j = y - 2; j <= y+2; j++) {
                        int pixel = bitmap.getPixel(x, y);
                        int R = Color.red(pixel);
                        int G = Color.green(pixel);
                        int B = Color.blue(pixel);
                        int A = Color.alpha(pixel);

                        Log.d(TAG, "ColorImageSmoothing: OanhNTn: ");

                        Rs += R;
                        Gs += G;
                        Bs += B;
                    }

                int K = 5*5;
                    Rs = (int) (Rs / K);
                    Gs = (int) (Gs / K);
                    Bs = (int) (Bs / K);

                    SmoothedTmage.setPixel(x, y, Color.rgb( Rs, Gs, Bs));

            }
        return SmoothedTmage;
    }
// tách màu R G b
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Bitmap[] tachMauRGB (Bitmap bitmap) {
        Log.d(TAG, "tachMauRGB: OanhNTn: " + bitmap.getWidth());
        Bitmap[] bitmapColor = new Bitmap[4];



        Bitmap red = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap green = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap blue = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap alpha = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//
                bitmapColor[0] = red;
                bitmapColor[1] = green;
                bitmapColor [2] = blue;
                bitmapColor[3] = alpha;
//                Log.d(TAG, "tachMauRGB: OanhNTn: " + bitmapColor.getClass());


        for (int x = 0; x < bitmap.getWidth(); x++)
            for (int y = 0; y < bitmap.getHeight(); y++) {
                // Đọc giá trị pixel tại điểm ảnh có vị trí (x, y)
                int pixel = bitmap.getPixel(x, y);

                // Mỗi pixel chứ 4 thông tin R G B A(độ trong suốt)
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);
                int A = Color.alpha(pixel);

                red.setPixel(x, y, Color.argb(A, R, 0, 0));
                green.setPixel(x, y, Color.argb(A, 0, G, 0));
                blue.setPixel(x, y, Color.argb(A, 0, 0, B));
                alpha.setPixel(x, y, Color.argb(A, 0, 0, 0));

            }
        return bitmapColor;
    }

    private void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openImagePicker();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void openImagePicker() {
//

        TedBottomPicker.with(MainActivity.this)
                .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        // here is selected image uri

                        try {
                           bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            img.setImageBitmap(bitmap);
                            Log.d(TAG, "onImageSelected: OanhNTn: "+ bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }


    public static Bitmap changeBitmapColor(Bitmap sourceBitmap, int color)
    {
        Bitmap resultBitmap = sourceBitmap.copy(sourceBitmap.getConfig(),true);

        Paint paint = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
        return resultBitmap;
    }
    public Bitmap changeColor(Bitmap srcImage) {

        Bitmap bmpRedscale = Bitmap.createBitmap(srcImage.getWidth(),
                srcImage.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmpRedscale);
        Paint paint = new Paint();

        ColorMatrix cm = new ColorMatrix();
        cm.setRGB2YUV();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(srcImage, 0, 0, paint);

//        mImgEdited.setImageBitmap(bmpRedscale);
        return bmpRedscale;
    }

    public static Bitmap getCircledBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    Bitmap BlurImage (Bitmap input)
    {
        try
        {
            RenderScript rsScript = RenderScript.create(getApplicationContext());
            Allocation alloc = Allocation.createFromBitmap(rsScript, input);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript,   Element.U8_4(rsScript));
            blur.setRadius(21);
            blur.setInput(alloc);

            Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
            Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);

            blur.forEach(outAlloc);
            outAlloc.copyTo(result);

            rsScript.destroy();
            return result;
        }
        catch (Exception e) {
            // TODO: handle exception
            return input;
        }

    }
    public Bitmap bitmapResize(Bitmap imageBitmap) {

        Bitmap bitmap = imageBitmap;
        float heightbmp = bitmap.getHeight();
        float widthbmp = bitmap.getWidth();

        // Get Screen width
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = displaymetrics.heightPixels / 3;
        float width = displaymetrics.widthPixels / 3;

        int convertHeight = (int) height, convertWidth = (int) width;

        // higher
        if (heightbmp > height) {
            convertHeight = (int) height - 20;
            bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth,
                    convertHeight, true);
        }

        // wider
        if (widthbmp > width) {
            convertWidth = (int) width - 20;
            bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth,
                    convertHeight, true);
        }

        return bitmap;
    }
    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    public void  saveImage(Bitmap bitmap)

    {
      File filepath = Environment.getExternalStorageDirectory();
      File dir = new File(filepath.getAbsolutePath() + "/DCIM/Camera/");
      dir.mkdir();
      File file = new File(dir, System.currentTimeMillis() + ".jpg");
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        Toast.makeText(getApplicationContext(), "Image Save To Internal", Toast.LENGTH_SHORT).show();
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}