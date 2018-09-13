package com.everzones.arabic;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText etContent;
    private TextView tvContent;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置光标从右往左（但是输入的文字还是从左往右的）
        etContent = (EditText) findViewById(R.id.et_content);
        tvContent = (TextView) findViewById(R.id.tv_content);
        button = (Button) findViewById(R.id.button);

        etContent.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
        Locale locale = getResources().getConfiguration().locale;

        //设置edittext
        String language = locale.getLanguage();
        Log.e("lan","当前语言为："+language);
        if (language.endsWith("zh"))//ar(阿拉伯语)
        {
            etContent.setGravity(Gravity.RIGHT|Gravity.TOP);
            tvContent.setGravity(Gravity.RIGHT);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                // Display an hint to the user about what he should say.
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说标准普通话");//注意不要硬编码

                // Given an hint to the recognizer about what the user is going to say
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                // Specify how many results you want to receive. The results will be sorted
                // where the first result is the one with higher confidence.
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);//通常情况下，第一个结果是最准确的。

                startActivityForResult(intent, 1234);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            StringBuilder stringBuilder = new StringBuilder();
            int Size = matches.size();
            for(int i=0;i<Size;++i)
            {
                stringBuilder.append(matches.get(i));
                stringBuilder.append("\n");
            }
            Toast.makeText(getApplicationContext(),matches.get(0),Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
