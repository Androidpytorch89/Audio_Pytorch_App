package com.somesh.fdrw2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

class label_names {
    public static String[] arrayLables= new String[]{"Coughing","Sneezing","Neither"};
}


public class tensor extends AppCompatActivity {
    Module module = null;
    int cou=0, sne=0, nee=0;
    EditText co, sn, ne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tensor);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();

        PyObject pyf = py.getModule("sample");

        try {
            module = Module.load(assetFilePath(this, "model.pt"));
        } catch (IOException e) {
            Log.e("PytorchHelloWorld", "Error reading assets", e);
            finish();
        }

        TextView t = findViewById(R.id.text1);
        TextView t2 = findViewById(R.id.t2);
        co = findViewById(R.id.cough);
        sn = findViewById(R.id.snezz);
        ne = findViewById(R.id.neither);

        float[] array = pyf.callAttr("test").toJava(float[].class);
        int ln = Array.getLength(array);
        t2.setText("Original length: " + String.valueOf(ln)+";modified on: "+MainActivity.dt.toString());
        array = (float[]) resizeArray(array, 220500);

        final long[] shape = new long[]{1, 1, array.length};

        final Tensor inputTensor = Tensor.fromBlob(array, shape);

        if(inputTensor!=null)
            t.setText(inputTensor.toString());
            //t.setText(pyf.callAttr("locate").toString());
        else
            t.setText("null");

        //co.setText(p.toString());

        final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();


        final float[] scores = outputTensor.getDataAsFloatArray();


        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {

            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }
        label_names obj= new label_names();
        String className = obj.arrayLables[maxScoreIdx];




        if (className == "Coughing")
        {
            cou+=1;
        }
        if (className== "Sneezing")
        {
            sne+=1;

        }
        if (className == "Neither")
        {
            nee=+1;
        }

        co.setText("Coughing : "+ cou);
        sn.setText("Sneezing : "+ sne);
        ne.setText("Neither : "+nee);




    }
    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    private static Object resizeArray (Object oldArray, int newSize) {
        int oldSize = java.lang.reflect.Array.getLength(oldArray);
        Class elementType = oldArray.getClass().getComponentType();
        Object newArray = java.lang.reflect.Array.newInstance(elementType, newSize);
        int preserveLength = Math.min(oldSize, newSize);
        if (preserveLength > 0)
            System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
        return newArray;
    }
}
